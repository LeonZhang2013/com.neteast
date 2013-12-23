package com.neteast.clouddisk.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import com.lib.db.DataInfo;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.param.Params;

public class DataHelpter {
	private static String TAG = "DataHelpers";
	public static final String IMG_DIR = "/ImageCache/"; 
	private static int userid=0;
	private static String token ="";
	private static List<DataInfo> mMusicPlaylist = new ArrayList<DataInfo>();
	//private static List<DataInfo>  videoList = null;
	//private static List<DataInfo>  musicList = null;
	//private static List<DataInfo> picList = null;
	
	//private static List<Map<String, Object>> appList = null;
	//private static List<Map<String, Object>> newsList = null;
	private static Map<String, Object> videoList = null;
	private static Map<String, Object> musicList = null;
	private static Map<String, Object> picList = null;
	private static Map<String, Object> appList = null;
	private static Map<String, Object> imagenewsList = null;
	private static Map<String, Object> textnewsList = null;
	
	private static List<String> installed = null;
	private static  List<Map<String, Object>> videoTagsList = null;
	private static  List<Map<String, Object>> musicTagsList = null;
	private static  List<Map<String, Object>> pictureTagsList = null;
	private static  List<Map<String, Object>> newsTagsList = null;
	private static  List<DataInfo> searchResultList = null;
	private static  List<DataInfo> newssearchResultList = null;
	
