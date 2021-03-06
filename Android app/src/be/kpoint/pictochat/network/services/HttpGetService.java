package be.kpoint.pictochat.network.services;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import be.kpoint.pictochat.network.RestCache;

public class HttpGetService extends IntentService
{
	public static int connectionTimeout = 10000;
	public static int socketTimeout = 60000;

	public static final int MSG_SUCCESS = 1;
	public static final int MSG_TIMEOUT = 2;
	public static final int MSG_ERROR = 3;

	public static final String RESULT_STRING = "result";

	public class IntentSettings
	{
		public class Optional {
			public static final String EXTRA_TIMEOUT = "timeout"; // MAY be set
			public static final String EXTRA_METADATA_BUNDLE = "metadata"; // MAY be set to identify the request, will be returned in response bundle
		}
		public class Mandatory {
			public static final String EXTRA_URL = "url";
			public static final String EXTRA_URLS = "urls";
			public static final String EXTRA_CACHE = "cache";
			public static final String EXTRA_RESULTRECEIVER = "resultreceiver"; // MUST be set -> class that handles the result (MUST be inherited from ResultReceiver)
		}
		public class Info {
			public static final String EXTRA_URL = "url";
			public static final String EXTRA_URLS = "urls";
			public static final String EXTRA_METHOD = "method";
		}
	}

	public HttpGetService() {
		super("HttpGetService");
	}

	/**
	 * content of intent (all values defined in extras)
	 * MUST NOT throw
	 */
	@Override
	protected void onHandleIntent(Intent intent)
	{
		try
		{
			if (!sanityCheck(intent))
				return;

			// 0 means infinite wait!  Only use it when you know what you are doing!
			if (intent.getExtras().get(IntentSettings.Optional.EXTRA_TIMEOUT) != null)
				socketTimeout = intent.getExtras().getInt(IntentSettings.Optional.EXTRA_TIMEOUT);

			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, connectionTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, socketTimeout);
			HttpProtocolParams.setUseExpectContinue(httpParameters, false);
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);

