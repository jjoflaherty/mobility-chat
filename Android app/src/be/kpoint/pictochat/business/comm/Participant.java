package be.kpoint.pictochat.business.comm;
import be.kpoint.pictochat.app.domain.User;
import be.kpoint.pictochat.business.comm.enums.AppState;


public class Participant
{
	private User user;
	private AppState state;

	public Participant(User user, AppState state) {
		this.user = user;
		this.state = state;
	}

	public User getUser() {
		return this.user;
	}
	public AppState getState() {
		return this.state;
	}
}