	public static List<Map<String, Object>> getVideoTags(){
		return videoTagsList;
	}
	public static List<Map<String, Object>> getMusicTags(){
		return musicTagsList;
	}
	public static List<Map<String, Object>> getPictureTags(){
		return pictureTagsList;
	}
	public static List<Map<String, Object>> getNewsTags(){
		return newsTagsList;
	}
	public static List<DataInfo> getSearchList(){
		return searchResultList;
	}
	public static List<DataInfo> getNewsSearchList(){
		return newssearchResultList;
	}
	public static void setVideoTags( List<Map<String, Object>> list){
		videoTagsList = list;
	}
	public static void setMusicTags( List<Map<String, Object>> list){
		musicTagsList = list;
	}
	public static void setPictureTags( List<Map<String, Object>> list){
		pictureTagsList = list;
	}
	public static void setNewsTags( List<Map<String, Object>> list){
		newsTagsList = list;
	}
	public static void setSearchList( List<DataInfo> list){
		searchResultList = list;
	}
	public static void setNewsSearchList( List<DataInfo> list){
		newssearchResultList = list;
	}
	
	
	public static Map<String, Object> fillData(List<Map<String, Object>> list,
			List<DataInfo> downloadList, Object... params) {
		if(list ==null||list.size()==0){
			return null;
		}
		int size = list.size();
		/*
		int temp1 = size / (Integer)params[2];
		int temp2 = size % (Integer)params[2];
		int totalPage = temp2==0?temp1:temp1+1;
		*/
		List<DataInfo> result = new ArrayList<DataInfo>();
		Map<String,Object> rm = new HashMap<String,Object>();
		
		//if(size <= (Integer)params[2]){
			for(int i=0;i<list.size();i++){
				DataInfo di = new DataInfo();
				Map<String,Object> m = (Map<String,Object>)list.get(i);
				String download = (String)m.get("download");
				if(download!=null && download.length()>0){
					if(Integer.parseInt(download)>0){
						di.setStatus(1);
					}else{
						di.setStatus(0);
					}
				}else{
					di.setStatus(0);
				}
				/*
				System.out.println("Recommand is download =" + download);
				if(findSameDataById((String) m.get("id"),downloadList)){
					di.setStatus(1);
				}
				*/
				String images = (String)m.get("images");
				if(images!=null && images.length()>0){
					String[] imagesurl = null;
					imagesurl = splitEpisode(images,";");
					//System.out.println("images id =" + (String)m.get("id") + "images name + " + (String)m.get("name"));
					//System.out.println("images = " + images);
					//System.out.println("imagesurl len = " + imagesurl.length);
					if(imagesurl.length>1)
					{
						di.SetIsDir("1");
						di.setImages(imagesurl);
					}else{
						di.SetIsDir("0");
					}
				}
				String downloads = (String)m.get("imgdownload");
				if(downloads!=null && downloads.length()>0){
					String[] downloads_temp = null;
					System.out.println("images = " + images);
					System.out.println("folder downloads = " + downloads);
					downloads_temp = splitEpisode(downloads,";");
					di.setImagesDownload(downloads_temp);
				}
				String temp = (String)m.get("name");
				if(temp.length()>8){
					di.setName(temp.substring(0,7)+"..");
				}else{
					di.setName(temp);
				}
				di.setImage((String)m.get("image"));
				di.setUrl((String) m.get("url"));
				di.setId((String)m.get("id"));
				di.setDesc((String)m.get("desc"));
				String remark = (String)m.get("remark");
				if(remark!=null && remark.length()>0){
					int index = remark.indexOf("分");
					if(index >0){
						String subRemark = remark.substring(0, index);
						di.setRemark(subRemark);
					}else{
						di.setRemark(remark);
					}
					
				}
				//di.setRemark((String)m.get("remark"));
				di.setType((String)m.get("type"));
				di.setChildtype((String)m.get("childtype"));
				di.setResid((String)m.get("resid"));
				di.setThumb((String)m.get("thumb"));
				di.setSinger((String)m.get("singer"));
				if(m.get("packagename")!=null){
					di.setPackages((String) m.get("packagename"));
				}
				//di.setChildtype((String)m.get("reschildtype"));
				result.add(di);
			}
			rm.put("result", result);
		/*}else{
			int currPage = (Integer) params[0];
			int index = (currPage - 1) * (Integer)params[2];
			int times =  currPage<totalPage?(Integer)params[2]: temp2==0?(Integer)params[2]:temp2;
			for(int i=0;i<times;i++){
				DataInfo di = new DataInfo();
				Map<String,Object> m = (Map<String,Object>)list.get(i+index);
				if(findSameDataById((String) m.get("id"),downloadList)){
					di.setStatus(1);
				}
				String temp = (String)m.get("name");
				if(temp.length()>8){
					di.setName(temp.substring(0,7)+"..");
				}else{
					di.setName(temp);
				}
				di.setImage((String)m.get("image"));
				di.setUrl((String)m.get("url"));
				di.setId((String)m.get("id"));
				di.setDesc((String)m.get("desc"));
				di.setRemark((String)m.get("remark"));
				di.setType((String)m.get("type"));
				di.setChildtype((String)m.get("childtype"));
				di.setResid((String)m.get("resid"));
				di.setThumb((String)m.get("thumb"));
				di.setSinger((String)m.get("singer"));
				if(m.get("packagename")!=null){
					di.setPackages((String) m.get("packagename"));
				}
				result.add(di);
			}
			rm.put("result", result);
		}
		*/
		//rm.put("totalpage", (Integer)totalPage);
		return rm;
	}
	public static Map<String,Object> fillDownloadData(List<Map<String,Object>>  list,Object... params){
		if(list ==null||list.size()==0){
			return null;
		}
		int size = list.size();
		int temp1 = size / (Integer)params[1];
		int temp2 = size % (Integer)params[1];
		int totalPage = temp2==0?temp1:temp1+1;
		List<DataInfo> result = new ArrayList<DataInfo>();
		Map<String,Object> rm = new HashMap<String,Object>();
		
		//if(size <= Params.MYDOWNLOAD_PICTRUE_PER_PAGE_NUM){
			for(int i=0;i<list.size();i++){
				DataInfo di = new DataInfo();
				Map<String,Object> m = (Map<String,Object>)list.get(i);
				System.out.println(m.keySet());
				di.setId((String)m.get("id"));
				di.setType((String)m.get("restype"));
				di.setResid((String)m.get("resid"));
				//di.setResInfo((String)m.get("resinfo"));
				String temp = (String)m.get("resname");
				if(temp.length()>8){
					di.setName(temp.substring(0,7)+"..");
				}else{
					di.setName(temp);
				}
				//di.setName((String)m.get("resname"));
				//di.setAddTime((String)m.get("addtime"));
				//di.setPath((String)m.get("path"));
				//di.sethuasuplayurl((String)m.get("huasuplayurl"));
				//di.setonlineplayurl((String)m.get("onlineplayurl"));
				di.setUrl((String)m.get("url"));
				di.setImage((String)m.get("image"));
				di.setThumb((String)m.get("image"));
				//di.setSize((String)m.get("size"));
				di.setRemark((String)m.get("remark"));
				di.setChildtype((String)m.get("reschildtype"));
				di.setSecurity((String)m.get("needpassword"));
				di.setSinger((String)m.get("singer"));
				if(m.get("path")!=null&&(!"".equals(m.get("path")))){
					di.SetPkgName((String) m.get("path"));
					di.SetIsDir("1");
				}
				if(m.get("movieid")!=null && (!m.get("movieid").equals(""))){
					di.setMovieId((String) m.get("movieid"));
				}
				result.add(di);
			}
			rm.put("result", result);
		/*}else{
			int currPage = (Integer) params[0];
			int index = (currPage - 1) * Params.MYDOWNLOAD_PICTRUE_PER_PAGE_NUM;
			int times =  currPage<totalPage?Params.MYDOWNLOAD_PICTRUE_PER_PAGE_NUM: temp2==0?Params.RECOMMEND_PICTRUE_PER_PAGE_NUM:temp2;
			for(int i=0;i<times;i++){
				DataInfo di = new DataInfo();
				Map<String,Object> m = (Map<String,Object>)list.get(i+index);
				di.setId((String)m.get("id"));
				di.setType((String)m.get("restype"));
				di.setResid((String)m.get("resid"));
				//di.setResInfo((String)m.get("resinfo"));
				String temp = (String)m.get("resname");
				if(temp.length()>8){
					di.setName(temp.substring(0,7)+"..");
				}else{
					di.setName(temp);
				}
				//di.setName((String)m.get("resname"));
				//di.setAddTime((String)m.get("addtime"));
				//di.setPath((String)m.get("path"));
				//di.sethuasuplayurl((String)m.get("huasuplayurl"));
				//di.setonlineplayurl((String)m.get("onlineplayurl"));
				di.setUrl((String)m.get("url"));
				//di.setImage((String)m.get("image"));
				di.setThumb((String)m.get("image"));
				//di.setSize((String)m.get("size"));
				di.setRemark((String)m.get("remark"));
				di.setChildtype((String)m.get("reschildtype"));
				di.setSecurity((String)m.get("needpassword"));
				if(m.get("path")!=null&&(!"".equals(m.get("path")))){
					di.SetPkgName((String) m.get("path"));
					di.SetIsDir("1");
				}
				if(m.get("movieid")!=null && (!m.get("movieid").equals(""))){
					di.setMovieId((String) m.get("movieid"));
				}
				result.add(di);
			}
			rm.put("result", result);
		
		}*/
		
		rm.put("totalpage", (Integer)totalPage);
		return rm;

	}
	public static Map<String,Object> fillUploadData(List<Map<String,Object>>  list,Object... params){
		if(list ==null||list.size()==0){
			return null;
		}
		int size = list.size();
		int temp1 = size / Params.MYUPLOAD_DATA_PER_PAGE_NUM;
		int temp2 = size % Params.MYUPLOAD_DATA_PER_PAGE_NUM;
		int totalPage = temp2==0?temp1:temp1+1;
		List<DataInfo> result = new ArrayList<DataInfo>();
		Map<String,Object> rm = new HashMap<String,Object>();
		
	//	if(size <= Params.MYUPLOAD_PICTRUE_PER_PAGE_NUM){
			for(int i=0;i<list.size();i++){
				DataInfo di = new DataInfo();
				Map<String,Object> m = (Map<String,Object>)list.get(i);
				di.setId((String)m.get("id"));
				di.setType((String)m.get("type"));
				String temp = (String)m.get("name");
				if(temp.length()>8){
					di.setName(temp.substring(0,7)+"..");
				}else{
					di.setName(temp);
				}
				//di.setName((String)m.get("name"));
				//di.setAddTime((String)m.get("addtime"));
				//di.setPath((String)m.get("path"));
				//di.sethuasuplayurl((String)m.get("huasuplayurl"));
				//di.setonlineplayurl((String)m.get("onlineplayurl"));
				di.setSecurity((String)m.get("security"));
				di.setUrl((String)m.get("sourceurl"));
				di.setImage((String)m.get("image"));
				di.setThumb((String)m.get("thumb"));
				di.SetIsDir((String)m.get("isdir"));
				di.setPasswd((String)m.get("password"));
				if(m.get("status")!=null) di.setStatus(Integer.parseInt((String)m.get("status")));
				//di.setSize((String)m.get("filesize"));	
				result.add(di);
			}
			rm.put("result", result);
		/*}else{
			int currPage = (Integer) params[0];
			int index = (currPage - 1) * Params.MYUPLOAD_PICTRUE_PER_PAGE_NUM;
			int times =  currPage<totalPage?Params.MYUPLOAD_PICTRUE_PER_PAGE_NUM: temp2==0?Params.MYUPLOAD_PICTRUE_PER_PAGE_NUM:temp2;
			for(int i=0;i<times;i++){
				DataInfo di = new DataInfo();
				Map<String,Object> m = (Map<String,Object>)list.get(i+index);
				di.setId((String)m.get("id"));
				di.setType((String)m.get("type"));
				String temp = (String)m.get("name");
				if(temp.length()>8){
					di.setName(temp.substring(0,7)+"..");
				}else{
					di.setName(temp);
				}
				//di.setName((String)m.get("name"));
				//di.setAddTime((String)m.get("addtime"));
				//di.setPath((String)m.get("path"));
				//di.sethuasuplayurl((String)m.get("huasuplayurl"));
				//di.setonlineplayurl((String)m.get("onlineplayurl"));
				di.setSecurity((String)m.get("security"));
				di.setUrl((String)m.get("sourceurl"));
				di.setImage((String)m.get("image"));
				di.setThumb((String)m.get("thumb"));
				di.SetIsDir((String)m.get("isdir"));
				//di.setSize((String)m.get("filesize"));	
				result.add(di);
			}
			
			rm.put("result", result);
			
		}
		*/
		rm.put("totalpage", (Integer)totalPage);
		return rm;

	}
	/*
	public static void createPlaylist(List<Map<String,Object>> list){
		mMusicPlaylist.clear();
		if(list ==null||list.size()==0){
			return ;
		}
		for(int i=0;i<list.size();i++){
			DataInfo di = new DataInfo();
			Map<String,Object> m = (Map<String,Object>)list.get(i);
			String temp = (String)m.get("name");
			if(temp==null||temp.length()==0){
				temp = (String)m.get("resname");
			}
			if(temp.length()>8){
				di.setName(temp.substring(0,7)+"..");
			}else{
				di.setName(temp);
			}
			//di.setName((String)m.get("name"));
			di.setImage((String)m.get("image"));
			temp = null;
			temp = (String)m.get("url");
			if(temp ==null || temp.length()==0){
				temp = (String)m.get("sourceurl");
			}
			di.setUrl(temp);
			di.setId((String)m.get("id"));
			di.setDesc((String)m.get("desc"));
			di.setRemark((String)m.get("remark"));
			temp = null;
			temp = (String)m.get("type");
			if(temp==null||temp.length()==0){
				temp = (String)m.get("restype");
			}
			di.setType(temp);
			di.setChildtype((String)m.get("childtype"));
			//di.setResid((String)m.get("resid"));
			String filesize = (String) m.get("filesize");
			if(filesize!=null && filesize.length() > 0){
				di.setFileSize(Long.parseLong(filesize));
			}
			di.setAddTime((String) m.get("createtime"));
			mMusicPlaylist.add(di);
		}
	}
	*/
	public static void createPlaylist(List<DataInfo> list){
		mMusicPlaylist.clear();
		if(list ==null||list.size()==0){
			return ;
		}
		for(int i=0;i<list.size();i++){
			DataInfo info = list.get(i);
			if(info.GetIsDir()!=null && info.GetIsDir().equals("1")){
				
			}else{
				mMusicPlaylist.add(info);
			}
		}
	}
	public static void setPlaylist(List<DataInfo> list){
		mMusicPlaylist.clear();
		for(int i=0;i<list.size();i++){
			DataInfo info = list.get(i);
			if(info.GetIsDir()!=null && info.GetIsDir().equals("1")){
				
			}else{
				mMusicPlaylist.add(info);
			}
		}
	}
	public static List<DataInfo> getPlaylist(){
		return mMusicPlaylist;
	}
	public static Map<String,Object> fillData(List<Map<String,Object>> list,boolean nodesc){
		if(list==null||list.size()==0){
			return null;
		}
		List<DataInfo> result = new ArrayList<DataInfo>();
		Map<String,Object> rm = new HashMap<String,Object>();
		for(int i=0;i<list.size();i++){
			DataInfo di = new DataInfo();
			Map<String,Object> m = (Map<String,Object>)list.get(i);
			//di.setName((String)m.get("name"));
			String temp = (String)m.get("name");
			if(temp==null||temp.length()==0){
				temp = (String)m.get("resname");
			}
			di.setName(temp);
			String thumb = (String)m.get("image");
			if(thumb==null || thumb.length()==0){
				thumb = (String)m.get("thumb");
			}
			di.setThumb(thumb);
			di.setImage(thumb);
			String url = (String)m.get("url");
			if(url==null ||url.length()==0){
				url = (String)m.get("sourceurl");
			}
			di.setUrl(url);
			di.setId((String)m.get("id"));
			if(nodesc){
				di.setDesc("");
			}else{
				di.setDesc((String)m.get("desc"));
			}
			di.setRemark((String)m.get("remark"));
			temp = null;
			temp = (String)m.get("type");
			if(temp==null||temp.length()==0){
				temp = (String)m.get("restype");
			}
			di.setType(temp);
			di.setChildtype((String)m.get("childtype"));
			di.setResid((String) m.get("resid"));
			String filesize = (String) m.get("filesize");
			if(filesize!=null && filesize.length() > 0){
				di.setFileSize(Long.parseLong(filesize));
			}
			String source = null;
			source = (String) m.get("source");
			if(source!=null && source.length()>0){
				di.SetTag(source);
			}else{
				di.SetTag("");
			}
			di.setAddTime((String) m.get("addtime"));
			String sourceTime = (String)m.get("sourcetime");
			if(sourceTime!=null && sourceTime.length()>0){
				di.setSourceTime((String)m.get("sourcetime"));
			}else{
				di.setSourceTime("");
			}
			di.setStatus(2);
			String str = (String) m.get("isdir");
			if(str!=null){
				if(!str.equals("0")){
					continue;
				}
			}
			result.add(di);
		}
		rm.put("result", result);
		return rm;
	}
	
