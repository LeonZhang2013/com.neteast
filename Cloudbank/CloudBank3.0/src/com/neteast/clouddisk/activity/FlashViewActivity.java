package com.neteast.clouddisk.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue.IdleHandler;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import com.neteast.clouddisk.R;

public class FlashViewActivity extends Activity {

	WebView mWebView = null;
	String url = null;
	String title = null;
	int series_num = -1;
	String source = null;
	private List<String> seriesinfo = null;
	private LinearLayout choicemenu = null;
	private ListView flashlv = null;
	private List<Map<String, Object>> items = null;
	private SimpleAdapter flashadapter = null;
	private LinearLayout mVideoLoading = null;
	private List<String> serieslistinfo = null;
	private GestureDetector mGestureDetector = null;

	private View controlView = null;
	private PopupWindow controler = null;

	private View seriesView = null;
	private PopupWindow series = null;

	private int screenWidth = 0;
	private int screenHeight = 0;
	private int controlHeight = 0;

	private boolean isControllerShow = true;
	private boolean isSeriesShow = false;

	private final int HIDE_CONTROLER = 1;
	private final int TIME = 6868;

	private final int SHOW_TOPWINDOW = 2;

	private int xjsel;
	private int pagew;
	private int pageh;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.flashwebview);

		Bundle map = null;
		Intent i = getIntent();
		this.url = i.getStringExtra("url");
		this.title = i.getStringExtra("title");
		this.source = i.getStringExtra("source");
		series_num = i.getIntExtra("series_num", series_num);
		map = i.getBundleExtra("seriesmap");
		if (map != null)
			seriesinfo = (List<String>) map.getSerializable("series");

		// this.url =
		// "http://218.108.168.73:9203/00000000000000000000000000000000000000000000000097F57D9E6B7A25D37E5CDE492F525EAC03F576CE0/url.mp4";

		Bundle maplist = null;
		maplist = i.getBundleExtra("serieslistmap");
		if (maplist != null)
			serieslistinfo = (List<String>) maplist
					.getSerializable("serieslist");

		mWebView = (WebView) findViewById(R.id.flashplaywebview);
		mWebView.setWebViewClient(new FlashplayerWebViewClient());
		WebSettings settings = mWebView.getSettings();

		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(false);

		WebSettings.PluginState localPluginState = WebSettings.PluginState.ON;
		settings.setPluginState(localPluginState);
		settings.setPluginsEnabled(true);
		settings.setUserAgentString("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.220 Safari/535.1");
		mWebView.setBackgroundColor(0x000000);

		getPageSize();

		String wpage;
		String hpage;
		if (pagew == 1280 || pageh == 800) {
			// wpage =Integer.toString(pagew);
			// hpage =Integer.toString(pageh);
			wpage = hpage = "100%";
		} else {
			wpage = hpage = "100%";
		}

		String temp = "<html><body bgcolor=\"" + "#000000" + "\">"
				+ "<div  width=\"" + "100%" + "\"height=\"" + "100%"
				+ "\" bgcolor=\"" + "#000000" + "\">" + "<embed src=\"" + url
				+ "\"flashvars=\"" + "isAutoPlay=true&autoPlay=true"
				+ "\" width=\"" + wpage + "\" height=\"" + hpage
				+ "\"allowFullScreen=\"" + "true" + "\" type=\""
				+ "application/x-shockwave-flash"
				+ "\"> </embed></div></body></html>";

		String mimeType = "text/html";
		String encoding = "utf-8";
		mWebView.loadDataWithBaseURL("about:blank", temp, mimeType, encoding,
				null);

		// initTopWindow();
		// initlistview();
		// initbtn();
		getScreenSize();

		Looper.myQueue().addIdleHandler(new IdleHandler() {

			@Override
			public boolean queueIdle() {
				// TODO Auto-generated method stub
				if (controler != null) {
					controler.showAtLocation(mWebView, Gravity.TOP, 0, 0);
					controler.update(0, 0, screenWidth, controlHeight);
				}
				return false;
			}
		});

	}

	private void loadpage() {
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				Log.d(this.toString(), "Loading Flash video progress = "
						+ newProgress);
				if (newProgress == 100) {

					System.out.println("load page 100%");

				}
			}
		});
	}

	private void initTopWindow() {
		/*
		 * controlView = getLayoutInflater().inflate(R.layout.flashviewtips,
		 * null); isControllerShow = false;
		 * 
		 * seriesView = getLayoutInflater().inflate(R.layout.flashchoicelayout,
		 * null);
		 */

	}

	private void getScreenSize() {
		Display display = getWindowManager().getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth();

		controlHeight = 57;

	}

	private void getPageSize() {
		Display display = getWindowManager().getDefaultDisplay();
		pageh = display.getHeight();
		pagew = display.getWidth();
		int temp;
		if (pageh > pagew) {
			temp = pageh;
			pageh = pagew;
			pagew = temp;
		}
		System.out.println("screen w:" + pagew + "h:" + pageh);
	}

	private void cancelDelayHide() {
		myHandler.removeMessages(HIDE_CONTROLER);
	}

	private void hideController() {
		if (controler != null && controler.isShowing()) {
			controler.dismiss();
			// controler.update(0,0,0, 0);
			controler = null;
			isControllerShow = false;
		}
	}

	private void hideControllerDelay() {
		myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
	}

	private void showController() {

		if (controler == null)
			controler = new PopupWindow(controlView);

		controler.showAtLocation(mWebView, Gravity.TOP, 0, 0);
		controler.update(0, 0, screenWidth, controlHeight);

		isControllerShow = true;
	}

	private void hideSeries() {
		if (series.isShowing()) {
			series.dismiss();
			series = null;
			isSeriesShow = false;
		}
	}

	private void showSeries() {
		series = new PopupWindow(seriesView);
		series.setFocusable(true);
		series.setOutsideTouchable(true);
		series.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句�?
		series.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
					hideSeries();
				return false;
			}
		});

		series.showAtLocation(mWebView, Gravity.RIGHT | Gravity.TOP, 0, 57);
		series.update(0, 57, 255 * screenWidth / 1280, 606 * screenHeight / 800);

		isSeriesShow = true;
	}

	Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			switch (msg.what) {
			case HIDE_CONTROLER:
				hideController();
				break;
			}

			super.handleMessage(msg);
		}
	};

	public void initlistview() {
		/*
		 * items= new ArrayList<Map<String, Object>>(); if(seriesinfo ==null ||
		 * serieslistinfo==null) return ; String str; for (int i = 0; i <
		 * serieslistinfo.size(); i++) { HashMap<String, Object> itemqq = new
		 * HashMap<String, Object>();
		 * 
		 * str=serieslistinfo.get(i); itemqq.put("textItem",str);
		 * items.add(itemqq); }
		 * flashlv=(ListView)seriesView.findViewById(R.id.flashlistView); xjlist
		 * = new XuanJiList(this,items);
		 * 
		 * flashlv.setAdapter(xjlist); xjsel =series_num;
		 * xjlist.setSelectItem(xjsel);
		 * flashlv.setOnItemClickListener(flashListOnItemClick);
		 */
	}

	AdapterView.OnItemClickListener flashListOnItemClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			/*
			 * HashMap<String, Object> item = (HashMap<String, Object>)
			 * arg0.getItemAtPosition(arg2);
			 * 
			 * hideSeries();
			 * 
			 * if(serieslistinfo!=null) setTiele(serieslistinfo.get(arg2));
			 * else{ setTiele(""); } xjsel = arg2+1; xjlist.setSelectItem(arg2);
			 * xjlist.notifyDataSetInvalidated();
			 * 
			 * String url=(String)seriesinfo.get(arg2); getPageSize(); String
			 * wpage; String hpage; if(pagew==1280||pageh==800){
			 * 
			 * wpage=hpage="100%"; }else{ wpage=hpage="100%"; } String temp=
			 * "<html><body bgcolor=\"" + "#000000" +
			 * "\">"+"<div  width=\""+"100%"+ "\"height=\""+"100%"+"\" >"+
			 * "<embed src=\""+url +
			 * "\"flashvars=\""+"isAutoPlay=true&autoPlay=true" + "\" width=\""
			 * + wpage + "\" height=\"" + hpage + "\"allowFullScreen=\""+"true"
			 * + "\" type=\"" + "application/x-shockwave-flash" +
			 * "\"> </embed></div></body></html>";
			 * 
			 * String mimeType = "text/html"; String encoding = "utf-8";
			 * mWebView.loadDataWithBaseURL(null, temp, mimeType, encoding,
			 * null);
			 */
		}

	};

	private void setTiele(int num) {
		/*
		 * TextView titleView =
		 * (TextView)controlView.findViewById(R.id.flashplayer_title);
		 * if(titleView!=null && title!=null){ if(num > 0){ String str =
		 * String.format(getResources().getString(R.string.detail_text),num);
		 * if(source!=null){ titleView.setText((CharSequence) this.title + str +
		 * "(" + source + ")"); }else { titleView.setText((CharSequence)
		 * this.title + str); } }else{ titleView.setText((CharSequence)
		 * this.title); } }
		 */
	}

	private void setTiele(String titlestr) {
		/*
		 * TextView titleView =
		 * (TextView)controlView.findViewById(R.id.flashplayer_title);
		 * 
		 * if(titleView!=null && title!=null){ if(titlestr!=null &&
		 * !titlestr.equals("")){
		 * 
		 * if(source!=null){ titleView.setText((CharSequence) this.title +
		 * titlestr + "(" + source + ")"); }else {
		 * titleView.setText((CharSequence) this.title + titlestr); } }else{
		 * titleView.setText((CharSequence) this.title); } }
		 */
	}

	private void initbtn() {
		/*
		 * 
		 * if(serieslistinfo!=null) setTiele(serieslistinfo.get(series_num));
		 * else{ setTiele(""); } Button
		 * tb=(Button)controlView.findViewById(R.id.flashplayer_series_button);
		 * if(seriesinfo==null) tb.setVisibility(View.GONE);
		 * tb.setOnClickListener(new OnClickListener(){
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub if(series==null ){ showSeries(); }else if(!series.isShowing()){
		 * showSeries(); } } });
		 */
	}

	private void setBulbState(boolean state) {// true on false off
		// ����ͼƬ״̬
		/*
		 * ToggleButton tb=(ToggleButton)this.findViewById(R.id.ToggleButton);
		 * if(state) choicemenu.setVisibility(View.VISIBLE); else
		 * choicemenu.setVisibility(View.GONE); tb.setChecked(state);//
		 */
	}

	private class FlashplayerWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	private void HiddenWebViewMethod(String name) {
		if (mWebView != null) {
			try {
				Method method = WebView.class.getMethod(name);
				method.invoke(mWebView);
			} catch (NoSuchMethodException e) {
				Log.i("No such method: " + name, e.toString());
			} catch (IllegalAccessException e) {
				Log.i("Illegal Access: " + name, e.toString());
			} catch (InvocationTargetException e) {
				Log.d("Invocation Target Exception: " + name, e.toString());
			}

		}

	}

	@Override
	public void onPause() {// 继承自Activity
		super.onPause();
		//mWebView.onPause();

	}

	@Override
	public void onResume() {// 继承自Activity
		super.onResume();
		//mWebView.onResume();
	}

	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// add pasue deal this
		if (controler != null && controler.isShowing()) {

			controler.dismiss();
			controler = null;
		}
		if (series != null && series.isShowing()) {
			// series.update(0,0,0, 0);

			series.dismiss();
			series = null;
		}
		myHandler.removeMessages(HIDE_CONTROLER);

		if (seriesinfo != null)
			seriesinfo.clear();
		if (items != null)
			items.clear();
		controlView = null;
		seriesView = null;
		mWebView.destroy();
		mWebView = null;
		// add pasue deal this

		finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);
		getScreenSize();

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:

			if (!isControllerShow) {
				showController();
				hideControllerDelay();
			} else {
				cancelDelayHide();
				hideController();
			}
			break;
		default:
			break;
		}
		super.dispatchTouchEvent(event);
		return true;

	}
}