package com.hs.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.hs.params.Params;
import com.hs.utils.AppData;
import com.lib.log.MyLog;

/** 应用安装成功 接收者
 * 
 * @author LeonZhang
 * @Email zhanglinlang1@163.com */
public class InstallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		/*
		 * intent.getDataString() == package:com.doodlejoy.studio.kidsdoojoy 需要截取前面的8个无用字符： 截取后 pack =
		 * com.doodlejoy.studio.kidsdoojoy
		 */
		MyLog.writeLog("InstallReceiver-1111111111");
		String packageName = intent.getDataString().substring(8);
		AppData data = AppData.getInstance(context);
		data.installedSuccess(packageName);
		Handler handler = data.getAppActionHandler();
		MyLog.writeLog("InstallReceiver-2222222222");
		if (handler != null) {
			Message msg = handler.obtainMessage();
			msg.obj = packageName;
			msg.what = Params.INSTALL_SUCCEED;
			handler.sendMessage(msg);
		}
	}
}
