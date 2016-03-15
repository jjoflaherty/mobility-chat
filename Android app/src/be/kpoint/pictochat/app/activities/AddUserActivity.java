package be.kpoint.pictochat.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import be.kpoint.pictochat.app.R;
import be.kpoint.pictochat.app.domain.Coach;

public class AddUserActivity extends Activity
{
	private static final String EXTRA_COACH = "coach";


	//Interface objects
	private TextView lblFirstName;
	private TextView lblLastName;
	private TextView lblPin;
	private EditText txtFirstName;
	private EditText txtLastName;
	private EditText txtPin;
	private Button btnOk;
	private Button btnCancel;

	private SharedPreferences settings;
	private Editor editor;

	private String firstName;
	private String lastName;
	private Integer pin;


	//Life cycle
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_user);

		/*this.settings = this.getSharedPreferences(Constants.PREF_LOCATION_LOGIN, Context.MODE_PRIVATE);
		this.editor = this.settings.edit();*/

		this.lblFirstName = (TextView)this.findViewById(R.id.addUser_lblFirstName);
		this.lblLastName = (TextView)this.findViewById(R.id.addUser_lblLastName);
		this.lblPin = (TextView)this.findViewById(R.id.addUser_lblPin);
		this.txtFirstName = (EditText)this.findViewById(R.id.addUser_txtFirstName);
		this.txtLastName = (EditText)this.findViewById(R.id.addUser_txtLastName);
		this.txtPin = (EditText)this.findViewById(R.id.addUser_txtPin);
		this.btnOk = (Button)this.findViewById(R.id.addUser_btnOk);
		this.btnCancel = (Button)this.findViewById(R.id.addUser_btnCancel);

		this.btnOk.setOnClickListener(this.btnOkListener);
		this.btnCancel.setOnClickListener(this.btnCancelListener);

		Bundle bundle = getIntent().getExtras();
	    if (bundle != null) {
	    	Coach coach = (Coach)bundle.get(EXTRA_COACH);
	    }
	}

	public static void start(Context context, Coach coach) {
		Intent intent = new Intent(context, AddUserActivity.class);

		intent.putExtra(EXTRA_COACH, coach);

    	context.startActivity(intent);
	}


	//Interface events
	private OnClickListener btnOkListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String firstName = AddUserActivity.this.txtFirstName.getText().toString();
			String lastName = AddUserActivity.this.txtLastName.getText().toString();
			String pinString = AddUserActivity.this.txtPin.getText().toString();
			Integer pin = pinString.isEmpty() ? 0 : Integer.parseInt(pinString);

			if (firstName.isEmpty())
				Toast.makeText(AddUserActivity.this, R.string.add_user_toast_firstName_required, Toast.LENGTH_SHORT).show();
			else if (lastName.isEmpty())
				Toast.makeText(AddUserActivity.this, R.string.add_user_toast_lastName_required, Toast.LENGTH_SHORT).show();
			else if (pin.equals(0))
				Toast.makeText(AddUserActivity.this, R.string.add_user_toast_pin_required, Toast.LENGTH_SHORT).show();
			else {
				/*Client client = new Client(firstName, lastName);
				client.setPin(pin);*/

				//App.addClient(client);

				AddUserActivity.this.finish();
			}
		}
	};
	private OnClickListener btnCancelListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			AddUserActivity.this.finish();
		}
	};
}
