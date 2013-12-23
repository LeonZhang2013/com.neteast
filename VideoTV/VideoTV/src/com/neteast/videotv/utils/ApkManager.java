
package com.neteast.videotv.utils;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.neteast.videotv.TVApplication;
import com.neteast.videotv.fragment.ConfirmDialog;
import com.neteast.videotv.fragment.TVDialog;
import com.neteast.videotv.fragment.UpdateNoticeDialog;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Base64;
import android.util.Xml;
import android.view.View;
import android.widget.Toast;



public class ApkManager {
    
    public static interface ApkUpdateListener {
        void onComplete(String message);
    }

    private static final String URL_UPDATE_URL = TVApplication.UPDATE_URL;
    Activity mActivity;
    ApkUpdateListener mCallback;
    private ExecutorService mThreadPools;
    private UpdateStrategy mUpdateStrategy;
    private UIHandler mUIHandler;
    private ProgressDialog mApkDownloadDialog;
    private boolean mApkManagerRunning;
    
    /**
     *  请在主线程中调用本类，并确保有网络和sd卡读写权限
     * 
     * @param activity
     * @param listener
     */
    public ApkManager(Activity activity, ApkUpdateListener listener) {
        this.mActivity = activity;
        this.mCallback = listener;
        mThreadPools = Executors.newCachedThreadPool();
        mUIHandler = new UIHandler(this);
        checkData();
    }

    private void checkData() {
        if (mActivity.checkCallingOrSelfPermission(Manifest.permission.INTERNET)!=PackageManager.PERMISSION_GRANTED) {
            throw new RuntimeException("必须有 \"android.permission.INTERNET \" 权限");
        }
        if (mActivity.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
            throw new RuntimeException("必须有 \"android.permission.WRITE_EXTERNAL_STORAGE \" 权限");
        }
    }

    public void startUpdate() {
        mThreadPools.execute(new Runnable() {
            @Override
            public void run() {
                mUpdateStrategy = parseUpdateStrategy(URL_UPDATE_URL);
                if (mUpdateStrategy == null) {
                    mUIHandler.obtainMessage(UIHandler.ON_COMPLETE, "获取升级信息失败").sendToTarget();
                    return;
                }
                if (!mUpdateStrategy.shouldUpload(mActivity)) {
                    mUIHandler.obtainMessage(UIHandler.ON_COMPLETE, "已经是最新版本").sendToTarget();
                    return;
                }
                mUIHandler.sendEmptyMessage(UIHandler.NOTIFY_USER_UPDATE);
            }
        });
    }
    
    /**
     * 解析服务器端的下载策略文件
     * @param updateStrategyPath
     * @return UpdateStrategy
     */
    private UpdateStrategy parseUpdateStrategy(String updateStrategyPath) {
        try {
            URL url = new URL(updateStrategyPath);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(url.openStream(), "UTF-8");
            UpdateStrategy strategy = UpdateStrategy.getUpdateStrategyFromXml(parser);
            return strategy;
        } catch (Exception e) {
            return null;
        }
    }

