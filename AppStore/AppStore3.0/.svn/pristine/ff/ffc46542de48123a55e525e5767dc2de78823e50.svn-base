package com.hs.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hs.db.AppDao;
import com.hs.domain.AppBean;
import com.hs.handler.DownloadProgressHandlerX;
import com.hs.handler.DownloadToastHandler;
import com.hs.params.Params;
import com.lib.appstore.LibAppstore;
import com.lib.net.WeiboException;
import com.neteast.data_acquisition.DataAcqusition;

/** @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2012-12-31 */
public class AppData {

	private List<AppBean> mInstalledList;
	private List<AppBean> mDownLoadList;
	private List<AppBean> mUpgradeList;
	private Map<Integer, Handler> mThreadhandlers;
	private int mUpgradeCount, mInstalledCount, mDownloadingCount;
	private AppDao mAppDao;
	private LibAppstore mLib;
	private Context mContext;
	private static AppData AppData;
	private DownloadToastHandler mDownloadToastHandler;// 下载完成提示handler
	private static ExecutorService downloadPool;
	private Handler mAppHandler;
	private DownloadProgressHandlerX handler;

	/** 构造方法
	 * 
	 * @param context */
	private AppData(Context context) {
		this.mContext = context;
		mAppDao = AppDao.getInstance(context);
		mLib = LibAppstore.getInstance(context);
		mThreadhandlers = new HashMap<Integer, Handler>();
	}

	/** 初始化数据 启动上次未下载完成的应用
	 * 
	 * @param context */
	public void initData(Context context) {
		mInstalledList = getDevicesInstalledApp(context);
		mDownLoadList = mAppDao.getDownLoadApp();
		mUpgradeList = getUpgradeList();
		if (downloadPool == null) {
			downloadPool = Executors.newFixedThreadPool(3);// 下载
			continueDownload(mDownLoadList);
		}
	}

	/** 获取设备上安装的非系统应用
	 * 
	 * @param context
	 * @return */

