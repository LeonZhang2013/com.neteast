package com.neteast.clouddisk.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.lib.net.WeiboException;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.handler.ExitHandler;
import com.neteast.clouddisk.handler.MyConfigHandler;
import com.neteast.clouddisk.handler.MyHandler;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.service.IMediaPlaybackService;
import com.neteast.clouddisk.service.MediaPlaybackService;
import com.neteast.clouddisk.service.MusicUtils;
import com.neteast.clouddisk.service.MusicUtils.ServiceToken;
import com.neteast.clouddisk.service.UploadService;
import com.neteast.clouddisk.utils.ApkManager;
import com.neteast.clouddisk.utils.DataHelpter;
import com.neteast.clouddisk.utils.DownLoadApplication;
import com.neteast.clouddisk.utils.FileUtil;
import com.neteast.clouddisk.utils.UIHelper;
import com.neteast.data_acquisition.DataAcqusition;

public class MainActivity extends ActivityGroup{
	private ImageButton mydownloadButton;
	private ImageButton myuploadButton;
	private ImageButton recommendButton;
	private RelativeLayout container;
	private LibCloud libCloud;
	ImageButton musicReplayButton;
	ImageButton musicPlayButton;
	ImageButton musicCloseButton;
	ImageButton musicNextButton;
	ImageButton musicPrevButton;
	ImageButton musicPlayModeButton;
	private Context ctx;
	RelativeLayout musicplayerView =null;
	private SeekBar seekBar = null; 
	private IMediaPlaybackService mService = null;
	private ServiceToken mToken=null;
	private ImageButton searchbutton;
	private int searchType = 3;
	private int index = 1;
	private int tag = 0;
	int userid=0;
	String token ="";
	private String curActivity = "recommend";
	
	String versionUrl =Params.APP_VERSION_URL;
	String apkUrl =Params.APP_APK_URL;
	String verCode =null;
	String verName =null;
	String verInfo =null;
	int mCurrentVerCode = 0;
	String mCurrentVerName = null;
	private LinearLayout mainLayout = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mainLayout = (LinearLayout) findViewById(R.id.main);
		mainLayout.setVisibility(View.GONE);
		libCloud = LibCloud.getInstance(this);
		
		if (!DataHelpter.isNetworkAvailable(this, libCloud.getUserServer())) {
			showWIFiDialog();
		}
		
		userid = DataHelpter.isLogin(getContentResolver());
		if (userid == 0) {
			try {
				Intent intent = new Intent();
				intent.putExtra("pkg", "com.neteast.clouddisk");
				intent.putExtra("cls", "com.neteast.clouddisk.activity.MainActivity");
				intent.setAction("com.neteast.androidclient.newscenter.login");
				startActivity(intent);
				this.finish();
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			token = DataHelpter.getToken();
		}
		Log.d("MainActivity", "userid = " + userid + "token" + token);
		libCloud.SetUserID(Integer.toString(userid));
		libCloud.SetToken(token);
		DataAcqusition.init(this, String.valueOf(userid), token);
		DataAcqusition.useCloudBank(DataAcqusition.LOGIN);
		GetCapacityAsync CheckUserInvalid = new GetCapacityAsync();
		CheckUserInvalid.execute();
	}
	
	private void isUserInvalid(Map<String, Object> result){
		String product_id = "";
		if (result != null && result.get("code").equals("1")) {
			product_id = (String) result.get("product_id");
			if (product_id != null && product_id.length() > 0) {
				clouddisk_start();
			} else {
				showInvalidDialog();
			}
		} else {
			showInvalidDialog();
		}
	}
	
