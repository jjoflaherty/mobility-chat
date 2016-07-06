package be.kpoint.pictochat.app.domain;

import android.os.Build;

import be.kpoint.pictochat.App;

public final class Device
{
	public static String getDeviceModel()
	{
		String manufacturer = Build.MANUFACTURER;
	  	String model = Build.MODEL;
	  	if (model.startsWith(manufacturer))
	  		return capitalize(model);
	  	else
	  		return capitalize(manufacturer) + " " + model;
	}
	private static String capitalize(String s)
	{
		if (s == null || s.length() == 0)
			return "";

		char first = s.charAt(0);
		if (Character.isUpperCase(first))
			return s;
		else
			return Character.toUpperCase(first) + s.substring(1);
	}

	public static String getVersion()
	{
		String version = "Unknown";
		try	{ version = App.getContext().getPackageManager().getPackageInfo(App.getContext().getPackageName(), 0).versionName; }
		catch (Exception e) {}

		return version;
	}
	public static Integer getVersionCode()
	{
		int version = 0;
		try	{ version = App.getContext().getPackageManager().getPackageInfo(App.getContext().getPackageName(), 0 ).versionCode; }
		catch (Exception e) {}

		return version;
	}
}
