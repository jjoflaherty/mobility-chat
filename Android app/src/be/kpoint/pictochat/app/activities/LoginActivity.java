package be.kpoint.pictochat.app.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.api.rest.coach.CoachManager;
import be.kpoint.pictochat.api.rest.coach.LoginResultReceiver;
import be.kpoint.pictochat.app.Constants;
import be.kpoint.pictochat.app.R;
import be.kpoint.pictochat.app.activities.client.RoomsActivity;
import be.kpoint.pictochat.app.activities.coach.ClientListActivity;
import be.kpoint.pictochat.app.activities.components.ProgressSpinnerOverlay;
import be.kpoint.pictochat.app.activities.components.ProgressSpinnerOverlay.IProgressSpinnerOverlay;
import be.kpoint.pictochat.app.domain.Client;
import be.kpoint.pictochat.app.domain.Coach;
import be.kpoint.pictochat.logging.Messages;
import be.kpoint.pictochat.logging.Tags;
import be.kpoint.pictochat.util.ReportBuilder;
import be.kpoint.pictochat.util.logging.FileLogItem;

public class LoginActivity extends Activity implements IProgressSpinnerOverlay
{
	//Interface objects
	private Button btnLogin, btnCancel;
	private TextView txtUsername, txtPassword;
	private GestureOverlayView gestureOverlayView;
	private GestureLibrary gestureLib;

	//Interface components
	private ProgressSpinnerOverlay progressOverlay;

	//Managers
	private CoachManager coachManager;


	//Life cycle
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.login);
	    super.onCreate(savedInstanceState);

	    App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_CREATE));

	    SharedPreferences settings = getSharedPreferences(Constants.PREF_LOCATION_DATA, MODE_PRIVATE);
	    String coachJson = settings.getString(Constants.PREF_KEY_COACH, null);
	    String clientJson = settings.getString(Constants.PREF_KEY_CLIENT, null);

	    if (clientJson != null) {
	    	Client client = new Gson().fromJson(clientJson, Client.class);
	    	RoomsActivity.start(this, client);
	    	this.finish();
	    }
	    else if (coachJson != null) {
	    	//TODO Refresh data by loading it from server
	    	Coach coach = new Gson().fromJson(coachJson, Coach.class);
	    	ClientListActivity.start(this, coach);
	    	this.finish();
	    }

	    this.btnLogin = (Button)findViewById(R.id.login_btnLogin);
	    this.btnLogin.setOnClickListener(this.mLogin);
	    this.btnCancel = (Button)findViewById(R.id.login_btnCancel);
	    this.btnCancel.setOnClickListener(this.mCancel);
	    this.txtUsername = (TextView)findViewById(R.id.login_txtUsername);
	    this.txtPassword = (TextView)findViewById(R.id.login_txtPassword);
	    this.gestureOverlayView = (GestureOverlayView)findViewById(R.id.login_gestureOverlayView);

	    this.progressOverlay = new ProgressSpinnerOverlay(this);

	    this.coachManager = new CoachManager(this);

	    this.gestureOverlayView.setEventsInterceptionEnabled(false);
	    this.gestureOverlayView.addOnGesturePerformedListener(this.gestureListener);
	    this.gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
	    this.gestureLib.load();

	    App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_CREATE_FINISHED));
	}


	@Override
	public void showWaitSpinner(String text) {
		this.progressOverlay.showWaitSpinner(text);
	}
	@Override
	public void hideWaitSpinner() {
		this.progressOverlay.hideWaitSpinner();
	}


	//Manager listeners
	private LoginResultReceiver loginCoachReceiver = new LoginResultReceiver("loginCoach")
	{
		@Override
		public void onLoggedInSuccessfully(be.kpoint.pictochat.api.rest.coach.Coach coach) {
			App.logToFile(FileLogItem.debug(LoginActivity.this, Tags.REST, Messages.LOGIN_SUCCESFULLY));

			//device.storeInPersistence(LoginActivity.this);

			Coach c = Coach.buildFromRest(coach);

			ModeActivity.start(LoginActivity.this, c);
			LoginActivity.this.finish();
		}

		@Override
		public void onLoginWrong() {
			Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_toast_wrong_password), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onFinished() {
			LoginActivity.this.hideWaitSpinner();
		}

		@Override
		public void onTimedOut() {
			App.logToFile(FileLogItem.warn(LoginActivity.this, Tags.REST, Messages.LOGIN_TIMED_OUT));

			//Toast.makeText(LoginActivity.this, VpadMessages.Toast.timeout(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onClientError() {
			App.logToFile(FileLogItem.error(LoginActivity.this, Tags.REST, Messages.LOGIN_CLIENT_ERROR));
		}

		@Override
		public void onServerError(String json) {
			App.logToFile(FileLogItem.error(LoginActivity.this, Tags.REST, Messages.LOGIN_SERVER_ERROR, json));

			Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_toast_server_error), Toast.LENGTH_LONG).show();
		}
	};


	//Interface events
	private OnClickListener mLogin = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			String username = LoginActivity.this.txtUsername.getText().toString();
			String password = LoginActivity.this.txtPassword.getText().toString();

			LoginActivity.this.showWaitSpinner(App.buildString(R.string.login_logging_in));
			LoginActivity.this.coachManager.login(username, password, LoginActivity.this.loginCoachReceiver);
		}
	};
	private OnClickListener mCancel = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			finish();
		}
	};
	private OnClickListener sendReportListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
	    {
			String report = ReportBuilder.build();

			App.getFileLogger().sendWithMail(LoginActivity.this, report);
	    }
	};

	//Gesture events
	private OnGesturePerformedListener gestureListener = new OnGesturePerformedListener() {
		@Override
		public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
			ArrayList<Prediction> predictions = LoginActivity.this.gestureLib.recognize(gesture);
	    	for (Prediction prediction : predictions) {
	    		if (prediction.score > 4.0 && prediction.name.equals("Dev")) {
	    			//LoginActivity.this.reportLayout.show();
	    		}
	    	}
		}
	};
}
