package com.hs.handler;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.hs.activity.DownLoadApplication;
import com.hs.utils.GuestEventListener;
import com.hs.utils.UIHelper;
import com.lib.appstore.LibAppstore;

public class UpgradeOverHandler extends Handler {
	  
	
	private LibAppstore lib;
	private ViewFlipper vf;
	private Context context;
	private DownLoadApplication app;
	private int pageNum ;
	private GuestEventListener gel;
	private GestureDetector clickDetecotor;

	public UpgradeOverHandler(LibAppstore lib, ViewFlipper vf,
			Context context ,
			DownLoadApplication app, int pageNum, final GuestEventListener gel,
			final GestureDetector clickDetecotor) {
		this.lib = lib;
		this.vf = vf;
		this.context = context;
		this.app = app;
		this.pageNum = pageNum;
		this.gel = gel;
		this.clickDetecotor = clickDetecotor;
	} 

	@Override
	public void handleMessage(Message msg) {
		if (msg.what == 0x8678) {
			List<Map<String,Object>> list = null;//app.getUpgradeList();
			UIHelper.addUpgrade2Layout(lib, vf,context,list, app, pageNum,
					gel, clickDetecotor);
/*			((TextView) ((LinearLayout) ((SixthActivity)context).getTablayout().getChildAt(2)).getChildAt(0))
			.setText("可更新(" + list.size() + ")");*/
		}
	}
}
