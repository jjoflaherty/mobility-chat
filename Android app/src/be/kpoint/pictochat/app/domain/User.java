package be.kpoint.pictochat.app.domain;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable
{
	private String firstName;
	private String lastName;
	private String uuid;

	protected User(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}


	public String getFirstName() {
		return this.firstName;
	}
	public String getLastName() {
		return this.lastName;
	}

	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}

	public String getUuid() {
		return this.uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}


	public Boolean hasUuid() {
		return this.uuid != null;
	}
}
