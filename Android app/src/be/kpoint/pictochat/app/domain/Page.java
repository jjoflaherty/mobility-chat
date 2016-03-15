package be.kpoint.pictochat.app.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import be.kpoint.pictochat.api.rest.ids.PageId;

public class Page implements Serializable
{
	private PageId id;
	private String name;
	private Integer rows;
	private Integer columns;
	private List<Button> buttons = new ArrayList<Button>();

	private Page(PageId id, String name) {
		this.id = id;
		this.name = name;
	}

	public static Page buildFromRest(be.kpoint.pictochat.api.rest.page.Page page) {
		Page p = new Page(page.getId(), page.getName());
		p.rows = page.getRows();
		p.columns = page.getColumns();

		for (be.kpoint.pictochat.api.rest.button.Button button : page.getButtons()) {
			Button b = Button.buildFromRest(button);

			p.addButton(b);
		}

		return p;
	}


	public PageId getId() {
		return this.id;
	}
	public String getName() {
		return this.name;
	}


	public Integer getRows() {
		return this.rows;
	}
	public Integer getColumns() {
		return this.columns;
	}

	public List<Button> getButtons() {
		return this.buttons;
	}

	public void addButton(Button button) {
		this.buttons.add(button);
	}
}
