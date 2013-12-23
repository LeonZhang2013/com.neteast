package com.neteast.androidclient.newscenter.receiver;

import com.neteast.androidclient.newscenter.ConfigManager;
import com.neteast.androidclient.newscenter.service.ReceiveInfoService;
import com.neteast.androidclient.newscenter.service.WidgetService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
/**
 * 接受AlarmManager的广播，根据action调用对应的组件
 * @author emellend
 */
public class AlarmReceiver extends BroadcastReceiver {

    
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle args = intent.getExtras();
        if (args!=null) {
            String action = args.getString("action");
            if (ConfigManager.ACTION_WIDGET_SERVICE.equals(action)) {
                Intent service=new Intent(context, WidgetService.class);
                context.startService(service);
            }else if (ConfigManager.ACTION_RECEIVE_INFO_SERVICE.equals(action)) {
                Intent service=new Intent(context, ReceiveInfoService.class);
                context.startService(service);
            }
        }
    }

}
