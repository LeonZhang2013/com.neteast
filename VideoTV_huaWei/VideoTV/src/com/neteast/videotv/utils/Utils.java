package com.neteast.videotv.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-9
 * Time: 下午4:07
 */
public class Utils {

    public static int dp2px(Context context,int dpInput){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpInput * scale + 0.5f);
    }

    public static int px2dp(Context context,int pxInput){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxInput / scale + 0.5f);
    }
    
    public static InputStream downloadUrl(String url) throws Exception {
		URL imageUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
		conn.setConnectTimeout(10000);
		conn.setReadTimeout(10000);
		conn.setInstanceFollowRedirects(true);

		InputStream is = conn.getInputStream();
		return is;
	}
    
	public static String parseXMLToString(InputStream inputStream,
			String encoding) {
		ByteArrayOutputStream swapStream = null;
		try {
			swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[1024];
			int rc = 0;
			while ((rc = inputStream.read(buff, 0, 512)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			byte[] data = swapStream.toByteArray();
			if(encoding==null){
				return new String(data);
			}else{
				return new String(data, encoding);
			}	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (swapStream != null)
					swapStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
