package com.neteast.cloudtv2.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class NetUtils {

	public static String CorrectPath(String path){
		if(path.endsWith("/")){
			return path;
		}else{
			return path +"/";
		}
	}
	
	
	/**
	 * 数据拉取方法
	 * @param path 网络接口地址
	 * @param data 要发送的数据
	 * @return 输入流 InputStream
	 * @throws Exception
	 */
	public static InputStream requestData(String path,
			final Map<String,String> data)throws Exception {
		DefaultHttpClient client=new DefaultHttpClient();
		HttpPost post=new HttpPost(path);
		if(data!=null){
			ArrayList<NameValuePair> valuePairs=new ArrayList<NameValuePair>();
			for(Entry<String, String> entry:data.entrySet()){
				Log.i("show", entry.getKey()+" == "+entry.getValue());
				BasicNameValuePair valuePair=new BasicNameValuePair(entry.getKey(),entry.getValue());
				valuePairs.add(valuePair);
			}
			UrlEncodedFormEntity entity=new UrlEncodedFormEntity(valuePairs, "UTF-8");
			post.setEntity(entity);
		}
		System.out.println(path);
		HttpResponse response = client.execute(post);
		HttpEntity httpEntity = response.getEntity();
		if (httpEntity!=null) {
			return httpEntity.getContent();
		}
		return null;
	}
	
	public static InputStream requestData(String path) throws MalformedURLException, IOException {
		//return requestData(path,null);
		return getData(path);
	}
	
	public static InputStream getData(String path) throws MalformedURLException, IOException{
		MyLog.writeLog("epg 请求的路径 = "+path);
		HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
		conn.setRequestMethod("POST");
		conn.setConnectTimeout(3000);
		if(conn.getResponseCode() == 200){
			return conn.getInputStream();
		}
		return null;
	}
	
	
	public static String parseXMLToString(InputStream inputStream,
			String encoding) {
		ByteArrayOutputStream swapStream = null;
		try {
			swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[512];
			int rc = 0;
			while ((rc = inputStream.read(buff, 0, 512)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			byte[] data = swapStream.toByteArray();
			return new String(data, encoding);
		} catch (Exception e) {
			MyLog.writeLog("parseXMLToString ==>>" + e.toString());
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
	
	public static InputStream queryGET(String path,Map<String,String> data){
		try{
			if(data!=null){
				StringBuilder sb = new StringBuilder();
				for(Entry<String, String> entry:data.entrySet()){
					sb.append("/").append(entry.getKey()).append("/").append(entry.getValue());
				}
				path = path+sb.toString();
			}
			URL url = new URL(path);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setConnectTimeout(5000);
			int code = con.getResponseCode();
			Log.i("show", "code = "+code);
			if(code == 200){
				return con.getInputStream();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
