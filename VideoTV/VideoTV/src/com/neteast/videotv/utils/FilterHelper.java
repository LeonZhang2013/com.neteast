package com.neteast.videotv.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-14
 * Time: 下午1:58
 */
public class FilterHelper {


    public static final String FILTER_CONFIG = "FilterConfig";
    public static final String FILTER_TYPES = "FilterTypes";
    public static final String FILTER_LABELS = "FilterLabels";

    public static void saveFilterItems(Context context,String types,String labels){
        SharedPreferences.Editor editor =
                context.getSharedPreferences(
                FILTER_CONFIG,
                Context.MODE_PRIVATE).edit();

        editor.putString(FILTER_TYPES,types);
        editor.putString(FILTER_LABELS,labels);
        editor.commit();
    }

    public static String[] getSavedFilterItems(Context context){

        SharedPreferences sp = context.getSharedPreferences(FILTER_CONFIG, Context.MODE_PRIVATE);

        String types = sp.getString(FILTER_TYPES, null);
        String labels = sp.getString(FILTER_LABELS, null);

        if (types==null || labels==null){
            return null;
        }

        String[] params=new String[2];
        params[0] = types;
        params[1] = labels;
        return params;
    }

    public static void clear(Context context){
        SharedPreferences.Editor editor =
                context.getSharedPreferences(FILTER_CONFIG, Context.MODE_PRIVATE).edit();
        editor.clear().commit();
    }
}
