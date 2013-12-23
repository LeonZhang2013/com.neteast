package com.neteast.clouddisk.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ActivityGroup;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.lib.net.WeiboException;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.activity.MyDownloadVideoActivity.GetSeriesDataTask;
import com.neteast.clouddisk.adapter.MyDownloadSeries0DataAdapter;
import com.neteast.clouddisk.adapter.MyDownloadSeriesDataAdapter;
import com.neteast.clouddisk.adapter.MyDownloadSourceDataAdapter;
import com.neteast.clouddisk.adapter.SearchDataAdapter;
import com.neteast.clouddisk.adapter.ViewPageAdapter;
import com.neteast.clouddisk.customerview.CustomerGridView;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.service.IMediaPlaybackService;
import com.neteast.clouddisk.service.MusicUtils;
import com.neteast.clouddisk.service.MusicUtils.ServiceToken;
import com.neteast.clouddisk.utils.DataHelpter;
import com.neteast.clouddisk.utils.MediaPlayerHelper;
import com.neteast.clouddisk.utils.UIHelper;


public class SearchResultActivity1 extends ActivityGroup {
	LibCloud lib;
	List<DataInfo> mlist;
	
	String key;
	int type ;
	private PopupWindow curinputpopw=null;
	private IMediaPlaybackService mService = null;
	private ServiceToken mToken;
	private SearchDataAdapter adapter;
	private GestureDetector slideGesture;
	private GestureDetector clickGesture;
	private CustomerGridView gridView;
	private TextView pageNum;
	private int currentPage = 1;
	private int totalPage = 1;
	private TextView currentPageNum = null;
	private TextView totalPageNum = null;
	
	private LibCloud libCloud;
	private LinearLayout seriesgridLayout = null;
	private GridView seriesGridView;
	private GridView seriesGridView0;
	private PopupWindow seriesPopWin=null;
	private View seriesView;
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
	
	private TextView sereisSelText1=null;
	private TextView sereisSelText2=null;
	private TextView sereisSelText3=null;
	private TextView sereisSelText4=null;
	private TextView sereisSelText5=null;
	private TextView sereisSelText6=null;
	
	private TextView seriesPageText = null;
	
	private ImageView leftSmallArrow;
	private ImageView rightSmallArrow;
	private ImageView leftBigArrow;
	private ImageView rightBigArrow;
	
	private ImageButton sourceselbtn = null ;
	private LinearLayout sourcetagView = null;
	
	int buttontype = 1; /*1 高清观看�? 在线观看�?离线下载*/
	private int mPlayType = 2; /*1：在线观看，2：高清观看*/
	private List<DataInfo> onlinePlayList = null;
	private List<DataInfo> hdseriesList = null;
	private List<DataInfo> offlineDownloadList = new ArrayList<DataInfo>();
	private List<DataInfo> selonlinePlayList = new ArrayList<DataInfo>();
	private int seriesCurrentPage = 1;
	private int seriesTotalPage = 1;
	private int MovieType = 1;
	private List<String> sourceTag= new ArrayList<String>();
	private List<DataInfo> selectList = new ArrayList<DataInfo>();
	private MyDownloadSourceDataAdapter sourceadapter;
	private MyDownloadSeries0DataAdapter seriesAdapter0;
	
	
	
	
	/** ViewPager控件，横屏滑动*/
	private ViewPager seriesPager;
	
	/** ViewPager的视图列表*/
	private List<View> mSeriesListViews;
	
	/** ViewPager的适配器. */
	private ViewPageAdapter seriesviewPageAdapter;
	private static final int SERIES_PAGE_SIZE = 30;
	private static final int VARITY_PAGE_SIZE = 8;
	
	
	
	
	int mplaylistisUpdated =0;
	/** ViewPager控件，横屏滑动*/
	private ViewPager filePager;
	
	/** ViewPager的视图列表*/
	private List<View> mListViews;
	
