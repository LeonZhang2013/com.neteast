package com.neteast.clouddisk.utils;

import java.util.ArrayList;
import java.util.List;
/**
 * 提供测试用数据的帮助类
 * @author tiangh
 * 2012-4-27 下午1:21:41
 */
public class Generator {
	
	public static List<Record> genRecords(int size){
		List<Record> records=new ArrayList<Record>();
		Record record;
		for(int i=0;i<size;i++){
			record=new Record();
			record.fileType=Record.FileType.AUDIO;
			record.fileName="海阔天空"+i+".mp4";
			record.fileSize="4.5M";
			record.uploadTime="2012/03/17/17:35";
			record.destDir="上传至：个人空间>文件>我的网盘>文件>新建文件夹";
			record.finishedSize=65;
			records.add(record);
		}
		return records;
	}
}
