package com.gnod.memo.command;

import android.content.Intent;

import com.gnod.memo.activity.App;

public class StartActivityCommand extends SimpleCommand{
//	private static final String TAG = StartActivityCommand.class.getSimpleName();
	private Intent mIntent = null;
	
	public StartActivityCommand(Intent intent){
		super(CommandConstant.COMMAND_LABEL_START_ACTIVITY, null);
		mIntent = intent;
	}

	@Override
	protected void doExecute() {
		if(App.getContext() != null)
			App.getContext().startActivity(mIntent);
	}
	@Override
	public boolean canUndo() {
		return false;
	}
}
