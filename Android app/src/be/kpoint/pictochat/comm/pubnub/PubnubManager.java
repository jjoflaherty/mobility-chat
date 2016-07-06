package be.kpoint.pictochat.comm.pubnub;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import be.kpoint.pictochat.comm.pubnub.PubnubService.IPubnubGetStateReceivedListener;
import be.kpoint.pictochat.comm.pubnub.PubnubService.IPubnubHereNowReceivedListener;
import be.kpoint.pictochat.comm.pubnub.PubnubService.IPubnubHistoryReceivedListener;
import be.kpoint.pictochat.comm.pubnub.PubnubService.IPubnubMessageReceivedListener;
import be.kpoint.pictochat.comm.pubnub.PubnubService.IPubnubPresenceReceivedListener;
import be.kpoint.pictochat.comm.pubnub.PubnubService.PubnubBinder;
import be.kpoint.pictochat.util.WeakReferenceHandler;

public class PubnubManager
{
	private List<IPubnubManagerListener> pubnubManagerListeners = new ArrayList<IPubnubManagerListener>();
	private List<IPubnubMessageReceivedListener> pubnubMessageReceivedListeners = new ArrayList<IPubnubMessageReceivedListener>();
	private List<IPubnubHistoryReceivedListener> pubnubHistoryReceivedListeners = new ArrayList<IPubnubHistoryReceivedListener>();
	private List<IPubnubHereNowReceivedListener> pubnubHereNowReceivedListeners = new ArrayList<IPubnubHereNowReceivedListener>();
	private List<IPubnubPresenceReceivedListener> pubnubPresenceReceivedListeners = new ArrayList<IPubnubPresenceReceivedListener>();
	private List<IPubnubGetStateReceivedListener> pubnubGetStateReceivedListeners = new ArrayList<IPubnubGetStateReceivedListener>();

	private WeakReference<Context> weakContext;

	private boolean isBound = false;
	private PubnubService service;
	private PubnubServiceConnection connection;
	private Messenger localMessenger;
	private Messenger remoteMessenger;
	private PubnubServiceMessageHandler messageHandler;


	public PubnubManager(final Context context) {
		this.weakContext = new WeakReference<Context>(context);
		this.connection = new PubnubServiceConnection();

		this.messageHandler = new PubnubServiceMessageHandler(this);
		this.localMessenger = new Messenger(this.messageHandler);
	}

	public Boolean isStarted() {
		return this.remoteMessenger != null || this.service != null;
	}

	public void dispose() {
		this.pubnubManagerListeners.clear();

		Context context = this.weakContext.get();
		if (context != null) {
			if (this.isBound) {
				context.unbindService(this.connection);
				this.isBound = false;
			}
			else{
				context.stopService(new Intent(context, PubnubService.class));
				//context.unregisterReceiver(this.receiver); //TODO Remove this if testing shows no problems

				onServiceStopped();
			}
		}
	}


	public void bind() {
		Context context = this.weakContext.get();
		if (context != null) {
			Intent service = new Intent(context, PubnubService.class);
			context.bindService(service, this.connection, Context.BIND_AUTO_CREATE);
		}
	}
	public void start() {
		Context context = this.weakContext.get();
		if (context != null) {
			PubnubService.start(context, this.localMessenger);
			onServiceStarted();
		}
	}

	public void callWhenStarted(final ICallWhenPubnubManagerStartedListener listener) {
		if (isStarted())
			listener.onStarted();
		else {
			this.pubnubManagerListeners.add(new IPubnubManagerListener() {
				@Override
				public void onServiceConnected() {
					PubnubManager.this.pubnubManagerListeners.remove(this);
					listener.onStarted();
				}
				@Override
				public void onRemoteMessengerReceived() {
					PubnubManager.this.pubnubManagerListeners.remove(this);
					listener.onStarted();
				}

				@Override
				public void onServiceStopped() {}
				@Override
				public void onServiceStarted() {}
				@Override
				public void onServiceDisconnected() {}
			});
		}
	}


