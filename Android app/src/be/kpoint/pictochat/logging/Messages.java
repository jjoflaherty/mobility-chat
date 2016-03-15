package be.kpoint.pictochat.logging;

public abstract class Messages
{
	public static final String APPLICATION_CREATE = "Creating application";
	public static final String APPLICATION_CREATE_FINISHED = "Succesfully created application";
	public static final String APPLICATION_DESTROY = "Destroying application";
	public static final String APPLICATION_DESTROY_FINISHED = "Succesfully destroyed application";

	public static final String ACTIVITY_CREATE = "Creating activity";
	public static final String ACTIVITY_CREATE_FINISHED = "Succesfully created activity";

	public static final String SERVICE_CREATE = "Creating service";
	public static final String SERVICE_CREATE_FINISHED = "Succesfully created service";
	public static final String SERVICE_START = "Starting service";
	public static final String SERVICE_START_FINISHED = "Succesfully started service";
	public static final String SERVICE_BOUND = "Bound service";
	public static final String SERVICE_DISCONNECTED = "Service disconnected";
	public static final String SERVICE_DESTROY = "Destroying service";
	public static final String SERVICE_DESTROY_FINISHED = "Succesfully destroyed service";

	public static final String CONN_NO_CONNECTION = "No connections";
	public static final String CONN_CHANGED = "Connection changed";

	public static final String CONNECTION_TIMED_OUT = "Connection timed out";
	public static final String CONNECTION_FAILED = "Connection failed";
	public static final String CONNECTION_FAILED_WIFI = "Connection failed. Inaccessible wifi network.";

	public static final String LOGIN_SUCCESFULLY = "Login was succesful";
	public static final String LOGIN_TIMED_OUT = "Login timed out";
	public static final String LOGIN_CLIENT_ERROR = "Login encountered error";
	public static final String LOGIN_SERVER_ERROR = "Login received invalid response";

	public static final String BLIND_LOGIN_SUCCESFULLY = "Blind login was succesful";
	public static final String BLIND_LOGIN_TIMED_OUT = "Blind login timed out";
	public static final String BLIND_LOGIN_CLIENT_ERROR = "Blind login encountered error";
	public static final String BLIND_LOGIN_SERVER_ERROR = "Blind login received invalid response";

	public static final String UPDATE = "Update";
	public static final String UPDATE_TIMED_OUT = "Update timed out";
	public static final String UPDATE_AVAILABLE = "Update is available";
	public static final String UPDATE_MANDATORY = "Mandatory update available";
	public static final String UPDATE_CLIENT_ERROR = "Update encountered error";
	public static final String UPDATE_SERVER_ERROR = "Update received invalid response";

	public static final String ORGANISATION_LOADED_SUCCESFULLY = "Organisation loaded succesfully";
	public static final String ORGANISATION_LOAD_TIMED_OUT = "Organisation load timed out";
	public static final String ORGANISATION_LOAD_CLIENT_ERROR = "Organisation load encountered error";
	public static final String ORGANISATION_LOAD_SERVER_ERROR = "Organisation load received invalid response";

	public static final String USERS_LOADED_SUCCESFULLY = "Users loaded succesfully";
	public static final String USERS_LOAD_TIMED_OUT = "Users load timed out";
	public static final String USERS_LOAD_CLIENT_ERROR = "Users load encountered error";
	public static final String USERS_LOAD_SERVER_ERROR = "Users load received invalid response";

	public static final String USER_LOADED_SUCCESFULLY = "User loaded succesfully";
	public static final String USER_LOAD_TIMED_OUT = "User load timed out";
	public static final String USER_LOAD_CLIENT_ERROR = "User load encountered error";
	public static final String USER_LOAD_SERVER_ERROR = "User load received invalid response";

	public static final String USER_LINKED_SUCCESFULLY = "User linked succesfully";
	public static final String USER_LINK_TIMED_OUT = "User link timed out";
	public static final String USER_LINK_CLIENT_ERROR = "User link encountered error";
	public static final String USER_LINK_SERVER_ERROR = "User link received invalid response";

	public static final String PATH_LOADED_SUCCESFULLY = "Path loaded succesfully";
	public static final String PATH_LOAD_TIMED_OUT = "Path load timed out";
	public static final String PATH_LOAD_CLIENT_ERROR = "Path load encountered error";
	public static final String PATH_LOAD_SERVER_ERROR = "Path load received invalid response";
	public static final String PATH_INVALID = "Path was invalid";

	public static final String PATHSINFO_LOADED_SUCCESFULLY = "Pathinfo list loaded succesfully";
	public static final String PATHSINFO_LOAD_TIMED_OUT = "Pathinfo list load timed out";
	public static final String PATHSINFO_LOAD_CLIENT_ERROR = "Pathinfo list load encountered error";
	public static final String PATHSINFO_LOAD_SERVER_ERROR = "Pathinfo list load received invalid response";

	public static final String SIM_TRANSPORTATION_LOADED_SUCCESFULLY = "Transportation for simulation loaded succesfully";
	public static final String SIM_TRANSPORTATION_LOAD_TIMED_OUT = "Transportation load for simulation timed out";
	public static final String SIM_TRANSPORTATION_LOAD_CLIENT_ERROR = "Transportation load for simulation encountered error";
	public static final String SIM_TRANSPORTATION_LOAD_SERVER_ERROR = "Transportation load for simulation received invalid response";

