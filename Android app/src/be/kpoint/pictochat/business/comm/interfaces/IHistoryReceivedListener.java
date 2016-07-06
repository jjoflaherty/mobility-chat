package be.kpoint.pictochat.business.comm.interfaces;

import java.util.List;

import be.kpoint.pictochat.app.domain.TextMessage;
import be.kpoint.pictochat.app.domain.User;
import be.kpoint.pictochat.comm.pubnub.PubnubChannel;


public interface IHistoryReceivedListener {
	public void onHistoryReceived(PubnubChannel channel, boolean isPrivate, User host, User user, List<TextMessage> messages);
}