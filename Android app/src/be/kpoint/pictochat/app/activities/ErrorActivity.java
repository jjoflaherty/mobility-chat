package be.kpoint.pictochat.app.activities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.app.R;
import be.kpoint.pictochat.util.ReportBuilder;

public class ErrorActivity extends Activity
{
	private static final String EXTRA_NAME = "name";
	private static final String EXTRA_STACK = "stack";
	private static final String EXTRA_EXCEPTION = "exception";


	private String name;
	private String stackTrace;
	private String exception;

	//Interface objects
	private Button btnSend;


	//Life cycle
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.error);

	    this.btnSend = (Button)findViewById(R.id.error_btnSendReport);
	    this.btnSend.setOnClickListener(this.sendReportListener);

	    Bundle bundle = getIntent().getExtras();
	    if (bundle != null)
	    {
	    	this.name = bundle.getString(EXTRA_NAME);
	    	this.stackTrace = bundle.getString(EXTRA_STACK);
	    	this.exception = bundle.getString(EXTRA_EXCEPTION);
	    }
	}

	public static void start(Context context, Throwable t) {
		Intent intent = new Intent(context, ErrorActivity.class);
		intent.putExtra(ErrorActivity.EXTRA_NAME, context.getClass().getSimpleName());
		intent.putExtra(ErrorActivity.EXTRA_EXCEPTION, t.toString());
		intent.putExtra(ErrorActivity.EXTRA_STACK, exceptionStacktraceToString(t));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		try {
			context.startActivity(intent);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static String exceptionStacktraceToString(Throwable t)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}


	//Interface events
	private OnClickListener sendReportListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
	    {
	        String report = ReportBuilder.build(ErrorActivity.this.exception, ErrorActivity.this.stackTrace);

	        Calendar calendar = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH'u'mm'm'ss");
	        String errorLogName = "E " + format.format(calendar.getTime());
	        App.getFileLogger().appendText(report, errorLogName);

			App.getFileLogger().sendWithMail(ErrorActivity.this, report, errorLogName);

	    	finish();
	    }
	};
}