	public void send(PubnubChannel channel, String message) {
		if (this.service != null)
			this.service.send(channel, message);
		else {
			try {
				Bundle data = new Bundle();
				data.putSerializable(PubnubService.BUNDLE_CHANNEL, channel);
				data.putString(PubnubService.BUNDLE_MESSAGE, message);

				Message msg = Message.obtain();
				msg.what = PubnubService.MSG_SEND_STRING;
				msg.setData(data);

				this.remoteMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	public void send(PubnubChannel channel, JSONObject message, SendResultReceiver receiver) {
		if (this.service != null)
			this.service.send(channel, message, receiver);
		else {
			try {
				Bundle data = new Bundle();
				data.putSerializable(PubnubService.BUNDLE_CHANNEL, channel);
				data.putString(PubnubService.BUNDLE_JSON, message.toString());
				data.putParcelable(PubnubService.BUNDLE_RESULTRECEIVER, receiver);

				Message msg = Message.obtain();
				msg.what = PubnubService.MSG_SEND_JSON;
				msg.setData(data);

				this.remoteMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	public void subscribe(PubnubChannel channel) {
		Log.i(this.getClass().getSimpleName(), channel.getName());

		if (this.service != null) {
			this.service.subscribe(channel);
		}
		else {
			try {
				Message msg = Message.obtain();
				msg.what = PubnubService.MSG_SUBSCRIBE;
				msg.obj = channel;

				this.remoteMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	public void unsubscribe(PubnubChannel channel) {
		if (this.service != null)
			this.service.unsubscribe(channel);
		else {
			try {
				Message msg = Message.obtain();
				msg.what = PubnubService.MSG_UNSUBSCRIBE;
				msg.obj = channel;

				this.remoteMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	public void history(PubnubChannel channel, int amount) {
		if (this.service != null)
			this.service.history(channel, amount);
		else {
			try {
				Bundle data = new Bundle();
				data.putSerializable(PubnubService.BUNDLE_CHANNEL, channel);
				data.putInt(PubnubService.BUNDLE_AMOUNT, amount);

				Message msg = Message.obtain();
				msg.what = PubnubService.MSG_HISTORY;
				msg.setData(data);

				this.remoteMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	public void history(PubnubChannel channel, Date start) {
		if (this.service != null)
			this.service.history(channel, start);
		else {
			try {
				Bundle data = new Bundle();
				data.putSerializable(PubnubService.BUNDLE_CHANNEL, channel);
				data.putSerializable(PubnubService.BUNDLE_TIME, start);

				Message msg = Message.obtain();
				msg.what = PubnubService.MSG_HISTORY_SINCE;
				msg.setData(data);

				this.remoteMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	public void hereNow(PubnubChannel channel) {
		Log.i(this.getClass().getSimpleName(), channel.getName());

		if (this.service != null) {
			this.service.hereNow(channel);
		}
		else {
			try {
				Message msg = Message.obtain();
				msg.what = PubnubService.MSG_HERENOW;
				msg.obj = channel;

				this.remoteMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	public void presence(PubnubChannel channel) {
		Log.i(this.getClass().getSimpleName(), channel.getName());

		if (this.service != null) {
			this.service.presence(channel);
		}
		else {
			try {
				Message msg = Message.obtain();
				msg.what = PubnubService.MSG_PRESENCE;
				msg.obj = channel;

				this.remoteMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	public void setState(PubnubChannel channel, JSONObject state) {
		if (this.service != null)
			this.service.setState(channel, state);
		else {
			try {
				Bundle data = new Bundle();
				data.putSerializable(PubnubService.BUNDLE_CHANNEL, channel);
				data.putString(PubnubService.BUNDLE_JSON, state.toString());

				Message msg = Message.obtain();
				msg.what = PubnubService.MSG_SETSTATE;
				msg.setData(data);

				this.remoteMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	public void getState(PubnubChannel channel, String uuid) {
		if (this.service != null)
			this.service.getState(channel, uuid);
		else {
			try {
				Bundle data = new Bundle();
				data.putSerializable(PubnubService.BUNDLE_CHANNEL, channel);
				data.putString(PubnubService.BUNDLE_UUID, uuid);

				Message msg = Message.obtain();
				msg.what = PubnubService.MSG_GETSTATE;
				msg.setData(data);

				this.remoteMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}


	private void logException(Exception e) {
		Log.e(this.getClass().getSimpleName(), e.toString());
	}


	//Messaging Handler
	private static class PubnubServiceMessageHandler extends WeakReferenceHandler<PubnubManager>
	{
		public PubnubServiceMessageHandler(PubnubManager manager) {
			super(manager);
		}

		@Override
		public void handleMessage(PubnubManager manager, Message msg)
		{
			Bundle data;

			switch (msg.what) {
				case PubnubService.MSG_SERVICE_MESSENGER:
					manager.remoteMessenger = (Messenger)msg.obj;
					manager.onRemoteMessengerReceived();
					break;

				case PubnubService.MSG_MESSAGE_RECEIVED:
					data = msg.getData();
					manager.onMessageReceived(
						data.getString(PubnubService.BUNDLE_CHANNEL),
						data.getString(PubnubService.BUNDLE_MESSAGE));
					break;

				case PubnubService.MSG_HISTORY_RECEIVED:
					data = msg.getData();
					manager.onHistoryReceived(
						data.getString(PubnubService.BUNDLE_CHANNEL),
						data.getString(PubnubService.BUNDLE_MESSAGE));
					break;

				case PubnubService.MSG_HERENOW_RECEIVED:
					data = msg.getData();
					manager.onHereNowReceived(
						data.getString(PubnubService.BUNDLE_CHANNEL),
						data.getString(PubnubService.BUNDLE_MESSAGE));
					break;

				case PubnubService.MSG_PRESENCE_RECEIVED:
					data = msg.getData();
					manager.onPresenceReceived(
						data.getString(PubnubService.BUNDLE_CHANNEL),
						data.getString(PubnubService.BUNDLE_MESSAGE));
					break;

				case PubnubService.MSG_GETSTATE_RECEIVED:
					data = msg.getData();
					manager.onGetStateReceived(
						data.getString(PubnubService.BUNDLE_CHANNEL),
						data.getString(PubnubService.BUNDLE_UUID),
						data.getString(PubnubService.BUNDLE_STATE));
					break;

				default:
					break;
			}
		}
	}


	//Service Connection
	private class PubnubServiceConnection implements ServiceConnection
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			PubnubBinder binder = (PubnubBinder)service;
			PubnubManager.this.service = binder.getService();
			PubnubManager.this.isBound = true;

			PubnubManager.this.onServiceConnected();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			PubnubManager.this.service = null;
			PubnubManager.this.isBound = false;

			PubnubManager.this.onServiceDisconnected();
		}
	}

	//Listener delegation
	private void onServiceStarted() {
		for (IPubnubManagerListener listener : this.pubnubManagerListeners)
			try {
				listener.onServiceStarted();
			} catch (Exception e) {
				logException(e);
			}
	}
	private void onServiceStopped() {
		for (IPubnubManagerListener listener : this.pubnubManagerListeners)
			try {
				listener.onServiceStopped();
			} catch (Exception e) {
				logException(e);
			}
	}

	private void onServiceConnected() {
		for (IPubnubManagerListener listener : this.pubnubManagerListeners)
			try {
				listener.onServiceConnected();
			} catch (Exception e) {
				logException(e);
			}
	}
	private void onServiceDisconnected() {
		for (IPubnubManagerListener listener : this.pubnubManagerListeners)
			try {
				listener.onServiceDisconnected();
			} catch (Exception e) {
				logException(e);
			}
	}

	private void onRemoteMessengerReceived() {
		for (IPubnubManagerListener listener : this.pubnubManagerListeners)
			try {
				listener.onRemoteMessengerReceived();
			} catch (Exception e) {
				logException(e);
			}
	}

	private void onMessageReceived(String channel, String message) {
		PubnubChannel pc = new PubnubChannel(channel);

		for (IPubnubMessageReceivedListener listener : this.pubnubMessageReceivedListeners)
			try {
				listener.onMessageReceived(pc, message);
			} catch (Exception e) {
				logException(e);
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
					logException(e);
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
				logException(e);
			}
	}
	private void onPresenceReceived(String channel, String message) {
		PubnubChannel pc = new PubnubChannel(channel);

		for (IPubnubPresenceReceivedListener listener : this.pubnubPresenceReceivedListeners)
			try {
				listener.onPresenceReceived(pc, message);
			} catch (Exception e) {
				logException(e);
			}
	}
	private void onGetStateReceived(String channel, String uuid, String state) {
		PubnubChannel pc = new PubnubChannel(channel);

		for (IPubnubGetStateReceivedListener listener : this.pubnubGetStateReceivedListeners)
			try {
				listener.onGetStateReceived(pc, uuid, state);
			} catch (Exception e) {
				logException(e);
			}
	}

	//Listener management
	public void addPubnubManagerListener(IPubnubManagerListener listener) {
		this.removePubnubManagerListener(listener);
		this.pubnubManagerListeners.add(listener);
	}
	public void removePubnubManagerListener(IPubnubManagerListener listener) {
		this.pubnubManagerListeners.remove(listener);
	}

	public void addPubnubMessageReceivedListener(IPubnubMessageReceivedListener listener) {
		this.removePubnubMessageReceivedListener(listener);
		this.pubnubMessageReceivedListeners.add(listener);

		if (this.service != null)
			this.service.addPubnubMessageReceivedListener(listener);
	}
	public void removePubnubMessageReceivedListener(IPubnubMessageReceivedListener listener) {
		this.pubnubMessageReceivedListeners.remove(listener);

		if (this.service != null)
			this.service.removePubnubMessageReceivedListener(listener);
	}

	public void addPubnubHistoryReceivedListener(IPubnubHistoryReceivedListener listener) {
		this.removePubnubHistoryReceivedListener(listener);
		this.pubnubHistoryReceivedListeners.add(listener);

		if (this.service != null)
			this.service.addPubnubHistoryReceivedListener(listener);
	}
	public void removePubnubHistoryReceivedListener(IPubnubHistoryReceivedListener listener) {
		this.pubnubHistoryReceivedListeners.remove(listener);

		if (this.service != null)
			this.service.removePubnubHistoryReceivedListener(listener);
	}

	public void addPubnubHereNowReceivedListener(IPubnubHereNowReceivedListener listener) {
		this.removePubnubHereNowReceivedListener(listener);
		this.pubnubHereNowReceivedListeners.add(listener);

		if (this.service != null)
			this.service.addPubnubHereNowReceivedListener(listener);
	}
	public void removePubnubHereNowReceivedListener(IPubnubHereNowReceivedListener listener) {
		this.pubnubHereNowReceivedListeners.remove(listener);

		if (this.service != null)
			this.service.removePubnubHereNowReceivedListener(listener);
	}

	public void addPubnubPresenceReceivedListener(IPubnubPresenceReceivedListener listener) {
		this.removePubnubPresenceReceivedListener(listener);
		this.pubnubPresenceReceivedListeners.add(listener);

		if (this.service != null)
			this.service.addPubnubPresenceReceivedListener(listener);
	}
	public void removePubnubPresenceReceivedListener(IPubnubPresenceReceivedListener listener) {
		this.pubnubPresenceReceivedListeners.remove(listener);

		if (this.service != null)
			this.service.removePubnubPresenceReceivedListener(listener);
	}

	public void addPubnubGetStateReceivedListener(IPubnubGetStateReceivedListener listener) {
		this.removePubnubGetStateReceivedListener(listener);
		this.pubnubGetStateReceivedListeners.add(listener);

		if (this.service != null)
			this.service.addPubnubGetStateReceivedListener(listener);
	}
	public void removePubnubGetStateReceivedListener(IPubnubGetStateReceivedListener listener) {
		this.pubnubGetStateReceivedListeners.remove(listener);

		if (this.service != null)
			this.service.removePubnubGetStateReceivedListener(listener);
	}

	//Listeners
	public interface IPubnubManagerListener {
		public void onServiceStarted();
		public void onServiceStopped();

		public void onServiceConnected();
		public void onServiceDisconnected();

		public void onRemoteMessengerReceived();
	}
	public interface ICallWhenPubnubManagerStartedListener {
		public void onStarted();
	}
}
