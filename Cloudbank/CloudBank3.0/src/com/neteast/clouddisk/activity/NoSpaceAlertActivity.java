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
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class NoSpaceAlertActivity extends Activity{
	private AlertDialog d;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setContentView(R.layout.nospace_alert);
		
		//View view = View.inflate(UploadAlertActivity.this, R.layout.tab_1, null);  

		AlertDialog.Builder b = new AlertDialog.Builder(NoSpaceAlertActivity.this);  
		b.setCancelable(false);
		

		//b.setView(view);  
		


		d = b.create();
		
		//d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG); //系统中关机对话框就是这个属性  

		d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); //窗口可以获得焦点，响应操作  

		//d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY); //窗口不可以获得焦点，点击时响应窗口后面的界面点击事件  
		
		d.setTitle(getResources().getString(R.string.system_tips));
		d.setMessage(getResources().getString(R.string.space_full));
		d.setButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Handler handler = ((DownLoadApplication)getApplication()).getUploadRecorderHandler();
				if(handler!=null) handler.sendEmptyMessage(0);
				dialog.dismiss();
				NoSpaceAlertActivity.this.finish();
				
			}
		});
		
		d.show();  
		
		/*
		Button yesButton = (Button) findViewById(R.id.alertentry);  	    

		yesButton.setOnClickListener(new View.OnClickListener() {  
		@Override 

		public void onClick(View v) {
			NoSpaceAlertActivity.this.finish();
		}  

		});  
		*/
	}
	/*
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		System.out.println("KeyCode = " + keyCode);
		
		if(keyCode == KeyEvent.KEYCODE_BACK){
			System.out.println("on back ");
			d.dismiss();
			NoSpaceAlertActivity.this.finish();
			return true;
		}else{ 
			return super.onKeyDown(keyCode, event); 
		}
	} 
	*/
}
