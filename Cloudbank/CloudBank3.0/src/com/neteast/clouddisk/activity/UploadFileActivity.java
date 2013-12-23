package com.neteast.clouddisk.activity;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityGroup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.lib.db.AppDao;
import com.lib.db.DataInfo;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.adapter.FileDataAdapter;
import com.neteast.clouddisk.adapter.RecyclerDataAdapter;
import com.neteast.clouddisk.adapter.ViewPageAdapter;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.service.UploadService;
import com.neteast.clouddisk.service.UploadService.ConnectionChangeReceiver;
import com.neteast.clouddisk.utils.DataHelpter;
import com.neteast.clouddisk.utils.FileItem;
import com.neteast.clouddisk.utils.UIHelper;


public class UploadFileActivity extends ActivityGroup{
	private String TAG = "UploadFileActivity";
	private Context mContext;
	
	/** 每一屏为GridView. */
	private GridView gridView;

	/** 上传文件显示. */
	private LinearLayout uploadLayout;
	/** 没有外设存储显示. */
	private LinearLayout noSDCardLayout;
	private Button btnSelectAll;
	private Button btnUpload;
	private Button btnCancel;
	private RadioButton rbRecord;
	
	private RadioButton rbVideo;
	private RadioButton rbAudio;
	private RadioButton rbPic;
	private RadioButton rbText;
	
	private AppDao UploadDao = AppDao.getInstance(this);
	

	private int currentPage = 1;
	private int totalPage = 1;

	private ImageButton returnbtn =null;
	List<FileItem> files = new ArrayList<FileItem>();
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
	private static final int PAGE_SIZE = Params.UPLOADFILE_PER_PAGE_NUM;
	private String UDISK_PATH="/sdcard/udisk";
	//private String UDISK_PATH="/sdcard/external_sd";
	private BroadcastReceiver mReceiver;
	class UsbReceiver{
		   UsbReceiver(Context context){
		      mReceiver = new BroadcastReceiver(){
		      @Override
		      public void onReceive(Context context, Intent intent) {

		         //intent.getAction());获取存储设备当前状态        

		         Log.i("usb","BroadcastReceiver:"+intent.getAction());

		         //intent.getData().getPath());获取存储设备路径
		         Log.i("usb","path:"+intent.getData().getPath());
		         UIHelper.displayToast(getResources().getString(R.string.usb_removed), UploadFileActivity.this);
		         File file = new File(UDISK_PATH);
		         fill(file,curCheckedType);      
		        }

		     };
		  
		      IntentFilter filter = new IntentFilter();
		      //filter.addAction(Intent.ACTION_MEDIA_SHARED);//如果SDCard未安装,并通过USB大容量存储共享返回
		      //filter.addAction(Intent.ACTION_MEDIA_MOUNTED);//表明sd对象是存在并具有读/写权限
		      //filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);//SDCard已卸掉,如果SDCard是存在但没有被安装
		      //filter.addAction(Intent.ACTION_MEDIA_CHECKING);  //表明对象正在磁盘检查
		      filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		      filter.addAction(Intent.ACTION_MEDIA_EJECT);  //物理的拔出 SDCARD
		      filter.addAction(Intent.ACTION_MEDIA_REMOVED);  //完全拔出
		      filter.addDataScheme("file"); // 必须要有此行，否则无法收到广播   
		      context.registerReceiver(mReceiver, filter);
		   }
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_upload_grid);
		
		UsbReceiver usbreceiver = new UsbReceiver(this);  
		
		
		filePager = (ViewPager) findViewById(R.id.uploadFileViewPager);
		
		curCheckedType = this.getIntent().getIntExtra("uploadType",1);

		uploadLayout = (LinearLayout) findViewById(R.id.uploadLayout);
		noSDCardLayout = (LinearLayout)findViewById(R.id.noSDCardLayout);
	
		btnSelectAll = (Button) findViewById(R.id.btnSelectAll);
		btnUpload = (Button) findViewById(R.id.btnUpload);
		btnCancel = (Button) findViewById(R.id.btnCancel);

