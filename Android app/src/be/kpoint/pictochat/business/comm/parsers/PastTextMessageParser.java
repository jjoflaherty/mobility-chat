package be.kpoint.pictochat.business.comm.parsers;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.app.domain.PastTextMessage;

public class PastTextMessageParser
{
	public static final String TYPE = "TEXT";

	private static final String JSON_SENDERNAME_TAG = "sender_name";
	private static final String JSON_TEXT_TAG = "text";
	private static final String JSON_UUID_TAG = "uuid";
	private static final String JSON_TIME_TAG = "t";


	public static PastTextMessage parse(JSONObject jsonObject, long timeToken) throws JSONException {
		String senderName = jsonObject.getString(JSON_SENDERNAME_TAG);
		String text = jsonObject.getString(JSON_TEXT_TAG);
		String uuid = jsonObject.getString(JSON_UUID_TAG);
		String t = jsonObject.optString(JSON_TIME_TAG);

		Calendar calender = Calendar.getInstance();
		calender.setTimeInMillis(timeToken / 10000);
		Date time = calender.getTime();

		Boolean sent = uuid.equals(App.getUUId());

		PastTextMessage message = new PastTextMessage(text, senderName, sent, time);
		message.setUuid(uuid);
		message.setServerTime(time);
		message.setSenderAppTime(t);

		return message;
	}

	private PastTextMessageParser() {}
}
