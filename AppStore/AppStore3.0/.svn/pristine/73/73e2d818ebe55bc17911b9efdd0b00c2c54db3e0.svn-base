package com.hs.utils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetWorkHandler {
	/*
	public static boolean isNetworkAvailable(Context context) {
		boolean flag = false;
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			flag = false;
		} else {
			NetworkInfo[] infos = manager.getAllNetworkInfo();
			if (infos != null) {
				for (int i = 0; i < infos.length; i++) {
					if (infos[i].getState() == NetworkInfo.State.CONNECTED) {
						flag = true;
					}
				}
			}
		}
		return flag;
	}
	*/
    public static boolean isNetworkAvailable(Context context,String address) {
        
    	ConnectivityManager connectivity =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
        	NetworkInfo networkinfo = connectivity.getActiveNetworkInfo();
        	if (networkinfo == null || !networkinfo.isAvailable() ||!networkinfo.isConnectedOrConnecting()) {
                return false;
        	}else{
        		return true;
        	}
        }
        
    /*
    	boolean flag = false;
    	Process process=null;
    	try {
    		System.out.println("run cmd ping !!!!!");
    		process = new ProcessBuilder().command("/system/bin/ping", "218.108.168.45")
    	            	.redirectErrorStream(true).start();
    		InputStream in = process.getInputStream();
    		OutputStream out = process.getOutputStream();
    		BufferedReader br = new BufferedReader(new InputStreamReader(in));
    		String s = null;
    		System.out.println("run cmd ping wait data!!!!!");
    		while((s = br.readLine())!= null){
    			Log.d("AndroidProcess get date: ", s);
    			System.out.println("run cmd get date data!!!!!" + s);
    			flag =  true;
    			break;
    		}
    	}
    	catch(IOException e){
    		e.printStackTrace(System.out);
    		System.err.println("创建进程时出错");
    		System.exit(1);
    	}
    	finally {
    		process.destroy();
    	} 
    	System.out.println("run cmd ping finished !!!!!");
    	return flag;
*/
    }
    public static boolean openUrl(String address) {
    	  String myString="";
    	  try {
    	   System.out.println("address = " + address);
    	   //URL url = new URL("http://www.baidu.com/index.html");
    	   URL url = new URL(address);
    	   HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
    	   urlCon.setConnectTimeout(2000);
    	   urlCon.setReadTimeout(2000);  
    	   InputStream is = urlCon.getInputStream();
    	   BufferedInputStream bis = new BufferedInputStream(is);
    	   // 用ByteArrayBuffer缓存
    	   ByteArrayBuffer baf = new ByteArrayBuffer(50);
    	   int current = 0;
    	   
    	   while ((current = bis.read()) != -1) {
    	    baf.append((byte) current);
    	   }
    	   myString = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
    	   bis.close();
    	   is.close();
    	  } catch (Exception e) {
    	   e.printStackTrace();
    	   return false;
    	  }
    	  return true;
    	}
}
