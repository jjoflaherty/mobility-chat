package be.kpoint.pictochat.api.picto.texttopicto;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import be.kpoint.pictochat.app.Constants;
import be.kpoint.pictochat.network.AbstractRestServiceManager;
import be.kpoint.pictochat.network.ApiTarget;

public class TextToPictoManager extends AbstractRestServiceManager
{
	public TextToPictoManager(Context context) {
		super(context);
	}

	public void get(String text, String database, String language, boolean parallel, TextToPictoResultReceiver receiver) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("text", text));
		nameValuePairs.add(new BasicNameValuePair("type", database));
		nameValuePairs.add(new BasicNameValuePair("language", language));

		if (parallel)
			nameValuePairs.add(new BasicNameValuePair("parallel", "true"));

		ApiTarget target = Constants.text_to_picto.setUrlParameters(nameValuePairs);

		Bundle metadata = new Bundle();
		metadata.putString(TextToPictoResultReceiver.BUNDLE_DATABASE_TAG, database);
		get(target, metadata, receiver);
	}
}
