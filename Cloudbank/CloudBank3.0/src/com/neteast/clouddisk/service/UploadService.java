/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.neteast.clouddisk.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.lib.cloud.LibCloud;
import com.lib.db.AppDao;
import com.lib.db.DataInfo;
import com.lib.net.WeiboException;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.activity.NoSpaceAlertActivity;
import com.neteast.clouddisk.activity.UploadAlertActivity;

/**
 * Performs the background uploads requested by applications that use the
 * Downloads provider.
 */
public class UploadService extends Service {

	/* ------------ Constants ------------ */

	/* ------------ Members ------------ */

	private String TAG = "UploadService";
	private AppDao UploadDao = AppDao.getInstance(this);
	private LibCloud libCloud = LibCloud.getInstance(this);

	/** Observer to get notified when the content observer's data changes */
	private DownloadManagerContentObserver mObserver;

	/** Class to handle Notification Manager updates */
	// private DownloadNotification mNotifier;

	/**
	 * The Service's view of the list of downloads. This is kept independently
	 * from the content provider, and the Service only initiates downloads based
	 * on this data, so that it can deal with situation where the data in the
	 * content provider changes or disappears.
	 */
	// private ArrayList<DownloadInfo> mDownloads;

	/**
	 * The thread that updates the internal download list from the content
	 * provider.
	 */
	private UpdateThread mUpdateThread;

	/**
	 * Whether the internal download list should be updated from the content
	 * provider.
	 */
	private boolean mPendingUpdate;

	/**
	 * The ServiceConnection object that tells us when we're connected to and
	 * disconnected from the Media Scanner
	 */

	/**
	 * Array used when extracting strings from content provider
	 */
	private CharArrayBuffer oldChars;

	/**
	 * Array used when extracting strings from content provider
	 */
	private CharArrayBuffer mNewChars;

	private UsbReceiver usbreceiver = null;
	private BroadcastReceiver mReceiver;

	private BroadcastReceiver mNetReceiver;
	private boolean usbremoved = false;

