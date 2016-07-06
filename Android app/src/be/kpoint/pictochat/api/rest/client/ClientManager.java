package be.kpoint.pictochat.api.rest.client;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

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
	public void addFriend(ClientId id, String clientCode, String friendCode, AddFriendResultReceiver receiver) {
		JSONObject body = new JSONObject();
		try {
			body.put("id", id.getNumber());
			body.put("clientCode", clientCode);
			body.put("friendCode", friendCode);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		post(Constants.client_friend, body, new Bundle(), receiver);
	}
}
