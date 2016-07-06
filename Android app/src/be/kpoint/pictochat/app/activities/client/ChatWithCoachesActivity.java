package be.kpoint.pictochat.app.activities.client;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.app.R;
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

public class ChatWithCoachesActivity extends AbstractClientChatActivity
{
	//Life cycle
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_CREATE));

		super.onCreate(savedInstanceState);

    	this.setTitle(getResources().getString(R.string.rooms_coaches));

	    App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_CREATE_FINISHED));
	}
	@Override
	protected void onStart()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_START));

		super.onStart();

		this.lastCheckedTime = App.getLastCheckedTime(this.client);

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_START_FINISHED));
	}
	@Override
	protected void onResume()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_RESUME));

		super.onResume();

		App.addPictoMessageReceivedListener(this.pictoMessageReceivedListener);

		startCommunicator(this.client);

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
				App.getPictoChatCommunicator().setClientState(ChatWithCoachesActivity.this.client, AppState.None);
			}
		});

		super.onPause();

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_PAUSE_FINISHED));
	}
	@Override
	protected void onStop()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_STOP));

		if (this.client != null) {
			Date time = Calendar.getInstance().getTime();
			App.setLastCheckedTime(this.client, time);
		}

		super.onStop();

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_STOP_FINISHED));
	}

	public static void start(Context context, Client client) {
		Intent intent = buildIntent(context, client);

    	context.startActivity(intent);
	}
	public static Intent buildIntent(Context context, Client client) {
		Intent intent = new Intent(context, ChatWithCoachesActivity.class);

		intent.putExtra(EXTRA_CLIENT, client);

		return intent;
	}


	private void startCommunicator(final Client client) {
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
		App.getPictoChatCommunicator().historyReceived(client, 5);
		App.getPictoChatCommunicator().historySent(client, 5);

		Date lastRead = Calendar.getInstance().getTime();
		App.getPictoChatCommunicator().setClientState(client, AppState.ChatOpen, lastRead);
	}

	private IPictoMessageReceivedListener pictoMessageReceivedListener = new IPictoMessageReceivedListener() {
		@Override
		public void onPictoMessageReceived(Coach coach, Client client, PictoMessage message) {
			App.logToFile(FileLogItem.debug(ChatWithCoachesActivity.this, Tags.MSG, Messages.MESSAGE_RECEIVED, coach.getFullName()));

			Date lastRead = Calendar.getInstance().getTime();
			App.getPictoChatCommunicator().setClientState(ChatWithCoachesActivity.this.client, AppState.ChatOpen, lastRead);
		}

		@Override
		public void onPictoMessageReceived(Client client, PictoMessage message) {}
		@Override
		public void onPictoMessageReceived(User remote, PictoMessage message) {}
	};


	@Override
	protected List<PictoMessage> getCacheMessages() {
		return App.getMessages(this.client);
	}


	@Override
	protected boolean ignoreMessage(Coach coach) {
		return false;
	}
	@Override
	protected boolean ignorePrivateMessage(User remote) {
		return true;
	}
	@Override
	protected boolean ignoreHistory(boolean isPrivate, User host) {
		return isPrivate;
	}

	@Override
	protected void onPictoControlsVisible() {
		App.getPictoChatCommunicator().callWhenStarted(new ICallWhenPubnubManagerStartedListener() {
			@Override
			public void onStarted() {
				App.getPictoChatCommunicator().setClientState(ChatWithCoachesActivity.this.client, AppState.Typing);
			}
		});
	}
	@Override
	protected void onPictoControlsInVisible() {
		App.getPictoChatCommunicator().callWhenStarted(new ICallWhenPubnubManagerStartedListener() {
			@Override
			public void onStarted() {
				Date lastRead = Calendar.getInstance().getTime();
				App.getPictoChatCommunicator().setClientState(ChatWithCoachesActivity.this.client, AppState.ChatOpen, lastRead);
			}
		});
	}
	@Override
	protected void sendPictoMessage(PictoMessage message, PictoSendResultReceiver receiver) {
		App.sendPictoMessageToCoaches(message, receiver);
	}
}
