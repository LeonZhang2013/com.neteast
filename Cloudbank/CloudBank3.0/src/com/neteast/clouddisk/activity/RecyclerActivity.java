package com.neteast.clouddisk.activity;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lib.cloud.LibCloud;
import com.lib.db.AppDao;
import com.lib.db.DataInfo;
import com.lib.net.WeiboException;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.activity.MyUploadVideoActivity.GetDataTask;
import com.neteast.clouddisk.adapter.FileDataAdapter;
import com.neteast.clouddisk.adapter.MyUploadVideoDataAdapter;
import com.neteast.clouddisk.adapter.RecyclerDataAdapter;
import com.neteast.clouddisk.adapter.ViewPageAdapter;
import com.neteast.clouddisk.customerview.CustomerGridView;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.service.UploadService;
import com.neteast.clouddisk.utils.DataHelpter;
import com.neteast.clouddisk.utils.OnDropCallBack;
import com.neteast.clouddisk.utils.UIHelper;


public class RecyclerActivity extends ActivityGroup{
	private String TAG = "UploadFileActivity";
	private Context mContext;
	
	/** 每一屏为GridView. */
	private GridView gridView;
	private LibCloud libCloud; 



	private Button btnSelectAll;
	private Button btnClear;
	private Button btnCancel;
	private Button btnRecover;
	private Button btnDel;
	private TextView currentPageNum = null;
	private TextView totalPageNum = null;

		

	private int currentPage = 1;
	private int totalPage = 1;

	private ImageButton returnbtn =null;
	List<File> files = new ArrayList<File>();
	int curCheckedType = 1;
	File parentFile = null;
	boolean selectAll = false;
	/** ViewPager控件，横屏滑动*/
	private ViewPager filePager;
	
	/** ViewPager的视图列表*/
	private List<View> mListViews;
	
	/** ViewPager的适配器. */
	private ViewPageAdapter viewPageAdapter;
	/** 每页显示的数量，Adapter保持一致. */
	private static final int PAGE_SIZE = Params.MYUPLOAD_DATA_PER_PAGE_NUM;
	
