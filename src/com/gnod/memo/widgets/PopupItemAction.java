package com.gnod.memo.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class PopupItemAction {

    public Drawable mDrawable;
    public CharSequence mTitle;
    public int type;

	public PopupItemAction(Drawable d, CharSequence title) {
        mDrawable = d;
        mTitle = title;
    }
	
	public PopupItemAction(Drawable d, CharSequence title, int type) {
        mDrawable = d;
        mTitle = title;
        this.type = type;
    }

    public PopupItemAction(Context context, int drawableId, String title) {
        mDrawable = context.getResources().getDrawable(drawableId);
        mTitle = title;
    }
    
    public PopupItemAction(Context context, int drawableId, int titleId) {
        mDrawable = context.getResources().getDrawable(drawableId);
        mTitle = context.getResources().getString(titleId);
    }

	public Drawable getIcon() {
		return mDrawable;
	}

	public void setIcon(Drawable mDrawable) {
		this.mDrawable = mDrawable;
	}

	public CharSequence getTitle() {
		return mTitle;
	}

	public void setTitle(CharSequence mTitle) {
		this.mTitle = mTitle;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
