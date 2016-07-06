package be.kpoint.pictochat.app.activities.coach;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.api.rest.coach.CoachManager;
import be.kpoint.pictochat.api.rest.coach.LoginResultReceiver;
import be.kpoint.pictochat.api.rest.ids.ClientId;
import be.kpoint.pictochat.app.R;
import be.kpoint.pictochat.app.domain.Client;
import be.kpoint.pictochat.app.domain.Coach;
import be.kpoint.pictochat.app.domain.PictoMessage;
import be.kpoint.pictochat.app.domain.TextMessage;
import be.kpoint.pictochat.app.domain.User;
import be.kpoint.pictochat.business.comm.Participant;
import be.kpoint.pictochat.business.comm.enums.AppState;
import be.kpoint.pictochat.business.comm.enums.Presence;
import be.kpoint.pictochat.business.comm.interfaces.IHereNowReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.IHistoryReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.IInitializedListener;
import be.kpoint.pictochat.business.comm.interfaces.IPictoMessageReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.IPresenceReceivedListener;
import be.kpoint.pictochat.comm.pubnub.PubnubChannel;
import be.kpoint.pictochat.logging.Messages;
import be.kpoint.pictochat.logging.Tags;
import be.kpoint.pictochat.util.List.IConverts;
import be.kpoint.pictochat.util.logging.FileLogItem;

public class ClientListActivity extends Activity
{
	private static final String EXTRA_COACH = "coach";


	//Interface object
	private ListView lstClients;
	private ClientsArrayAdapter clientsAdapter;
	private List<ClientStatus> clients = new ArrayList<ClientStatus>();

	private boolean userNavigated = false;
	private int totalUnread = 0;

	private Coach bundleCoach;
	private Coach apiCoach;

	private CoachManager coachManager;


