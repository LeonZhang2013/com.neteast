package com.neteast.clouddisk.activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue.IdleHandler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.adapter.SeriesAdapter;
import com.neteast.clouddisk.model.Info;
import com.neteast.clouddisk.model.MovieInfo;
import com.neteast.clouddisk.videoview.VideoView;
import com.neteast.clouddisk.videoview.VideoView.MySizeChangeLinstener;


public class VideoPlaybackActivity extends Activity {
	
	private final static String TAG = "VideoPlayerActivity";
	
	public static LinkedList<MovieInfo> playList = new LinkedList<MovieInfo>();
	private final String SHARE_SERVER_TAG = "MAP_SHARE_SERVER_TAG";
	private String SHARE_SERVER_ADDRESS = "MAP_SERVER_ADDRESS";
	
	private final String DLNA_FILE_NAME = "/sdcard/pic/dlna.png";
	private final String DLNA_FILE_NAME1 = "/sdcard/pic/dlna1.png";
	
	private String dlnafilename = DLNA_FILE_NAME;
	
	private final static int PLAYING_VIDEO = 0;
	private final static int PLAYING_MUSIC = 1;
	private final static int PLAYING_PHOTO = 2;
	private final static int PLAYING_DMC = 3;
	private final static int PLAYING_DMR_VIDEO = 4;
	private final static int PLAYING_DMR_MUSIC = 5;
	private final static int PLAYING_DMR_PHOTO = 6;
	
	private int mPlayingType = PLAYING_VIDEO;
	private Uri videoListUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	private static int position ;
	private static boolean backFromAD = false;
	private int playedTime;
	private int mDuration =0;
	
	private VideoView vv = null;
	private SeekBar seekBar = null;  
	private SeekBar soundSeekBar =null;
	private TextView durationTextView = null;
	private TextView playedTextView = null;
	private GestureDetector mGestureDetector = null;
	private AudioManager mAudioManager = null;  
	
	private LinearLayout mVideoLoading = null;
	
	private int maxVolume = 0;
	private int currentVolume = 0;  
	
	private ImageButton bnhd = null;
	private ImageButton bnprev = null;
	private ImageButton bnbackward = null;
	private ImageButton bnpause = null;
	private ImageButton bnforward = null;
	private ImageButton bnnext = null;
	private ImageButton bnsound = null;
	private Button bnback = null;
	private Button bnseries = null;
	
	private Button bnsuperdflag = null;
	private Button bnhdflag =null;
	private Button bnsdflag =null;
	
	private TextView mtitle =null;
	
	private ImageButton dlanbn = null;
	private ImageButton screenshortbt = null;
	private View titleView = null;
	private View controlView = null;
//	private View shareView = null;
	private View hdvideoView =null;
	private View seriesView = null;
	private PopupWindow controler = null;
	private PopupWindow titlecontroler = null;
//	private PopupWindow sharecontroler = null;
	private PopupWindow sourcecontroler = null;
	private PopupWindow seriescontroler=null;
	
	private TextView superdView = null;
	private TextView hdView = null;
	private TextView sdView = null;
			
	
	//private PopupWindow mSoundWindow = null;
	
	
	private static int screenWidth = 0;
	private static int screenHeight = 0;
	private static int controlHeight = 0;  
	
	private final static int TIME = 6868;  
	
	private boolean isControllerShow = true;
	private boolean isPaused = false;
	private boolean isFullScreen = true;
	private boolean isSilent = false;
	private boolean isSoundShow = false;
	private boolean isSourceShow = false;
	private boolean isSeriesShow = false;
	
	
	private String playingURL=null;
	private String playingName=null;
	
