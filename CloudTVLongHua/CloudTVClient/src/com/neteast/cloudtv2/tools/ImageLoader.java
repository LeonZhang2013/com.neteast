package com.neteast.cloudtv2.tools;

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
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Debug;
import android.util.Log;
import android.widget.ImageView;

/**
 * @author LeonZhang 
 * @Email zhanglinlang1@163.com
 */
public class ImageLoader {

	/** 缓存类�? */
	private MemoryCache memoryCache;
	/** 缓存文件管理工具 */
	private FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	private ExecutorService executorService;
	private boolean bMemoryCacheEnable = true;
	private int connectTimeout = 3000;
	private int readTimeout = 5000;
	/**下载图片失败尝试次数*/
	private int retry = 3;
	/**线程池数*/

	private long mMaxNativeHeap = 16777216; // 16*1024*1024 Bytes
	private long mRetainedNativeHeap = 3145728; // 3*1024*1024 Bytes
	private static ImageLoader mImageLoader;
	public static synchronized ImageLoader getInstanse(Context context){
		if(mImageLoader!=null){
			return mImageLoader;
		}else{
			return new ImageLoader(context);
		}
	}

	private ImageLoader(Context context) {
		long maxVmHeap = Runtime.getRuntime().maxMemory();
		if (maxVmHeap == 16777216)
			mMaxNativeHeap = 16777216; // 16*1024*1024 Bytes
		else if (maxVmHeap == 25165824) // 24
			mMaxNativeHeap = 25165824; // 24*1024*1024 Bytes
		else if (maxVmHeap == 33554432) // 32
			mMaxNativeHeap = 25165824; // 24*1024*1024 Bytes
		else if (maxVmHeap > 33554432 && maxVmHeap < 50331648)
			mMaxNativeHeap = 36700160; // 35*1024*1024 Bytes
		else if (maxVmHeap == 50331648) // 48
			mMaxNativeHeap = 41943040; // 40*1024*1024 Bytes
		else
			mMaxNativeHeap = maxVmHeap - 10 * 1024 * 1024; // max -10M
		
		memoryCache = new MemoryCache();
		fileCache = new FileCache(context);
		executorService = MyThreadPool.getThreadPool();
	}

	public void SetRetainedHeapInMB(int mb) {
		mRetainedNativeHeap = (long) mb * 1024;
	}

	public void SetConnectTimeout(int timeout) {
		if (timeout > 0)
			connectTimeout = timeout;
	}

	public void SetReadTimeout(int timeout) {
		if (timeout > 0)
			readTimeout = timeout;
	}

	public void SetRetry(int num) {
		if (num >= 0)
			retry = num;
	}

	public void SetMemoryCacheEnable(boolean enable) {
		bMemoryCacheEnable = enable;
	}
	

	/** 通过URL获取图片填充 ImageView对象 (该方法会首先从内存中读取图片，如果没有就使用URL地址进行下载)
	 * 
	 * @param url 图片网络地址
	 * @param imageView ImageView对象
	 * @param scale 确定图片大小 1:原图�?   2: 原图�?/4 �?3: 原图�?/9 */
	public void setScaledImage(String url, ImageView imageView, int scale,Long priority) {
		imageViews.put(imageView, url);
		Bitmap bitmap = null;
		if (bMemoryCacheEnable)
			bitmap = memoryCache.get(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			scale = (scale < 1) ? 1 : scale;
			queuePhoto(url, imageView, scale,priority);
		}
	}
	
	/** 通过URL获取图片填充 ImageView对象 (该方法会首先从内存中读取图片，如果没有就使用URL地址进行下载)
	 * 
	 * @param priority 图片下载优先级，数字越大下载优先级越高， 推荐使用 System.currentTimeMillis();
	 * @param url 图片网络地址
	 * @param imageView ImageView对象 */
	public void setImage(String url, ImageView imageView,Long priority) {
		setScaledImage(url,imageView,1,priority);
	}
	
