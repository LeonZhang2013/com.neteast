package com.neteast.clouddisk.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.app.ActivityGroup;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.cloud.LibCloud;
import com.lib.db.AppDao;
import com.lib.db.DataInfo;
import com.lib.net.WeiboException;

import com.neteast.clouddisk.R;
import com.neteast.clouddisk.adapter.AppDataAdapter;
import com.neteast.clouddisk.adapter.MusicDataAdapter;
import com.neteast.clouddisk.adapter.ViewPageAdapter;
import com.neteast.clouddisk.customerview.CustomerGridView;
import com.neteast.clouddisk.handler.DownloadToastHandler;
import com.neteast.clouddisk.handler.InstallFinishHandler;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.service.MusicUtils;
import com.neteast.clouddisk.utils.DataHelpter;
import com.neteast.clouddisk.utils.DownLoadApplication;
import com.neteast.clouddisk.utils.ImageDownloader2;
import com.neteast.data_acquisition.DataAcqusition;

public class RecommendAppActivity extends ActivityGroup {
	private LinearLayout sourceSiteLayout;
	private LinearLayout searchResultView;
	private GridView gridView;
	private LinearLayout loadingView = null;

	private int currentPage = 1;
	private int totalPage = 1;
	private TextView currentPageNum = null;
	private TextView totalPageNum = null;
	private ImageView currentTab;
	
	private int currentIndex = 0;
	
	private RadioButton rbApp;
	private RadioButton rbGame;
	
	private LibCloud libCloud;
	private int flingDirection = 1;
	private DownLoadApplication download;
	private AppDao appDao = AppDao.getInstance(this);
	List<Map<String, Object>> appList = null;
	List<DataInfo> downloadList = null;
	private String[] sourceSite = new String[] {"应用","游戏" };
	/** ViewPager控件，横屏滑动*/
	private ViewPager filePager;
	
	/** ViewPager的视图列表*/
	private List<View> mListViews;
	
	/** ViewPager的适配器. */
	private ViewPageAdapter viewPageAdapter;
	private static final int PAGE_SIZE = Params.RECOMMEND_APP_PER_PAGE_NUM;
	private ImageDownloader2 mImageDownloader2;
	
	public int getSelectTag(){
		return currentIndex; 
	}
	public String getSelectTagStr(){
		return sourceSite[currentIndex];
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		libCloud  = LibCloud.getInstance(this);
		download = (DownLoadApplication) getApplication();
		mImageDownloader2 = download.getImageDownloader();
		setContentView(R.layout.recommendapp);
		
		Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY);
		currentPageNum = (TextView) findViewById(R.id.curentpage);
		totalPageNum = (TextView) findViewById(R.id.totalpage);
		rbApp = (RadioButton) findViewById(R.id.appbtn);
		rbGame = (RadioButton) findViewById(R.id.gamebtn);
		
		loadingView = (LinearLayout)findViewById(R.id.recommand_app_loading);
		
