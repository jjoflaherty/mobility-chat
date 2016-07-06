package be.kpoint.pictochat.business.comm.interfaces;

import java.util.List;

import be.kpoint.pictochat.app.domain.User;
import be.kpoint.pictochat.business.comm.Participant;
import be.kpoint.pictochat.comm.pubnub.PubnubChannel;


public interface IHereNowReceivedListener {
	public void onHereNowReceived(PubnubChannel channel, boolean isPrivate, User host, List<Participant> users, int occupancy);
}