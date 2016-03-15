package be.kpoint.pictochat.app.domain.buttons;

import android.util.Log;

import be.kpoint.pictochat.api.rest.ids.PageId;
import be.kpoint.pictochat.app.domain.Button;
import be.kpoint.pictochat.app.domain.Picto;


public class NavigateAndPictoButton extends Button
{
	private INavigateAndPictoButtonClickedListener navigateAndPictoButtonClickedListener;

	private PageId pageId;
	private Picto picto;

	public NavigateAndPictoButton(Long id, String color, String icon, String url, Integer cell, String text, PageId pageId) {
		super(id, color, icon, url, cell);

		this.picto = new Picto(icon, text, url);
		this.pageId = pageId;
	}

	public static NavigateAndPictoButton buildFromRest(be.kpoint.pictochat.api.rest.button.Button button) {
		NavigateAndPictoButton c = new NavigateAndPictoButton(button.getId(), button.getColor(), button.getIcon(), button.getUrl(), button.getCell(), button.getText(), button.getPageId());

		return c;
	}


	public PageId getPageId() {
		return this.pageId;
	}
	public Picto getPicto() {
		return this.picto;
	}


	@Override
	public void doAction() {
		onNavigateButtonClicked();
	}

	//Listener delegation
	private void onNavigateButtonClicked() {
		try {
			if (this.navigateAndPictoButtonClickedListener != null)
				this.navigateAndPictoButtonClickedListener.onClick(this.picto, this.pageId);
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.toString());
		}
	}

	//Listener management
	public void setNavigateAndPictoButtonClickedListener(INavigateAndPictoButtonClickedListener listener) {
		this.navigateAndPictoButtonClickedListener = listener;
	}

	//Listeners
	public interface INavigateAndPictoButtonClickedListener {
		public void onClick(Picto picto, PageId pageId);
	}
}
