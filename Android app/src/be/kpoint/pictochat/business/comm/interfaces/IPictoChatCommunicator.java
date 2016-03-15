package be.kpoint.pictochat.business.comm.interfaces;

import be.kpoint.pictochat.app.domain.Client;
import be.kpoint.pictochat.app.domain.Friend;

public interface IPictoChatCommunicator
{
	public void hostRoom(Client client);
	public void closeRoom(Client client);

	public void joinRoom(Client client);
	public void leaveRoom(Client client);

	public void hostRoom(Client client, Friend friend);
	public void closeRoom(Client client, Friend friend);

	public void historyReceived(Client client, int amount);
	public void historySent(Client client, int amount);

	public void historyReceived(Client client, Friend friend, int amount);
	public void historySent(Client client, Friend friend, int amount);

	public void clientsHereNow(Client client);
	public void friendsHereNow(Client client, Friend friend);
	public void coachesHereNow(Client client);
	public void clientsPresence(Client client);
	public void friendsPresence(Client client, Friend friend);
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