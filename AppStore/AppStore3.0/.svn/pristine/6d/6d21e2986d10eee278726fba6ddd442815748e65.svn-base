package com.hs.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.hs.activity.DownLoadApplication;
import com.hs.handler.LoginHandler;

public class LoginReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		DownLoadApplication app = (DownLoadApplication) context
				.getApplicationContext();
		LoginHandler login = app.getLoginHandler();
		if(login!=null){
			Message msg = new Message();
			msg.what = 0x999;
			login.sendMessage(msg);
		}
	}
}
