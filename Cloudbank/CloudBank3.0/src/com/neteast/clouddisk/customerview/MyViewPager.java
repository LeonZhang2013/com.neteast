package com.neteast.clouddisk.customerview;

 
 import android.content.Context;
 import android.support.v4.view.ViewPager;
 import android.util.AttributeSet;
 import android.util.Log;
 
 
 /**
  * ��д��������ж���������ķ���
  * @author zxy
  *
  */
 public class MyViewPager extends ViewPager {
     private boolean left = false;
     private boolean right = false;
     private boolean isScrolling = false;
     private int lastValue = -1;
     private int mOffscreenPageLimit = DEFAULT_OFFSCREEN_PAGES=0;
     private String TAG="MyViewPager";
 
     public MyViewPager(Context context, AttributeSet attrs) {
         super(context, attrs);
         init();
     }
 
     public MyViewPager(Context context) {
         super(context);
         init();
     }
 
     /**
      * init method .
 */
     private void init() {
         //setOnPageChangeListener(listener);
     }
     int DEFAULT_OFFSCREEN_PAGES=1;
     public void setOffscreenPageLimit(int limit) {   
    	 if (limit < DEFAULT_OFFSCREEN_PAGES) {   
    		 Log.w(TAG, "Requested offscreen page limit " + limit + " too small; defaulting to " +   
    			 DEFAULT_OFFSCREEN_PAGES);   
    	             limit = DEFAULT_OFFSCREEN_PAGES;   
    	 	}   
    	    if (limit != mOffscreenPageLimit) {   
    	    	mOffscreenPageLimit = limit;   
    	       // populate();   
    	     }   
     }  
 
 }