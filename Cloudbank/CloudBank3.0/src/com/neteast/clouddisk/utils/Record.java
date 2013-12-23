package com.neteast.clouddisk.utils;

import com.neteast.clouddisk.R;



/**
 * 表示上传记录的实体类，测试用，字段都是为了方便起见，随便起的
 * @author tiangh
 * 2012-4-27 上午11:09:14
 */
public class Record {
	public FileType fileType;
	public String fileName;
	public String fileSize;
	public String uploadTime;
	
	public String destDir;
	public int finishedSize;
	
	
	public static enum FileType{
		AUDIO {
			public int getResource() { return R.drawable.ico_audio;}
		},VIDEO {
			public int getResource() { return R.drawable.ico_video;}
		},PIC {
			public int getResource() { return R.drawable.ico_pic;}
		},TEXT {
			public int getResource() { return R.drawable.ico_text;}
		},FOLDER {
			public int getResource() { return R.drawable.ico_foder;}
		};
		public abstract int getResource();
	}
}
