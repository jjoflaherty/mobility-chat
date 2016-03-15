package be.kpoint.pictochat.api.rest.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import be.kpoint.pictochat.api.rest.button.Button;
import be.kpoint.pictochat.api.rest.ids.PageId;

public class Page implements Serializable
{
	private PageId id;
	private String name;
	private Integer rows = 4;
	private Integer columns = 3;
	private List<Button> buttons = new ArrayList<Button>();

	private Page() {
		//Needed for serialization
	}
	private Page(long id)
	{
		this.id = new PageId(id);
	}
	public static Page create(long id, String name) {
		Page page = new Page(id);
		page.name = name;

		return page;
	}

	public PageId getId()
	{
		return this.id;
	}


	protected void addButton(Button button) {
		this.buttons.add(button);
	}

	public List<Button> getButtons() {
		return this.buttons;
	}


	public String getName()
	{
		return this.name;
	}
	public Integer getRows() {
		return this.rows;
	}
	public Integer getColumns() {
		return this.columns;
	}

	protected void setRows(Integer rows) {
		this.rows = rows;
	}
	protected void setColumns(Integer columns) {
		this.columns = columns;
	}


	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;

		if (obj == null)
			return false;

		if (!(obj instanceof Page))
			return false;

		return this.id.equals(((Page)obj).id);
	}
}
