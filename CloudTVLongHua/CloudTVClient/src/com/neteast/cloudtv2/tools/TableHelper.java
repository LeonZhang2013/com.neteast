package com.neteast.cloudtv2.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-5-10
 */
public class TableHelper {

	private LinearLayout mline;
	private Context mContext;
	
	public TableHelper(Context context, LinearLayout mTableTitle) {
		mContext = context;
		mline = mTableTitle;
	}
	
	/**
	 * 谁知表格线的颜色
	 * @param color
	 */
	public void setTableLineColor(int color){
		mline.setBackgroundColor(color);
	}
	
	public void addUnits(int[] titleW, String[] titles) {
		for(int i=0; i<titles.length; i++){
			TextView t = new TextView(mContext);
			t.setTextSize(20);
			t.setBackgroundColor(Color.WHITE);
			t.setText(titles[i]);
			t.setTextColor(Color.BLACK);
			t.setPadding(10, 0, 0, 0);
			t.setGravity(Gravity.CENTER_VERTICAL);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(titleW[i], LayoutParams.MATCH_PARENT);
			if(i!=titles.length-1)params.setMargins(0, 0, 1, 0);
			mline.addView(t,params);
		}
	}
	
}