	public static Map<String,Object> fillRecordData(List<Map<String,Object>> list){
		if(list==null||list.size()==0){
			return null;
		}
		List<DataInfo> result = new ArrayList<DataInfo>();
		Map<String,Object> rm = new HashMap<String,Object>();
		for(int i=0;i<list.size();i++){
			DataInfo di = new DataInfo();
			Map<String,Object> m = (Map<String,Object>)list.get(i);
			//di.setName((String)m.get("name"));
			String temp = (String)m.get("name");
			if(temp==null||temp.length()==0){
				temp = (String)m.get("resname");
			}
			di.setName(temp);
			String thumb = (String)m.get("image");
			if(thumb==null || thumb.length()==0){
				thumb = (String)m.get("thumb");
			}
			di.setThumb(thumb);
			di.setImage(thumb);
			String url = (String)m.get("url");
			if(url==null ||url.length()==0){
				url = (String)m.get("sourceurl");
			}
			di.setUrl(url);
			di.setId((String)m.get("id"));
			
			di.setRemark((String)m.get("remark"));
			temp = null;
			temp = (String)m.get("type");
			if(temp==null||temp.length()==0){
				temp = (String)m.get("restype");
			}
			di.setType(temp);
			di.setChildtype((String)m.get("childtype"));
			di.setResid((String) m.get("resid"));
			String filesize = (String) m.get("filesize");
			if(filesize!=null && filesize.length() > 0){
				di.setFileSize(Long.parseLong(filesize));
			}
			String source = null;
			source = (String) m.get("source");
			if(source!=null && source.length()>0){
				di.SetTag(source);
			}else{
				di.SetTag("");
			}
			di.setAddTime((String) m.get("addtime"));
			String sourceTime = (String)m.get("sourcetime");
			if(sourceTime!=null && sourceTime.length()>0){
				di.setSourceTime((String)m.get("sourcetime"));
			}else{
				di.setSourceTime("");
			}
			
			String str = (String) m.get("isdir");
			if(str!=null){
				if(!str.equals("0")){
					continue;
				}
			}
			String status = (String) m.get("status");
			//System.out.println("stauts = " + status);
			if(!status.equals("0")){
				di.setStatus(2);
				result.add(di);
			}
		}
		rm.put("result", result);
		return rm;
	}
	
