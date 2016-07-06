package be.kpoint.pictochat.api.rest.coach;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import be.kpoint.pictochat.api.rest.ids.CoachId;
import be.kpoint.pictochat.app.Constants;
import be.kpoint.pictochat.network.AbstractRestServiceManager;
import be.kpoint.pictochat.network.ApiTarget;


public class CoachManager extends AbstractRestServiceManager
{
	public CoachManager(Context context) {
		super(context);
	}

	public void login(String name, String password, final LoginResultReceiver loginReceiver) {
		String hash = null;

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.update(password.getBytes("UTF-8"));
			byte[] byteData = digest.digest();

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		    }

			hash = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("email", name));
		nameValuePairs.add(new BasicNameValuePair("pw", hash));
		ApiTarget target = Constants.coach_login.setUrlParameters(nameValuePairs);

		Bundle metadata = new Bundle();
		metadata.putString(LoginResultReceiver.BUNDLE_USERNAME_TAG, name);
		metadata.putString(LoginResultReceiver.BUNDLE_PASSWORD_TAG, hash);
		get(target, metadata, loginReceiver);
	}
	public void get(CoachId id, LoginResultReceiver receiver) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("id", Long.toString(id.getNumber())));
		ApiTarget target = Constants.coach_read.setUrlParameters(nameValuePairs);

		get(target, receiver);
	}
}