	private List<AppBean> get2DevicesInstalledApp(Context context) {
		List<AppBean> beanList = new ArrayList<AppBean>();
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> list = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES);
		for (PackageInfo packageInfo : list) {
			Log.i("flag_params", packageInfo.applicationInfo.flags + "  & " + ApplicationInfo.FLAG_SYSTEM);
			Log.i("flag_params", (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) + "=" + 0);
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				Log.i("flag_true", packageInfo.applicationInfo.flags + "  & " + ApplicationInfo.FLAG_SYSTEM);
				AppBean bean = new AppBean();
				bean.setPackageName(packageInfo.packageName);
				bean.setVersion(packageInfo.versionName);
				String appLable = (String) pm.getApplicationLabel(packageInfo.applicationInfo);
				bean.setTitle(appLable);
				bean.setSize(Tools.getFileSize(packageInfo));
				beanList.add(bean);
			}
		}
		return beanList;
	}

	/** 获取设备上安装的非系统应用
	 * 
	 * @param context
	 * @return */
	private List<AppBean> getDevicesInstalledApp(Context context) {
		List<AppBean> beanList = new ArrayList<AppBean>();
		try {
			PackageManager pm = context.getPackageManager();
			List<ApplicationInfo> list = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
			for (ApplicationInfo appInfo : list) {
				if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					AppBean bean = new AppBean();
					bean.setPackageName(appInfo.packageName);
					PackageInfo info = pm.getPackageInfo(appInfo.packageName, 0);
					bean.setVersion(info.versionName);
					String appLable = (String) pm.getApplicationLabel(appInfo);
					bean.setTitle(appLable);
					bean.setSize(Tools.getFileSize(info));
					beanList.add(bean);
				}
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return beanList;
	}

	/** 获取handler
	 * 
	 * @return */
	public Handler getAppActionHandler() {
		return mAppHandler;
	}

	public void setAppActionHandler(Handler mAppHandler) {
		this.mAppHandler = mAppHandler;
	}

	public static AppData getInstance(Context context) {
		if (AppData == null) {
			AppData = new AppData(context);
		}
		return AppData;
	}

	public List<AppBean> getInstalledList() {
		if (mInstalledList == null) {
			initInstalledList(mContext);
		}
		return mInstalledList;
	}

	public List<AppBean> getDownloadList() {
		if (mDownLoadList == null) {
			initDownloadList();
		}
		return mDownLoadList;
	}

	public List<AppBean> getUpgradeList() {
		if (mUpgradeList == null) {
			initUpgradeList();
		}
		return mUpgradeList;
	}

	public int getUpgradeCount() {
		return getUpgradeList().size();
	}

	public int getInstalledCount() {
		return getInstalledList().size();
	}

	public int getDownloadCount() {
		return getDownloadList().size();
	}

	public void initDownloadList() {
		mDownLoadList = mAppDao.getDownLoadApp();
	}

	public void setHandler(DownloadProgressHandlerX handler) {
		this.handler = handler;
	}

	public void initInstalledList(Context context) {
		mInstalledList = new ArrayList<AppBean>();
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> list = pm.getInstalledPackages(0);
		for (PackageInfo packageInfo : list) {
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				AppBean bean = new AppBean();
				String appLable = (String) pm.getApplicationLabel(packageInfo.applicationInfo);
				bean.setPackageName(packageInfo.packageName);
				bean.setVersion(packageInfo.versionName);
				bean.setTitle(appLable);
				bean.setId(-1);
				bean.setSize(Tools.getFileSize(packageInfo));
				mInstalledList.add(bean);
			}
		}
	}

	public void initUpgradeList() {
		String oldVersion;
		String newVersion;
		int oldId;
		int newId;

		List<AppBean> installList = mAppDao.getAppByStatus(1);
		mUpgradeList = new ArrayList<AppBean>();
		if (installList == null || installList.size() == 0) {
			return;
		}

		List<AppBean> latestAppList = new ArrayList<AppBean>();
		StringBuilder sb = new StringBuilder();
		for (Iterator<AppBean> iterator = installList.iterator(); iterator.hasNext();) {
			AppBean bean = iterator.next();
			sb.append(bean.getId());
			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
		latestAppList = mLib.Get_latest_version(sb.toString());

		for (Iterator<AppBean> iterator = installList.iterator(); iterator.hasNext();) {
			AppBean oldBean = iterator.next();
			oldVersion = oldBean.getVersion().trim();
			oldId = oldBean.getId();
			for (Iterator<AppBean> iterator2 = latestAppList.iterator(); iterator2.hasNext();) {
				AppBean newBean = iterator2.next();
				newId = newBean.getId();
				newVersion = newBean.getVersion().trim();
				if (oldId == newId && oldId != 0) {
					if ((oldVersion.compareTo(newVersion) == 0) && oldVersion != "" && newVersion != "") {
						oldBean.setNewVersion(newVersion);
						oldBean.setUrl(newBean.getUrl());
						mUpgradeList.add(oldBean);
					}
				}
			}
		}
	}

	/** 删除下载应用，如果当前应用是升级模块加入的会自动还原到升级模块。
	 * 
	 * @param bean */
	public void deleteDownloadingApp(AppBean bean) {
		int index = Tools.getIndexByAppId(mDownLoadList, bean.getId());
		if (index != -1)
			mDownLoadList.remove(index);

		// 如果状态是安装成功表示该对象是从升级模块添加进来,需要还原到安装状态。
		if (bean.getStatus() == Params.INSTALL_SUCCEED) {
			bean.setDownloadStatus(Params.DOWNLOADSTATUS_COMPLETE);
			mUpgradeList.add(bean);
			mAppDao.updateAppStatus(bean.getStatus(), bean.getDownloadStatus(), bean.getPackageName());
		} else {
			mAppDao.deleteApp(bean.getId());
		}
		Tools.deleteLocalFile(bean.getApkName());
	}

	public void updateIgnoreStatus(int appId, boolean isIgnore) {
		if (isIgnore) {
			mAppDao.updateIgnoreStatus(appId, System.currentTimeMillis());
		} else {
			mAppDao.updateIgnoreStatus(appId, 0);
		}
	}

	/** 实时播发下载进度
	 * 
	 * @param appId
	 * @param rate */
	private void updateDownloadRate(int appId, int rate, int oldRate, int hasRead) {
		if (rate > oldRate) {
			mAppDao.updateDownloadProgress(appId, oldRate, hasRead);
		}
	}

	private class DownloadThread extends Thread {
		private AppBean bean;

		public DownloadThread(AppBean bean) {
			this.bean = bean;
		}

		@Override
		public void run() {
			RandomAccessFile file = null;
			InputStream in = null;
			String filePath = Params.DOWNLOAD_FILE_PATH + bean.getApkName();
			try {
				if (Tools.checkFileExists(filePath)) { // 查看文件是否存在，如果存在继续下载
					file = new RandomAccessFile(filePath, "rw");
				} else {
					bean.setDownloadStatus(Params.DOWNLOADSTATUS_DOWNLOADING);
					mAppDao.insertApp(bean);
					int fileSize = requestDataSize(bean.getUrl());
					long freeSpace = Tools.getSDCardFreeSpace();
					if (freeSpace <= fileSize) {
						sendMessage(bean.getId(), Params.DOWN_LOAD_FAILED + "");
						sendOtherMessage(Params.DOWN_LOAD_FAILED + "", bean.getId());
						deleteDownloadingApp(bean);
					}
					file = Tools.createFile(bean.getApkName(), fileSize);
				}
				in = requestData(bean.getUrl(), bean.getStartpos());
				boolean isComplete = writer(in, bean, file);
				if (!isComplete)
					return;
				String packageName = Tools.getAppPackage(mContext, Params.DOWNLOAD_FILE_PATH + bean.getApkName());
				sendMessage(bean.getId(), Params.DOWN_LOAD_COMPLETE + "");
				sendOtherMessage(Params.DOWN_LOAD_COMPLETE + "", bean.getId());
				updateListStatusAndPakage(bean.getId(), Params.APP_STATUS_UNINSTALLED, Params.DOWNLOADSTATUS_COMPLETE,
						packageName);
				mAppDao.updateAppStatusAndPackage(Params.APP_STATUS_UNINSTALLED, Params.DOWNLOADSTATUS_COMPLETE, packageName,
						bean.getId());// 更新数据库
				removeHandler(bean.getId());// 下载完成后移除监听
				installedToast(bean);
			} catch (Exception e) {
				sendMessage(bean.getId(), Params.DOWN_LOAD_FAILED + "");
				sendOtherMessage(Params.DOWN_LOAD_FAILED + "", bean.getId());
				deleteDownloadingApp(bean);
			} finally {
				try {
					if (file != null)
						file.close();
					if (in != null)
						in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private int requestDataSize(String fileUrl) throws Exception {
		URL url = new URL(fileUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestProperty("Accept-Language", Params.ACCEPT_LANGUAGE);
		conn.setRequestProperty("Charset", Params.CHARSET);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Connetion", "Keep-Alive");
		// 文件大小
		int fileSize = conn.getContentLength();
		conn.disconnect();
		return fileSize;
	}

	private InputStream requestData(String fileUrl, int startPos) throws Exception {
		URL url = new URL(fileUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestProperty("Accept-Language", Params.ACCEPT_LANGUAGE);
		conn.setRequestProperty("Charset", Params.CHARSET);
		conn.setRequestProperty("Connetion", "Keep-Alive");
		conn.setRequestProperty("User-Agent", "NetFox");
		conn.setRequestProperty("Range", "bytes=" + startPos + "-");
		return conn.getInputStream();
	}

	/** 程序下载现在完成返回 true 终端返回 false
	 * 
	 * @param in 和服务器建立的输入流
	 * @param bean 应用实体对象
	 * @param file 本地开辟文件
	 * @return
	 * @throws IOException */
	private boolean writer(InputStream in, AppBean bean, RandomAccessFile file) throws IOException {
		byte[] buffer = new byte[64 * 1024];
		int len;
		int hasRead = bean.getStartpos();
		double doublerate;
		int rate;
		long totalLength = file.length();
		int oldRate = 0;
		file.seek(bean.getStartpos());
		while ((len = in.read(buffer)) != -1) {
			int index = Tools.getIndexByAppId(mDownLoadList, bean.getId());
			if (index == -1) {
				removeHandler(bean.getId());
				return false;
			}
			file.write(buffer, 0, len);
			hasRead += len;
			doublerate = ((hasRead * 1.0) / ((totalLength + bean.getStartpos())) * 100);
			rate = (int) doublerate;
			updateDownloadRate(bean.getId(), rate, oldRate, hasRead);// 实时更新下载进度到数据库
			oldRate = (int) doublerate;
			sendMessage(bean.getId(), rate + "");
			sendOtherMessage(rate + "", bean.getId());
		}
		return true;
	}

	private void sendMessage(int appId, String message) {
		Handler handler = mThreadhandlers.get(appId);
		if (handler != null) {
			Message msg = new Message();
			msg.what = 0x444;
			Bundle b = new Bundle();
			b.putCharSequence("rate", message);
			msg.setData(b);
			handler.sendMessage(msg);
		}
	}

	private void sendOtherMessage(String message, int appId) {
		if (handler != null) {
			Message msg = new Message();
			msg.what = 0x7777 + appId;
			Bundle b = new Bundle();
			b.putCharSequence("rate", message);
			msg.setData(b);
			handler.sendMessage(msg);
		}
	}

	/** 更新列表的状态和包
	 * 
	 * @param appId
	 * @param status
	 * @param downloadstatus */
	public void updateListStatusAndPakage(int appId, int status, int downloadstatus, String pack) {
		int index = Tools.getIndexByAppId(mDownLoadList, appId);
		if (index != -1) {
			AppBean bean = mDownLoadList.get(index);
			bean.setStatus(status);
			bean.setDownloadStatus(downloadstatus);
			bean.setPackageName(pack);
		}

	}

	public void continueDownload(List<AppBean> list) {
		if (list != null) {
			for (Iterator<AppBean> iterator = list.iterator(); iterator.hasNext();) {
				AppBean bean = iterator.next();
				if (Params.DOWNLOADSTATUS_DOWNLOADING == bean.getDownloadStatus()) {
					if (bean != null) {
						DownloadThread thread = new DownloadThread(bean);
						downloadPool.execute(thread);
					}
				}
			}
		}
	}

	public void addHandler(int appId, Handler handler) {
		mThreadhandlers.put(appId, handler);
	}

	public void removeHandler(int appId) {
		mThreadhandlers.remove(appId);
	}

	public void download(AppBean bean) throws Exception {
		Params.app_dcount++;
		DataAcqusition.acquisitionDownloadData(bean.getId());
		if (Tools.getIndexByAppId(mDownLoadList, bean.getId()) != -1) {
			return;
		}
		mDownLoadList.add(bean);
		DownloadThread thread = new DownloadThread(bean);
		downloadPool.execute(thread);
	}

	public void UpdateApp(AppBean bean) throws Exception {
		if (Tools.getIndexByAppId(mDownLoadList, bean.getId()) != -1)
			return;
		int index = Tools.getIndexByAppId(mUpgradeList, bean.getId());
		mUpgradeList.remove(index);
		bean.setStartpos(0);
		mDownLoadList.add(bean);
		DownloadThread thread = new DownloadThread(bean);
		downloadPool.execute(thread);
	}

	public void setDownloadToastHandler(DownloadToastHandler downloadToastHandler) {
		this.mDownloadToastHandler = downloadToastHandler;
	}

	/** 判断是否安装过了该程序。
	 * 
	 * @param packageName
	 * @return true 安装 false 没有安装 */
	public boolean isAppInstalled(String packageName) {
		for (int i = 0; i < mInstalledList.size(); i++) {
			if (packageName.equals(mInstalledList.get(i).getPackageName())) {
				return true;
			}
		}
		return false;
	}

	public void uninstallApp(String packageName) {
		mAppDao.deleteApp(packageName);
		if (mInstalledList != null) {
			for (int i = 0; i < mInstalledList.size(); i++) {
				AppBean bean = mInstalledList.get(i);
				if (packageName.equals(bean.getPackageName())) {
					mInstalledList.remove(i);
				}
			}
		}
	}

	public void installedSuccess(String packageName) {
		mAppDao.updateAppStatus(Params.INSTALL_SUCCEED, Params.DOWNLOADSTATUS_DOWNLOADING, packageName);
		if (mDownLoadList != null) {
			for (int i = 0; i < mDownLoadList.size(); i++) {
				AppBean bean = mDownLoadList.get(i);
				if (packageName.equals(bean.getPackageName())) {
					mDownLoadList.remove(i);
					mInstalledList.add(0, bean);
				}
			}
		}
	}

	private void installedToast(AppBean bean) {
		if (mDownloadToastHandler != null) {
			Message msg = new Message();
			msg.what = Params.DOWNLOADSTATUS_COMPLETE;
			Bundle bun = new Bundle();
			bun.putCharSequence("downloadmsg", bean.getTitle() + "下载完成,确定要安装吗？");
			bun.putCharSequence("fileName", bean.getApkName());
			bun.putInt("appId", bean.getId());
			msg.setData(bun);
			mDownloadToastHandler.sendMessage(msg);
		}
	}
}
