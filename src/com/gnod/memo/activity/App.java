package com.gnod.memo.activity;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;

import com.gnod.memo.command.CommandStack;
import com.gnod.memo.models.AppModel;

public class App extends Application {

	private static Context context = null;
	
	private static CommandStack commandStack = new CommandStack();

	private static int position = -1;
	
	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				AppModel.getInstance().getCursor();
			}
		});
	}
	
	public static ContentResolver getResolver(){
		return context.getContentResolver();
	}
	
	public static Context getContext() {
		return context;
	}
	public static void setContext(Context context) {
		App.context = context;
	}
	public static int getPosition() {
		return position;
	}
	public static void setPosition(int position) {
		App.position = position;
	}

	public static void setCommandStack(CommandStack commandStack) {
		App.commandStack = commandStack;
	}

	public static CommandStack getCommandStack() {
		return commandStack;
	}
	
}
