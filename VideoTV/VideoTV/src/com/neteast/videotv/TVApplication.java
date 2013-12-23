package com.neteast.videotv;

import android.app.Application;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.neteast.lib.bean.VideoRaw;
import com.neteast.videotv.dao.Menu;
import com.neteast.videotv.dao.Poster;
import com.neteast.videotv.utils.DiskLruImageCache;
import com.neteast.videotv.utils.VolleyCallback;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created with IntelliJ IDEA. User: emellend Date: 13-7-29 Time: 下午5:45 To
 * change this template use File | Settings | File Templates.
 */
public class TVApplication extends Application implements VolleyCallback {
	private static final int state = 2;
	
	private static final String BASE_URL;
	public static final String API_HOME_SCROLL_IMAGE;
	public static final String API_MENU_LIST;
	public static final String API_MENU_DOC;
	public static final String API_MOVIE_DETAIL;
	public static final String API_MOVIE_RELATION;
	public static final String API_HOME_WELCOME_IMAGE;

	public static final String API_FLVCD;
	public static final String API_SEARCH;
	public static final String SOHU_HOST;
	public static final String UPDATE_URL = "http://58.17.218.61:9000/update.xml";
	
	static {
		switch (state) {
		case 0://测试环境
			BASE_URL = "http://10.0.2.244:9300";
			API_FLVCD = "http://10.0.2.244:9301/Mobile/httptv/id/";
			API_SEARCH = "http://10.0.2.244:9301/Mobile/moviesearch/httpcheck/tv/http/2";
			SOHU_HOST = "http://10.0.2.207:8000/";
			break;
		case 1://外网环境
			BASE_URL = "http://124.207.5.60:9300";
			API_FLVCD = "http://124.207.5.60:9301/Mobile/httptv/id/";
			API_SEARCH = "http://124.207.5.60:9301/Mobile/moviesearch/httpcheck/tv/http/2";
			SOHU_HOST = "http://58.17.218.61:8000/";
			break;
		case 2://重庆环境
			BASE_URL = "http://58.17.218.61:9300";
			API_FLVCD = "http://58.17.218.61:9301/Mobile/httptv/id/";
			API_SEARCH = "http://58.17.218.61:9301/Mobile/moviesearch/httpcheck/tv/http/2";
			SOHU_HOST = "http://58.17.218.61:8000/";
			break;
		case 3://浙江移动
			BASE_URL = "http://218.205.48.224:9300";
			API_FLVCD = "http://218.205.48.224:9301/Mobile/httptv/id/";
			API_SEARCH = "http://218.205.48.224:9301/Mobile/moviesearch/httpcheck/tv/http/2";
			SOHU_HOST = "http://58.17.218.61:8000/";
			break;
		default:
			BASE_URL = "http://58.17.218.61:9300";
			API_FLVCD = "http://58.17.218.61:9301/Mobile/httptv/id/";
			API_SEARCH = "http://58.17.218.61:9301/Mobile/moviesearch/httpcheck/tv/http/2";
			SOHU_HOST = "http://58.17.218.61:8000/";
			break;
		}
		API_HOME_SCROLL_IMAGE = BASE_URL + "/api/menudoc/menukey/LSTV_index/isgz/n/check/no";
		API_MENU_LIST = BASE_URL + "/api/menulist/menukey/%s/isgz/n/check/no";
		API_MENU_DOC = BASE_URL + "/api/menudoc/menukey/%s/isgz/n/check/no/checkimage/1";
		API_MOVIE_DETAIL = BASE_URL + "/api/postStatistic/Mobile/moviedetail/mid/%d/isgz/n/http/tv";
		API_MOVIE_RELATION = BASE_URL + "/api/postStatistic/Mobile/movierelation/isgz/n/mid/%d/PS/6/httpcheck/tv/checkimage/1";
		API_HOME_WELCOME_IMAGE = BASE_URL + "/api/menudoc/menukey/LSTV_recommend/isgz/n/check/no";
		
		cupboard().register(Poster.class);
		cupboard().register(Menu.class);
		cupboard().register(VideoRaw.class);
	}

	private ImageLoader mImageLoader;
	private RequestQueue mRequestQueue;

	@Override
	public void onCreate() {
		super.onCreate();
		mRequestQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mRequestQueue,
				DiskLruImageCache.getInstance(this));
	}

	@Override
	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	@Override
	public RequestQueue getRequestQueue() {
		return mRequestQueue;
	}
}
