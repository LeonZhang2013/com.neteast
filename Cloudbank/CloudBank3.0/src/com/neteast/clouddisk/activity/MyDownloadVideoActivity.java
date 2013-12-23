package com.neteast.clouddisk.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.lib.net.WeiboException;

import com.neteast.clouddisk.R;
import com.neteast.clouddisk.activity.FolderDrop.FloderDropListener;
import com.neteast.clouddisk.adapter.MyDownloadSeries0DataAdapter;
import com.neteast.clouddisk.adapter.MyDownloadSeriesDataAdapter;
import com.neteast.clouddisk.adapter.MyDownloadSourceDataAdapter;
import com.neteast.clouddisk.adapter.MyDownloadVideoDataAdapter;
import com.neteast.clouddisk.adapter.TransDataAdapter;
import com.neteast.clouddisk.adapter.VideoDataAdapter;
import com.neteast.clouddisk.adapter.ViewPageAdapter;
import com.neteast.clouddisk.customerview.CustomerGridView;
import com.neteast.clouddisk.customerview.DragLayer;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.utils.DataHelpter;
import com.neteast.clouddisk.utils.DownLoadApplication;
import com.neteast.clouddisk.utils.ImageDownloader2;
import com.neteast.clouddisk.utils.MediaPlayerHelper;
import com.neteast.clouddisk.utils.OnDropCallBack;
import com.neteast.clouddisk.utils.UIHelper;


public class MyDownloadVideoActivity extends ActivityGroup implements FloderDropListener {
	
	private CustomerGridView gridView;
	private CustomerGridView transgridView;
	private GridView seriesGridView;
	private GridView seriesGridView0;
	private LinearLayout loadingView = null;
	
	private MyDownloadVideoDataAdapter adapter;
	private MyDownloadSeriesDataAdapter seriesAdapter;
	private MyDownloadSeries0DataAdapter seriesAdapter0;
	private int parentCurrentPage = 1;
	private int currentPage = 1;
	private int totalPage = 1;
	private int seriesCurrentPage = 1;
	private int seriesTotalPage = 1;
	private TextView currentPageNum = null;
	private TextView totalPageNum = null;
	private TextView currentTab;
	//private FrameLayout seriesView;
	private View seriesView;
	private View zongyiView;
	private LinearLayout seriesgridLayout = null;
	private PopupWindow seriesPopWin=null;
	private PopupWindow zongyiPopWin=null;
	private PopupWindow curEncryptpopw=null;
	private PopupWindow curinputpopw=null;
	private LinearLayout sereisSelView1=null;
	private LinearLayout sereisSelView2=null;
	private LinearLayout sereisSelView3=null;
	private LinearLayout sereisSelView4=null;
	private LinearLayout sereisSelView5=null;
	private LinearLayout sereisSelView6=null;
	
	private ImageView sereisSelImage1=null;
	private ImageView sereisSelImage2=null;
	private ImageView sereisSelImage3=null;
	private ImageView sereisSelImage4=null;
	private ImageView sereisSelImage5=null;
	private ImageView sereisSelImage6=null;
	List<DataInfo> selectList = new ArrayList<DataInfo>();
	private TextView sereisSelText1=null;
	private TextView sereisSelText2=null;
	private TextView sereisSelText3=null;
	private TextView sereisSelText4=null;
	private TextView sereisSelText5=null;
	private TextView sereisSelText6=null;
	private TextView seriesPageText = null;
	int buttontype = 1; /*1 高清观看�? 在线观看�?离线下载*/
	private int mPlayType = 2; /*1：在线观看，2：高清观看*/
	//DataInfo selectDataInfo;
	private LibCloud libCloud;
	private DragLayer mDragLayer;
	int type =0;
	private PopupWindow curpopw=null;
	private PopupWindow curTranscodepopw = null;
	
	TextView totalText1 =null;
	TextView usedText1 =null;
	TextView nousedText1 = null;
	
	
	private List<String> sourceTag= new ArrayList<String>();
	private MyDownloadSourceDataAdapter sourceadapter;
	
	int flingDirection = 1;
	List<DataInfo> inFolderList = new ArrayList<DataInfo>();
	private List<Map<String, Object>> videoList = null;
	
	private List<DataInfo> onlinePlayList = null;
	private List<DataInfo> hdseriesList = null;
	private List<DataInfo> offlineDownloadList = new ArrayList<DataInfo>();
	private List<DataInfo> selonlinePlayList = new ArrayList<DataInfo>();
	private int MovieType = 1;
	
	private ImageButton sourceselbtn = null ;
	private LinearLayout sourcetagView = null;
	private Button returnbutton;
	String path=""; 
	private Button editbutton;
	private Button cancelbutton;
	boolean dragcondition = false;
	
	/** ViewPager控件，横屏滑动*/
	private ViewPager filePager;
	
	/** ViewPager的视图列表*/
	private List<View> mListViews;
	
	/** ViewPager的适配器. */
	private ViewPageAdapter viewPageAdapter;
	private static final int PAGE_SIZE = Params.MYUPLOAD_DATA_PER_PAGE_NUM;
	private FolderDrop mFolderDrop;
	
	/** ViewPager控件，横屏滑动*/
	private ViewPager seriesPager;
	
	/** ViewPager的视图列表*/
	private List<View> mSeriesListViews;
	
	/** ViewPager的适配器. */
	private ViewPageAdapter seriesviewPageAdapter;
	private static final int SERIES_PAGE_SIZE = 30;
	private static final int VARITY_PAGE_SIZE = 8;
	
	/** ViewPager控件，横屏滑动*/
	private ViewPager transPager;
	
	/** ViewPager的视图列表*/
	private List<View> transListViews;
	
	/** ViewPager的适配器. */
	private ViewPageAdapter transPageAdapter;
	private static final int TRANS_PAGE_SIZE = Params.TRANSCODE_PER_PAGE_NUM;
	
	private String[] transType = new String[]{ "640", "1280", "1920", "1024"};
	
