package com.hs.activity;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TabHost;

import com.hs.db.AppDao;
import com.hs.domain.AppBean;
import com.hs.domain.User;
import com.hs.handler.DownloadProgressHandlerX;
import com.hs.handler.DownloadToastHandler;
import com.hs.handler.LoginHandler;
import com.hs.handler.UpgradeHandler;
import com.hs.handler.UpgradeOverHandler;
import com.hs.params.Params;
import com.hs.utils.Tools;
import com.lib.appstore.LibAppstore;
import com.lib.net.WeiboException;
import com.neteast.data_acquisition.DataAcqusition;

public class DownLoadApplication extends Application {
	
	private TabHost tbhost = null;
	Map<String, Handler> upgradHandlers = new HashMap<String, Handler>();
	LoginHandler loginhandler = null;
	ExecutorService pool = Executors.newFixedThreadPool(3);// 升级
	UpgradeOverHandler uoh;
	
	

	@Override
	public void onCreate() {
		super.onCreate();
		User.getUserInfo(this);
	}


	public void setTabHost(TabHost tb){
		tbhost = tb;
	}
	
	public int getCurrentTabIndex(){
		if(tbhost!=null){
			return tbhost.getCurrentTab();
		}
		return 0;
	}



	public void setLoginHandler(LoginHandler handler){
		this.loginhandler = handler;
	}
	public LoginHandler getLoginHandler(){
		return loginhandler;
	}
	
}
