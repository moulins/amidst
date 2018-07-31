package amidst.devtools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import amidst.Amidst;
import amidst.AmidstMetaData;
import amidst.AmidstSettings;
import amidst.Application;
import amidst.CommandLineParameters;
import amidst.PerApplicationInjector;
import amidst.gui.main.MainWindow;
import amidst.mojangapi.RunningLauncherProfile;
import amidst.mojangapi.file.DotMinecraftDirectoryNotFoundException;
import amidst.mojangapi.file.LauncherProfile;
import amidst.mojangapi.file.MinecraftInstallation;
import amidst.mojangapi.file.Version;
import amidst.mojangapi.file.VersionList;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaces;
import amidst.mojangapi.mocking.BenchmarkingMinecraftInterface;
import amidst.mojangapi.mocking.json.BiomeRequestRecordJson;
import amidst.mojangapi.world.WorldBuilder;
import amidst.mojangapi.world.WorldOptions;
import amidst.mojangapi.world.WorldSeed;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.coordinates.Coordinates;
import amidst.parsing.FormatException;

public class WorldGenerationBencher {
	
	private static final Gson GSON = new GsonBuilder().create();
	private static final WorldOptions WORLD_OPTIONS = new WorldOptions(
			WorldSeed.fromSaveGame(123456), WorldType.DEFAULT);
	
	private final String prefix;
	private final VersionList versionList;
	private final MinecraftInstallation minecraftInstallation;
	private final File outDir;
	private final List<BiomeRequestRecordJson> records = new ArrayList<>();
	private final List<Version> successful = new ArrayList<>();
	private final List<Version> failed = new ArrayList<>();
	
	private final Semaphore fullyLoadedBarrier = new Semaphore(0);
	
	public WorldGenerationBencher(String outDir, String prefix, String libraries, VersionList versionList)
			throws DotMinecraftDirectoryNotFoundException {
		this.prefix = prefix;
		this.versionList = versionList;
		this.outDir = new File(outDir);
		this.minecraftInstallation = MinecraftInstallation
				.newCustomMinecraftInstallation(new File(libraries), null, new File(prefix), null);
	}
	
	public void run() throws FormatException, IOException {
		Application app = startAmidst();
		
		for(Version version: versionList.getVersions()) {
			benchmarkOne(app, version);
		}
		
		displayVersionList("===== VERSIONS SUCCESSFUL =====", successful);
		displayVersionList("======= VERSIONS FAILED =======", failed);
	}
	
	private void benchmarkOne(Application app, Version version) {
		if(!version.tryDownloadClient(prefix)) {
			failed.add(version);
			return;
		}
		
		RunningLauncherProfile profile;
		try {
			LauncherProfile launcherProfile = minecraftInstallation.newLauncherProfile(version.getId());
			profile = new RunningLauncherProfile(
					WorldBuilder.createSilentPlayerless(),
					launcherProfile,
					new BenchmarkingMinecraftInterface(MinecraftInterfaces.fromLocalProfile(launcherProfile), records));
		} catch (FormatException | IOException | MinecraftInterfaceCreationException e) {
			failed.add(version);
			return;
		}
		
		if(!version.getId().equals(profile.getRecognisedVersion().getName()))
			return;
		
		//dummy run + real run, only the data gathered on the real run counts
		for(int i = 0; i < 2; i++) {
			records.clear();
			long startTime = System.nanoTime();
			SwingUtilities.invokeLater(() -> launchMainWindow(app, profile));
			fullyLoadedBarrier.acquireUninterruptibly();
			adjustRecords(startTime);
		}
		
		try {
			saveRecords(version);
		} catch (IOException e) {
			failed.add(version);
			return;
		}
		successful.add(version);
	}
	
	private void launchMainWindow(Application app, RunningLauncherProfile profile) {
		MainWindow window = app.displayMainWindow(profile);
		window.getWorldSwitcher().displayWorld(WORLD_OPTIONS);

		for(int i = 0; i < 10; i++) {
			window.getViewerFacade().adjustZoom(1);
		}
		window.getViewerFacade().centerOn(Coordinates.origin());
		
		Timer timer = new Timer(1000, e -> {});
		timer.setRepeats(true);
		timer.setInitialDelay(1000);
		timer.addActionListener(e -> {
			if(window.getViewerFacade().isFullyLoaded()) {
				timer.stop();
				fullyLoadedBarrier.release();
			}
		});
		timer.start();
	}
	
	private Application startAmidst() throws FormatException, IOException {
		AmidstSettings settings = new AmidstSettings(Preferences.userNodeForPackage(getClass()));
		CommandLineParameters params = new CommandLineParameters();
		AmidstMetaData metadata = Amidst.createMetadata();
		return new PerApplicationInjector(params, metadata, settings).getApplication();
	}
	
	private void adjustRecords(long startTime) {
		for(BiomeRequestRecordJson record: records) {
			record.startTime -= startTime;
		}
	}
	
	private void saveRecords(Version version) throws IOException {
		File file = new File(outDir, "worldgen-" + version.getId() + ".json");
		try (Writer writer = new FileWriter(file)) {
		    GSON.toJson(records, writer);
		}
	}
	
	private void displayVersionList(String message, List<Version> versions) {
		System.out.println();
		System.out.println(message);
		for (Version version : versions) {
			System.out.println(version.getId());
		}
	}
}
