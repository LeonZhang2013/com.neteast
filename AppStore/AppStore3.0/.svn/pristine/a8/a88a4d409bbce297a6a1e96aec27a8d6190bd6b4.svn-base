package com.hs.utils;

import com.hs.activity.DownLoadApplication;
import com.hs.domain.User;
import com.lib.appstore.LibAppstore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.WindowManager;

public class ExitHandler {
	public static void exitApp(final Activity activity) {
		AlertDialog dialog = new AlertDialog.Builder(activity).create();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = 200;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(params);
		dialog.setTitle("退出");
		dialog.setMessage("你确定要退出应用仓库吗？");
		dialog.setButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				activity.finish();
				dialog.dismiss();
				int userid = User.getUserId();	
				String deviceid = Tools.getDeviceId(activity);
				LibAppstore.getInstance(activity).userReport("2",String.valueOf(userid),deviceid);
			}
		});
		dialog.setButton2("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
}