	public static Map<String,Object> fillDetailData(List<Map<String,Object>>  list,Object... params){
		if(list ==null||list.size()==0){
			return null;
		}
		int size = list.size();
		int temp1 = size / (Integer)params[2];
		int temp2 = size % (Integer)params[2];
		int totalPage = temp2==0?temp1:temp1+1;
		List<DataInfo> result = new ArrayList<DataInfo>();
		Map<String,Object> rm = new HashMap<String,Object>();
		
		if(size <= (Integer)params[2]){
			for(int i=0;i<list.size();i++){
				DataInfo di = new DataInfo();
				Map<String,Object> m = (Map<String,Object>)list.get(i);
				String temp = (String)m.get("name");
				if(temp.length()>8){
					di.setName(temp.substring(0,7)+"..");
				}else{
					di.setName(temp);
				}
				di.setImage((String)m.get("image"));
				di.setUrl((String) m.get("url"));
				di.setId((String)m.get("id"));
				di.setDesc((String)m.get("desc"));
				di.setRemark((String)m.get("remark"));
				di.setType((String)m.get("type"));
				di.setChildtype((String)m.get("childtype"));
				//di.setResid((String)m.get("resid"));
				di.setThumb((String)m.get("thumb"));
				//di.setChildtype((String)m.get("reschildtype"));
				result.add(di);
			}
			rm.put("result", result);
		}else{
			int currPage = (Integer) params[0];
			int index = (currPage - 1) * (Integer)params[2];
			int times =  currPage<totalPage?(Integer)params[2]: temp2==0?(Integer)params[2]:temp2;
			for(int i=0;i<times;i++){
				DataInfo di = new DataInfo();
				Map<String,Object> m = (Map<String,Object>)list.get(i+index);
				String temp = (String)m.get("name");
				if(temp.length()>8){
					di.setName(temp.substring(0,7)+"..");
				}else{
					di.setName(temp);
				}
				di.setImage((String)m.get("image"));
				di.setUrl((String)m.get("url"));
				di.setId((String)m.get("id"));
				di.setDesc((String)m.get("desc"));
				di.setRemark((String)m.get("remark"));
				di.setType((String)m.get("type"));
				di.setChildtype((String)m.get("childtype"));
				//di.setResid((String)m.get("resid"));
				di.setThumb((String)m.get("thumb"));
				result.add(di);
			}
			rm.put("result", result);
		}
		
		rm.put("totalpage", (Integer)totalPage);
		return rm;
	}
	/**
	 * ���id���������б��в�����ͬ����
	 * @param id ���?
	 * @param list �Ѿ����ص��б�
	 * @return
	 */
	public static boolean findSameDataById(String id, List<DataInfo> list) {
		boolean flag = false;
		if(list!=null){
			for (Iterator<DataInfo> iterator = list.iterator(); iterator.hasNext();) {
				DataInfo dataInfo = (DataInfo) iterator.next();
				if(id.equals(dataInfo.getResid())){
					flag = true;
					break;
				}
			}
		}
		return flag;
	}
	public static List<DataInfo> GetSeriesList(List<Map<String,Object>> list){
		List<DataInfo> result = new ArrayList<DataInfo>();
		if(list==null||list.size()==0){
			return result;
		}
		for(int i=0;i<list.size();i++){
			Map<String,Object> m  = (Map<String,Object>)list.get(i);
			DataInfo di = new DataInfo();
			String title = (String) m.get("series");
		//	String series = (String) m.get("series");
			String playurl = (String) m.get("playurl");
			String movieid = (String)m.get("movieid");
			String videoid = (String)m.get("videoid");
			di.setUrl(playurl);
			di.setName(title);
			di.setMovieId(movieid);
			di.setVideoid(videoid);
		//	di.SetSeries(series);
			result.add(di);
		}
		return result;
	}
	
	
	/*
	public static List<DataInfo> GetSeriesList(List<Map<String,Object>> list){
		List<DataInfo> result = new ArrayList<DataInfo>();
		if(list==null||list.size()==0){
			return result;
		}
		
		String[] seriesurl = null;
		String[] nameinfo =null;
		String huasuplayurl= (String) list.get(0).get("huasuplayurl");
		String desc2 = (String) list.get(0).get("desc2");
		if(huasuplayurl!=null && huasuplayurl.length()>0){
			seriesurl = splitEpisode(huasuplayurl,";");
		}if(desc2!=null && desc2.length() > 0){
			nameinfo = splitEpisode(desc2,";");
		}
		if(seriesurl==null||nameinfo==null) return result;
		
		System.out.println("GetSeriesList url size:"+seriesurl.length + "nameinfo size"+nameinfo.length);
		
		for(int i=0;i<seriesurl.length;i++){
			DataInfo di = new DataInfo();
			di.setUrl(seriesurl[i]);
			if(nameinfo.length >0 && nameinfo.length >i &&nameinfo[i].length()>0){
				di.setName(nameinfo[i]);
			}else{
				int index = i+1;
				di.setName("第"+index+"集");
			}
			result.add(di);
		}
		return result;
	}
	*/
	public static List<DataInfo> GetOnlineList(Map<String,Object> map,int type){
		List<DataInfo> result = new ArrayList<DataInfo>();
		List<Map<String, Object>>  seriesItemList = null;
		if(map==null) return result;
		String Count = (String) map.get("http_count");
		int itemCount = Integer.parseInt(Count);
		if(itemCount<=0){
			return result;
		}
		seriesItemList = (List<Map<String, Object>>) map.get("http_item");
		for(int i=0;i< seriesItemList.size();i++){
			Map<String,Object> itemHttpInfo = null;
			itemHttpInfo = (Map<String, Object>) seriesItemList.get(i);
			if(itemHttpInfo!=null){
				DataInfo info = new DataInfo();
				String series = (String) itemHttpInfo.get("series");
				//String swfurl = (String) itemHttpInfo.get("swfurl");
				String swfurl = (String) itemHttpInfo.get("mp4url");
				info.setMovieId((String) itemHttpInfo.get("mid"));
				info.setVideoid((String) itemHttpInfo.get("id"));
				info.setName((String) itemHttpInfo.get("title"));
				/*
				if(type ==1 || type ==2){//1:电影，2：电视剧
					info.setName((String) itemHttpInfo.get("title"));
					
				}
	
				else{
					if(series!=null || series.length()>0 && swfurl!=null && swfurl.length()>0){
						info.setName(series);
					}else{
						break;
					}
				}
				*/
				info.setUrl(swfurl);
				info.SetTag((String) itemHttpInfo.get("tag"));
				info.SetTagName((String) itemHttpInfo.get("tagname"));
				info.SetSeries((String) itemHttpInfo.get("series"));
				if(info.getUrl()!=null && info.getUrl().length()>0){
					result.add(info);	
				}
			}
			
				//System.out.println("title:"+ title + "tag" + tag+ "playurl:" + playurl + "series:"+series +"swfurl:"+swfurl);
		}
		
	return result;
	}
	
