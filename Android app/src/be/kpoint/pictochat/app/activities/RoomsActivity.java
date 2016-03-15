package be.kpoint.pictochat.app.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.api.rest.client.ClientManager;
import be.kpoint.pictochat.api.rest.client.ClientResultReceiver;
import be.kpoint.pictochat.app.R;
import be.kpoint.pictochat.app.activities.components.ProgressSpinnerOverlay;
import be.kpoint.pictochat.app.activities.components.ProgressSpinnerOverlay.IProgressSpinnerOverlay;
import be.kpoint.pictochat.app.domain.Client;
import be.kpoint.pictochat.app.domain.Coach;
import be.kpoint.pictochat.app.domain.Friend;
import be.kpoint.pictochat.app.domain.PictoMessage;
import be.kpoint.pictochat.app.domain.RoomInfo;
import be.kpoint.pictochat.app.domain.TextMessage;
import be.kpoint.pictochat.app.domain.User;
import be.kpoint.pictochat.business.comm.enums.Presence;
import be.kpoint.pictochat.business.comm.interfaces.IHereNowReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.IInitializedListener;
import be.kpoint.pictochat.business.comm.interfaces.IPictoMessageReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.IPresenceReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.ITextMessageReceivedListener;
import be.kpoint.pictochat.comm.pubnub.PubnubChannel;
import be.kpoint.pictochat.logging.Messages;
import be.kpoint.pictochat.logging.Tags;
import be.kpoint.pictochat.util.SnappedHorizontalScrollView;
import be.kpoint.pictochat.util.SnappedHorizontalScrollView.ISelectedItemChangedListener;
import be.kpoint.pictochat.util.logging.FileLogItem;

public class RoomsActivity extends Activity implements IProgressSpinnerOverlay
{
	private static final String EXTRA_CLIENT = "client";


	//Interface objects
	private Button btnOpen, btnRefresh;
	private Button btnPrevious, btnNext;
	private TextView txtUser;
	private TextView txtNoRooms;
	private TextView txtRoomInfo;
	private SnappedHorizontalScrollView snappedScroll;
	private View prev = null;

	private Client client;
	private List<RoomInfo> rooms;

	//Interface components
	private ProgressSpinnerOverlay progressOverlay;

	private ClientManager clientManager;


