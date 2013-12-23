package com.neteast.clouddisk.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import com.neteast.clouddisk.R;
import com.neteast.clouddisk.fragment.SpaceFragment;
import com.neteast.clouddisk.fragment.UploadFinishedFragment;
import com.neteast.clouddisk.fragment.UploadingFragment;
public class UploadActivity extends FragmentActivity {
	private View uploadLayout;
	private RadioButton rbRecord;
	private RadioButton rbUpload;
	private FragmentManager fm;
	private SpaceFragment spaceFragment;
	private UploadingFragment uploadingFragment;
	private UploadFinishedFragment finishedFragment;
	private FrameLayout spaceLayout;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.layout_upload);
		init();
		showUploading();
		/*
		int index = ((MyUploadActivity) UploadActivity.this
				.getParent()).getSelectIndex();
		
		if(index ==8){
			showUploading();
		}else showUploadRecord();
		*/
		//showUploadFinished();
	}


	private void init() {
		findView();
//		showUploadFinished();
		initFragment();
		//showUploadRecord();
		addListener();
	}
	//这个是为了测试上传完成的样式效果的，具体什么时候加载这个fragment，自己决定。
	public void showUploadFinished() {
		
		FragmentTransaction ft = fm.beginTransaction();
		ft.show(finishedFragment);
		ft.hide(uploadingFragment);
		ft.hide(spaceFragment);
		ft.commit();
		
	}


	public void findView() {
		uploadLayout = findViewById(R.id.upload_layout);
		rbRecord = (RadioButton) findViewById(R.id.upload_btn_record);
		rbUpload = (RadioButton) findViewById(R.id.upload_btn_upload);
		/*
		uploadFileCategory = findViewById(R.id.upload_file_category);
		cbVideo = (CheckBox) findViewById(R.id.upload_btn_video);
		cbAudio = (CheckBox) findViewById(R.id.upload_btn_audio);
		cbPic = (CheckBox) findViewById(R.id.upload_btn_pic);
		cbText = (CheckBox) findViewById(R.id.upload_btn_text);
		*/
		spaceLayout = (FrameLayout) findViewById(R.id.space);
	}
	
	private void initFragment() {
		uploadingFragment = new UploadingFragment();
		spaceFragment = new SpaceFragment();
		finishedFragment=new UploadFinishedFragment();
		fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.container, uploadingFragment);
		ft.add(R.id.space, spaceFragment);
		ft.add(R.id.container, finishedFragment);
		ft.commit();
		
	}
	private void addListener() {
		rbRecord.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					showUploadRecord();
				}				
			}
		});
		rbUpload.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					showUploadFile();
				}
			}
		});
	}
	/**
	 * 显示上传文件页面
	 */
	private void showUploadFile(){
		/*
		uploadFileCategory.setVisibility(View.VISIBLE);
		//uploadLayout.setBackgroundResource(R.drawable.bg_upload_grid);
		FragmentTransaction ft = fm.beginTransaction();
		ft.show(uploadFileFragment);
		ft.hide(uploadRecordFragment);
		ft.hide(uploadingFragment);
		ft.hide(spaceFragment);
		ft.commit();
		spaceLayout.setVisibility(View.GONE);
		*/
		/*
		((MyUploadActivity) UploadActivity.this
				.getParent()).startUploadFileActivity();
		*/
	}
	/**
	 * 显示查看记录页面
	 */
	private void showUploadRecord(){
		/*
		uploadFileCategory.setVisibility(View.INVISIBLE);
		//uploadLayout.setBackgroundResource(R.drawable.bg_upload_list);
		FragmentTransaction ft = fm.beginTransaction();
		ft.show(uploadRecordFragment);
		//ft.hide(uploadFileFragment);
		ft.hide(finishedFragment);
		ft.hide(uploadingFragment);
		ft.show(spaceFragment);
		ft.commit();
		spaceFragment.setUsedSpace(60);
		spaceLayout.setVisibility(View.VISIBLE);
		*/
		((MyUploadActivity) UploadActivity.this
				.getParent()).startUploadRecordActivity(1);
	}
	
	/**
	 * 显示查看记录页面
	 */
	public void showUploading(){
		//rbUpload.setChecked(true);
		//uploadLayout.setBackgroundResource(R.drawable.bg_upload_list);
		FragmentTransaction ft = fm.beginTransaction();
		ft.show(uploadingFragment);
		ft.show(spaceFragment);
		ft.hide(finishedFragment);
		ft.commit();
		spaceLayout.setVisibility(View.VISIBLE);
	}
}
