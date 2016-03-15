package be.kpoint.pictochat.api.rest.ids;

import java.io.Serializable;

public class PageId extends AbstractId implements Serializable
{
	private static final long serialVersionUID = -6373128365083069035L;


	public PageId(long number) {
		super(number);
	}
	protected PageId() {
		super();
	}

	@Override
	public boolean equals(Object other)
	{
		if(!(other instanceof PageId))
			return false;

		return super.equals(other);
	}
}
