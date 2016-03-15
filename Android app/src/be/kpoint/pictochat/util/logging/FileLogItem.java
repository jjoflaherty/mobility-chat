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
	private FileLogItem(Type type, Object object, String tag, String message, String... extras) {
		this.type = type;
		this.contextName = object.getClass().getSimpleName();
		this.tag = tag;
		this.message = message;
		this.extras = extras;
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

	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb
		.append(this.type.name()).append(";")
		.append(this.contextName).append(";")
		.append(this.tag).append(";")
		.append(this.message);
		
		for (String extra : extras)
			sb.append(";").append(extra);

		return sb.toString();
	}

	public enum Type {
		DEBUG, INFO, WARN, ERROR
	}
}
