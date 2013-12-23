package com.neteast.clouddisk.handler;


import java.util.Map;

import com.lib.db.DataInfo;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.utils.DownLoadApplication;
import android.app.ActivityGroup;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
/*
 * 安装完成处理按钮样式
 */
public class InstallFinishHandler extends Handler {
	private View v;
	private ImageView open;
	private Map<String,DataInfo> dd;
	private DownLoadApplication da;
	
	public InstallFinishHandler(ActivityGroup context ,View v,ImageView open) {
		this.v = v;
		this.open = open;
		da = (DownLoadApplication)context.getApplicationContext();
		//dd = da.getAppList();
	}
	@Override
	public void handleMessage(final Message msg) {
		if (msg.what == 0x1455) {
			dd = da.getAppList();
			((ImageView)v).setVisibility(View.GONE);
			System.out.println("data info Id = " + ((DataInfo) v.getTag()).getId());
			DataInfo d = dd.get(((DataInfo)v.getTag()).getId());
			System.out.println("InstallFinishHandler AppList size = " + dd.size());
			System.out.println("dd = " + dd);
			open.setTag(d.getPackages());
			open.setVisibility(View.VISIBLE);
			
		}
	}

}
