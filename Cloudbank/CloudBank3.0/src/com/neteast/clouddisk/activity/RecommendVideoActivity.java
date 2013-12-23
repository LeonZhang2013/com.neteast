package com.neteast.clouddisk.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.lib.net.WeiboException;

import com.neteast.clouddisk.R;
import com.neteast.clouddisk.activity.RecommendAppActivity.ClearImageTask;
import com.neteast.clouddisk.adapter.VideoDataAdapter;
import com.neteast.clouddisk.adapter.VideoViewPageAdapter;
import com.neteast.clouddisk.adapter.ViewPageAdapter;
import com.neteast.clouddisk.customerview.CustomerGridView;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.utils.DataHelpter;
import com.neteast.clouddisk.utils.DownLoadApplication;
import com.neteast.clouddisk.utils.ImageDownloader2;
import com.neteast.clouddisk.utils.UIHelper;
import com.neteast.data_acquisition.DataAcqusition;

public class RecommendVideoActivity extends ActivityGroup {
	private String[] sourceSite = new String[]{ "全部", "电影", "电视剧", "综艺", "动漫", "新闻","体育" };
	
	private LinearLayout sourceSiteLayout;
	private LinearLayout searchResultView;
	private GridView gridView;
	
	private LinearLayout loadingView = null;

	private int currentPage = 1;
	private int totalPage = 1;
	private TextView currentPageNum = null;
	private TextView totalPageNum = null;
	private TextView currentTab;
	private LibCloud libCloud;
	int flingDirection = 1;
	PopupWindow pw = null;
	private View itemView = null;
	private List<Map<String, Object>> videoList = null;
	private List<DataInfo>  videoDataList = null;

	List<DataInfo> downloadList = null;

	private int curType = 1;
	private int currentIndex = 0;
	private int searchflag = 0;
	
	
	/** ViewPager控件，横屏滑动*/
	private ViewPager filePager;
	
	/** ViewPager的视图列表*/
	private List<View> mListViews;
	
	/** ViewPager的适配器. */
	private ViewPageAdapter viewPageAdapter;
	private static final int PAGE_SIZE = Params.RECOMMEND_DATA_PER_PAGE_NUM;
	
	private ImageDownloader2 mImageDownloader2;
	
	private int testFlag = 0;
	
	public int getSelectTag(){
		return currentIndex; 
	}
	public String getSelectTagStr(){
		TextView textView = (TextView) sourceSiteLayout.getChildAt(currentIndex);
		return (String) textView.getText();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		libCloud = LibCloud.getInstance(this);
		mImageDownloader2 = ((DownLoadApplication) getApplication()).getImageDownloader();
		setContentView(R.layout.recommendvideo);
		
		Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY);

		currentPageNum = (TextView) findViewById(R.id.curentpage);
		totalPageNum = (TextView) findViewById(R.id.totalpage);
		gridView = (CustomerGridView) findViewById(R.id.mygridview);

		loadingView = (LinearLayout)findViewById(R.id.recommand_video_loading);
		
		filePager = (ViewPager) findViewById(R.id.recommendVideoViewPager);
		
