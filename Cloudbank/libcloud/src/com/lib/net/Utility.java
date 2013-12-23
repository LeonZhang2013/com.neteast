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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.json.simple.JSONValue;

import com.lib.db.AppDao;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore.Video;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

/**
 * Utility class for Weibo object.
 *
 * @author  ZhangJie (zhangjie2@staff.sina.com.cn)
 */


public class Utility {
	
	private static WeiboParameters mRequestHeader = new WeiboParameters();
	private static HttpHeaderFactory mAuth;
	private static Token mToken = null;
	
	public static final String BOUNDARY = "---------------------------"+getRandomNumber(12);
    public static final String MP_BOUNDARY = "--" + BOUNDARY;
    public static final String END_MP_BOUNDARY = "--" + BOUNDARY + "--";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    
    
    public static final String HTTPMETHOD_POST = "POST";
    public static final String HTTPMETHOD_GET  = "GET";
    public static final String HTTPMETHOD_DELETE  = "DELETE";
    
    private static final int SET_CONNECTION_TIMEOUT = 10000;
    //private static final int SET_SOCKET_TIMEOUT = 10000;
    private static final int SET_SOCKET_TIMEOUT = 30000;
    //private static final int SET_SOCKET_TIMEOUT = 200000;
    private static final int UPLOAD_BUFFER_SIZE = 1024 * 64;
    
    private static int mUploadSliceMaxSize = 1024 * 1024; //equals 1M Bytes
    
	//璁剧疆Token
	public static void setTokenObject(Token token){
		mToken = token;
	}
			
	public static void setAuthorization(HttpHeaderFactory auth){
		mAuth = auth;
	}
	
	public static int getUploadSliceMaxSize(){
		return mUploadSliceMaxSize;
	}
	
	public static void setUploadSliceMaxSize(int size){
		mUploadSliceMaxSize = size;
	}
	
	//璁剧疆http澶�濡傛灉authParam涓嶄负绌猴紝鍒欒〃绀哄綋鍓嶆湁token璁よ瘉淇℃伅闇�鍔犲叆鍒板ご涓�
	public static void setHeader(String httpMethod, HttpUriRequest request, WeiboParameters authParam, String url, Token token) 
		throws WeiboException{
		if(!isBundleEmpty(mRequestHeader)){
			for(int loc = 0; loc < mRequestHeader.size(); loc++){
				String key = mRequestHeader.getKey(loc);
				request.setHeader(key, mRequestHeader.getValue(key));
			}
		}
		if(token !=null && !isBundleEmpty(authParam)){
			String authHeader = mAuth.getWeiboAuthHeader(httpMethod, url, authParam, 
					Weibo.APP_KEY, Weibo.APP_SECRET, token);
			request.setHeader("Authorization", authHeader);
		}
		request.setHeader("User-Agent", System.getProperties().
                getProperty("http.agent") + " AndroidSDK");
	}
	
	public static boolean isBundleEmpty(WeiboParameters bundle){
		if(bundle == null || bundle.size() == 0){
			return true;
		}
		return false;
	}
	
	//濉厖request bundle
	public static void setRequestHeader(String key, String value){
//		mRequestHeader.clear();
		mRequestHeader.add(key, value);
	}
	
	public static void setRequestHeader(WeiboParameters params){
		mRequestHeader.addAll(params);
	}
	
	
	public static void clearRequestHeader(){
		mRequestHeader.clear();
		
	}
		
    public static String encodePostBody(Bundle parameters, String boundary) {
        if (parameters == null) return "";
        StringBuilder sb = new StringBuilder();

        for (String key : parameters.keySet()) {
            if (parameters.getByteArray(key) != null) {
                continue;
            }

            sb.append("Content-Disposition: form-data; name=\"" + key +
                    "\"\r\n\r\n" + parameters.getString(key));
            sb.append("\r\n" + "--" + boundary + "\r\n");
        }

        return sb.toString();
    }

    public static String encodePostBody(WeiboParameters parameters, String boundary) {
        if (parameters == null) return "";
        StringBuilder sb = new StringBuilder();
        String key = "";
        for(int loc = 0; loc < parameters.size(); loc++){
    		key = parameters.getKey(loc);
            if (key == "") {
                continue;
            }

            sb.append("--").append(boundary).append("\r\n");
    		sb.append("content-disposition: form-data; name=\"").append(key)
    		        .append("\"\r\n\r\n");
    		sb.append(parameters.getValue(key)).append("\r\n");
        }

        return sb.toString();
    }
    
