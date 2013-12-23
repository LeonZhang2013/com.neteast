package com.neteast.lib.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-1
 * Time: 下午4:09
 * To change this template use File | Settings | File Templates.
 */
public class DateUtils {
    public static final BiMap<Integer,String> DigitalChineseMonthDic;

    static {
        DigitalChineseMonthDic= HashBiMap.create();
        DigitalChineseMonthDic.put(1,"一月");
        DigitalChineseMonthDic.put(2,"二月");
        DigitalChineseMonthDic.put(3,"三月");
        DigitalChineseMonthDic.put(4,"四月");
        DigitalChineseMonthDic.put(5,"五月");
        DigitalChineseMonthDic.put(6,"六月");
        DigitalChineseMonthDic.put(7,"七月");
        DigitalChineseMonthDic.put(8,"八月");
        DigitalChineseMonthDic.put(9,"九月");
        DigitalChineseMonthDic.put(10,"十月");
        DigitalChineseMonthDic.put(11,"十一月");
        DigitalChineseMonthDic.put(12,"十二月");
    }

    public static String getChineseMonth(int digital){
        return DigitalChineseMonthDic.get(digital);
    }

    public static int getDigitalMonth(String chinese){
        return DigitalChineseMonthDic.inverse().get(chinese);
    }

    /**
     *
     * @param dateInfo 必须是yyyymmdd格式
     * @return
     */
    public static String getChineseMonthInfo(String dateInfo){
        int digitalMonthInfo = getDigitalMonthInfo(dateInfo);
        return getChineseMonth(digitalMonthInfo);
    }

    /**
     *
     * @param dateInfo 必须是yyyymmdd格式
     * @return
     */
    public static int getDigitalMonthInfo(String dateInfo){
        return Integer.valueOf(dateInfo.substring(4, 6));
    }
}
