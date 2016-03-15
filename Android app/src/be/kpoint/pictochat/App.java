package be.kpoint.pictochat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import be.kpoint.pictochat.app.R;
import be.kpoint.pictochat.app.activities.AbstractClientChatActivity;
import be.kpoint.pictochat.app.activities.ChatWithClientActivity;
import be.kpoint.pictochat.app.activities.ChatWithCoachesActivity;
import be.kpoint.pictochat.app.activities.ChatWithFriendActivity;
import be.kpoint.pictochat.app.domain.Client;
import be.kpoint.pictochat.app.domain.Coach;
import be.kpoint.pictochat.app.domain.Device;
import be.kpoint.pictochat.app.domain.Friend;
import be.kpoint.pictochat.app.domain.PictoMessage;
import be.kpoint.pictochat.app.domain.TextMessage;
import be.kpoint.pictochat.app.domain.User;
import be.kpoint.pictochat.business.comm.PictoChatCommunicator;
import be.kpoint.pictochat.business.comm.PictoSendResultReceiver;
import be.kpoint.pictochat.business.comm.interfaces.IHereNowReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.IHistoryReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.IPictoMessageReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.IPresenceReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.ITextMessageReceivedListener;
import be.kpoint.pictochat.comm.pubnub.SendResultReceiver;
import be.kpoint.pictochat.logging.Messages;
import be.kpoint.pictochat.logging.Tags;
import be.kpoint.pictochat.util.ImageCache;
import be.kpoint.pictochat.util.Installation;
import be.kpoint.pictochat.util.MaxVolumeSoundPlayer;
import be.kpoint.pictochat.util.logging.FileLogItem;
import be.kpoint.pictochat.util.logging.FileLogger;

public class App extends Application
{
	private static Context context;
	private static Device device;
	private static User user;

	private static Map<User, List<PictoMessage>> messages = new HashMap<User, List<PictoMessage>>();

	private static boolean devMode = false;

	private static PictoChatCommunicator pictoChatCommunicator;
	private static ImageCache imageCache;
	private static FileLogger fileLogger;
	private static ConnectionMonitor connectionMonitor;

	private static MaxVolumeSoundPlayer soundPlayer;

	/*private static SharedPreferences settings;
	private static Editor editor;*/

