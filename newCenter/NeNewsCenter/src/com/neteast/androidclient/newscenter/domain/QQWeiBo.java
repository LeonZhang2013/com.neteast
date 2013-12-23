package com.neteast.androidclient.newscenter.domain;

import com.neteast.androidclient.newscenter.exception.AuthorizeException;
import com.neteast.androidclient.newscenter.exception.SnsPublishException;
import com.neteast.androidclient.newscenter.utils.JsonHelper;
import com.neteast.androidclient.newscenter.utils.LogUtil;
import com.neteast.androidclient.newscenter.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Properties;

final class QQWeiBo extends SNSEntity {
    
    public static SNSEntity getInstance(Context context) {
        if (sInstance==null) {
            sInstance=new QQWeiBo(context);
        }
        return sInstance;
    }
    
    public QQWeiBo(Context context) {
        super(context);
    }
    
    private static QQWeiBo sInstance;
    private static final String APP_KEY = "801130933";
    @SuppressWarnings("unused")
    private static final String APP_SECRET = "fb9dd467927353736b399c9928de054e";
    private static final String REDIRECT_URI = "http://www.net-east.com";
    private static final String AUTHORIZE = "https://open.t.qq.com/cgi-bin/oauth2/authorize?" 
                                                                            +  "client_id="+ APP_KEY 
                                                                            + "&response_type=token" 
                                                                            + "&redirect_uri=" + REDIRECT_URI;
    
    private static final String IP = "202.106.18.197";

    private static final String CONFIG_NAME = "qqweibo";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String EXPIRES_IN = "expires_in";
    private static final String OPEN_ID = "openid";
    private static final String OPEN_KEY = "openkey";
    
    @Override
    public void deauthorize() {
        Utils.clearSessionCookie(mContext);
        setAccessToken(null);
        setExpiresIn("0");
        setOpenid(null);
        setOpenkey(null);
    }

    @Override
    public boolean isSessionValid() {
        return ( !TextUtils.isEmpty(getAccessToken() ) 
                && ( System .currentTimeMillis() < getExpiresIn() ) );
    }
    