	public static List<DataInfo> GetOfflineTaskList(Map<String,Object> map){
		List<DataInfo> result = new ArrayList<DataInfo>();
		List<Map<String, Object>>  offlineItemList = null;
		if(map==null) return result;
		
		offlineItemList = (List<Map<String, Object>>) map.get("tasklist");
		if(offlineItemList==null) return result;
		for(int i=0;i< offlineItemList.size();i++){
			Map<String,Object> itemHttpInfo = null;
			itemHttpInfo = (Map<String, Object>) offlineItemList.get(i);
			if(itemHttpInfo!=null){
				DataInfo info = new DataInfo();
				String status = (String) itemHttpInfo.get("status");
		
				//if(status.equals("3")){ /* 已下载完成*/
					info.setName((String) itemHttpInfo.get("title"));
					info.setUrl((String) itemHttpInfo.get("download_url"));
					info.setInfoHash((String) itemHttpInfo.get("content_infohash"));
					info.setId((String) itemHttpInfo.get("id"));
					result.add(info);
				//}
				
				//System.out.println("title:"+ title + "tag" + tag+ "playurl:" + playurl + "series:"+series +"swfurl:"+swfurl);
			}
		}
		
		return result;
	}
	public static void GetOfflineResList(List<Map<String, Object>> list,List<DataInfo> datalist,String taskid){
		for(int i=0;i<list.size();i++){
			Map<String,Object> m = (Map<String,Object>)list.get(i);
			DataInfo info = new DataInfo();
			info.setId(taskid);
			info.setFileid((String)m.get("file_id"));
			info.setName((String)m.get("filename"));
			datalist.add(info);
		}
	}
	public static List<DataInfo> getImagesList(List<String> imagesurl,String name,String id,List<String> downloads){
		List<DataInfo> imagesList = new ArrayList<DataInfo>();
		for(int i=0;i<imagesurl.size();i++){
			String url = imagesurl.get(i);
			DataInfo info_tmp = new DataInfo();
			info_tmp.setName(name);
			info_tmp.setUrl(imagesurl.get(i)); 	
			info_tmp.setId(id);
			int index = url.lastIndexOf("/");
			String filename=url.substring(index + 1);
			String smallName = filename.replace(".", "_s.");
			String smallurl = url.substring(0,index+1)+smallName;
			info_tmp.setThumb(smallurl);
			
			for(int j=0;j<downloads.size();j++){
				
				String download = downloads.get(j);
				if(Integer.parseInt(download) == i){
					info_tmp.setStatus(1);
					break;
				}else{
					info_tmp.setStatus(0);
				}
			}
			imagesList.add(info_tmp);
		}
		return imagesList;
	}
	
