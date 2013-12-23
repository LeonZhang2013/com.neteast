package com.neteast.androidclient.newscenter.domain;

import com.neteast.androidclient.newscenter.ConfigManager;
import com.neteast.androidclient.newscenter.utils.Utils;
import com.neteast.androidclient.newscenter.view.NewsWidget;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

class SysInformation extends Information {

    public SysInformation(Cursor cursor) {
        super(cursor);
    }
    
    public SysInformation(JSONObject jobj) {
        super(jobj);
    }
    
    @Override
    public String getTitle() {
        return "系统消息";
    }
    
    @Override
    public boolean needDrawTextColor() {
        return !TextUtils.isEmpty(url);
    }

    @Override
    public boolean editable() {
        return false;
    }


    
    @Override
    public void executeAction(Context context) {
        postClickInfo(context,infoId);
        super.executeAction(context);
    }
    /**
     * 上报点击链接
     * @param info
     */
    private void postClickInfo(final Context context,final long infoId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data="userid="+CloudAccount.getInstance(context).getUserId()+"&msg_id="+infoId;
                String verify=Utils.md5(data+ConfigManager.KEY);
                String reqstr=Utils.base64(data);
                String path="http://stat.cbb.wasu.com.cn:8360/Message/clickreport";
                DefaultHttpClient client = new DefaultHttpClient();
                ArrayList<NameValuePair> valuePairs=new ArrayList<NameValuePair>();
                BasicNameValuePair valuePair=new BasicNameValuePair("appcode",ConfigManager.APPCODE);
                valuePairs.add(valuePair);
                valuePair=new BasicNameValuePair("datatype","j");
                valuePairs.add(valuePair);
                valuePair=new BasicNameValuePair("verify",verify);
                valuePairs.add(valuePair);
                valuePair=new BasicNameValuePair("reqstr",reqstr);
                valuePairs.add(valuePair);
                UrlEncodedFormEntity reqEntity=null;
                try {
                    reqEntity = new UrlEncodedFormEntity(valuePairs,"UTF-8");
                } catch (UnsupportedEncodingException e) {}
                HttpPost httpPost=new HttpPost(path);
                httpPost.setEntity(reqEntity);
                try {
                    HttpEntity resEntity = client.execute(httpPost).getEntity();
                    Log.i("test", EntityUtils.toString(resEntity));
                } catch (ClientProtocolException e) {
                    Log.e("test", e.getMessage(),e);
                } catch (IOException e) {
                    Log.e("test", e.getMessage(),e);
                }
            }
        }).start();
    }


    @Override
    protected int getActionCode() {
        if (applicationId==InfoAction.ACTION_VIDEO_TRANSCODE) {
            return InfoAction.ACTION_VIDEO_TRANSCODE;
        }else {
            return InfoAction.ACTION_WEB;
        }
    }

    @Override
    protected int getInfoType() {
        return SYS_INFO;
    }

    @Override
    public void notifyHasNewInfo(Context context) {
        NewsWidget.getIntance(context).addSysNumsNum();
    }
}
