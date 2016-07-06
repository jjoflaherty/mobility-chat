package be.kpoint.pictochat.logging;

public abstract class Messages
{
	public static final String APPLICATION_CREATE = "Creating application";
	public static final String APPLICATION_CREATE_FINISHED = "Succesfully created application";
	public static final String APPLICATION_DESTROY = "Destroying application";
	public static final String APPLICATION_DESTROY_FINISHED = "Succesfully destroyed application";

	public static final String ACTIVITY_CREATE = "Creating activity";
	public static final String ACTIVITY_CREATE_FINISHED = "Succesfully created activity";
	public static final String ACTIVITY_PAUSE = "Pausing activity";
	public static final String ACTIVITY_PAUSE_FINISHED = "Succesfully paused activity";
	public static final String ACTIVITY_START = "Starting activity";
	public static final String ACTIVITY_START_FINISHED = "Succesfully started activity";
	public static final String ACTIVITY_STOP = "Stopping activity";
	public static final String ACTIVITY_STOP_FINISHED = "Succesfully stopped activity";
	public static final String ACTIVITY_RESUME = "Resuming activity";
	public static final String ACTIVITY_RESUME_FINISHED = "Succesfully resumed activity";
	public static final String ACTIVITY_RESTART = "Restarting activity";
	public static final String ACTIVITY_RESTART_FINISHED = "Succesfully restarted activity";
	public static final String ACTIVITY_DESTROY = "Destroying activity";
	public static final String ACTIVITY_DESTROY_FINISHED = "Succesfully destroyed activity";

	public static final String SERVICE_CREATE = "Creating service";
	public static final String SERVICE_CREATE_FINISHED = "Succesfully created service";
	public static final String SERVICE_START = "Starting service";
	public static final String SERVICE_START_FINISHED = "Succesfully started service";
	public static final String SERVICE_BOUND = "Bound service";
	public static final String SERVICE_DISCONNECTED = "Service disconnected";
	public static final String SERVICE_DESTROY = "Destroying service";
	public static final String SERVICE_DESTROY_FINISHED = "Succesfully destroyed service";

	public static final String ADDED_LISTENER = "Add listener";
	public static final String REMOVED_LISTENER = "Remove listener";

	public static final String CONN_NO_CONNECTION = "No connections";
	public static final String CONN_CHANGED = "Connection changed";

	public static final String CONNECTION_TIMED_OUT = "Connection timed out";
	public static final String CONNECTION_FAILED = "Connection failed";
	public static final String CONNECTION_FAILED_WIFI = "Connection failed. Inaccessible wifi network.";

	public static final String LOGIN_SUCCESFULLY = "Login was succesful";
	public static final String LOGIN_TIMED_OUT = "Login timed out";
	public static final String LOGIN_CLIENT_ERROR = "Login encountered error";
	public static final String LOGIN_SERVER_ERROR = "Login received invalid response";

	public static final String USERS_LOADED_SUCCESFULLY = "Users loaded succesfully";
	public static final String USERS_LOAD_TIMED_OUT = "Users load timed out";
	public static final String USERS_LOAD_CLIENT_ERROR = "Users load encountered error";
	public static final String USERS_LOAD_SERVER_ERROR = "Users load received invalid response";

	public static final String USER_LOADED_SUCCESFULLY = "User loaded succesfully";
	public static final String USER_LOAD_TIMED_OUT = "User load timed out";
	public static final String USER_LOAD_CLIENT_ERROR = "User load encountered error";
	public static final String USER_LOAD_SERVER_ERROR = "User load received invalid response";

	public static final String COACH_LOADED_SUCCESFULLY = "Coach loaded succesfully";
	public static final String COACH_LOAD_TIMED_OUT = "Coach load timed out";
	public static final String COACH_LOAD_CLIENT_ERROR = "Coach load encountered error";
	public static final String COACH_LOAD_SERVER_ERROR = "Coach load received invalid response";

	public static final String MESSAGE_SENT = "Message sent";
	public static final String MESSAGE_RECEIVED = "Message received";
	public static final String MESSAGE_IGNORED = "Message ignored";
	public static final String PRIVATE_MESSAGE_RECEIVED = "Private message received";
	public static final String PRIVATE_MESSAGE_IGNORED = "Private message ignored";

	public static final String SUBSCRIBED = "Subscribed";
}