	public static List<DataInfo> fillTransData(List<Map<String,Object>>  list,String fileid,String imageurl){
		List<DataInfo> result = new ArrayList<DataInfo>();
		if(list==null||list.size()==0){
			return result;
		}
		System.out.println("fill trans Data List size = " + list.size());
		for(int i=0;i<list.size();i++){
			DataInfo di = new DataInfo();
			Map<String,Object> m = (Map<String,Object>)list.get(i);
			
			di.setId(fileid);
			di.setThumb(imageurl);
			di.setName((String)m.get("name"));
			di.setStatus(Integer.parseInt((String)m.get("status")));
			di.setProgress(Long.parseLong((String)m.get("percent")));
			di.setUrl((String)m.get("transurl"));
			di.SetTag((String)m.get("width"));
			System.out.println("name =" + di.getName() + "status" + di.getStatus() + "percent" + di.getProgress());
			result.add(di);
		}
		return result;
	}
	public static boolean findDataByType(List<DataInfo> list,String type){
		if(list==null || list.size()==0) return false;
		for(int i=0;i<list.size();i++){
			DataInfo info = list.get(i);
			if(info.GetTag().equals(type)){
				return true;
			}
		}
		return false;
	}
	public static boolean needUpdatePercent(List<DataInfo> list){
		for(int i=0;i<list.size();i++){
			DataInfo info = list.get(i);
			if(info.getStatus()==3){
				return true;
			}
		}
		return false;
	}
	
	public static String[] splitEpisode(String url,String Delimiter){
	    	return url.split(Delimiter);
	}
	    
	public static String[] splitEpisode(String url){
	    	return splitEpisode(url,";");
	}
	
	public static void deleteSDcardCacheAll(){
		String mImagePath = Environment.getExternalStorageDirectory().getPath() + IMG_DIR;
		if(mImagePath!=null){
			File img = new File(mImagePath);
			File[] imgs = img.listFiles();
			for (int i = 0; i < imgs.length; i++) {
				imgs[i].delete();
			}
		}
	}

	public static int isLogin(ContentResolver cr) {
		userid =0;
		Uri uri = Uri.parse("content://com.neteast.androidclient.newscenter/info");
		Cursor cursor = cr.query(uri, null, null, null, null);
		if(cursor==null) return 0;
		if (cursor.moveToFirst()) {
			userid = cursor.getInt(cursor.getColumnIndex("userid"));
			token = cursor.getString(cursor.getColumnIndex("token"));
			System.out.println("getLoginInfo userid= " + userid + "token =" + token);
		}
		return userid;
	}
	public static int getUserId(){
		return userid;
	}
	public static String getToken(){
		return token;
	}
	public static int computeTotalPage(int totalItems,int singlePageItems){
		if (totalItems<=0) {
			return 1;
		}
		return (int) Math.ceil(((double)totalItems)/singlePageItems);
	}
	public static void printException(Context context,Exception e){
		/*
		try {
			SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日HH�?);
			SimpleDateFormat format2=new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss�?);
			Date date = new Date(System.currentTimeMillis());
			String fileName = format.format(date);
			String dividerLine="\n\n\n\n====================="+format2.format(date)+"=====================\n";
			
			File logFile=new File(createDir(context), "udisk/log/"+fileName+".txt");
			logFile.getParentFile().mkdirs();
			logFile.createNewFile();
			FileWriter writer=new FileWriter(logFile,true);
			
			StringBuilder sb=new StringBuilder(dividerLine);
			sb.append(e.toString()).append("\n");
			StackTraceElement[] stackTrace = e.getStackTrace();
			final int N=stackTrace.length;
			for (int i=0;i<N;i++) {
				sb.append(stackTrace[i].toString()).append("\n");
			}
			writer.write(sb.toString());
			writer.close();
		} catch (IOException e2) {
			Log.e("test", e2.getMessage());
		}
		*/
	}
	public static int GetFileType(String filename){
		int type = 0;
		/*
		if(filename.matches("^.*?\\.(mp4|3gp|avi|rm|rmvb|RMVB|mkv|ts|mpg|flv)$")){
			type = 1;
		}else if(filename.matches("^.*?\\.(mp3|MP3|wma|WMA|aac|AAC)$")){
			type = 2;
		}else if(filename.matches("^.*?\\.(jpg|JPG|png|PNG|bmp|BMP|gif|GIF)$")){
			type = 3;
		}else if(filename.matches("^.*?\\.(doc|txt|pdf|docx)$")){
			type = 4;
		}
		*/
		if(filename.matches("^.*?\\.(mp4|3gp|avi|rm|rmvb|RMVB|flv)$")){
			type = 1;
		}else if(filename.matches("^.*?\\.(mp3|MP3|wma|WMA)$")){
			type = 2;
		}else if(filename.matches("^.*?\\.(jpg|JPG|png|PNG|bmp|BMP)$")){
			type = 3;
		}else if(filename.matches("^.*?\\.(doc|txt|pdf|docx)$")){
			type = 4;
		}
		return type;
	}
	public static Map<String, Object> getVideoList(String source){
		if(videoList!=null){
			return (Map<String, Object>) videoList.get(source);
		}
		return null;
	}
	public static void setVideoList(List<DataInfo> list,String source,String reqtime){
		Map<String,Object> map = new HashMap<String,Object>(); 	
		map.put("recommend", list);
		map.put("reqtime", reqtime);
		if(videoList!=null){
			videoList.put(source,map);
	
		}else{
			videoList = new HashMap<String,Object>(); 
			videoList.put(source,map);

		}	
		//videoList = list;
	}
	public static void updateVideoList(List<DataInfo> list,String source,String reqtime){
		if(videoList!=null){
			Map<String,Object> map = (Map<String, Object>) videoList.get(source);
			List<DataInfo> savedList = (List<DataInfo>) map.get("recommend");
			String savedtime = (String) map.get("reqtime");
			savedtime = reqtime;
			for(int i=0;i<list.size();i++){
				savedList.add(0,list.get(i));
			}
		}
	}
	public static Map<String, Object> getMusicList(String source){	
		if(musicList!=null){
			return (Map<String, Object>) musicList.get(source);
		}
		return null;
	}
	public static void setMusicList(List<DataInfo> list,String source,String reqtime){
		Map<String,Object> map = new HashMap<String,Object>(); 	
		map.put("recommend", list);
		map.put("reqtime", reqtime);
		if(musicList!=null){
			musicList.put(source,map);
	
		}else{
			musicList = new HashMap<String,Object>(); 
			musicList.put(source,map);

		}	
		//musicList = list;
	}
	public static void updateMusicList(List<DataInfo> list,String source,String reqtime){
		if(musicList!=null){
			Map<String,Object> map = (Map<String, Object>) musicList.get(source);
			List<DataInfo> savedList = (List<DataInfo>) map.get("recommend");
			String savedtime = (String) map.get("reqtime");
			savedtime = reqtime;
			for(int i=0;i<list.size();i++){
				savedList.add(0,list.get(i));
			}
		}
	}
	
