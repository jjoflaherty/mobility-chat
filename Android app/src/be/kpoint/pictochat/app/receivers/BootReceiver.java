package be.kpoint.pictochat.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import be.kpoint.pictochat.App;

public class BootReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent) {
		App.setUploadLogAlarm(context);
	}

}
