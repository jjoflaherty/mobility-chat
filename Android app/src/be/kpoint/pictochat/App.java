package be.kpoint.pictochat;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import be.kpoint.pictochat.api.rest.ids.AbstractId;
import be.kpoint.pictochat.api.rest.ids.ClientId;
import be.kpoint.pictochat.app.Constants;
import be.kpoint.pictochat.app.R;
import be.kpoint.pictochat.app.activities.client.AbstractClientChatActivity;
import be.kpoint.pictochat.app.activities.client.ChatWithCoachesActivity;
import be.kpoint.pictochat.app.activities.client.PrivateChatActivity;
import be.kpoint.pictochat.app.activities.coach.GroupChatWithClientActivity;
import be.kpoint.pictochat.app.domain.Client;
import be.kpoint.pictochat.app.domain.Coach;
import be.kpoint.pictochat.app.domain.Device;
import be.kpoint.pictochat.app.domain.PictoMessage;
import be.kpoint.pictochat.app.domain.TextMessage;
import be.kpoint.pictochat.app.domain.User;
import be.kpoint.pictochat.app.domain.serializer.JsonDateSerializer;
import be.kpoint.pictochat.app.domain.serializer.JsonUserIdSerializer;
import be.kpoint.pictochat.app.receivers.AlarmReceiver;
import be.kpoint.pictochat.business.comm.PictoChatCommunicator;
import be.kpoint.pictochat.business.comm.PictoSendResultReceiver;
import be.kpoint.pictochat.business.comm.enums.AppState;
import be.kpoint.pictochat.business.comm.enums.Presence;
import be.kpoint.pictochat.business.comm.interfaces.IHereNowReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.IHistoryReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.IPictoMessageReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.IPresenceReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.ITextMessageReceivedListener;
import be.kpoint.pictochat.comm.pubnub.PubnubChannel;
import be.kpoint.pictochat.logging.Messages;
import be.kpoint.pictochat.logging.Tags;
import be.kpoint.pictochat.util.ExceptionHandler;
import be.kpoint.pictochat.util.ImageCache;
import be.kpoint.pictochat.util.Installation;
import be.kpoint.pictochat.util.MaxVolumeSoundPlayer;
import be.kpoint.pictochat.util.logging.FileLogItem;
import be.kpoint.pictochat.util.logging.FileLogger;
import be.kpoint.pictochat.util.mailer.Mailer;

public class App extends Application
{
	private static Context context;
	private static User user;
	private static int badgeCount = 0;
	private static boolean alreadySubscribed = false;

	private static Map<User, List<PictoMessage>> messages = new HashMap<User, List<PictoMessage>>();
	private static Map<User, List<PictoMessage>> privateMessages = new HashMap<User, List<PictoMessage>>();

	/* Stores timestamps when the local user last saw a particular channel
	 * These are used to indicate new and unread messages. */
	private static Map<AbstractId, Date> lastCheckedTime = new HashMap<AbstractId, Date>();
	private static Map<AbstractId, Date> lastPrivateCheckedTime = new HashMap<AbstractId, Date>();

	/* Stores timestamps when the remote user last saw a particular channel.
	 * These are used to indicate a 'read' indicator for coaches. */
	private static Map<ClientId, Date> lastReadTime = new HashMap<ClientId, Date>();
	private static Map<ClientId, Date> lastPrivateReadTime = new HashMap<ClientId, Date>();

	private static boolean devMode = false;

	private static PictoChatCommunicator pictoChatCommunicator;
	private static ImageCache imageCache;
	private static FileLogger fileLogger;
	private static PendingIntent pendingAlarmIntent;
	private static ConnectionMonitor connectionMonitor;

	private static MaxVolumeSoundPlayer soundPlayer;

	private static SharedPreferences settings;
	private static Editor editor;

