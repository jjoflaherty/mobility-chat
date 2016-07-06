package be.kpoint.pictochat.app.activities.client;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.app.domain.Client;
import be.kpoint.pictochat.app.domain.Coach;
import be.kpoint.pictochat.app.domain.PictoMessage;
import be.kpoint.pictochat.app.domain.User;
import be.kpoint.pictochat.business.comm.PictoSendResultReceiver;
import be.kpoint.pictochat.business.comm.enums.AppState;
import be.kpoint.pictochat.business.comm.interfaces.IInitializedListener;
import be.kpoint.pictochat.business.comm.interfaces.IPictoMessageReceivedListener;
import be.kpoint.pictochat.comm.pubnub.PubnubManager.ICallWhenPubnubManagerStartedListener;
import be.kpoint.pictochat.logging.Messages;
import be.kpoint.pictochat.logging.Tags;
import be.kpoint.pictochat.util.logging.FileLogItem;

public class PrivateChatActivity extends AbstractClientChatActivity
{
	protected static final String EXTRA_USER = "user";

	private User remote;


	//Life cycle
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_CREATE));

		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
	    if (bundle != null) {
	    	this.remote = (User)bundle.get(EXTRA_USER);

	    	this.setTitle(this.remote.getFullName());
	    }

	    App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_CREATE_FINISHED));
	}
	@Override
	protected void onStart()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_START));

		super.onStart();

		this.lastCheckedTime = App.getLastPrivateCheckedTime(this.remote);

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_START_FINISHED));
	}
	@Override
	protected void onResume()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_RESUME));

		super.onResume();

		App.addPictoMessageReceivedListener(this.pictoMessageReceivedListener);

		startCommunicator(this.client, this.remote);

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_RESUME_FINISHED));
	}
	@Override
	protected void onPause()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_PAUSE));

		App.removePictoMessageReceivedListener(this.pictoMessageReceivedListener);
		App.getPictoChatCommunicator().callWhenStarted(new ICallWhenPubnubManagerStartedListener() {
			@Override
			public void onStarted() {
				App.getPictoChatCommunicator().setUserState(PrivateChatActivity.this.client, PrivateChatActivity.this.remote, AppState.None);
			}
		});

		super.onPause();

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_PAUSE_FINISHED));
	}
	@Override
	protected void onStop()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_STOP));

		if (this.remote != null) {
			Date time = Calendar.getInstance().getTime();
			App.setLastPrivateCheckedTime(this.remote, time);
		}

		super.onStop();

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_STOP_FINISHED));
	}

	public static void start(Context context, Client local, User remote) {
		Intent intent = buildIntent(context, local, remote);

    	context.startActivity(intent);
	}
	public static Intent buildIntent(Context context, Client client, User user) {
		Intent intent = new Intent(context, PrivateChatActivity.class);

		intent.putExtra(EXTRA_CLIENT, client);
		intent.putExtra(EXTRA_USER, user);

		return intent;
	}


	private void startCommunicator(final Client client, final User remote) {
		if (App.getPictoChatCommunicator().isStarted())
			setupCommunications(client, remote);
    	else
    		App.getPictoChatCommunicator().addInitializedListener(new IInitializedListener() {
				@Override
				public void onInitialized() {
					setupCommunications(client, remote);
				}
			});
	}
	private void setupCommunications(Client local, User remote) {
		App.getPictoChatCommunicator().historyReceived(local, remote, 5);
		App.getPictoChatCommunicator().historySent(local, remote, 5);

		Date lastRead = Calendar.getInstance().getTime();
		App.getPictoChatCommunicator().setUserState(local, remote, AppState.ChatOpen, lastRead);
	}

	private IPictoMessageReceivedListener pictoMessageReceivedListener = new IPictoMessageReceivedListener() {
		@Override
		public void onPictoMessageReceived(User remote, PictoMessage message) {
			if (!ignorePrivateMessage(remote)) {
				App.logToFile(FileLogItem.debug(PrivateChatActivity.this, Tags.MSG, Messages.PRIVATE_MESSAGE_RECEIVED, remote.getFullName()));

				Date lastRead = Calendar.getInstance().getTime();
				App.getPictoChatCommunicator().setUserState(PrivateChatActivity.this.client, PrivateChatActivity.this.remote, AppState.ChatOpen, lastRead);
			}
			else
				App.logToFile(FileLogItem.debug(PrivateChatActivity.this, Tags.MSG, Messages.PRIVATE_MESSAGE_IGNORED, remote.getFullName()));
		}

		@Override
		public void onPictoMessageReceived(Client client, PictoMessage message) {}
		@Override
		public void onPictoMessageReceived(Coach coach, Client client, PictoMessage message) {}
	};


	@Override
	protected List<PictoMessage> getCacheMessages() {
		return App.getPrivateMessages(this.client);
	}


	@Override
	protected boolean ignoreMessage(Coach coach) {
		return true;
	}
	@Override
	protected boolean ignorePrivateMessage(User remote) {
		return !this.remote.equals(remote);
	}
	@Override
	protected boolean ignoreHistory(boolean isPrivate, User host) {
		return !isPrivate || (!this.client.equals(host) && !this.remote.equals(host));
	}

	@Override
	protected void onPictoControlsVisible() {
		App.getPictoChatCommunicator().callWhenStarted(new ICallWhenPubnubManagerStartedListener() {
			@Override
			public void onStarted() {
				App.getPictoChatCommunicator().setUserState(PrivateChatActivity.this.client, PrivateChatActivity.this.remote, AppState.Typing);
			}
		});
	}
	@Override
	protected void onPictoControlsInVisible() {
		App.getPictoChatCommunicator().callWhenStarted(new ICallWhenPubnubManagerStartedListener() {
			@Override
			public void onStarted() {
				Date lastRead = Calendar.getInstance().getTime();
				App.getPictoChatCommunicator().setUserState(PrivateChatActivity.this.client, PrivateChatActivity.this.remote, AppState.ChatOpen, lastRead);
			}
		});
	}
	@Override
	protected void sendPictoMessage(PictoMessage message, PictoSendResultReceiver receiver) {
		App.sendPictoMessageToUser(this.remote, message, receiver);
	}
}
