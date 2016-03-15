package be.kpoint.pictochat.app.domain;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.os.Build;

import org.apache.http.message.BasicNameValuePair;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.app.Constants;

public class Device implements Serializable
{
	private static final long serialVersionUID = 126422135848786799L;


	private String phoneNr;
	private String pin = "";
	private String api;
	private boolean debug = false;
	private boolean logging = false;


	@SuppressWarnings("unused")
	private Device() {
		//Needed for serialization
	}
	public Device(String phoneNr)
	{
		/*TelephonyManager tm = (TelephonyManager)App.getContext().getSystemService(Context.TELEPHONY_SERVICE);
		this.imei = tm.getDeviceId();*/

		this.phoneNr = phoneNr;
	}
	public static Device create(String phoneNr, String pin) {
		Device device = new Device(phoneNr);
		device.pin = pin;

		return device;
	}

	public String getPhoneNr()
	{
		return this.phoneNr;
	}
	protected String getPin() {
		return this.pin;
	}

	public String getApi() {
		return this.api;
	}
	protected void setApi(String api) {
		this.api = api;
	}

	public void setLogging(boolean logging) {
		this.logging = logging;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}









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



	//Utilities
	public static String calculateHashedPin(String pin) {
		return MD5(MD5(pin) + Constants.SALT);
	}
	public static String MD5(String s)
	{
        MessageDigest m = null;

        try {
                m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
        }

        m.update(s.getBytes(),0,s.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
	}





	public String GetHashedPin() {
		return Device.calculateHashedPin(this.pin);
	}
	public BasicNameValuePair GetPinNameValuePair()
	{
		return new BasicNameValuePair("pin", this.GetHashedPin());
	}
	public BasicNameValuePair GetPhoneNrNameValuePair()
	{
		return new BasicNameValuePair("phoneNr", this.phoneNr);
	}



	public boolean InDebugMode()
	{
		return this.debug;
	}
	public boolean MustLog()
	{
		return this.logging;
	}


	/*public ApiTarget downloadUpdate()
	{
		ArrayList<NameValuePair> sourceNameValuePairs = new ArrayList<NameValuePair>();
		sourceNameValuePairs.add(GetPhoneNrNameValuePair());
		sourceNameValuePairs.add(GetPinNameValuePair());

    	return Constants.app_download.setUrlParameters(sourceNameValuePairs);
	}*/

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

	public enum VersionState {
		ERROR, UP_TO_DATE, UPDATE_AVAILABLE, MUST_UPDATE
	}
}
