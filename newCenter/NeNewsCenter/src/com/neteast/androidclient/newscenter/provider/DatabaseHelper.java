package com.neteast.androidclient.newscenter.provider;

import com.neteast.androidclient.newscenter.utils.LogUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    
    public static final String INFORMATIONS_TABLE_NAME = "informations";
    public static final String ACCOUNTS_TABLE_NAME = "accounts";
    private static final String DATABASE_NAME = "newscenter.db";
    private static final int DATABASE_VERSION = 2;
    
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        
        db.execSQL("CREATE TABLE " + ACCOUNTS_TABLE_NAME + " ("
                + CloudAccountColumns._ID + " INTEGER PRIMARY KEY,"
                + CloudAccountColumns.USERID + " INTEGER,"
                + CloudAccountColumns.TOKEN + " TEXT,"
                + CloudAccountColumns.ACCOUNT + " TEXT,"
                + CloudAccountColumns.IS_CURRENT + " TEXT,"
                + CloudAccountColumns.LAST_BROADCASTID + " INTEGER,"
                + CloudAccountColumns.LAST_UNICASTID + " INTEGER,"
                + CloudAccountColumns.DATA1 + " TEXT,"
                + CloudAccountColumns.DATA2 + " TEXT,"
                + CloudAccountColumns.DATA3 + " TEXT,"
                + CloudAccountColumns.DATA4 + " TEXT,"
                + CloudAccountColumns.DATA5 + " TEXT"
                + ");");
        
        db.execSQL("CREATE TABLE " + INFORMATIONS_TABLE_NAME + " ("
                + InformationColumns._ID + " INTEGER PRIMARY KEY,"
                + InformationColumns.INFO_ID + " INTEGER,"
                + InformationColumns.INFO_TYPE_ID + " INTEGER,"
                + InformationColumns.LIMIT_TIME + " INTEGER,"
                + InformationColumns.SEND_TIME + " INTEGER,"
                + InformationColumns.TEXTCONTENT + " TEXT,"
                + InformationColumns.URL + " TEXT,"
                + InformationColumns.PICTURE + " TEXT,"
                + InformationColumns.APPLICATION_ID + " INTEGER,"
                + InformationColumns.APPLICATION_NAME + " TEXT,"
                + InformationColumns.FROM_USER_ID + " INTEGER,"
                + InformationColumns.FROM_USER_NAME + " TEXT,"
                + InformationColumns.INTERACTIVE_INFO_TYPE + " INTEGER,"
                + InformationColumns.USERID + " INTEGER"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtil.e("升级数据库，从版本 " + oldVersion + " 到 "+ newVersion + ", 会销毁所有旧数据");
        db.execSQL("DROP TABLE IF EXISTS informations");
        db.execSQL("DROP TABLE IF EXISTS accounts");
        onCreate(db);
    }

}
