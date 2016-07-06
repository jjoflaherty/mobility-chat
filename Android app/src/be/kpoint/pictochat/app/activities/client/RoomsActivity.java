package be.kpoint.pictochat.app.activities.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import be.kpoint.pictochat.api.rest.client.AddFriendResultReceiver;
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
import be.kpoint.pictochat.app.domain.RoomInfo.OnRoomClickListener;
import be.kpoint.pictochat.app.domain.TextMessage;
import be.kpoint.pictochat.app.domain.User;
import be.kpoint.pictochat.business.comm.enums.AppState;
import be.kpoint.pictochat.business.comm.interfaces.IHistoryReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.IInitializedListener;
import be.kpoint.pictochat.business.comm.interfaces.IPictoMessageReceivedListener;
import be.kpoint.pictochat.comm.pubnub.PubnubChannel;
import be.kpoint.pictochat.comm.pubnub.PubnubManager.ICallWhenPubnubManagerStartedListener;
import be.kpoint.pictochat.logging.Messages;
import be.kpoint.pictochat.logging.Tags;
import be.kpoint.pictochat.util.List.IConverts;
import be.kpoint.pictochat.util.WebImageView;
import be.kpoint.pictochat.util.logging.FileLogItem;

public class RoomsActivity extends Activity implements IProgressSpinnerOverlay
{
	private static final String EXTRA_CLIENT = "client";


	//Interface objects
	private Button btnRefresh;
	private ListView lstRooms;

	private float dpppx;
	private RoomArrayAdapter roomsAdapter;

	private boolean userNavigated = false;
	private int totalUnread = 0;

	private Client bundleClient;
	private Client apiClient;
	private List<RoomInfo> rooms = new ArrayList<RoomInfo>();
	private Map<User, RoomInfo> roomMap = new HashMap<User, RoomInfo>();

	//Interface components
	private ProgressSpinnerOverlay progressOverlay;

	private ClientManager clientManager;


