package be.kpoint.pictochat.api.rest.coach;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import be.kpoint.pictochat.api.rest.client.Client;
import be.kpoint.pictochat.api.rest.client.ClientParser;


public class CoachParser
{
	private static final String JSON_FIRSTNAME_TAG = "firstName";
	private static final String JSON_LASTNAME_TAG = "lastName";
	private static final String JSON_CLIENTS_TAG = "clients";
	private static final String JSON_IMAGEURL_TAG = "imageUrl";
	private static final String JSON_COACHID_TAG = "id";


	public static Coach parse(JSONObject jsonObject) throws JSONException {
		long id = jsonObject.getLong(JSON_COACHID_TAG);
		String firstName = jsonObject.getString(JSON_FIRSTNAME_TAG);
		String lastName = jsonObject.getString(JSON_LASTNAME_TAG);
		String imageUrl = jsonObject.getString(JSON_IMAGEURL_TAG);

		Coach coach = Coach.create(id, firstName, lastName, imageUrl);

		if (!jsonObject.isNull(JSON_CLIENTS_TAG)) {
			JSONArray jsonClients = jsonObject.getJSONArray(JSON_CLIENTS_TAG);
			for (int i = 0; i < jsonClients.length(); i++)
        	{
				JSONObject jsonClient = jsonClients.getJSONObject(i);
				Client client = ClientParser.parse(jsonClient);
				coach.addClient(client);
        	}
		}

		return coach;
	}

	private CoachParser() {}
}
