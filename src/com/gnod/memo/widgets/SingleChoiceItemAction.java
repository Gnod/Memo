package com.gnod.memo.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class SingleChoiceItemAction {
		public CharSequence title;
		public Drawable icon;
		public int tag;
		public boolean isSelected = false;
		
		public boolean isSelected() {
			return isSelected;
		}

		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}

		public SingleChoiceItemAction(CharSequence title, Drawable icon, int tag, boolean selected) {
			this.title = title;
			this.icon = icon;
			this.tag = tag;
			isSelected = selected;
		}
		
		public SingleChoiceItemAction(Context context, int titleId, int iconId, int tag, boolean selected) {
			this.title = context.getResources().getString(titleId);
			this.icon = context.getResources().getDrawable(iconId);
			this.tag = tag;
			isSelected = selected;
		}
		
		public CharSequence getTitle() {
			return title;
		}
		public void setTitle(CharSequence title) {
			this.title = title;
		}
		public Drawable getIcon() {
			return icon;
		}
		public void setIcon(Drawable icon) {
			this.icon = icon;
		}
		public int getTag() {
			return tag;
		}
		public void setTag(int tag) {
			this.tag = tag;
		}
		
}
