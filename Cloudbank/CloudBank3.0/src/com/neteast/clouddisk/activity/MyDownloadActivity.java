package com.neteast.clouddisk.activity;


import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.neteast.clouddisk.R;

public class MyDownloadActivity extends ActivityGroup {
	ImageButton settingButton;
	 

	ImageButton videoButton, musiceButton, pictureButton;
	ImageButton newsButton;
	ImageView videoselect, musicselect, picutrueselect;
	RelativeLayout container;
	private int selectIndex = 1;
	private String  curActivity="video";

	public int getSelectIndex() {
		return selectIndex;
	}

	public void setSelectIndex(int selectIndex) {
		this.selectIndex = selectIndex;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mydownload);
		videoButton = (ImageButton) findViewById(R.id.videobutton);
		musiceButton = (ImageButton) findViewById(R.id.musicbutton);
		pictureButton = (ImageButton) findViewById(R.id.picturepbutton);
		//newsButton = (ImageButton) findViewById(R.id.newsbutton);
		videoselect = (ImageView) findViewById(R.id.cateselectimage1);
		musicselect = (ImageView) findViewById(R.id.cateselectimage2);
		picutrueselect = (ImageView) findViewById(R.id.cateselectimage3);
		container = (RelativeLayout) findViewById(R.id.datacontainer);
		final LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		/*
		 * container.addView(getLocalActivityManager().startActivity( "game",
		 * new Intent(PublicSpaceActivity.this, PublicVideoViewer.class)
		 * .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)) .getDecorView());
		 */
		settingButton = (ImageButton) findViewById(R.id.settingbutton);
		settingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectIndex = 6;
				getLocalActivityManager().destroyActivity(curActivity, true); 
				container.removeAllViews();
				View view = getLocalActivityManager().startActivity(
						"settings",
						new Intent(MyDownloadActivity.this,
								SettingActivity.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();
				view.setLayoutParams(param);
				container.addView(view);
				curActivity="settings";

			}
		});
		videoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				displaySelectButton(videoselect,musicselect, picutrueselect);
				getLocalActivityManager().destroyActivity(curActivity, true); 
				container.removeAllViews();
				View view = getLocalActivityManager().startActivity(
						"video",
						new Intent(MyDownloadActivity.this,
								MyDownloadVideoActivity.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();
				view.setLayoutParams(param);
				container.addView(view);
				curActivity="video";
				setSelectIndex(1);
			}
		});
		musiceButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				displaySelectButton(musicselect,picutrueselect,	videoselect);
				getLocalActivityManager().destroyActivity(curActivity, true); 
				container.removeAllViews();
				View view = getLocalActivityManager().startActivity(
						"music",
						new Intent(MyDownloadActivity.this,
								MyDownloadMusicActivity.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();
				view.setLayoutParams(param);
				container.addView(view);
				curActivity="music";
				setSelectIndex(2);
				
			}
		});
		pictureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				displaySelectButton(picutrueselect,musicselect, videoselect);
				getLocalActivityManager().destroyActivity(curActivity, true); 
				container.removeAllViews();
				View view = getLocalActivityManager().startActivity(
						"picture",
						new Intent(MyDownloadActivity.this,
								MyDownloadPictureActivity.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();
				view.setLayoutParams(param);
				container.addView(view);
				curActivity="picture";
				setSelectIndex(3);
			}
		});
	
		View view = getLocalActivityManager().startActivity(
				"video",
				new Intent(MyDownloadActivity.this,
						MyDownloadVideoActivity.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();
		view.setLayoutParams(param);
		container.addView(view);
		curActivity="video";
		setSelectIndex(1);
	}
	public void destroyCurActivityGroup(){
		getLocalActivityManager().destroyActivity(curActivity, true); 
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			 return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public RelativeLayout getContainer() {
		return container;
	}

	public void setContainer(RelativeLayout container) {
		this.container = container;
	}

	/**
	 * 显示或隐藏�1�?�中图片
	 * 
	 * @param v1
	 * @param v2
	 * @param v3
	 */
	private void displaySelectButton(ImageView v1, ImageView v2, ImageView v3) {
		v1.setVisibility(View.VISIBLE);
		v2.setVisibility(View.INVISIBLE);
		v3.setVisibility(View.INVISIBLE);
	}
}