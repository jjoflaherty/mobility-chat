package be.kpoint.pictochat.app.activities.coach;

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
import be.kpoint.pictochat.comm.pubnub.PubnubManager.ICallWhenPubnubManagerStartedListener;
import be.kpoint.pictochat.logging.Messages;
import be.kpoint.pictochat.logging.Tags;
import be.kpoint.pictochat.util.logging.FileLogItem;

public class GroupChatWithClientActivity extends AbstractChatWithClientActivity
{
	//Life cycle
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_CREATE));

		super.onCreate(savedInstanceState);
		setTitle(this.client.getFullName() + " - " + getResources().getString(R.string.clientDetail_group_chat));

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

		startCommunicator(this.client);

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_RESUME_FINISHED));
	}
	@Override
	protected void onPause()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_PAUSE));

		App.getPictoChatCommunicator().callWhenStarted(new ICallWhenPubnubManagerStartedListener() {
			@Override
			public void onStarted() {
				App.getPictoChatCommunicator().setCoachState(GroupChatWithClientActivity.this.client, GroupChatWithClientActivity.this.coach, AppState.None);
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


	public static void start(Context context, Client client, Coach coach) {
		Intent intent = buildIntent(context, client, coach);

    	context.startActivity(intent);
	}
	public static Intent buildIntent(Context context, Client client, Coach coach) {
		Intent intent = new Intent(context, GroupChatWithClientActivity.class);

		intent.putExtra(EXTRA_CLIENT, client);
		intent.putExtra(EXTRA_COACH, coach);

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

		App.getPictoChatCommunicator().setCoachState(client, this.coach, AppState.ChatOpen);
	}

	//Implement abstract methods
	@Override
	protected boolean ignoreMessage(Client client) {
		return !this.client.equals(client);
	}
	@Override
	protected boolean ignorePrivateMessage(User remote) {
		return true;
	}
	@Override
	protected boolean ignoreHistory(boolean isPrivate, User host) {
		return isPrivate || !this.client.equals(host);
	}

	@Override
	protected List<PictoMessage> getCacheMessages() {
		return App.getMessages(this.client);
	}
	@Override
	protected Date getLastReadTime() {
		return App.getLastReadTime(this.client);
	}
	@Override
	protected void sendPictoMessage(PictoMessage message, PictoSendResultReceiver receiver) {
		App.sendPictoMessageToClient(this.client, message, receiver);
	}
}