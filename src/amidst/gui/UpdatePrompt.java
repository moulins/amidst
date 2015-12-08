package amidst.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import amidst.utilities.UpdateInformationRetriever;

public class UpdatePrompt {
	private UpdateInformationRetriever retriever = new UpdateInformationRetriever();
	private MainWindow mainWindow;
	private boolean silent;

	public void checkSilently(MainWindow mainWindow) {
		check(mainWindow, true);
	}

	public void check(MainWindow mainWindow) {
		check(mainWindow, false);
	}

	private void check(MainWindow mainWindow, boolean silent) {
		this.mainWindow = mainWindow;
		this.silent = silent;
		try {
			doCheck();
		} catch (MalformedURLException e) {
			error("Error connecting to update server: Malformed URL.");
		} catch (IOException e) {
			error("Error reading update data.");
		} catch (URISyntaxException e) {
			error("Error parsing update URL.");
		} catch (NullPointerException e) {
			error("Error \"NullPointerException\" in update.");
		}
	}

	private void error(String message) {
		if (!silent) {
			mainWindow.displayMessage(message);
		}
	}

	private void doCheck() throws IOException, URISyntaxException {
		retriever.check();
		if (!retriever.isSuccessful()) {
			error(retriever.getErrorMessage());
		} else if (getUserChoice() == JOptionPane.YES_OPTION) {
			openUpdateURL();
		}
	}

	private int getUserChoice() {
		if (retriever.isNewMajorVersionAvailable()) {
			return mainWindow.askToConfirm("Update Found",
					"A new version was found. Would you like to update?");
		} else if (retriever.isNewMinorVersionAvailable()) {
			return mainWindow.askToConfirm("Update Found",
					"A minor revision was found. Update?");
		} else if (!silent) {
			mainWindow.displayMessage("There are no new updates.");
		}
		return JOptionPane.NO_OPTION;
	}

	private void openUpdateURL() throws IOException, URISyntaxException {
		if (!Desktop.isDesktopSupported()) {
			mainWindow.displayMessage("Error unable to open browser.");
		} else {
			Desktop desktop = Desktop.getDesktop();
			if (!desktop.isSupported(Desktop.Action.BROWSE)) {
				mainWindow.displayMessage("Error unable to open browser page.");
			} else {
				desktop.browse(new URI(retriever.getUpdateURL()));
			}
		}
	}
}