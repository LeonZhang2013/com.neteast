package com.neteast.clouddisk.handler;

import java.io.File;
import java.util.Map;


import com.lib.db.AppDao;
import com.lib.db.DataInfo;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.activity.OpenFile;
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
import android.widget.Toast;
 /*
  * 下载完成提示对话框
  */
public class DownloadToastHandler extends Handler {
	private ActivityGroup context;
	private AppDao dao = null;
	private Map<String,View> appStateList; 
	public DownloadToastHandler(ActivityGroup context, View v) {
		this.context = context;
		dao = AppDao.getInstance(context);
		appStateList = ((DownLoadApplication)context.getApplicationContext()).getDownloadListState();
	}

	/**
	 * 下载完成后更新按钮显示样式
	 * @param datainfo
	 */
	private void changeState(DataInfo datainfo){
		View vv= appStateList.get(datainfo.getId());
		if(vv!=null){
			vv.setClickable(true);
			//((ImageView)vv).setImageResource(R.drawable.installbuttonstyle);
			((ImageView)vv).setImageResource(R.drawable.installbtnstyle);
			appStateList.remove(datainfo.getId());
		}
	}
	@Override
	public void handleMessage(final Message msg) {
		if (msg.what == 0x1458) {
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
			DownLoadApplication da = (DownLoadApplication) context
					.getApplicationContext();
			da.setAppList(dao.getAppList());
			changeState(datainfo);//下载完毕更改按钮样式
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
		}else if(msg.what == 0x1459){
			System.out.println(" download toast handler msg = " + msg.what);
			final String path = (String) msg.getData().get("path");
			final DataInfo datainfo = (DataInfo) msg.getData().get("datainfo");
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(Intent.ACTION_VIEW);
			File f = new File(path);
			Uri uri = Uri.fromFile(f);
			intent.setDataAndType(uri,new OpenFile().getType(f));
			try{
				context.startActivity(intent);
			}catch(Exception ee){
				Toast.makeText(context, context.getResources().getString(R.string.noapptoopenthefile_notices),
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	private String getAppPackage(String path) {
		String packageName = null;
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo(path,
					PackageManager.GET_ACTIVITIES);
			ApplicationInfo appInfo = null;
			if (info != null) {
				appInfo = info.applicationInfo;
				packageName = appInfo.packageName;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return packageName;

	}

}
