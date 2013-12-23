package com.neteast.cloudtv2;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.neteast.cloudtv2.view.ScrollLinearLayout;
import com.neteast.cloudtv2.view.ScrollLinearLayout;

/**
 *
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-3-13
 */
public class ToLeadViewActivity extends Activity{

	private Button startAppBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.to_lead_view);
		initLayout();
	}
	
	ScrollLinearLayout scrollView;
	private void initLayout(){
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		scrollView = (ScrollLinearLayout) findViewById(R.id.scrollimage);
		scrollView.setDisplayMetrics(outMetrics);
		List<Integer> listData = new ArrayList<Integer>();
		for(int i=0; i<3; i++){
			listData.add(R.drawable.guide_1+i);
		}
		scrollView.setImages(listData);
		scrollView.startScroll(ScrollLinearLayout.LEFT);
		startAppBtn = (Button) findViewById(R.id.start_app_btn);
		
		startAppBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				StartApplication();
			}
		});
		scrollView.setOnCloseListener(new ScrollLinearLayout.OnCloseListener() {
			@Override
			public void onClose() {
				StartApplication();
			}
		});
		
/*		
        // 自动显示开始应用button按钮 		
 		scrollView.setOnPageChangeListener(new ScrollLinearLayout.OnPageChangeListener() {
			@Override
			public void onClick(int pageIndex, boolean isLast) {
				if(isLast){
					Animation alpha=AnimationUtils.loadAnimation(ToLeadViewActivity.this, R.anim.a1);
					alpha.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
							startAppBtn.setVisibility(View.VISIBLE);
						}
						
						@Override
						public void onAnimationRepeat(Animation animation) {
							
						}
						
						@Override
						public void onAnimationEnd(Animation animation) {
						}
					});
					startAppBtn.setAnimation(alpha);
				}
			}
		});*/
	}
	

	private void StartApplication(){
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void nextPage(View view){
		StartApplication();
		//scrollView.resetAnima(ScrollLinearLayout.LEFT);
	}
}