    private void startDownloadApk() {
        mApkManagerRunning=true;
        showApkDownloadDialog();
        mThreadPools.execute(new Runnable() {
            @Override
            public void run() {
                InputStream in = null;
                FileOutputStream fos = null;
                try {
                    final int UNIT=(int) (mUpdateStrategy.getApkFileSize()/100);
                    URL url = new URL(mUpdateStrategy.getApkDownloadUrl());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    in = conn.getInputStream();
                    File file = new File(Environment.getExternalStorageDirectory(), url.getFile().substring(url.getFile().lastIndexOf("/")));
                    fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    int completed = 0;
                    while (mApkManagerRunning && (len = in.read(buffer)) > 0) {
                        completed += len;
                        fos.write(buffer, 0, len);
                        int newProgress=completed/UNIT;
                        if (newProgress!=mApkDownloadDialog.getProgress()) {
                            mUIHandler.obtainMessage(UIHandler.ON_PROGRESS_UPDATE,newProgress).sendToTarget();
                        }
                    }
                    if (file.length()==mUpdateStrategy.getApkFileSize()) {
                        mUIHandler.obtainMessage(UIHandler.ON_DOWNLOAD_SUCCESS, file).sendToTarget();
                    }else {
                        mUIHandler.sendEmptyMessage(UIHandler.ON_DOWNLOAD_ERROR);
                    }
                } catch (IOException e) {
                    mUIHandler.sendEmptyMessage(UIHandler.ON_DOWNLOAD_ERROR);
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        });
    }

    /**
     * 显示APK下载进度的对话框
     * 
     * @return
     */
    private void showApkDownloadDialog() {
        if (mApkDownloadDialog == null) {
            mApkDownloadDialog = new ProgressDialog(mActivity);
            PackageManager pm = mActivity.getPackageManager();
            Drawable icon = mActivity.getApplicationInfo().loadIcon(pm);
            CharSequence label = mActivity.getApplicationInfo().loadLabel(pm);
            //mApkDownloadDialog.setTitle("正在更新" + label);
            mApkDownloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
           // mApkDownloadDialog.setIcon(icon);
            mApkDownloadDialog.setMax(100);
            mApkDownloadDialog.setCancelable(false);
            mApkDownloadDialog.setButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onDownloadCancel();
                    dialog.dismiss();
                }
            });
        }
        mApkDownloadDialog.show();
    }
    
    /**
     * 当下载失败或者下载被取消时
     */
    private void onDownloadCancel() {
        mApkManagerRunning = false;
        mCallback.onComplete("网络错误，升级未完成");
        if (mUpdateStrategy.isForceUpdate()) {
            mActivity.finish();
        }
    }
    
    /**
     * 下载APK完成
     * @param file
     */
    private void onDownLoadSuccess(File file) {
        installApk(file);
    }
    /**
     * 下载进度发生变化
     * @param progress
     */
    private void onProgressUpdate(int progress) {
        mApkDownloadDialog.setProgress(progress);
        if (progress==100) {
            mApkDownloadDialog.dismiss();
        }
    }
    
    UpdateNoticeDialog mConfirmDialog;
    /**
     * 提示用户进行更新
     */
    private void notifyUserUpdate() {
    	mConfirmDialog = new UpdateNoticeDialog();
    	//mConfirmDialog.setTitile("版本升级                          当前版本号 "+mUpdateStrategy.getCurrentVersionName());
    	mConfirmDialog.setMessage(Html.fromHtml(mUpdateStrategy.getDescription()));
        mConfirmDialog.setOKListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	startDownloadApk();
                    mConfirmDialog.dismiss();
                }
            });
    	mConfirmDialog.show(mActivity.getFragmentManager(), "update");
    	if (!mUpdateStrategy.isForceUpdate()) {
    		
    	}
