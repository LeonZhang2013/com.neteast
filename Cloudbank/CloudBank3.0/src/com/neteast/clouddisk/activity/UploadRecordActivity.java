package com.neteast.clouddisk.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ActivityGroup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lib.cloud.LibCloud;
import com.lib.db.AppDao;
import com.lib.db.DataInfo;
import com.lib.net.WeiboException;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.adapter.UploadRecordAdapter;
import com.neteast.clouddisk.service.UploadService;
import com.neteast.clouddisk.utils.DataHelpter;
import com.neteast.clouddisk.utils.DownLoadApplication;
import com.neteast.clouddisk.utils.UIHelper;

public class UploadRecordActivity extends ActivityGroup {
	private String TAG = "UploadRecordActivity";
	private TextView tvPage;
	
	private final static Double SINGLE_PAGE_ITEMS=6.00;
	private static int totalPages;
	private LibCloud libCloud; 
	private ListView mUploadRecordListView = null;
	private UploadRecordAdapter adapter = null;
	private ImageButton returnbtn;
	private ImageButton clearallbtn;
	private ImageButton reuploadallbtn = null;
	private LinearLayout reuploadView = null;
	private TextView uploadTitle;
	private List<DataInfo> records=new ArrayList<DataInfo>();
	private List<DataInfo> uploadingList = new ArrayList<DataInfo>();
	private List<DataInfo> waitingList = new ArrayList<DataInfo>();
	private List<DataInfo> errorList = new ArrayList<DataInfo>();
	private List<DataInfo> mList = new ArrayList<DataInfo>();
	private AppDao UploadDao = null;
	int uploadType = 1;
	private String uploadingFileid = null;
	private boolean uploadFinished = false;
	
