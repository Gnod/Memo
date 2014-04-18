package com.gnod.memo.widgets;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

public class ItemAnimPool {

	private Context context = null;
	private int size = 0;
	private List<Animation> animPool = null;
	private List<Animation> textAnimPool = null;
	
	public ItemAnimPool(Context context, int size){
		this.context = context;
		this.size = size;
		animPool = new ArrayList<Animation>(size);
		textAnimPool = new ArrayList<Animation>(size);
	}
	
	public Animation getAnimation(){
		if(!animPool.isEmpty()){
			return animPool.remove(0);
		} else {
			return createAnimation();
		}
	}
	
	public Animation getTextAnimation(){
		if(!textAnimPool.isEmpty()){
			return textAnimPool.remove(0);
		}else {
			return createTextAnimation();
		}
	}
	
	private Animation createTextAnimation() {
		AnimationSet set = new AnimationSet(true);
		TranslateAnimation tanim = new TranslateAnimation(
	            Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.0f,
	            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
	        );
		tanim.setDuration(200);
		AlphaAnimation alphaStart = new AlphaAnimation(0, 1);
		alphaStart.setDuration(200);
		alphaStart.setStartOffset(80);
		set.addAnimation(tanim);
		set.addAnimation(alphaStart);
		return set;
	}
	
	public void recyleTextAnimation(Animation anim){
		if(textAnimPool.size() < size) {
			textAnimPool.add(anim);
		}
	}

	public void recyle(Animation anim) {
		if(animPool.size() < size) {
			animPool.add(anim);
		}
	}

	private Animation createAnimation() {
		AnimationSet set = new AnimationSet(true);
		TranslateAnimation tanim = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, -0.8f, Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
		tanim.setDuration(500);
		AlphaAnimation alphaStart = new AlphaAnimation(0, 1);
		alphaStart.setStartOffset(200);
		alphaStart.setDuration(300);
		set.addAnimation(tanim);
		set.addAnimation(alphaStart);
		
		return set;
	}
	
}