	public static final String START_PATH_SUCCESFULLY = "Start path succesfully";
	public static final String START_PATH_CLIENT_ERROR = "Start path encountered error";
	public static final String START_PATH_SERVER_ERROR = "Start path received invalid response";
	public static final String START_PATH_TIMED_OUT = "Start path timed out";

	public static final String START_PATH_SIM_SUCCESFULLY = "Start path sim succesfully";
	public static final String START_PATH_SIM_CLIENT_ERROR = "Start path sim encountered error";
	public static final String START_PATH_SIM_SERVER_ERROR = "Start path sim received invalid response";
	public static final String START_PATH_SIM_TIMED_OUT = "Start path sim timed out";

	public static final String START_TRACKING_SUCCESFULLY = "Start tracking succesfully";
	public static final String START_TRACKING_CLIENT_ERROR = "Start tracking encountered error";
	public static final String START_TRACKING_SERVER_ERROR = "Start tracking received invalid response";
	public static final String START_TRACKING_TIMED_OUT = "Start tracking timed out";
	public static final String STOP_TRACKING = "Stop tracking";

	public static final String CONTINUE_PATH_SUCCESFULLY = "Continue path succesfully";
	public static final String CONTINUE_PATH_CLIENT_ERROR = "Continue path encountered error";
	public static final String CONTINUE_PATH_SERVER_ERROR = "Continue path received invalid response";
	public static final String CONTINUE_PATH_TIMED_OUT = "Continue path timed out";

	public static final String CONTINUE_ACTIVE_TRANSPORTATION = "Continuing active transportation";
	public static final String CONTINUE_ALLOWED_TRANSPORTATION = "Continuing allowed transportation";

	public static final String USER_HAS_ACTIVE_TRANSPORTATION = "User has an active transportation";
	public static final String LOAD_PENDING_TRANSPORTATION_SUCCESFULLY = "Pending transportation loaded succesfully";
	public static final String LOAD_PENDING_TRANSPORTATION_TIMED_OUT = "Pending transportation load timed out";
	public static final String LOAD_PENDING_TRANSPORTATION_CLIENT_ERROR = "Pending transportation load encountered error";
	public static final String LOAD_PENDING_TRANSPORTATION_SERVER_ERROR = "Pending transportation load received invalid response";
	public static final String LOAD_FORBIDDEN_TRANSPORTATION_SUCCESFULLY = "Forbidden transportation loaded succesfully";
	public static final String LOAD_FORBIDDEN_TRANSPORTATION_TIMED_OUT = "Forbidden transportation load timed out";
	public static final String LOAD_FORBIDDEN_TRANSPORTATION_CLIENT_ERROR = "Forbidden transportation load encountered error";
	public static final String LOAD_FORBIDDEN_TRANSPORTATION_SERVER_ERROR = "Forbidden transportation load received invalid response";

	public static final String CLEAR_PATH_GATES = "Clearing path gates";
	public static final String FIND_LOCATION_FOR_PATH_LIST = "Finding location for path list";

	public static final String COACH_DENIED = "Coach denied path";
	public static final String COACH_REPLIED = "Coach replied";
	public static final String COACH_FIRST = "Coach was first";
	public static final String COACH_NONE_FOUND = "No coaches found";
	public static final String COACH_ALREADY_REPLIED = "Coach had already replied";

	public static final String FIRST_FIX = "First location found";
	public static final String LOCATION_PROJECTION = "Calculating path coordinate from location";

	public static final String END_OF_PATH_TEST = "Checking for end of path";
	public static final String END_OF_PATH_TRUE = "At end of path";
	public static final String END_OF_PATH_FALSE = "Left end of path";

	public static final String CLOSE_TO_PATH_TEST = "Checking for diverting off path";
	public static final String CLOSE_TO_PATH_TRUE = "At path";
	public static final String CLOSE_TO_PATH_FALSE = "Left path";

	public static final String SAFEZONE_TEST = "Checking for safe zone";
	public static final String SAFEZONE_TRUE = "At safe zone";
	public static final String SAFEZONE_FALSE = "Left safe zone";

	public static final String DANGERZONE_TEST = "Checking for dangerzone";
	public static final String DANGERZONE_TRUE = "At dangerzone";
	public static final String DANGERZONE_FALSE = "Left dangerzone";

	public static final String POI_TEST = "Checking for poi";
	public static final String POI_TRUE = "At poi";
	public static final String POI_FALSE = "Left poi";

	public static final String WAYPOINT_TEST = "Checking for waypoint";
	public static final String WAYPOINT_TRUE = "At waypoint";
	public static final String WAYPOINT_FALSE = "Left waypoint";

	public static final String SPEED_TIMER_STARTED = "Speed timer started";
	public static final String SPEED_TIMER_RUNNING = "Speed timer already running";
	public static final String SPEED_TIMER_STOPPED = "Speed timer stopped";
	public static final String SPEED_TIMER_EXTENDED = "Speed timer extended";
	public static final String SPEED_ABNORMAL = "Speed abnormal";
	public static final String SPEED_NORMAL = "Speed normal";

	public static final String ALERT_NEW_ADDED = "New alert added";
	public static final String ALERT_GENERATING = "Alert sent to generator";
	public static final String ALERT_NEW_SENT = "New alert sent";

	public static final String ALERT_GENERATOR_RETURNED_NULL = "Alert generator returned null in callback";
	public static final String ALERT_GENERATOR_INVALID_TRANSITION = "Alert generator encountered an invalid transition";
}
