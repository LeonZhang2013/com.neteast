package com.neteast.cloudtv2.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Comparator;

import com.neteast.cloudtv2.Constant;

import android.content.Context;

/**
 * 缓存文件
 * @author LeonZhang 
 * @Email zhanglinlang1@163.com
 */
public class FileCache {

	/** 缓存文件目录 */
	private File cacheDir;
	/** 缓存子路�?*/
	private String filedir = Constant.IMAGE_STORE_PATH;
	/** 图片后缀�?*/
	private final String imagerFlag = ".cloud";
	/**
	 * 创建缓存目录
	 * @param context
	 */
	public FileCache(Context context) {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), filedir);
		else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	/**
	 * 返回目录的�?大小
	 * @param dir 目录
	 * @return 目录大小 byte
	 */
	private static long dirSize(File dir) {
		long result = 0;
		File[] fileList = dir.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isDirectory()) {
				result += dirSize(fileList[i]);
			} else {
				result += fileList[i].length();
			}
		}
		return result;
	}

	/** 
	 * 使用URL来命名文件并转换URL中的不能作为文件名使用的特殊符号�?
	 * @param url
	 * @return */
	public File getFile(String url) {
		String filename = URLEncoder.encode(url);
		File f = new File(cacheDir, filename+imagerFlag);
		//Log.i("image", f.toString());
		return f;
	}

	/**
	 * 删除缓存目录下的�?��文件
	 */
	public void clearNativeFile() {
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}
	
	/**
	 * 当本地文件�?大小超过 cacheMaxInByte，会删除长久没有使用的文件�?
	 * @param cacheMaxInByte 允许保存的本地图片数�?
	 */
	public void removeEldestFile(long cacheMaxInByte) {
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		Arrays.sort(files, lastModified);
		long curcache = dirSize(cacheDir);
		for (File f : files) {
			if (curcache > cacheMaxInByte) {
				f.delete();
				curcache -= f.length();
			} else {
				return;
			}
		}
	}
	
	/**
	 * 文件比较�?  对两个文件的�?��修改日期做匹配�?
	 */
	private static final Comparator<File> lastModified = new Comparator<File>() {
		@Override
		public int compare(File o1, File o2) {
			return o1.lastModified() == o2.lastModified() ? 0 : (o1.lastModified() < o2.lastModified() ? 1 : -1);
		}
	};
	
	
	public static void saveFileLocal(InputStream is, File f) throws IOException {
		final int buffer_size = 1024;
		OutputStream os = null;
		try {
			os = new FileOutputStream(f);
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} finally {
			if (os != null)
				os.close();
		}
	}
}