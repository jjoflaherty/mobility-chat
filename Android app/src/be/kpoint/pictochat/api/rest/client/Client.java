package be.kpoint.pictochat.api.rest.client;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import be.kpoint.pictochat.api.rest.friend.Friend;
import be.kpoint.pictochat.api.rest.ids.ClientId;
import be.kpoint.pictochat.api.rest.ids.CoachId;
import be.kpoint.pictochat.api.rest.page.Page;

public class Client implements Serializable
{
	private ClientId id;
	private String firstName;
	private String lastName;
	private Integer pin;
	private Page startPage;
	private Set<Page> pages = new HashSet<Page>();
	private Set<CoachId> coachIds = new HashSet<CoachId>();
	private Set<Friend> friends = new HashSet<Friend>();

	private Client() {
		//Needed for serialization
	}
	private Client(long id)
	{
		this.id = new ClientId(id);
	}
	public static Client create(long id, String firstName, String lastName) {
		Client user = new Client(id);
		user.firstName = firstName;
		user.lastName = lastName;

		return user;
	}


	public ClientId getId()
	{
		return this.id;
	}


	protected void setPin(Integer pin) {
		this.pin = pin;
	}
	protected void addCoachId(long coachId) {
		this.coachIds.add(new CoachId(coachId));
	}

	protected void addPage(Page page) {
		this.pages.add(page);
	}
	protected void addFriend(Friend friend) {
		this.friends.add(friend);
	}

	public Set<CoachId> getCoachIds() {
		return this.coachIds;
	}
	public Set<Friend> getFriends() {
		return this.friends;
	}


	public Page getStartPage() {
		return this.startPage;
	}
	public void setStartPage(Page startPage) {
		this.startPage = startPage;
	}
	public Set<Page> getPages() {
		return this.pages;
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
	public Integer getPin() {
		return this.pin;
	}


	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;

		if (obj == null)
			return false;

		if (!(obj instanceof Client))
			return false;

		return this.id.equals(((Client)obj).id);
	}
}
