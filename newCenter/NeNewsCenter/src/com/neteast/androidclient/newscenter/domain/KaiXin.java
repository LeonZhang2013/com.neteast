package com.neteast.androidclient.newscenter.domain;

import com.neteast.androidclient.newscenter.exception.AuthorizeException;
import com.neteast.androidclient.newscenter.exception.SnsPublishException;
import com.neteast.androidclient.newscenter.utils.JsonHelper;
import com.neteast.androidclient.newscenter.utils.LogUtil;
import com.neteast.androidclient.newscenter.utils.Utils;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
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

final class KaiXin extends SNSEntity {
    
    public static SNSEntity getInstance(Context context) {
        if (sInstance==null) {
            sInstance=new KaiXin(context);
        }
        return sInstance;
    }
    
    public KaiXin(Context context) {
        super(context);
    }
    
    private static KaiXin sInstance;
    private static final String APP_KEY = "24684818301852b063570c2f20cbdd64";
    @SuppressWarnings("unused")
    private static final String APP_SECRET = "d9059f51b6f77c05c8b960332490e39e";
    private static final String REDIRECT_URI = "http://www.net-east.com/";
    private static final String AUTHORIZE = "http://api.kaixin001.com/oauth2/authorize?" 
                                                                            + "response_type=token" 
                                                                            + "&client_id=" + APP_KEY
                                                                            + "&scope=basic create_records" 
                                                                            + "&client=1" 
                                                                            + "&redirect_uri=" + REDIRECT_URI;
    
    private static final String CONFIG_NAME = "kaixin";
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
        return (getAccessToken() != null)
                && ((getExpiresIn() == 0) || (System.currentTimeMillis() < getExpiresIn()));
    }

    @Override
    public void publishMessage(String message, PublishListener listener) throws SnsPublishException {
        publishMessageWithPicture(message, null, listener);
    }

    @Override
    public void publishMessageWithPicture(String message, String filePath, PublishListener listener) throws SnsPublishException {
        Charset utf8 = Charset.forName("UTF-8");
        MultipartEntity reqEntity = new MultipartEntity();
        try {
            StringBody access_token = new StringBody(getAccessToken(),utf8);
            StringBody content = new StringBody(message, utf8);
            reqEntity.addPart("access_token", access_token);
            reqEntity.addPart("content", content);
            if (filePath != null) {
                FileBody pic = new FileBody(new File(filePath), "image/*");
                reqEntity.addPart("pic", pic);
            }
        } catch (UnsupportedEncodingException e) {}//不可能发生
        String api="https://api.kaixin001.com/records/add.json";
        
        String json=null;
        try {
            json= Utils.doHttpsPost(api, reqEntity);
        } catch (IOException e) {
            LogUtil.printException(e);
            throw new SnsPublishException("开心网发布失败\t网络异常");
        }
        processServerResponse(listener, json);
    }

    @Override
    protected void handleRedirectUrl(String url, AuthListener listener) {
        Bundle values = Utils.parseUrl(url);
        String accessToken = values.getString(ACCESS_TOKEN);
        String expires = values.getString(EXPIRES_IN);
        
        if (TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(expires)) {
            listener.onAuthorizeException(new AuthorizeException("开心网授权失败"));
        }else {
            setAccessToken(accessToken);
            setExpiresIn(expires);
            listener.onComplete();
        }
    }

    @Override
    protected String getRedirectUrl() {
        return REDIRECT_URI;
    }

    @Override
    protected String getAuthorizeUrl() {
        return AUTHORIZE;
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
        Editor editor = mContext.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE).edit();
        long expiresIn = System.currentTimeMillis() + Long.parseLong(expires)* 1000;
        editor.putLong(EXPIRES_IN, expiresIn);
        editor.commit();
    }
    
    private void processServerResponse(PublishListener listener, String json) throws SnsPublishException {
        JSONObject jobj=null;
        try {
            jobj=new JSONObject(json);
        } catch (JSONException e) {
            LogUtil.printException(e);
            throw new SnsPublishException("开心网发布失败\tjson解析失败");
        }
        long rid = JsonHelper.readLong(jobj, "rid");
        if (rid>0) {
            listener.onPublishComplete("开心网发布成功");
        }else {
            String error = JsonHelper.readString(jobj, "error");
            throw new SnsPublishException(error);
        }
    }
}
