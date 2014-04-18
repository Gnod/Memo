package com.gnod.memo.command;

import com.gnod.memo.activity.App;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

public class InsertCommand extends SimpleCommand {
	private ContentResolver resolver = null;
	private ContentValues mValues = null;
	private Uri mPath = null;
	
	public InsertCommand(Uri uri, ContentValues values){
		super(CommandConstant.COMMAND_LABEL_INSERT_NOTE, null);
		this.resolver = App.getContext().getContentResolver();
		mPath = uri;
		mValues = values;
	}
	
	@Override
	protected void doExecute() {
		Uri uri = resolver.insert(mPath, mValues);
		setUri(uri);
	}

	@Override
	public void undo() {
		resolver.delete(getUri(), null, null);
	}

	
}
