package com.gnod.memo.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by DLC on 14-4-26.
 */
public class MemoSwipeCursorAdapter extends CursorAdapter {

    public MemoSwipeCursorAdapter(Context context) {
        super(context, null, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
//        int index = getCursor().getColumnIndex(
//                MemoConstants.COLUMN_NAME_NOTE_BG_ID);
//        int bgId = this.getCursor().getInt(index);
//        childView.setBackgroundResource(MemoConstants.BG_BAR_IMAGE_IDS[bgId]);
//        v.setPadding(15, 15, 15, 0);
//        childView.setPadding(15, 0, 15, 0);
//        v.setTag(position);
    }
}
