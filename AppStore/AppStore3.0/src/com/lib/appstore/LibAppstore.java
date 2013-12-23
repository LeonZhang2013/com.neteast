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
package com.lib.appstore;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONValue;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.hs.activity.AppStore;
import com.hs.domain.AppBean;
import com.hs.domain.User;
import com.lib.net.ImageLoader;
import com.lib.net.Utility;
import com.lib.net.WeiboException;
import com.lib.net.WeiboParameters;

public class LibAppstore {
	private static LibAppstore mAppstoreInstance;
	private static Context mContext;
	private static ImageLoader imageloader;
	private static String USER_SERVER;
	private static String SERVER;
	private static String APP_CODE;
	private static String APP_KEY;
	private static String USER_ID;
	private static String TOKEN;
	private static String USERNAME;
	private static String REVIEW_SERVER;

	// 从本地配置文件获取 server地址。 (未完善)
	public static void getserver() {
		File cacheDir = null;
		String cacheDirPath = android.os.Environment.getExternalStorageDirectory() + "AppStore/server";
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(cacheDirPath);
		if (!cacheDir.exists())
			cacheDir.mkdirs();
		Map<String, Object> map;
		File file = new File(cacheDirPath + "server.xml");
		if (!file.exists()) {
			return;
		}
		FileInputStream read;
		XMLHander myExampleHandler = null;
		try {
			myExampleHandler = new XMLHander();
			myExampleHandler.setmap();
			read = new FileInputStream(file);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			xr.setContentHandler(myExampleHandler);
			xr.parse(new InputSource(read));
			map = myExampleHandler.getmap();
			if (map.size() > 0) {
				// testsver = (String) map.get("server");
				// testusver = (String) map.get("userver");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static LibAppstore getInstance(Context context) {
		if (mAppstoreInstance == null) {
			mAppstoreInstance = new LibAppstore();
			SERVER = AppStore.SERVER;
			REVIEW_SERVER = AppStore.USERCENTER_SERVER;
			USER_SERVER = AppStore.USERCENTER_SERVER;
			APP_CODE = AppStore.APP_CODE;
			APP_KEY = AppStore.APP_KEY;
			mContext = context;
			imageloader = ImageLoader.getInstanse(context);
		}
		return mAppstoreInstance;
	}
	
	public LibAppstore() {
		super();
		SetUserID(Integer.toString(User.getUserId()));
		SetToken(User.getToken());
		SetUserName(User.getUserName());
	}

	private Document getDocument(String rlt) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document doc = null;
		try {
			db = factory.newDocumentBuilder();
			InputSource inStream = new InputSource();
			inStream.setCharacterStream(new StringReader(rlt));
			doc = db.parse(inStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	private String getTextByTagName(org.w3c.dom.Element nameElement, String name) {
		NodeList nl = nameElement.getElementsByTagName(name);
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
				Node n = nl.item(i);
				if (n.getChildNodes().getLength() > 0) {
					String strValue = n.getFirstChild().getNodeValue();
					return strValue;
				} else
					return "";
			}
		}
		return "";
	}

	private String base64Encode(byte[] toencode) {
		return com.lib.net.Base64.encodeToString(toencode, android.util.Base64.NO_WRAP);
	}

	public void SetRetainedHeapSize(int MaxInMB) {
		imageloader.SetRetainedHeapInMB(MaxInMB);
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

	public void SetUserName(String userName) {
		USERNAME = userName;
	}

	public void SetToken(String token) {
		TOKEN = token;
	}

	public void SetAppcode(String appcode) {
		APP_CODE = appcode;
	}

	public String GetUserID() {
		return USER_ID;
	}

	public String GetToken() {
		return TOKEN;
	}

	public void DisplayImage(String url, ImageView view) {
		if (url != null && url != "") {
			imageloader.setImage(url, view);
		}
	}

	public void DisplayScaledImage(String url, ImageView view, int scale) {
		if (url != null && url != "" && scale >= 1) {
			imageloader.setImage(url, view, scale);
		}
	}

	public List<Map<String, Object>> Get_hot_slide(int num) throws WeiboException {
		String url = SERVER + "/Interface/slides/format/json";
		url += "/n/" + Integer.toString(num);
		WeiboParameters bundle = new WeiboParameters();

		String rlt = Utility.openUrl(mContext, url, bundle, "GET");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			JSONArray jsonArray = new JSONArray(rlt);
			JSONObject jsonObject;
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", jsonObject.optString("id", ""));
				map.put("image", jsonObject.optString("logo", ""));
				map.put("image_show", jsonObject.optString("img_show", ""));
				map.put("title", jsonObject.optString("title", ""));
				map.put("rating", jsonObject.optString("score", ""));
				map.put("url", jsonObject.optString("file_path", ""));
				map.put("version", jsonObject.optString("version", ""));
				map.put("ctitle", jsonObject.optString("channel", ""));
				list.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return list;
	}

	public List<Map<String, Object>> Get_hot_list(int page, int per) throws WeiboException {
		// long begin0 = System.currentTimeMillis();
		String url = SERVER + "/Interface/index/format/json";
		url += "/p/" + page;
		url += "/n/" + per;
		WeiboParameters bundle = new WeiboParameters();

		String rlt = Utility.openUrl(mContext, url, bundle, "GET");
		// long end0 = System.currentTimeMillis();
		// System.out.println("����ִ�к�ʱ:" + (end0 - begin0) + " ����");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			JSONArray jsonArray = new JSONArray(rlt);
			JSONObject jsonObject;
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", jsonObject.optString("id", ""));
				map.put("image", jsonObject.optString("logo", ""));
				map.put("title", jsonObject.optString("title", ""));
				map.put("rating", jsonObject.optString("score", ""));
				map.put("version", jsonObject.optString("version", ""));
				map.put("url", jsonObject.optString("file_path", ""));
				map.put("type", jsonObject.optString("type", "app"));
				map.put("total", jsonObject.optString("total", "0"));
				map.put("ctitle", jsonObject.optString("channel", ""));
				list.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<AppBean> Get_latest_list(int page, int per) throws WeiboException {
		String url = SERVER + "/Interface/newly/format/json";
		url += "/p/" + page;
		url += "/n/" + per;
		WeiboParameters bundle = new WeiboParameters();
		String rlt = Utility.openUrl(mContext, url, bundle, "GET");

		List<AppBean> list = new ArrayList<AppBean>();

		try {
			JSONArray jsonArray = new JSONArray(rlt);
			JSONObject jsonObject;
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
/*				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", jsonObject.optString("id", ""));
				map.put("image", jsonObject.optString("logo", ""));
				map.put("title", jsonObject.optString("title", ""));
				map.put("rating", jsonObject.optString("score", ""));
				map.put("version", jsonObject.optString("version", ""));
				map.put("url", jsonObject.optString("file_path", ""));
				map.put("type", jsonObject.optString("type", "app"));
				map.put("total", jsonObject.optString("total", "0"));
				map.put("ctitle", jsonObject.optString("channel", ""));
				list.add(map);*/
				list.add(AppBean.instance(jsonObject));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Map<String, Object>> Get_top_list(String type, int num) throws WeiboException {
		String url = SERVER + "/Interface/top/format/json";
		url += "/type/" + type;
		url += "/n/" + num;
		WeiboParameters bundle = new WeiboParameters();
		String rlt = Utility.openUrl(mContext, url, bundle, "GET");

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			JSONArray jsonArray = new JSONArray(rlt);
			JSONObject jsonObject;
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", jsonObject.optString("id", ""));
				map.put("image", jsonObject.optString("logo", ""));
				map.put("title", jsonObject.optString("title", ""));
				map.put("rating", jsonObject.optString("score", ""));
				map.put("version", jsonObject.optString("version", ""));
				map.put("url", jsonObject.optString("file_path", ""));
				map.put("type", jsonObject.optString("type", "app"));
				map.put("ctitle", jsonObject.optString("channel", ""));
				map.put("size", jsonObject.optString("file_size", "0.0K"));
				list.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Map<String, Object>> Get_category_title(String type) throws WeiboException {
		String url = SERVER + "/Interface/cat_list/format/json";
		if (!(type.equals("app")) && !(type.equals("game"))) {
			type = "app";
		}
		url += "/apptype/" + type;
		// System.out.println("url: "+ url);
		WeiboParameters bundle = new WeiboParameters();
		String rlt = Utility.openUrl(mContext, url, bundle, "GET");
		// System.out.println("rlt: "+ rlt);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			JSONArray jsonArray = new JSONArray(rlt);
			JSONObject jsonObject;
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("cid", jsonObject.optString("id", ""));
				map.put("ctitle", jsonObject.optString("title", ""));
				list.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<AppBean> Get_category_list(String type, int page, int per, String cid) throws WeiboException {

		String url = SERVER + "/Interface";
		if (!(type.equals("app")) && !(type.equals("game"))) {
			type = "app";
		}
		url += "/" + type + "/format/json";
		url += "/p/" + Integer.toString(page);
		url += "/n/" + Integer.toString(per);
		url += "/cid/" + cid;

		WeiboParameters bundle = new WeiboParameters();
		String rlt = Utility.openUrl(mContext, url, bundle, "GET");

		List<AppBean> list = new ArrayList<AppBean>();

		try {
			JSONArray jsonArray = new JSONArray(rlt);
			JSONObject jsonObject;
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
/*				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", jsonObject.optString("id", ""));
				map.put("image", jsonObject.optString("logo", ""));
				map.put("title", jsonObject.optString("title", ""));
				map.put("rating", jsonObject.optString("score", ""));
				map.put("version", jsonObject.optString("version", ""));
				map.put("url", jsonObject.optString("file_path", ""));
				map.put("type", jsonObject.optString("type", "app"));
				map.put("total", jsonObject.optString("total", "0"));
				map.put("ctitle", jsonObject.optString("channel", ""));*/
				list.add(AppBean.instance(jsonObject));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Map<String, Object>> Get_search_list(String keyword, int page, int per) throws WeiboException {

		String url = SERVER + "/Interface/search/format/json";
		if (keyword != null && keyword.length() > 0) {
			try {
				url += "/k/" + java.net.URLEncoder.encode(keyword, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		url += "/n/" + Integer.toString(per);
		url += "/p/" + Integer.toString(page);
		WeiboParameters bundle = new WeiboParameters();
		// System.out.println(url);
		String rlt = Utility.openUrl(mContext, url, bundle, "GET");

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			JSONArray jsonArray = new JSONArray(rlt);
			JSONObject jsonObject;
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", jsonObject.optString("id", ""));
				map.put("image", jsonObject.optString("logo", ""));
				map.put("title", jsonObject.optString("title", ""));
				map.put("rating", jsonObject.optString("score", ""));
				map.put("type", jsonObject.optString("type", "app"));
				map.put("version", jsonObject.optString("version", ""));
				map.put("author", jsonObject.optString("owner", ""));
				map.put("size", jsonObject.optString("file_size", "0.0K"));
				map.put("description", jsonObject.optString("description", ""));
				map.put("url", jsonObject.optString("file_path", ""));
				map.put("total", jsonObject.optString("total", "0"));
				map.put("ctitle", jsonObject.optString("channel", ""));
				list.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public Map<String, Object> Get_details(String id) throws WeiboException {

		String url = SERVER + "/Interface/details/format/json";
		url += "/id/" + id;

		WeiboParameters bundle = new WeiboParameters();
		String rlt = Utility.openUrl(mContext, url, bundle, "GET");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			JSONObject jsonObject = new JSONObject(rlt);
			map.put("id", jsonObject.optString("id", ""));
			map.put("image", jsonObject.optString("logo", ""));
			map.put("title", jsonObject.optString("title", ""));
			map.put("rating", jsonObject.optString("score", ""));
			map.put("type", jsonObject.optString("type", "app"));
			map.put("version", jsonObject.optString("version", ""));
			map.put("author", jsonObject.optString("owner", ""));
			map.put("size", jsonObject.optString("file_size", "0.0K"));
			map.put("description", jsonObject.optString("description", ""));
			map.put("cid", jsonObject.optString("cid", "1"));
			map.put("ctitle", jsonObject.optString("channel", ""));
			map.put("done", jsonObject.optString("download_count", "0"));
			map.put("date", jsonObject.optString("release_time", ""));
			map.put("os", jsonObject.optString("os_ver", ""));
			map.put("pay", jsonObject.optString("price", "free"));
			map.put("url", jsonObject.optString("file_path", ""));
			map.put("hash", jsonObject.optString("file_hash", ""));
			map.put("source", jsonObject.optString("source", ""));
			map.put("package", jsonObject.optString("packagename", ""));
			

			JSONArray imgarray = jsonObject.getJSONArray("img");
			List<String> imglist = new ArrayList<String>();
			for (int i = 0; i < imgarray.length(); i++) {
				imglist.add(imgarray.getString(i));
			}
			map.put("screenshot", imglist);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return map;
	}

	public List<AppBean> Get_latest_version(String appid) {

		String url = SERVER + "/Interface/get_latest_version/format/json";

		url += "/content_id/" + appid;
		WeiboParameters bundle = new WeiboParameters();
		String rlt = Utility.openUrl(mContext, url, bundle, "GET");
		List<AppBean> list = new ArrayList<AppBean>();
		try {
			JSONArray jsonArray = new JSONArray(rlt);
			JSONObject jsonObject;
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				AppBean bean = new AppBean();
				bean.setId(jsonObject.optInt("id", 0));
				bean.setImage(jsonObject.optString("logo", ""));
				bean.setTitle(jsonObject.optString("title", ""));
				bean.setUrl(jsonObject.optString("file_path", ""));
				bean.setVersion(jsonObject.optString("version", ""));
				list.add(bean);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Map<String, Object>> Get_related_list(String appid, int num) throws WeiboException {

		String url = SERVER + "/Interface/get_related/format/json";

		url += "/id/" + appid;

		if (num > 0) {
			url += "/n/" + Integer.toString(num);
		}
		WeiboParameters bundle = new WeiboParameters();
		// System.out.println(url);
		String rlt = Utility.openUrl(mContext, url, bundle, "GET");

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			JSONArray jsonArray = new JSONArray(rlt);
			JSONObject jsonObject;
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", jsonObject.optString("id", ""));
				map.put("image", jsonObject.optString("logo", ""));
				map.put("title", jsonObject.optString("title", ""));
				map.put("rating", jsonObject.optString("score", ""));
				map.put("url", jsonObject.optString("file_path", ""));
				map.put("type", jsonObject.optString("type", "app"));
				map.put("size", jsonObject.optString("file_size", "0"));
				map.put("author", jsonObject.optString("owner", ""));
				map.put("version", jsonObject.optString("version", ""));
				map.put("description", jsonObject.optString("description", ""));
				map.put("ctitle", jsonObject.optString("channel", ""));
				map.put("total", jsonObject.optString("total", "0"));
				list.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public Map<String, Object> Myfavorite_list() throws WeiboException {
		Map<String, Object> map = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "") {
			map.put("code", "201");
			return map;
		}
		String url = SERVER + "/Operation/fav_list/format/json";
		url += "/user_id/" + USER_ID;

		WeiboParameters bundle = new WeiboParameters();
		String rlt = Utility.openUrl(mContext, url, bundle, "GET");

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			JSONArray jsonArray = new JSONArray(rlt);
			JSONObject jsonObject;
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				Map<String, Object> map1 = new HashMap<String, Object>();
				map1.put("id", jsonObject.optString("content_id", ""));
				map1.put("image", jsonObject.optString("logo", ""));
				map1.put("title", jsonObject.optString("title", ""));
				map1.put("rating", jsonObject.optString("score", ""));
				map1.put("type", jsonObject.optString("type", "app"));
				map1.put("version", jsonObject.optString("version", ""));
				list.add(map1);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("list", list);
		map.put("code", "1");
		return map;
	}

	public Map<String, Object> Myfavorite_add(String appid) throws WeiboException {
		Map<String, Object> map = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "") {
			map.put("code", "201");
			return map;
		}
		String url = SERVER + "/Operation/add_fav";
		url += "/content_id/" + appid;
		url += "/user_id/" + USER_ID;

		WeiboParameters bundle = new WeiboParameters();
		String rlt = Utility.openUrl(mContext, url, bundle, "GET");
		// System.out.println("String:"+rlt+"|size:"+rlt.length());
		if (rlt.equals("0")) {
			map.put("code", "1");
		} else {
			map.put("code", "101");
		}
		return map;
	}

	public Map<String, Object> Myfavorite_del(String appid) throws WeiboException {
		Map<String, Object> map = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "") {
			map.put("code", "201");
			return map;
		}
		String url = SERVER + "/Operation/del_fav";
		url += "/content_id/" + appid;
		url += "/user_id/" + USER_ID;

		WeiboParameters bundle = new WeiboParameters();
		String rlt = Utility.openUrl(mContext, url, bundle, "GET");
		// System.out.println("String:"+rlt+"|size:"+rlt.length());
		if (rlt.equals("0")) {
			map.put("code", "1");
		} else {
			map.put("code", "101");
		}
		return map;
	}

	public Map<String, Object> Login(String user, String password) throws WeiboException {

		String url = USER_SERVER + "/login";
		url += "/appcode/" + APP_CODE;

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			password = Utility.MD5Encode(password.getBytes("UTF-8"));
			String reqstr = base64Encode(("user=" + user + "&password=" + password).getBytes());
			String verify = Utility.MD5Encode(("user=" + user + "&password=" + password + APP_KEY).getBytes("UTF-8"));

			// System.out.println("String:"+"user="+user+"&password="+password+APP_KEY);
			WeiboParameters bundle = new WeiboParameters();
			bundle.add("reqstr", reqstr);
			bundle.add("verify", verify.toLowerCase());
			String rlt = Utility.openUrl(mContext, url, bundle, "POST");

			Document doc = getDocument(rlt);

			if (doc != null) {
				NodeList nl = doc.getElementsByTagName("result");
				if (nl.getLength() > 0) {
					org.w3c.dom.Element nameElement = (org.w3c.dom.Element) nl.item(0);
					map.put("code", getTextByTagName(nameElement, "code"));
					map.put("userid", getTextByTagName(nameElement, "userid"));
					map.put("token", getTextByTagName(nameElement, "token"));
					SetUserID((String) (map.get("userid")));
					SetToken((String) (map.get("token")));
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			map.put("code", "103");
		}
		return map;
	}

	public Map<String, Object> Register(String user, String password) throws WeiboException {

		String url = USER_SERVER + "/register";
		url += "/appcode/" + APP_CODE;

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			password = Utility.MD5Encode(password.getBytes("UTF-8"));
			String reqstr = base64Encode(("user=" + user + "&password=" + password).getBytes());
			String verify = Utility.MD5Encode(("user=" + user + "&password=" + password + APP_KEY).getBytes("UTF-8"));

			// System.out.println("String:"+"user="+user+"&password="+password+APP_KEY);
			WeiboParameters bundle = new WeiboParameters();
			bundle.add("reqstr", reqstr);
			bundle.add("verify", verify.toLowerCase());
			String rlt = Utility.openUrl(mContext, url, bundle, "POST");

			Document doc = getDocument(rlt);

			if (doc != null) {
				NodeList nl = doc.getElementsByTagName("result");
				if (nl.getLength() > 0) {
					org.w3c.dom.Element nameElement = (org.w3c.dom.Element) nl.item(0);
					map.put("code", getTextByTagName(nameElement, "code"));
					map.put("userid", getTextByTagName(nameElement, "userid"));
					map.put("token", getTextByTagName(nameElement, "token"));
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			map.put("code", "103");
		}
		return map;
	}

	public Map<String, Object> Get_user() throws WeiboException {

		Map<String, Object> map = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "") {
			map.put("code", "201");
			return map;
		}
		String url = USER_SERVER + "/getuser";
		url += "/appcode/" + APP_CODE;

		WeiboParameters bundle = new WeiboParameters();
		bundle.add("userid", USER_ID);
		bundle.add("token", TOKEN);
		String rlt = Utility.openUrl(mContext, url, bundle, "POST");

		Document doc = getDocument(rlt);

		if (doc != null) {
			NodeList nl = doc.getElementsByTagName("result");
			if (nl.getLength() > 0) {
				org.w3c.dom.Element nameElement = (org.w3c.dom.Element) nl.item(0);
				map.put("code", getTextByTagName(nameElement, "code"));
				map.put("userid", getTextByTagName(nameElement, "user_id"));
				map.put("starttime", getTextByTagName(nameElement, "start_time"));
				map.put("endtime", getTextByTagName(nameElement, "end_time"));
				map.put("processcount", getTextByTagName(nameElement, "process_count"));
				map.put("spacesize", getTextByTagName(nameElement, "space_size"));
				map.put("keepdays", getTextByTagName(nameElement, "keep_days"));
				map.put("usedspace", getTextByTagName(nameElement, "used_space"));
				map.put("freespace", getTextByTagName(nameElement, "free_space"));
			}
		}
		return map;
	}

	public Map<String, Object> add_user_score(String score, String type, String id, String ip) throws WeiboException {
		Map<String, Object> map = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "") {
			map.put("code", "201");
			return map;
		}
		String url = REVIEW_SERVER + "/score";
		url += "/appcode/" + APP_CODE + "/datatype/x";

		WeiboParameters bundle = new WeiboParameters();
		bundle.add("userid", USER_ID);
		bundle.add("username", USERNAME);
		bundle.add("score", score);
		bundle.add("restype", type);
		bundle.add("resid", id);
		bundle.add("clientip", ip);
		String rlt = Utility.openUrl(mContext, url, bundle, "POST");

		// System.out.println("score rlt = " + rlt);

		Document doc = getDocument(rlt);

		if (doc != null) {
			NodeList nl = doc.getElementsByTagName("result");
			if (nl.getLength() > 0) {
				org.w3c.dom.Element nameElement = (org.w3c.dom.Element) nl.item(0);
				map.put("code", getTextByTagName(nameElement, "code"));
				map.put("score", getTextByTagName(nameElement, "score"));
			}
		}
		return map;
	}

	public Map<String, Object> get_user_score(String type, String id) throws WeiboException {
		Map<String, Object> map = new HashMap<String, Object>();

		String url = REVIEW_SERVER + "/getscore";
		url += "/appcode/" + APP_CODE + "/datatype/x";

		WeiboParameters bundle = new WeiboParameters();
		bundle.add("restype", type);
		bundle.add("resid", id);
		String rlt = Utility.openUrl(mContext, url, bundle, "POST");

		// System.out.println("getscore rlt = " + rlt);

		Document doc = getDocument(rlt);

		if (doc != null) {
			NodeList nl = doc.getElementsByTagName("result");
			if (nl.getLength() > 0) {
				org.w3c.dom.Element nameElement = (org.w3c.dom.Element) nl.item(0);
				map.put("code", getTextByTagName(nameElement, "code"));
				map.put("count", getTextByTagName(nameElement, "count"));
				map.put("score", getTextByTagName(nameElement, "score"));
			}
		}
		return map;
	}

	public Map<String, Object> add_user_review(String content, String type, String id, String ip) throws WeiboException {
		Map<String, Object> map = new HashMap<String, Object>();
		if (USER_ID == "" || TOKEN == "") {
			map.put("code", "201");
			return map;
		}
		String url = REVIEW_SERVER + "/addreview";
		url += "/appcode/" + APP_CODE + "/datatype/x";

		WeiboParameters data = new WeiboParameters();
		data.add("userid", USER_ID);
		data.add("token", TOKEN);
		data.add("restype", type);
		data.add("resid", id);
		data.add("username", USERNAME);
		data.add("clientip", ip);
		data.add("content", content);

		String strdata = Utility.encodeParameters(data);
		WeiboParameters bundle = new WeiboParameters();
		bundle.add("reqstr", base64Encode(strdata.getBytes()));
		try {
			bundle.add("verify", Utility.MD5Encode((strdata + APP_KEY).getBytes("UTF-8")).toLowerCase());
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			map.put("code", "301");
			return map;
		}

		String rlt = Utility.openUrl(mContext, url, bundle, "POST");
		// System.out.println("rlt = " + rlt);
		Document doc = getDocument(rlt);

		if (doc != null) {
			NodeList nl = doc.getElementsByTagName("result");
			if (nl.getLength() > 0) {
				org.w3c.dom.Element nameElement = (org.w3c.dom.Element) nl.item(0);
				map.put("code", getTextByTagName(nameElement, "code"));
			}
		}
		return map;
	}

	public Map<String, Object> get_user_review(String restype, String id, String type) throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();

		String url = REVIEW_SERVER + "/getreview";
		url += "/appcode/" + APP_CODE + "/datatype/j";

		WeiboParameters data = new WeiboParameters();
		data.add("restype", restype);
		data.add("resid", id);
		if (type != null && type.length() > 0) {
			data.add("type", type);
		}

		String strdata = Utility.encodeParameters(data);
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

		String rlt = Utility.openUrl(mContext, url, bundle, "POST");
		System.out.println("rlt = " + rlt);
		rlt = rlt.replace(":{}", ":\"0\"");
		retmap = (Map<String, Object>) (JSONValue.parse(rlt));
		if (retmap.containsKey("count") && Integer.parseInt(retmap.get("count").toString()) == 1) {
			retmap = Object2List(retmap, "review");
		}
		return retmap;

	}

	public Map<String, Object> login_report(String login, String userid, String deviceid, String ip, String mac)
			throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		String url = SERVER + "/Operation/appuserstat/format/json";

		url += "/user_login/" + login;
		url += "/user_id/" + userid;
		url += "/user_ip/" + ip;
		url += "/user_mac/" + mac;
		url += "/DeviceId" + deviceid;

		WeiboParameters bundle = new WeiboParameters();

		String rlt = Utility.openUrl(mContext, url, bundle, "GET");
		// retmap = (Map<String,Object>)(JSONValue.parse(rlt));
		System.out.println("login report rlt = " + rlt);
		return retmap;
	}

	public Map<String, Object> search_report(String searchkey, String userid, String deviceid, String ip, String mac)
			throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		String url = SERVER + "/Operation/appsearchstat/format/json";

		url += "/searchkey/" + searchkey;
		url += "/user_id/" + userid;
		url += "/user_ip/" + ip;
		url += "/user_mac/" + mac;
		url += "/DeviceId" + deviceid;

		WeiboParameters bundle = new WeiboParameters();

		String rlt = Utility.openUrl(mContext, url, bundle, "GET");
		System.out.println("search report rlt = " + rlt);
		// retmap = (Map<String,Object>)(JSONValue.parse(rlt));
		return retmap;
	}

	public Map<String, Object> download_report(String appid, String userid, String deviceid, String ip, String mac)
			throws WeiboException {
		Map<String, Object> retmap = new HashMap<String, Object>();
		String url = SERVER + "/Operation/appdownstat/format/json";

		url += "/content_id/" + appid;
		url += "/user_id/" + userid;
		url += "/user_ip/" + ip;
		url += "/user_mac/" + mac;
		url += "/DeviceId/" + deviceid;

		WeiboParameters bundle = new WeiboParameters();

		String rlt = Utility.openUrl(mContext, url, bundle, "GET");
		System.out.println("download report rlt = " + rlt);
		// retmap = (Map<String,Object>)(JSONValue.parse(rlt));
		return retmap;
	}

	private Map<String, Object> Object2List(Map<String, Object> retmap, String key) {
		if (retmap.containsKey(key)) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list.add((Map<String, Object>) (retmap.get(key)));
			retmap.put(key, list);
		}
		return retmap;
	}
	
	
	public void userReport(String login,String userid,String deviceid){
		userReportAsync userReport = new userReportAsync();
		userReport.execute(login,userid,deviceid);
	}
	 
	class userReportAsync extends AsyncTask<String,Object,String> {
		@Override
		protected String doInBackground(String... params) {
			String loginStatus = params[0];
			String userid = params[1];
			String deviceid = params[2];
			try {
				System.out.println("longin report : status= " + loginStatus + "userid = " + userid + "deviceid = " + deviceid );
				Map<String, Object> retmap = login_report(loginStatus, userid, deviceid, "", "");
				System.out.println("login report retmap = " + retmap);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}
	}
}