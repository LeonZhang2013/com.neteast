package com.go3c.utill;

public class NetCheck {
   
	 
	 static {
	        System.loadLibrary("netcheck-jni");
	 }
     
	 public void enable_sighup(boolean enable)
	 {
		   System.out.println("Enable the signal HUP:" + enable);
		   if(enable) enable_signal_hug(1);
		   else enable_signal_hug(0);
	 }
	 
	   
	 public native int netcheck_start();
	 public native int netcheck_stop();
	 public native int enable_signal_hug(int enable);
	 
}
