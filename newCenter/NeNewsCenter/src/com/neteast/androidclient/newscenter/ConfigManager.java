package com.neteast.androidclient.newscenter;

import com.neteast.androidclient.newscenter.domain.CloudAccount;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public final class ConfigManager {
    
    public static final String APPCODE = "10002";
    
    public static final String KEY = "9cf1259a625fd96d";
    
    /**用户中心地址*/
    public static String URL_USERCENTER="http://mng.wasu.com.cn:8360";

    /**bootstrap服务器*/
    public static String BOOTSTRAP_SERVER_IP="aolmsg.wasu.com.cn";
    public static int  BOOTSTRAP_SERVER_PORT=8087;
    
    
    /**意图，登录用户发生改变*/
    public static final String ACTION_LOGIN_CHANGED = "com.neteast.newscenter.login.success";
    /**意图，控制消息中心桌面图标的service*/
    public static final String ACTION_WIDGET_SERVICE = "neteast.newscenter.service.widget";
    /**意图，于消息服务器保持UDP通信，接受消息的service*/
    public static final String ACTION_RECEIVE_INFO_SERVICE = "neteast.newscenter.service.receiveInfo";
    
    
    
    
    
    private static final String SP_FILE_NAME="newscenter.sp";
    private static final String SP_AUTO_CLOSE_SUFFIX="_autoClose";
    private static final String SP_NOTIFY_BROADCAST_SUFFIX="_notifyBroadcast";
    private static final String SP_DELETE_CONFIRM_SUFFIX="_deleteConfirm";
    /**
     * 设置清空消息是否自动关闭消息列表
     * @param context
     * @param autoClose
     */
    public static void setAutoClose(Context context, boolean autoClose) {
        Editor edit  = context.getSharedPreferences(SP_FILE_NAME,Context.MODE_PRIVATE).edit();
        edit.putBoolean(CloudAccount.getInstance(context).getUserId()+SP_AUTO_CLOSE_SUFFIX, autoClose);
        edit.commit();
    }

    /**
     * 获取清空消息是否自动关闭消息列表
     * @param context
     * @return
     */
    public static boolean isAutoClose(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME,Context.MODE_PRIVATE);
        return sp.getBoolean(CloudAccount.getInstance(context).getUserId()+SP_AUTO_CLOSE_SUFFIX, false);
    }

    /**
     * 设置广播消息是否提示
     * @param context
     * @param notify
     */
    public static void setNotifyBroadcast(Context context, boolean notify) {
        Editor edit  = context.getSharedPreferences(SP_FILE_NAME,Context.MODE_PRIVATE).edit();
        edit.putBoolean(CloudAccount.getInstance(context).getUserId()+SP_NOTIFY_BROADCAST_SUFFIX, notify);
        edit.commit();
    }

    /**
     * 广播消息是否提示
     * @param context
     * @return
     */
    public static boolean isNotifyBroadcast(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME,Context.MODE_PRIVATE);
        return sp.getBoolean(CloudAccount.getInstance(context).getUserId()+SP_NOTIFY_BROADCAST_SUFFIX, true);
    }

    /**
     * 设置删除消息时是否进一步确认。
     * @param context
     * @param confirm
     */
    public static void setDeleteConfirm(Context context, boolean confirm) {
        Editor edit  = context.getSharedPreferences(SP_FILE_NAME,Context.MODE_PRIVATE).edit();
        edit.putBoolean(CloudAccount.getInstance(context).getUserId()+SP_DELETE_CONFIRM_SUFFIX, confirm);
        edit.commit();
    }

    /**
     * 获取删除消息时是否进一步弹出对话框确认
     * 
     * @param context
     * @return
     */
    public static boolean isDeleteConfirm(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME,Context.MODE_PRIVATE);
        return sp.getBoolean(CloudAccount.getInstance(context).getUserId()+SP_DELETE_CONFIRM_SUFFIX, false);
    }
}
