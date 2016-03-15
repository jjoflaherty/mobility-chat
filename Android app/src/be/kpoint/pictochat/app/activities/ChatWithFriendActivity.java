package be.kpoint.pictochat.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.app.domain.Client;
import be.kpoint.pictochat.app.domain.Friend;
import be.kpoint.pictochat.app.domain.PictoMessage;
import be.kpoint.pictochat.business.comm.PictoSendResultReceiver;
import be.kpoint.pictochat.business.comm.interfaces.IInitializedListener;

public class ChatWithFriendActivity extends AbstractClientChatActivity
{
	private static final String EXTRA_CLIENT = "client";
	protected static final String EXTRA_FRIEND = "friend";


	private Friend friend;


	//Life cycle
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
	    if (bundle != null) {
	    	final Client client = (Client)bundle.get(EXTRA_CLIENT);
	    	final Friend friend = (Friend)bundle.get(EXTRA_FRIEND);

	    	this.setTitle(friend.getFullName());

	    	this.friend = friend;

	    	if (App.getPictoChatCommunicator().isStarted())
	    		setupCommunications(client, friend);
	    	else
	    		App.getPictoChatCommunicator().addInitializedListener(new IInitializedListener() {
					@Override
					public void onInitialized() {
						setupCommunications(client, friend);
					}
				});
	    }
	}

	public static void start(Context context, Client client, Friend friend) {
		Intent intent = buildIntent(context, client, friend);

    	context.startActivity(intent);
	}
	public static Intent buildIntent(Context context, Client client, Friend friend) {
		Intent intent = new Intent(context, ChatWithFriendActivity.class);

		intent.putExtra(EXTRA_CLIENT, client);
		intent.putExtra(EXTRA_FRIEND, friend);

		return intent;
	}


	private void setupCommunications(Client client, Friend friend) {
		super.setupCommunications(client);

		App.getPictoChatCommunicator().historyReceived(client, friend, 5);
		App.getPictoChatCommunicator().historySent(client, friend, 5);
	}


	@Override
	protected void sendPictoMessage(PictoMessage message, PictoSendResultReceiver receiver) {
		App.sendPictoMessageToFriend(this.friend, message, receiver);
	}
}
