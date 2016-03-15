package be.kpoint.pictochat.app.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apmem.tools.layouts.FlowLayout;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.api.picto.texttopicto.TextToPictoManager;
import be.kpoint.pictochat.api.picto.texttopicto.TextToPictoResultReceiver;
import be.kpoint.pictochat.app.Constants;
import be.kpoint.pictochat.app.R;
import be.kpoint.pictochat.app.activities.components.HistoryMessagesManager;
import be.kpoint.pictochat.app.domain.Client;
import be.kpoint.pictochat.app.domain.Coach;
import be.kpoint.pictochat.app.domain.Friend;
import be.kpoint.pictochat.app.domain.PastPictoMessage;
import be.kpoint.pictochat.app.domain.Picto;
import be.kpoint.pictochat.app.domain.PictoMessage;
import be.kpoint.pictochat.app.domain.TextMessage;
import be.kpoint.pictochat.business.comm.PictoSendResultReceiver;
import be.kpoint.pictochat.business.comm.interfaces.IHistoryReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.IInitializedListener;
import be.kpoint.pictochat.business.comm.interfaces.IPictoMessageReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.ITextMessageReceivedListener;
import be.kpoint.pictochat.comm.pubnub.PubnubChannel;

public class ChatWithClientActivity extends Activity
{
	private static final String EXTRA_CLIENT = "client";
	private static final String EXTRA_COACH = "coach";


	//Managers
	private TextToPictoManager textToPicto;

	//Interface objects
	private TextView lblClientName;
	private EditText txtMessage;
	private Button btnSend;
	private LinearLayout lytDummy;
	private ListView lstMessages;
	private LinearLayout lytPictos;
	private ProgressBar prgBusy;
	private String selected;
	private List<String> databases;
	private Spinner lstDatabases;
	private MessagesArrayAdapter messageAdapter;

	private Client client;
	private HistoryMessagesManager historyMessagesManager;

	private ColorMatrix colorMatrix;
	private ColorMatrixColorFilter grayScaleFilter;

	private Coach coach;
	private float dpppx;
	private List<PictoMessage> messages = new ArrayList<PictoMessage>();

	private int busyCounter = 0;
	private Timer timer;


