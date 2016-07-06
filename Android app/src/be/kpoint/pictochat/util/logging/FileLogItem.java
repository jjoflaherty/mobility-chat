package be.kpoint.pictochat.util.logging;

import android.content.Context;

public class FileLogItem
{
	private Type type;
	private String contextName;
	private String tag;
	private String message;
	private String[] extras;

	private FileLogItem(Type type, Context context, String tag, String message, String... extras) {
		this(type, (Object)context, tag, message, extras);
	}
	private FileLogItem(Type type, String contextName, String tag, String message, String... extras) {
		this.type = type;
		this.contextName = contextName;
		this.tag = tag;
		this.message = message;
		this.extras = extras;
	}
	private FileLogItem(Type type, Object object, String tag, String message, String... extras) {
		this.type = type;
		this.contextName = object.getClass().getSimpleName();
		this.tag = tag;
		this.message = message;
		this.extras = extras;

		if (this.contextName == null)
			this.contextName = object.toString();
	}

	public static FileLogItem debug(Context context, String tag, String message, String... extras) {
		return new FileLogItem(Type.DEBUG, context, tag, message, extras);
	}
	public static FileLogItem info(Context context, String tag, String message, String... extras) {
		return new FileLogItem(Type.INFO, context, tag, message, extras);
	}
	public static FileLogItem warn(Context context, String tag, String message, String... extras) {
		return new FileLogItem(Type.WARN, context, tag, message, extras);
	}
	public static FileLogItem error(Context context, String tag, String message, String... extras) {
		return new FileLogItem(Type.ERROR, context, tag, message, extras);
	}
	public static FileLogItem verbose(Context context, String tag, String message, String... extras) {
		return new FileLogItem(Type.VERBOSE, context, tag, message, extras);
	}
	public static FileLogItem wtf(Context context, String tag, String message, String... extras) {
		return new FileLogItem(Type.WTF, context, tag, message, extras);
	}

	public static FileLogItem debug(String string, String tag, String message, String... extras) {
		return new FileLogItem(Type.DEBUG, string, tag, message, extras);
	}
	public static FileLogItem info(String string, String tag, String message, String... extras) {
		return new FileLogItem(Type.INFO, string, tag, message, extras);
	}
	public static FileLogItem warn(String string, String tag, String message, String... extras) {
		return new FileLogItem(Type.WARN, string, tag, message, extras);
	}
	public static FileLogItem error(String string, String tag, String message, String... extras) {
		return new FileLogItem(Type.ERROR, string, tag, message, extras);
	}
	public static FileLogItem verbose(String string, String tag, String message, String... extras) {
		return new FileLogItem(Type.VERBOSE, string, tag, message, extras);
	}
	public static FileLogItem wtf(String string, String tag, String message, String... extras) {
		return new FileLogItem(Type.WTF, string, tag, message, extras);
	}

	public static FileLogItem debug(Object object, String tag, String message, String... extras) {
		return new FileLogItem(Type.DEBUG, object, tag, message, extras);
	}
	public static FileLogItem info(Object object, String tag, String message, String... extras) {
		return new FileLogItem(Type.INFO, object, tag, message, extras);
	}
	public static FileLogItem warn(Object object, String tag, String message, String... extras) {
		return new FileLogItem(Type.WARN, object, tag, message, extras);
	}
	public static FileLogItem error(Object object, String tag, String message, String... extras) {
		return new FileLogItem(Type.ERROR, object, tag, message, extras);
	}
	public static FileLogItem verbose(Object object, String tag, String message, String... extras) {
		return new FileLogItem(Type.VERBOSE, object, tag, message, extras);
	}
	public static FileLogItem wtf(Object object, String tag, String message, String... extras) {
		return new FileLogItem(Type.WTF, object, tag, message, extras);
	}


	public Type getType() {
		return this.type;
	}
	public String getTag() {
		return this.tag;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb
		.append(this.type.name()).append(";")
		.append(this.contextName).append(";")
		.append(this.tag).append(";")
		.append(this.message);

		for (String extra : this.extras)
			sb.append(";").append(extra);

		return sb.toString();
	}

	public enum Type {
		DEBUG, INFO, WARN, ERROR, VERBOSE, WTF
	}
}
