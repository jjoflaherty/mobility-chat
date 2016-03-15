package be.kpoint.pictochat.app.domain;

import android.text.format.Time;

public class PastTextMessage extends TextMessage
{
	private Time time;

	public PastTextMessage(String text, String senderName, Boolean sent, Time time) {
		super(text, senderName, sent);

		this.time = time;
	}

	public Time getTime() {
		return this.time;
	}
}
