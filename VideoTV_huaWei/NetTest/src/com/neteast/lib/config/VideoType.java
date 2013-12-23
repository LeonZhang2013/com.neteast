package com.neteast.lib.config;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-1
 * Time: 上午11:27
 * To change this template use File | Settings | File Templates.
 */
public class VideoType {
    public static final int MOVIE = 1;
    public static final int TV = 2;
    public static final int CARTOON = 3;
    public static final int VARIETY = 4;
    public static final int ENTERTAINMENT = 5;
    public static final int FUNNY = 6;
    public static final int SPORTS = 10;
    public static final int NEWS = 11;
    public static final int RECORD = 12;
    public static final int MUSIC = 13;
    public static final int SHORT_VIDEO = 14;

    private static final BiMap<Integer,String> sTypeNameDic;

    static {
        sTypeNameDic=HashBiMap.create();
        sTypeNameDic.put(MOVIE,"电影");
        sTypeNameDic.put(TV,"电视剧");
        sTypeNameDic.put(CARTOON,"动漫");
        sTypeNameDic.put(VARIETY,"综艺");
        sTypeNameDic.put(ENTERTAINMENT,"娱乐");
        sTypeNameDic.put(FUNNY,"搞笑");
        sTypeNameDic.put(SPORTS,"体育");
        sTypeNameDic.put(NEWS,"新闻");
        sTypeNameDic.put(RECORD,"纪录片");
        sTypeNameDic.put(MUSIC,"音乐");
        sTypeNameDic.put(SHORT_VIDEO,"短视频");
    }

   public static final String getNameByType(int type){
       return sTypeNameDic.get(type);
   }

   public static final int getTypeByName(String name){
       return sTypeNameDic.inverse().get(name);
   }
}
