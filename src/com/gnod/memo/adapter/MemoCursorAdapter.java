package com.gnod.memo.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import com.gnod.activity.R;
import com.gnod.memo.activity.App;
import com.gnod.memo.provider.MemoConstants;
import com.gnod.memo.widgets.Item.GestureListener;
import com.gnod.memo.widgets.ItemAnimPool;

/**
 * Implement to draw ListView items with different background.
 */
public class MemoCursorAdapter extends SimpleCursorAdapter {

	private GestureListener gestureListener = null;
	private ItemAnimPool animPool = null;
    private MemoItemMenuListener mMenuListener;

    public MemoCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		animPool = new ItemAnimPool(context, 10);
		
	}
	
	public void setGestureListener(GestureListener listener){
		this.gestureListener = listener;
	}

    public void setItemMenuListener(MemoItemMenuListener listener) {
        this.mMenuListener = listener;
    }

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View v = super.getView(position, convertView, parent);

        View btnDel = v.findViewById(R.id.listitem_btn_delete);
        View btnShare = v.findViewById(R.id.listitem_btn_share);

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.setPosition(position);
                if(mMenuListener != null) {
                    mMenuListener.onDelete();
                }
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.setPosition(position);
                if(mMenuListener != null) {
                    mMenuListener.onShare(v);
                }
            }
        });


//		View childView = v.findViewById(R.id.list_item_title);
//
//		int index = getCursor().getColumnIndex(
//				MemoConstants.COLUMN_NAME_NOTE_BG_ID);
//		int bgId = this.getCursor().getInt(index);
//		childView.setBackgroundResource(MemoConstants.BG_BAR_IMAGE_IDS[bgId]);
//		v.setPadding(15, 15, 15, 0);
//		childView.setPadding(15, 0, 15, 0);
//		((Item)v).setAnimPool(animPool);
//		if(gestureListener != null)
//			((Item)v).setOnGestureListener(gestureListener);
//
//		((Item)v).enableMoved(true);
//		((Item)v).showInitAnimation((position % 12) * 30);
		return v;
	}


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        View v = view;

        View childView = v.findViewById(R.id.list_item_title);
        View frontView = v.findViewById(R.id.listitem_front);

        int index = getCursor().getColumnIndex(
                MemoConstants.COLUMN_NAME_NOTE_BG_ID);
        int bgId = this.getCursor().getInt(index);
        frontView.setBackgroundResource(MemoConstants.BG_BAR_IMAGE_IDS[bgId]);
//        v.setPadding(15, 15, 15, 0);
//        childView.setPadding(15, 0, 15, 0);
    }

    public interface MemoItemMenuListener {
        public void onDelete();
        public void onShare(View view);
    }
}
