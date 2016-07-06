package be.kpoint.pictochat.app.activities.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class HorizontalGestureScrollView extends HorizontalScrollView
{
	private GestureDetector gestureDetector;

	public HorizontalGestureScrollView(Context context) {
		super(context);

		init();
	}
	public HorizontalGestureScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}
	public HorizontalGestureScrollView(Context context, AttributeSet attrs,	int defStyle) {
		super(context, attrs, defStyle);

		init();
	}

	private void init() {
		this.gestureDetector = new GestureDetector(getContext(), new GestureListener());
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean handled = this.gestureDetector.onTouchEvent(ev);

		if (handled && ev.getAction() != MotionEvent.ACTION_DOWN)
			return false;

		return super.onInterceptTouchEvent(ev);
	}

	private class GestureListener extends SimpleOnGestureListener
	{
		private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
        	//Must return true;
        	return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        	return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        	try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                }

                return true;
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        	return false;
        }
	}

    public void onSwipeRight() {}
    public void onSwipeLeft() {}
    public void onSwipeTop() {}
    public void onSwipeBottom() {}
}
