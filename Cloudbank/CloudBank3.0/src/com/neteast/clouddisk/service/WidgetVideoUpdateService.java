package com.neteast.clouddisk.service;

import com.neteast.clouddisk.receiver.CloudVideoWidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetVideoUpdateService extends Service{

	private static final String TAG = "WidgetVideoUpdateService";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "start service view update");

		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		RemoteViews updateView = CloudVideoWidget.updateImage(this);

		if (updateView != null) {
			manager.updateAppWidget(new ComponentName(this, CloudVideoWidget.class), updateView);
		}
		long now = System.currentTimeMillis();
		long updateMilis = 5000;
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
		// 设置一个闹钟, 强制设备唤醒这个更新
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, now + updateMilis, pendingIntent);
		
		// MyThread thread = new MyThread(this, manager);
		// thread.start();
		stopSelf();
	}


	class MyThread extends Thread {
		private Context context;
		private RemoteViews views;
		private AppWidgetManager widgetManager;

		public MyThread(Context context, AppWidgetManager widgetManager) {
			this.context = context;
			this.widgetManager = widgetManager;

		}

		@Override
		public void run() {
			while (true) {
				views = CloudVideoWidget.updateImage(context);
				widgetManager.updateAppWidget(new ComponentName(context, CloudVideoWidget.class), views);
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
}