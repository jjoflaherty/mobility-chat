package be.kpoint.pictochat.api.rest.client;

import android.os.Bundle;

import org.json.JSONObject;

import be.kpoint.pictochat.network.AbstractRestResultReceiver;

public abstract class AddFriendResultReceiver extends AbstractRestResultReceiver
{
	protected abstract void onFriendAdded();

	public AddFriendResultReceiver(String name) {
		super(name);
	}

	@Override
	protected void onAfterPostProcessing() {
		onFriendAdded();
	}

	@Override
	protected void onComplete(JSONObject jsonObject, Bundle metadata) {

	}
}
