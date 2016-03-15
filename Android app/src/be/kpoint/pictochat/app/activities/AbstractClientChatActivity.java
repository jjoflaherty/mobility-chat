package be.kpoint.pictochat.app.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apmem.tools.layouts.FlowLayout;

import be.kpoint.pictochat.App;
import be.kpoint.pictochat.api.rest.client.ClientManager;
import be.kpoint.pictochat.api.rest.client.ClientResultReceiver;
import be.kpoint.pictochat.api.rest.ids.PageId;
import be.kpoint.pictochat.app.Constants;
import be.kpoint.pictochat.app.R;
import be.kpoint.pictochat.app.activities.components.HistoryMessagesManager;
import be.kpoint.pictochat.app.domain.Client;
import be.kpoint.pictochat.app.domain.Coach;
import be.kpoint.pictochat.app.domain.Friend;
import be.kpoint.pictochat.app.domain.Page;
import be.kpoint.pictochat.app.domain.PastPictoMessage;
import be.kpoint.pictochat.app.domain.Picto;
import be.kpoint.pictochat.app.domain.PictoMessage;
import be.kpoint.pictochat.app.domain.TextMessage;
import be.kpoint.pictochat.app.domain.buttons.CallCoachButton;
import be.kpoint.pictochat.app.domain.buttons.CallCoachButton.ICallCoachButtonClickedListener;
import be.kpoint.pictochat.app.domain.buttons.NavigateAndPictoButton;
import be.kpoint.pictochat.app.domain.buttons.NavigateAndPictoButton.INavigateAndPictoButtonClickedListener;
import be.kpoint.pictochat.app.domain.buttons.NavigateButton;
import be.kpoint.pictochat.app.domain.buttons.NavigateButton.INavigateButtonClickedListener;
import be.kpoint.pictochat.app.domain.buttons.PictoButton;
import be.kpoint.pictochat.app.domain.buttons.PictoButton.IPictoButtonClickedListener;
import be.kpoint.pictochat.business.comm.PictoSendResultReceiver;
import be.kpoint.pictochat.business.comm.interfaces.IHistoryReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.IPictoMessageReceivedListener;
import be.kpoint.pictochat.business.comm.interfaces.ITextMessageReceivedListener;
import be.kpoint.pictochat.comm.pubnub.PubnubChannel;
import be.kpoint.pictochat.util.SnappedHorizontalScrollView;

public abstract class AbstractClientChatActivity extends Activity
{
	private static final String EXTRA_CLIENT = "client";


	//Managers
	private ClientManager clientManager;

	//Interface objects
	private LinearLayout lytMain;
	private FlowLayout lytMessage;
	private Button btnSend;
	private ListView lstMessages;
	private ImageView imgEmpty;
	private LinearLayout lytDrawer;
	private ImageView imgChevron;
	private LinearLayout lytControls;
	private SnappedHorizontalScrollView snappedScroll;
	private MessagesArrayAdapter messageAdapter;

	private Client client;
	private HistoryMessagesManager historyMessagesManager;

	private float dpppx;
	private List<PictoMessage> messages = new ArrayList<PictoMessage>();
	private List<Picto> pictos = new ArrayList<Picto>();

	private View selectedInputPicto = null;
	private Timer timer;


