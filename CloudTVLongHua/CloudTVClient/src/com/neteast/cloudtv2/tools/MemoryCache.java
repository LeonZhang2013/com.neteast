package com.neteast.cloudtv2.tools;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;

public class MemoryCache {
	private static final int HARD_CACHE_CAPACITY = 15;
	private HashMap<String, Bitmap> mHardBitmapCache;
	private final static ConcurrentHashMap<String, SoftReference<Bitmap>> mSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
			HARD_CACHE_CAPACITY / 2);

	public MemoryCache() {
		mHardBitmapCache = new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY / 2, 0.75f, true) {
			private static final long serialVersionUID = 1L;
			@Override
			protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) {
				if (size() > HARD_CACHE_CAPACITY) {
					// Entries push-out of hard reference cache are transferred to soft reference cache
					mSoftBitmapCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
					return true;
				} else
					return false;
			}
		};

	}

	protected Bitmap getBitmapFromCache(String url, boolean bRenew) {
		synchronized (mHardBitmapCache) {
			final Bitmap bitmap = mHardBitmapCache.get(url);
			if (bitmap != null) {
				if (bRenew) {
					mHardBitmapCache.remove(url);
					mHardBitmapCache.put(url, bitmap);
				} else {
					mHardBitmapCache.remove(url);
				}
				// System.out.println("find bitmap in hardcache. url:"+url);
				return bitmap;
			}
		}
		SoftReference<Bitmap> bitmapReference = mSoftBitmapCache.get(url);
		if (bitmapReference != null) {
			final Bitmap bitmap = bitmapReference.get();
			if (bitmap != null) {
				if (bRenew) {
					mHardBitmapCache.put(url, bitmap);
					mSoftBitmapCache.remove(url);
				} else {
					mSoftBitmapCache.remove(url);
				}
				// System.out.println("find bitmap in softcache. url:"+url);
				return bitmap;
			} else {
				mSoftBitmapCache.remove(url);
			}
		}
		return null;
	}

	public Bitmap get(String url) {
		return getBitmapFromCache(url, true);
	}

	public void del(String url) {
		Bitmap bitmap = getBitmapFromCache(url, false);
		if (bitmap != null && bitmap.isRecycled() == false) {
			bitmap.recycle();
			bitmap = null;
		}
	}

	public void put(String url, Bitmap bitmap) {
		if (bitmap != null) {
			synchronized (mHardBitmapCache) {
				mHardBitmapCache.put(url, bitmap);
			}
		}
	}

	public void clear() {
		Iterator hIter = mHardBitmapCache.entrySet().iterator();
		while (hIter.hasNext()) {
			Map.Entry<String, Bitmap> entry = (Map.Entry<String, Bitmap>) hIter.next();
			Bitmap bitmap = (Bitmap) (entry.getValue());
			if (bitmap != null && bitmap.isRecycled() == false) {
				bitmap.recycle();
				bitmap = null;
			}
			//Log.i("show","hJheap:"+Runtime.getRuntime().totalMemory()+",Nheap:"+Debug.getNativeHeapAllocatedSize());
		}
		mHardBitmapCache.clear();

		Iterator sIter = mSoftBitmapCache.entrySet().iterator();
		while (sIter.hasNext()) {
			Map.Entry<String, SoftReference<Bitmap>> entry = (Map.Entry<String, SoftReference<Bitmap>>) sIter.next();
			Bitmap bitmap = (Bitmap) (entry.getValue().get());
			if (bitmap != null && bitmap.isRecycled() == false) {
				bitmap.recycle();
				bitmap = null;
			}

		}

		mSoftBitmapCache.clear();
		System.gc();

	}

}