package com.neteast.videotv.dao;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

import java.util.List;

import nl.qbusict.cupboard.QueryResultIterable;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.Lists;
import com.neteast.lib.bean.MenuRaw;
import com.neteast.lib.bean.VideoRaw;
import com.neteast.videotv.TVApplication;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-16
 * Time: 下午1:40
 */
public class VideoRawDao {

    public static final String API_MENU_DOC= TVApplication.API_MENU_DOC;

    public static List<VideoRaw> getAll(SQLiteDatabase db){
        QueryResultIterable<VideoRaw> menus = cupboard().withDatabase(db).query(VideoRaw.class).query();
        return Lists.newArrayList(menus);
    }

    public static void add(SQLiteDatabase db,VideoRaw videoRaw){
        cupboard().withDatabase(db).put(videoRaw);
    }
    
    public static void addAll(SQLiteDatabase db,List<VideoRaw> videoRaws){
    	if(videoRaws==null) return;
    	for(int i=0; i<videoRaws.size(); i++){
    		add(db,videoRaws.get(i));
    	}
    }

    public static void deleteAll(SQLiteDatabase db){
        cupboard().withDatabase(db).delete(VideoRaw.class,null,null);
    }

}