/*        Builder builder = new AlertDialog.Builder(mActivity).setTitle("版本升级").setCancelable(false)
                .setMessage(Html.fromHtml(mUpdateStrategy.getDescription()))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startDownloadApk();
                        dialog.dismiss();
                    }
                });
        // 若是强制升级，那么没有取消按钮
        if (!mUpdateStrategy.isForceUpdate()) {
            builder.setNegativeButton(android.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }
        builder.create().show();*/
    }
    /**
     * 安装APK
     * @param file
     */
    private void installApk(File file) {
        Toast.makeText(mActivity, "下载完成，准备安装", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(intent);
        mActivity.finish();
    }

    private static class UIHandler extends Handler {
        /**当升级流程完成后回调*/
        public static final int ON_COMPLETE = 1;
        /**提示用户进行升级*/
        public static final int NOTIFY_USER_UPDATE = 2;
        /**当下载APK进度发生改变*/
        public static final int ON_PROGRESS_UPDATE = 3;
        /**下载APK成功*/
        public static final int ON_DOWNLOAD_SUCCESS = 4;
        /**下载APK失败*/
        public static final int ON_DOWNLOAD_ERROR = 5;

        ApkManager manager;

        public UIHandler(ApkManager manager) {
            this.manager = manager;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ON_COMPLETE:
                    manager.mCallback.onComplete((String) msg.obj);
                    break;
                case NOTIFY_USER_UPDATE:
                    manager.notifyUserUpdate();
                    break;
                case ON_PROGRESS_UPDATE:
                    manager.onProgressUpdate((Integer) msg.obj);
                    break;
                case ON_DOWNLOAD_SUCCESS:
                    manager.onDownLoadSuccess((File) msg.obj);
                    break;
                case ON_DOWNLOAD_ERROR:
                    manager.onDownloadCancel();
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 下载策略文件类
     * @author emellend
     */
    private final static class UpdateStrategy {

        /** 普通升级模式 */
        @SuppressWarnings("unused")
        static final int NORMAL_UPDATE = 0;

        /** 强制升级模式 */
        static final int FORCE_UPDATE = 1;

        /** 强制跳转到指定的版本号 */
        static final int FORCE_TO_VERSION = 2;

        private UpdateStrategy() {
        }

        @SuppressWarnings("unused")
        private String name;
        private String versionName;
        private int versionCode;
        private String apkDownloadUrl;
        private int updateMode;
        private String description;
        private long apkFileSize;

        /**
         * 从XML文件获取升级策略信息
         * 
         * @param activity
         * @param parser
         * @return
         * @throws IOException
         * @throws XmlPullParserException
         */
        public static UpdateStrategy getUpdateStrategyFromXml(XmlPullParser parser)
                throws XmlPullParserException, IOException {
            UpdateStrategy strategy = null;
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        strategy = new UpdateStrategy();
                        break;
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();
                        if ("name".equals(tag)) {
                            strategy.name = parser.nextText();
                        } else if ("versionName".equals(tag)) {
                            strategy.versionName = parser.nextText();
                        } else if ("versionCode".equals(tag)) {
                            strategy.versionCode = Integer.parseInt(parser.nextText());
                        } else if ("apkDownloadUrl".equals(tag)){
                        	strategy.apkDownloadUrl = parser.nextText();
                        } else if ("must".equals(tag)) {
                            strategy.updateMode = Integer.parseInt(parser.nextText());
                        } else if ("size".equals(tag)) {
                            strategy.apkFileSize = Long.parseLong(parser.nextText());
                        }else if ("description".equals(tag)) {
                            strategy.description = parser.nextText();
                        }
                        break;
                }
                event = parser.next();
            }
            return strategy;
        }

        private String localVersionName;
        /**
         * 应用是否需要升级
         * 
         * @param context
         * @return
         */
        public boolean shouldUpload(Context context) {
            PackageManager pm = context.getPackageManager();
            int localVersionCode = 0;
            try {
                localVersionCode = pm.getPackageInfo(context.getPackageName(),
                        PackageManager.GET_ACTIVITIES).versionCode;
                localVersionName = pm.getPackageInfo(context.getPackageName(),
                        PackageManager.GET_ACTIVITIES).versionName;
            } catch (NameNotFoundException e) {
            }
           
            if (updateMode == FORCE_TO_VERSION) {
                return versionCode != localVersionCode;
            } else {
                return versionCode > localVersionCode;
            }
        }

        /**
         * 是否为强制更新
         * @return
         */
        public boolean isForceUpdate() {
            return updateMode == FORCE_TO_VERSION || updateMode == FORCE_UPDATE;
        }

        public String getDescription() {
           return description;
        }
        
        public String getCurrentVersionName(){
        	return localVersionName;
        }
        
        public String getVersionName(){
        	 return versionName;
        }

        public String getApkDownloadUrl() {
            return apkDownloadUrl;
        }

        public long getApkFileSize() {
            return apkFileSize;
        }
    }
}
