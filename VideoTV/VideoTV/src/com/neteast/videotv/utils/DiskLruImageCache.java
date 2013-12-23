package com.neteast.videotv.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import com.android.volley.toolbox.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-9-5
 * Time: 下午2:39
 */
public class DiskLruImageCache implements ImageLoader.ImageCache {

    private SimpleDiskCache mCache;
    private static DiskLruImageCache Instance;

    public static final synchronized DiskLruImageCache getInstance(Context context){
        if (Instance==null){
            Instance=new DiskLruImageCache(context);
        }
        return Instance;
    }

    private DiskLruImageCache(Context context){
        File rootDir=null;
        int maxCache=0;
        boolean useExtStorage = false;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            rootDir=new File(Environment.getExternalStorageDirectory(),"/videotv/");
            useExtStorage = rootDir.mkdirs();
            maxCache=10*1024*1024;
           // maxCache = (int) (Runtime.getRuntime().maxMemory()/8/1024);
        }
        if (!useExtStorage){
            rootDir=context.getCacheDir();
           maxCache=5*1024*1024;
           //maxCache = (int) (Runtime.getRuntime().maxMemory()/8/1024);
        }
        try {
            mCache = SimpleDiskCache.open(rootDir,1,maxCache);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Bitmap getBitmap(String url) {
        try {
            SimpleDiskCache.BitmapEntry bitmap = mCache.getBitmap(url);
            if (bitmap!=null){
                return bitmap.getBitmap();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        try {
            OutputStream outputStream = mCache.openStream(url);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
