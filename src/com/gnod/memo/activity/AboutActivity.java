package com.gnod.memo.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gnod.activity.R;

public class AboutActivity extends Activity {

	private ImageView[] bgImage = new ImageView[2];
	private static GreetAnimPool animPool = null;
	private List<Drawable> imageList;
	private int poolIndex = 0;
	private int curBgIndex = 0;
	private int[] bgIdArray = {
			R.drawable.about_1,
			R.drawable.about_2,
			R.drawable.about_3,
			R.drawable.about_4,
			R.drawable.about_5,
			R.drawable.about_6
	};
	
	private int curContextIndex = 0;
	private String[] contentArray = {
		"Author: Gnod",
		"Gnod Studio",
		"CopyRight ⓒ2013"
	};
	
	private TextView contentView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		bindView();
		initImageRes();
		loopRefresh();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void bindView() {
		bgImage[0] = (ImageView)findViewById(R.id.iv_about_image_fore);
		bgImage[1] = (ImageView)findViewById(R.id.iv_about_image_back);
		contentView = (TextView) findViewById(R.id.tv_about_content);
		initImageView(bgImage[0]);
		initImageView(bgImage[1]);
		updateContent();
	}

	private void initImageRes(){
		animPool = new GreetAnimPool(this, 5);
		imageList = new ArrayList<Drawable>();
		
		Drawable drawble = null;
		for(int i = 0; i < bgIdArray.length; i ++) {
			drawble = getResources().getDrawable(bgIdArray[i]);
			imageList.add(drawble);
		}
		
		poolIndex = 0;
		curBgIndex = 0;
		
		bgImage[0].setImageDrawable(imageList.get(0));
		bgImage[0].setAnimation(animPool.createAnimation());
		
		curBgIndex = (++ curBgIndex) % imageList.size();
	}
	
	private void loopRefresh(){
		final Handler poolHandler = new Handler();
		poolHandler.postDelayed(new Runnable() {
		    @Override
		    public void run() {	
		    	poolIndex = ++ poolIndex % bgImage.length;
		    	bgImage[poolIndex].setImageDrawable(imageList.get(curBgIndex));
		    	bgImage[poolIndex].setAnimation(animPool.createAnimation());
		    	curBgIndex = ++ curBgIndex % imageList.size();
		    	
		    	updateContent();
				poolHandler.postDelayed(this, 
						GreetAnimPool.PER_TIME_SHOWN - GreetAnimPool.PER_TIME_MIX_SHOWN );
			}		
		}, GreetAnimPool.PER_TIME_SHOWN - GreetAnimPool.PER_TIME_MIX_SHOWN);
	}
	
	protected void updateContent() {
		String text = contentArray[curContextIndex];
		contentView.setText(text);
		contentView.startAnimation(createTextAnimation());
		curContextIndex = ++curContextIndex %contentArray.length;		
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * Currently it is useless.
	 */
	private class GreetAnimPool {
		public static final int PER_TIME_SHOWN = 8000;
		public static final int PER_TIME_MIX_SHOWN = 2000;
		
		private int poolSize = 0;
		private List<Animation> animList = null;
		private Context context = null;
		
		public GreetAnimPool(Context context, int size) {
			this.context = context;
			this.poolSize = size;
			animList = new ArrayList<Animation>(size);
		}
		
		public Animation getAnimation(){
			if(!animList.isEmpty()){
				return animList.remove(0);
			}else{
				return createAnimation();
			}
		}
		
		public void recyle(Animation anim){
			if(animList.size() < poolSize){
				animList.add(anim);
			}
		}

		public Animation createAnimation() {
			AnimationSet set = new AnimationSet(true);
			int adjustHeight = (int)(getDisplayMetrics().heightPixels * 1.2);
			int adjustWidth = (int)(getDisplayMetrics().widthPixels * 1.2);
			
			TranslateAnimation translate = new TranslateAnimation(
					(int) (adjustWidth * 0.16), (int) (adjustWidth * 0.17), 
					(int) (adjustHeight * 0.16), (int) (adjustHeight * 0.17));
			Random r = new Random();
			boolean factor = r.nextBoolean();
			ScaleAnimation scale; 
			if(factor){
				scale = new ScaleAnimation(0.87f, 1.0f, 0.87f, 1.0f);
			}else {
				scale = new ScaleAnimation(1.0f, 0.87f, 1.0f, 0.87f);
			}
			AlphaAnimation alphaStart = new AlphaAnimation(0, 1);
			AlphaAnimation alphaEnd = new AlphaAnimation(1, 0);
			
			translate.setDuration(12000);
			scale.setDuration(12000);
			alphaStart.setDuration(PER_TIME_MIX_SHOWN);
			alphaEnd.setDuration(PER_TIME_MIX_SHOWN);
			
			alphaEnd.setStartOffset(PER_TIME_SHOWN - PER_TIME_MIX_SHOWN);
			alphaEnd.setDuration(PER_TIME_MIX_SHOWN);
			
			set.addAnimation(translate);
			set.addAnimation(scale);
			set.addAnimation(alphaStart);
			set.addAnimation(alphaEnd);
			set.setFillAfter(true);
			return set;
		}
	}
	
	private Animation createTextAnimation()
	{
		Animation res =  new AlphaAnimation(0,1);
		res.setFillAfter(true);
		res.setDuration(GreetAnimPool.PER_TIME_MIX_SHOWN / 2);		
		return res;
	}
	
	private void initImageView(ImageView view){
		DisplayMetrics metric = getDisplayMetrics();
		int adjustHeight = (int) (metric.heightPixels * 1.2);
		int adjustWidth = (int) (metric.widthPixels * 1.2);		
		
		RelativeLayout.LayoutParams  params = 
			new RelativeLayout.LayoutParams(adjustWidth, adjustHeight);		
		params.leftMargin = (int) (0 - (metric.widthPixels * 0.2));
		params.topMargin = (int) (0 - (metric.heightPixels * 0.2));
		
		view.setLayoutParams(params);
		view.setScaleType(ImageView.ScaleType.CENTER_CROP);
		view.setBackgroundColor(Color.TRANSPARENT);
	}
	
	private DisplayMetrics getDisplayMetrics(){
		AnimationSet set = new AnimationSet(true);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric;
	}
}
