package com.gnod.memo.command;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.gnod.memo.provider.MemoConstants;

public class DeleteCommand extends SimpleCommand {
	
	private static final String[] PROJECTION = new String[]{
		MemoConstants.COLUMN_NAME_NOTE_TITLE,
		MemoConstants.COLUMN_NAME_DATE_MODIFIED,
		MemoConstants.COLUMN_NAME_NOTE_CONTENTS,
		MemoConstants.COLUMN_NAME_NOTE_TIME,
		MemoConstants.COLUMN_NAME_NOTE_BG_ID,
		MemoConstants.COLUMN_NAME_DATE_CREATED,
		MemoConstants._ID
	};
	private ContentResolver resolver = null;
	
	private ContentValues undoValues = new ContentValues();
	
	public DeleteCommand(ContentResolver resolver, Uri uri){
		super(CommandConstant.COMMAND_LABEL_DELETE_NOTE, uri);
		this.resolver = resolver;
		
		Cursor cursor = resolver.query(uri, PROJECTION, null, null, null);
		cursor.moveToFirst();
		undoValues.put(MemoConstants.COLUMN_NAME_NOTE_TITLE, cursor.getString(0));
		undoValues.put(MemoConstants.COLUMN_NAME_DATE_MODIFIED, cursor.getLong(1));
		undoValues.put(MemoConstants.COLUMN_NAME_NOTE_CONTENTS, cursor.getString(2));
		undoValues.put(MemoConstants.COLUMN_NAME_NOTE_TIME, cursor.getString(3));
		undoValues.put(MemoConstants.COLUMN_NAME_NOTE_BG_ID, cursor.getInt(4));
		undoValues.put(MemoConstants.COLUMN_NAME_DATE_CREATED, cursor.getLong(5));
		undoValues.put(MemoConstants._ID, cursor.getLong(6));
		cursor.close();
	}
	
	@Override
	protected void doExecute() {
		resolver.delete(getUri(), null, null);
	}

	@Override
	public void undo() {
		Uri uri = resolver.insert(
				MemoConstants.CONTENT_URI, undoValues);
		setUri(uri);
	}
	
}
