package be.kpoint.pictochat.api.rest.page;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import be.kpoint.pictochat.api.rest.button.Button;
import be.kpoint.pictochat.api.rest.button.ButtonParser;


public class PageParser
{
	private static final String JSON_NAME_TAG = "name";
	private static final String JSON_ROWS_TAG = "rows";
	private static final String JSON_COLUMNS_TAG = "columns";
	private static final String JSON_BUTTONS_TAG = "buttons";
	private static final String JSON_PAGEID_TAG = "id";


	public static Page parse(JSONObject jsonObject) throws JSONException {
		long id = jsonObject.getLong(JSON_PAGEID_TAG);
		String name = jsonObject.getString(JSON_NAME_TAG);

		Page page = Page.create(id, name);

		page.setRows(jsonObject.getInt(JSON_ROWS_TAG));
		page.setColumns(jsonObject.getInt(JSON_COLUMNS_TAG));

		if (!jsonObject.isNull(JSON_BUTTONS_TAG)) {
			JSONArray jsonButtons = jsonObject.getJSONArray(JSON_BUTTONS_TAG);
			for (int i = 0; i < jsonButtons.length(); i++)
        	{
				JSONObject jsonButton = jsonButtons.getJSONObject(i);
				Button button = ButtonParser.parse(jsonButton);
				page.addButton(button);
        	}
		}

		return page;
	}

	private PageParser() {}
}
