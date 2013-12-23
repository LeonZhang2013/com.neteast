package com.lib.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Debug;
import android.os.Process;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader {
    
    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService; 
    ExecutorService executorServiceDecode; 
    private static long cacheMaxInByte = 52428800; //50*1024*1024 Byte
    private static int stubId = 0;
    private static boolean bMemoryCacheEnable = true;
    private static int connectTimeout = 3000;
    private static int readTimeout = 5000;
    private static int retry = 1;
    
    private static long mMaxNativeHeap = 16777216; //16*1024*1024 Bytes
    private static long mRetainedNativeHeap = 3145728; //3*1024*1024 Bytes
    
    public ImageLoader(Context context){
    	long maxVmHeap = Runtime.getRuntime().maxMemory();
    	if (maxVmHeap == 16777216)
    	     mMaxNativeHeap = 16777216; // 16*1024*1024 Bytes
    	else if (maxVmHeap == 25165824) //24
    	     mMaxNativeHeap = 25165824; // 24*1024*1024 Bytes
    	else if (maxVmHeap == 33554432) //32
   	     	mMaxNativeHeap = 25165824; // 24*1024*1024 Bytes
    	else if(maxVmHeap > 33554432 && maxVmHeap < 50331648)
    		mMaxNativeHeap = 36700160; // 35*1024*1024 Bytes
    	else if (maxVmHeap == 50331648) //48
   	     	mMaxNativeHeap = 41943040; // 40*1024*1024 Bytes
        fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(12);
        executorServiceDecode=Executors.newFixedThreadPool(4);
    }
    
    public void SetCacheCapacityInMB(int mb){
    	cacheMaxInByte = (long)mb* 1024 *1024;
    }
    
    public void SetRetainedHeapInMB(int mb){
    //	mRetainedNativeHeap = (long)mb* 1024 *1024;
    	mRetainedNativeHeap = (long)mb* 1024;
    }
    
    public void SetStubID(int stub_id){
    	stubId = stub_id;
    }
    
    public void SetConnectTimeout(int timeout){
    	if(timeout > 0)
    		connectTimeout = timeout;
    }
    
    public void SetReadTimeout(int timeout){
    	if(timeout > 0)
    		readTimeout = timeout;
    }
    
    public void SetRetry(int num){
    	if(num >= 0)
    		retry = num;
    }
    
    public void SetMemoryCacheEnable(boolean enable){
    	bMemoryCacheEnable = enable;
    }
   // final int stub_id=R.drawable.stub;
    public void DisplayImage(String url, ImageView imageView)
    {
        //imageViews.put(imageView, url);
        Bitmap bitmap= null;
        if(bMemoryCacheEnable)
        	bitmap = memoryCache.get(url);
        
        if(bitmap!=null)
            imageView.setImageBitmap(bitmap);
        else
        {
        	PhotoToLoad p=new PhotoToLoad(url, imageView,1,0,0);
            queuePhoto(url, imageView,p);
            if(stubId > 0)
            	imageView.setImageResource(stubId);
        }
    }
    
    public void DisplayScaledImage(String url, ImageView imageView,int scale)
    {
        //imageViews.put(imageView, url);
        Bitmap bitmap= null;
        if(bMemoryCacheEnable)
        	bitmap = memoryCache.get(url);
        
        if(bitmap!=null)
            imageView.setImageBitmap(bitmap);
        else
        {
        	scale = (scale < 1)?1:scale;
        	PhotoToLoad p=new PhotoToLoad(url, imageView,scale,0,0);
            queuePhoto(url, imageView,p);
            if(stubId > 0)
            	imageView.setImageResource(stubId);
        }
    }
    public void DisplayLimitedImage(String url, ImageView imageView,int viewHeight,int viewWidth)
    {
        //imageViews.put(imageView, url);
        Bitmap bitmap= null;
        if(bMemoryCacheEnable)
        	bitmap = memoryCache.get(url);
        
        if(bitmap!=null)
            imageView.setImageBitmap(bitmap);
        else
        {
        	viewHeight = (viewHeight < 0)?0:viewHeight;
        	viewWidth = (viewWidth < 0)?0:viewWidth;
        	PhotoToLoad p=new PhotoToLoad(url, imageView,1,viewHeight,viewWidth);
            queuePhoto(url, imageView,p);
            if(stubId > 0)
            	imageView.setImageResource(stubId);
        }
    }
    public void DisplayVideoThumbnail(String url, ImageView imageView)
    {
        //imageViews.put(imageView, url);
        Bitmap bitmap= null;
        if(bMemoryCacheEnable)
        	bitmap = memoryCache.get(url);
        
        if(bitmap!=null)
            imageView.setImageBitmap(bitmap);
        else
        {
        	PhotoToLoad p=new PhotoToLoad("video::"+url, imageView,1,0,0);
            queuePhoto(url, imageView,p);
            if(stubId > 0)
            	imageView.setImageResource(stubId);
        }
    }
    public void ClearImageCache(String url){
    	if(bMemoryCacheEnable)
        	memoryCache.del(url);
    }
    
    public void PreloadImage(String url){
        PhotoToLoad p=new PhotoToLoad(url,(ImageView) null,1,0,0);
        executorService.submit(new PhotosLoader(p));
    }
    
    private void queuePhoto(String url, ImageView imageView,PhotoToLoad p)
    {
        if(imageView == null ){
        	executorService.submit(new PhotosLoader(p));
        	return;
        }
        
        File f = p.file;
        	
        if(f!= null && f.exists())
        executorServiceDecode.submit(new PhotosDecoder(p));
        else
        executorService.submit(new PhotosLoader(p));
    }
    
    private InputStream forceDownloadImage(String url,File f){	
    	InputStream is = null;
    	for(int i=0;i<=retry;i++){
    		try{
    			is = downloadUrl(url,f);
    		}catch(Exception ex){
    			continue;
    		}
    		break;
    	}
    	return is;
    }
    
    private InputStream downloadUrl(String url,File f)
    	throws WeiboException{
    	try{
	    	URL imageUrl = new URL(url);
	        HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
	        conn.setConnectTimeout(connectTimeout);
	        conn.setReadTimeout(readTimeout);
	        conn.setInstanceFollowRedirects(true);
	        InputStream is=conn.getInputStream();
	        OutputStream os = new FileOutputStream(f);
	        fileCache.removeEldestFile(cacheMaxInByte);
	        Utility.CopyStream(is, os);
	        os.close();
	       // System.out.println("loaded image :  "+url);
	        if(is.markSupported()){
		        is.reset();
		        return is;
	        }else{
	        	is.close();
	        	return null;
	        }
	    } catch (Exception ex){
	        ex.printStackTrace();
	       // System.out.println("load image error:  "+url);
	        throw new WeiboException(ex);
	    }
    }
    
    private void downloadImage(String url){
    	File f=fileCache.getFile(url);
    	if(!f.exists()){
    		InputStream is = forceDownloadImage(url,f);
    		if(is != null){
	    		try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    }
    private Bitmap getBitmap(PhotoToLoad p) 
    {
    	if(p.bVideo){
    		Bitmap bitmap = Utility.getThumbnail(p.url,"video",0);
    		return bitmap;
    	}else{
	        //File f=fileCache.getFile(p.url);
	        
	        File f = p.file;
	        
	        //from SD cache
	        if(f!= null && f.exists()){
	        Bitmap b = decodeFile(f,p);
        	
        if(b!=null)
            return b;
        }
        
        //from web
        try {
            Bitmap bitmap=null;
            InputStream is = forceDownloadImage(p.url,f);
            if(is == null)
            	bitmap = decodeFile(f,p);
            else{
            	bitmap = decodeStream(is,p);
	            is.close();
            }
            return bitmap;
        } catch (Exception ex){
           ex.printStackTrace();
           return null;
        }
    }
    }

    private boolean checkHeap(long sizeReqd){
    	
    	return false;
    	/*
    	long allocNativeHeap = Debug.getNativeHeapAllocatedSize();
    	//System.out.println("bitmap:"+sizeReqd+",Jheap:"+Runtime.getRuntime().totalMemory()+",Nheap:"+Debug.getNativeHeapAllocatedSize()+",maxNative:"+mMaxNativeHeap+",remainNative:"+mRetainedNativeHeap);
    	//System.out.println("bitmap:"+sizeReqd+",Jheap:"+Runtime.getRuntime().totalMemory()+",Nheap:"+Debug.getNativeHeapAllocatedSize());
    	//System.out.println("bitmap:"+sizeReqd+",mMaxNativeHeap :"+mMaxNativeHeap+",mRetainedNativeHeap :"+mRetainedNativeHeap);
    	if ((sizeReqd + allocNativeHeap + mRetainedNativeHeap) >= mMaxNativeHeap)
    	{
    	    return true;
    	   // return false;
    	}

    	return false;
    	*/
    }
    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f,PhotoToLoad p){
        try {
        	InputStream is = new FileInputStream(f);
        	BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            int width=o.outWidth, height=o.outHeight;
            int BytePerPixel = 4; // 32bit/8
            int retry = 5;
            long sizeReqd = 0;
            if(p.scale == 1 && p.viewHeight > 0 && p.viewWidth > 0)
            	sizeReqd = p.viewWidth * p.viewHeight * BytePerPixel;
            else
            	sizeReqd = width * height * BytePerPixel;
            while(checkHeap(sizeReqd) && (retry > 0)){
            	System.gc();
            	wait(100); //wait 100ms
            	retry--;
            }
            if(retry > 0){
	        	if(p.scale > 1){
	        		 //decode with inSampleSize
		            BitmapFactory.Options o2 = new BitmapFactory.Options();
		            o2.inSampleSize=p.scale;
		            
		            Bitmap ret = BitmapFactory.decodeStream(is, null, o2);	            
		            is.close();
		            return ret;
	    		}else if(p.viewHeight > 0 && p.viewWidth > 0){
	    			int width_tmp = width , height_tmp = height , scale_tmp = 1;
	    			while(true){
	                    if(width_tmp/2<p.viewWidth && height_tmp/2<p.viewHeight)
	                        break;
	                    width_tmp/=2;
	                    height_tmp/=2;
	                    scale_tmp*=2;
	                }
	    			BitmapFactory.Options o2 = new BitmapFactory.Options();
		            o2.inSampleSize=scale_tmp;
		            Bitmap ret = BitmapFactory.decodeStream(is, null, o2);	            
		            is.close();
		            return ret;
	        	}else{
	                BitmapFactory.Options o2 = new BitmapFactory.Options();
	                Bitmap ret = BitmapFactory.decodeStream(is,null,o2);           
	                is.close();
		            return ret;
	        	}
            }
            is.close();
        } catch (Exception e) {}
        return null;
    }
    
    private Bitmap decodeStream(InputStream is,PhotoToLoad p){
        if(is == null) return null;
    	try {
    		BitmapFactory.Options o = new BitmapFactory.Options();
    		o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is,null,o);
            int width=o.outWidth, height=o.outHeight;
            int BytePerPixel = 4; // 32bit/8
            int retry = 5;
            long sizeReqd = 0;
            if(p.scale == 1 && p.viewHeight > 0 && p.viewWidth > 0)
            	sizeReqd = p.viewWidth * p.viewHeight * BytePerPixel;
            else
            	sizeReqd = width * height * BytePerPixel;
            while(checkHeap(sizeReqd) && (retry > 0)){
            	System.gc();
            	wait(100); //wait 100ms
            	retry--;
            }
            if(retry > 0){
	    		if(p.scale > 1){
		            //decode with inSampleSize
		            BitmapFactory.Options o2 = new BitmapFactory.Options();
		            o2.inSampleSize=p.scale;
		            Bitmap ret = BitmapFactory.decodeStream(is, null, o2);
		            return ret;
	    		}else if(p.viewHeight > 0 && p.viewWidth > 0){
	    			int width_tmp = width , height_tmp = height , scale_tmp = 1;
	    			while(true){
	                    if(width_tmp/2<p.viewWidth && height_tmp/2<p.viewHeight)
	                        break;
	                    width_tmp/=2;
	                    height_tmp/=2;
	                    scale_tmp*=2;
	                }
	    			BitmapFactory.Options o2 = new BitmapFactory.Options();
		            o2.inSampleSize=scale_tmp;
		            Bitmap ret = BitmapFactory.decodeStream(is, null, o2);
		            return ret;
	    		}else{
	                BitmapFactory.Options o2 = new BitmapFactory.Options();
	                //o.inJustDecodeBounds = true;
	                Bitmap ret = BitmapFactory.decodeStream(is,null,o2);
		            return ret;
	    		}
            }
        } catch (Exception e) {}
        return null;
    }
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public int scale;
        public int viewHeight;
        public int viewWidth;
        public File file = null;
        public boolean bVideo = false;
        public PhotoToLoad(String u, ImageView i, int s , int h, int w){
            url=u;
            if(url.indexOf("file://") == 0)
                file=new File(url.substring(7)); 
            else if(url.indexOf("video::") == 0 ){
            	bVideo = true;
            	url=u.substring(7);
            }else
                file=fileCache.getFile(url);
            
            imageView=i;
            scale = s;
            viewHeight = h;
            viewWidth = w;
        }
    }
    
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        @Override
        public void run() {
        	Log.v("Loader","url:"+photoToLoad.url);
            if(!photoToLoad.bVideo && photoToLoad.imageView == null){
            	downloadImage(photoToLoad.url);
                return;
            } 
            Bitmap bmp=getBitmap(photoToLoad);
            if(bMemoryCacheEnable)
            	memoryCache.put(photoToLoad.url, bmp);
            
            if(imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
            Activity a=(Activity)photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }
    
    class PhotosDecoder implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosDecoder(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        @Override
        public void run() {
        	//Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            //Log.v("Decode","url:"+photoToLoad.url);
            Bitmap bmp=getBitmap(photoToLoad);
            if(bMemoryCacheEnable)
            	memoryCache.put(photoToLoad.url, bmp);
            
            if(imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
            Activity a=(Activity)photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad){
    	/*
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        */
        return false;
    }
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null){
                photoToLoad.imageView.setImageBitmap(bitmap);
               // Log.out.println("Max:"+Runtime.getRuntime().maxMemory()+",Jheap:"+Runtime.getRuntime().totalMemory()+",Nheap:"+Debug.getNativeHeapAllocatedSize());
            }else{
            	if(stubId > 0)
            		photoToLoad.imageView.setImageResource(stubId);
            }
        }
    }

    public void clearCache() {
        //memoryCache.clear();
        fileCache.clear();
    }
    public void clearMemoryCache(){
    	Iterator hIter = imageViews.entrySet().iterator();
    	while(hIter.hasNext()){
    		 Map.Entry<ImageView, String> entry= (Map.Entry<ImageView, String>)hIter.next();  
    		 ImageView view = (ImageView)(entry.getKey());
    		 view.setImageResource(stubId);
    	}
    	imageViews.clear();
    	memoryCache.clear();
    }
}
