package com.gnod.memo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.gnod.memo.provider.MemoConstants;
import com.gnod.memo.tool.PrefConstants;

public class MainActivity extends Activity{
	
	@Override
	protected void onPostResume() {
		super.onPostResume();
		
		SharedPreferences pref = App.getContext().getSharedPreferences(
				MemoConstants.PREFERENCES_NAME, Context.MODE_APPEND);
		final Editor editor = pref.edit();
		final String firstLaunch = pref.getString(PrefConstants.FIRST_LAUNCH, "");
		final String usePassword = pref.getString("UsePassword", "False");
		
//		if(StringHelper.isNullOrEmpty(firstLaunch))
//		{
//			editor.putString(PrefConstants.FIRST_LAUNCH, "False");
//			editor.commit();
//			Intent intent = new Intent();
//			intent.putExtra("First_launch", true);
//			intent.setClass(MainActivity.this, GuideActivity.class);
//			startActivity(intent);
//		}else if (usePassword.equalsIgnoreCase("True"))
//		{
//			Intent intent = new Intent();
//			intent.setClass(MainActivity.this, PasswordActivity.class);
//			startActivity(intent);
//		}
//    	else
    	{
    		Intent intent = new Intent();
			intent.setClass(MainActivity.this, MemoListActivity.class);
			startActivity(intent);
    	}
		
	}
}
