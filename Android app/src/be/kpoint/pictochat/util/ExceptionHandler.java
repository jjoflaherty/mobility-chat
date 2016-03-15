package be.kpoint.pictochat.util;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.Service;
import android.content.Context;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.app.activities.ErrorActivity;

public class ExceptionHandler implements UncaughtExceptionHandler
{
	protected WeakReference<Context> weakContext;

	public ExceptionHandler(Activity activity) {
		this((Context)activity);
	}
	public ExceptionHandler(Service service) {
		this(App.getContext());
	}
	public ExceptionHandler(Context context) {
        this.weakContext = new WeakReference<Context>(context);
    }

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		Context context = this.weakContext.get();
		if (context != null) {
			ErrorActivity.start(context, ex);
		}

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
	}
}
