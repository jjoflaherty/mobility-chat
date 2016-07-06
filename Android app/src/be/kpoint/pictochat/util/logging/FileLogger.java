package be.kpoint.pictochat.util.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import be.kpoint.pictochat.app.Constants;
import be.kpoint.pictochat.util.FileCache;

public class FileLogger extends FileCache
{
	private static final String LINE_SEPERATOR = "\r\n";


	private File currentLog;
	private Set<File> sessionLogs = new HashSet<File>();

	public FileLogger(Context context, String name) {
		super(context, "logs");

		setCurrentLog(name);
	}

	public static FileLogger getDefault(Context context) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH'u'mm'm'ss");
		String logName = format.format(calendar.getTime());
		return new FileLogger(context, logName);
	}

	public void setCurrentLog(String name) {
		this.currentLog = this.selectLogFile(name);
	}

	public void appendText(String text) {
		this.appendText(text, this.currentLog);
	}
	public void appendText(String text, String name) {
		this.appendText(text, this.selectLogFile(name));
	}
	private void appendText(String text, File file) {
		BufferedWriter bw = null;

		try {
			FileWriter writer = new FileWriter(file, true);
			bw = new BufferedWriter(writer, 1024);

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = format.format(Calendar.getInstance().getTime());

			bw.append(time + ";" + text);
			bw.append(LINE_SEPERATOR);

			if (!this.sessionLogs.contains(file))
				this.sessionLogs.add(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try { if (bw != null) bw.close(); }
		catch (IOException e) {	e.printStackTrace(); }
	}

	public void sendWithMail(Context context, String body) {
		this.sendWithMail(context, body, null);
	}
	public void sendWithMail(Context context, String body, String attachment) {
		Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Constants.FileLogMailRecipient});
		intent.putExtra(Intent.EXTRA_SUBJECT, "PictoChat Log " + this.currentLog.getName());
		intent.putExtra(Intent.EXTRA_TEXT, body);

		ArrayList<Uri> uris = new ArrayList<Uri>();
		uris.add(Uri.fromFile(this.currentLog));

		if (attachment != null) {
			File att = this.selectLogFile(attachment);
			if (att.exists() && att.canRead())
				uris.add(Uri.fromFile(att));
		}

		for (File att : this.sessionLogs) {
			if (att.exists() && att.canRead())
				uris.add(Uri.fromFile(att));
		}

		intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

		context.startActivity(intent);
	}

	public List<File> getLogFiles() {
		File[] files = this.cacheDir.listFiles();

		SortedSet<File> sorted = new TreeSet<>(new Comparator<File>() {
			@Override
			public int compare(File lhs, File rhs) {
				return Long.valueOf(lhs.lastModified()).compareTo(Long.valueOf(rhs.lastModified()));
			}
		});
		for (File file : files)
			if (sorted.add(file));

		return new ArrayList<File>(sorted);
	}

	private File selectLogFile(String name) {
		File file = new File(this.cacheDir, name + ".txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return file;
	}
}
