package be.kpoint.pictochat.app.domain;

import be.kpoint.pictochat.api.rest.ids.ClientId;

public class Friend extends User
{
	private static final long serialVersionUID = -1136305178328440484L;


	private ClientId id;


	protected Friend(ClientId id, String firstName, String lastName, String imageUrl)
	{
		super(firstName, lastName, imageUrl);

		this.id = id;
	}

	public static Friend buildFromRest(be.kpoint.pictochat.api.rest.friend.Friend friend) {
		Friend f = new Friend(friend.getId(), friend.getFirstName(), friend.getLastName(), friend.getImageUrl());

		return f;
	}


	@Override
	public ClientId getId()
	{
		return this.id;
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
