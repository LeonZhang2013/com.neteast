package com.neteast.androidclient.newscenter.domain;

import com.neteast.androidclient.newscenter.exception.AuthorizeException;
import com.neteast.androidclient.newscenter.exception.SnsPublishException;
import com.neteast.androidclient.newscenter.utils.JsonHelper;
import com.neteast.androidclient.newscenter.utils.LogUtil;
import com.neteast.androidclient.newscenter.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

final class RenRen extends SNSEntity {
    
    public static SNSEntity getInstance(Context context) {
        if (sInstance==null) {
            sInstance=new RenRen(context);
        }
        return sInstance;
    }
    
    public RenRen(Context context) {
        super(context);
    }
    private static RenRen sInstance;
    private static final String APP_KEY = "7e3473bf828b4e159de5b6b7b9270d71";
    private static final String APP_SECRET = "4d0472839f3e4fce9c3eca2c39284c97";
    private static final String REDIRECT_URI = "http://sns.whalecloud.com";
    private static final String AUTHORIZE = "https://graph.renren.com/oauth/authorize?" 
                                                                        + "client_id="+ APP_KEY
                                                                        + "&scope=publish_share" 
                                                                        + "&redirect_uri="+ REDIRECT_URI
                                                                        + "&response_type=token";

    private static final String CONFIG_NAME = "renren";

    private static final String ACCESS_TOKEN = "access_token";
    private static final String EXPIRES_IN = "expires_in";

    @Override
    public void deauthorize() {
        Utils.clearSessionCookie(mContext);
        setAccessToken(null);
        setExpiresIn("0");
    }

    @Override
    public boolean isSessionValid() {
        return (!TextUtils.isEmpty(getAccessToken()) && (System.currentTimeMillis() < getExpiresIn()));
    }

    @Override
    public void publishMessage(String message, PublishListener listener) throws SnsPublishException {
        listener.onPublishComplete("");
    }

    @Override
    public void publishMessageWithPicture(String message, String url, PublishListener listener) throws SnsPublishException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("method", "share.share");
        params.put("v", "1.0");
        params.put("url", url);
        params.put("comment", message);
        params.put("access_token", getAccessToken());
        params.put("format", "JSON");
        params.put("type", "2");
        String signature = getSignature(params, APP_SECRET);
        
        ArrayList<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
        BasicNameValuePair valuePair = new BasicNameValuePair("sig", signature);
        valuePairs.add(valuePair);
        valuePair = new BasicNameValuePair("v", "1.0");
        valuePairs.add(valuePair);
        valuePair = new BasicNameValuePair("access_token", getAccessToken());
        valuePairs.add(valuePair);
        valuePair = new BasicNameValuePair("format", "JSON");
        valuePairs.add(valuePair);
        valuePair = new BasicNameValuePair("method", "share.share");
        valuePairs.add(valuePair);
        valuePair = new BasicNameValuePair("type", "2");
        valuePairs.add(valuePair);
        valuePair = new BasicNameValuePair("url", url);
        valuePairs.add(valuePair);
        valuePair = new BasicNameValuePair("comment", message);
        valuePairs.add(valuePair);
        
        UrlEncodedFormEntity reqEntity=null;
        try {
            reqEntity= new UrlEncodedFormEntity(valuePairs, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {}//不可能发送，必然支持
        String json=null;
        try {
            json= Utils.doPost("http://api.renren.com/restserver.do", reqEntity);
        } catch (IOException e) {
            LogUtil.printException(e);
            throw new SnsPublishException("人人网发布失败\t网络异常");
        }
        processServerResponse(listener, json);
    }
    
    @Override
    protected void handleRedirectUrl(String url, AuthListener mListener) {
        Bundle values = Utils.parseUrl(url);
        
        String error = values.getString("error");
        String errorDescription = values.getString("error_description");
        
        if (!TextUtils.isEmpty(error)) {
            mListener.onAuthorizeException(new AuthorizeException(error));
            LogUtil.printLog("RenRen(67)--->"+errorDescription);
            return;
        }

        setAccessToken(values.getString(ACCESS_TOKEN));
        setExpiresIn(values.getString(EXPIRES_IN));
        
        mListener.onComplete();
    }

    @Override
    protected String getRedirectUrl() {
        return REDIRECT_URI;
    }

    @Override
    protected String getAuthorizeUrl() {
        return AUTHORIZE;
    }
    
    private void setAccessToken(String accessToken) {
        Editor edit = mContext.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE).edit();
        edit.putString(ACCESS_TOKEN, accessToken);
        edit.commit();
    }
    
    private String getAccessToken() {
        SharedPreferences sp = mContext.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        return sp.getString(ACCESS_TOKEN, null);
    }
    
    private void setExpiresIn(String expires) {
        Editor edit = mContext.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE).edit();
        long expiresIn=System.currentTimeMillis()+Long.parseLong(expires)*1000;
        edit.putLong(EXPIRES_IN, expiresIn);
        edit.commit();
    }
    
    private long getExpiresIn() {
        SharedPreferences sp = mContext.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        return sp.getLong(EXPIRES_IN, 0);
    }
    
    /**
     * 计算签名
     * @param paramMap
     * @param secret
     * @return
     */
    public String getSignature(Map<String, String> paramMap, String secret) {
        List<String> paramList = new ArrayList<String>(paramMap.size());
        // 1、参数格式化
        for (Map.Entry<String, String> param : paramMap.entrySet()) {
            paramList.add(param.getKey() + "=" + param.getValue());
        }
        // 2、排序并拼接成一个字符串
        Collections.sort(paramList);
        StringBuffer buffer = new StringBuffer();
        for (String param : paramList) {
            buffer.append(param);
        }
        // 3、追加script key
        buffer.append(secret);
        // 4、将拼好的字符串转成MD5值
        return Utils.md5(buffer.toString());
    }
    
    private void processServerResponse(PublishListener listener, String json) throws SnsPublishException {
        JSONObject jobj=null;
        try {
            jobj=new JSONObject(json);
        } catch (JSONException e) {
            LogUtil.printException(e);
            throw new SnsPublishException("人人网发布失败\tjson解析失败");
        }
        String errorCode = JsonHelper.readString(jobj, "error_code");
        String errorMsg = JsonHelper.readString(jobj, "error_msg");
        
        if (!TextUtils.isEmpty(errorCode) && !TextUtils.isEmpty(errorMsg)) {
            listener.onPublishComplete("人人网发布成功");
        }else {
            LogUtil.printLog("人人网发布失败，errorCode="+errorCode+",errorMsg="+errorMsg);
            throw new SnsPublishException("人人网发布失败，"+getErrorMsg(errorCode));
        }
    }
    
    private String getErrorMsg(String key) {
        Properties prop=new Properties();
        try {
            prop.load(mContext.getAssets().open("renren_error_dic.properties"));
            return prop.getProperty(key);
        } catch (IOException e) {}
        return null;
    }
}
