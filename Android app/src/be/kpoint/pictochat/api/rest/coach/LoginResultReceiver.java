package be.kpoint.pictochat.api.rest.coach;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import be.kpoint.pictochat.network.AbstractRestResultReceiver;

public abstract class LoginResultReceiver extends AbstractRestResultReceiver
{
	protected static final String BUNDLE_USERNAME_TAG = "name";
	protected static final String BUNDLE_PASSWORD_TAG = "password";

	protected Coach coach;

	protected abstract void onLoggedInSuccessfully(Coach coach);
	protected abstract void onLoginWrong();

	public LoginResultReceiver(String name) {
		super(name);
	}

	@Override
	protected final void onAfterPostProcessing() {
		onLoggedInSuccessfully(this.coach);
	}

	@Override
	protected final void onComplete(JSONObject jsonObject, Bundle metadata) {
		if (!jsonObject.has("error")) {
			try {
				this.coach = CoachParser.parse(jsonObject);
			} catch (JSONException e) {
				Log.e(this.getClass().getSimpleName(), e.toString());
				onClientError();
			}
		}
		else
			onLoginWrong();
	}
}
