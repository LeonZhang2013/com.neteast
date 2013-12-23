package com.neteast.clouddisk.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.neteast.clouddisk.R;
import com.neteast.clouddisk.activity.MainActivity;

public class CloudMusicWidget extends AppWidgetProvider {
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for (int i = 0, size = appWidgetIds.length; i < size; i++) {
			Intent intent = new Intent(context, MainActivity.class);
			intent.setAction("com.neteast.clouddisk.activity.music.widget");
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.music_widget_layout);
			remoteViews.setOnClickPendingIntent(R.id.music_widget_action, pendingIntent);
			appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
