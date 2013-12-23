package com.neteast.androidclient.newscenter.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonHelper {
    
    public static String readString(JSONObject jobj, String key){
        String dest="";
        if (!jobj.isNull(key)) {
            try {
                dest = jobj.getString(key).trim();
            } catch (JSONException e) {}
        } 
        if ("".equals(dest) || "null".equalsIgnoreCase(dest)||"{}".equals(dest)||"{ }".equals(dest)) {
            dest = "";
        }
        return dest;
    }
    
    public static boolean readBoolean(JSONObject jobj, String key){
        String result = readString(jobj, key).toLowerCase();
        try {
            int i = Integer.parseInt(result);
            return i==1;
        } catch (NumberFormatException e) {}
        
        return Boolean.getBoolean(result);
    }
    
    public static int readInt(JSONObject jobj, String key){
        String result = readString(jobj, key);
        try {
            return Integer.parseInt(result);
        } catch (NumberFormatException e) {}
        return 0;
    }
    
    public static float readFloat(JSONObject jobj, String key) {
        String result = readString(jobj, key);
        try {
            return Float.parseFloat(result);
        } catch (NumberFormatException e) {}
        return 0;
    }
    
    public static long readLong(JSONObject jobj, String key) {
        String result = readString(jobj, key);
        try {
            return Long.parseLong(result);
        } catch (NumberFormatException e) {}
        return 0;
    }
}