	public class ConnectionChangeReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			// NetworkInfo lanNetInfo =
			// connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_);
			System.out.println("network Connection changed !!!!!!");
			showMessage("network Connection States changed!!!!!");
			if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
				System.out.println("network is disconnected !!!!!!");
				// Toast.makeText(this,getString(R.string.network_disconnected),Toast.LENGTH_SHORT).show())
			} else {

			}
		}
	}

	class UsbReceiver {
		
		UsbReceiver(Context context) {
			mReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {

					// intent.getAction()); 获取存储设备当前状态
					Log.i("UploadService", "BroadcastReceiver:" + intent.getAction());

					// intent.getData().getPath()); 获取存储设备路径
					Log.i("UploadService", "path:" + intent.getData().getPath());
					Log.i("UploadService", "Action :" + intent.getAction());

					List<DataInfo> list = UploadDao.getUploadList(1); // 获取需要上传列表
					if (list.size() > 0) {
						DataInfo di = list.get(0);
						UploadDao.updateUploadStatus(Integer.parseInt(di.getId()), Integer.parseInt(di.getFileid()), 99);
						/*
						 * String title =
						 * getResources().getString(R.string.upload_usbremoved_title); 
						 * String message = getResources().getString(R.string.upload_usbremoved_message); 
						 * String fileName = di.getUrl(); 
						 * System.out.println("fileName = " + di.getUrl());
						 * showAlertDialog(title,message,fileName);
						 */
					}
					usbremoved = true;

				}

			};

			IntentFilter filter = new IntentFilter();
//			filter.addAction(Intent.ACTION_MEDIA_SHARED);		//如果SDCard未安装,并通过USB大容量存储共享返回
//			filter.addAction(Intent.ACTION_MEDIA_MOUNTED);		//表明sd对象是存在并具有读/写权限
//			filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);	//SDCard已卸掉,如果SDCard是存在但没有被安装
//			filter.addAction(Intent.ACTION_MEDIA_CHECKING); 	//表明对象正在磁盘检查
//			filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
			filter.addAction(Intent.ACTION_MEDIA_EJECT); 		// 物理的拔出 SDCARD
//			filter.addAction(Intent.ACTION_MEDIA_REMOVED); 		//完全拔出
			filter.addDataScheme("file"); 						// 必须要有此行，否则无法收到广播
			context.registerReceiver(mReceiver, filter);
		}
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		mNetReceiver = new ConnectionChangeReceiver();
		this.registerReceiver(mNetReceiver, filter);
	}

	private void unRegisterReceiver() {
		if (mNetReceiver != null) {
			this.unregisterReceiver(mNetReceiver);
		}
	}

	/* ------------ Inner Classes ------------ */

	/**
	 * Receives notifications when the data in the content provider changes
	 */
	private class DownloadManagerContentObserver extends ContentObserver {

		public DownloadManagerContentObserver() {
			super(new Handler());
		}

		/**
		 * Receives notification when the data in the observed content provider
		 * changes.
		 */
		public void onChange(final boolean selfChange) {

			Log.v(TAG, "Upload Service ContentObserver received notification");

			updateFromProvider();
		}

	}

	/* ------------ Methods ------------ */

	/**
	 * Returns an IBinder instance when someone wants to connect to this
	 * service. Binding to this service is not allowed.
	 * 
	 * @throws UnsupportedOperationException
	 */
	public IBinder onBind(Intent i) {
		throw new UnsupportedOperationException("Cannot bind to Download Manager Service");
	}

	/**
	 * Initializes the service when it is first created
	 */
	public void onCreate() {
		super.onCreate();

		Log.v(TAG, "Upload Service onCreate");

		// trimDatabase();
		updateFromProvider();
		usbreceiver = new UsbReceiver(this);

		// registerReceiver();
	}

	/**
	 * Responds to a call to startService
	 */
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		Log.v(TAG, "Upload Service onStart");
		updateFromProvider();
	}

	/**
	 * Cleans up when the service is destroyed
	 */
	public void onDestroy() {

		Log.v(TAG, "Service onDestroy");
		super.onDestroy();
		if (mReceiver != null)
			unregisterReceiver(mReceiver);
		// unRegisterReceiver();
	}

	/**
	 * Parses data from the content provider into private array
	 */
	private void updateFromProvider() {
		synchronized (this) {
			mPendingUpdate = true;
			if (mUpdateThread == null) {
				Log.v(TAG, "Service start UpdateThread!!!!");
				mUpdateThread = new UpdateThread();
				mUpdateThread.start();
			}
		}
	}

	private class UpdateThread extends Thread {
		public UpdateThread() {
			super("Download Service");
		}

		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

			boolean keepService = false;
			// for each update from the database, remember which download is
			// supposed to get restarted soonest in the future
			long wakeUp = Long.MAX_VALUE;
			for (;;) {
				synchronized (UploadService.this) {
					if (mUpdateThread != this) {
						throw new IllegalStateException("multiple UpdateThreads in UploadService");
					}
				}
				if (usbremoved) {
					System.out.println("USB is removed , upload Service exit");
					mUpdateThread = null;
					stopSelf();
					return;
				}
				Log.v(TAG, " check network is Available");
				// boolean networkAvailable =
				// DataHelpter.isNetworkAvailable(UploadService.this);
				long now = System.currentTimeMillis();
				List<DataInfo> list = UploadDao.getUploadList(1);		 /* 获取需要上传列表 */
				Log.v(TAG, "uploading list.size = " + list.size());
				List<DataInfo> waitingList = UploadDao.getUploadList(0); /* 获取需要上传列表 */
				Log.v(TAG, "waiting uplaod list.size = " + waitingList.size());
				List<DataInfo> errorList = UploadDao.getUploadList(99);  /* 获取上传失败列表 */
				for (int i = 0; i < waitingList.size(); i++) {
					list.add(waitingList.get(i));
				}
				Log.v(TAG, "start upload  list.size = " + list.size());
				if (list == null || list.size() == 0) {
					// TODO: this doesn't look right, it'd leave the loop in an
					// inconsistent state
					if (!usbremoved) {
						if (errorList.size() > 0) {
							String title = getResources().getString(R.string.upload_failed_title);
							String message = getResources().getString(R.string.upload_failed_message);
							String fileName = errorList.get(0).getUrl();
							showAlertDialog(title, message, fileName);
						}
					}
					mUpdateThread = null;
					stopSelf();
					return;
				}

				int i = 0;
				// for(int i=0;i<list.size();i++){
				try {
					DataInfo info = list.get(0);
					String thubType = null;
					String fileid;
					String server = null;
					long limitedSize = (long) (2 * 1024 * 1024 * 1024 - 1);

					if (info.getFileSize() >= limitedSize) {
						System.out.println("file size = " + info.getFileSize() + "file name = " + info.getName() + "limited size =" + limitedSize);
						System.out.println("upload error ,file size is big than 2GB");
						UploadDao.updateUploadTask(Integer.parseInt(info.getId()), 0, "", AppDao.UPLOAD_SUSPEND);
						showMessage(getResources().getString(R.string.file_islimited));
						continue;
					}
					if (info.getType().equals("1")) {
						thubType = "VIDEO";
					} else if (info.getType().equals("3")) {
						thubType = "IMAGE";
					}
					fileid = info.getFileid();
					File tmp = new File(info.getUrl());
					if (tmp.exists()) {
						if (fileid != null && !fileid.equals("0")) {
							//1 表示正在上传  0表示等待上传
							if ((info.getStatus() == 1 || info.getStatus() == 0) && info.getProgress() < info.getFileSize()) {/* 续传 */
								fileid = info.getFileid();
								server = info.getServier();
								
								if (info.getType().equals("1") || info.getType().equals("3")) {
									System.out.println("continue upload fileid = " + info.getFileid() + "slice done = " + info.getSliceDone());
									libCloud.Upload_file_with_thumbnail(info.getId(), info.getFileid(),info.getServier(), info.getUrl(), thubType, info.getSliceDone());

								} else {
									System.out.println("continue upload fileid = "+ info.getFileid() + "slice done = " + info.getSliceDone());
									libCloud.Upload_file(info.getId(), info.getFileid(), info.getServier(), info.getUrl(), info.getSliceDone());
								}
								UploadDao.updateUploadTask(Integer.parseInt(info.getId()), Integer.parseInt(fileid), server, AppDao.UPLOAD_ING);

							} else {
								UploadDao.updateUploadTask(Integer.parseInt(info.getId()), Integer.parseInt(fileid), server, AppDao.UPLOAD_SUSPEND);
							}
						} else {
							Map m = libCloud.Get_upload_fileid( Integer.parseInt(info.getType()), "", info.getName(), Long.toString(info.getFileSize()));
							String code = (String) m.get("code");//如果等于0说明已经上传过了！
							fileid = (String) m.get("fileid");
							server = (String) m.get("server");
							String isnew = (String) m.get("isnew");
							// int isnew = (Integer) m.get("isnew");
							if (fileid == null || fileid.length() <= 0) {
								fileid = "0";
							}
							if (code.equals("1")) {
								if (isnew.equals("1")) {
									UploadDao.updateUploadTask(Integer.parseInt(info.getId()), Integer.parseInt(fileid), server, AppDao.UPLOAD_ING);
									if (info.getType().equals("1") || info.getType().equals("3")) {
										libCloud.Upload_file_with_thumbnail(info.getId(), fileid, server, info.getUrl(), thubType);
									} else {
										libCloud.Upload_file(info.getId(), fileid, server, info.getUrl());
									}
								} else {
									String message = String.format(getResources().getString( R.string.file_isuploaded), info.getName());
									showMessage(message);
									UploadDao.updateUploadTask(Integer.parseInt(info.getId()),Integer.parseInt(fileid), server, AppDao.UPLOAD_DONE);
									continue;
								}
							} else if (code.equals("401")) {
								System.out.println("您的空间不足");
								// showMessage(getResources().getString(R.string.space_full));
								UploadDao.updateUploadTask(Integer.parseInt(info.getId()), Integer.parseInt(fileid), server, AppDao.UPLOAD_ERROR);
								NoSpaceAlertDialog();
								mUpdateThread = null;
								stopSelf();
								return;
							} else {
								System.out.println("上传失败");
								String message = String.format(getResources().getString(R.string.upload_failed), info.getName());
								showMessage(message);

								UploadDao.updateUploadTask(Integer.parseInt(info.getId()), Integer.parseInt(fileid), server, AppDao.UPLOAD_ERROR);
								continue;
							}
						}
					} else {
						System.out.println("uploading file is not exit");
						UploadDao.updateUploadTask(Integer.parseInt(info.getId()), Integer.parseInt(fileid), server, AppDao.UPLOAD_ERROR);
					}
					List<DataInfo> uploadinglist = UploadDao.getUploadingTask(Integer.parseInt(info.getId()), Integer.parseInt(fileid));
					if (uploadinglist == null || uploadinglist.size() == 0) {
						mUpdateThread = null;
						stopSelf();
						return;
					}
					DataInfo uploaddata = uploadinglist.get(0);
					Log.v(TAG, "uploadinglist size =" + uploadinglist.size());
					Log.v(TAG, "Waiting for upload task finish,uploaded =" + uploaddata.getProgress() + "file size =" + info.getFileSize() + "status =" + uploaddata.getStatus());
					// while(uploaddata.getProgress() < info.getFileSize() || uploaddata.getStatus()==1 ){
					while (uploaddata.getStatus() == 1) {
						try {
							uploadinglist = UploadDao.getUploadingTask(Integer.parseInt(info.getId()), Integer.parseInt(fileid));
							uploaddata = uploadinglist.get(0);
							// Log.v(TAG, "Waiting for upload task finish id = "
							// + uploaddata.getId()+"uploaded =" +
							// uploaddata.getProgress() + "status=" +
							// uploaddata.getStatus()+"slice done" +
							// uploaddata.getSliceDone() +
							// "slice total"+uploaddata.getSliceTotal());
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (uploaddata.getStatus() == AppDao.UPLOAD_ERROR) {
							// String message =
							// String.format(getResources().getString(R.string.upload_failed),info.getName());
							String message = String.format(getResources().getString(R.string.upload_error));
							showMessage(message);
						}
					}
				} catch (WeiboException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Drops old rows from the database to prevent it from growing too large
	 */
	private void trimDatabase() {
		/*
		 * Cursor cursor = getContentResolver().query(Downloads.Impl.CONTENT_URI, 
		 * new String[] { Downloads.Impl._ID }, Downloads.Impl.COLUMN_STATUS + " >= '200'",
		 * null, Downloads.Impl.COLUMN_LAST_MODIFICATION); if (cursor == null) {
		 * // This isn't good - if we can't do basic queries in our database,
		 * nothing's gonna work Log.e(Constants.TAG,
		 * "null cursor in trimDatabase"); return; } if (cursor.moveToFirst()) {
		 * int numDelete = cursor.getCount() - Constants.MAX_DOWNLOADS; int
		 * columnId = cursor.getColumnIndexOrThrow(Downloads.Impl._ID); while
		 * (numDelete > 0) { getContentResolver().delete(
		 * ContentUris.withAppendedId(Downloads.Impl.CONTENT_URI,
		 * cursor.getLong(columnId)), null, null); if (!cursor.moveToNext()) {
		 * break; } numDelete--; } } cursor.close();
		 */
	}

	/**
	 * Returns a String that holds the current value of the column, optimizing
	 * for the case where the value hasn't changed.
	 */
	private String stringFromCursor(String old, Cursor cursor, String column) {
		int index = cursor.getColumnIndexOrThrow(column);
		if (old == null) {
			return cursor.getString(index);
		}
		if (mNewChars == null) {
			mNewChars = new CharArrayBuffer(128);
		}
		cursor.copyStringToBuffer(index, mNewChars);
		int length = mNewChars.sizeCopied;
		if (length != old.length()) {
			return cursor.getString(index);
		}
		if (oldChars == null || oldChars.sizeCopied < length) {
			oldChars = new CharArrayBuffer(length);
		}
		char[] oldArray = oldChars.data;
		char[] newArray = mNewChars.data;
		old.getChars(0, length, oldArray, 0);
		for (int i = length - 1; i >= 0; --i) {
			if (oldArray[i] != newArray[i]) {
				return new String(newArray, 0, length);
			}
		}
		return old;
	}

	private void showMessage(final String message) {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			public void run() {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			}
		});
	}

	private void showAlertDialog(String title, String message, String fileName) {
		Context mContext = getApplicationContext();
		Intent activityIntent = new Intent(mContext, UploadAlertActivity.class);
		activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activityIntent.putExtra("title", title);
		activityIntent.putExtra("message", message);
		if (fileName != null && fileName.length() > 0) {
			activityIntent.putExtra("fileName", fileName);
		}
		/*
		 * Bundle mBundle = new Bundle(); mBundle.putInt("mCurKeyboard", 1);
		 * activityIntent.putExtras(mBundle);
		 */
		mContext.startActivity(activityIntent);
	}

	private void NoSpaceAlertDialog() {
		Context mContext = getApplicationContext();
		Intent activityIntent = new Intent(mContext, NoSpaceAlertActivity.class);
		activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		/*
		 * Bundle mBundle = new Bundle(); mBundle.putInt("mCurKeyboard", 1);
		 * activityIntent.putExtras(mBundle);
		 */
		mContext.startActivity(activityIntent);
	}
}
