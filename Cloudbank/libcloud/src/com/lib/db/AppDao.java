package com.lib.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;


public class AppDao {
	private static SQLiteHelpler db = null;
	private static AppDao dao = null;
	public final String DB_NAME="clouddisk_db.db3";
	public final int DB_VERSION = 3;
	public static final int UPLOAD_IDLE = 0;
	public static final int UPLOAD_ING = 1;
	public static final int UPLOAD_DONE = 2;
	public static final int UPLOAD_SUSPEND = 3;
	
	public static final int UPLOAD_ERROR = 99;
	private AppDao(Context context) {
		db = new SQLiteHelpler(context, DB_NAME, null, DB_VERSION);
	}

	public static AppDao getInstance(Context context) {
		if (dao == null) {
			dao = new AppDao(context);
			return dao;
		} else {
			return dao;
		}
	}

	public static AppDao getDao() {
		if (dao != null) {
			return dao;
		} else {
			return null;
		}
	}
	/*
	 * ͨ��������ɾ����??
	 */
	public void deleteAppBtPackage(String packages){
		String sql = "delete from clouddisk_table where package=?;";
		try {
			db.getWritableDatabase().execSQL(sql,new String[]{packages});
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
	public Map<String,DataInfo> getAppList(){
		 Map<String,DataInfo> list = new  HashMap<String,DataInfo>();
		String sql = "select * from clouddisk_table;";
		Cursor cursor = null;
		try {
			cursor = db.getReadableDatabase().rawQuery(sql,new String[]{});
			list = convertCursor2Map(cursor);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
				cursor = null;
			}
		}
		return list;
	}

	public Integer getAppIdByPackage(String packageName) {
		Integer id = null;
		String sql = "select id from clouddisk_table where package=?";
		Cursor cursor = null;
		try {
			cursor = db.getReadableDatabase().rawQuery(sql,
					new String[] { packageName });
			if (cursor.moveToFirst()) {
				id = cursor.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
				cursor = null;
			}
		}

		return id;
	}

	public List<DataInfo> getAppById(int id) {
		List<DataInfo> list = new ArrayList<DataInfo>();
		String sql = "select * from clouddisk_table where id=?";
		Cursor cursor = null;
		try {
			cursor = db.getReadableDatabase().rawQuery(sql,
					new String[] { id + "" });
			list = convertCursor2List(cursor);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
				cursor = null;
			}
		}

		return list;
	}

	public void insertApp(int appId, int resId, String name, String url,
			int status, String version, String packageName) {
		String sql = "insert into clouddisk_table(id,resid,name,url,status,version,package) values(?,?,?,?,?,?,?)";
		db.getWritableDatabase().execSQL(
				sql,
				new Object[] { appId, resId, name, url, status, version,
						packageName });
	}

	public void updateAppStatusAndPackage(int status, String packageName,
			int appId) {
		String sql = "update clouddisk_table set  status=? ,package=?  where id=?";
		db.getWritableDatabase().execSQL(sql,
				new Object[] { status, packageName, appId });
	}
	public void addToUploadTask(List<DataInfo> list){
		if(list==null || list.size()==0){
			return ;
		}
		
		String sql = "insert into clouddisk_upload_table(fileid,type,name,status,path,filesize) values(0,?,?,?,?,?)";
		for(int i=0;i<list.size();i++){
			DataInfo data = list.get(i);
			List<DataInfo> uploadingList = getUploadingListByName(data.getName());
			if(uploadingList==null || uploadingList.size()==0){
				db.getWritableDatabase().execSQL(
						sql,
						new Object[] { data.getType(), data.getName(),0,data.getUrl(),data.getFileSize()});
			}
		}
	}
	public void addToUploadTask2(List<DataInfo> list){
		if(list==null || list.size()==0){
			return ;
		}	
		String sql = "insert into clouddisk_upload_table(fileid,type,name,path,status,progress,filesize,server,isnew,slicedone,slicetotal) values(?,?,?,?,?,?,?,?,?,?,?)";
		for(int i=0;i<list.size();i++){
			DataInfo data = list.get(i);
			List<DataInfo> uploadingList = getUploadingListByName(data.getName());
			if(uploadingList==null || uploadingList.size()==0){
				db.getWritableDatabase().execSQL(
						sql,
						new Object[] {data.getFileid(),data.getType(), data.getName(),data.getUrl(),0,data.getProgress(),data.getFileSize(),data.getServier(),1,data.getSliceDone(),data.getSliceDone()});
			}
		}
	}
	private List<DataInfo> getUploadingListByName(String filename){
		List<DataInfo> list = new ArrayList<DataInfo>();
		String sql = "select * from clouddisk_upload_table where (status=0 or status=1) and name=?";
		Cursor cursor = null;
		try {
			cursor = db.getReadableDatabase().rawQuery(sql,
					new String[] {  filename });
			list = convertUploadCursor2List(cursor);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
				cursor = null;
			}
		}
		
