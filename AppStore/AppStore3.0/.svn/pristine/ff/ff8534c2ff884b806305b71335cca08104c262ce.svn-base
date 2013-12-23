package com.hs.activity;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.hs.db.AppDao;
import com.hs.domain.AppBean;
import com.hs.domain.User;
import com.hs.handler.DownloadToastHandler;
import com.hs.params.Params;
import com.hs.utils.ApkManager;
import com.hs.utils.AppData;
import com.hs.utils.ExitHandler;
import com.hs.utils.NetWorkHandler;
import com.hs.utils.Tools;
import com.hs.utils.UIHelper;
import com.hs.view.ScrollLinearLayout;
import com.hs.view.ScrollLinearLayout.OnClickScrollItemListener;
import com.hs.view.ScrollLinearLayout.OnPageChangeListener;
import com.lib.appstore.LibAppstore;
import com.lib.log.MyLog;
import com.lib.net.WeiboException;
import com.neteast.data_acquisition.DataAcqusition;

public class AppStoreActivity extends TabActivity {

	private EditText searchContent;
	private static TabHost th;
	private Resources res;
	private String tabtag;
	private int currentPage = 1, totalPage = 1, numperPage = 16, searchTotal = 0;
	private ImageButton leftButton, rightButton;
	private TextView total, pageWithTotal;
	private ViewFlipper viewFlipper;
	private String keyword;
	private LibAppstore appstore;
	private View clickedView;// 点击的图片
	private GestureDetector clickGesture, slideGesture;
	private PopupWindow search;
	private ScrollLinearLayout mScrollLinearLayout;
	private TextView appdcount = null;
	private int currentMaxPage = 1;// 访问的最大页数
	private AppData mAppData;
	public static LinearLayout topSlide;
	public static RelativeLayout titlelay;
	public static TabWidget tw;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		appstore = LibAppstore.getInstance(this);
		checkNetWork();
		checkUpdate();
		initOther();
		initLayoutParams();
		initTab();
		//checkOtherAppUpdate();
	}
	
	// add zcy
	@Override
	protected void onResume() {
		super.onResume();
		checkNetWork();
		showappdownloadcount();
	}
	
	private void checkNetWork(){
		/* 检查网络 */
		if (!NetWorkHandler.isNetworkAvailable(this, AppStore.SERVER)) {
			showWIFiDialog();
		}
	}
	
	
	
	private void checkUpdate(){
		new ApkManager(this, new ApkManager.ApkListener() {
			public void onApkUpdateFinished() {
				Log.i("test", "应用仓库已经是最新版本");
			}
		}).startUpdate(AppStore.APP_UPGRADE_URL);
	}
	
	private void initOther(){
		Tools.initIgnoerTime(this);
		mAppData = AppData.getInstance(this);
		mAppData.initData(this);
		int userid = User.getUserId();
		DataAcqusition.init(this, userid);
		DataAcqusition.acquisitionAppStatusData(DataAcqusition.LOGIN);
		String deviceid = Tools.getDeviceId(this);
		appstore.userReport("1", String.valueOf(userid), deviceid);
	}

	/** 检查是否有安装的应用需要升级，如果有就弹出对话框。 */
	private void checkOtherAppUpdate() {
		List<AppBean> upgradelist = mAppData.getUpgradeList();
		int upgradeCount = 0;
		List<AppBean> list = new ArrayList<AppBean>();
		for (int i = 0; i < upgradelist.size(); i++) {
			AppBean bean = list.get(i);
			if (Tools.isUpdate(bean.getIgnored())) {
				upgradeCount++;
			}
		}
		if (upgradeCount > 0) {
			new AlertDialog.Builder(this)
			.setTitle("温馨提示")
			.setMessage("当前有"+upgradeCount+"个应用可以更新")
			.setPositiveButton("查看", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SixthFragmentActivity.setCategory(SixthFragmentActivity.CATE_UPGRADEABLE);
						onClickTab(5);
						Params.app_dcount = 0;
						if (appdcount != null) {
							appdcount.setVisibility(View.GONE);
						}
					}
			})
			.setNegativeButton("不查看", null).show();
		}
	}

	private List<Map<String, Object>> result;
	private RadioGroup mRadioGroup;
	private void initLayoutParams() {
		
		titlelay = (RelativeLayout) findViewById(R.id.titlelay);
		searchContent = (EditText) findViewById(R.id.searchcontent);
		searchContent.setSingleLine(true);
		searchContent.setFocusable(true);
		topSlide = (LinearLayout) findViewById(R.id.topslide);
		mRadioGroup = (RadioGroup) findViewById(R.id.radio_view);
		
		mScrollLinearLayout = (ScrollLinearLayout) findViewById(R.id.scrollimage);
		mScrollLinearLayout.setOnClickScrollItemListener(new OnClickScrollItemListener() {
			@Override
			public void onClick(int imageIndex) {
				Intent intent = new Intent();
				intent.setClass(AppStoreActivity.this, PopWindowActivity.class);
				intent.putExtra("id", (String) result.get(imageIndex).get("id"));
				startActivity(intent);
			}
		});
		mScrollLinearLayout.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onClick(int pageIndex) {
				mRadioGroup.check(pageIndex);
			}
		});
		new requestData().execute();
		res = getResources();
		th = getTabHost();
		tw = th.getTabWidget();
		th.setup();
		titlelay.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, (float) 2.5));
		tw.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, (float) 1.8));
	}
	
	class requestData extends AsyncTask<Integer, Integer, List<Map<String, Object>>> {
		@Override
		protected List<Map<String, Object>> doInBackground(Integer... params) {
			try {
				return appstore.Get_hot_slide(6);
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(List<Map<String, Object>> result) {
			if(result == null) return;
			AppStoreActivity.this.result = result;
			super.onPostExecute(result);
			mScrollLinearLayout.setImages(result);
			if(result.size()>=6){
				mScrollLinearLayout.startScroll(ScrollLinearLayout.LEFT);
			}
			initRadioGrop(result.size());
		}
	}
	
	private void initRadioGrop(int dataSize){
		if(mRadioGroup!=null) mRadioGroup.removeAllViews();
		int radioButtonCount = dataSize%3==0? dataSize/3 : dataSize/3+1;
		for(int i=0; i<radioButtonCount; i++){
			RadioButton radioButton = new RadioButton(this);
			radioButton.setButtonDrawable(R.drawable.radiobuttonstyle);
			radioButton.setClickable(false);
			radioButton.setId(i);
			mRadioGroup.addView(radioButton);
		}
		mRadioGroup.check(0);
	}

	private void initTab() {
		Intent intent;
		RelativeLayout tab1, tab2, tab3, tab4, tab5, tab6;
		TextView tv1, tv2, tv3, tv4, tv5, tv6;
		tab1 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tablayout, null);
		tab2 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tablayout, null);
		tab3 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tablayout, null);
		tab4 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tablayout, null);
		tab5 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tablayout, null);
		tab6 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tablayout6, null);
		tv1 = (TextView) tab1.findViewById(R.id.tabtext);
		tv2 = (TextView) tab2.findViewById(R.id.tabtext);
		tv3 = (TextView) tab3.findViewById(R.id.tabtext);
		tv4 = (TextView) tab4.findViewById(R.id.tabtext);
		tv5 = (TextView) tab5.findViewById(R.id.tabtext);
		tv6 = (TextView) tab6.findViewById(R.id.tabtext);
		appdcount = (TextView) tab6.findViewById(R.id.download_count);

		((DownLoadApplication) getApplication()).setTabHost(th);

		String action = getIntent().getAction();
		Bundle args = null;
		String id = null;
		if (action != null) {
			System.out.println("action  = " + action);
		} else {
			System.out.println("action is null ");
		}
		if (action != null && action.equals("com.neteast.appstore.activity.share")) {
			args = getIntent().getExtras();
			if (args != null) {
				id = args.getString("movieid");
			}

		}
		MyLog.writeLog("666666666666666622222222222222");
		intent = new Intent().setClass(this, FirstActivity.class);

		if (id != null) {
			intent.putExtra("id", id);
		}

		tv1.setText(res.getString(R.string.first_tab));
		th.addTab(th.newTabSpec("first").setIndicator(tab1).setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

		intent = new Intent().setClass(this, SecondFragmentActivity.class);
		tv2.setText(res.getString(R.string.second_tab));
		th.addTab(th.newTabSpec("second").setIndicator(tab2).setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

		intent = new Intent().setClass(this, ThirdActivity.class);
		tv3.setText(res.getString(R.string.third_tab));
		th.addTab(th.newTabSpec("third").setIndicator(tab3).setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

		intent = new Intent().setClass(this, FourthAndFifthActivity.class);
		intent.putExtra("type", Params.APP_TYPE);
		tv4.setText(res.getString(R.string.fourth_tab));
		th.addTab(th.newTabSpec("fourth").setIndicator(tab4).setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

		intent = new Intent().setClass(this, FourthAndFifthActivity.class);
		intent.putExtra("type", Params.GAME_TYPE);
		tv5.setText(res.getString(R.string.fifith_tab));
		th.addTab(th.newTabSpec("fifth").setIndicator(tab5).setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

		intent = new Intent().setClass(this, SixthFragmentActivity.class);
		//intent = new Intent().setClass(this, SixthActivity.class);
		tv6.setText(res.getString(R.string.sixth_tab));
		th.addTab(th.newTabSpec("sixth").setIndicator(tab6).setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

		th.setCurrentTab(0);

		tab1.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				showappdownloadcount(); // add zcy
				if (search != null && search.isShowing()) {
					search.dismiss();
				}
				if (th.getCurrentTab() != 0) {
					th.setCurrentTab(0);
				}
				//thimage.obtainData();
				topSlide.setVisibility(View.VISIBLE);
				titlelay.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, (float) 2.5));
				tw.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, (float) 1.8));
				return false;
			}
		});
		tab2.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				showappdownloadcount(); // add zcy
				onClickTab(1);
				return false;
			}
		});
		tab3.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				showappdownloadcount(); // add zcy
				onClickTab(2);
				return false;
			}
		});
		tab4.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				showappdownloadcount(); // add zcy
				onClickTab(3);
				return false;
			}
		});
		tab5.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				showappdownloadcount(); // add zcy
				onClickTab(4);
				return false;
			}
		});
		tab6.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				onClickTab(5);
				Params.app_dcount = 0;
				if (appdcount != null) {
					appdcount.setVisibility(View.GONE);
				}
				return false;
			}
		});
		searchContent.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				searchContent.setFocusable(true);
				if (search != null && search.isShowing()) {
					search.dismiss();
				}
			}
		});
		MyLog.writeLog("66666666666666333333333333333");
		DownloadToastHandler dth = new DownloadToastHandler(this);
		mAppData.setDownloadToastHandler(dth);
	}
	
	public void onClickTab(int tabNum){
		if (th.getCurrentTab() != tabNum) {
			th.setCurrentTab(tabNum);
		}
		topSlide.setVisibility(View.GONE);
		titlelay.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, (float) 1.5));
		tw.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
		if (search != null && search.isShowing()) {
			search.dismiss();
		}
	}

	public void listversion(int flag) {
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = 200;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(params);
		dialog.setTitle("关于");
		if (flag == 1)
			dialog.setMessage("当前版本:" + Tools.getVersion(this));
		else
			dialog.setMessage("1当前版本:" + Tools.getVersion(this));
		dialog.setButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void searchOnClickHandler(View view) {
		keyword = searchContent.getText().toString();
		keyword = keyword.trim();
		DataAcqusition.acquisitionUserSearchKeyData(keyword);
		if (keyword == null || keyword.length() == 0) {
			return;
		}
		if (search != null && search.isShowing()) {
			search.dismiss();
		}

		tabtag = th.getCurrentTabTag();
		if (tabtag.equals("first")) {
			topSlide.setVisibility(View.GONE);
			titlelay.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, (float) 1.5));
			tw.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
		}
		View searchWindow = getLayoutInflater().inflate(R.layout.search, null);

		leftButton = (ImageButton) searchWindow.findViewById(R.id.searchLeftPageButton);
		rightButton = (ImageButton) searchWindow.findViewById(R.id.searchRightPageButton);
		viewFlipper = (ViewFlipper) searchWindow.findViewById(R.id.searchViewFlipper);
		total = (TextView) searchWindow.findViewById(R.id.total);
		pageWithTotal = (TextView) searchWindow.findViewById(R.id.pagewithtotal);
		clickGesture = new GestureDetector(new GuestEventListener());
		slideGesture = new GestureDetector(new GuestEventSlideListener(AppStoreActivity.this));
		LinearLayout linear = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.datalayout, null);
		viewFlipper.addView(linear);
		GetSearchListTask gslt = new GetSearchListTask(linear);
		gslt.execute(keyword, String.valueOf(currentPage), String.valueOf(numperPage));

		searchWindow.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return slideGesture.onTouchEvent(event);
			}
		});
		search = new PopupWindow(searchWindow, LayoutParams.FILL_PARENT, 648);
		search.setOutsideTouchable(true);
		View parent = findViewById(android.R.id.tabhost);
		search.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

		searchContent.setText("");

		searchReportAsync searchreport = new searchReportAsync();
		int userid = User.getUserId();
		String deviceid = Tools.getDeviceId(this);
		searchreport.execute(keyword, String.valueOf(userid), deviceid);
	}

	private void addDataToLayout(List<Map<String, Object>> result, LinearLayout linear) {

		int i = 0;
		int j = 0;
		int lastIndex = viewFlipper.getChildCount() - 1;
		LinearLayout linearLay = (LinearLayout) viewFlipper.getChildAt(lastIndex);
		for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
			RelativeLayout elementlayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.elementlayout, null);
			elementlayout.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
			ImageView iv = (ImageView) elementlayout.getChildAt(0);
			Map<String, Object> map = iterator.next();
			appstore.DisplayImage((String) map.get("image"), iv);
			elementlayout.setTag(map.get("id"));
			elementlayout.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					clickedView = v;
					return clickGesture.onTouchEvent(event);
				}
			});
			LinearLayout textAndRate = (LinearLayout) elementlayout.getChildAt(1);
			TextView name = (TextView) textAndRate.getChildAt(0);// 名称
			TextView cate = (TextView) textAndRate.getChildAt(1);// 分类
			LinearLayout ratebarAndTimes = (LinearLayout) textAndRate.getChildAt(2);
			RatingBar rb = (RatingBar) ratebarAndTimes.getChildAt(0);
			// TextView times = (TextView) ratebarAndTimes.getChildAt(1);
			if (map.get("rating").equals(""))
				rb.setRating(0);
			else
				rb.setRating(Float.parseFloat((String) map.get("rating")));
			// times.setText("(2256份评分)");
			name.setText((String) map.get("title"));
			cate.setText(Params.getTypeName((String) map.get("type")) + map.get("ctitle"));
			i++;
			if (i > Params.NUM_PER_ROW) {
				i = 1;
				j++;
			}
			((LinearLayout) linearLay.getChildAt(j)).addView(elementlayout);
			if (!iterator.hasNext()) {
				int z = Params.NUM_PER_ROW - i;
				for (int x = 0; x < z; x++) {
					RelativeLayout reduElement = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.elementlayout, null);
					reduElement.removeAllViews();
					reduElement.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
					((LinearLayout) linearLay.getChildAt(j)).addView(reduElement);
				}
			}
		}
	}

	public void nextPageButtonHandler(View view) {
		if (currentPage >= totalPage) {
			return;
		}
		if (currentPage == 1) {
			leftButton.setBackgroundResource(R.drawable.leftbutton1);
		}
		currentPage++;
		if (currentPage == totalPage) {
			rightButton.setBackgroundResource(R.drawable.rightbutton2);
		}
		if (currentPage > currentMaxPage) {
			currentMaxPage++;
			LinearLayout linear = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.datalayout, null);
			viewFlipper.addView(linear);
			GetSearchListTask gslt = new GetSearchListTask(linear);
			gslt.execute(keyword, String.valueOf(currentPage), String.valueOf(numperPage));
		} else {
			pageWithTotal.setText(currentPage + "/" + totalPage);
		}
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(AppStoreActivity.this, R.anim.push_left_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(AppStoreActivity.this, R.anim.push_left_out));
		viewFlipper.showNext();

	}

	/** 上一页
	 * 
	 * @param view */
	public void prePageButtonHandler(View view) {
		if (currentPage <= 1) {
			return;
		}
		currentPage--;
		if (currentPage == 1) {
			leftButton.setBackgroundResource(R.drawable.leftbutton2);
		}
		pageWithTotal.setText(currentPage + "/" + totalPage);
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(AppStoreActivity.this, R.anim.push_right_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(AppStoreActivity.this, R.anim.push_right_out));
		viewFlipper.showPrevious();
	}

	class DownloadOSTask extends AsyncTask<String, Integer, String> {

		ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = new ProgressDialog(AppStoreActivity.this);
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setCancelable(false);
			progress.setMessage("下载中...");
			progress.show();
			progress.getWindow().setLayout(200, LayoutParams.WRAP_CONTENT);
		}

		@Override
		protected String doInBackground(String... params) {
			InputStream in = null;
			RandomAccessFile file = null;
			String fileName = null;
			try {
				URL url = new URL(params[0]);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5 * 1000);
				conn.setRequestProperty("Accept-Language", Params.ACCEPT_LANGUAGE);
				conn.setRequestProperty("Charset", Params.CHARSET);
				conn.setRequestProperty("Connetion", "Keep-Alive");
				conn.setRequestMethod("GET");
				conn.setRequestProperty("User-Agent", "NetFox");
				in = conn.getInputStream();
				int totalLength = conn.getContentLength();
				int urlIndex = url.toString().lastIndexOf("/");
				fileName = url.toString().substring(urlIndex + 1);

				File f = new File(Params.DOWNLOAD_FILE_PATH);
				if (!f.exists()) {
					f.mkdir();
				}

				String currentTempFilePath = Params.DOWNLOAD_FILE_PATH + fileName;// add zcy
				File myFile = new File(currentTempFilePath);
				if (myFile.exists()) {
					myFile.delete();
				}
				file = new RandomAccessFile(Params.DOWNLOAD_FILE_PATH + fileName, "rw");
				byte[] buffer = new byte[20 * 1024];
				int len = -1;
				int count = 0;
				while ((len = in.read(buffer)) != -1) {
					file.write(buffer, 0, len);
					count += len;
					publishProgress((int) ((float) count / totalLength * 100));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (file != null) {
						file.close();
					}
					if (in != null) {
						in.close();
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			return fileName;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			progress.setMessage("下载中" + values[0] + "%");
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			File file = new File(Params.DOWNLOAD_FILE_PATH + result);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse("file://" + file.toString()), "application/vnd.android.package-archive");
			progress.dismiss();
			startActivityForResult(intent, 0);
		}

	}

	class GetSearchListTask extends AsyncTask<String, Integer, List<Map<String, Object>>> {

		private LinearLayout linar;

		public GetSearchListTask(LinearLayout linear) {
			this.linar = linear;
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected List<Map<String, Object>> doInBackground(String... params) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			if (params[0] == null || "".equals(params[0])) {
				return null;
			}
			try {
				list = appstore.Get_search_list(params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2]));
				if (list.size() == 0) {
					return null;
				}
				int total;
				if (list.get(0).get("total").equals(""))
					total = 0;
				else
					total = Integer.parseInt((String) list.get(0).get("total"));

				int a = total / numperPage;

				searchTotal = total;
				if (total % numperPage != 0) {
					totalPage = a + 1;
				} else {
					totalPage = a;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			return list;
		}

		@Override
		protected void onPostExecute(List<Map<String, Object>> result) {
			if (currentPage == totalPage) {
				if (currentPage == 1) {
					rightButton.setBackgroundResource(R.drawable.rightbutton2);
				} else {
					leftButton.setBackgroundResource(R.drawable.leftbutton1);
					rightButton.setBackgroundResource(R.drawable.rightbutton2);
				}
			} else if (currentPage == 1) {
				leftButton.setBackgroundResource(R.drawable.leftbutton2);
			} else if (currentPage < totalPage) {
				leftButton.setBackgroundResource(R.drawable.leftbutton1);
				rightButton.setBackgroundResource(R.drawable.rightbutton1);
			}
			if (null == result) {
				currentPage = 1;
				searchTotal = 0;
				totalPage = 1;

			} else {
				addDataToLayout(result, linar);
			}
			total.setText("共搜索出相关应用" + searchTotal + "个");
			pageWithTotal.setText(currentPage + "/" + totalPage);
		}

	}

	class GuestEventListener implements OnGestureListener {

		@Override
		public void onShowPress(MotionEvent e) {

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			Intent intent = new Intent();
			intent.setClass(AppStoreActivity.this, PopWindowActivity.class);
			intent.putExtra("id", (String) (clickedView.getTag()));
			startActivity(intent);
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// listversion(1);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (e1.getX() - e2.getX() > 50) {
				if (currentPage >= totalPage) {
					return true;
				}
				nextPageButtonHandler(rightButton);
			}
			if (e2.getX() - e1.getX() > 50) {
				if (currentPage <= 1) {
					return true;
				}
				prePageButtonHandler(leftButton);
			}
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

	}

	class GuestEventSlideListener extends View implements OnGestureListener {

		public GuestEventSlideListener(Context context) {
			super(context);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (e1.getX() - e2.getX() > 50) {
				if (currentPage >= totalPage) {
					return true;
				}
				nextPageButtonHandler(rightButton);
			}
			if (e2.getX() - e1.getX() > 50) {
				if (currentPage <= 1) {
					return true;
				}
				prePageButtonHandler(leftButton);
			}
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// listversion(0);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (search != null && search.isShowing()) {
				search.dismiss();
				if (tabtag.equals("first")) {
					topSlide.setVisibility(View.VISIBLE);
					titlelay.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, (float) 2.5));
					tw.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, (float) 1.8));
				}
				return true;
			}
			ExitHandler.exitApp(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppDao.getInstance(this).closeAll();
		DataAcqusition.acquisitionAppStatusData(DataAcqusition.LOGOUT);
		mScrollLinearLayout.stopScroll();
	}

	public static void setSelectTab(int index) {
		th.setCurrentTab(index);
	}

	public void showappdownloadcount() {
		if (Params.app_dcount > 0) {
			if (appdcount != null) {
				appdcount.setVisibility(View.VISIBLE);
				appdcount.setText(Integer.toString(Params.app_dcount));
			}
		}
	}


	private void showWIFiDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(AppStoreActivity.this);
		builder.setTitle(R.string.system_tips);
		builder.setMessage(R.string.msg_network_nowifi);
		builder.setPositiveButton(R.string.net_setup, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				AppStoreActivity.this.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
			}
		});
		builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
				AppStoreActivity.this.finish();
				System.exit(0);
			}
		});
		builder.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuItem helpItem = menu.add(Menu.NONE, Menu.FIRST + 1, 1, R.string.online_help).setIcon(android.R.drawable.ic_menu_help);
		MenuItem feedbackItem = menu.add(Menu.NONE, Menu.FIRST + 2, 2, R.string.online_feedback).setIcon(
				R.drawable.ic_menu_compose);
		MenuItem aboutItem = menu.add(Menu.NONE, Menu.FIRST + 3, 3, R.string.about).setIcon(
				android.R.drawable.ic_menu_info_details);
		MenuItem upgradeItem = menu.add(Menu.NONE, Menu.FIRST + 4, 4, R.string.version_upgrade).setIcon(
				R.drawable.ic_menu_refresh);
		MenuItem ignoreItem = menu.add(Menu.NONE, Menu.FIRST + 4, 4, R.string.set).setIcon(
				R.drawable.ic_menu_refresh);

		helpItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {

				try {
					getPackageManager().getPackageInfo("com.neteast.oh", 0);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					UIHelper.displayToast(getResources().getString(R.string.online_help_notinstall), AppStoreActivity.this);
					return true;
				}
				Intent help = new Intent();
				help.setComponent(new ComponentName("com.neteast.oh", "com.neteast.oh.MainActivity"));
				startActivity(help);
				return true;
			}
		});
		feedbackItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				try {
					getPackageManager().getPackageInfo("com.wasu.feedback", 0);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					UIHelper.displayToast(getResources().getString(R.string.online_feedback_notinstall), AppStoreActivity.this);
					return true;
				}
				Intent feedback = new Intent();
				feedback.setComponent(new ComponentName("com.wasu.feedback", "com.wasu.feedback.FeedbackActivity"));
				feedback.putExtra("appcode", "10025");// 自己应用的appcode
				startActivity(feedback);
				return true;
			}
		});
		aboutItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				listversion(1);
				return true;
			}
		});
		upgradeItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				new ApkManager(AppStoreActivity.this, new ApkManager.ApkListener() {
					public void onApkUpdateFinished() {
						Toast.makeText(AppStoreActivity.this, "应用仓库已经是最新版本", Toast.LENGTH_SHORT).show();
					}
				}).startUpdate(AppStore.APP_UPGRADE_URL);
				return true;
			}
		});
		
		ignoreItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				final EditText e = new EditText(AppStoreActivity.this);
				e.setText(String.valueOf(Params.IGNORE_TIME));
				new AlertDialog.Builder(AppStoreActivity.this)
				.setTitle("请输入忽略时间")  
				.setIcon(android.R.drawable.ic_dialog_info) 
				.setView(e)  
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String time = e.getText().toString();
						try{
							Params.IGNORE_TIME = Long.parseLong(time);
							Tools.saveIgnoerTime(AppStoreActivity.this, Params.IGNORE_TIME);
						}catch (Exception e) {
							Toast.makeText(AppStoreActivity.this, "只能输入数字", Toast.LENGTH_SHORT).show();
						}
					}
				})  
				.setNegativeButton("取消", null)  
				.show(); 
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	class searchReportAsync extends AsyncTask<String, Object, String> {
		@Override
		protected String doInBackground(String... params) {
			String keyword = params[0];
			String userid = params[1];
			String deviceid = params[2];
			try {
				System.out.println("search report : keyword = " + keyword + "userid = " + userid + "deviceid = " + deviceid);
				Map<String, Object> retmap = appstore.search_report(keyword, userid, deviceid, "", "");
				System.out.println("search report retmap = " + retmap);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {

		}
	}
	
	
}
