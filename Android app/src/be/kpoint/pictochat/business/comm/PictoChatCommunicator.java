package be.kpoint.pictochat.business.comm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.api.rest.ids.ClientId;
import be.kpoint.pictochat.api.rest.ids.CoachId;
import be.kpoint.pictochat.app.domain.Client;
import be.kpoint.pictochat.app.domain.Coach;
import be.kpoint.pictochat.app.domain.PastPictoMessage;
import be.kpoint.pictochat.app.domain.PastTextMessage;
import be.kpoint.pictochat.app.domain.PictoMessage;
import be.kpoint.pictochat.app.domain.TextMessage;
import be.kpoint.pictochat.app.domain.User;
import be.kpoint.pictochat.business.comm.enums.AppState;
import be.kpoint.pictochat.business.comm.enums.Presence;
import be.kpoint.pictochat.business.comm.interfaces.IHereNowReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.IHistoryReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.IInitializedListener;
import be.kpoint.pictochat.business.comm.interfaces.IPictoChatCommunicator;
import be.kpoint.pictochat.business.comm.interfaces.IPictoMessageReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.IPresenceReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.ITextMessageReceivedListener;
import be.kpoint.pictochat.business.comm.parsers.PastPictoMessageParser;
import be.kpoint.pictochat.business.comm.parsers.PastTextMessageParser;
import be.kpoint.pictochat.business.comm.parsers.PictoMessageParser;
import be.kpoint.pictochat.business.comm.parsers.TextMessageParser;
import be.kpoint.pictochat.comm.pubnub.PubnubChannel;
import be.kpoint.pictochat.comm.pubnub.PubnubManager;
import be.kpoint.pictochat.comm.pubnub.PubnubManager.ICallWhenPubnubManagerStartedListener;
import be.kpoint.pictochat.comm.pubnub.PubnubManager.IPubnubManagerListener;
import be.kpoint.pictochat.comm.pubnub.PubnubService;
import be.kpoint.pictochat.comm.pubnub.PubnubService.IPubnubGetStateReceivedListener;
import be.kpoint.pictochat.comm.pubnub.PubnubService.IPubnubHereNowReceivedListener;
import be.kpoint.pictochat.comm.pubnub.PubnubService.IPubnubHistoryReceivedListener;
import be.kpoint.pictochat.comm.pubnub.PubnubService.IPubnubMessageReceivedListener;
import be.kpoint.pictochat.comm.pubnub.PubnubService.IPubnubPresenceReceivedListener;
import be.kpoint.pictochat.comm.pubnub.SendResultReceiver;
import be.kpoint.pictochat.logging.Messages;
import be.kpoint.pictochat.logging.Tags;
import be.kpoint.pictochat.util.logging.FileLogItem;

public class PictoChatCommunicator implements IPictoChatCommunicator
{
	/* PictoChat uses two channels per user.
	 * One channel receives messages from the user, the other receives messages from coaches.
	 * Coaches should subscribe to the first and publish to second.
	 * Users should publish to the first and subscribe to the other.
	 */

	private static final String JSON_TYPE_TAG = "type";


	private List<IInitializedListener> initializedListeners = new ArrayList<IInitializedListener>();
	private List<ITextMessageReceivedListener> textMessageReceivedListeners = new ArrayList<ITextMessageReceivedListener>();
	private List<IPictoMessageReceivedListener> pictoMessageReceivedListeners = new ArrayList<IPictoMessageReceivedListener>();
	private List<IHereNowReceivedListener> hereNowReceivedListeners = new ArrayList<IHereNowReceivedListener>();
	private List<IPresenceReceivedListener> presenceReceivedListeners = new ArrayList<IPresenceReceivedListener>();
	private List<IHistoryReceivedListener> historyReceivedListeners = new ArrayList<IHistoryReceivedListener>();
	private Map<String, IPubnubGetStateReceivedListener> getStateListeners = new HashMap<String, IPubnubGetStateReceivedListener>();

