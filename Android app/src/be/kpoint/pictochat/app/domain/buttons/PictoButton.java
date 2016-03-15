package be.kpoint.pictochat.app.domain.buttons;

import android.util.Log;

import be.kpoint.pictochat.app.domain.Button;
import be.kpoint.pictochat.app.domain.Picto;


public class PictoButton extends Button
{
	private IPictoButtonClickedListener pictoButtonClickedListener;

	private Picto picto;

	public PictoButton(Long id, String color, String icon, String url, Integer cell, String text) {
		super(id, color, icon, url, cell);

		this.picto = new Picto(icon, text, url);
	}

	public static PictoButton buildFromRest(be.kpoint.pictochat.api.rest.button.Button button) {
		PictoButton c = new PictoButton(button.getId(), button.getColor(), button.getIcon(), button.getUrl(), button.getCell(), button.getText());

		return c;
	}


	public Picto getPicto() {
		return this.picto;
	}


	@Override
	public void doAction() {
		onTextButtonClicked();
	}

	//Listener delegation
	private void onTextButtonClicked() {
		try {
			if (this.pictoButtonClickedListener != null)
				this.pictoButtonClickedListener.onClick(this.picto);
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.toString());
		}
	}

	//Listener management
	public void setPictoButtonClickedListener(IPictoButtonClickedListener listener) {
		this.pictoButtonClickedListener = listener;
	}

	//Listeners
	public interface IPictoButtonClickedListener {
		public void onClick(Picto picto);
	}
}
