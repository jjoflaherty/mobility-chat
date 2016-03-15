package be.kpoint.pictochat.business.comm.interfaces;

import be.kpoint.pictochat.app.domain.User;
import be.kpoint.pictochat.business.comm.enums.Presence;


public interface IPresenceReceivedListener {
	public void onPresenceReceived(User host, User user, Presence presence);
}
