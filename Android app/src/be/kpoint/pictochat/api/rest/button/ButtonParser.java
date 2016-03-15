package be.kpoint.pictochat.api.rest.button;


import org.json.JSONException;
import org.json.JSONObject;

import be.kpoint.pictochat.api.rest.ids.PageId;


public class ButtonParser
{
	private static final String JSON_ID_TAG = "id";
	private static final String JSON_COLOR_TAG = "color";
	private static final String JSON_ICON_TAG = "icon";
	private static final String JSON_CELL_TAG = "cell";
	private static final String JSON_ACTION_TAG = "action";
	private static final String JSON_URL_TAG = "url";
	private static final String JSON_TEXT_TAG = "text";
	private static final String JSON_PAGEID_TAG = "targetPageId";
	private static final String JSON_PHONE_TAG = "coachPhone";


	public static Button parse(JSONObject jsonObject) throws JSONException {
		Long id = jsonObject.getLong(JSON_ID_TAG);
		String color = jsonObject.getString(JSON_COLOR_TAG);
		String icon = jsonObject.getString(JSON_ICON_TAG);
		String action = jsonObject.getString(JSON_ACTION_TAG);
		String url = jsonObject.getString(JSON_URL_TAG);
		Integer cell = jsonObject.getInt(JSON_CELL_TAG);

		Button button = Button.create(id, color, icon, action, url, cell);

		if (!jsonObject.isNull(JSON_TEXT_TAG))
			button.setText(jsonObject.getString(JSON_TEXT_TAG));

		if (!jsonObject.isNull(JSON_PAGEID_TAG))
			button.setPageId(new PageId(jsonObject.getLong(JSON_PAGEID_TAG)));

		if (!jsonObject.isNull(JSON_PHONE_TAG))
			button.setPhoneNr(jsonObject.getString(JSON_PHONE_TAG));

		return button;
	}

	private ButtonParser() {}
}
