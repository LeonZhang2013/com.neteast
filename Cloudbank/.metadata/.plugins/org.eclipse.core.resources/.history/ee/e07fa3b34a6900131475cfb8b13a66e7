package com.neteast.androidclient.newscenter.service;

import com.neteast.androidclient.newscenter.ConfigManager;
import com.neteast.androidclient.newscenter.receiver.AlarmReceiver;
import com.neteast.androidclient.newscenter.utils.LogUtil;
import com.neteast.androidclient.newscenter.view.NewsWidget;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

public class WidgetService extends Service {
    
    /**所有的桌面，包含自定义桌面的包名集合*/
    private ArrayList<String> mLauncherPackageName=new ArrayList<String>();
    private Context mContext;
	private ScreenBroadcastReceiver mScreenReceiver;
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        initLauncherPackageName();
        NewsWidget.getIntance(mContext).addOnScreen();
        startScreenBroadcastReceiver();
    }
    /**
     * 开始监听屏幕状态
     */
    private void startScreenBroadcastReceiver() {
    	IntentFilter filter = new IntentFilter();  
        filter.addAction(Intent.ACTION_SCREEN_ON);  
        filter.addAction(Intent.ACTION_SCREEN_OFF);  
        mScreenReceiver = new ScreenBroadcastReceiver();
        mContext.registerReceiver(mScreenReceiver, filter);  
	}

	/**
     * 设置一个定时器，2秒后发送广播，然后每个1秒重复发广播。</BR>
     * 广播的作用是调用AlarmReceiver，AlarmReceiver再调用本服务，<BR>
     * 由于本服务已经启动，所以会重复调用onStartCommand方法。<BR>
     * 即每隔一秒检查是否处于桌面，刷新NewsWidget的状态
     */
    private void setTimer() {
        AlarmManager alarmMgr=(AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(mContext,AlarmReceiver.class);
        intent.putExtra("action", ConfigManager.ACTION_WIDGET_SERVICE);
        PendingIntent pendIntent = PendingIntent.getBroadcast(mContext,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);  
        long triggerAtTime =  System.currentTimeMillis()+2 * 1000;  
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, 1 * 1000, pendIntent);
    }
    
    /**
     * 取消广播
     */
    private void cancelTimer() {
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);  
        Intent intent = new Intent(mContext, AlarmReceiver.class);  
        PendingIntent pendIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);  
        alarmMgr.cancel(pendIntent);
    }
 
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        refreshNewsWidgetVisibility();
        return super.onStartCommand(intent,flags,startId);
    }
    
    /**
     * 设置哪些例外的包名(即在这些应用里，小图标依然显示) 包括桌面和自定义桌面
     */
    private void initLauncherPackageName() {
        PackageManager pm = getPackageManager();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> activities = pm.queryIntentActivities(intent,PackageManager.GET_ACTIVITIES| PackageManager.GET_UNINSTALLED_PACKAGES);
        if (activities.size() == 0) {
        } else {
            for (ResolveInfo resolveInfo : activities) {
                mLauncherPackageName.add(resolveInfo.activityInfo.packageName);
            }
        }
    }
    
    /**
     * 当前的应用是否处于桌面
     * @return
     */
    private boolean currentAppIsLauncher() {
        ActivityManager activityManager= (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = activityManager.getRunningTasks(2).get(0).topActivity.getPackageName();
        return mLauncherPackageName.contains(packageName);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopScreenStateUpdate();
        NewsWidget.getIntance(mContext).deleteFromScreen();
    }
    /**
     * 停止监听屏幕状态
     */
    private void stopScreenStateUpdate(){  
        mContext.unregisterReceiver(mScreenReceiver);  
    }
    
    /**
     * 根据当前是否处于桌面，刷新消息图标的可见性
     */
    private void refreshNewsWidgetVisibility() {
        if (currentAppIsLauncher()) {
            NewsWidget.getIntance(mContext).show();
        }else {
            NewsWidget.getIntance(mContext).hidden();
        }
    }
    /**
     * 监听屏幕状态的类，当屏幕锁定时，不再监听是否处于桌面。
     * 当屏幕解锁时，开始监听
     * @author net-east
     *
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver{  
        private String action = null;  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            action = intent.getAction();  
            if(Intent.ACTION_SCREEN_ON.equals(action)){  
            	setTimer();
            }else if(Intent.ACTION_SCREEN_OFF.equals(action)){  
            	cancelTimer();
            }  
        }  
    }  
}
