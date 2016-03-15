package be.kpoint.pictochat.business.comm.interfaces;

import java.util.List;

import be.kpoint.pictochat.app.domain.User;
import be.kpoint.pictochat.comm.pubnub.PubnubChannel;


public interface IHereNowReceivedListener {
	public void onHereNowReceived(PubnubChannel channel, int occupancy, User host, List<User> users);
}
