package com.gnod.memo.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.gnod.activity.R;

import java.util.ArrayList;

public class GuideActivity extends Activity {

    private ViewPager viewPager;  
    private ArrayList<View> pageViews;  
    private ViewGroup group;  
    private ImageView imageView;  
    private ImageView[] imageViews;
    private int[] imageIDs = {
    		R.drawable.guide_1,
    		R.drawable.guide_2,
    		R.drawable.guide_3,
    		R.drawable.guide_4,
    		R.drawable.guide_5};
    
    private final int LAST_PAGE_DELAYED_TIME = 2000;
    private boolean isFirstLaunch = false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		initControl();
		
		isFirstLaunch = getIntent().getBooleanExtra("First_launch", false);
	}

	private void initControl() {
        LayoutInflater inflater = getLayoutInflater();  
        pageViews = new ArrayList<View>();  
        for(int i = 0; i < imageIDs.length; i++)
        {
        	View layoutView = inflater.inflate(R.layout.guide_spot, null);
        	ImageView  imageView = (ImageView) layoutView.findViewById(R.id.guide_spot_imageview);
        	imageView.setImageResource(imageIDs[i]);
        	pageViews.add(layoutView);
        } 
  
        imageViews = new ImageView[pageViews.size()];  
        
        group = (ViewGroup)findViewById(R.id.guide_viewgroup);
        viewPager = (ViewPager)findViewById(R.id.guide_pages);
  
        for (int i = 0; i < pageViews.size(); i++) {  
            imageView = new ImageView(this);  
            imageView.setLayoutParams(new LayoutParams(20,20));  
            imageView.setPadding(20, 0, 20, 0);  
            imageViews[i] = imageView;  
            if (i == 0) {  
                imageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);  
            } else {  
                imageViews[i].setBackgroundResource(R.drawable.page_indicator);  
            }  
            group.addView(imageViews[i]);  
        }  
        
        viewPager.setAdapter(new GuidePageAdapter());  
        viewPager.setOnPageChangeListener(new GuidePageChangeListener());  
	}

    class GuidePageAdapter extends PagerAdapter {  
        @Override  
        public int getCount() {  
            return pageViews.size();  
        }  
        @Override  
        public boolean isViewFromObject(View arg0, Object arg1) {  
            return arg0 == arg1;  
        }  
        @Override  
        public int getItemPosition(Object object) {  
            return super.getItemPosition(object);  
        }  
        @Override  
        public void destroyItem(View v, int arg1, Object arg2) {  
            ((ViewPager)v).removeView(pageViews.get(arg1));  
        }  
        @Override  
        public Object instantiateItem(View v, int arg1) {  
            ((ViewPager) v).addView(pageViews.get(arg1));  
            return pageViews.get(arg1);  
        }  
        @Override  
        public void restoreState(Parcelable arg0, ClassLoader arg1) {  
        }  
  
        @Override  
        public Parcelable saveState() {  
            return null;  
        }  
        @Override  
        public void startUpdate(View arg0) {  
  
        }  
        @Override  
        public void finishUpdate(View arg0) {  
        }  
    } 
    
    class GuidePageChangeListener implements OnPageChangeListener {  
        @Override  
        public void onPageScrollStateChanged(int arg0) {  
  
        }  
        @Override  
        public void onPageScrolled(int arg0, float arg1, int arg2) {  
  
        }  
        @Override  
        public void onPageSelected(int index) {  
            for (int i = 0; i < imageViews.length; i++) {  
                imageViews[index]  
                        .setBackgroundResource(R.drawable.page_indicator_focused);  
                if (index != i) {  
                    imageViews[i]  
                            .setBackgroundResource(R.drawable.page_indicator);  
                }  
            }
            if(index == imageViews.length - 1)
            {
            	new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if(isFirstLaunch) {
							Intent intent = new Intent();
	        				intent.setClass(GuideActivity.this, MemoListActivity.class);
	        				startActivity(intent);
						} else {
							finish();
						}
					}
				}, LAST_PAGE_DELAYED_TIME);
            }
        }  
  
    }  
}
