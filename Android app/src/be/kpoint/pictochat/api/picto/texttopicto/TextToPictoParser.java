package be.kpoint.pictochat.api.picto.texttopicto;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import be.kpoint.pictochat.app.domain.Picto;


public class TextToPictoParser
{
	private static final String JSON_STATUS_TAG = "status";
	private static final String JSON_TEXTINPUT_TAG = "textInput";
	private static final String JSON_PICTOS_TAG = "pictos";


	public static List<Picto> parse(String database, JSONObject jsonObject) throws JSONException {
		String status = jsonObject.getString(JSON_STATUS_TAG);
		String input = jsonObject.getString(JSON_TEXTINPUT_TAG);
		List<Picto> pictos = new ArrayList<Picto>();

		if (!jsonObject.isNull(JSON_PICTOS_TAG)) {
			JSONArray jsonPictos = jsonObject.getJSONArray(JSON_PICTOS_TAG);
			for (int i = 0; i < jsonPictos.length(); i++) {
				Object value = jsonPictos.get(i);
				if (value instanceof JSONArray) {
					JSONArray jsonPicto = (JSONArray)value;

					String url = jsonPicto.getString(0);
					String text = jsonPicto.getString(1);

					if (!url.isEmpty())
						pictos.add(new Picto(database, text, url));
				}
			}
		}

		return pictos;
	}

	private TextToPictoParser() {}
}
