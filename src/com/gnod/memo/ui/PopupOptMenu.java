package com.gnod.memo.ui;

import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gnod.activity.R;
import com.gnod.memo.widgets.PopupItemAction;
import com.gnod.memo.widgets.PopupListWidget;

public class PopupOptMenu extends PopupListWidget {

    public PopupOptMenu(Context context) {
        super(context);
    }

    @Override
	public void onMeasureLayout(Rect anchorRect, View contentView) {
    	 int anchorX = anchorRect.centerX();
    	 int dyTop = anchorRect.top;
         int dyBottom = getScreenHeight() - anchorRect.bottom;

         RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(getScreenWidth() / 2, RelativeLayout.LayoutParams.WRAP_CONTENT);
         getListView().setLayoutParams(params);
         boolean isOnAnchorTop = (dyTop > dyBottom);
         int rootHeight = contentView.getMeasuredHeight();
         int popupY = (isOnAnchorTop) ? anchorRect.top - rootHeight : anchorRect.bottom + 5;

         int popupX = anchorRect.centerX();
         if(anchorX < getScreenWidth() / 4){
        	 popupX = 5;
         } else if(anchorX > 3 * getScreenWidth() / 4){
        	 popupX = getScreenWidth() - anchorRect.width() - 5;
         }
         setWidgetPosition(popupX, popupY, isOnAnchorTop);
    	
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
                textView.setTag(action.type);
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
