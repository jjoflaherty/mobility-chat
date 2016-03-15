package be.kpoint.pictochat.comm.pubnub;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.format.Time;

public abstract class SendResultReceiver extends ResultReceiver 
{
	public static final int SUCCESS = 1;
	public static final int ERROR = 2;
	public static final int TIMEOUT = 3;
	
	
	public SendResultReceiver() {
		super(new Handler());
	}
	
	@Override
	protected final void onReceiveResult(int resultCode, Bundle resultData) {
		super.onReceiveResult(resultCode, resultData);
		
		switch (resultCode) {
			case SUCCESS:				
				String uuid = resultData.getString("uuid");
				String appTime = resultData.getString("appTime");
				long timeToken = resultData.getLong("serverTime");
				Time serverTime = new Time(); serverTime.set(timeToken / 10000);
				
				onSuccess(resultData, uuid, appTime, serverTime);
				break;
			default:
				onError(resultData);
				break;
		}
	}
	
	public abstract void onSuccess(Bundle resultData, String uuid, String appTime, Time serverTime);
	public abstract void onError(Bundle resultData);
}
