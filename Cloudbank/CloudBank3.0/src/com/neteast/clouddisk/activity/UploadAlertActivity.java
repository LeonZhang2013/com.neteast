package com.neteast.clouddisk.activity;

import java.io.File;

import com.lib.db.AppDao;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.service.UploadService;
import com.neteast.clouddisk.utils.DownLoadApplication;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class UploadAlertActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab_1);
		Intent i = getIntent();
		String title = i.getStringExtra("title");
		String message = i.getStringExtra("message");
		final String fileName = i.getStringExtra("fileName");
		
		final AppDao UploadDao = AppDao.getInstance(this);
		/*
		View view = View.inflate(UploadAlertActivity.this, R.layout.tab_1, null);  

		AlertDialog.Builder b = new AlertDialog.Builder(UploadAlertActivity.this);  
		
		

		b.setView(view);  
		


		final AlertDialog d = b.create();
		
		//d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG); //系统中关机对话框就是这个属性  

		d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); //窗口可以获得焦点，响应操作  

		//d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY); //窗口不可以获得焦点，点击时响应窗口后面的界面点击事件  
		
		d.setTitle("上传队列中有失败任务,");
		d.setMessage("是否重新上传" + " ?");
		d.setButton("重新上传", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				UploadDao.updateUploadFailedTask(0);
				dialog.dismiss();
				UploadAlertActivity.this.finish();
				UploadAlertActivity.this.startService(new Intent(UploadAlertActivity.this, UploadService.class));
				Handler handler = ((DownLoadApplication)getApplication()).getUploadRecorderHandler();
				if(handler!=null) handler.sendEmptyMessage(0);
			}
		});
		d.setButton2("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				UploadAlertActivity.this.finish();
			
			}
		});
		
		d.show();  
		*/
		
		TextView titletv = (TextView) findViewById(R.id.alert_title); 
		TextView messagetv = (TextView) findViewById(R.id.alert_message);
		titletv.setText(title);
		messagetv.setText(message);
		ImageButton yesButton = (ImageButton) findViewById(R.id.alertreupload);  

		ImageButton canclButton = (ImageButton) findViewById(R.id.alertcancel);  

		    

		yesButton.setOnClickListener(new View.OnClickListener() {  
		@Override 

		public void onClick(View v) {
			if(fileName!=null){
				File tmpfile = new File(fileName);
				if(tmpfile.exists()){
					UploadDao.updateUploadFailedTask(0);
					UploadAlertActivity.this.finish();
					UploadAlertActivity.this.startService(new Intent(UploadAlertActivity.this, UploadService.class));
					Handler handler = ((DownLoadApplication)getApplication()).getUploadRecorderHandler();
					if(handler!=null) handler.sendEmptyMessage(0);
				}else{
					Toast.makeText(getApplicationContext(), "文件不存在", Toast.LENGTH_LONG).show(); 
				}
			}else{
				UploadDao.updateUploadFailedTask(0);
				UploadAlertActivity.this.finish();
				UploadAlertActivity.this.startService(new Intent(UploadAlertActivity.this, UploadService.class));
				Handler handler = ((DownLoadApplication)getApplication()).getUploadRecorderHandler();
				if(handler!=null) handler.sendEmptyMessage(0);
			}
		}  

		});  

		    

		canclButton.setOnClickListener(new View.OnClickListener() {  
		@Override 

		public void onClick(View v) {  
			Handler handler = ((DownLoadApplication)getApplication()).getUploadRecorderHandler();
			if(handler!=null) handler.sendEmptyMessage(0);
			UploadAlertActivity.this.finish();

		}  
		
		}); 
		
	}
}
