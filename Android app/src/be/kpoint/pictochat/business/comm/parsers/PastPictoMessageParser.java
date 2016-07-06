package be.kpoint.pictochat.business.comm.parsers;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.app.domain.PastPictoMessage;
import be.kpoint.pictochat.app.domain.Picto;

public class PastPictoMessageParser
{
	public static final String TYPE = "PICTO";

	private static final String JSON_TYPE_TAG = "type";
	private static final String JSON_SENDERNAME_TAG = "sender_name";
	private static final String JSON_SENDERIMAGE_TAG = "sender_image";
	private static final String JSON_TEXT_TAG = "text";
	private static final String JSON_UUID_TAG = "uuid";
	private static final String JSON_TIME_TAG = "t";
	private static final String JSON_PICTOS_TAG = "pictos";
	private static final String JSON_ICON_TAG = "icon";
	private static final String JSON_TAG_TAG = "tag";
	private static final String JSON_URL_TAG = "url";


	public static PastPictoMessage parse(JSONObject jsonObject, long timeToken) throws JSONException {
		String senderName = jsonObject.getString(JSON_SENDERNAME_TAG);
		String text = jsonObject.getString(JSON_TEXT_TAG);
		String uuid = jsonObject.getString(JSON_UUID_TAG);
		String t = jsonObject.optString(JSON_TIME_TAG);

		Calendar calender = Calendar.getInstance();
		calender.setTimeInMillis(timeToken / 10000);
		Date time = calender.getTime();

		Boolean sent = uuid.equals(App.getUUId());

		PastPictoMessage message = new PastPictoMessage(text, senderName, sent, time);
		message.setUuid(uuid);
		message.setServerTime(time);
		message.setSenderAppTime(t);

		JSONArray jsonPictos = jsonObject.getJSONArray(JSON_PICTOS_TAG);
		for (int i = 0; i < jsonPictos.length(); i++) {
			JSONObject jsonPicto = jsonPictos.getJSONObject(i);
			String icon = jsonPicto.getString(JSON_ICON_TAG);
			String tag = jsonPicto.getString(JSON_TAG_TAG);
			String url = jsonPicto.getString(JSON_URL_TAG);
			message.addPicto(new Picto(icon, tag, url));
		}

		if (!jsonObject.isNull(JSON_SENDERIMAGE_TAG))
			message.setSenderImageUrl(jsonObject.getString(JSON_SENDERIMAGE_TAG));

		return message;
	}

	private PastPictoMessageParser() {}
}