	//Life cycle
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_CREATE));

		setContentView(R.layout.rooms);
	    super.onCreate(savedInstanceState);

	    this.dpppx = getResources().getDisplayMetrics().density;

	    this.btnRefresh = (Button)findViewById(R.id.rooms_btnRefresh);
	    this.lstRooms = (ListView)findViewById(R.id.rooms_lstRooms);

	    this.roomsAdapter = new RoomArrayAdapter(this, this.rooms);
		this.lstRooms.setAdapter(this.roomsAdapter);

	    this.btnRefresh.setOnClickListener(this.refreshListener);

	    this.progressOverlay = new ProgressSpinnerOverlay(this);

	    this.lstRooms.setOnItemClickListener(this.itemClickListener);

	    Bundle bundle = getIntent().getExtras();
	    if (bundle != null)
	    {
	    	this.bundleClient = (Client)bundle.getSerializable(EXTRA_CLIENT);
	    }

		this.clientManager = new ClientManager(this);

	    App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_CREATE_FINISHED));
	}
	@Override
	protected void onStart()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_START));

		super.onStart();

		App.addPictoMessageReceivedListener(this.pictoMessageReceivedListener);

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_START_FINISHED));
	}
	@Override
	protected void onResume()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_RESUME));

		super.onResume();

		App.addHistoryReceivedListener(this.historyReceivedListener);

		App.getPictoChatCommunicator().callWhenStarted(new ICallWhenPubnubManagerStartedListener() {
			@Override
			public void onStarted() {
				App.getPictoChatCommunicator().setClientState(RoomsActivity.this.bundleClient, AppState.ViewingList);
			}
		});

		//Don't refresh when user got here by returning from an activity
		if (!this.userNavigated) {
			showWaitSpinner(App.buildString(R.string.rooms_loading_client_data));
			this.clientManager.get(this.bundleClient.getId(), this.loadClientReceiver);
		}
		else {
			this.userNavigated = false;

			if (this.apiClient != null)
				queueRefreshCommunications(this.apiClient);
		}

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_RESUME_FINISHED));
	}
	@Override
	protected void onPause()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_PAUSE));

		App.removeHistoryReceivedListener(this.historyReceivedListener);

		App.getPictoChatCommunicator().callWhenStarted(new ICallWhenPubnubManagerStartedListener() {
			@Override
			public void onStarted() {
				App.getPictoChatCommunicator().setClientState(RoomsActivity.this.bundleClient, AppState.None);
			}
		});

		super.onPause();

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_PAUSE_FINISHED));
	}
	@Override
	protected void onStop()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_STOP));

		App.removePictoMessageReceivedListener(this.pictoMessageReceivedListener);

		super.onStop();

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_STOP_FINISHED));
	}
	@Override
	protected void onDestroy()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_DESTROY));

		//TODO Place this code somewhere useful...
		/*App.closeRoom();

		for (RoomInfo room : this.rooms) {
			User user = room.getUser();
			if (user != null)
				App.closeRoom(user);
		}

		App.setAlreadySubscribed(false);*/

		super.onDestroy();

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_DESTROY_FINISHED));
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


	private void queueSetupCommunications(final Client client) {
		if (App.getPictoChatCommunicator().isStarted())
			setupCommunications(client);
    	else
    		App.getPictoChatCommunicator().addInitializedListener(new IInitializedListener() {
				@Override
				public void onInitialized() {
					setupCommunications(client);
				}
			});
	}
	private void setupCommunications(Client client) {
		App.setUser(client);
		App.hostRoom(AppState.ViewingList);

		List<Friend> friends = client.getFriends();
		for (Friend friend : friends)
			App.hostRoom(friend, AppState.ViewingList);

		List<Coach> coaches = client.getCoaches();
		for (Coach coach : coaches)
			App.hostRoom(coach, AppState.ViewingList);

		App.setAlreadySubscribed(true);

		refreshCommunications(client);
	}
	private void queueRefreshCommunications(final Client client) {
		if (App.getPictoChatCommunicator().isStarted())
			refreshCommunications(client);
    	else
    		App.getPictoChatCommunicator().addInitializedListener(new IInitializedListener() {
				@Override
				public void onInitialized() {
					refreshCommunications(client);
				}
			});
	}
	private void refreshCommunications(Client client) {
		this.totalUnread = 0;

		App.getPictoChatCommunicator().coachesHereNow(client);
		App.getPictoChatCommunicator().coachesPresence(client);
		App.getPictoChatCommunicator().historyReceived(client, App.getLastCheckedTime(client));

		List<Friend> friends = client.getFriends();
		for (Friend friend : friends) {
			App.getPictoChatCommunicator().usersHereNow(client, friend);
			App.getPictoChatCommunicator().usersPresence(client, friend);
			App.getPictoChatCommunicator().historyReceived(client, friend, App.getLastPrivateCheckedTime(friend));
		}

		List<Coach> coaches = client.getCoaches();
		for (Coach coach : coaches) {
			App.getPictoChatCommunicator().usersHereNow(client, coach);
			App.getPictoChatCommunicator().usersPresence(client, coach);
			App.getPictoChatCommunicator().historyReceived(client, coach, App.getLastPrivateCheckedTime(coach));
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rooms, menu);

		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		switch (id) {
			case R.id.roomsMenu_addFriend:
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
	            startActivityForResult(intent, 0);
				break;
			case R.id.roomsMenu_showCode:
				ShowCodeActivity.start(this, this.bundleClient);
				break;
		}

		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == 0) {
	        if (resultCode == RESULT_OK) {
	            String contents = data.getStringExtra("SCAN_RESULT");
	            this.clientManager.addFriend(this.bundleClient.getId(), this.bundleClient.getCode(), contents, this.addFriendReceiver);
	        }
	        if(resultCode == RESULT_CANCELED){
	            //handle cancel
	        }
	    }
	}


	private void updateRoomListItem(RoomInfo item) {
		int position = this.roomsAdapter.getPosition(item);
		View view = this.lstRooms.getChildAt(position - this.lstRooms.getFirstVisiblePosition());
		if (view != null) {
			this.roomsAdapter.getView(position, view, this.lstRooms);
		}
	}

	@Override
	public void showWaitSpinner(String text) {
		this.progressOverlay.showWaitSpinner(text);
	}
	@Override
	public void hideWaitSpinner() {
		this.progressOverlay.hideWaitSpinner();
	}


	//Manager listeners
	private ClientResultReceiver loadClientReceiver = new ClientResultReceiver("loadClient")
	{
		@Override
		protected void onClientLoaded(be.kpoint.pictochat.api.rest.client.Client client) {
			App.logToFile(FileLogItem.debug(RoomsActivity.this, Tags.REST, Messages.USER_LOADED_SUCCESFULLY, client.getId().toString(), client.getFullName()));

			Client c = Client.buildFromRest(client);
			if (!App.isAlreadySubscribed())
				RoomsActivity.this.queueSetupCommunications(c);
			else
				queueRefreshCommunications(c);

			List<RoomInfo> rooms = new ArrayList<RoomInfo>();
			Map<User, RoomInfo> roomMap = new HashMap<User, RoomInfo>();

			RoomInfo groupRoom = RoomInfo.create(getResources().getString(R.string.rooms_coaches), "", RoomsActivity.this.openCoachGroupChatListener);
			rooms.add(groupRoom);
			roomMap.put(c, groupRoom);

			List<Coach> coaches = c.getCoaches();
			for (Coach co : coaches) {
				RoomInfo roomInfo = RoomInfo.create(co, RoomsActivity.this.openFriendChatListener);
				roomInfo.setImageUrl(co.getImageUrl());

				rooms.add(roomInfo);
				roomMap.put(co, roomInfo);
			}

			List<Friend> friends = c.getFriends();
			for (Friend f : friends) {
				RoomInfo roomInfo = RoomInfo.create(f, RoomsActivity.this.openFriendChatListener);
				roomInfo.setImageUrl(f.getImageUrl());

				rooms.add(roomInfo);
				roomMap.put(f, roomInfo);
			}

			be.kpoint.pictochat.util.List.updateList(RoomsActivity.this.rooms, rooms, new IConverts<RoomInfo, RoomInfo>() {
				@Override
				public RoomInfo convert(RoomInfo newValue) {
					return newValue;
				}
			});
			RoomsActivity.this.roomsAdapter.notifyDataSetChanged();

			RoomsActivity.this.roomMap = roomMap;
		    RoomsActivity.this.apiClient = c;
		}

		@Override
		public void onFinished() {
			hideWaitSpinner();
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
	private AddFriendResultReceiver addFriendReceiver = new AddFriendResultReceiver("addFriend")
	{
		@Override
		protected void onFriendAdded() {
			RoomsActivity.this.clientManager.get(RoomsActivity.this.bundleClient.getId(), RoomsActivity.this.loadClientReceiver);
		}

		@Override
		public void onFinished() {
			hideWaitSpinner();
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
	private OnRoomClickListener openFriendChatListener = new OnRoomClickListener()
	{
		@Override
		public void onClick(RoomInfo room)
	    {
			PrivateChatActivity.start(RoomsActivity.this, RoomsActivity.this.apiClient, room.getUser());
	    }
	};
	private OnRoomClickListener openCoachGroupChatListener = new OnRoomClickListener()
	{
		@Override
		public void onClick(RoomInfo room)
	    {
			ChatWithCoachesActivity.start(RoomsActivity.this, RoomsActivity.this.apiClient);
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
	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
			RoomsActivity.this.userNavigated = true;

			RoomInfo item = RoomsActivity.this.roomsAdapter.getItem(position);
			item.unread = 0;
			updateRoomListItem(item);
			item.getRoomClickListener().onClick(item);
		}
	};


	private IPictoMessageReceivedListener pictoMessageReceivedListener = new IPictoMessageReceivedListener() {
		@Override
		public void onPictoMessageReceived(User remote, PictoMessage message) {
			//Private message received
			RoomInfo item = RoomsActivity.this.roomMap.get(remote);
			if (item != null) {
				item.unread++;
				updateRoomListItem(item);
			}
		}
		@Override
		public void onPictoMessageReceived(Coach coach, Client client, PictoMessage message) {
			//Message receive in group chat
			RoomInfo item = RoomsActivity.this.roomMap.get(RoomsActivity.this.bundleClient);
			if (item != null) {
				item.unread++;
				updateRoomListItem(item);
			}
		}

		@Override
		public void onPictoMessageReceived(Client client, PictoMessage message) {
			App.logToFile(FileLogItem.warn(RoomsActivity.this, Tags.MSG, Messages.MESSAGE_IGNORED, client.getFullName()));
		}
	};
	private IHistoryReceivedListener historyReceivedListener = new IHistoryReceivedListener() {
		@Override
		public void onHistoryReceived(PubnubChannel channel, boolean isPrivate, User host, User other, List<TextMessage> messages) {
			RoomInfo item = RoomsActivity.this.roomMap.get(host);
			if (item != null) {
				item.unread = messages.size();
				updateRoomListItem(item);
			}

			RoomsActivity.this.totalUnread += messages.size();
			App.setBadge(RoomsActivity.this, RoomsActivity.this.totalUnread);
		}
	};

	private class RoomArrayAdapter extends ArrayAdapter<RoomInfo>
	{
		private Activity context;

		private class ViewHolder {
			public WebImageView image;
			public TextView text;
			public RelativeLayout lytBubble;
			public ImageView imgUnread;
		}

		public RoomArrayAdapter(Activity context, List<RoomInfo> rooms) {
			super(context, R.layout.room_item, rooms);

			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView;

			if (convertView != null)
				rowView = convertView;
			else {
				LayoutInflater inflater = this.context.getLayoutInflater();
				rowView = inflater.inflate(R.layout.room_item, parent, false);

				ViewHolder viewHolder = new ViewHolder();
				viewHolder.image = (WebImageView)rowView.findViewById(R.id.roomItem_imgAvatar);
				viewHolder.text = (TextView)rowView.findViewById(R.id.roomItem_txtText);
				viewHolder.imgUnread = (ImageView)rowView.findViewById(R.id.roomItem_imgUnread);
				viewHolder.lytBubble = (RelativeLayout)rowView.findViewById(R.id.roomItem_lytBubble);

				rowView.setTag(viewHolder);
			}

			//Insert data
			ViewHolder holder = (ViewHolder)rowView.getTag();
			RoomInfo room = this.getItem(position);
			holder.text.setText(room.getLargeText() + " " + room.getSmallText());

			WebImageView imageView = holder.image;
			if (imageView != null) {
				imageView.cancel();
				imageView.setImageResource(R.drawable.group);
				room.showInImageView(holder.image, 75);

				if (position == 0) {
					int padding = (int)(15 * RoomsActivity.this.dpppx);
					imageView.setPadding(padding, padding, padding, padding);
				}
			}

			if (room.unread > 0) {
				holder.lytBubble.setVisibility(View.VISIBLE);
				holder.imgUnread.setVisibility(View.VISIBLE);
			}
			else
				holder.lytBubble.setVisibility(View.INVISIBLE);

			return rowView;
		}
	}
}