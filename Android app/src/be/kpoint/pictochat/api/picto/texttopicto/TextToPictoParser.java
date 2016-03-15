package be.kpoint.pictochat.api.picto.texttopicto;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TextToPictoParser
{
	private static final String JSON_STATUS_TAG = "status";
	private static final String JSON_TEXTINPUT_TAG = "textInput";
	private static final String JSON_PICTOS_TAG = "pictos";


	public static List<String> parse(JSONObject jsonObject) throws JSONException {
		String status = jsonObject.getString(JSON_STATUS_TAG);
		String input = jsonObject.getString(JSON_TEXTINPUT_TAG);
		List<String> pictos = new ArrayList<String>();

		if (!jsonObject.isNull(JSON_PICTOS_TAG)) {
			JSONArray jsonPictos = jsonObject.getJSONArray(JSON_PICTOS_TAG);
			for (int i = 0; i < jsonPictos.length(); i++)
				pictos.add(jsonPictos.getString(i));
		}

		return pictos;
	}

	private TextToPictoParser() {}
}
