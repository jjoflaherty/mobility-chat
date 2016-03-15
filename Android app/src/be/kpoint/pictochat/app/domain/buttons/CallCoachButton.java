package be.kpoint.pictochat.app.domain.buttons;

import android.util.Log;

import be.kpoint.pictochat.app.domain.Button;


public class CallCoachButton extends Button
{
	private ICallCoachButtonClickedListener callCoachButtonClickedListener;

	private String phoneNr;

	public CallCoachButton(Long id, String color, String icon, String url, Integer cell, String phoneNr) {
		super(id, color, icon, url, cell);

		this.phoneNr = phoneNr;
	}

	public static CallCoachButton buildFromRest(be.kpoint.pictochat.api.rest.button.Button button) {
		CallCoachButton c = new CallCoachButton(button.getId(), button.getColor(), button.getIcon(), button.getUrl(), button.getCell(), button.getPhoneNr());

		return c;
	}


	public String getPhoneNr() {
		return this.phoneNr;
	}


	@Override
	public void doAction() {
		onCallCoachButtonClicked();
	}

	//Listener delegation
	private void onCallCoachButtonClicked() {
		try {
			if (this.callCoachButtonClickedListener != null)
				this.callCoachButtonClickedListener.onClick(this.phoneNr);
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.toString());
		}
	}

	//Listener management
	public void setCallCoachButtonClickedListener(ICallCoachButtonClickedListener listener) {
		this.callCoachButtonClickedListener = listener;
	}

	//Listeners
	public interface ICallCoachButtonClickedListener {
		public void onClick(String phoneNr);
	}
}
