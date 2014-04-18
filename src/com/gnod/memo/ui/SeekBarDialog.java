package com.gnod.memo.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.gnod.activity.R;

public class SeekBarDialog extends Dialog{

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
