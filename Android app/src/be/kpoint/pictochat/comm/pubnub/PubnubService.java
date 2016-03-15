package be.kpoint.pictochat.comm.pubnub;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.app.R;
import be.kpoint.pictochat.util.WeakReferenceHandler;

public class PubnubService extends Service
{
	public static final int MSG_MESSAGE_RECEIVED = 1;
	public static final int MSG_SERVICE_MESSENGER = 2;
	public static final int MSG_SUBSCRIBE = 10;
	public static final int MSG_UNSUBSCRIBE = 11;
	public static final int MSG_SEND_JSON = 12;
	public static final int MSG_SEND_STRING = 13;
	public static final int MSG_HISTORY = 14;
	public static final int MSG_HERENOW = 15;
	public static final int MSG_PRESENCE = 16;
	public static final int MSG_SETSTATE = 17;
	public static final int MSG_GETSTATE = 18;
	public static final int MSG_HISTORY_RECEIVED = 30;
	public static final int MSG_HERENOW_RECEIVED = 31;
	public static final int MSG_PRESENCE_RECEIVED = 32;
	public static final int MSG_GETSTATE_RECEIVED = 33;

	public static final String BUNDLE_ACTIVITY_MESSENGER = "activity_messenger";
	public static final String BUNDLE_JSON = "json";
	public static final String BUNDLE_MESSAGE = "message";
	public static final String BUNDLE_CHANNEL = "channel";
	public static final String BUNDLE_RESULTRECEIVER = "resultreceiver";
	public static final String BUNDLE_UUID = "uuid";
	public static final String BUNDLE_STATE = "state";
	public static final String BUNDLE_AMOUNT = "amount";

	//public static final String MESSAGE_INTENT = "pubnub.service.message"; //TODO Remove this if testing shows no problems


	private List<IPubnubMessageReceivedListener> pubnubMessageReceivedListeners = new ArrayList<IPubnubMessageReceivedListener>();
	private List<IPubnubHistoryReceivedListener> pubnubHistoryReceivedListeners = new ArrayList<IPubnubHistoryReceivedListener>();
	private List<IPubnubHereNowReceivedListener> pubnubHereNowReceivedListeners = new ArrayList<IPubnubHereNowReceivedListener>();
	private List<IPubnubPresenceReceivedListener> pubnubPresenceReceivedListeners = new ArrayList<IPubnubPresenceReceivedListener>();
	private List<IPubnubGetStateReceivedListener> pubnubGetStateReceivedListeners = new ArrayList<IPubnubGetStateReceivedListener>();


	private WakeLock wakeLock;

	private Pubnub pubnub = null;

	private Messenger localMessenger;
	private Messenger remoteMessenger;

	private PubnubServiceMessageHandler messageHandler;
	private final PubnubBinder binder = new PubnubBinder();


	@Override
	public void onCreate() {
        super.onCreate();

        this.messageHandler = new PubnubServiceMessageHandler(this);
        this.localMessenger = new Messenger(this.messageHandler);

        if (this.pubnub == null) {
        	this.pubnub = new Pubnub(
		            PubnubServiceSettings.PUBLISH_KEY,
		            PubnubServiceSettings.SUBSCRIBE_KEY,
		            PubnubServiceSettings.SECRET_KEY,
		            PubnubServiceSettings.CIPHER_KEY,
		            PubnubServiceSettings.SSL
		    );

        	this.pubnub.setUUID(App.getUUId());
        	this.pubnub.setCacheBusting(false);
        	this.pubnub.setResumeOnReconnect(PubnubServiceSettings.RESUME_ON_RECONNECT);

        	Log.d("pub", "uuid: " + this.pubnub.getUUID());
        }

        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        this.wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SubscribeAtBoot");
        if (this.wakeLock != null) {
        	this.wakeLock.acquire();

            Log.i("PUBNUB", "Partial Wake Lock : " + this.wakeLock.isHeld());
        }

        Log.i("PUBNUB", "PubnubService created");
    }
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			this.remoteMessenger = (Messenger)bundle.get(BUNDLE_ACTIVITY_MESSENGER);