		returnbtn =(ImageButton) findViewById(R.id.uploadfile_return);
		//returnbtn.setVisibility(View.INVISIBLE);
		returnbtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				currentPage = 1;
				if(files.size()>0){
					FileDataAdapter adapter1 = (FileDataAdapter) ((GridView) mListViews.get(0)).getAdapter();
					if(!adapter1.isRoot()){
						FileItem item = (FileItem) adapter1.getItem(0);
						fill(item.getFile().getParentFile().getParentFile(),curCheckedType);
						
					}else{
						UploadExit();
					}
				}else{
					if(!parentFile.getAbsolutePath().equalsIgnoreCase(UDISK_PATH)){
						fill(parentFile.getParentFile(),curCheckedType);
						FileDataAdapter adapter1 = (FileDataAdapter) ((GridView) mListViews.get(0)).getAdapter();
						
					}else{
						UploadExit();
					}
				}
			}
		});	
		
		//addListener();

		if(checkExternalStorageState()){
			noSDCardLayout.setVisibility(View.GONE);
			uploadLayout.setVisibility(View.VISIBLE);
			
			System.out.println(" UploadFileActivity curCheckedType = " + curCheckedType);
			
			File file = new File(UDISK_PATH);
			fill(file,curCheckedType);
			
		}else{
			uploadLayout.setVisibility(View.GONE);
			noSDCardLayout.setVisibility(View.VISIBLE);
		}
		
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
		
		btnUpload.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				int size = addToUpload();
				if(size > 0){
					((MyUploadActivity) UploadFileActivity.this.getParent()).startUploadRecordActivity(curCheckedType);
				}
				
			}
		});
		
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		files.clear();
		if(mReceiver!=null)unregisterReceiver(mReceiver); 

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	private void setGrid(Context context, List<FileItem> list) {
	//	int PageCount = (int) Math.ceil(list.size() / PAGE_SIZE);
		currentPage = 1;
		totalPage = (int) Math.ceil(((double)list.size())/PAGE_SIZE);
		System.out.println("setGrid  PageCount = " + totalPage);
		for (int i = 0; i < totalPage; i++) {
			gridView = (GridView) LayoutInflater.from(context).inflate(
					R.layout.uploadfile_grid, null);
			gridView.setAdapter(new FileDataAdapter(context, list,i));
			gridView.setOnItemClickListener(mOnItemClick); 
				
			// 去掉点击时的黄色背景
			//gridView.setSelector(R.color.transparent);
			mListViews.add(gridView);
		}
	 }
	OnItemClickListener mOnItemClick = new OnItemClickListener() {   
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {   
			System.out.println("UploadFileActivity on Item click arg2 :" + arg2 + "mListViews size = " + mListViews.size());
			FileDataAdapter adapter1 = (FileDataAdapter) ((GridView) mListViews.get(currentPage -1)).getAdapter();
			FileItem dataInfo = (FileItem) adapter1.getItem(arg2);
			System.out.println("UploadFileActivity on Item click dataInfo isdir  :" + dataInfo.getFile().isDirectory());
			if(dataInfo.getFile().isDirectory()){
				fill(dataInfo.getFile(),curCheckedType);
				//returnbtn.setVisibility(View.VISIBLE);
			}else{
				long filelimited = (2*1024*1024*1024-1);
				System.out.println("UploadFileActivity file size = " + dataInfo.getFile().length() + "filelimited size = " + filelimited);
				if(dataInfo.getFile().length()>= filelimited){
					String message = getResources().getString(R.string.file_islimited);
					//String message = String.format(getResources().getString(R.string.file_isbigthan2G),dataInfo.getFile().getName());
					//UIHelper.displayToast(message,UploadFileActivity.this);
					UIHelper.showToast(UploadFileActivity.this,message);
				}else{
					CheckBox check ;
					check = (CheckBox)arg1.findViewById(R.id.uploadItemCheckBox);
					if(check.isChecked()){
						check.setChecked(false);
						dataInfo.setIsChecked(0);
					}else{
						check.setChecked(true);
						dataInfo.setIsChecked(1);
					}
				}
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
			RelativeLayout rl = (RelativeLayout) UploadFileActivity.this
					.getParent().getWindow()
					.findViewById(R.id.datacontainer);
			rl.removeAllViews();
			Intent intent = new Intent(UploadFileActivity.this, cls);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			Window sub = ((ActivityGroup) UploadFileActivity.this
					.getParent()).getLocalActivityManager().startActivity(
					selectName, intent);
			View view = sub.getDecorView();
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT);
			view.setLayoutParams(params);
			rl.addView(view);

	}
	
	private int addToUpload(){
		List<DataInfo> list = new ArrayList<DataInfo>();
		int position = 0;
		//int count = filePager.getChildCount();
		int count = mListViews.size();
		System.out.println(" UploadFileActivity filePage Count = " + count);
		for(int j=0;j< count ;j++){
			GridView gridView1 = (GridView) mListViews.get(j);
			FileDataAdapter adapter1 = (FileDataAdapter) gridView1.getAdapter();
		
			for(int i=0;i<gridView1.getCount();i++){
				
				FileItem dataInfo = (FileItem)adapter1.getItem(i);
				
				if(dataInfo.getIsChecked()==1){
					FileItem item = null;
					item = (FileItem) adapter1.getItem(i);
					if(!item.getFile().isDirectory()){
						DataInfo data = new DataInfo();
						data.setType(DataHelpter.GetFileType(item.getFile().getName())+"");
						System.out.println("file type = " + DataHelpter.GetFileType(item.getFile().getName()));
						//data.setType(curCheckedType+"");
						data.setName(item.getFile().getName());
						data.setFileSize(item.getFile().length());
						data.setUrl(item.getFile().getAbsolutePath());
						list.add(data);			
					}
				}
			}
		}
		
		if(list.size() > 0){
			//插入上传数据，到clouddisk_upload_table表中，如果已经存在，就忽略。
			UploadDao.addToUploadTask(list);
			//启动上传服务
			this.startService(new Intent(this, UploadService.class));
		}
		
		return list.size();
	}
	private void addListener() {
		rbRecord.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					((MyUploadActivity) UploadFileActivity.this
							.getParent()).startUploadRecordActivity(curCheckedType);
				}
				
			}
		});
		rbVideo.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					curCheckedType = 1;
					File file = new File(UDISK_PATH);
					fill(file,curCheckedType);
				}
				
			}
		});
		rbAudio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					curCheckedType = 2;
					File file = new File(UDISK_PATH);
					fill(file,curCheckedType);
				}
				
			}
		});
		rbPic.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					curCheckedType = 3;
					File file = new File(UDISK_PATH);
					fill(file,curCheckedType);
				}
				
			}
		});
		rbText.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					curCheckedType = 4;
					File file = new File(UDISK_PATH);
					fill(file,curCheckedType);
				}
				
			}
		});
	}

	
	public Button getUploadButton(){
		return btnUpload;
	}
	public Button getSelectAllButton(){
		return btnSelectAll;
	}
	public Button getCancelButton(){
		return btnCancel;
	}
	private void setGridSelectAll(){
		//int count = filePager.getChildCount();
		int count = mListViews.size();
		for(int j=0;j< count ;j++){
			gridView = (GridView) mListViews.get(j);
			FileDataAdapter adapter =  (FileDataAdapter) gridView.getAdapter();
			for (int i=0;i<gridView.getCount();i++){
				FileItem dataInfo = (FileItem)adapter.getItem(i);
				long filelimited = (2*1024*1024*1024-1);
				if(dataInfo.getFile().length()>= filelimited){
					String message = getResources().getString(R.string.file_islimited);
					//String message = String.format(getResources().getString(R.string.file_isbigthan2G),dataInfo.getFile().getName());
					//UIHelper.displayToast(message,UploadFileActivity.this);
					UIHelper.showToast(UploadFileActivity.this,message);
				}else{
					dataInfo.setIsChecked(1);
				}
			}
			adapter.notifyDataSetChanged();
		}
	}
	private void setGridCancelAll(){
		//int count = filePager.getChildCount();
		int count = mListViews.size();
		for(int j=0;j< count ;j++){
			gridView = (GridView) mListViews.get(j);
			FileDataAdapter adapter =  (FileDataAdapter) gridView.getAdapter();
			for (int i=0;i<gridView.getCount();i++){	
				FileItem dataInfo = (FileItem)adapter.getItem(i);
				dataInfo.setIsChecked(0);
			}
			adapter.notifyDataSetChanged();
		}
	}
	 private boolean checkExternalStorageState(){
		 Log.d(TAG, "--------------------"+Environment.getExternalStorageState());
		 if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			 File dir = Environment.getExternalStorageDirectory();
			 File external=new File(dir, "udisk");
			 if(external.exists()){
				 return true;
			 }else return false;
		 }
		 return false;
	 }
	 
	 private static final FileFilter FILTER = new FileFilter(){
		 public boolean accept(File f) {
			 	/*
				return f.isDirectory() || f.getName().matches("^.*?\\.(mp4|3gp|avi|rm|rmvb|RMVB|mkv|ts|mpg|flv)$")
						||f.getName().matches("^.*?\\.(mp3|MP3|wma|WMA|aac|AAC)$") 
						||f.getName().matches("^.*?\\.(jpg|JPG|png|PNC|bmp|BMP|gif|GIF)$")
						|| f.getName().matches("^.*?\\.(doc|txt|pdf|docx)$");
				*/
			 	/*
				return f.isDirectory() || f.getName().matches("^.*?\\.(mp4|3gp|avi|rm|rmvb|RMVB|flv)$")
						||f.getName().matches("^.*?\\.(mp3|MP3|wma|WMA)$") 
						||f.getName().matches("^.*?\\.(jpg|JPG|png|PNC|bmp|BMP)$")
						|| f.getName().matches("^.*?\\.(doc|txt|pdf|docx)$");
				*/
			 return true;
			} 
	 };
	 
	 private static final FileFilter VIDEOFILTER = new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().matches("^.*?\\.(mp4|3gp|avi|rm|rmvb|RMVB|mkv|ts|mpg)$");
			}
	 };
	 private static final FileFilter MUSICFILTER = new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().matches("^.*?\\.(mp3|MP3|wma|WMA|aac|AAC)$");
			}
	 };
	 private static final FileFilter PICTUREFILTER = new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().matches("^.*?\\.(jpg|JPG|png|PNC|bmp|BMP|gif|GIF)$");
			}
	 };
	 private static final FileFilter DOCFILTER = new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().matches("^.*?\\.(doc|txt|pdf|docx)$");
			}
	 };
	 private void fill(File folder,int type ) {
		if(files!=null) files.clear();
		if(folder==null ) return ;
		for(File file:folder.listFiles(FILTER)){
			//files.add(file);
			FileItem item = new FileItem();
			item.setFile(file);
			files.add(item);
		}
		/*
		if(type ==1){
			for (File file : folder.listFiles(VIDEOFILTER)) {
				System.out.println(" UploadFileActivity fill filename " + file.getName() );
				files.add(file);
			}
		}
		else if(type ==2){
			for (File file : folder.listFiles(MUSICFILTER)) {
				files.add(file);
			}
		}
		else if(type ==3){
			for (File file : folder.listFiles(PICTUREFILTER)) {
				files.add(file);
			}
		}
		else if(type ==4){
			for (File file : folder.listFiles(DOCFILTER)) {
				files.add(file);
			}
		}
		*/
		btnSelectAll.setVisibility(View.VISIBLE);
		btnCancel.setVisibility(View.GONE);
		if(files.size()==0){
			parentFile = folder ;
		}
		mListViews = new ArrayList<View>();
		setGrid(this, files);
		viewPageAdapter = new ViewPageAdapter(this,mListViews,2);
		filePager.setAdapter(viewPageAdapter);
		filePager.setCurrentItem(0);
		filePager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int page) {
				currentPage = page + 1;
				
			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			public void onPageScrollStateChanged(int arg0) {}
		});
		
	}
	private  List<FileItem> getOnePageFiles(int currPage){
		List<FileItem> pagefiles = new ArrayList<FileItem>();
		if(files.size() <=PAGE_SIZE){
			for(int i=0;i<files.size();i++){
				pagefiles.add(files.get(i));
			}
		}else{
			int index = (currPage - 1) * PAGE_SIZE;
			int temp2 = files.size() % PAGE_SIZE;
			int times =  currPage<totalPage?PAGE_SIZE: temp2==0?PAGE_SIZE:temp2;
			for(int i=0;i<times;i++){
				pagefiles.add(files.get(i+index));
			}
		}
		return pagefiles;
	}

}
