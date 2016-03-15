package be.kpoint.pictochat.app;

import be.kpoint.pictochat.network.ApiServer;
import be.kpoint.pictochat.network.ApiTarget;

public final class Constants {
	public static final boolean DEBUG = false;
	public static final boolean SHOW_DEBUG = true;

	public static final int STOPPED_TYPING_DELAY = 1000;
	public static final int TAPPED_INPUT_PICTO_DELAY = 2000;


	public static final String FileLogMailRecipient = "steven.solberg@thomasmore.be";

	public static final int SmsServiceNotificationId = 1;
	public static final int RequestCoachServiceNotificationId = 2;

	public static final String SALT = "Pi2ct0ch15at";
	//private static final ApiServer AbleServer = new ApiServer("prod.asi-soft.com:8080/ABLE_Webservice/ABLE_API");
	private static final ApiServer AbleServer = new ApiServer("al.abletoinclude.eu");
	private static final ApiServer AppServer = new ApiServer("ablechat.thomasmore.be/AbleChat/api");

	public static final String TrackingServiceTag = "android.intent.action.vpad_StopVpadTracker";
	public static final String PathEndedHandlerTag = "android.intent.action.vpad_PathEndedHandler";
	public static final String PathKilledHandlerTag = "android.intent.action.vpad_PathKilledHandler";
	public static final String PathGraphMessageTag = "android.intent.action.vpad_PathGraphHandler";
	public static final String PathDebugMessageTag = "android.intent.action.vpad_PathDebugHandler";

	public static final String pictochat_life_cycle_tag = "pct_lfc";

	public static final String PREF_LOCATION_DATA = "pictoChatData";
	public static final String PREF_KEY_COACH = "coach";
	public static final String PREF_KEY_CLIENT = "client";
	public static final String PREF_KEY_FIRSTNAME = "firstName";
	public static final String PREF_KEY_LASTNAME = "lastName";
	public static final String PREF_KEY_PIN = "pin";
	public static final String PREF_LOCATION_USERS = "users";

	//API targets
	public static ApiTarget client_read = new ApiTarget(Constants.AppServer, "client/read", false);
	public static ApiTarget coach_login = new ApiTarget(Constants.AppServer, "coach/login", false);

	//public static ApiTarget text_to_picto = new ApiTarget(Constants.AbleServer, "Text2Picto/", false);
	public static ApiTarget text_to_picto = new ApiTarget(Constants.AbleServer, "Text2Picto.php", false);
}
