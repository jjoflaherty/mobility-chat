package be.kpoint.pictochat.network.services;

import java.io.IOException;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

public class HttpPostService extends IntentService
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
			public static final String EXTRA_URL = "url"; // MUST be set
			public static final String EXTRA_RESULTRECEIVER = "resultreceiver"; // MUST be set -> class that handles the result (MUST be inherited from ResultReceiver)
			public static final String EXTRA_REQUESTBODY = "requestbody";
		}
	}

	public HttpPostService()
	{
		super("HttpPostService");
	}


	/**
	 * create and send a HTTP POST request
	 * intent MUST have as extra: UPLOADTYPE, URL, RESULTRECEIVER and MULTIPARTBUILDER
	 * intent MAY have as extra: TIMEOUT and REQUESTDATA_BUNDLE
	 *
	 * given intent will be used to create the MultipartEntity, so other extra's are also passed
	 * only the REQUESTDATA_BUNDLE will be present in the response!
	 *
	 * function MUST NOT throw
	 */
	@Override
	protected void onHandleIntent(Intent intent)
	{
		try
		{
			if (!sanityCheck(intent))
				return;

			// 0 means infinite wait!  Only use it when you know what you are doing!
			if (intent.getExtras().get(IntentSettings.Optional.EXTRA_TIMEOUT) != null )
				socketTimeout = intent.getExtras().getInt(IntentSettings.Optional.EXTRA_TIMEOUT);

			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, connectionTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, socketTimeout);
			HttpProtocolParams.setUseExpectContinue(httpParameters, false);
			DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);

			String postUrl = (String)intent.getExtras().get(IntentSettings.Mandatory.EXTRA_URL);
			HttpPost post = new HttpPost(postUrl);

			StringEntity entity = new StringEntity((String)intent.getExtras().get(IntentSettings.Mandatory.EXTRA_REQUESTBODY), "iso-8859-1");
			post.setEntity(entity);

			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response)	throws ClientProtocolException, IOException {
					 return EntityUtils.toString(response.getEntity());
				}
			};

			try {
				String res = httpclient.execute(post, responseHandler);
				sendResponse(intent, res, MSG_SUCCESS);
			} catch (ClientProtocolException e) {
				sendResponse(intent, e.toString(), MSG_ERROR);
			} catch (IOException e) {
				sendResponse(intent, e.toString(), MSG_ERROR);
			} catch (Exception e) {
				sendResponse(intent, e.toString(), MSG_ERROR);
			}
		} catch (Exception e) {
			sendResponse(intent, "]]~Error 0: "+ e.toString(), MSG_ERROR);
		}
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
			((ResultReceiver)intent.getExtras().get(IntentSettings.Mandatory.EXTRA_RESULTRECEIVER)).send(action, res);
		} catch (Exception e) {
			//AppManagerSingleton.getLogManager().add("MultipartUploadService", "sendResponse",
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

		// URL and RESULTRECEIVER MUST be set
		if (!(bundle.containsKey(IntentSettings.Mandatory.EXTRA_URL) && bundle.get(IntentSettings.Mandatory.EXTRA_URL) instanceof String))
		{
			//AppManagerSingleton.getLogManager().add(CLASSNAME, "onHandleIntent", "Failure: no URL given in intent!", LogLevel.ERROR);
			sendResponse(intent, "FAILED: " + IntentSettings.Mandatory.EXTRA_URL + " not set!", MSG_ERROR);
			return false;
		}
		else if (!(bundle.containsKey(IntentSettings.Mandatory.EXTRA_RESULTRECEIVER) && bundle.get(IntentSettings.Mandatory.EXTRA_RESULTRECEIVER) instanceof ResultReceiver))
		{
			//AppManagerSingleton.getLogManager().add(CLASSNAME, "onHandleIntent", "Failure: no RESULTRECEIVER given in intent!", LogLevel.ERROR);
			sendResponse(intent, "FAILED: " + IntentSettings.Mandatory.EXTRA_RESULTRECEIVER + " not found!", MSG_ERROR);
			return false;
		}
		else if (!(bundle.containsKey(IntentSettings.Mandatory.EXTRA_REQUESTBODY) && bundle.get(IntentSettings.Mandatory.EXTRA_REQUESTBODY) instanceof String))
		{
			//AppManagerSingleton.getLogManager().add(CLASSNAME, "onHandleIntent", "Failure: no REQUESTBODY given in intent!", LogLevel.ERROR);
			sendResponse(intent, "FAILED: " + IntentSettings.Mandatory.EXTRA_REQUESTBODY + " not found!", MSG_ERROR);
			return false;
		}

		return true;
	}
}
