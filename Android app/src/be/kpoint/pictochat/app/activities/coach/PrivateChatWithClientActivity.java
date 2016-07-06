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

public class PrivateChatWithClientActivity extends AbstractChatWithClientActivity
{
	//Life cycle
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_CREATE));

		super.onCreate(savedInstanceState);
		setTitle(this.client.getFullName() + " - " + getResources().getString(R.string.clientDetail_private_chat));

    	App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_CREATE_FINISHED));
	}
	@Override
	protected void onStart()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_START));

		super.onStart();

		this.lastCheckedTime = App.getLastPrivateCheckedTime(this.client);

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_START_FINISHED));
	}
	@Override
	protected void onResume()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_RESUME));

		super.onResume();

		startCommunicator(this.coach, this.client);

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_RESUME_FINISHED));
	}
	@Override
	protected void onPause()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_PAUSE));

		App.getPictoChatCommunicator().callWhenStarted(new ICallWhenPubnubManagerStartedListener() {
			@Override
			public void onStarted() {
				App.getPictoChatCommunicator().setUserState(PrivateChatWithClientActivity.this.coach, PrivateChatWithClientActivity.this.client, AppState.None);
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
			App.setLastPrivateCheckedTime(this.client, time);
		}

		super.onStop();

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_STOP_FINISHED));
	}


	public static void start(Context context, Client client, Coach coach) {
		Intent intent = buildIntent(context, client, coach);

    	context.startActivity(intent);
	}
	public static Intent buildIntent(Context context, Client client, Coach coach) {
		Intent intent = new Intent(context, PrivateChatWithClientActivity.class);

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
	private void setupCommunications(Coach local, User remote) {
		App.getPictoChatCommunicator().historyReceived(local, remote, 5);
		App.getPictoChatCommunicator().historySent(local, remote, 5);

		App.getPictoChatCommunicator().setUserState(local, remote, AppState.ChatOpen);
	}

	@Override
	protected List<PictoMessage> getCacheMessages() {
		return App.getPrivateMessages(this.client);
	}

	@Override
	protected boolean ignoreMessage(Client client) {
		return true;
	}
	@Override
	protected boolean ignorePrivateMessage(User remote) {
		return !this.client.equals(remote);
	}
	@Override
	protected boolean ignoreHistory(boolean isPrivate, User host) {
		return !isPrivate || (!this.client.equals(host) && !this.coach.equals(host));
	}

	@Override
	protected Date getLastReadTime() {
		return App.getLastPrivateReadTime(this.client);
	}
	@Override
	protected void sendPictoMessage(PictoMessage message, PictoSendResultReceiver receiver) {
		App.sendPictoMessageToUser(this.client, message, receiver);
	}
}