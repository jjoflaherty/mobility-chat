package be.kpoint.pictochat.app.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import be.kpoint.pictochat.api.rest.ids.ClientId;
import be.kpoint.pictochat.api.rest.ids.PageId;
import be.kpoint.pictochat.app.domain.buttons.PictoButton;

public class Client extends User
{
	private static final long serialVersionUID = 3637181077320210689L;


	private ClientId id;
	private String code;
	private Room room;
	private List<Friend> friends = new ArrayList<Friend>();
	private List<Coach> coaches = new ArrayList<Coach>();
	private List<PictoButton> buttons = new ArrayList<PictoButton>();

	private Page startPage;
	private Map<PageId, Page> pages = new HashMap<PageId, Page>();


	public Client(ClientId id, String firstName, String lastName, String imageUrl) {
		super(firstName, lastName, imageUrl);

		this.id = id;
	}

	public static Client buildFromRest(be.kpoint.pictochat.api.rest.client.Client client) {
		Client c = new Client(client.getId(), client.getFirstName(), client.getLastName(), client.getImageUrl());
		c.code = client.getCode();
		c.room = Room.createForClientAndCoaches(c);

		be.kpoint.pictochat.api.rest.page.Page startPage = client.getStartPage();
		if (startPage != null)
			c.startPage = Page.buildFromRest(startPage);

		for (be.kpoint.pictochat.api.rest.page.Page page : client.getPages()) {
			Page p = Page.buildFromRest(page);

			c.addPage(p);
		}

		SortedSet<be.kpoint.pictochat.api.rest.coach.Coach> sortedCoaches = new TreeSet<>(new Comparator<be.kpoint.pictochat.api.rest.coach.Coach>() {
			@Override
			public int compare(be.kpoint.pictochat.api.rest.coach.Coach lhs, be.kpoint.pictochat.api.rest.coach.Coach rhs) {
				return lhs.getFullName().compareTo(rhs.getFullName());
			}
		});
		sortedCoaches.addAll(client.getCoaches());
		for (be.kpoint.pictochat.api.rest.coach.Coach coach : sortedCoaches) {
			Coach co = Coach.buildFromRest(coach);

			c.addCoach(co);
		}

		SortedSet<be.kpoint.pictochat.api.rest.friend.Friend> sortedFriends = new TreeSet<>(new Comparator<be.kpoint.pictochat.api.rest.friend.Friend>() {
			@Override
			public int compare(be.kpoint.pictochat.api.rest.friend.Friend lhs, be.kpoint.pictochat.api.rest.friend.Friend rhs) {
				return lhs.getFullName().compareTo(rhs.getFullName());
			}
		});
		sortedFriends.addAll(client.getFriends());
		for (be.kpoint.pictochat.api.rest.friend.Friend friend : sortedFriends) {
			Friend f = Friend.buildFromRest(friend);

			c.addFriend(f);
		}

		for (be.kpoint.pictochat.api.rest.button.Button button : client.getButtons()) {
			PictoButton b = PictoButton.buildFromRest(button);

			c.addButton(b);
		}

		return c;
	}


	@Override
	public ClientId getId() {
		return this.id;
	}
	public String getCode() {
		return this.code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public void addCoach(Coach coach) {
		this.coaches.add(coach);
	}
	public void addPage(Page page) {
		this.pages.put(page.getId(), page);
	}
	public void addFriend(Friend friend) {
		this.friends.add(friend);
	}
	public void addButton(PictoButton button) {
		this.buttons.add(button);
	}

	public List<Coach> getCoaches() {
		return this.coaches;
	}
	public List<Friend> getFriends() {
		return this.friends;
	}
	public List<PictoButton> getButtons() {
		return this.buttons;
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
}
