package com.gnod.memo.views;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gnod.activity.R;
import com.gnod.memo.widgets.SideBarItemAction;

public abstract class SideBarView extends ViewGroup{

	protected View sideBar = null;
	protected View mainView = null;
	protected ListView sideBarList = null;
	protected ArrayList<SideBarItemAction> sideBarActions = new ArrayList<SideBarItemAction>();
	private int sideBarWidth = 200;
	private boolean isSideBarOpen = false;
	private boolean isSideBarInLeft = true;
	private OnOpenListener openListener;
	private OnCloseListener closeListener;
	private boolean isPressed;
	
	public abstract OnItemClickListener getSideBarClickListener();
	
	public SideBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		initView();
		initSideBarActions();
		openListener = new OnOpenListener();
		closeListener = new OnCloseListener();
	}
	
	/**
	 * in this method you must attract sideBar,mainView and sideBarList 
	 * with method findViewById()
	 * */
	public abstract void initView();
		
	public abstract void initSideBarActions();
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		super.measureChildren(widthMeasureSpec, heightMeasureSpec);
		sideBarWidth = sideBar.getMeasuredWidth();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		int sideBarLeft = l;
		if(!isSideBarInLeft){
			sideBarLeft = r - sideBarWidth;
		}
		sideBar.layout(sideBarLeft, t, sideBarLeft + sideBarWidth, sideBar.getMeasuredHeight());
		
		if(isSideBarOpen) {
			if(isSideBarInLeft) {
				mainView.layout(l + sideBarWidth, t, r + sideBarWidth, b);
			} else {
				mainView.layout(l - sideBarWidth, t, r - sideBarWidth, b);
			}
		} else {
			mainView.layout(l, t, r, b);
		}
	}

	@Override
	protected void measureChild(View child, int parentWidthMeasureSpec,
			int parentHeightMeasureSpec) {
		if(child == sideBar) {
			int mode = MeasureSpec.getMode(parentWidthMeasureSpec);
			int width = (int)(parentWidthMeasureSpec * 0.8);
			super.measureChild(child, MeasureSpec.makeMeasureSpec(width, mode), parentHeightMeasureSpec);
		} else {
			super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
		}
	}
	
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(!isSideBarOpen) {
			return false;
		}
		
		int action = ev.getAction();
		if(action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_UP) {
			return false;
		}
		
		int x = (int) ev.getX();
		int y = (int) ev.getY();
		if(mainView.getLeft() < x &&
			mainView.getRight() > x &&
			mainView.getTop() < y &&
			mainView.getBottom() > y) {
			isPressed = true;
		}
		
		if(isPressed && action == MotionEvent.ACTION_UP) {
			isPressed = false;
			closeSideBar();
			return true;
		} else {
			isPressed = false;
		}
		
		return false;
	}

	public boolean isSideBarOpen() {
		return isSideBarOpen;
	}
	public void openSideBar(){
		if(!isSideBarOpen) {
			toggleSideBar();
		}
	}
	
	public void closeSideBar() {
		if(isSideBarOpen) {
			toggleSideBar();
		}
	}
	
	public void toggleSideBar() {
		if(mainView.getAnimation() != null) {
			return;
		}
		
		Animation anim = null;
		if(isSideBarOpen) {
			if(isSideBarInLeft) {
				anim = new TranslateAnimation(0, - sideBarWidth, 0, 0);
			} else {
				anim = new TranslateAnimation(0, sideBarWidth, 0, 0);
			}
			anim.setAnimationListener(closeListener);
		} else {
			if(isSideBarInLeft) {
				anim = new TranslateAnimation(0, sideBarWidth, 0, 0);
			} else {
				anim = new TranslateAnimation(0, - sideBarWidth, 0, 0);
			}
			anim.setAnimationListener(openListener);
		}
		anim.setDuration(500);
		anim.setFillAfter(true);
		anim.setFillEnabled(true);
		mainView.startAnimation(anim);
	}
	
	public void setSideBarInLeft(boolean b) {
		isSideBarInLeft = b;
	}
	
	private class OnOpenListener implements Animation.AnimationListener {
		@Override
		public void onAnimationStart(Animation animation){
			sideBarList.setAdapter(sideBarAdapter);
			if(getSideBarClickListener() == null)
				throw new IllegalStateException("You must implements getSideBarClickListener() and return a OnItemClickedListener Object");
			
			sideBarList.setOnItemClickListener(getSideBarClickListener());
			sideBar.setVisibility(View.VISIBLE);
		}
		@Override
		public void onAnimationEnd(Animation animation) {
			isSideBarOpen = true;
			mainView.clearAnimation();
			requestLayout();
			
		}
		@Override
		public void onAnimationRepeat(Animation animation) {
		}
	}
	
	private class OnCloseListener implements Animation.AnimationListener {
		@Override
		public void onAnimationStart(Animation animation) {
		}
		@Override
		public void onAnimationEnd(Animation animation) {
			isSideBarOpen = false;
			mainView.clearAnimation();
			sideBar.setVisibility(View.INVISIBLE);
			requestLayout();
		}
		@Override
		public void onAnimationRepeat(Animation animation) {
		}
	}
	
	protected BaseAdapter sideBarAdapter = new BaseAdapter() {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				final LayoutInflater inflater = LayoutInflater.from(getContext());
				convertView = inflater.inflate(R.layout.sidebar_item, sideBarList, false);
			}
			SideBarItemAction action = sideBarActions.get(position);
			TextView title = (TextView) convertView.findViewById(R.id.sidebar_item_text);
			if(action.getTitle() != null)
				title.setText(action.getTitle());
			
			TextView subTitle = (TextView)convertView.findViewById(R.id.sidebar_item_subtext);
			if(action.getSubTitle() != null){
				subTitle.setText(action.getSubTitle());
			}
			convertView.setTag(action.getType());
			return convertView;
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public Object getItem(int position) {
			return sideBarActions.get(position);
		}
		@Override
		public int getCount() {
			return sideBarActions.size();
		}
	};
	
}
