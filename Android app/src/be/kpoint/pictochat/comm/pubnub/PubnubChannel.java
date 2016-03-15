package be.kpoint.pictochat.comm.pubnub;

import java.io.Serializable;

public class PubnubChannel implements Serializable {
	private static final long serialVersionUID = -1141894298906031854L;


	private String name;

	public PubnubChannel(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return this.getName();
	}


	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof PubnubChannel))
			return false;

		return this.name.equals(((PubnubChannel)obj).name);
	}
}