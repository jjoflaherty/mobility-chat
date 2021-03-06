package be.kpoint.pictochat.api.picto.texttopicto;

import java.util.List;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import be.kpoint.pictochat.app.domain.Picto;
import be.kpoint.pictochat.network.AbstractRestResultReceiver;

public abstract class TextToPictoResultReceiver extends AbstractRestResultReceiver
{
	protected static final String BUNDLE_DATABASE_TAG = "database";


	protected abstract void onPictosLoaded(List<Picto> pictos);

	public TextToPictoResultReceiver(String name) {
		super(name);
	}

	@Override
	protected void onComplete(JSONObject jsonObject, Bundle metadata) {
		String database = metadata.getString(BUNDLE_DATABASE_TAG);

		try {
			List<Picto> pictos = TextToPictoParser.parse(database, jsonObject);
			onPictosLoaded(pictos);
		} catch (JSONException e) {
			Log.e(this.getClass().getSimpleName(), e.toString());
			onClientError();
		}
	}
}
