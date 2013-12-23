package com.neteast.clouddisk.param;

public class Params {
	public static final int distance = 50;//左右移动的距离
	public static final int RECOMMEND_PICTRUE_PER_PAGE_NUM = 21;
	public static final int RECOMMEND_DATA_PER_PAGE_NUM = 12;
	public static final int RECOMMEND_APP_PER_PAGE_NUM = 16;
	public static final int MYUPLOAD_DATA_PER_PAGE_NUM = 12;
	public static final int MYDOWNLOAD_DATA_PER_PAGE_NUM = 12;
	public static final int MYDOWNLOAD_PICTURE_PER_PAGE_NUM = 21;
	public static final int UPLOADFILE_PER_PAGE_NUM= 18;
	public static final int TRANSCODE_PER_PAGE_NUM = 4;
	
	public static final int MYDOWNLOAD_SERIES_PER_PAGE_NUM = 30;
	public static final int MYDOWNLOAD_VARIETY_PER_PAGE_NUM = 8;
	public static final int MYDOWNLOAD_SERIES0_PER_PAGE_NUM = 10;
	public static final int SEARCH_RESULT_PER_PAGE_NUM = 10;
	public static final int RECOMMEND_VIDEO = 1;
	public static final int RECOMMEND_MUSIC = 2;
	public static final int RECOMMEND_PICTURE = 3;
	public static final int RECOMMEND_APP = 4;
	public static final int RECOMMEND_NEWS = 5;
	public static final int MYDOWNLOAD_VIDEO = 6;
	public static final int MYDOWNLOAD_MUSIC = 7;
	public static final int MYDOWNLOAD_PICTURE = 8;
	public static final int UPLOAD_VIDEO = 1;
	public static final int UPLOAD_MUSIC = 2;
	public static final int UPLOAD_PICTURE = 3;
	public static final int UPLOAD_DOC = 4;
	public static final String DOWNLOAD_FILE_PATH = "/mnt/sdcard/download/";
	public static final String ACCEPT_LANGUAGE = "zh-CN";
	public static final String ChARSET = "UTF-8";
	public static final int DOWNLOAD_START = 1;
	public static final int DOWNLOAD_PAUSE = 0;
	public static final String UPGRADE_DOWNLOAD_FILE_PATH = "/mnt/sdcard/download/tmp/";
	public static final int DOWN_LOAD_COMPLETE = -10;
	public static final int DOWN_LOAD_FAILED = -100;
	public static final String DB_NAME="clouddisk_db.db3";
	public static final int DB_VERSION = 3;
	
	public static final int DOWNLOADING_TOAST_TIME=2000;
	public static final int PER_CHAPTER_COUNT =20;
	public static final int PER_VARITY_COUNT =8;
	public static final int SERIA_TITLE_COUNT =4;
	
	
	/*for upgrade*/
		
	//public static final String APP_VERSION_URL = "http://218.108.168.46:8888/upgrade/clouddisk/version.xml";
	//public static final String APP_APK_URL = "http://218.108.168.46:8888/upgrade/clouddisk/CloudDisk.apk";
	
	public static final String APP_UPGRADE_URL = "http://service.wasu.com.cn:9126/Update/xml/appcode/10009";
//	public static final String APP_UPGRADE_URL ="http://service.wasu.com.cn:9126/update/android/clouddisk/updatestrategy.xml";
	//public static final String APP_UPGRADE_URL ="http://218.108.85.62:9999/update/android/clouddisk/updatestrategy.xml";
	
	//public static final String APP_UPGRADE_URL ="http://192.168.2.106/update/android/clouddisk/updatestrategy.xml";
	public static final String APP_VERSION_URL ="http://service.wasu.com.cn:9126/update/android/clouddisk/version.xml";
	public static final String APP_APK_URL ="http://service.wasu.com.cn:9126/update/android/clouddisk/CloudDisk.apk";
	/*
	public static final String APP_VERSION_URL ="http://192.168.1.100/update/android/clouddisk/version.xml";
	public static final String APP_APK_URL ="http://192.168.1.100/update/android/clouddisk/CloudDisk.apk";
	*/
	
	//public static final String APP_VERSION_URL = "http://10.0.2.237/upgrade/clouddisk/version.xml";
	//public static final String APP_APK_URL = "http://10.0.2.237/upgrade/clouddisk/CloudDisk.apk";
	
	public static final String APP_VERSIONCODE = "versionCode";
	public static final String APP_VERSIOMNAME = "versionName";
	public static final String APP_VERSIONINFO = "versionInfo";
	
	
}
