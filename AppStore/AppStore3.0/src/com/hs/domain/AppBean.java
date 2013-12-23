package com.hs.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.database.Cursor;

/**
 *
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2012-12-26
 */
public class AppBean {
	
	private int id;
	private int percent;
	private int startpos;
	private int status;
	private int downloadStatus;
	private long ignored;
	private String apkName;
	private String title;
	private String url;
	private String image;
	private String version;
	private String newVersion;
	private String packageName;
	private String size;
	private String installTime;
	
	private String rating;
	private String type;
	private String author;
	private String description;
	private String ctitle;
	private int total;
	private int runType;
	
	
	public int getRunType() {
		return runType;
	}
	public void setRunType(int runType) {
		this.runType = runType;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCtitle() {
		return ctitle;
	}
	public void setCtitle(String ctitle) {
		this.ctitle = ctitle;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getNewVersion() {
		return newVersion;
	}
	public void setNewVersion(String newVersion) {
		this.newVersion = newVersion;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPercent() {
		return percent;
	}
	public void setPercent(int percent) {
		this.percent = percent;
	}
	public int getStartpos() {
		return startpos;
	}
	public void setStartpos(int startpos) {
		this.startpos = startpos;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getDownloadStatus() {
		return downloadStatus;
	}
	public void setDownloadStatus(int downloadStatus) {
		this.downloadStatus = downloadStatus;
	}
	public long getIgnored() {
		return ignored;
	}
	public void setIgnored(long ignored) {
		this.ignored = ignored;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getInstallTime() {
		return installTime;
	}
	public void setInstallTime(String installTime) {
		this.installTime = installTime;
	}
	public String getApkName() {
		if(apkName==null&&url!=null&&url.length()>0){
			apkName = url.substring(url.lastIndexOf("/") + 1);
		}
		return apkName;
	}
	public void setApkName(String apkName) {
		this.apkName = apkName;
	}
	
	
	public static AppBean instance(JSONObject jsonObject){
		AppBean bean = new AppBean();
		bean.setId(jsonObject.optInt("id"));
		bean.setImage(jsonObject.optString("logo"));
		bean.setTitle(jsonObject.optString("title"));
		bean.setRating(jsonObject.optString("score"));
		bean.setVersion(jsonObject.optString("version"));
		bean.setUrl(jsonObject.optString("file_path"));
		bean.setType(jsonObject.optString("type", "app"));
		bean.setTotal(jsonObject.optInt("total"));
		bean.setCtitle(jsonObject.optString("channel"));
		return bean;
	}
	
	
	public static AppBean instance(Cursor cursor){
		AppBean bean = new AppBean();
		bean.setId(cursor.getInt(cursor.getColumnIndex("id")));
		bean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
		bean.setPercent(cursor.getInt(cursor.getColumnIndex("percent")));
		bean.setUrl(cursor.getString(cursor.getColumnIndex("url")));
		bean.setImage(cursor.getString(cursor.getColumnIndex("image")));
		bean.setStartpos(cursor.getInt(cursor.getColumnIndex("startpos")));
		bean.setStatus(cursor.getInt(cursor.getColumnIndex("stauts")));
		bean.setDownloadStatus(cursor.getInt(cursor.getColumnIndex("downloadstatus")));
		bean.setIgnored(cursor.getLong(cursor.getColumnIndex("ignored")));
		bean.setVersion(cursor.getString(cursor.getColumnIndex("version")));
		bean.setPackageName(cursor.getString(cursor.getColumnIndex("package")));
		bean.setSize(cursor.getString(cursor.getColumnIndex("filesize")));
		bean.setInstallTime(cursor.getString(cursor.getColumnIndex("installtime")));
		return bean;
	}
}
