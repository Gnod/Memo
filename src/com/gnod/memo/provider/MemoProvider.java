package com.gnod.memo.provider;

import java.util.Date;
import java.util.HashMap;



import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class MemoProvider extends ContentProvider{

	private static NoteDatabaseHelper mDatabaseHelper = null;
	
	/**
	 * Projection map using to select column from the database.
	 */
	private static HashMap<String, String> sProjectMap = new HashMap<String, String>();
	
	/**
	 * UriMatcher instance for URI matching
	 */
	private static final UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	/**
	 * Constant to indicate the matched URI type.
	 */
	private static final int URIMATCHER_NOTE_ID = 0;
	private static final int URIMATCHER_NOTE_PATH = 1;
	
	static {
		sProjectMap.put(MemoConstants._ID, MemoConstants._ID);
		sProjectMap.put(MemoConstants.COLUMN_NAME_NOTE_TITLE, 
						MemoConstants.COLUMN_NAME_NOTE_TITLE);
		sProjectMap.put(MemoConstants.COLUMN_NAME_NOTE_CONTENTS,
						MemoConstants.COLUMN_NAME_NOTE_CONTENTS);
		sProjectMap.put(MemoConstants.COLUMN_NAME_DATE_CREATED, 
						MemoConstants.COLUMN_NAME_DATE_CREATED);
		sProjectMap.put(MemoConstants.COLUMN_NAME_DATE_MODIFIED, 
						MemoConstants.COLUMN_NAME_DATE_MODIFIED);
		sProjectMap.put(MemoConstants.COLUMN_NAME_NOTE_TIME, 
						MemoConstants.COLUMN_NAME_NOTE_TIME);
		sProjectMap.put(MemoConstants.COLUMN_NAME_NOTE_BG_ID, 
						MemoConstants.COLUMN_NAME_NOTE_BG_ID);
		
		
		sMatcher.addURI(MemoConstants.AUTHORITY, "notes/#", URIMATCHER_NOTE_ID);
		sMatcher.addURI(MemoConstants.AUTHORITY, "notes", URIMATCHER_NOTE_PATH);
	}

	
	
	static class NoteDatabaseHelper extends SQLiteOpenHelper{
		
		private static final String DATABASE_NAME = "note_storage.db";
		
		private static final int DATABASE_VERSION = 2;
		
		NoteDatabaseHelper(Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL("create table " + MemoConstants.TABLE_NAME + " ("
					+ MemoConstants._ID + " integer primary key,"
					+ MemoConstants.COLUMN_NAME_NOTE_TITLE + " text,"
					+ MemoConstants.COLUMN_NAME_NOTE_CONTENTS + " text,"
					+ MemoConstants.COLUMN_NAME_DATE_CREATED + " integer,"
					+ MemoConstants.COLUMN_NAME_DATE_MODIFIED + " integer,"
					+ MemoConstants.COLUMN_NAME_NOTE_TIME + " text,"
					+ MemoConstants.COLUMN_NAME_NOTE_BG_ID + " integer"
					+ ");"
					);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + MemoConstants.TABLE_NAME);

	        onCreate(db);
		}

	}

	@Override
	public boolean onCreate() {
		
		mDatabaseHelper = new NoteDatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(MemoConstants.TABLE_NAME);
		
		switch(sMatcher.match(uri)){
		case URIMATCHER_NOTE_ID:
			//the incoming URI is for a single note.
			builder.setProjectionMap(sProjectMap);
			//use where clause to indicate a single note
			builder.appendWhere(MemoConstants._ID + "="
								+ uri.getPathSegments().
								get(MemoConstants.SEGMENTS_POSITION_NOTE_ID));
			break;
		case URIMATCHER_NOTE_PATH:
			//the incoming URI is for notes
			builder.setProjectionMap(sProjectMap);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
			
		}
		
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		
		Cursor cursor = builder.query(db, 
				projection, 
				selection, 
				selectionArgs, 
				null, null, 
				sortOrder);
		
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		
		switch(sMatcher.match(uri)){
		case URIMATCHER_NOTE_ID:
			return MemoConstants.CONTENT_ITEM_TYPE;
			
		case URIMATCHER_NOTE_PATH:
			return MemoConstants.CONTENT_TYPE;
			
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		if(sMatcher.match(uri) != URIMATCHER_NOTE_PATH){
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		ContentValues contentValues;
		
		if(values != null){
			contentValues = new ContentValues(values);
		}else{
			contentValues = new ContentValues();
		}
		
		Long now = System.currentTimeMillis();
		
		if(!contentValues.containsKey(MemoConstants.COLUMN_NAME_DATE_CREATED)){
			contentValues.put(MemoConstants.COLUMN_NAME_DATE_CREATED, now);
		}

		if(!contentValues.containsKey(MemoConstants.COLUMN_NAME_DATE_MODIFIED)){
			contentValues.put(MemoConstants.COLUMN_NAME_DATE_MODIFIED, now);
		}
		if(!contentValues.containsKey(MemoConstants.COLUMN_NAME_NOTE_TIME)){
			String value = MemoConstants.DATEFORMATION.format(new Date(now));
			contentValues.put(MemoConstants.COLUMN_NAME_NOTE_TIME, value);
		}
		if(!contentValues.containsKey(MemoConstants.COLUMN_NAME_NOTE_TITLE)){
			contentValues.put(MemoConstants.COLUMN_NAME_NOTE_TITLE, 
					Resources.getSystem().getString(android.R.string.untitled));
		}
		
		if(!contentValues.containsKey(MemoConstants.COLUMN_NAME_NOTE_CONTENTS)){
			contentValues.put(MemoConstants.COLUMN_NAME_NOTE_CONTENTS, "");
		}
		if(!contentValues.containsKey(MemoConstants.COLUMN_NAME_NOTE_BG_ID)){
			contentValues.put(MemoConstants.COLUMN_NAME_NOTE_BG_ID, 0);
		}
		
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		
		long id = db.insert(
				MemoConstants.TABLE_NAME, 
				MemoConstants.COLUMN_NAME_NOTE_CONTENTS, 
				contentValues);
		
		if(id > 0){
			Uri rowUri = ContentUris.withAppendedId(MemoConstants.CONTENT_URI_BASE, id);
			getContext().getContentResolver().notifyChange(rowUri, null);
			
			return rowUri;
		}
		
		throw new SQLiteException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		String whereClause;
		int count;
		
		switch(sMatcher.match(uri)){
		
		case URIMATCHER_NOTE_PATH:
			whereClause = selection;
			break;
			
		case URIMATCHER_NOTE_ID:
			whereClause = MemoConstants._ID + " = "
				+ uri.getPathSegments().get(MemoConstants.SEGMENTS_POSITION_NOTE_ID);
			
			if(selection != null){
				whereClause += " and " + selection; 
			}
			break;
			
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		count = db.delete(MemoConstants.TABLE_NAME, whereClause, selectionArgs);
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		String whereClause;
		int count;
		
		switch(sMatcher.match(uri)){
		
		case URIMATCHER_NOTE_PATH:
			//The URI is matches notes
			whereClause = selection;
			break;
			
		case URIMATCHER_NOTE_ID:
			//The URI is matches a single note.
			if(values.containsKey(MemoConstants.COLUMN_NAME_DATE_MODIFIED)){
				long now = values.getAsLong(MemoConstants.COLUMN_NAME_DATE_MODIFIED);
				String value = MemoConstants.DATEFORMATION.format(new Date(now));
				values.put(MemoConstants.COLUMN_NAME_NOTE_TIME, value);
			}
			
			whereClause = MemoConstants._ID + " = "
				+ uri.getPathSegments().get(MemoConstants.SEGMENTS_POSITION_NOTE_ID);
			
			if(selection != null){
				whereClause += " and " + selection; 
			}
			break;
			
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		count = db.update(MemoConstants.TABLE_NAME, 
				values, whereClause, selectionArgs);
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}
	
}
