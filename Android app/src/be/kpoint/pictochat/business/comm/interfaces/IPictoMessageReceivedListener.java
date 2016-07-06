package be.kpoint.pictochat.business.comm.interfaces;

import be.kpoint.pictochat.app.domain.Client;
import be.kpoint.pictochat.app.domain.Coach;
import be.kpoint.pictochat.app.domain.PictoMessage;
import be.kpoint.pictochat.app.domain.User;


public interface IPictoMessageReceivedListener {
	public void onPictoMessageReceived(Client client, PictoMessage message);
	public void onPictoMessageReceived(Coach coach, Client client, PictoMessage message);
	public void onPictoMessageReceived(User remote, PictoMessage message);
}