    public static String encodeUrl(WeiboParameters parameters) {
        if (parameters == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int loc = 0; loc < parameters.size(); loc++) {
            if (first) first = false; else sb.append("&");
            sb.append(URLEncoder.encode(parameters.getKey(loc)) + "=" +
                      URLEncoder.encode(parameters.getValue(loc)));
        }
        return sb.toString();
    }

    public static Bundle decodeUrl(String s) {
        Bundle params = new Bundle();
        if (s != null) {
            String array[] = s.split("&");
            for (String parameter : array) {
                String v[] = parameter.split("=");
                params.putString(URLDecoder.decode(v[0]),
                                 URLDecoder.decode(v[1]));
            }
        }
        return params;
    }

    /**
     * Parse a URL query and fragment parameters into a key-value bundle.
     *
     * @param url the URL to parse
     * @return a dictionary bundle of keys and values
     */
    public static Bundle parseUrl(String url) {
        // hack to prevent MalformedURLException
        url = url.replace("fbconnect", "http");
        try {
            URL u = new URL(url);
            Bundle b = decodeUrl(u.getQuery());
            b.putAll(decodeUrl(u.getRef()));
            return b;
        } catch (MalformedURLException e) {
            return new Bundle();
        }
    }

    
    /**
     * Construct a url encoded entity by parameters .
     *
     * @param bundle:parameters key pairs
     * @return UrlEncodedFormEntity: encoed entity
     */
    public static UrlEncodedFormEntity getPostParamters(Bundle bundle) throws WeiboException{
		if(bundle == null || bundle.isEmpty()){
			return null;
		}
		try {
			List<NameValuePair> form = new ArrayList<NameValuePair>();
			for(String key: bundle.keySet()){
				form.add(new BasicNameValuePair(key, bundle.getString(key)));
			}
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, "UTF-8");
			return entity;
		} catch (UnsupportedEncodingException e) {
			throw new WeiboException(e);
		}
	}
    
    
    /**
     * Implement a weibo http request and return results  .
     *
     * @param context : context of activity 
     * @param url : request url of open api
     * @param method : HTTP METHOD.GET, POST, DELETE
     * @param params : Http params , query or postparameters
     * @param Token : oauth token or accesstoken
     * @return UrlEncodedFormEntity: encoed entity
     */
    
    public static String openUrl(Context context, String url, String method, WeiboParameters params,  
    		Token token) throws WeiboException{
    	String rlt = "";
    	String file = "";
    	for(int loc = 0; loc < params.size(); loc++){
    		String key = params.getKey(loc);
    		if(key.equals("sourcefile")){
    			file = params.getValue(key);
    			params.remove(key);
    		}
    	}
    	if(TextUtils.isEmpty(file)){
    		rlt = openUrl(context, url, method, params, null, token);
    	}else{
    		rlt = openUrl(context, url, method, params, file, token);
    	}
    	return rlt;
    }
    
    
    @SuppressWarnings("unused")
	public static String openUrl(Context context, String url, String method, WeiboParameters params,  
    		String file, Token token)throws WeiboException {
	    		String result = "";
	    		HttpClient client = getHttpClient(context);
	    		try {
		    	HttpUriRequest request = null;
		    	ByteArrayOutputStream bos = null;
		    	if(method.equals("GET")){
		    		if(params.size()>0)
		    		url = url + "?" + encodeUrl(params);
		    		HttpGet get = new HttpGet(url);
		    		request = get;
		    	}else if(method.equals("POST")){	
		    		HttpPost post = new HttpPost(url);	
		    		byte[] data = null;
				    bos = new ByteArrayOutputStream(UPLOAD_BUFFER_SIZE);
				    if(!TextUtils.isEmpty(file)){
				    		try{
				    			return postLargeFile(context,url,params,file);
				    		}catch(WeiboException ex){
				    			ex.printStackTrace();
				    			throw ex;
				    		}
				    }else{
				    	post.setHeader("Content-Type", "application/x-www-form-urlencoded");
				    	String postParam = encodeParameters(params);
					    data = postParam.getBytes("UTF-8");
				    	bos.write(data);
				    	System.out.println("post url="+url);
				    	System.out.println("post data="+postParam);
				    }
				    data = bos.toByteArray();
				    bos.close();
				    ByteArrayEntity formEntity = new ByteArrayEntity(data);
		    		post.setEntity(formEntity);
		    		request = post;
		    	}else if(method.equals("DELETE")){
		    		request = new HttpDelete(url);
		    	}
		    	setHeader(method, request, params, url, token);
		    	HttpResponse response = client.execute(request);
		    	StatusLine status = response.getStatusLine();
		    	int statusCode = status.getStatusCode();
		    	
		    	if (statusCode != 200) {
		    		result = read(response);
					throw new WeiboException(String.format(status.toString()), statusCode);
				}
				result = read(response);
				return result;
	    	} catch (IOException e) {
				throw new WeiboException(e);
			}finally{
				client.getConnectionManager().shutdown();
				System.out.println("connection shutdown");
			}
    }
    
    
    /**
     * Get a HttpClient object which is setting correctly  .
     *
     * @param context : context of activity 
     * @return HttpClient: HttpClient object
     */
    public static HttpClient getHttpClient(Context context){
    	BasicHttpParams httpParameters = new BasicHttpParams();
        // Set the default socket timeout (SO_TIMEOUT) // in
        // milliseconds which is the timeout for waiting for data.
        HttpConnectionParams.setConnectionTimeout(httpParameters,
                Utility.SET_CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParameters,
                Utility.SET_SOCKET_TIMEOUT);
        HttpClient client = new DefaultHttpClient(httpParameters);  
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
        	// 鑾峰彇褰撳墠姝ｅ湪浣跨敤鐨凙PN鎺ュ叆鐐�
            Uri uri = Uri.parse("content://telephony/carriers/preferapn"); 
            Cursor mCursor = context.getContentResolver().query(uri,
                    null, null, null, null);
            if (mCursor != null && mCursor.moveToFirst()) {
                // 娓告爣绉昏嚦绗竴鏉¤褰曪紝褰撶劧涔熷彧鏈変竴鏉�
                String proxyStr = mCursor.getString(mCursor
                        .getColumnIndex("proxy"));
                if (proxyStr != null && proxyStr.trim().length() > 0) {
                    HttpHost proxy = new HttpHost(proxyStr, 80);
                    client.getParams().setParameter(
                            ConnRouteParams.DEFAULT_PROXY, proxy);
                }
            }
        }
    	return client;
    }
    
    
    /**
     * Upload image into output stream  .
     *
     * @param out : output stream for uploading weibo 
     * @param imgpath : bitmap for uploading
     * @return void
     */
    private static void imageContentToUpload(OutputStream out, Bitmap imgpath ,String filename)
    	throws WeiboException {
		StringBuilder temp = new StringBuilder();
		
		temp.append(MP_BOUNDARY).append("\r\n");
		temp.append("Content-Disposition: form-data; name=\"thumbnail\"; filename=\"")
		        .append(filename+".jpg").append("\"\r\n");
		String filetype = "image/jpeg";
		temp.append("Content-Type: ").append(filetype).append("\r\n\r\n");
		byte[] res = temp.toString().getBytes();
		BufferedInputStream bis = null;
		try {
			out.write(res);
			imgpath.compress(CompressFormat.JPEG, 75, out);
			out.write("\r\n".getBytes());
			//out.write(("\r\n" + END_MP_BOUNDARY).getBytes());
		} catch (IOException e) {
			throw new WeiboException(e);
		} finally {
			if (null != bis) {
	        try {
	            bis.close();
	        } catch (IOException e) {
	        	throw new WeiboException(e);
	        }
	    }	
		}
	}

    
    /**
     * Upload weibo contents into output stream  .
     *
     * @param baos : output stream for uploading weibo 
     * @param params : post parameters for uploading
     * @return void
     */
    private static void paramToUpload(OutputStream baos, WeiboParameters params)
    	throws WeiboException {
    	String key = "";
    	for(int loc = 0; loc < params.size(); loc++){
    		key = params.getKey(loc);
    		StringBuilder temp = new StringBuilder(10);
    		temp.setLength(0);
    		temp.append(MP_BOUNDARY).append("\r\n");
    		temp.append("content-disposition: form-data; name=\"").append(key)
    		        .append("\"\r\n\r\n");
    		temp.append(params.getValue(key)).append("\r\n");
    		byte[] res = temp.toString().getBytes();
    		try {
				baos.write(res);
			} catch (IOException e) {
				throw new WeiboException(e);
			}
    	}
	}
    
    /**
     * Read http requests result from response .
     *
     * @param response : http response by executing httpclient 
     * 
     * @return String : http response content
     */
    private static String read(HttpResponse response) throws WeiboException{
    	String result = "";
    	HttpEntity entity = response.getEntity();
		InputStream inputStream;
		try {
			inputStream = entity.getContent();
			ByteArrayOutputStream content = new ByteArrayOutputStream();
	
			Header header = response.getFirstHeader("Content-Encoding");
			if (header != null && header.getValue().toLowerCase().indexOf("gzip") > -1) {
				inputStream = new GZIPInputStream(inputStream);
			}
	
			// Read response into a buffered stream
			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = inputStream.read(sBuffer)) != -1) {
				content.write(sBuffer, 0, readBytes);
			}
			// Return result from buffered stream
			result = new String(content.toByteArray());
	    	return result;
		} catch (IllegalStateException e) {
			throw new WeiboException(e);
		} catch (IOException e) {
			throw new WeiboException(e);
		}
    }
    
    /**
     * Read http requests result from inputstream .
     *
     * @param inputstream : http inputstream from HttpConnection
     * 
     * @return String : http response content
     */
    private static String read(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }
    
    
    /**
     * Clear current context cookies .
     *
     * @param context : current activity context.
     * 
     * @return void 
     */
    public static void clearCookies(Context context) {
        @SuppressWarnings("unused")
        CookieSyncManager cookieSyncMngr =
            CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }


    /**
     * Display a simple alert dialog with the given text and title.
     *
     * @param context
     *          Android context in which the dialog should be displayed
     * @param title
     *          Alert dialog title
     * @param text
     *          Alert dialog message
     */
    public static void showAlert(Context context, String title, String text) {
        Builder alertBuilder = new Builder(context);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(text);
        alertBuilder.create().show();
    }
    
    
    public static String encodeParameters(WeiboParameters httpParams) {
		if (null == httpParams || Utility.isBundleEmpty(httpParams)) {
			return "";
		}
		StringBuilder buf = new StringBuilder();
		int j = 0;
		for (int loc = 0; loc < httpParams.size(); loc++) {
			String key = httpParams.getKey(loc);
			if (j != 0) {
				buf.append("&");
			}
			try {
				buf.append(URLEncoder.encode(key, "UTF-8"))
					.append("=")
					.append(URLEncoder.encode(httpParams.getValue(key), "UTF-8"));
			} catch (java.io.UnsupportedEncodingException neverHappen) {
			}
			j++;
		}
		return buf.toString();

	}
    
    
    /**
     * Base64 encode mehtod for weibo request.Refer to weibo development document.
     *
     */
    public static char[] base64Encode(byte[] data) {
		final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
		char[] out = new char[((data.length + 2) / 3) * 4];
		for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
			boolean quad = false;
			boolean trip = false;
			int val = (0xFF & (int) data[i]);
			val <<= 8;
			if ((i + 1) < data.length) {
				val |= (0xFF & (int) data[i + 1]);
				trip = true;
			}
			val <<= 8;
			if ((i + 2) < data.length) {
				val |= (0xFF & (int) data[i + 2]);
				quad = true;
			}
			out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
			val >>= 6;
			out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
			val >>= 6;
			out[index + 1] = alphabet[val & 0x3F];
			val >>= 6;
			out[index + 0] = alphabet[val & 0x3F];
		}
		return out;
	}

    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
    

    public static String MD5Encode(byte[] toencode) {
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                md5.reset();
                md5.update(toencode);
                byte[] encoded = md5.digest();
                StringBuilder sb = new StringBuilder(encoded.length * 2);
                for(byte b: encoded){
                    sb.append(Integer.toHexString((b & 0xf0) >>> 4));
                    sb.append(Integer.toHexString(b & 0x0f));
                }
                return sb.toString().toUpperCase();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return "";
        }
    
    /**
    * JAVA获得0-9的随机数
    *
    * @param length
    * @return String
    */
    public static String getRandomNumber(int length) {
	    Random random = new Random();
	    StringBuffer buffer = new StringBuffer();
	
	    for (int i = 0; i < length; i++) {
	    buffer.append(random.nextInt(10));
	    }
	    return buffer.toString();
    }
    
    public static String getNameExtension(File f){
    	return (f!=null)?getNameExtension(f.getName()):"";
    }
    
    public static String getNameExtension(String filename){
    	if(filename != null && filename.length() > 0){
    		int i = filename.lastIndexOf(".");
    		if(i > -1 && i < (filename.length() -1)){
    			return filename.substring(i+1);
    		}
    	}
    	return "";
    }
    
    public static String getNameBody(File f){
    	return (f!=null)?getNameBody(f.getName()):"";
    }
    
    public static String getNameBody(String filename){
    	if(filename != null && filename.length() > 0){
    		int i = filename.lastIndexOf(".");
    		if(i > -1 && i < (filename.length() -1)){
    			return filename.substring(0,i);
    		}
    	}
    	return "";
    }
    
    public static Bitmap getThumbnail(String url,String type ,int limited){
	    	if(type.equalsIgnoreCase("image")){
	    		if(limited ==0) limited = 100;
	    		/*TODO download image and create thumbnail*/
	    		return null;
	    	}else if(type.equalsIgnoreCase("video")){
	    		if(limited == 0) limited = Video.Thumbnails.MICRO_KIND;
	    		return ThumbnailUtils.createVideoThumbnail(url,limited);
	    	}
	    	
    	return null;
    }
    
    public static Bitmap getThumbnail(File f,String type ,int limited){
    	if(type.equalsIgnoreCase("image")){
    		if(limited ==0) limited = 100;
    		try{
	    		InputStream is = new FileInputStream(f);
	        	BitmapFactory.Options o = new BitmapFactory.Options();
	            o.inJustDecodeBounds = true;
	            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
	            int width=o.outWidth, height=o.outHeight;
	            
	    		if(limited > 0){
		    			int width_tmp = width , height_tmp = height , scale_tmp = 1;
		    			while(true){
		                    if(width_tmp/2< limited && height_tmp/2< limited)
		                        break;
		                    width_tmp/=2;
		                    height_tmp/=2;
		                    scale_tmp*=2;
		                }
		    			BitmapFactory.Options o2 = new BitmapFactory.Options();
			            o2.inSampleSize=scale_tmp;
			            Bitmap ret = BitmapFactory.decodeStream(is, null, o2);	            
			            is.close();
			            return ret;
	    		}
    		}catch (Exception e) {
    			e.printStackTrace();
    		}
    		
    		return null;
    	}else if(type.equalsIgnoreCase("video")){
    		if(limited == 0) limited = Video.Thumbnails.MICRO_KIND;
    		return ThumbnailUtils.createVideoThumbnail(f.getPath(),limited);
    	}
    	
	return null;
}
    
    public static String[] splitEpisode(String url,String Delimiter){
    	return url.split(Delimiter);
    }
    
    public static String[] splitEpisode(String url){
    	return splitEpisode(url,";");
    }
    
    public static Map<String,Long> parseResponseFromUploadServer(String response){
    	int start=-1;
		int end  =-1;
		start = response.indexOf("<div id=\"message\" class=\"bubble\">");
		if(start >= 0)
			end = response.indexOf("</div>",start);
		if(start >= 0 && end > 0){
			String json = response.substring(start+"<div id=\"message\" class=\"bubble\">".length(), end);
			//System.out.println("response "+json );
			return (Map<String,Long>)(JSONValue.parse(json));
		}
		return null;
    }
    
    private static String postSlicedFile(Context context,String url,WeiboParameters params,File hfile,int taskid,int slice_num,int max_post_bytes)
    		throws WeiboException {
    	
    	HttpURLConnection connection = null;
    	DataOutputStream outputStream = null;
    	ByteArrayOutputStream thumbbos = null;
    	
    	long totalBytes = 0;
    	int fileid = 0;
    	int id = 0;
    	int totalSlices = 0;
    	
    	if(hfile.exists())
    		totalBytes = hfile.length();
    	else{
    		throw new WeiboException("file does not exist");
    	}
    	
    	AppDao UploadDao = AppDao.getInstance(context);
    		
    	if(params.getLocation("fileid") > -1 ){
    		fileid = Integer.parseInt(params.getValue("fileid"));
    	}
    	if(params.getLocation("id") > -1 ){
    		id = Integer.parseInt(params.getValue("id"));
    		params.remove("id");
    	}else{
    		id = taskid;
    	}
    	if(params.getLocation("thumbnail") > -1){
    		String type = params.getValue("thumbnail");
    		params.remove("thumbnail");
    		Bitmap thumbbmp = getThumbnail(hfile,type,0);
    		if(thumbbmp != null){
    			try{
    				thumbbos = new ByteArrayOutputStream(UPLOAD_BUFFER_SIZE);
    				imageContentToUpload(thumbbos,thumbbmp,"newpic");
    			}catch(Exception e){
    				e.printStackTrace();
    				thumbbos = null;
    				thumbbmp = null;
    			}
    		}
    	}
    	
    	if(slice_num > 0 && max_post_bytes > 0){
    		totalSlices = (int) Math.ceil((double)(totalBytes) / (double)(max_post_bytes));
    		if(slice_num > totalSlices) 
    			slice_num = 1;
    	}

    	long uploadedBytes = max_post_bytes * ((slice_num > 0)?(slice_num -1) : 0);
    	int sliceSizeBytes = (int)(Math.min(max_post_bytes, totalBytes-uploadedBytes));
    	
    	if(totalSlices > 1){
    		String sliceparam = "{'num':"+Integer.toString(slice_num)+",'total':"+Integer.toString(totalSlices)+",'slicesize':"+Integer.toString(sliceSizeBytes)+"}";
    		System.out.println(sliceparam);
    		params.add("slice", sliceparam);
    	}
		StringBuilder str_params = new StringBuilder();
		str_params.append(Utility.encodePostBody(params,BOUNDARY));
		
		String ext = getNameExtension(hfile);
		ext = ext.length() > 0 ? ("."+ext):ext;
		StringBuilder str_before = new StringBuilder();
		str_before.append(MP_BOUNDARY).append("\r\n");
		str_before.append("Content-Disposition: form-data; name=\"sourcefile\"; filename=\"")
		        .append("newfile"+ext).append("\"\r\n");
		String filetype = "application/octet-stream";
		str_before.append("Content-Type: ").append(filetype).append("\r\n\r\n");
		
		//byte[] res = temp.toString().getBytes();
		StringBuilder str_after = new StringBuilder();
		str_after.append("\r\n").append(END_MP_BOUNDARY).append("\r\n");
		
		
		
		
    	int bytesRead, bufferSize;
    	byte[] buffer;
    	int maxBufferSize = UPLOAD_BUFFER_SIZE;
    	

    	boolean postSizeLimited = ( max_post_bytes > 0 )? true:false;
    	//int postSizeAvaliable = max_post_bytes;
    	int postSizeAvaliable = Math.min(max_post_bytes,sliceSizeBytes);
    	String result;
    	try
    	{
    		long content_length = str_params.length() 
    				+ ((thumbbos != null)?thumbbos.size():0)
    				+ str_before.length() 
    				+ str_after.length() 
    				+ ((postSizeLimited)?sliceSizeBytes:totalBytes);
    		
	    	//FileInputStream fileInputStream = new FileInputStream( hfile );
	    	RandomAccessFile raf = new RandomAccessFile(hfile,"r"); 
	    	if(uploadedBytes > 0)
	    		raf.seek(uploadedBytes);
	    	
	    	URL oUrl = new URL(url);
	    	connection = (HttpURLConnection) oUrl.openConnection();
	
	    	// Allow Inputs & Outputs
	    	connection.setDoInput(true);
	    	connection.setDoOutput(true);
	    	connection.setUseCaches(false);
	    	connection.setConnectTimeout(Utility.SET_CONNECTION_TIMEOUT);
	    	connection.setReadTimeout(Utility.SET_SOCKET_TIMEOUT);  
	    	//connection.setChunkedStreamingMode(UPLOAD_BUFFER_SIZE);
	    	
	    	// Enable POST method
	    	connection.setRequestMethod("POST");
	
	    	connection.setRequestProperty("Connection", "Keep-Alive");
	    	connection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+BOUNDARY);
	    	connection.setRequestProperty("Content-Length", Long.toString(content_length));
	    	System.out.println("content_length "+Long.toString(content_length)+"/ file " + sliceSizeBytes +"/ str "+(str_params.length()+str_before.length()+str_after.length()));
	    	
	    	connection.setFixedLengthStreamingMode((int)content_length);
	    	
	    	outputStream = new DataOutputStream( connection.getOutputStream() );
	    	
	    	/*upload param*/
	    	outputStream.writeBytes(str_params.toString());
	    	
	    	/*upload thumbnail*/
	    	if(thumbbos != null){
	    		outputStream.write(thumbbos.toByteArray(),0,thumbbos.size());
	    		System.out.println("thumbnail size:" + thumbbos.size());
	    	}
	    	/*upload string before file body*/
	    	outputStream.writeBytes(str_before.toString());
	    	
	    	//bytesAvailable = fileInputStream.available();
	    	
	    	bufferSize = Math.min(postSizeAvaliable, maxBufferSize);
	    	
	    	buffer = new byte[bufferSize];
	
	    	// Read file
	    	//bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	    	int status = UploadDao.getUploadStatus(id, fileid);
	    	if( status == AppDao.UPLOAD_SUSPEND || status==AppDao.UPLOAD_ERROR){
	    		System.out.println("check status=UPLOAD_SUSPEND.");
		    	raf.close();
		    	outputStream.flush();
		    	outputStream.close();
		    	return "";
	    	}else{
	    		bytesRead = raf.read(buffer, 0, bufferSize);
    		}
	    	while (bytesRead > 0)
	    	{
		    	outputStream.write(buffer, 0, bytesRead);
		    	Thread.sleep(100);
		    	uploadedBytes += bytesRead;
		    	
		    	System.out.println("Write "+bytesRead+"Bytes.Uploaded "+uploadedBytes+" Bytes / "+ uploadedBytes*100/totalBytes+"%");
		    	UploadDao.updateUploadProgress(id,fileid,(int)uploadedBytes);
		    	
		    	//System.out.println("check status id = " +id + "fileid" + fileid + "Status =" +UploadDao.getUploadStatus(id, fileid) );
		    	status = UploadDao.getUploadStatus(id, fileid);
		    	if( status == AppDao.UPLOAD_SUSPEND || status==AppDao.UPLOAD_ERROR){
		    		System.out.println("check status=UPLOAD_SUSPEND.");
		    		
			    	raf.close();
			    	outputStream.flush();
			    	outputStream.close();
			    	return "";
		    	}
		    	//bytesAvailable = fileInputStream.available();
		    	//bufferSize = Math.min(bytesAvailable, maxBufferSize);
		    	//bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		    	if(postSizeLimited){
			    	postSizeAvaliable -= bytesRead;
			    	if(postSizeAvaliable > 0){
			    		bufferSize = Math.min(postSizeAvaliable, maxBufferSize);
			    		bytesRead = raf.read(buffer, 0, bufferSize);
			    	}else{
			    		break;
			    	}
		    	}
	    	}
	    	/*while (bytesRead > 0){
	    	    bufferSize = Math.min(bytesAvailable, maxBufferSize);
	    	    byte byt[]=new byte[bufferSize];
	    	    fileInputStream.read(byt);
	    	    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	    	    outputStream.write(buffer, 0, bufferSize);
	    	}*/
	    	
	    	outputStream.writeBytes(str_after.toString());
	    	
	    	//fileInputStream.close();
	    	raf.close();
	    	outputStream.flush();
	    	outputStream.close();
    	}
    	catch (Exception ex)
    	{
    		//Exception handling
    		throw new WeiboException("post failed",ex);
    	}
    	
    	try{
	    	int statusCode = connection.getResponseCode();
			System.out.println("result code="+statusCode);
			

	    	if (statusCode != 200) {
	    		result = connection.getResponseMessage();
	    		//client.getConnectionManager().shutdown();
	    		//connection.disconnect();
				throw new WeiboException(result, statusCode);
			}
			// parse content stream from response
	    	System.out.println("Upload slice over");
	    	//UploadDao.updateUploadStatus(fileid,AppDao.UPLOAD_DONE);
	    	result = read(connection.getInputStream());
			//client.getConnectionManager().shutdown();
			//connection.disconnect();
    	}catch(IOException e){
    		throw new WeiboException("receive response failed",1);
    	}catch(WeiboException e){
    		throw e;
    	}
    	
    	if(slice_num > 0 && max_post_bytes > 0){
    		//result = "resp_messages{\"fileid\":12341,\"piecenum\":0,\"filesize\":9}";
    		//System.out.println(result);
    		Map<String,Long> res = null;
    		try{
    		res = parseResponseFromUploadServer(result);
    		System.out.println(" id="+res.get("fileid")+" filesize="+res.get("filesize")+" piecenum="+res.get("piecenum"));
    		}catch(Exception e){
    			throw new WeiboException("response json error");
    		}
    		
    		if(res.get("filesize") != (long)(sliceSizeBytes)){
    			throw new WeiboException("filesize is incorrect",1);
    		}else if(res.get("fileid") != (long)(fileid)){
    			throw new WeiboException("fileid is incorrect");
    		}else if(res.get("piecenum") != (long)(slice_num) && (totalSlices>1)){
    			throw new WeiboException("piecenum is incorrect");
    		}else{
    			
    			UploadDao.updateUploadSlice(id,fileid,slice_num,totalSlices);
	    		if(slice_num != totalSlices){
	    			throw new WeiboException("Continue to post slice["+(slice_num+1)+"/"+totalSlices+"]",0);
	    		}
    		}
    	}else{
    		UploadDao.updateUploadSlice(id,fileid,1,1);
    	}
			
		return result;

    }
    
    
    private static String postSingleFile(Context context,String url,WeiboParameters params,String file)
    		throws WeiboException {
    	File hfile = new File(file);
    	try{
    		int id=0;
    		if(params.getLocation("id") > -1){
        		id = Integer.parseInt(params.getValue("id"));
        		params.remove("id");
        	}
    		return postSlicedFile(context,url,params,hfile,id,0,0);
    	}catch(WeiboException ex){
    		throw ex;
    	}
    }

    private static String postLargeFile(Context context,String url,WeiboParameters params,String file)
    		throws WeiboException {
    	AppDao UploadDao = AppDao.getInstance(context);
    	
    	File hfile = new File(file);
    	int id = 0;
    	int fileid = 0;
    	if(params.getLocation("fileid") > -1 ){
    		fileid = Integer.parseInt(params.getValue("fileid"));
    	}
    	if(params.getLocation("id") > -1){
    		id = Integer.parseInt(params.getValue("id"));
    		params.remove("id");
    	}
    	
    	long totalBytes = 0;
    	if(hfile.exists())
    		totalBytes = hfile.length();
    	else{
    		System.out.println("Upload Failed. File not found.");
    		UploadDao.updateUploadStatus(id,fileid,AppDao.UPLOAD_ERROR);
    		throw new WeiboException("file does not exist");
    	}
    	
    	UploadDao.updateUploadStatus(id,fileid,AppDao.UPLOAD_ING);
    	
    	int slice_num = 1;
    	if(params.getLocation("slice") > -1){
    		slice_num = Integer.parseInt(params.getValue("slice"));
    		if(slice_num<=0) slice_num=1;
    	}
    	
    	int totalSlices = (int) Math.ceil((double)(totalBytes) / (double)(mUploadSliceMaxSize));
    	int retry = 5;
    	String response = "";
    	while( slice_num <= totalSlices && retry > 0){
	    	try{        	
	    		response = postSlicedFile(context,url,params,hfile,id,slice_num,mUploadSliceMaxSize);
	    		if(response.length() < 1){
	    			System.out.println("Upload suspended");
	    			return "";
	    		}
	    	}catch(WeiboException ex){
	    		if(ex.getStatusCode() > 0){
	    			retry --;
	    			System.out.println("code "+ex.getStatusCode()+":"+ex.getMessage());
	    			continue;
	    		}else if(ex.getStatusCode() == 0){
	    			retry = 5;
	    			slice_num++;
	    			System.out.println("code "+ex.getStatusCode()+":"+ex.getMessage());
	    			continue;
	    		}else{
	    			System.out.println("Upload Failed. Assert.");
	    			if(UploadDao.getUploadStatus(id, fileid) != AppDao.UPLOAD_SUSPEND){
	    				UploadDao.updateUploadStatus(id,fileid,AppDao.UPLOAD_ERROR);
	    			}
	    			throw ex;
	    		}
	    		
	    	}
	    	slice_num++;
	    	retry =5;
    	}
    	if(retry <=0){
    		System.out.println("Upload Failed. Retries exhaust.");
    		UploadDao.updateUploadStatus(id,fileid,AppDao.UPLOAD_ERROR);
    	}else{
    		System.out.println("Upload Complete.");
    		UploadDao.updateUploadStatus(id,fileid,AppDao.UPLOAD_DONE);
    	}
    	return response;
    }
}
