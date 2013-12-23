package com.neteast.videotv.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import com.google.common.collect.Lists;
import com.neteast.lib.bean.MenuRaw;
import com.neteast.videotv.TVApplication;
import nl.qbusict.cupboard.QueryResultIterable;

import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-16
 * Time: 下午1:40
 */
public class MenuDao {

    public static final String API_MENU_DOC= TVApplication.API_MENU_DOC;

    public static List<Menu> getAllMenu(SQLiteDatabase db){
        QueryResultIterable<Menu> menus = cupboard().withDatabase(db).query(Menu.class).orderBy("currentIndex").query();
        return Lists.newArrayList(menus);
    }

    public static void addMenu(SQLiteDatabase db,Menu menu){
        cupboard().withDatabase(db).put(menu);
    }

    public static void deleteAll(SQLiteDatabase db){
        cupboard().withDatabase(db).delete(Menu.class,null,null);
    }

    public static void updateDefaultIndex(SQLiteDatabase db,Menu menu){
        ContentValues values=new ContentValues();
        values.put("defaultIndex",menu.getDefaultIndex());
        cupboard().withDatabase(db).update(Menu.class, values, "menuId=?", String.valueOf(menu.getMenuId()));
    }

    public static void updateCurrentIndex(SQLiteDatabase db,Menu menu){
        ContentValues values=new ContentValues();
        values.put("currentIndex",menu.getCurrentIndex());
        cupboard().withDatabase(db).update(Menu.class, values, "menuId=?", String.valueOf(menu.getMenuId()));
    }

    public static boolean exist(SQLiteDatabase db,Menu menu){
        Menu result = cupboard().withDatabase(db).query(Menu.class).withSelection("menuId=? and action=?", String.valueOf(menu.getMenuId()),menu.getAction()).get();
        if (result!=null){
            menu.setCurrentIndex(result.getCurrentIndex());
            return true;
        }else {
            return false;
        }
    }

    public static Menu convert(int defaultIndex,MenuRaw raw){
        Menu menu=new Menu();
        menu.setMenuId(raw.getMenuId());
        menu.setTitle(raw.getTitle());
        menu.setAction(String.format(API_MENU_DOC,raw.getKeywords()));
        menu.setDefaultIndex(defaultIndex);
        menu.setCurrentIndex(defaultIndex);
        return menu;
    }

}
