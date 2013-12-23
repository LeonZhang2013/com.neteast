package com.neteast.cloudtv2.play;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.neteast.cloudtv2.R;

/** @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-3-26 */
public class PlayerActivity extends Activity {

	private VideoView mVideoView;
	private int mLayout = VideoView.VIDEO_LAYOUT_SCALE;
	 private GestureDetector mGestureDetector;
	 private View mTitleView;
	 //填充物
	 private View mFiller;
	 private ProgressBar mLoadingView;
	 private int rate =1024*1024*5;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.screen_layout);
		//findViewById(R.id.activity_logo).setVisibility(View.VISIBLE);
		mTitleView = findViewById(R.id.title_bar);
		mFiller = findViewById(R.id.filler);
		mLoadingView = (ProgressBar) findViewById(R.id.progressBar1);
		initData();
	}

	public void onClickBack(View view) {
		this.finish();
	}

	private void initData() {
		mVideoView = (VideoView) findViewById(R.id.surface_view);
		mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);

		String playPath = getIntent().getStringExtra("playPath");
		boolean dianbao = getIntent().getBooleanExtra("dianbo",false);
		mVideoView.setVideoPath(playPath);
		mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW);
		if(!dianbao) mVideoView.setMediaController(new MediaController(this));
		mVideoView.setBufferSize(rate);
		//VideoView.VIDEO_LAYOUT_WIDTH = 1280;
		//VideoView.VIDEO_LAYOUT_HEIGHT = 674;
		mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
		setListner();
	}
	
	
	
	public void setListner(){
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
/*		mVideoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
			@Override
			public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
				Log.i("my_medi", "缓冲监听 = "+mVideoView.getBufferPercentage());
			}
		});*/
		
		
		mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
			/** 是否需要自动恢复播放，用于自动暂停，恢复播放 */
		    private boolean needResume;
		    private int tempRate = rate;
		    @Override
		    public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
		    	 
		        switch (arg1) {
		        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
		            //开始缓存，暂停播放
		            if (mVideoView.isPlaying()) {
		            	tempRate = 0;
		            	mVideoView.pause();
		                needResume = true;
		            }
		            mLoadingView.setVisibility(View.VISIBLE);
		            break;
		        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
		            //缓存完成，继续播放
		            if (needResume)
		            	mVideoView.start();
		            mLoadingView.setVisibility(View.GONE);
		            break;
		        case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
		        	if(mVideoView.isPlaying()){
		        		if(mLoadingView.isShown()) mLoadingView.setVisibility(View.GONE);
		        	}
		            //显示 下载速度
		           // tempRate = tempRate + arg2;
		           // Log.i("download rate:" + tempRate);
		            break;
		        }
		        return true;
		    }
		});
	}
	
    private class MyGestureListener extends SimpleOnGestureListener {
        
        /** 双击 */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mLayout == VideoView.VIDEO_LAYOUT_SCALE){
                mLayout = VideoView.VIDEO_LAYOUT_STRETCH;
                mTitleView.setVisibility(View.GONE);
                mFiller.setVisibility(View.GONE);
            }else{
                mLayout = VideoView.VIDEO_LAYOUT_SCALE;
                mTitleView.setVisibility(View.VISIBLE);
                mFiller.setVisibility(View.VISIBLE);
            }
            if (mVideoView != null)
                mVideoView.setVideoLayout(mLayout, 0);
            return true;
        }

    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;
        return super.onTouchEvent(event);
    }
    
    
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mVideoView != null)
            mVideoView.setVideoLayout(mLayout, 0);
        super.onConfigurationChanged(newConfig);
    }
	
	@Override
	protected void onDestroy() {
		mVideoView.stopPlayback();
		super.onDestroy();
	}

}
