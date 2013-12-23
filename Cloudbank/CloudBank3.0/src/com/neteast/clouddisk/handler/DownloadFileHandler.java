package com.neteast.clouddisk.handler;

import java.io.File;
import java.util.Map;


import com.lib.db.AppDao;
import com.lib.db.DataInfo;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.utils.DownLoadApplication;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
 /*
  * 下载完成提示对话框
  */
public class DownloadFileHandler extends Handler {
	private ActivityGroup context;
	private AppDao dao = null;
	private Map<String,View> appStateList; 
	public DownloadFileHandler(ActivityGroup context, View v) {
		this.context = context;
		//dao = AppDao.getInstance(context);
		//appStateList = ((DownLoadApplication)context.getApplicationContext()).getDownloadListState();
	}

	
	@Override
	public void handleMessage(final Message msg) {
		if (msg.what == 0x1458) {
			System.out.println("download file finished !!!!!!!!");
			/*
			final String path = (String) msg.getData().get("path");
			final DataInfo datainfo = (DataInfo) msg.getData().get("datainfo");
			AlertDialog dialog = null;
			dialog = new AlertDialog.Builder(context.getParent()).create();
			WindowManager.LayoutParams params = dialog.getWindow()
					.getAttributes();
			params.width = 200;
			params.height = WindowManager.LayoutParams.WRAP_CONTENT;
			dialog.getWindow().setAttributes(params);
			dialog.setTitle("下载完成");
			dialog.setMessage("是否安装 " + datainfo.getName() + " ?");
			dao.insertApp(Integer.parseInt(datainfo.getId()),
					Integer.parseInt(datainfo.getId()), datainfo.getName(),
					datainfo.getUrl(), 0, "1", getAppPackage(path));
			
			dialog.setButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					File file = new File(path);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.putExtra("path", path);
					intent.setDataAndType(
							Uri.parse("file://" + file.toString()),
							"application/vnd.android.package-archive");
					context.startActivityForResult(intent, 0);
				}
			});
			dialog.setButton2("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					DownLoadApplication da = (DownLoadApplication) context
							.getApplicationContext();
					da.getUpdateBtnList().remove(datainfo.getId());
				}
			});
			dialog.show();
			*/
		}
	}

}
