package com.neteast.clouddisk.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import android.widget.FrameLayout;
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
import com.neteast.clouddisk.activity.RecommendMusicActivity.GetDataTask;
import com.neteast.clouddisk.activity.RecommendMusicActivity.GetMusicTypeAsync;
import com.neteast.clouddisk.adapter.PictureDataAdapter;
import com.neteast.clouddisk.adapter.VideoDataAdapter;
import com.neteast.clouddisk.adapter.ViewPageAdapter;
import com.neteast.clouddisk.customerview.CustomerGridView;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.service.MusicUtils;
import com.neteast.clouddisk.utils.DataHelpter;
import com.neteast.clouddisk.utils.DownLoadApplication;
import com.neteast.clouddisk.utils.ImageDownloader2;
import com.neteast.clouddisk.utils.UIHelper;
import com.neteast.data_acquisition.DataAcqusition;

public class RecommendPictureActivity extends ActivityGroup {
	private String[] sourceSite = new String[]{ "全部", "电影", "电视剧", "综艺", "动漫", "新闻","体育" };
	private LinearLayout sourceSiteLayout;
	private LinearLayout searchResultView;
	private GridView gridView;
	private LinearLayout loadingView = null;

	private int currentPage = 1;
	private int totalPage = 1;
	
	private int currentIndex = 0;

	private TextView currentPageNum = null;
	private TextView totalPageNum = null;
	private TextView currentTab;
	private LibCloud libCloud;
	private int flingDirection = 1;
	PopupWindow pw = null;

	List<DataInfo> downloadList = null;
	List<Map<String,Object>>  pictureList = null;
	/** ViewPager控件，横屏滑动*/
	private ViewPager filePager;
	
	/** ViewPager的视图列表*/
	private List<View> mListViews;
	
	/** ViewPager的适配器. */
	private ViewPageAdapter viewPageAdapter;
	private static final int PAGE_SIZE = Params.RECOMMEND_PICTRUE_PER_PAGE_NUM;
	List<DataInfo> picDataList =null;
	
