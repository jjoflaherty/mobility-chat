package be.kpoint.pictochat.business.comm.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.app.domain.Picto;
import be.kpoint.pictochat.app.domain.PictoMessage;

public class PictoMessageParser
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


	public static PictoMessage parse(JSONObject jsonObject) throws JSONException {
		String senderName = jsonObject.getString(JSON_SENDERNAME_TAG);
		String text = jsonObject.getString(JSON_TEXT_TAG);
		String uuid = jsonObject.getString(JSON_UUID_TAG);
		String t = jsonObject.getString(JSON_TIME_TAG);

		Boolean sent = uuid.equals(App.getUUId());

		PictoMessage message = new PictoMessage(text, senderName, sent);
		message.setUuid(uuid);
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
	public static JSONObject compose(String senderName, PictoMessage message) throws JSONException {
		JSONObject jsonObject = new JSONObject();

		jsonObject.put(JSON_TYPE_TAG, TYPE);
		jsonObject.put(JSON_SENDERNAME_TAG, senderName);
		jsonObject.put(JSON_TEXT_TAG, message.getText());

		if (message.getSenderImageUrl() != null)
			jsonObject.put(JSON_SENDERIMAGE_TAG, message.getSenderImageUrl());

		JSONArray pictos = new JSONArray();
		for (Picto picto : message.getPictos()) {
			JSONObject jsonPicto = new JSONObject();
			jsonPicto.put(JSON_ICON_TAG, picto.getIcon());
			jsonPicto.put(JSON_TAG_TAG, picto.getTag());
			jsonPicto.put(JSON_URL_TAG, picto.getUrl());

			pictos.put(jsonPicto);
		}

		jsonObject.put(JSON_PICTOS_TAG, pictos);

		return jsonObject;
	}

	private PictoMessageParser() {}
}
