package com.hs.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

public class ApkManager {
    
    private static final String TAG = "ApkManager";
	Activity activity;
	ApkListener listener;
	UpgradeInfo info;

	/** 需要更新 */
	protected static final int NEED_UPGRADE = 0;
	/** 不需要更新 */
	protected static final int DO_NOT_NEED_UPGRADE = 1;
	/** 更新出错 */
	protected static final int UPGRADE_ERROR = 2;
	/**应用的key*/
    private static final String KEY = "2cfce59a625fd9e6";
    /**应用的appcode*/
    private static final String APPCODE = "10025";
    /**应用的appid*/
    private static final int APPID = 25;
    /**用户中心的配置地址*/
    private static final String UserCenter = "http://mng.wasu.com.cn:8360";
    

	/**
	 * 一个APK管理类，目前功能只有apk在线升级功能。 请在主线程中调用本类，并确保有网络和sd卡读写权限
	 * 
	 * @param activity
	 * @param listener
	 */
	public ApkManager(Activity activity, ApkListener listener) {
		this.activity = activity;
		this.listener = listener;
	}

	/**
	 * 开始更新
	 * 
	 * @param path
	 *            更新配置文件的URL
	 */
	public void startUpdate(String path) {
		getUploadInfo(path);
	}

	/**
	 * 获取更新信息
	 * 
	 * @param path
	 */
	private void getUploadInfo(final String path) {
		new Thread(new Runnable() {
			public void run() {
				try {
					URL url = new URL(path);
					XmlPullParser parser = Xml.newPullParser();
					parser.setInput(url.openStream(), "UTF-8");
					int event = parser.getEventType();
					while (event != XmlPullParser.END_DOCUMENT) {
						switch (event) {
						case XmlPullParser.START_DOCUMENT:
							info = new UpgradeInfo();
							break;
						case XmlPullParser.START_TAG:
							String tag = parser.getName();
							if ("code".equals(tag)) {
								info.code = Integer.parseInt(parser.nextText());
							} else if ("description".equals(tag)) {
								info.description = parser.nextText();
							} else if ("versioncode".equals(tag)) {
								info.versioncode = Integer.parseInt(parser
										.nextText());
							} else if ("url".equals(tag)) {
								info.url = parser.nextText();
							} else if ("must".equals(tag)) {
								int b = Integer.parseInt(parser.nextText());
								info.must = b == 1;
							} else if ("size".equals(tag)) {
								info.size = Integer.parseInt(parser.nextText());
							}
							break;
						}
						event = parser.next();
					}
					if (shouldUpload(info.versioncode)) {
						handler.sendEmptyMessage(NEED_UPGRADE);
					} else {
						handler.sendEmptyMessage(DO_NOT_NEED_UPGRADE);
					}
				} catch (Exception e) {
					Log.i("test", "get Upgrade info error", e);
					handler.sendEmptyMessage(UPGRADE_ERROR);
				}
			}
		}).start();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NEED_UPGRADE:
				showAppUploadWindow();
				break;
			case DO_NOT_NEED_UPGRADE:
				listener.onApkUpdateFinished();
				break;
			case UPGRADE_ERROR:
				// 如果本次更新是强制更新的，那么更新出错就结束应用
				if (info != null && info.must) {
					activity.finish();
				} else {// 如果不是强制更新的，那么更新出错则暂不进行更新，进入应用
					listener.onApkUpdateFinished();
				}
				break;
			}
		}
	};

	/**
	 * 判断当前软件版本是否需要更新
	 * 
	 * @param remoteVersioncode
	 *            服务器端的软件版本号
	 * @return
	 * @throws NameNotFoundException
	 */
	private boolean shouldUpload(int remoteVersioncode)
			throws NameNotFoundException {
		PackageManager pm = activity.getPackageManager();
		int localVersionCode = pm.getPackageInfo(activity.getPackageName(),
				PackageManager.GET_ACTIVITIES).versionCode;
		return remoteVersioncode > localVersionCode;
	}

	/**
	 * 显示提示用户更新的对话框
	 */
	private void showAppUploadWindow() {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("版本升级");
		builder.setCancelable(false);
		builder.setMessage(info.description);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						startDownLoad();
						dialog.dismiss();
					}
				});
		if (!info.must) {
			builder.setNegativeButton(android.R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							listener.onApkUpdateFinished();
							dialog.dismiss();
						}
					});
		}
		builder.create().show();
	}

	/**
	 * 开始下载apk
	 */
	private void startDownLoad() {
		new UpdateApkTask().execute(info.url, info.size);
	}

	/**
	 * 更新信息的实体类
	 * 
	 * @author tiangh 2012-5-26 下午2:23:04
	 */
	static class UpgradeInfo {
		public int code;
		public String description;
		public int versioncode;
		public String url;
		public boolean must;
		public int size;
	}

	/**
	 * Apk管理器的回调接口
	 * 
	 * @author tiangh 2012-5-26 下午2:23:38
	 */
	public static interface ApkListener {
		/**
		 * 更新已经完成或者不需要更新
		 */
		void onApkUpdateFinished();
	}
	
	private void installApk(File file) {
	    new Thread(new Runnable() {
            public void run() {
                reportUpdateInfo(activity, 1);
            }
        }).start();
		Toast.makeText(activity, "下载完成，准备安装", Toast.LENGTH_LONG).show();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(intent);
		activity.finish();
	}
	
	/**
	 * 下载apk文件的方法类，包含一个进度条对话框，异步的方式来通知
	 * 
	 * @author tiangh 2012-5-26 下午2:24:13
	 */
	public class UpdateApkTask extends AsyncTask<Object, Integer, File> {

		private int UNIT = 1;
		private ProgressDialog dialog;
		@Override
		protected File doInBackground(Object... params) {
			try {
				String path = (String) params[0];
				int length = (Integer) params[1];
				UNIT = length / 100;
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				InputStream in = conn.getInputStream();
				File file = new File(Environment.getExternalStorageDirectory(),
						url.getFile().substring(url.getFile().lastIndexOf("/")));
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				int completed = 0;
				while ((len = in.read(buffer)) > 0&&getStatus()==AsyncTask.Status.RUNNING) {
					completed += len;
					fos.write(buffer, 0, len);
					onProgressUpdate(completed / UNIT);
				}
				in.close();
				fos.close();
				return file;
			} catch (IOException e) {
				Log.e("test", e.getMessage(), e);
				cancel(true);
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			createDialog();
		}

		@Override
		protected void onPostExecute(File result) {
			super.onPostExecute(result);
			if (getStatus()==AsyncTask.Status.RUNNING&&result != null) {
				installApk(result);
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			int newProgress = values[0];
			dialog.setProgress(newProgress);
			if (100 == newProgress) {
				dialog.dismiss();
			}
		}
		@Override
		protected void onCancelled() {
		    super.onCancelled();
		    new Thread(new Runnable() {
                public void run() {
                    reportUpdateInfo(activity, 2);
                }
            }).start();
		    Toast.makeText(activity, "网络连接中段，更新未完成", Toast.LENGTH_LONG).show();
		    dialog.dismiss();
		}
		/**
		 * 创建下载进度条对话框
		 */
		private void createDialog() {
			dialog = new ProgressDialog(activity);
			PackageManager pm = activity.getPackageManager();
			Drawable icon = activity.getApplicationInfo().loadIcon(pm);
			CharSequence label = activity.getApplicationInfo().loadLabel(pm);
			dialog.setTitle("正在更新" + label);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setIcon(icon);
			dialog.setMax(100);
			dialog.setCancelable(false);
			dialog.setButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					cancel(true);
				}
			});
			dialog.show();
		}
	}
	
	private static HashMap<Integer, Integer> ReasonCodeDic=new HashMap<Integer, Integer>();
    private static HashMap<Integer, String> ReasonDescDic=new HashMap<Integer, String>();
    
    static{
        ReasonCodeDic.put(1, 101);
        ReasonCodeDic.put(2, 201);
        ReasonCodeDic.put(3, 301);
        
        ReasonDescDic.put(1, "安装成功");
        ReasonDescDic.put(2, "网络断开");
        ReasonDescDic.put(3, "签名不一致");
    }
    
	public static void reportUpdateInfo(Context context,int UserCode) {
        TelephonyManager tm=(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        StringBuilder data=new StringBuilder();
        data.append("UserId="+getUserId(context)+"&")
                .append("EquipId=1&")
                .append("DeviceId="+deviceId+"&")
                .append("UserIp=&")
                .append("UserMac=&")
                .append("UserCode="+UserCode+"&")
                .append("ReasonCode="+ReasonCodeDic.get(UserCode)+"&")
                .append("ReasonDesc="+ReasonDescDic.get(UserCode));
        
        String reqstr = base64(data.toString());
        String verify=md5(data.toString()+KEY);
        
        String path=UserCenter+"/Update/report";
        List<NameValuePair> parameters=new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("reqstr", reqstr));
        parameters.add(new BasicNameValuePair("verify", verify));
        parameters.add(new BasicNameValuePair("appcode", APPCODE));
        parameters.add(new BasicNameValuePair("datatype", "j"));
        try {
            UrlEncodedFormEntity reqEntity=new UrlEncodedFormEntity(parameters, "UTF-8");
            String result = doPost(path, reqEntity);
            Log.i(TAG, result);
        } catch (UnsupportedEncodingException e) {} 
           catch (IOException e) {
               Log.e(TAG, e.getMessage(),e);
        }
    }
	
	private static String doPost(String path,HttpEntity reqEntity) throws IOException{
        AndroidHttpClient client=AndroidHttpClient.newInstance("Contact-Android");
        try {
            HttpPost httpPost=new HttpPost(path);
            httpPost.setEntity(reqEntity);
            HttpResponse response = client.execute(httpPost);
            HttpEntity resEntity = response.getEntity();
            String result = EntityUtils.toString(resEntity, "UTF-8");
            return result;
        } finally{
            client.getConnectionManager().shutdown();
        }
    }
    
    private static int getUserId(Context context) {
        Uri uri = Uri.parse("content://com.neteast.androidclient.newscenter/info");
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        int userid=0;
        if (cursor!=null&&cursor.moveToFirst()) {
            userid  = cursor.getInt(cursor.getColumnIndex("userid"));
        }
        return userid;
    }
    
    private static String base64(String input) {
        String result = input;
        try {
            result = Base64.encodeToString(input.getBytes("UTF-8"),
                    Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    private static String md5(String message) {
        String result = message;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(message.getBytes());
            BigInteger hash = new BigInteger(1, md.digest());
            result = hash.toString(16);
            if ((result.length() % 2) != 0) {
                result = "0" + result;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }
}
