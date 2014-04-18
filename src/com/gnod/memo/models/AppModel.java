package com.gnod.memo.models;

import java.util.Date;

import android.database.Cursor;
import android.net.Uri;

import com.gnod.memo.activity.App;
import com.gnod.memo.events.EventDispatcher;
import com.gnod.memo.events.SimpleEvent;
import com.gnod.memo.provider.MemoConstants;
import com.gnod.memo.tool.StringHelper;

public class AppModel extends EventDispatcher{

	private static AppModel instance = null;
	private Cursor cursor = null;
	private Uri uri = MemoConstants.CONTENT_URI;
	
	private AppModel(){
		super();
	}
	
	public static AppModel getInstance(){
		synchronized (AppModel.class) {
			if(instance == null)
				instance = new AppModel();
			return instance;
		}
	}
	
	public String getContents(){
		return getString(MemoConstants.COLUMN_NAME_NOTE_CONTENTS);
	}
	
	public String getSubTime(){
		String text = getString(MemoConstants.COLUMN_NAME_NOTE_TIME);
		if(StringHelper.isNullOrEmpty(text)){
			long now = System.currentTimeMillis();
			text = MemoConstants.DATEFORMATION.format(new Date(now));
		}
		return text;
	}
	
	public int getBackground(){
		Integer id = getInteger(MemoConstants.COLUMN_NAME_NOTE_BG_ID);
		return (id == null)? 0:id;
	}
	
	public Integer getId(){
		Integer id = getInteger(MemoConstants._ID);
		if(id == null)
			return -1;
		return id;
	}
	
	public Integer getItemCount(){
		if(getCursor() == null) return null;
		int count = getCursor().getCount();
		if(count == 0) return null;
		return Integer.valueOf(count);
	}
	
	public boolean hasPrevious(){
		if(App.getPosition() == -1)
			return false;
		return !cursor.isFirst();
	}
	
	public boolean hasForward(){
		if(App.getPosition() == -1)
			return false;
		return !cursor.isLast();
	}
	
	public boolean moveToNext(){
		if(cursor != null && !cursor.isLast()){
			cursor.moveToNext();
			notifyChange(ViewEvent.VIEW_NEXT);
			return true;
		}
		return false;
	}
	
	public boolean moveToPrevious(){
		if(cursor != null && !cursor.isFirst()){
			cursor.moveToPrevious();
			notifyChange(ViewEvent.VIEW_PREVIOUS);
			return true;
		}
		return false;
	}
	
	public void add(){
		App.setPosition(-1);
		notifyChange(ViewEvent.VIEW_NEW);
	}
	
	public String getString(String columnName){
		if(App.getPosition() == -1 || cursor == null){
			return "";
		}
		int columnIndex = cursor.getColumnIndex(columnName);
		return cursor.getString(columnIndex);
	}
	
	public Integer getInteger(String columnName){
		if(App.getPosition() == -1 || cursor == null){
			return null;
		}
		int index = cursor.getColumnIndex(columnName);
		return cursor.getInt(index);
	}
	
	public Cursor getCursor(){
		if(cursor == null){
			cursor = App.getResolver().query(
					uri, null, null,
					null, MemoConstants.DEFAULT_SORT_ORDER);
		}
		return cursor;
	}
	
	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}
	
	public void notifyChange(String type){
		dispatchEvent(new ViewEvent(type));
	}
	
	public static class ViewEvent extends SimpleEvent{
		public static final String VIEW_PREVIOUS = "ViewPrevious";
		public static final String VIEW_NEXT = "ViewForward";
		public static final String VIEW_NEW = "ViewNew";
		
		public ViewEvent(String type){
			super(type);
		}
	}
}
