package be.kpoint.pictochat.api.rest.client;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import be.kpoint.pictochat.api.rest.button.Button;
import be.kpoint.pictochat.api.rest.button.ButtonParser;
import be.kpoint.pictochat.api.rest.coach.Coach;
import be.kpoint.pictochat.api.rest.coach.CoachParser;
import be.kpoint.pictochat.api.rest.friend.Friend;
import be.kpoint.pictochat.api.rest.friend.FriendParser;
import be.kpoint.pictochat.api.rest.ids.PageId;
import be.kpoint.pictochat.api.rest.page.Page;
import be.kpoint.pictochat.api.rest.page.PageParser;


public class ClientParser
{
	private static final String JSON_FIRSTNAME_TAG = "firstName";
	private static final String JSON_LASTNAME_TAG = "lastName";
	private static final String JSON_CODE_TAG = "code";
	private static final String JSON_IMAGEURL_TAG = "imageUrl";
	private static final String JSON_COACHES_TAG = "coaches";
	private static final String JSON_FRIENDS_TAG = "friends";
	private static final String JSON_CONTACTS_TAG = "contacts";
	private static final String JSON_BUTTONS_TAG = "buttons";
	private static final String JSON_STARTPAGE_TAG = "startPage";
	private static final String JSON_STARTPAGEID_TAG = "id";
	private static final String JSON_PAGES_TAG = "pages";
	private static final String JSON_CLIENTID_TAG = "id";


	public static Client parse(JSONObject jsonObject) throws JSONException {
		long id = jsonObject.getLong(JSON_CLIENTID_TAG);
		String firstName = jsonObject.getString(JSON_FIRSTNAME_TAG);
		String lastName = jsonObject.getString(JSON_LASTNAME_TAG);
		String url = jsonObject.getString(JSON_IMAGEURL_TAG);
		PageId startPageId = null;

		Client client = Client.create(id, firstName, lastName, url);

		if (!jsonObject.isNull(JSON_CODE_TAG))
			client.setCode(jsonObject.getString(JSON_CODE_TAG));

		if (!jsonObject.isNull(JSON_STARTPAGE_TAG)) {
			JSONObject jsonStartPage = jsonObject.getJSONObject(JSON_STARTPAGE_TAG);
			startPageId = new PageId(jsonStartPage.getLong(JSON_STARTPAGEID_TAG));
		}

		if (!jsonObject.isNull(JSON_COACHES_TAG)) {
			JSONArray jsonCoaches = jsonObject.getJSONArray(JSON_COACHES_TAG);
			for (int i = 0; i < jsonCoaches.length(); i++)
        	{
				JSONObject jsonCoach = jsonCoaches.getJSONObject(i);
				Coach coach = CoachParser.parse(jsonCoach);
				client.addCoach(coach);
        	}
		}

		if (!jsonObject.isNull(JSON_FRIENDS_TAG)) {
			JSONArray jsonFriends = jsonObject.getJSONArray(JSON_FRIENDS_TAG);
			for (int i = 0; i < jsonFriends.length(); i++)
        	{
				JSONObject jsonFriend = jsonFriends.getJSONObject(i);
				Friend friend = FriendParser.parse(jsonFriend);
				client.addFriend(friend);
        	}
		}

		if (!jsonObject.isNull(JSON_PAGES_TAG)) {
			JSONArray jsonPages = jsonObject.getJSONArray(JSON_PAGES_TAG);
			for (int i = 0; i < jsonPages.length(); i++)
        	{
				JSONObject jsonPage = jsonPages.getJSONObject(i);
				Page page = PageParser.parse(jsonPage);
				client.addPage(page);

				if (page.getId().equals(startPageId))
					client.setStartPage(page);
        	}
		}

		if (!jsonObject.isNull(JSON_BUTTONS_TAG)) {
			JSONArray jsonButtons = jsonObject.getJSONArray(JSON_BUTTONS_TAG);
			for (int i = 0; i < jsonButtons.length(); i++)
        	{
				JSONObject jsonButton = jsonButtons.getJSONObject(i);
				Button button = ButtonParser.parse(jsonButton);
				client.addButton(button);
        	}
		}

		return client;
	}

	private ClientParser() {}
}
