package be.kpoint.pictochat.business.comm.interfaces;

import java.util.Date;

import be.kpoint.pictochat.app.domain.User;
import be.kpoint.pictochat.business.comm.enums.AppState;
import be.kpoint.pictochat.business.comm.enums.Presence;
import be.kpoint.pictochat.comm.pubnub.PubnubChannel;


public interface IPresenceReceivedListener {
	public void onPresenceReceived(PubnubChannel channel, boolean isPrivate, User host, User user, Presence presence, AppState state, Date lastRead);
}