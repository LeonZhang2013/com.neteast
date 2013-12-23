package com.neteast.androidclient.newscenter.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageUtil {
    
    public static interface ImageCallback{
        public void imageLoaded(Bitmap imageBitmap, String imageUrl);
    }
    
    private static final String TAG="ImageUtil";
    private Map<String, SoftReference<Bitmap>> imageCache;
    private static  ImageUtil imageUtil=new ImageUtil();
    private File imageDir;
    
    private ImageUtil(){
        imageDir=createImageDir(); 
        imageCache=new HashMap<String, SoftReference<Bitmap>>();
    }
    
    public static ImageUtil getInstance() {
        return imageUtil;
    }
    
    private File createImageDir() {
       File dir=new File(Environment.getExternalStorageDirectory(), "newscenter/pic");
       dir.mkdirs();
       return dir;
    }
    
    public Bitmap loadImage(String path,ImageCallback callback) {
        Bitmap bitmap = getBitmapFromCache(path);
        if (bitmap!=null) {
            setBitmapToMemory(path, bitmap);
            return bitmap;
        }else {
            getBitmapFromInternet(path,callback);
            return null;
        }
    }

    private void getBitmapFromInternet(String path,ImageCallback callback) {
        new LoadImageTask().execute(path, callback);
    }
    
    public Bitmap getBitmapFromCache(String path) {
        Bitmap bitmap = getBitmapFromMemory(path);
        if (bitmap!=null) {
            return bitmap;
        }
        return getBitmapFromDisk(path);
    }
    
    private Bitmap getBitmapFromMemory(String path) {
        if (imageCache.containsKey(path)) {
            Bitmap bitmap = imageCache.get(path).get();
            if (bitmap != null) {
                return bitmap;
            }
        }
        return null;
    }
    
    private void setBitmapToMemory(String path,Bitmap bitmap) {
        imageCache.put(path, new SoftReference<Bitmap>(bitmap));
    }
    
    private Bitmap getBitmapFromDisk(String path) {
        File file = getFile(path);
        if (file.exists() && file.length() > 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bitmap != null) {
                return bitmap;
            }
        }
        return null;
    }
    /**
     * 根据图片的网络地址，找到对应缓存的本地路径，若没有缓存，返回null
     * @param netPath
     * @return
     */
    public String getLocalImagePath(String netPath) {
        File file = getFile(netPath);
        if (file.exists()&&file.length()>0) {
            return file.getAbsolutePath();
        }else {
            return null;
        }
    }
    /**
     * 下载图片保存到磁盘
     * <P>不要在主线程调用该方法</P>
     * @param path
     */
    public boolean downloadImageFile(String path) {
        if (path==null) {
            return false;
        }
        try {
            File file = getFile(path);
            FileOutputStream out = new FileOutputStream(file);
            InputStream in = new URL(path).openStream();
            Utils.downloadFile(in, out);
            return true;
        } catch (IOException e) {}
        return false;
    }
    
    public void deleteImage(String path) {
        if (path==null) {
            return;
        }
        imageCache.remove(path);
        getFile(path).delete();
    }
    
    public File getFile(String path) {
        String fileName=path.substring(path.lastIndexOf("/")+1);
        return new File(imageDir,fileName);
    }
    
    class LoadImageTask extends AsyncTask<Object, Integer, Bitmap> {
        private ImageCallback mCallback;
        private String mPath;

        @Override
        protected Bitmap doInBackground(Object... params) {
            mPath = (String) params[0];
            mCallback = (ImageCallback) params[1];
            File file=getFile(mPath);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                URL url = new URL(mPath);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5 * 1000);
                conn.setRequestMethod("GET");
                Bitmap bitmap = BitmapFactory.decodeStream(conn.getInputStream());
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                setBitmapToMemory(mPath, bitmap);
                return bitmap;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                mCallback.imageLoaded(result, mPath);
            }
        }
    }
}
