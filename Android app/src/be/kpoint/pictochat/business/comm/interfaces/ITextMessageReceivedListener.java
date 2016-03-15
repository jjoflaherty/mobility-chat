package be.kpoint.pictochat.business.comm.interfaces;

import be.kpoint.pictochat.app.domain.TextMessage;


public interface ITextMessageReceivedListener {
	public void onTextMessageReceived(TextMessage message);
}
