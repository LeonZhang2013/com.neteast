package com.neteast.androidclient.newscenter.receiver;

import com.neteast.androidclient.newscenter.service.ReceiveInfoService;
import com.neteast.androidclient.newscenter.service.WidgetService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, WidgetService.class));
        //30秒后启动receiveInfo Service，因为刚刚开机时，系统的网络模块没有准备好
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent receiveInfo=new Intent(context,ReceiveInfoService.class);
        PendingIntent pendIntent = PendingIntent.getService(context, 0, receiveInfo, PendingIntent.FLAG_UPDATE_CURRENT);  
        long triggerAtTime =  System.currentTimeMillis()+30*1000;
        alarmMgr.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pendIntent);
    }
}