	private PubnubManager pubnubManager;
	private Map<PubnubChannel, Client> clientChannelMap = new HashMap<PubnubChannel, Client>();
	private Map<PubnubChannel, User> remoteChannelMap = new HashMap<PubnubChannel, User>();
	private Map<String, User> uuidMap = new HashMap<String, User>();


	public PictoChatCommunicator(final Context context) {
		this.pubnubManager = new PubnubManager(context);
		this.pubnubManager.addPubnubManagerListener(new IPubnubManagerListener() {
			@Override
			public void onServiceConnected() {
				addListeners();
				onInitialized();
			}
			@Override
			public void onServiceDisconnected() {
				App.logToFile(FileLogItem.warn(PictoChatCommunicator.this, Tags.LIFE_CYCLE, Messages.SERVICE_DISCONNECTED, PubnubService.class.getSimpleName()));
				removeListeners();
			}

			@Override
			public void onServiceStarted() {
				addListeners();
			}
			@Override
			public void onServiceStopped() {
				removeListeners();
			}

			@Override
			public void onRemoteMessengerReceived() {
				onInitialized();
			}
		});
		this.pubnubManager.start();
	}


	public Boolean isStarted() {
		return this.pubnubManager.isStarted();
	}
	public void callWhenStarted(ICallWhenPubnubManagerStartedListener listener) {
		this.pubnubManager.callWhenStarted(listener);
	}


	@Override
	public void hostRoom(Client client) {
		PubnubChannel channel = PictoChatChannelFactory.buildReceiveChannelForClientRoom(client);

		this.clientChannelMap.put(channel, client);
		this.clientChannelMap.put(PictoChatChannelFactory.buildReceiveChannelForCoaches(client), client);

		this.pubnubManager.subscribe(channel);

		App.logToFile(FileLogItem.debug(this, Tags.MSG, Messages.SUBSCRIBED, "client", client.getFullName(), channel.toString()));
	}
	@Override
	public void closeRoom(Client client) {
		this.pubnubManager.unsubscribe(PictoChatChannelFactory.buildReceiveChannelForClientRoom(client));
	}

	@Override
	public void joinRoom(Client client) {
		PubnubChannel channel = PictoChatChannelFactory.buildReceiveChannelForCoaches(client);

		this.clientChannelMap.put(channel, client);
		this.clientChannelMap.put(PictoChatChannelFactory.buildReceiveChannelForClientRoom(client), client);

		this.pubnubManager.subscribe(channel);

		App.logToFile(FileLogItem.debug(this, Tags.MSG, Messages.SUBSCRIBED, "coach", client.getFullName(), channel.toString()));
	}
	@Override
	public void leaveRoom(Client client) {
		this.pubnubManager.unsubscribe(PictoChatChannelFactory.buildReceiveChannelForCoaches(client));
	}

	@Override
	public void hostRoom(User local, User remote) {
		PubnubChannel channel = PictoChatChannelFactory.buildPrivateReceiveChannel(local, remote);

		this.remoteChannelMap.put(channel, remote);
		this.remoteChannelMap.put(PictoChatChannelFactory.buildPrivateTransmitChannel(local, remote), local);

		this.pubnubManager.subscribe(channel);

		App.logToFile(FileLogItem.debug(this, Tags.MSG, Messages.SUBSCRIBED, "user", remote.getFullName(), channel.toString()));
	}
	@Override
	public void closeRoom(User local, User remote) {
		this.pubnubManager.unsubscribe(PictoChatChannelFactory.buildPrivateReceiveChannel(local, remote));
	}