	//Life cycle
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_CREATE));

		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_list);

		this.lstClients = (ListView)findViewById(R.id.coach_lstClients);

		this.clientsAdapter = new ClientsArrayAdapter(this, this.clients);
		this.lstClients.setAdapter(ClientListActivity.this.clientsAdapter);

		this.lstClients.setOnItemClickListener(this.itemClickListener);

		Bundle bundle = getIntent().getExtras();
	    if (bundle != null)
	    {
	    	this.bundleCoach = (Coach)bundle.get(EXTRA_COACH);
	    }

	    this.coachManager = new CoachManager(this);

	    App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_CREATE_FINISHED));
	}
	@Override
	protected void onStart()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_START));

		super.onStart();

		App.addPictoMessageReceivedListener(this.pictoMessageReceivedListener);
		App.addHereNowReceivedListener(this.hereNowReceivedListener);
	    App.addPresenceReceivedListener(this.presenceReceivedListener);

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_START_FINISHED));
	}
	@Override
	protected void onResume()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_RESUME));

		super.onResume();

		App.addHistoryReceivedListener(this.historyReceivedListener);

		//Don't refresh when user got here by returning from an activity
		if (!this.userNavigated)
			this.coachManager.get(this.bundleCoach.getId(), this.loadCoachReceiver);
		else {
			this.userNavigated = false;

			if (this.apiCoach != null) {
				queueRefreshCommunications(this.apiCoach);
			}
		}

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_RESUME_FINISHED));
	}
	@Override
	protected void onPause()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_PAUSE));

		App.removeHistoryReceivedListener(this.historyReceivedListener);

		super.onPause();

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_PAUSE_FINISHED));
	}
	@Override
	protected void onStop()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_STOP));

		App.removePictoMessageReceivedListener(this.pictoMessageReceivedListener);
		App.removeHereNowReceivedListener(this.hereNowReceivedListener);
		App.removePresenceReceivedListener(this.presenceReceivedListener);

		super.onStop();

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_STOP_FINISHED));
	}
	@Override
	protected void onDestroy()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_DESTROY));

		//TODO Place this code somewhere useful...
		/*App.leaveClientRooms();

		for (Client client : this.bundleCoach.getClientList())
			App.closeRoom(client);

		App.setAlreadySubscribed(false);*/

		super.onDestroy();

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_DESTROY_FINISHED));
	}

	public static void start(Context context, Coach coach) {
		Intent intent = buildIntent(context, coach);

    	context.startActivity(intent);
	}
	public static Intent buildIntent(Context context, Coach coach) {
		Intent intent = new Intent(context, ClientListActivity.class);

		intent.putExtra(EXTRA_COACH, coach);

		return intent;
	}


	private void queueSetupCommunications(final Coach coach) {
		if (App.getPictoChatCommunicator().isStarted())
			setupCommunications(coach);
    	else
    		App.getPictoChatCommunicator().addInitializedListener(new IInitializedListener() {
				@Override
				public void onInitialized() {
					setupCommunications(coach);
				}
			});
	}
	private void setupCommunications(Coach coach) {
		App.setUser(coach);
		App.joinClientRooms(AppState.ViewingList);

		for (Client client : coach.getClientList())
			App.hostRoom(client, AppState.ViewingList);

		App.setAlreadySubscribed(true);

		refreshCommunications(coach);
	}
	private void queueRefreshCommunications(final Coach coach) {
		if (App.getPictoChatCommunicator().isStarted())
			refreshCommunications(coach);
    	else
    		App.getPictoChatCommunicator().addInitializedListener(new IInitializedListener() {
				@Override
				public void onInitialized() {
					refreshCommunications(coach);
				}
			});
	}
	private void refreshCommunications(Coach coach) {
		this.totalUnread = 0;

		for (Client client : coach.getClientList()) {
			App.getPictoChatCommunicator().usersHereNow(coach, client);
		    App.getPictoChatCommunicator().clientsHereNow(client);
		    App.getPictoChatCommunicator().coachesHereNow(client);

		    App.getPictoChatCommunicator().usersPresence(coach, client);
			App.getPictoChatCommunicator().clientsPresence(client);
			App.getPictoChatCommunicator().coachesPresence(client);

			App.getPictoChatCommunicator().historySent(client, App.getLastCheckedTime(client));
			App.getPictoChatCommunicator().historyReceived(coach, client, App.getLastPrivateCheckedTime(client));
	    }
	}


	private void updateClientListItem(ClientStatus item) {
		int position = this.clientsAdapter.getPosition(item);
		View view = this.lstClients.getChildAt(position - this.lstClients.getFirstVisiblePosition());
		if (view != null) {
			this.clientsAdapter.getView(position, view, this.lstClients);
		}
	}


	//Interface events
	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
			ClientStatus item = ClientListActivity.this.clientsAdapter.getItem(position);
			item.privateUnread = 0;
			item.unread = 0;
			updateClientListItem(item);

			//TODO Clean up this mess
			for (Client client : ClientListActivity.this.apiCoach.getClientList())
				if (client.getId().equals(item.getId())) {
					ClientListActivity.this.userNavigated = true;
					ClientDetailsActivity.start(ClientListActivity.this, client, ClientListActivity.this.apiCoach);
					break;
				}
		}
	};

	private IPictoMessageReceivedListener pictoMessageReceivedListener = new IPictoMessageReceivedListener() {
		@Override
		public void onPictoMessageReceived(Client client, PictoMessage message) {
			App.logToFile(FileLogItem.debug(ClientListActivity.this, Tags.MSG, Messages.MESSAGE_RECEIVED, client.getFullName()));

			ClientStatus item = ClientListActivity.this.clientsAdapter.getClientStatus(client);
			if (item != null) {
				item.unread++;
				updateClientListItem(item);
			}
		}
		@Override
		public void onPictoMessageReceived(User remote, PictoMessage message) {
			if (remote instanceof Client)
				onPictoMessageReceived((Client)remote, message);
			else
				App.logToFile(FileLogItem.debug(ClientListActivity.this, Tags.MSG, Messages.MESSAGE_IGNORED, remote.getFullName()));
		}
		@Override
		public void onPictoMessageReceived(Coach coach, Client client, PictoMessage message) {
			onPictoMessageReceived(client, message);
		}
	};
	private IPresenceReceivedListener presenceReceivedListener = new IPresenceReceivedListener() {
		@Override
		public void onPresenceReceived(PubnubChannel channel, boolean isPrivate, User host, User user, Presence presence, AppState appState, Date lastRead) {
			if (user instanceof Client) {
				Client client = (Client)user;
				ClientStatus item = ClientListActivity.this.clientsAdapter.getClientStatus(client);

				if (isPrivate)
					item.privateState = appState;
				else
					item.groupState = appState;

				if (!Presence.STATE_CHANGE.equals(presence))
					item.status = presence;

				updateClientListItem(item);
			}
			else if (user instanceof Coach && host instanceof Client) {
				Coach coach = (Coach)user;
				Client client = (Client)host;
				ClientStatus item = ClientListActivity.this.clientsAdapter.getClientStatus(client);

				if (Presence.JOIN.equals(presence))
					item.addCoach(coach);
				else if (Presence.LEAVE.equals(presence) || Presence.TIMEOUT.equals(presence))
					item.removeCoach(coach);

				updateClientListItem(item);
			}
		}
	};
	private IHereNowReceivedListener hereNowReceivedListener = new IHereNowReceivedListener() {
		@Override
		public void onHereNowReceived(PubnubChannel channel, boolean isPrivate, User host, List<Participant> participants, int occupancy) {
			ClientStatus item = null;
			if (host instanceof Client)
				item = ClientListActivity.this.clientsAdapter.getClientStatus((Client)host);

			for (Participant participant : participants) {
				User user = participant.getUser();
				if (user instanceof Client) {
					ClientStatus cs = ClientListActivity.this.clientsAdapter.getClientStatus((Client)user);
					cs.status = Presence.JOIN;

					if (isPrivate)
						cs.privateState = participant.getState();
					else
						cs.groupState = participant.getState();

					updateClientListItem(cs);
				}
				else if (user instanceof Coach) {
					Coach coach = (Coach)user;
					if (item != null) {
						item.addCoach(coach);
						updateClientListItem(item);
					}
				}
			}
		}
	};
	private IHistoryReceivedListener historyReceivedListener = new IHistoryReceivedListener() {
		@Override
		public void onHistoryReceived(PubnubChannel channel, boolean isPrivate, User host, User other, List<TextMessage> messages) {
			if (host instanceof Client) {
				Client client = (Client)host;
				ClientStatus item = ClientListActivity.this.clientsAdapter.getClientStatus(client);

				int size = 0;
				for (TextMessage message : messages)
					if (!message.getSent())
						size++;

				if (isPrivate)
					item.privateUnread = size;
				else
					item.unread = size;

				ClientListActivity.this.totalUnread += size;
				App.setBadge(ClientListActivity.this, ClientListActivity.this.totalUnread);

				updateClientListItem(item);
			}
		}
	};


	//Manager listeners
	private LoginResultReceiver loadCoachReceiver = new LoginResultReceiver("loadCoach")
	{
		@Override
		protected void onLoggedInSuccessfully(be.kpoint.pictochat.api.rest.coach.Coach c) {
			App.logToFile(FileLogItem.debug(ClientListActivity.this, Tags.REST, Messages.COACH_LOADED_SUCCESFULLY, c.getId().toString(), c.getFullName()));

			Coach coach = Coach.buildFromRest(c);
			if (!App.isAlreadySubscribed())
				ClientListActivity.this.queueSetupCommunications(coach);
			else
				queueRefreshCommunications(coach);

			List<Client> coachClients = coach.getClientList();

			be.kpoint.pictochat.util.List.updateList(ClientListActivity.this.clients, coachClients, new IConverts<ClientStatus, Client>() {
				@Override
				public ClientStatus convert(Client client) {
					return new ClientStatus(client.getId(), client.getFirstName(), client.getLastName());
				}
			});
			ClientListActivity.this.clientsAdapter.notifyDataSetChanged();

			ClientListActivity.this.apiCoach = coach;
		}

		@Override
		public void onFinished() {
			//hideWaitSpinner();
		}

		@Override
		public void onTimedOut() {
			App.logToFile(FileLogItem.warn(ClientListActivity.this, Tags.REST, Messages.USER_LOAD_TIMED_OUT));
		}

		@Override
		public void onClientError() {
			App.logToFile(FileLogItem.error(ClientListActivity.this, Tags.REST, Messages.USER_LOAD_CLIENT_ERROR));
		}

		@Override
		public void onServerError(String json) {
			App.logToFile(FileLogItem.error(ClientListActivity.this, Tags.REST, Messages.USER_LOAD_SERVER_ERROR));
		}

		@Override
		protected void onLoginWrong() {
			// TODO Auto-generated method stub

		}
	};


	private class ClientsArrayAdapter extends ArrayAdapter<ClientStatus>
	{
		private class ViewHolder {
			public ImageView image;
			public TextView text;
			public TextView subText;
			public ImageView imgAppState;
			public RelativeLayout lytBubble;
			public TextView lblUnreadCount;
		}

		public ClientsArrayAdapter(Activity context, List<ClientStatus> clients) {
			super(context, R.layout.client_item, clients);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView;

			if (convertView != null)
				rowView = convertView;
			else {
				LayoutInflater inflater = ((Activity)this.getContext()).getLayoutInflater();
				rowView = inflater.inflate(R.layout.client_item, null);

				ViewHolder viewHolder = new ViewHolder();
				viewHolder.image = (ImageView)rowView.findViewById(R.id.clientItem_imgAvatar);
				viewHolder.text = (TextView)rowView.findViewById(R.id.clientItem_txtText);
				viewHolder.subText = (TextView)rowView.findViewById(R.id.clientItem_txtSubText);
				viewHolder.imgAppState = (ImageView)rowView.findViewById(R.id.clientItem_imgStatus);
				viewHolder.lblUnreadCount = (TextView)rowView.findViewById(R.id.clientItem_lblUnreadCount);
				viewHolder.lytBubble = (RelativeLayout)rowView.findViewById(R.id.clientItem_lytBubble);

				rowView.setTag(viewHolder);
			}

			//Insert data
			ViewHolder holder = (ViewHolder)rowView.getTag();
			ClientStatus client = this.getItem(position);
			holder.text.setText(client.getFullName());
			holder.subText.setText(client.getInfoString(ClientListActivity.this.bundleCoach));

			int count = client.unread + client.privateUnread;
			if (count > 0) {
				holder.lytBubble.setVisibility(View.VISIBLE);
				holder.lblUnreadCount.setVisibility(View.VISIBLE);
				holder.lblUnreadCount.setText(String.valueOf(count));
			}
			else
				holder.lytBubble.setVisibility(View.INVISIBLE);

			switch (client.status) {
				case JOIN:
					holder.image.setImageResource(R.drawable.status_online);
					switch (client.getAppState()) {
						case ChatOpen:
							holder.imgAppState.setImageResource(R.drawable.ic_chat_bubble_black_36dp);
							break;
						case Typing:
							holder.imgAppState.setImageResource(R.drawable.ic_textsms_black_36dp);
							break;
						case ViewingList:
							holder.imgAppState.setImageResource(R.drawable.ic_list_black_36dp);
							break;
						default:
							holder.imgAppState.setImageDrawable(null);
					}
					break;
				case LEAVE:
					holder.image.setImageResource(R.drawable.status_offline);
					holder.imgAppState.setImageDrawable(null);
					break;
				default:
					holder.image.setImageResource(R.drawable.status_away);
					holder.imgAppState.setImageDrawable(null);
					break;
			}

			return rowView;
		}

		public ClientStatus getClientStatus(Client client) {
			ClientStatus status = new ClientStatus(client.getId(), client.getFirstName(), client.getLastName());
			int position = this.getPosition(status);
			ClientStatus item = this.getItem(position);

			return item;
		}
	}


	private class ClientStatus extends Client
	{
		public Presence status = Presence.LEAVE;
		public AppState groupState = AppState.None;
		public AppState privateState = AppState.None;
		public int unread = 0;
		public int privateUnread = 0;
		public Set<Coach> presentCoaches = new HashSet<Coach>();

		public ClientStatus(ClientId id, String firstName, String lastName) {
			super(id, firstName, lastName, null);
		}

		@Override
		public void addCoach(Coach coach) {
			this.presentCoaches.add(coach);
		}
		public void removeCoach(Coach coach) {
			this.presentCoaches.remove(coach);
		}

		public AppState getAppState() {
			int max = Math.max(this.groupState.ordinal(), this.privateState.ordinal());
			return AppState.values[max];
		}
		public Integer coachCount() {
			return this.presentCoaches.size();
		}

		public String getInfoString(Coach me) {
			StringBuilder sb = new StringBuilder("(" + coachCount() + ") ");
			for (Coach coach : this.presentCoaches)
				sb.append(coach.equals(me) ? getResources().getString(R.string.clientList_me) : coach.getFirstName()).append(", ");

			String info = sb.toString();
			if (coachCount() > 0)
				 info = info.substring(0, info.length() - 2);

			return info;
		}
	}
}
