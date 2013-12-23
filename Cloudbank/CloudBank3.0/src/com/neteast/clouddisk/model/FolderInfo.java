package com.neteast.clouddisk.model;

import java.util.ArrayList;
import java.util.List;

import com.lib.db.DataInfo;

public class FolderInfo extends DataInfo {
	public String folderName;
	
	public List<DataInfo> mFolderList;
	
	public FolderInfo(){
		mFolderList = new ArrayList<DataInfo>();
	}
}
