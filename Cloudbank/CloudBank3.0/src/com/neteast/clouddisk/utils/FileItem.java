package com.neteast.clouddisk.utils;

import java.io.File;

import com.neteast.clouddisk.R;

public class FileItem {
	private File file;
	private int ischecked = 0;
	
	public int getIsChecked(){
		return ischecked;
	}
	public void setIsChecked(int value){
		this.ischecked = value;
	}
	public File getFile(){
		return file;
	}
	public void setFile(File value){
		this.file = value;
	}
}
