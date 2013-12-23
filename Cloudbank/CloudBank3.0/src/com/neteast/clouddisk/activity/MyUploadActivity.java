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

public class MyUploadActivity extends ActivityGroup {
	ImageButton settingButton;
	ImageButton mydownloadButton;
	ImageButton recommendButton;
	ImageButton videoButton, musiceButton, pictureButton, filebutton;
	ImageView videoselect, musicselect, picutrueselect, fileselect;
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
		setContentView(R.layout.myupload);
        
		videoButton = (ImageButton) findViewById(R.id.videobutton);
		musiceButton = (ImageButton) findViewById(R.id.musicbutton);
		pictureButton = (ImageButton) findViewById(R.id.picturepbutton);
		filebutton = (ImageButton) findViewById(R.id.filebutton);
		videoselect = (ImageView) findViewById(R.id.cateselectimage1);
		musicselect = (ImageView) findViewById(R.id.cateselectimage2);
		picutrueselect = (ImageView) findViewById(R.id.cateselectimage3);
		fileselect = (ImageView) findViewById(R.id.cateselectimage4);
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
				getLocalActivityManager().destroyActivity(curActivity, true);
				container.removeAllViews();
				View view = getLocalActivityManager().startActivity(
						"settings",
						new Intent(MyUploadActivity.this,
								SettingActivity.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();
				view.setLayoutParams(param);
				container.addView(view);
				curActivity="settings";
				setSelectIndex(6);
			}
		});
		videoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectIndex(1);
				displaySelectButton(videoselect,musicselect, picutrueselect,fileselect);
				getLocalActivityManager().destroyActivity(curActivity, true);
				container.removeAllViews();
				View view = getLocalActivityManager().startActivity(
						"video",
						new Intent(MyUploadActivity.this,
								MyUploadVideoActivity.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();
				view.setLayoutParams(param);
				container.addView(view);
				curActivity="video";
			}
		});
		musiceButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectIndex(2);
				displaySelectButton(musicselect,picutrueselect,
						videoselect, fileselect);
				getLocalActivityManager().destroyActivity(curActivity, true);
				container.removeAllViews();
				View view = getLocalActivityManager().startActivity(
						"music",
						new Intent(MyUploadActivity.this,
								MyUploadMusicActivity.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();
				view.setLayoutParams(param);
				container.addView(view);
				curActivity="music"; 		
			}
		});
		pictureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setSelectIndex(3);
				displaySelectButton(picutrueselect,musicselect, 
						videoselect, fileselect);
				getLocalActivityManager().destroyActivity(curActivity, true);
				container.removeAllViews();
				View view = getLocalActivityManager().startActivity(
						"picture",
						new Intent(MyUploadActivity.this,
								MyUploadPictureActivity.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();
				view.setLayoutParams(param);
				container.addView(view);
				curActivity="picture"; 
			}
		});
		filebutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setSelectIndex(4);
				displaySelectButton(fileselect, picutrueselect, musicselect,
						videoselect);
				getLocalActivityManager().destroyActivity(curActivity, true);
				container.removeAllViews();
				container.addView(getLocalActivityManager().startActivity(
						"file",
						new Intent(MyUploadActivity.this,
								MyUploadFileActivity.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView());
				curActivity="file"; 
				
			}
		});

		View view = getLocalActivityManager().startActivity(
				"video",
				new Intent(MyUploadActivity.this,
						MyUploadVideoActivity.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();
		view.setLayoutParams(param);
		container.addView(view);
		curActivity="video"; 
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			 return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void startUploadRecordActivity(int type){
		
		final LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		container.removeAllViews();
			
		Intent intent = new Intent(this, UploadRecordActivity.class)
		.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("uploadType", type);
			
		View view = getLocalActivityManager().startActivity(
				"uploadRecordFile",
				intent).getDecorView();
		
		view.setLayoutParams(param);
		container.addView(view);
	}
	public void startUploadActivity(int value){
		
		final LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		container.removeAllViews();
		setSelectIndex(value);
		View view = getLocalActivityManager().startActivity(
				"upload",
				new Intent(MyUploadActivity.this,
						UploadActivity.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();
		view.setLayoutParams(param);
		container.addView(view);
		setSelectIndex(5);
	}
	public void startUploadFileActivity(int type){
		
		final LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		container.removeAllViews();
			
		Intent intent = new Intent(this, UploadFileActivity.class)
		.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("uploadType", type);
			
		View view = getLocalActivityManager().startActivity(
				"uploadFile",
				intent).getDecorView();
		
		view.setLayoutParams(param);
		container.addView(view);
		setSelectIndex(6);
	}
	
	public void startRecyclerActivity(int type){
		
		final LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		container.removeAllViews();
			
		Intent intent = new Intent(this, RecyclerActivity.class)
		.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("Type", type);
			
		View view = getLocalActivityManager().startActivity(
				"recycler",
				intent).getDecorView();
		
		view.setLayoutParams(param);
		container.addView(view);
		//setSelectIndex(6);
	}

	@Override
	public void onDestroy()
	{
		//unregisterReceiver(mStatusListener);
		//MusicUtils.unbindFromService(mToken);
		super.onDestroy();
	        //System.out.println("***************** playback activity onDestroy\n");
	}
	
	public RelativeLayout getContainer() {
		return container;
	}

	public void setContainer(RelativeLayout container) {
		this.container = container;
	}

	/**
	 * 显示或隐藏选中图片
	 * 
	 * @param v1
	 * @param v2
	 * @param v3
	 */
	private void displaySelectButton(ImageView v1, ImageView v2, ImageView v3,
			ImageView v4) {
		v1.setVisibility(View.VISIBLE);
		v2.setVisibility(View.INVISIBLE);
		v3.setVisibility(View.INVISIBLE);
		v4.setVisibility(View.INVISIBLE);
	}
}