package com.go3c.utill;

import android.app.Activity;
import android.os.Bundle;

public class SignalActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
          
        NetCheck nc = new NetCheck();
        nc.enable_sighup(false); 
        
    }
}