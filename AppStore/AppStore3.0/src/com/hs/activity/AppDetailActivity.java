package com.hs.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hs.adapter.CommentDataAdapter;
import com.hs.db.AppDao;
import com.hs.domain.AppBean;
import com.hs.domain.User;
import com.hs.handler.DownloadToastHandler;
import com.hs.handler.LoginHandler;
import com.hs.params.Params;
import com.hs.utils.AppData;
import com.hs.utils.ToastHandler;
import com.hs.utils.Tools;
import com.hs.utils.UIHelper;
import com.hs.view.ScrollLinearLayout.OnPageChangeListener;
import com.hs.view.ScrollLinearLayout1;
import com.lib.appstore.LibAppstore;
import com.lib.net.ImageLoader;
import com.lib.net.WeiboException;
import com.neteast.data_acquisition.DataAcqusition;
import com.neteast.data_acquisition.Utils;

public class AppDetailActivity extends Activity {

	List<String> imageList = null;
	LibAppstore appstore;
	LinearLayout imagecontent;
	TextView appname, appprice, appsize, appversion, appdate, appcategory1, appcategory2, appauthor, appdesc, source;
	RatingBar apprate;
	String nodata;
	String url, imageUrl;
	ImageView image;
	ImageButton downloadButton;
	ImageButton shareButton;
	ImageButton adviewButton;
	String appId;
	DownLoadApplication mApp;
	int topPage = 0;
	int currentMaxPage = 0;// 已经使用过的最大页数
	GestureDetector slideGesture;
	private AppDao dao;
	Intent resultIntent;
	LinearLayout customeToast;
	WindowManager wm;
	private int isRun = 0;// 1==run 2 == install null == 为未下载
	private RelativeLayout processbarlayout;

	private View commentView = null;
	private PopupWindow commentPopWin = null;
	private Map<String, Object> commentmap = null;
	private ListView mcommentList = null;
	List<Map<String, Object>> commentdataList = null;
	private LinearLayout commentLayout = null;
	private TextView scoreText = null;
	private TextView commentText = null;

