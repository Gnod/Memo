package com.gnod.memo.provider;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.net.Uri;
import android.provider.BaseColumns;

import com.gnod.activity.R;

/**
 * Class that defined the note table contact
 */
public interface MemoConstants extends BaseColumns{

	/**
	 * Scheme part of the provider's URI
	 */
	public static final String SCHEME = "content://";
	
	/**
	 * Authority part of the provider's URI
	 */
	public static final String AUTHORITY = "com.gnod.provider.NoteConstants";
	
	/**
	 * Path part of the provider's URI.
	 */
	public static final String PATH = "/notes";
	
	/**
	 * URI of the provider
	 */
	public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH);
	
	/**
	 * URI to appended the note's URI
	 */
	public static final Uri CONTENT_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH + "/");
	
	/**
	 * the position of note id part in it's URI segment.
	 */
	public static final int SEGMENTS_POSITION_NOTE_ID = 1;
	
	/**
	 * Constant for date formating.
	 */
	public static final SimpleDateFormat DATEFORMATION = 
		new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
	
	/**
	 * Array use in {@link com.gnod.memo.handler#CustomSimpleCursorAdapter}. Its order is
	 * relative to the order of {@link #EDITOR_BG_IMAGE_IDS}.
	 */
	public static final int[] BG_BAR_IMAGE_IDS = {
		R.drawable.sl_bg_noteitem_green, 
		R.drawable.sl_bg_noteitem_pink,
		R.drawable.sl_bg_noteitem_blue, 
		R.drawable.sl_bg_noteitem_deepblue,
		R.drawable.sl_bg_noteitem_yellow };
	
	/**
	 * Resource order is determined by R.array.bg_color in /res/values/strings.xml
	 */
	public static final int[] EDITOR_BG_IMAGE_IDS = BG_BAR_IMAGE_IDS;
	
	/**
	 * MIME type of {@link #CONTENT_URI} which provide the note's directory.
	 */
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.gnod.note";

	/**
	 * MIME type of {@link #CONTENT_URI} which provide the note a single item.
	 */
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.gnod.note";
	
	public static final String TABLE_NAME = "note";
	
	public static final String COLUMN_NAME_NOTE_TITLE = "title";
	
	public static final String COLUMN_NAME_NOTE_CONTENTS = "contents";
	
	public static final String COLUMN_NAME_DATE_CREATED = "created";
	
	public static final String COLUMN_NAME_DATE_MODIFIED = "modified";
	
	public static final String COLUMN_NAME_NOTE_TIME = "time";
	
	public static final String COLUMN_NAME_NOTE_BG_ID = "bgid";

	public static final String DEFAULT_SORT_ORDER = "modified desc";
	
	public static final String PREFERENCES_NAME = "com.gnod.memo";
	
}
