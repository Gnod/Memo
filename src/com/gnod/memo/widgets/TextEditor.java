package com.gnod.memo.widgets;

import com.gnod.memo.activity.App;
import com.gnod.memo.provider.MemoConstants;
import com.gnod.memo.tool.PrefConstants;
import com.gnod.memo.tool.PreferenceHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.EditText;

public class TextEditor extends EditText {

	private GestureDetector dector = null;
	private ScaleGestureDetector mScaleDetector = null;
	private boolean enableScaled = false;
	private float mScaleFactor = 1f;
	private float mTextSize;
	
	public TextEditor(Context context) {
		super(context);
		initTextSize();
	}

	public TextEditor(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initTextSize();
	}

	public TextEditor(Context context, AttributeSet attrs) {
		super(context, attrs);
		initTextSize();
	}

	public void initTextSize(){
		mTextSize = PreferenceHelper.getFloat(PrefConstants.EDITOR_TEXTSIZE, 27);
		setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
	}
	
	
	public void enableScale(boolean enabled){
		enableScaled = enabled;
		if(mScaleDetector == null){
			mScaleDetector  = new ScaleGestureDetector(
					getContext(), new ScaleListener());
		}
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if(enableScaled){
			mScaleDetector.onTouchEvent(event);
			float size = mTextSize * mScaleFactor;
			setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
		}
        if(isEnabled() == false && dector != null){
        	dector.onTouchEvent(event);
        }
		return super.dispatchTouchEvent(event);
	}

	public void setOnGestureListener(OnGestureListener listener){
		dector = new GestureDetector(getContext(), listener);
	}
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor(); 
			mScaleFactor = Math.max(0.4f, Math.min(mScaleFactor, 10.0f)); 
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			super.onScaleEnd(detector);
			SharedPreferences pref = App.getContext()
			.getSharedPreferences(MemoConstants.PREFERENCES_NAME,
					Context.MODE_APPEND);
			final Editor editor = pref.edit();
			editor.putFloat("TextSize", getTextSize());
			editor.commit();
		}
		
	}	
}
