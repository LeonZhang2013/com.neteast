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

package com.lib.cloud;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONValue;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.lib.net.AsyncWeiboRunner;
import com.lib.net.ImageLoader;
import com.lib.net.Utility;
import com.lib.net.Weibo;
import com.lib.net.WeiboException;
import com.lib.net.WeiboParameters;

public class LibCloud {
	private static LibCloud mCloudInstance = null;
	private static Weibo mWeiboInstance = null;
	private static Context mContext = null;
	private static ImageLoader imageloader = null;
	private static String USER_SERVER = "";
	private static String MEDIA_SERVER = "";
	private static String OFFLINE_TRANSPERCENT_SERVER = "";
	private static String APP_CODE = "";
	private static String APP_KEY = "";
	private static String USER_ID = "";
	private static String TOKEN = "";
	private static String DATATYPE = "j";

	private static final Boolean DEBUG = false;

	private Boolean preload = false;

	private LibCloud() {

	}

	public static LibCloud getInstance(Context context) {
		if (mCloudInstance == null) {
			mCloudInstance = new LibCloud();
			mWeiboInstance = Weibo.getInstance();
			// mWeiboInstance.setServer("http://118.144.81.24:9205"); // 北京
			// USER_SERVER = "http://118.144.81.24:9205/Mobile";

			// mWeiboInstance.setServer("http://218.108.85.59:8360"); // 杭州演示环境
			// USER_SERVER = "http://218.108.85.59:8360/Mobile";

			// mWeiboInstance.setServer("http://218.108.168.45:8360"); // 线上服务器
			// USER_SERVER = "http://218.108.168.45:8360/Mobile";
			// MEDIA_SERVER = "http://218.108.168.3:80/Mobile";
			// OFFLINE_TRANSPERCENT_SERVER =
			// "http://218.108.168.7:8810/Interface";

			// mWeiboInstance.setServer("http://218.108.85.62:8360"); // 预发布环境

			mWeiboInstance.setServer("http://mng.wasu.com.cn:8360"); // 线上服务器
			USER_SERVER = "http://mng.wasu.com.cn:8360/Mobile";
			MEDIA_SERVER = "http://sapi.cbb.wasu.com.cn/Mobile";
			OFFLINE_TRANSPERCENT_SERVER = "http://aolvdo.wasu.com.cn:8810/Interface";

			// mWeiboInstance.setServer("http://192.168.1.200:8360"); // 展览环境
			// USER_SERVER = "http://192.168.1.200:8360/Mobile";
			// MEDIA_SERVER = "http://192.168.1.200:80/Mobile";
			// OFFLINE_TRANSPERCENT_SERVER =
			// "http://192.168.1.200:8810/Interface";

			// mWeiboInstance.setServer("http://10.0.3.24:8080"); // 北京测试环境
			// USER_SERVER = "http://10.0.3.24:8080/Mobile";
			// MEDIA_SERVER = "http://10.0.3.188:8001/Mobile";
			// OFFLINE_TRANSPERCENT_SERVER = "http://10.0.3.20:8810/Interface";

			// mWeiboInstance.setServer("http://10.0.3.24:8080"); USER_SERVER =
			// "http://10.0.3.24:8080/Mobile";
			// MEDIA_SERVER = "http://218.108.168.3:80/Mobile";

			APP_CODE = "10009";
			APP_KEY = "2cfce59a625fd9e6";
			mContext = context;
			imageloader = new ImageLoader(context);
		}
		return mCloudInstance;
	}

	public String getUserServer() {
		return USER_SERVER;
	}

	private String base64Encode(byte[] toencode) {
		return com.lib.net.Base64.encodeToString(toencode, android.util.Base64.NO_WRAP);
	}

