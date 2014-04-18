package com.gnod.memo.tool;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.gnod.activity.R;
import com.gnod.memo.activity.App;

public class DialogHelper {

	public static void showSingleList(CharSequence[] items, 
			int checkedItem, OnClickListener listener){
		AlertDialog.Builder builder = new AlertDialog.Builder(App.getContext());
		builder.setSingleChoiceItems(items, checkedItem, listener);
		Dialog dialog = builder.create();
		dialog.show();
	}
	
	public static void showList(Context context, CharSequence[] items, OnClickListener listener){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setItems(items, listener);
		Dialog dialog = builder.create();
		dialog.show();
	}
	
	public static void showList(Context context, String title, CharSequence[] items, OnClickListener listener){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setItems(items, listener);
		builder.setTitle(title);
		Dialog dialog = builder.create();
		dialog.show();
	}
	
	public static void showSeekBar(int defaultValue, 
			OnSeekBarChangeListener listener){
		Dialog dialog = new  SeekBarDialog(
				App.getContext(), 
				R.style.theme_wrap_contents,
				defaultValue,
				listener
				);
		dialog.show();
	}
}

class SeekBarDialog extends Dialog{

	private OnSeekBarChangeListener listener = null;
	private SeekBar mSeekBar = null;
	private int defaultValue = 0;
	
	public SeekBarDialog(Context context, int theme, int defaultValue, 
			OnSeekBarChangeListener listener) {
		super(context, theme);
		this.defaultValue = defaultValue;
		this.listener = listener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_seekbar);
		
		mSeekBar = (SeekBar)findViewById(R.id.seek);
		mSeekBar.setOnSeekBarChangeListener(listener);
		mSeekBar.setProgress(defaultValue);
	}
}
