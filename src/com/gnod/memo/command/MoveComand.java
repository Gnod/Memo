package com.gnod.memo.command;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.gnod.memo.models.AppModel;

public abstract class MoveComand extends SimpleCommand {

	private AppModel model = null;
	private Context context = null;
	private String TAG;
	
	public MoveComand(String label, Uri uri) {
		super(label, uri);
	}

	public MoveComand(Context c, AppModel model){
		this(CommandConstant.COMMAND_LABEL_PREVIOUS_NOTE, null);
		this.model = model;
		this.context = c;
	}

	@Override
	public boolean canUndo() {
		return false;
	}
	
	protected void toggleUri(){
		Integer id = model.getId();
		if(id == null){
			Log.e(TAG, "null id");
			return;
		}
		Intent intent = ((Activity)getContext()).getIntent();
		Uri noteUri = ContentUris.withAppendedId(
				intent.getData(), id);
		model.setUri(noteUri);
	}

	public AppModel getModel() {
		return model;
	}

	public void setModel(AppModel model) {
		this.model = model;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

}
