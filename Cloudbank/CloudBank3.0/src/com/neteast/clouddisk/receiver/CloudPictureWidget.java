package com.neteast.clouddisk.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.neteast.clouddisk.R;
import com.neteast.clouddisk.activity.MainActivity;
import com.neteast.clouddisk.service.WidgetPictureUpdateService;

public class CloudPictureWidget extends AppWidgetProvider {
	
	private static int temp = 0;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for (int i = 0, size = appWidgetIds.length; i < size; i++) {
			Intent intent = new Intent(context, MainActivity.class);
			intent.setAction("com.neteast.clouddisk.activity.picture.widget");
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.picture_widget_layout);
			remoteViews.setOnClickPendingIntent(R.id.picture_widget_action, pendingIntent);
			appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
		}
		context.startService(new Intent(context, WidgetPictureUpdateService.class));
	}
	
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		context.stopService(new Intent(context, WidgetPictureUpdateService.class));
	}
	
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
//		context.startService(new Intent(context, WidgetPictureUpdateService.class));
	}
	
	public static RemoteViews updateImage(Context context){
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.picture_widget_layout);
		if (temp > 4) {
			temp = 0;
		}
		switch (temp) {
		case 0:
			views.setImageViewResource(R.id.picture_widget_action, R.drawable.picture_poster_1);
			break;
		case 1:
			views.setImageViewResource(R.id.picture_widget_action, R.drawable.picture_poster_2);
			break;
		case 2:
			views.setImageViewResource(R.id.picture_widget_action, R.drawable.picture_poster_3);
			break;
		case 3:
			views.setImageViewResource(R.id.picture_widget_action, R.drawable.picture_poster_4);
			break;
		case 4:
			views.setImageViewResource(R.id.picture_widget_action, R.drawable.picture_poster_5);
			break;
		default:
			break;
		}
		temp++;
		return views;
	}
	
}
