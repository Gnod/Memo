package com.gnod.memo.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import com.gnod.activity.R;
import com.gnod.memo.provider.MemoConstants;
import com.gnod.memo.widgets.Item;
import com.gnod.memo.widgets.Item.GestureListener;
import com.gnod.memo.widgets.ItemAnimPool;

/**
 * Implement to draw ListView items with different background.
 */
public class MemoCursorAdapter extends SimpleCursorAdapter {

	private GestureListener gestureListener = null;
	private ItemAnimPool animPool = null;
	
	public MemoCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		animPool = new ItemAnimPool(context, 10);
		
	}
	
	public void setGestureListener(GestureListener listener){
		this.gestureListener = listener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = super.getView(position, convertView, parent);
		
		View childView = v.findViewById(R.id.list_item_title);

		int index = getCursor().getColumnIndex(
				MemoConstants.COLUMN_NAME_NOTE_BG_ID);
		int bgId = this.getCursor().getInt(index);
		childView.setBackgroundResource(MemoConstants.BG_BAR_IMAGE_IDS[bgId]);
		v.setPadding(15, 15, 15, 0);
		childView.setPadding(15, 0, 15, 0);
		v.setTag(position);
		((Item)v).setAnimPool(animPool);
		if(gestureListener != null)
			((Item)v).setOnGestureListener(gestureListener);

		((Item)v).enableMoved(true);
		((Item)v).showInitAnimation((position % 12) * 30);
		return v;
	}

}
