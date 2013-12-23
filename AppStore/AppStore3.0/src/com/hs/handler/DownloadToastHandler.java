package com.hs.handler;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

import com.hs.db.AppDao;
import com.hs.params.Params;

public class DownloadToastHandler extends Handler {
	Activity mActivity;
	AppDao dao;
	public static Activity pop = null;

	public DownloadToastHandler(Activity context) {
		this.mActivity = context;
		dao = AppDao.getInstance(context);
	}

	@Override
	public void handleMessage(final Message msg) {
		if (msg.what == Params.DOWNLOADSTATUS_COMPLETE) {
			String message = (String) msg.getData().get("downloadmsg");
			final String fileName = (String) msg.getData().get("fileName");
			AlertDialog dialog = null;
			if (pop != null) {
				dialog = new AlertDialog.Builder(pop).create();
			} else {
				dialog = new AlertDialog.Builder(mActivity).create();
			}
			WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
			params.width = 200;
			params.height = WindowManager.LayoutParams.WRAP_CONTENT;
			dialog.getWindow().setAttributes(params);
			dialog.setTitle("下载完成");
			dialog.setMessage(message);
			dialog.setButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					File file = new File(Params.DOWNLOAD_FILE_PATH + fileName);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.putExtra("IsCreateShortCut", true); // 设置产生快捷方式
					intent.putExtra("ExpandedInstallFlg", true); // 设置禁止自启动标准
					intent.setDataAndType(Uri.parse("file://" + file.toString()), "application/vnd.android.package-archive");
					mActivity.startActivity(intent);
				}
			});
			dialog.setButton2("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			try{
				dialog.show();
			}catch (Exception e) {
				//应用已经关闭了
			}
		}
	}

}