		sourceSiteLayout = (LinearLayout) findViewById(R.id.videosourcesite);
		searchResultView = (LinearLayout) findViewById(R.id.searchresultView);
		Button returnbtn = (Button) findViewById(R.id.searchreturn);
		returnbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sourceSiteLayout.setVisibility(View.VISIBLE);
				searchResultView.setVisibility(View.GONE);
				recommendStart();
				searchflag = -1;
			}
		});
		TextView searchresulttv = (TextView) findViewById(R.id.searchresultText);

		currentIndex  = this.getIntent().getIntExtra("focusIndex", 0);
		curType  = this.getIntent().getIntExtra("curType",1);
		currentPage  = this.getIntent().getIntExtra("currentPage",1);
		
		System.out.println("recommendVideoActivity start currentPage = " + currentPage);
		
		//recommandStart(null);
		
		 searchflag = this.getIntent().getIntExtra("searchflag",-1);
		 System.out.println("recommendVideoActivity start searchflag = " + searchflag);
		
		if(searchflag >=0){
			searchResultView.setVisibility(View.VISIBLE);
			sourceSiteLayout.setVisibility(View.INVISIBLE);
			loadingView.setVisibility(View.GONE);
			currentIndex = searchflag;
			List<Map<String, Object>> videoTags = DataHelpter.getVideoTags();
			if(videoTags!=null && videoTags.size()>0){
				initTagView(videoTags);
			}
			Intent intent = this.getIntent();
			List<DataInfo> list = (List<DataInfo>) intent.getSerializableExtra("result");
			if(list!=null && list.size()>0){
				addDataToGridView(list);
				DataHelpter.setSearchList(list);
				totalPage = DataHelpter.computeTotalPage(list.size(),PAGE_SIZE);
			}else{
				list = DataHelpter.getSearchList();
				addDataToGridView(list);
				totalPage = DataHelpter.computeTotalPage(list.size(),PAGE_SIZE);
			}
			String value = String.format(getResources().getString(R.string.search_result_text), list.size());
			searchresulttv.setText(value);
			currentPageNum.setText(currentPage+"");
			totalPageNum.setText(totalPage+"");
		}
		else{
			searchResultView.setVisibility(View.GONE);
			sourceSiteLayout.setVisibility(View.VISIBLE);
			List<Map<String, Object>> videoTags = DataHelpter.getVideoTags();
			if(videoTags!=null && videoTags.size()>0){
				initTagView(videoTags);
				recommendStart();
			}else{
				GetMovieTypeAsync movieTypeAsync = new GetMovieTypeAsync();
				movieTypeAsync.execute();
			}
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		mImageDownloader2.clearCache();
		System.out.println("RecommendVideoActivity on Destroy !!!!!!!\n");
	}
	
	@Override
	public void onPause() {
		super.onPause();
		System.out.println("RecommendVideoActivity on Pause !!!!!!!\n");
	}
	private void initTagView(List<Map<String, Object>> datatype){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(90, LinearLayout.LayoutParams.WRAP_CONTENT);
		if(datatype!=null){
			for (int i = 0; i < datatype.size(); i++) {
				TextView tv = new TextView(this);
				// //////temp
				tv.setTag(i);
				// ////////////
				//tv.setText(sourceSite[i]);
				tv.setText((CharSequence) datatype.get(i).get("name"));
				tv.setLayoutParams(params);
				tv.setGravity(Gravity.CENTER_HORIZONTAL);
				tv.setTextAppearance(this, R.style.tabtextstyle);
				if (i != 0) {
					params.leftMargin = 20;
				}
				tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					currentIndex = ((Integer)v.getTag()).intValue();
					System.out.println("currentIndex = " + currentIndex);
					if(currentTab != v){
						searchflag = -1;
						currentTab = (TextView) v;
						String source = (String) currentTab.getText();
						if(source.equals(getResources().getString(R.string.type_movie))){
							curType = 1;
						}else if(source.equals(getResources().getString(R.string.type_series))
								||source.equals(getResources().getString(R.string.type_anima))){
							curType = 2;
						}else if(source.equals(getResources().getString(R.string.type_variety)) ){
							curType = 3;
						}else if(source.equals(getResources().getString(R.string.type_sports))
								||source.equals(getResources().getString(R.string.type_news))){
							curType = 4;
						}
						UIHelper.titleStyleDependState(sourceSiteLayout,
								RecommendVideoActivity.this, currentTab);
						GetDataTask gdt = new GetDataTask();
						currentPage = 1;
						totalPage = 1;
						gdt.execute(currentPage,currentTab,PAGE_SIZE);
					}
				}});
				sourceSiteLayout.addView(tv);
			}
		}
		
		if(datatype!=null && datatype.size()>0){
			TextView textView;
			textView = (TextView) sourceSiteLayout.getChildAt(currentIndex);
			textView.setTextAppearance(this, R.style.tabtextselectstyle);
			textView.setBackgroundResource(R.drawable.titlefocus);
			currentTab = textView;
			//curType = 1;
		}else{
			TextView textView = new TextView(this);
			textView.setText(sourceSite[0]);
			textView.setTag(0);
			currentTab = textView;
		}
	}
	private void recommendStart(){
		GetDataTask gdt = new GetDataTask();
		gdt.execute(currentPage,currentTab,PAGE_SIZE);
	}
	
	private void setGrid(Context context, List<DataInfo> list) {
	//	int PageCount = (int) Math.ceil(list.size() / PAGE_SIZE);
		int PageCount = (int) Math.ceil(((double)list.size())/PAGE_SIZE);
		System.out.println("setGrid  PageCount = " + PageCount);
		//PageCount = 1;
		int displayImage = 0;
		for (int i = 0; i < PageCount; i++) {
			gridView = (GridView) LayoutInflater.from(context).inflate(R.layout.recommend_grid, null);
			if(i == (currentPage -1)){
				displayImage = 1;
			}else{
				displayImage = 0;
			}
			gridView.setVerticalSpacing(35);
			gridView.setAdapter(new VideoDataAdapter(context, list,i,displayImage,mClickListener));
			//gridView.setOnItemClickListener(mOnItemClick); 
				
			// 去掉点击时的黄色背景
			gridView.setSelector(R.color.transparent);
			mListViews.add(gridView);
		}
	 }
	
	private View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			System.out.println("RecommendVideo on Item Clicked");
			DataInfo info = (DataInfo) arg0.getTag();
			DataAcqusition.clickDetail(info.getType(), info.getResid());
			System.out.println("RecommendVideoActivity on item clicked" + "name = " + info.getName() + "url = " + info.getUrl() );	
			/*
			if(info.getUrl()==null || info.getUrl().length()==0){
				UIHelper.displayToast(getResources().getString(R.string.invalid_url), RecommendVideoActivity.this);
			}else {
				Intent it = new Intent(); 
				it.setClass(RecommendVideoActivity.this,VideoPlaybackActivity.class);
				Uri uri = Uri.parse(info.getUrl());
				it.setData(uri);
				it.putExtra("position", 0);

				startActivity (it);
			}
			*/
			if(info.getChildtype().equals(getResources().getString(R.string.type_movie))){
				curType = 1;
			}else if(info.getChildtype().equals(getResources().getString(R.string.type_series))
					|| info.getChildtype().equals(getResources().getString(R.string.type_anima))){
				curType = 2;
			}else if(info.getChildtype().equals(getResources().getString(R.string.type_variety))){
				curType = 3;
			}else if(info.getChildtype().equals(getResources().getString(R.string.type_sports))
					||info.getChildtype().equals(getResources().getString(R.string.type_news))){
				curType = 4;
			}else{	 
				curType = 1;
			}
			
			Activity a = RecommendVideoActivity.this.getParent();
			if(a instanceof RecommendActivity){
				((RecommendActivity)a).startDetailActivity(curType,currentIndex,info,currentPage,searchflag);
			}
			else if(a instanceof MovieDetailActivity){
				((RecommendActivity)a.getParent()).startDetailActivity(curType,currentIndex,info,currentPage,searchflag);
			}
		}
	};
	
	OnItemClickListener mOnItemClick = new OnItemClickListener() {   
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {   
			System.out.println("RecommendVideoActivity on Item click arg2 :" + arg2);				
			VideoDataAdapter adapter1 = (VideoDataAdapter) ((GridView) mListViews.get(currentPage -1)).getAdapter();
			DataInfo info = (DataInfo) adapter1.getItem(arg2);
			System.out.println("RecommendVideoActivity on item clicked" + "name = " + info.getName() + "url = " + info.getUrl() );	
			
		
			/*
			if(info.getUrl()==null || info.getUrl().length()==0){
				UIHelper.displayToast(getResources().getString(R.string.invalid_url), RecommendVideoActivity.this);
			}else {
				Intent it = new Intent(); 
				it.setClass(RecommendVideoActivity.this,VideoPlaybackActivity.class);
				Uri uri = Uri.parse(info.getUrl());
				it.setData(uri);
				it.putExtra("position", 0);

				startActivity (it);
			}
		  	*/

		}      
	};

	/*
	 * 点击下载按钮
	 * */
	@SuppressWarnings("unchecked")
	public void downloadButtonClick(View v) {
		ImageView iv = (ImageView) v;
		iv.setClickable(false);
		LinearLayout parent = (LinearLayout) v.getParent().getParent();
		ProgressBar pb = (ProgressBar) parent.findViewById(R.id.downloadprogress);
		pb.setVisibility(View.VISIBLE);
		Map<String, Object> info = (Map<String, Object>) v.getTag();
		DataInfo di = (DataInfo) info.get("datainfo");
		DownLoadAsync async = new DownLoadAsync();
		async.execute(new Object[]{di,iv,pb});
		UIHelper.displayToast(getResources().getString(R.string.download_start_text), RecommendVideoActivity.this);
	}
	/*
	 * 点击详细
	 */
	public void showVideoDetail(View v) {
		DataInfo info =null;
		info = (DataInfo)v.getTag();
		Activity a = RecommendVideoActivity.this.getParent();
		if(a instanceof RecommendActivity){
			((RecommendActivity)a).startDetailActivity(curType,currentIndex,info,currentPage,searchflag);
		}
		else if(a instanceof MovieDetailActivity){
			((RecommendActivity)a.getParent()).startDetailActivity(curType,currentIndex,info,currentPage,searchflag);
		}

	}
	private void showDetailWin(View v,String value) {
		if (pw != null) {
			pw.dismiss();
			pw = null;
		}
		LinearLayout layout = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.popdetailslayout, null);
		/*View view = ((FrameLayout) ((LinearLayout) ((LinearLayout) v
				.getParent()).getParent()).getChildAt(0)).getChildAt(0);*/
		
		
		pw = new PopupWindow(layout, 392, 243);
		// pw.setFocusable(true);
		pw.setOutsideTouchable(true);
		
		// pw.showAsDropDown(, 0, 0);
		pw.showAtLocation(v, Gravity.TOP, 0,0);
		int position = ((DataInfo)itemView.getTag()).getPosition();
		if(position%7>3){
			pw.update(v, -300, -280, 392, 243);
		}else{
			pw.update(v, -150, -280, 392, 243);
		}
		TextView tv = (TextView) layout.findViewById(R.id.details);
		tv.setText(value);
		ImageButton ib = (ImageButton) layout.findViewById(R.id.popclose);
		ib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pw.dismiss();
			}
		});
	}	


	public void addDataToGridView(final List<DataInfo> result) {
		mListViews = new ArrayList<View>();
		setGrid(this, result);
		viewPageAdapter = new ViewPageAdapter(this,mListViews,Params.RECOMMEND_VIDEO);
		filePager.setAdapter(viewPageAdapter);
		
		filePager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int page) {

				totalPage = DataHelpter.computeTotalPage(result.size(),PAGE_SIZE);
				//System.out.println("currentPage = " + currentPage + "page = " + page);
				
				myHandler.removeMessages(PAGE_CHANGED);	
				
				Message msg = new Message();
				msg.what = PAGE_CHANGED;
				Bundle b = new Bundle();
				b.putInt("page", page);
				b.putInt("cpage", currentPage);
				msg.setData(b);
				myHandler.sendMessageDelayed(msg, 1000);
				
			//	displayImageView(page);
				currentPage = page + 1;
				
				currentPageNum.setText(currentPage+"");
				totalPageNum.setText(totalPage+"");
				
				System.out.println(" set OnPageChangeListener finished  page = " + page);
			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		
		filePager.setCurrentItem(currentPage-1);
		

		DataHelpter.printLog(RecommendVideoActivity.this,"recommend video : dinished");
		DataHelpter.printCurTime("finished ");
		
	}
	
	private void displayImageView(int page){
		GridView v = (GridView) mListViews.get(page);
		VideoDataAdapter adapter1 = (VideoDataAdapter) v.getAdapter();
		for(int i=0;i<v.getCount();i++){
			View childv = v.getChildAt(i);	
			DataInfo info = (DataInfo) adapter1.getItem(i);
			if(childv!=null){
				ImageView imageView = (ImageView) childv.findViewById(R.id.imageView);
				String imageurl = info.getImage();
				if(imageurl!=null && imageurl.length()>0){
					
					//libCloud.DisplayImage(imageurl,imageView);
					//System.out.println("start download image url = " + imageurl);
				
					mImageDownloader2.download(imageurl, imageView);
				}
			}
		}
	}
	
	private void clearImageView(int page){
		GridView v = (GridView) mListViews.get(page);
		VideoDataAdapter adapter1 = (VideoDataAdapter) v.getAdapter();
		for(int i=0;i<v.getCount();i++){
			View childv = v.getChildAt(i);
			
			DataInfo info = (DataInfo) adapter1.getItem(i);
			if(childv!=null){
				ImageView imageView = (ImageView) childv.findViewById(R.id.imageView);
				if(imageView!=null){
					imageView.setImageDrawable(null);
				}
			}
		}
		
	}
	
	private void clearImageCache(int page){
		System.out.println("clearImageView page = " + page);
		GridView v = (GridView) mListViews.get(page);
		VideoDataAdapter adapter1 = (VideoDataAdapter) v.getAdapter();
		for(int i=0;i<v.getCount();i++){
			View childv = v.getChildAt(i);
			
			DataInfo info = (DataInfo) adapter1.getItem(i);
			if(childv!=null){
				ImageView imageView = (ImageView) childv.findViewById(R.id.imageView);
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
			String source;
			TextView tab = (TextView) params[1];
			source = (String) tab.getText();
			index  = ((Integer)tab.getTag()).intValue();
			System.out.println("index = " + index);
			if(source.equals(sourceSite[0])){
				source="";
			}
			System.out.println("RecommendVideoActivity doInBackground source :"+source);
			//source = "";
			try {
				/*
				videoDataList = DataHelpter.getVideoList();
				if(videoDataList!=null){
					totalPage = DataHelpter.computeTotalPage(videoDataList.size(),PAGE_SIZE);
					return videoDataList;
				}
				
				if(videoDataList ==null){
					//DataHelpter.printCurTime("recommend request start");
					//DataHelpter.printLog(RecommendVideoActivity.this,"recommend video :recommend request start");
					videoList = (List<Map<String, Object>>) libCloud.Get_recommend_list(
						Params.RECOMMEND_VIDEO, source,"").get("recommend");
					//DataHelpter.printCurTime("recommend request finished ");
					//DataHelpter.printLog(RecommendVideoActivity.this,"recommend video :recommend request finished");
					
				}
				*/
				String reqtime = "";
				Map<String,Object> map =  DataHelpter.getVideoList(source);
				if(map!=null){
					videoDataList = (List<DataInfo>) map.get("recommend");
				}
				if (map==null || videoDataList == null) {
					Map<String ,Object> map1 = libCloud.Get_recommend_list(Params.RECOMMEND_VIDEO, source,"");
					if(map1!=null){
						String code = (String) map1.get("code");
						if(code.equals("1")){
							videoList = (List<Map<String, Object>>) map1.get("recommend");
							reqtime = (String) map1.get("reqtime");
						}
						Map<String, Object> m = DataHelpter.fillData(videoList,
								downloadList, params);
						
						if(m!=null){
							DataHelpter.setVideoList((List<DataInfo>)m.get("result"),source,reqtime);
						}
					}	
				}else{
					reqtime = (String) map.get("reqtime");
					Map<String ,Object> map1 = libCloud.Get_recommend_list(Params.RECOMMEND_MUSIC, source,reqtime);
					List<Map<String, Object>> updateList = null ;
					if(map1!=null){
						String code = (String) map1.get("code");
						if(code.equals("1")){
							videoList = (List<Map<String, Object>>) map1.get("recommend");
							reqtime = (String) map1.get("reqtime");
						}
						Map<String, Object> m = DataHelpter.fillData(videoList,
								downloadList, params);
						if(m!=null){
							DataHelpter.updateVideoList((List<DataInfo>)m.get("result"), source, reqtime);
						}
					}
				}
				System.out.println("sourc = " + source);
				
				Map<String,Object> ret =  DataHelpter.getVideoList(source);
				if(ret!=null){
					videoDataList = (List<DataInfo>) ret.get("recommend");
				}else{
					totalPage = 0;
					return  new ArrayList<DataInfo>();
				}
				if(videoDataList!=null){
					totalPage = DataHelpter.computeTotalPage(videoDataList.size(),PAGE_SIZE);
					return videoDataList;
				}else{
					totalPage = 0;
					return  new ArrayList<DataInfo>();
				}
				
				
			} catch (WeiboException e) {
				e.printStackTrace();
			}			
			return  new ArrayList<DataInfo>();
		}

		@Override
		protected void onPostExecute(List<DataInfo> result) {
			if(index == currentIndex){
				if(result.size()==0){
					currentPageNum.setText("1");
					totalPageNum.setText("1");
				}else{
					currentPageNum.setText(currentPage+"");
					totalPageNum.setText(totalPage+"");
				}
				
				addDataToGridView(result);
			}
			loadingView.setVisibility(View.GONE);
		}

	}
	


	class DownLoadAsync extends AsyncTask<Object, Integer, String>{
		ImageView iv;
		ProgressBar pb;
		@Override
		protected String doInBackground(Object... params) {
			DataInfo di = (DataInfo) params[0];
			iv = (ImageView) params[1];
			pb = (ProgressBar) params[2];
			String code = "0";
			try {
				Map m = libCloud.Mydownload_add(Params.RECOMMEND_VIDEO, di.getId());
				code = (String) m.get("code");
				if(code.equals("1")){
					publishProgress(100);
					di.setStatus(1);
					di.setResid(di.getId());
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return code;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("1")) {
				iv.setImageResource(R.drawable.downloaded);
				pb.setVisibility(View.INVISIBLE);
				UIHelper.displayToast(getResources().getString(R.string.download_finished_text), RecommendVideoActivity.this);
			} else {
				iv.setClickable(true);
				pb.setVisibility(View.INVISIBLE);
				UIHelper.displayToast(getResources().getString(R.string.download_failed_text), RecommendVideoActivity.this);
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			pb.setProgress(values[0]);
		}
	}
	class GetMovieTypeAsync extends AsyncTask<Object, Integer, List<Map<String, Object>> >{
		@Override
		protected List<Map<String, Object>>  doInBackground(Object... params) {
			Map<String, Object> retmap=null;
			List<Map<String, Object>> datatype = null;
			try {
				retmap = libCloud.Get_datatype(Params.RECOMMEND_VIDEO);
				if(retmap!=null){
					if(retmap.get("code").equals("1")){
						datatype = (List<Map<String, Object>>) retmap.get("datatype");
						
					}
				}
			}
			catch (WeiboException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return datatype;
		}

		@Override
		protected void onPostExecute( List<Map<String, Object>> result) {
			DataHelpter.setVideoTags(result);
			initTagView(result);
			recommendStart();
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
					break;
			}	
			super.handleMessage(msg);
		}	
	};
}