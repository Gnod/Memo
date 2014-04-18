package com.gnod.memo.command;

import android.content.Context;

import com.gnod.memo.activity.App;
import com.gnod.memo.models.AppModel;

public class ForwardCommand extends MoveComand {
	
	public ForwardCommand(Context c, AppModel m){
		super(c, m);
	}
	
	@Override
	protected void doExecute() {
		boolean moved = getModel().moveToNext();
		if(!moved) return;
		App.setPosition(App.getPosition() + 1);
		toggleUri();
	}

}
