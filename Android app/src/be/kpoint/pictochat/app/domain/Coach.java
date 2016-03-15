package be.kpoint.pictochat.app.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import be.kpoint.pictochat.api.rest.ids.CoachId;

public class Coach extends User
{
	private static final long serialVersionUID = 6175811848650977046L;


	private CoachId id;
	private String imageUrl;
	private HashMap<String, Client> clients = new HashMap<String, Client>();


	public Coach(CoachId id, String firstName, String lastName, String imageUrl) {
		super(firstName, lastName);

		this.id = id;
		this.imageUrl = imageUrl;
	}

	public static Coach buildFromRest(be.kpoint.pictochat.api.rest.coach.Coach coach) {
		Coach c = new Coach(coach.getId(), coach.getFirstName(), coach.getLastName(), coach.getImageUrl());

		for (be.kpoint.pictochat.api.rest.client.Client client : coach.getClients()) {
			Client cl = new Client(client.getId(), client.getFirstName(), client.getLastName());
			cl.setPin(client.getPin());

			c.addClient(cl);
		}

		return c;
	}


	public CoachId getId() {
		return this.id;
	}
	public String getImageUrl() {
		return this.imageUrl;
	}
	public HashMap<String, Client> getClients() {
		return this.clients;
	}
	public List<Client> getClientList() {
		return new ArrayList<Client>(this.getClients().values());
	}

	public void clearClients() {
		this.clients.clear();
	}
	public void addClient(Client client) {
		this.clients.put(client.getRoom().getChannelName(), client);
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;

		if (obj == null)
			return false;

		if (!(obj instanceof Coach))
			return false;

		return this.id.equals(((Coach)obj).id);
	}
}
