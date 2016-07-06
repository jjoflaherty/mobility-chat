package be.kpoint.pictochat.app.activities.coach;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import be.kpoint.pictochat.app.domain.PastPictoMessage;
import be.kpoint.pictochat.app.domain.Picto;
import be.kpoint.pictochat.app.domain.PictoMessage;
import be.kpoint.pictochat.app.domain.TextMessage;
import be.kpoint.pictochat.app.domain.User;
import be.kpoint.pictochat.app.domain.buttons.PictoButton;
import be.kpoint.pictochat.business.comm.PictoSendResultReceiver;
import be.kpoint.pictochat.business.comm.enums.AppState;
import be.kpoint.pictochat.business.comm.enums.Presence;
import be.kpoint.pictochat.business.comm.interfaces.IHistoryReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.IPictoMessageReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.IPresenceReceivedListener;
import be.kpoint.pictochat.comm.pubnub.PubnubChannel;
import be.kpoint.pictochat.logging.Messages;
import be.kpoint.pictochat.logging.Tags;
import be.kpoint.pictochat.util.logging.FileLogItem;

public abstract class AbstractChatWithClientActivity extends Activity
{
	protected static final String EXTRA_CLIENT = "client";
	protected static final String EXTRA_COACH = "coach";


	//Managers
	private TextToPictoManager textToPicto;

	//Interface objects
	private TextView lblClientName;
	private FrameLayout lytPopup;
	private EditText txtMessage;
	private Button btnSend;
	private Button btnClosePopup;
	private LinearLayout lytDummy;
	private ListView lstMessages;
	private LinearLayout lytMessage;
	private LinearLayout lytTranslations;
	private ProgressBar prgBusy;
	private String selected;
	private List<Picto> pictos = new ArrayList<Picto>();
	private Spinner lstDatabases;
	private MessagesArrayAdapter messageAdapter;

	private HistoryMessagesManager historyMessagesManager;

	protected Client client;
	protected Coach coach;
	protected Date lastCheckedTime;
	private float dpppx;
	private List<PictoMessage> messages = new ArrayList<PictoMessage>();

	private int busyCounter = 0;
	private View selectedInputPicto = null;
	private Timer timer;


