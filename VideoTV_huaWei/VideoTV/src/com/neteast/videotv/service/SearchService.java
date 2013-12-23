package com.neteast.videotv.service;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;

import com.huawei.iptv.stb.asr.aidl.neteast.Callback;
import com.huawei.iptv.stb.asr.aidl.neteast.IAsrNeteastService;
import com.neteast.lib.bean.SearchResult;
import com.neteast.lib.bean.SearchResult.SearchRaw;
import com.neteast.videotv.TVApplication;
import com.neteast.videotv.ui.VideoDetailActivity_;
import com.neteast.videotv.utils.Utils;

public class SearchService extends Service {

	private final String ContentP = "content://com.neteast.longtv.desc/movieid"; 
	private IAsrNeteastService.Stub mBinder = new MyIAsrNeteastService();
	//private Callback mCallback;

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	class MyIAsrNeteastService extends IAsrNeteastService.Stub {
		@Override
		public void setCallback(Callback callback) throws RemoteException {
			System.out.println("callback =---" + callback);
			//mCallback = callback;
		}

		@Override
		public String execute(String json) {
			try {
				parseJson(json);
			} catch (Exception e) {
				return "parse error";
			}
			return "parse success~!";
		}
	}

	private void parseJson(String json) throws Exception {
		JSONObject jsonObject = new JSONObject(json);
		String cmd = jsonObject.getString("cmd");
		JSONObject jsonObject1 = jsonObject.getJSONObject("data");
		//播放
		if("play".equals(cmd)){
			String key = jsonObject1.getString("keyword");
			String netPath = getPath(key);
			new MyQueryData().execute(netPath,key);
		}
		//搜索
		if("startSearch".equals(cmd)){
			String movieid = jsonObject1.getString("_id");
	        Intent intent1=new Intent(getApplicationContext(),VideoDetailActivity_.class);
	        intent1.putExtra("movieId",movieid);
	        startActivity(intent1);
		}
	}
	
	
	
	
	
	

	class MyQueryData extends AsyncTask<String, String, List<String>> {
		@Override
		protected List<String> doInBackground(String... params) {
			 InputStream in;
			 Serializer serializer = new Persister();
			 List<String> result = null;
			try {
				in = Utils.downloadUrl(params[0]);
				SearchResult searchResult = serializer.read(SearchResult.class, in);
				result = getJson(searchResult,params[1]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(List<String> result) {
			try {
				for(int i=0; i<result.size(); i++){
					System.out.println(result.get(i).toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.onPostExecute(result);
		}

	}
	
	private List<String> getJson(SearchResult searchResult,String key){
		List<String> lis = new ArrayList<String>();
		List<SearchRaw> getmVideos = searchResult.getmVideos();
		for(int i=0; i<=getmVideos.size()/20; i++ ){
			int start = i*20;
			int end = (i+1)*20;
			if(end>getmVideos.size())end = getmVideos.size();
			String json = parseObjectToJson(getmVideos.subList(start, end),key);
			lis.add(json);
		}
		return lis;
	}
	
	private String parseObjectToJson(List<SearchRaw> searchRaws,String key){
		Map<String,Object> jsonData = new HashMap<String, Object>();
		StringBuffer sb = new StringBuffer("[");
		for(int i=0; i<searchRaws.size(); i++){
			SearchRaw searchRaw = searchRaws.get(i);
			Map<String,String> jsonitem = new HashMap<String, String>();
			jsonitem.put("_id", String.valueOf(searchRaw.getMovieID()));
			Uri uri = Uri.parse(ContentP);
			Uri resultUri = ContentUris.withAppendedId(uri, searchRaw.getMovieID());
			jsonitem.put("contentId", resultUri.toString());
			jsonitem.put("title", searchRaw.getMovieName());
			jsonitem.put("url", "");
			jsonitem.put("app", "NETEAST");
			jsonitem.put("episodeNumber", searchRaw.getMaxSeries());
			jsonitem.put("length", searchRaw.getTimeSpan());
			jsonitem.put("definition", "");
			jsonitem.put("playactor", searchRaw.getActor());
			jsonitem.put("director", searchRaw.getDirector());
			jsonitem.put("summary", "简介");
			jsonitem.put("type", "视频来源");
			jsonitem.put("source", "视频来源");
			jsonitem.put("image", searchRaw.getPoster());
			jsonitem.put("image1",searchRaw.getPoster2());
			sb.append(new JSONObject(jsonitem).toString()).append(",");
		}
		sb.deleteCharAt(sb.length()-1).append("]");
		jsonData.put("cmd", "onSearchResults ");
		jsonData.put("keyword", key);
		jsonData.put("data", sb);
		JSONObject json = new JSONObject(jsonData);
		return json.toString();
	}
	
	private String getPath(String searchKey) throws UnsupportedEncodingException{
		String keyword = URLEncoder.encode(searchKey, "UTF-8");
		return TVApplication.API_SEARCH + "/sort/1/PS/100/p/1/search/"+ keyword;
	}
	

}
