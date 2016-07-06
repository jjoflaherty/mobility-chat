package be.kpoint.pictochat.business.comm;

import be.kpoint.pictochat.app.domain.Client;
import be.kpoint.pictochat.app.domain.Room;
import be.kpoint.pictochat.app.domain.User;
import be.kpoint.pictochat.comm.pubnub.PubnubChannel;

public class PictoChatChannelFactory
{
	public static PubnubChannel buildTransmitChannelForClientRoom(Client client) {
		return new PubnubChannel(client.getRoom().getChannelName() + "-T");
	}
	public static PubnubChannel buildReceiveChannelForClientRoom(Client client) {
		return new PubnubChannel(client.getRoom().getChannelName() + "-R");
	}

	public static PubnubChannel buildTransmitChannelForCoaches(Client client) {
		return buildReceiveChannelForClientRoom(client);
	}
	public static PubnubChannel buildReceiveChannelForCoaches(Client client) {
		return buildTransmitChannelForClientRoom(client);
	}

	public static PubnubChannel buildPrivateTransmitChannel(User local, User remote) {
		return new PubnubChannel(Room.createForRemoteAsHost(local, remote).getChannelName());
	}
	public static PubnubChannel buildPrivateReceiveChannel(User local, User remote) {
		return new PubnubChannel(Room.createForLocalAsHost(local, remote).getChannelName());
	}

	private PictoChatChannelFactory() {}
}
