package com.lib.net;

import java.io.File;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Comparator;

import android.content.Context;

public class FileCache {
    
    private File cacheDir;
    private String filedir="ImageCache";
    
    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),filedir);
        else
            cacheDir=new File("/mnt/sdcard/"+filedir);//context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
    
    private static final Comparator<File> lastModified = new Comparator<File>() {
		@Override
		public int compare(File o1, File o2) {
			return o1.lastModified() == o2.lastModified() ? 0 : (o1.lastModified() < o2.lastModified() ? 1 : -1 ) ;
		}
	};
	
	/**
	 * Return the size of a directory in bytes
	 */
	private static long dirSize(File dir) {
	    long result = 0;
	    File[] fileList = dir.listFiles();

	    for(int i = 0; i < fileList.length; i++) {
	        // Recursive call if it's a directory
	        if(fileList[i].isDirectory()) {
	            result += dirSize(fileList [i]);
	        } else {
	            // Sum the file size in bytes
	            result += fileList[i].length();
	        }
	    }
	    return result; // return the file size
	}

    public File getFile(String url){
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        //String filename=String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
        String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;
        
    }
    
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

    public void removeEldestFile(long cacheMaxInByte){
    	File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        Arrays.sort(files, lastModified);
        long curcache = dirSize(cacheDir);
        for(File f:files){
        	if(curcache > cacheMaxInByte){
        		// System.out.println( curcache+ " > " + cacheMaxInByte);
        		f.delete();
        		curcache -= f.length();
        	}else{
        		return;
        	}        	
        }
    }
}