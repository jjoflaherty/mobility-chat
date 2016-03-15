package be.kpoint.pictochat.api.rest.ids;

import java.io.Serializable;

public class CoachId extends AbstractId implements Serializable
{
	private static final long serialVersionUID = -5837695670418211567L;


	public CoachId(long number) {
		super(number);
	}
	protected CoachId() {
		super();
	}

	@Override
	public boolean equals(Object other)
	{
		if(!(other instanceof CoachId))
			return false;

		return super.equals(other);
	}
}