	public static Map<String, Object>  getPicList(String source){
		if(picList!=null){
			return (Map<String, Object>) picList.get(source);
		}
		return null;
	}
	public static void setPicList(List<DataInfo> list,String source,String reqtime){
		Map<String,Object> map = new HashMap<String,Object>(); 	
		map.put("recommend", list);
		map.put("reqtime", reqtime);
		if(picList!=null){
			picList.put(source,map);
	
		}else{
			picList = new HashMap<String,Object>(); 
			picList.put(source,map);

		}	
		//picList = list;
	}
	
	public static void updatePicList(List<DataInfo> list,String source,String reqtime){
		if(picList!=null){
			Map<String,Object> map = (Map<String, Object>) picList.get(source);
			List<DataInfo> savedList = (List<DataInfo>) map.get("recommend");
			String savedtime = (String) map.get("reqtime");
			savedtime = reqtime;
			for(int i=0;i<list.size();i++){
				savedList.add(0,list.get(i));
			}
		}
	}
	
	public static Map<String, Object> getAppList(String source){
		if(appList!=null){
			return (Map<String, Object>) appList.get(source);
		}
		return null;
	}
	public static void setAppList(List<Map<String, Object>> list,String source,String reqtime){
		Map<String,Object> map = new HashMap<String,Object>(); 	
		map.put("recommend", list);
		map.put("reqtime", reqtime);
		if(appList!=null){
			appList.put(source,map);
	
		}else{
			appList = new HashMap<String,Object>(); 
			appList.put(source,map);

		}
		//appList = list;
	}
	public static void updateAppList(List<Map<String, Object>> list,String source,String reqtime){
		if(appList!=null){
			Map<String,Object> map = (Map<String, Object>) appList.get(source);
			List<Map<String, Object>> savedList = (List<Map<String, Object>>) map.get("recommend");
			String savedtime = (String) map.get("reqtime");
			savedtime = reqtime;
			for(int i=0;i<list.size();i++){
				savedList.add(0,list.get(i));
			}
		}
	}
	public static Map<String, Object> getimageNewsList(String source){
		if(imagenewsList!=null){
			return (Map<String, Object>) imagenewsList.get(source);
		}
		return null;
	}
	public static void setimageNewsList(List<Map<String, Object>> list,String source,String reqtime){
		Map<String,Object> map = new HashMap<String,Object>(); 	
		map.put("recommend", list);
		map.put("reqtime", reqtime);
		if(imagenewsList!=null){
			imagenewsList.put(source,map);
	
		}else{
			imagenewsList = new HashMap<String,Object>(); 
			imagenewsList.put(source,map);

		}
		//newsList = list;
	}
	public static void updateimageNewsList(List<Map<String, Object>> list,String source,String reqtime){
		if(imagenewsList!=null){
			Map<String,Object> map = (Map<String, Object>) imagenewsList.get(source);
			List<Map<String, Object>> savedList = (List<Map<String, Object>>) map.get("recommend");
			String savedtime = (String) map.get("reqtime");
			savedtime = reqtime;
			for(int i=0;i<list.size();i++){
				savedList.add(0,list.get(i));
			}
		}
	}
	
	public static Map<String, Object> gettextNewsList(String source){
		if(textnewsList!=null){
			return (Map<String, Object>) textnewsList.get(source);
		}
		return null;
	}
	public static void settextNewsList(List<Map<String, Object>> list,String source,String reqtime){
		Map<String,Object> map = new HashMap<String,Object>(); 	
		map.put("recommend", list);
		map.put("reqtime", reqtime);
		if(textnewsList!=null){
			textnewsList.put(source,map);
	
		}else{
			textnewsList = new HashMap<String,Object>(); 
			textnewsList.put(source,map);

		}
		//newsList = list;
	}
	public static void updatetextNewsList(List<Map<String, Object>> list,String source,String reqtime){
		if(textnewsList!=null){
			Map<String,Object> map = (Map<String, Object>) textnewsList.get(source);
			List<Map<String, Object>> savedList = (List<Map<String, Object>>) map.get("recommend");
			String savedtime = (String) map.get("reqtime");
			savedtime = reqtime;
			for(int i=0;i<list.size();i++){
				savedList.add(0,list.get(i));
			}
		}
	}
	
	public static void updateDownloadStatus(List<DataInfo> list,String id,int status){
		if(list!=null){
			for(int i=0;i<list.size();i++){
				DataInfo info = list.get(i);
				if(info.getId().equals(id)){
					info.setStatus(status);
				}
			}
		}
	}
	public static void updatePicDownloadStatus(List<DataInfo> list,String id,int status,String childid){
		if(list!=null){
			for(int i=0;i<list.size();i++){
				DataInfo info = list.get(i);
				if(info.getId().equals(id)){
					if(info.GetIsDir()!=null && info.GetIsDir().equals("1")){
						List<String> downloads = info.getImagesDownload();
						if(downloads!=null){
							if(status==0){
								for(int j=0;j<downloads.size();j++){
									String download = downloads.get(j);
									if(download.equals(childid)){
										System.out.println("remove j = " + j + "childid =" + childid);
										downloads.remove(j);
									}
								}
							}else if(status ==1){
								downloads.add(childid);
							}
						}
					}else{
						info.setStatus(status);
					}
				}
			}
		}
	}
	
