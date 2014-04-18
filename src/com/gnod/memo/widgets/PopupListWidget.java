package com.gnod.memo.widgets;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.gnod.activity.R;

public abstract class PopupListWidget extends PopupWidget {

    private ListView mListView;

    public PopupListWidget(Context context) {
        super(context);
        setContentView(R.layout.popup_list);
        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.WRAP_CONTENT);

        final View v = getContentView();
        mListView = (ListView) v.findViewById(R.id.menu_action_list);
    }
    public ListView getListView() {
		return mListView;
	}
    
    protected OnItemClickListener mInternalItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            ItemClickListener listener = getOnMenuItemActionClickListener();
            if(listener != null){
            	view.clearFocus();
            	listener.onClicked(PopupListWidget.this, view, position);
            }
            if (getDismissOnClick()) {
                dismiss();
            }
        }
    };
    
    protected LinearLayout.OnKeyListener mKeyListener = new LinearLayout.OnKeyListener(){
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if((keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) && event.getRepeatCount() == 0 && isShowing()) {
				if(isMenuClick()) {
					setMenuClick(false);
				}else{
					dismiss();
				}
			}
			return true;
		}    	
    };

}
