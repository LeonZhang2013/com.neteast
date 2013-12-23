package com.example.testlive;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmReceiver extends BroadcastReceiver {

	
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle args = intent.getExtras();
        if (args!=null) {
            String action = args.getString("action");
            System.out.println("action ============= "+action);
            Intent ii = new Intent(context,MainActivity.class);
            ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(ii);
        }
    }

}