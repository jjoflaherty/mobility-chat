package be.kpoint.pictochat.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.kpoint.pictochat.network.ApiServer;
import be.kpoint.pictochat.network.ApiTarget;

public final class Constants {
	public static final boolean DEBUG = false;
	public static final boolean SHOW_DEBUG = true;

	public static final int STOPPED_TYPING_DELAY = 2000;
	public static final int TAPPED_INPUT_PICTO_DELAY = 2000;

	public static final String FileLogMailRecipient = "steven.solberg@thomasmore.be";

	public static final int PubnubServiceNotificationId = 334;

	public static final int UPLOAD_LOG_HOUR = 1;
	public static final int UPLOAD_LOG_MINUTE = 00;

	//Preference Keys
	public static final String PREF_LOCATION_DATA = "pictoChatData";
	public static final String PREF_KEY_LAST_READ = "lastReadData";
	public static final String PREF_KEY_LAST_PRIVATE_READ = "lastPrivateReadData";
	public static final String PREF_KEY_LAST_CHECKED = "lastCheckedData";
	public static final String PREF_KEY_LAST_PRIVATE_CHECKED = "lastPrivateCheckedData";
	public static final String PREF_KEY_COACH = "coach";
	public static final String PREF_KEY_CLIENT = "client";
	public static final String PREF_KEY_FIRSTNAME = "firstName";
	public static final String PREF_KEY_LASTNAME = "lastName";
	public static final String PREF_KEY_PIN = "pin";
	public static final String PREF_LOCATION_USERS = "users";

	//Picto Databases
	public static final List<String> Databases = new ArrayList<>();

	//Supported languages
	public static final Map<String, String> Languages = new HashMap<>();

	//API Servers
	private static final ApiServer AbleServer = new ApiServer("al.abletoinclude.eu");
	private static final ApiServer AppServer = new ApiServer("ablechat.thomasmore.be/AbleChat/api");
	private static final ApiServer KULServer = new ApiServer("picto.ccl.kuleuven.be");

	//API Targets
	public static ApiTarget client_read = new ApiTarget(Constants.AppServer, "client/read", false, true);
	public static ApiTarget client_friend = new ApiTarget(Constants.AppServer, "client/friend", false, false);
	public static ApiTarget coach_login = new ApiTarget(Constants.AppServer, "coach/login", false, false);
	public static ApiTarget coach_read = new ApiTarget(Constants.AppServer, "coach/read", false, true);
	public static ApiTarget text_to_picto = new ApiTarget(Constants.AbleServer, "Text2Picto.php", false, false);


	static {
		Databases.add("beta");
		Databases.add("sclera");

		Languages.put("en", "english");
		Languages.put("nl", "dutch");
	}
}
