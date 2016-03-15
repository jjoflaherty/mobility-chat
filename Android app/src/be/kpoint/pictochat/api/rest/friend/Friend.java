package be.kpoint.pictochat.api.rest.friend;

import java.io.Serializable;

import be.kpoint.pictochat.api.rest.ids.ClientId;

public class Friend implements Serializable
{
	private ClientId id;
	private String firstName;
	private String lastName;
	private Boolean host;
	private String imageUrl;

	private Friend() {
		//Needed for serialization
	}
	private Friend(long id)
	{
		this.id = new ClientId(id);
	}
	public static Friend create(long id, String firstName, String lastName, String imageUrl, Boolean host) {
		Friend user = new Friend(id);
		user.firstName = firstName;
		user.lastName = lastName;
		user.imageUrl = imageUrl;
		user.host = host;

		return user;
	}


	public ClientId getId()
	{
		return this.id;
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
	public Boolean getWillHost() {
		return this.host;
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

		if (!(obj instanceof Friend))
			return false;

		return this.id.equals(((Friend)obj).id);
	}
}
