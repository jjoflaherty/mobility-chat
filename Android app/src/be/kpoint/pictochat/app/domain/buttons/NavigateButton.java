package be.kpoint.pictochat.app.domain.buttons;

import android.util.Log;

import be.kpoint.pictochat.api.rest.ids.PageId;
import be.kpoint.pictochat.app.domain.Button;


public class NavigateButton extends Button
{
	private INavigateButtonClickedListener navigateButtonClickedListener;

	private PageId pageId;

	public NavigateButton(Long id, String color, String icon, String url, Integer cell, PageId pageId) {
		super(id, color, icon, url, cell);

		this.pageId = pageId;
	}

	public static NavigateButton buildFromRest(be.kpoint.pictochat.api.rest.button.Button button) {
		NavigateButton c = new NavigateButton(button.getId(), button.getColor(), button.getIcon(), button.getUrl(), button.getCell(), button.getPageId());

		return c;
	}


	public PageId getPageId() {
		return this.pageId;
	}


	@Override
	public void doAction() {
		onNavigateButtonClicked();
	}

	//Listener delegation
	private void onNavigateButtonClicked() {
		try {
			if (this.navigateButtonClickedListener != null)
				this.navigateButtonClickedListener.onClick(this.pageId);
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), e.toString());
		}
	}

	//Listener management
	public void setNavigateButtonClickedListener(INavigateButtonClickedListener listener) {
		this.navigateButtonClickedListener = listener;
	}

	//Listeners
	public interface INavigateButtonClickedListener {
		public void onClick(PageId pageId);
	}
}
