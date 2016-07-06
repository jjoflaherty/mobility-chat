package be.kpoint.pictochat.app.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

import com.google.gson.Gson;

import be.kpoint.pictochat.app.Constants;
import be.kpoint.pictochat.app.R;
import be.kpoint.pictochat.app.activities.client.RoomsActivity;
import be.kpoint.pictochat.app.activities.coach.ClientListActivity;
import be.kpoint.pictochat.app.domain.Client;
import be.kpoint.pictochat.app.domain.Coach;

public class ModeActivity extends Activity
{
	private static final String EXTRA_COACH = "coach";


	//Interface objects
	private RadioGroup radGrpUser;
	private RadioButton radClient;
	private RadioButton radCoach;
	private LinearLayout lytClients;
	private Spinner lstClients;
	private Button btnOk;

	private SharedPreferences settings;
	private Editor editor;

	private Coach coach;
	private Client selected;
	private List<Client> clients;


	//Life cycle
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mode);

		this.settings = this.getSharedPreferences(Constants.PREF_LOCATION_DATA, Context.MODE_PRIVATE);
		this.editor = this.settings.edit();

		/*this.firstName = this.settings.getString(Constants.PREF_KEY_FIRSTNAME, "");
		this.lastName = this.settings.getString(Constants.PREF_KEY_LASTNAME, "");
		this.pin = this.settings.getInt(Constants.PREF_KEY_PIN, 0);*/

		this.radGrpUser = (RadioGroup)this.findViewById(R.id.mode_radGrpUser);
		this.radClient = (RadioButton)this.findViewById(R.id.mode_radClient);
		this.radCoach = (RadioButton)this.findViewById(R.id.mode_radCoach);
		this.lytClients = (LinearLayout)this.findViewById(R.id.mode_lytClientInfo);
		this.lstClients = (Spinner)this.findViewById(R.id.mode_lstClients);
		this.btnOk = (Button)this.findViewById(R.id.mode_btnOk);

		this.radGrpUser.setOnCheckedChangeListener(this.radGrpUserChangedListener);
		this.lstClients.setOnItemSelectedListener(this.mSelect);
		this.btnOk.setOnClickListener(this.btnOkListener);

		Bundle bundle = getIntent().getExtras();
	    if (bundle != null) {
	    	this.coach = (Coach)bundle.get(EXTRA_COACH);

    		List<String> spinnerList = new ArrayList<String>();
    		this.clients = new ArrayList<Client>();
    		for (Client client : this.coach.getClients().values()) {
    			this.clients.add(client);
    			spinnerList.add(client.getFullName());
    		}

    		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.lstClients.setAdapter(dataAdapter);
	    }


		/*if (!this.firstName.isEmpty() && !this.lastName.isEmpty()) {
			if (this.pin.equals(0)) {
				Coach coach = new Coach(this.firstName, this.lastName);
				CoachActivity.start(this, coach);
			}
			else {
				Client client = new Client(this.firstName, this.lastName, this.pin);
				ClientActivity.start(this, client);
			}

			finish();
			return;
		}*/


	}

	public static void start(Context context, Coach coach) {
		Intent intent = buildIntent(context, coach);

    	context.startActivity(intent);
	}
	public static Intent buildIntent(Context context, Coach coach) {
		Intent intent = new Intent(context, ModeActivity.class);

		intent.putExtra(EXTRA_COACH, coach);

		return intent;
	}


	//Interface events
	private OnCheckedChangeListener radGrpUserChangedListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			Log.i("rad", Integer.toString(checkedId));

			if (ModeActivity.this.radClient.isChecked())
				ModeActivity.this.lytClients.setVisibility(View.VISIBLE);
			else
				ModeActivity.this.lytClients.setVisibility(View.GONE);
		}
	};
	private OnItemSelectedListener mSelect = new OnItemSelectedListener()
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id)
		{
			ModeActivity.this.selected = ModeActivity.this.clients.get(position);
		}
		@Override
		public void onNothingSelected(AdapterView<?> parentView)
		{
			ModeActivity.this.selected = null;
	    }
	};
	private OnClickListener btnOkListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (ModeActivity.this.radCoach.isChecked()) {
				ModeActivity.this.editor.putString(Constants.PREF_KEY_COACH, new Gson().toJson(ModeActivity.this.coach));
				ModeActivity.this.editor.commit();

				ClientListActivity.start(ModeActivity.this, ModeActivity.this.coach);
				ModeActivity.this.finish();
			} else if (ModeActivity.this.radClient.isChecked() && ModeActivity.this.selected != null) {
				ModeActivity.this.editor.putString(Constants.PREF_KEY_CLIENT, new Gson().toJson(ModeActivity.this.selected));
				ModeActivity.this.editor.commit();

				RoomsActivity.start(ModeActivity.this, ModeActivity.this.selected);
				ModeActivity.this.finish();
			}
		}
	};
}
