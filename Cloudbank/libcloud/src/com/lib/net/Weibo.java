/*
 * Copyright 2011 Sina.
 *
 * Licensed under the Apache License and Weibo License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.open.weibo.com
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lib.net;


import java.io.IOException;
import java.net.MalformedURLException;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;



/**
 * Encapsulation main Weibo APIs, Include: 1. getRquestToken , 2. getAccessToken, 3. url request.
 * Used as a single instance class. Implements a weibo api as a synchronized way.
 *
 * @author  ZhangJie (zhangjie2@staff.sina.com.cn)
 */
public class Weibo {
	
	public static String SERVER = "http://api.t.sina.com.cn/";
	public static String URL_OAUTH_TOKEN = "http://api.t.sina.com.cn/oauth/request_token";
	public static String URL_AUTHORIZE = "http://api.t.sina.com.cn/oauth/authorize";
	public static String URL_ACCESS_TOKEN = "http://api.t.sina.com.cn/oauth/access_token";
	public static String URL_AUTHENTICATION = "http://api.t.sina.com.cn/oauth/authenticate";
	
	public static String APP_KEY = "837378871";
	public static String APP_SECRET = "eead7b50ef53f056daf08996d0ccabb9";
	
	public static Boolean NO_AUTH = false;
	
	private static Weibo mWeiboInstance = null;
	private AccessToken mAccessToken = null;
	private RequestToken mRequestToken = null;
	
	
	private Weibo(){
		if(!Weibo.NO_AUTH){
		Utility.setRequestHeader("Accept-Encoding","gzip");	
		Utility.setTokenObject(this.mRequestToken);
		}
	}
	
	
	
	public static Weibo getInstance(){	
		if(mWeiboInstance == null){
			mWeiboInstance = new Weibo();
		}
		return mWeiboInstance;
	}
	
	public void setAccessToken(AccessToken token){
		mAccessToken = token;
	}
	
	public AccessToken getAccessToken(){
		return this.mAccessToken;
	}
	
	public void setupConsumerConfig(String consumer_key, String consumer_secret){
		Weibo.APP_KEY = consumer_key;
		Weibo.APP_SECRET = consumer_secret;
	}
	
	public void setServer(String server){
		Weibo.SERVER = server;
	}
	
	public void setRequestToken(RequestToken token){
		this.mRequestToken = token;
	}
	
	public void addOauthverifier(String verifier){
		mRequestToken.setVerifier(verifier);
	}
	
	
    /**
     * Requst sina weibo open api by get or post
     *
     * @param url
     *            Openapi request URL.
     * @param params
     *            http get or post parameters . e.g. gettimeling?max=max_id&min=min_id
     *            max and max_id is a pair of key and value for params, also the min and min_id
     * @param httpMethod
     *            http verb: e.g. "GET", "POST", "DELETE" 
     * @throws IOException 
     * @throws MalformedURLException 
     * @throws WeiboException 
     */
	public String request(Context context, String url, WeiboParameters params, String httpMethod, AccessToken token) 
		throws WeiboException{
			if(!Weibo.NO_AUTH)
				Utility.setAuthorization(new RequestHeader());
			String rlt = Utility.openUrl(context, url, httpMethod, params, this.mAccessToken);
			return rlt;
	}
	
	
	/**/
	public RequestToken getRequestToken(Context context, String key, String secret, String callback_url) 
		throws WeiboException{
		Utility.setAuthorization(new RequestTokenHeader());
		WeiboParameters postParams = new WeiboParameters();
		postParams.add("oauth_callback", callback_url);
		String rlt;
		rlt = Utility.openUrl(context, Weibo.URL_OAUTH_TOKEN, "POST", postParams, null);
		RequestToken request = new RequestToken(rlt);
		this.mRequestToken = request;
		return request;
	}
	
	
	public AccessToken generateAccessToken(Context context, RequestToken requestToken) 
		throws WeiboException{
		Utility.setAuthorization(new AccessTokenHeader());
		WeiboParameters authParam = new WeiboParameters();
		authParam.add("oauth_verifier", this.mRequestToken.getVerifier()/*"605835"*/);
		authParam.add("source", APP_KEY);
		String rlt = Utility.openUrl(context, Weibo.URL_ACCESS_TOKEN, "POST", authParam, this.mRequestToken);
		AccessToken accessToken = new AccessToken(rlt);
		this.mAccessToken = accessToken;
		return accessToken;
	}
	

	
	
	private void startActivitySignOn(Activity activity, String key, String secret){
		
	}
	
	
	private void startDialogAuth(Activity activity, String app_key, String app_secret){
		
		
	}
	
	public void authorizeCallBack(int requestCode, int resultCode , Intent data){
		
		
	}
	
}
