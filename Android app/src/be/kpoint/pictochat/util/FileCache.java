package be.kpoint.pictochat.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

public class FileCache {
	protected File cacheDir;

	public FileCache(Context context, String folder) {
		File appCache;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())	|| !Environment.isExternalStorageRemovable())
			appCache = context.getExternalCacheDir();
		else
			appCache = context.getCacheDir();

		this.cacheDir = new File(appCache, folder);
		this.cacheDir.mkdir();
	}

	public File getFile(String name) {
		File file = new File(this.cacheDir, name);
		if (file.exists() && file.isFile() && file.canRead())
			return file;

		return null;
	}

	public void addFile(String name, byte[] bytes) throws IOException {
		FileOutputStream os = null;

		try {
			File file = new File(this.cacheDir, name);
			file.createNewFile();

			os = new FileOutputStream(file);
			os.write(bytes);
		} catch (IOException e) {
			throw e;
		} finally {
			try { if (os != null) os.close(); }
			catch (IOException e) {	e.printStackTrace(); }
		}
	}
	public void removeFile(String name) {
		File file = getFile(name);
		if (file != null)
			file.delete();
	}

	public void clear() {
		File[] files = this.cacheDir.listFiles();

		//listFiles returns null when not a directory
		if (files != null)
			for (File file : files)
				file.delete();
	}
}
