package be.kpoint.pictochat.network;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;


public abstract class BasicAsyncPostProcessor implements IAsyncPostProcessor
{
	private List<IFinishedListener> finishedListeners = new ArrayList<IFinishedListener>();

	//Listener delegation
	public void onFinished() {
		for (IFinishedListener listener : this.finishedListeners)
			try {
				listener.onFinished();
			} catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), e.toString());
			}
	}

	//Listener management
	@Override
	public void addFinishedListener(IFinishedListener listener) {
		this.removeFinishedListener(listener);
		this.finishedListeners.add(listener);
	}
	@Override
	public void removeFinishedListener(IFinishedListener listener) {
		this.finishedListeners.remove(listener);
	}
}
