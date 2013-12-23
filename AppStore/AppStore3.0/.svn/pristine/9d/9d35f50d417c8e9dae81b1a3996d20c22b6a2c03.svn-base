package com.neteast.data_acquisition;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/** @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2012-10-16 */
public class Utils {

	/** 获取手机MAC地址。
	 * 
	 * @param mContext
	 * @return MAC地址 */
	public static String getMacAddress(Context mContext) {
		WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wi = wm.getConnectionInfo();
		return wi.getMacAddress();
	}

	/** 获取用户IP地址。
	 * 
	 * @param mContext
	 * @return IP地址 */
	public static String getIpAddress(Context mContext) {
		WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wm.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		if (ipAddress == 0)
			return "0";
		return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "." + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));

	}

}
