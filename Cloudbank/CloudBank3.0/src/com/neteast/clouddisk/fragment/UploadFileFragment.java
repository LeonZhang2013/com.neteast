package com.neteast.clouddisk.fragment;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.lib.cloud.LibCloud;
import com.lib.net.WeiboException;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.activity.UploadActivity;
import com.neteast.clouddisk.adapter.UploadSingleGridAdapter;
import com.neteast.clouddisk.adapter.ViewPageAdapter;
import com.neteast.clouddisk.model.MovieInfo;
import com.neteast.clouddisk.param.Params;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;


public class UploadFileFragment extends Fragment {
	private String TAG = "UploadFileFragment";
	private Context mContext;
	/** 每页显示的数量，Adapter保持一致. */
	private static final float PAGE_SIZE = 18.0f;
	/** 每一屏为GridView. */
	private GridView gridView;
	/** ViewPager控件，横屏滑动*/
	private ViewPager filePager;
	/** ViewPager的视图列表*/
	private List<View> mListViews;
	/** ViewPager的适配器. */
	private ViewPageAdapter viewPageAdapter;
	/** 上传文件显示. */
	private LinearLayout uploadLayout;
	/** 没有外设存储显示. */
	private LinearLayout noSDCardLayout;
	private Button btnSelectAll;
	private Button btnUpload;
	private Button btnCancel;
	
	private LibCloud libCloud;
	private static  LinkedList<MovieInfo> videoList = new LinkedList<MovieInfo>();
	private static  LinkedList<MovieInfo> musicList = new LinkedList<MovieInfo>();
	private  static LinkedList<MovieInfo> photoList = new LinkedList<MovieInfo>();
	private  static LinkedList<MovieInfo> docList = new LinkedList<MovieInfo>();
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_upload_grid, null);
		
		//filePager = (ViewPager) view.findViewById(R.id.uploadViewPager);
		uploadLayout = (LinearLayout) view.findViewById(R.id.uploadLayout);
		noSDCardLayout = (LinearLayout)view.findViewById(R.id.noSDCardLayout);
		btnSelectAll = (Button) view.findViewById(R.id.btnSelectAll);
		btnUpload = (Button) view.findViewById(R.id.btnUpload);
		btnCancel = (Button) view.findViewById(R.id.btnCancel);
		createMediaList();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		libCloud = LibCloud.getInstance(mContext);
		
		mListViews = new ArrayList<View>();
		if(checkExternalStorageState()){
			noSDCardLayout.setVisibility(View.GONE);
			uploadLayout.setVisibility(View.VISIBLE);
			/*
			ArrayList<String> list = new ArrayList<String>();
			for(int i = 0; i<100; i++){
				list.add("文件1");
			}
			*/
			//setGrid(mContext, videoList);
			Log.d(TAG, "--------------------"+"setGrid");
			setGrid(mContext, photoList);
			viewPageAdapter = new ViewPageAdapter(mContext,mListViews,3);
			filePager.setAdapter(viewPageAdapter);
			filePager.setCurrentItem(0);
		}else{
			uploadLayout.setVisibility(View.GONE);
			noSDCardLayout.setVisibility(View.VISIBLE);
		}
		
		btnUpload.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
			
				for(int i= 0;i< videoList.size();i++){
					 Log.d(TAG, "Video List"+ "path" + videoList.get(i).path + "filesize" + videoList.get(i).filesize);
				}
				for(int i= 0;i< musicList.size();i++){
					 Log.d(TAG, "Music List"+ "path" + musicList.get(i).path + "filesize" + musicList.get(i).filesize);
				}
				for(int i= 0;i< photoList.size();i++){
					 Log.d(TAG, "Photo List"+ "path" + photoList.get(i).path + "filesize" + photoList.get(i).filesize);
				}
				MovieInfo info = musicList.get(0);
				String filesize = Long.toString(info.filesize);
				/*
				try {
					Map m =libCloud.Get_upload_fileid(2, "", info.displayName, filesize);
					String code = (String)m.get("code");
					String fileid = (String)m.get("fileid");
					String server = (String)m.get("server");
					//int isnew = (Integer) m.get("isnew");
					if(code.equals("1")){
						libCloud.Upload_file(fileid, server, info.path);
					}else{
						
					}
				} catch (WeiboException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
				UploadActivity uploadActivity=(UploadActivity) getActivity();
				uploadActivity.showUploading();
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
	
	 private void setGrid(Context context, LinkedList<MovieInfo> list) {
			int PageCount = (int) Math.ceil(list.size() / PAGE_SIZE);
			for (int i = 0; i < PageCount; i++) {
				gridView = (GridView) LayoutInflater.from(context).inflate(
						R.layout.item_upload_grid, null);
				gridView.setAdapter(new UploadSingleGridAdapter(context, list, i));
				
				// 去掉点击时的黄色背景
				//gridView.setSelector(R.color.transparent);
				mListViews.add(gridView);
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
		public static void createMediaList(){
			videoList.clear();
			musicList.clear();
			photoList.clear();
			docList.clear();
			getMediaFile(new File("/sdcard/udisk/"));
		}
		
	    private static void getMediaFile(File file){
	    	
	    	file.listFiles(new FileFilter(){

				@Override
				public boolean accept(File file) {
					// TODO Auto-generated method stub
					String name = file.getName();
					int i = name.indexOf('.');
					if(i != -1){
						name = name.substring(i);
						MovieInfo mi = new MovieInfo();
						mi.displayName = file.getName();
						mi.path = file.getAbsolutePath();
						mi.filesize =  file.length();
						mi.isdecoded = false;
						if(name.equalsIgnoreCase(".mp4")||name.equalsIgnoreCase(".3gp") ){
							mi.type=Params.UPLOAD_VIDEO;
							//mi.thumbnail = ThumbnailUtils.createVideoThumbnail(mi.path,Video.Thumbnails.MICRO_KIND);//MINI_KIND;MICRO_KIND
							videoList.add(mi);
							return true;
						}else if(name.equalsIgnoreCase(".mp3")){
							mi.type=Params.UPLOAD_MUSIC;
							musicList.add(mi);
						}else if(name.equalsIgnoreCase(".jpg") || name.equalsIgnoreCase(".png")){
							mi.type=mi.type=Params.UPLOAD_PICTURE;
							photoList.add(mi);
						}else if(name.equalsIgnoreCase(".txt")||name.equalsIgnoreCase(".doc")||name.equalsIgnoreCase(".pdf")){
							mi.type = Params.UPLOAD_DOC;
							docList.add(mi);
						}
					}else if(file.isDirectory()){
						getMediaFile(file);
					}
					return false;
				}
	    	});
	    }   
}
