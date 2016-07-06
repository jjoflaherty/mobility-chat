package be.kpoint.pictochat.api.rest.friend;


import org.json.JSONException;
import org.json.JSONObject;


public class FriendParser
{
	private static final String JSON_FIRSTNAME_TAG = "firstName";
	private static final String JSON_LASTNAME_TAG = "lastName";
	private static final String JSON_IMAGEURL_TAG = "imageUrl";
	private static final String JSON_FRIENDID_TAG = "id";


	public static Friend parse(JSONObject jsonObject) throws JSONException {
		long id = jsonObject.getLong(JSON_FRIENDID_TAG);
		String firstName = jsonObject.getString(JSON_FIRSTNAME_TAG);
		String lastName = jsonObject.getString(JSON_LASTNAME_TAG);
		String url = jsonObject.getString(JSON_IMAGEURL_TAG);

		Friend friend = Friend.create(id, firstName, lastName, url);

		return friend;
	}

	private FriendParser() {}
}