	/** 通过URL获取图片填充 ImageView对象 (该方法会首先从内存中读取图片，如果没有就使用URL地址进行下载)
	 * 
	 * @param url 图片网络地址
	 * @param imageView ImageView对象 */
	public void setImage(String url, ImageView imageView) {
		setScaledImage(url,imageView,1,System.currentTimeMillis());
	}

	/** 删除URL对应的图片�?
	 * 
	 * @param url 图片路径 */
	public void ClearImageCache(String url) {
		if (bMemoryCacheEnable)
			memoryCache.del(url);
	}

	/** 预加载，直接下载图片到本�?
	 * 
	 * @param url
	 * @param scale */
	public void PreloadImage(String url, int scale,Long priority) {
		PhotoToLoad p = new PhotoToLoad(url, (ImageView) null, scale);
		executorService.submit(new PhotosLoader(p,priority));
	}

	/** 使用线程池，下载图片
	 * 
	 * @param url
	 * @param imageView
	 * @param scale */
	private void queuePhoto(String url, ImageView imageView, int scale,Long priority) {
		PhotoToLoad p = new PhotoToLoad(url, imageView, scale);
		executorService.submit(new PhotosLoader(p,priority));
	}

	
	private static ConcurrentHashMap<String,String> lockUrl = new ConcurrentHashMap<String,String>();
	/** 从网络获取图片�?
	 *  
	 * @param url
	 * @param f
	 * @return */
	private InputStream forceDownloadImage(String url) {
		InputStream is = null;
		Log.i("scorll", url);
		//防止相同的url被同�?��间内多次下载
		if(lockUrl.get(url)!=null) return null;
		Log.i("scorll", "url   ------ " + url);
		for (int i = 0; i <= retry; i++) {
			try {
				lockUrl.put(url, url);
				is = downloadUrl(url);
			} catch (Exception ex) {
				Log.i("outtime", url);
				continue;
			}
			break;
		}
		lockUrl.remove(url);
		return is;
	}

	/** 下载图片 并将图片保存到本地�?
	 * 
	 * @param url 图片网络路径
	 * @param f 图片将会被写入到该文件�?
	 * @return
	 * @throws Exception */
	private InputStream downloadUrl(String url) throws Exception {
		URL imageUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
		conn.setConnectTimeout(connectTimeout);
		conn.setReadTimeout(readTimeout);
		conn.setInstanceFollowRedirects(true);

		InputStream is = conn.getInputStream();
		return is;
	}

