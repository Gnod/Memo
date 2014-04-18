package com.gnod.memo.ui;

import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gnod.activity.R;
import com.gnod.memo.widgets.PopupItemAction;
import com.gnod.memo.widgets.PopupListWidget;

public class PopupList extends PopupListWidget {

    public PopupList(Context context) {
        super(context);
    }

    public void onMeasureLayout(Rect anchorRect, View contentView){
    	int dyTop = anchorRect.top;
        int dyBottom = getScreenHeight() - anchorRect.bottom;

        boolean isOnAnchorTop = (dyTop > dyBottom);
        int popupY = (getScreenHeight() - contentView.getMeasuredHeight())/2;
        setWidgetPosition(0, popupY, isOnAnchorTop);
   }
    
	@Override
    protected void populateItemActions(final List<PopupItemAction> actions) {

        getListView().setAdapter(new BaseAdapter() {
			public View getView(int position, View view, ViewGroup parent) {
                TextView textView = (TextView) view;

                if (view == null) {
                    final LayoutInflater inflater = LayoutInflater.from(getContext());
                    textView = (TextView) inflater.inflate(R.layout.popup_list_item, getListView(), false);
                }
                PopupItemAction action = actions.get(position);
                textView.setText(action.mTitle);
                return textView;
            }
			public long getItemId(int position) {
                return position;
            }
			public Object getItem(int position) {
                return null;
            }

			public int getCount() {
                return actions.size();
            }
        });

        getListView().setOnItemClickListener(mInternalItemClickListener);
        getListView().setOnKeyListener(mKeyListener);
    }
}
