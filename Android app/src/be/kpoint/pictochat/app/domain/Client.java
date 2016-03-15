package be.kpoint.pictochat.app.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.kpoint.pictochat.api.rest.ids.ClientId;
import be.kpoint.pictochat.api.rest.ids.PageId;

public class Client extends User
{
	private static final long serialVersionUID = 3637181077320210689L;


	private ClientId id;
	private Integer pin;
	private Room room;
	private Page startPage;
	private Map<PageId, Page> pages = new HashMap<PageId, Page>();
	private List<Friend> friends = new ArrayList<Friend>();

	public Client(ClientId id, String firstName, String lastName) {
		super(firstName, lastName);

		this.id = id;
	}

	public static Client buildFromRest(be.kpoint.pictochat.api.rest.client.Client client) {
		Client c = new Client(client.getId(), client.getFirstName(), client.getLastName());
		c.startPage = Page.buildFromRest(client.getStartPage());

		for (be.kpoint.pictochat.api.rest.page.Page page : client.getPages()) {
			Page p = Page.buildFromRest(page);

			c.addPage(p);
		}

		for (be.kpoint.pictochat.api.rest.friend.Friend friend : client.getFriends()) {
			Friend f = Friend.buildFromRest(friend);

			c.addFriend(f);
		}

		return c;
	}


	public ClientId getId() {
		return this.id;
	}
	public Integer getPin() {
		return this.pin;
	}
	public void setPin(Integer pin) {
		this.pin = pin;
		this.room = Room.createForClientAndCoaches(this);
	}

	public void addPage(Page page) {
		this.pages.put(page.getId(), page);
	}
	public void addFriend(Friend friend) {
		this.friends.add(friend);
	}

	public List<Friend> getFriends() {
		return this.friends;
	}

	public Page getStartPage() {
		return this.startPage;
	}
	public Page getPage(PageId pageId) {
		return this.pages.get(pageId);
	}

	public Room getRoom() {
		return this.room;
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
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
}
