package com.neteast.clouddisk.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lib.cloud.LibCloud;
import com.lib.db.AppDao;
import com.lib.db.DataInfo;
import com.lib.net.WeiboException;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.service.UploadService;
import com.neteast.clouddisk.utils.DataHelpter;


public class UploadRecordAdapter extends BaseAdapter {
	private LayoutInflater mInflater = null;
	private List<DataInfo> mList;
	private AppDao UploadDao = null;
	private Context mcontext=null;
	private LibCloud libCloud; 
	private Handler mHandler = null;
	public UploadRecordAdapter(Context context,  List<DataInfo> list, Handler myHandler) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		mList = list;
		UploadDao = AppDao.getInstance(context);
		mcontext = context;
		libCloud = LibCloud.getInstance(mcontext);
		mHandler = myHandler;
	}

	public int getCount() {
		return mList.size();
	}

	public Object getItem(int position) {
		return mList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	public void setUploadRecordList(List<DataInfo> list){
		mList = list;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		if(mList==null)
			return convertView;
		if (convertView == null && null != mInflater) {
			convertView = mInflater.inflate(R.layout.item_upload_record, null);
			
		}
		if(position >= getCount()){
			System.out.println("position = " + position + "getCount = " + getCount());
			return convertView;
		}
		final DataInfo dataInfo  = (DataInfo)mList.get(position);
		
		dataInfo.setPosition(position);
		//DownloadInfo info = (DownloadInfo) getItem(position);
		//convertView.setTag(mDownloadList.get(position));
		//ImageView image = (ImageView) convertView.findViewById(R.id.record_ico);
		TextView uploading_title = (TextView) convertView.findViewById(R.id.uploading_name);
		TextView uploaded_title = (TextView) convertView.findViewById(R.id.uploaded_name);
		TextView filesize = (TextView) convertView.findViewById(R.id.record_filesize);
		TextView uploadTime = (TextView) convertView.findViewById(R.id.record_uploadtime);
		ProgressBar progress = (ProgressBar) convertView.findViewById(R.id.uploading_progress);
		TextView tvPercent=(TextView) convertView.findViewById(R.id.upload_percent);
		
		LinearLayout uploadingView = (LinearLayout) convertView.findViewById(R.id.record_uploading);
		LinearLayout uploadedView = (LinearLayout) convertView.findViewById(R.id.record_uploaded);
		final Button cancel = (Button) convertView.findViewById(R.id.btn_cancel);
		final Button retry = (Button) convertView.findViewById(R.id.btn_retry);
		cancel.setTag(dataInfo);
		retry.setTag(dataInfo);
		cancel.setOnClickListener(new OnClickListener() {   
            @Override
            public void onClick(View v) { 
                // TODO Auto-generated method stub 
            	System.out.println("!!!!!!!!!!cancel btn clicked !!!!!!!!!!");
            	DataInfo di = (DataInfo) cancel.getTag();
            	mList.remove(di.getPosition());
            	notifyDataSetChanged(); 
            	UploadDao.updateUploadStatus(Integer.parseInt(di.getId()),Integer.parseInt(di.getFileid()), 3);
            	DelFileTask deltask = new DelFileTask();
            	if(di.getStatus()==1){
            		deltask.execute(di.getFileid());
            	}
            } 
        }); 
		retry.setOnClickListener(new OnClickListener() {   
            @Override
            public void onClick(View v) { 
                // TODO Auto-generated method stub 
            	System.out.println("!!!!!!!!!!cancel btn clicked !!!!!!!!!!");
            	DataInfo di = (DataInfo) cancel.getTag();
            	File tmp = new File(di.getUrl());
            	System.out.println("reupload file = " + di.getUrl());
            	if(tmp.exists()){
	            	mList.remove(di.getPosition());
	            	notifyDataSetChanged(); 
	            	List<DataInfo> list =  new ArrayList<DataInfo>();
	            	//UploadDao.updateUploadStatus(Integer.parseInt(di.getId()),Integer.parseInt(di.getFileid()), 0);
	            	UploadDao.removeUploadTask(Integer.parseInt(di.getId()),Integer.parseInt(di.getFileid()));
	            	list.add(di);
	            	UploadDao.addToUploadTask2(list);
	            	mcontext.startService(new Intent(mcontext, UploadService.class));
	            	if(mHandler!=null){
	            		mHandler.sendEmptyMessage(0);
	            	}
            	}
            } 
        }); 
		//System.out.println("data info id=" + dataInfo.getId() + "fileid = " + dataInfo.getFileid() + "status = " + dataInfo.getStatus() + "name = " + dataInfo.getName());
		
		if(dataInfo.getStatus()==1){
			uploadedView.setVisibility(View.GONE);
			uploadingView.setVisibility(View.VISIBLE);
			cancel.setVisibility(View.VISIBLE);
			retry.setVisibility(View.GONE);
			//System.out.println("upload Byte = " + dataInfo.getProgress() + "file size = " + dataInfo.getFileSize() + "file name = " + dataInfo.getName());
			int value = (int) (dataInfo.getProgress()*100/dataInfo.getFileSize());
			progress.setProgress(value);
			uploading_title.setText(dataInfo.getName());
			tvPercent.setText("已完成"+ value +"%");
			
		}else if(dataInfo.getStatus()==0){
			uploadedView.setVisibility(View.GONE);
			uploadingView.setVisibility(View.VISIBLE);
			cancel.setVisibility(View.VISIBLE);
			retry.setVisibility(View.GONE);
			progress.setProgress(0);
			uploading_title.setText(dataInfo.getName());
			tvPercent.setText("已完成"+ 0 +"%");
		}else if(dataInfo.getStatus()==99){
			uploadedView.setVisibility(View.GONE);
			uploadingView.setVisibility(View.VISIBLE);
			cancel.setVisibility(View.INVISIBLE);
			retry.setVisibility(View.VISIBLE);
			int value = (int) (dataInfo.getProgress()*100/dataInfo.getFileSize());
			progress.setProgress(value);
			uploading_title.setText(dataInfo.getName());
			tvPercent.setText("上传失败");
		}
		else{
			uploadingView.setVisibility(View.GONE);
			uploadedView.setVisibility(View.VISIBLE);
			uploaded_title.setText(dataInfo.getName());
		}
		String fileSize ="";
		long size = dataInfo.getFileSize();
		if(size <1024){
			fileSize = size+"Byte";
		}else if(size <1024*1024){
			fileSize =  size/1024 + "KB";
		}else if(size <1024*1024*1024){
			fileSize =  size/(1024*1024) + "MB";
		}else if(size <1024*1024*1024*1024){
			fileSize =  size/(1024*1024*1024) + "GB";
		}
			
		
		filesize.setText(fileSize);
		uploadTime.setText(dataInfo.getAddTime());
	
		
		return convertView;
	}
	
	class DelFileTask extends AsyncTask<Object, Integer, String> {
		int firstrun = 0;
		@Override
		protected String doInBackground(Object... params) {
			String fileid = (String) params[0];
			try {
    			libCloud.Delete_file(fileid);
    		} catch (WeiboException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			
		}
	}
}
