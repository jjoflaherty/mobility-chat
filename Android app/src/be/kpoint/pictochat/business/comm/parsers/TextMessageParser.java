package be.kpoint.pictochat.business.comm.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import be.kpoint.pictochat.app.domain.TextMessage;

public class TextMessageParser
{
	public static final String TYPE = "TEXT";

	private static final String JSON_TYPE_TAG = "type";
	private static final String JSON_SENDERNAME_TAG = "sender_name";
	private static final String JSON_TEXT_TAG = "text";
	private static final String JSON_UUID_TAG = "uuid";
	private static final String JSON_TIME_TAG = "t";


	public static TextMessage parse(JSONObject jsonObject) throws JSONException {
		String senderName = jsonObject.getString(JSON_SENDERNAME_TAG);
		String text = jsonObject.getString(JSON_TEXT_TAG);
		String uuid = jsonObject.getString(JSON_UUID_TAG);
		String t = jsonObject.getString(JSON_TIME_TAG);

		TextMessage message = new TextMessage(text, senderName, false);
		message.setUuid(uuid);
		message.setSenderAppTime(t);

		return message;
	}
	public static JSONObject compose(String senderName, String text) throws JSONException {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put(JSON_TYPE_TAG, TYPE);
		jsonObject.put(JSON_SENDERNAME_TAG, senderName);
		jsonObject.put(JSON_TEXT_TAG, text);

		return jsonObject;
	}

	private TextMessageParser() {}
}
