package be.kpoint.pictochat.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.Service;
import android.content.Context;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.util.logging.FileLogItem;

public class ExceptionHandler implements UncaughtExceptionHandler
{
	protected WeakReference<Context> weakContext;
	private UncaughtExceptionHandler defaultHandler;

	public ExceptionHandler(Activity activity) {
		this((Context)activity);
	}
	public ExceptionHandler(Service service) {
		this(App.getContext());
	}
	public ExceptionHandler(Context context) {
        this.weakContext = new WeakReference<Context>(context);
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		StringWriter writer = new StringWriter();
		ex.printStackTrace(new PrintWriter(writer));
		App.logToFile(FileLogItem.wtf("CrashHandler", "crash", ex.toString(), writer.toString()));

		App.processAndMailLogs("AbleChat Crash Report - ");

		this.defaultHandler.uncaughtException(thread, ex);

        /*android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);*/
	}
}
