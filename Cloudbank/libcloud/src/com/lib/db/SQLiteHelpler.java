package com.lib.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelpler extends SQLiteOpenHelper {

	final String sql = "create table clouddisk_table(id integer, resid integer,name text(50),"
			+ "url text(200), status integer,version text(10),package text(100))";
	final String upload_sql = "create table clouddisk_upload_table(id INTEGER PRIMARY KEY,fileid integer, type integer,name text(50),"
			+ "path text(200), status integer,progress integer,filesize integer,server text(100),isnew integer,slicedone integer,slicetotal integer)";

	/**
	 * @param context
	 * @param name 数据库名�??
	 * @param factory
	 * @param version 数据库版�??
	 */
	public SQLiteHelpler(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	} 

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sql);
		db.execSQL(upload_sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion != newVersion) {
			String sql = "alter table clouddisk_table add size real default 0";
			db.execSQL(sql); 
		}
	}

}
