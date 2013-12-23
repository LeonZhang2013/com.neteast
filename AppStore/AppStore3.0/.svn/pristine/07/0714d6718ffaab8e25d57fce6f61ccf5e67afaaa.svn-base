package com.hs.handler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;

import com.hs.db.AppDao;
import com.hs.domain.AppBean;
import com.hs.utils.InstallHandler;

public class UpgradeHandler extends Handler {
	private Context context;
	private AppBean bean;

	public UpgradeHandler(Context context,AppDao dao,AppBean bean) {
		this.context = context;
		this.bean = bean;
	}
	@Override
	public void handleMessage(Message msg) {
		if (msg.what == 0x889) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("替换应用程序");
			builder.setMessage("安装此版本将会替换现有版本，你确定要继续安装吗？\n安装版本：" + bean.getNewVersion()
					+ "\n本地版本：" + bean.getVersion());
			builder.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					InstallHandler.upgradeApp(bean.getApkName(), context, bean.getId(),
							bean.getVersion(), bean.getNewVersion());

				}
			});
			builder.setNegativeButton("取消",null);
			builder.create().show();
		}
	}
}