	private ImageDownloader2 mImageDownloader2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mydownloadvideo);
		libCloud = LibCloud.getInstance(this);
		mImageDownloader2 = ((DownLoadApplication) getApplication()).getImageDownloader();
		mDragLayer = (DragLayer)findViewById(R.id.mydownloadvideo);
		currentPageNum = (TextView) findViewById(R.id.curentpage);
		totalPageNum = (TextView) findViewById(R.id.totalpage);
		//gridView = (CustomerGridView) findViewById(R.id.mygridview);
		loadingView = (LinearLayout)findViewById(R.id.loading);
		
		totalText1 = (TextView) findViewById(R.id.totalText1);
		usedText1 = (TextView) findViewById(R.id.usedText1);
		nousedText1 = (TextView) findViewById(R.id.nousedText1);
		
		returnbutton = (Button) this.findViewById(R.id.returnbutton);
		returnbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				path="";
				currentPage = parentCurrentPage;
				changeForder(path);
			}
		});
		editbutton = (Button) this.findViewById(R.id.mydownload_edit);
		cancelbutton = (Button) this.findViewById(R.id.mydownload_cancel);
		editbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dragcondition = true;
				cancelbutton.setVisibility(View.VISIBLE);
				editbutton.setVisibility(View.GONE);
			}
		});
		cancelbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dragcondition = false;
				cancelbutton.setVisibility(View.GONE);
				editbutton.setVisibility(View.VISIBLE);
			}
		});
		mFolderDrop = (FolderDrop)findViewById(R.id.folder_drop);
		mFolderDrop.setFloderDropListener(this);
		mFolderDrop.setVisibility(View.GONE);
		
		initSeriesPopWin();
		
		filePager = (ViewPager) findViewById(R.id.mydownloadVideoViewPager);
		
		GetCapacityAsync capacity = new GetCapacityAsync();
		capacity.execute();
		
		GetDataTask gdt = new GetDataTask();
		gdt.execute(currentPage,PAGE_SIZE);
		
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mImageDownloader2.clearCache();
		System.out.println("MyDownloadVideoActivity on Destroy !!!!!!!\n");
	}
	private void setGrid(Context context, List<DataInfo> list) {
		int PageCount = (int) Math.ceil(((double)list.size())/PAGE_SIZE);
		System.out.println("setGrid  PageCount = " + PageCount);
		for (int i = 0; i < PageCount; i++) {
			gridView = (CustomerGridView) LayoutInflater.from(context).inflate(
					R.layout.mydownload_grid, null);
			gridView.setAdapter(new MyDownloadVideoDataAdapter(context, list,i,mLongClickListener,mClickListener));
			gridView.setVerticalSpacing(35);
			gridView.setDataList(list);
			gridView.setDragger(mDragLayer);
			gridView.setOnDropListener(new  OnDropCallBack(){   
				public  void  onDropCompleted(DataInfo sourceInfo,DataInfo destInfo) {              
					//System.out.println("sourceInfo : name = " + sourceInfo.getName() + "destInfo name=" + destInfo.getName()); 
					mFolderDrop.setVisibility(View.GONE);			
					if (destInfo == null || sourceInfo == null) {
						return ;
					}
					if(destInfo==sourceInfo) return ;

					Map<String, Object> retmap=null;
					try {
						if(sourceInfo.GetIsDir()!=null && sourceInfo.GetIsDir().equals("1")){
							return ;
						}
						if(destInfo.GetIsDir()!=null&&destInfo.GetIsDir().equals("1")){
							//retmap = libCloud.Mydownload_merge(Params.RECOMMEND_VIDEO, destInfo.GetPkgName(), "", sourceInfo.getResid());
							retmap = libCloud.Mydownload_merge(Params.RECOMMEND_VIDEO, destInfo.GetPkgName(), "", sourceInfo.getId());
						}else{
							//retmap = libCloud.Mydownload_merge(Params.RECOMMEND_VIDEO, "", destInfo.getResid(), sourceInfo.getResid());
							retmap = libCloud.Mydownload_merge(Params.RECOMMEND_VIDEO, "", destInfo.getId(), sourceInfo.getId());
						}
						if(retmap!=null && retmap.get("code").equals("1")){
							/*
							MyDownloadVideoDataAdapter adapter1 = (MyDownloadVideoDataAdapter) ((CustomerGridView) mListViews.get(currentPage -1)).getAdapter();
							adapter1.getList().remove(sourceInfo.getPosition());
							adapter1.notifyDataSetChanged();
							*/
							
							if(videoList!=null){
								videoList.clear();
							}
							videoList = null;
							GetDataTask gdt = new GetDataTask();
							gdt.execute(currentPage,PAGE_SIZE);
							
						}
					} catch (WeiboException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}   
			}); 
				
			// 去掉点击时的黄色背景
			//gridView.setSelector(R.color.transparent);
			mListViews.add(gridView);
		}
	}
	private void displayImageView(int page){
		GridView v = (GridView) mListViews.get(page);
		MyDownloadVideoDataAdapter adapter1 = (MyDownloadVideoDataAdapter) v.getAdapter();
		for(int i=0;i<v.getCount();i++){
			View childv = v.getChildAt(i);	
			DataInfo info = (DataInfo) adapter1.getItem(i);
			if(childv!=null){
				if (info.GetIsDir() != null && info.GetIsDir().equals("1")) {
					
					LinearLayout mergeView = (LinearLayout) childv.findViewById(R.id.mergeView);
					TableLayout tl = (TableLayout) mergeView.getChildAt(0);
					TableRow tr1 = (TableRow) tl.getChildAt(0);
					TableRow tr2 = (TableRow) tl.getChildAt(1);
					TableRow tr3 = (TableRow) tl.getChildAt(2);
					String images = info.getThumb();
					String[] imageArray = images.split(",");
					for (int j = 0; j < imageArray.length; j++) {
							String smallurl = imageArray[j];
		
							switch (j) {
							case 0:
								mImageDownloader2.download(smallurl, (ImageView)tr1.getChildAt(0));
								//libCloud.DisplayImage(smallurl, (ImageView)tr1.getChildAt(0));
								break;
							case 1:
								mImageDownloader2.download(smallurl, (ImageView)tr1.getChildAt(1));
								//libCloud.DisplayImage(smallurl, (ImageView)tr1.getChildAt(1));
								break;
							case 2:
								mImageDownloader2.download(smallurl, (ImageView)tr1.getChildAt(2));
								//libCloud.DisplayImage(smallurl, (ImageView)tr1.getChildAt(2));
								break;
							case 3:
								mImageDownloader2.download(smallurl, (ImageView)tr2.getChildAt(0));
								//libCloud.DisplayImage(smallurl, (ImageView)tr2.getChildAt(0));
								break;
							case 4:
								mImageDownloader2.download(smallurl, (ImageView)tr2.getChildAt(1));
								//libCloud.DisplayImage(smallurl, (ImageView)tr2.getChildAt(1));
								break;
							case 5:
								mImageDownloader2.download(smallurl, (ImageView)tr2.getChildAt(2));
								//libCloud.DisplayImage(smallurl, (ImageView)tr2.getChildAt(2));
								break;
							case 6:
								mImageDownloader2.download(smallurl, (ImageView)tr3.getChildAt(0));
								//libCloud.DisplayImage(smallurl, (ImageView)tr3.getChildAt(0));
								break;
							case 7:
								mImageDownloader2.download(smallurl, (ImageView)tr3.getChildAt(1));
								//libCloud.DisplayImage(smallurl, (ImageView)tr3.getChildAt(1));
								break;
							case 8:
								mImageDownloader2.download(smallurl, (ImageView)tr3.getChildAt(2));
								//libCloud.DisplayImage(smallurl, (ImageView)tr3.getChildAt(2));
								break;
							}
						}
					
				}else{
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
	}
	
	private View.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			
			//TODO:判断条件，是否是拖拽事件
			System.out.println("MyDownloadVideoActivity on Long Clicked");
			if(dragcondition){
				if(path!=null&&(!path.equals(""))){
					mFolderDrop.setVisibility(View.VISIBLE);
				}
				gridView.startDrag(v);	
			}
			else{
				DataInfo info = (DataInfo) v.getTag();
				if(info.GetIsDir()!=null && info.GetIsDir().equals("1")){
					return true;
				}
				showpopmenu(info,null);
			}
			return true;
		}
	};
	
	private View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			DataInfo info1 = (DataInfo) arg0.getTag();
			System.out.println("MyDownloadVideoActivity on item clicked" + "name = " + info1.getName() + "position = " + info1.getPosition() +  "url = " + info1.getUrl() );	
			
			if(info1.getSecurity()!=null &&info1.getSecurity().equals("1")){
				showInputPsw(info1);
			}else{
				execute(info1);
			}
		}
	};
	private ImageView leftSmallArrow;
	private ImageView rightSmallArrow;
	private ImageView leftBigArrow;
	private ImageView rightBigArrow;
	
	private void setSeriesGrid(Context context, List<DataInfo> list) {
		int pageSize = 30;
		if(list==null) return ;
		if(buttontype == 3){
			pageSize = VARITY_PAGE_SIZE;
		}else{	
			if(MovieType ==1 ||MovieType ==2 || MovieType == 4){
				pageSize = SERIES_PAGE_SIZE;
			}else if(MovieType ==3){
				pageSize = VARITY_PAGE_SIZE;
			}
		}
		seriesCurrentPage = 1;
		seriesTotalPage = (int) Math.ceil(((double)list.size())/pageSize);
		System.out.println("setSeriesGrid  PageCount = " + seriesTotalPage);
		for (int i = 0; i < seriesTotalPage; i++) {
			seriesGridView = (GridView) LayoutInflater.from(context).inflate(
					R.layout.mydownload_series_grid, null);
			seriesGridView.setAdapter(new MyDownloadSeriesDataAdapter(context, list,i,pageSize));
			seriesGridView.setOnItemClickListener(mOnSeriesItemClick); 
			if(pageSize == 30){
				seriesGridView.setNumColumns(10);
			}else if(pageSize == 8){
				seriesGridView.setNumColumns(2);
			}
				
			// 去掉点击时的黄色背景
			//gridView.setSelector(R.color.transparent);
			mSeriesListViews.add(seriesGridView);
		}
	}
	private void initSeriesPopWin(){
		
		if(seriesPopWin!=null){
			seriesPopWin.dismiss();
			seriesPopWin = null;
		}
		
		seriesView = getLayoutInflater().inflate(R.layout.mydownload_series, null);
		seriesPopWin = new PopupWindow(seriesView, 1189, 522, true);
		
		sereisSelView1 = (LinearLayout)seriesView.findViewById(R.id.series_sel1);
		sereisSelView2 = (LinearLayout)seriesView.findViewById(R.id.series_sel2);
		sereisSelView3 = (LinearLayout)seriesView.findViewById(R.id.series_sel3);
		sereisSelView4 = (LinearLayout)seriesView.findViewById(R.id.series_sel4);
		sereisSelView5 = (LinearLayout)seriesView.findViewById(R.id.series_sel5);
		sereisSelView6 = (LinearLayout)seriesView.findViewById(R.id.series_sel6);
		
		leftSmallArrow = (ImageView)seriesView.findViewById(R.id.series_small_left_arrow);
		rightSmallArrow = (ImageView)seriesView.findViewById(R.id.series_small_right_arrow);
		leftBigArrow = (ImageView)seriesView.findViewById(R.id.series_big_left_arrow);
		rightBigArrow = (ImageView)seriesView.findViewById(R.id.series_big_right_arrow);
		leftSmallArrow.setVisibility(View.GONE);
		rightSmallArrow.setVisibility(View.GONE);
		leftBigArrow.setVisibility(View.GONE);
		rightBigArrow.setVisibility(View.GONE);
		sereisSelImage1 = (ImageView)seriesView.findViewById(R.id.imageView1);
		sereisSelImage2 = (ImageView)seriesView.findViewById(R.id.imageView2);
		sereisSelImage3 = (ImageView)seriesView.findViewById(R.id.imageView3);
		sereisSelImage4 = (ImageView)seriesView.findViewById(R.id.imageView4);
		sereisSelImage5 = (ImageView)seriesView.findViewById(R.id.imageView5);
		sereisSelImage6 = (ImageView)seriesView.findViewById(R.id.imageView6);
		sereisSelImage1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(seriesPopWin!=null){
					seriesPopWin.dismiss();
					seriesPopWin = null;
				}
			}
		});
		sereisSelImage2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(seriesPopWin!=null){
					seriesPopWin.dismiss();
					seriesPopWin = null;
				}
			}
		});
		sereisSelImage3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(seriesPopWin!=null){
					seriesPopWin.dismiss();
					seriesPopWin = null;
				}
			}
		});
		sereisSelImage4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(seriesPopWin!=null){
					seriesPopWin.dismiss();
					seriesPopWin = null;
				}
			}
		});
		sereisSelImage5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(seriesPopWin!=null){
					seriesPopWin.dismiss();
					seriesPopWin = null;
				}
			}
		});
		sereisSelImage6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(seriesPopWin!=null){
					seriesPopWin.dismiss();
					seriesPopWin = null;
				}
			}
		});
		
		
		sereisSelText1 = (TextView)seriesView.findViewById(R.id.nameTextView1);
		sereisSelText2 = (TextView)seriesView.findViewById(R.id.nameTextView2);
		sereisSelText3 = (TextView)seriesView.findViewById(R.id.nameTextView3);
		sereisSelText4 = (TextView)seriesView.findViewById(R.id.nameTextView4);
		sereisSelText5 = (TextView)seriesView.findViewById(R.id.nameTextView5);
		sereisSelText6 = (TextView)seriesView.findViewById(R.id.nameTextView6);
		
		seriesPageText = (TextView)seriesView.findViewById(R.id.series_pageinfo);
		
		seriesPager = (ViewPager) seriesView.findViewById(R.id.mydownloadSeriesViewPager);
		
		seriesgridLayout = (LinearLayout)seriesView.findViewById(R.id.seriesgridLayout);
		//seriesgridLayout.setOnTouchListener(l)
		seriesGridView = (CustomerGridView)seriesView.findViewById(R.id.seriesgridview);
		//seriesGridView0 =(CustomerGridView)seriesView.findViewById(R.id.seriesgridview0);
		seriesGridView0=(GridView)seriesView.findViewById(R.id.seriesgriditem);
	
		
		//seriesgridLayout.setLongClickable(true);
		
		seriesPopWin.setFocusable(true);
        seriesPopWin.setTouchable(true);
		seriesPopWin.setOutsideTouchable(true);
		seriesPopWin.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句
		
		final Button hdplaybtn = (Button) seriesView.findViewById(R.id.hdplaybtn);
		final Button onlineplaybtn = (Button) seriesView.findViewById(R.id.onlineplaybtn);
