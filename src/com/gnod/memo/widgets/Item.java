package com.gnod.memo.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gnod.activity.R;
import com.gnod.memo.activity.App;
import com.gnod.memo.gesture.MoveGestureDetector;

public class Item extends RelativeLayout {

	private GestureDetector dector = null;
	private GestureListener listener = null;
	private MoveGestureDetector mMoveDetector = null;
	private TextView textView = null;
	private TextView timeView = null;
	private int mFocusX = 0;
	private boolean canMoved;
	private boolean isInit = false;
	private static ItemAnimPool animPool = null;
	
	public Item(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setAnimPool(ItemAnimPool pool){
		animPool = pool;
	}
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		textView = (TextView)findViewById(R.id.list_item_title);
		timeView = (TextView)findViewById(R.id.list_item_time);
	}
	
	public void showInitAnimation(int delay){
		if(!isInit) {
			isInit = true;
			Animation set = animPool.getAnimation();
			set.setStartOffset(delay);
			set.setAnimationListener(new AnimationListener() {
				@Override public void onAnimationStart(Animation animation) {}
				@Override public void onAnimationRepeat(Animation animation) {}
				@Override
				public void onAnimationEnd(final Animation animation) {
					new Handler().post(new Runnable() {
						@Override
						public void run() {
							animPool.recyle(animation);
						}
					});
				}
			});
			setAnimation(set);
		}
	}

	public void showScrollLeftAnimation(){
		AnimationSet set = new AnimationSet(true);
		TranslateAnimation tanim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -0.8f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
		tanim.setDuration(400);
		AlphaAnimation alphaAnim = new AlphaAnimation(1, 0);
		alphaAnim.setStartOffset(100);
		alphaAnim.setDuration(300);
		set.addAnimation(tanim);
		set.addAnimation(alphaAnim);
		setAnimation(set);
	}

	public void setOnGestureListener(GestureListener listener){
		this.listener = listener;
		dector = new GestureDetector(getContext(), gestureListener);
	}

	public void enableMoved(boolean enabled){
		canMoved = enabled;
		if(mMoveDetector == null){
			mMoveDetector = new MoveGestureDetector(getContext(), new MoveListener());
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		if(canMoved){
			boolean moveResult = mMoveDetector.onTouchEvent(event);
        }

		if(dector != null){
	        dector.onTouchEvent(event);
	    }

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			App.setPosition((Integer)getTag());
			textView.setTextColor(Color.BLACK);
			timeView.startAnimation(animPool.getTextAnimation());
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			textView.setTextColor(Color.WHITE);
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			textView.setTextColor(Color.BLACK);
		} else {
			textView.setTextColor(Color.WHITE);
		}

		 return true;
	}
	
	private class MoveListener extends MoveGestureDetector.SimpleOnMoveGestureListener {
		
		@Override
		public boolean onMove(MoveGestureDetector detector) {
			PointF d = detector.getFocusDelta();
			mFocusX += d.x;
			setPadding( 15 + mFocusX, 15, (15 - mFocusX), 0);
			return true;
		}

		@Override
		public void onMoveEnd(MoveGestureDetector detector) {
			int move = mFocusX;
			mFocusX = 0;
			setPadding( 15 + mFocusX, 15, (15 - mFocusX), 0);
			if(move > 120){
				new Handler().post(new Runnable() {
					public void run() {
						listener.onScrollRightUp(Item.this);
					}
				});
			}else if(move < -120){
				new Handler().post(new Runnable() {
					public void run() {
						listener.onScrollLeftUp(Item.this);
					}
				});
			}
		}

		@Override
		public boolean onMoveBegin(MoveGestureDetector detector) {

			return true;
		}
	}
	
	private GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
		@Override
		public boolean onSingleTapUp(final MotionEvent e) {
			TranslateAnimation anim = new TranslateAnimation(
	                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.8f,
	                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
	                );
			anim.setDuration(100);
	        startAnimation(anim);
	        new Handler().post(new Runnable() {
				@Override
				public void run() {
					listener.onSingleTapUp(Item.this);
				}
			});
			return false;
		}
		
		@Override
		public void onShowPress(MotionEvent e) {
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			return false;
		}
		
		@Override
		public void onLongPress(MotionEvent e) {
		}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return false;
		}
		
		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}
	};
	
	public interface GestureListener {
		public boolean onSingleTapUp(View v);
		public void onScrollLeftUp(View v);
		public void onScrollRightUp(View v);
	}
}
