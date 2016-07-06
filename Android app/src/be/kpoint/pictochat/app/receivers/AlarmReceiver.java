package be.kpoint.pictochat.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import be.kpoint.pictochat.App;

public class AlarmReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(final Context context, Intent intent)
	{
		App.processAndMailLogs("AbleChat Logs - ");
	}

}
