package be.kpoint.pictochat.app.domain;

import java.util.Date;


public class TextMessage
{
	private String text;
	private String uuid;
	private String senderAppTime;
	private Date serverTime;
	private String senderName;
	private String senderImageUrl;
	private Boolean sent;

	public TextMessage(String text, String senderName, Boolean sent) {
		this.text = text;
		this.senderName = senderName;
		this.sent = sent;
	}

	public String getText() {
		return this.text;
	}
	public String getSenderName() {
		return this.senderName;
	}
	public Boolean getSent() {
		return this.sent;
	}

	public String getUuid() {
		return this.uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSenderAppTime() {
		return this.senderAppTime;
	}
	public void setSenderAppTime(String senderAppTime) {
		this.senderAppTime = senderAppTime;
	}

	public Date getServerTime() {
		return this.serverTime;
	}
	public void setServerTime(Date serverTime) {
		this.serverTime = serverTime;
	}

	public String getSenderImageUrl() {
		return this.senderImageUrl;
	}
	public void setSenderImageUrl(String senderImageUrl) {
		this.senderImageUrl = senderImageUrl;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.senderAppTime == null) ? 0 : this.senderAppTime.hashCode());
		result = prime * result + ((this.text == null) ? 0 : this.text.hashCode());
		result = prime * result + ((this.uuid == null) ? 0 : this.uuid.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;

		if (obj == null)
			return false;

		if (!(obj instanceof TextMessage))
			return false;

		TextMessage other = (TextMessage)obj;
		if (this.senderAppTime == null) {
			if (other.senderAppTime != null)
				return false;
		}
		else if (!this.senderAppTime.equals(other.senderAppTime))
			return false;

		if (this.text == null) {
			if (other.text != null)
				return false;
		}
		else if (!this.text.equals(other.text))
			return false;

		if (this.uuid == null) {
			if (other.uuid != null)
				return false;
		}
		else if (!this.uuid.equals(other.uuid))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return this.uuid.substring(0, 4) + " - " + this.senderAppTime + " - " + this.text;
	}
}
