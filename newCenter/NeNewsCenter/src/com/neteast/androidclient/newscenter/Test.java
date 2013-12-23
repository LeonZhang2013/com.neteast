package com.neteast.androidclient.newscenter;

import com.neteast.androidclient.newscenter.domain.CloudAccount;
import com.neteast.androidclient.newscenter.domain.Contact;
import com.neteast.androidclient.newscenter.domain.Information;
import com.neteast.androidclient.newscenter.domain.SNSEntity;
import com.neteast.androidclient.newscenter.domain.SNSEntity.SnsType;
import com.neteast.androidclient.newscenter.provider.CloudAccountColumns;
import com.neteast.androidclient.newscenter.utils.LogUtil;
import com.neteast.androidclient.newscenter.view.NewsWidget;

import android.content.ContentResolver;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import java.util.ArrayList;

public class Test extends AndroidTestCase {
    
    public void testCloudCursor() {
        ContentResolver resolver = getContext().getContentResolver();
        Cursor cursor =resolver.query(
                CloudAccountColumns.CONTENT_URI, 
                null, 
                CloudAccountColumns.IS_CURRENT+"=?", 
                new String[]{"true"}, 
                CloudAccountColumns.DEFAULT_SORT_ORDER);
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
           int  userId=cursor.getInt(cursor.getColumnIndex(CloudAccountColumns.USERID));
           String  token=cursor.getString(cursor.getColumnIndex(CloudAccountColumns.TOKEN));
           String account=cursor.getString(cursor.getColumnIndex(CloudAccountColumns.ACCOUNT));
           long broadcastId=cursor.getLong(cursor.getColumnIndex(CloudAccountColumns.LAST_BROADCASTID));
           long unicastId=cursor.getLong(cursor.getColumnIndex(CloudAccountColumns.LAST_UNICASTID));
           LogUtil.i(userId+","+token+","+account+","+broadcastId+","+unicastId);
        }
    }
    
    public void testQuerySysInfos() {
        Cursor cursor = Information.queryInformation(getContext(), Information.SYS_INFO);
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            Information information = Information.parseCursor(cursor);
            LogUtil.i(information.toString());
         }
    }
    
    public void testNewsWidget() {
        NewsWidget.getIntance(getContext()).addAppNumsNum();
    }
    
    public void testCloudAccount() {
        CloudAccount.getInstance(getContext()).login(6907, "abdasda", "");
        CloudAccount.getInstance(getContext()).login(6107, "adasdasda", "");
        CloudAccount.getInstance(getContext()).login(6207, "abdasda", "");
        Cursor cursor = getContext().getContentResolver().query(CloudAccountColumns.CONTENT_URI, null, null, null, null);
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            int  userId=cursor.getInt(cursor.getColumnIndex(CloudAccountColumns.USERID));
            String  token=cursor.getString(cursor.getColumnIndex(CloudAccountColumns.TOKEN));
            String account=cursor.getString(cursor.getColumnIndex(CloudAccountColumns.ACCOUNT));
            long broadcastId=cursor.getLong(cursor.getColumnIndex(CloudAccountColumns.LAST_BROADCASTID));
            long unicastId=cursor.getLong(cursor.getColumnIndex(CloudAccountColumns.LAST_UNICASTID));
            LogUtil.i(userId+","+token+","+account+","+broadcastId+","+unicastId);
         }
    }
    
    public void testDeauthorize() {
        SNSEntity.getEntity(getContext(), SnsType.RenRen).deauthorize();
    }
    
    public void testCooike() {
        CookieSyncManager.createInstance(getContext());
        CookieManager instance = CookieManager.getInstance();
        String cookie = instance.getCookie("https://graph.renren.com/oauth/authorize");
        LogUtil.i("cookie="+cookie);
        cookie = instance.getCookie("https://open.t.qq.com/cgi-bin/oauth2/authorize");
        LogUtil.i("cookie="+cookie);
        cookie = instance.getCookie("https://api.weibo.com/oauth2/authorize");
        LogUtil.i("cookie="+cookie);
    }
    
    public void testJsonObject() {
        String ss=null;
        ArrayList<Contact> newInstances = Contact.newInstances(ss);
        LogUtil.i(newInstances.toString());
        
    }
}