	private  ListView playSeriesList=null;
	private SeriesAdapter myseriesadapter = null;
	private int xjsel=0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);  
       // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.player);
        
        Looper.myQueue().addIdleHandler(new IdleHandler(){

			@Override
			public boolean queueIdle() {
				// TODO Auto-generated method stub
				if(controler != null && vv.isShown()){
					
					titlecontroler.showAtLocation(vv, Gravity.TOP,0, 0);
					titlecontroler.update(0, 0, screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
					
					controler.showAtLocation(vv, Gravity.BOTTOM, 0, 0);
					controler.update(0, 0, screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
					
					//sharecontroler.showAtLocation(vv, Gravity.RIGHT, 0, 0);
					//sharecontroler.update(0, 0, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					
					sourcecontroler.showAtLocation(vv, Gravity.LEFT,60, screenHeight-40);
 					
					//seriescontroler.showAtLocation(vv, Gravity.RIGHT, 0, 0);
					//seriescontroler.update(0, 0, 344, 646);
					
				}
				hideController();
				//myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
				return false;  
			}
        });
        
        titleView = getLayoutInflater().inflate(R.layout.player_title, null);
        controlView = getLayoutInflater().inflate(R.layout.player_controler, null);
        //shareView = getLayoutInflater().inflate(R.layout.player_share, null);
        hdvideoView = getLayoutInflater().inflate(R.layout.player_source, null);
        seriesView = getLayoutInflater().inflate(R.layout.player_series, null);
        controler = new PopupWindow(controlView);
        titlecontroler = new PopupWindow(titleView);
        //sharecontroler = new PopupWindow(shareView);
        sourcecontroler = new PopupWindow(hdvideoView);
        seriescontroler = new PopupWindow(seriesView);
        
        
        seriescontroler.setFocusable(true);
        
        seriescontroler.setOutsideTouchable(true);
        seriescontroler.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句�?
        
        durationTextView = (TextView) controlView.findViewById(R.id.video_duration);
        playedTextView = (TextView) controlView.findViewById(R.id.video_has_played);
        
        superdView = (TextView) hdvideoView.findViewById(R.id.video_super);
        hdView = (TextView) hdvideoView.findViewById(R.id.video_hd);
        sdView = (TextView) hdvideoView.findViewById(R.id.video_sd);
        bnsuperdflag = (Button)hdvideoView.findViewById(R.id.video_super_sel);
        bnhdflag = (Button)hdvideoView.findViewById(R.id.video_hd_sel);
        bnsdflag = (Button)hdvideoView.findViewById(R.id.video_sd_sel);
        bnsuperdflag.setVisibility(View.INVISIBLE);
        bnsdflag.setVisibility(View.INVISIBLE);
        
        
        position = -1;
        
        bnhd = (ImageButton) controlView.findViewById(R.id.hdvideo);
        bnprev = (ImageButton) controlView.findViewById(R.id.video_prev);
        bnbackward = (ImageButton) controlView.findViewById(R.id.video_backward);
        bnpause = (ImageButton) controlView.findViewById(R.id.video_pause);
        bnforward = (ImageButton) controlView.findViewById(R.id.video_forward);
        bnnext = (ImageButton) controlView.findViewById(R.id.video_next);
        bnsound = (ImageButton) controlView.findViewById(R.id.video_sound);
        
        bnback = (Button) titleView.findViewById(R.id.video_back);
        mtitle = (TextView) titleView.findViewById(R.id.video_title);
        bnseries = (Button)titleView.findViewById(R.id.player_series_bn);
        
        mVideoLoading = (LinearLayout)findViewById(R.id.video_loading);
        
        initserieslist();
        
        superdView.setOnClickListener(new OnClickListener(){
 			@Override
 			public void onClick(View v) {
 				// TODO Auto-generated method stub
 				 bnsuperdflag.setVisibility(View.VISIBLE);
 				 bnhdflag.setVisibility(View.INVISIBLE);
 				 bnsdflag.setVisibility(View.INVISIBLE);
 				 bnhd.setImageResource(R.drawable.superd_source);
 			}
        });
        hdView.setOnClickListener(new OnClickListener(){
 			@Override
 			public void onClick(View v) {
 				// TODO Auto-generated method stub
 				 bnsuperdflag.setVisibility(View.INVISIBLE);
 				 bnhdflag.setVisibility(View.VISIBLE);
 				 bnsdflag.setVisibility(View.INVISIBLE);
 				 bnhd.setImageResource(R.drawable.hd_source);
 			}
        });
        sdView.setOnClickListener(new OnClickListener(){
 			@Override
 			public void onClick(View v) {
 				// TODO Auto-generated method stub
 				 bnsuperdflag.setVisibility(View.INVISIBLE);
 				 bnhdflag.setVisibility(View.INVISIBLE);
 				 bnsdflag.setVisibility(View.VISIBLE);
 				 bnhd.setImageResource(R.drawable.sd_source);
 			}
        });
        
        bnhd.setVisibility(View.INVISIBLE);
        bnhd.setOnClickListener(new OnClickListener(){
 			@Override
 			public void onClick(View v) {
 				// TODO Auto-generated method stub
 				if(!isSourceShow){
 					sourcecontroler.update(60, screenHeight-40, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
 					isSourceShow = true;
 				}else{
 					sourcecontroler.update(0,0,0,0);
 					isSourceShow = false;
 				}
 			}
        });
 
       bnback.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(mPlayingType == PLAYING_VIDEO || mPlayingType == PLAYING_MUSIC ){
					vv.stopPlayback();
					VideoPlaybackActivity.this.finish();
					//hideControllerDelay();
				}else if(mPlayingType == PLAYING_DMR_VIDEO || mPlayingType == PLAYING_DMR_MUSIC){
					vv.stopPlayback();
					
				}
				else if(mPlayingType == PLAYING_DMC) {
					
					//mPlayingType = PLAYING_VIDEO;
					if(playingURL!=null){
						PlayingMedia(playingURL,playingName,PLAYING_VIDEO);
						vv.seekTo(playedTime);
						vv.start();
					}
				}
			}
       });
       bnseries.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					//if(!isSeriesShow)
						showSeries();
					//else hideSeries();
				
			}

       });
       
       bnprev.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(position -1 >=0){
					//vv.setVideoPath(playList.get(position).path);
					position--;
					String path = playList.get(position).path;
					String title = playList.get(position).displayName;
					if(mPlayingType ==PLAYING_DMC){
						
						PlayingMedia(path,title,PLAYING_DMC);
						//mPlayingType = PLAYING_DMC;
						//playingURL = path;
						//playingName = title;
					}else{
						PlayingMedia(path,title,PLAYING_VIDEO);
					}
					cancelDelayHide();
					hideControllerDelay();
				}else{
					//VideoPlayerActivity.this.finish();
				}
			}
       	
       });
       
       bnnext.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int n = playList.size();
				if(position+1 < n){
					//vv.setVideoPath(playList.get(position).path);
					position ++ ;
					String path = playList.get(position).path;
					String title = playList.get(position).displayName;			
					if(mPlayingType ==PLAYING_DMC){
						
						PlayingMedia(path,title,PLAYING_DMC);
						//mPlayingType = PLAYING_DMC;
						//playingURL = path;
						//playingName = title;
					}else{
						PlayingMedia(path,title,PLAYING_VIDEO);
					}
					
					cancelDelayHide();
					hideControllerDelay();
				}else{
					//VideoPlayerActivity.this.finish();
				}
			}
       	
       });
        
        vv = (VideoView) findViewById(R.id.vv);
 
        Uri uri = getIntent().getData();
        if(uri!=null){
        	if(vv.getVideoHeight()==0){
        		playingURL = uri.toString();
        		Intent i = getIntent();
        		position = i.getIntExtra("position",position);
        		String title = i.getStringExtra("title");
        		/*
        		playList = MediaManager.videoList;
        		if (title==null) title = playList.get(position).displayName;
        		*/
        		System.out.println("videoPlayback url=" + playingURL);
        		PlayingMedia(playingURL,title,PLAYING_VIDEO);
        		
        	}
        	bnpause.setImageResource(R.drawable.pause);
        	mVideoLoading.setVisibility(View.VISIBLE);
        }else{
        	bnpause.setImageResource(R.drawable.play);
        }
 
        vv.setMySizeChangeLinstener(new MySizeChangeLinstener(){

			@Override
			public void doMyThings() {
				// TODO Auto-generated method stub
				setVideoScale(SCREEN_DEFAULT);
			}
        	
        });
             
        bnbackward.setAlpha(0xBB);  
        bnpause.setAlpha(0xBB);
        bnforward.setAlpha(0xBB);
        bnprev.setVisibility(View.INVISIBLE);
        bnnext.setVisibility(View.INVISIBLE);
        
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        bnsound.setAlpha(findAlphaFromSound());
        
        
        bnforward.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int pos = vv.getCurrentPosition();
				vv.seekTo(pos + 15000);
			}
        	
        });
        
        bnpause.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PauseMedia();
				/*
				cancelDelayHide();
				if(isPaused){
					vv.start();
					bnpause.setImageResource(R.drawable.pause);
					hideControllerDelay();
				}else{
					vv.pause();
					bnpause.setImageResource(R.drawable.play);
				}
				isPaused = !isPaused;
				*/
			}
        	
        });
        
        bnbackward.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int pos = vv.getCurrentPosition();
				vv.seekTo(pos - 5000);

			}
        	
        });
        
        bnsound.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(isSilent){
				bnsound.setImageResource(R.drawable.soundenable);
			}else{
				bnsound.setImageResource(R.drawable.sounddisable);
			}
			isSilent = !isSilent;
			updateVolume(currentVolume);
			cancelDelayHide();
			hideControllerDelay();
		}   
       });
        
        bnsound.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				if(isSilent){
					bnsound.setImageResource(R.drawable.soundenable);
				}else{
					bnsound.setImageResource(R.drawable.sounddisable);
				}
				isSilent = !isSilent;
				updateVolume(currentVolume);
				cancelDelayHide();
				hideControllerDelay();
				return true;
			}
        	
        });
        
        System.out.println("VideoPlaybackActivity currentVolume:" + currentVolume);
        soundSeekBar = (SeekBar) controlView.findViewById(R.id.video_sound_bar);
        soundSeekBar.setProgress(currentVolume);
        soundSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
        	@Override
 			public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
 			// TODO Auto-generated method stub
 					
 				if(fromUser){
 					cancelDelayHide();
 					updateVolume(progress);
 					hideControllerDelay();
 				}
 					
 			}
 	
 			@Override
 			public void onStartTrackingTouch(SeekBar arg0) {
 					// TODO Auto-generated method stub
 				myHandler.removeMessages(HIDE_CONTROLER);
 			}
 	
 			@Override
 			public void onStopTrackingTouch(SeekBar seekBar) {
 					// TODO Auto-generated method stub
 				myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
 			}
         });
        
        seekBar = (SeekBar) controlView.findViewById(R.id.video_seekbar);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
				@Override
				public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
					// TODO Auto-generated method stub
					
					if(fromUser){
						if(mPlayingType == PLAYING_DMC){
						}
						vv.seekTo(progress);
					}
					
				}
	
				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub
					myHandler.removeMessages(HIDE_CONTROLER);
				}
	
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
				}
        	});
        
        getScreenSize();
       
        mGestureDetector = new GestureDetector(new SimpleOnGestureListener(){

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				// TODO Auto-generated method stub
				if(isFullScreen){
					setVideoScale(SCREEN_DEFAULT);
				}else{
					setVideoScale(SCREEN_FULL);
				}
				isFullScreen = !isFullScreen;
				Log.d(TAG, "onDoubleTap");
				
				if(isControllerShow){
					showController();
				}
				//return super.onDoubleTap(e);
				return true;
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				// TODO Auto-generated method stub
				if(!isControllerShow){
					showController();
					hideControllerDelay();
				}else {
					cancelDelayHide();
					hideController();
				}
				//return super.onSingleTapConfirmed(e);
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub
				/*
				if(isPaused){
					vv.start();
					bnpause.setImageResource(R.drawable.pause);
					cancelDelayHide();
					hideControllerDelay();
				}else{
					vv.pause();
					bnpause.setImageResource(R.drawable.play);
					cancelDelayHide();
					showController();
				}
				isPaused = !isPaused;
				*/
				//super.onLongPress(e);
			}	
        });
                
        vv.setOnPreparedListener(new OnPreparedListener(){

				@Override
				public void onPrepared(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					mVideoLoading.setVisibility(View.GONE);
					setVideoScale(SCREEN_FULL);
					isFullScreen = true; 
					if(!isControllerShow){
						showController();  
					}
					
					int i = vv.getDuration();
					mDuration = i;
					Log.d("setOnPreparedListener", "duration "+i);
					seekBar.setMax(i);
					i/=1000;
					int minute = i/60;
					int hour = minute/60;
					int second = i%60;
					minute %= 60;
					durationTextView.setText(String.format("%02d:%02d:%02d", hour,minute,second));
					
					/*controler.showAtLocation(vv, Gravity.BOTTOM, 0, 0);
					controler.update(screenWidth, controlHeight);
					myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);*/
					
					vv.start();  
					updateVolume(currentVolume);
					Log.d("ononPrepared", " mPlayingType = " + mPlayingType + "currentVolume = " + currentVolume);
					
					bnpause.setImageResource(R.drawable.pause);
					hideControllerDelay();
					myHandler.sendEmptyMessage(PROGRESS_CHANGED);
				}	
	        });
        
        vv.setOnCompletionListener(new OnCompletionListener(){

				@Override
				public void onCompletion(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					/*
					int n = playList.size();
					Log.d("onCompletion", " playList.size() = " + n + "position =" + position);
					if(++position < n){
						PlayingMedia(playList.get(position).path,playList.get(position).displayName,PLAYING_VIDEO);
						//vv.setVideoPath(playList.get(position).path);
					}else{
						//VideoPlayerActivity.this.finish();
					}
					*/
				}
        	});
        
        vv.setOnErrorListener(new OnErrorListener(){

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				Log.d("onError", "Video Player erorr !!");
				vv.stopPlayback();
				hideController();
				mVideoLoading.setVisibility(View.GONE);
				int index = playingURL.lastIndexOf(".");
				String extName = playingURL.substring(index+1);
				String message="";
				System.out.println("extName = " + extName);
				if(extName.equalsIgnoreCase("mp4")||extName.equalsIgnoreCase("3gp")||extName.equalsIgnoreCase("ts")){
					message = getResources().getString(R.string.video_play_title);
				}else{
					
					message = getResources().getString(R.string.video_play_unsupport);
				}
				VideoFailedDialog(VideoPlaybackActivity.this,message);
				return true;
			}
        	
        });
        
        
       /* 
        vv.setOnBufferingUpdateListener(new OnBufferingUpdateListener(){
        	@Override
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				// TODO Auto-generated method stub
        		Log.d("onBufferingUpdate", "percent:" + percent + "isPlaying :" + vv.isPlaying());
			}
        });
       */ 
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
    	if(requestCode==0&&resultCode==Activity.RESULT_OK){
    		int result = data.getIntExtra("CHOOSE", -1);
    		if(result!=-1){
    			position = result;
    		}
    		
    		return ;
    	}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private final static int PROGRESS_CHANGED = 0;
    private final static int HIDE_CONTROLER = 1;
    private final static int DLNA_CONTROLER = 2;
    private final static int PLAYER_VIDEO_ERROR=3;
    
    Handler myHandler = new Handler(){
    
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			switch(msg.what){
			
				case PROGRESS_CHANGED:
					int i=0;
					if(mPlayingType==PLAYING_DMC){
							if(vv.isPlaying()){
								vv.pause();
							}
						
						playedTime = i;
					}else if(mPlayingType==PLAYING_DMR_VIDEO){
						i = vv.getCurrentPosition();
						
						playedTime = i;
					}else{
						i = vv.getCurrentPosition();
					}
					seekBar.setProgress(i);
					
					i/=1000;
					int minute = i/60;
					int hour = minute/60;
					int second = i%60;
					minute %= 60;
					playedTextView.setText(String.format("%02d:%02d:%02d", hour,minute,second));
					if(mPlayingType!=PLAYING_DMC){
						sendEmptyMessageDelayed(PROGRESS_CHANGED, 1000);
						//sendEmptyMessage(PROGRESS_CHANGED);
					}
					break;
					
				case HIDE_CONTROLER:
					hideController();
					break;
				case DLNA_CONTROLER:
					if(vv.isPlaying()) vv.pause();
					mPlayingType = PLAYING_DMC;
					showController();
					break;
				case PLAYER_VIDEO_ERROR:
					//VideoFailedDialog(VideoPlaybackActivity.this);
					break;
			}
			
			super.handleMessage(msg);
		}	
    };

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		System.out.println("video Player Activity onTouchEvent !!!!!!");
		boolean result = mGestureDetector.onTouchEvent(event);
		System.out.println("video Player Activity onTouchEvent result = " + result);
		if(!result){
			if(event.getAction()==MotionEvent.ACTION_UP){
				
				/*if(!isControllerShow){
					showController();
					hideControllerDelay();
				}else {
					cancelDelayHide();
					hideController();
				}*/
			}
			result = super.onTouchEvent(event);
		}
		
		return result;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		
		getScreenSize();
		if(isControllerShow){
			
			cancelDelayHide();
			hideController();
			showController();
			hideControllerDelay();
		}
		
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		playedTime = vv.getCurrentPosition();
		vv.pause();
		bnpause.setImageResource(R.drawable.play);
		super.onPause();   
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		vv.seekTo(playedTime);
		vv.start();  
		if(vv.getVideoHeight()!=0){
			bnpause.setImageResource(R.drawable.pause);
			hideControllerDelay();
		}
		
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(controler!=null){
			Log.d(TAG, "onDestroy 2222 " );
			controler.dismiss();
			titlecontroler.dismiss();
			//sharecontroler.dismiss();
		}
		
		if(sourcecontroler.isShowing()){
			sourcecontroler.dismiss();
		}
		if(seriescontroler.isShowing()){
			seriescontroler.dismiss();
		}
		
		//if(dmc!=null) dmc.dmcServiceStop();
		//if(dmr!=null) dmr.dmrServiceStop();
		myHandler.removeMessages(PROGRESS_CHANGED);
		myHandler.removeMessages(HIDE_CONTROLER);
		
	
		super.onDestroy();
	}     

	private void getScreenSize()
	{
		Display display = getWindowManager().getDefaultDisplay();
        screenHeight = display.getHeight();
        screenWidth = display.getWidth();
        controlHeight = screenHeight;
        //controlHeight = screenHeight/4;
      
	}
	
	private void hideController(){
		if(mPlayingType != PLAYING_DMC){
			if(controler.isShowing()){
				controler.update(0,0,0, 0);
				titlecontroler.update(0,0,0,0);
				//sharecontroler.update(0, 0, 0, 0);
				sourcecontroler.update(0,0,0,0);
				isControllerShow = false;
			}
			/*
			if(mSoundWindow.isShowing()){
				mSoundWindow.dismiss();
				isSoundShow = false;
			}
			*/
		}
	}
	
	private void hideControllerDelay(){
		myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
	}
	
	private void showController(){
		
		Log.d("showController","mPlayingType =" + mPlayingType );
		
		controler.update(0,0,screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
		titlecontroler.update(0,0,screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
		//sharecontroler.update(0, 0, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		isControllerShow = true;
	
	}
	
	private void cancelDelayHide(){
		myHandler.removeMessages(HIDE_CONTROLER);
	}

    private final static int SCREEN_FULL = 0;
    private final static int SCREEN_DEFAULT = 1;
    
    private void setVideoScale(int flag){
    	
    	LayoutParams lp = vv.getLayoutParams();
    	
    	switch(flag){
    		case SCREEN_FULL:
    			
    			Log.d(TAG, "screenWidth: "+screenWidth+" screenHeight: "+screenHeight);
    			vv.setVideoScale(screenWidth, screenHeight);
    			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    			
    			break;
    			
    		case SCREEN_DEFAULT:
    			
    			int videoWidth = vv.getVideoWidth();
    			int videoHeight = vv.getVideoHeight();
    			int mWidth = screenWidth;
    			int mHeight = screenHeight - 25;
    			
    			if (videoWidth > 0 && videoHeight > 0) {
    	            if ( videoWidth * mHeight  > mWidth * videoHeight ) {
    	                //Log.i("@@@", "image too tall, correcting");
    	            	mHeight = mWidth * videoHeight / videoWidth;
    	            } else if ( videoWidth * mHeight  < mWidth * videoHeight ) {
    	                //Log.i("@@@", "image too wide, correcting");
    	            	mWidth = mHeight * videoWidth / videoHeight;
    	            } else {
    	                
    	            }
    	        }
    			
    			vv.setVideoScale(mWidth, mHeight);

    			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    			
    			break;
    	}
    }

    private int findAlphaFromSound(){
    	if(mAudioManager!=null){
    		//int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    		int alpha = currentVolume * (0xCC-0x55) / maxVolume + 0x55;
    		return alpha;
    	}else{
    		return 0xCC;
    	}
    }

    private void updateVolume(int index){
    	if(mAudioManager!=null){
    		if(isSilent){
    			if(mPlayingType == PLAYING_VIDEO || mPlayingType == PLAYING_MUSIC ){
    				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
    			}else if(mPlayingType == PLAYING_DMR_VIDEO || mPlayingType == PLAYING_DMR_MUSIC ){
    				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
    			}
    			else if(mPlayingType ==PLAYING_DMC){
    				
    			}
    		}else{
    			if(mPlayingType == PLAYING_VIDEO || mPlayingType == PLAYING_MUSIC ){
    				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
    			}else if(mPlayingType == PLAYING_DMR_VIDEO || mPlayingType == PLAYING_DMR_MUSIC ){
    				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
    			}
    			else if(mPlayingType ==PLAYING_DMC){
    				
    			}
    		}
    		currentVolume = index;
    		bnsound.setAlpha(findAlphaFromSound());
    	}
    }
    private static Activity thisact;
	private void VideoFailedDialog(Activity activity,String message) {
		thisact = activity;
	     AlertDialog.Builder builder = new Builder(activity);
	     builder.setMessage(message);
	     builder.setTitle(R.string.system_tips);
	     builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
		      @Override
			  public void onClick(DialogInterface dialog, int which) {
		    	  
			       dialog.dismiss();
			       dialog=null;
			     //  VideoPlayerActivity.this.removeDialog(id);
			       thisact.finish();
			      
		      }
	     });
	     builder.create().show();
	}
	private void PlayingMedia(String path,String title,int type){
		mVideoLoading.setVisibility(View.VISIBLE);
		vv.setVisibility(View.VISIBLE);
		mPlayingType = type;
		vv.setVideoPath(path);
			
		
		playingURL = path;
		playingName = title;
		if(mtitle!=null) mtitle.setText(title);
	}
	private void PauseMedia(){
		cancelDelayHide();
		if(isPaused){
			if(mPlayingType == PLAYING_VIDEO || mPlayingType == PLAYING_MUSIC){
				vv.start();
				hideControllerDelay();
			}else if(mPlayingType == PLAYING_DMR_VIDEO || mPlayingType == PLAYING_DMR_MUSIC){
				vv.start();
				hideControllerDelay();
			}else if(mPlayingType == PLAYING_DMC) {
				
			}
			bnpause.setImageResource(R.drawable.pause);
		}else{
			if(mPlayingType == PLAYING_VIDEO || mPlayingType == PLAYING_MUSIC){
				vv.pause();
			}else if(mPlayingType == PLAYING_DMR_VIDEO || mPlayingType == PLAYING_DMR_MUSIC){
				vv.pause();
			}else if(mPlayingType == PLAYING_DMC){
				
			}
			bnpause.setImageResource(R.drawable.play);
		}
		isPaused = !isPaused;
	}
    public void initserieslist(){
    	playSeriesList=(ListView)seriesView.findViewById(R.id.player_series);
    	List<Info> list = new ArrayList<Info>();
    	for(int i=0;i<50;i++){
    		Info info = new Info();
    		//info.setName("�?+i+"�?);
    		list.add(info);
    	}
    	myseriesadapter = new SeriesAdapter(this,list);
    	playSeriesList.setAdapter(myseriesadapter);
        xjsel = 0 ;
        myseriesadapter.setSelectItem(xjsel);
       // playSeriesList.setOnItemClickListener(listener);
    	playSeriesList.setOnItemClickListener(new  AdapterView.OnItemClickListener(){   
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {   
        		
        		Info  item = (Info ) arg0.getItemAtPosition(arg2);
        		/*
        		hideSeries();
        		if(serieslistinfo!=null)
        			setTiele(serieslistinfo.get(arg2));
        		else{
        			setTiele("");
        		}
        		*/
        		Log.d("seriesListOnItemClick","arg2 =" + arg2 );
        		xjsel = arg2+1;
        		myseriesadapter.setSelectItem(arg2);
        		myseriesadapter.notifyDataSetInvalidated();  
       
        		//String url=(String)seriesinfo.get(arg2);
     
            }   
               
        });  
    }
	private void hideSeries(){
		Log.d("hideSeries ","isSeriesShow =" + isSeriesShow );
		if(isSeriesShow){
			seriescontroler.update(0,0,0, 0);
			isSeriesShow = false;
		}
	}
	private void showSeries(){
		Log.d("showSeries ","isSeriesShow =" + isSeriesShow );
		seriescontroler.showAtLocation(vv, Gravity.RIGHT, 0, 0);
		seriescontroler.update(0, 0, 344, 646);
		
		isSeriesShow = true;
	}
}