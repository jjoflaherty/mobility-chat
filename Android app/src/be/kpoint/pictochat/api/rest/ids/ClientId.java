package be.kpoint.pictochat.api.rest.ids;

import java.io.Serializable;

public class ClientId extends AbstractId implements Serializable
{
	private static final long serialVersionUID = 1867828091636701322L;


	public ClientId(long number) {
		super(number);
	}
	protected ClientId() {
		super();
	}

	@Override
	public boolean equals(Object other)
	{
		if(!(other instanceof ClientId))
			return false;

		return super.equals(other);
	}
}
