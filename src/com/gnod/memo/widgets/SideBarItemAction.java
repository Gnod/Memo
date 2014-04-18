package com.gnod.memo.widgets;

import android.content.Context;

public class SideBarItemAction {

    public CharSequence title = null;
    public CharSequence subTitle = null;
    public int type = 0;
    
	public SideBarItemAction(CharSequence title) {
        this.title = title;
    }
	
	public SideBarItemAction(CharSequence title, int type) {
		this.title = title;
		this.type = type;
	}
	
	public SideBarItemAction(CharSequence title, CharSequence subTitle) {
        this.title = title;
        this.subTitle = subTitle;
    }
	
	public SideBarItemAction(CharSequence title, CharSequence subTitle, int type) {
        this.title = title;
        this.subTitle = subTitle;
        this.type = type;
    }

    public SideBarItemAction(Context context, int titleId, int subTitleId) {
        title = context.getResources().getString(titleId);
        subTitle = context.getResources().getString(subTitleId);
    }

	public CharSequence getTitle() {
		return title;
	}

	public void setTitle(CharSequence mTitle) {
		this.title = mTitle;
	}
	
	public CharSequence getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(CharSequence subTitle) {
		this.subTitle = subTitle;
	}
	
	public int getType(){
		return type;
	}
}
