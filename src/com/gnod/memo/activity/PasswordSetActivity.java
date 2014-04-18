package com.gnod.memo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.gnod.activity.R;
import com.gnod.memo.provider.MemoConstants;
import com.gnod.memo.tool.PrefConstants;
import com.gnod.memo.tool.PreferenceHelper;
import com.gnod.memo.tool.StringHelper;
import com.gnod.memo.tool.ToastHelper;

public class PasswordSetActivity extends Activity {

	private EditText textInput1;
	private EditText textInput2;
	private RelativeLayout layoutClear = null;
	private RelativeLayout layoutConfirm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_set);
		initView();
	}
	
	private void initView()
	{
		textInput1 = (EditText) findViewById(R.id.password_input_1);
		textInput2 = (EditText) findViewById(R.id.password_input_2);
		layoutClear = (RelativeLayout)findViewById(R.id.password_clear);
		layoutConfirm = (RelativeLayout) findViewById(R.id.password_confirm);
		layoutConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmClicked();
			}
		});
		
		String usePassword = PreferenceHelper.getString(PrefConstants.USE_PASSWORD);
		if(!StringHelper.isNullOrEmpty(usePassword)){
			layoutClear.setVisibility(View.VISIBLE);
			layoutClear.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					clearClicked();
				}
			});
		}
	}
	
	protected void clearClicked() {
		SharedPreferences pref = App.getContext().getSharedPreferences(
				MemoConstants.PREFERENCES_NAME, Context.MODE_APPEND);
    	Editor editor = pref.edit();
    	editor.remove(PrefConstants.PASSWORD);
    	editor.remove(PrefConstants.USE_PASSWORD);
    	editor.commit();
    	finish();
	}

	private void confirmClicked()
	{
		String password1 = textInput1.getText().toString();
		String password2 = textInput2.getText().toString();
		if(StringHelper.isNullOrEmpty(password1) || 
				StringHelper.isNullOrEmpty(password2))
		{
			ToastHelper.show(getResources().getString(R.string.password_empty));
			return;
		}
		
		if(!password1.equals(password2))
		{
			ToastHelper.show(getResources().getString(R.string.password_diff));
			return;
		}
		
		SharedPreferences pref = App.getContext().getSharedPreferences(
				MemoConstants.PREFERENCES_NAME, Context.MODE_APPEND);
    	Editor editor = pref.edit();
    	editor.putString(PrefConstants.PASSWORD, password1);
    	editor.putString(PrefConstants.USE_PASSWORD, "True");
    	editor.commit();
    	finish();
	}

}
