package com.hs.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hs.activity.R;

public class ToastHandler {
	/**
	 * @param data
	 */
	public static void toastDisplay(String data,Context context) {
		// TODO Auto-generated method stub
		Toast toast = new Toast(context.getApplicationContext());
		LinearLayout linear = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.toastlayout, null);
		TextView tv = (TextView) linear.getChildAt(0);
		tv.setText(data);
		toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
		toast.setView(linear);
		toast.show();
	}
	
	public static Toast getToast(String data,Context context) {
		// TODO Auto-generated method stub
		Toast toast = new Toast(context.getApplicationContext());
		LinearLayout linear = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.toastlayout, null);
		TextView tv = (TextView) linear.getChildAt(0);
		tv.setText(data);
		toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
		toast.setView(linear);
		return toast;
	}
}
