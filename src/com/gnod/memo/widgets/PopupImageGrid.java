package com.gnod.memo.widgets;

import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gnod.activity.R;

public class PopupImageGrid extends PopupWidget {

    private GridView mGridView;

    public PopupImageGrid(Context context) {
        super(context);
        setContentView(R.layout.popup_grid);
        
        final View v = getContentView();
        mGridView = (GridView) v.findViewById(R.id.menu_action_list);
    }

    @Override
    protected void populateItemActions(final List<PopupItemAction> actions) {

        mGridView.setAdapter(new BaseAdapter() {
			public View getView(int position, View view, ViewGroup parent) {
                ImageView imageView = (ImageView) view;

                if (view == null) {
                    final LayoutInflater inflater = LayoutInflater.from(getContext());
                    imageView = (ImageView) inflater.inflate(R.layout.popup_grid_image_item, mGridView, false);
                }
                PopupItemAction action = actions.get(position);
                imageView.setBackgroundDrawable(action.mDrawable);
                return imageView;
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

        mGridView.setOnItemClickListener(mInternalItemClickListener);
        mGridView.setOnKeyListener(mKeyListener);
    }

    private OnItemClickListener mInternalItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            ItemClickListener listener = getOnMenuItemActionClickListener();
            if(listener != null){
            	listener.onClicked(PopupImageGrid.this, view, position);
            }
            if (getDismissOnClick()) {
                dismiss();
            }
        }
    };
    
    private LinearLayout.OnKeyListener mKeyListener = new LinearLayout.OnKeyListener(){
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

	@Override
	public void onMeasureLayout(Rect anchorRect, View contentView) {
		int dyTop = anchorRect.top;
        int dyBottom = getScreenHeight() - anchorRect.bottom;

        boolean isOnAnchorTop = (dyTop > dyBottom);
        int rootHeight = contentView.getMeasuredHeight();
        int popupY = (isOnAnchorTop) ? anchorRect.top - rootHeight : anchorRect.bottom + 5;

        setWidgetPosition(0, popupY, isOnAnchorTop);
	}

}
