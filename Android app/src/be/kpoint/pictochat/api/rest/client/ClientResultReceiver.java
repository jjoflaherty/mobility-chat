package be.kpoint.pictochat.api.rest.client;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import be.kpoint.pictochat.network.AbstractRestResultReceiver;

public abstract class ClientResultReceiver extends AbstractRestResultReceiver
{
	protected Client client;

	protected abstract void onClientLoaded(Client client);

	public ClientResultReceiver(String name) {
		super(name);
	}

	@Override
	protected void onAfterPostProcessing() {
		onClientLoaded(this.client);
	}

	@Override
	protected void onComplete(JSONObject jsonObject, Bundle metadata) {
		try {
			this.client = ClientParser.parse(jsonObject);
		} catch (JSONException e) {
			Log.e(this.getClass().getSimpleName(), e.toString());
			onClientError();
		}
	}
}