//		final Button offlinedownloadbtn = (Button) seriesView.findViewById(R.id.offlinedownloadbtn);
		sourceselbtn = (ImageButton) seriesView.findViewById(R.id.sourceselbtn);
		sourcetagView = (LinearLayout) seriesView.findViewById(R.id.sourcetagView);
		sourcetagView.setVisibility(View.INVISIBLE);
		sourceselbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sourcetagView.setVisibility(View.VISIBLE);
			}
		});
		hdplaybtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hdplaybtn.setBackgroundResource(R.drawable.detail_hdplaybtn_p);
				onlineplaybtn.setBackgroundResource(R.drawable.detail_onlineplaybtn);
//				offlinedownloadbtn.setBackgroundResource(R.drawable.detail_offlinedownload);
				//sourceselbtn.setVisibility(View.INVISIBLE);
				//sourcetagView.setVisibility(View.INVISIBLE);
				mPlayType = 2;
				buttontype = 1;
				showSeriesPage();
			}
		});
		onlineplaybtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onlineplaybtn.setBackgroundResource(R.drawable.detail_onlineplaybtn_p);
				hdplaybtn.setBackgroundResource(R.drawable.detail_hdplaybtn);
//				offlinedownloadbtn.setBackgroundResource(R.drawable.detail_offlinedownload);
				//sourceselbtn.setVisibility(View.VISIBLE);
				//sourcetagView.setVisibility(View.VISIBLE);
				buttontype = 2;
				mPlayType = 1;
				showSeriesPage();
			}
		});
		
		/** 离线下载按钮监听 */
