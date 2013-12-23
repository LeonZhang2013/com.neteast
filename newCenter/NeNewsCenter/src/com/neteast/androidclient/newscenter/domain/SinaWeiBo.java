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

final class SinaWeiBo extends SNSEntity {
    
    public static SNSEntity getInstance(Context context) {
        if (sInstance==null) {
            sInstance=new SinaWeiBo(context);
        }
        return sInstance;
    }
    
    public SinaWeiBo(Context context) {
        super(context);
    }
    
    private static SinaWeiBo sInstance;
    private static final String APP_KEY = "3353932868";
    @SuppressWarnings("unused")
    private static final String APP_SECRET = "50955df914859f99aadf77f4455ea548";
    private static final String REDIRECT_URI = "http://www.net-east.com/";
    private static final String AUTHORIZE = "https://api.weibo.com/oauth2/authorize?" 
    		                                                            + "client_id="+ APP_KEY
                                                                        + "&redirect_uri="+ REDIRECT_URI
                                                                        + "&response_type=token";
    private static final String CONFIG_NAME = "sina";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String EXPIRES_IN = "expires_in";
    private static final String UID = "uid";

    @Override
    public void deauthorize() {
        Utils.clearSessionCookie(mContext);
        setAccessToken(null);
        setgetExpiresIn("0");
        setUID(null);
    }

    @Override
    public boolean isSessionValid() {
        return (!TextUtils.isEmpty(getAccessToken()) && (getExpiresIn() == 0 || (System.currentTimeMillis() < getExpiresIn() )));
    }

    @Override
    public void publishMessage(String message,PublishListener listener) throws SnsPublishException {
        String api = "https://api.weibo.com/2/statuses/update.json";
        ArrayList<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
        BasicNameValuePair valuePair = new BasicNameValuePair("access_token", getAccessToken());
        valuePairs.add(valuePair);
        valuePair = new BasicNameValuePair("status", message);
        valuePairs.add(valuePair);
        UrlEncodedFormEntity reqEntity = null;
        
        try {
            reqEntity = new UrlEncodedFormEntity(valuePairs, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }// 不可能发生
        
        String json=null;
        try {
            json=Utils.doHttpsPost(api, reqEntity);
        } catch (IOException e) {
            LogUtil.printException(e);
            throw new SnsPublishException("新浪微博发布失败\t网络异常");
        }
        
        processServerResponse(listener, json);
    }


    @Override
    public void publishMessageWithPicture(String message, String filePath,PublishListener listener) throws SnsPublishException {
        String api="https://upload.api.weibo.com/2/statuses/upload.json";
        MultipartEntity reqEntity = new MultipartEntity();
        try {
            Charset utf8 = Charset.forName("UTF-8");
            StringBody access_token = new StringBody(getAccessToken(),utf8);
            StringBody status = new StringBody(message, utf8);
            FileBody pic = new FileBody(new File(filePath), "image/*");
            reqEntity.addPart("access_token", access_token);
            reqEntity.addPart("status", status);
            reqEntity.addPart("pic", pic);
        } catch (UnsupportedEncodingException e) {}//不可能发生
        String json=null;
        try {
            json= Utils.doHttpsPost(api, reqEntity);
        } catch (IOException e) {
            LogUtil.printException(e);
            throw new SnsPublishException("新浪微博发布失败\t网络异常");
        }
        processServerResponse(listener, json);
    }

    @Override
    protected void handleRedirectUrl(String url, AuthListener mListener) {
        Bundle values = Utils.parseUrl(url);
        String error = values.getString("error");
        String error_code = values.getString("error_code");

        if (error == null && error_code == null) {
            //没有错误
            setAccessToken(values.getString(ACCESS_TOKEN));
            setgetExpiresIn(values.getString(EXPIRES_IN));
            setUID(values.getString(UID));
            mListener.onComplete();
        } else if (error.equals("access_denied")) {
            // 用户或授权服务器拒绝授予数据访问权限
            mListener.onCancel();
        } else {
            if(error_code==null){
                mListener.onAuthorizeException(new AuthorizeException(error, 0));
            }
            else{
                mListener.onAuthorizeException(new AuthorizeException(error, Integer.parseInt(error_code)));
            }
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
        return sp.getLong(EXPIRES_IN, 0l);
    }
    
    private void setgetExpiresIn(String expires) {
        Editor editor = mContext.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE).edit();
        long expiresIn=System.currentTimeMillis() + Long.parseLong(expires)* 1000;
        editor.putLong(EXPIRES_IN, expiresIn);
        editor.commit();
    }
    
    @SuppressWarnings("unused")
    private String getUID() {
        SharedPreferences sp = mContext.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        return sp.getString(UID, null);
    }
    
    private void setUID(String uid) {
        Editor editor = mContext.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(UID, uid);
        editor.commit();
    }
    
    private String getErrorMsg(String errorCode) {
        Properties prop=new Properties();
        try {
            prop.load(mContext.getAssets().open("sina_error_dic.properties"));
            return prop.getProperty(errorCode);
        } catch (IOException e) {}
        return null;
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
            jobj=new JSONObject(json);
        } catch (JSONException e) {
            LogUtil.printException(e);
            throw new SnsPublishException("新浪微博发布失败\tjson解析失败");
        }
        
        String errorCode = JsonHelper.readString(jobj, "error_code");
        String error=JsonHelper.readString(jobj, "error");
        
        if (TextUtils.isEmpty(error)&&TextUtils.isEmpty(errorCode)) {
            listener.onPublishComplete("新浪微博发布成功");
        }else {
            throw new SnsPublishException("新浪微博发布失败\t"+getErrorMsg(errorCode));
        }
    }
}
