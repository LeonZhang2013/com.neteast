package com.hs.db;

import com.lib.log.MyLog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/** 数据库
 * 
 * @author LeonZhang
 * @Email zhanglinlang1@163.com */
public class SqliteHandler extends SQLiteOpenHelper {
	/** title: 应用名称 percent: 下载百分比 url：下载地址 image: 图片地址： startpos： 上次下载位置 status: 应用状态 0 表示未安装 1表示已经安装 downloadstatus: 表示下载状态 1
	 * 正在下载 ，0 表示下载完成。 ignored 忽略时间，0表示存在 当前数据不需要升级。 大于0表示 */
	final String sql = "create table app_list(id INTEGER,title TEXT(50),"
			+ "percent INTEGER,url TEXT(200),image TEXT(200),startpos INTEGER, stauts INTEGER,downloadstatus INTEGER,"
			+ "ignored INTEGER,version TEXT,package TEXT(100),size REAL,installtime timestamp,filesize TEXT)";

	public SqliteHandler(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		MyLog.writeLog("SqliteHandler-1111111111");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		MyLog.writeLog("create db error");
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		MyLog.writeLog("oldVersion = " + oldVersion + "   newVersion = " + newVersion);
		try {
			String sql = "alter table app_list add COLUMN filesize TEXT";
			db.execSQL(sql);
		} catch (Exception e) {
			MyLog.writeLog("alter table error");
		}
	}
}
