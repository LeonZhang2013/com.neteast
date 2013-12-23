package com.neteast.data_acquisition;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;

import com.lib.log.MyLog;

/** 数据收集类
 * 
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2012-10-16 */
public class DataAcqusition {
	
	private final static String DAS_IP = "http://mngapps.wasu.com.cn:8120";
	private static String USER_IP;
	private static String USER_MAC;
	private static String USER_ID;
	private final static String  charsetName = "UTF-8"; 
	private static ExecutorService executorService;
	
	public final static int LOGIN = 1;
	public final static int LOGOUT = 2;
	
	
	public static void init(Context mContext,int userId){
		executorService = Executors.newCachedThreadPool();
		USER_IP = Utils.getIpAddress(mContext);
		USER_MAC = Utils.getMacAddress(mContext);
		USER_ID = String.valueOf(userId);
		
	}
	
	public static HashMap<String,String> getMapParam(){
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("user_id", checkdata(USER_ID));
		map.put("user_ip", checkdata(USER_IP));
		map.put("user_mac",checkdata(USER_MAC));
		return map;
	}
	
	private static String checkdata(String data){
		if(data==null||data.trim().length()==0){
			return "-1";
		}else{
			return data;
		}
	}
	
	
	/** 
	 * 用户下载行为统计接口： 
	 * http://218.108.168.46:8120/Operation/appdownstat
	 * 上报参数 ：content_id，user_id，user_ip，user_mac
	 * 
	 * @param contentId 应用id 
	 * @throws Exception 
	 * */
	public static void acquisitionDownloadData(final int contentId) {
			executorService.execute(new Runnable(){
				@Override
				public void run() {
					HashMap<String,String> map = getMapParam();
					map.put("content_id", String.valueOf(contentId));
					String path = DAS_IP + "/Operation/appdownstat";
					sendGETRequest(path,map,charsetName);
				}
			});
	}
	
	/**
	 * 用户打开详情页面行为统计接口：
	 * http://218.108.168.46:8120/Operation/appdetailstat
	 * 上报参数 ：content_id，user_id，user_ip，user_mac
	 * @return
	 */
	public static void acquisitionUserOptionData(final String contentId) {
		executorService.execute(new Runnable(){
			@Override
			public void run() {
			HashMap<String,String> map = getMapParam();
			map.put("content_id", contentId);
			String path = DAS_IP + "/Operation/appdetailstat";
			sendGETRequest(path,map,charsetName);
			}
		});
	}

	/** 	用户登陆行为统计接口：
	 * http://218.108.168.46:8120/Operation/appuserstat
	 * 上报参数 ：user_login，user_id，user_ip，user_mac
	 * user_login 取值 1，登陆行为；2，用户退出
	 * 
	 * @param isLogin 进入应用返回 1， 退出返回 2 
	 * @throws Exception 
	 * */
	public static void acquisitionAppStatusData(final int isLogin) {
		executorService.execute(new Runnable(){
			@Override
			public void run() {
			HashMap<String,String> map = getMapParam();
			map.put("user_login", String.valueOf(isLogin));
			String path = DAS_IP + "/Operation/appuserstat";
			sendGETRequest(path,map,charsetName);
			}
		});
	}

	/** 	
	 * 用户搜索行为统计接口：
	 * http://218.108.168.46:8120/Operation/appsearchstat
	 * 上报参数 ：searchkey，user_id，user_ip，user_mac 
	 * 
	 * @param searchkey 用户搜索关键字
	 * @return 返回 true代表发送成功，false 代表失败
	 * @throws Exception 
	 */
	public static void acquisitionUserSearchKeyData(final String searchkey) {
		executorService.execute(new Runnable(){
			@Override
			public void run() {
			HashMap<String,String> map = getMapParam();
			map.put("searchkey", String.valueOf(searchkey));
			String path = DAS_IP + "/Operation/appsearchstat";
			sendGETRequest(path,map,charsetName);
			}
		});
	}
	
    /**
     * 发送GET请求
     * @param path 请求路径
     * @param params 请求参数
	 * @param charsetName 字符集
	 * @return  返回 true代表发送成功，false 代表失败
	 * @throws Exception
	 */
    private static boolean sendGETRequest(String path, Map<String, String> params, String charsetName) {
    	try{
          StringBuilder sb = new StringBuilder(path);
          sb.append("?");
          for(Map.Entry<String, String> entry : params.entrySet()){
                sb.append(entry.getKey()).append("=")
                      .append(URLEncoder.encode(entry.getValue(), charsetName))
                      .append("&");
          }
          sb.deleteCharAt(sb.length() - 1);
          String url = sb.toString();
          MyLog.writeLog("url ==  "+url);
          HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
          conn.setConnectTimeout(5000);
          conn.setRequestMethod("GET");
          int ResponseCode = conn.getResponseCode();
          MyLog.writeLog("数据收集返回值 === "+ResponseCode);
          if(ResponseCode == 200){
                return true;
          }
          return false;
    	}catch (Exception e) {
    		return false;
		}
    }
}
