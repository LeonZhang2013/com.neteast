package com.neteast.videotv.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-15
 * Time: 上午9:51
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NetEastVideoTV.db";
    private static final int DATABASE_VERSION = 4;



    public MySQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }
}
