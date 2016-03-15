package be.kpoint.pictochat.app.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.app.domain.Client;
import be.kpoint.pictochat.app.domain.PictoMessage;
import be.kpoint.pictochat.business.comm.PictoSendResultReceiver;
import be.kpoint.pictochat.business.comm.interfaces.IInitializedListener;

public class ChatWithCoachesActivity extends AbstractClientChatActivity
{
	private static final String EXTRA_CLIENT = "client";


	//Life cycle
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
	    if (bundle != null) {
	    	final Client client = (Client)bundle.get(EXTRA_CLIENT);

	    	this.setTitle("Coaches");

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


	@Override
	protected void setupCommunications(Client client) {
		super.setupCommunications(client);

		App.getPictoChatCommunicator().historyReceived(client, 5);
		App.getPictoChatCommunicator().historySent(client, 5);
	}


	@Override
	protected void sendPictoMessage(PictoMessage message, PictoSendResultReceiver receiver) {
		App.sendPictoMessageToCoaches(message, receiver);
	}
}
