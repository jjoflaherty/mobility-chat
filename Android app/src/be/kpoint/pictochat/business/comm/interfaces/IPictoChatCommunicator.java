package be.kpoint.pictochat.business.comm.interfaces;

import java.util.Date;

import be.kpoint.pictochat.app.domain.Client;
import be.kpoint.pictochat.app.domain.User;

public interface IPictoChatCommunicator
{
	public void hostRoom(Client client);
	public void closeRoom(Client client);

	public void joinRoom(Client client);
	public void leaveRoom(Client client);

	public void hostRoom(User local, User remote);
	public void closeRoom(User local, User remote);

	public void historyReceived(Client client, int amount);
	public void historySent(Client client, int amount);
	public void historyReceived(Client client, Date start);
	public void historySent(Client client, Date start);

	public void historyReceived(User local, User remote, int amount);
	public void historySent(User local, User remote, int amount);
	public void historyReceived(User local, User remote, Date start);
	public void historySent(User local, User remote, Date start);

	public void clientsHereNow(Client client);
	public void usersHereNow(User local, User remote);
	public void coachesHereNow(Client client);
	public void clientsPresence(Client client);
	public void usersPresence(User local, User remote);
	public void coachesPresence(Client client);

	public void addTextMessageReceivedListener(ITextMessageReceivedListener listener);
	public void removeTextMessageReceivedListener(ITextMessageReceivedListener listener);

	public void addPictoMessageReceivedListener(IPictoMessageReceivedListener listener);
	public void removePictoMessageReceivedListener(IPictoMessageReceivedListener listener);

	public void addHereNowReceivedListener(IHereNowReceivedListener listener);
	public void removeHereNowReceivedListener(IHereNowReceivedListener listener);

	public void addPresenceReceivedListener(IPresenceReceivedListener listener);
	public void removePresenceReceivedListener(IPresenceReceivedListener listener);

	public void addHistoryReceivedListener(IHistoryReceivedListener listener);
	public void removeHistoryReceivedListener(IHistoryReceivedListener listener);
}