			Message msg = Message.obtain();
			msg.what = PubnubService.MSG_SERVICE_MESSENGER;
			msg.obj = this.localMessenger;
			this.sendMessageToRemoteMessenger(msg);
		}

		/*
		 * WARNING: If this code ever fails because of a NoClassDef or ClassNotFoundException you should try reverting to a previous Eclipse installation.
		 * Updating the sdk tools through the Android SDK Manager might mess up your project and prevent support libraries from being exported properly.
		 *
		 * - Steven Solberg
		 */
		PendingIntent contentIntent = PendingIntent.getActivity(
			    getApplicationContext(),
			    0,
			    new Intent(),
			    PendingIntent.FLAG_UPDATE_CURRENT);
		Notification note = new NotificationCompat.Builder(this)
			.setContentTitle("Pubnub")
		    .setContentText("AbleChat started Pubnub service")
		    .setSmallIcon(R.drawable.note)
		    .setContentIntent(contentIntent)
			.build();

		startForeground(334, note);

		return START_STICKY;
	}
	@Override
	public IBinder onBind(Intent intent)
	{
	    return this.binder;
	}
    @Override
    public void onDestroy()
    {
        if (this.wakeLock != null) {
        	this.wakeLock.release();
        	this.wakeLock = null;

            Log.i("PUBNUB", "Partial Wake Lock : " + this.wakeLock.isHeld());
            Toast.makeText(this, "Partial Wake Lock : " + this.wakeLock.isHeld(), Toast.LENGTH_LONG).show();
        }

        Log.i("PUBNUB", "PubnubService destroyed");

        super.onDestroy();
    }

    public static void start(Context context, Messenger messenger) {
    	Intent service = new Intent(context, PubnubService.class);
    	service.putExtra(PubnubService.BUNDLE_ACTIVITY_MESSENGER, messenger);

    	context.startService(service);
    }

	public void send(PubnubChannel channel, String message) {
		this.pubnub.publish(channel.toString(), message, this.sendCallback);
	}
	public void send(PubnubChannel channel, JSONObject message, final SendResultReceiver receiver) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final String time = format.format(Calendar.getInstance().getTime());
		final String uuid = this.pubnub.getUUID();

		try {
			message.put("uuid", uuid);
			message.put("t", time);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		this.pubnub.publish(channel.toString(), message, new Callback() {
			@Override
			public void successCallback(String channel, Object response) {
				System.out.println(response.toString());

				Bundle data = new Bundle();
				data.putString(BUNDLE_CHANNEL, channel);
				data.putString("response", response.toString());

				if (response instanceof JSONArray) {
					Long timeToken;
					try {
						JSONArray jsonResponse = (JSONArray)response;
						timeToken = jsonResponse.getLong(2);

						data.putLong("serverTime", timeToken);
						data.putString("appTime", time);
						data.putString("uuid", uuid);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				receiver.send(SendResultReceiver.SUCCESS, data);
			}

			@Override
			public void errorCallback(String channel, PubnubError error) {
				System.out.println(error.toString());

				Bundle data = new Bundle();
				data.putString(BUNDLE_CHANNEL, channel);
				data.putString("error", error.toString());
				receiver.send(SendResultReceiver.ERROR, data);
			}
		});
	}

	public void subscribe(PubnubChannel channel) {
		try {
			this.pubnub.subscribe(channel.toString(), this.subscribeCallback);
		} catch (PubnubException e) {
			Log.d("PUBNUB", e.toString());
		}
	}
	public void unsubscribe(PubnubChannel channel) {
		this.pubnub.unsubscribe(channel.toString());
	}
	public void hereNow(PubnubChannel channel) {
		this.pubnub.hereNow(channel.toString(), true, true, this.hereNowCallback);
	}
	public void presence(PubnubChannel channel) {
		try {
			this.pubnub.presence(channel.toString(), this.presenceCallback);
		} catch (PubnubException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void history(PubnubChannel channel, int amount) {
		this.pubnub.history(channel.toString(), -1, -1, amount, false, true, this.historyCallback);
	}
	public void setState(PubnubChannel channel, JSONObject state) {
		this.pubnub.setState(channel.toString(), this.pubnub.getUUID(), state, this.setStateCallback);
	}
	public void getState(final PubnubChannel channel, final String uuid) {
		this.pubnub.getState(channel.toString(), uuid, new Callback() {
			@Override
			public void successCallback(String ch, Object state) {
				Bundle data = new Bundle();
				data.putString(PubnubService.BUNDLE_CHANNEL, channel.toString());
				data.putString(PubnubService.BUNDLE_UUID, uuid);
				data.putString(PubnubService.BUNDLE_STATE, state.toString());

				Message msg = Message.obtain();
				msg.what = PubnubService.MSG_GETSTATE_RECEIVED;
				msg.setData(data);
				PubnubService.this.messageHandler.sendMessage(msg);
			}
		});
	}


	private void sendMessageToRemoteMessenger(Message message) {
		if (this.remoteMessenger != null) {
			try {
				this.remoteMessenger.send(message);
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	//Local listeners
	private Callback sendCallback = new Callback() {
		@Override
		public void successCallback(String channel, Object response) {
			Long timeToken;

			if (response instanceof JSONArray) {
				try {
					JSONArray jsonResponse = (JSONArray)response;
					timeToken = jsonResponse.getLong(2);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			System.out.println(response.toString());
		}

		@Override
		public void errorCallback(String channel, PubnubError error) {
			System.out.println(error.toString());
		}
	};
	private Callback subscribeCallback = new Callback() {
		@Override
		public void connectCallback(String channel, Object message) {
			Log.d("pub", channel + ": " + message);
		}

		@Override
		public void successCallback(String channel, Object message) {
			Log.d("pub", channel + ": " + message);

			Bundle data = new Bundle();
			data.putString(PubnubService.BUNDLE_CHANNEL, channel);
			data.putString(PubnubService.BUNDLE_MESSAGE, message.toString());

			Message msg = Message.obtain();
			msg.what = PubnubService.MSG_MESSAGE_RECEIVED;
			msg.setData(data);
			PubnubService.this.messageHandler.sendMessage(msg);

			/* TODO Remove this if testing shows no problems
			Intent pubnubMessageIntent = new Intent(MESSAGE_INTENT);
			pubnubMessageIntent.putExtras(data);
			PubnubService.this.sendBroadcast(pubnubMessageIntent);*/
		}
	};
	private Callback hereNowCallback = new Callback() {
		@Override
		public void successCallback(String channel, Object message) {
			Bundle data = new Bundle();
			data.putString(PubnubService.BUNDLE_CHANNEL, channel);
			data.putString(PubnubService.BUNDLE_MESSAGE, message.toString());

			Message msg = Message.obtain();
			msg.what = PubnubService.MSG_HERENOW_RECEIVED;
			msg.setData(data);
			PubnubService.this.messageHandler.sendMessage(msg);
		}
	};
	private Callback presenceCallback = new Callback() {
		@Override
		public void successCallback(String channel, Object message) {
			Bundle data = new Bundle();
			data.putString(PubnubService.BUNDLE_CHANNEL, channel);
			data.putString(PubnubService.BUNDLE_MESSAGE, message.toString());

			Message msg = Message.obtain();
			msg.what = PubnubService.MSG_PRESENCE_RECEIVED;
			msg.setData(data);
			PubnubService.this.messageHandler.sendMessage(msg);
		}

		@Override
		public void errorCallback(String arg0, PubnubError arg1) {
			Log.d("", arg0);
		}
	};
	private Callback historyCallback = new Callback() {
		@Override
		public void successCallback(String channel, Object message) {
			Bundle data = new Bundle();
			data.putString(PubnubService.BUNDLE_CHANNEL, channel);
			data.putString(PubnubService.BUNDLE_MESSAGE, message.toString());

			Message msg = Message.obtain();
			msg.what = PubnubService.MSG_HISTORY_RECEIVED;
			msg.setData(data);
			PubnubService.this.messageHandler.sendMessage(msg);
		}
	};
	private Callback setStateCallback = new Callback() {
		@Override
		public void successCallback(String arg0, Object arg1, String arg2) {
			// TODO Auto-generated method stub
			super.successCallback(arg0, arg1, arg2);
		}

		@Override
		public void successCallback(String arg0, Object arg1) {
			// TODO Auto-generated method stub
			super.successCallback(arg0, arg1);
		}

		@Override
		public void errorCallback(String arg0, PubnubError arg1) {
			// TODO Auto-generated method stub
			super.errorCallback(arg0, arg1);
		}
	};

	//Messaging Handler
	private static class PubnubServiceMessageHandler extends WeakReferenceHandler<PubnubService>
	{
		public PubnubServiceMessageHandler(PubnubService reference) {
			super(reference);
		}

		@Override
		public void handleMessage(PubnubService service, Message msg)
		{
			Message message;

			switch (msg.what) {
				//Received from callbacks
				case PubnubService.MSG_MESSAGE_RECEIVED:
					message = Message.obtain(msg);
					service.sendMessageToRemoteMessenger(message);

					service.onMessageReceived(
						msg.getData().getString(PubnubService.BUNDLE_CHANNEL),
						msg.getData().getString(PubnubService.BUNDLE_MESSAGE));
					break;

				case PubnubService.MSG_HISTORY_RECEIVED:
					message = Message.obtain(msg);
					service.sendMessageToRemoteMessenger(message);

					service.onHistoryReceived(
						msg.getData().getString(PubnubService.BUNDLE_CHANNEL),
						msg.getData().getString(PubnubService.BUNDLE_MESSAGE));
					break;

				case PubnubService.MSG_HERENOW_RECEIVED:
					message = Message.obtain(msg);
					service.sendMessageToRemoteMessenger(message);

					service.onHereNowReceived(
						msg.getData().getString(PubnubService.BUNDLE_CHANNEL),
						msg.getData().getString(PubnubService.BUNDLE_MESSAGE));
					break;

				case PubnubService.MSG_PRESENCE_RECEIVED:
					message = Message.obtain(msg);
					service.sendMessageToRemoteMessenger(message);

					service.onPresenceReceived(
						msg.getData().getString(PubnubService.BUNDLE_CHANNEL),
						msg.getData().getString(PubnubService.BUNDLE_MESSAGE));
					break;

				case PubnubService.MSG_GETSTATE_RECEIVED:
					message = Message.obtain(msg);
					service.sendMessageToRemoteMessenger(message);

					service.onGetStateReceived(
						msg.getData().getString(PubnubService.BUNDLE_CHANNEL),
						msg.getData().getString(PubnubService.BUNDLE_UUID),
						msg.getData().getString(PubnubService.BUNDLE_STATE));
					break;

				//Received from manager
				case PubnubService.MSG_SUBSCRIBE:
					service.subscribe((PubnubChannel)msg.obj);
					break;

				case PubnubService.MSG_UNSUBSCRIBE:
					service.unsubscribe((PubnubChannel)msg.obj);
					break;

				case PubnubService.MSG_SEND_JSON:
					try {
						service.send(
							(PubnubChannel)msg.getData().getSerializable(PubnubService.BUNDLE_CHANNEL),
							new JSONObject(msg.getData().getString(PubnubService.BUNDLE_JSON)),
							(SendResultReceiver)msg.getData().get(BUNDLE_RESULTRECEIVER));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;

				case PubnubService.MSG_SEND_STRING:
					service.send(
						(PubnubChannel)msg.getData().getSerializable(PubnubService.BUNDLE_CHANNEL),
						msg.getData().getString(PubnubService.BUNDLE_MESSAGE));
					break;

				case MSG_HISTORY:
					service.history(
						(PubnubChannel)msg.getData().getSerializable(PubnubService.BUNDLE_CHANNEL),
						msg.getData().getInt(BUNDLE_AMOUNT, 5));
					break;

				case MSG_HERENOW:
					service.hereNow((PubnubChannel)msg.obj);
					break;

				case MSG_PRESENCE:
					service.presence((PubnubChannel)msg.obj);
					break;

				case PubnubService.MSG_SETSTATE:
					try {
						service.setState(
							(PubnubChannel)msg.getData().getSerializable(PubnubService.BUNDLE_CHANNEL),
							new JSONObject(msg.getData().getString(PubnubService.BUNDLE_JSON)));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;

				case PubnubService.MSG_GETSTATE:
					service.getState(
						(PubnubChannel)msg.getData().getSerializable(PubnubService.BUNDLE_CHANNEL),
						msg.getData().getString(PubnubService.BUNDLE_UUID));
					break;

				default:
					break;
			}
		}
	}

	//Listener delegation
	private void onMessageReceived(String channel, String message) {
		PubnubChannel pc = new PubnubChannel(channel);

		for (IPubnubMessageReceivedListener listener : this.pubnubMessageReceivedListeners)
			try {
				listener.onMessageReceived(pc, message);
			} catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), e.toString());
			}
	}
	private void onHistoryReceived(String channel, String message) {
		PubnubChannel pc = new PubnubChannel(channel);

		try {
			JSONArray jsonArray = new JSONArray(message);
			JSONArray jsonHistory = jsonArray.getJSONArray(0);
			int start = jsonArray.getInt(1);
			int end = jsonArray.getInt(2);

			for (IPubnubHistoryReceivedListener listener : this.pubnubHistoryReceivedListeners)
				try {
					listener.onHistoryReceived(pc, jsonHistory.toString(), start, end);
				} catch (Exception e) {
					Log.e(this.getClass().getSimpleName(), e.toString());
				}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	private void onHereNowReceived(String channel, String message) {
		PubnubChannel pc = new PubnubChannel(channel);

		for (IPubnubHereNowReceivedListener listener : this.pubnubHereNowReceivedListeners)
			try {
				listener.onHereNowReceived(pc, message);
			} catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), e.toString());
			}
	}
	private void onPresenceReceived(String channel, String message) {
		PubnubChannel pc = new PubnubChannel(channel);

		for (IPubnubPresenceReceivedListener listener : this.pubnubPresenceReceivedListeners)
			try {
				listener.onPresenceReceived(pc, message);
			} catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), e.toString());
			}
	}
	private void onGetStateReceived(String channel, String uuid, String message) {
		PubnubChannel pc = new PubnubChannel(channel);

		for (IPubnubGetStateReceivedListener listener : this.pubnubGetStateReceivedListeners)
			try {
				listener.onGetStateReceived(pc, uuid, message);
			} catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), e.toString());
			}
	}

	//Listener management
	public void addPubnubMessageReceivedListener(IPubnubMessageReceivedListener listener) {
		this.removePubnubMessageReceivedListener(listener);
		this.pubnubMessageReceivedListeners.add(listener);
	}
	public void removePubnubMessageReceivedListener(IPubnubMessageReceivedListener listener) {
		this.pubnubMessageReceivedListeners.remove(listener);
	}

	public void addPubnubHistoryReceivedListener(IPubnubHistoryReceivedListener listener) {
		this.removePubnubHistoryReceivedListener(listener);
		this.pubnubHistoryReceivedListeners.add(listener);
	}
	public void removePubnubHistoryReceivedListener(IPubnubHistoryReceivedListener listener) {
		this.pubnubHistoryReceivedListeners.remove(listener);
	}

	public void addPubnubHereNowReceivedListener(IPubnubHereNowReceivedListener listener) {
		this.removePubnubHereNowReceivedListener(listener);
		this.pubnubHereNowReceivedListeners.add(listener);
	}
	public void removePubnubHereNowReceivedListener(IPubnubHereNowReceivedListener listener) {
		this.pubnubHereNowReceivedListeners.remove(listener);
	}

	public void addPubnubPresenceReceivedListener(IPubnubPresenceReceivedListener listener) {
		this.removePubnubPresenceReceivedListener(listener);
		this.pubnubPresenceReceivedListeners.add(listener);
	}
	public void removePubnubPresenceReceivedListener(IPubnubPresenceReceivedListener listener) {
		this.pubnubPresenceReceivedListeners.remove(listener);
	}

	public void addPubnubGetStateReceivedListener(IPubnubGetStateReceivedListener listener) {
		this.removePubnubGetStateReceivedListener(listener);
		this.pubnubGetStateReceivedListeners.add(listener);
	}
	public void removePubnubGetStateReceivedListener(IPubnubGetStateReceivedListener listener) {
		this.pubnubGetStateReceivedListeners.remove(listener);
	}


	//Listeners
	public interface IPubnubMessageReceivedListener {
		public void onMessageReceived(PubnubChannel channel, String message);
	}
	public interface IPubnubHistoryReceivedListener {
		public void onHistoryReceived(PubnubChannel channel, String history, int start, int end);
	}
	public interface IPubnubHereNowReceivedListener {
		public void onHereNowReceived(PubnubChannel channel, String message);
	}
	public interface IPubnubPresenceReceivedListener {
		public void onPresenceReceived(PubnubChannel channel, String message);
	}
	public interface IPubnubGetStateReceivedListener {
		public void onGetStateReceived(PubnubChannel channel, String uuid, String state);
	}

    //Binder
  	public class PubnubBinder extends Binder
  	{
  		public PubnubService getService() {
  			// Return this instance of PubnubService so clients can call public methods
  			return PubnubService.this;
  		}
  	}
}