	private LinearLayout loadingView = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recycler);
		
		libCloud = LibCloud.getInstance(this);
		filePager = (ViewPager) findViewById(R.id.recyclerViewPager);
		loadingView = (LinearLayout)findViewById(R.id.loading);
		currentPageNum = (TextView) findViewById(R.id.curentpage);
		totalPageNum = (TextView) findViewById(R.id.totalpage);
		
		curCheckedType = this.getIntent().getIntExtra("Type",1);

	
		btnSelectAll = (Button) findViewById(R.id.btnSelectAll);
		btnClear = (Button) findViewById(R.id.btnclear);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnRecover = (Button) findViewById(R.id.btnrecover);
		btnDel = (Button) findViewById(R.id.btndel);

		returnbtn =(ImageButton) findViewById(R.id.recycler_return);
		//returnbtn.setVisibility(View.INVISIBLE);
		returnbtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				currentPage = 1;
				UploadExit();
			}
		});	
				
		btnSelectAll.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				selectAll = true;
				btnSelectAll.setVisibility(View.GONE);
				btnCancel.setVisibility(View.VISIBLE);
				setGridSelectAll();
			}
		});	
		
		btnCancel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				selectAll = false;
				btnSelectAll.setVisibility(View.VISIBLE);
				btnCancel.setVisibility(View.GONE);
				setGridCancelAll();
			}
		});	
		
		btnClear.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				ClearRecycler();		
			}
		});
		
		btnRecover.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
				RecoverFile();
			}
		});
		
		btnDel.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				
				DelFile();
			}
		});
		
		GetDataTask task = new GetDataTask();
		task.execute();
		
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		files.clear();
        //System.out.println("***************** playback activity onDestroy\n");
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	OnItemClickListener mOnItemClick = new OnItemClickListener() {   
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {   
			System.out.println("UploadFileActivity on Item click arg2 :" + arg2);
			RecyclerDataAdapter adapter1 = (RecyclerDataAdapter) ((GridView) mListViews.get(currentPage -1)).getAdapter();
			//File dataInfo = (File) adapter1.getItem(arg2);
			//System.out.println("UploadFileActivity on Item click dataInfo isdir  :" + dataInfo.isDirectory());
			DataInfo dataInfo= (DataInfo) adapter1.getItem(arg2);
			CheckBox check ;
			if(dataInfo.GetIsDir()!=null && dataInfo.GetIsDir().equals("1")){
				check = (CheckBox)arg1.findViewById(R.id.uploadItemCheckBox1);
			}else{
				check = (CheckBox)arg1.findViewById(R.id.uploadItemCheckBox);
			}
			if(check.isChecked()){
				dataInfo.setIsChecked(0);
				check.setChecked(false);
			}else{
				dataInfo.setIsChecked(1);
				check.setChecked(true);
			}
			
		}      
	};
	private void UploadExit(){
		Class cls = null;
		String selectName="";
			selectName = "MyUploadVideoActivity";
			switch (curCheckedType) {
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
			RelativeLayout rl = (RelativeLayout) RecyclerActivity.this
					.getParent().getWindow()
					.findViewById(R.id.datacontainer);
			rl.removeAllViews();
			Intent intent = new Intent(RecyclerActivity.this, cls);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			Window sub = ((ActivityGroup) RecyclerActivity.this
					.getParent()).getLocalActivityManager().startActivity(
					selectName, intent);
			View view = sub.getDecorView();
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT);
			view.setLayoutParams(params);
			rl.addView(view);

	}
	
	private void RecoverFile(){
		StringBuilder filestr = new StringBuilder();
		//int count = filePager.getChildCount();
		int count = mListViews.size();
		//System.out.println("count = " + count);
		for(int j=0;j< count ;j++){
			gridView = (GridView) mListViews.get(j);
			RecyclerDataAdapter adapter =  (RecyclerDataAdapter) gridView.getAdapter();
			//System.out.println("gridView count = " + gridView.getCount());
			for (int i=0;i<gridView.getCount();i++){
				DataInfo dataInfo = (DataInfo)adapter.getItem(i);
				if(dataInfo.getIsChecked() == 1){
					filestr.append(dataInfo.getId());
					filestr.append(",");
				}
				
			}
		}
		
		System.out.println("filestr = " + filestr.toString());
		
		if(filestr.toString()!=null && filestr.toString().length() > 0){
			int len = filestr.toString().length();
			String fileid = filestr.toString().substring(0, len-1);
			System.out.println("fileid = " + fileid);
			RecoverTask recover = new RecoverTask();
			recover.execute(fileid);
		}
		
	}
	private void DelFile(){
		StringBuilder filestr = new StringBuilder();
		//int count = filePager.getChildCount();
		int count = mListViews.size();
		for(int j=0;j< count ;j++){
			gridView = (GridView) mListViews.get(j);
			RecyclerDataAdapter adapter =  (RecyclerDataAdapter) gridView.getAdapter();
			for (int i=0;i<gridView.getCount();i++){
				DataInfo dataInfo = (DataInfo)adapter.getItem(i);
				if(dataInfo.getIsChecked() == 1){
					filestr.append(dataInfo.getId());
					filestr.append(",");
				}
				
			}
		}
		if(filestr.toString()!=null && filestr.toString().length() > 0){
			int len = filestr.toString().length();
			String fileid = filestr.toString().substring(0, len-1);
			System.out.println("fileid = " + fileid);
			ClearRecyclerTask recover = new ClearRecyclerTask();
			recover.execute(fileid);
		}
	}
	private void ClearRecycler(){
		ClearRecyclerTask recover = new ClearRecyclerTask();
		recover.execute("");
	}
	private void setGridSelectAll(){
		//int count = filePager.getChildCount();
		int count = mListViews.size();
		for(int j=0;j< count ;j++){
			gridView = (GridView) mListViews.get(j);
			RecyclerDataAdapter adapter =  (RecyclerDataAdapter) gridView.getAdapter();
			for (int i=0;i<gridView.getCount();i++){			
				DataInfo dataInfo = (DataInfo)adapter.getItem(i);
				dataInfo.setIsChecked(1);
				
			}
			adapter.notifyDataSetChanged();
		}
	}
	private void setGridCancelAll(){
		//int count = filePager.getChildCount();
		int count = mListViews.size();
		for(int j=0;j< count ;j++){
			gridView = (GridView) mListViews.get(j);
			RecyclerDataAdapter adapter =  (RecyclerDataAdapter) gridView.getAdapter();
			for (int i=0;i<gridView.getCount();i++){
				View view = gridView.getChildAt(i);
				DataInfo dataInfo = (DataInfo)adapter.getItem(i);
				dataInfo.setIsChecked(0);		
			}
			adapter.notifyDataSetChanged();
		}
	}
	

	private void setGrid(Context context, List<DataInfo> list) {
		int PageCount = (int) Math.ceil(((double)list.size())/PAGE_SIZE);
		System.out.println("setGrid  PageCount = " + PageCount);
		for (int i = 0; i < PageCount; i++) {
			gridView = (GridView) LayoutInflater.from(context).inflate(
					R.layout.uploadfile_grid, null);
			gridView.setAdapter(new RecyclerDataAdapter(context, list,i,curCheckedType));
			gridView.setOnItemClickListener(mOnItemClick); 
			
			// 去掉点击时的黄色背景
			//gridView.setSelector(R.color.transparent);
			mListViews.add(gridView);
		}
	}
	
	public void addDataToGridView(final List<DataInfo> result) {
		//refreshList = result;
		mListViews = new ArrayList<View>();
		setGrid(this, result);
		viewPageAdapter = new ViewPageAdapter(this,mListViews,1);
		filePager.setAdapter(viewPageAdapter);
		
		filePager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int page) {
				totalPage = DataHelpter.computeTotalPage(result.size(),PAGE_SIZE);
				currentPage = page + 1;
				currentPageNum.setText(currentPage+"");
				totalPageNum.setText(totalPage+"");
			
			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			public void onPageScrollStateChanged(int arg0) {}
		});
		filePager.setCurrentItem(currentPage-1);
	}	
	
	class GetDataTask extends AsyncTask<Object, Integer, List<DataInfo>> {
		
		@Override  
	    protected void onPreExecute() {  
			//第一个执行方法  
			loadingView.setVisibility(View.VISIBLE);
		    super.onPreExecute();  
		}  
		@Override
		protected List<DataInfo> doInBackground(Object... params) {
			Map<String, Object> retmap=null;
			List<Map<String, Object>> videoList = null;
			try {
				if(videoList==null){
					retmap = libCloud.Get_file_list(curCheckedType, "","","1");
					if(retmap==null)
						return null;
					if(!retmap.get("code").equals("1")){
		
						return null;
					}
					videoList = (List<Map<String, Object>>) retmap.get("filelist");
					if(videoList==null){
						return null;
					}
				}
				
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			Map<String, Object> m = DataHelpter.fillUploadData(videoList, params);
			if (m == null) {
				return new ArrayList<DataInfo>();
			}
 
			totalPage = (Integer) m.get("totalpage");
			return (List<DataInfo>) m.get("result");
		}

		@Override
		protected void onPostExecute(List<DataInfo> result) {
			
			if(result!=null){
				addDataToGridView(result);
			}else{
				addDataToGridView(new ArrayList<DataInfo>());
				//videoList = new ArrayList<Map<String, Object>>();
				currentPage = 1;
				totalPage = 1;
			}
			loadingView.setVisibility(View.GONE);
			currentPageNum.setText(currentPage+"");
			totalPageNum.setText(totalPage+"");
			//retbtn.setClickable(true);
		}

	}
	
	class RecoverTask extends AsyncTask<Object, Integer, String> {	
		@Override
		protected String doInBackground(Object... params) {
			Map<String, Object> retmap=null;
			String code = "";
			String fileid = (String) params[0];
			try {
				retmap = libCloud.Recover_file(fileid);
				if(retmap!=null){
					code = (String) retmap.get("code");
				}
				
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			return code;
		}

		@Override
		protected void onPostExecute(String result) {
			
			if(result.equals("1")){
				UIHelper.displayToast("文件还原成功",RecyclerActivity.this);
				GetDataTask task = new GetDataTask();
				task.execute();
			}else{
				UIHelper.displayToast("文件还原失败",RecyclerActivity.this);
			}
		}

	}
	
	class ClearRecyclerTask extends AsyncTask<Object, Integer, String> {	
		@Override
		protected String doInBackground(Object... params) {
			Map<String, Object> retmap=null;
			String code = "";
			String fileid = (String) params[0];
			try {
				retmap = libCloud.Clear_recycler(curCheckedType,fileid);
				if(retmap!=null){
					code = (String) retmap.get("code");
				}
				
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			return code;
		}

		@Override
		protected void onPostExecute(String result) {
			
			if(result.equals("1")){
				UIHelper.displayToast("文件删除成功",RecyclerActivity.this);
				GetDataTask task = new GetDataTask();
				task.execute();
			}else{
				UIHelper.displayToast("文件删除失败",RecyclerActivity.this);
			}
		}

	}


}
