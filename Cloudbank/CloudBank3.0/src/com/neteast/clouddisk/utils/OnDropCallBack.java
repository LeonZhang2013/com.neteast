package com.neteast.clouddisk.utils;

import com.lib.db.DataInfo;

public interface OnDropCallBack {
	void onDropCompleted(DataInfo sourceInfo,DataInfo destInfo); 
}
