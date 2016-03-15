package be.kpoint.pictochat.business.comm;

import be.kpoint.pictochat.app.domain.Client;
import be.kpoint.pictochat.app.domain.Friend;
import be.kpoint.pictochat.app.domain.Room;
import be.kpoint.pictochat.comm.pubnub.PubnubChannel;

public class PictoChatChannelFactory
{
	public static PubnubChannel buildTransmitChannelForClient(Client client) {
		return new PubnubChannel(client.getRoom().getChannelName() + "-T");
	}
	public static PubnubChannel buildReceiveChannelForClient(Client client) {
		return new PubnubChannel(client.getRoom().getChannelName() + "-R");
	}

	public static PubnubChannel buildTransmitChannelForCoach(Client client) {
		return buildReceiveChannelForClient(client);
	}
	public static PubnubChannel buildReceiveChannelForCoach(Client client) {
		return buildTransmitChannelForClient(client);
	}

	public static PubnubChannel buildTransmitChannelForClientAndFriend(Client client, Friend friend) {
		return new PubnubChannel(Room.createForFriendAsHost(client, friend).getChannelName());
	}
	public static PubnubChannel buildReceiveChannelForClientAndFriend(Client client, Friend friend) {
		return new PubnubChannel(Room.createForClientAsHost(client, friend).getChannelName());
	}

	private PictoChatChannelFactory() {}
}
