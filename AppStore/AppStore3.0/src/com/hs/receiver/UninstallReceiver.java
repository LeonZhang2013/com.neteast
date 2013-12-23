package com.hs.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hs.db.AppDao;
import com.hs.params.Params;
import com.hs.utils.AppData;
import com.neteast.data_acquisition.MyLog;

/** 应用卸载成功 接收者
 * 
 * @author LeonZhang
 * @Email zhanglinlang1@163.com */
public class UninstallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String packageName = intent.getDataString().substring(8);
		MyLog.writeLog("packageName = "+packageName+"    允许通过 = "+(!packageName.equals("com.hs.activity")));
		if(packageName.equals("com.hs.activity")) return;
		MyLog.writeLog("通过");
		AppData data = AppData.getInstance(context);
		data.uninstallApp(packageName);
		Handler handler = data.getAppActionHandler();
		if (handler != null) {
			Message msg = handler.obtainMessage();
			msg.obj = packageName;
			msg.what = Params.UNINSTALL_SUCCEED;
			handler.sendMessage(msg);
		}
	}
}
