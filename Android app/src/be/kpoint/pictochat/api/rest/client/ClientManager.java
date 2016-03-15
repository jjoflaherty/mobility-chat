package be.kpoint.pictochat.api.rest.client;

import java.util.ArrayList;

import android.content.Context;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import be.kpoint.pictochat.api.rest.ids.ClientId;
import be.kpoint.pictochat.app.Constants;
import be.kpoint.pictochat.network.AbstractRestServiceManager;
import be.kpoint.pictochat.network.ApiTarget;


public class ClientManager extends AbstractRestServiceManager
{
	public ClientManager(Context context) {
		super(context);
	}

	public void get(ClientId id, ClientResultReceiver receiver) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("id", Long.toString(id.getNumber())));
		ApiTarget target = Constants.client_read.setUrlParameters(nameValuePairs);

		get(target, receiver);
	}
}
