package be.kpoint.pictochat.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;

import be.kpoint.pictochat.util.FileCache;

public class RestCache extends FileCache
{
	public RestCache(Context context) {
		super(context, "rest");
	}

	public String getCachedResult(String key) {
		BufferedReader br = null;
		String lineSeparator = System.getProperty("line.separator");

		try {
			String hash = calculateHash(key);

			File file = new File(this.cacheDir, hash);
			FileReader reader = new FileReader(file);
			br = new BufferedReader(reader, 1024);

			String line = br.readLine();
			StringBuilder sb = new StringBuilder(line);

			while (line != null) {
		        line = br.readLine();

		        if (line != null) {
			        sb.append(lineSeparator);
		        	sb.append(line);
		        }
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try { if (br != null) br.close(); }
		catch (IOException e) {	e.printStackTrace(); }

		return null;
	}
	public void addResult(String key, String result) {
		OutputStreamWriter writer = null;

		try {
			String hash = calculateHash(key);

			File file = new File(this.cacheDir, hash);
			FileOutputStream stream = new FileOutputStream(file);
			writer = new OutputStreamWriter(stream);

			writer.write(result);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try { if (writer != null) writer.close(); }
		catch (IOException e) {	e.printStackTrace(); }
	}

	private String calculateHash(String value) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.update(value.getBytes("UTF-8"));
		byte[] byteData = digest.digest();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	    }

		return sb.toString();
	}
}
