package com.gnod.memo.tool;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gnod.memo.activity.App;

public class ToastHelper {
	
	private static int density = 1;

	public static void show(final String content, final Boolean isTop)
	{
		if(content == null)
			return;	
		
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				innerShow(content, isTop);		
			}
		});
	}	

	
	public static void show(final String content)
	{
		if(content == null)
			return;	
		
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				innerShow(content, false);		
			}
		});
	}	

	
	public static void innerShow(String content, Boolean isTop)
	{		
		Toast t = Toast.makeText(App.getContext(),content, Toast.LENGTH_SHORT);
		if(isTop){
			t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, (int)(60 * density ));
		}
		t.show();
		
	}
	
	private static void showt(String content){
		LinearLayout layout = new LinearLayout(App.getContext());
		LinearLayout.LayoutParams  params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layout.setBackgroundColor(Color.RED);
		TextView text = new TextView(App.getContext());
		text.setText(content);
		layout.addView(text);
		Toast toast = new Toast(App.getContext());
		toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, (int)(60 * density ));
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
	}
}