		return list;
	}
	public List<DataInfo> getUploadList(int status){
		List<DataInfo> list = new ArrayList<DataInfo>();
		String sql = "select * from clouddisk_upload_table where status=?";
		Cursor cursor = null;
		try {
			cursor = db.getReadableDatabase().rawQuery(sql,
					new String[] { status + "" });
			list = convertUploadCursor2List(cursor);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
				cursor = null;
			}
		}
		
		return list;
	}
	public List<DataInfo> getUploadingTask(int id,int fileid){
		int progress = 0;
		List<DataInfo> list = new ArrayList<DataInfo>();
		//System.out.println("getUploadProgress  start");
		String sql = "select * from clouddisk_upload_table where fileid=? and id=?";
		Cursor cursor = null;
		try {
			cursor = db.getReadableDatabase().rawQuery(sql,
					new String[] { fileid+"",id+"" });
			list = convertUploadCursor2List(cursor);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
				cursor = null;
			}
		}

		return list;
	}
	public void updateUploadProgress(int id,int fileid, int progress) {
		//System.out.println("updateUploadProgress id= "+ id+"fileid= "+ fileid + "progress = " + progress);
		String sql = "update clouddisk_upload_table set  progress=?  where id=? and fileid=?";
		db.getWritableDatabase().execSQL(sql,
				new Object[] { progress,id,fileid });
	}
	public void updateUploadStatus(int id,int fileid, int status) {
		String sql = "update clouddisk_upload_table set  status=?  where id=? and fileid=?";
		System.out.println("sql id=" + id + "fileid = " + fileid + "status" + status);
		db.getWritableDatabase().execSQL(sql,
				new Object[] { status,id,fileid });
		
	}
	public void removeUploadTask(int id,int fileid){
		String sql = "delete from clouddisk_upload_table where id=? and fileid=?";
		db.getWritableDatabase().execSQL(sql, new Object[] {id,fileid});
	}
	public void removeFaliedTask(int status){
		String sql = "delete from clouddisk_upload_table where status=?";
		db.getWritableDatabase().execSQL(sql, new Object[] {status});
	}
	public int  getUploadStatus(int id,int fileid) {
		String sql = "select * from clouddisk_upload_table where id =? and fileid=?";
		Cursor cursor = null;
		int status = 1;
		try {
			cursor = db.getReadableDatabase().rawQuery(sql,
					new String[] { id+"",fileid+"" });
			while (cursor.moveToNext()) {
				status = cursor.getInt(5);
				break;

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
				cursor = null;
			}
		}
		return status;
	}
	public void updateUploadTask(int id,int fileid, String server,int status) {
		String sql = "update clouddisk_upload_table set  fileid=?,status=?,server=? where id=?";
		db.getWritableDatabase().execSQL(sql,
				new Object[] { fileid, status,server,id});
	}
	public void updateUploadSlice(int id,int fileid ,int slice_num, int slice_total) {
		
		String sql = "update clouddisk_upload_table set slicedone=?,slicetotal=? where fileid=? and id=?";
		db.getWritableDatabase().execSQL(sql,
				new Object[] { slice_num, slice_total, fileid,id });
		
	}
	public void updateUploadFailedTask(int status) {
		
		String sql = "update clouddisk_upload_table set status=? where status=99";
		db.getWritableDatabase().execSQL(sql,
				new Object[] {status});
		
	}

	private Map<String,DataInfo> convertCursor2Map(Cursor cursor) {
		Map<String,DataInfo> m = new HashMap<String,DataInfo>();
		while (cursor.moveToNext()) {
			DataInfo dataInfo = new DataInfo();
			dataInfo.setId(cursor.getInt(0)+"");
			dataInfo.setResid(cursor.getInt(1)+"");
			dataInfo.setName(cursor.getString(2));
			dataInfo.setUrl(cursor.getString(3));
			dataInfo.setStatus(cursor.getInt(4));
			dataInfo.setVersion(cursor.getString(5));
			dataInfo.setPackages(cursor.getString(6));
			m.put(dataInfo.getId(),dataInfo);
		}
		return m;

	}
	private List<DataInfo> convertCursor2List(Cursor cursor) {
		List<DataInfo> list = new ArrayList<DataInfo>();
		while (cursor.moveToNext()) {
			DataInfo dataInfo = new DataInfo();
			dataInfo.setId(cursor.getInt(0)+"");
			dataInfo.setResid(cursor.getInt(1)+"");
			dataInfo.setName(cursor.getString(2));
			dataInfo.setUrl(cursor.getString(3));
			dataInfo.setStatus(cursor.getInt(4));
			dataInfo.setVersion(cursor.getString(5));
			dataInfo.setPackages(cursor.getString(6));
			list.add(dataInfo);
		}
		return list;
	}
	private List<DataInfo> convertUploadCursor2List(Cursor cursor) {
		List<DataInfo> list = new ArrayList<DataInfo>();
		//cursor.moveToFirst();
		while (cursor.moveToNext()) {
			DataInfo dataInfo = new DataInfo();
			dataInfo.setId(cursor.getInt(0)+"");
			dataInfo.setFileid(cursor.getInt(1)+"");
			dataInfo.setType(cursor.getInt(2)+"");
			dataInfo.setName(cursor.getString(3));
			dataInfo.setUrl(cursor.getString(4));
			dataInfo.setStatus(cursor.getInt(5));
			dataInfo.setProgress(cursor.getLong(6));
			dataInfo.setFileSize(cursor.getInt(7));
			dataInfo.setServer(cursor.getString(8));
			dataInfo.setSliceDone(cursor.getInt(10));
			dataInfo.setSliceTotal(cursor.getInt(11));
			list.add(dataInfo);
		}
		return list;
	}

}
