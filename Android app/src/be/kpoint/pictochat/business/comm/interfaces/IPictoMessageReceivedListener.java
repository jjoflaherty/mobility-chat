package be.kpoint.pictochat.business.comm.interfaces;

import be.kpoint.pictochat.app.domain.Client;
import be.kpoint.pictochat.app.domain.Coach;
import be.kpoint.pictochat.app.domain.Friend;
import be.kpoint.pictochat.app.domain.PictoMessage;


public interface IPictoMessageReceivedListener {
	//public void onPictoMessageReceived(Client client, Coach coach, PictoMessage message);
	public void onPictoMessageReceived(Client client, PictoMessage message);
	public void onPictoMessageReceived(Coach coach, PictoMessage message);
	public void onPictoMessageReceived(Friend friend, PictoMessage message);
}