	TextView totalText1 =null;
	TextView usedText1 =null;
	TextView nousedText1 = null;
	TextView uploadnotestv = null;
	private boolean firstStart = true;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_upload_record);
		mUploadRecordListView = (ListView) findViewById(R.id.upload_record_list);
		returnbtn = (ImageButton) findViewById(R.id.uploadfile_return);
		clearallbtn = (ImageButton)findViewById(R.id.uploadfile_clearall);
		reuploadView =  (LinearLayout)findViewById(R.id.reuploadView);
		reuploadallbtn = (ImageButton)findViewById(R.id.reuploadbtn);
		uploadTitle = (TextView) findViewById(R.id.uploadfile_title);
		
		totalText1 = (TextView) findViewById(R.id.totalText1);
		usedText1 = (TextView) findViewById(R.id.usedText1);
		nousedText1 = (TextView) findViewById(R.id.nousedText1);
		uploadnotestv = (TextView) findViewById(R.id.uploading_notes);
		
		libCloud = LibCloud.getInstance(this);
		UploadDao = AppDao.getInstance(this);
		addListener();
		uploadType = this.getIntent().getIntExtra("uploadType",1);
		waitingList = UploadDao.getUploadList(0);
		uploadingList = UploadDao.getUploadList(1);
		errorList = UploadDao.getUploadList(99);
		Log.v(TAG, "onCreateView " + "waiting list size =" + waitingList.size() +"uploading list size = " + uploadingList.size());
		
		for(int i=0;i< uploadingList.size();i++){
			mList.add(uploadingList.get(i));
			uploadingFileid = uploadingList.get(i).getFileid();
		}
		for(int i=0;i<errorList.size();i++){
			mList.add(errorList.get(i));
		}
		for(int i=0;i<waitingList.size();i++){
		
			mList.add(waitingList.get(i));
		}
		GetCapacityAsync capacity = new GetCapacityAsync();
		capacity.execute();
		
		GetDataTask gdt = new GetDataTask();
		gdt.execute(1);
		
		((DownLoadApplication)getApplication()).setUploadRecorderHandler(myHandler);	
		
	}
	@Override
	public void onDestroy()
	{
		myHandler.removeMessages(UPDATE_PROGRESS);
		super.onDestroy();
	        //System.out.println("***************** playback activity onDestroy\n");
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	/*
	 * 点击取消按钮
	 * */
	@SuppressWarnings("unchecked")
	public void uploadCancelButtonClick(View v) {
		/*
		Button cancelbt = (Button)v;
		DataInfo di = (DataInfo) cancelbt.getTag();
		System.out.println("****upload cancel btn click id =  "+ di.getId() + "name = " + di.getName());
		UploadDao.updateUploadStatus(Integer.parseInt(di.getId()),Integer.parseInt(di.getFileid()), 3);
		myHandler.sendEmptyMessage(UPDATE_PROGRESS);
		*/
	}
	
	
	private void addListener() {
		
		returnbtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Class cls = null;
				String selectName="";
				
				selectName = "MyUploadVideoActivity";
				switch (uploadType) {
				case 1:
					cls = MyUploadVideoActivity.class;
					selectName = "MyUploadVideoActivity";
					break;
				case 2:
					cls = MyUploadMusicActivity.class;
					selectName = "MyUploadMusicActivity";
					break;
				case 3:
					cls = MyUploadPictureActivity.class;
					selectName = "MyUploadPictureActivity";
					break;
				case 4:
					cls = MyUploadFileActivity.class;
					selectName = "MyUploadFileActivity";
					break;
				default:
					cls = MyUploadVideoActivity.class;
					selectName = "MyUploadVideoActivity";
					break;
				}
				RelativeLayout rl = (RelativeLayout) UploadRecordActivity.this
						.getParent().getWindow()
						.findViewById(R.id.datacontainer);
				rl.removeAllViews();
				Intent intent = new Intent(UploadRecordActivity.this, cls);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Window sub = ((ActivityGroup) UploadRecordActivity.this
						.getParent()).getLocalActivityManager().startActivity(
						selectName, intent);
				View view = sub.getDecorView();
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.FILL_PARENT);
				view.setLayoutParams(params);
				rl.addView(view);
			}
		});	
		clearallbtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				StringBuilder filestr = new StringBuilder();
				List<DataInfo> list = null;
				list= UploadDao.getUploadList(99);
				for(int i=0;i<list.size();i++){
					DataInfo info = list.get(i);
					filestr.append(info.getFileid());
					filestr.append(",");
				}
				int len = filestr.toString().length();
				if(len>0){
					String fileid = filestr.toString().substring(0, len-1);
					DelFileTask deltask = new DelFileTask();
					System.out.println("clearAllbtn clicked fileid = " + fileid);
					deltask.execute(fileid);
				}
				
				UploadDao.updateUploadFailedTask(3);
				clearallbtn.setVisibility(View.INVISIBLE);
				reuploadView.setVisibility(View.GONE);
				myHandler.sendEmptyMessage(UPDATE_PROGRESS);
			}
		});	
		reuploadallbtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				List<DataInfo> list = null;
				list= UploadDao.getUploadList(99);
				if(list.size()>0){
					UploadDao.removeFaliedTask(99);
					UploadDao.addToUploadTask2(list);
					clearallbtn.setVisibility(View.INVISIBLE);
					reuploadView.setVisibility(View.GONE);
					UploadRecordActivity.this.startService(new Intent(UploadRecordActivity.this, UploadService.class));
					myHandler.sendEmptyMessage(UPDATE_PROGRESS);
					uploadFinished = false;
				}
			}
		});	
		
	}
	public void addDataToGridView(List<DataInfo> result) {
		if(result.size()>0){
			records.clear();
		}
		if(uploadingList.size()>0 || waitingList.size()>0){
		//if(mList.size()>0){
			myHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000);
		}
		System.out.println("add data 2 grid result size = " + result.size());	
		String title = String.format(getResources().getString(R.string.upload_info_text),uploadingList.size()+waitingList.size(),result.size());
		uploadTitle.setText(title);
		if(result.size()>0){
			if(mList!=null){
				mList.clear();
			}
			for(int i=0;i< uploadingList.size();i++){
				mList.add(uploadingList.get(i));
				
			}
			
			for(int i=0;i<errorList.size();i++){
				mList.add(errorList.get(i));
			}
			
			for(int i=0;i<waitingList.size();i++){
				mList.add(waitingList.get(i));
			}
			for(int i=0;i<result.size();i++){
				records.add(result.get(i));
				mList.add(result.get(i));
			}
		}else{
			if(mList!=null){
				mList.clear();
			}
			for(int i=0;i< uploadingList.size();i++){
				mList.add(uploadingList.get(i));
				
			}
			
			for(int i=0;i<errorList.size();i++){
				mList.add(errorList.get(i));
			}
			
			for(int i=0;i<waitingList.size();i++){
				mList.add(waitingList.get(i));
			}
			for(int i=0;i<records.size();i++){
				mList.add(records.get(i));
			}
		}
		System.out.println("add data 2 grid mList size = " + mList.size());
		if(adapter==null){
			adapter = new UploadRecordAdapter(this, mList,myHandler);
			mUploadRecordListView.setAdapter(adapter);
		}else{
			adapter.notifyDataSetChanged();
		}
		
	}
	class GetDataTask extends AsyncTask<Object, Integer, List<DataInfo>> {
		int firstrun = 0;
		@Override
		protected List<DataInfo> doInBackground(Object... params) {
			List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();
			Map<String, Object> retmap=null;
			firstrun = (Integer) params[0];
			try {
				
				retmap = libCloud.Get_upload_record();
				//System.out.println("UploadRecordActivity GetDataTast remap :" + retmap);
				if(retmap!=null && retmap.get("code").equals("1")){
					l = (List<Map<String, Object>>) retmap.get("filelist");
				}
				
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			Map<String, Object> m = DataHelpter.fillRecordData(l);
			if (m == null) {
				return new ArrayList<DataInfo>();
			}
 
			//totalPage = (Integer) m.get("totalpage");
			return (List<DataInfo>) m.get("result");
		}

		@Override
		protected void onPostExecute(List<DataInfo> result) {
			if(firstrun==1){
				if(uploadingList.size()<=0 && waitingList.size()<=0&& errorList.size()>0){
					clearallbtn.setVisibility(View.VISIBLE);
					reuploadView.setVisibility(View.VISIBLE);
					uploadnotestv.setVisibility(View.GONE);
				}else if(uploadingList.size()>0 || waitingList.size()>0){
					uploadnotestv.setVisibility(View.VISIBLE);			
					clearallbtn.setVisibility(View.INVISIBLE);
					reuploadView.setVisibility(View.GONE);
				}
			}
			
			if(result!=null){
				addDataToGridView(result);
			}
			
		}
	}
	class GetCapacityAsync extends AsyncTask<Object, Integer, Map<String, Object> >{
		@Override
		protected Map<String, Object>  doInBackground(Object... params) {
			Map<String, Object> retmap=null;
			try {
				retmap = libCloud.Get_capacity();	
			}
			catch (WeiboException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return retmap;
		}

		@Override
		protected void onPostExecute(Map<String, Object> result) {
			String totalSpace = null;
			String usedSpace = null;
			if(result==null) return ;
			if(result.get("code").equals("1")){
				totalSpace = (String) result.get("totalspace");
				usedSpace = (String) result.get("usedspace");
				if(totalSpace==null){
					totalSpace="0";
				}if(usedSpace==null){
					usedSpace="0";
				}
				String usedGB = "";
				long size = Long.parseLong(usedSpace);
				long total = Long.parseLong(totalSpace);
				System.out.println("size = " + size);
				usedGB = DataHelpter.getSpaceStr(size);
				String leave = DataHelpter.getSpaceStr(total*1024*1024*1024-size);
				totalText1.setText(totalSpace+"GB");
				usedText1.setText(usedGB);
				nousedText1.setText(leave);
				
			}else if(result.get("code").equals("202")){
				UIHelper.displayToast("userid or token is invalid",UploadRecordActivity.this);
			}
		}
		
	}
	private final static int UPDATE_PROGRESS = 0;  
    Handler myHandler = new Handler(){
        
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			switch(msg.what){
				case UPDATE_PROGRESS:
					//Log.v(TAG, "UPDATE_PROGRESS");
					
					if(waitingList!=null){
						waitingList.clear();
					}
					if(uploadingList!=null){
						uploadingList.clear();
					}
					if(errorList!=null){
						errorList.clear();
					}
					waitingList = UploadDao.getUploadList(0);
					uploadingList = UploadDao.getUploadList(1);
					errorList = UploadDao.getUploadList(99);
					
					System.out.println("message handler on message received");
					

					/*
					if(uploadingList.size()<=0 && waitingList.size()<=0&& errorList.size()>0){
						clearallbtn.setVisibility(View.VISIBLE);
						reuploadView.setVisibility(View.VISIBLE);
						uploadnotestv.setVisibility(View.GONE);
					}else{
						clearallbtn.setVisibility(View.INVISIBLE);
						reuploadView.setVisibility(View.GONE);
						uploadnotestv.setVisibility(View.VISIBLE);
					}
					*/
					String title = String.format(getResources().getString(R.string.upload_info_text),uploadingList.size()+waitingList.size(),records.size());
					uploadTitle.setText(title);
					if(uploadingList.size()==0 && waitingList.size()==0){
						myHandler.removeMessages(UPDATE_PROGRESS);
						GetDataTask gdt = new GetDataTask();
						gdt.execute(0);					
						GetCapacityAsync capacity = new GetCapacityAsync();
						capacity.execute();
						if(errorList.size()>0){
							clearallbtn.setVisibility(View.VISIBLE);
							reuploadView.setVisibility(View.VISIBLE);
							uploadnotestv.setVisibility(View.GONE);
						}else{
							clearallbtn.setVisibility(View.INVISIBLE);
							reuploadView.setVisibility(View.GONE);
							uploadnotestv.setVisibility(View.GONE);
						}
					    
					}else{
						uploadnotestv.setVisibility(View.VISIBLE);
						clearallbtn.setVisibility(View.INVISIBLE);
						reuploadView.setVisibility(View.GONE);
						if(uploadingList.size()>0){
							String tmpfileid = uploadingList.get(0).getFileid();
							//System.out.println("tmpfileid = "+ tmpfileid + "uploadingFileid = " + uploadingFileid);
							if(tmpfileid.equals(uploadingFileid)){
								if(mList!=null){
									mList.clear();
								}
								for(int i=0;i< uploadingList.size();i++){
									mList.add(uploadingList.get(i));
									
								}
								
								for(int i=0;i<errorList.size();i++){
									mList.add(errorList.get(i));
								}
								
								for(int i=0;i<waitingList.size();i++){
									mList.add(waitingList.get(i));
								}
								for(int i=0;i<records.size();i++){
									mList.add(records.get(i));
								}
								adapter.notifyDataSetChanged() ;
								myHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000);
							}else{		
								GetDataTask gdt = new GetDataTask();
								gdt.execute(0);								
								uploadingFileid = tmpfileid;	
								GetCapacityAsync capacity = new GetCapacityAsync();
								capacity.execute();
							}
						}else{
							myHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000);
						}
						
					}
					break;
					
			}
			
			super.handleMessage(msg);
		}	
    };
    
	@Override
	public void onResume()
	{
		super.onResume();
		if(firstStart==true){
			firstStart = false;
		}else{
			if(myHandler!=null)
				myHandler.sendEmptyMessageDelayed(0, 1000);
		}
	}
    
	@Override
	public void onPause()
	{
		super.onPause();
		myHandler.removeMessages(UPDATE_PROGRESS);
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
