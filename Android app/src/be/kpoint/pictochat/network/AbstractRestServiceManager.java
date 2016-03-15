package be.kpoint.pictochat.network;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import be.kpoint.pictochat.network.services.HttpGetService;
import be.kpoint.pictochat.network.services.HttpPostService;


public abstract class AbstractRestServiceManager
{
	private List<IRestServiceErrorListener> errorListeners = new ArrayList<IRestServiceErrorListener>();

	protected WeakReference<Context> weakContext;

	public AbstractRestServiceManager(final Context context) {
		this.weakContext = new WeakReference<Context>(context);
	}


	protected void get(ApiTarget target) {
		this.get(target, null, null, this.resultReceiver);
	}
	protected void get(ApiTarget target, Integer timeout) {
		this.get(target, timeout, null, this.resultReceiver);
	}
	protected void get(ApiTarget target, Bundle metadata) {
		this.get(target, null, metadata, this.resultReceiver);
	}
	protected void get(ApiTarget target, ResultReceiver receiver) {
		this.get(target, null, null, receiver);
	}
	protected void get(ApiTarget target, Integer timeout, ResultReceiver receiver) {
		this.get(target, timeout, null, receiver);
	}
	protected void get(ApiTarget target, Bundle metadata, ResultReceiver receiver) {
		this.get(target, null, metadata, receiver);
	}
	protected void get(ApiTarget target, Integer timeout, Bundle metadata, ResultReceiver receiver) {
		Context context = this.weakContext.get();
		if (context != null) {
			try {
				if (metadata == null)
					metadata = new Bundle();
				metadata.putString(HttpGetService.IntentSettings.Info.EXTRA_URL, target.toString());
				metadata.putString(HttpGetService.IntentSettings.Info.EXTRA_METHOD, "GET");

				Intent service = new Intent(context, HttpGetService.class);
				service.putExtra(HttpGetService.IntentSettings.Mandatory.EXTRA_URL, target.toString());
				service.putExtra(HttpGetService.IntentSettings.Mandatory.EXTRA_RESULTRECEIVER, receiver);

				if (timeout != null)
					service.putExtra(HttpGetService.IntentSettings.Optional.EXTRA_TIMEOUT, timeout);
				if (metadata != null)
					service.putExtra(HttpGetService.IntentSettings.Optional.EXTRA_METADATA_BUNDLE, metadata);

				Log.v("rest_receiver", "GET " + target.toString());
				context.startService(service);
			}
			catch (Exception e) {
				this.onClientError();
			}
		}
	}

	protected void get(List<ApiTarget> targets, Integer timeout, Bundle metadata, ResultReceiver receiver) {
		Context context = this.weakContext.get();
		if (context != null) {
			try {
				if (metadata == null)
					metadata = new Bundle();

				ArrayList<String> urls = new ArrayList<String>();
				for (ApiTarget target : targets)
					urls.add(target.toString());

				metadata.putStringArrayList(HttpGetService.IntentSettings.Info.EXTRA_URLS, urls);
				metadata.putString(HttpGetService.IntentSettings.Info.EXTRA_METHOD, "GET");

				Intent service = new Intent(context, HttpGetService.class);
				service.putStringArrayListExtra(HttpGetService.IntentSettings.Mandatory.EXTRA_URLS, urls);
				service.putExtra(HttpGetService.IntentSettings.Mandatory.EXTRA_RESULTRECEIVER, receiver);

				if (timeout != null)
					service.putExtra(HttpGetService.IntentSettings.Optional.EXTRA_TIMEOUT, timeout);
				if (metadata != null)
					service.putExtra(HttpGetService.IntentSettings.Optional.EXTRA_METADATA_BUNDLE, metadata);

				context.startService(service);
			}
			catch (Exception e) {
				this.onClientError();
			}
		}
	}

