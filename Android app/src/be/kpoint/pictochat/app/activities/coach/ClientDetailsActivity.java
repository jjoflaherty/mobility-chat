package be.kpoint.pictochat.app.activities.coach;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.api.rest.ids.CoachId;
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
import be.kpoint.pictochat.util.logging.FileLogItem;

public class ClientDetailsActivity extends Activity
{
	private static final String EXTRA_CLIENT = "client";
	private static final String EXTRA_COACH = "coach";


	//Interface objects
	private TextView lblClientName;
	private Button btnGroupChat;
	private Button btnPrivateChat;
	private RelativeLayout lytGroupBubble;
	private RelativeLayout lytPrivateBubble;
	private TextView lblGroupUnread;
	private TextView lblPrivateUnread;
	private ListView lstCoaches;
	private CoachesArrayAdapter coachAdapter;
	private List<CoachStatus> coaches = new ArrayList<CoachStatus>();

	private boolean userNavigated = false;

	private int privateUnread = 0;
	private int groupUnread = 0;

	private Client client;
	private Coach coach;


	//Life cycle
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_detail);

		this.lblClientName = (TextView)findViewById(R.id.clientDetail_lblClientName);
		this.btnGroupChat = (Button)findViewById(R.id.clientDetail_btnGroupChat);
		this.lytGroupBubble = (RelativeLayout)findViewById(R.id.clientDetail_lytGroupBubble);
		this.lblGroupUnread = (TextView)findViewById(R.id.clientDetail_lblGroupUnreadCount);
		this.btnPrivateChat = (Button)findViewById(R.id.clientDetail_btnPrivateChat);
		this.lytPrivateBubble = (RelativeLayout)findViewById(R.id.clientDetail_lytPrivateBubble);
		this.lblPrivateUnread = (TextView)findViewById(R.id.clientDetail_lblPrivateUnreadCount);
		this.lstCoaches = (ListView)findViewById(R.id.clientDetail_lstCoaches);

    	this.coachAdapter = new CoachesArrayAdapter(this, this.coaches);
		this.lstCoaches.setAdapter(this.coachAdapter);

		this.lstCoaches.setOnItemClickListener(this.itemClickListener);
		this.btnGroupChat.setOnClickListener(this.btnGroupChatListener);
		this.btnPrivateChat.setOnClickListener(this.btnPrivateChatListener);

		Bundle bundle = getIntent().getExtras();
	    if (bundle != null) {
	    	this.client = (Client)bundle.get(EXTRA_CLIENT);
	    	this.coach = (Coach)bundle.get(EXTRA_COACH);
	    	this.lblClientName.setText(this.client.getFullName());

	    	for (Coach c : this.client.getCoaches())
	    		this.coaches.add(new CoachStatus(c.getId(), c.getFirstName(), c.getLastName(), c.getImageUrl()));
	    }
	}
	@Override
	protected void onStart()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_START));

		super.onStart();

		App.addHereNowReceivedListener(this.hereNowReceivedListener);
		App.addPresenceReceivedListener(this.presenceReceivedListener);
		App.addPictoMessageReceivedListener(this.pictoMessageReceivedListener);

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_START_FINISHED));
	}
	@Override
	protected void onResume()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_RESUME));

		super.onResume();

		App.addHistoryReceivedListener(this.historyReceivedListener);

		//Don't refresh when user got here by returning from an activity
		if (!this.userNavigated) {
			startCommunicator(this.coach, this.client);
		}
		else
			this.userNavigated = false;

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

		App.removeHereNowReceivedListener(this.hereNowReceivedListener);
		App.removePresenceReceivedListener(this.presenceReceivedListener);
		App.removePictoMessageReceivedListener(this.pictoMessageReceivedListener);

		super.onStop();

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_STOP_FINISHED));
	}
	@Override
	protected void onDestroy()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_DESTROY));

		super.onDestroy();

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_DESTROY_FINISHED));
	}

	public static void start(Context context, Client client, Coach coach) {
		Intent intent = buildIntent(context, client, coach);

    	context.startActivity(intent);
	}
	public static Intent buildIntent(Context context, Client client, Coach coach) {
		Intent intent = new Intent(context, ClientDetailsActivity.class);

		intent.putExtra(EXTRA_CLIENT, client);
		intent.putExtra(EXTRA_COACH, coach);

		return intent;
	}


	private void startCommunicator(final Coach coach, final Client client) {
		if (App.getPictoChatCommunicator().isStarted())
			setupCommunications(coach, client);
		else
			App.getPictoChatCommunicator().addInitializedListener(new IInitializedListener() {
				@Override
				public void onInitialized() {
					setupCommunications(coach, client);
				}
			});
	}
	private void setupCommunications(Coach coach, Client client) {
		App.getPictoChatCommunicator().clientsHereNow(client);
	    App.getPictoChatCommunicator().coachesHereNow(client);
		App.getPictoChatCommunicator().clientsPresence(client);
		App.getPictoChatCommunicator().coachesPresence(client);

		App.getPictoChatCommunicator().historySent(client, App.getLastCheckedTime(client));
		App.getPictoChatCommunicator().historyReceived(coach, client, App.getLastPrivateCheckedTime(client));
	}


	private void setPrivateUnreadCounter(int count) {
		this.privateUnread = count;

		if (count > 0) {
			ClientDetailsActivity.this.lytPrivateBubble.setVisibility(View.VISIBLE);
			ClientDetailsActivity.this.lblPrivateUnread.setVisibility(View.VISIBLE);
			ClientDetailsActivity.this.lblPrivateUnread.setText(String.valueOf(count));
		}
		else
			ClientDetailsActivity.this.lytPrivateBubble.setVisibility(View.INVISIBLE);
	}
	private void setGroupUnreadCounter(int count) {
		this.groupUnread = count;

		if (count > 0) {
			ClientDetailsActivity.this.lytGroupBubble.setVisibility(View.VISIBLE);
			ClientDetailsActivity.this.lblGroupUnread.setVisibility(View.VISIBLE);
			ClientDetailsActivity.this.lblGroupUnread.setText(String.valueOf(count));
		}
		else
			ClientDetailsActivity.this.lytGroupBubble.setVisibility(View.INVISIBLE);
	}

	private void updateCoachListItem(CoachStatus item) {
		int position = this.coachAdapter.getPosition(item);
		View view = this.lstCoaches.getChildAt(position - this.lstCoaches.getFirstVisiblePosition());
		if (view != null) {
			this.coachAdapter.getView(position, view, this.lstCoaches);
		}
	}


	//Interface events
	private OnClickListener btnGroupChatListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ClientDetailsActivity.this.lytGroupBubble.setVisibility(View.INVISIBLE);
			ClientDetailsActivity.this.userNavigated = true;

			GroupChatWithClientActivity.start(ClientDetailsActivity.this, ClientDetailsActivity.this.client, ClientDetailsActivity.this.coach);
		}
	};
	private OnClickListener btnPrivateChatListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ClientDetailsActivity.this.lytPrivateBubble.setVisibility(View.INVISIBLE);
			ClientDetailsActivity.this.userNavigated = true;

			PrivateChatWithClientActivity.start(ClientDetailsActivity.this, ClientDetailsActivity.this.client, ClientDetailsActivity.this.coach);
		}
	};
	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
			Coach coach = ClientDetailsActivity.this.coachAdapter.getItem(position);
		}
	};


	private IPresenceReceivedListener presenceReceivedListener = new IPresenceReceivedListener() {
		@Override
		public void onPresenceReceived(PubnubChannel channel, boolean isPrivate, User host, User user, Presence presence, AppState appState, Date lastRead) {
			if (user instanceof Coach) {
				Coach coach = (Coach)user;
				CoachStatus item = ClientDetailsActivity.this.coachAdapter.getCoachStatus(coach);
				if (item == null) {
					item = new CoachStatus(coach.getId(), coach.getFirstName(), coach.getLastName(), coach.getImageUrl());
					ClientDetailsActivity.this.coachAdapter.add(item);
				}

				if (Presence.JOIN.equals(presence))
					item.status = Presence.JOIN;
				else if (Presence.LEAVE.equals(presence) || Presence.TIMEOUT.equals(presence))
					item.status = Presence.LEAVE;

				updateCoachListItem(item);
			}
		}
	};
	private IHereNowReceivedListener hereNowReceivedListener = new IHereNowReceivedListener() {
		@Override
		public void onHereNowReceived(PubnubChannel channel, boolean isPrivate, User host, List<Participant> participants, int occupancy) {
			for (Participant participant : participants) {
				User user = participant.getUser();
				if (user instanceof Coach) {
					Coach coach = (Coach)user;
					CoachStatus item = ClientDetailsActivity.this.coachAdapter.getCoachStatus(coach);
					if (item == null) {
						item = new CoachStatus(coach.getId(), coach.getFirstName(), coach.getLastName(), coach.getImageUrl());
						ClientDetailsActivity.this.coachAdapter.add(item);
					}

					item.status = Presence.JOIN;

					updateCoachListItem(item);
				}
			}
		}
	};
	private IHistoryReceivedListener historyReceivedListener = new IHistoryReceivedListener() {
		@Override
		public void onHistoryReceived(PubnubChannel channel, boolean isPrivate, User host, User other, List<TextMessage> messages) {
			if (ClientDetailsActivity.this.client.equals(host)) {
				int size = 0;
				for (TextMessage message : messages)
					if (!message.getSent())
						size++;

				if (isPrivate)
					setPrivateUnreadCounter(size);
				else
					setGroupUnreadCounter(size);
			}
		}
	};
	private IPictoMessageReceivedListener pictoMessageReceivedListener = new IPictoMessageReceivedListener() {
		@Override
		public void onPictoMessageReceived(User remote, PictoMessage message) {
			if (ClientDetailsActivity.this.client.equals(remote)) {
				App.logToFile(FileLogItem.debug(ClientDetailsActivity.this, Tags.MSG, Messages.PRIVATE_MESSAGE_RECEIVED, remote.getFullName()));

				setPrivateUnreadCounter(ClientDetailsActivity.this.privateUnread++);
			}
			else
				App.logToFile(FileLogItem.debug(ClientDetailsActivity.this, Tags.MSG, Messages.PRIVATE_MESSAGE_IGNORED, remote.getFullName()));
		}
		@Override
		public void onPictoMessageReceived(Client client, PictoMessage message) {
			if (ClientDetailsActivity.this.client.equals(client)) {
				App.logToFile(FileLogItem.debug(ClientDetailsActivity.this, Tags.MSG, Messages.MESSAGE_RECEIVED, client.getFullName()));

				setGroupUnreadCounter(ClientDetailsActivity.this.groupUnread++);
			}
			else
				App.logToFile(FileLogItem.debug(ClientDetailsActivity.this, Tags.MSG, Messages.MESSAGE_IGNORED, client.getFullName()));
		}

		@Override
		public void onPictoMessageReceived(Coach coach, Client client, PictoMessage message) {}
	};


	private class CoachesArrayAdapter extends ArrayAdapter<CoachStatus>
	{
		private Activity context;

		private class ViewHolder {
			public ImageView image;
			public TextView text;
		}

		public CoachesArrayAdapter(Activity context, List<CoachStatus> coaches) {
			super(context, R.layout.room_item, coaches);

			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView;

			if (convertView != null)
				rowView = convertView;
			else {
				LayoutInflater inflater = this.context.getLayoutInflater();
				rowView = inflater.inflate(R.layout.coach_item, parent, false);

				ViewHolder viewHolder = new ViewHolder();
				viewHolder.text = (TextView)rowView.findViewById(R.id.coachItem_txtText);
				viewHolder.image = (ImageView)rowView.findViewById(R.id.coachItem_imgAvatar);

				rowView.setTag(viewHolder);
			}

			//Insert data
			ViewHolder holder = (ViewHolder)rowView.getTag();
			CoachStatus coach = this.getItem(position);
			holder.text.setText(coach.getFullName());

			switch (coach.status) {
				case JOIN:
					holder.image.setImageResource(R.drawable.status_online);
					break;
				case LEAVE:
					holder.image.setImageResource(R.drawable.status_offline);
					break;
				default:
					holder.image.setImageResource(R.drawable.status_away);
					break;
			}

			return rowView;
		}

		public CoachStatus getCoachStatus(Coach coach) {
			CoachStatus status = new CoachStatus(coach.getId(), coach.getFirstName(), coach.getLastName(), coach.getImageUrl());
			int position = this.getPosition(status);

			CoachStatus item = null;
			if (position >= 0)
				item = this.getItem(position);

			return item;
		}
	}

	private class CoachStatus extends Coach
	{
		public Presence status = Presence.LEAVE;

		public CoachStatus(CoachId id, String firstName, String lastName, String imageUrl) {
			super(id, firstName, lastName, imageUrl);
		}
	}
}