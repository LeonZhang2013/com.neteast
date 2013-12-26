package com.hs.handler;

import java.util.Map;

import android.os.Handler;
import android.os.Message;

import com.hs.activity.AppDetailActivity;

public class LoginHandler extends Handler {
	
	AppDetailActivity ac;
	Map<String, Object> mmap;
	
	public LoginHandler(AppDetailActivity ac,Map<String, Object> map) {
		this.ac = ac;
		mmap = map;
	}

	@Override
	public void handleMessage(Message msg) {
		if (msg.what == 0x999) {
			ac.showcomment(mmap);
		}
	}
}
