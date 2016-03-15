package be.kpoint.pictochat.api.rest.ids;

import java.io.Serializable;

public abstract class AbstractId implements Serializable
{
	private static final long serialVersionUID = 7923385628329263223L;


	private long number;

	protected AbstractId(long number) {
	    this.number = number;
	}
	protected AbstractId() {}

	public long getNumber() {
	    return this.number;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (super.equals(obj))
			return true;

		if (obj == null)
			return false;

		if (!(obj instanceof AbstractId))
			return false;

		return this.getNumber() == ((AbstractId)obj).getNumber();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (this.number ^ (this.number >>> 32));
		return result;
	}

	@Override
	public String toString() {
	    return "" + this.number;
	}
}