	@Override
	public void historyReceived(Client client, int amount) {
		this.pubnubManager.history(PictoChatChannelFactory.buildReceiveChannelForClientRoom(client), amount);
	}
	@Override
	public void historySent(Client client, int amount) {
		this.pubnubManager.history(PictoChatChannelFactory.buildTransmitChannelForClientRoom(client), amount);
	}
	@Override
	public void historyReceived(User local, User remote, int amount) {
		this.pubnubManager.history(PictoChatChannelFactory.buildPrivateReceiveChannel(local, remote), amount);
	}
	@Override
	public void historySent(User local, User remote, int amount) {
		this.pubnubManager.history(PictoChatChannelFactory.buildPrivateTransmitChannel(local, remote), amount);
	}

	@Override
	public void historyReceived(Client client, Date start) {
		this.pubnubManager.history(PictoChatChannelFactory.buildReceiveChannelForClientRoom(client), start);
	}
	@Override
	public void historySent(Client client, Date start) {
		this.pubnubManager.history(PictoChatChannelFactory.buildTransmitChannelForClientRoom(client), start);
	}
	@Override
	public void historyReceived(User local, User remote, Date start) {
		this.pubnubManager.history(PictoChatChannelFactory.buildPrivateReceiveChannel(local, remote), start);
	}
	@Override
	public void historySent(User local, User remote, Date start) {
		this.pubnubManager.history(PictoChatChannelFactory.buildPrivateTransmitChannel(local, remote), start);
	}

	@Override
	public void clientsHereNow(Client client) {
		this.pubnubManager.hereNow(PictoChatChannelFactory.buildReceiveChannelForClientRoom(client));
	}
	@Override
	public void usersHereNow(User local, User remote) {
		this.pubnubManager.hereNow(PictoChatChannelFactory.buildPrivateTransmitChannel(local, remote));
	}
	@Override
	public void coachesHereNow(Client client) {
		this.pubnubManager.hereNow(PictoChatChannelFactory.buildReceiveChannelForCoaches(client));
	}
	@Override
	public void clientsPresence(Client client) {
		this.pubnubManager.presence(PictoChatChannelFactory.buildReceiveChannelForClientRoom(client));
	}
	@Override
	public void usersPresence(User local, User remote) {
		this.pubnubManager.presence(PictoChatChannelFactory.buildPrivateTransmitChannel(local, remote));
	}
	@Override
	public void coachesPresence(Client client) {
		this.pubnubManager.presence(PictoChatChannelFactory.buildReceiveChannelForCoaches(client));
	}