	private ImageDownloader2 mImageDownloader2;
	
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
		libCloud  = LibCloud.getInstance(this);
		mImageDownloader2 = ((DownLoadApplication) getApplication()).getImageDownloader();
		setContentView(R.layout.recommendpicture);
		
		Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY);
		currentPageNum = (TextView) findViewById(R.id.curentpage);
		totalPageNum = (TextView) findViewById(R.id.totalpage);
		gridView = (CustomerGridView) findViewById(R.id.mygridview);
		filePager = (ViewPager) findViewById(R.id.recommendPictureViewPager);
		loadingView = (LinearLayout)findViewById(R.id.recommand_pic_loading);
		sourceSiteLayout = (LinearLayout) findViewById(R.id.picturesourcesite);
		searchResultView = (LinearLayout) findViewById(R.id.searchresultView);
		Button returnbtn = (Button) findViewById(R.id.searchreturn);
		returnbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sourceSiteLayout.setVisibility(View.VISIBLE);
				searchResultView.setVisibility(View.GONE);
				recommendStart();
			}
		});
		TextView searchresulttv = (TextView) findViewById(R.id.searchresultText);
		
		//GetDataTask gdt = new GetDataTask();
		//gdt.execute(currentPage, currentTab,PAGE_SIZE);
		
		int searchflag = this.getIntent().getIntExtra("searchflag",-1);
		System.out.println("recommendMusicActivity start searchflag = " + searchflag);
		
		if(searchflag >=0){
			searchResultView.setVisibility(View.VISIBLE);
			sourceSiteLayout.setVisibility(View.INVISIBLE);
			loadingView.setVisibility(View.GONE);
			currentIndex = searchflag;
			List<Map<String, Object>> musicTags = DataHelpter.getPictureTags();
			if(musicTags!=null && musicTags.size()>0){
				initTagView(musicTags);
			}
			Intent intent = this.getIntent();
			List<DataInfo> list = (List<DataInfo>) intent.getSerializableExtra("result");
			if(list!=null && list.size()>0){
				addDataToGridView(list);
				//DataHelpter.setSearchList(list);
			}else{
				//List<DataInfo> searchlist = DataHelpter.getSearchList();
				//addDataToGridView(searchlist);
			}
			String value = String.format(getResources().getString(R.string.search_result_text), list.size());
			searchresulttv.setText(value);
			currentPageNum.setText(currentPage+"");
			totalPageNum.setText(totalPage+"");
		}else{
			searchResultView.setVisibility(View.GONE);
			sourceSiteLayout.setVisibility(View.VISIBLE);
			List<Map<String, Object>> pictureTags = DataHelpter.getPictureTags();
			if(pictureTags!=null && pictureTags.size()>0){
				initTagView(pictureTags);
				recommendStart();
			}else{
				GetPictureTypeAsync picturetype = new GetPictureTypeAsync();
				picturetype.execute();
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
	
	private void initTagView(List<Map<String, Object>> datatype){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120,
				LinearLayout.LayoutParams.WRAP_CONTENT);
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
					if(currentTab != v){
						currentTab = (TextView) v;
						
						UIHelper.titleStyleDependState(sourceSiteLayout,
								RecommendPictureActivity.this, currentTab);
						GetDataTask gdt = new GetDataTask();
						currentPage = 1;
						totalPage = 1;
						gdt.execute(currentPage,currentTab,PAGE_SIZE);
					}
				}});
				System.out.println("add music type = " + (CharSequence) datatype.get(i).get("name"));
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
		for (int i = 0; i < PageCount; i++) {
			gridView = (GridView) LayoutInflater.from(context).inflate(
					R.layout.recommend_grid, null);
			gridView.setAdapter(new PictureDataAdapter(context, list,i));
			gridView.setOnItemClickListener(mOnItemClick); 
			gridView.setNumColumns(7);
			gridView.setVerticalSpacing(20);
			gridView.setHorizontalSpacing(10);	
			// 去掉点击时的黄色背景
			//gridView.setSelector(R.color.transparent);
			mListViews.add(gridView);
		}
	 }
	OnItemClickListener mOnItemClick = new OnItemClickListener() {   
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {   
			System.out.println("RecommendVideoActivity on Item click arg2 :" + arg2);				
			PictureDataAdapter adapter1 = (PictureDataAdapter) ((GridView) mListViews.get(currentPage -1)).getAdapter();
			DataInfo info = (DataInfo) adapter1.getItem(arg2);
			DataAcqusition.clickDetail(info.getType(), info.getResid());
			System.out.println("RecommendVideoActivity on item clicked" + "name = " + info.getName() + "url = " + info.getUrl() );	
			
			if(info.getUrl()==null || info.getUrl().length()==0){
				UIHelper.displayToast(getResources().getString(R.string.invalid_url), RecommendPictureActivity.this);
			}else {
				int index = (currentPage-1)*PAGE_SIZE + info.getPosition();
				Intent it = new Intent(); 
		    	it.setClass(RecommendPictureActivity.this,ImageSwitcher1.class);
		    	List<DataInfo> imagesList = null;
		    	if(info.GetIsDir()!=null && info.GetIsDir().equals("1")){
		    		List<String> imagesurl = info.getImages();
		    		List<String> downloads = info.getImagesDownload();
		    		imagesList = DataHelpter.getImagesList(imagesurl, info.getName(),info.getId(),downloads);
		    	}else{
		    		imagesList = new ArrayList<DataInfo>();
		    		imagesList.add(info);
		    	}
		    	it.putExtra("result", (Serializable)imagesList);
		    	it.putExtra("position", 0);
		    	it.putExtra("displayType", 0);
			    startActivity (it);
			
			}

		}      
	};
	/*
	 * 点击下载按钮
	 */
	public void onClickDownloadBtn(View v){
		ImageView iv = (ImageView) v;
		iv.setClickable(false);
		LinearLayout parent = (LinearLayout) v.getParent().getParent();
		ProgressBar pb = (ProgressBar) parent.findViewById(R.id.downloadprogress);
		pb.setVisibility(View.VISIBLE);
		DataInfo di = (DataInfo) v.getTag();
		DownLoadAsync async = new DownLoadAsync();
		async.execute(new Object[]{di,iv,pb});
		UIHelper.displayToast(getResources().getString(R.string.download_start_text), RecommendPictureActivity.this);
	}


	public void addDataToGridView(final List<DataInfo> result) {
		mListViews = new ArrayList<View>();
		setGrid(this, result);
		viewPageAdapter = new ViewPageAdapter(this,mListViews,Params.RECOMMEND_PICTURE);
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
		
		DataHelpter.printLog(RecommendPictureActivity.this,"recommend picture :finished");
	}
	
	private void displayImageView(int page){
		GridView v = (GridView) mListViews.get(page);
		PictureDataAdapter adapter1 = (PictureDataAdapter) v.getAdapter();
		for(int i=0;i<v.getCount();i++){
			View childv = v.getChildAt(i);
			if(childv!=null){
				DataInfo info = (DataInfo) adapter1.getItem(i);

				FrameLayout imageLayout = (FrameLayout) childv.findViewById(R.id.imageLayout);
				FrameLayout imagesLayout = (FrameLayout) childv.findViewById(R.id.imagesLayout);
				TextView imagesnum = (TextView) childv.findViewById(R.id.imagesnumText);
				ImageView imageView ;
				if(info.GetIsDir()!=null && info.GetIsDir().equals("1")){
					imagesLayout.setVisibility(View.VISIBLE);
					imageLayout.setVisibility(View.GONE);
					imageView = (ImageView) childv.findViewById(R.id.imageView1);
					List<String> images = info.getImages();
					imagesnum.setText(images.size()+"");
				}else{
					imageLayout.setVisibility(View.VISIBLE);
					imagesLayout.setVisibility(View.GONE);
					imageView = (ImageView) childv.findViewById(R.id.imageView);
				}
				String thumb = info.getThumb();
				if(thumb!=null&&thumb.length()>0 ){
					//libCloud.DisplayImage(thumb, imageView);
					mImageDownloader2.download(thumb, imageView);
				}
			}
		}
	}
	
	private void clearImageView(int page){
		GridView v = (GridView) mListViews.get(page);
		PictureDataAdapter adapter1 = (PictureDataAdapter) v.getAdapter();
		for(int i=0;i<v.getCount();i++){
			View childv = v.getChildAt(i);
			
			DataInfo info = (DataInfo) adapter1.getItem(i);
			if(childv!=null){
				FrameLayout imageLayout = (FrameLayout) childv.findViewById(R.id.imageLayout);
				FrameLayout imagesLayout = (FrameLayout) childv.findViewById(R.id.imagesLayout);
				TextView imagesnum = (TextView) childv.findViewById(R.id.imagesnumText);
				ImageView imageView ;
				if(info.GetIsDir()!=null && info.GetIsDir().equals("1")){
					imagesLayout.setVisibility(View.VISIBLE);
					imageLayout.setVisibility(View.GONE);
					imageView = (ImageView) childv.findViewById(R.id.imageView1);
					List<String> images = info.getImages();
					imagesnum.setText(images.size()+"");
				}else{
					imageLayout.setVisibility(View.VISIBLE);
					imagesLayout.setVisibility(View.GONE);
					imageView = (ImageView) childv.findViewById(R.id.imageView);
				}
				if(imageView!=null){
					imageView.setImageDrawable(null);
				}
				String thumb = info.getThumb();
				if(thumb!=null&&thumb.length()>0 ){
					libCloud.ClearImageCache(thumb);
					//mImageDownloader2.download(thumb, imageView);
				}
			}
		}
	}

	public void showPicutrueDetail(View view) {
		if (pw != null) {
			pw.dismiss();
			pw = null;
		}
		LinearLayout layout = (LinearLayout) this.getLayoutInflater()
				.from(this).inflate(R.layout.popdetailslayout, null);
		View v = ((LinearLayout) ((LinearLayout) view.getParent()).getParent())
				.getChildAt(0);
		pw = new PopupWindow(layout, 300, 200);
		// pw.setFocusable(true);
		pw.setOutsideTouchable(true);
		// pw.showAsDropDown(, 0, 0);
		pw.showAtLocation(v, Gravity.BOTTOM|Gravity.LEFT, 0, -200);
		pw.update(v, 0, -200, 300, 200);
		TextView tv = (TextView) layout.findViewById(R.id.details);
		Object tag = v.getTag();
		if (tag != null) {
			tv.setText(tag.toString());
		}

		ImageButton ib = (ImageButton) layout.findViewById(R.id.popclose);
		ib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pw.dismiss();
			}
		});
	}
	class GetDataTask extends AsyncTask<Object, Integer, List<DataInfo>>{
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
			try {
				String source;
				TextView tab = (TextView) params[1];
				source = (String) tab.getText();
				index = ((Integer)tab.getTag()).intValue();
				//source ="";
				
				/*
				picDataList = DataHelpter.getPicList();
				if(picDataList!=null){
					totalPage = DataHelpter.computeTotalPage(picDataList.size(),PAGE_SIZE);
					 return picDataList;
				}
				
				System.out.println("get recommend picture : source = " + source);
				if(picDataList==null){
					//DataHelpter.printLog(RecommendPictureActivity.this,"recommend picture :recommend request start");
					pictureList = (List<Map<String,Object>>)libCloud.Get_recommend_list(Params.RECOMMEND_PICTURE, source,"").get("recommend");
					//DataHelpter.printLog(RecommendPictureActivity.this,"recommend picture :recommend request end");
				}
				*/
				String reqtime = "";
				System.out.println(" source = " + source);
				Map<String,Object> map =  DataHelpter.getPicList(source);
				if(map!=null){
					picDataList = (List<DataInfo>) map.get("recommend");
				}
				System.out.println(" map = " + map + "picDataList =" + picDataList);
				if (map==null || picDataList == null) {
					Map<String ,Object> map1 = libCloud.Get_recommend_list(Params.RECOMMEND_PICTURE, source,"");
					if(map1!=null){
						String code = (String) map1.get("code");
						if(code.equals("1")){
							pictureList = (List<Map<String, Object>>) map1.get("recommend");
							reqtime = (String) map1.get("reqtime");
						}
						Map<String, Object> m = DataHelpter.fillData(pictureList,
								downloadList, params);
						
						if(m!=null){
							DataHelpter.setPicList((List<DataInfo>)m.get("result"),source,reqtime);
						}
					}	
				}else{
					reqtime = (String) map.get("reqtime");
					System.out.println("reqtime = " + reqtime);
					Map<String ,Object> map1 = libCloud.Get_recommend_list(Params.RECOMMEND_PICTURE, source,reqtime);
					List<Map<String, Object>> updateList = null ;
					if(map1!=null){
						String code = (String) map1.get("code");
						if(code.equals("1")){
							pictureList = (List<Map<String, Object>>) map1.get("recommend");
							reqtime = (String) map1.get("reqtime");
						}
						Map<String, Object> m = DataHelpter.fillData(pictureList,
								downloadList, params);
						if(m!=null){
							DataHelpter.updatePicList((List<DataInfo>)m.get("result"), source, reqtime);
						}
					}
				}
				System.out.println("sourc = " + source);
				
				Map<String,Object> ret =  DataHelpter.getPicList(source);
				if(ret!=null){
					picDataList = (List<DataInfo>) ret.get("recommend");
				}else{
					totalPage = 0;
					return  new ArrayList<DataInfo>();
				}
				if(picDataList!=null){
					totalPage = DataHelpter.computeTotalPage(picDataList.size(),PAGE_SIZE);
					return picDataList;
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
				if(result !=null ){
					addDataToGridView(result);
					currentPageNum.setText(currentPage+"");
					totalPageNum.setText(totalPage+"");
				}else{
					currentPageNum.setText("1");
					totalPageNum.setText("1");
				}
			}
			loadingView.setVisibility(View.GONE);
		}

	}

	class DownLoadAsync extends AsyncTask<Object, Integer, String>{
		ImageView iv;
		ProgressBar pb;
		ImageView cover;
		@Override
		protected String doInBackground(Object... params) {
			DataInfo di = (DataInfo) params[0];
			iv = (ImageView) params[1];
			pb = (ProgressBar) params[2];
			String code = "0";
			try {
				Map<String,Object> m = libCloud.Mydownload_add(Params.RECOMMEND_PICTURE,
						di.getId());
				code = (String) m.get("code");
				if(code.equals("1")){
					publishProgress(100);
					di.setStatus(1);
					di.setResid(di.getId());
					//downloadList.add(di);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return code;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("1")) {
				cover.setVisibility(View.INVISIBLE);
				iv.setImageResource(R.drawable.downloaded);
				pb.setVisibility(View.INVISIBLE);
				UIHelper.displayToast(getResources().getString(R.string.download_finished_text), RecommendPictureActivity.this);
			} else {
				iv.setClickable(true);
				cover.setVisibility(View.INVISIBLE);
				pb.setVisibility(View.INVISIBLE);
				UIHelper.displayToast(getResources().getString(R.string.download_failed_text), RecommendPictureActivity.this);
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			pb.setProgress(values[0]);
		}
		
	}
	
	@Override
	public void onDestroy()
	{
		mImageDownloader2.clearCache();
		//libCloud.ClearMemoryCache();
		super.onDestroy();
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
	
	class GetPictureTypeAsync extends AsyncTask<Object, Integer, List<Map<String, Object>> >{
		@Override
		protected List<Map<String, Object>>  doInBackground(Object... params) {
			Map<String, Object> retmap=null;
			List<Map<String, Object>> datatype = null;
			try {
				retmap = libCloud.Get_datatype(Params.RECOMMEND_PICTURE);
				if(retmap!=null){
					if(retmap.get("code").equals("1")){
						datatype = (List<Map<String, Object>>) retmap.get("datatype");
						System.out.println("music datatype = " + datatype);
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
			DataHelpter.setPictureTags(result);
			initTagView(result);
			recommendStart();
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
