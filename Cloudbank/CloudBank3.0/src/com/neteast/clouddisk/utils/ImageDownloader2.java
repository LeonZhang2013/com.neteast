package com.neteast.clouddisk.utils;

/*
 * Copyright (C) 2010 The Android Open Source Project
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * This helper class download images from the Internet and binds those with the
 * provided ImageView.
 * 
 * <p>
 * It requires the INTERNET permission, which should be added to your
 * application's manifest file.
 * </p>
 * 
 * A local cache of downloaded images is maintained internally to improve
 * performance.
 */
public class ImageDownloader2
{
	private static final String LOG_TAG = "ImageDownloader";
	private static final boolean UseLocalCache = true;
	private static final int HARD_CACHE_CAPACITY = 50;
	private static final int DELAY_BEFORE_PURGE = 30 * 1000; // in milliseconds
	ExecutorService executorService; 

	 public ImageDownloader2(){
		 executorService = Executors.newFixedThreadPool(10);
	 }
	
	// Hard cache, with a fixed maximum capacity and a life duration
	private final HashMap<String, Bitmap> sHardBitmapCache = new LinkedHashMap<String, Bitmap>(
			HARD_CACHE_CAPACITY / 2, 0.75f, true)
	{
		@Override
		protected boolean removeEldestEntry(
				LinkedHashMap.Entry<String, Bitmap> eldest)
		{
			//System.out.println("removeEldestEntry size() = " + size() + "HARD_CACHE_CAPACITY=" + HARD_CACHE_CAPACITY);
			if (size() > HARD_CACHE_CAPACITY)
			{
				// Entries push-out of hard reference cache are transferred to
				// soft reference cache
				//System.out.println("removeEldestEntry url = " + eldest.getKey());
				
				Bitmap bm = eldest.getValue();
				if(bm!=null && !bm.isRecycled()){
					bm.recycle();
					bm = null;
				}
				
				
				/*
				sSoftBitmapCache.put(eldest.getKey(),
						new SoftReference<Bitmap>(eldest.getValue()));
				*/
				return true;
			} else
				return false;
		}
	};