	/** ViewPager的适配器. */
	private ViewPageAdapter viewPageAdapter;
	private static final int PAGE_SIZE = Params.MYUPLOAD_DATA_PER_PAGE_NUM;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lib = LibCloud.getInstance(this);
		setContentView(R.layout.searchresult);
		Button viewBtn = (Button) findViewById(R.id.searchreturn);
		currentPageNum = (TextView) findViewById(R.id.curentpage);
		totalPageNum = (TextView) findViewById(R.id.totalpage);
		//gridView = (CustomerGridView) findViewById(R.id.mygridview);
		TextView searchResult = (TextView) this.findViewById(R.id.searchresultinfo);
		
		filePager = (ViewPager) findViewById(R.id.searchresultViewPager);
		
		libCloud = LibCloud.getInstance(this);
	
		Intent intent = this.getIntent();
		mlist = (List<DataInfo>) intent
				.getSerializableExtra("result");
		key = (String) intent.getStringExtra("key");
		type = this.getIntent().getIntExtra("searchType",1);
	
		int size = mlist.size();
		String value = String.format(getResources().getString(R.string.search_result_text), size);
		searchResult.setText(value);
		/*
		if(type ==1){
			
			searchResult.setText("在\"我的上传\"中为您搜索到\"" + key + "\"" + size
				+ "条相关结果");
		}else{
		
			searchResult.setText("在\"我的下载\"中为您搜索到\"" + key + "\"" + size
				+ "条相关结果");
		}
		*/
		//showPages(currentPage);
		addDataToGridView(mlist);
		if(size > 0){
			DataInfo info = mlist.get(0);
			if(info.getType().equals("2")){
				mToken = MusicUtils.bindToService(this, osc);
				if(mToken==null){
					Log.d("AudioActivity","bindToMediaPlaybackService error" );
				}
				DataHelpter.setPlaylist(mlist);
			}
		}
		

		viewBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onreturn();
			}
		});
	}
	
	@Override
	public void onDestroy()
	{
		if(mToken!=null){
			MusicUtils.unbindFromService(mToken);
		}
		super.onDestroy();
	        //System.out.println("***************** playback activity onDestroy\n");
	}
	
	private void setGrid(Context context, List<DataInfo> list) {
		int PageCount = (int) Math.ceil(((double)list.size())/PAGE_SIZE);
		System.out.println("setGrid  PageCount = " + PageCount);
		for (int i = 0; i < PageCount; i++) {
			gridView = (CustomerGridView) LayoutInflater.from(context).inflate(
					R.layout.myupload_grid, null);
			gridView.setAdapter(new SearchDataAdapter(context, list,i,mLongClickListener,mClickListener));
			gridView.setVerticalSpacing(50);			
			// 去掉点击时的黄色背景
			//gridView.setSelector(R.color.transparent);
			mListViews.add(gridView);
		}
		currentPageNum.setText(currentPage+"");
		totalPageNum.setText(PageCount+"");
	}
	private View.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			boolean condition = false;
			//TODO:判断条件，是否是拖拽事件
			
			return true;
		}
	};
	
	private View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			DataInfo info = (DataInfo) arg0.getTag();
			//System.out.println("RecommendVideoActivity on item clicked" + "name = " + info.getName() + "url = " + info.getUrl() );	
			
			if(info.getSecurity()!=null &&info.getSecurity().equals("1")){
				showInputPsw(info,getResources().getString(R.string.pswd_input_notices));			 
			}else{
				judgeOperate(info);
			}
		}

	};
	/*
	 * 判断点击后操作
	 */
	private void judgeOperate(DataInfo di){
		String isDir = di.GetIsDir();
					
			if(di.getType().equals("1")){
				System.out.println("type = " + type);
				if(type == 1){
					if(di.getUrl()==null || di.getUrl().length()==0){
						UIHelper.displayToast(getResources().getString(R.string.invalid_url), SearchResultActivity1.this);
					}else{
						MediaPlayerHelper.play(SearchResultActivity1.this, di.getUrl(),"0","0","0");
					}
				}else {	
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
				}
				/*
				Intent it = new Intent(); 
		    	it.setClass(SearchResultActivity1.this,VideoPlaybackActivity.class);
		    	Uri uri = Uri.parse(di.getUrl());
		    	it.setData(uri);
		    	it.putExtra("position", 0);
			    startActivity (it);
			    */
			}else if(di.getType().equals("2")){
				int index = (currentPage -1)*Params.SEARCH_RESULT_PER_PAGE_NUM+di.getPosition();
				try {
					if(mplaylistisUpdated==0){
						mService.setRepeatMode(2);
						mService.createplaylist(index);
						mService.playById(di.getId());
						mplaylistisUpdated = 1;
					}else{
						//mService.setQueuePosition(index);
						mService.playById(di.getId());
					}
				} catch (RemoteException e1) {
						// TODO Auto-generated catch block
					e1.printStackTrace();
				}
					
					
	
			}else if(di.getType().equals("3")){
				Intent it = new Intent(); 
		    	it.setClass(SearchResultActivity1.this,ImageSwitcher1.class);
		    	it.putExtra("result", (Serializable)mlist);
		    	//Uri uri = Uri.parse(di.getUrl());
		    	//it.setData(uri);
		    	it.putExtra("position", di.getPosition());
			    startActivity (it);
			}else if(di.getType().equals("4")){
				String urlStr = di.getUrl();
				if (urlStr != null && !urlStr.equals("")) {
					String downloadName = urlStr.substring(
							urlStr.lastIndexOf("/"), urlStr.length());
					String path = downloadFile(urlStr, downloadName);
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setAction(Intent.ACTION_VIEW);
					File f = new File(path);
					Uri uri = Uri.fromFile(f);
					intent.setDataAndType(uri,new OpenFile().getType(f));
					try{
						startActivity(intent);
					}catch(Exception ee){
						Toast.makeText(SearchResultActivity1.this, "无默认程序打开该文？",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(SearchResultActivity1.this, "无法打开该文？",
							Toast.LENGTH_SHORT).show();
				}	
			}
	}

	
	public void addDataToGridView(final List<DataInfo> result) {
		mListViews = new ArrayList<View>();
		setGrid(this, result);
		viewPageAdapter = new ViewPageAdapter(this,mListViews,1);
		filePager.setAdapter(viewPageAdapter);
		
		filePager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int page) {
				totalPage = DataHelpter.computeTotalPage(result.size(),PAGE_SIZE);
				currentPage = page + 1;
				currentPageNum.setText(currentPage+"");
				totalPageNum.setText(totalPage+"");
			
			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			public void onPageScrollStateChanged(int arg0) {}
		});
		filePager.setCurrentItem(currentPage-1);
	}
	/*
	 * 显示密码输入框
	 */
	private void showInputPsw(final DataInfo di,String msg){
		if(curinputpopw!=null){
			curinputpopw.dismiss();
		}
		View pWin= getLayoutInflater().inflate(R.layout.myuploadinputpswmenu, null);
		final PopupWindow popmenu = new PopupWindow(pWin, 301, 260);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());
		curinputpopw = popmenu;
		Button okbtn = (Button) pWin.findViewById(R.id.bt_ok);
		Button cancelbtn = (Button) pWin.findViewById(R.id.bt_cancel);
		final EditText psw = (EditText) pWin.findViewById(R.id.psw);
		TextView msgs = (TextView) pWin.findViewById(R.id.msg);
		msgs.setText(msg);
		okbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				String pswStr = psw.getText().toString();
				if(pswStr.trim().equals("")){
					UIHelper.displayToast(getResources().getString(R.string.pswd_cannot_empty),SearchResultActivity1.this);
				}else{
					//Map<String,Object> result = libCloud.Set_file_password(id,pswStr);
					if(pswStr.trim().equals(di.getPasswd())){
						judgeOperate(di);
						popmenu.dismiss();
					}else{
						UIHelper.displayToast(getResources().getString(R.string.pswd_error),SearchResultActivity1.this);
					}
					
				}
			}});
		cancelbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				
			}});
		popmenu.showAtLocation(findViewById(R.id.myuploadvideo_page), Gravity.CENTER, 0, 0);
	}


	private String  downloadFile(String urlStr, String downloadName) {
		RandomAccessFile file = null;
		String path = Params.DOWNLOAD_FILE_PATH+ downloadName;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestProperty("Accept-Language", Params.ACCEPT_LANGUAGE);
			conn.setRequestProperty("Charset", Params.ChARSET);
			conn.setRequestProperty("User-Agent", "NetFox");
			conn.setRequestProperty("Range", "bytes=0-");
			conn.setRequestProperty("Connetion", "Keep-Alive");
			int fileSize = conn.getContentLength();
			File f = new File(Params.DOWNLOAD_FILE_PATH);
			if (!f.exists()) {
				f.mkdir();
			}
			file = new RandomAccessFile(path, "rw");
			file.setLength(fileSize);

			InputStream in = conn.getInputStream();
			byte[] buffer = new byte[1024];
			int len;
			file.seek(0);
			while ((len = in.read(buffer)) != -1) {
				file.write(buffer, 0, len);
			}
			file.close();
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;

	}
    private ServiceConnection osc = new ServiceConnection() {
        public void onServiceConnected(ComponentName classname, IBinder obj) {
            mService = IMediaPlaybackService.Stub.asInterface(obj);
            //startPlayback();
            
           // finish();
        }
        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		System.out.println("Search result on KeyDown !!!!!!");
	
		if(keyCode == KeyEvent.KEYCODE_BACK){
			System.out.println("on BACK KeyDown !!!!!!");
			onreturn();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	private void onreturn(){
		Class cls = null;
		String selectName = "MyUploadVideoActivity";
		MainActivity main = ((MainActivity) SearchResultActivity1.this
				.getParent().getParent());
		int index = main.getSelectIndex();
		if (type == 1) {
			switch (index) {
			case 1:
				cls = MyUploadVideoActivity.class;
				selectName = "MyUploadVideoActivity";
				break;
			case 2:
				cls = MyUploadMusicActivity.class;
				selectName = "MyUploadMusicActivity";
				break;
			case 3:
				cls = MyUploadPictureActivity.class;
				selectName = "MyUploadPictureActivity";
				break;
			case 4:
				cls = MyUploadFileActivity.class;
				selectName = "MyUploadFileActivity";
				break;
			case 5:
				cls = UploadActivity.class;
				selectName = "UploadActivity";
				break;
			default:
				cls = MyUploadVideoActivity.class;
				selectName = "MyUploadVideoActivity";
				break;
			}
		}else if(type ==2){
			switch (index) {
			case 1:
				cls = MyDownloadVideoActivity.class;
				selectName = "MyDownloadVideoActivity";
				break;
			case 2:
				cls = MyDownloadMusicActivity.class;
				selectName = "MyDownloadMusicActivity";
				break;
			case 3:
				cls = MyDownloadPictureActivity.class;
				selectName = "MyDownloadPictureActivity";
				break;
			default:
				cls = MyDownloadVideoActivity.class;
				selectName = "MyDownloadVideoActivity";
				break;
			}
		}
		/*
		 * RelativeLayout rl = (RelativeLayout)
		 * SearchResultActivity1.this .getParent().getWindow()
		 * .findViewById(R.id.datacontainer);
		 */
		// ///////////////////////////////
		RelativeLayout rl = null;
		if (type == 1) {
			MyUploadActivity context = (MyUploadActivity) main.getCtx();
			rl = context.getContainer();

		} else {
			MyDownloadActivity context = (MyDownloadActivity) main
					.getCtx();
			rl = context.getContainer();
		}
		rl.removeAllViews();
		Intent intent = new Intent(SearchResultActivity1.this, cls);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window sub = ((ActivityGroup) SearchResultActivity1.this
				.getParent()).getLocalActivityManager().startActivity(
				selectName, intent);
		View view = sub.getDecorView();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		view.setLayoutParams(params);
		rl.addView(view);
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
		final Button offlinedownloadbtn = (Button) seriesView.findViewById(R.id.offlinedownloadbtn);
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
				offlinedownloadbtn.setBackgroundResource(R.drawable.detail_offlinedownload);
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
				offlinedownloadbtn.setBackgroundResource(R.drawable.detail_offlinedownload);
				//sourceselbtn.setVisibility(View.VISIBLE);
				//sourcetagView.setVisibility(View.VISIBLE);
				buttontype = 2;
				mPlayType = 1;
				showSeriesPage();
			}
		});
		offlinedownloadbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				offlinedownloadbtn.setBackgroundResource(R.drawable.detail_offlinedownload_p);
				onlineplaybtn.setBackgroundResource(R.drawable.detail_onlineplaybtn);
				hdplaybtn.setBackgroundResource(R.drawable.detail_hdplaybtn);
				//sourceselbtn.setVisibility(View.INVISIBLE);
				//sourcetagView.setVisibility(View.INVISIBLE);
				mPlayType = 3;
				buttontype = 3;
				showSeriesPage();
			}
		});
		
	}
	
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
						MediaPlayerHelper.play(SearchResultActivity1.this, di.getUrl(),di.getMovieId(),di.getVideoid(),"2");
						
					}else{
						UIHelper.displayToast(getResources().getString(R.string.url_invalid), SearchResultActivity1.this);
					}
				
			}else if(mPlayType == 2){/*高清播放*/
				MediaPlayerHelper.play(SearchResultActivity1.this, di.getUrl(),di.getMovieId(),di.getVideoid(),"1");
					
			}else if(mPlayType == 3){/*离线下载播放*/
				//showTrancodeWin(di);	
			}
		}      
	};
	
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
	
	OnItemClickListener mOnSeries0ItemClick = new OnItemClickListener() {   
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {   
				System.out.println("mydownloadVideo on Item click arg2 :" + arg2);				
				seriesPager.setCurrentItem(arg2);
		    }      
	};
	
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
		if(seriesPopWin==null){
			System.out.println("sereisPopWin is NUll");
		}
		//seriesPopWin.showAtLocation(pageNum, Gravity.CENTER|Gravity.LEFT, 160, 75);
		seriesPopWin.showAtLocation(findViewById(R.id.searchresult), Gravity.CENTER|Gravity.LEFT, 160, 58);
		if(onlinePlayList!=null) onlinePlayList.clear();
		if(hdseriesList!=null) hdseriesList.clear();
		if(offlineDownloadList!=null) offlineDownloadList.clear();
		if(selonlinePlayList!=null) selonlinePlayList.clear();
		GetSeriesDataTask gdt = new GetSeriesDataTask();
		gdt.execute(seriesCurrentPage,di);
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
	public void addDataToSeriesGridView(final List<DataInfo> result){
		
		initSourceTagList();
		System.out.println("sourceTag size = " + sourceTag.size());
		if(sourceTag.size()>0){
			String tag = sourceTag.get(0);
			initOnlinePlayListWithTag(tag);
		}						
		showSeriesPage();
		
	}
	private boolean exist(String tag){
		for(String s:sourceTag){
			if(s.equals(tag)){
				return true;
			}
		}
		return false;
	}
	class GetSeriesDataTask extends AsyncTask<Object, Integer, List<DataInfo>> {
		
		@Override
		protected List<DataInfo> doInBackground(Object... params) {
			
			Map<String, Object> retmap=null;
			Map<String, Object> retmap1=null;
			//Map<String,	Object> retmap2 = null; /*离线下载暂时去掉*/
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
				//retmap2 = libCloud.Get_task(0,info.getMovieId());
				
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
			/*
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
			*/
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
	
	
}