//		offlinedownloadbtn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				offlinedownloadbtn.setBackgroundResource(R.drawable.detail_offlinedownload_p);
//				onlineplaybtn.setBackgroundResource(R.drawable.detail_onlineplaybtn);
//				hdplaybtn.setBackgroundResource(R.drawable.detail_hdplaybtn);
//				//sourceselbtn.setVisibility(View.INVISIBLE);
//				//sourcetagView.setVisibility(View.INVISIBLE);
//				mPlayType = 3;
//				buttontype = 3;
//				showSeriesPage();
//			}
//		});
		
	}

	private void displaySelectView(DataInfo di) {
		System.out.println("displaySelectView info position = " + di.getPosition());
		int position = di.getPosition()%6;
		sereisSelView1.setVisibility(View.INVISIBLE);
		sereisSelView2.setVisibility(View.INVISIBLE);
		sereisSelView3.setVisibility(View.INVISIBLE);
		sereisSelView4.setVisibility(View.INVISIBLE);
		sereisSelView5.setVisibility(View.INVISIBLE);
		sereisSelView6.setVisibility(View.INVISIBLE);
		switch(position){
		case 0:
			sereisSelView1.setVisibility(View.VISIBLE);
			libCloud.DisplayImage(di.getThumb(), sereisSelImage1);
			sereisSelText1.setText(di.getName());
			break;
		case 1:
			sereisSelView2.setVisibility(View.VISIBLE);
			libCloud.DisplayImage(di.getThumb(), sereisSelImage2);
			sereisSelText2.setText(di.getName());
			break;
		case 2:
			sereisSelView3.setVisibility(View.VISIBLE);
			libCloud.DisplayImage(di.getThumb(), sereisSelImage3);
			sereisSelText3.setText(di.getName());
			break;
		case 3:
			sereisSelView4.setVisibility(View.VISIBLE);
			libCloud.DisplayImage(di.getThumb(), sereisSelImage4);
			sereisSelText4.setText(di.getName());
			break;
		case 4:
			sereisSelView5.setVisibility(View.VISIBLE);
			libCloud.DisplayImage(di.getThumb(), sereisSelImage5);
			sereisSelText5.setText(di.getName());
			break;
		case 5:
			sereisSelView6.setVisibility(View.VISIBLE);
			libCloud.DisplayImage(di.getThumb(), sereisSelImage6);
			sereisSelText6.setText(di.getName());
			break;
		}
	}




	private void showSeriesView(DataInfo di){
		initSeriesPopWin();
		buttontype = 1;
		mPlayType = 2;
		displaySelectView(di);
		//selectDataInfo = di;
		if(seriesPopWin==null){
			System.out.println("sereisPopWin is NUll");
		}
		//seriesPopWin.showAtLocation(pageNum, Gravity.CENTER|Gravity.LEFT, 160, 75);
		seriesPopWin.showAtLocation(findViewById(R.id.mydownloadvideo), Gravity.CENTER|Gravity.LEFT, 160, 58);
		if(onlinePlayList!=null) onlinePlayList.clear();
		if(hdseriesList!=null) hdseriesList.clear();
		if(offlineDownloadList!=null) offlineDownloadList.clear();
		if(selonlinePlayList!=null) selonlinePlayList.clear();
		GetSeriesDataTask gdt = new GetSeriesDataTask();
		gdt.execute(seriesCurrentPage,di);
	}
	private void showSeriesPage(){
		List<DataInfo> tempList = new ArrayList<DataInfo>();
		System.out.println("showSeriesPage buttontype = " + buttontype);
		if(buttontype == 2){
			sourceselbtn.setVisibility(View.VISIBLE);
			//sourcetagView.setVisibility(View.VISIBLE);
			System.out.println("selonlinePlayList size = " + selonlinePlayList.size());
			selectList = selonlinePlayList;
		}
		if(buttontype == 1){
			sourceselbtn.setVisibility(View.INVISIBLE);
			sourcetagView.setVisibility(View.INVISIBLE);
			selectList = hdseriesList;
			//selectList = selonlinePlayList;
		}
		if(buttontype == 3){
			sourceselbtn.setVisibility(View.INVISIBLE);
			sourcetagView.setVisibility(View.INVISIBLE);
			selectList = offlineDownloadList;
		}
		if(selectList==null){
			selectList = new ArrayList<DataInfo>();
		}
		mSeriesListViews = new ArrayList<View>();
		setSeriesGrid(this, selectList);
		
		seriesviewPageAdapter = new ViewPageAdapter(this,mSeriesListViews,2);
		
		seriesPager.setAdapter(seriesviewPageAdapter);
		
		seriesPager.setCurrentItem(0);
		
		System.out.println("seriesTotalPage = " + seriesTotalPage);
		if(seriesTotalPage<=1){
			leftSmallArrow.setVisibility(View.INVISIBLE);
			rightSmallArrow.setVisibility(View.INVISIBLE); 
			leftBigArrow.setVisibility(View.INVISIBLE);
			rightBigArrow.setVisibility(View.INVISIBLE);
		}else{
			leftBigArrow.setVisibility(View.VISIBLE);
			rightBigArrow.setVisibility(View.VISIBLE);
		}
		if(seriesTotalPage ==0){
			seriesPageText.setText("");
		}else{
			seriesPageText.setText(seriesCurrentPage + "/" + seriesTotalPage);
		}
		seriesPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int page) {
				seriesCurrentPage = page + 1;
				seriesPageText.setText(seriesCurrentPage + "/" + seriesTotalPage);
			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			public void onPageScrollStateChanged(int arg0) {}
		});
		
		
		List <String> list = new ArrayList<String>();
		if(MovieType == 2){
			if(selectList==null) return ;
			int size = selectList.size()/30;
			int temp1 = selectList.size()%30;
			if(temp1 > 0) size+=1;
			for(int i=0;i<size;i++){
				String temp = i*30+1 + "-" +(i+1)*30;
				list.add(temp);
			}
			seriesAdapter0 = new MyDownloadSeries0DataAdapter(this,list);
			seriesGridView0.setAdapter(seriesAdapter0);
			seriesGridView0.setOnItemClickListener(mOnSeries0ItemClick); 
		}else if(MovieType == 3){
			int size = selectList.size()/8;
			int temp1 = selectList.size()%8;
			if(temp1 > 0) size+=1;
			for(int i=0;i<size;i++){
				String temp = i*8+1 + "-" +(i+1)*8;
				list.add(temp);
			}
			seriesAdapter0 = new MyDownloadSeries0DataAdapter(this,list);
			seriesGridView0.setAdapter(seriesAdapter0);
			seriesGridView0.setOnItemClickListener(mOnSeries0ItemClick); 
		}
		

	}
	public void addDataToSeriesGridView(final List<DataInfo> result){
		
		initSourceTagList();
		System.out.println("sourceTag size = " + sourceTag.size());
		if(sourceTag.size()>0){
			String tag = sourceTag.get(0);
			initOnlinePlayListWithTag(tag);
		}						
		showSeriesPage();
		
	}
	private void initOnlinePlayListWithTag(String tag){
		selonlinePlayList.clear();
		for(int i= 0;i<onlinePlayList.size();i++ ){
			DataInfo info = onlinePlayList.get(i);
			if(info.GetTag().equals(tag)){
				selonlinePlayList.add(info);
			}
		}
		if(MovieType ==1 || MovieType == 2 || MovieType == 3){
			int size = selonlinePlayList.size();
			int temp1 = size /Params.MYDOWNLOAD_SERIES_PER_PAGE_NUM;
			int temp2 = size % Params.MYDOWNLOAD_SERIES_PER_PAGE_NUM;
			seriesTotalPage = temp2==0?temp1:temp1+1;
		}else if(MovieType == 3){
			int size = selonlinePlayList.size();
			int temp1 = size /Params.MYDOWNLOAD_VARIETY_PER_PAGE_NUM;
			int temp2 = size % Params.MYDOWNLOAD_VARIETY_PER_PAGE_NUM;
			seriesTotalPage = temp2==0?temp1:temp1+1;
		}
	}
	private void initSourceTagList(){
		sourceTag.clear();
		if(onlinePlayList.size()>0){
			for(int i=0;i< onlinePlayList.size();i++){
				DataInfo info = onlinePlayList.get(i);
				if(!exist(info.GetTag())){
					sourceTag.add(info.GetTag());
				}
			}
		}
		if(sourceTag.size()>0){
			setOnlinePlayIco(sourceTag.get(0));
			GridView titlegridv=(GridView)seriesView.findViewById(R.id.sourcegriditem);
			
			//System.out.println("mThumbIds size " + mThumbIds.length);
		     titlegridv.setNumColumns(sourceTag.size());
			 titlegridv.setHorizontalSpacing(10);  
			 titlegridv.setStretchMode(GridView.NO_STRETCH);
			 sourceadapter = new MyDownloadSourceDataAdapter(titlegridv.getContext(),sourceTag);  
			 titlegridv.setAdapter(sourceadapter);  
			
			 titlegridv.setOnItemClickListener(sourceListOnItemClick); 
		}
	}
	AdapterView.OnItemClickListener sourceListOnItemClick = new AdapterView.OnItemClickListener() {   
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {   
        	String tag = sourceTag.get(arg2);
        	System.out.println("item = " + arg2 + "clicked" + "tag = " + tag);
        	setOnlinePlayIco(tag);
        	initOnlinePlayListWithTag(tag);
        	sourcetagView.setVisibility(View.INVISIBLE);
        	showSeriesPage();    	
        }   
           
    };
   
    
	private boolean exist(String tag){
		for(String s:sourceTag){
			if(s.equals(tag)){
				return true;
			}
		}
		return false;
	}
	
	private void setOnlinePlayIco(String tag){
		if(tag==null) return ;
   		if(tag.equals("56")){
   			sourceselbtn.setImageResource(R.drawable.btn56);
   			sourceselbtn.setTag(tag);
		}else if(tag.equals("6")){
			sourceselbtn.setImageResource(R.drawable.btn6);
			sourceselbtn.setTag(tag);
		}else if(tag.equals("hualu5")){
			sourceselbtn.setImageResource(R.drawable.btnhualu5);
			sourceselbtn.setTag(tag);
		}else if(tag.equals("joy")){
			sourceselbtn.setImageResource(R.drawable.btnjoy);
			sourceselbtn.setTag(tag);
		}else if(tag.equals("ku6")){
			sourceselbtn.setImageResource(R.drawable.btnku6);
			sourceselbtn.setTag(tag);
		}else if(tag.equals("letv")){
			sourceselbtn.setImageResource(R.drawable.btnletv);
			sourceselbtn.setTag(tag);
		}else if(tag.equals("m1905")){
			sourceselbtn.setImageResource(R.drawable.btnm1905);
			sourceselbtn.setTag(tag);
		}else if(tag.equals("pptv")){
			sourceselbtn.setImageResource(R.drawable.btnpptv);
			sourceselbtn.setTag(tag);
		}else if(tag.equals("qiyi")){
			sourceselbtn.setImageResource(R.drawable.btnqiyi);
			sourceselbtn.setTag(tag);
		}else if(tag.equals("qq")){
			sourceselbtn.setImageResource(R.drawable.btnqq);
			sourceselbtn.setTag(tag);
		}else if(tag.equals("sina")){
			sourceselbtn.setImageResource(R.drawable.btnsina);
			sourceselbtn.setTag(tag);
		}else if(tag.equals("smgbb")){
			sourceselbtn.setImageResource(R.drawable.btnsmgbb);
			sourceselbtn.setTag(tag);
		}else if(tag.equals("sohu")){
			sourceselbtn.setImageResource(R.drawable.btnsohu);
			sourceselbtn.setTag(tag);
		}else if(tag.equals("tudou")){
			sourceselbtn.setImageResource(R.drawable.btntudou);
			sourceselbtn.setTag(tag);
		}else if(tag.equals("youku")){
			sourceselbtn.setImageResource(R.drawable.btnyouku);
			sourceselbtn.setTag(tag);
		}
	}
	
	
	public void addDataToGridView(final List<DataInfo> result) {
		mListViews = new ArrayList<View>();
		setGrid(this, result);
		viewPageAdapter = new ViewPageAdapter(this,mListViews,Params.MYDOWNLOAD_VIDEO);
		filePager.setAdapter(viewPageAdapter);
		
		filePager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int page) {
				totalPage = DataHelpter.computeTotalPage(result.size(),PAGE_SIZE);
				currentPage = page + 1;
				currentPageNum.setText(currentPage+"");
				totalPageNum.setText(totalPage+"");
				
				Message msg = new Message();
				msg.what = PAGE_CHANGED;
				Bundle b = new Bundle();
				b.putInt("page", page);
				b.putInt("cpage", currentPage);
				msg.setData(b);
				myHandler.sendMessageDelayed(msg, 1000);
			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			public void onPageScrollStateChanged(int arg0) {}
		});
		filePager.setCurrentItem(currentPage-1);
	}	
	OnItemClickListener mOnSeries0ItemClick = new OnItemClickListener() {   
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {   
				System.out.println("mydownloadVideo on Item click arg2 :" + arg2);				
				seriesPager.setCurrentItem(arg2);
		    }      
	};
	
	OnItemClickListener mOnSeriesItemClick = new OnItemClickListener() {   
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {   
			System.out.println("mydownloadVideo on Item click arg2 :" + arg2);	
			MyDownloadSeriesDataAdapter adapter1 = (MyDownloadSeriesDataAdapter) ((GridView) mSeriesListViews.get(seriesCurrentPage -1)).getAdapter();
			DataInfo di = (DataInfo) adapter1.getItem(arg2);
			System.out.println("PopWinClickGestureListener onSingleTapUp url = :" + di.getUrl());
				
			if(mPlayType ==1){/*在线观看*/
					/*
					Intent it = new Intent();  
			    	it.setClass(MyDownloadVideoActivity.this,FlashViewActivity.class);
			    	it.putExtra("url", di.getUrl());
			    	startActivity(it); 
			    	*/
					if(di.getUrl()!=null && di.getUrl().length()>0){
						MediaPlayerHelper.play(MyDownloadVideoActivity.this, di.getUrl(),di.getMovieId(),di.getVideoid(),"2");
						/*
						Intent it = new Intent(); 
						it.setClass(MyDownloadVideoActivity.this,VideoPlaybackActivity.class);
						Uri uri = Uri.parse(di.getUrl());
						it.setData(uri);
						it.putExtra("position", 0);
						startActivity (it);
						*/
					}else{
						UIHelper.displayToast(getResources().getString(R.string.url_invalid), MyDownloadVideoActivity.this);
					}
				
			}else if(mPlayType == 2){/*高清播放*/
				MediaPlayerHelper.play(MyDownloadVideoActivity.this, di.getUrl(),di.getMovieId(),di.getVideoid(),"1");
					/*
					Intent it = new Intent(); 
					it.setClass(MyDownloadVideoActivity.this,VideoPlaybackActivity.class);
					Uri uri = Uri.parse(di.getUrl());
					it.setData(uri);
					it.putExtra("position", 0);
					startActivity (it);
					*/
			}else if(mPlayType == 3){/*离线下载播放*/
				showTrancodeWin(di);	
			}
		}      
	};
	

	class GetDataTask extends AsyncTask<Object, Integer, List<DataInfo>> {
		
		@Override  
	    protected void onPreExecute() {  
			//第一个执行方法
			loadingView.setVisibility(View.VISIBLE);
		    super.onPreExecute();  
		}  
		@Override
		protected List<DataInfo> doInBackground(Object... params) {
			Map<String, Object> retmap=null;
			
			try {
				if(videoList==null){ 
					
					retmap = libCloud.Mydownload_list(Params.RECOMMEND_VIDEO, path);
					
					//retmap = libCloud.Get_search_list(2, 3, "你好");

					System.out.println("mydownloadVideo GetDataTast code :" + retmap.get("code"));
					
					if(!retmap.get("code").equals("1")){
						System.out.println("mydownloadVideo return NULL,code="+retmap.get("code") );
						return null;
					}
					
					videoList = (List<Map<String, Object>>) retmap.get("download");
					//videoList = (List<Map<String, Object>>) retmap.get("filelist");
					if(videoList==null || videoList.size()==0) return null;
					List<String> paths = new ArrayList<String>();
					if("".equals(path)){
						for (Iterator iterator = videoList.iterator(); iterator
								.hasNext();) {
							Map<String, Object> map = (Map<String, Object>) iterator.next();
							if(map.get("path")!=null&&(!"".equals(map.get("path")))){
								StringBuilder imageUrls=new StringBuilder();
								if(!paths.contains(map.get("path"))){
									paths.add((String)map.get("path"));
									/*---------------------------获取四幅图片start*/
									int i = 8;
									for (Iterator iterator2 = videoList.iterator(); iterator2
											.hasNext();) {
										Map<String, Object> map2 = (Map<String, Object>) iterator2.next();
										if(i<0){
											break;
										}
										if(map2.get("path").equals(map.get("path"))){
											imageUrls.append(map2.get("image"));
											imageUrls.append(",");
											i--;
										}
									}
									map.put("image", imageUrls.toString());
									/*---------------------------获取四幅图片end*/
								}else{
									iterator.remove();
								}
								
							}
						}
					}else{
						for (Iterator iterator = videoList.iterator(); iterator
								.hasNext();) {
							Map<String, Object> map = (Map<String, Object>) iterator.next();
							if(map.get("path")!=null&&(!"".equals(map.get("path")))){
								map.put("path", "");
							}
						}
					}
					
				}
				
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			
			Map<String, Object> m = DataHelpter.fillDownloadData(videoList, params);
			if (m == null) {
				return new ArrayList<DataInfo>();
			}
 
			totalPage = (Integer) m.get("totalpage");
			System.out.println("totalPage = " + totalPage);
			return (List<DataInfo>) m.get("result");
			
		}

		@Override
		protected void onPostExecute( List<DataInfo> result ) {
			System.out.println("on Post Execute totalPage = " + totalPage );
			if(result !=null){
			
				addDataToGridView(result);
				
			}else{
				System.out.println("on Post Execute result is null ,totalPage = " + totalPage );
				currentPage = 1;
				totalPage =1;
	
			}
		
			currentPageNum.setText(currentPage+"");
			totalPageNum.setText(totalPage+"");
			loadingView.setVisibility(View.GONE);
		}
	}
	
	class GetSeriesDataTask extends AsyncTask<Object, Integer, List<DataInfo>> {
		
		@Override
		protected List<DataInfo> doInBackground(Object... params) {
			
			Map<String, Object> retmap=null;
			Map<String, Object> retmap1=null;
			Map<String,	Object> retmap2 = null; /*离线下载暂时去掉*/
			List<Map<String, Object>> detail = null;
			
			DataInfo info1  = (DataInfo)params[1];
			DataInfo info = new DataInfo();
			info.setId(info1.getId());
			info.setType(info1.getType());
			info.setResid(info1.getResid());
			info.setName(info1.getName());
			info.setUrl(info1.getUrl());
			info.setThumb(info1.getThumb());
			info.setChildtype(info1.getChildtype());
			info.setSecurity(info1.getSecurity());
			info.setMovieId(info1.getMovieId());
			//System.out.println("mydownloadVideo get Series Data id=" + info.getId() + "resid =" + info.getResid());
			try {
			
				retmap =  (Map<String, Object>) libCloud.get_hdplayInfo(info.getMovieId());
				
				retmap1 = libCloud.Get_movie_detail(info.getMovieId(), "http", 0);
				/* 离线下载暂时去掉*/
				retmap2 = libCloud.Get_task(0,info.getMovieId());
				
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			if(retmap!=null &&  retmap.containsKey("list")){
				//detail = (List<Map<String, Object>>) retmap.get("detail");
				detail = (List<Map<String, Object>>) retmap.get("list");
				hdseriesList = DataHelpter.GetSeriesList(detail);
			}
			if(retmap1!=null){
				Map<String,Object> seriesMap = (Map<String, Object>) retmap1.get("http_list");
				onlinePlayList = DataHelpter.GetOnlineList(seriesMap,MovieType);
				System.out.println("onlinePlayList size = " + onlinePlayList.size());
				
			}
			 /*离线下载暂时去掉*/
			
			if(retmap2!=null){
				List<DataInfo> taskList = DataHelpter.GetOfflineTaskList(retmap2);
				System.out.println("mydownloadVideo taskList size : "+taskList.size());
				offlineDownloadList.clear();
				for(int i=0;i< taskList.size();i++){
					List<Map<String, Object>> map = null ;
					DataInfo info2 = taskList.get(i);
					try {
						map = libCloud.Get_taskres(info2.getInfoHash());
					} catch (WeiboException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(map!=null && map.size()>0){
						DataHelpter.GetOfflineResList(map, offlineDownloadList,info2.getId());
					}
				}
			}
			
			if(buttontype == 1){
				return hdseriesList;
			}else if(buttontype == 2){
				return onlinePlayList;
			}else{
				return offlineDownloadList;
			}
		}

		@Override
		protected void onPostExecute(List<DataInfo> result) {
			//pageNum.setText(seriesCurrentPage + "/" + seriesTotalPage);
			//if(result!=null){
				addDataToSeriesGridView(result);
			//}
		}

	}
	
	class GetCapacityAsync extends AsyncTask<Object, Integer, Map<String, Object> >{
		@Override
		protected Map<String, Object>  doInBackground(Object... params) {
			Map<String, Object> retmap=null;
			try {
				retmap = libCloud.Get_capacity();	
			}
			catch (WeiboException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return retmap;
		}

		@Override
		protected void onPostExecute(Map<String, Object> result) {
			String totalSpace = null;
			String usedSpace = null;
			if(result==null) return ;
			if(result.get("code").equals("1")){
				totalSpace = (String) result.get("totalspace");
				usedSpace = (String) result.get("usedspace");
				if(totalSpace==null){
					totalSpace="0";
				}if(usedSpace==null){
					usedSpace="0";
				}
				String usedGB = "";
				long size = Long.parseLong(usedSpace);
				long total = Long.parseLong(totalSpace);
				System.out.println("size = " + size);
				usedGB = DataHelpter.getSpaceStr(size);
				String leave = DataHelpter.getSpaceStr(total*1024*1024*1024-size);
				totalText1.setText(totalSpace+"GB");
				usedText1.setText(usedGB);
				nousedText1.setText(leave);
			}else if(result.get("code").equals("202")){
				UIHelper.displayToast("userid or token is invalid",MyDownloadVideoActivity.this);
			}
		}
		
	}


	/*
	 * 显示密码输入�?
	 */
	private void showInputPsw(final DataInfo di){
		if(curinputpopw!=null){
			curinputpopw.dismiss();
		}
		View pWin= getLayoutInflater().inflate(R.layout.myuploadinputpswmenu, null);
		final PopupWindow popmenu = new PopupWindow(pWin, 301, 210);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());
		curinputpopw = popmenu;
		Button okbtn = (Button) pWin.findViewById(R.id.bt_ok);
		Button cancelbtn = (Button) pWin.findViewById(R.id.bt_cancel);
		final EditText psw = (EditText) pWin.findViewById(R.id.psw);
		
		okbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				String pswStr = psw.getText().toString();
				if(pswStr.trim().equals("")){
					UIHelper.displayToast(getResources().getString(R.string.pswd_cannot_empty),MyDownloadVideoActivity.this);
				}else{
					//Map<String,Object> result = libCloud.Set_file_password(id,pswStr);
					if(true){
						execute(di);
						popmenu.dismiss();
					}else{
						UIHelper.displayToast(getResources().getString(R.string.pswd_error),MyDownloadVideoActivity.this);
					}
					
				}
			}});
		cancelbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				
			}});
		popmenu.showAtLocation(findViewById(R.id.mydownloadvideo), Gravity.CENTER, 0, 0);
	}
	private void changeForder(String pf){
		currentPageNum.setText("1");
		totalPageNum.setText("1");
		videoList = null;
		
		GetDataTask gdt = new GetDataTask();
		//String str = order.peek();
		path = pf;
		gdt.execute(currentPage,PAGE_SIZE);

		//returnbutton.setClickable(false);
		if(returnbutton.getVisibility()==View.INVISIBLE){
			returnbutton.setVisibility(View.VISIBLE);
		}
		if(path!=null&&(!path.equals(""))){
			returnbutton.setVisibility(View.VISIBLE);
		}else{
			returnbutton.setVisibility(View.GONE);
		}
	}
	/*
	 * 执行具体操作
	 */
	private void execute(DataInfo di){
		if(di.GetIsDir()!=null&&di.GetIsDir().equals("1")){
			parentCurrentPage = currentPage;
			currentPage = 1;
			changeForder(di.GetPkgName());
			return;
		}
		if(di.getChildtype().equals(getResources().getString(R.string.type_movie))){
			MovieType = 1;
		}else if(di.getChildtype().equals(getResources().getString(R.string.type_series))
				|| di.getChildtype().equals(getResources().getString(R.string.type_anima))){
			MovieType = 2;
		}else if(di.getChildtype().equals(getResources().getString(R.string.type_variety))){
			MovieType = 3;
		}else if(di.getChildtype().equals(getResources().getString(R.string.type_sports))
				||di.getChildtype().equals(getResources().getString(R.string.type_news))){
			MovieType = 1;
		}else{	 
		    MovieType = 1;
		}
		
		showSeriesView(di);
		/*
		if(di.getChildtype().equals(getResources().getString(R.string.type_movie))){
			Intent it = new Intent(); 
	    	it.setClass(MyDownloadVideoActivity.this,VideoPlaybackActivity.class);
	    	Uri uri = Uri.parse(di.getUrl());
	    	it.setData(uri);
	    	it.putExtra("position", 0);

		    startActivity (it);
		    
		   
		}else if(di.getChildtype().equals(getResources().getString(R.string.type_series))){
			showSeriesView(di);
		}else if(di.getChildtype().equals(getResources().getString(R.string.type_variety))){
			showSeriesView(di);
		}else{	 
			Intent it = new Intent(); 
	    	it.setClass(MyDownloadVideoActivity.this,VideoPlaybackActivity.class);
	    	Uri uri = Uri.parse(di.getUrl());
	    	it.setData(uri);
	    	it.putExtra("position", 0);

		    startActivity (it);
		    
		}
		*/
	}
	
	private void showRenameWin(PopupWindow pmenu,final DataInfo info){
		if(pmenu !=null)
			pmenu.dismiss();
		
		View popmenuWindow = getLayoutInflater().inflate(R.layout.renamemenu, null);
		final PopupWindow popmenu = new PopupWindow(popmenuWindow, 403, 400);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句
		curpopw=popmenu;
		final EditText inputName = (EditText) popmenuWindow.findViewById(R.id.rename_input_name);
		Button okbtn = (Button) popmenuWindow.findViewById(R.id.bt_ok);
		okbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				
				//Toast.makeText(getApplicationContext(),(String)di.get("name"), 1000);
				popmenu.dismiss();
				
				Editable text = inputName.getText();
				if (text != null) {
					final String name = text.toString().trim();
					try {
						//libCloud.Mydownload_rename(Params.RECOMMEND_VIDEO, info.getResid(), name);
						libCloud.Mydownload_rename(Params.RECOMMEND_VIDEO, info.getId(), name);
						int index = (currentPage-1)*PAGE_SIZE + info.getPosition();
						Map<String,Object> m = (Map<String,Object>)videoList.get(index);
						m.put("resname", name);
					} catch (WeiboException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
				GetDataTask gdt = new GetDataTask();
				gdt.execute(currentPage,PAGE_SIZE);
				
				curpopw=null;
			}});
		Button cancelbtn = (Button) popmenuWindow.findViewById(R.id.bt_cancel);
		cancelbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				curpopw=null;
			}});
		popmenu.showAtLocation(findViewById(R.id.mydownloadvideo), Gravity.CENTER, 20, -20);
	}
	public void showpopmenu(final DataInfo di,View view){
		if(curpopw!=null)
			curpopw.dismiss();
		System.out.println("show pop menu");
		String strtext=di.getName();
		
		View popmenuWindow = getLayoutInflater().inflate(R.layout.mydownloadpopmenu, null);

		final PopupWindow popmenu = new PopupWindow(popmenuWindow, 180, 91);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句
		curpopw= popmenu;
		//ImageButton passwordQueDing = (ImageButton) passwordWindow.findViewById(R.id.passwordqueding);
		Button playbtn = (Button) popmenuWindow.findViewById(R.id.bt_play);
		playbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				System.out.println("show pop menu: "+di.getName());
				//Toast.makeText(getApplicationContext(),(String)di.get("name"), 1000);
				popmenu.dismiss();
				/*
				VideoPlayTask playtask = new VideoPlayTask();
				playtask.execute(di.getMovieId());
				*/
				execute(di);
				
			}});
		/*
		Button renamebtn = (Button) popmenuWindow.findViewById(R.id.bt_rename);
		renamebtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				showRenameWin(null,di);
			}});
		*/
		Button deletebtn = (Button) popmenuWindow.findViewById(R.id.bt_delete);
		deletebtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				showDelNoteDialog(di);
			}});
		popmenu.showAtLocation(findViewById(R.id.mydownloadvideo), Gravity.CENTER, 0, 0);
	}
	/*
	 * 显示加密对话框
	*/
	private void showEncryptMenu(final DataInfo di){
		if(curEncryptpopw!=null){
			curEncryptpopw.dismiss();
		}
	
		View pWin= getLayoutInflater().inflate(R.layout.myuploadpopencryptmenu, null);
		final PopupWindow popmenu = new PopupWindow(pWin, 301, 317);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句
		curEncryptpopw = popmenu;
		Button okbtn = (Button) pWin.findViewById(R.id.bt_ok);
		Button cancelbtn = (Button) pWin.findViewById(R.id.bt_cancel);
		final EditText psw = (EditText) pWin.findViewById(R.id.psw);
		final EditText pswconfirm = (EditText) pWin.findViewById(R.id.pswconfirm);
		okbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				String pswStr = psw.getText().toString();
				String pswConfirmStr = pswconfirm.getText().toString();
				if(pswStr.trim().equals("")||pswConfirmStr.trim().equals("")){
					UIHelper.displayToast(getResources().getString(R.string.pswd_cannot_empty),MyDownloadVideoActivity.this);
					return ;
				}
				if(pswStr.equals(pswConfirmStr)){
					try {
						Map<String,Object> result = libCloud.Set_file_password(di.getId(),pswStr);
						if(((String)result.get("code")).equals("1")){
							di.setSecurity("1");
							int index = (currentPage-1)*PAGE_SIZE + di.getPosition();
							Map<String,Object> m= videoList.get(index);
							m.put("needpassword", "1");
							GetDataTask gdt = new GetDataTask();
							gdt.execute(currentPage,PAGE_SIZE);
							/*
							ListAdapter ad = gridView.getAdapter();
							((MyDownloadVideoDataAdapter)ad).notifyDataSetChanged();
							*/
						}else{
							UIHelper.displayToast(getResources().getString(R.string.pswd_setting_failed),MyDownloadVideoActivity.this);
						}
					} catch (WeiboException e) {
						e.printStackTrace();
						UIHelper.displayToast(getResources().getString(R.string.pswd_setting_failed),MyDownloadVideoActivity.this);
					}
					popmenu.dismiss();
				}else{
					UIHelper.displayToast(getResources().getString(R.string.pswd_isnot_same),MyDownloadVideoActivity.this);
				}
			}});
		cancelbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				
			}});
		popmenu.showAtLocation(findViewById(R.id.mydownloadvideo), Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(!path.equals("")){
				path="";
				currentPage = parentCurrentPage;
				changeForder(path);
				return true;
			}else{
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	public void onDropOutFoder(Object dragInfo){
		//System.out.println("=============================drop outside of folder ==================================");
		
		if(path!=null&&(!path.equals(""))){
			mFolderDrop.setVisibility(View.GONE);
			if(dragInfo!=null){
				DataInfo info = (DataInfo) dragInfo;
				Map<String, Object> retmap=null;
				System.out.println("drop outside info name "+ info.getName() + "position=" + info.getPosition()+ "currentPage =" + currentPage);
				try {
					//retmap = libCloud.Mydownload_depart(Params.RECOMMEND_VIDEO,path,info.getResid());
					retmap = libCloud.Mydownload_depart(Params.RECOMMEND_VIDEO,path,info.getId());
				} catch (WeiboException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MyDownloadVideoDataAdapter adapter1 = (MyDownloadVideoDataAdapter) ((CustomerGridView) mListViews.get(currentPage -1)).getAdapter();
				System.out.println("drop outside drop position = " + info.getPosition());
				List<DataInfo> list1 = adapter1.getList();
				for(int i=0;i<list1.size();i++){
					DataInfo inf = list1.get(i);
					System.out.println("position = " + inf.getPosition() + "name = " + inf.getName());
				}
				list1.remove(info.getPosition());
				//adapter1.getList().remove(info.getPosition());
				for(int i=0;i<list1.size();i++){
					DataInfo inf = list1.get(i);
					System.out.println("position = " + inf.getPosition() + "name = " + inf.getName());
				}
				adapter1.notifyDataSetChanged();
			}
		}
	}
	
	private void showTrancodeWin(DataInfo di){
		if(curTranscodepopw !=null){ 
			if(curTranscodepopw.isShowing()){
				System.out.println("cruTranscodepopw is showing ");
			}else {
				System.out.println("curTranscodepopw is not showing");
			}
		}else{
			System.out.println("cruTranscodepopw is null");
		}
		if(curTranscodepopw!=null){
			curTranscodepopw.dismiss();
		}
		View pWin= getLayoutInflater().inflate(R.layout.transcode, null);
		final PopupWindow popmenu = new PopupWindow(pWin, 782, 272);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句
		curTranscodepopw = popmenu;
		transPager = (ViewPager) pWin.findViewById(R.id.transcodeViewPager);

		//addDataToTransGridView(transList);
		
		popmenu.showAtLocation(findViewById(R.id.mydownloadvideo), Gravity.CENTER, 0, 0);
		
		onPercentChanged(di.getId(),di.getFileid(),di.getThumb());
	}
	private void setTransGrid(Context context, List<DataInfo> list) {
		int PageCount = (int) Math.ceil(((double)list.size())/TRANS_PAGE_SIZE);
		System.out.println("setTransGrid  PageCount = " + PageCount);
		for (int i = 0; i < PageCount; i++) {
			transgridView = (CustomerGridView) LayoutInflater.from(context).inflate(
					R.layout.transcode_grid, null);
			transgridView.setAdapter(new TransDataAdapter(context, list,i));
				
			// 去掉点击时的黄色背景
			transgridView.setSelector(R.color.transparent);
			transListViews.add(transgridView);
		}
	}
	public void addDataToTransGridView(List<DataInfo> list){	
		transListViews = new ArrayList<View>();
		setTransGrid(this, list);
		transPageAdapter = new ViewPageAdapter(this,transListViews,2);
		transPager.setAdapter(transPageAdapter);
		transPager.setCurrentItem(0);
		transPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int page) {
				
			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			public void onPageScrollStateChanged(int arg0) {}
		});
	}
	
	 /*
     * 点击转码按钮
     */
	public void onTranscodeClick(View v) {
		ImageView iv = (ImageView) v;
		DataInfo info = (DataInfo) v.getTag();
		Log.i("onTranscodeClick", "detailResDownloadClick + type = " + info.GetTag());
		/*
		if(info.getStatus()==5){
			Intent it = new Intent(); 
    		it.setClass(MyDownloadVideoActivity.this,VideoPlaybackActivity.class);
    		Uri uri = Uri.parse(info.getUrl());
    		it.setData(uri);
    		it.putExtra("position", 0);
    		startActivity (it);
		}else if(info.getStatus()==2){
			iv.setImageResource(R.drawable.transcodeing);
			TransCodeTask transTask = new TransCodeTask();
			transTask.execute(info.getId(),info.GetTag(),info.getThumb());
		}
		*/
		TransCodeTask transTask = new TransCodeTask();
		transTask.execute(info.getId(),info.getFileid(),info.GetTag(),info.getThumb());
	}
	class TransPercentTask extends AsyncTask<Object, Integer, List<DataInfo>> {
		String fileid;
		String imageurl;
		String taskid;
		@Override
		protected List<DataInfo> doInBackground(Object... params) {
			taskid = (String) params[0];
			fileid = (String) params[1];
			imageurl = (String)params[2];
			Map<String, Object> retmap=null;
			List<DataInfo> percentList = null;
			try {
				System.out.println("get offline transpercent start ");
				retmap = libCloud.get_offline_transpercent(fileid);
				System.out.println("get offline transpercent end ");
				if(retmap!=null && retmap.get("code").equals("1")){
					System.out.println("get offline transpercent fill transData ");
					percentList = DataHelpter.fillTransData((List<Map<String, Object>>) retmap.get("percentlist"),fileid,imageurl);
				}else{
					System.out.println(" get offline transpercent list is NULL ");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(percentList==null){
				percentList =  new ArrayList<DataInfo>();
			}
			
			for(int i=0;i<4;i++){		
				if(DataHelpter.findDataByType(percentList,transType[i])==false){
					DataInfo info = new DataInfo();
					info.setThumb(imageurl);
					info.SetTag(transType[i]);/*0 :640x480; 1:1280x720;2:1920x1080;3:1024x768*/
					info.setStatus(2);
					info.setId(taskid);
					info.setFileid(fileid);
					percentList.add(info);
				}
			}
			return percentList;
		}
		

		@Override
		protected void onPostExecute(List<DataInfo> result) {
			if(myHandler!=null){
				addDataToTransGridView(result);
				if(DataHelpter.needUpdatePercent(result)){		
					onUpdateTransPercent(fileid);
			
				}
			}
		}
	}
	class UpdatePercentTask extends AsyncTask<Object, Integer, List<DataInfo>> {
		String fileid;
		@Override
		protected List<DataInfo> doInBackground(Object... params) {
			fileid = (String) params[0];
			String imageurl="1";
			Map<String, Object> retmap=null;
			List<DataInfo> percentList = null;
			try {
				retmap = libCloud.get_offline_transpercent(fileid);
				if(retmap!=null && retmap.get("code").equals("1")){
					percentList = DataHelpter.fillTransData((List<Map<String, Object>>) retmap.get("percentlist"),fileid,imageurl);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(percentList==null){
				return new ArrayList<DataInfo>();
			}
			for(int i=0;i<percentList.size();i++){
				DataInfo info = percentList.get(i);
				
			}
			return percentList;
		}
		

		@Override
		protected void onPostExecute(List<DataInfo> result) {
			if(myHandler!=null){
				UpdateTransGridView(result);
				if(DataHelpter.needUpdatePercent(result) && curTranscodepopw.isShowing()){		
					Message msg = new Message();
					msg.arg1=Integer.parseInt(fileid);
					msg.what = PERCENT_CHANGED;
					myHandler.sendMessageDelayed(msg, 1000);
				}
			}
		}
	}
	class TransCodeTask extends AsyncTask<Object, Integer, String> {
		String fileid = null;
		String imageurl =null;
		String taskid = null;
		@Override
		protected String doInBackground(Object... params) {
			taskid = (String) params[0];
			fileid = (String) params[1];
			String width = (String)params[2];
			imageurl = (String)params[3];
			Map<String, Object> retmap=null;
			String code = null;
			try {
				retmap = libCloud.Start_offlinetranscode(taskid,fileid,width);
				if(retmap!=null ){
					code = (String) retmap.get("code");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return code;
		}

		@Override
		protected void onPostExecute(String result) {
			if(result!=null && result.equals("1")){
				//onUpdateTransPercent(fileid);
			}
		}
	}
	
	private void onPercentChanged(String taskid,String fileid,String imageurl){
		TransPercentTask percentTask = new TransPercentTask();
		percentTask.execute(taskid,fileid,imageurl);
	}
	private void onUpdateTransPercent(String fileid){
		UpdatePercentTask updatepercentTask = new UpdatePercentTask();
		updatepercentTask.execute(fileid);
	}
	private void UpdateTransGridView(List<DataInfo> list){
		TransDataAdapter adapter1 = (TransDataAdapter) ((CustomerGridView) transListViews.get(0)).getAdapter();
		List<DataInfo> transList = adapter1.getList();
		for(int i=0;i<transList.size();i++){
			DataInfo info = transList.get(i);
			for(int j=0;j<list.size();j++){
				DataInfo info1 = list.get(j);
				if(info.GetTag().equals(info1.GetTag())){
					info.setProgress(info1.getProgress());
				}
			}
		}
		adapter1.notifyDataSetChanged();
	}
	private final static int PERCENT_CHANGED = 0; 
	private final static int PAGE_CHANGED = 1;
    Handler myHandler = new Handler(){
    
		@Override
		public void handleMessage(Message msg) {
			String fileid ;
			switch(msg.what){
				
				case PERCENT_CHANGED:
					onUpdateTransPercent(msg.arg1+"");
					break;
				case PAGE_CHANGED:
					int page = msg.getData().getInt("page");
					int cpage = msg.getData().getInt("cpage");
					System.out.println("hadnleMessage page = " + page + "cpage = " + cpage);
					displayImageView(page);
					break;
				default:
					break;
			}
			
			super.handleMessage(msg);
		}	
    };
    
	/*删除提示对话框*/
	private void showDelNoteDialog(final DataInfo di) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MyDownloadVideoActivity.this.getParent());
		builder.setTitle(R.string.system_tips);
		builder.setMessage(R.string.msg_del_file);
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						doDelFile(di);
					}
				});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
				
			}
		});
		builder.show();
	}
	private void doDelFile(DataInfo di){
		try {
			//libCloud.Mydownload_del(Params.RECOMMEND_VIDEO,di.getResid(),"0");
			libCloud.Mydownload_del(Params.RECOMMEND_VIDEO,di.getId(),"0");
		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int index = (currentPage-1)*PAGE_SIZE + di.getPosition();
		videoList.remove(index);
		
		List<DataInfo> videoDataList = null;
		String source = di.getChildtype();
		Map<String,Object> ret =  DataHelpter.getVideoList(source);
		if(ret!=null){
			videoDataList = (List<DataInfo>) ret.get("recommend");
			DataHelpter.updateDownloadStatus(videoDataList,di.getResid(),0);
		}
		
		GetDataTask gdt = new GetDataTask();
		gdt.execute(currentPage,PAGE_SIZE);
		
		GetCapacityAsync capacity = new GetCapacityAsync();
		capacity.execute();
	}
	
	class VideoPlayTask extends AsyncTask<Object, Integer, List<DataInfo>> {
		@Override
		protected List<DataInfo> doInBackground(Object... params) {
			String movieid = (String) params[0];
			Map<String, Object> retmap=null;
			String code = null;
			List<DataInfo> list = null ;
			try {
				retmap =  (Map<String, Object>) libCloud.get_hdplayInfo(movieid);
				if(retmap!=null &&  retmap.containsKey("list")){
					
					List<Map<String, Object>> detail  = (List<Map<String, Object>>) retmap.get("list");
					list = DataHelpter.GetSeriesList(detail);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list;
		}

		@Override
		protected void onPostExecute(List<DataInfo> result) {
			if(result!=null && result.size()>0){
				DataInfo info = result.get(0);
				MediaPlayerHelper.play(MyDownloadVideoActivity.this, info.getUrl(),info.getMovieId(),info.getVideoid(),"1");
			}else{
				UIHelper.displayToast(getResources().getString(R.string.url_invalid), MyDownloadVideoActivity.this);
			}
		}
	}
	
}