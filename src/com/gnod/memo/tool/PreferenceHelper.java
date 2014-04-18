package com.gnod.memo.tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.content.SharedPreferences;

import com.gnod.memo.activity.App;
import com.gnod.memo.provider.MemoConstants;

public class PreferenceHelper {

	public static String getString(String key){
		SharedPreferences pref = App.getContext().getSharedPreferences(
				MemoConstants.PREFERENCES_NAME, Context.MODE_APPEND);
    	return pref.getString(key, "");
	}
	
	public static String getString(String key, String defaultValue){
		SharedPreferences pref = App.getContext().getSharedPreferences(
				MemoConstants.PREFERENCES_NAME, Context.MODE_APPEND);
    	return pref.getString(key, defaultValue);
	}
	
	public static float getFloat(String key, float defaultalue){
		SharedPreferences pref = App.getContext().getSharedPreferences(
				MemoConstants.PREFERENCES_NAME, Context.MODE_APPEND);
    	return pref.getFloat(key, defaultalue);
	}
	
	public static int getInt(String key, int defaultalue){
		SharedPreferences pref = App.getContext().getSharedPreferences(
				MemoConstants.PREFERENCES_NAME, Context.MODE_APPEND);
    	return pref.getInt(key, defaultalue);
	}
	
	public static boolean writeFile(File dir, String name, String text){
		File f = new File(dir, File.separator  + name);
		try {
			FileOutputStream out = new FileOutputStream(f);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
			bw.write(text);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