	/** 下载图片到本地�?
	 * 
	 * @param url */
	private void downloadImage(String url) {
		if (url != null) {
			InputStream is = forceDownloadImage(url);
			if (is != null) {
				try {
					File f = fileCache.getFile(url);
					FileCache.saveFileLocal(is, f);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/** 获取图片先从本地获取，如果本地没有再从网络获取�? 如果都没有返回空
	 * 
	 * @param url 图片网络路径
	 * @param scale 确定图片大小 1:原图�?   2: 原图�?/4 �?3: 原图�?/9 
	 * @return 返回图片获取失败返回null�?*/
	public Bitmap getBitmap(String url, int scale) {
		InputStream in = null;

		try {
			//从本地获取图�?
			File f = fileCache.getFile(url);
			Bitmap b = decodeFile(f, scale);
			if (b != null) return b;
			//从网络获取图�?
			Bitmap bitmap = null;
			in = forceDownloadImage(url);
			if (in != null) {
				bitmap = BitmapFactory.decodeStream(in);
				saveBitMapToLocal(bitmap, f);
				if(bitmap!=null) bitmap = scaleBitMap(bitmap, scale);
			}
			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.i("imageurl", "网络下载失败");
			return null;
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/** 保存图片到本�?
	 * 
	 * @param bitmap
	 * @param f */
	public void saveBitMapToLocal(Bitmap bitmap, File f) {
		if (bitmap == null)
			return;
		OutputStream out = null;
		try {
			out = new FileOutputStream(f);
			bitmap.compress(CompressFormat.PNG, 100, out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/** �?��堆栈信息是否超出了设定的�?��容量 mMaxNativeHeap
	 * 
	 * @param bitmapWidth
	 * @param bitmapHeight
	 * @param BytePerPixel
	 * @return */
	@SuppressWarnings("unused")
	private boolean checkHeap(int bitmapWidth, int bitmapHeight, int BytePerPixel) {
		long sizeReqd = bitmapWidth * bitmapHeight * BytePerPixel;
		long allocNativeHeap = Debug.getNativeHeapAllocatedSize();

		if ((sizeReqd + allocNativeHeap + mRetainedNativeHeap) >= mMaxNativeHeap) {
			return true;
		}
		return false;
	}

	/** 获取缓存到sdcard的图�?
	 * 
	 * @param f 保存图片的本地图�?
	 * @param scale 决定图片大小节约内存�?
	 * @return 返回图片 如果文件不存在返回空
	 * @throws Exception */
	private Bitmap decodeFile(File f, int scale) throws Exception {
		if(!f.exists()) return null; 
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		return bitmap;
	}

	private Bitmap scaleBitMap(Bitmap bitmap, int scale) {
		if (bitmap == null) return null;
		return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/scale, bitmap.getHeight()/scale, true);
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;
		public int scale = 2;
		
		public PhotoToLoad(String u, ImageView i, int s) {
			url = u;
			imageView = i;
			scale = (s < 1) ? 1 : s;
		}
	}

	/** 图片加载线程
	 * 
	 * @author LeonZhang
	 * @Email zhanglinlang1@163.com */
	class PhotosLoader implements Runnable,Comparable<PhotosLoader>{
		PhotoToLoad photoToLoad;
		private long priority;
		
		PhotosLoader(PhotoToLoad photoToLoad,long priority) {
			this.photoToLoad = photoToLoad;
			this.priority = priority;
		}

		@Override
		public void run() {
			if (photoToLoad.imageView == null) {
				downloadImage(photoToLoad.url);
				return;
			}
			Bitmap bmp = getBitmap(photoToLoad.url, photoToLoad.scale);
			if (bMemoryCacheEnable)
				memoryCache.put(photoToLoad.url, bmp);
			if (bmp == null)
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}

		@Override
		/**
		 * 数字越大优先级越�?
		 * @param o
		 * @return
		 */
        public int compareTo(PhotosLoader o) {
            return priority ==o.priority ? 1 :priority>o.priority ? 0: -1;
        }
	}

	/** 判断图片url是否被改动�?
	 * 
	 * @param photoToLoad
	 * @return 当图片的 RUL等于空或者被改动时返回false，则返回true */
	private boolean imageViewReused(PhotoToLoad photoToLoad) {
		String mUrl = imageViews.get(photoToLoad.imageView);
		if (mUrl == null || !mUrl.equals(photoToLoad.url))
			return true;
		return false;
	}

	/** 设置图片（使�?Acticity.runOnUiThread(Runnable runnable)�?该方法可以代�?Thread + Handle的方式）
	 * 
	 * @author LeonZhang
	 * @Email zhanglinlang1@163.com */
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		/** 设置图片（使�?Acticity.runOnUiThread(Runnable runnable)�?该方法可以代�?Thread + Handle的方式） */
		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null) {
				photoToLoad.imageView.setImageBitmap(bitmap);
			}
		}
	}

	/** 删除本地文件夹的图片�?��图片 */
	public void clearCache() {
		fileCache.clearNativeFile();
	}

	/** 清空内存中图�?*/
	public void clearMemoryCache() {
		/*
		 * Log.i("show", "imageViews.size == "+imageViews.size()); Iterator<Entry<ImageView, String>> hIter =
		 * imageViews.entrySet().iterator(); while (hIter.hasNext()) { Map.Entry<ImageView, String> entry = (Map.Entry<ImageView,
		 * String>) hIter.next(); ImageView view = (ImageView) (entry.getKey()); view.setImageResource(R.drawable.movie_cover); }
		 * imageViews.clear(); memoryCache.clear();
		 */
	}
}
