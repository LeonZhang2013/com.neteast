/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.neteast.clouddisk.activity;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.lib.net.WeiboException;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.model.MovieInfo;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.utils.DataHelpter;
import com.neteast.clouddisk.utils.DownLoadApplication;
import com.neteast.clouddisk.utils.UIHelper;


public class ImageSwitcher1 extends Activity implements OnTouchListener,AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory {
	private int curposition=-1;
	private Gallery g =null;
	private GestureDetector mGestureDetector;
	
	private int currentRotateValue = 0;
	
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	
	private PopupWindow titlecontroler = null;
	private PopupWindow controler = null;
	private View titlecontrolView;
	private View controlView = null;
	
	
	private final static int TIME = 6868; 
	private final static int SLIDETIME = 8000;
	private boolean isControllerShow = false;
	private static int screenWidth = 0;
	private static int screenHeight = 0;
	private static int controlHeight = 0; 
	private int SlideMode = 0;
	
	private final static int PLAYING_VIDEO = 0;
	private final static int PLAYING_MUSIC = 1;
	private final static int PLAYING_PHOTO = 2;
	private final static int PLAYING_DMC = 3;
	private final static int PLAYING_DMR_VIDEO = 4;
	private final static int PLAYING_DMR_MUSIC = 5;
	private final static int PLAYING_DMR_PHOTO = 6;
	
	private int mPlayingType = PLAYING_PHOTO;
	
	private Button bnback = null;
	private Button delbtn = null;

	private TextView mtitle =null;
	
	private ContentResolver cr;
	public  LinkedList<MovieInfo> playList = new LinkedList<MovieInfo>();
	
	List<DataInfo> picDataList = null;
	
	private LinearLayout loadingView = null;
	private TextView loadingText =null;
	String PATH = "/sdcard/ImageCache/";
	private LibCloud libCloud;
	
	Button setWallpaperBtn = null;
	
	private Bitmap SaveBitmap = null;
	
	
	//String url = "http://pic.yesky.com/imagelist/09/01/11277904_7147.jpg";
	
	
	private DecodeImageAsync  imgasync;
	
	private final static int DOWNLOAD_IDLE = 0;
	private final static int DOWNLOAD_RUNNING = 1;
	private final static int DOWNLOAD_STOPED = 2;
	private final static int DOWNLOAD_ERROR = 3;
	
	private ImageAdapter imgadapter = null;
	private int mDisplayType = 0; /*0:精彩推荐图片�?:我的下载图片，３：我的上传图�?*/
	
	//private ImageDownloader2 mImageDownloader2;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.image_switcher);
      //  mGestureDetector = new GestureDetector(this);
      //  mGestureDetector.setIsLongpressEnabled(true);
        libCloud = LibCloud.getInstance(this);
        //mImageDownloader2 = ((DownLoadApplication) getApplication()).getImageDownloader();
        getScreenSize();
        
        imgasync = new DecodeImageAsync(DOWNLOAD_RUNNING);
        //imgasync[1] =  new DecodeImageAsync(DOWNLOAD_RUNNING);

        mSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
        mSwitcher.setFactory(this);
        slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
        slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.push_right_out);
        slideRightIn = AnimationUtils.loadAnimation(this, R.anim.push_right_in);
        slideRightOut = AnimationUtils.loadAnimation(this, R.anim.push_left_out);
        
        loadingView = (LinearLayout) findViewById(R.id.loading_id);
        loadingText = (TextView) findViewById(R.id.loading_text);
        
        loadingText.setVisibility(View.GONE);

        
        mSwitcher.setFocusable(true);
        mSwitcher.setClickable(true);
        mSwitcher.setLongClickable(true);
        mSwitcher.setFocusableInTouchMode(true);
        mSwitcher.setOnTouchListener(this);
        
       // Uri uri = getIntent().getData();
		Intent i = getIntent();
		curposition = i.getIntExtra("position",curposition); 
		mDisplayType = i.getIntExtra("displayType",0); 
		
		if(mDisplayType ==0){
			//picDataList = DataHelpter.getPicList();
			picDataList =  (List<DataInfo>) i.getSerializableExtra("result");
		}else{
			picDataList =  (List<DataInfo>) i.getSerializableExtra("result");
		}
		//cr = getContentResolver(); 
		//CreateImageList(cr);
		
		/*
		playList = MediaManager.photoList;
		*/
		if(curposition==-1) curposition = 0; 
	
		//MovieInfo info = playList.get(curposition); 
		//playingURL = info.path;
		//playingName = info.displayName;
        
       // curposition =0;
       // mSwitcher.setImageURI(Uri.parse(info.path));
        
        //displayImage(info.path);
        
       // displayInternetImage(url[0]);
		
        DataInfo info1 = picDataList.get(curposition);
        System.out.println("打开图片 position ="+ curposition + "url = " + info1.getUrl() + "displaytype =" + mDisplayType);
        displayInternetImage(info1.getUrl());
        
        g = (Gallery) findViewById(R.id.gallery);
        imgadapter = new ImageAdapter(this);
        g.setAdapter(imgadapter);
        g.setOnItemSelectedListener(this);
        imgadapter.setSelectItem(curposition);
        g.setSelection(curposition);
        //g.setUnselectedAlpha(0.3f); 
       
        

        
        titlecontrolView = getLayoutInflater().inflate(R.layout.image_controler, null);
        controlView = getLayoutInflater().inflate(R.layout.image_command, null);
        controler = new PopupWindow(controlView);
        titlecontroler = new PopupWindow(titlecontrolView);
      
        
        bnback = (Button) titlecontrolView.findViewById(R.id.image_back);
        
       
        
        mtitle = (TextView) titlecontrolView.findViewById(R.id.image_title);
        mtitle.setText(info1.getName());
        

        
        bnback.setOnClickListener(new OnClickListener(){
  			@Override
  			public void onClick(View v) {
  				// TODO Auto-generated method stub
  				
  				if(mPlayingType == PLAYING_DMC) {
  					mPlayingType = PLAYING_PHOTO;
  				}else{
  					ImageSwitcher1.this.finish();
  				}
  			}
         });
        
        delbtn = (Button) controlView.findViewById(R.id.deleteTextView);
        if(mDisplayType ==0){
        	//delbtn.setVisibility(View.GONE);
        	DataInfo di = picDataList.get(curposition);
        	if(di.getStatus()==1){
        		delbtn.setBackgroundResource(R.drawable.picdownloadedbtn);
        		delbtn.setClickable(false);
        		
        	}else if(di.getStatus()==0){
        		delbtn.setClickable(true);
        		delbtn.setBackgroundResource(R.drawable.picdownloadbtn);
        	}
        	
        }
        delbtn.setOnClickListener(new OnClickListener(){
  			@Override
  			public void onClick(View v) {
  				// TODO Auto-generated method stub
  				
  				DataInfo di = picDataList.get(curposition);
  				if(mDisplayType==0){
  					DownLoadAddAsync downloadadd = new DownLoadAddAsync();
  					downloadadd.execute(di);
  					/*
  					String code = "0";
  					try {
  						Map<String,Object> m = libCloud.Mydownload_add(Params.RECOMMEND_PICTURE,
  								di.getId()+":"+curposition);
  						if(m==null){
  							return ;
  						}
  						code = (String) m.get("code");
  						if(code.equals("1")){
  							di.setStatus(1);
  							di.setResid(di.getId());
  							delbtn.setBackgroundResource(R.drawable.picdownloadedbtn);
  							delbtn.setClickable(false);
  							
  							List<DataInfo> pictureDataList = null;
  							String source = di.getChildtype();
  							Map<String,Object> ret =  DataHelpter.getPicList(source);
  							if(ret!=null){
  								pictureDataList = (List<DataInfo>) ret.get("recommend");
  					
  								DataHelpter.updatePicDownloadStatus(pictureDataList,di.getId(),1,curposition+"");
  							}
  				
  						}else if(code.equals("401")){
  							UIHelper.displayToast(getResources().getString(R.string.space_full), ImageSwitcher1.this);
  						}
  					} catch (Exception e) {
  						e.printStackTrace();
  					}
  					*/
  				}else{
	  				if(mDisplayType==1){
	  					try {
	  						libCloud.Delete_file(di.getId());
	  					} catch (WeiboException e) {
	  						// TODO Auto-generated catch block
	  						e.printStackTrace();
	  					}
	  				}else if(mDisplayType ==2){
	  					try {
	  						System.out.println("ImageSwitcher1 del Image id = " + di.getResid() + "num = " + di.getRemark());
	  						//libCloud.Mydownload_del(Params.RECOMMEND_PICTURE,di.getResid(),di.getRemark());
	  						libCloud.Mydownload_del(Params.RECOMMEND_PICTURE,di.getId(),di.getRemark());
	  						List<DataInfo> pictureDataList = null;
	  						String source = di.getChildtype();
	  						Map<String,Object> ret =  DataHelpter.getPicList(source);
	  						if(ret!=null){
	  							pictureDataList = (List<DataInfo>) ret.get("recommend");
	  							DataHelpter.updatePicDownloadStatus(pictureDataList,di.getResid(),0,di.getRemark());
	  						}
	  						
	  					} catch (WeiboException e) {
	  						// TODO Auto-generated catch block
	  						e.printStackTrace();
	  					}
	  				}
	  				picDataList.remove(curposition);
	  				if(curposition < picDataList.size()){
	  					di = picDataList.get(curposition);
	  				}else {
	  					if(picDataList.size()==0){
	  						ImageSwitcher1.this.finish();
	  						return;
	  					}else{
	  						di = picDataList.get(picDataList.size()-1);
	  					}
	  				}
	  				displayInternetImage(di.getUrl());
	  				imgadapter.notifyDataSetChanged();
	  				mtitle.setText(di.getName());
  				}
  			}
         });
        Button slideText = (Button)controlView.findViewById(R.id.pptTextView);
        slideText.setFocusable(true);
        slideText.setClickable(true);
        slideText.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Log.i("click", "slide show");
				slideShow();
				setSlideShowMode(1);
				g.setVisibility(View.GONE);
				hideController();
				
			}
		});
        
        Button leftRotateText = (Button)controlView.findViewById(R.id.lefRouteTextView);
         leftRotateText.setFocusable(true);
         leftRotateText.setClickable(true);
         leftRotateText.setOnClickListener(new OnClickListener() {
 			@Override
 			public void onClick(View v) {
 				Log.i("click", "left rotate");
 				//MovieInfo minfo = playList.get(curposition); 
 				DataInfo minfo1 =picDataList.get(curposition);
 				//mSwitcher.clearAnimation();
 				mSwitcher.setInAnimation(null);
 				mSwitcher.setOutAnimation(null);
 				//rotate(minfo.path,-90);
 				rotate(minfo1.getUrl(),-90);
 				
 			}
 		});
         
         Button rightRotateText = (Button)controlView.findViewById(R.id.rightRouteTextView);
         rightRotateText.setFocusable(true);
         rightRotateText.setClickable(true);
         rightRotateText.setOnClickListener(new OnClickListener() {
 			
 			@Override
 			public void onClick(View v) {
 				Log.i("click", "right rotate");
 				//MovieInfo minfo = playList.get(curposition); 
 				DataInfo minfo1 =picDataList.get(curposition);
 				//mSwitcher.clearAnimation();
 				mSwitcher.setInAnimation(null);
 				mSwitcher.setOutAnimation(null);
 				//rotate(minfo.path,90);
 				rotate(minfo1.getUrl(),90);
 				
 			}
 		});
         setWallpaperBtn = (Button)controlView.findViewById(R.id.setWallpaper);
         setWallpaperBtn.setOnClickListener(new OnClickListener() {		
  			@Override
  			public void onClick(View v) {
  				DataInfo minfo1 =picDataList.get(curposition);
  				String url = minfo1.getUrl();
  				setWallpaperTask wallpaper = new setWallpaperTask();
  				wallpaper.execute(url);	
  			}
  		});
         
         Looper.myQueue().addIdleHandler(new IdleHandler(){

  			@Override
  			public boolean queueIdle() {
  				// TODO Auto-generated method stub
  				if(controler != null ){
  					titlecontroler.showAtLocation(mSwitcher, Gravity.TOP, 0, 0);
  					titlecontroler.update(0, 0, screenWidth,45);
  					
  					controler.showAtLocation(mSwitcher, Gravity.BOTTOM, 0, 90);
  					controler.update(0, 90, screenWidth, 47);
  					
  					
  				}
  				hideController();
  				//myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
  				return false;  
  			}
          });
         mGestureDetector = new GestureDetector(new SimpleOnGestureListener(){

 			@Override
 			public boolean onDoubleTap(MotionEvent e) {
 				// TODO Auto-generated method stub
 				//return super.onDoubleTap(e);
 				return true;
 			}

 			@Override
 			//public boolean onSingleTapConfirmed(MotionEvent e) {
 			public boolean onSingleTapUp(MotionEvent e) {
 				// TODO Auto-generated method stub
 				System.out.println("onSingle TapUp");
 				if(!isControllerShow){
 					showController();
 					if(getSlideShowMode()==1){
 						slideShowStop();
 			    	}
 					//hideControllerDelay();
 				}else {
 					//cancelDelayHide();
 					hideController();
 				}
 				//return super.onSingleTapConfirmed(e);
 				return true;
 				//return false;
 			}

 			@Override
 			public void onLongPress(MotionEvent e) {
 				// TODO Auto-generated method stub
 				
 				//super.onLongPress(e);
 			}	
 			@Override
 			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
 				if(e1==null || e2==null) return false;
 				if (e2.getX() - e1.getX() > 50) {
 					if(curposition-1 >= 0){
 						slideShowStop();
 						showPrevImage();
 						imgadapter.setSelectItem(curposition);
 						g.setSelection(curposition);
 			    		if(mDisplayType ==0){
 			            	//delbtn.setVisibility(View.GONE);
 			            	DataInfo di = picDataList.get(curposition);
 			            	if(di.getStatus()==1){
 			            		delbtn.setBackgroundResource(R.drawable.picdownloadedbtn);
 			            		delbtn.setClickable(false);
 			            	}else if(di.getStatus()==0){
 			            		delbtn.setClickable(true);
 			            		delbtn.setBackgroundResource(R.drawable.picdownloadbtn);
 			            	}
 			            }
 						
 					}
 				}
 				if (e1.getX() - e2.getX() > 50) {
 					if(curposition+1 < picDataList.size()){
 						slideShowStop();
 						showNextImage();
 						imgadapter.setSelectItem(curposition);
 						g.setSelection(curposition);
 			    		if(mDisplayType ==0){
 			            	//delbtn.setVisibility(View.GONE);
 			            	DataInfo di = picDataList.get(curposition);
 			            	if(di.getStatus()==1){
 			            		delbtn.setBackgroundResource(R.drawable.picdownloadedbtn);
 			            		delbtn.setClickable(false);
 			            	}else if(di.getStatus()==0){
 			            		delbtn.setClickable(true);
 			            		delbtn.setBackgroundResource(R.drawable.picdownloadbtn);
 			            	}
 			            }
 						
 					}else if(picDataList.size() > 1){
 						slideShowStop();
 						curposition=-1;
 						showNextImage();
 						imgadapter.setSelectItem(curposition);
 						g.setSelection(curposition);
 			    		if(mDisplayType ==0){
 			            	//delbtn.setVisibility(View.GONE);
 			            	DataInfo di = picDataList.get(curposition);
 			            	if(di.getStatus()==1){
 			            		delbtn.setBackgroundResource(R.drawable.picdownloadedbtn);
 			            		delbtn.setClickable(false);
 			            	}else if(di.getStatus()==0){
 			            		delbtn.setClickable(true);
 			            		delbtn.setBackgroundResource(R.drawable.picdownloadbtn);
 			            	}
 			            }
 						
 					}
 				}
 				return true;
 			}
      });
         
      
    }
    public boolean fileIsExists(String filename){
    	try{
    		File f=new File(filename);
    	    if(!f.exists()){
    	    	return false;
    	    }               
    	}catch (Exception e) {
    		// TODO: handle exception
    		return false;
    	}
    		return true;

    }
    private void displayInternetImage(String url){
    	String filename =null;
    	Log.i("displayInternetImage", "url = " + url);
    	
    	
    	String fileName = getFile(url);
    	Bitmap bm = DownLoadApplication.mFU.getBitmap(fileName,screenWidth,screenHeight);
    	if(bm!=null){
    		loadingView.setVisibility(View.GONE);
    		if(imgasync.getStatus()==AsyncTask.Status.RUNNING){
        		imgasync.onStop();
    		}
    		displayImage(bm);
    		return ;
    	}
    	/*
    	filename=url.substring(url.lastIndexOf("/") + 1);
    	if(fileIsExists(PATH+filename)){
    		loadingView.setVisibility(View.GONE);
    		if(imgasync.getStatus()==AsyncTask.Status.RUNNING){
        		imgasync.onStop();
    		}
    		displayImage(PATH+filename);
    		return ;
    	}
    	*/
    	/*
    	Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, playList.get(curposition).image_id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
		
    	if(bitmap!=null){
    		ImageView imageView = new ImageView(this);
    		imageView.setImageBitmap(bitmap);
    		mSwitcher.setImageDrawable(imageView.getDrawable());	
		}
		*/
    	loadingView.setVisibility(View.VISIBLE);
    	loadingText.setText("0%");
    	Status status = imgasync.getStatus();
       // Log.i("displayInternetImage", "thread status = " + status);
    	if(imgasync.getStatus()==AsyncTask.Status.RUNNING){
    		imgasync.onStop();
    		imgasync = new DecodeImageAsync(DOWNLOAD_RUNNING);
    		imgasync.execute(url);
    	}else{
    		imgasync = new DecodeImageAsync(DOWNLOAD_RUNNING);
    		imgasync.execute(url);
    	}
    	/*
        Status status = imgasync.getStatus();
        Log.i("displayInternetImage", "thread status11 = " + status);
       */
    }
    
    private void displayImage(Bitmap bitmap){
    	if(bitmap!=null){
    		/*
    		if(SaveBitmap!=null){
    			SaveBitmap.recycle();
    			SaveBitmap = null;
    		}
    		SaveBitmap = bitmap;
    		*/
    		Drawable d = new BitmapDrawable(bitmap);
    		mSwitcher.setImageDrawable(d);
    		//mSwitcher.setImageDrawable(imageView.getDrawable());
    		if(getSlideShowMode()==1){
    			slideShow();
    		}	
    	}
    }
    /*
    private void displayImage(String path){
    	Bitmap bitmap;
    	ImageView imageView = new ImageView(this);
    	//go3c.DisplayLocalImage("file://" + path, imageView, 800, 600);
    	bitmap = decodeImage(path,screenWidth,screenHeight);
    	imageView.setImageBitmap(bitmap);
    	mSwitcher.setImageDrawable(imageView.getDrawable());
    	if(getSlideShowMode()==1){
    		slideShow();
    	}
    	
    }
    */
    private int getSlideShowMode(){
    	return SlideMode;
    }
    private void setSlideShowMode(int mode){
    	this.SlideMode = mode;
    }
    private void slideShow(){
    	myHandler.sendEmptyMessageDelayed(SLIDE_SHOW, SLIDETIME);
    }
    private void slideShowStop(){
    	myHandler.removeMessages(SLIDE_SHOW);
    	g.setVisibility(View.VISIBLE);
    	setSlideShowMode(0);
    }
    private void showNextImage(){
        mSwitcher.setInAnimation(slideLeftIn);
	    mSwitcher.setOutAnimation(slideRightOut);
	    curposition++;
		//MovieInfo info = playList.get(curposition); 
		DataInfo info1 = picDataList.get(curposition); 
		//displayImage(info.path);
		//displayInternetImage(url[curposition]);
		displayInternetImage(info1.getUrl());
		mtitle.setText(info1.getName());
    }
    private void showPrevImage(){
    	mSwitcher.setInAnimation(slideRightIn);
		mSwitcher.setOutAnimation(slideLeftOut);
		curposition--;
		//MovieInfo info = playList.get(curposition); 
		//displayImage(info.path);
		//displayInternetImage(url[curposition]);
		DataInfo info1 = picDataList.get(curposition); 
		displayInternetImage(info1.getUrl());
		mtitle.setText(info1.getName());
    }
    private Bitmap decodeImage(String path,int dstwidth,int dstheight){
        try {
        	Bitmap bitmap;
        	File f=new File(path); 
        	InputStream is = new FileInputStream(f);
        	BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            int width=o.outWidth, height=o.outHeight;
            int BytePerPixel = 4; // 32bit/8
            int retry = 5;
            long sizeReqd = 0;
            float scale_tmp =0;
            int scale =0;
            Log.i("decodeImage", "srcWidth = " + width + "srcHeight=" + height + "dstWidth" + dstwidth + "dstHeight" + dstheight);
	        if(dstwidth > 0 && dstheight > 0){
	        	float scale_width=0,scale_height=0;
	        	if(width > dstwidth){
	        		scale_width = width/dstwidth;
	        	}
	        	if(height > dstheight){
	        		scale_height = height/dstheight;
	        	}
	        	scale_tmp = scale_width >= scale_height ? scale_width : scale_height;
	        	Log.i("decodeImage","scale_tmp = " + scale_tmp + "scale_width=" + scale_width + "scale_height=" + scale_height);
	        	if(scale_tmp ==0){
	        		scale =0;
	        	}
	        	else if(scale_tmp < 2){
	        		scale = 2;
	        	}else if(scale_tmp < 4){
	        		scale = 4;
	        	}else if(scale_tmp < 8){
	        		scale = 8;
	        	}else {
	        		scale = 16;
	        	}
	        	Log.i("decodeImage","scale = " + scale);
	    		BitmapFactory.Options o2 = new BitmapFactory.Options();
		        o2.inSampleSize=scale;
		        bitmap = BitmapFactory.decodeStream(is, null, o2);
	        }else{
	        	BitmapFactory.Options o2 = new BitmapFactory.Options();
	            bitmap = BitmapFactory.decodeStream(is,null,o2);           
	        }
      
            is.close();
            return bitmap;
        } catch (Exception e) {}
        return null;
    }

    public void onItemSelected(AdapterView parent, View v, int position, long id) {
    	Log.i("onItemSelected", "curposition = " + curposition + "position=" + position);
    	if(curposition !=position){
    		//displayImage(playList.get(position).path);
    		//displayInternetImage(url[position]);
    		DataInfo info1 = picDataList.get(position); 
    		displayInternetImage(info1.getUrl());
    		curposition = position;
    		imgadapter.setSelectItem(curposition);
    		mtitle.setText(info1.getName());
    		if(mDisplayType ==0){
            	//delbtn.setVisibility(View.GONE);
            	DataInfo di = picDataList.get(curposition);
            	if(di.getStatus()==1){
            		delbtn.setBackgroundResource(R.drawable.picdownloadedbtn);
            		delbtn.setClickable(false);
            	}else if(di.getStatus()==0){
            		delbtn.setClickable(true);
            		delbtn.setBackgroundResource(R.drawable.picdownloadbtn);
            	}
            }
    		

    	}
    }

    public void onNothingSelected(AdapterView parent) {
    }

    public View makeView() {
        ImageView i = new ImageView(this);
        i.setBackgroundColor(0xFF000000);
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        i.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        return i;
    }
    


 

	
	private final static int HIDE_CONTROLER = 1;
	private final static int DLNA_CONTROLER = 2;
	private final static int SLIDE_SHOW = 3;
	Handler myHandler = new Handler(){
	    
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			switch(msg.what){		
				case HIDE_CONTROLER:
					hideController();
					break;
				case DLNA_CONTROLER:
					mPlayingType = PLAYING_DMC;
					showController();
					break;
				case SLIDE_SHOW:
					if(curposition +1 < picDataList.size()){
						showNextImage();
					}else{
						curposition = -1;
						showNextImage();
					}
					//slideShow();
					break;
			}
			
			super.handleMessage(msg);
		}	
    };
	private void getScreenSize()
	{
		Display display = getWindowManager().getDefaultDisplay();
        screenHeight = display.getHeight();
        screenWidth = display.getWidth();
        controlHeight = screenHeight;
        //controlHeight = screenHeight/4;
      
	}
	private void hideController(){
		if(controler.isShowing()){
			controler.update(0,0,0, 0);
			titlecontroler.update(0,0,0,0);
			isControllerShow = false;
		}

	}
	private void hideControllerDelay(){
		myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
	}
	private void showController(){
		System.out.println("show controller !!!!!!");
		controler.update(0,90,screenWidth, 47);
		titlecontroler.update(0,0,screenWidth, 45);
		isControllerShow = true;
		
	}
	
	private void cancelDelayHide(){
		myHandler.removeMessages(HIDE_CONTROLER);
	}
	
	private void rotate(String path,int value){
		if(imgasync.getStatus()==AsyncTask.Status.RUNNING){
			return ;
		}
		ImageView img= new ImageView(this);
		String filename =null;
		Bitmap bmp = null;
		if(path.startsWith("http://")){
			/*
			filename=path.substring(path.lastIndexOf("/") + 1);
			if(fileIsExists(PATH+filename)){
				bmp = decodeImage(PATH+filename,screenWidth,screenHeight);
			}
			*/
			String fileName = getFile(path);
			bmp = DownLoadApplication.mFU.getBitmap(fileName,screenWidth,screenHeight);
			
		}else{
			bmp = decodeImage(path,screenWidth,screenHeight);
		}
		//Bitmap bmp = BitmapFactory.decodeFile(minfo.path);
		//Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bighunsha);
		// Getting width & height of the given image.
		int w = bmp.getWidth();
		int h = bmp.getHeight();
		// Setting post rotate to 90
		currentRotateValue = currentRotateValue + value;
		if(currentRotateValue == 360){
			currentRotateValue = 0;
		}
		if(currentRotateValue == -360){
			currentRotateValue = 0;
		}
		
		Matrix mtx = new Matrix();
		img.setImageMatrix(mtx);
		mtx.postRotate(currentRotateValue);
		//mtx.postScale(1.0f, 1.0f);
		// Rotating Bitmap
		Bitmap rotatedBMP = Bitmap.createBitmap(bmp, 0, 0, w, h, mtx, true);
		BitmapDrawable bmd = new BitmapDrawable(rotatedBMP);
		img.setImageDrawable(bmd);
		int x = img.getWidth() - rotatedBMP.getWidth();
		int y = img.getHeight() - rotatedBMP.getHeight();
		if(x < 0){
			x = 0;
		}
		if(y < 0){
			y = 0;
		}
		mtx = new Matrix();
		mtx.postTranslate(x /2.0f , y / 2.0f);
		img.setImageMatrix(mtx);
		mSwitcher.setImageDrawable(img.getDrawable());
	}
	
    public void CreateImageList(ContentResolver cr) {          
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        Log.i("getMiniThumb","count =" + cursor.getCount());
        cursor.moveToFirst();
        int i=0;
        int _id;   
    	int image_id;   
    	String image_path; 
    	String display_name;
        while (cursor.moveToNext()) {   
        	//Log.i("getMiniThumb","moveToNext i=" + i);
        	MovieInfo mi = new MovieInfo();
			mi.isdecoded = false;
        	 _id = cursor.getInt(0);
        	 image_path = cursor.getString(1);
        	 display_name = cursor.getString(3);
        	 mi.image_id = _id;
        	 mi.displayName = cursor.getString(3);;
 			 mi.path = cursor.getString(1);
 			 mi.type=2;
        	// Log.i("getMiniThumb","moveToNext i=" + i + "image id=" + _id +"paht:" + image_path);
        	
        	//mi.thumbnail = MediaStore.Images.Thumbnails.getThumbnail(cr, _id, MediaStore.Images.Thumbnails.MICRO_KIND, null); 
 			playList.add(mi);
        }
        
    }
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(controler!=null){
			controler.dismiss();
			titlecontroler.dismiss();
		}
	
		myHandler.removeMessages(SLIDE_SHOW);
		myHandler.removeMessages(HIDE_CONTROLER);
		//mImageDownloader2.clearCache();
		//if(SaveBitmap!=null) SaveBitmap.recycle();
		//SaveBitmap = null;
		super.onDestroy();
	}

    private ImageSwitcher mSwitcher;

    public class ImageAdapter extends BaseAdapter {
    	int mGalleryItemBackground;
    	private int  selectItem=-1; 
        public ImageAdapter(Context c) {
            mContext = c;
            /*
            TypedArray a = obtainStyledAttributes(R.styleable.Gallery1);
            mGalleryItemBackground = a.getResourceId(
                    R.styleable.Gallery1_android_galleryItemBackground, 0);
            a.recycle();
            */
        }

        public int getCount() {
           // return mThumbIds.length;
            return picDataList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }
        public  void setSelectItem(int select) {
        	if(select != this.selectItem){
        		this.selectItem = select;
        		notifyDataSetChanged();
        	}
         }  
        public View getView(int position, View convertView, ViewGroup parent) {
            //ImageView i = new ImageView(mContext);
        	ImageView i;
            DataInfo info = picDataList.get(position);
            convertView = LayoutInflater.from(mContext).inflate(
    				R.layout.gallery_item, null);
            i = (ImageView) convertView.findViewById(R.id.gallery_image);
            libCloud.DisplayImage(info.getThumb(), i);
            //mImageDownloader2.download(info.getThumb(), i);
            
            Log.d("ImageSwitcher1","ImageAdapter getView selectItem = " + selectItem + "position = " + position );
            if (position ==selectItem) {
            	i.setBackgroundResource(R.drawable.picture_frame);
            }else{
            	i.setBackgroundDrawable(null);
            }
            
            //i.setImageResource(mThumbIds[position]);
           // i.setAdjustViewBounds(true);
            
            //i.setBackgroundColor(Color.alpha(1)); 
            /*
            i.setLayoutParams(new Gallery.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            */
            /*
            i.setLayoutParams(new Gallery.LayoutParams(
                    83, 64));
            */
            
            
          // i.setBackgroundResource(mGalleryItemBackground);
           //i.setBackgroundResource(R.drawable.picture_frame);
          //  i.setBackgroundResource(R.drawable.gallery_background_1);
            
            //return i;
            return convertView;
        }

        private Context mContext;
    }
  
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		//Log.d("ImageSwitcher1","onTouch" );
		return mGestureDetector.onTouchEvent(event);
		//return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//Log.d("ImageSwitcher1","onTouchEvent" );
		//event.setAction(MotionEvent.ACTION_UP);
		return mGestureDetector.onTouchEvent(event);
	}
	
	private String getPureUrl(String url)
	{
		if (url == null)
			return null;
		String tmp = url;
		int pos = tmp.indexOf("?key");
		if (pos > 0)
		{
			tmp = tmp.substring(0, pos);
		}
		return tmp;
	}
	private String getFile(String url)
	{
		String tmp = getPureUrl(url);
		int pos = tmp.lastIndexOf("/");
		if (pos > 0)
			tmp = tmp.substring(pos + 1);
		return tmp;
	}
	
	class DecodeImageAsync extends AsyncTask<String, Integer, String> {
		private int status = DOWNLOAD_IDLE; /*0:IDLE, 1:RUNNING,2:STOPED,3:ERROR*/
		
		public DecodeImageAsync(int status) {
			// TODO Auto-generated constructor stub
			this.status = status;
		}
		
		@Override
		protected String doInBackground(String... params) {
			String path = null;
			//String url = "http://pic.yesky.com/imagelist/09/01/11277904_7147.jpg";
			if(this.status == DOWNLOAD_STOPED) return null;
			
			//path = downloadFile(params[0]);
			String fileName = getFile(params[0]);
			DownLoadApplication.mFU.saveUrl(params[0], fileName);
			//path = PATH+fileName;
			//return path;
			return fileName;
		}

		@Override
		protected void onPostExecute(String result) {
			//System.out.println("onPostExecute：this.status=" + this.status);
			printInfo("onPostExecute：this.status=" + this.status);
			if(this.status!=DOWNLOAD_STOPED && result!=null){
				mSwitcher.setInAnimation(null);
				mSwitcher.setOutAnimation(null);
				loadingView.setVisibility(View.GONE);
			   	final Bitmap bitmap = DownLoadApplication.mFU.getBitmap(result,screenWidth,screenHeight);
		    	
			   	//Drawable d = new BitmapDrawable(bitmap);
		    	//mSwitcher.setImageDrawable(d);
		    	
				displayImage(bitmap);
			}
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
		// 更新进度
			//printInfo("onProgressUpdate" + values[0]);
			//System.out.println("onProgressUpdate�? + values[0]);
			loadingText.setText(values[0] + "%");
		}
		private void printInfo(String info){
            //Log.d("WEI", "Tread is " + Thread.currentThread().getName() + info);
        }
		public void onStop(){
			this.status = DOWNLOAD_STOPED;
		}

		private String downloadFile(String url) {    
			int fileSize = 0;
			int downLoadFileSize = 0;
			String filename;
			
			Long time1 = System.currentTimeMillis();  
			Long time2 = 0L;   
			//获取文件大小	
			filename=url.substring(url.lastIndexOf("/") + 1);
			try {  
				//System.out.println("downloadFile�? + url);
				//printInfo("downloadFile url = " + url);	
				URL myURL = new URL(url);
				if(this.status == DOWNLOAD_STOPED) {
		    		return null;
		    	}
				HttpURLConnection conn = (HttpURLConnection)myURL.openConnection();
		    	//conn.connect();
				conn.setConnectTimeout(5 * 1000);
				
				conn.setRequestProperty("Accept-Language",Params.ACCEPT_LANGUAGE);
				conn.setRequestProperty("Charset", Params.ChARSET);
				conn.setRequestProperty("Connetion", "Keep-Alive");
				conn.setRequestProperty("User-Agent", "NetFox");
		    	if(this.status == DOWNLOAD_STOPED) {
		    		conn.disconnect();
		    		return null;
		    	}
		    	InputStream is = conn.getInputStream();
		    	if (is == null){
		    		printInfo("connect to server error = ");	
		    		return null;
		    		//throw new RuntimeException("stream is null");
		    	}
			    fileSize = conn.getContentLength();//根据响应获取文件大小
			    if (fileSize <= 0){
			    	printInfo("fileSize is error ");	
			    	return null;
			    	//throw new RuntimeException("无法获知文件大小 ");
			    }
			 
			    File tmpFile = new File(PATH);
		    	if (!tmpFile.exists()) {
		    		tmpFile.mkdir();
		    	}
			    FileOutputStream fos = new FileOutputStream(PATH+filename);
			    //把数据存入路�?文件�?	
			    byte buf[] = new byte[1024];
			    downLoadFileSize = 0;
			    do
			      {
			    	//循环读取
			        int numread = is.read(buf);
			        if (numread == -1)
			        {
			          break;
			        }
			        fos.write(buf, 0, numread);
			        downLoadFileSize += numread;
			        //printInfo("downloadFile downLoadFileSize = " +  downLoadFileSize +"fileSize=" + fileSize);	
			       // System.out.println("downloadFile downLoadFileSize = " +  downLoadFileSize +"fileSize=" + fileSize);
			        publishProgress((int) (downLoadFileSize*100/fileSize));   
		 
			        //sendMsg(1);//更新进度�?
			   } while (this.status!=DOWNLOAD_STOPED);
				
				
				/*
				File tmpFile = new File(path);
			    	if (!tmpFile.exists()) {
			    		tmpFile.mkdir();
			    }
				FileOutputStream fos = new  FileOutputStream(path+filename);  
			    InputStream is = new URL(url).openStream();  
			              
		        time2 = System.currentTimeMillis();  
			              
			    int   data = is.read();   
			    while(data!=-1){   
			    	fos.write(data);   
			        data=is.read();   
			    } 
			   	*/
			    is.close();  
			    fos.close(); 
			} catch (IOException e) {     
				e.printStackTrace();  
				return null;
			}     
			                    
			Long time3 = System.currentTimeMillis();  
			//System.out.println("把输入流保存成文件的时间�?+ (time3 - time1));
			if(this.status!=DOWNLOAD_STOPED){
				return PATH+filename;
			}else{
				printInfo("exit from Stop status ");
				File tmpFile = new File(PATH+filename);
				if(tmpFile.exists()){
					tmpFile.delete();
				}
				return null;
			}
		}
	}
	
	class setWallpaperTask extends AsyncTask<Object, Integer, Integer> {	
		@SuppressWarnings("unchecked")
		@Override  
	    protected void onPreExecute() {  
			//第一个执行方法
			UIHelper.showToast2(ImageSwitcher1.this,getResources().getString(R.string.setting_wallpaper));
			setWallpaperBtn.setClickable(false);
		    super.onPreExecute();  
		}  
		@Override
		protected Integer doInBackground(Object... params) {
			String url = (String) params[0];
			if(url==null) return 0;
			String fileName = getFile(url);
  		    Bitmap bmp = DownLoadApplication.mFU.getBitmap(fileName,screenWidth,screenHeight);
  		    if(bmp!=null){
  		    	try {
  		    		setWallpaper(bmp);
  		    	} catch (IOException e) {
  		    		// TODO Auto-generated catch block
  		    		e.printStackTrace();
  		    	}
  		    	return 1;
  		    }else{
  		    	
  		    	return 0;
  		    }
		}

		@Override
		protected void onPostExecute(Integer result) {
			setWallpaperBtn.setClickable(true);
			if(result==1){
				UIHelper.showToast(ImageSwitcher1.this,getResources().getString(R.string.set_wallpaper_ok));
				//UIHelper.displayToast(getResources().getString(R.string.set_wallpaper_ok),ImageSwitcher1.this);
			}else if(result == 0){
				UIHelper.showToast(ImageSwitcher1.this,getResources().getString(R.string.set_wallpaper_failed));
				//UIHelper.displayToast(getResources().getString(R.string.set_wallpaper_ok),ImageSwitcher1.this);
			}
		}
	}
	
	class DownLoadAddAsync extends AsyncTask<Object, Integer, String> {
		DataInfo info = null;
		@Override  
	    protected void onPreExecute() {  
			//第一个执行方法
			delbtn.setClickable(false);
		    super.onPreExecute();  
		}  
		@Override
		protected String doInBackground(Object... params) {
			String code = "0";
			info = (DataInfo) params[0];
			try {
				Map<String,Object> m = libCloud.Mydownload_add(Params.RECOMMEND_PICTURE,info.getId()+":"+curposition);
				if(m!=null){
					code = (String) m.get("code");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return code;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("1")) {	
				info.setStatus(1);
					info.setResid(info.getId());
					delbtn.setBackgroundResource(R.drawable.picdownloadedbtn);
					delbtn.setClickable(false);
					
					List<DataInfo> pictureDataList = null;
					String source = info.getChildtype();
					Map<String,Object> ret =  DataHelpter.getPicList(source);
					if(ret!=null){
						pictureDataList = (List<DataInfo>) ret.get("recommend");
			
						DataHelpter.updatePicDownloadStatus(pictureDataList,info.getId(),1,curposition+"");
					}
					UIHelper.showToast(ImageSwitcher1.this,getResources().getString(R.string.image_download_sucess));
			}else if(result.equals("401")){
				delbtn.setClickable(true);
				UIHelper.showInvalidDialog(ImageSwitcher1.this,getResources().getString(R.string.space_full));
				//UIHelper.displayToast(getResources().getString(R.string.space_full), ImageSwitcher1.this);
			}else if(result.equals("801")){
				delbtn.setClickable(true);
				UIHelper.showInvalidDialog(ImageSwitcher1.this,getResources().getString(R.string.invalid_user_message));
			}
			else {
				delbtn.setClickable(true);
				UIHelper.showToast(ImageSwitcher1.this,getResources().getString(R.string.download_failed_text));
				//UIHelper.displayToast(getResources().getString(R.string.download_failed_text), ImageSwitcher1.this);
			}
		}

	}
	
	/*
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	*/
}