	public void setClientState(Client client, AppState appState) {
		setClientState(client, appState, null);
	}
	public void setClientState(Client client, AppState appState, Date lastRead) {
		try {
			JSONObject state = new JSONObject();
			state.put("client", client.getId().getNumber());
			state.put("firstName", client.getFirstName());
			state.put("lastName", client.getLastName());
			state.put("appState", appState);

			if (lastRead != null)
				state.put("lastRead", lastRead.getTime());

			this.pubnubManager.setState(PictoChatChannelFactory.buildReceiveChannelForClientRoom(client), state);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setUserState(Client local, User remote, AppState appState) {
		setUserState(local, remote, appState, null);
	}
	public void setUserState(Client local, User remote, AppState appState, Date lastRead) {
		try {
			JSONObject state = new JSONObject();
			state.put("client", local.getId().getNumber());
			state.put("firstName", local.getFirstName());
			state.put("lastName", local.getLastName());
			state.put("appState", appState);

			if (lastRead != null)
				state.put("lastRead", lastRead.getTime());

			this.pubnubManager.setState(PictoChatChannelFactory.buildPrivateReceiveChannel(local, remote), state);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setUserState(Coach local, User remote, AppState appState) {
		try {
			JSONObject state = new JSONObject();
			state.put("coach", local.getId().getNumber());
			state.put("firstName", local.getFirstName());
			state.put("lastName", local.getLastName());
			state.put("appState", appState);

			this.pubnubManager.setState(PictoChatChannelFactory.buildPrivateReceiveChannel(local, remote), state);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setCoachState(Client client, Coach coach, AppState appState) {
		try {
			JSONObject json = new JSONObject();
			json.put("coach", coach.getId().getNumber());
			json.put("firstName", coach.getFirstName());
			json.put("lastName", coach.getLastName());
			json.put("appState", appState);

			this.pubnubManager.setState(PictoChatChannelFactory.buildReceiveChannelForCoaches(client), json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void getState(Client client, String uuid) {
		this.getState(PictoChatChannelFactory.buildReceiveChannelForClientRoom(client), uuid);
	}
	public void getState(PubnubChannel channel, String uuid) {
		this.pubnubManager.getState(channel, uuid);
	}


	public void sendTextMessageToClient(Client client, Coach coach, String text, SendResultReceiver receiver) {
		try {
			this.sendMessageToClientRoom(client, TextMessageParser.compose(coach.getFullName(), text), receiver);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendTextMessageToCoaches(Client client, String text, SendResultReceiver receiver) {
		try {
			this.sendMessageToCoaches(client, TextMessageParser.compose(client.getFullName(), text), receiver);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendPictoMessageToClientRoom(Client client, Coach coach, PictoMessage message, PictoSendResultReceiver receiver) {
		try {
			App.logToFile(FileLogItem.debug(this, Tags.MSG, Messages.MESSAGE_SENT, "to client", client.getFullName(), coach.getFullName(), message.getText()));

			message.setSenderImageUrl(coach.getImageUrl());
			this.sendMessageToClientRoom(client, PictoMessageParser.compose(coach.getFullName(), message), receiver);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendPictoMessageToUser(User local, User remote, PictoMessage message, PictoSendResultReceiver receiver) {
		try {
			App.logToFile(FileLogItem.debug(this, Tags.MSG, Messages.MESSAGE_SENT, "to user", remote.getFullName(), message.getText()));

			message.setSenderImageUrl(local.getImageUrl());
			this.sendMessageToUser(local, remote, PictoMessageParser.compose(local.getFullName(), message), receiver);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendPictoMessageToCoaches(Client client, PictoMessage message, PictoSendResultReceiver receiver) {
		try {
			App.logToFile(FileLogItem.debug(this, Tags.MSG, Messages.MESSAGE_SENT, "to coaches", client.getFullName(), message.getText()));

			message.setSenderImageUrl(client.getImageUrl());
			this.sendMessageToCoaches(client, PictoMessageParser.compose(client.getFullName(), message), receiver);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendMessageToClientRoom(Client client, JSONObject message, SendResultReceiver receiver) {
		this.pubnubManager.send(PictoChatChannelFactory.buildReceiveChannelForClientRoom(client), message, receiver);
	}
	private void sendMessageToUser(User local, User remote, JSONObject message, SendResultReceiver receiver) {
		this.pubnubManager.send(PictoChatChannelFactory.buildPrivateTransmitChannel(local, remote), message, receiver);
	}
	private void sendMessageToCoaches(Client client, JSONObject message, SendResultReceiver receiver) {
		this.pubnubManager.send(PictoChatChannelFactory.buildReceiveChannelForCoaches(client), message, receiver);
	}


	private void processJson(JSONObject jsonObject, PubnubChannel channel) throws JSONException {
		if (!jsonObject.isNull(JSON_TYPE_TAG)) {
			String type = jsonObject.getString(JSON_TYPE_TAG);
			if (TextMessageParser.TYPE.equalsIgnoreCase(type)) {
				TextMessage textMessage = TextMessageParser.parse(jsonObject);

				//Ignore messages that were sent by this device
				if (textMessage.getUuid().equals(App.getUUId()))
					return;

				PictoChatCommunicator.this.onTextMessageReceived(textMessage);
			}
			else if (PictoMessageParser.TYPE.equalsIgnoreCase(type)) {
				PictoMessage pictoMessage = PictoMessageParser.parse(jsonObject);

				//Ignore messages that were sent by this device
				if (pictoMessage.getUuid().equals(App.getUUId()))
					return;

				User remote = this.remoteChannelMap.get(channel);
				if (remote != null)
					PictoChatCommunicator.this.onPictoMessageReceived(remote, pictoMessage);
				else {
					User user = this.uuidMap.get(pictoMessage.getUuid());
					Client client = this.clientChannelMap.get(channel);
					if (user instanceof Coach)
						PictoChatCommunicator.this.onPictoMessageReceived((Coach)user, client, pictoMessage);
					else if (client != null)
						PictoChatCommunicator.this.onPictoMessageReceived(client, pictoMessage);
				}
			}
		}
		else
			Log.e(this.getClass().getSimpleName(), "Pubnub messages must include a type");
	}
	private TextMessage processJsonHistory(JSONObject jsonObject, long timeToken) throws JSONException {
		if (!jsonObject.isNull(JSON_TYPE_TAG)) {
			String type = jsonObject.getString(JSON_TYPE_TAG);
			if (PastTextMessageParser.TYPE.equalsIgnoreCase(type)) {
				PastTextMessage textMessage = PastTextMessageParser.parse(jsonObject, timeToken);
				return textMessage;
			}
			else if (PastPictoMessageParser.TYPE.equalsIgnoreCase(type)) {
				PastPictoMessage pictoMessage = PastPictoMessageParser.parse(jsonObject, timeToken);
				return pictoMessage;
			}
		}
		else
			Log.e(this.getClass().getSimpleName(), "Pubnub messages must include a type");

		return null;
	}



	private void addListeners() {
		App.logToFile(FileLogItem.debug(this, Tags.LISTENER, "AddListeners"));

		this.pubnubManager.addPubnubMessageReceivedListener(this.pubnubMessageReceivedListener);
		this.pubnubManager.addPubnubHistoryReceivedListener(this.pubnubHistoryReceivedListener);
		this.pubnubManager.addPubnubHereNowReceivedListener(this.pubnubHereNowReceivedListener);
		this.pubnubManager.addPubnubPresenceReceivedListener(this.pubnubPresenceReceivedListener);
		this.pubnubManager.addPubnubGetStateReceivedListener(this.pubnubGetStateReceivedListener);
	}
	private void removeListeners() {
		App.logToFile(FileLogItem.debug(this, Tags.LISTENER, "RemoveListeners"));

		this.pubnubManager.removePubnubMessageReceivedListener(this.pubnubMessageReceivedListener);
		this.pubnubManager.removePubnubHistoryReceivedListener(this.pubnubHistoryReceivedListener);
		this.pubnubManager.removePubnubHereNowReceivedListener(this.pubnubHereNowReceivedListener);
		this.pubnubManager.removePubnubPresenceReceivedListener(this.pubnubPresenceReceivedListener);
		this.pubnubManager.removePubnubGetStateReceivedListener(this.pubnubGetStateReceivedListener);
	}


	//Local listeners
	private IPubnubMessageReceivedListener pubnubMessageReceivedListener = new IPubnubMessageReceivedListener()
	{
		@Override
		public void onMessageReceived(PubnubChannel channel, String message) {
			App.logToFile(FileLogItem.debug(PictoChatCommunicator.this, Tags.MSG, Messages.MESSAGE_RECEIVED, channel.toString()));

			try {
				JSONObject jsonObject = new JSONObject(message);
				processJson(jsonObject, channel);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};
	private IPubnubHistoryReceivedListener pubnubHistoryReceivedListener = new IPubnubHistoryReceivedListener() {
		@Override
		public void onHistoryReceived(PubnubChannel channel, String history, int start, int end) {
			try {
				List<TextMessage> messages = new ArrayList<TextMessage>();

				JSONArray jsonHistory = new JSONArray(history);
				for (int i = 0; i < jsonHistory.length(); i++) {
					JSONObject jsonItem = jsonHistory.getJSONObject(i);
					JSONObject jsonMessage = jsonItem.getJSONObject("message");
					long timeToken = jsonItem.getLong("timetoken");

					TextMessage message = processJsonHistory(jsonMessage, timeToken);
					if (message != null)
						messages.add(message);
				}

				PictoChatCommunicator.this.onHistoryReceived(channel, messages);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	private IPubnubHereNowReceivedListener pubnubHereNowReceivedListener = new IPubnubHereNowReceivedListener() {
		@Override
		public void onHereNowReceived(PubnubChannel channel, String hereNow) {
			try {
				JSONObject jsonHereNow = new JSONObject(hereNow);

				Integer occupancy = 0;

				if (!jsonHereNow.isNull("occupancy")) {
					occupancy = jsonHereNow.getInt("occupancy");
				}
				if (!jsonHereNow.isNull("uuids")) {
					JSONArray jsonUuids = jsonHereNow.getJSONArray("uuids");
					List<Participant> participants = new ArrayList<Participant>();
					for (int i = 0; i < jsonUuids.length(); i++) {
						JSONObject jsonUuid = jsonUuids.getJSONObject(i);
						String uuid = jsonUuid.getString("uuid");

						if (!jsonUuid.isNull("state")) {
							JSONObject jsonState = jsonUuid.getJSONObject("state");
							AppState appState = getAppStateFromState(jsonState);
							User user = buildUserFromState(jsonState);
							if (user != null) {
								user.setUuid(uuid);
								participants.add(new Participant(user, appState));
							}
						}
						else {
							Log.d("", "");
						}
					}

					PictoChatCommunicator.this.onHereNowReceived(channel, participants, occupancy);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	private IPubnubPresenceReceivedListener pubnubPresenceReceivedListener = new IPubnubPresenceReceivedListener() {
		@Override
		public void onPresenceReceived(PubnubChannel channel, String message) {
			try {
				PubnubChannel c = new PubnubChannel(channel.toString().replace("-pnpres", ""));

				JSONObject jsonPresence = new JSONObject(message);

				String uuid = jsonPresence.getString("uuid");

				Presence p;
				try {
					p = Presence.valueOf(jsonPresence.getString("action").toUpperCase().replace("-", "_"));
				} catch (IllegalArgumentException e) {
					p = Presence.ERROR;
				}
				final Presence presence = p;

				User user = null;
				if (!jsonPresence.isNull("data")) {
					JSONObject jsonData = jsonPresence.getJSONObject("data");

					Date lastRead = getLastReadFromState(jsonData);
					AppState appState = getAppStateFromState(jsonData);
					user = buildUserFromState(jsonData);
					user.setUuid(uuid);

					PictoChatCommunicator.this.onPresenceReceived(c, user, presence, appState, lastRead);
				}
				else {
					PictoChatCommunicator.this.getStateListeners.put(uuid, new IPubnubGetStateReceivedListener() {
						@Override
						public void onGetStateReceived(PubnubChannel channel, String uuid, String state) {
							try {
								JSONObject jsonData = new JSONObject(state);

								Date lastRead = getLastReadFromState(jsonData);
								AppState appState = getAppStateFromState(jsonData);
								User user = buildUserFromState(jsonData);
								user.setUuid(uuid);

								PictoChatCommunicator.this.onPresenceReceived(channel, user, presence, appState, lastRead);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							PictoChatCommunicator.this.getStateListeners.remove(uuid);
						}
					});
					getState(c, uuid);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	private IPubnubGetStateReceivedListener pubnubGetStateReceivedListener = new IPubnubGetStateReceivedListener() {
		@Override
		public void onGetStateReceived(PubnubChannel channel, String uuid, String state) {
			if (PictoChatCommunicator.this.getStateListeners.containsKey(uuid))
				PictoChatCommunicator.this.getStateListeners.get(uuid).onGetStateReceived(channel, uuid, state);
		}
	};

	//Listener delegation
	private void onInitialized() {
		for (IInitializedListener listener : this.initializedListeners)
  			try {
  				listener.onInitialized();
  			} catch (Exception e) {
  				Log.e(this.getClass().getSimpleName(), e.toString());
  			}
	}

	private void onTextMessageReceived(TextMessage message) {
		for (ITextMessageReceivedListener listener : this.textMessageReceivedListeners)
  			try {
  				listener.onTextMessageReceived(message);
  			} catch (Exception e) {
  				Log.e(this.getClass().getSimpleName(), e.toString());
  			}
	}
	private void onPictoMessageReceived(Client client, PictoMessage message) {
		App.logToFile(FileLogItem.debug(this, Tags.MSG, Messages.MESSAGE_RECEIVED, "client", client.getFullName(), message.getSenderName(), message.getText(), this.pictoMessageReceivedListeners.toString()));

		for (IPictoMessageReceivedListener listener : this.pictoMessageReceivedListeners)
  			try {
  				listener.onPictoMessageReceived(client, message);
  			} catch (Exception e) {
  				Log.e(this.getClass().getSimpleName(), e.toString());
  			}
	}
	private void onPictoMessageReceived(Coach coach, Client client, PictoMessage message) {
		App.logToFile(FileLogItem.debug(this, Tags.MSG, Messages.MESSAGE_RECEIVED, "coach", coach.getFullName(), message.getSenderName(), message.getText(), this.pictoMessageReceivedListeners.toString()));

		for (IPictoMessageReceivedListener listener : this.pictoMessageReceivedListeners)
  			try {
  				listener.onPictoMessageReceived(coach, client, message);
  			} catch (Exception e) {
  				Log.e(this.getClass().getSimpleName(), e.toString());
  			}
	}
	private void onPictoMessageReceived(User remote, PictoMessage message) {
		App.logToFile(FileLogItem.debug(this, Tags.MSG, Messages.MESSAGE_RECEIVED, "user", remote.getFullName(), message.getSenderName(), message.getText(), this.pictoMessageReceivedListeners.toString()));

		for (IPictoMessageReceivedListener listener : this.pictoMessageReceivedListeners)
  			try {
  				listener.onPictoMessageReceived(remote, message);
  			} catch (Exception e) {
  				Log.e(this.getClass().getSimpleName(), e.toString());
  			}
	}
	private void onHereNowReceived(PubnubChannel channel, List<Participant> participants, int occupancy) {
		for (Participant participant : participants)
			addUserToMapIfHasUuid(participant.getUser());

		final Client client = this.clientChannelMap.get(channel);
		final User remote = this.remoteChannelMap.get(channel);
		final User host = (remote != null ? remote : client);

		for (IHereNowReceivedListener listener : this.hereNowReceivedListeners)
  			try {
  				listener.onHereNowReceived(channel, remote != null, host, participants, occupancy);
  			} catch (Exception e) {
  				Log.e(this.getClass().getSimpleName(), e.toString());
  			}
	}
	private void onPresenceReceived(PubnubChannel channel, User user, Presence presence, AppState appState, Date lastRead) {
		addUserToMapIfHasUuid(user);

		final Client client = PictoChatCommunicator.this.clientChannelMap.get(channel);
		final User remote = PictoChatCommunicator.this.remoteChannelMap.get(channel);
		final User host = (remote != null ? remote : client);

		for (IPresenceReceivedListener listener : this.presenceReceivedListeners)
  			try {
  				listener.onPresenceReceived(channel, remote != null, host, user, presence, appState, lastRead);
  			} catch (Exception e) {
  				Log.e(this.getClass().getSimpleName(), e.toString());
  			}
	}
	private void onHistoryReceived(PubnubChannel channel, List<TextMessage> messages) {
		final Client client = this.clientChannelMap.get(channel);
		final User remote = this.remoteChannelMap.get(channel);
		final User host = (remote != null ? remote : client);
		final User other = (remote == null ? remote : client);

		for (IHistoryReceivedListener listener : this.historyReceivedListeners)
  			try {
  				listener.onHistoryReceived(channel, remote != null, host, remote, messages);
  			} catch (Exception e) {
  				Log.e(this.getClass().getSimpleName(), e.toString());
  			}
	}

	private void addUserToMapIfHasUuid(User user) {
		if (user.hasUuid()) {
			this.uuidMap.put(user.getUuid(), user);
			App.logToFile(FileLogItem.debug(PictoChatCommunicator.this, Tags.PCC_MAP, "Added " + user.getClass().getSimpleName(), user.getFullName(), user.getUuid()));
		}
		else
			App.logToFile(FileLogItem.debug(PictoChatCommunicator.this, Tags.PCC_MAP, user.getClass().getSimpleName() + " did not have a uuid", user.getFullName()));
	}

	//Listener management
	public void addInitializedListener(IInitializedListener listener) {
		this.removeInitializedListener(listener);
		this.initializedListeners.add(listener);
	}
	public void removeInitializedListener(IInitializedListener listener) {
		this.initializedListeners.remove(listener);
	}

	@Override
	public void addTextMessageReceivedListener(ITextMessageReceivedListener listener) {
		this.removeTextMessageReceivedListener(listener);
		this.textMessageReceivedListeners.add(listener);
	}
	@Override
	public void removeTextMessageReceivedListener(ITextMessageReceivedListener listener) {
		this.textMessageReceivedListeners.remove(listener);
	}

	@Override
	public void addPictoMessageReceivedListener(IPictoMessageReceivedListener listener) {
		this.removePictoMessageReceivedListener(listener);
		this.pictoMessageReceivedListeners.add(listener);
	}
	@Override
	public void removePictoMessageReceivedListener(IPictoMessageReceivedListener listener) {
		this.pictoMessageReceivedListeners.remove(listener);
	}

	@Override
	public void addHereNowReceivedListener(IHereNowReceivedListener listener) {
		this.removeHereNowReceivedListener(listener);
		this.hereNowReceivedListeners.add(listener);
	}
	@Override
	public void removeHereNowReceivedListener(IHereNowReceivedListener listener) {
		this.hereNowReceivedListeners.remove(listener);
	}

	@Override
	public void addPresenceReceivedListener(IPresenceReceivedListener listener) {
		this.removePresenceReceivedListener(listener);
		this.presenceReceivedListeners.add(listener);
	}
	@Override
	public void removePresenceReceivedListener(IPresenceReceivedListener listener) {
		this.presenceReceivedListeners.remove(listener);
	}

	@Override
	public void addHistoryReceivedListener(IHistoryReceivedListener listener) {
		this.removeHistoryReceivedListener(listener);
		this.historyReceivedListeners.add(listener);
	}
	@Override
	public void removeHistoryReceivedListener(IHistoryReceivedListener listener) {
		this.historyReceivedListeners.remove(listener);
	}


	private Date getLastReadFromState(JSONObject jsonState) {
		try {
			if (!jsonState.isNull("lastRead"))
				return new Date(jsonState.getLong("lastRead"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}
	private AppState getAppStateFromState(JSONObject jsonState) {
		try {
			return AppState.valueOf(jsonState.getString("appState"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return AppState.None;
	}
	private User buildUserFromState(JSONObject jsonState) {
		User user = null;
		try {
			if (!jsonState.isNull("client")) {
				user = new Client(
					new ClientId(jsonState.getLong("client")),
					jsonState.getString("firstName"),
					jsonState.getString("lastName"),
					null);
			}
			else if (!jsonState.isNull("coach"))
				user = new Coach(
					new CoachId(jsonState.getLong("coach")),
					jsonState.getString("firstName"),
					jsonState.getString("lastName"),
					null);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return user;
	}
}
