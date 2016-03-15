package be.kpoint.pictochat.business.comm;

import java.util.ArrayList;
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
import be.kpoint.pictochat.app.domain.Friend;
import be.kpoint.pictochat.app.domain.PastPictoMessage;
import be.kpoint.pictochat.app.domain.PastTextMessage;
import be.kpoint.pictochat.app.domain.PictoMessage;
import be.kpoint.pictochat.app.domain.TextMessage;
import be.kpoint.pictochat.app.domain.User;
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
	private Map<PubnubChannel, Friend> friendChannelMap = new HashMap<PubnubChannel, Friend>();
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


	@Override
	public void hostRoom(Client client) {
		PubnubChannel channel = PictoChatChannelFactory.buildReceiveChannelForClient(client);

		this.clientChannelMap.put(channel, client);
		this.clientChannelMap.put(PictoChatChannelFactory.buildReceiveChannelForCoach(client), client);

		this.pubnubManager.subscribe(channel);
	}
	@Override
	public void closeRoom(Client client) {
		this.pubnubManager.unsubscribe(PictoChatChannelFactory.buildReceiveChannelForClient(client));
	}

	@Override
	public void joinRoom(Client client) {
		PubnubChannel channel = PictoChatChannelFactory.buildReceiveChannelForCoach(client);

		this.clientChannelMap.put(channel, client);
		this.clientChannelMap.put(PictoChatChannelFactory.buildReceiveChannelForClient(client), client);

		this.pubnubManager.subscribe(channel);
	}
	@Override
	public void leaveRoom(Client client) {
		this.pubnubManager.unsubscribe(PictoChatChannelFactory.buildReceiveChannelForCoach(client));
	}

	@Override
	public void hostRoom(Client client, Friend friend) {
		PubnubChannel channel = PictoChatChannelFactory.buildReceiveChannelForClientAndFriend(client, friend);

		this.friendChannelMap.put(channel, friend);
		this.friendChannelMap.put(PictoChatChannelFactory.buildTransmitChannelForClientAndFriend(client, friend), friend);

		this.pubnubManager.subscribe(channel);
	}
	@Override
	public void closeRoom(Client client, Friend friend) {
		this.pubnubManager.unsubscribe(PictoChatChannelFactory.buildReceiveChannelForClientAndFriend(client, friend));
	}

	@Override
	public void historyReceived(Client client, int amount) {
		this.pubnubManager.history(PictoChatChannelFactory.buildReceiveChannelForClient(client), amount);
	}
	@Override
	public void historySent(Client client, int amount) {
		this.pubnubManager.history(PictoChatChannelFactory.buildTransmitChannelForClient(client), amount);
	}
	@Override
	public void historyReceived(Client client, Friend friend, int amount) {
		this.pubnubManager.history(PictoChatChannelFactory.buildReceiveChannelForClientAndFriend(client, friend), amount);
	}
	@Override
	public void historySent(Client client, Friend friend, int amount) {
		this.pubnubManager.history(PictoChatChannelFactory.buildTransmitChannelForClientAndFriend(client, friend), amount);
	}

	@Override
	public void clientsHereNow(Client client) {
		this.pubnubManager.hereNow(PictoChatChannelFactory.buildReceiveChannelForClient(client));
	}
	@Override
	public void friendsHereNow(Client client, Friend friend) {
		this.pubnubManager.hereNow(PictoChatChannelFactory.buildTransmitChannelForClientAndFriend(client, friend));
	}
	@Override
	public void coachesHereNow(Client client) {
		this.pubnubManager.hereNow(PictoChatChannelFactory.buildReceiveChannelForCoach(client));
	}
	@Override
	public void clientsPresence(Client client) {
		this.pubnubManager.presence(PictoChatChannelFactory.buildReceiveChannelForClient(client));
	}
	@Override
	public void friendsPresence(Client client, Friend friend) {
		this.pubnubManager.presence(PictoChatChannelFactory.buildTransmitChannelForClientAndFriend(client, friend));
	}
	@Override
	public void coachesPresence(Client client) {
		this.pubnubManager.presence(PictoChatChannelFactory.buildReceiveChannelForCoach(client));
	}

	public void setClientState(Client client, JSONObject state) {
		this.pubnubManager.setState(PictoChatChannelFactory.buildReceiveChannelForClient(client), state);
	}
	public void setClientState(Client client, Friend friend, JSONObject state) {
		this.pubnubManager.setState(PictoChatChannelFactory.buildReceiveChannelForClientAndFriend(client, friend), state);
	}
	public void setCoachState(Client client, JSONObject state) {
		this.pubnubManager.setState(PictoChatChannelFactory.buildReceiveChannelForCoach(client), state);
	}
	public void getState(Client client, String uuid) {
		this.getState(PictoChatChannelFactory.buildReceiveChannelForClient(client), uuid);
	}
	public void getState(PubnubChannel channel, String uuid) {
		this.pubnubManager.getState(channel, uuid);
	}


	public void sendTextMessageToClient(Client client, Coach coach, String text, SendResultReceiver receiver) {
		try {
			this.sendMessageToClient(client, TextMessageParser.compose(coach.getFullName(), text), receiver);
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

	public void sendPictoMessageToClient(Client client, Coach coach, PictoMessage message, PictoSendResultReceiver receiver) {
		try {
			message.setSenderImageUrl(coach.getImageUrl());
			//message.setSenderImageId(senderImageId);
			this.sendMessageToClient(client, PictoMessageParser.compose(coach.getFullName(), message), receiver);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendPictoMessageToFriend(Client client, Friend friend, PictoMessage message, PictoSendResultReceiver receiver) {
		try {
			//message.setSenderImageUrl(coach.getImageUrl());
			//message.setSenderImageId(senderImageId);
			this.sendMessageToFriend(client, friend, PictoMessageParser.compose(client.getFullName(), message), receiver);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendPictoMessageToCoaches(Client client, PictoMessage message, PictoSendResultReceiver receiver) {
		try {
			//message.setSenderImageUrl(coach.getImageUrl());
			//message.setSenderImageId(senderImageId);
			this.sendMessageToCoaches(client, PictoMessageParser.compose(client.getFullName(), message), receiver);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendMessageToClient(Client client, JSONObject message, SendResultReceiver receiver) {
		this.pubnubManager.send(PictoChatChannelFactory.buildReceiveChannelForClient(client), message, receiver);
	}
	private void sendMessageToFriend(Client client, Friend friend, JSONObject message, SendResultReceiver receiver) {
		this.pubnubManager.send(PictoChatChannelFactory.buildTransmitChannelForClientAndFriend(client, friend), message, receiver);
	}
	private void sendMessageToCoaches(Client client, JSONObject message, SendResultReceiver receiver) {
		this.pubnubManager.send(PictoChatChannelFactory.buildReceiveChannelForCoach(client), message, receiver);
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

				User user = this.uuidMap.get(pictoMessage.getUuid());

				if (user instanceof Coach)
					PictoChatCommunicator.this.onPictoMessageReceived((Coach)user, pictoMessage);
				else {
					Client client = this.clientChannelMap.get(channel);
					Friend friend = this.friendChannelMap.get(channel);

					if (friend != null)
						PictoChatCommunicator.this.onPictoMessageReceived(friend, pictoMessage);
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
		this.pubnubManager.addPubnubMessageReceivedListener(this.pubnubMessageReceivedListener);
		this.pubnubManager.addPubnubHistoryReceivedListener(this.pubnubHistoryReceivedListener);
		this.pubnubManager.addPubnubHereNowReceivedListener(this.pubnubHereNowReceivedListener);
		this.pubnubManager.addPubnubPresenceReceivedListener(this.pubnubPresenceReceivedListener);
		this.pubnubManager.addPubnubGetStateReceivedListener(this.pubnubGetStateReceivedListener);
	}
	private void removeListeners() {
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
					List<User> users = new ArrayList<User>();
					for (int i = 0; i < jsonUuids.length(); i++) {
						JSONObject jsonUuid = jsonUuids.getJSONObject(i);
						String uuid = jsonUuid.getString("uuid");

						if (!jsonUuid.isNull("state")) {
							JSONObject jsonState = jsonUuid.getJSONObject("state");
							User user = buildUserFromState(jsonState);
							user.setUuid(uuid);
							if (user != null)
								users.add(user);
						}
						else {
							Log.d("", "");
						}
					}

					PictoChatCommunicator.this.onHereNowReceived(channel, occupancy, users);
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

				final Client client = PictoChatCommunicator.this.clientChannelMap.get(c);
				final Friend friend = PictoChatCommunicator.this.friendChannelMap.get(c);
				final User host = (friend != null ? friend : client);

				User user = null;
				if (!jsonPresence.isNull("data")) {
					user = buildUserFromState(jsonPresence.getJSONObject("data"));
					user.setUuid(uuid);

					PictoChatCommunicator.this.onPresenceReceived(host, user, presence);
				}
				else {
					PictoChatCommunicator.this.getStateListeners.put(uuid, new IPubnubGetStateReceivedListener() {
						@Override
						public void onGetStateReceived(PubnubChannel channel, String uuid, String state) {
							try {
								User user = buildUserFromState(new JSONObject(state));
								user.setUuid(uuid);

								PictoChatCommunicator.this.onPresenceReceived(host, user, presence);
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
		for (IPictoMessageReceivedListener listener : this.pictoMessageReceivedListeners)
  			try {
  				listener.onPictoMessageReceived(client, message);
  			} catch (Exception e) {
  				Log.e(this.getClass().getSimpleName(), e.toString());
  			}
	}
	private void onPictoMessageReceived(Coach coach, PictoMessage message) {
		for (IPictoMessageReceivedListener listener : this.pictoMessageReceivedListeners)
  			try {
  				listener.onPictoMessageReceived(coach, message);
  			} catch (Exception e) {
  				Log.e(this.getClass().getSimpleName(), e.toString());
  			}
	}
	private void onPictoMessageReceived(Friend friend, PictoMessage message) {
		for (IPictoMessageReceivedListener listener : this.pictoMessageReceivedListeners)
  			try {
  				listener.onPictoMessageReceived(friend, message);
  			} catch (Exception e) {
  				Log.e(this.getClass().getSimpleName(), e.toString());
  			}
	}
	private void onHereNowReceived(PubnubChannel channel, int occupancy, List<User> users) {
		for (User user : users)
			addUserToMapIfHasUuid(user);

		final Client client = this.clientChannelMap.get(channel);
		final Friend friend = this.friendChannelMap.get(channel);
		final User host = (friend != null ? friend : client);

		for (IHereNowReceivedListener listener : this.hereNowReceivedListeners)
  			try {
  				listener.onHereNowReceived(channel, occupancy, host, users);
  			} catch (Exception e) {
  				Log.e(this.getClass().getSimpleName(), e.toString());
  			}
	}
	private void onPresenceReceived(User host, User user, Presence presence) {
		addUserToMapIfHasUuid(user);

		for (IPresenceReceivedListener listener : this.presenceReceivedListeners)
  			try {
  				listener.onPresenceReceived(host, user, presence);
  			} catch (Exception e) {
  				Log.e(this.getClass().getSimpleName(), e.toString());
  			}
	}
	private void onHistoryReceived(PubnubChannel channel, List<TextMessage> messages) {
		for (IHistoryReceivedListener listener : this.historyReceivedListeners)
  			try {
  				listener.onHistoryReceived(channel, messages);
  			} catch (Exception e) {
  				Log.e(this.getClass().getSimpleName(), e.toString());
  			}
	}

	private void addUserToMapIfHasUuid(User user) {
		if (user.hasUuid()) {
			this.uuidMap.put(user.getUuid(), user);
			Log.d("pcc_map", "Added " + user.getClass().getSimpleName() + " " + user.getFullName());
		}
		else
			Log.d("pcc_map", user.getClass().getSimpleName() + " " + user.getFullName() + " did not have a uuid.");
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




	private User buildUserFromState(JSONObject jsonState) {
		User user = null;
		try {
			if (!jsonState.isNull("client")) {
				user = new Client(
					new ClientId(jsonState.getLong("client")),
					jsonState.getString("firstName"),
					jsonState.getString("lastName"));
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
