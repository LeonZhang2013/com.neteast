package com.neteast.clouddisk.utils;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.lib.cloud.LibCloud;
import com.lib.db.AppDao;
import com.lib.db.DataInfo;
import com.lib.net.WeiboException;
import com.neteast.clouddisk.handler.DownloadToastHandler;
import com.neteast.clouddisk.handler.InstallFinishHandler;

import com.neteast.clouddisk.param.Params;

public class DownLoadApplication extends Application {
	private ExecutorService pool = Executors.newFixedThreadPool(3);
	private Map<String, DownloadToastHandler> downloadList = new HashMap<String, DownloadToastHandler>();
	private Map<String, View> downloadListState = new HashMap<String, View>();
	private Map<String, InstallFinishHandler> updateBtnList = new HashMap<String, InstallFinishHandler>();
	private AppDao dao = AppDao.getInstance(this);
	private Map<String, DataInfo> appList = null;//存取数据库全部记录，加载一次
	private List<PackageInfo> packInfoList =  null;//已安装程序列表
	//private ExecutorService executorServiceDownload;
	String version = null;
	public static FileUtil mFU;
	private ImageDownloader2 mImageDownloader = null;
	
	private Handler mhandler = null;
	
	LibCloud libCloud = null;
	
	public Handler getUploadRecorderHandler(){
		return mhandler;
	}
	public void setUploadRecorderHandler(Handler handler){
		mhandler= handler;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public ImageDownloader2 getImageDownloader(){
		return mImageDownloader;
	}

	@Override
	public void onCreate() {
		appList = dao.getAppList();
		mFU = FileUtil.getInstance(this);
		mImageDownloader = new ImageDownloader2();
		libCloud = LibCloud.getInstance(this);
		
		//executorServiceDownload=Executors.newFixedThreadPool(4);
		//List<PackageInfo> list =  this.getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
		//getInstalledAppInfo(list);
	}
	/*
	 * 获得手机已安装信息
	 */
	/*private void getInstalledAppInfo(List<PackageInfo> list){
		packInfoList = new ArrayList<PackageInfo>();
		for(PackageInfo pi:list){
			if((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0){
				packInfoList.add(pi);
			}
		}
		
	}*/

	/**
	 * 下载
	 * @param dataInfo
	 * @param runHandler
	 * @throws Exception
	 */
	public void download(DataInfo dataInfo) throws Exception {
		/*
		String downloadUrl = dataInfo.getUrl();
		String downloadName = dataInfo.getUrl().substring(
				downloadUrl.lastIndexOf("/"), downloadUrl.length());
		URL url = new URL(downloadUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestProperty("Accept-Language", Params.ACCEPT_LANGUAGE);
		conn.setRequestProperty("Charset", Params.ChARSET);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Connetion", "Keep-Alive");

		// 文件大小
		int fileSize = conn.getContentLength();
		conn.disconnect();
		File f = new File(Params.DOWNLOAD_FILE_PATH);
		if (!f.exists()) {
			f.mkdir();
		}

		RandomAccessFile file = new RandomAccessFile(Params.DOWNLOAD_FILE_PATH
				+ downloadName, "rw");
		file.setLength(fileSize);
		*/
		/*
		executorServiceDownload.submit(new DownloadThread(0, file, downloadName,
				dataInfo));
		*/
		
		DownloadThread thread = new DownloadThread(0,
				dataInfo);
		pool.execute(thread);
	}

	/*
	 * 下载线程负责下载
	 */
	private class DownloadThread extends Thread {
		private int startPos = 0;
		private RandomAccessFile file;
		InputStream in = null;
		private String downloadName;
		private DataInfo dataInfo;

		public DownloadThread(int startPos,DataInfo dataInfo) {
			this.startPos = startPos;
			//this.file = file;
			//this.downloadName = downloadName;
			this.dataInfo = dataInfo;
		}

		@Override
		public void run() {
			try {
				
				String downloadUrl = dataInfo.getUrl();
			    downloadName = dataInfo.getUrl().substring(
						downloadUrl.lastIndexOf("/"), downloadUrl.length());
				URL url1 = new URL(downloadUrl);
				HttpURLConnection conn1 = (HttpURLConnection) url1.openConnection();
				conn1.setConnectTimeout(5 * 1000);
				conn1.setRequestProperty("Accept-Language", Params.ACCEPT_LANGUAGE);
				conn1.setRequestProperty("Charset", Params.ChARSET);
				conn1.setRequestMethod("GET");
				conn1.setRequestProperty("Connetion", "Keep-Alive");

				// 文件大小
				int fileSize = conn1.getContentLength();
				conn1.disconnect();
				File f = new File(Params.DOWNLOAD_FILE_PATH);
				if (!f.exists()) {
					f.mkdir();
				}

				file = new RandomAccessFile(Params.DOWNLOAD_FILE_PATH
						+ downloadName, "rw");
				file.setLength(fileSize);
				
				
				
				URL url = new URL(dataInfo.getUrl());
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setConnectTimeout(5 * 1000);
				conn.setRequestProperty("Accept-Language",
						Params.ACCEPT_LANGUAGE);
				conn.setRequestProperty("Charset", Params.ChARSET);
				conn.setRequestProperty("Connetion", "Keep-Alive");
				conn.setRequestProperty("User-Agent", "NetFox");
				conn.setRequestProperty("Range", "bytes=" + startPos + "-");
				in = conn.getInputStream();
				byte[] buffer = new byte[20 * 1024];
				int len;
				file.seek(startPos);
				while ((len = in.read(buffer)) != -1) {
					file.write(buffer, 0, len);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (file != null) {
						file.close();
					}
					if (in != null) {
						in.close();
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			if(dataInfo.getType().equals("6")){
				sendDownMsg(dataInfo, downloadName);
			}else if(dataInfo.getType().equals("4")){
				sendFileMsg(dataInfo,downloadName);
			}

		}

	}

	/*
	 * 下载完毕发送消息弹出下载完成对话框
	 */
	private void sendDownMsg(DataInfo dataInfo, String downloadName) {
		DownloadToastHandler dth = (DownloadToastHandler) downloadList
				.get(dataInfo.getId());
		downloadList.remove(dataInfo.getId());
		System.out.println("sendDownMsg 111 appList size = " + appList.size() );
		/*
		if(appList!=null){
			appList.clear();		
		}
		appList = dao.getAppList();
		*/
		System.out.println("sendDownMsg 222 appList size = " + appList.size() );
		if (dth != null) {
			Message msg = new Message();
			msg.what = 0x1458;
			Bundle b = new Bundle();
			b.putString("path", Params.DOWNLOAD_FILE_PATH + downloadName);
			b.putSerializable("datainfo", dataInfo);
			msg.setData(b);
			dth.sendMessage(msg);
		}
	}
	/*
	 * 下载完毕发送消息弹出下载完成对话框
	 */
	private void sendFileMsg(DataInfo dataInfo, String downloadName) {
		DownloadToastHandler dth = (DownloadToastHandler) downloadList
				.get(dataInfo.getId());
		downloadList.remove(dataInfo.getId());
		if (dth != null) {
			Message msg = new Message();
			msg.what = 0x1459;
			Bundle b = new Bundle();
			b.putString("path", Params.DOWNLOAD_FILE_PATH + downloadName);
			b.putSerializable("datainfo", dataInfo);
			msg.setData(b);
			dth.sendMessage(msg);
		}
	}

	public Map<String, DownloadToastHandler> getDownloadList() {
		return downloadList;
	}

	public void setDownloadList(Map<String, DownloadToastHandler> downloadList) {
		this.downloadList = downloadList;
	}

	public void updateBtn(String id) {
		Message msg = new Message();
		msg.what = 0x1455;
		InstallFinishHandler ifh = updateBtnList.get(id);
		if(ifh!=null){
			updateBtnList.remove(id);
			ifh.sendMessage(msg);
		}
	}

	public Map<String, InstallFinishHandler> getUpdateBtnList() {
		return updateBtnList;
	}

	public void setUpdateBtnList(Map<String, InstallFinishHandler> updateBtnList) {
		this.updateBtnList = updateBtnList;
	}
	public List<PackageInfo> getPackInfoList() {
		return packInfoList;
	}

	public void setPackInfoList(List<PackageInfo> packInfoList) {
		this.packInfoList = packInfoList;
	}

	public Map<String, DataInfo> getAppList() {
		//return appList;
		return dao.getAppList();
	}

	public void setAppList(Map<String, DataInfo> appList) {
		this.appList = appList;
	}
	public Map<String, View> getDownloadListState() {
		return downloadListState;
	}

	public void setDownloadListState(Map<String, View> downloadListState) {
		this.downloadListState = downloadListState;
	}
	
	void playorder(String movieid,String videoid,String isonline){
		userorderAsync userorder = new userorderAsync();
		userorder.execute(movieid,videoid,isonline);
		
	}
	
	class userorderAsync extends AsyncTask<Object, Integer, Map<String, Object> >{
		@Override
		protected Map<String, Object>  doInBackground(Object... params) {
			Map<String, Object> retmap=null;
			String movieid = (String) params[0];
			String videoid = (String) params[1];
			String isonline = (String) params[2];
			try {
				System.out.println("user player order movieid = " + movieid + "videoid = " + videoid + "isonline = " + isonline);
				retmap = libCloud.user_onlineorder(movieid,videoid,isonline);	
			}
			catch (WeiboException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("user online order retmap = " + retmap);
			return retmap;
		}

		@Override
		protected void onPostExecute(Map<String, Object> result) {
			
		}
		
	}
}