	//Life cycle
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client);

		this.dpppx = getResources().getDisplayMetrics().density;

		this.lytMain = (LinearLayout)findViewById(R.id.client_lytMain);
		this.lytMessage = (FlowLayout)findViewById(R.id.client_lytMessage);
		this.btnSend = (Button)findViewById(R.id.client_btnSend);
		this.lstMessages = (ListView)findViewById(R.id.client_lstMessages);
		this.imgEmpty = (ImageView)findViewById(R.id.client_imgEmpty);
		this.lytDrawer = (LinearLayout)findViewById(R.id.client_lytDrawer);
		this.imgChevron = (ImageView)findViewById(R.id.client_imgChevron);
		this.lytControls = (LinearLayout)findViewById(R.id.client_lytControls);
		this.snappedScroll = (SnappedHorizontalScrollView)findViewById(R.id.client_hsvColumns);

		this.messageAdapter = new MessagesArrayAdapter(this, this.messages);
		this.lstMessages.setAdapter(this.messageAdapter);
		this.lstMessages.setEmptyView(this.imgEmpty);

		this.lytDrawer.setOnClickListener(this.emptyListListener);
		this.btnSend.setOnClickListener(this.btnSendListener);
		this.imgEmpty.setOnClickListener(this.emptyListListener);
		this.lstMessages.setOnItemClickListener(this.lstMessagesListener);

		this.clientManager = new ClientManager(this);
		this.historyMessagesManager = new HistoryMessagesManager();

		this.timer = new Timer();

		Bundle bundle = getIntent().getExtras();
	    if (bundle != null) {
	    	Client client = (Client)bundle.get(EXTRA_CLIENT);
	    	this.clientManager.get(client.getId(), this.loadClientReceiver);
	    }
	}
	@Override
	protected void onResume() {
		addHistoryAndCacheToMessageList();

		super.onResume();
	}
	@Override
	protected void onDestroy() {
		App.removeTextMessageReceivedListener(this.textMessageReceivedListener);
		App.removePictoMessageReceivedListener(this.pictoMessageReceivedListener);
		App.removeHistoryReceivedListener(this.historyReceivedListener);

		super.onDestroy();
	}


	protected void setupCommunications(Client client) {
		App.addTextMessageReceivedListener(this.textMessageReceivedListener);
		App.addPictoMessageReceivedListener(this.pictoMessageReceivedListener);
		App.addHistoryReceivedListener(this.historyReceivedListener);

		this.historyMessagesManager.clear();
	}


	private void addInputPicto(Picto picto) {
		this.pictos.add(picto);

		LayoutInflater inflater = this.getLayoutInflater();
		LinearLayout l = (LinearLayout)inflater.inflate(R.layout.input_picto, this.lytMessage, false);
		l.setTag(picto);
		this.lytMessage.addView(l);

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
	}
	@TargetApi(16)
	private void addButtonsToGrid(Page page) {
		Map<Integer, be.kpoint.pictochat.app.domain.Button> buttons = new HashMap<Integer, be.kpoint.pictochat.app.domain.Button>();
		for (be.kpoint.pictochat.app.domain.Button button : page.getButtons()) {
			if (!buttons.containsKey(button.getCell() - 1))
				buttons.put(button.getCell() - 1, button);
		}

		int columns = page.getColumns();
		int amount = page.getRows() * columns;

		this.snappedScroll.removeAllViews();
		List<LinearLayout> list = addColumnsToList(columns);

		for (int i = 0; i < amount; i++) {
			LinearLayout lyt = buildButtonLayout(R.drawable.bluesquare, 120, 20);

			if (buttons.containsKey(i)) {
				final be.kpoint.pictochat.app.domain.Button b = buttons.get(i);

				/*TextView textView = new TextView(this);
		    	textView.setText("Id: " + b.getId());
		    	textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		    	lyt.addView(textView);*/

				ImageView image = new ImageView(this);
				image.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				image.setScaleType(ScaleType.FIT_CENTER);
				image.setAdjustViewBounds(true);
				lyt.addView(image);
				b.showInImageView(image, 120, 120);

		    	lyt.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						b.doAction();
					}
				});

		    	if (b instanceof PictoButton) {
		    		((PictoButton)b).setPictoButtonClickedListener(this.pictoButtonClickedListener);
		    	} else if (b instanceof NavigateButton) {
		    		((NavigateButton)b).setNavigateButtonClickedListener(this.navigateButtonClickedListener);
		    	} else if (b instanceof NavigateAndPictoButton) {
		    		((NavigateAndPictoButton)b).setNavigateAndPictoButtonClickedListener(this.navigateAndPictoButtonClickedListener);
		    	} else if (b instanceof CallCoachButton) {
		    		((CallCoachButton)b).setCallCoachButtonClickedListener(this.callCoachButtonClickedListener);
		    	}

		    	try {
		    		int color = Color.parseColor(b.getColor());

		    		final int w = (int)(120 * this.dpppx);
		    		final int h = (int)(120 * this.dpppx);

		    		Bitmap bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		    		Canvas canvas = new Canvas(bitmap);

		    		Paint bgPaint = new Paint();
		    		bgPaint.setColor(color);
		    		canvas.drawRect(0, 0, w, h, bgPaint);

		    		Paint maskPaint = new Paint();
		    		maskPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		    		Bitmap mask = BitmapFactory.decodeResource(getResources(), R.drawable.button_mask);
		    		canvas.drawBitmap(mask, new Rect(0, 0, mask.getWidth(), mask.getHeight()), new Rect(0, 0, w, h), maskPaint);
		    		mask.recycle();

		    		Paint overlayPaint = new Paint();
		    		overlayPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
		    		Bitmap overlay = BitmapFactory.decodeResource(getResources(), R.drawable.button_overlay);
		    		canvas.drawBitmap(overlay, new Rect(0, 0, overlay.getWidth(), overlay.getHeight()), new Rect(0, 0, w, h), overlayPaint);
		    		overlay.recycle();

		    		Drawable drawable = new BitmapDrawable(getResources(), bitmap);
		    		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
		    			lyt.setBackground(drawable);
		    		else
		    			lyt.setBackgroundDrawable(drawable);

			    	//lyt.setLayoutParams(new LayoutParams(width, height));
		    	}
		    	catch (IllegalArgumentException e) {
		    		e.printStackTrace();
		    	}
			}

			list.get(i % columns).addView(lyt);
		}
	}
	private List<LinearLayout> addColumnsToList(int amount) {
		List<LinearLayout> list = new ArrayList<LinearLayout>();

		for (int i = 0; i < amount; i++) {
			LinearLayout lyt = new LinearLayout(this);
			lyt.setOrientation(LinearLayout.VERTICAL);
			lyt.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));

			this.snappedScroll.addRawChildView(lyt);

			list.add(lyt);
		}

    	return list;
	}
	private LinearLayout buildButtonLayout(int background, int size, int padding) {
		LinearLayout lyt = new LinearLayout(this);
		lyt.setOrientation(LinearLayout.VERTICAL);
		lyt.setGravity(Gravity.CENTER);
		lyt.setLayoutParams(new LayoutParams(size, size));
		lyt.setBackgroundResource(background);
		lyt.setPadding(padding, padding, padding, padding);

    	return lyt;
	}


	private void addHistoryAndCacheToMessageList() {
		AbstractClientChatActivity.this.messageAdapter.clear();

		SortedSet<PastPictoMessage> history = this.historyMessagesManager.getMessages();
		List<PictoMessage> cache = App.getMessages(this.client);
		for (PictoMessage message : history) {
			if (cache.contains(message))
				cache.remove(message);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			AbstractClientChatActivity.this.messageAdapter.addAll(AbstractClientChatActivity.this.historyMessagesManager.getMessages());
			AbstractClientChatActivity.this.messageAdapter.addAll(App.getMessages(this.client));
		}
		else {
			this.messages.addAll(AbstractClientChatActivity.this.historyMessagesManager.getMessages());
			this.messages.addAll(App.getMessages(this.client));
			this.messageAdapter.notifyDataSetChanged();
		}

		scrollToBottomWorkaround();
	}
	private void scrollToBottomWorkaround() {
		final int index = AbstractClientChatActivity.this.messageAdapter.getCount() - 1;
		AbstractClientChatActivity.this.lstMessages.post(new Runnable() {
			@Override
			public void run() {
				AbstractClientChatActivity.this.lstMessages.smoothScrollToPosition(index);
				AbstractClientChatActivity.this.lstMessages.post(new Runnable() {
					@Override
					public void run() {
						AbstractClientChatActivity.this.lstMessages.setSelection(index);
					}
				});
			}
		});
	}


	private void toggle() {
		if (this.lytControls.getVisibility() == View.VISIBLE)
			collapse();
		else
			expand();
	}
	private void expand() {
		final View v = this.lytControls;

		this.imgChevron.setImageResource(R.drawable.ic_expand_more_white_48dp);

		v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		final int targetHeight = v.getMeasuredHeight();

		v.getLayoutParams().height = 0;
	    v.setVisibility(View.VISIBLE);
	    Animation a = new Animation()
	    {
	        @Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
	        	v.getLayoutParams().height = (interpolatedTime == 1) ? LayoutParams.WRAP_CONTENT : (int)(targetHeight * interpolatedTime);
	            v.requestLayout();

	            super.applyTransformation(interpolatedTime, t);
			}

			@Override
	        public boolean willChangeBounds() {
	            return true;
	        }
	    };

	    a.setDuration(500);
	    v.startAnimation(a);
	}
	private void collapse() {
		final View v = this.lytControls;

		this.imgChevron.setImageResource(R.drawable.ic_expand_less_white_48dp);

		v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		final int targetHeight = v.getMeasuredHeight();

		v.getLayoutParams().height = targetHeight;
	    Animation a = new Animation()
	    {
	        @Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
	        	v.getLayoutParams().height = (int)(targetHeight * (1 - interpolatedTime));
	            v.requestLayout();

	            super.applyTransformation(interpolatedTime, t);
			}

			@Override
	        public boolean willChangeBounds() {
	            return true;
	        }
	    };

	    a.setDuration(500);
	    a.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				v.setVisibility(View.GONE);
			}
		});
	    v.startAnimation(a);
	}


	private void onTappedInputPicto(View selected) {
		this.timer.cancel();

		if (this.selectedInputPicto != null) {
			Object tag = AbstractClientChatActivity.this.selectedInputPicto.getTag();
			if (tag instanceof View)
				((View)tag).setVisibility(View.GONE);
		}

		Object tag = selected.getTag();
		if (tag instanceof View)
			((View)tag).setVisibility(View.VISIBLE);

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
			AbstractClientChatActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Object tag = AbstractClientChatActivity.this.selectedInputPicto.getTag();
					if (tag instanceof View)
						((View)tag).setVisibility(View.GONE);

					AbstractClientChatActivity.this.selectedInputPicto = null;
				}
			});
		}
	};


	protected void sendPictoMessage(final PictoMessage message, PictoSendResultReceiver receiver) {}


	//Manager listeners
	private ClientResultReceiver loadClientReceiver = new ClientResultReceiver("loadClient")
	{
		@Override
		protected void onClientLoaded(be.kpoint.pictochat.api.rest.client.Client c) {
			AbstractClientChatActivity.this.client = Client.buildFromRest(c);

			addButtonsToGrid(AbstractClientChatActivity.this.client.getStartPage());
		}

		@Override
		public void onFinished() {
			//ClientActivity.this.hideWaitSpinner();
		}

		@Override
		public void onTimedOut() {
			//App.logToFile(FileLogItem.warn(LoginActivity.this, Tags.REST, Messages.LOGIN_TIMED_OUT));

			//Toast.makeText(LoginActivity.this, VpadMessages.Toast.timeout(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onClientError() {
			//App.logToFile(FileLogItem.error(LoginActivity.this, Tags.REST, Messages.LOGIN_CLIENT_ERROR));
		}

		@Override
		public void onServerError(String json) {
			//App.logToFile(FileLogItem.error(LoginActivity.this, Tags.REST, Messages.LOGIN_SERVER_ERROR, json));

			Toast.makeText(AbstractClientChatActivity.this, "De server gaf een ongeldig resultaat", Toast.LENGTH_LONG).show();
		}
	};


	//Interface events
	private OnClickListener btnSendListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (AbstractClientChatActivity.this.pictos.size() == 0)
				return;

			String text = "";
			for (Picto picto : AbstractClientChatActivity.this.pictos)
				text += picto.getTag() + " ";

			final PictoMessage message = new PictoMessage(text, AbstractClientChatActivity.this.client.getFullName(), true);
			message.setPictos(new ArrayList<Picto>(AbstractClientChatActivity.this.pictos));
			sendPictoMessage(message, new PictoSendResultReceiver(message) {
				@Override
				public void onSuccess(TextMessage message) {
					if (message instanceof PictoMessage) {
						AbstractClientChatActivity.this.messageAdapter.add((PictoMessage)message);
						scrollToBottomWorkaround();
					}
				}

				@Override
				public void onError(Bundle resultData) {}
			});

			AbstractClientChatActivity.this.pictos.clear();
			AbstractClientChatActivity.this.lytMessage.removeAllViews();
		}
	};
	private OnClickListener emptyListListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			toggle();
		}
	};
	private OnItemClickListener lstMessagesListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			toggle();
		}
	};
	private OnClickListener inputPictoListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			AbstractClientChatActivity.this.onTappedInputPicto(v);
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
					AbstractClientChatActivity.this.pictos.remove(picto);
				}
				AbstractClientChatActivity.this.lytMessage.removeView(view);
			}
		}
	};
	private IPictoButtonClickedListener pictoButtonClickedListener = new IPictoButtonClickedListener() {
		@Override
		public void onClick(Picto picto) {
			addInputPicto(picto);
		}
	};
	private INavigateButtonClickedListener navigateButtonClickedListener = new INavigateButtonClickedListener() {
		@Override
		public void onClick(PageId pageId) {
			Page page = AbstractClientChatActivity.this.client.getPage(pageId);
			Log.d("", page.getName());

			addButtonsToGrid(page);
		}
	};
	private INavigateAndPictoButtonClickedListener navigateAndPictoButtonClickedListener = new INavigateAndPictoButtonClickedListener() {
		@Override
		public void onClick(Picto picto, PageId pageId) {
			addInputPicto(picto);

			Page page = AbstractClientChatActivity.this.client.getPage(pageId);
			Log.d("", page.getName());

			addButtonsToGrid(page);
		}
	};
	private ICallCoachButtonClickedListener callCoachButtonClickedListener = new ICallCoachButtonClickedListener() {
		@Override
		public void onClick(String phoneNr) {
			Intent intent = new Intent(Intent.ACTION_CALL);
	        intent.setData(Uri.parse("tel:" + phoneNr));
	        AbstractClientChatActivity.this.startActivity(intent);
		}
	};


	private ITextMessageReceivedListener textMessageReceivedListener = new ITextMessageReceivedListener() {
		@Override
		public void onTextMessageReceived(TextMessage message) {
			TextView textView = new TextView(AbstractClientChatActivity.this);
			textView.setText(message.getSenderName() + ": " + message.getText());

			//ClientActivity.this.lytMessages.addView(textView);
		}
	};
	private IPictoMessageReceivedListener pictoMessageReceivedListener = new IPictoMessageReceivedListener() {
		@Override
		public void onPictoMessageReceived(Coach coach, PictoMessage message) {
			AbstractClientChatActivity.this.messageAdapter.add(message);
			scrollToBottomWorkaround();
		}

		@Override
		public void onPictoMessageReceived(Friend friend, PictoMessage message) {
			AbstractClientChatActivity.this.messageAdapter.add(message);
			scrollToBottomWorkaround();
		}

		@Override
		public void onPictoMessageReceived(Client client, PictoMessage message) {
			Log.d("", message.getText());
		}
	};
	private IHistoryReceivedListener historyReceivedListener = new IHistoryReceivedListener() {
		@Override
		public void onHistoryReceived(PubnubChannel channel, List<TextMessage> messages) {
			for (TextMessage textMessage : messages) {
				if (textMessage instanceof PastPictoMessage) {
					PastPictoMessage pictoMessage = (PastPictoMessage)textMessage;
					AbstractClientChatActivity.this.historyMessagesManager.addMessage(pictoMessage);
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
			Context context = AbstractClientChatActivity.this;

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
			final FlowLayout receivedMessagePictos = (FlowLayout)rowView.findViewById(R.id.receivedChat_lytPictos);
			TextView receivedText = (TextView)rowView.findViewById(R.id.room_lblLastName);
			FlowLayout sentMessagePictos = (FlowLayout)rowView.findViewById(R.id.sentChat_lytPictos);
			TextView sentText = (TextView)rowView.findViewById(R.id.sentChat_lblText);

			int size = (int)(AbstractClientChatActivity.this.dpppx * 70);
			int padding = (int)(AbstractClientChatActivity.this.dpppx * 10);

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