	// Soft cache for bitmap kicked out of hard cache
	private final  ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
			HARD_CACHE_CAPACITY / 2);

	private final Handler purgeHandler = new Handler();

	private final Runnable purger = new Runnable()
	{
		public void run()
		{
			//clearCache();
		}
	};

	public void downloadCDN(String url, ImageView imageView)
	{
		String path = url;
		/*
		if (Constants.useCDNUrl)
		{
			CdnUtil cdn = new CdnUtil();
			path = cdn.createFull(url);
		}
		*/
		if(path == null)return;
		else download(path, imageView, null);
	}

	/**
	 * Download the specified image from the Internet and binds it to the
	 * provided ImageView. The binding is immediate if the image is found in the
	 * cache and will be done asynchronously otherwise. A null bitmap will be
	 * associated to the ImageView if an error occurs.
	 * 
	 * @param url
	 *            The URL of the image to download.
	 * @param imageView
	 *            The ImageView to bind the downloaded image to.
	 */
	public void download(String url, ImageView imageView)
	{
		if(url==null || imageView==null) return ;
		download(url, imageView, null);
		//executorService.submit(new imgdownload(url, imageView, null));
	}
	
	public void download(String url, ImageView imageView,int dstwidth,int dstheight)
	{
		if(url==null || imageView==null) return ;
		download(url, imageView, null,dstwidth,dstheight);
		//executorService.submit(new imgdownload1(url, imageView, null,dstwidth,dstheight));
		//executorService.submit(new imgdownload(url, imageView, null));
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

	/**
	 * Same as {@link #download(String, ImageView)}, with the possibility to
	 * provide an additional cookie that will be used when the image will be
	 * retrieved.
	 * 
	 * @param url
	 *            The URL of the image to download.
	 * @param imageView
	 *            The ImageView to bind the downloaded image to.
	 * @param cookie
	 *            A cookie String that will be used by the http connection.
	 */
		
	public void download(String url, ImageView imageView, String cookie)
	{
		resetPurgeTimer();

		Bitmap bitmap = getBitmapFromCache(getPureUrl(url));
		if (bitmap == null)
		{
			forceDownload(url, imageView, cookie);
		} else
		{
			cancelPotentialDownload(url, imageView);
			//imageView.setImageBitmap(bitmap);
			Drawable d = new BitmapDrawable(bitmap);
			//imageView.setBackgroundDrawable(d);
			imageView.setImageDrawable(d);
			//imageView.setImageBitmap(bitmap);
			setVisibility(imageView);
		}
	}
	
	public void download(String url, ImageView imageView, String cookie,int dstwidth,int dstheight)
	{
		resetPurgeTimer();

		Bitmap bitmap = getBitmapFromCache(getPureUrl(url));

		if (bitmap == null)
		{
			forceDownload(url, imageView, cookie,dstwidth,dstheight);
		} else
		{
			cancelPotentialDownload(url, imageView);
			//imageView.setImageBitmap(bitmap);
			Drawable d = new BitmapDrawable(bitmap);
			//imageView.setBackgroundDrawable(d);
			imageView.setImageDrawable(d);
			//imageView.setImageBitmap(bitmap);
			setVisibility(imageView);
		}
	}

	private void setVisibility(ImageView imageView)
	{
		int visibility = imageView.getVisibility();
		if (visibility == View.INVISIBLE || visibility == View.GONE)
			imageView.setVisibility(View.VISIBLE);
	}

	/*
	 * Same as download but the image is always downloaded and the cache is not
	 * used. Kept private at the moment as its interest is not clear. private
	 * void forceDownload(String url, ImageView view) { forceDownload(url, view,
	 * null); }
	 */

	/**
	 * Same as download but the image is always downloaded and the cache is not
	 * used. Kept private at the moment as its interest is not clear.
	 */
	private void forceDownload(String url, ImageView imageView, String cookie)
	{
		// State sanity: url is guaranteed to never be null in
		// DownloadedDrawable and cache keys.
		if (url == null)
		{
			imageView.setImageDrawable(null);
			return;
		}

		if (cancelPotentialDownload(url, imageView))
		{
			String fileName = getFile(url);
			if (UseLocalCache)
			{
				Bitmap bm = DownLoadApplication.mFU.getBitmap(fileName);
				if (bm != null)
				{
					Drawable d = new BitmapDrawable(bm);
					//imageView.setBackgroundDrawable(d);
					if(!bm.isRecycled()){
						imageView.setImageDrawable(d);
						//imageView.setImageBitmap(bm);
						synchronized (sHardBitmapCache)
						{
							sHardBitmapCache.put(getPureUrl(url), bm);
						}
					
						return;
					}else{
						System.out.println("bitmap is recycled !!!!!!");
					}
				}else{
					//System.out.println("getBitmap from File is NULL!!!!!!");
				}
			}
			
			BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
			DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
			imageView.setImageDrawable(downloadedDrawable);
			task.execute(url, cookie,"0","0");
			
		}
	}
	
	private void forceDownload(String url, ImageView imageView, String cookie,int dstwidth,int dstheight)
	{
		// State sanity: url is guaranteed to never be null in
		// DownloadedDrawable and cache keys.
		if (url == null)
		{
			imageView.setImageDrawable(null);
			return;
		}

		if (cancelPotentialDownload(url, imageView))
		{
			String fileName = getFile(url);
			if (UseLocalCache)
			{
				Bitmap bm = DownLoadApplication.mFU.getBitmap(fileName,dstwidth,dstheight);
				if (bm != null)
				{
					Drawable d = new BitmapDrawable(bm);
					//imageView.setBackgroundDrawable(d);
					imageView.setImageDrawable(d);
					//imageView.setImageBitmap(bm);
					synchronized (sHardBitmapCache)
					{
						sHardBitmapCache.put(getPureUrl(url), bm);
					}
					return;
				}
			}
			
			BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
			DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
			imageView.setImageDrawable(downloadedDrawable);
			task.execute(url, cookie,Integer.toString(dstwidth),Integer.toString(dstheight));
			
		}
	}

	/**
	 * Clears the image cache used internally to improve performance. Note that
	 * for memory efficiency reasons, the cache will automatically be cleared
	 * after a certain inactivity delay.
	 */
	public void clearCache()
	{
		
		for (Entry<String, Bitmap> mapx : sHardBitmapCache.entrySet())
        {
			//System.out.println(mapx.getKey());
            Bitmap result = mapx.getValue();
            if(result!=null && !result.isRecycled()){
	        	result.recycle();
	        	result=null;
	        }
        }
		
		for (Entry<String, SoftReference<Bitmap>> mapx : sSoftBitmapCache.entrySet())
        {
			//System.out.println(mapx.getKey());
			SoftReference<Bitmap> bitmapReference = mapx.getValue();
			if(bitmapReference!=null){
				Bitmap bitmap = bitmapReference.get();
	            if(bitmap!=null && !bitmap.isRecycled()){
	            	bitmap.recycle();
	            	bitmap=null;
		        }
			}
        }
		
		sHardBitmapCache.clear();
		sSoftBitmapCache.clear();
		Runtime.getRuntime().gc();
	
	}
	
	private void resetPurgeTimer()
	{
		purgeHandler.removeCallbacks(purger);
		purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
	}

	/**
	 * Returns true if the current download has been canceled or if there was no
	 * download in progress on this image view. Returns false if the download in
	 * progress deals with the same url. The download is not stopped in that
	 * case.
	 */
	private static boolean cancelPotentialDownload(String url,
			ImageView imageView)
	{
		BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

		if (bitmapDownloaderTask != null)
		{
			String bitmapUrl = bitmapDownloaderTask.url;
			if ((bitmapUrl == null) || (!bitmapUrl.equals(url)))
			{
				bitmapDownloaderTask.cancel(true);
			} else
			{
				// The same URL is already being downloaded.
				return false;
			}
		}
		return true;
	}

	/**
	 * @param imageView
	 *            Any imageView
	 * @return Retrieve the currently active download task (if any) associated
	 *         with this imageView. null if there is no such task.
	 */
	private static BitmapDownloaderTask getBitmapDownloaderTask(
			ImageView imageView)
	{
		if (imageView != null)
		{
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof DownloadedDrawable)
			{
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}
		return null;
	}

	/**
	 * @param url
	 *            The URL of the image that will be retrieved from the cache.
	 * @return The cached bitmap or null if it was not found.
	 */
	private Bitmap getBitmapFromCache(String url)
	{
		// First try the hard reference cache
		synchronized (sHardBitmapCache)
		{
			//System.out.println("sHardBitmapCache size = " + sHardBitmapCache.size());
			final Bitmap bitmap = sHardBitmapCache.get(url);
			if (bitmap != null)
			{
				// Bitmap found in hard cache
				// Move element to first position, so that it is removed last
			
				if(!bitmap.isRecycled()){
					sHardBitmapCache.remove(url);
					sHardBitmapCache.put(url, bitmap);
					return bitmap;
				}else{
					sHardBitmapCache.remove(url);
					return null;
				}
				
			}
		}

		// Then try the soft reference cache
		SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(url);
		if (bitmapReference != null)
		{
			final Bitmap bitmap = bitmapReference.get();
			if (bitmap != null)
			{
				// Bitmap found in soft cache
				return bitmap;
			} else
			{
				// Soft reference has been Garbage Collected
				sSoftBitmapCache.remove(url);
			}
		}

		return null;
	}

	/**
	 * The actual AsyncTask that will asynchronously download the image.
	 */
	class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap>
	{
		private static final int IO_BUFFER_SIZE = 4 * 1024;
		private String url;
		private final WeakReference<ImageView> imageViewReference;

		public BitmapDownloaderTask(ImageView imageView)
		{
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		/**
		 * Actual download method.
		 */
		@Override
		protected Bitmap doInBackground(String... params)
		{
			url = params[0];
			String param1 = params[1];
			String width ="0";
			String height="0";
			int dstwidth = 0;
			int dstheight = 0;
			if(params[2]!=null){
				width = params[2];
				dstwidth = Integer.parseInt(width);
			}
			if(params[3]!=null){
				height = params[3];
				dstheight = Integer.parseInt(height);
			}
			String fileName = getFile(url);
			DownLoadApplication.mFU.saveUrl(url, fileName);
			
			final Bitmap bitmap = DownLoadApplication.mFU.getBitmap(fileName,dstwidth,dstheight);
			return bitmap;
			/*
			final DefaultHttpClient client = new DefaultHttpClient();
			final HttpGet getRequest = new HttpGet(url);
			if (param1 != null)
			{
				getRequest.setHeader("cookie", param1);
			}

			try
			{
				HttpResponse response = client.execute(getRequest);
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK)
				{
					Log.w("ImageDownloader", "Error " + statusCode
							+ " while retrieving bitmap from " + url);
					return null;
				}

				final HttpEntity entity = response.getEntity();
				if (entity != null)
				{
					InputStream inputStream = null;
					OutputStream outputStream = null;
					float scale_tmp =0;
					int scale =0;
					try
					{
						inputStream = entity.getContent();
						

						BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.RGB_565;   
                        options.inPurgeable = true;  
                        options.inInputShareable = true;  
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);

						
						if (UseLocalCache) {
							String fileName = getFile(url);
							DownLoadApplication.mFU.saveBmp(bitmap, fileName);
							if(!bitmap.isRecycled()){
								bitmap.recycle();
							}
							bitmap = DownLoadApplication.mFU.getBitmap(fileName,dstwidth,dstheight);
						}
						// FIXME : Should use BitmapFactory.decodeStream(inputStream) instead.
						// final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
						
						
						return bitmap;

					} finally
					{
						if (inputStream != null)
						{
							inputStream.close();
						}
						if (outputStream != null)
						{
							outputStream.close();
						}
						entity.consumeContent();
					}
				}
			} catch (IOException e)
			{
				getRequest.abort();
				Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url,
						e);
			} catch (IllegalStateException e)
			{
				getRequest.abort();
				Log.w(LOG_TAG, "Incorrect URL: " + url);
			} catch (Exception e)
			{
				getRequest.abort();
				Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
			} finally
			{
				if (client != null)
				{
					client.getConnectionManager().shutdown();
				}
			}
			return null;
			*/
		}

		/**
		 * Once the image is downloaded, associates it to the imageView
		 */
		@Override
		protected void onPostExecute(Bitmap bitmap)
		{
			if (isCancelled())
			{
				bitmap = null;
			}

			// Add bitmap to cache
			if (bitmap != null)
			{
				synchronized (sHardBitmapCache)
				{
					sHardBitmapCache.put(getPureUrl(url), bitmap);
				}
			}else return ;

			if (imageViewReference != null)
			{
				ImageView imageView = imageViewReference.get();
				BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
				// Change bitmap only if this process is still associated with
				// it
				if (this == bitmapDownloaderTask)
				{
					Drawable d = new BitmapDrawable(bitmap);
					//imageView.setBackgroundDrawable(d);
					imageView.setImageDrawable(d);
					//imageView.setImageBitmap(bitmap);
					setVisibility(imageView);
				}
			}
		}

		public void copy(InputStream in, OutputStream out) throws IOException
		{
			byte[] b = new byte[IO_BUFFER_SIZE];
			int read;
			while ((read = in.read(b)) != -1)
			{
				out.write(b, 0, read);
			}
		}

	}

	/**
	 * A fake Drawable that will be attached to the imageView while the download
	 * is in progress.
	 * 
	 * <p>
	 * Contains a reference to the actual download task, so that a download task
	 * can be stopped if a new binding is required, and makes sure that only the
	 * last started download process can bind its result, independently of the
	 * download finish order.
	 * </p>
	 */
	static class DownloadedDrawable extends ColorDrawable
	{
		private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

		public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask)
		{
			super(Color.TRANSPARENT);
			bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(
					bitmapDownloaderTask);
		}

		public BitmapDownloaderTask getBitmapDownloaderTask()
		{
			return bitmapDownloaderTaskReference.get();
		}
	}
}