	@Override
 	public void onCreate() {
		super.onCreate();

		//Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

		App.context = this;

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH'u'mm'm'ss");
		String logName = format.format(calendar.getTime());
		App.fileLogger = new FileLogger(this, logName);

		App.logToFile(FileLogItem.debug(context, Tags.LIFE_CYCLE, Messages.APPLICATION_CREATE, "v" + Device.getVersion()));

		App.imageCache = new ImageCache(this);
		App.pictoChatCommunicator = new PictoChatCommunicator(this);
		App.pictoChatCommunicator.addTextMessageReceivedListener(new ITextMessageReceivedListener() {
			@Override
			public void onTextMessageReceived(TextMessage message) {
				Log.i("msg", message.getSenderName() + ": " + message.getText());
			}
		});
		App.pictoChatCommunicator.addPictoMessageReceivedListener(new IPictoMessageReceivedListener() {
			private Intent buildResultIntent(Friend friend) {
				return ChatWithFriendActivity.buildIntent(App.context, (Client)App.user, friend);
			}
			private Intent buildResultIntent(Client client) {
				return ChatWithClientActivity.buildIntent(App.context, client, (Coach)App.user);
			}
			private Intent buildResultIntent() {
				return ChatWithCoachesActivity.buildIntent(App.context, (Client)App.user);
			}

			private void showNotification(Intent resultIntent, String senderName) {
				NotificationCompat.Builder builder = new NotificationCompat.Builder(App.context)
		        .setSmallIcon(R.drawable.note)
		        .setContentTitle("New message")
		        .setContentText("from " + senderName);

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
				Log.i("msg", message.getSenderName() + ": " + message.getText());

				if (isCoach()) {
					// Client parameter does not contain a value for room
					// Look up client in coach's client list
					Client clientWithRoom = null;
					for (Client c : ((Coach)App.user).getClientList())
						if (c.equals(client))
							clientWithRoom = c;

					if (clientWithRoom != null) {
						App.addMessageToCacheList(client, message);

						soundPlayer.playSound(R.raw.you_wouldnt_believe);

						Intent resultIntent = buildResultIntent(clientWithRoom);
						showNotification(resultIntent, message.getSenderName());
					}
				}
			}
			@Override
			public void onPictoMessageReceived(Coach coach, PictoMessage message) {
				Log.i("msg", message.getSenderName() + ": " + message.getText());

				if (isClient()) {
					App.addMessageToCacheList((Client)App.user, message);

					soundPlayer.playSound(R.raw.you_wouldnt_believe);

					Intent resultIntent = buildResultIntent();
					showNotification(resultIntent, message.getSenderName());
				}
			}
			@Override
			public void onPictoMessageReceived(Friend friend, PictoMessage message) {
				Log.i("msg", message.getSenderName() + ": " + message.getText());

				if (isClient()) {
					App.addMessageToCacheList(friend, message);

					soundPlayer.playSound(R.raw.you_wouldnt_believe);

					Intent resultIntent = buildResultIntent(friend);
					showNotification(resultIntent, message.getSenderName());
				}
			}
		});

		App.connectionMonitor = new ConnectionMonitor();
	    App.context.registerReceiver(App.connectionMonitor, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

	    App.soundPlayer = new MaxVolumeSoundPlayer(this);

	    /*App.settings = this.getSharedPreferences(Constants.PREF_LOCATION_DATA, Context.MODE_PRIVATE);
	    App.editor = App.settings.edit();
		Client[] clientArray = (new Gson()).fromJson(App.settings.getString(Constants.PREF_LOCATION_USERS, "[]"), Client[].class);
		App.clients = new ArrayList<Client>(Arrays.asList(clientArray));*/

	    App.logToFile(FileLogItem.debug(context, Tags.LIFE_CYCLE, Messages.APPLICATION_CREATE_FINISHED));
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

	public static void setDevice(Device device) {
		App.device = device;
	}
	public static Device ThisDevice() {
		return App.device;
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

	public static List<PictoMessage> getMessages(User user) {
		if (App.messages.containsKey(user))
			return App.messages.get(user);
		else
			return new ArrayList<PictoMessage>();
	}

	public static void hostRoom() {
		if (isClient()) {
			Client client = (Client)user;

			try {
				JSONObject jsonClient = new JSONObject();
				jsonClient.put("client", client.getId().getNumber());
				jsonClient.put("firstName", client.getFirstName());
				jsonClient.put("lastName", client.getLastName());

				App.pictoChatCommunicator.setClientState(client, jsonClient);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			App.pictoChatCommunicator.hostRoom(client);
		}
		else
			throw new IllegalStateException();
	}
	public static void hostRoom(Friend friend) {
		if (isClient()) {
			Client client = (Client)user;

			try {
				JSONObject jsonClient = new JSONObject();
				jsonClient.put("client", client.getId().getNumber());
				jsonClient.put("firstName", client.getFirstName());
				jsonClient.put("lastName", client.getLastName());

				App.pictoChatCommunicator.setClientState(client, friend, jsonClient);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			App.pictoChatCommunicator.hostRoom(client, friend);
		}
		else
			throw new IllegalStateException();
	}
	public static void closeRoom() {
		if (isClient()) {
			Client client = (Client)user;
			App.pictoChatCommunicator.closeRoom(client);
		}
		else
			throw new IllegalStateException();
	}
	public static void closeRoom(Friend friend) {
		if (isClient()) {
			Client client = (Client)user;
			App.pictoChatCommunicator.closeRoom(client, friend);
		}
		else
			throw new IllegalStateException();
	}
	public static void joinClientRooms() {
		if (isCoach()) {
			Coach coach = (Coach)user;
			for (Client client : coach.getClients().values())
				App.joinRoom(client);
		}
		else
			throw new IllegalStateException();
	}
	public static void joinRoom(Client client) {
		if (isCoach()) {
			Coach coach = (Coach)user;

			try {
				JSONObject jsonCoach = new JSONObject();
				jsonCoach.put("coach", coach.getId().getNumber());
				jsonCoach.put("firstName", coach.getFirstName());
				jsonCoach.put("lastName", coach.getLastName());

				App.pictoChatCommunicator.setCoachState(client, jsonCoach);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			App.pictoChatCommunicator.joinRoom(client);
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

	public static void sendTextMessageToCoaches(String text, final SendResultReceiver receiver) {
		if (isClient()) {
			Client client = (Client)user;
			App.pictoChatCommunicator.sendTextMessageToCoaches(client, text, receiver);
		}
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
	public static void sendTextMessageToClient(Client client, String text, final SendResultReceiver receiver) {
		if (isCoach()) {
			Coach coach = (Coach)user;
			App.pictoChatCommunicator.sendTextMessageToClient(client, coach, text, receiver);
		}
		else
			throw new IllegalStateException();
	}
	public static void sendPictoMessageToClient(final Client client, PictoMessage message, final PictoSendResultReceiver receiver) {
		if (isCoach()) {
			Coach coach = (Coach)user;
			App.pictoChatCommunicator.sendPictoMessageToClient(client, coach, message, new PictoSendResultReceiver(message) {
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
	public static void sendPictoMessageToFriend(final Friend friend, PictoMessage message, final PictoSendResultReceiver receiver) {
		if (isClient()) {
			final Client client = (Client)user;
			App.pictoChatCommunicator.sendPictoMessageToFriend(client, friend, message, new PictoSendResultReceiver(message) {
				@Override
				public void onSuccess(TextMessage message) {
					if (message instanceof PictoMessage)
						App.addMessageToCacheList(friend, (PictoMessage)message);

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

	public static void addTextMessageReceivedListener(ITextMessageReceivedListener listener) {
		App.pictoChatCommunicator.addTextMessageReceivedListener(listener);
	}
	public static void removeTextMessageReceivedListener(ITextMessageReceivedListener listener) {
		App.pictoChatCommunicator.removeTextMessageReceivedListener(listener);
	}
	public static void addPictoMessageReceivedListener(IPictoMessageReceivedListener listener) {
		App.pictoChatCommunicator.addPictoMessageReceivedListener(listener);
	}
	public static void removePictoMessageReceivedListener(IPictoMessageReceivedListener listener) {
		App.pictoChatCommunicator.removePictoMessageReceivedListener(listener);
	}
	public static void addHereNowReceivedListener(IHereNowReceivedListener listener) {
		App.pictoChatCommunicator.addHereNowReceivedListener(listener);
	}
	public static void removeHereNowReceivedListener(IHereNowReceivedListener listener) {
		App.pictoChatCommunicator.removeHereNowReceivedListener(listener);
	}
	public static void addPresenceReceivedListener(IPresenceReceivedListener listener) {
		App.pictoChatCommunicator.addPresenceReceivedListener(listener);
	}
	public static void removePresenceReceivedListener(IPresenceReceivedListener listener) {
		App.pictoChatCommunicator.removePresenceReceivedListener(listener);
	}
	public static void addHistoryReceivedListener(IHistoryReceivedListener listener) {
		App.pictoChatCommunicator.addHistoryReceivedListener(listener);
	}
	public static void removeHistoryReceivedListener(IHistoryReceivedListener listener) {
		App.pictoChatCommunicator.removeHistoryReceivedListener(listener);
	}

	/*public static void addClient(Client client) {
		if (isCoach()) {
			App.clients.add(client);
			App.persistClients();
		}
		else
			throw new IllegalStateException();
	}
	public static void removeClient(Client client) {
		if (isCoach()) {
			App.clients.remove(client);
			App.persistClients();
		}
		else
			throw new IllegalStateException();
	}
	private static void persistClients() {
		App.editor.putString(Constants.PREF_LOCATION_USERS, (new Gson()).toJson(App.clients.toArray()));
		App.editor.commit();
	}
	public static List<Client> getClients() {
		if (isCoach())
			return App.clients;
		else
			throw new IllegalStateException();
	}*/

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
			Log.d("log", item.toString());
			App.fileLogger.appendText(item.toString());
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