	private Map<String, Object> DEFAULT_ERROR() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", "-1");
		map.put("description", "error");
		return map;
	}

	private Map<String, Object> Object2List(Map<String, Object> retmap, String key) {
		if (retmap.containsKey(key)) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list.add((Map<String, Object>) (retmap.get(key)));
			retmap.put(key, list);
		}
		return retmap;
	}

	private Map<String, Object> List2Object(Map<String, Object> retmap, Object jsonvalue, String key) {
		if (jsonvalue instanceof org.json.simple.JSONArray) {
			retmap.put(key, (List<Map<String, Object>>) jsonvalue);
		} else if (jsonvalue instanceof org.json.simple.JSONObject) {
			retmap = (Map<String, Object>) jsonvalue;
		} else {
			retmap = DEFAULT_ERROR();
		}
		return retmap;
	}

	private void LogPrintf(String log) {
		if (DEBUG)
			System.out.println(log);
	}

	private void LogPrintf(Object o) {
		if (DEBUG)
			System.out.println(o);
	}

	public void SetPreload(Boolean bpreload) {
		preload = bpreload;
	}

	public void SetCacheSize(int MaxInMB) {
		imageloader.SetCacheCapacityInMB(MaxInMB);
	}

	public void SetRetainedHeapSize(int MaxInMB) {
		imageloader.SetRetainedHeapInMB(MaxInMB);
	}

	public void SetStubID(int stubId) {
		imageloader.SetStubID(stubId);
	}

	public void EnableMemoryCache(boolean enable) {
		imageloader.SetMemoryCacheEnable(enable);
	}

	public void SetConnectTimeout(int timeout) {
		imageloader.SetConnectTimeout(timeout);
	}

	public void SetReadTimeout(int timeout) {
		imageloader.SetReadTimeout(timeout);
	}

	public void SetRetry(int num) {
		imageloader.SetRetry(num);
	}

	public void ClearImageCache(String url) {
		imageloader.ClearImageCache(url);
	}

	public void ClearCache() {
		imageloader.clearCache();
	}

	public void ClearMemoryCache() {
		imageloader.clearMemoryCache();
	}

	public void SetUserID(String id) {
		USER_ID = id;
	}

	public void SetToken(String token) {
		TOKEN = token;
	}

	public void SetAppcode(String appcode) {
		APP_CODE = appcode;
	}

	public String GetAppcode() {
		return APP_CODE;
	}

	public String GetUserID() {
		return USER_ID;
	}

	public String GetToken() {
		return TOKEN;
	}

	public String GetAppKey() {
		return APP_KEY;
	}

	public void DisplayImage(String url, ImageView view) {
		if (url != null && url != "") {
			imageloader.DisplayImage(url, view);
		}
	}

	public void DisplayScaledImage(String url, ImageView view, int scale) {
		if (url != null && url != "" && scale >= 1) {
			imageloader.DisplayScaledImage(url, view, scale);
		}
	}

	public void DisplayLimitedImage(String url, ImageView view, int width, int height) {
		if (url != null && url != "") {
			imageloader.DisplayLimitedImage(url, view, width, height);
		}
	}

	public void DisplayVideoThumbnail(String videourl, ImageView view) {
		if (videourl != null && videourl != "") {
			imageloader.DisplayVideoThumbnail(videourl, view);
		}
	}

	public void PreloadImage(String url) {
		if (url != null && url != "") {
			imageloader.PreloadImage(url);
		}
	}

	public Map<String, Object> Login(String user, String password) throws WeiboException {

		String url = USER_SERVER + "/login";
		url += "/appcode/" + APP_CODE;
		url += "/datatype/" + DATATYPE;

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			password = Utility.MD5Encode(password.getBytes("UTF-8"));
			String reqstr = base64Encode(("user=" + user + "&password=" + password).getBytes());
			String verify = Utility.MD5Encode(("user=" + user + "&password=" + password + APP_KEY).getBytes("UTF-8"));
			
			LogPrintf("String:" + "user=" + user + "&password=" + password + APP_KEY);
			
			WeiboParameters bundle = new WeiboParameters();
			bundle.add("reqstr", reqstr);
			bundle.add("verify", verify.toLowerCase());
			
			String rlt = mWeiboInstance.request(mContext, url, bundle, "POST", null);
			
			map = (Map<String, Object>) (JSONValue.parse(rlt));
			SetUserID((String) (map.get("userid")));
			SetToken((String) (map.get("token")));

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			map.put("code", "103");
		}
		return map;
	}

	public Map<String, Object> Register(String user, String password) throws WeiboException {

		String url = USER_SERVER + "/register";
		url += "/appcode/" + APP_CODE;
		url += "/datatype/" + DATATYPE;

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			password = Utility.MD5Encode(password.getBytes("UTF-8"));
			String reqstr = base64Encode(("user=" + user + "&password=" + password) .getBytes());
			String verify = Utility.MD5Encode(("user=" + user + "&password=" + password + APP_KEY).getBytes("UTF-8"));

			LogPrintf("String:" + "user=" + user + "&password=" + password + APP_KEY);
			WeiboParameters bundle = new WeiboParameters();
			bundle.add("reqstr", reqstr);
			bundle.add("verify", verify.toLowerCase());
			String rlt = mWeiboInstance.request(mContext, url, bundle, "POST", null);

			map = (Map<String, Object>) (JSONValue.parse(rlt));

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			map.put("code", "103");
		}
		return map;
	}

	public Map<String, Object> UpdatePassword(String oldpwd, String newpwd) throws WeiboException {

		String url = USER_SERVER + "/updatepassword";
		url += "/appcode/" + APP_CODE;
		url += "/datatype/" + DATATYPE;

		Map<String, Object> map = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			map.put("code", "201");
			return map;
		}

		try {
			oldpwd = Utility.MD5Encode(oldpwd.getBytes("UTF-8"));
			newpwd = Utility.MD5Encode(newpwd.getBytes("UTF-8"));
			String userstr = "oldpass=" + oldpwd + "&newpass=" + newpwd;
			String reqstr = base64Encode(userstr.getBytes());
			String verify = Utility.MD5Encode((userstr + APP_KEY).getBytes("UTF-8"));

			WeiboParameters bundle = new WeiboParameters();
			bundle.add("userid", USER_ID);
			bundle.add("token", TOKEN);
			bundle.add("reqstr", reqstr);
			bundle.add("verify", verify.toLowerCase());
			
			String rlt = mWeiboInstance.request(mContext, url, bundle, "POST", null);
			
			map = (Map<String, Object>) (JSONValue.parse(rlt));
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			map.put("code", "103");
		}
		return map;
	}

	public Map<String, Object> Get_user() throws WeiboException {

		Map<String, Object> map = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			map.put("code", "201");
			return map;
		}
		String url = USER_SERVER + "/getuser";
		url += "/appcode/" + APP_CODE;
		url += "/datatype/" + DATATYPE;

		WeiboParameters bundle = new WeiboParameters();
		bundle.add("userid", USER_ID);
		bundle.add("token", TOKEN);
		String rlt = mWeiboInstance.request(mContext, url, bundle, "POST", null);

		map = (Map<String, Object>) (JSONValue.parse(rlt));
		
		return map;
	}

	public Map<String, Object> Get_task(int type, String movieid) throws WeiboException {

		String url = USER_SERVER + "/gettask";
		url += "/appcode/" + APP_CODE;
		url += "/datatype/" + DATATYPE;

		Map<String, Object> map = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			map.put("code", "201");
			return map;
		}

		if (type < 0)
			type = 0;
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("userid", USER_ID);
		bundle.add("token", TOKEN);
		bundle.add("type", Integer.toString(type));
		bundle.add("movieid", movieid);
		String rlt = mWeiboInstance.request(mContext, url, bundle, "POST", null);

		map = (Map<String, Object>) (JSONValue.parse(rlt));

		if (map.containsKey("tasklist")) {
			if (map.get("tasklist") instanceof ArrayList) {
			} else {
				map = Object2List(map, "tasklist");
			}
		}

		LogPrintf(map);
		return map;
	}

	public List<Map<String, Object>> Get_taskres(String infohash) throws WeiboException {
		String url = MEDIA_SERVER + "/videofileinfo/isgz/n/datatype/" + DATATYPE;
		
		// Map<String, Object> map = new HashMap<String, Object>();

		List<Map<String, Object>> map = new ArrayList<Map<String, Object>>();
		if (infohash == null || infohash == "") {
			return map;
		}
		url += "/infohash/" + infohash;
		WeiboParameters bundle = new WeiboParameters();
		LogPrintf("post url:" + url);
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_GET, null);

		LogPrintf("movie detail:" + rlt);
		
		// rlt = rlt.replace(":[]", ":\"\"");
		// rlt = rlt.replace(":{}", ":\"\"");
		// map = (List<Object>)(JSONValue.parse(rlt));

		map = (List<Map<String, Object>>) (JSONValue.parse(rlt));
		LogPrintf(map);
		return map;
	}

	public String Upload_file(String id, String fileid, String server, String sourcefile, String filetype, int slicestart) throws WeiboException {
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			return "0";
		}
		String url = server;

		WeiboParameters bundle = new WeiboParameters();
		bundle.add("sourcefile", sourcefile);
		bundle.add("appcode", APP_CODE);
		bundle.add("userid", USER_ID);
		bundle.add("token", TOKEN);

		if (slicestart > 1) {
			bundle.add("slice", Integer.toString(slicestart));
		}

		if (filetype != null && filetype.length() > 0) {
			bundle.add("thumbnail", filetype);
		}
		File hfile = new File(sourcefile);

		if (!hfile.exists())
			return "0";

		bundle.add("fileid", fileid);
		bundle.add("id", id);

		String rlt = "1";

		AsyncWeiboRunner weiboRunner = new AsyncWeiboRunner(mWeiboInstance);
		weiboRunner.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		// rlt = mWeiboInstance.request(mContext, url, bundle,
		// Utility.HTTPMETHOD_POST, null);

		return rlt;
	}

	public String Upload_file(String id, String fileid, String server, String sourcefile) throws WeiboException {
		return Upload_file(id, fileid, server, sourcefile, "", 1);
	}

	public String Upload_file(String id, String fileid, String server, String sourcefile, int slicestart) throws WeiboException {
		return Upload_file(id, fileid, server, sourcefile, "", slicestart);
	}

	/**
	 * Upload file with thumbnail image.
	 * 
	 * @param fileid       fileid gain by Get_upload_fileid
	 * @param server       upload server url
	 * @param sourcefile   path of local sourcefile
	 * @param filetype     "IMAGE" or "VIDEO"
	 */
	public String Upload_file_with_thumbnail(String id, String fileid, String server, String sourcefile, String filetype) throws WeiboException {
		return Upload_file(id, fileid, server, sourcefile, filetype, 1);
	}

	public String Upload_file_with_thumbnail(String id, String fileid, String server, String sourcefile, String filetype, int slicestart) throws WeiboException {
		return Upload_file(id, fileid, server, sourcefile, filetype, slicestart);
	}

	public Map<String, Object> Get_upload_fileid(int type, String parentid, String filename, String filesize) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/uploadfile";
		WeiboParameters data = new WeiboParameters();
		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		// data.add("type",Integer.toString(type));
		if (parentid != null && parentid.length() > 0)
			data.add("parentid", parentid);

		data.add("filename", filename);
		data.add("filesize", filesize);

		// /datatype/"+DATATYPE+"/appcode/"+APP_CODE

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		bundle.add("datatype", DATATYPE);
		bundle.add("appcode", APP_CODE);
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("upload info:" + rlt);
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Move_file(String fileid, String newparentid, String newname) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/movefile/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		data.add("fileid", fileid);
		if (newparentid != null && newparentid.length() > 0)
			data.add("newparentid", newparentid);

		data.add("newfilename", newname);
		// data.add("filesize", filesize);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("move file info:" + rlt);
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Delete_file(String fileid) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/deletefile/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		data.add("fileid", fileid);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("delete file info:" + rlt);
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Recover_file(String fileid) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/recoverfile/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		data.add("fileid", fileid);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("recover file info:" + rlt);
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Clear_recycler(int type, String fileid) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/clearrecycler/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		data.add("type", Integer.toString(type));
		data.add("fileid", fileid);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("clear info:" + rlt);
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Set_file_password(String fileid, String newpassword) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/setfilepasswd/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		data.add("fileid", fileid);
		if (newpassword != null && newpassword.length() > 0)
			data.add("newpasswd", newpassword);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("set password info:" + rlt);
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Create_dir(int type, String dirname, String parentid) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/createdir/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		data.add("type", Integer.toString(type));
		data.add("dirname", dirname);
		if (parentid != null && parentid.length() > 0)
			data.add("parentid", parentid);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("create dir:" + rlt);
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Get_file_list(int type, String fileid, String parentid) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/getfilelist/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		data.add("type", Integer.toString(type));
		if (fileid != null && fileid.length() > 0)
			data.add("fileid", fileid);
		if (parentid != null && parentid.length() > 0)
			data.add("parentid", parentid);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("get file list:" + rlt);
		rlt = rlt.replace(":{}", ":\"\"");
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		if (retmap.containsKey("count") && Integer.parseInt(retmap.get("count").toString()) == 1) {
			retmap = Object2List(retmap, "filelist");
		}

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Get_file_list(int type, String fileid, String parentid, String isdelete) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/getfilelist/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		data.add("type", Integer.toString(type));
		data.add("isdelete", isdelete);
		if (fileid != null && fileid.length() > 0)
			data.add("fileid", fileid);
		if (parentid != null && parentid.length() > 0)
			data.add("parentid", parentid);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("get file list:" + rlt);
		rlt = rlt.replace(":{}", ":\"\"");
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		if (retmap.containsKey("count") && Integer.parseInt(retmap.get("count").toString()) == 1) {
			retmap = Object2List(retmap, "filelist");
		}

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Get_recommend_list(int type, String source, String reqtime, String images, int num) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();

		String url = Weibo.SERVER + "/Cloud/recommend";
		WeiboParameters data = new WeiboParameters();
		data.add("restype", Integer.toString(type));
		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		if (source != null && source.length() > 0) {
			data.add("source", source);
		}
		if (reqtime != null && reqtime.length() > 0) {
			data.add("reqtime", reqtime);
		}
		if (images != null && images.length() > 0) {
			data.add("image", images);
		}
		if (num > 0) {
			data.add("num", num + "");
		}

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		// /datatype/x/appcode/"+APP_CODE

		bundle.add("datatype", DATATYPE);
		bundle.add("appcode", APP_CODE);
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}

		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("recommend:" + rlt);
		rlt = rlt.replace(":{}", ":\"\"");
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		if (retmap.containsKey("count") && Integer.parseInt(retmap.get("count").toString()) == 1) {
			retmap = Object2List(retmap, "recommend");
		}
		printLog(mContext, "recommend jiexi end");
		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Get_recommend_list(int type, String source, String reqtime) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();

		String url = Weibo.SERVER + "/Cloud/recommend";
		WeiboParameters data = new WeiboParameters();
		data.add("restype", Integer.toString(type));
		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		if (source != null && source.length() > 0) {
			data.add("source", source);
		}
		if (reqtime != null && reqtime.length() > 0) {
			data.add("reqtime", reqtime);
		}

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		// /datatype/x/appcode/"+APP_CODE

		bundle.add("datatype", DATATYPE);
		bundle.add("appcode", APP_CODE);
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}

		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("recommend:" + rlt);
		rlt = rlt.replace(":{}", ":\"\"");
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		if (retmap.containsKey("count") && Integer.parseInt(retmap.get("count").toString()) == 1) {
			retmap = Object2List(retmap, "recommend");
		}
		printLog(mContext, "recommend jiexi end");
		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Mydownload_add(int type, String resid) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/adddownload/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		data.add("restype", Integer.toString(type));
		data.add("resid", resid);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}

		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);

		retmap = (Map<String, Object>) (JSONValue.parse(rlt));

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Mydownload_list(int type, String path) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/getdownload/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		data.add("restype", Integer.toString(type));
		if (path != null && path.length() > 0)
			data.add("path", path);
		// data.add("resid", "2");

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		printCurTime("request start");
		printLog(mContext, "libcloud download request start");
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		printCurTime("request finished");
		printLog(mContext, "libcloud download request end");
		LogPrintf("download_list:" + rlt);
		rlt = rlt.replace(":{}", ":\"\"");
		// rlt = "{\"code\":\"1\",\"description\":\"ok\",\"download\":"+rlt+"}";
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		if (retmap.containsKey("count") && Integer.parseInt(retmap.get("count").toString()) == 1) {
			retmap = Object2List(retmap, "download");
		}
		LogPrintf(retmap);
		printLog(mContext, "libcloud download jiexi end");
		return retmap;
	}

	public Map<String, Object> Mydownload_merge(int type, String path, String resid, String mergeid) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/mergedownload/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		data.add("restype", Integer.toString(type));
		if (path != null && path.length() > 0)
			data.add("path", path);
		else
			data.add("rid", resid);
		data.add("mid", mergeid);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("download_merge:" + rlt);

		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Mydownload_depart(int type, String path, String resid) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/departdownload/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		data.add("restype", Integer.toString(type));
		data.add("path", path);
		data.add("resid", resid);
		data.add("rid", resid);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("download_depart:" + rlt);

		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Get_detail(String resid) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();

		String url = Weibo.SERVER + "/Cloud/detail/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("resid", resid);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("detail:" + rlt);
		rlt = rlt.replace(":{}", ":\"\"");
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		if (retmap.containsKey("count") && Integer.parseInt(retmap.get("count").toString()) == 1) {
			retmap = Object2List(retmap, "detail");
		}

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Start_offlinetranscode(String taskid, String fileid, String width) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}
		String url = USER_SERVER + "/addconvert";
		url += "/appcode/" + APP_CODE;
		url += "/datatype/" + DATATYPE;
		url += "/taskid/" + taskid;
		url += "/fileid/" + fileid;
		url += "/width/" + width;
		url += "/userid/" + USER_ID;
		url += "/token/" + TOKEN;
		System.out.println("start offline transcode url = " + url);
		System.out.println("taskid = " + taskid + "fileid=" + fileid + "width=" + width);

		WeiboParameters data = new WeiboParameters();
		data.add("taskid", taskid);
		data.add("fileid", fileid);
		data.add("width", width);
		data.add("userid", USER_ID);
		data.add("token", TOKEN);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}

		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("detail:" + rlt);
		rlt = rlt.replace(":{}", ":\"\"");
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		
//		if (retmap.containsKey("count") && Integer.parseInt(retmap.get("count").toString()) == 1) {
//			retmap = Object2List(retmap, "detail");
//		}

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Start_transcode(String fileid, String width) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/transcode/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("fileid", fileid);
		data.add("width", width);
		data.add("userid", USER_ID);
		data.add("token", TOKEN);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("detail:" + rlt);
		rlt = rlt.replace(":{}", ":\"\"");
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		
//		if(retmap.containsKey("count") && Integer.parseInt(retmap.get("count").toString()) == 1){
//			retmap = Object2List(retmap,"detail");
//		}

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Get_transpercent(String fileid) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/transpercent/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("fileid", fileid);
		data.add("userid", USER_ID);
		data.add("token", TOKEN);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("detail:" + rlt);
		rlt = rlt.replace(":{}", ":\"\"");
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		
		if (retmap.containsKey("count") && Integer.parseInt(retmap.get("count").toString()) == 1) {
			retmap = Object2List(retmap, "percentlist");
		}

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> get_offline_transpercent(String fileid) {

		Map<String, Object> retmap = new HashMap<String, Object>();
		String url = OFFLINE_TRANSPERCENT_SERVER + "/getTranspercent/isgz/n/datatype/" + DATATYPE;
		url += "/fileid/" + fileid;

		WeiboParameters bundle = new WeiboParameters();
		String rlt = null;
		try {
			rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_GET, null);
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		LogPrintf("offline percent:" + rlt);
		rlt = rlt.replace(":{}", ":\"\"");
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		
		if (retmap.containsKey("count") && Integer.parseInt(retmap.get("count").toString()) == 1) {
			retmap = Object2List(retmap, "percentlist");
		}
		return retmap;
	}

	public Map<String, Object> get_hdplayInfo(String movieid) {

		Map<String, Object> retmap = new HashMap<String, Object>();
		String url = OFFLINE_TRANSPERCENT_SERVER + "/hdplay/format/json";
		url += "/movieid/" + movieid;

		System.out.println("get hdplay post = " + url);
		WeiboParameters bundle = new WeiboParameters();
		String rlt = null;
		try {
			rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_GET, null);
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		LogPrintf("gethdplay:" + rlt);
		if (rlt != null) {
			rlt = rlt.replace(":{}", ":\"\"");
			Object jsonvalue = JSONValue.parse(rlt);
			retmap = List2Object(retmap, jsonvalue, "list");
		}
		
//		retmap = (Map<String,Object>)(JSONValue.parse(rlt));
//		if(retmap.containsKey("count") && Integer.parseInt(retmap.get("count").toString()) == 1){ 
//			retmap = Object2List(retmap,"percentlist"); 
//		}
		
		return retmap;
	}

	public Map<String, Object> Get_search_list(int querytype, int type, String keywords, String childtype) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/search/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		if (keywords != null && keywords.length() > 0) {
			data.add("search", keywords);
		}

		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		data.add("type", Integer.toString(type));
		data.add("querytype", Integer.toString(querytype));
		if (childtype != null && childtype.length() > 0) {
			data.add("childtype", childtype);
		}
		// System.out.println("data = " + data);
		String strdata = Utility.encodeParameters(data);
		// System.out.println("strdata = " + strdata);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("search (" + keywords + ") :" + rlt);
		rlt = rlt.replace(":{}", ":\"\"");
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		if (retmap.containsKey("count") && Integer.parseInt(retmap.get("count").toString()) == 1) {
			if (querytype == 1)
				retmap = Object2List(retmap, "download");
			else if (querytype == 2)
				retmap = Object2List(retmap, "filelist");
			else if (querytype == 3)
				retmap = Object2List(retmap, "datalist");
		}

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Get_datatype(int type) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/datatype/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("type", Integer.toString(type));
		data.add("userid", USER_ID);
		data.add("token", TOKEN);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);

		LogPrintf("datatype=" + rlt);

		rlt = rlt.replace(":{}", ":\"\"");
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		
//		if(retmap.containsKey("count") && Integer.parseInt(retmap.get("count").toString()) == 1){
//			retmap = Object2List(retmap,"detail");
//		}

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Get_capacity() throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/getsize/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("userid", USER_ID);
		data.add("token", TOKEN);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("getsize:" + rlt);
		rlt = rlt.replace(":{}", ":\"\"");
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		
//		if(retmap.containsKey("count") && Integer.parseInt(retmap.get("count").toString()) == 1){
//			retmap = Object2List(retmap,"detail");
//		}

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Mydownload_del(int type, String resid, String childid) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/deldownload/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		data.add("restype", Integer.toString(type));
		data.add("resid", resid);
		data.add("id", resid);
		/*
		 * if(type ==3){ data.add("num",childid); }
		 */
		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("mydownload del:" + rlt);
		rlt = rlt.replace(":{}", ":\"\"");
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		
//		if(retmap.containsKey("count") && Integer.parseInt(retmap.get("count").toString()) == 1){
//			retmap = Object2List(retmap,"detail");
//		}

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Mydownload_rename(int type, String resid, String newname) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/renamedownload/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		data.add("restype", Integer.toString(type));
		data.add("resid", resid);
		data.add("id", resid);
		data.add("newname", newname);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("mydownload rename:" + rlt);
		rlt = rlt.replace(":{}", ":\"\"");
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		
//		if(retmap.containsKey("count") && Integer.parseInt(retmap.get("count").toString()) == 1){
//			retmap = Object2List(retmap,"detail");
//		}

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Get_upload_record() throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}

		String url = Weibo.SERVER + "/Cloud/getuploadrecord/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("userid", USER_ID);
		data.add("token", TOKEN);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("get records:" + rlt);
		rlt = rlt.replace(":{}", ":\"\"");
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		if (retmap.containsKey("count") && Integer.parseInt(retmap.get("count").toString()) == 1) {
			retmap = Object2List(retmap, "filelist");
		}

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> Get_movie_detail(String mid, String detail_type, int http_group) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();

		String url = MEDIA_SERVER + "/moviedetail/isgz/n/datatype/" + DATATYPE;

		url += "/mid/" + mid;
		if (detail_type != null && detail_type.length() > 0)
			url += "/type/" + detail_type;

		if (http_group > 0)
			url += "/http/group";
		System.out.println("get movie detail : post url = " + url);
		WeiboParameters bundle = new WeiboParameters();
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_GET, null);
		LogPrintf("movie detail:" + rlt);
		rlt = rlt.replace(":[]", ":\"\"");
		rlt = rlt.replace(":{}", ":\"\"");
		// rlt = "{\"code\":\"1\",\"description\":\"ok\",\"download\":"+rlt+"}";
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		
//		if (retmap.containsKey("count") && Integer.parseInt(retmap.get("count").toString()) == 1) {
//			retmap = Object2List(retmap, "movie_item");
//		}
		

		// {"movie_list":[],"http_list":{"http_count":"0"},"photo_list":{"photo_count":"0"},"clips_list":{"clips_count":"0"}}

		if (retmap.containsKey("movie_list")) {
			if (retmap.get("movie_list") instanceof Map) {
				Map<String, Object> tmp = (Map<String, Object>) (retmap.get("movie_list"));
				if (tmp.containsKey("movie_item")) {
					if (tmp.get("movie_item") instanceof ArrayList) {
					} else if (tmp.get("movie_item") instanceof Object) {
						tmp = Object2List(tmp, "movie_item");
						retmap.put("movie_list", tmp);
					} else {
						retmap.put("movie_list", null);
					}
				}
			} else {
				retmap.put("movie_list", null);
			}
		} else {
			LogPrintf("No movie_list");
		}

		if (retmap.containsKey("http_list")) {
			Map<String, Object> tmp = (Map<String, Object>) (retmap.get("http_list"));
			if (tmp.containsKey("http_item")) {
				if (tmp.get("http_item") instanceof ArrayList) {
				} else {
					tmp = Object2List(tmp, "http_item");
					retmap.put("http_list", tmp);
				}
			}
		} else {
			LogPrintf("No http_list");
		}

		if (retmap.containsKey("photo_list")) {
			Map<String, Object> tmp = (Map<String, Object>) (retmap.get("photo_list"));
			if (tmp.containsKey("photo_item")) {
				if (tmp.get("photo_item") instanceof ArrayList) {
				} else {
					tmp = Object2List(tmp, "photo_item");
					retmap.put("photo_list", tmp);
				}
			}
		} else {
			LogPrintf("No photo_list");
		}

		if (retmap.containsKey("clips_list")) {
			Map<String, Object> tmp = (Map<String, Object>) (retmap.get("clips_list"));
			if (tmp.containsKey("clips_item")) {
				if (tmp.get("clips_item") instanceof ArrayList) {
				} else {
					tmp = Object2List(tmp, "clips_item");
					retmap.put("clips_list", tmp);
				}
			}
		} else {
			LogPrintf("No clips_list");
		}

		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> user_onlineorder(String movieid, String videoid, String isonline) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}
		String url = Weibo.SERVER + "/Cloud/onlineorder/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		data.add("movieid", movieid);
		data.add("videoid", videoid);
		data.add("isonline", isonline);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("user online order:" + rlt);
		rlt = rlt.replace(":{}", ":\"\"");
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		LogPrintf(retmap);
		return retmap;
	}

	public Map<String, Object> reset_filepasswd(String username, String passwd, String newpasswd, String fileid) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "" || TOKEN == null) {
			retmap.put("code", "202");
			return retmap;
		}
		String url = Weibo.SERVER + "/Cloud/resetfilepasswd/datatype/" + DATATYPE + "/appcode/" + APP_CODE;
		WeiboParameters data = new WeiboParameters();
		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		data.add("username", username);
		data.add("password", passwd);
		data.add("fileid", fileid);
		data.add("newpasswd", newpasswd);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")) .toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			retmap.put("code", "301");
			return retmap;
		}
		String rlt = mWeiboInstance.request(mContext, url, bundle, Utility.HTTPMETHOD_POST, null);
		LogPrintf("reset file password :" + rlt);
		rlt = rlt.replace(":{}", ":\"\"");
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		LogPrintf(retmap);
		return retmap;
	}

	private void printCurTime(String flag) {
		final Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR); // ��
		int mMonth = c.get(Calendar.MONTH) + 1;// ��
		int mDate = c.get(Calendar.DATE);
		int mHour = c.get(Calendar.HOUR);
		int mMinute = c.get(Calendar.MINUTE);
		int mSecont = c.get(Calendar.SECOND);
		int mMilean = c.get(Calendar.MILLISECOND);
		System.out.println(flag + " " + mYear + "-" + mMonth + "-" + mDate
				+ " " + mHour + ":" + mMinute + ":" + mSecont + " " + mMilean);
	}

	private static String createDir(Context context) {
		String dir = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			dir = Environment.getExternalStorageDirectory().getAbsolutePath();
		} else {
			dir = context.getFilesDir().toString();
		}
		return dir;
	}

	public static void printLog(Context context, String value) {
		if (!DEBUG)
			return;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日HH时");
			SimpleDateFormat format2 = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
			Date date = new Date(System.currentTimeMillis());
			String fileName = format.format(date);
			// String
			// dividerLine="\n\n\n\n====================="+format2.format(date)+"=====================\n";

			final Calendar c = Calendar.getInstance();
			int mYear = c.get(Calendar.YEAR);
			int mMonth = c.get(Calendar.MONTH) + 1;
			int mDate = c.get(Calendar.DATE);
			int mHour = c.get(Calendar.HOUR);
			int mMinute = c.get(Calendar.MINUTE);
			int mSecont = c.get(Calendar.SECOND);
			int mMilean = c.get(Calendar.MILLISECOND);
			String dividerLine = "\n\n\n\n==============" + mYear + "-"
					+ mMonth + "-" + mDate + " " + mHour + ":" + mMinute + ":"
					+ mSecont + " " + mMilean + "=====================\n";

			File logFile = new File(createDir(context), "udisk/log/" + fileName + ".txt");
			logFile.getParentFile().mkdirs();
			logFile.createNewFile();
			FileWriter writer = new FileWriter(logFile, true);

			StringBuilder sb = new StringBuilder(dividerLine);
			sb.append(value).append("\n");
			writer.write(sb.toString());
			writer.close();
		} catch (IOException e2) {
			Log.e("test", e2.getMessage());
		}
	}
}