	/**
	 * 获取所有安装的非系统应用
	 * @return
	 */
	public static List<String> getInstalledApp(Context context){
		/*
		if(installed!=null) return installed;
		else {
			installed = new ArrayList<String>();
		}
		*/
		installed = new ArrayList<String>();
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> list = pm.getInstalledPackages(0);
		 for(PackageInfo packageInfo : list){
			 if((packageInfo.applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==0){
				 //System.out.println(packageInfo.packageName);
				 installed.add(packageInfo.packageName);
			 }
		 }
		 return installed;
	}
	
	private static String createDir(Context context) {
		String dir=null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			dir=Environment.getExternalStorageDirectory().getAbsolutePath();
		}else {
			dir=context.getFilesDir().toString();
		}
		return dir;
	}
	private static boolean DEBUG = false;
	public static void printLog(Context context,String value){
		if(!DEBUG) return ;		
		try {
			
			SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日HH时");
			SimpleDateFormat format2=new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
			Date date = new Date(System.currentTimeMillis());
			String fileName = format.format(date);
			//String dividerLine="\n\n\n\n====================="+format2.format(date)+"=====================\n";
			
			final Calendar c = Calendar.getInstance(); 
    		int mYear = c.get(Calendar.YEAR); // 年
    		int mMonth = c.get(Calendar.MONTH) + 1;// 月
    	    int mDate = c.get(Calendar.DATE);
    	    int mHour = c.get(Calendar.HOUR);
    	    int mMinute = c.get(Calendar.MINUTE);
    	    int mSecont = c.get(Calendar.SECOND);
    	    int mMilean = c.get(Calendar.MILLISECOND);
    	    String dividerLine="\n\n\n\n=============="+mYear+"-"+mMonth+"-"+ mDate +" " + mHour + ":" + mMinute + ":" + mSecont + " " + mMilean+"=====================\n";
			
			File logFile=new File(createDir(context), "udisk/log/"+fileName+".txt");
			logFile.getParentFile().mkdirs();
			logFile.createNewFile();
			FileWriter writer=new FileWriter(logFile,true);
			
			StringBuilder sb=new StringBuilder(dividerLine);
			sb.append(value).append("\n");
			writer.write(sb.toString());
			writer.close();
		} catch (IOException e2) {
			Log.e("test", e2.getMessage());
		}
		
	}
	public static void printLog(Context context,Exception e){
			if(!DEBUG) return ;		
			try {
				
				SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日HH时");
				SimpleDateFormat format2=new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
				Date date = new Date(System.currentTimeMillis());
				String fileName = format.format(date);
				//String dividerLine="\n\n\n\n====================="+format2.format(date)+"=====================\n";
				
				final Calendar c = Calendar.getInstance(); 
	    		int mYear = c.get(Calendar.YEAR); // 年
	    		int mMonth = c.get(Calendar.MONTH) + 1;// 月
	    	    int mDate = c.get(Calendar.DATE);
	    	    int mHour = c.get(Calendar.HOUR);
	    	    int mMinute = c.get(Calendar.MINUTE);
	    	    int mSecont = c.get(Calendar.SECOND);
	    	    int mMilean = c.get(Calendar.MILLISECOND);
	    	    String dividerLine="\n\n\n\n=============="+mYear+"-"+mMonth+"-"+ mDate +" " + mHour + ":" + mMinute + ":" + mSecont + " " + mMilean+"=====================\n";
				
				File logFile=new File(createDir(context), "udisk/log/"+fileName+".txt");
				logFile.getParentFile().mkdirs();
				logFile.createNewFile();
				FileWriter writer=new FileWriter(logFile,true);
				
				StringBuilder sb=new StringBuilder(dividerLine);
				/*
				sb.append(value).append("\n");
				writer.write(sb.toString());
				*/
				sb.append(e.toString()).append("\n");
				StackTraceElement[] stackTrace = e.getStackTrace();
				final int N=stackTrace.length;
				for (int i=0;i<N;i++) {
					sb.append(stackTrace[i].toString()).append("\n");
				}
				writer.close();
			} catch (IOException e2) {
				Log.e("test", e2.getMessage());
			}	
		}
    /**
     * Returns whether the network is available
     */
    public static boolean isNetworkAvailable(Context context,String address) {
        
    	
    	ConnectivityManager connectivity =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.w(TAG, "couldn't get connectivity manager");
            return false;
        } else {
        	NetworkInfo networkinfo = connectivity.getActiveNetworkInfo();
        	//networkinfo.getType();
        	if (networkinfo == null || !networkinfo.isAvailable() ||!networkinfo.isConnectedOrConnecting()) {
                return false;
        	}else{
        		if(address!=null && address.length()>0){
        			if(openUrl(address)){////正常
        				return true;
        			}else{
        				return false;
        			}
        		}
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

    
	public static void printCurTime(String flag){
		
		final Calendar c = Calendar.getInstance(); 
		int mYear = c.get(Calendar.YEAR); // 年
		int mMonth = c.get(Calendar.MONTH) + 1;// 月
	    int mDate = c.get(Calendar.DATE);
	    int mHour = c.get(Calendar.HOUR);
	    int mMinute = c.get(Calendar.MINUTE);
	    int mSecont = c.get(Calendar.SECOND);
	    int mMilean = c.get(Calendar.MILLISECOND);
	    System.out.println(flag + " " + mYear+"-"+mMonth+"-"+ mDate +" " + mHour + ":" + mMinute + ":" + mSecont + " " + mMilean );
		
	}
	public static String getSpaceStr(Long size){
		String usedMB ="0";
		if(size<0){
			usedMB = "0Byte";
		}
		else if(size <1024){
			usedMB = size+"Byte";
		}else if(size <1024*1024){
			double used = (double)size/(1024);
			usedMB =   (double) (Math.round(used*100)/100.0) + "KB";
		}else if(size <1024*1024*1024){
			double used = (double)size/(1024*1024);
			usedMB =  (double) (Math.round(used*100)/100.0) + "MB";
		}else {
			double used = (double)size/(1024*1024*1024);
			/*
			String temp = String.valueOf(used);
			int index = temp.indexOf(".");
			if(index >0){
				usedMB = temp.substring(0,index+3) + "GB";
			}
			*/
			usedMB = (double) (Math.round(used*100)/100.0) + "GB";
			//System.out.println("used = " + used + "usedMB = " + usedMB);
		}
		return usedMB;
	}
}
