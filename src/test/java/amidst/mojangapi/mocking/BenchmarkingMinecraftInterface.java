package amidst.mojangapi.mocking;

import java.util.Collections;
import java.util.List;

import amidst.documentation.ThreadSafe;
import amidst.mojangapi.minecraftinterface.MinecraftInterface;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceException;
import amidst.mojangapi.minecraftinterface.RecognisedVersion;
import amidst.mojangapi.mocking.json.BiomeRequestRecordJson;
import amidst.mojangapi.world.WorldType;
import amidst.mojangapi.world.coordinates.Region;

@ThreadSafe
public class BenchmarkingMinecraftInterface implements MinecraftInterface {
	private final MinecraftInterface inner;
	private final List<BiomeRequestRecordJson> records;

	public BenchmarkingMinecraftInterface(MinecraftInterface inner, List<BiomeRequestRecordJson> records) {
		this.inner = inner;
		this.records = Collections.synchronizedList(records);
	}

	@Override
	public int[] getBiomeData(Region.Box region, boolean useQuarterResolution)
			throws MinecraftInterfaceException {
		long start = System.nanoTime();
		int[] biomeData = inner.getBiomeData(region, useQuarterResolution);
		long end = System.nanoTime();
		
		String thread = Thread.currentThread().getName();
		records.add(new BiomeRequestRecordJson(region, useQuarterResolution, start, end-start, thread));
		
		return biomeData;
	}

	@Override
	public void createWorld(long seed, WorldType worldType, String generatorOptions)
			throws MinecraftInterfaceException {
		inner.createWorld(seed, worldType, generatorOptions);
	}

	@Override
	public RecognisedVersion getRecognisedVersion() {
		return inner.getRecognisedVersion();
	}
}