			if (intent.hasExtra(IntentSettings.Mandatory.EXTRA_URL)) {
				String requestUrl = intent.getExtras().getString(IntentSettings.Mandatory.EXTRA_URL);
				boolean allowCache = intent.getExtras().getBoolean(IntentSettings.Mandatory.EXTRA_CACHE, false);

				processSingleRequest(intent, requestUrl, allowCache, httpClient);
			}
			else if (intent.hasExtra(IntentSettings.Mandatory.EXTRA_URLS)) {
				List<String> requestUrls = intent.getStringArrayListExtra(IntentSettings.Mandatory.EXTRA_URLS);
				processRequestList(intent, requestUrls, httpClient);
			}
		} catch (Exception e) {
			sendResponse(intent, e.toString(), MSG_ERROR);
		}
	}

	private void processSingleRequest(Intent intent, String requestUrl, boolean allowCache, DefaultHttpClient httpClient) {
		ResponseHandler<String> responseHandler = new BasicResponseHandler();

		try {
			String response = doRequest(requestUrl, httpClient, responseHandler);

			if (allowCache) {
				RestCache cache = new RestCache(this);
				cache.addResult(requestUrl, response);
			}

			sendResponse(intent, response, MSG_SUCCESS);
			return;
		} catch (SocketTimeoutException e) {
			sendResponse(intent, "Socket timed out", MSG_TIMEOUT);
		} catch (ConnectTimeoutException e) {
			sendResponse(intent, "Connection timed out", MSG_TIMEOUT);
		} catch (ClientProtocolException e) {
			sendResponse(intent, e.toString(), MSG_ERROR);
		} catch (IOException e) {
			sendResponse(intent, e.toString(), MSG_ERROR);
		} catch (Exception e) {
			sendResponse(intent, e.toString(), MSG_ERROR);
		}

		if (allowCache) {
			RestCache cache = new RestCache(this);
			String response = cache.getCachedResult(requestUrl);
			if (response != null)
				sendResponse(intent, response, MSG_SUCCESS);
		}
	}
	private void processRequestList(Intent intent, List<String> requestUrls, DefaultHttpClient httpClient) {
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		JSONArray jsonArray = new JSONArray();

		try {
			for (String requestUrl : requestUrls) {
				String response = doRequest(requestUrl, httpClient, responseHandler);

				try {
					jsonArray.put(new JSONObject(response));
				} catch (JSONException ex) {
					//TODO Add handling here
				}
			}
			sendResponse(intent, jsonArray.toString(), MSG_SUCCESS);
		} catch (SocketTimeoutException e) {
			sendResponse(intent, "Socket timed out", MSG_TIMEOUT);
		} catch (ConnectTimeoutException e) {
			sendResponse(intent, "Connection timed out", MSG_TIMEOUT);
		} catch (ClientProtocolException e) {
			sendResponse(intent, e.toString(), MSG_ERROR);
		} catch (IOException e) {
			sendResponse(intent, e.toString(), MSG_ERROR);
		} catch (Exception e) {
			sendResponse(intent, e.toString(), MSG_ERROR);
		}
	}
	private String doRequest(String requestUrl, DefaultHttpClient httpClient, ResponseHandler<String> responseHandler) throws SocketTimeoutException, ConnectTimeoutException, ClientProtocolException, IOException {
		HttpGet get = new HttpGet(requestUrl);
		return httpClient.execute(get, responseHandler);
	}

	/**
	 * send response through the ResultReceiver
	 * may NOT throw!
	 * @param intent
	 */
	private void sendResponse(Intent intent, String response, int action)
	{
		try {
			Bundle res = new Bundle();
			res.putString(RESULT_STRING, response);

			if(intent.getExtras().containsKey(IntentSettings.Optional.EXTRA_METADATA_BUNDLE))
				res.putBundle(IntentSettings.Optional.EXTRA_METADATA_BUNDLE, intent.getExtras().getBundle(IntentSettings.Optional.EXTRA_METADATA_BUNDLE));

			ResultReceiver resultReceiver = (ResultReceiver)intent.getExtras().get(IntentSettings.Mandatory.EXTRA_RESULTRECEIVER);
			resultReceiver.send(action, res);
		} catch (Exception e) {
			//AppManagerSingleton.getLogManager().add(CLASSNAME, "sendResponse",
			//		"Sending response failed: " + e.getMessage(), LogLevel.ERROR);
			// log it! -> we cannot contact back the caller to signal the failure!
		}
	}

	private boolean sanityCheck(Intent intent) {
		Bundle bundle = intent.getExtras();
		if (bundle == null) {
			//AppManagerSingleton.getLogManager().add(CLASSNAME, "onHandleIntent", "Failure: no extras given in intent!", LogLevel.ERROR);
			return false;
		}

		// URL or URLS and RESULTRECEIVER MUST be set
		if (!(bundle.containsKey(IntentSettings.Mandatory.EXTRA_URL) && bundle.get(IntentSettings.Mandatory.EXTRA_URL) instanceof String) &&
			!(bundle.containsKey(IntentSettings.Mandatory.EXTRA_URLS) && bundle.get(IntentSettings.Mandatory.EXTRA_URLS) instanceof List<?>))
		{
			//AppManagerSingleton.getLogManager().add(CLASSNAME, "onHandleIntent", "Failure: no URL given in intent!", LogLevel.ERROR);
			sendResponse(intent, "FAILED: " + IntentSettings.Mandatory.EXTRA_URL + " and " + IntentSettings.Mandatory.EXTRA_URLS + " not set!", MSG_ERROR);
			return false;
		}
		else if (!(bundle.containsKey(IntentSettings.Mandatory.EXTRA_RESULTRECEIVER) && bundle.get(IntentSettings.Mandatory.EXTRA_RESULTRECEIVER) instanceof ResultReceiver))
		{
			//AppManagerSingleton.getLogManager().add(CLASSNAME, "onHandleIntent", "Failure: no RESULTRECEIVER given in intent!", LogLevel.ERROR);
			sendResponse(intent, "FAILED: " + IntentSettings.Mandatory.EXTRA_RESULTRECEIVER + " not found!", MSG_ERROR);
			return false;
		}

		return true;
	}
}
