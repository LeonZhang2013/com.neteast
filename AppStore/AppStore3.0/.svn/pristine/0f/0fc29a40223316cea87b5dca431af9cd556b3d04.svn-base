package com.hs.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;

import com.hs.activity.AppStore;
import com.hs.domain.AppBean;
import com.hs.params.Params;

/** @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2012-10-29 */
public class Tools {

	/** 截取浮点数字
	 * 
	 * @param value
	 * @return */
	public static float subFloat(String value) {
		int indexof = 0;
		byte[] byt = value.getBytes();
		for (int i = 0; i < byt.length; i++) {
			int zi = byt[i];
			if (46 > zi || zi > 58 || zi == 47) {
				indexof = i;
				break;
			}
		}
		return Float.parseFloat(value.substring(0, indexof));
	}

	public static void deleteLocalFile(String fileName) {
		File file = new File(Params.DOWNLOAD_FILE_PATH + fileName);
		if (file.exists()) {
			file.delete();
		}
	}

	/** 返回文件大小 单位
	 * 
	 * @param mContext
	 * @param packageName
	 * @return
	 * @throws NameNotFoundException */
	public static String getFileSize(PackageInfo pi) {
		String dir = pi.applicationInfo.publicSourceDir;
		File file = new File(dir);
		return unitTranslate(file.length());
	}
	

	public static float unitTranslate(String fileSize) {
		float fsize = 0.0f;
		long fbyte = 0;
		fileSize = fileSize.toUpperCase();
		Pattern pattern = Pattern.compile("[A-Z]");
		Matcher matcher = pattern.matcher(fileSize);
		if (matcher.find()) {
			String sizeStr = fileSize.substring(0, matcher.start());
			String unitStr = fileSize.substring(matcher.start());
			if (unitStr.contains("G")) {
				fsize = Float.parseFloat(sizeStr);
				fbyte = (long) (fsize * 1024 * 1024 * 1024);
			} else if (unitStr.contains("M")) {
				fsize = Float.parseFloat(sizeStr);
				fbyte = (long) (fsize * 1024 * 1024);
			} else if (unitStr.contains("K")) {
				fsize = Float.parseFloat(sizeStr);
				fbyte = (long) (fsize * 1024);
			} else {
				fsize = Float.parseFloat(sizeStr);
				fbyte = (long) (fsize * 1024);
			}
		}
		return fbyte;
	}

	/** 尺寸转换器
	 * 
	 * @param fileSize 原始尺寸
	 * @param unit 转换单位
	 * @return */
	public static String unitTranslate(Long fileSize) {
		BigDecimal bd = new BigDecimal(fileSize);
		BigDecimal unitSize = null;
		String unit = null;
		if(fileSize>1024*1024*1024){
			unitSize = new BigDecimal(1024 * 1024 * 1024);
			unit = "GB";
		}else
		if(fileSize>1024*1024){
			unitSize = new BigDecimal(1024 * 1024);
			unit = "MB";
		}else
		if(fileSize>1024){
			unitSize = new BigDecimal(1024);
			unit = "KB";
		}
		return bd.divide(unitSize, 2, BigDecimal.ROUND_HALF_UP).floatValue()+unit;
	}
	
	public static long getSDCardFreeSpace(){
		File sdcard = new File("/sdcard/");
		return sdcard.getFreeSpace();
	}
	
	/**
	 * 判断是否还能容纳 该文件但空间
	 * @param size 被下载的文件大小
	 * @return
	 */
	public static boolean hasFreeSpace(String size){
		long freeSpace = Tools.getSDCardFreeSpace();
		float fsize = Tools.unitTranslate(size);
		if (freeSpace <= fsize) {
			return false;
		}
		return true;
	}
	
	
	/** 判断忽略时间是否过期。
	 * 
	 * @param ignore 忽略时间。
	 * @return 如果是 true 需要更新，否者为false暂时不需要更新； */
	public static boolean isUpdate(Long ignore) {
		Long ignoreSpaceTime = System.currentTimeMillis() - (Params.IGNORE_TIME * 1000);
		if (ignoreSpaceTime > ignore) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 保存忽略时间
	 * @param context 
	 * @param ignoreTime 时间以秒为单位
	 */
	public static void saveIgnoerTime(Context context, long ignoreTime) {
		SharedPreferences preferences = context.getSharedPreferences("ignoretime", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putLong("time", ignoreTime);
		editor.commit();
	}

	/** 
	 * 获取忽略时间
	 * @return */
	public static void initIgnoerTime(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("ignoretime", Context.MODE_PRIVATE);
		Params.IGNORE_TIME = preferences.getLong("time", Params.IGNORE_TIME);
	}
	
	public static int getIndexByAppId(List<AppBean> list,int id) {
		for (int i = 0; i < list.size(); i++) {
			AppBean bean = list.get(i);
			if (id == bean.getId()) {
				return i;
			}
		}
		return -1;
	}
	
	public static String getDeviceId(Context context){
		 TelephonyManager tm=(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	     String deviceId = tm.getDeviceId();
	     return deviceId;
	}
	
	/**
	 * 根据apk文件获取包名
	 * 
	 * @param path
	 * @return
	 */
	public static String getAppPackage(Context context,String path) {
		String packageName = null;
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo(path,
					PackageManager.GET_ACTIVITIES);
			ApplicationInfo appInfo = null;
			if (info != null) {
				appInfo = info.applicationInfo;
				packageName = appInfo.packageName;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return packageName;
	}

	
	public static String getVersion(Context context){
		PackageManager pm = context.getPackageManager();
		try {
			return pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "没有取到版本号！";
	}
	
	public static RandomAccessFile createFile(String fileName,int fileSize) throws Exception{
		File f = new File(Params.DOWNLOAD_FILE_PATH);
		if (!f.exists()) {
			f.mkdir();
		}
		String currentTempFilePath = Params.DOWNLOAD_FILE_PATH + fileName;
		RandomAccessFile  file = new RandomAccessFile(currentTempFilePath, "rw");
		file.setLength(fileSize);
		return file;
	}
	
	public static boolean checkFileExists(String path){
		File myFile = new File(path);
		if (myFile.exists()) {
			return true;
		}
		return false;
	}
}
