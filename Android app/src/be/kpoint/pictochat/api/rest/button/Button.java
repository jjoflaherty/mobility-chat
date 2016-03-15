package be.kpoint.pictochat.api.rest.button;

import java.io.Serializable;

import be.kpoint.pictochat.api.rest.ids.PageId;

public class Button implements Serializable
{
	private Long id;

	private String color;
	private String icon;
	private String action;
	private String url;
	private Integer cell;

	private String text;
	private PageId pageId;
	private String phoneNr;

	private Button() {
		//Needed for serialization
	}
	private Button(String color)
	{
		this.color = color;
	}
	public static Button create(Long id, String color, String icon, String action, String url, Integer cell) {
		Button button = new Button(color);
		button.id = id;
		button.icon = icon;
		button.action = action;
		button.url = url;
		button.cell = cell;

		return button;
	}


	public Long getId() {
		return this.id;
	}
	public String getColor() {
		return this.color;
	}
	public String getIcon() {
		return this.icon;
	}
	public String getAction() {
		return this.action;
	}
	public String getUrl() {
		return this.url;
	}
	public Integer getCell() {
		return this.cell;
	}

	public String getText() {
		return this.text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public PageId getPageId() {
		return this.pageId;
	}
	public void setPageId(PageId pageId) {
		this.pageId = pageId;
	}

	public String getPhoneNr() {
		return this.phoneNr;
	}
	public void setPhoneNr(String phoneNr) {
		this.phoneNr = phoneNr;
	}


	/*@Override
	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;

		if (obj == null)
			return false;

		if (!(obj instanceof Button))
			return false;

		return this.id.equals(((Button)obj).id);
	}*/

}