	private LinearLayout detailLayout = null;
	private AppData mAppData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DownloadToastHandler.pop = this;
		dao = AppDao.getInstance(this);
		mAppData = AppData.getInstance(this);
		setContentView(R.layout.appdetailpage);
		LinearLayout rl = (LinearLayout) findViewById(R.id.test);
		detailLayout = (LinearLayout) findViewById(R.id.detail_content);
		commentLayout = (LinearLayout) findViewById(R.id.comment_score);
		commentLayout.setVisibility(View.INVISIBLE);
		scoreText = (TextView) findViewById(R.id.detail_score);
		commentText = (TextView) findViewById(R.id.detail_review);
		detailLayout.setVisibility(View.GONE);
		rl.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		appstore = LibAppstore.getInstance(this);
		mApp = (DownLoadApplication) getApplication();
		imagecontent = (LinearLayout) findViewById(R.id.imagecontent);
		slideGesture = new GestureDetector(new ImageGesture(AppDetailActivity.this));
		nodata = getString(R.string.nodata);
		source = (TextView) findViewById(R.id.source);
		appname = (TextView) findViewById(R.id.appname);
		apprate = (RatingBar) findViewById(R.id.apprate);
		appprice = (TextView) findViewById(R.id.appprice);
		appsize = (TextView) findViewById(R.id.appsize);
		appversion = (TextView) findViewById(R.id.appversion);
		appdate = (TextView) findViewById(R.id.appdate);
		appcategory1 = (TextView) findViewById(R.id.appcategory1);
		appcategory2 = (TextView) findViewById(R.id.appcategory2);
		appauthor = (TextView) findViewById(R.id.appauthor);
		appdesc = (TextView) findViewById(R.id.appdesc);
		processbarlayout = (RelativeLayout) findViewById(R.id.processbarlayout);
		appdesc.setMovementMethod(ScrollingMovementMethod.getInstance());
		image = (ImageView) findViewById(R.id.image);
		downloadButton = (ImageButton) findViewById(R.id.detailDownLoad);
		shareButton = (ImageButton) findViewById(R.id.share);
		adviewButton = (ImageButton) findViewById(R.id.adview);
		downloadButton.setClickable(false);
		Intent intent = this.getIntent();
		appId = (String) intent.getExtras().get("id");
		DetailsTask dt = new DetailsTask();
		dt.execute(appId);
		resultIntent = getIntent();
		resultIntent.putExtra("downloadingstatus", 0);
		customeToast = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.toastlayout, null);

		initcomment();
	}

	public void showcomment(Map<String, Object> map) {

		if (map == null)
			return;
		String ip = Utils.getIpAddress(this);
		System.out.println("local ip = " + ip);
		map.put("ip", ip);

		commentmap = map;
		commentPopWin.showAtLocation(findViewById(R.id.test), Gravity.CENTER, 0, 10);
		CommentDataAdapter commentadapter = new CommentDataAdapter(AppDetailActivity.this, new ArrayList<Map<String, Object>>());
		mcommentList.setAdapter(commentadapter);
		getCommentAsync getcommenttask = new getCommentAsync();
		getcommenttask.execute(map);
	}

	private void initcomment() {
		commentView = getLayoutInflater().inflate(R.layout.comment, null);
		ImageButton closebtn = (ImageButton) commentView.findViewById(R.id.comment_close);
		ImageButton commitbtn = (ImageButton) commentView.findViewById(R.id.comment_commit);
		final TextView inputnum = (TextView) commentView.findViewById(R.id.comment_inputNum);
		final EditText inputword = (EditText) commentView.findViewById(R.id.comment_input);
		mcommentList = (ListView) commentView.findViewById(R.id.comment_list);

		final RadioButton score1 = (RadioButton) commentView.findViewById(R.id.score1);
		final RadioButton score2 = (RadioButton) commentView.findViewById(R.id.score2);
		final RadioButton score3 = (RadioButton) commentView.findViewById(R.id.score3);
		final RadioButton score4 = (RadioButton) commentView.findViewById(R.id.score4);
		final RadioButton score5 = (RadioButton) commentView.findViewById(R.id.score5);
		inputword.requestFocus();

		commentPopWin = new PopupWindow(commentView, 746, 649, true);
		commentPopWin.setFocusable(true);
		commentPopWin.setTouchable(true);
		commentPopWin.setOutsideTouchable(true);
		commentPopWin.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句
		inputword.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			public void afterTextChanged(Editable s) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				temp = s;
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				selectionStart = inputword.getSelectionStart();
				selectionEnd = inputword.getSelectionEnd();
				Log.i("gongbiao1", "" + selectionStart);
				if (temp.length() > 80) {
					Toast.makeText(AppDetailActivity.this, "已经超出字数限制", Toast.LENGTH_SHORT).show();
					((Editable) s).delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionStart;
					inputword.setText(s);
					inputword.setSelection(tempSelection);
					inputnum.setText("0");
				} else {
					inputnum.setText(80 - temp.length() + "");
				}
			}
		});
		commitbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Editable text = inputword.getText();
				String key = "";
				if (text != null) {
					key = text.toString().trim();
				}
				if (key != null && key.length() > 0) {
					commentmap.put("content", key);
					commentAsync commenttask = new commentAsync();
					commenttask.execute(commentmap);
				}
				String score = "";
				if (score1.isChecked()) {
					score = "1";
				} else if (score2.isChecked()) {
					score = "2";
				} else if (score3.isChecked()) {
					score = "3";
				} else if (score4.isChecked()) {
					score = "4";
				} else if (score5.isChecked()) {
					score = "5";
				}
				commentmap.put("score", score);
				scoreAsync task = new scoreAsync();
				task.execute(commentmap);
				inputword.setText("");
				inputnum.setText("80");
			}
		});

		closebtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				commentPopWin.dismiss();
			}
		});

	}

	private void executeIt(String packageName) {
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageName);
		List<ResolveInfo> apps = getPackageManager().queryIntentActivities(resolveIntent, PackageManager.GET_ACTIVITIES);
		if (apps.size() == 0) {
			Toast.makeText(this, "无法启动该应用", Toast.LENGTH_SHORT).show();
			return;
		}
		ResolveInfo ri = apps.get(0);
		if (ri != null) {
			String className = ri.activityInfo.name;
			ComponentName cn = new ComponentName(packageName, className);
			Intent intent = new Intent();
			intent.setComponent(cn);
			startActivity(intent);
			// startActivityForResult(intent, 1);
		}
	}

	public void detailDownLoadHandler(View view) {
		Map<String, Object> map = (Map<String, Object>) view.getTag();

		AppBean bean = new AppBean();
		bean.setId(Integer.parseInt(map.get("id").toString()));
		bean.setImage((String) map.get("image"));
		bean.setTitle((String) map.get("title"));
		bean.setVersion((String) map.get("version"));
		bean.setSize((String) map.get("size"));
		bean.setUrl((String) map.get("url"));
		bean.setPackageName((String) map.get("package"));

		Integer runType = (Integer) map.get("runType");
		if (runType != null) {
			isRun = runType;
		}
		if (1 == isRun) {// 1 表示可运行
			executeIt(map.get("package").toString());
		} else if (2 == isRun) { // 2 表示可安装
			File file = new File(Params.DOWNLOAD_FILE_PATH + bean.getApkName());
			if (!file.exists()) {
				ToastHandler.toastDisplay("安装失败！文件不存在!", this);
				dao.deleteApp(bean.getId());
				return;
			}
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.putExtra("IsCreateShortCut", true); // 设置产生快捷方式
			intent.putExtra("ExpandedInstallFlg", true); // 设置禁止自启动标准
			intent.setDataAndType(Uri.parse("file://" + file.toString()), "application/vnd.android.package-archive");
			startActivityForResult(intent, 0);
			mAppData.setAppActionHandler(new InstallButtonHandlerX(view));
			return;
		} else {
			long freeSpace = Tools.getSDCardFreeSpace();
			float fsize = Tools.unitTranslate((String) map.get("size"));
			if (freeSpace <= fsize) {
				Toast.makeText(AppDetailActivity.this, "SD卡存储空间不足", Toast.LENGTH_SHORT).show();
				return;
			}

			view.setVisibility(View.GONE);
			processbarlayout.setVisibility(View.VISIBLE);
			mAppData.setAppActionHandler(new InstallButtonHandlerX(view));
			try {
				customeToast.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						SixthFragmentActivity.setCategory(SixthFragmentActivity.CATE_DOWNLOAD);
						AppStoreActivity.setSelectTab(5);
						AppStoreActivity.topSlide.setVisibility(View.GONE);
						AppStoreActivity.titlelay.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, (float) 1.5));
						AppStoreActivity.tw.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
						closePopWindow(null);
					}
				});

				wm = UIHelper.createToastWindow(this, customeToast, bean.getTitle());
				UIHelper.addDownload2layout(mAppData, processbarlayout, bean.getId(), view);
				mAppData.download(bean);
				resultIntent.putExtra("downloadingstatus", 1);

				/*
				 * 该段代码没有做任何数据改动 不知道干嘛用暂时不删 zll int userid = mApp.getUserId(this); String deviceid = mApp.getDeviceId(this);
				 * downloadReportAsync downloadreport = new downloadReportAsync(); downloadreport.execute((String) map.get("id"),
				 * String.valueOf(userid), deviceid);
				 */
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void onShareClick(View view) {
		Map<String, Object> map = (Map<String, Object>) view.getTag();
		if (map == null)
			return;
		String id = map.get("id").toString();
		String title = (String) map.get("title");
		String image = map.get("image").toString();

		Intent intent = new Intent("com.neteast.share");

		intent.putExtra("title", title);
		intent.putExtra("movieid", Integer.parseInt(id));
		intent.putExtra("moviename", title);
		intent.putExtra("url", id);
		intent.putExtra("picture", image);
		intent.putExtra("msglevel", 1);
		intent.putExtra("applicationid", 25);
		startActivity(intent);
	}

	public void onAdviewClick(View view) {
		int userid = 0;
		Map<String, Object> map = (Map<String, Object>) view.getTag();
		if (map == null)
			return;
		userid = User.getUserId();
		if (userid == 0) {
			LoginHandler ibh = new LoginHandler(AppDetailActivity.this, map);
			mApp.setLoginHandler(ibh);
			try {
				Intent intent = new Intent();
				intent.setAction("com.neteast.androidclient.newscenter.login");
				startActivity(intent);
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			showcomment(map);
		}
	}

	public void addButtonAndGallery(final List<String> list) {
		if (list == null)// add zcy
			return;
		for (int i = 0; i < list.size(); i++) {
			Button button = new Button(this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(15, 15);
			lp.setMargins(7, 0, 7, 0);
			button.setLayoutParams(lp);
			if (i == 0) {
				button.setBackgroundResource(R.drawable.pagebutton2);
			} else {
				button.setBackgroundResource(R.drawable.pagebutton1);
			}
			final int index = i;
			button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					topPage = index;
					v.setBackgroundResource(R.drawable.pagebutton2);
				}
			});

		}
	}

	class DetailsTask extends AsyncTask<String, Integer, Map<String, Object>> {
		@Override
		protected Map<String, Object> doInBackground(String... params) {
			Map<String, Object> map = new HashMap<String, Object>();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			try {
				System.out.println("appId = " + appId + "params[0] = " + params[0]);
				map = appstore.Get_details(params[0]);
				list = appstore.Get_related_list(appId, 2);
				map.put("xiangguan", list);
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			return map;
		}

		@SuppressWarnings("unchecked")
		protected void onPostExecute(Map<String, Object> result) {
			String packageName = (String) result.get("package");

			downloadButton.setTag(result);
			shareButton.setTag(result);
			adviewButton.setTag(result);

			getCommentAndScoreNumAsync task = new getCommentAndScoreNumAsync();
			task.execute(result);
			int downloadindex = Tools.getIndexByAppId(mAppData.getDownloadList(), Integer.parseInt(appId));
			if (downloadindex != -1) {
				List<AppBean> downloadlist = mAppData.getDownloadList();
				AppBean bean = downloadlist.get(downloadindex);
				Integer state = (Integer) bean.getStatus();
				Integer downloadstatus = (Integer) bean.getDownloadStatus();
				String version = bean.getVersion();
				if (state == 1) { // 运行
					if (mAppData.isAppInstalled(packageName)) {
						downloadButton.setBackgroundResource(R.drawable.runbutton1);
						downloadButton.setClickable(true);
						downloadButton.setTag(result);
						isRun = 1;
					} else {
						downloadButton.setBackgroundResource(R.drawable.downloadbuttonstyle);
						downloadButton.setClickable(true);
						isRun = 0;
					}
				} else if (downloadstatus == 1) {/* 下载中 */
					downloadButton.setVisibility(View.GONE);
					processbarlayout.setVisibility(View.VISIBLE);
					InstallButtonHandlerX ibh = new InstallButtonHandlerX(downloadButton);
					mAppData.setAppActionHandler(ibh);

					UIHelper.addDownload2layout(mAppData, processbarlayout, bean.getId(), downloadButton);
				} else if (downloadstatus == 2) {
					// 安装
					File file = new File(Params.DOWNLOAD_FILE_PATH + bean.getApkName());
					if (!file.exists()) {
						dao.deleteApp(bean.getId());
						downloadButton.setBackgroundResource(R.drawable.downloadbuttonstyle);
						downloadButton.setClickable(true);
						isRun = 0;

					} else {
						isRun = 2;
						downloadButton.setBackgroundResource(R.drawable.detail_installbtn1);
						downloadButton.setClickable(true);
					}
				} else {
					// 下载
					downloadButton.setBackgroundResource(R.drawable.downloadunclick);
					downloadButton.setClickable(false);
				}

			} else {
				if (mAppData.isAppInstalled(packageName)) {
					downloadButton.setBackgroundResource(R.drawable.runbutton1);
					downloadButton.setClickable(true);
					isRun = 1;
				} else {
					downloadButton.setBackgroundResource(R.drawable.downloadbuttonstyle);
					downloadButton.setClickable(true);
					isRun = 0;
				}
			}
			if (result.get("image") != null) {
				imageUrl = result.get("image").toString();
				appstore.DisplayImage(imageUrl, image);
			}
			if (result.get("title") != null) {
				appname.setText((String) result.get("title"));
			} else {
				appname.append(nodata);
			}
			if (result.get("type") != null && result.get("ctitle") != null) {
				if (result.get("type").toString().equals("app")) {
					appcategory1.append("应用" + ">" + (String) result.get("ctitle"));
				} else {
					appcategory1.append("游戏" + ">" + (String) result.get("ctitle"));
				}
			} else {
				appcategory1.append(nodata);
			}
			if (result.get("rating") != null && !(result.get("rating").equals(""))) {
				apprate.setRating(Float.parseFloat((String) result.get("rating")));

			}
			if (result.get("pay") != null) {
				appprice.append((String) result.get("pay"));
			} else {
				appprice.append(nodata);
			}
			if (result.get("size") != null) {
				appsize.append((String) result.get("size") + "B");
			} else {
				appsize.append(nodata);
			}
			if (result.get("version") != null) {
				appversion.append((String) result.get("version"));
			} else {
				appversion.append(nodata);
			}
			if (result.get("date") != null) {
				appdate.append((String) result.get("date"));
			} else {
				appdate.append(nodata);
			}
			if (result.get("type") != null && result.get("ctitle") != null) {
				if (result.get("type").toString().equals("app")) {
					appcategory2.append("应用" + ">" + (String) result.get("ctitle"));
				} else {
					appcategory2.append("游戏" + ">" + (String) result.get("ctitle"));
				}
			} else {
				appcategory2.append(nodata);
			}
			if (result.get("author") != null) {
				appauthor.append((String) result.get("author"));
			} else {
				appauthor.append(nodata);
			}
			if (result.get("description") != null) {
				appdesc.append((String) result.get("description"));
			} else {
				appdesc.append(nodata);
			}

			source.setText("");
			String strs = "";
			strs = result.get("source") == null ? "" : result.get("source") + "";

			if (strs == "") {
				source.append("来源：" + "互联网");
			} else {
				source.append("来源：" + strs);
			}
			if (result.get("url") != null) {
				url = result.get("url").toString();
			}
			imageList = (List<String>) result.get("screenshot");
			addButtonAndGallery((List<String>) result.get("screenshot"));
			addViewFlipper(imageList);
			addXiangGuanContent((List<Map<String, Object>>) result.get("xiangguan"));

			detailLayout.setVisibility(View.VISIBLE);
		}
	}

	public void closePopWindow(View view) {
		try {
			wm.removeView(customeToast);
		} catch (Exception e) {

		}
		if (view != null) {
			AppDetailActivity.this.setResult(0, resultIntent);
		} else {
			AppDetailActivity.this.setResult(1, resultIntent);
		}
		AppDetailActivity.this.finish();
	}

	public void addViewFlipper(List<String> imageList2) {
		imagecontent.removeAllViews();
		if (imageList2 == null) {
			return;
		}
		ScrollLinearLayout1 scrollLinearLayout = (ScrollLinearLayout1) findViewById(R.id.scroll_view_2);
		addRadioButton(imageList2.size(),scrollLinearLayout);
		scrollLinearLayout.setImages(imageList2);
	}
	
	private void addRadioButton(final int imageSize,ScrollLinearLayout1 scrollLinearLayout) {
		final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_view);
		radioGroup.removeAllViews();
		for (int i = 0; i < imageSize; i++) {
			RadioButton child = new RadioButton(this);
			child.setEnabled(false);
			child.setId(i);
			child.setButtonDrawable(R.drawable.radiobuttonstyle);
			radioGroup.addView(child);
		}
		radioGroup.check(0);
		scrollLinearLayout.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onClick(int pageIndex) {
				radioGroup.check(pageIndex);		
			}
		});
	}

	public void addXiangGuanContent(List<Map<String, Object>> list) {
		if (list == null)
			return;
		LinearLayout xiangGuen = (LinearLayout) findViewById(R.id.xiangguanneirong);
		for (Iterator<Map<String, Object>> iterator = list.iterator(); iterator.hasNext();) {
			RelativeLayout elementlayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.detailelementlayout, null);
			elementlayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1));
			ImageView iv = (ImageView) elementlayout.getChildAt(0);
			final Map<String, Object> map = iterator.next();
			appstore.DisplayImage((String) map.get("image"), iv);
			elementlayout.setOnClickListener(new OnClickListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onClick(View v) {
					processbarlayout.setVisibility(View.GONE);
					downloadButton.setVisibility(View.VISIBLE);
					int downloadindex = Tools.getIndexByAppId(mAppData.getDownloadList(),
							Integer.parseInt((String) map.get("id")));
					if (downloadindex != -1) {
						List<AppBean> downloadlist = mAppData.getDownloadList();
						AppBean info = downloadlist.get(downloadindex);
						Integer state = (Integer) info.getStatus();
						Integer downloadstatus = (Integer) info.getDownloadStatus();
						if (state == Params.APP_STATUS_INSTALLED) {
							downloadButton.setBackgroundResource(R.drawable.runbutton1);
							downloadButton.setClickable(true);
							downloadButton.setTag(map);
							isRun = 1;
						} else if (downloadstatus == Params.DOWNLOADSTATUS_COMPLETE) {
							isRun = 2;
							downloadButton.setBackgroundResource(R.drawable.detail_installbtn1);
							downloadButton.setClickable(true);
							downloadButton.setTag(map);
						} else {
							// 下载
							downloadButton.setBackgroundResource(R.drawable.downloadunclick);
							downloadButton.setClickable(false);
						}

					} else {
						downloadButton.setClickable(true);
						downloadButton.setBackgroundResource(R.drawable.downloadbuttonstyle);
						downloadButton.setTag(map);
						isRun = 0;
					}
					appname.setText(null);
					appprice.setText(null);
					appsize.setText(null);
					appversion.setText(null);
					appdate.setText(null);
					appcategory1.setText(null);
					appcategory2.setText(null);
					appauthor.setText(null);
					appdesc.setText(null);
					apprate.setRating(0);
					url = null;
					if (map.get("image") != null) {
						imageUrl = map.get("image").toString();
						appstore.DisplayImage(imageUrl, image);
					}
					if (map.get("title") != null) {

						appname.setText((String) map.get("title"));
					} else {
						appname.append(nodata);
					}
					if (map.get("type") != null && map.get("ctitle") != null) {
						if (map.get("type").toString().equals("app")) {
							appcategory1.append("应用" + ">" + (String) map.get("ctitle"));
						} else {
							appcategory1.append("游戏" + ">" + (String) map.get("ctitle"));
						}
					} else {
						appcategory1.append(nodata);
					}
					if (map.get("rating") != null && !(map.get("rating").equals(""))) {
						apprate.setRating(Float.parseFloat((String) map.get("rating")));
					}
					if (map.get("pay") != null) {
						appprice.append("价格：" + (String) map.get("pay"));
					} else {
						appprice.append("价格：" + nodata);
					}
					if (map.get("size") != null) {
						appsize.append("大小：" + (String) map.get("size"));
					} else {
						appsize.append("大小：" + nodata);
					}
					if (map.get("version") != null) {
						appversion.append("版本：" + (String) map.get("version"));
					} else {
						appversion.append("版本：" + nodata);
					}
					if (map.get("date") != null) {
						appdate.append("发布时间：" + (String) map.get("date"));
					} else {
						appdate.append("发布时间：" + nodata);
					}
					/*
					 * String strs=""; strs=map.get("source")==null?"":map.get("source")+""; if(strs==""){ source.append("互联网");
					 * }else{ source.append(strs); }
					 */
					if (map.get("type") != null && map.get("ctitle") != null) {
						if (map.get("type").toString().equals("app")) {
							appcategory2.append("类别：应用" + ">" + (String) map.get("ctitle"));
						} else {
							appcategory2.append("游戏" + ">" + (String) map.get("ctitle"));
						}
					} else {
						appcategory2.append("类别：" + nodata);
					}
					if (map.get("author") != null) {
						appauthor.append("作者：" + (String) map.get("author"));
					} else {
						appauthor.append("作者：" + nodata);
					}
					if (map.get("description") != null) {
						appdesc.append((String) map.get("description"));
					} else {
						appdesc.append(nodata);
					}
					if (map.get("url") != null) {
						url = map.get("url").toString();
					}
					// /////////////////
					try {
						Map<String, Object> m = appstore.Get_details((String) map.get("id"));
						addViewFlipper((List<String>) m.get("screenshot"));
					} catch (WeiboException e) {
						e.printStackTrace();
					}

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

			name.setText((String) map.get("title"));
			cate.setText(Params.getTypeName((String) map.get("type")) + map.get("ctitle"));
			xiangGuen.addView(elementlayout);
		}

	}

	class ImageGesture extends View implements OnGestureListener {

		public ImageGesture(Context context) {
			super(context);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			return slideGesture.onTouchEvent(event);
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			slideGesture.onTouchEvent(ev);
			return super.dispatchTouchEvent(ev);
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
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

			return true;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		DownloadToastHandler.pop = null;

	}

	// add zcy
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("on Resume !!!!!!!");
		DataAcqusition.acquisitionUserOptionData(appId);

		/*
		 * if (Params.app_installed == 1) { Params.app_installed = 0; int downloadindex = download.getIndexByAppId(Integer
		 * .parseInt(appId)); if (downloadindex != -1) { List<Map<String, Object>> downloadlist = download.getAllList();
		 * Map<String, Object> info = downloadlist.get(downloadindex); Integer state = (Integer) info.get("status"); if (state ==
		 * 1) { System.out.println("on Resume set run button !!!!!!" );
		 * downloadButton.setBackgroundResource(R.drawable.runbutton1); downloadButton.setClickable(true);
		 * downloadButton.setTag(info); isRun = 1; } } }
		 */
	}

	class commentAsync extends AsyncTask<Object, Object, String> {
		Map<String, Object> map;

		@Override
		protected String doInBackground(Object... params) {
			map = (Map<String, Object>) params[0];
			String code = null;
			try {
				String content = (String) map.get("content");
				String id = (String) map.get("id");
				String ip = (String) map.get("ip");
				Map<String, Object> retmap = appstore.add_user_review(content, "4", id, ip);
				System.out.println("retmap code = " + retmap.get("code"));
				code = (String) retmap.get("code");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return code;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null && result.equals("1")) {
				Toast.makeText(AppDetailActivity.this, "评论发表成功", Toast.LENGTH_SHORT).show();
				getCommentAsync task = new getCommentAsync();
				task.execute(map);
			}
		}
	}

	class scoreAsync extends AsyncTask<Object, Object, String> {
		String avgscore = "0";

		@Override
		protected String doInBackground(Object... params) {
			Map<String, Object> map = (Map<String, Object>) params[0];
			String code = "0";
			try {
				String score = (String) map.get("score");
				String id = (String) map.get("id");
				String ip = (String) map.get("ip");
				Map<String, Object> retmap = appstore.add_user_score(score, "4", id, ip);
				System.out.println("retmap code = " + retmap.get("code"));
				code = (String) retmap.get("code");
				avgscore = (String) retmap.get("score");
				if (avgscore == null)
					avgscore = score;
				System.out.println("fabiao pinfen score = " + retmap.get("score"));

			} catch (Exception e) {
				e.printStackTrace();
			}
			return code;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null && result.equals("1")) {
				String num = (String) scoreText.getText();
				scoreText.setText(Integer.parseInt(num) + 1 + "");

				if (!avgscore.equals("0")) {
					System.out.println("updateadfaf rating score = " + avgscore);
					apprate.setRating(Float.parseFloat(avgscore));
				}
			} else {
				if (avgscore.equals("5")) {
					Toast.makeText(AppDetailActivity.this, "谢谢配合，但是评论还是失败，网络原因！", Toast.LENGTH_SHORT).show();
				}
				Toast.makeText(AppDetailActivity.this, "评论失败，请选择5星！", Toast.LENGTH_SHORT).show();
			}
		}
	}

	class getCommentAsync extends AsyncTask<Object, Object, List<Map<String, Object>>> {

		@Override
		protected List<Map<String, Object>> doInBackground(Object... params) {
			if (commentdataList != null) {
				commentdataList.clear();
			} else {
				commentdataList = new ArrayList<Map<String, Object>>();
			}
			Map<String, Object> map = (Map<String, Object>) params[0];
			try {
				String id = (String) map.get("id");
				Map<String, Object> retmap = appstore.get_user_review("4", id, "");
				System.out.println("retmap1 code = " + retmap.get("code"));
				String code = (String) retmap.get("code");
				String count = (String) retmap.get("count");
				if (code.equals("1") && Integer.parseInt(count) > 0) {
					commentdataList = (List<Map<String, Object>>) retmap.get("review");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return commentdataList;
		}

		@Override
		protected void onPostExecute(List<Map<String, Object>> result) {
			CommentDataAdapter commentadapter = new CommentDataAdapter(AppDetailActivity.this, result);
			mcommentList.setAdapter(commentadapter);
			commentText.setText(result.size() + "");
		}
	}

	class getCommentAndScoreNumAsync extends AsyncTask<Object, Object, String> {
		String reviewCount = "0";
		String scoreCount = "0";
		String score = "0";

		@Override
		protected String doInBackground(Object... params) {
			Map<String, Object> map = (Map<String, Object>) params[0];
			try {
				String id = (String) map.get("id");
				Map<String, Object> retmap = appstore.get_user_review("4", id, "count");
				System.out.println("retmap1 code = " + retmap.get("code"));
				if (retmap.get("code").equals("1")) {
					reviewCount = (String) retmap.get("count");
				}
				retmap = appstore.get_user_score("4", id);
				if (retmap.get("code").equals("1")) {
					scoreCount = (String) retmap.get("count");
					score = (String) retmap.get("score");
					System.out.println("score = " + score);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			scoreText.setText(scoreCount);
			commentText.setText(reviewCount);
			if (!score.equals("0")) {
				apprate.setRating(Float.parseFloat(score));
			}
			commentLayout.setVisibility(View.VISIBLE);
		}
	}

	class downloadReportAsync extends AsyncTask<String, Object, String> {
		@Override
		protected String doInBackground(String... params) {
			String appid = params[0];
			String userid = params[1];
			String deviceid = params[2];
			try {
				System.out.println("download report : appid = " + appid + "userid = " + userid + "deviceid = " + deviceid);
				Map<String, Object> retmap = appstore.download_report(appid, userid, deviceid, "", "");
				System.out.println("download report retmap = " + retmap);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {

		}
	}

	class InstallButtonHandlerX extends Handler {
		private View installButton;

		public InstallButtonHandlerX(View installButton) {
			this.installButton = installButton;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Params.INSTALL_SUCCEED:
				installButton.setBackgroundResource(R.drawable.runbuttonstyle);
				String pck = (String) msg.obj;
				Map<String, Object> m = (Map<String, Object>) installButton.getTag();
				m.put("runType", 1);
				m.put("package", pck);
				break;
			default:
				break;
			}
		}
	}
}