	//Life cycle
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_CREATE));

		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_chat);

		this.dpppx = getResources().getDisplayMetrics().density;

		this.lblClientName = (TextView)findViewById(R.id.clientChat_lblClientName);
		this.txtMessage = (EditText)findViewById(R.id.clientChat_txtMessage);
		this.btnSend = (Button)findViewById(R.id.clientChat_btnSend);
		this.prgBusy = (ProgressBar)findViewById(R.id.clientChat_prgBusy);
		this.lytDummy = (LinearLayout)findViewById(R.id.clientChat_lytDummy);
		this.lytTranslations = (LinearLayout)findViewById(R.id.clientChat_lytTranslations);
		this.lstMessages = (ListView)findViewById(R.id.clientChat_lstMessages);
		this.lytMessage = (LinearLayout)findViewById(R.id.clientChat_lytMessage);
		this.btnClosePopup = (Button)findViewById(R.id.clientChat_btnClosePopup);
		this.lytPopup = (FrameLayout)findViewById(R.id.clientChat_lytPopup);
		this.lstDatabases = (Spinner)findViewById(R.id.clientChat_lstDatabases);

		this.messageAdapter = new MessagesArrayAdapter(this, this.messages);
		this.lstMessages.setAdapter(this.messageAdapter);

		this.btnSend.setOnClickListener(this.btnSendListener);
		this.btnClosePopup.setOnClickListener(this.btnClosePopupListener);
		this.lstDatabases.setOnItemSelectedListener(this.mSelect);

		this.timer = new Timer();
		this.txtMessage.addTextChangedListener(this.textChangedWatcher);

		this.textToPicto = new TextToPictoManager(this);
		this.historyMessagesManager = new HistoryMessagesManager();

		List<String> spinnerList = new ArrayList<String>();
		for (String database : Constants.Databases)
			spinnerList.add(database);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.lstDatabases.setAdapter(dataAdapter);

		Bundle bundle = getIntent().getExtras();
	    if (bundle != null) {
	    	this.client = (Client)bundle.get(EXTRA_CLIENT);
	    	this.coach = (Coach)bundle.get(EXTRA_COACH);

	    	this.lblClientName.setText(this.client.getFullName());

	    	//TODO Translate me
	    	addClientUploadPictos(getResources().getString(R.string.clientChat_uploadedPictos), this.client.getButtons());
	    }

	    App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_CREATE_FINISHED));
	}
	@Override
	protected void onStart()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_START));

		super.onStart();

		App.addPictoMessageReceivedListener(this.pictoMessageReceivedListener);
		App.addHistoryReceivedListener(this.historyReceivedListener);
		App.addPresenceReceivedListener(this.presenceReceivedListener);

		this.historyMessagesManager.clear();

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_START_FINISHED));
	}
	@Override
	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	protected void onResume()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_RESUME));

		super.onResume();

		addHistoryAndCacheToMessageList();

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_RESUME_FINISHED));
	}
	@Override
	protected void onStop()
	{
		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_STOP));

		App.removePictoMessageReceivedListener(this.pictoMessageReceivedListener);
		App.removeHistoryReceivedListener(this.historyReceivedListener);
		App.removePresenceReceivedListener(this.presenceReceivedListener);

		super.onStop();

		App.logToFile(FileLogItem.debug(this, Tags.LIFE_CYCLE, Messages.ACTIVITY_STOP_FINISHED));
	}


	private void addInputPicto(Picto picto) {
		Integer index = null;

		if (this.selectedInputPicto != null) {
			Object buttonTag = this.selectedInputPicto.getTag();
			if (buttonTag instanceof View) {
				View button = (View)buttonTag;
				Object viewTag = button.getTag();
				if (viewTag instanceof View) {
					View view = (View)viewTag;
					Object pictoTag = view.getTag();
					if (pictoTag instanceof Picto) {
						Picto selectedPicto = (Picto)pictoTag;

						index = this.pictos.indexOf(selectedPicto);
						this.pictos.remove(selectedPicto);
						if (index >= 0)
							this.pictos.add(index, picto);
					}

					AbstractChatWithClientActivity.this.lytMessage.removeView(view);
				}
			}
		}
		else {
			this.pictos.add(picto);
		}

		LayoutInflater inflater = this.getLayoutInflater();
		LinearLayout l = (LinearLayout)inflater.inflate(R.layout.input_picto, this.lytMessage, false);
		l.setTag(picto);

		if (index == null)
			this.lytMessage.addView(l);
		else
			this.lytMessage.addView(l, index);

		TextView text = (TextView)l.findViewById(R.id.inputPicto_lblText);
		ImageView image = (ImageView)l.findViewById(R.id.inputPicto_imgPicto);
		ImageView button = (ImageView)l.findViewById(R.id.inputPicto_imgButton);

		image.setLayoutParams(new FrameLayout.LayoutParams(75, 75));
		image.setOnClickListener(this.inputPictoListener);
		image.setTag(button);

		button.setOnClickListener(this.deletePictoListener);
		button.setTag(l);

		picto.showInImageView(image, 75, 75);
		text.setText(picto.getTag());
		text.setTextColor(getResources().getColor(R.color.black));
	}
	private void addClientUploadPictos(String database, List<PictoButton> buttons) {
		List<Picto> pictos = new ArrayList<Picto>();
		for (PictoButton button : buttons)
			pictos.add(button.getPicto());

		addTranslationResult(database, pictos);
	}
	private void addTranslationResult(String database, List<Picto> pictos) {
		LayoutInflater inflater = this.getLayoutInflater();

		LinearLayout l = (LinearLayout)inflater.inflate(R.layout.translation_result, this.lytTranslations, false);
		this.lytTranslations.addView(l);

		TextView lblDatase = (TextView)l.findViewById(R.id.translationResult_lblDatabase);
		LinearLayout lytPictos = (LinearLayout)l.findViewById(R.id.translationResult_lytPictos);

		lblDatase.setText(database);

		for (Picto picto : pictos) {
			FrameLayout p = (FrameLayout)inflater.inflate(R.layout.picto, lytPictos, false);
			lytPictos.addView(p);
			p.setTag(picto);

			TextView lblText = (TextView)p.findViewById(R.id.picto_lblText);
			ImageView image = (ImageView)p.findViewById(R.id.picto_imgPicto);

			picto.showInImageView(image, 75, 75);
			lblText.setText(picto.getTag());

			p.setOnClickListener(this.pictoClickedListener);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void addHistoryAndCacheToMessageList() {
		AbstractChatWithClientActivity.this.messageAdapter.clear();

		SortedSet<PastPictoMessage> history = this.historyMessagesManager.getMessages();
		List<PictoMessage> cache = getCacheMessages();
		for (PastPictoMessage message : history) {
			if (cache.contains(message))
				cache.remove(message);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			AbstractChatWithClientActivity.this.messageAdapter.addAll(AbstractChatWithClientActivity.this.historyMessagesManager.getMessages());
			AbstractChatWithClientActivity.this.messageAdapter.addAll(cache);
		}
		else {
			this.messages.addAll(AbstractChatWithClientActivity.this.historyMessagesManager.getMessages());
			this.messages.addAll(cache);
			this.messageAdapter.notifyDataSetChanged();
		}

		scrollToBottomWorkaround();
	}

	private void closePopup() {
		this.lytPopup.setVisibility(View.GONE);
	}

	private void clearPictos() {
		this.pictos.clear();
		this.lytMessage.removeAllViews();
	}
	private void clearTranslationResults() {
		this.lytTranslations.removeAllViews();
	}


	private void incrementBusy() {
		this.busyCounter++;
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AbstractChatWithClientActivity.this.prgBusy.setVisibility(View.VISIBLE);
			}
		});
	}
	private void decrementBusy() {
		this.busyCounter--;
		if (this.busyCounter == 0) {
			this.prgBusy.setVisibility(View.INVISIBLE);
			this.lytPopup.setVisibility(View.VISIBLE);
		}
	}

	private void onTappedInputPicto(View selected) {
		this.timer.cancel();

		if (this.selectedInputPicto != null) {
			Object tag = this.selectedInputPicto.getTag();
			if (tag instanceof View)
				((View)tag).setVisibility(View.GONE);
		}

		Object buttonTag = selected.getTag();
		if (buttonTag instanceof View)
			((View)buttonTag).setVisibility(View.VISIBLE);

		if (this.selectedInputPicto != selected) {
			this.selectedInputPicto = selected;

			this.timer = new Timer();
			this.timer.schedule(new TappedInputPictoTask(), Constants.TAPPED_INPUT_PICTO_DELAY);
		}
		else if (this.selectedInputPicto == selected) {

		}
	}
	private class TappedInputPictoTask extends TimerTask {
		@Override
		public void run() {
			AbstractChatWithClientActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Object tag = AbstractChatWithClientActivity.this.selectedInputPicto.getTag();
					if (tag instanceof View)
						((View)tag).setVisibility(View.GONE);

					AbstractChatWithClientActivity.this.selectedInputPicto = null;
				}
			});
		}
	};


	//Interface events
	private OnItemSelectedListener mSelect = new OnItemSelectedListener()
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id)
		{
			AbstractChatWithClientActivity.this.selected = Constants.Databases.get(position);
			String text = AbstractChatWithClientActivity.this.txtMessage.getText().toString();
			if (!text.isEmpty()) {
				String language = getLanguageFromLocale();

				if (language != null) {
					incrementBusy();
					AbstractChatWithClientActivity.this.textToPicto.get(text, AbstractChatWithClientActivity.this.selected.toLowerCase(), language, true, AbstractChatWithClientActivity.this.textToPictoResultReceiver);
				}
			}
		}
		@Override
		public void onNothingSelected(AdapterView<?> parentView)
		{
			AbstractChatWithClientActivity.this.selected = null;
	    }
	};
	private OnClickListener btnSendListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!AbstractChatWithClientActivity.this.pictos.isEmpty()) {
				String text = "";
				for (Picto picto : AbstractChatWithClientActivity.this.pictos)
					text += picto.getTag() + " ";

				PictoMessage message = new PictoMessage(text, AbstractChatWithClientActivity.this.coach.getFullName(), true);
				message.setPictos(new ArrayList<Picto>(AbstractChatWithClientActivity.this.pictos));

				if (false) {
				sendPictoMessage(message, new PictoSendResultReceiver(message) {
					@Override
					public void onSuccess(TextMessage message) {
						if (message instanceof PictoMessage) {
							AbstractChatWithClientActivity.this.messageAdapter.add((PictoMessage)message);
							scrollToBottomWorkaround();
						}

						AbstractChatWithClientActivity.this.clearPictos();
						AbstractChatWithClientActivity.this.txtMessage.setText("");
						closePopup();
						hideKeyboard();
					}

					@Override
					public void onError(Bundle resultData) {}
				});
				}
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
			AbstractChatWithClientActivity.this.timer.cancel();
			AbstractChatWithClientActivity.this.timer = new Timer();
			AbstractChatWithClientActivity.this.timer.schedule(new StoppedTypingTask(), Constants.STOPPED_TYPING_DELAY);
		}
	};
	private OnClickListener btnClosePopupListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			closePopup();
		}
	};
	private OnClickListener pictoClickedListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Object picto = v.getTag();
			if (picto instanceof Picto)
				addInputPicto((Picto)picto);
		}
	};
	private OnClickListener inputPictoListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			onTappedInputPicto(v);
		}
	};
	private OnClickListener deletePictoListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Object viewTag = v.getTag();
				if (viewTag instanceof View) {
					View view = (View)viewTag;
					Object pictoTag = view.getTag();
					if (pictoTag instanceof Picto) {
						Picto picto = (Picto)pictoTag;
						AbstractChatWithClientActivity.this.pictos.remove(picto);
					}
					AbstractChatWithClientActivity.this.lytMessage.removeView(view);
				}
			}
		};

	private IPictoMessageReceivedListener pictoMessageReceivedListener = new IPictoMessageReceivedListener() {
		@Override
		public void onPictoMessageReceived(Client client, PictoMessage message) {
			if (!ignoreMessage(client)) {
				App.logToFile(FileLogItem.debug(AbstractChatWithClientActivity.this, Tags.MSG, Messages.MESSAGE_RECEIVED, client.getFullName()));

				AbstractChatWithClientActivity.this.messageAdapter.add(message);
				scrollToBottomWorkaround();
			}
			else
				App.logToFile(FileLogItem.debug(AbstractChatWithClientActivity.this, Tags.MSG, Messages.MESSAGE_IGNORED, client.getFullName()));
		}
		@Override
		public void onPictoMessageReceived(User remote, PictoMessage message) {
			if (!ignorePrivateMessage(remote)) {
				App.logToFile(FileLogItem.debug(AbstractChatWithClientActivity.this, Tags.MSG, Messages.PRIVATE_MESSAGE_RECEIVED, remote.getFullName()));

				AbstractChatWithClientActivity.this.messageAdapter.add(message);
				scrollToBottomWorkaround();
			}
			else
				App.logToFile(FileLogItem.debug(AbstractChatWithClientActivity.this, Tags.MSG, Messages.PRIVATE_MESSAGE_IGNORED, remote.getFullName()));
		}
		@Override
		public void onPictoMessageReceived(Coach coach, Client client, PictoMessage message) {
			if (!ignoreMessage(client)) {
				App.logToFile(FileLogItem.debug(AbstractChatWithClientActivity.this, Tags.MSG, Messages.MESSAGE_RECEIVED, coach.getFullName()));

				AbstractChatWithClientActivity.this.messageAdapter.add(message);
				scrollToBottomWorkaround();
			}
			else
				App.logToFile(FileLogItem.debug(AbstractChatWithClientActivity.this, Tags.MSG, Messages.MESSAGE_IGNORED, coach.getFullName()));
		}
	};
	private IHistoryReceivedListener historyReceivedListener = new IHistoryReceivedListener() {
		@Override
		public void onHistoryReceived(PubnubChannel channel, boolean isPrivate, User host, User other, List<TextMessage> messages) {
			if (!ignoreHistory(isPrivate, host)) {
				for (TextMessage textMessage : messages) {
					if (textMessage instanceof PastPictoMessage) {
						PastPictoMessage pictoMessage = (PastPictoMessage)textMessage;
						AbstractChatWithClientActivity.this.historyMessagesManager.addMessage(pictoMessage);
					}
				}

				addHistoryAndCacheToMessageList();
			}
		}
	};
	private IPresenceReceivedListener presenceReceivedListener = new IPresenceReceivedListener() {
		@Override
		public void onPresenceReceived(PubnubChannel channel, boolean isPrivate, User host, User user, Presence presence, AppState appState, Date lastRead) {
			if (user instanceof Client)
				if (lastRead != null)
					AbstractChatWithClientActivity.this.messageAdapter.notifyDataSetChanged();
		}
	};


	//Manager listeners
	private TextToPictoResultReceiver textToPictoResultReceiver = new TextToPictoResultReceiver("clientChatTextChanged") {
		@Override
		public void onServerError(String json) {
			clearPictos();

			//TODO Translate me
			Toast.makeText(AbstractChatWithClientActivity.this, getResources().getString(R.string.error_translate_server_error), Toast.LENGTH_LONG).show();
			Log.e("ttp", json);
		}
		@Override
		public void onClientError() {
			clearPictos();

			//TODO Translate me
			Toast.makeText(AbstractChatWithClientActivity.this, getResources().getString(R.string.error_translate_client_error), Toast.LENGTH_LONG).show();
			Log.e("ttp", "");
		}
		@Override
		public void onTimedOut() {
			clearPictos();

			//TODO Translate me
			Toast.makeText(AbstractChatWithClientActivity.this, getResources().getString(R.string.error_translate_timeout), Toast.LENGTH_LONG).show();
			Log.e("ttp", "");
		}
		@Override
		public void onFinished() {
			decrementBusy();
		}

		@Override
		protected void onPictosLoaded(List<Picto> pictos) {
			clearPictos();
			for (Picto picto : pictos)
				addInputPicto(picto);

			hideKeyboard();
		}
	};


	//Helper functions
	private String getLanguageFromLocale() {
		String langCode = Locale.getDefault().getISO3Language();
		String language = Constants.Languages.get(langCode.substring(0, 2));

		return language;
	}
	private void scrollToBottomWorkaround() {
		final int index = this.messageAdapter.getCount() - 1;
		this.lstMessages.post(new Runnable() {
			@Override
			public void run() {
				AbstractChatWithClientActivity.this.lstMessages.smoothScrollToPosition(index);
				AbstractChatWithClientActivity.this.lstMessages.post(new Runnable() {
					@Override
					public void run() {
						AbstractChatWithClientActivity.this.lstMessages.setSelection(index);
					}
				});
			}
		});
	}
	private void hideKeyboard() {
		this.lytDummy.requestFocus();
	    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(this.txtMessage.getWindowToken(), 0);
	}


	protected abstract boolean ignoreMessage(Client client);
	protected abstract boolean ignorePrivateMessage(User remote);
	protected abstract boolean ignoreHistory(boolean isPrivate, User host);

	protected abstract Date getLastReadTime();
	protected abstract List<PictoMessage> getCacheMessages();

	protected abstract void sendPictoMessage(final PictoMessage message, PictoSendResultReceiver receiver);


	private class StoppedTypingTask extends TimerTask {
		@Override
		public void run() {
			String text = AbstractChatWithClientActivity.this.txtMessage.getText().toString();
			if (!text.isEmpty()) {
				String language = getLanguageFromLocale();

				if (language != null) {
					incrementBusy();
					AbstractChatWithClientActivity.this.textToPicto.get(text, AbstractChatWithClientActivity.this.selected.toLowerCase(), language, true, AbstractChatWithClientActivity.this.textToPictoResultReceiver);
				}
			}
		}
	}
	private class MessagesArrayAdapter extends ArrayAdapter<PictoMessage>
	{
		private Activity context;
		private List<PictoMessage> messages;
		private Date lastReadTime;
		private DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

		public MessagesArrayAdapter(Activity context, List<PictoMessage> messages) {
			super(context, R.layout.client_item, messages);

			this.context = context;
			this.messages = messages;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView;
			Context context = AbstractChatWithClientActivity.this;
			LayoutInflater inflater = this.context.getLayoutInflater();

			if (convertView != null)
				rowView = convertView;
			else {
				if(getItemViewType(position) == 0)
					rowView = inflater.inflate(R.layout.received_chat, parent, false);
		        else
		        	rowView = inflater.inflate(R.layout.sent_chat, parent, false);
			}

			final ImageView receivedImageView = (ImageView)rowView.findViewById(R.id.receivedChat_imgAvatar);
			ImageView sentImageView = (ImageView)rowView.findViewById(R.id.sentChat_imgAvatar);
			FlowLayout receivedMessagePictos = (FlowLayout)rowView.findViewById(R.id.receivedChat_lytPictos);
			TextView receivedTime = (TextView)rowView.findViewById(R.id.receivedChat_lblTime);
			ImageView imgRead = (ImageView)rowView.findViewById(R.id.sentChat_imgRead);
			FlowLayout sentMessagePictos = (FlowLayout)rowView.findViewById(R.id.sentChat_lytPictos);

			int size = (int)(AbstractChatWithClientActivity.this.dpppx * 70);

			//Insert data
			PictoMessage message = this.messages.get(position);
			if (receivedMessagePictos != null) {
				Date time = message.getServerTime();
				if (AbstractChatWithClientActivity.this.lastCheckedTime == null || (time != null && time.after(AbstractChatWithClientActivity.this.lastCheckedTime)))
					rowView.setBackgroundColor(getResources().getColor(R.color.unread_message_background));
				else
					rowView.setBackgroundColor(getResources().getColor(R.color.received_message_background));

				receivedMessagePictos.removeAllViews();
				for (Picto picto : message.getPictos()) {
					FrameLayout lytPicto = (FrameLayout)inflater.inflate(R.layout.picto, receivedMessagePictos, false);

					TextView pictoText = (TextView)lytPicto.findViewById(R.id.picto_lblText);
					ImageView pictoImage = (ImageView)lytPicto.findViewById(R.id.picto_imgPicto);

					picto.showInImageView(pictoImage, size, size);
					pictoText.setText(picto.getTag());

					receivedMessagePictos.addView(lytPicto);
				}
			}
			if (receivedTime != null) {
				Date time = message.getServerTime();
				if (time != null)
					receivedTime.setText(this.df.format(time));
			}
			if (sentMessagePictos != null) {
				sentMessagePictos.removeAllViews();
				for (Picto picto : message.getPictos()) {
					FrameLayout lytPicto = (FrameLayout)inflater.inflate(R.layout.picto, sentMessagePictos, false);

					TextView pictoText = (TextView)lytPicto.findViewById(R.id.picto_lblText);
					ImageView pictoImage = (ImageView)lytPicto.findViewById(R.id.picto_imgPicto);

					picto.showInImageView(pictoImage, size, size);
					pictoText.setText(picto.getTag());

					sentMessagePictos.addView(lytPicto);
				}
			}
			if (imgRead != null) {
				if (this.lastReadTime != null) {
					if (message.getServerTime().after(this.lastReadTime))
						imgRead.setImageResource(R.drawable.ic_markunread_black_36dp);
					else
						imgRead.setImageResource(R.drawable.ic_drafts_black_36dp);
				}
				else
					imgRead.setImageDrawable(null);
			}
			if (sentImageView != null) {
				sentImageView.setImageResource(R.drawable.ic_person_pin_black_48dp);
				message.showInImageView(sentImageView, 75, 75);
			}
			if (receivedImageView != null) {
				receivedImageView.setImageResource(R.drawable.ic_person_pin_black_48dp);
				message.showInImageView(receivedImageView, 75, 75);
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


		@Override
		public void notifyDataSetChanged() {
			this.lastReadTime = getLastReadTime();

			super.notifyDataSetChanged();
		}
	}
}