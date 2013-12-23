package com.hs.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;

import com.hs.domain.AppBean;
import com.hs.params.Params;
import com.lib.log.MyLog;
import com.lib.net.WeiboException;

public class AppDao {
	private static SqliteHandler db;
	private static AppDao mDao;
	
	private AppDao(Context context) {
		MyLog.writeLog("AppDao-1111111111");
		db = new SqliteHandler(context, Params.DB_NAME, null, Params.DB_VERSION);
		MyLog.writeLog("AppDao-2222222222");
	}
	
	/**
	 * 创建数据库
	 * @param context
	 */
	public static AppDao getInstance(Context context) {
		if (mDao == null) {
			mDao = new AppDao(context);
		}
		return mDao;
	}
	

	/**
	 *  获取数据库实例
	 * @return
	 */
	public static AppDao getDao() {
		if (mDao != null) {
			return mDao;
		} else {
			return null;
		}
	}

	/** 
	 * 根据状态查询数据
	 * @param status 1为安装  0下载完成 未安装
	 * @param downloadstatus 0 下载完毕 1正在下载。
	 * @return */
	public List<AppBean> getAppByStatus(int status, int downloadstatus) {
		Cursor cursor = null;
		List<AppBean> list = new ArrayList<AppBean>();
		try {
			String sql = "select * from app_list where stauts=? and downloadstatus=? group by id";
			cursor = db.getReadableDatabase().rawQuery(sql, new String[] { status + "", downloadstatus + "" });
			list = convertCursor2List(cursor);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
				cursor = null;
			}
		}
		return list;
	}

	/** 根据状态获取本地数据库安装的应用程序
	 * 
	 * @param status 1 表示安装 0表示未安装
	 * @param downloadstatus
	 * @return */
	public List<AppBean> getAppByStatus(int status) {
		Cursor cursor = null;
		List<AppBean> list = new ArrayList<AppBean>();
		try {
			String sql = "select * from app_list where stauts=? group by id order by installtime desc";
			cursor = db.getReadableDatabase().rawQuery(sql, new String[] { String.valueOf(status) });
			list = convertCursor2List(cursor);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
				cursor = null;
			}
		}
		return list;
	}

	/** 
	 * 获取所有下载，或下载完 未安装，以及更新升级的App
	 * @return */
	public List<AppBean> getDownLoadApp() {
		List<AppBean> list = new ArrayList<AppBean>();
		Cursor cursor = null;
		try {										   
			String sql = "select * from app_list where stauts=? ";
			cursor = db.getReadableDatabase().rawQuery(sql, new String[]{"0"});
			list = convertCursor2List(cursor);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
				cursor = null;
			}
		}
		return list;
	}
	
	/** 
	 * 获取所有下载未完成 或者更新未完成的程序。
	 * @return */
	public List<AppBean> getDownLoadingApp() {
		List<AppBean> list = new ArrayList<AppBean>();
		Cursor cursor = null;
		try {
			String sql = "select * from app_list where dowmloadstatus=?";
			cursor = db.getReadableDatabase().rawQuery(sql, new String[]{"1"});
			list = convertCursor2List(cursor);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
				cursor = null;
			}
		}
		return list;
	}

	/** 插入数据到数据库
	 * 
	 * @param appId
	 * @param appTitle
	 * @param url
	 * @param image
	 * @param percent
	 * @param startPos
	 * @param status 0 未安装   1 安装完成 
	 * @param downloadStatus 1 表示表示开始加载  0 下载完成
	 * @param version
	 * @param ignored 
	 * @param vernum */