	//Life cycle
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_chat);

		this.dpppx = getResources().getDisplayMetrics().density;

		this.lblClientName = (TextView)findViewById(R.id.clientChat_lblClientName);
		this.txtMessage = (EditText)findViewById(R.id.clientChat_txtMessage);
		this.btnSend = (Button)findViewById(R.id.clientChat_btnSend);
		this.prgBusy = (ProgressBar)findViewById(R.id.clientChat_prgBusy);
		this.lytDummy = (LinearLayout)findViewById(R.id.clientChat_lytDummy);
		this.lstMessages = (ListView)findViewById(R.id.clientChat_lstMessages);
		this.lytPictos = (LinearLayout)findViewById(R.id.clientChat_lytPictos);
		this.lstDatabases = (Spinner)findViewById(R.id.clientChat_lstDatabases);

		this.messageAdapter = new MessagesArrayAdapter(this, this.messages);
		this.lstMessages.setAdapter(this.messageAdapter);

		this.lstDatabases.setOnItemSelectedListener(this.mSelect);
		this.btnSend.setOnClickListener(this.btnSendListener);

		this.timer = new Timer();
		this.txtMessage.addTextChangedListener(this.textChangedWatcher);

		this.textToPicto = new TextToPictoManager(this);
		this.historyMessagesManager = new HistoryMessagesManager();

		this.databases = new ArrayList<String>();
		this.databases.add("Beta");
		this.databases.add("Sclera");

		List<String> spinnerList = new ArrayList<String>();
		for (String database : this.databases)
			spinnerList.add(database);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.lstDatabases.setAdapter(dataAdapter);

		Bundle bundle = getIntent().getExtras();
	    if (bundle != null) {
	    	final Client client = this.client = (Client)bundle.get(EXTRA_CLIENT);
	    	this.coach = (Coach)bundle.get(EXTRA_COACH);
	    	this.lblClientName.setText(this.client.getFullName());

	    	if (App.getPictoChatCommunicator().isStarted())
	    		startCommunicator(client);
	    	else
	    		App.getPictoChatCommunicator().addInitializedListener(new IInitializedListener() {
					@Override
					public void onInitialized() {
						startCommunicator(client);
					}
				});
	    }

	    this.colorMatrix = new ColorMatrix();
	    this.colorMatrix.setSaturation(0);
	    this.grayScaleFilter = new ColorMatrixColorFilter(this.colorMatrix);
	}
	@Override
	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	protected void onResume() {
		addHistoryAndCacheToMessageList();

		super.onResume();
	}
	@Override
	protected void onDestroy() {
		App.removePictoMessageReceivedListener(this.pictoMessageReceivedListener);

		super.onDestroy();
	}

	public static void start(Context context, Client client, Coach coach) {
		Intent intent = buildIntent(context, client, coach);

    	context.startActivity(intent);
	}
	public static Intent buildIntent(Context context, Client client, Coach coach) {
		Intent intent = new Intent(context, ChatWithClientActivity.class);

		intent.putExtra(EXTRA_CLIENT, client);
		intent.putExtra(EXTRA_COACH, coach);

		return intent;
	}


	private void startCommunicator(Client client) {
		App.addTextMessageReceivedListener(this.textMessageReceivedListener);
		App.addPictoMessageReceivedListener(this.pictoMessageReceivedListener);
		App.addHistoryReceivedListener(this.historyReceivedListener);

		this.historyMessagesManager.clear();
		App.getPictoChatCommunicator().historyReceived(client, 5);
		App.getPictoChatCommunicator().historySent(client, 5);
	}


	//Interface events
	private OnClickListener btnSendListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			final String text = ChatWithClientActivity.this.txtMessage.getText().toString();
			if (!text.isEmpty()) {
				ChatWithClientActivity.this.timer.cancel();
				ChatWithClientActivity.this.clearPictos();
				ChatWithClientActivity.this.txtMessage.setText("");
				ChatWithClientActivity.this.lytDummy.requestFocus();
				hideKeyboard();


				incrementBusy();
				ChatWithClientActivity.this.textToPicto.get(text, ChatWithClientActivity.this.selected.toLowerCase(), "dutch", new TextToPictoResultReceiver("clientChatSend") {
					@Override
					public void onServerError(String json) {
						//TODO Translate me
						Toast.makeText(ChatWithClientActivity.this, "De vertaalserver gaf een ongeldig resultaat", Toast.LENGTH_LONG).show();
						Log.e("ttp", json);
					}
					@Override
					public void onClientError() {
						//TODO Translate me
						Toast.makeText(ChatWithClientActivity.this, "De app stuurde een ongeldige aanvraag naar de vertaalserver", Toast.LENGTH_LONG).show();
						Log.e("ttp", "");
					}
					@Override
					public void onTimedOut() {
						//TODO Translate me
						Toast.makeText(ChatWithClientActivity.this, "Het wachten op de vertaalserver duurde te lang", Toast.LENGTH_LONG).show();
						Log.e("ttp", "");
					}

					@Override
					public void onFinished() {
						decrementBusy();
					}

					@Override
					protected void onPictosLoaded(String database, List<String> urls) {
						final PictoMessage message = new PictoMessage(text, ChatWithClientActivity.this.coach.getFullName(), true);

						for (String url : urls) {
							String tag = Picto.getPictoTagFromUrl(url);
							if (tag != null)
								message.addPicto(new Picto(database, tag, url));
						}

						App.sendPictoMessageToClient(ChatWithClientActivity.this.client, message, new PictoSendResultReceiver(message) {
							@Override
							public void onSuccess(TextMessage message) {
								if (message instanceof PictoMessage) {
									ChatWithClientActivity.this.messageAdapter.add((PictoMessage)message);
									scrollToBottomWorkaround();
								}
							}

							@Override
							public void onError(Bundle resultData) {}
						});
					}
				});
			}
		}
	};
	private TextWatcher textChangedWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void afterTextChanged(Editable s) {
			ChatWithClientActivity.this.timer.cancel();
			ChatWithClientActivity.this.timer = new Timer();
			ChatWithClientActivity.this.timer.schedule(new StoppedTypingTask(), Constants.STOPPED_TYPING_DELAY);
		}
	};
	private OnItemSelectedListener mSelect = new OnItemSelectedListener()
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id)
		{
			ChatWithClientActivity.this.selected = ChatWithClientActivity.this.databases.get(position);
			String text = ChatWithClientActivity.this.txtMessage.getText().toString();
			if (!text.isEmpty()) {
				incrementBusy();
				ChatWithClientActivity.this.textToPicto.get(text, ChatWithClientActivity.this.selected.toLowerCase(), "dutch", ChatWithClientActivity.this.textToPictoResultReceiver);
			}
		}
		@Override
		public void onNothingSelected(AdapterView<?> parentView)
		{
			ChatWithClientActivity.this.selected = null;
	    }
	};


	private TextToPictoResultReceiver textToPictoResultReceiver = new TextToPictoResultReceiver("clientChatTextChanged") {
		@Override
		public void onServerError(String json) {
			//TODO Translate me
			Toast.makeText(ChatWithClientActivity.this, "De vertaalserver gaf een ongeldig resultaat", Toast.LENGTH_LONG).show();
			Log.e("ttp", json);
		}
		@Override
		public void onClientError() {
			//TODO Translate me
			Toast.makeText(ChatWithClientActivity.this, "De app stuurde een ongeldige aanvraag naar de vertaalserver", Toast.LENGTH_LONG).show();
			Log.e("ttp", "");
		}
		@Override
		public void onTimedOut() {
			//TODO Translate me
			Toast.makeText(ChatWithClientActivity.this, "Het wachten op de vertaalserver duurde te lang", Toast.LENGTH_LONG).show();
			Log.e("ttp", "");
		}
		@Override
		public void onFinished() {
			decrementBusy();
		}

		@Override
		protected void onPictosLoaded(String database, List<String> urls) {
			List<Picto> pictos = new ArrayList<Picto>();

			for (String url : urls) {
				String tag = Picto.getPictoTagFromUrl(url);
				if (tag != null)
					pictos.add(new Picto(database, tag, url));
			}

			setPictos(pictos);
		}
	};


	private void addHistoryAndCacheToMessageList() {
		ChatWithClientActivity.this.messageAdapter.clear();

		SortedSet<PastPictoMessage> history = this.historyMessagesManager.getMessages();
		List<PictoMessage> cache = App.getMessages(this.client);
		for (PastPictoMessage message : history) {
			if (cache.contains(message))
				cache.remove(message);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ChatWithClientActivity.this.messageAdapter.addAll(ChatWithClientActivity.this.historyMessagesManager.getMessages());
			ChatWithClientActivity.this.messageAdapter.addAll(App.getMessages(this.client));
		}
		else {
			this.messages.addAll(ChatWithClientActivity.this.historyMessagesManager.getMessages());
			this.messages.addAll(App.getMessages(this.client));
			this.messageAdapter.notifyDataSetChanged();
		}

		scrollToBottomWorkaround();
	}
	private void scrollToBottomWorkaround() {
		final int index = this.messageAdapter.getCount() - 1;
		this.lstMessages.post(new Runnable() {
			@Override
			public void run() {
				ChatWithClientActivity.this.lstMessages.smoothScrollToPosition(index);
				ChatWithClientActivity.this.lstMessages.post(new Runnable() {
					@Override
					public void run() {
						ChatWithClientActivity.this.lstMessages.setSelection(index);
					}
				});
			}
		});
	}

	private void setPictos(List<Picto> pictos) {
		this.clearPictos();

		for (Picto picto : pictos) {
			ImageView view = new ImageView(ChatWithClientActivity.this);
			picto.showInImageView(view, 75, 75);
			this.lytPictos.addView(view);
		}
	}
	private void clearPictos() {
		this.lytPictos.removeAllViews();
	}

	private void hideKeyboard() {
	    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(this.txtMessage.getWindowToken(), 0);
	}


	private void incrementBusy() {
		this.busyCounter++;
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ChatWithClientActivity.this.prgBusy.setVisibility(View.VISIBLE);
			}
		});
	}
	private void decrementBusy() {
		this.busyCounter--;
		if (this.busyCounter == 0)
			this.prgBusy.setVisibility(View.INVISIBLE);
	}


	private class StoppedTypingTask extends TimerTask {
		@Override
		public void run() {
			String text = ChatWithClientActivity.this.txtMessage.getText().toString();
			if (!text.isEmpty()) {
				incrementBusy();
				ChatWithClientActivity.this.textToPicto.get(text, ChatWithClientActivity.this.selected.toLowerCase(), "dutch", ChatWithClientActivity.this.textToPictoResultReceiver);
			}
		}
	};

	private ITextMessageReceivedListener textMessageReceivedListener = new ITextMessageReceivedListener() {
		@Override
		public void onTextMessageReceived(TextMessage message) {
			TextView textView = new TextView(ChatWithClientActivity.this);
			textView.setText(message.getSenderName() + ": " + message.getText());

			//ClientActivity.this.lytMessages.addView(textView);
		}
	};
	private IPictoMessageReceivedListener pictoMessageReceivedListener = new IPictoMessageReceivedListener() {
		@Override
		public void onPictoMessageReceived(Client client, PictoMessage message) {
			if (ChatWithClientActivity.this.client.equals(client)) {
				ChatWithClientActivity.this.messageAdapter.add(message);
				scrollToBottomWorkaround();
			}
		}

		@Override
		public void onPictoMessageReceived(Coach coach, PictoMessage message) {}

		@Override
		public void onPictoMessageReceived(Friend friend, PictoMessage message) {}
	};
	private IHistoryReceivedListener historyReceivedListener = new IHistoryReceivedListener() {
		@Override
		public void onHistoryReceived(PubnubChannel channel, List<TextMessage> messages) {
			for (TextMessage textMessage : messages) {
				if (textMessage instanceof PastPictoMessage) {
					PastPictoMessage pictoMessage = (PastPictoMessage)textMessage;
					ChatWithClientActivity.this.historyMessagesManager.addMessage(pictoMessage);
				}
			}

			addHistoryAndCacheToMessageList();
		}
	};


	private class MessagesArrayAdapter extends ArrayAdapter<PictoMessage>
	{
		private Activity context;
		private List<PictoMessage> messages;

		public MessagesArrayAdapter(Activity context, List<PictoMessage> messages) {
			super(context, R.layout.client_item, messages);

			this.context = context;
			this.messages = messages;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView;
			Context context = ChatWithClientActivity.this;

			if (convertView != null)
				rowView = convertView;
			else {
				LayoutInflater inflater = this.context.getLayoutInflater();

				if(getItemViewType(position) == 0)
					rowView = inflater.inflate(R.layout.received_chat, parent, false);
		        else
		        	rowView = inflater.inflate(R.layout.sent_chat, parent, false);
			}

			final ImageView imageView = (ImageView)rowView.findViewById(R.id.room_imgAvatar);
			FlowLayout receivedMessagePictos = (FlowLayout)rowView.findViewById(R.id.receivedChat_lytPictos);
			TextView receivedText = (TextView)rowView.findViewById(R.id.room_lblLastName);
			FlowLayout sentMessagePictos = (FlowLayout)rowView.findViewById(R.id.sentChat_lytPictos);
			TextView sentText = (TextView)rowView.findViewById(R.id.sentChat_lblText);

			int size = (int)(ChatWithClientActivity.this.dpppx * 70);
			int padding = (int)(ChatWithClientActivity.this.dpppx * 10);

			//Insert data
			PictoMessage message = this.messages.get(position);
			if (receivedMessagePictos != null) {
				receivedMessagePictos.removeAllViews();
				for (Picto picto : message.getPictos()) {
					ImageView image = new ImageView(context);
					image.setPadding(0, 0, padding, padding);
					image.setLayoutParams(new LayoutParams(size, size));
					image.setScaleType(ScaleType.FIT_XY);
					picto.showInImageView(image, size, size);
					receivedMessagePictos.addView(image);

					if (message instanceof PastPictoMessage)
						image.setColorFilter(ChatWithClientActivity.this.grayScaleFilter);
				}
			}
			if (receivedText != null) {
				receivedText.setText(message.getText());
			}
			if (sentMessagePictos != null) {
				sentMessagePictos.removeAllViews();
				for (Picto picto : message.getPictos()) {
					ImageView image = new ImageView(context);
					image.setPadding(0, 0, padding, padding);
					image.setLayoutParams(new LayoutParams(size, size));
					image.setScaleType(ScaleType.FIT_XY);
					picto.showInImageView(image, size, size);
					sentMessagePictos.addView(image);

					if (message instanceof PastPictoMessage)
						image.setColorFilter(ChatWithClientActivity.this.grayScaleFilter);
				}
			}
			if (sentText != null) {
				sentText.setText(message.getText());
			}
			if (imageView != null) {
				imageView.setImageResource(R.drawable.ic_person_pin_black_48dp);
				message.showInImageView(imageView, 75, 75);
			}

			return rowView;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			PictoMessage message = this.messages.get(position);

			if (message.getSent())
				return 1;
			else
				return 0;
		}
	}
}