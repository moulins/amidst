package amidst.mojangapi.world;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import amidst.documentation.ThreadSafe;
import amidst.logging.Log;

@ThreadSafe
public class SeedHistoryLogger {
	private final File file;

	public SeedHistoryLogger(String filename) {
		this.file = getHistoryFile(filename);
	}

	private File getHistoryFile(String filename) {
		if (filename != null) {
			return new File(filename);
		} else {
			return null;
		}
	}

	public synchronized void log(WorldSeed seed) {
		if (file != null) {
			if (!file.exists()) {
				tryCreateFile();
			}
			if (file.exists() && file.isFile()) {
				writeLine(seed);
			} else {
				Log.w("unable to write seed to seed log file");
			}
		}
	}

	private void tryCreateFile() {
		try {
			file.createNewFile();
		} catch (IOException e) {
			Log.w("Unable to create history file: " + file);
			e.printStackTrace();
		}
	}

	private void writeLine(WorldSeed seed) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file, true);
			writer.append(createLine(seed));
		} catch (IOException e) {
			Log.w("Unable to write to history file.");
			e.printStackTrace();
		} finally {
			closeWriter(writer);
		}
	}

	private String createLine(WorldSeed seed) {
		return new Timestamp(new Date().getTime()) + " " + seed.getLong()
				+ "\r\n";
	}

	private void closeWriter(FileWriter writer) {
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (IOException e) {
			Log.w("Unable to close writer for history file.");
			e.printStackTrace();
		}
	}
}