package com.neteast.data_acquisition;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.util.Log;

import com.lib.cloud.LibCloud;
import com.lib.net.Utility;

/** 数据收集类
 * 
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2012-10-30 */

public class DataAcqusition {

	private static LibCloud libCloud;
//	 private final static String SERVICE_PATH = "http://218.108.168.45:8360/Cloud";
	private final static String SERVICE_PATH = "http://mngapps.wasu.com.cn:8360/Cloud";
	private static String USER_ID;
	private static String TOKEN;
	private static String APP_CODE;
	private static String APP_KEY;
	private final static String charsetName = "UTF-8";
	private static ExecutorService executorService;

	public final static int LOGIN = 1;
	public final static int LOGOUT = 2;

	public static void init(Context mContext, String userid, String token) {
		executorService = Executors.newCachedThreadPool();
		libCloud = LibCloud.getInstance(mContext);
		USER_ID = userid;
		TOKEN = token;
		APP_CODE = libCloud.GetAppcode();
		APP_KEY = libCloud.GetAppKey();
	}

	private static String base64Encode(byte[] toencode) {
		return com.lib.net.Base64.encodeToString(toencode, android.util.Base64.NO_WRAP);
	}

	
	/** 
	 * 组拼URL数据
	 * @param path
	 * @param param
	 * @return 
	 */
//	private static String getUrl(String path, Map<String, String> param) {
//		String url = null;
//		try {
//			StringBuffer data = new StringBuffer("userid=").append(USER_ID).append("&token").append(TOKEN);
//			for (Entry<String, String> entry : param.entrySet()) {
//				data.append("&").append(entry.getKey()).append("=").append(entry.getValue());
//			}
//			String verify = Utility.MD5Encode(data.toString().getBytes("UTF-8"));
//			String reqstr = base64Encode((data.append(APP_KEY)).toString().getBytes());
//			url = new StringBuffer(SERVICE_PATH).append(path).append("?appcode=").append(APP_CODE).append("&verify=")
//					.append(verify).append("&datatype=j").append("&reqstr=").append(reqstr).toString();
//			Log.i("show", "url = " + url);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return url;
//	}
	

	/** 
	 * 组拼Map数据
	 * @param path
	 * @param param
	 * @return */
	private static Map<String, String> pinMap(Map<String, String> param) {
		HashMap<String, String> map = null;
		try {
			StringBuffer data = new StringBuffer("userid=").append(USER_ID).append("&token=").append(TOKEN);
			for (Entry<String, String> entry : param.entrySet()) {
				data.append("&").append(entry.getKey()).append("=").append(entry.getValue());
			}
			String reqstr = base64Encode(data.toString().getBytes());
			String verify = Utility.MD5Encode((data.append(APP_KEY)).toString().getBytes("UTF-8")).toLowerCase();
			map = new HashMap<String, String>();
			map.put("appcode", APP_CODE);
			map.put("verify", verify);
			map.put("datatype", "j");
			map.put("reqstr", reqstr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/** @function 云盘用户行为统计数据接口
	 * @comment 本接口用于对云盘高清、在线播放用户点播行为做统计。
	 * @url http://218.108.168.45:8360/Cloud/onlineorder
	 * @param movieId 电影资料id
	 * @param videoId 视频标识
	 * @param isOnline 1,在线; 2,高清 */
	public static void setOnlineorderRecord(final String movieId, final String videoId, final int isOnline) {
		Log.i("Other","setOnlineorderRecord................................."+isOnline);
		MyLog.writeLog("电影ID: "+movieId+"   数据收集  播放  是否是高清 = "+(isOnline==2));
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("movieid", movieId);
				map.put("videoid", videoId);
				map.put("isonline", String.valueOf(isOnline));
				try {
					sendPOSTRequest(SERVICE_PATH + "/onlineorder", pinMap(map));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/** @function 云盘应用数据下载统计接口
	 * @comment 功能：apk下载统计。
	 * @url http://218.108.168.45:8360/Cloud/apkdown
	 * @param apk_id 应用id */
	public static void downLoadRecord(final String appId) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("apk_id", appId);
				try {
					sendPOSTRequest(SERVICE_PATH + "/apkdown", pinMap(map));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/** @function 云盘用户登录使用统计接口
	 * @comment 本接口用于对用户打开云盘、关闭云盘行为做统计。
	 * @url http://218.108.168.45:8360/Cloud/loginstat
	 * @param type 操作类型，1登录，2关闭 */
	public static void useCloudBank(final int type) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("type", String.valueOf(type));
				try {
					sendPOSTRequest(SERVICE_PATH + "/loginstat", pinMap(map));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/** @function 云盘用户打开推荐内容详情页面统计接口
	 * @comment 功能：本接口用于对用户打开推荐内容详情页面行为做统计。
	 * @url 地址：http://218.108.168.45:8360/Cloud/detailstat
	 * @param type 资源类型，1视频 2音乐 3图片 4应用 5资讯
	 * @param resourceid 推荐资源id */
	public static void clickDetail(final String type, final String resourceId) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("type", type);
				map.put("resourceid", resourceId);
				try {
					sendPOSTRequest(SERVICE_PATH + "/detailstat", pinMap(map));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/** 发送GET请求
	 * 
	 * @param path 请求路径
	 * @param params 请求参数
	 * @param charsetName 字符集
	 * @return 返回 true代表发送成功，false 代表失败
	 * @throws Exception */
//	private static boolean sendGETRequest(String url) {
//		try {
//			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
//			conn.setConnectTimeout(5000);
//			conn.setRequestMethod("GET");
//			int ResponseCode = conn.getResponseCode();
//			Log.i("show", "数据收集返回值 === " + ResponseCode);
//			if (ResponseCode == 200) {
//				return true;
//			}
//			return false;
//		} catch (Exception e) {
//			return false;
//		}
//	}

	/** 
	 * 发送POST请求
	 * @param path 请求路径
	 * @param params 请求参数
	 * @throws Exception */
	private static boolean sendPOSTRequest(String path, Map<String, String> params) throws Exception {
		StringBuilder sb = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				sb.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		String data = sb.toString();
		Log.i("Other", data);
		byte[] entity = data.getBytes();
		HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);// 允许对外输出数据
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(entity.length));
		OutputStream outStream = conn.getOutputStream();
		outStream.write(entity);
		int responseCode = conn.getResponseCode();
		
		if (responseCode == 200) {
			InputStream in = conn.getInputStream();
			ByteArrayOutputStream  baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			while(in.read(buffer) != -1){
				baos.write(buffer);
			}
			return true;
		}
		return false;
	}

}
