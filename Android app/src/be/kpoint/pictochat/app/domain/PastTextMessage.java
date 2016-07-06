package be.kpoint.pictochat.app.domain;

import java.util.Date;

public class PastTextMessage extends TextMessage
{
	private Date time;

	public PastTextMessage(String text, String senderName, Boolean sent, Date time) {
		super(text, senderName, sent);

		this.time = time;
	}

	public Date getTime() {
		return this.time;
	}
}
