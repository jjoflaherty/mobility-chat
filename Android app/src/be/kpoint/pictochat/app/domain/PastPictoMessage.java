package be.kpoint.pictochat.app.domain;

import java.util.Date;


public class PastPictoMessage extends PictoMessage
{
	private Date time;

	public PastPictoMessage(String text, String senderName, Boolean sent, Date time) {
		super(text, senderName, sent);

		this.time = time;
	}

	public Date getTime() {
		return this.time;
	}
}