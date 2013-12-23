package com.hs.handler;

import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hs.activity.R;
import com.hs.params.Params;

public class DownloadProgressHandlerX extends Handler {
	private ProgressBar pb;
	private int x;
	private View mview;
	public DownloadProgressHandlerX(ProgressBar pb,int x,View view){
		this.pb = pb;
		this.x = x;
		this.mview = view;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(Message msg) {
		if(msg.what == 0x7777+x){
			Bundle b = msg.getData();
			String rate = (String) b.get("rate");
			int rateNum = Integer.parseInt(rate);
			RelativeLayout rl = (RelativeLayout) pb.getParent();
			TextView rateTextView = (TextView)rl.getChildAt(1);
			if(rateNum==Params.DOWN_LOAD_COMPLETE){
				rl.setVisibility(View.GONE);
				View v = ((LinearLayout)rl.getParent()).getChildAt(1);
				v.setVisibility(View.VISIBLE);
				v.setBackgroundResource(R.drawable.detail_installbtn1);
				Map<String,Object> m = (Map<String,Object>)v.getTag();
				m.put("runType", 2);
				v.setClickable(true);
				return;
			}
			if(rateNum == Params.DOWN_LOAD_FAILED){
				rl.setVisibility(View.GONE);
				mview.setVisibility(View.VISIBLE);
				return;
			}
			rateTextView.setText(rateNum+"%");
			pb.setProgress(Integer.parseInt(rate));
		}
	}
}