	//Life cycle
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.rooms);
	    super.onCreate(savedInstanceState);

	    App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_CREATE));

	    this.btnOpen = (Button)findViewById(R.id.rooms_btnOpen);
	    this.btnRefresh = (Button)findViewById(R.id.rooms_btnRefresh);
	    this.btnNext = (Button)findViewById(R.id.rooms_btnNext);
	    this.btnPrevious = (Button)findViewById(R.id.rooms_btnPrevious);
	    this.txtNoRooms = (TextView)findViewById(R.id.rooms_txtNoRooms);
	    this.txtRoomInfo = (TextView)findViewById(R.id.rooms_txtRoomInfo);
	    this.snappedScroll = (SnappedHorizontalScrollView)findViewById(R.id.rooms_hsvRooms);

	    this.btnRefresh.setOnClickListener(this.refreshListener);
	    this.btnNext.setOnClickListener(this.nextListener);
	    this.btnPrevious.setOnClickListener(this.previousListener);

	    this.progressOverlay = new ProgressSpinnerOverlay(this);

	    this.snappedScroll.setSelectedItemChangedListener(new ISelectedItemChangedListener() {
			@Override
			public void onSelectedItemChanged(Integer index, View item) {
				highlightAndSelectPath(null);

				if (index != null) {
					RoomInfo roomInfo = RoomsActivity.this.rooms.get(index);
					RoomsActivity.this.txtRoomInfo.setText(roomInfo.getLargeText());
					RoomsActivity.this.txtRoomInfo.setVisibility(View.VISIBLE);
				}
			}
		});

	    Bundle bundle = getIntent().getExtras();
	    if (bundle != null)
	    {
	    	this.client = (Client)bundle.getSerializable(EXTRA_CLIENT);
	    }

		this.clientManager = new ClientManager(this);

	    App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_CREATE_FINISHED));
	}
	@Override
	protected void onResume()
	{
		super.onResume();

		showWaitSpinner(App.buildString(R.string.checking_for_active_path));

		//Check whether user has an active path
		this.clientManager.get(this.client.getId(), this.loadClientReceiver);
	}
	@Override
	protected void onDestroy() {
		//this.networkConnectionManager.stopListening();

		App.closeRoom();

		for (RoomInfo room : this.rooms) {
			Friend friend = room.getFriend();
			if (friend != null)
				App.closeRoom(friend);
		}

		super.onDestroy();
	}

	public static void start(Context context, Client client) {
		Intent intent = buildIntent(context, client);

    	context.startActivity(intent);
	}
	public static Intent buildIntent(Context context, Client client) {
		Intent intent = new Intent(context, RoomsActivity.class);

		intent.putExtra(EXTRA_CLIENT, client);

		return intent;
	}


	private void setupCommunications(final Client client) {
		if (App.getPictoChatCommunicator().isStarted())
    		startCommunicator(client);
    	else
    		App.getPictoChatCommunicator().addInitializedListener(new IInitializedListener() {
				@Override
				public void onInitialized() {
					startCommunicator(client);
				}
			});
	}
	private void startCommunicator(Client client) {
		App.addTextMessageReceivedListener(this.textMessageReceivedListener);
		App.addPictoMessageReceivedListener(this.pictoMessageReceivedListener);
		App.addPresenceReceivedListener(this.presenceReceivedListener);
		App.addHereNowReceivedListener(this.hereNowReceivedListener);

		App.setUser(this.client);
		App.hostRoom();
		App.getPictoChatCommunicator().coachesHereNow(this.client);
		App.getPictoChatCommunicator().coachesPresence(this.client);

		List<Friend> friends = client.getFriends();
		for (Friend friend : friends) {
			App.hostRoom(friend);
			App.getPictoChatCommunicator().friendsHereNow(this.client, friend);
			App.getPictoChatCommunicator().friendsPresence(this.client, friend);
		}
	}


	private void addRoomToList(RoomInfo roomInfo, boolean last) {
		LayoutInflater inflater = this.getLayoutInflater();
		RelativeLayout l = (RelativeLayout)inflater.inflate(R.layout.room, this.snappedScroll, false);

		ImageView image = (ImageView)l.findViewById(R.id.room_imgAvatar);
		TextView lblFirstName = (TextView)l.findViewById(R.id.room_lblFirstName);
		TextView lblLastName = (TextView)l.findViewById(R.id.room_lblLastName);

    	/*ImageView image = new ImageView(this);
    	image.setImageResource(R.drawable.hand_touch_icon);*/
    	image.setOnClickListener(roomInfo.getClickListener());
    	lblFirstName.setText(roomInfo.getLargeText());
    	lblLastName.setText(roomInfo.getSmallText());
    	roomInfo.showInImageView(image, this.snappedScroll.getWidth());

    	this.snappedScroll.addChildView(l, R.drawable.bluesquare);

    	if (last) {
    		hideWaitSpinner();

    		/*Window window = this.getWindow();
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);*/
    	}
	}
	private void highlightAndSelectPath(Integer index) {
		if (this.prev != null)
			this.prev.setBackgroundResource(R.drawable.bluesquare);

		if (index != null) {
			View parent = this.snappedScroll.getViewFromIndex(index);
			parent.setBackgroundResource(R.drawable.button_mask);

			RoomsActivity.this.btnOpen.setVisibility(View.VISIBLE);
			this.prev = parent;
		}
		else
			RoomsActivity.this.btnOpen.setVisibility(View.INVISIBLE);
	}


	@Override
	public void showWaitSpinner(String text) {
		this.progressOverlay.showWaitSpinner(text);
	}
	@Override
	public void hideWaitSpinner() {
		this.progressOverlay.hideWaitSpinner();
	}


	private IPresenceReceivedListener presenceReceivedListener = new IPresenceReceivedListener() {
		@Override
		public void onPresenceReceived(User host, User user, Presence presence) {
			if (user instanceof Client) {
				Client client = (Client)user;
			}
			else if (user instanceof Coach) {
				Coach coach = (Coach)user;
			}
		}
	};
	private IHereNowReceivedListener hereNowReceivedListener = new IHereNowReceivedListener() {
		@Override
		public void onHereNowReceived(PubnubChannel channel, int occupancy, User host, List<User> users) {
			for (User user : users) {
				if (user instanceof Client) {
					Client client = (Client)user;
				}
				else if (user instanceof Coach) {
					Coach coach = (Coach)user;
				}
			}
		}
	};


	//Manager listeners
	private ClientResultReceiver loadClientReceiver = new ClientResultReceiver("loadClient")
	{
		@Override
		protected void onClientLoaded(be.kpoint.pictochat.api.rest.client.Client client) {
			App.logToFile(FileLogItem.debug(RoomsActivity.this, Tags.REST, Messages.USER_LOADED_SUCCESFULLY, client.getId().toString(), client.getFullName()));

			Client c = Client.buildFromRest(client);
			RoomsActivity.this.setupCommunications(c);

			List<RoomInfo> rooms = new ArrayList<RoomInfo>();
			rooms.add(RoomInfo.create("Coaches", "", RoomsActivity.this.openCoachChatListener));

			List<Friend> friends = c.getFriends();
			for (Friend f : friends) {
				RoomInfo roomInfo = RoomInfo.create(f, RoomsActivity.this.openFriendChatListener);
				roomInfo.setImageUrl(f.getImageUrl());
				rooms.add(roomInfo);
			}

			int i = 0;
			RoomsActivity.this.snappedScroll.removeAllViews();
			for (RoomInfo r : rooms) {
				addRoomToList(r, i == rooms.size() - 1);
				i++;
			}

			RoomsActivity.this.rooms = rooms;
		}

		@Override
		public void onFinished() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTimedOut() {
			App.logToFile(FileLogItem.warn(RoomsActivity.this, Tags.REST, Messages.USER_LOAD_TIMED_OUT));
		}

		@Override
		public void onClientError() {
			App.logToFile(FileLogItem.error(RoomsActivity.this, Tags.REST, Messages.USER_LOAD_CLIENT_ERROR));
		}

		@Override
		public void onServerError(String json) {
			App.logToFile(FileLogItem.error(RoomsActivity.this, Tags.REST, Messages.USER_LOAD_SERVER_ERROR));
		}
	};


	//Interface events
	private OnClickListener openFriendChatListener = new OnClickListener()
	{
		@Override
		public void onClick(View image)
	    {
			ViewGroup parent = (ViewGroup)image.getParent().getParent();
			Integer index = RoomsActivity.this.snappedScroll.getViewIndex(parent);
			if (index != null) {
				RoomInfo room = RoomsActivity.this.rooms.get(index);
				ChatWithFriendActivity.start(RoomsActivity.this, RoomsActivity.this.client, room.getFriend());
			}
	    }
	};
	private OnClickListener openCoachChatListener = new OnClickListener()
	{
		@Override
		public void onClick(View image)
	    {
			ViewGroup parent = (ViewGroup)image.getParent().getParent();
			Integer index = RoomsActivity.this.snappedScroll.getViewIndex(parent);
			if (index != null) {
				RoomInfo room = RoomsActivity.this.rooms.get(index);
				ChatWithCoachesActivity.start(RoomsActivity.this, RoomsActivity.this.client);
			}
	    }
	};
	private OnClickListener refreshListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
	    {
			finish();
			startActivity(getIntent());
	    }
	};
	private OnClickListener nextListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) {
			RoomsActivity.this.snappedScroll.scrollToNextElement();
		}
	};
	private OnClickListener previousListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) {
			RoomsActivity.this.snappedScroll.scrollToPreviousElement();
		}
	};


	private ITextMessageReceivedListener textMessageReceivedListener = new ITextMessageReceivedListener() {
		@Override
		public void onTextMessageReceived(TextMessage message) {
			int i = 0 + 1;
		}
	};
	private IPictoMessageReceivedListener pictoMessageReceivedListener = new IPictoMessageReceivedListener() {
		@Override
		public void onPictoMessageReceived(Coach coach, PictoMessage message) {
			Log.d("", "");
		}

		@Override
		public void onPictoMessageReceived(Friend friend, PictoMessage message) {
			for (int i = 0; i < RoomsActivity.this.rooms.size(); i++) {
				RoomInfo room = RoomsActivity.this.rooms.get(i);
				if (friend.equals(room.getFriend())) {
					View v = RoomsActivity.this.snappedScroll.getViewFromIndex(i);

					ImageView bubble = (ImageView)v.findViewById(R.id.room_imgBubble);
					bubble.setVisibility(View.VISIBLE);
				}
			}
		}

		@Override
		public void onPictoMessageReceived(Client client, PictoMessage message) {
			Log.d("", "");
		}
	};
}