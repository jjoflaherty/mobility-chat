package be.kpoint.pictochat.api.rest.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import be.kpoint.pictochat.api.rest.button.Button;
import be.kpoint.pictochat.api.rest.coach.Coach;
import be.kpoint.pictochat.api.rest.friend.Friend;
import be.kpoint.pictochat.api.rest.ids.ClientId;
import be.kpoint.pictochat.api.rest.page.Page;

public class Client implements Serializable
{
	private ClientId id;
	private String firstName;
	private String lastName;
	private String imageUrl;
	private String code;
	private Page startPage;
	private Set<Page> pages = new HashSet<Page>();
	private Set<Coach> coaches = new HashSet<Coach>();
	private Set<Friend> friends = new HashSet<Friend>();
	private List<Button> buttons = new ArrayList<Button>();

	private Client() {
		//Needed for serialization
	}
	private Client(long id)
	{
		this.id = new ClientId(id);
	}
	public static Client create(long id, String firstName, String lastName, String imageUrl) {
		Client user = new Client(id);
		user.firstName = firstName;
		user.lastName = lastName;
		user.imageUrl = imageUrl;

		return user;
	}


	public ClientId getId()
	{
		return this.id;
	}


	protected void setCode(String code) {
		this.code = code;
	}
	protected void addCoach(Coach coach) {
		this.coaches.add(coach);
	}
	protected void addPage(Page page) {
		this.pages.add(page);
	}
	protected void addFriend(Friend friend) {
		this.friends.add(friend);
	}
	protected void addButton(Button button) {
		this.buttons.add(button);
	}

	public Set<Coach> getCoaches() {
		return this.coaches;
	}
	public Set<Friend> getFriends() {
		return this.friends;
	}
	public List<Button> getButtons() {
		return this.buttons;
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
	public String getImageUrl() {
		return this.imageUrl;
	}
	public String getCode() {
		return this.code;
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