/*	public void insertApp(int appId, String appTitle, String url, String image, int percent, int startPos, int status,
			int downloadStatus, String version, int ignored, String size, String packageName) {*/
	public void insertApp(AppBean bean){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String s = sdf.format(date);
		String sql = new StringBuffer("insert into app_list ")
		.append("(id,title,percent,url,image,startpos,stauts,downloadstatus,ignored,version,package,filesize,installtime) values(")
		.append(bean.getId()).append(",'").append(bean.getTitle()).append("',")
		.append(bean.getPercent()).append(",'").append(bean.getUrl()).append("','")
		.append(bean.getImage()).append("',").append(bean.getStartpos()).append(",")
		.append(bean.getStatus()).append(",").append(bean.getDownloadStatus()).append(",")
		.append(bean.getIgnored()).append(",'").append(bean.getVersion()).append("','")
		.append(bean.getPackageName()).append("','").append(bean.getSize()).append("','").append(s).append("')").toString();
		db.getWritableDatabase().execSQL(sql);
	}

	/**
	 * 解析数据
	 * @param cursor
	 * @return
	 */
	private List<AppBean> convertCursor2List(Cursor cursor) {
		List<AppBean> appList = new ArrayList<AppBean>();
		while (cursor.moveToNext()) {
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
			appList.add(bean);
		}
		return appList;
	}

	/**
	 * 更新数据库应用状态
	 * @param status
	 * @param downloadstatus
	 * @param appId
	 */
	public void updateAppStatus(int status, int downloadstatus, String packageName) {
		String sql = "update app_list set stauts=?,downloadstatus=? where package=?";
		db.getWritableDatabase().execSQL(sql, new Object[] { status, downloadstatus, packageName });
	}

	public Integer getAppIdByPackage(String packageName) {
		Integer id = null;
		String sql = "select id from app_list where package=?";
		Cursor cursor = null;
		try {
			cursor = db.getReadableDatabase().rawQuery(sql, new String[] { packageName });
			if (cursor.moveToFirst()) {
				id = cursor.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
				cursor = null;
			}
		}
		return id;
	}

	/**
	 * 更具id 删除数据库数据
	 * @param id
	 */
	public void deleteApp(int id) {
		String sql = "delete from app_list where id=?";
		db.getWritableDatabase().execSQL(sql, new Object[] { id });
	}
	
	/**
	 * 更具id 删除数据库数据
	 * @param id
	 */
	public void deleteApp(String packageName) {
		String sql = "delete from app_list where package=?";
		db.getWritableDatabase().execSQL(sql, new Object[] { packageName });
	}

	public void updateDownloadProgress(int appId, int rate, int hasRead) {
		String sql = "update app_list set startpos=?,percent=? where id=?";
		db.getWritableDatabase().execSQL(sql, new Object[] { hasRead, rate, appId });
	}

	/** 更新忽略状态
	 * 
	 * @param appId
	 * @param ignore */
	public void updateIgnoreStatus(int appId, long ignore) {
		String sql = "update app_list set ignored=? where id=?";
		db.getWritableDatabase().execSQL(sql, new Object[] { ignore, appId });

	}

	/** 批量更新忽略状态
	 * 
	 * @param appId
	 * @param ignore */
	public void updateIgnoreStatus(int[] appIds, Long ignore) {
		String sql = "update app_list set ignored=? where id in (";
		for (int i = 0; i < appIds.length; i++) {
			sql = sql + appIds[i];
			sql = sql + ",";
		}
		sql = sql.substring(0, sql.length() - 1);
		sql = sql + ")";
		db.getWritableDatabase().execSQL(sql, new Object[] { ignore });
	}

	/** 更新数据库App状态和版本
	 * 
	 * @param status 安装完成：1 
	 * @param downloadstatus  下载完成：2
	 * @param appId 应用id
	 * @param version 应用版本号
	 * @param size 应用大小
	 */
	public void updateAppStatusAndVersionById(int appId, int status, int downloadstatus, String version, String size) {
		String sql = "update app_list set stauts=?,downloadstatus=?,version=?,filesize=?,installtime=? where id=?";
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String s = sdf.format(date);
		db.getWritableDatabase().execSQL(sql, new Object[] { status, downloadstatus, version, size, s, appId });

	}

	/** 获取不包含状态的数据
	 * 
	 * @param downloadstatus
	 * @return */
	public List<AppBean> getAppExcludeDownloadstatus(int downloadstatus, int start, int end) {
		List<AppBean> list = new ArrayList<AppBean>();
		String sql = "select * from app_list where downloadstatus!=? group by id order by installtime desc limit ?,?";
		Cursor cursor = null;
		try {
			cursor = db.getReadableDatabase().rawQuery(sql, new String[] { downloadstatus + "", start + "", end + "" });
			list = convertCursor2List(cursor);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
				cursor = null;
			}
		}
		return list;
	}

	public void closeAll() {
		if (db != null) {
			db.close();
		}
	}

	public void updateAppStatusAndPackage(int status, int downloadStatus, String packageName, int appId) {
		String sql = "update app_list set stauts=?,downloadstatus=?,package=? where id=?";
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String s = sdf.format(date);
		db.getWritableDatabase().execSQL(sql, new Object[] { status, downloadStatus, packageName, appId });
	}
	
}
