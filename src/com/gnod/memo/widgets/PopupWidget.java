package com.gnod.memo.widgets;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.gnod.activity.R;

public abstract class PopupWidget extends PopupWindow {

    private final Rect mRect = new Rect();

    private Context mContext;

    private View contentView; 
    private boolean mIsMenuClick;

    private boolean mDismissOnClick;

    private boolean mIsOnAnchorTop;

    private int mScreenHeight;
    private int mScreenWidth;
    private boolean mIsDirty;

    private ItemClickListener mItemClickListener;
    private ArrayList<PopupItemAction> mPopupItemActions = new ArrayList<PopupItemAction>();

	private int mOpupY;

	private int mOpupX;

    public static interface ItemClickListener {
        void onClicked(PopupWidget widget, View v, int pos);
    }

    public PopupWidget(Context context){
        super(context);

        mContext = context;

        initializeDefault();

        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.WRAP_CONTENT);

        final WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = windowManager.getDefaultDisplay().getWidth();
        mScreenHeight = windowManager.getDefaultDisplay().getHeight();
    }

    public void setContentView(int layoutId) {
        setContentView(LayoutInflater.from(mContext).inflate(layoutId, null));
    }

    private void initializeDefault() {
        mDismissOnClick = true;
    }

    public PopupItemAction getMenuItemAction(int pos) {
    	if(pos < 0 || pos >= mPopupItemActions.size()) return null;
    	return mPopupItemActions.get(pos);
    }
    
    protected int getScreenWidth() {
        return mScreenWidth;
    }

    protected int getScreenHeight() {
        return mScreenHeight;
    }

    public void setDismissOnClick(boolean dismissOnClick) {
        mDismissOnClick = dismissOnClick;
    }

    public boolean getDismissOnClick() {
        return mDismissOnClick;
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void addMenuItemAction(PopupItemAction action) {
        if (action != null) {
            mPopupItemActions.add(action);
            mIsDirty = true;
        }
    }

    public void clearAllMenuItemActions() {
        if (!mPopupItemActions.isEmpty()) {
            mPopupItemActions.clear();
            mIsDirty = true;
        }
    }

    public void show(View anchor) {
        contentView = getContentView();

        if (contentView == null) {
            throw new IllegalStateException("you muse call setContentView method");
        }

        contentView.setOnTouchListener(dismissTouchListener);
        setBackgroundDrawable(null);

        int[] loc = new int[2];
		anchor.getLocationOnScreen(loc);
		mRect.set(loc[0], loc[1], loc[0] + anchor.getWidth(), loc[1] + anchor.getHeight());
        if (mIsDirty) {
            clearMenuItemActions();
            populateItemActions(mPopupItemActions);
        }
        
        onMeasureLayout(mRect, contentView);

        prepareAnimationStyle();
        showAtLocation(anchor, Gravity.NO_GRAVITY, mOpupX, mOpupY);
    }
    
    public abstract void onMeasureLayout(Rect anchorRect, View contentView);
    
    public void setWidgetPosition(int posX, int posY, boolean isOnAnchorTop) {
    	this.mOpupX = posX;
    	this.mOpupY = posY;
    	this.mIsOnAnchorTop = isOnAnchorTop;
    }
    
    public void show(View anchor,boolean isMenuClick) {
    	this.mIsMenuClick = isMenuClick;
    	show(anchor);
    }

    protected boolean isMenuClick() {
    	return mIsMenuClick;
    }
    protected void setMenuClick(boolean isMenuClick) {
    	this.mIsMenuClick = isMenuClick;
    }
    
    protected void clearMenuItemActions() {
        if (!mPopupItemActions.isEmpty()) {
            onClearMenuItemActions();
        }
    }

    protected void onClearMenuItemActions() {
    }

    protected abstract void populateItemActions(List<PopupItemAction> menuItemActions);

    private void prepareAnimationStyle() {

        final int screenWidth = mScreenWidth;
        final boolean onTop = mIsOnAnchorTop;
        final int arrowPointX = mRect.centerX();
        
        if (arrowPointX <= screenWidth / 4) {
            setAnimationStyle(onTop ? R.style.GreenDroid_Animation_PopUp_Left
                    : R.style.GreenDroid_Animation_PopDown_Left);
        } else if (arrowPointX >= 3 * screenWidth / 4) {
            setAnimationStyle(onTop ? R.style.GreenDroid_Animation_PopUp_Right
                    : R.style.GreenDroid_Animation_PopDown_Right);
        } else {
            setAnimationStyle(onTop ? R.style.GreenDroid_Animation_PopUp_Center
                    : R.style.GreenDroid_Animation_PopDown_Center);
        }
    }

    protected Context getContext() {
        return mContext;
    }

    protected ItemClickListener getOnMenuItemActionClickListener() {
        return mItemClickListener;
    }
    
    private View.OnTouchListener dismissTouchListener =  new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
            final int x = (int) event.getX();
            final int y = (int) event.getY();
            
            if ((event.getAction() == MotionEvent.ACTION_DOWN)
                    && ((x < 0) || (x >= getWidth()) || (y < 0) || (y >= getHeight()))) {
                dismiss();
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                dismiss();
                return true;
            } else {
                return contentView.onTouchEvent(event);
            }
		}
	};
}
