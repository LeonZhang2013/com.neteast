package com.neteast.clouddisk.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.neteast.clouddisk.receiver.CloudPictureWidget;

public class WidgetPictureUpdateService extends Service{
	private static final String TAG = "WidgetPictureUpdateService";
	
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
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		RemoteViews updateView = CloudPictureWidget.updateImage(this);
		if (updateView != null) {
			manager.updateAppWidget(new ComponentName(this, CloudPictureWidget.class), updateView);
		}
		long now = System.currentTimeMillis();
		long updateMilis = 5000;
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, now + updateMilis, pendingIntent);
		stopSelf();
	}
}