	private void clouddisk_start(){		
		mainLayout.setVisibility(View.VISIBLE);
		new ApkManager(this, new ApkManager.ApkUpdateListener() {

			public void onComplete(String message) {
				Log.i("test", "我的云盘已经是最新版本");
			}
		}).startUpdate();

		getAppVersion();
		
		final LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		container = (RelativeLayout) findViewById(R.id.datacontainer);
		
		recommendButton = (ImageButton) findViewById(R.id.recommendbutton);
		mydownloadButton = (ImageButton) findViewById(R.id.mydownloadbutton);
		myuploadButton = (ImageButton) findViewById(R.id.myuploadbutton);
		searchbutton = (ImageButton) findViewById(R.id.searchbutton);
		
		searchbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startsearchactivity(null);
			}
		});
		
		mToken = MusicUtils.bindToService(this, osc);
		if(mToken==null){
			Log.d("AudioActivity","bindToMediaPlaybackService error" );
		}
		
        IntentFilter f = new IntentFilter();
        f.addAction(MediaPlaybackService.PLAYSTATE_CHANGED);
        f.addAction(MediaPlaybackService.META_CHANGED);
        f.addAction(MediaPlaybackService.PLAYBACK_COMPLETE);
        f.addAction(MediaPlaybackService.ASYNC_OPEN_COMPLETE);
        registerReceiver(mStatusListener, new IntentFilter(f));
        musicplayerView = (RelativeLayout) findViewById(R.id.musicPlayerView);

		//musicReplayButton = (ImageButton) findViewById(R.id.musicreplayer);
		
		musicNextButton = (ImageButton) findViewById(R.id.musicnext);
		musicPrevButton = (ImageButton) findViewById(R.id.musicprev);
		musicNextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if(mService!=null) mService.next();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		
		musicPrevButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if(mService!=null) mService.prev();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		

		musicPlayButton = (ImageButton) findViewById(R.id.musicstartorpause);
		musicPlayButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if(mService.isPlaying()){
						mService.pause();
						musicPlayButton.setImageResource(R.drawable.playerbutton);
						myHandler.sendEmptyMessageDelayed(CLOSE_MUSIC_PLAYER, 1000*60*5);
					}else{
						mService.play();
						musicPlayButton.setImageResource(R.drawable.pausebutton);
						myHandler.removeMessages(CLOSE_MUSIC_PLAYER);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		
		musicPlayModeButton = (ImageButton) findViewById(R.id.musicplaymode);
		musicPlayModeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					
					if(mService.getShuffleMode()==0){
						mService.setShuffleMode(1);
						musicPlayModeButton.setImageResource(R.drawable.shufflebtn);
					}else{
						mService.setShuffleMode(0);
						musicPlayModeButton.setImageResource(R.drawable.orderbtn);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		
        seekBar = (SeekBar) findViewById(R.id.musicseekbar);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

				@Override
				public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
					
					if(fromUser){
						try {
							mService.seek(progress);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
					
				}
	
				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
					
				}
	
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					
				}
        	});
        
        // 精彩推荐Tab
		recommendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchType = 3;
				//searchbutton.setVisibility(View.INVISIBLE);
				getLocalActivityManager().destroyActivity(curActivity, true); 
				container.removeAllViews();
				recommendButton.setImageResource(R.drawable.recommendico_press);
				myuploadButton.setImageResource(R.drawable.myuploadico);
				mydownloadButton.setImageResource(R.drawable.mydownloadico);
				View view = getLocalActivityManager().startActivity("recommend", 
						new Intent(MainActivity.this, RecommendActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
				ctx = view.getContext();
				view.setLayoutParams(param);
				container.addView(view);
				curActivity = "recommend";
			}
		}); 
		
		// 我的下载Tab
		mydownloadButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchType = 2;
				searchbutton.setVisibility(View.VISIBLE);
				getLocalActivityManager().destroyActivity(curActivity, true);
				container.removeAllViews();
				recommendButton.setImageResource(R.drawable.recommendico);
				myuploadButton.setImageResource(R.drawable.myuploadico);
				mydownloadButton.setImageResource(R.drawable.mydownloadico_press);
				View view = getLocalActivityManager().startActivity("mydownload", 
						new Intent(MainActivity.this, MyDownloadActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
				ctx = view.getContext();
				view.setLayoutParams(param);
				container.addView(view);
				curActivity = "mydownload";
			}
		});

		// 我的上传Tab
		myuploadButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchType = 1;
				searchbutton.setVisibility(View.VISIBLE);
				getLocalActivityManager().destroyActivity(curActivity, true);
				container.removeAllViews();
				recommendButton.setImageResource(R.drawable.recommendico);
				myuploadButton.setImageResource(R.drawable.myuploadico_press);
				mydownloadButton.setImageResource(R.drawable.mydownloadico);
				View view = getLocalActivityManager().startActivity("myupload",
						new Intent(MainActivity.this, MyUploadActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
				ctx = view.getContext();
				view.setLayoutParams(param);
				container.addView(view);
				curActivity = "myupload";
			}
		});
		recommendButton.setImageResource(R.drawable.recommendico_press);
		myuploadButton.setImageResource(R.drawable.myuploadico);
		mydownloadButton.setImageResource(R.drawable.mydownloadico);
		
		
	

		String action = getIntent().getAction();
		DataInfo info = new DataInfo();
		Bundle args = null;
		if (action != null) {
			System.out.println("action  = " + action);
		} else {
			System.out.println("action is null ");
		}
		if (action != null && action.equals("com.neteast.clouddisk.activity.share")) {
			args = getIntent().getExtras();
			if (args != null) {
				String movieid = args.getString("movieid");
				int index = movieid.indexOf(":");
				int index1 = movieid.lastIndexOf("");
				System.out.println("movieid = " + movieid);
				String resid = movieid.substring(0, index);
				String id;
				String type;
				if (index1 > 0 && index1 != index) {
					id = movieid.substring(index + 1, index1);
					type = movieid.substring(index1 + 1, movieid.length());
				} else {
					id = movieid.substring(index + 1, movieid.length());
					type = "1";
				}
				info.setType(type);
				info.setResid(resid);
				info.setId(id);
				info.setName(args.getString("moviename"));
				info.setUrl(args.getString("url"));
				info.setImage(args.getString("picUrl"));
				System.out.println("id = " + info.getId() + "movieid +" + info.getResid() + "picUrl" + info.getImage());
			}
		}
		Intent intent = new Intent(this, RecommendActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		if (args != null) {
			intent.putExtra("dataInfo", (Serializable) info);
		}		
		if ("com.neteast.clouddisk.activity.music.widget".equals(getIntent().getAction())) {
			intent.setAction("com.neteast.clouddisk.activity.music.widget");
			View view = getLocalActivityManager().startActivity("recommend", intent).getDecorView();
			ctx = view.getContext();
			view.setLayoutParams(param);
			container.addView(view);
			curActivity = "recommend";
		} else if ("com.neteast.clouddisk.activity.picture.widget".equals(getIntent().getAction())) {
			intent.setAction("com.neteast.clouddisk.activity.picture.widget");
			View view = getLocalActivityManager().startActivity("recommend", intent).getDecorView();
			ctx = view.getContext();
			view.setLayoutParams(param);
			container.addView(view);
			curActivity = "recommend";
		} else {
			View view = getLocalActivityManager().startActivity("recommend", intent).getDecorView();
			ctx = view.getContext();
			view.setLayoutParams(param);
			container.addView(view);
			curActivity = "recommend";
		}
		
		startService(new Intent(this, UploadService.class));
		
	}
	public int getSelectIndex(){
		return index;
	}
	public void startsearchactivity(View view) {
		Intent intent = new Intent(this, SearchActivity.class);
		intent.putExtra("searchType",searchType);
		if(searchType==1){
			MyUploadActivity context = (MyUploadActivity)ctx;
			index = context.getSelectIndex();
			System.out.println("index = " + index);
		}else if(searchType ==2){
			MyDownloadActivity context = (MyDownloadActivity)ctx;
			index = context.getSelectIndex();
			System.out.println("index = " + index);
		}else if(searchType == 3){
			RecommendActivity context = (RecommendActivity)ctx;
			index = context.getSelectIndex();
			tag =context.getSelectTag();
			String tagstr = context.getSelectTagStr();
			intent.putExtra("childtype", tagstr);
			System.out.println("search recommend tag = " + tag + "tagStr = " + tagstr + "index = " + index);
		}
		if(index == 6) return ;
		intent.putExtra("selectIndex", index);
		startActivityForResult(intent, 1);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == 1) {
			
			if(searchType ==3){
				RecommendActivity context = (RecommendActivity)ctx;
				context.startSearchResult(tag,data);
			}else{
				Intent intent = new Intent(this, SearchResultActivity1.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("result", data.getSerializableExtra("result"));
				intent.putExtra("key", data.getCharSequenceExtra("key"));
				intent.putExtra("searchType", searchType);
				View v = container.getChildAt(0);
				RelativeLayout childContainer =null;
				if(searchType==1){
					MyUploadActivity context = (MyUploadActivity)v.getContext();
					
					childContainer = context.getContainer();
					
					childContainer.removeAllViews(); 
					View view = context.getLocalActivityManager()
							.startActivity("searcherres", intent).getDecorView();
					final LayoutParams param = new LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
					view.setLayoutParams(param);
					childContainer.addView(view);
					
				}else{
					MyDownloadActivity context = (MyDownloadActivity)v.getContext();
					childContainer = context.getContainer();
					
					childContainer.removeAllViews(); 
					View view = context.getLocalActivityManager()
							.startActivity("searcherres", intent).getDecorView();
					final LayoutParams param = new LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
					view.setLayoutParams(param);
					childContainer.addView(view);
				}
			}
		}

	}
	@Override
	public void onDestroy() {
		if (mToken != null) {
			try {
				if (mService.isPlaying()) {
					mService.stop();
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			unregisterReceiver(mStatusListener);
			MusicUtils.unbindFromService(mToken);
			myHandler.removeMessages(PROGRESS_CHANGED);
		}
		if (userid != 0) {
			DataAcqusition.useCloudBank(DataAcqusition.LOGOUT);
		} 
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			System.out.println("MainActivity on KeyBACK");
			 ActivityGroup ag =  (ActivityGroup)this.getCurrentActivity();
			 Activity a = ag.getCurrentActivity();
			 if(a!=null){
				 boolean rel = a.onKeyDown(keyCode, event);
				 if(!rel){
					 ExitHandler.exitApp(this);
				 }
				 return true;
			 }
			 return false;
		}else{ 
			return super.onKeyDown(keyCode, event); 
		}
	} 
	
    private ServiceConnection osc = new ServiceConnection() {
        public void onServiceConnected(ComponentName classname, IBinder obj) {
            mService = IMediaPlaybackService.Stub.asInterface(obj);
            try {
				if(mService!=null && mService.isPlaying()){
					String name="" ;
					String imageurl="";
					name = mService.getName();
					imageurl =mService.getImage();
					setName(name,imageurl);
					musicplayerView.setVisibility(View.VISIBLE);
					musicPlayButton.setImageResource(R.drawable.pausebutton);
					seekBar.setMax((int) mService.duration());
	            	myHandler.sendEmptyMessage(PROGRESS_CHANGED);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
            
        }
        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };
    private BroadcastReceiver mStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String name="" ;
			String imageurl="";
            System.out.println(" music player mStatusListener action : " + action);
            String temp = "mStatusListener Action" + action;
            DataHelpter.printLog(MainActivity.this, temp);
            if (action.equals(MediaPlaybackService.META_CHANGED)) {
            	try {
					name = mService.getName();
					imageurl =mService.getImage();
					
					seekBar.setProgress(0);
	            	musicplayerView.setVisibility(View.VISIBLE);
	            	musicPlayButton.setImageResource(R.drawable.playerbutton);
	            	musicPlayButton.setClickable(false);
	            	myHandler.removeMessages(PROGRESS_CHANGED);
	            	myHandler.removeMessages(CLOSE_MUSIC_PLAYER);
	            	
		            setName(name,imageurl);
				} catch (RemoteException e) {
					DataHelpter.printLog(MainActivity.this,e);
					e.printStackTrace();
				}
				
             
                
            } else if (action.equals(MediaPlaybackService.PLAYBACK_COMPLETE)) {
            	/*
                if (mOneShot) {
                    finish();
                } else {
                    setPauseButtonImage();
                }
                */
            } else if (action.equals(MediaPlaybackService.PLAYSTATE_CHANGED)) {
            	try {
            		if(mService.isPlaying()){
            			name = mService.getName();
            			imageurl =mService.getImage();
            		    setPauseButtonImage();
                        setName(name,imageurl);
            		}
				} catch (RemoteException e) {
					DataHelpter.printLog(MainActivity.this,e);
					e.printStackTrace();
				}
            
            }else if(action.equals(MediaPlaybackService.ASYNC_OPEN_COMPLETE)){
            	try {
					mService.play();
					System.out.println("ASYNC_OPEN_COMPLETE mService isPlaying = " + mService.isPlaying());
					if(mService.isPlaying()){
            			name = mService.getName();
            			imageurl =mService.getImage();
            			setPauseButtonImage();
                        setName(name,imageurl);
            		}
				} catch (RemoteException e) {
					DataHelpter.printLog(MainActivity.this,e);
					e.printStackTrace();
				}
            }
        }
    };
    private void setName(String name,String imageurl){
  
    	TextView nameview = (TextView) findViewById(R.id.musicName);
		nameview.setText(name);
    }
    private void setPauseButtonImage() {
        try {
        	System.out.println("mService is Playing = " + mService.isPlaying());
            if (mService != null && mService.isPlaying()) {
            	musicplayerView.setVisibility(View.VISIBLE);
            	musicPlayButton.setImageResource(R.drawable.pausebutton);
            	musicPlayButton.setClickable(true);
            	seekBar.setMax((int) mService.duration());
            	myHandler.sendEmptyMessage(PROGRESS_CHANGED);
            	myHandler.removeMessages(CLOSE_MUSIC_PLAYER);
            } else {
            	seekBar.setProgress(0);
            	musicplayerView.setVisibility(View.VISIBLE);
            	musicPlayButton.setImageResource(R.drawable.playerbutton);
            	musicPlayButton.setClickable(false);
            	myHandler.removeMessages(PROGRESS_CHANGED);
            	myHandler.removeMessages(CLOSE_MUSIC_PLAYER);
            }
        } catch (RemoteException ex) {
        }
    }
	private final static int PROGRESS_CHANGED = 0; 
	private final static int CLOSE_MUSIC_PLAYER = 1;
    Handler myHandler = new Handler(){
    
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			
				case PROGRESS_CHANGED:
					onProgressChanged();
					break;
				case CLOSE_MUSIC_PLAYER:
					closeMusicPlayer();
					break;
				default:
					break;
			}
			
			super.handleMessage(msg);
		}	
    };
    private void closeMusicPlayer(){
    	try {
			if(mService.isPlaying()){
				mService.stop();
				
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		musicplayerView.setVisibility(View.GONE);
    }
    private void onProgressChanged(){
    	int i=0;
		try {
			if(mService!=null && mService.isPlaying()){
				i = (int) mService.position();
			}
			if(seekBar!=null && mService.isPlaying()){
				seekBar.setProgress(i);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		if(myHandler!=null)
			myHandler.sendEmptyMessageDelayed(PROGRESS_CHANGED, 100);
    }
	public void startSettingActivity(View v) {
		Intent intent = new Intent(this, SettingActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		container.removeAllViews();
		View view = this.getLocalActivityManager()
				.startActivity("setting", intent).getDecorView();
		final LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		view.setLayoutParams(param);
		container.addView(view);
	}

	public Context getCtx() {
		return ctx;
	}

	public void setCtx(Context ctx) {
		this.ctx = ctx;
	}
	public RelativeLayout getContainer() {
		return container;
	}
	public void setContainer(RelativeLayout container) {
		this.container = container;
	}
	private boolean isUpdate() {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> urlmap = new HashMap<String, Object>();
		String versionName = null;

		InputStream inStream = null;
		/*read url from local config*/
		try { 
			 File file = new File("/sdcard/clouddisk/upgrade/upgrade.xml");
			 if(file.exists()){
				 inStream = new FileInputStream("/sdcard/clouddisk/upgrade/upgrade.xml");
				 Log.d("MainActivity","open InputStream !!!!!!!!!!!!");
				 if(inStream!=null){
					 SAXParserFactory spf = SAXParserFactory.newInstance(); 
					 SAXParser saxParser = spf.newSAXParser(); //创建解析器 
					 //设置解析器的相关特性，http://xml.org/sax/features/namespaces = true 表示开启命名空间特性   
					 // saxParser.setProperty("http://xml.org/sax/features/namespaces",true); 
					 MyConfigHandler handler = new MyConfigHandler(urlmap); 
					 saxParser.parse(inStream, handler); 
					 inStream.close(); 
					 versionUrl = (String) urlmap.get("versionurl");
					 apkUrl = (String) urlmap.get("apkurl");
					 Log.d("MainActivity","versionurl = " + versionUrl + "apkUrl" + apkUrl);
				 }else{
					 Log.d("MainActivity","open InputStream is NULL");
				 }
			 }
			 
		} catch (Exception e) { 
			 e.printStackTrace(); 

		} 
		
		int vercode=-1;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			XMLReader reader = factory.newSAXParser().getXMLReader();

			MyHandler handler = new MyHandler(map);//创建事件处理器
			reader.setContentHandler(handler);
			reader.parse(versionUrl);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
		for(int i=0;i<packages.size();i++) {         
			PackageInfo packageInfo = packages.get(i);     
			if(packageInfo.packageName.equals("com.neteast.clouddisk")){
				//versionName = packageInfo.versionName;
				mCurrentVerName = packageInfo.versionName;
				mCurrentVerCode = packageInfo.versionCode;
				vercode = packageInfo.versionCode;
				break;
			}
		}
		System.out.println("ver:"+vercode+"sver:"+map.get("versionCode"));
		System.out.println("vername:"+ map.get("versionName"));
		verCode = (String) map.get("versionCode");
		verName = (String) map.get("versionName");
		verInfo = (String) map.get("versionInfo");
		String strver=(String)map.get("versionCode");
		int sver=-1;
		if(map.get("versionCode")!=null)
			sver= Integer.parseInt(strver);
		if(sver > vercode){
			
			AlertDialog dialog = new AlertDialog.Builder(this).create();
			WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
			params.width = 200;
			params.height = WindowManager.LayoutParams.WRAP_CONTENT;
			dialog.getWindow().setAttributes(params);
			dialog.setTitle("升级");
			dialog.setMessage("有新的版本，要升级吗？");
			dialog.setButton("升级", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					DownloadOSTask dlos = new DownloadOSTask();
					dlos.execute(apkUrl);
				}
			});
			dialog.setButton2("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			dialog.show();
			
			return true;
		}
		return false;
	}
	
	class DownloadOSTask extends AsyncTask<String, Integer, String>{

		ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = new ProgressDialog(MainActivity.this);
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setCancelable(false);
			progress.setMessage("下载中...");
			
			progress.getWindow().setLayout(200, LayoutParams.WRAP_CONTENT);
			progress.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			InputStream in = null;
			RandomAccessFile file = null;
			String fileName = null;
			try {
				URL url = new URL(params[0]);
				HttpURLConnection conn = (HttpURLConnection) url
				.openConnection();
				conn.setConnectTimeout(5 * 1000);
				conn.setRequestProperty("Accept-Language",
						Params.ACCEPT_LANGUAGE);
				conn.setRequestProperty("Charset", Params.ChARSET);
				conn.setRequestProperty("Connetion", "Keep-Alive");
				conn.setRequestMethod("GET");
				conn.setRequestProperty("User-Agent", "NetFox");
				in = conn.getInputStream();
				int totalLength = conn.getContentLength();
				int urlIndex = url.toString().lastIndexOf("/");
				
				File f = new File(Params.DOWNLOAD_FILE_PATH);
				if (!f.exists()) {
					f.mkdir();
				}
				
				fileName = url.toString().substring(urlIndex + 1);
				file = new RandomAccessFile(
						Params.DOWNLOAD_FILE_PATH + fileName, "rw");
				byte[] buffer = new byte[20 * 1024];
				int len = -1;
				int count = 0;
				while ((len = in.read(buffer)) != -1) {
					file.write(buffer, 0, len);
					count += len;
					publishProgress((int) ((float)count / totalLength * 100));
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
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			progress.setMessage("下载中" + values[0] + "%");
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			File file = new File(Params.DOWNLOAD_FILE_PATH + result);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse("file://" + file.toString()),
			"application/vnd.android.package-archive");
			progress.dismiss();
			startActivityForResult(intent, 0);
		}
	}
	
	private void showWIFiDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle(R.string.system_tips);
		builder.setMessage(R.string.msg_network_nowifi);
		builder.setPositiveButton(R.string.net_setup,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						MainActivity.this
								.startActivity(new Intent(
										android.provider.Settings.ACTION_WIRELESS_SETTINGS));
					}
				});
		builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
				MainActivity.this.finish();
				System.exit(0);
			}
		});
		builder.show();
	}
	
	private void showInvalidDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle(R.string.system_tips);
		builder.setMessage(R.string.invalid_user_message);
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
						MainActivity.this.finish();
						System.exit(0);
					}
		});
		
		builder.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		
		MenuItem helpItem = menu.add(Menu.NONE, Menu.FIRST+1, 1, R.string.online_help).setIcon(android.R.drawable.ic_menu_help);
		MenuItem feedbackItem = menu.add(Menu.NONE, Menu.FIRST+2, 2, R.string.online_feedback).setIcon(R.drawable.ic_menu_compose);
		MenuItem spaceItem = menu.add(Menu.NONE, Menu.FIRST+3, 3, R.string.space_upgrade).setIcon(android.R.drawable.ic_menu_view);
		MenuItem clearItem = menu.add(Menu.NONE, Menu.FIRST+4, 4, R.string.clear_cache).setIcon(android.R.drawable.ic_menu_delete);
		MenuItem aboutItem = menu.add(Menu.NONE, Menu.FIRST+5, 5, R.string.about).setIcon(android.R.drawable.ic_menu_info_details);
		MenuItem upgradeItem = menu.add(Menu.NONE, Menu.FIRST+6, 6, R.string.version_upgrade).setIcon(R.drawable.ic_menu_refresh);
		
		helpItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				
				try {
					getPackageManager().getPackageInfo("com.neteast.oh", 0);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					UIHelper.displayToast(getResources().getString(R.string.online_help_notinstall),MainActivity.this);
					return true;
				}
				Intent help=new Intent();
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
					//e.printStackTrace();
					UIHelper.displayToast(getResources().getString(R.string.online_feedback_notinstall),MainActivity.this);
					return true;
				}
				Intent feedback = new Intent();
				feedback.setComponent(new ComponentName("com.wasu.feedback", "com.wasu.feedback.FeedbackActivity"));
				feedback.putExtra("appcode", "10009");//自己应用的appcode
				startActivity(feedback);
				return true;
			}
		});
		spaceItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				
				return true;
			}
		});
		clearItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				FileUtil fu = FileUtil.getInstance(MainActivity.this);
				fu.deleteAll();
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
				
				return true;
			}
		});
		
		return super.onCreateOptionsMenu(menu);
	}
	
	private void getAppVersion(){
		int vercode=-1;
		String strvername = null;
		List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
		for(int i=0;i<packages.size();i++) {         
			PackageInfo packageInfo = packages.get(i);     
			if(packageInfo.packageName.equals("com.neteast.clouddisk")){
				vercode = packageInfo.versionCode;
				strvername =packageInfo.versionName;
				break;
			}
		}
		((DownLoadApplication)getApplication()).setVersion(strvername);	
	}
	
    public void listversion(int flag){
    	AlertDialog dialog = new AlertDialog.Builder(this).create();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = 200;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(params);
		dialog.setTitle("关于");
		if(flag ==1)
			dialog.setMessage("当前版本:"+((DownLoadApplication)getApplication()).getVersion());
		else
			dialog.setMessage("1当前版本:"+((DownLoadApplication)getApplication()).getVersion());
		dialog.setButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		dialog.show();
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
    		isUserInvalid(result);
    	}	
    }	
	
}