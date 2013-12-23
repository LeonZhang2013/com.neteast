package com.neteast.videotv.service;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.huawei.iptv.stb.asr.aidl.neteast.Callback;
import com.huawei.iptv.stb.asr.aidl.neteast.IAsrNeteastService;
import com.neteast.lib.bean.SearchResult;
import com.neteast.lib.bean.VideoDetailRaw;
import com.neteast.lib.bean.SearchResult.SearchRaw;
import com.neteast.videotv.TVApplication;
import com.neteast.videotv.ui.VideoDetailActivity_;
import com.neteast.videotv.utils.Utils;

/**
 * 东方网信 service示例
 * 
 * @author yWX173354
 * 
 */
public class AsrNeteastService extends Service {

	private static final String TAG = "AsrNeteastService";

	private AsrTvBinder mAsrTvBinder;

	@Override
	public IBinder onBind(Intent intent) {
		if (mAsrTvBinder == null) {
			mAsrTvBinder = new AsrTvBinder();
		}
		return mAsrTvBinder;
	}

	public void startMyActivity(long movieId) {
		/*
		 * Intent intent = new Intent("com.neteast.longtv.VIDEODESC");
		 * intent.putExtra("movieId", id); startActivity(intent);
		 */
		Intent intent = new Intent(getApplicationContext(),
				VideoDetailActivity_.class);
		intent.putExtra("movieId", movieId);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	class AsrTvBinder extends IAsrNeteastService.Stub {

		public Callback mCallback;

		@Override
		public void setCallback(Callback callback) throws RemoteException {
			this.mCallback = callback;
		}

		@Override
		public String execute(String json) throws RemoteException {
			JSONObject jsonObject = null;
			String command = null;
			try {
				jsonObject = new JSONObject(json);
				command = jsonObject.getString(JsonBuilder.KEY_CMD);
			} catch (JSONException e) {
				e.printStackTrace();
				command = null;
			}
			if (command == null) {
				return null;
			}
			// 开始搜索，并返回搜索结果
			if (JsonBuilder.START_SEARCH.equals(command)) {
				try {
					JSONObject dataObject = jsonObject
							.getJSONObject(JsonBuilder.KEY_DATA);
					if (dataObject != null) {
						String keyword = dataObject.getString("keyword");
						new Thread(new MyQueryData(keyword)).start();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null;
			}
			// 停止搜索
			else if (JsonBuilder.STOP_SEARCH.equals(command)) {
				Log.i(TAG, "Stop search");
			}
			// 播放视频
			else if (JsonBuilder.PLAY.equals(command)) {
				try {
					JSONObject dataObject = jsonObject
							.getJSONObject(JsonBuilder.KEY_DATA);
					if (dataObject != null) {
						String id = dataObject.optString("_id");
						String contentId = dataObject.optString("contentId");
						String title = dataObject.optString("title");
						String url = dataObject.optString("url");
						startMyActivity(Long.parseLong(id));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null;

			}
			// 进入东方网信进行搜索
			else if (JsonBuilder.ENTRY_FOR_SEARCH.equals(command)) {
				try {
					JSONObject dataObject = jsonObject
							.getJSONObject(JsonBuilder.KEY_DATA);
					if (dataObject != null) {
						String keyword = dataObject.getString("keyword");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else if (JsonBuilder.IS_VOICE_INPUT_ACTIVITY.equals(command)) {
				Log.i(TAG, "IS_VOICE_INPUT");
				return JsonBuilder.buildIsVoiceInput(true /* 若支持则为true */);
			}
			// 语音输入出错，当前页面支持语音输入时有效
			else if (JsonBuilder.VOICE_INPUT_ERROR.equals(command)) {
				Log.i(TAG, "VOICE_INPUT_ERROR");
				try {
					JSONObject dataObject = jsonObject
							.getJSONObject(JsonBuilder.KEY_DATA);
					if (dataObject != null) {
						String desc = dataObject.getString("desc");
						String code = dataObject.getString("code");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			// 语音输入结果，当前页面支持语音输入时有效
			else if (JsonBuilder.VOICE_INPUT_RESULT.equals(command)) {
				Log.i(TAG, "VOICE_INPUT_RESULT");
				try {
					JSONObject dataObject = jsonObject
							.getJSONObject(JsonBuilder.KEY_DATA);
					if (dataObject != null) {
						String whole = dataObject.getString("whole");
						String keyword = dataObject.getString("keyword");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			// 语音输入开始，当前页面支持语音输入时有效
			else if (JsonBuilder.VOICE_INPUT_START.equals(command)) {
				Log.i(TAG, "VOICE_INPUT_START");
			} else if (JsonBuilder.ON_VOICE_INPUT_REQUEST.equals(command)) {
				if (mCallback != null) {
					Log.i(TAG, "ON_VOICE_INPUT_REQUEST");
					mCallback.onResult(json);
				}
			}
			// 返回当前播放位置：
			else if (JsonBuilder.GET_CURRENT_POSITION.equals(command)) {
				Log.i(TAG, "GET_CURRENT_POSITION");
			}
			// 返回播放长度：
			else if (JsonBuilder.GET_DURATION.equals(command)) {
				Log.i(TAG, "GET_DURATION");
			}
			// 开始播放视频
			else if (JsonBuilder.START.equals(command)) {
				Log.i(TAG, "START");
			}
			// 暂停播放
			else if (JsonBuilder.PAUSE.equals(command)) {
				Log.i(TAG, "PAUSE");
			}
			// 停止播放
			else if (JsonBuilder.STOP.equals(command)) {
				Log.i(TAG, "STOP");
			}
			// 播放下一个视频
			else if (JsonBuilder.NEXT.equals(command)) {
				Log.i(TAG, "NEXT");
			}
			// 播放上一个视频
			else if (JsonBuilder.PREVIOS.equals(command)) {
				Log.i(TAG, "PREVIOS");
			}
			// 定位到某个点进行播放，主要用于快进、快退等操作
			else if (JsonBuilder.SEEK_TO.equals(command)) {
				Log.i(TAG, "SEEK_TO");
			}
			// 设置播放音量
			else if (JsonBuilder.SET_VOLUME.equals(command)) {
				Log.i(TAG, "SET_VOLUME");
			} else {
				Log.w(TAG, "Not support command./n" + command);
				return null;
			}

			return null;
		}

		public void onResult(String json) {
			if (mCallback != null) {
				try {
					mCallback.onResult(json);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}

		class MyQueryData implements Runnable {
			private String key;

			public MyQueryData(String key) {
				super();
				this.key = key;
			}

			protected List<String> doInBackground(String key) {
				InputStream in;
				Serializer serializer = new Persister();
				List<String> result = null;
				try {
					in = Utils.downloadUrl(getPath(key));
					SearchResult searchResult = serializer.read(
							SearchResult.class, in);
					result = getJson(searchResult, key);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}

			protected void onPostExecute(List<String> result) {
				try {
					if (mCallback != null) {
						for (int i = 0; i < result.size(); i++) {
							mCallback.onResult(result.get(i).toString());
						}
						String buildOnComplete = JsonBuilder.buildOnComplete(key);
						mCallback.onResult(buildOnComplete);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void run() {
				List<String> doInBackground = doInBackground(key);
				onPostExecute(doInBackground);
			}

		}

	}

	private static int jsonPreCount = 20;

	private static List<String> getJson(SearchResult searchResult, String key)
			throws JSONException {
		List<String> lis = new ArrayList<String>();
		List<SearchRaw> getmVideos = searchResult.getmVideos();
		for (int i = 0; i < getmVideos.size() / jsonPreCount; i++) {
			int start = i * jsonPreCount;
			int end = (i + 1) * jsonPreCount;
			if (end > getmVideos.size())
				end = getmVideos.size();
			String json = parseObjectToJson(getmVideos.subList(start, end), key);
			lis.add(json);
		}
		return lis;
	}

	private final static String ContentP = "content://com.neteast.longtv.desc/movieid";

	private static String parseObjectToJson(List<SearchRaw> searchRaws,
			String key) throws JSONException {
		Map<String, Object> jsonData = new HashMap<String, Object>();
		List<JSONObject> list = new ArrayList<JSONObject>();
		for (int i = 0; i < searchRaws.size(); i++) {
			SearchRaw searchRaw = searchRaws.get(i);
			JSONObject dataItem = new JSONObject();
			dataItem.put("_id", String.valueOf(searchRaw.getMovieID()));
			Uri uri = Uri.parse(ContentP);
			Uri resultUri = ContentUris.withAppendedId(uri,
					searchRaw.getMovieID());
			dataItem.put("contentId", resultUri.toString());
			dataItem.put("title", searchRaw.getMovieName());
			dataItem.put("url", "");
			dataItem.put("app", "NETEAST");
			dataItem.put("episodeNumber", searchRaw.getMaxSeries());
			dataItem.put("length", searchRaw.getTimeSpan());
			dataItem.put("definition", "");
			dataItem.put("playactor", searchRaw.getActor());
			dataItem.put("director", searchRaw.getDirector());
			dataItem.put("summary", "简介");
			dataItem.put("type", "视频来源");
			dataItem.put("source", "视频来源");
			dataItem.put("image", searchRaw.getPoster());
			dataItem.put("image1", searchRaw.getPoster2());
			list.add(dataItem);
		}
		JSONArray jsonArray = new JSONArray(list);
		jsonData.put("cmd", "onSearchResults");
		jsonData.put("keyword", key);
		jsonData.put("data", jsonArray);
		JSONObject json = new JSONObject(jsonData);
		return json.toString();
	}

	private VideoDetailRaw getMoviceDesc(String movieId) throws Exception {
		InputStream in;
		Serializer serializer = new Persister();
		String api = String.format(TVApplication.API_MOVIE_DETAIL, movieId);
		in = Utils.downloadUrl(api);
		return serializer.read(VideoDetailRaw.class, in);
	}

	private static String getPath(String searchKey)
			throws UnsupportedEncodingException {
		String keyword = URLEncoder.encode(searchKey, "UTF-8");
		return TVApplication.API_SEARCH + "/sort/1/PS/100/p/1/search/"
				+ keyword;
	}

}
