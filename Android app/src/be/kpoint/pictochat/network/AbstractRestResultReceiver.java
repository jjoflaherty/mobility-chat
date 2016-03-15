package be.kpoint.pictochat.network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import be.kpoint.pictochat.network.AbstractRestServiceManager.IRestServiceErrorListener;
import be.kpoint.pictochat.network.AbstractRestServiceManager.IRestServiceListener;
import be.kpoint.pictochat.network.services.HttpGetService;


public abstract class AbstractRestResultReceiver extends ResultReceiver implements IRestServiceListener, IRestServiceErrorListener
{
	private static final String tag = "rest_receiver";
	private String name;

	private List<IFinishedListener> finishedListeners = new ArrayList<IFinishedListener>();
	private List<IAsyncPostProcessor> postProcessors = new ArrayList<IAsyncPostProcessor>();
	private AtomicInteger counter = new AtomicInteger();

 	public AbstractRestResultReceiver(String name) {
		super(new Handler()); //Run on calling thread

		this.name = name;
	}

 	public void addPostProcessor(IAsyncPostProcessor processor) {
		processor.addFinishedListener(this.finishedListener);

		this.postProcessors.remove(processor);
		this.postProcessors.add(processor);
	}


	@Override
	protected final void onReceiveResult(int resultCode, Bundle resultData) {
		boolean call = true;

		switch (resultCode) {
			case HttpGetService.MSG_SUCCESS:
				String result = resultData.getString(HttpGetService.RESULT_STRING);
				if (result == null)
					result = "";

				Bundle metadata = new Bundle();
				if (resultData.containsKey(HttpGetService.IntentSettings.Optional.EXTRA_METADATA_BUNDLE))
					metadata = resultData.getBundle(HttpGetService.IntentSettings.Optional.EXTRA_METADATA_BUNDLE);

				try {
					JSONArray jsonArray = new JSONArray(result);
					this.onComplete(jsonArray, metadata);
				} catch (JSONException e) {
					try {
						JSONObject jsonObject = new JSONObject(result);
						this.runOnComplete(jsonObject, metadata);
						call = false;
					} catch (JSONException ex) {
						this.onServerError(result);
					}
				}

				break;

			case HttpGetService.MSG_ERROR:
				this.onServerError(resultData.getString(HttpGetService.RESULT_STRING));

				break;

			case HttpGetService.MSG_TIMEOUT:
				this.onTimedOut();

				break;
		}

		if (call)
			this.runOnAfterPostProcessing(false);
	}

	protected void onAllRequestsCompleted() {}

	/**
	 * Override this method with your own <b>finalized</b> implementation to parse the provided jsonObject and store the result in your receiver class.
	 * There is no need to call the base implementation of this method.
	 * @param jsonObject : The result returned by the http get/post intent service
	 * @param metadata : Data supplied by the caller of the http get/post intent service
	 */
	protected void onComplete(JSONObject jsonObject, Bundle metadata) {}
	/**
	 * Override this method with your own <b>finalized</b> implementation to parse the provided jsonArray and store the result in your receiver class.
	 * There is no need to call the base implementation of this method.
	 * @param jsonArray : The result returned by the http get/post intent service
	 * @param metadata : Data supplied by the caller of the http get/post intent service
	 */
	protected void onComplete(JSONArray jsonArray, Bundle metadata) {}

	private void runOnComplete(JSONObject jsonObject, Bundle metadata) {
		writeLog("Running onComplete");

		onComplete(jsonObject, metadata);
		runPostProcessing();
	}

	/**
	 * This function will be called after all post processors have completed.
	 * Use this function to return the object stored in {@link #onComplete(JSONObject, Bundle)} as a parameter in a specialized function.
	 */
	protected void onAfterPostProcessing() {}
	private void runOnAfterPostProcessing(boolean success) {
		writeLog("Running onAfterPostProcessing");

		onAllRequestsCompleted();

		if (success)
			onAfterPostProcessing();

		runOnFinished();
	}


	private void runPostProcessing() {
		writeLog("Starting post-processing");

		if (this.postProcessors.isEmpty()) {
			writeLog("No post-processors attached");
			runOnAfterPostProcessing(true);
		} else {
			writeLog(this.postProcessors.size() + " post-processors attached");
			this.counter.set(0);
			for (IAsyncPostProcessor processor : this.postProcessors) {
				try {
					if (!processor.doPostProcessing())
						break;
				} catch (Exception e) {
					break;
				}
			}
		}
	}


	//Local listener
	private IFinishedListener finishedListener = new IFinishedListener()
	{
		@Override
		public void onFinished() {
			writeLog("Post-processor returned");
			if (AbstractRestResultReceiver.this.counter.incrementAndGet() == AbstractRestResultReceiver.this.postProcessors.size())
				runOnAfterPostProcessing(true);
		}
	};


	//Listener delegation
	@Override
	public void onFinished() {}
	private void runOnFinished() {
		writeLog("Calling finishedListeners");
		for (IFinishedListener listener : this.finishedListeners)
			try {
				listener.onFinished();
			} catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), e.toString());
			}

		onFinished();
	}

	//Listener management
	public void addFinishedListener(IFinishedListener listener) {
		this.removeFinishedListener(listener);
		this.finishedListeners.add(listener);
	}
	public void removeFinishedListener(IFinishedListener listener) {
		this.finishedListeners.remove(listener);
	}

	//Logging
	private void writeLog(String text) {
		Log.d(tag, this.name + ": " + text);
	}
}
