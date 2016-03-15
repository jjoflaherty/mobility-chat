package be.kpoint.pictochat.app.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.api.rest.ids.ClientId;
import be.kpoint.pictochat.app.R;
import be.kpoint.pictochat.app.domain.Client;
import be.kpoint.pictochat.app.domain.Coach;
import be.kpoint.pictochat.app.domain.User;
import be.kpoint.pictochat.business.comm.enums.Presence;
import be.kpoint.pictochat.business.comm.interfaces.IHereNowReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.IInitializedListener;
import be.kpoint.pictochat.business.comm.interfaces.IPresenceReceivedListener;
import be.kpoint.pictochat.comm.pubnub.PubnubChannel;

public class ClientListActivity extends Activity
{
	private static final String EXTRA_COACH = "coach";


	//Interface object
	private ListView lstClients;
	private ClientsArrayAdapter clientsAdapter;

	private Coach coach;


	//Life cycle
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_list);

		this.lstClients = (ListView)findViewById(R.id.coach_lstClients);

		this.lstClients.setOnItemClickListener(this.itemClickListener);

		Bundle bundle = getIntent().getExtras();
	    if (bundle != null) {
	    	final Coach coach = this.coach = (Coach)bundle.get(EXTRA_COACH);

	    	List<ClientStatus> clients = new ArrayList<ClientStatus>();
			for (Client client : this.coach.getClientList())
				clients.add(new ClientStatus(client.getId(), client.getFirstName(), client.getLastName()));

		    this.clientsAdapter = new ClientsArrayAdapter(this, clients);
		    this.lstClients.setAdapter(this.clientsAdapter);

    		if (App.getPictoChatCommunicator().isStarted())
	    		startCommunicator(coach);
	    	else
	    		App.getPictoChatCommunicator().addInitializedListener(new IInitializedListener() {
					@Override
					public void onInitialized() {
						startCommunicator(coach);
					}
				});
	    }
	}
	@Override
	protected void onResume() {
		this.clientsAdapter.notifyDataSetChanged();

		super.onResume();
	}

	public static void start(Context context, Coach coach) {
		Intent intent = buildIntent(context, coach);

    	context.startActivity(intent);
	}
	public static Intent buildIntent(Context context, Coach coach) {
		Intent intent = new Intent(context, ClientListActivity.class);

		intent.putExtra(EXTRA_COACH, coach);

		return intent;
	}


	private void startCommunicator(Coach coach) {
		App.setUser(this.coach);
		App.joinClientRooms();

		App.addPresenceReceivedListener(this.presenceReceivedListener);
		App.addHereNowReceivedListener(this.hereNowReceivedListener);

		for (Client client : this.coach.getClientList()) {
		    App.getPictoChatCommunicator().clientsHereNow(client);
		    App.getPictoChatCommunicator().coachesHereNow(client);
			App.getPictoChatCommunicator().clientsPresence(client);
			App.getPictoChatCommunicator().coachesPresence(client);
	    }
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.coach, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		switch (id) {
			case R.id.coachMenu_addUser:
				AddUserActivity.start(ClientListActivity.this, this.coach);
				break;
		}

		return super.onOptionsItemSelected(item);
	}


	//Interface events
	private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
			ClientStatus cs = ClientListActivity.this.clientsAdapter.getItem(position);

			//TODO Clean this mess up
			for (Client client : ClientListActivity.this.coach.getClientList())
				if (client.getId().equals(cs.getId())) {
					ChatWithClientActivity.start(ClientListActivity.this, client, ClientListActivity.this.coach);
					break;
				}
		}
	};


	private IPresenceReceivedListener presenceReceivedListener = new IPresenceReceivedListener() {
		@Override
		public void onPresenceReceived(User host, User user, Presence presence) {
			if (user instanceof Client) {
				Client client = (Client)user;
				ClientStatus item = ClientListActivity.this.clientsAdapter.getClientStatus(client);

				item.status = presence;
			}
			else if (user instanceof Coach && host instanceof Client) {
				Coach coach = (Coach)user;
				Client client = (Client)host;
				ClientStatus item = ClientListActivity.this.clientsAdapter.getClientStatus(client);

				if (Presence.JOIN.equals(presence))
					item.addCoach(coach);
				else if (Presence.LEAVE.equals(presence) || Presence.TIMEOUT.equals(presence))
					item.removeCoach(coach);
			}

			ClientListActivity.this.clientsAdapter.notifyDataSetChanged();
		}
	};
	private IHereNowReceivedListener hereNowReceivedListener = new IHereNowReceivedListener() {
		@Override
		public void onHereNowReceived(PubnubChannel channel, int occupancy, User host, List<User> users) {
			ClientStatus item = null;
			if (host instanceof Client)
				item = ClientListActivity.this.clientsAdapter.getClientStatus((Client)host);

			for (User user : users) {
				if (user instanceof Client) {
					ClientStatus cs = ClientListActivity.this.clientsAdapter.getClientStatus((Client)user);
					cs.status = Presence.JOIN;
				}
				else if (user instanceof Coach) {
					Coach coach = (Coach)user;
					if (item != null)
						item.addCoach(coach);
				}
			}

			ClientListActivity.this.clientsAdapter.notifyDataSetChanged();
		}
	};


	private class ClientsArrayAdapter extends ArrayAdapter<ClientStatus>
	{
		private class ViewHolder {
			public ImageView image;
			public TextView text;
			public TextView subText;
		}

		public ClientsArrayAdapter(Activity context, List<ClientStatus> clients) {
			super(context, R.layout.client_item, clients);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView;

			if (convertView != null)
				rowView = convertView;
			else {
				LayoutInflater inflater = ((Activity)this.getContext()).getLayoutInflater();
				rowView = inflater.inflate(R.layout.client_item, null);

				ViewHolder viewHolder = new ViewHolder();
				viewHolder.image = (ImageView)rowView.findViewById(R.id.userItem_imgPresence);
				viewHolder.text = (TextView)rowView.findViewById(R.id.userItem_txtText);
				viewHolder.subText = (TextView)rowView.findViewById(R.id.userItem_txtSubText);

				rowView.setTag(viewHolder);
			}

			//Insert data
			ViewHolder holder = (ViewHolder)rowView.getTag();
			ClientStatus client = this.getItem(position);
			holder.text.setText(client.getFullName());
			holder.subText.setText(client.getInfoString(ClientListActivity.this.coach));

			switch (client.status) {
				case JOIN:
					holder.image.setImageResource(R.drawable.status_online);
					break;
				case LEAVE:
					holder.image.setImageResource(R.drawable.status_offline);
					break;
				case TIMEOUT:
					holder.image.setImageResource(R.drawable.status_away);
					break;
			}

			return rowView;
		}

		public ClientStatus getClientStatus(Client client) {
			ClientStatus status = new ClientStatus(client.getId(), client.getFirstName(), client.getLastName());
			int position = this.getPosition(status);
			ClientStatus item = this.getItem(position);

			return item;
		}
	}

	private class ClientStatus extends Client
	{
		public Presence status = Presence.LEAVE;
		public List<Coach> presentCoaches = new ArrayList<Coach>();
		//public String info = "";

		public ClientStatus(ClientId id, String firstName, String lastName) {
			super(id, firstName, lastName);
		}

		public void addCoach(Coach coach) {
			this.presentCoaches.add(coach);
		}
		public void removeCoach(Coach coach) {
			this.presentCoaches.remove(coach);
		}

		public Integer coachCount() {
			return this.presentCoaches.size();
		}

		public String getInfoString(Coach me) {
			StringBuilder sb = new StringBuilder("(" + coachCount() + ") ");
			for (Coach coach : this.presentCoaches)
				sb.append(coach.equals(me) ? "Ik" : coach.getFirstName()).append(", ");

			String info = sb.toString();
			if (coachCount() > 0)
				 info = info.substring(0, info.length() - 2);

			return info;
		}
	}
}
