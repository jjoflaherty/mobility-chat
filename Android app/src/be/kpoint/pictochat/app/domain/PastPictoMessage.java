package be.kpoint.pictochat.app.domain;

import android.text.format.Time;

public class PastPictoMessage extends PictoMessage
{
	private Time time;

	public PastPictoMessage(String text, String senderName, Boolean sent, Time time) {
		super(text, senderName, sent);

		this.time = time;
	}

	public Time getTime() {
		return this.time;
	}
}