package com.neteast.cloudtv2;


import java.text.SimpleDateFormat;

import com.neteast.cloudtv2.bean.UserBean;

public class Constant {
	
	public static final String SOAP_NAMESPACE = "http://service.patient.health.os";
	public final static int TimeOut = 5000;
	public static SimpleDateFormat DATE_FORMAT_LONG = new SimpleDateFormat("yyyy-MM-dd'T00:00:00'");
	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final String USER_CENTER="http://mng.wasu.com.cn:8360"+"/Mobile/login";
	public static final String APPCODE = "10002";
	public static final String KEY = "10002";
	
	/** sp配置文件名 */	
	public static final String SP_FILE = "cloud2_pad";

	/** 医疗服务 **/
	public static final String[] HISES = {
		"http://10.0.2.238:8080/",
		"http://192.168.49.21:8080/",
		"http://www.xmaware.com:8086/",
		"http://119.4.50.62:8086/"};
	
	/** 点播 数据 **/
	public static String[] DEMAND_PATHS = {
		"http://10.10.1.2:8080/",
		"http://192.168.49.21:9090/",
		"http://192.168.49.21:8080/",
		"http://119.4.50.62:8080/"};
	
	/** EPG 数据 **/
	public static String[] PROGROM_PATHS = {
		"http://10.10.1.2/yunmao/wangxin/",
		"http://192.168.49.21/",
		"http://192.168.49.21/",
		"http://119.4.50.62/"};

	/** 播放地址 **/
	public static String[] PLAY_PATHS = {
		"http://10.10.1.2/seg/",
		"http://192.168.49.21/",
		"http://192.168.49.21/",
		"http://119.4.50.62/"};

	/** 图片地址 **/
	public static String[] PHOTO_PATHS = {
		"http://10.10.1.2/yunmao/wangxin/logo/big/",
		"http://192.168.49.21/logo/small/",
		"http://192.168.49.21/logo/small/",
		"http://119.4.50.62/logo/small/"};

	/** 点播 数据 **/
	public static String DEMAND_PATH = DEMAND_PATHS[2];
	/** 左侧频道数据 **/
	public static String PROGROM_PATH = PROGROM_PATHS[2];
	/** 播放地址 **/
	public static String PLAY_PATH = PLAY_PATHS[2];
	/** 图片地址 **/
	public static String PHOTO_PATH = PHOTO_PATHS[2];
	/** 医疗服务 **/
	public static String HIS = HISES[2];
	
	/** http://192.168.7.244/yunmao/wangxin/epg.xml 图片地址：http://192.168.7.244/big/small 播放地址：http://192.168.7.244/seg/ */
	
	/** 图片本地存储地址 **/
	public static final String IMAGE_STORE_PATH = "/neteast/Cloud2/image/";

	/** 播放文件后缀 **/
	public static final String FLAG = ".m3u8";

	/** 直播字段是否加 ch */
	public static boolean IS_CH = true;
	
	/**用户信息**/
	public static UserBean USER_BEAN = null;
	
	public static String EDUCATION = "xuanjiao.xml";
	public static String EPG = "epg.xml";
	public static String RECREATION = "dianbo.xml";
	public static String MEDICAL_SERVICE = "yiliaofuwu.xml";
	public static final String SOAP_SERVICE_END = "health/services/listServices";
	public static final String WSDL_END = "Cloud2/CloudData";
	public static String SOAP_SERVICE = HIS+SOAP_SERVICE_END;
	public static String SERVER_PATH = HIS+WSDL_END;

	
}
