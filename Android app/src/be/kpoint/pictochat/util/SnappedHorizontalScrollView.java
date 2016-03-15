package be.kpoint.pictochat.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class SnappedHorizontalScrollView extends HorizontalScrollView
{
	private static final int SWIPE_MIN_DISTANCE = 5;
	private static final int SWIPE_THRESHOLD_VELOCITY = 300;


	private ISelectedItemChangedListener selectedItemChangedListener;

	private GestureDetector flingDetector;
	private LinearLayout wrapper;

	private Integer selected = null;

	private int itemSize;
	private int itemMargin;
	private int itemPadding;

	public SnappedHorizontalScrollView(Context context, AttributeSet attrs,	int defStyle) {
		super(context, attrs, defStyle);

		init(context);
	}
	public SnappedHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context);
	}
	public SnappedHorizontalScrollView(Context context) {
		super(context);

		init(context);
	}
	private void init(Context context) {
		this.flingDetector = new GestureDetector(context, new FlingGestureListener());

		this.wrapper = new LinearLayout(context);
		this.wrapper.setOrientation(LinearLayout.HORIZONTAL);
		this.wrapper.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		this.addView(this.wrapper);

		this.setOnTouchListener(this.swipeListener);
	}

	public void setSelectedItemChangedListener(ISelectedItemChangedListener listener) {
		this.selectedItemChangedListener = listener;
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);

		int width = this.getWidth();

		this.itemPadding = (int)(width * 0.1);
		this.itemMargin = (int)(width * 0.05);
		this.itemSize = (int)(width * 0.8);

		int offset = (width - this.itemSize) / 2;
		this.wrapper.setPadding(offset, 0, offset, 0);
	}

	public void addRawChildView(View view) {
		this.wrapper.addView(view);
	}
	public void addChildView(View view, int background) {
		LinearLayout frame = new LinearLayout(getContext());
		frame.setOrientation(LinearLayout.VERTICAL);
		frame.setGravity(Gravity.CENTER_VERTICAL);
		frame.setLayoutParams(new LayoutParams(this.itemSize, this.itemSize));
    	frame.setBackgroundResource(background);
    	frame.setPadding(this.itemPadding, this.itemPadding, this.itemPadding, this.itemPadding);

    	//view.setOnTouchListener(this.swipeListener);

		frame.addView(view);

		this.wrapper.addView(frame);
	}

	@Override
	public void removeAllViews() {
		this.wrapper.removeAllViews();
	}

	private int snappedScrollPosition(int scroll) {
		int totalWidth = this.wrapper.getPaddingLeft();

		for (int i = 0; i < this.wrapper.getChildCount(); i++) {
			int offset = (this.getWidth() - this.itemSize) / 2;

			if (totalWidth - offset + this.itemSize / 2 > scroll) {
				if (this.selected == null || this.selected != i) {
					this.selected = i;
					onSelectedItemChanged();
				}

				return totalWidth - offset;
			}

			totalWidth += this.itemSize;
		}

		return 0;
	}

	public void scrollToElement(int index) {
		int totalWidth = this.wrapper.getPaddingLeft();

		if (index >= this.wrapper.getChildCount())
			index = this.wrapper.getChildCount() - 1;

		for (int i = 0; i < index; i++)
			totalWidth += this.itemSize;

		int offset = (this.getWidth() - this.itemSize) / 2;

		if (this.selected == null || this.selected != index) {
			this.selected = index;
			onSelectedItemChanged();
		}

		smoothScrollTo(totalWidth - offset, 0);
	}
	public void scrollToNextElement() {
		if (this.selected != null && this.selected < this.wrapper.getChildCount() - 1)
			scrollToElement(this.selected + 1);
	}
	public void scrollToPreviousElement() {
		if (this.selected != null && this.selected > 0)
			scrollToElement(this.selected - 1);
	}


	public View getViewFromIndex(int index) {
		return this.wrapper.getChildAt(index);
	}
	public int getViewIndex(View view) {
		return this.wrapper.indexOfChild(view);
	}

	private OnTouchListener swipeListener = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (SnappedHorizontalScrollView.this.flingDetector.onTouchEvent(event)) {
				return true;
			}
			else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
				smoothScrollTo(snappedScrollPosition(getScrollX()), 0);

				return true;
			}

			return false;
		}
	};

	public void onSelectedItemChanged() {
		if (this.selectedItemChangedListener != null)
			this.selectedItemChangedListener.onSelectedItemChanged(this.selected, this.selected != null ? this.wrapper.getChildAt(this.selected) : null);
	}

	class FlingGestureListener extends SimpleOnGestureListener
	{
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (e1 == null || e2 == null)
				return false;

			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				scrollToNextElement();
				return true;
			}
			else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				scrollToPreviousElement();
				return true;
			}

			return false;
		}

		/*@Override
		public boolean onDown(MotionEvent e) {
			//Toast.makeText(SnappedHorizontalScrollView.this.getContext(), "Down", Toast.LENGTH_SHORT).show();
			super.onDown(e);
			return true;
		}*/
	}

	public interface ISelectedItemChangedListener {
		public void onSelectedItemChanged(Integer index, View item);
	}
}