    @Override
    public void publishMessage(String message,PublishListener listener) throws SnsPublishException{
        String api = "https://open.t.qq.com/api/t/add";
        
        ArrayList<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
        BasicNameValuePair valuePair = new BasicNameValuePair("format", "json");
        valuePairs.add(valuePair);
        valuePair = new BasicNameValuePair("content", message);
        valuePairs.add(valuePair);
        valuePair = new BasicNameValuePair("clientip", IP);
        valuePairs.add(valuePair);
        valuePair = new BasicNameValuePair("clientip", IP);
        valuePairs.add(valuePair);
        valuePair = new BasicNameValuePair("access_token", getAccessToken());
        valuePairs.add(valuePair);
        valuePair = new BasicNameValuePair("oauth_consumer_key", APP_KEY);
        valuePairs.add(valuePair);
        valuePair = new BasicNameValuePair("openid", getOpenid());
        valuePairs.add(valuePair);
        valuePair = new BasicNameValuePair("oauth_version", "2.a");
        valuePairs.add(valuePair);
        UrlEncodedFormEntity reqEntity=null;
        try {
            reqEntity= new UrlEncodedFormEntity(valuePairs, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {}//不可能发送，必然支持
        
        String json=null;
        try {
            json = Utils.doHttpsPost(api, reqEntity);
        } catch (IOException e) {
            throw new SnsPublishException("腾讯微博发布失败\t网络异常");
        }
        
        processServerResponse(listener, json);
    }
    
    @Override
    public void publishMessageWithPicture(String message, String filePath,PublishListener listener) throws SnsPublishException {
        String api = "https://open.t.qq.com/api/t/add_pic";
        
        MultipartEntity reqEntity = new MultipartEntity();
        try {
            Charset utf8 = Charset.forName("UTF-8");
            StringBody format = new StringBody("json", utf8);
            StringBody content = new StringBody(message, utf8);
            StringBody clientip = new StringBody(IP, utf8);
            StringBody access_token=new StringBody(getAccessToken(), utf8);
            StringBody oauth_consumer_key=new StringBody(APP_KEY, utf8);
            StringBody openid=new StringBody(getOpenid(), utf8);
            StringBody oauth_version=new StringBody("2.a", utf8);
            
            FileBody pic = new FileBody(new File(filePath), "image/*");
            reqEntity.addPart("format", format);
            reqEntity.addPart("content", content);
            reqEntity.addPart("clientip", clientip);
            reqEntity.addPart("access_token", access_token);
            reqEntity.addPart("oauth_consumer_key", oauth_consumer_key);
            reqEntity.addPart("openid", openid);
            reqEntity.addPart("oauth_version", oauth_version);
            reqEntity.addPart("pic", pic);
        } catch (UnsupportedEncodingException e) {}//不可能发生
        
        String json = null;
        try {
            json=Utils.doHttpsPost(api, reqEntity);
        } catch (IOException e) {
            LogUtil.printException(e);
            throw new SnsPublishException("腾讯微博发布失败\t网络异常");
        }
        processServerResponse(listener, json);
    }

    @Override
    protected void handleRedirectUrl(String url, AuthListener mListener) {
        if ( url==null || url.indexOf(ACCESS_TOKEN) == -1){
            mListener.onAuthorizeException(new AuthorizeException("腾讯微博授权失败"));
            return;
        }
        Bundle values = Utils.parseUrl(url);
        setAccessToken(values.getString(ACCESS_TOKEN));
        setExpiresIn(values.getString(EXPIRES_IN));
        setOpenid(values.getString(OPEN_ID));
        setOpenkey(values.getString(OPEN_KEY));
        
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
    
    @SuppressWarnings("unused")
    private String getErrorMsg(String key) {
        Properties prop=new Properties();
        try {
            prop.load(mContext.getAssets().open("qq_error_dic.properties"));
            return prop.getProperty(key);
        } catch (IOException e) {}
        return null;
    }
    
    private String getAccessToken() {
        SharedPreferences sp = mContext.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        return sp.getString(ACCESS_TOKEN, null);
    }

    private void setAccessToken(String accessToken) {
        Editor editor = mContext.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(ACCESS_TOKEN, accessToken);
        editor.commit();
    }

    private long getExpiresIn() {
        SharedPreferences sp = mContext.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        return sp.getLong(EXPIRES_IN, 0);
    }

    private void setExpiresIn(String expires) {
        long expiresIn = System.currentTimeMillis() + Integer.parseInt(expires)* 1000;
        Editor editor = mContext.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(EXPIRES_IN, expiresIn);
        editor.commit();
    }

    private String getOpenid() {
        SharedPreferences sp = mContext.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        return sp.getString(OPEN_ID, null);
    }

    private void setOpenid(String openid) {
        Editor editor = mContext.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(OPEN_ID, openid);
        editor.commit();
    }

    @SuppressWarnings("unused")
    private String getOpenkey() {
        SharedPreferences sp = mContext.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        return sp.getString(OPEN_KEY, null);
    }

    private void setOpenkey(String openkey) {
        Editor editor = mContext.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(OPEN_KEY, openkey);
        editor.commit();
    }
    /**
     * 处理服务器返回的json数据
     * @param listener
     * @param json
     * @throws SnsPublishException
     */
    private void processServerResponse(PublishListener listener, String json) throws SnsPublishException {
        JSONObject jobj=null;
        try {
            jobj = new JSONObject(json);
        } catch (JSONException e) {
            LogUtil.printException(e);
            throw new SnsPublishException("腾讯微博发布失败\tjson解析失败");
        }
        int ret = JsonHelper.readInt(jobj, "ret");
        
        if (ret!=0) {
            int errcode = JsonHelper.readInt(jobj,"errcode");
            throw new SnsPublishException("腾讯微博发布失败\terrcode="+errcode);
        }
        //发布成功
        listener.onPublishComplete("腾讯微博发布成功");
    }
}
