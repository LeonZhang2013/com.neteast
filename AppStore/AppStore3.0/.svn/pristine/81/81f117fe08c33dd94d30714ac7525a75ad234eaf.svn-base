package com.hs.utils;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.hs.activity.DownLoadApplication;
import com.hs.db.AppDao;
import com.hs.domain.AppBean;
import com.hs.params.Params;

public class InstallHandler {
	/**
	 * 安装应用程序
	 * 
	 * @param fileName
	 * @param context
	 * @param appId
	 * @param dao
	 */
	public static void installApp(String fileName, Context context, int appId,
			AppDao dao) {
		File file = new File(Params.DOWNLOAD_FILE_PATH + fileName);
		if (!file.exists()) {
			ToastHandler.toastDisplay("安装失败！文件不存在!", context);
			dao.deleteApp(appId);
			return;
		}
		//final int GROUP_INSTALL_FLG=0x20;
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.putExtra("IsCreateShortCut",true);   //设置产生快捷方式
		intent.putExtra("ExpandedInstallFlg",true);   //设置禁止自启动标准
		intent.setDataAndType(Uri.parse("file://" + file.toString()),
				"application/vnd.android.package-archive");
		ToastHandler.toastDisplay("开始安装...", context);
		((Activity) context).startActivityForResult(intent, 0);
	}

	/**
	 * 升级应用程序
	 * 
	 * @param fileName
	 * @param context
	 * @param appId
	 * @param dao
	 */
	public static void upgradeApp(final String fileName, final Context context,
			final int appId, String oldVersion,
			String newVersion) {

		File file = new File(Params.UPGRADE_DOWNLOAD_FILE_PATH + fileName);
		if (!file.exists()) {
			ToastHandler.toastDisplay("安装失败！文件不存在!", context);
			//dao.deleteApp(appId);
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.putExtra("IsCreateShortCut",false);   //设置产生快捷方式
		intent.putExtra("ExpandedInstallFlg",true);   //设置禁止自启动标准
		intent.setDataAndType(Uri.parse("file://" + file.toString()),
				"application/vnd.android.package-archive");
		//System.out.println("test11 start intent:"+intent);
		ToastHandler.toastDisplay("开始安装...", context);
		((Activity) context).startActivityForResult(intent, 0);
	}

	/**
	 * 卸载应用程序
	 * 
	 * @param appId
	 * @param context
	 * @param dao
	 */
	public static void uninstallApp(String packName, Context context, AppDao dao) {
		Intent intent = new Intent(Intent.ACTION_DELETE);
		Uri packageUri = Uri.parse("package:" + packName);
		intent.setData(packageUri);
		((Activity) context).startActivityForResult(intent, 0);
		ToastHandler.toastDisplay("开始卸载...", context);
		dao.updateAppStatus(0, 2, packName);
	}

	/**
	 * @throws NameNotFoundException
	 * 
	 */
	public static void openApp(int appId, Context context,
			DownLoadApplication download) throws NameNotFoundException {
		int index = Tools.getIndexByAppId(AppData.getInstance(context).getDownloadList(),appId);
		if (index != -1) {
			List<AppBean> list = AppData.getInstance(context).getDownloadList();
			AppBean map = list.get(index);
			if (map.getDownloadStatus() == 2 &&  map.getStatus() == 1) {
				String packageName = map.getPackageName();
				PackageInfo pi = context.getPackageManager().getPackageInfo(
						packageName, 0);

				Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
				resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				resolveIntent.setPackage(pi.packageName);

				List<ResolveInfo> apps = context.getPackageManager()
						.queryIntentActivities(resolveIntent, 0);

				ResolveInfo ri = apps.iterator().next();
				if (ri != null) {
					String pkName = ri.activityInfo.packageName;
					String className = ri.activityInfo.name;

					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);

					ComponentName cn = new ComponentName(pkName, className);

					intent.setComponent(cn);
					context.startActivity(intent);
				}

			} else {
				ToastHandler.toastDisplay("文件未安装", context);
			}
		} else {
			ToastHandler.toastDisplay("文件未下载", context);
		}
	}
}
