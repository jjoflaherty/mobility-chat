package be.kpoint.pictochat.util;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

/**
 * This class combines the old AsyncTask functionality of Android versions before API 11 (Honeycomb)
 * with the new behavior and added definitions.
 * The new behavior runs threads in sequence unless we use the newly added functions.
 * These functions are not present in the older APIs and cause the app to crash with a NoSuchFieldError
 * @see <a href="http://developer.android.com/reference/android/os/AsyncTask.html">AsyncTask reference</a>
 */
public class ParallelAsyncTask
{
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static <T> void executeAsyncTask(AsyncTask<T, ?, ?> task, T... params) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
		} else {
			task.execute(params);
		}
	}
}
