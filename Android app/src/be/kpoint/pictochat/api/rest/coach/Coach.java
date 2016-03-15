package be.kpoint.pictochat.api.rest.coach;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import be.kpoint.pictochat.api.rest.client.Client;
import be.kpoint.pictochat.api.rest.ids.CoachId;

public class Coach implements Serializable
{
	private CoachId id;
	private String firstName;
	private String lastName;
	private String imageUrl;
	private Set<Client> clients = new HashSet<Client>();

	private Coach() {
		//Needed for serialization
	}
	private Coach(long id)
	{
		this.id = new CoachId(id);
	}
	public static Coach create(long id, String firstName, String lastName, String imageUrl) {
		Coach user = new Coach(id);
		user.firstName = firstName;
		user.lastName = lastName;
		user.imageUrl = imageUrl;

		return user;
	}

	public CoachId getId()
	{
		return this.id;
	}


	protected void addClient(Client client) {
		this.clients.add(client);
	}

	public Set<Client> getClients() {
		return this.clients;
	}


	public String getFirstName()
	{
		return this.firstName;
	}
	public String getLastName()
	{
		return this.lastName;
	}
	public String getFullName()
	{
		return this.firstName + " " + this.lastName;
	}
	public String getImageUrl() {
		return this.imageUrl;
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