	protected void post(ApiTarget target) {
		this.post(target, new JSONObject(), null, null, this.resultReceiver);
	}
	protected void post(ApiTarget target, Integer timeout) {
		this.post(target, new JSONObject(), timeout, null, this.resultReceiver);
	}
	protected void post(ApiTarget target, Bundle metadata) {
		this.post(target, new JSONObject(), null, metadata, this.resultReceiver);
	}
	protected void post(ApiTarget target, JSONObject body) {
		this.post(target, body, null, null, this.resultReceiver);
	}
	protected void post(ApiTarget target, JSONObject body, Integer timeout) {
		this.post(target, body, timeout, null, this.resultReceiver);
	}
	protected void post(ApiTarget target, JSONObject body, Bundle metadata) {
		this.post(target, body, null, metadata, this.resultReceiver);
	}
	protected void post(ApiTarget target, ResultReceiver receiver) {
		this.post(target, new JSONObject(), null, null, receiver);
	}
	protected void post(ApiTarget target, Integer timeout, ResultReceiver receiver) {
		this.post(target, new JSONObject(), timeout, null, receiver);
	}
	protected void post(ApiTarget target, Bundle metadata, ResultReceiver receiver) {
		this.post(target, new JSONObject(), null, metadata, receiver);
	}
	protected void post(ApiTarget target, JSONObject body, Integer timeout, ResultReceiver receiver) {
		this.post(target, body, timeout, null, receiver);
	}
	protected void post(ApiTarget target, JSONObject body, Bundle metadata, ResultReceiver receiver) {
		this.post(target, body, null, metadata, receiver);
	}
	protected void post(ApiTarget target, JSONObject body, Integer timeout, Bundle metadata, ResultReceiver receiver) {
		Context context = this.weakContext.get();
		if (context != null) {
			try {
				if (metadata == null)
					metadata = new Bundle();
				metadata.putString(HttpGetService.IntentSettings.Info.EXTRA_URL, target.toString());
				metadata.putString(HttpGetService.IntentSettings.Info.EXTRA_METHOD, "POST");
				metadata.putString(HttpGetService.IntentSettings.Info.EXTRA_BODY, body.toString());

				Intent service = new Intent(context, HttpPostService.class);
				service.putExtra(HttpPostService.IntentSettings.Mandatory.EXTRA_URL, target.toString());
				service.putExtra(HttpPostService.IntentSettings.Mandatory.EXTRA_REQUESTBODY, body.toString());
				service.putExtra(HttpPostService.IntentSettings.Mandatory.EXTRA_RESULTRECEIVER, receiver);

				if (timeout != null)
					service.putExtra(HttpPostService.IntentSettings.Optional.EXTRA_TIMEOUT, timeout);
				if (metadata != null)
					service.putExtra(HttpPostService.IntentSettings.Optional.EXTRA_METADATA_BUNDLE, metadata);

				Log.v("rest_receiver", "POST " + target.toString());
				context.startService(service);
			}
			catch (Exception e) {
				this.onClientError();
			}
		}
	}

	protected void onComplete(JSONObject jsonObject, Bundle metadata) {
		throw new UnsupportedOperationException();
	}
	protected void onComplete(JSONArray jsonArray, Bundle metadata) {
		throw new UnsupportedOperationException();
	}
	protected void onTimedOut() {}
	protected void onFinished() {}


	//Result receiver
	private AbstractRestResultReceiver resultReceiver = new AbstractRestResultReceiver("internal")
	{
		@Override
		public void onServerError(String json) {
			AbstractRestServiceManager.this.onServerError(json);
		}

		@Override
		public void onClientError() {
			AbstractRestServiceManager.this.onClientError();
		}

		@Override
		public void onTimedOut() {
			AbstractRestServiceManager.this.onTimedOut();
		}

		@Override
		public void onFinished() {
			AbstractRestServiceManager.this.onFinished();
		}

		@Override
		protected void onComplete(JSONArray jsonArray, Bundle metadata) {
			AbstractRestServiceManager.this.onComplete(jsonArray, metadata);
		}

		@Override
		protected void onComplete(JSONObject jsonObject, Bundle metadata) {
			AbstractRestServiceManager.this.onComplete(jsonObject, metadata);
		}

		@Override
		protected void onAfterPostProcessing() {}
	};


	//Listener delegation
	protected void onClientError() {
		Log.wtf("rst_svc", "Request failed in " + this.getClass().getCanonicalName());

		for (IRestServiceErrorListener listener : this.errorListeners)
			try {
				listener.onClientError();
			} catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), e.toString());
			}
	}
	protected void onServerError(String json) {
		Log.wtf("rst_svc", "Request returned an invalid response in " + this.getClass().getCanonicalName());

		for (IRestServiceErrorListener listener : this.errorListeners)
			try {
				listener.onServerError(json);
			} catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), e.toString());
			}
	}

	//Listener management
	public void addRestServiceErrorListener(IRestServiceErrorListener listener) {
  		this.removeRestServiceErrorListener(listener);
  		this.errorListeners.add(listener);
  	}
  	public void removeRestServiceErrorListener(IRestServiceErrorListener listener) {
  		this.errorListeners.remove(listener);
  	}

	//Listeners
	public interface IRestServiceListener {
		public void onFinished();
		public void onTimedOut();
	}
	public interface IRestServiceErrorListener {
		public void onClientError();
		public void onServerError(String json);
	}
}
