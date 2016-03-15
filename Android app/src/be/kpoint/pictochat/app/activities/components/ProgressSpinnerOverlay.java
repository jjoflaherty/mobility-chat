package be.kpoint.pictochat.app.activities.components;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import be.kpoint.pictochat.app.R;

public class ProgressSpinnerOverlay
{
	private LinearLayout lytProgress;
	private TextView lytProgressText;

	public ProgressSpinnerOverlay(Activity activity) {
		this.lytProgress = (LinearLayout)activity.findViewById(R.id.lytProgress);
	    this.lytProgressText = (TextView)activity.findViewById(R.id.lytProgressText);
	}

	public void showWaitSpinner(String text) {
		this.lytProgress.setVisibility(View.VISIBLE);
    	this.lytProgressText.setText(text);
	}
	public void hideWaitSpinner() {
		this.lytProgress.setVisibility(View.GONE);
	}

	public interface IProgressSpinnerOverlay {
		public void showWaitSpinner(String text);
		public void hideWaitSpinner();
	}
}
