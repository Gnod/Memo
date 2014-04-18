package com.gnod.memo.command;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.gnod.memo.activity.App;
import com.gnod.memo.provider.MemoConstants;

public class UpdateCommand extends SimpleCommand {
	private static final String[] PROJECTION = new String[]{
		MemoConstants.COLUMN_NAME_NOTE_TITLE,
		MemoConstants.COLUMN_NAME_DATE_MODIFIED,
		MemoConstants.COLUMN_NAME_NOTE_CONTENTS,
		MemoConstants.COLUMN_NAME_NOTE_TIME,
		MemoConstants.COLUMN_NAME_NOTE_BG_ID,
	};
	private ContentResolver resolver = null;
	private ContentValues mValues = null;
	private ContentValues undoValues = new ContentValues();
	
	public UpdateCommand( Uri uri, ContentValues values) {
		super(CommandConstant.COMMAND_LABEL_UPDATE_NOTE, uri);
		this.resolver = App.getContext().getContentResolver();
		this.mValues = values;
		
		Cursor cursor = resolver.query(
				uri, PROJECTION, null, null, null);
		cursor.moveToFirst();
		undoValues.put(MemoConstants.COLUMN_NAME_NOTE_TITLE, cursor.getString(0));
		undoValues.put(MemoConstants.COLUMN_NAME_DATE_MODIFIED, cursor.getLong(1));
		undoValues.put(MemoConstants.COLUMN_NAME_NOTE_CONTENTS, cursor.getString(2));
		undoValues.put(MemoConstants.COLUMN_NAME_NOTE_TIME, cursor.getString(3));
		undoValues.put(MemoConstants.COLUMN_NAME_NOTE_BG_ID, cursor.getInt(4));
	}

	@Override
	protected void doExecute() {
		resolver.update(getUri(), mValues, null, null);
	}

	@Override
	public void undo() {
		resolver.update(getUri(), undoValues, null, null);
	}

	
}