	@Override
 	public void onCreate() {
		super.onCreate();

		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

		App.context = this;

		App.fileLogger = FileLogger.getDefault(this);

		App.logToFile(FileLogItem.debug(context, Tags.LIFE_CYCLE, Messages.APPLICATION_CREATE, "v" + Device.getVersion()));

		App.imageCache = new ImageCache(this);
		App.pictoChatCommunicator = new PictoChatCommunicator(this);
		App.pictoChatCommunicator.addTextMessageReceivedListener(new ITextMessageReceivedListener() {
			@Override
			public void onTextMessageReceived(TextMessage message) {
				Log.d("msg", "App received text message from " + message.getSenderName() + ": " + message.getText());
			}
		});
		App.pictoChatCommunicator.addPictoMessageReceivedListener(new IPictoMessageReceivedListener() {
			private Intent buildResultIntent(User remote) {
				return PrivateChatActivity.buildIntent(App.context, (Client)App.user, remote);
			}
			private Intent buildResultIntent(Client client) {
				return GroupChatWithClientActivity.buildIntent(App.context, client, (Coach)App.user);
			}
			private Intent buildResultIntent() {
				return ChatWithCoachesActivity.buildIntent(App.context, (Client)App.user);
			}

			private void showNotification(Intent resultIntent, String senderName) {
				NotificationCompat.Builder builder = new NotificationCompat.Builder(App.context)
		        .setSmallIcon(R.drawable.note)
		        .setContentTitle(getResources().getString(R.string.new_message_title_notification))
		        .setContentText(getResources().getString(R.string.new_message_notification, senderName));

				// The stack builder object will contain an artificial back stack for the
				// started Activity.
				// This ensures that navigating backward from the Activity leads out of
				// your application to the Home screen.
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(App.context);
				// Adds the back stack for the Intent (but not the Intent itself)
				stackBuilder.addParentStack(AbstractClientChatActivity.class);
				// Adds the Intent that starts the Activity to the top of the stack
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent =
				        stackBuilder.getPendingIntent(
				            0,
				            PendingIntent.FLAG_UPDATE_CURRENT
				        );
				builder.setContentIntent(resultPendingIntent);

				NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify("msg", 1, builder.build());
			}

			@Override
			public void onPictoMessageReceived(Client client, PictoMessage message) {
				App.logToFile(FileLogItem.debug(App.this, Tags.MSG, Messages.MESSAGE_RECEIVED, "client", client.getFullName(), message.getSenderName(), message.getText()));

				if (isCoach()) {
					// Client parameter does not contain a value for room
					// Look up client in coach's client list
					Client clientWithRoom = null;
					for (Client c : ((Coach)App.user).getClientList())
						if (c.equals(client))
							clientWithRoom = c;

					if (clientWithRoom != null) {
						App.addMessageToCacheList(client, message);

						soundPlayer.playSound(R.raw.bicycle_horn);

						increaseBadge(context, 1);

						Intent resultIntent = buildResultIntent(clientWithRoom);
						showNotification(resultIntent, message.getSenderName());
					}
				}
			}
			@Override
			public void onPictoMessageReceived(Coach coach, Client client, PictoMessage message) {
				App.logToFile(FileLogItem.debug(App.this, Tags.MSG, Messages.MESSAGE_RECEIVED, "coach", coach.getFullName(), message.getSenderName(), message.getText()));

				if (isClient()) {
					App.addMessageToCacheList((Client)App.user, message);

					soundPlayer.playSound(R.raw.bicycle_horn);

					increaseBadge(context, 1);

					Intent resultIntent = buildResultIntent();
					showNotification(resultIntent, message.getSenderName());
				}
				else if (isCoach()) {
					App.addMessageToCacheList(client, message);

					soundPlayer.playSound(R.raw.bicycle_horn);

					increaseBadge(context, 1);

					Intent resultIntent = buildResultIntent();
					showNotification(resultIntent, message.getSenderName());
				}
			}
			@Override
			public void onPictoMessageReceived(User remote, PictoMessage message) {
				App.logToFile(FileLogItem.debug(App.this, Tags.MSG, Messages.PRIVATE_MESSAGE_RECEIVED, "user", user.getFullName(), message.getSenderName(), message.getText()));

				App.addPrivateMessageToCacheList(remote, message);

				soundPlayer.playSound(R.raw.bicycle_horn);

				increaseBadge(context, 1);

				Intent resultIntent = buildResultIntent(remote);
				showNotification(resultIntent, message.getSenderName());
			}
		});
		App.pictoChatCommunicator.addPresenceReceivedListener(new IPresenceReceivedListener() {
			@Override
			public void onPresenceReceived(PubnubChannel channel, boolean isPrivate, User host, User user, Presence presence, AppState state, Date lastRead) {
				if (user instanceof Client && lastRead != null) {
					Client client = (Client)user;

					if (isPrivate)
						App.setLastPrivateReadTime(client, lastRead);
					else
						App.setLastReadTime(client, lastRead);
				}
			}
		});

		App.connectionMonitor = new ConnectionMonitor();
	    App.context.registerReceiver(App.connectionMonitor, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

	    App.soundPlayer = new MaxVolumeSoundPlayer(this);

	    App.settings = this.getSharedPreferences(Constants.PREF_LOCATION_DATA, Context.MODE_PRIVATE);
	    App.editor = App.settings.edit();

		loadSettings();

		setUploadLogAlarm(context);

	    App.logToFile(FileLogItem.debug(context, Tags.LIFE_CYCLE, Messages.APPLICATION_CREATE_FINISHED));
	}

	private void loadSettings() {
		String lastChecked = settings.getString(Constants.PREF_KEY_LAST_CHECKED, "{}");
		String lastPrivateChecked = settings.getString(Constants.PREF_KEY_LAST_PRIVATE_CHECKED, "{}");
		String lastRead = settings.getString(Constants.PREF_KEY_LAST_READ, "{}");
		String lastPrivateRead = settings.getString(Constants.PREF_KEY_LAST_PRIVATE_READ, "{}");

		GsonBuilder gsonUserMap = new GsonBuilder();
		gsonUserMap.registerTypeAdapter(AbstractId.class, new JsonUserIdSerializer());
		gsonUserMap.registerTypeAdapter(Date.class, new JsonDateSerializer());

		GsonBuilder gsonClientMap = new GsonBuilder();
		gsonClientMap.registerTypeAdapter(AbstractId.class, new JsonUserIdSerializer());
		gsonClientMap.registerTypeAdapter(Date.class, new JsonDateSerializer());

		try {
			Type t1 = new TypeToken<Map<AbstractId, Date>>(){}.getType();
			lastCheckedTime = gsonUserMap.create().fromJson(lastChecked, t1);
			lastPrivateCheckedTime = gsonUserMap.create().fromJson(lastPrivateChecked, t1);

			Type t2 = new TypeToken<Map<ClientId, Date>>(){}.getType();
			lastReadTime = gsonClientMap.create().fromJson(lastRead, t2);
			lastPrivateReadTime = gsonClientMap.create().fromJson(lastPrivateRead, t2);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public static Context getContext() {
		return context;
	}

	public static String getUUId() {
		return Installation.getId(context);
	}

	public static String buildString(int resId, Object... formatArgs) {
		return getContext().getString(resId, formatArgs);
	}

	public static void setDevMode() {
		App.devMode = true;
	}
	public static boolean isDevMode() {
		return App.devMode;
	}

	public static User getUser() {
		return user;
	}
	public static void setUser(User user) {
		App.user = user;
	}

	public static boolean isAlreadySubscribed() {
		return alreadySubscribed;
	}
	public static void setAlreadySubscribed(boolean alreadySubscribed) {
		App.alreadySubscribed = alreadySubscribed;
	}

	public static List<PictoMessage> getMessages(User user) {
		if (App.messages.containsKey(user))
			return App.messages.get(user);
		else
			return new ArrayList<PictoMessage>();
	}
	public static List<PictoMessage> getPrivateMessages(User user) {
		if (App.privateMessages.containsKey(user))
			return App.privateMessages.get(user);
		else
			return new ArrayList<PictoMessage>();
	}


	public static Date getLastCheckedTime(User user) {
		return lastCheckedTime.get(user.getId());
	}
	public static void setLastCheckedTime(User user, Date time) {
		lastCheckedTime.put(user.getId(), time);

		GsonBuilder gson = new GsonBuilder().enableComplexMapKeySerialization();
		gson.registerTypeAdapter(AbstractId.class, new JsonUserIdSerializer());
		gson.registerTypeAdapter(Date.class, new JsonDateSerializer());

		Type type = new TypeToken<Map<AbstractId, Date>>(){}.getType();
		String lastChecked = gson.create().toJson(lastCheckedTime, type);

		editor.putString(Constants.PREF_KEY_LAST_CHECKED, lastChecked);
		editor.commit();

		Log.d("lct", "Set lastCheckedTime to " + time.toString() + " for user " + user.getFullName());
	}

	public static Date getLastPrivateCheckedTime(User user) {
		return lastPrivateCheckedTime.get(user.getId());
	}
	public static void setLastPrivateCheckedTime(User user, Date time) {
		lastPrivateCheckedTime.put(user.getId(), time);

		GsonBuilder gson = new GsonBuilder().enableComplexMapKeySerialization();
		gson.registerTypeAdapter(AbstractId.class, new JsonUserIdSerializer());
		gson.registerTypeAdapter(Date.class, new JsonDateSerializer());

		Type type = new TypeToken<Map<AbstractId, Date>>(){}.getType();
		String lastPrivateChecked = gson.create().toJson(lastPrivateCheckedTime, type);

		editor.putString(Constants.PREF_KEY_LAST_PRIVATE_CHECKED, lastPrivateChecked);
		editor.commit();

		Log.d("lct", "Set lastPrivateCheckedTime to " + time.toString() + " for user " + user.getFullName());
	}

	public static Date getLastReadTime(Client client) {
		return lastReadTime.get(client.getId());
	}
	public static void setLastReadTime(Client client, Date time) {
		App.lastReadTime.put(client.getId(), time);

		GsonBuilder gson = new GsonBuilder().enableComplexMapKeySerialization();
		gson.registerTypeAdapter(AbstractId.class, new JsonUserIdSerializer());
		gson.registerTypeAdapter(Date.class, new JsonDateSerializer());

		Type type = new TypeToken<Map<AbstractId, Date>>(){}.getType();
		String lastRead = gson.create().toJson(lastReadTime, type);

		editor.putString(Constants.PREF_KEY_LAST_READ, lastRead);
		editor.commit();
	}

	public static Date getLastPrivateReadTime(Client client) {
		return lastPrivateReadTime.get(client.getId());
	}
	public static void setLastPrivateReadTime(Client client, Date time) {
		App.lastPrivateReadTime.put(client.getId(), time);

		GsonBuilder gson = new GsonBuilder().enableComplexMapKeySerialization();
		gson.registerTypeAdapter(AbstractId.class, new JsonUserIdSerializer());
		gson.registerTypeAdapter(Date.class, new JsonDateSerializer());

		Type type = new TypeToken<Map<AbstractId, Date>>(){}.getType();
		String lastPrivateRead = gson.create().toJson(lastPrivateReadTime, type);

		editor.putString(Constants.PREF_KEY_LAST_PRIVATE_READ, lastPrivateRead);
		editor.commit();
	}

	public static void hostRoom(AppState appState) {
		if (isClient()) {
			Client client = (Client)user;

			App.pictoChatCommunicator.setClientState(client, appState);
			App.pictoChatCommunicator.hostRoom(client);
		}
		else
			throw new IllegalStateException();
	}
	public static void hostRoom(User remote, AppState appState) {
		if (isClient())
			App.pictoChatCommunicator.setUserState((Client)user, remote, appState);
		else if (isCoach())
			App.pictoChatCommunicator.setUserState((Coach)user, remote, appState);

		App.pictoChatCommunicator.hostRoom(user, remote);
	}
	public static void closeRoom() {
		if (isClient()) {
			Client client = (Client)user;
			App.pictoChatCommunicator.closeRoom(client);
		}
		else
			throw new IllegalStateException();
	}
	public static void closeRoom(User remote) {
		App.pictoChatCommunicator.closeRoom(user, remote);
	}
	public static void joinClientRooms(AppState appState) {
		if (isCoach()) {
			Coach coach = (Coach)user;
			for (Client client : coach.getClients().values())
				App.joinRoom(client, appState);
		}
		else
			throw new IllegalStateException();
	}
	public static void joinRoom(Client client, AppState appState) {
		if (isCoach()) {
			Coach coach = (Coach)user;

			App.pictoChatCommunicator.setCoachState(client, coach, appState);
			App.pictoChatCommunicator.joinRoom(client);
		}
		else
			throw new IllegalStateException();
	}
	public static void leaveClientRooms() {
		if (isCoach()) {
			Coach coach = (Coach)user;
			for (Client client : coach.getClients().values())
				App.leaveRoom(client);
		}
		else
			throw new IllegalStateException();
	}
	public static void leaveRoom(Client client) {
		if (isCoach())
			App.pictoChatCommunicator.leaveRoom(client);
		else
			throw new IllegalStateException();
	}

	public static void sendPictoMessageToCoaches(PictoMessage message, final PictoSendResultReceiver receiver) {
		if (isClient()) {
			final Client client = (Client)user;
			App.pictoChatCommunicator.sendPictoMessageToCoaches(client, message, new PictoSendResultReceiver(message) {
				@Override
				public void onSuccess(TextMessage message) {
					if (message instanceof PictoMessage)
						App.addMessageToCacheList(client, (PictoMessage)message);

					receiver.onSuccess(message);
				}

				@Override
				public void onError(Bundle resultData) {
					receiver.onError(resultData);
				}
			});
		}
		else
			throw new IllegalStateException();
	}
	public static void sendPictoMessageToClient(final Client client, PictoMessage message, final PictoSendResultReceiver receiver) {
		if (isCoach()) {
			Coach coach = (Coach)user;
			App.pictoChatCommunicator.sendPictoMessageToClientRoom(client, coach, message, new PictoSendResultReceiver(message) {
				@Override
				public void onSuccess(TextMessage message) {
					if (message instanceof PictoMessage)
						App.addMessageToCacheList(client, (PictoMessage)message);

					receiver.onSuccess(message);
				}

				@Override
				public void onError(Bundle resultData) {
					receiver.onError(resultData);
				}
			});
			App.pictoChatCommunicator.sendPictoMessageToCoaches(client, message, new PictoSendResultReceiver(message) {
				@Override
				public void onSuccess(TextMessage message) {}

				@Override
				public void onError(Bundle resultData) {}
			});
		}
		else
			throw new IllegalStateException();
	}
	public static void sendPictoMessageToUser(final User remote, PictoMessage message, final PictoSendResultReceiver receiver) {
		App.pictoChatCommunicator.sendPictoMessageToUser(user, remote, message, new PictoSendResultReceiver(message) {
			@Override
			public void onSuccess(TextMessage message) {
				if (message instanceof PictoMessage)
					App.addPrivateMessageToCacheList(remote, (PictoMessage)message);

				receiver.onSuccess(message);
			}

			@Override
			public void onError(Bundle resultData) {
				receiver.onError(resultData);
			}
		});
	}

	public static void addTextMessageReceivedListener(ITextMessageReceivedListener listener) {
		logToFile(FileLogItem.debug(App.context, Tags.LISTENER, Messages.ADDED_LISTENER, listener.toString()));

		App.pictoChatCommunicator.addTextMessageReceivedListener(listener);
	}
	public static void removeTextMessageReceivedListener(ITextMessageReceivedListener listener) {
		logToFile(FileLogItem.debug(App.context, Tags.LISTENER, Messages.REMOVED_LISTENER, listener.toString()));

		App.pictoChatCommunicator.removeTextMessageReceivedListener(listener);
	}
	public static void addPictoMessageReceivedListener(IPictoMessageReceivedListener listener) {
		logToFile(FileLogItem.debug(App.context, Tags.LISTENER, Messages.ADDED_LISTENER, listener.toString()));

		App.pictoChatCommunicator.addPictoMessageReceivedListener(listener);
	}
	public static void removePictoMessageReceivedListener(IPictoMessageReceivedListener listener) {
		logToFile(FileLogItem.debug(App.context, Tags.LISTENER, Messages.REMOVED_LISTENER, listener.toString()));

		App.pictoChatCommunicator.removePictoMessageReceivedListener(listener);
	}
	public static void addHereNowReceivedListener(IHereNowReceivedListener listener) {
		logToFile(FileLogItem.debug(App.context, Tags.LISTENER, Messages.ADDED_LISTENER, listener.toString()));

		App.pictoChatCommunicator.addHereNowReceivedListener(listener);
	}
	public static void removeHereNowReceivedListener(IHereNowReceivedListener listener) {
		logToFile(FileLogItem.debug(App.context, Tags.LISTENER, Messages.REMOVED_LISTENER, listener.toString()));

		App.pictoChatCommunicator.removeHereNowReceivedListener(listener);
	}
	public static void addPresenceReceivedListener(IPresenceReceivedListener listener) {
		logToFile(FileLogItem.debug(App.context, Tags.LISTENER, Messages.ADDED_LISTENER, listener.toString()));

		App.pictoChatCommunicator.addPresenceReceivedListener(listener);
	}
	public static void removePresenceReceivedListener(IPresenceReceivedListener listener) {
		logToFile(FileLogItem.debug(App.context, Tags.LISTENER, Messages.REMOVED_LISTENER, listener.toString()));

		App.pictoChatCommunicator.removePresenceReceivedListener(listener);
	}
	public static void addHistoryReceivedListener(IHistoryReceivedListener listener) {
		logToFile(FileLogItem.debug(App.context, Tags.LISTENER, Messages.ADDED_LISTENER, listener.toString()));

		App.pictoChatCommunicator.addHistoryReceivedListener(listener);
	}
	public static void removeHistoryReceivedListener(IHistoryReceivedListener listener) {
		logToFile(FileLogItem.debug(App.context, Tags.LISTENER, Messages.REMOVED_LISTENER, listener.toString()));

		App.pictoChatCommunicator.removeHistoryReceivedListener(listener);
	}


	private static void addMessageToCacheList(User user, PictoMessage message) {
		List<PictoMessage> list;
		if (App.messages.containsKey(user))
			list = App.messages.get(user);
		else {
			list = new ArrayList<PictoMessage>();
			App.messages.put(user, list);
		}

		list.add(message);
	}
	private static void addPrivateMessageToCacheList(User user, PictoMessage message) {
		List<PictoMessage> list;
		if (App.privateMessages.containsKey(user))
			list = App.privateMessages.get(user);
		else {
			list = new ArrayList<PictoMessage>();
			App.privateMessages.put(user, list);
		}

		list.add(message);
	}

	private static Boolean isClient() {
		return user instanceof Client;
	}
	private static Boolean isCoach() {
		return user instanceof Coach;
	}

	public static PictoChatCommunicator getPictoChatCommunicator() {
		return App.pictoChatCommunicator;
	}
	public static void logToFile(FileLogItem item) {
		try {
			switch (item.getType()) {
				case VERBOSE:
					Log.v("log-" + item.getTag(), item.toString());
					break;
				case DEBUG:
					Log.d("log-" + item.getTag(), item.toString());
					break;
				case ERROR:
					Log.e("log-" + item.getTag(), item.toString());
					break;
				case INFO:
					Log.i("log-" + item.getTag(), item.toString());
					break;
				case WARN:
					Log.w("log-" + item.getTag(), item.toString());
					break;
				case WTF:
					Log.wtf("log-" + item.getTag(), item.toString());
					break;
				default:
					break;
			}

			App.getFileLogger().appendText(item.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ImageCache getImageCache() {
		return App.imageCache;
	}
	public static FileLogger getFileLogger() {
		return App.fileLogger;
	}
	public static void pushNextFileLogger() {
		App.fileLogger = FileLogger.getDefault(context);
	}

	public static void setUploadLogAlarm(Context context) {
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		if (pendingAlarmIntent != null)
			alarmManager.cancel(pendingAlarmIntent);

		Intent alarmIntent = new Intent(context, AlarmReceiver.class);
		pendingAlarmIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, Constants.UPLOAD_LOG_HOUR);
		calendar.set(Calendar.MINUTE, Constants.UPLOAD_LOG_MINUTE);

		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingAlarmIntent);
	}

	public static void processAndMailLogs(final String subject) {
		final JSONObject jsonBody = new JSONObject();
		final String user = App.getUser() == null ? "null" : App.getUser().getFullName();

		final List<File> files = App.getFileLogger().getLogFiles();

		//Create a new log file so we can process and remove old ones
		App.pushNextFileLogger();

		try {
			jsonBody.put("files", files.size());
			jsonBody.put("uuid", App.getUUId());
			jsonBody.put("user", user);
		} catch (JSONException e) {

		}

		AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				String body;

				try {
					body = jsonBody.toString(3);
				} catch (JSONException e) {
					body = "Failed to produce json";
				}

				Mailer mailer = new Mailer(context);
				boolean success = mailer.send(subject, body, "steven.solberg@thomasmore.be", files);

				return success;
			}

			@Override
			protected void onPostExecute(Boolean success) {
				if (success)
					for (File file : files)
						file.delete();
			}
		};
		task.execute();
	}

	public static void increaseBadge(Context context, int count) {
		App.setBadge(context, badgeCount + count);
	}
	public static void setBadge(Context context, int count) {
	    String launcherClassName = getLauncherClassName(context);
	    if (launcherClassName == null)
	        return;

	    Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
	    intent.putExtra("badge_count", count);
	    intent.putExtra("badge_count_package_name", context.getPackageName());
	    intent.putExtra("badge_count_class_name", launcherClassName);

	    context.sendBroadcast(intent);

	    badgeCount = count;
	}

	private static String getLauncherClassName(Context context) {

	    PackageManager pm = context.getPackageManager();

	    Intent intent = new Intent(Intent.ACTION_MAIN);
	    intent.addCategory(Intent.CATEGORY_LAUNCHER);

	    List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
	    for (ResolveInfo resolveInfo : resolveInfos) {
	        String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
	        if (pkgName.equalsIgnoreCase(context.getPackageName())) {
	            String className = resolveInfo.activityInfo.name;
	            return className;
	        }
	    }
	    return null;
	}


	private class ConnectionMonitor extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	String action = intent.getAction();
	        if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
	            return;

	    	ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

	        if (activeNetInfo != null)
	        	App.logToFile(FileLogItem.warn(App.context, Tags.CONN, Messages.CONN_CHANGED, activeNetInfo.toString()));
	        else
	        	App.logToFile(FileLogItem.warn(App.context, Tags.CONN, Messages.CONN_NO_CONNECTION));
	    }
	}
}
