package com.neteast.clouddisk.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neteast.clouddisk.R;
import com.neteast.clouddisk.activity.MainActivity;

public class UIHelper {
	private static Toast toast = null;
	public static void titleStyleDependState(LinearLayout sourceSiteLayout,
			Context context, TextView v) {
		for (int i = 0; i < sourceSiteLayout.getChildCount(); i++) {
			TextView textView = (TextView) sourceSiteLayout.getChildAt(i);
			textView.setTextAppearance(context, R.style.tabtextstyle);
			textView.setBackgroundDrawable(null);
		}
		v.setTextAppearance(context, R.style.tabtextselectstyle);
		v.setBackgroundResource(R.drawable.titlefocus);
	}

	public static void displayToast(String text, Context context) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	public static void displayToast(String text, Context context,int times) {
		Toast.makeText(context, text, times).show();
	}
	
	public static void showInvalidDialog(Context context,String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.system_tips);
		builder.setMessage(message);
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
		});
		
		builder.show();
	}
	public static void showToast(Context context,String message){
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		View layout = inflater.inflate(R.layout.mytoast1,null);
		   
		  TextView toasttv = (TextView)layout.findViewById(R.id.toastmessage);
		  toasttv.setText(message);
		  if(toast==null){
			  toast = new Toast(context.getApplicationContext());
			  toast.setGravity(Gravity.CENTER, 0, 0);
			  toast.setDuration(Toast.LENGTH_LONG);
		  } 
		  toast.setView(layout);
		  toast.show();
	}
	public static void showToast2(Activity ac,String message){
		LayoutInflater inflater = ac.getLayoutInflater();
		View layout = inflater.inflate(R.layout.mytoast2,null);
		   
		  TextView toasttv = (TextView)layout.findViewById(R.id.toastmessage);
		  toasttv.setText(message);
		  if(toast==null){
			  toast = new Toast(ac.getApplicationContext());
			  toast.setGravity(Gravity.CENTER, 0, 0);
			  toast.setDuration(Toast.LENGTH_LONG);
		  }   
		   toast.setView(layout);
		   toast.show();
	}
}