		gridView = (CustomerGridView) findViewById(R.id.mygridview);
		filePager = (ViewPager) findViewById(R.id.recommendAppViewPager);
		sourceSiteLayout = (LinearLayout) findViewById(R.id.titlebtnlayout);
		searchResultView = (LinearLayout) findViewById(R.id.searchresultView);
		Button returnbtn = (Button) findViewById(R.id.searchreturn);
		returnbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sourceSiteLayout.setVisibility(View.VISIBLE);
				searchResultView.setVisibility(View.GONE);
				GetDataTask gdt = new GetDataTask();
				if(currentIndex==0){
					rbApp.setChecked(true);
				}else{
					rbGame.setChecked(true);
				}
				gdt.execute(currentPage, sourceSite[currentIndex],PAGE_SIZE,currentIndex);
			}
		});
		TextView searchresulttv = (TextView) findViewById(R.id.searchresultText);
		
	
		
		
		int searchflag = this.getIntent().getIntExtra("searchflag",-1);
		System.out.println("recommendMusicActivity start searchflag = " + searchflag);
		if(searchflag>=0){
			searchResultView.setVisibility(View.VISIBLE);
			sourceSiteLayout.setVisibility(View.INVISIBLE);
			loadingView.setVisibility(View.GONE);
			currentIndex = searchflag;
			if(currentIndex==0){
				rbApp.setChecked(true);
			}else{
				rbGame.setChecked(true);
			}
			Intent intent = this.getIntent();
			List<DataInfo> list = (List<DataInfo>) intent.getSerializableExtra("result");
			if(list!=null && list.size()>0){
				addDataToGridView(list);
			}
			String value = String.format(getResources().getString(R.string.search_result_text), list.size());
			searchresulttv.setText(value);
			currentPageNum.setText(currentPage+"");
			totalPageNum.setText(totalPage+"");
		}else{
			searchResultView.setVisibility(View.GONE);
			sourceSiteLayout.setVisibility(View.VISIBLE);
			GetDataTask gdt = new GetDataTask();
			gdt.execute(currentPage, sourceSite[0],PAGE_SIZE,currentIndex);
		}
		addListener();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void addListener() {
		rbApp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					GetDataTask gdt = new GetDataTask();
					currentPage = 1;
					totalPage = 1;
					currentIndex = 0;
					gdt.execute(currentPage, sourceSite[0],PAGE_SIZE,currentIndex);
				}
			}
		});
		rbGame.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					GetDataTask gdt = new GetDataTask();
					currentPage = 1;
					totalPage = 1;
					currentIndex = 1;
					gdt.execute(currentPage, sourceSite[1],PAGE_SIZE,currentIndex);
				}
				
			}
		});

	}
	private void setGrid(Context context, List<DataInfo> list) {
	//	int PageCount = (int) Math.ceil(list.size() / PAGE_SIZE);
		int PageCount = (int) Math.ceil(((double)list.size())/PAGE_SIZE);
		System.out.println("setGrid  PageCount = " + PageCount);
		List<String> installedApp =  DataHelpter.getInstalledApp(this);
		
		for (int i = 0; i < PageCount; i++) {
			gridView = (GridView) LayoutInflater.from(context).inflate(R.layout.recommend_appgrid, null);
			gridView.setAdapter(new AppDataAdapter(context, list,i,installedApp));
			//gridView.setVerticalSpacing(100);
			//gridView.setHorizontalSpacing(20);	
			
			// 去掉点击时的黄色背景
			gridView.setSelector(R.color.transparent);
			mListViews.add(gridView);
		}
	 }

	public void onClickInstallBtn(View v){
		v.setClickable(false);
		DataInfo dataInfo = (DataInfo)v.getTag();
		DataAcqusition.downLoadRecord(dataInfo.getId());
		dataInfo.setType("6");
		System.out.println("onClickInstallBtn datainfo id = " + dataInfo.getId());
		try {
			ImageView open = (ImageView) ((LinearLayout) v.getParent()).getChildAt(2);
			InstallFinishHandler runHandler = new InstallFinishHandler(this, v, open);
			DownloadToastHandler handler = new DownloadToastHandler(this, v);
			download.getDownloadList().put(dataInfo.getId(), handler);
			download.getUpdateBtnList().put(dataInfo.getId(), runHandler);
			String downloadUrl = dataInfo.getUrl();
			String downloadName = dataInfo.getUrl().substring(downloadUrl.lastIndexOf("/"), downloadUrl.length());
			List<DataInfo> dInfo = appDao.getAppById(Integer.parseInt(dataInfo.getId()));
			if (dInfo.size() != 0) {
				File fName = new File(Params.DOWNLOAD_FILE_PATH + downloadName);
				if (fName.exists()) {
					// 安装
					download.getDownloadList().remove(dataInfo.getId());
					v.setClickable(true);
					File file = new File(Params.DOWNLOAD_FILE_PATH + downloadName);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.putExtra("path", file.toString());
					intent.setDataAndType( Uri.parse("file://" + file.toString()), "application/vnd.android.package-archive");
					this.startActivity(intent);
					return;
				} else {
					download.getDownloadList().remove(dataInfo.getId());
					appDao.deleteAppBtPackage((dInfo.get(0)).getPackages());
				}
			}
			download.download(dataInfo);
			((ImageView)v).setImageResource(R.drawable.downloading);
			showCustomTost();
			download.getDownloadListState().put(dataInfo.getId(),v);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showCustomTost(){
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.toastlayout, (ViewGroup) findViewById(R.id.toast_layout_root));
		ImageView image = (ImageView) layout.findViewById(R.id.image);
		image.setImageResource(R.drawable.downloadingtoast);
		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL,-400, -230);
		toast.setDuration(Params.DOWNLOADING_TOAST_TIME);
		toast.setView(layout);
		toast.show();
	}
		
	public void onClickOpenBtn(View view) {
		String pack = (String) view.getTag();
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(pack);
		List<ResolveInfo> apps = getPackageManager().queryIntentActivities(
				resolveIntent, PackageManager.GET_ACTIVITIES);
		if (apps == null || apps.size() == 0) {
			appDao.deleteAppBtPackage(pack);
			view.setVisibility(View.GONE);
			((LinearLayout) view.getParent()).getChildAt(1).setVisibility(
					View.VISIBLE);
			Toast.makeText(this, "无法启动该应用？", Toast.LENGTH_LONG).show();
			return;
		}
		ResolveInfo ri = apps.get(0);
		if (ri != null) {
			String className = ri.activityInfo.name;
			ComponentName cn = new ComponentName(pack, className);
			Intent intent = new Intent();
			intent.setComponent(cn);
			startActivity(intent);
		}
	}

	public void addDataToGridView(final List<DataInfo> result) {
		mListViews = new ArrayList<View>();
		setGrid(this, result);
		viewPageAdapter = new ViewPageAdapter(this,mListViews,Params.RECOMMEND_APP);
		filePager.setAdapter(viewPageAdapter);
		filePager.setCurrentItem(0);
		filePager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int page) {
				totalPage = DataHelpter.computeTotalPage(result.size(),PAGE_SIZE);
				
				myHandler.removeMessages(PAGE_CHANGED);		
				Message msg = new Message();
				msg.what = PAGE_CHANGED;
				Bundle b = new Bundle();
				b.putInt("page", page);
				b.putInt("cpage", currentPage);
				msg.setData(b);
				myHandler.sendMessageDelayed(msg, 1000);
				
				currentPage = page + 1;
				currentPageNum.setText(currentPage+"");
				totalPageNum.setText(totalPage+"");
				

			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			public void onPageScrollStateChanged(int arg0) {}
		});
	}
	
	private void displayImageView(int page){
		GridView v = (GridView) mListViews.get(page);
		AppDataAdapter adapter1 = (AppDataAdapter) v.getAdapter();
		for(int i=0;i<v.getCount();i++){
			View childv = v.getChildAt(i);
			if(childv!=null){
				DataInfo info = (DataInfo) adapter1.getItem(i);
				ImageView imageView = (ImageView) childv.findViewById(R.id.imageView);
			
				//libCloud.DisplayImage(info.getImage(), imageView);
				
				mImageDownloader2.download(info.getImage(), imageView);
			}
		}
	}
	
	private void clearImageView(int page){
		GridView v = (GridView) mListViews.get(page);
		AppDataAdapter adapter1 = (AppDataAdapter) v.getAdapter();
		for(int i=0;i<v.getCount();i++){
			View childv = v.getChildAt(i);
			
			DataInfo info = (DataInfo) adapter1.getItem(i);
			if(childv!=null){
				ImageView imageView = (ImageView) childv.findViewById(R.id.imageView);
				
				if(imageView!=null){
					imageView.setImageDrawable(null);
				}
				
				String imageurl = info.getImage();
				if(imageurl!=null){
					libCloud.ClearImageCache(imageurl);
				}
			}
		}
	}

	class GetDataTask extends AsyncTask<Object, Integer, List<DataInfo>> {
		int index = 0;
		@Override  
	    protected void onPreExecute() {  
			//第一个执行方法
			loadingView.setVisibility(View.VISIBLE);
		    super.onPreExecute();  
		}  
		
		@SuppressWarnings("unchecked")
		@Override
		protected List<DataInfo> doInBackground(Object... params) {
			String source =(String) params[1];
			index = (Integer) params[3];
			DataHelpter.printCurTime("recommand app request start source = " + source);
			try {
				/*
				appList = DataHelpter.getAppList();
				if (appList == null) {
					appList = (List<Map<String, Object>>) libCloud
							.Get_recommend_list(Params.RECOMMEND_APP, source,"").get(
									"recommend");
					DataHelpter.setAppList(appList);
				}
				*/
				String reqtime = "";
				Map<String,Object> map =  DataHelpter.getAppList(source);
				if(map!=null){
					appList = (List<Map<String, Object>>) map.get("recommend");
				}
				if (map==null || appList == null) {
					Map<String ,Object> map1 = libCloud.Get_recommend_list(Params.RECOMMEND_APP, source,"");
					if(map1!=null){
						String code = (String) map1.get("code");
						if(code.equals("1")){
							appList = (List<Map<String, Object>>) map1.get("recommend");
							reqtime = (String) map1.get("reqtime");
						}
					}	
					DataHelpter.setAppList(appList,source,reqtime);
				}else{
					
					reqtime = (String) map.get("reqtime");
					Map<String ,Object> map1 = libCloud.Get_recommend_list(Params.RECOMMEND_APP, source,reqtime);
					List<Map<String, Object>> updateList = null ;
					if(map1!=null){
						String code = (String) map1.get("code");
						if(code.equals("1")){
							updateList = (List<Map<String, Object>>) map1.get("recommend");
							reqtime = (String) map1.get("reqtime");
						}
					}
					if(updateList==null){
						updateList = new ArrayList<Map<String, Object>>();
					}
					DataHelpter.updateAppList(updateList,source,reqtime);
					Map<String,Object> map2 =  DataHelpter.getAppList(source);
					System.out.println("map2 = " + map2);
					if(map2!=null){
						System.out.println("get new from cache!!!");
						appList = (List<Map<String, Object>>) map2.get("recommend");
					}
					System.out.println("appList = " + appList);
				}

			} catch (WeiboException e) {
				e.printStackTrace();
			}
			//DataHelpter.printCurTime("recommand app request finished ");
			Map<String, Object> m = DataHelpter.fillData(appList,downloadList, params);
			if(m==null){
				return new ArrayList<DataInfo>();
			}
			//DataHelpter.printCurTime("recommand app fenji finished ");
			//totalPage = (Integer)m.get("totalpage");
			
			return (List<DataInfo>)m.get("result");
		}

		@Override
		protected void onPostExecute(List<DataInfo> result) {
			if(index == currentIndex){
				totalPage = DataHelpter.computeTotalPage(result.size(),PAGE_SIZE);
				if(result!=null && result.size()>0){
					currentPageNum.setText(currentPage+"");
					totalPageNum.setText(totalPage+"");
					addDataToGridView(result);
				}else{
					currentPageNum.setText("1");
					totalPageNum.setText("1");
				}
			}
			loadingView.setVisibility(View.GONE);
		}

	}
	class ClearImageTask extends AsyncTask<Object, Integer, Integer> {
		
		@SuppressWarnings("unchecked")
		@Override
		protected Integer doInBackground(Object... params) {
			int page =(Integer) params[0];
			
			return page;
		}

		@Override
		protected void onPostExecute(Integer result) {
			clearImageView(result);
			
		}

	}
	
	@Override
	public void onDestroy()
	{
		//mImageDownloader2.clearCache();
		//libCloud.ClearCache();
		libCloud.ClearMemoryCache();
		super.onDestroy();
	  
	}
	
	private final static int PAGE_CHANGED = 0;
	Handler myHandler = new Handler(){
		    
		@Override
		public void handleMessage(Message msg) {
				// TODO Auto-generated method stub	
			switch(msg.what){	
				case PAGE_CHANGED:
					int page = msg.getData().getInt("page");
					int cpage = msg.getData().getInt("cpage");
					System.out.println("hadnleMessage page = " + page + "cpage = " + cpage);
					displayImageView(page);
					/*
					if(cpage <=page){
						clearImageView(page-1);
					}else{
						clearImageView(page+1);
					}
					*/
					break;
			}	
			super.handleMessage(msg);
		}	
	};
}