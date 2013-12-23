package com.neteast.clouddisk.fragment;

import java.util.ArrayList;
import java.util.List;


import com.lib.db.AppDao;
import com.lib.db.DataInfo;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.activity.UploadActivity;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
/**
 * 用来显示上传中文件的fragment
 * @author tiangh
 * 2012-4-27 下午4:52:23
 */
public class UploadingFragment extends Fragment {
	private String TAG = "UploadingFragment";
	private Context mContext;
	private ViewPager viewPager;
	private static List<DataInfo> records=new ArrayList<DataInfo>();
	private List<DataInfo> uploadingList = new ArrayList<DataInfo>();
	private List<DataInfo> waitingList = new ArrayList<DataInfo>();
	
	private final static Double SINGLE_PAGE_ITEMS=7.00;
	private static int totalPages;
	
	private AppDao UploadDao = null;
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
		mContext = getActivity();
		UploadDao = AppDao.getInstance(mContext);
		View root = inflater.inflate(R.layout.fragment_uploading, null);
		viewPager = (ViewPager) root.findViewById(R.id.pager_uploading);
		//这是生成测试数据
		Log.v(TAG, "onCreateView ");
		waitingList = UploadDao.getUploadList(0);
		uploadingList = UploadDao.getUploadList(1);
		Log.v(TAG, "onCreateView " + "waiting list size =" + waitingList.size() +"uploading list size = " + uploadingList.size());
		records.clear();
		for(int i=0;i< uploadingList.size();i++){
			records.add(uploadingList.get(i));
		}
		for(int i=0;i<waitingList.size();i++){
			records.add(waitingList.get(i));
		}
		
		if(records.size() > 0){
		//records.addAll(Generator.genRecords(50));
			totalPages=computeTotalPage(records.size());
		
			viewPager.setAdapter(new UploadingAdapter(getFragmentManager()));
		}
		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.v(TAG, "onActivityCreated ");
		myHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 500);
	}
	private final static int UPDATE_PROGRESS = 0;  
    Handler myHandler = new Handler(){
    
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			switch(msg.what){
				case UPDATE_PROGRESS:
					Log.v(TAG, "UPDATE_PROGRESS");
					waitingList = UploadDao.getUploadList(0);
					uploadingList = UploadDao.getUploadList(1);
					Log.v(TAG, "handleMessage " + "waiting list size =" + waitingList.size() +"uploading list size = " + uploadingList.size());
					records.clear();
					for(int i=0;i< uploadingList.size();i++){
						records.add(uploadingList.get(i));
					}
					for(int i=0;i<waitingList.size();i++){
						records.add(waitingList.get(i));
					}
					if(records.size()>0){
						setRecords(records);
						myHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 500);
					}else{
						UploadActivity uploadActivity=(UploadActivity) getActivity();
						uploadActivity.showUploadFinished();
					}
					break;
					
			}
			
			super.handleMessage(msg);
		}	
    };
	
	/**
	 * 真正使用时，一定要通过该方法来设置数据
	 * @param initRecords
	 */
	public void setRecords(List<DataInfo> initRecords) {
		if (initRecords!=null) {
			//records.clear();
			//records.addAll(initRecords);
			totalPages=computeTotalPage(records.size());
			viewPager.setAdapter(new UploadingAdapter(getFragmentManager()));
		}
	}
	
	public int computeTotalPage(int totalItems){
		if (totalItems<=0) {
			return 1;
		}
		return (int) Math.ceil(((double)totalItems)/SINGLE_PAGE_ITEMS);
	}
	
	
	public static class UploadingAdapter extends FragmentPagerAdapter {
		
		
		public UploadingAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return ArrayListFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			return totalPages;
		}
		
		public static class ArrayListFragment extends ListFragment {
			int mNum;
			static ArrayListFragment newInstance(int num){
				ArrayListFragment f=new ArrayListFragment();
				Bundle args=new Bundle();
				args.putInt("num", num);
				f.setArguments(args);
				return f;
			}
			
			@Override
			public void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				mNum=getArguments()!=null?getArguments().getInt("num"):1;
			}
			
			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container,
					Bundle savedInstanceState) {
				return inflater.inflate(R.layout.list_uploading, null);
			}
			
			@Override
			public void onActivityCreated(Bundle savedInstanceState) {
				super.onActivityCreated(savedInstanceState);
				List<DataInfo> pageData = getPageData(mNum);
				setListAdapter(new SinglePageAdapter(getActivity(), pageData));
			}
			
			@Override
			public void onListItemClick(ListView l, View v, int position, long id) {
				super.onListItemClick(l, v, position, id);
			}
			
			public static List<DataInfo> getPageData(int page){
				List<DataInfo> data=new ArrayList<DataInfo>();
				int startIndex=(int) (page*SINGLE_PAGE_ITEMS);
				int endIndex=(int) (startIndex+SINGLE_PAGE_ITEMS);
				if (endIndex>records.size()) {
					endIndex=records.size();
				}
				for(int i=startIndex;i<endIndex;i++){
					data.add(records.get(i));
				}
				return data;
			}
		}
		
		public static class SinglePageAdapter extends BaseAdapter{
			
			private List<DataInfo> records;
			private LayoutInflater inflater;
			private int progressLength;
			private ImageView ivTotalTask;
			
			public SinglePageAdapter(Context context,List<DataInfo> records){
				inflater = LayoutInflater.from(context);
				this.records=records;
			}
			public int getCount() {
				return records.size();
			}

			public Object getItem(int position) {
				return records.get(position);
			}

			public long getItemId(int position) {
				return position;
			}

			public View getView(int position, View convertView, ViewGroup parent) {
				View root = inflater.inflate(R.layout.item_uploading, null);
				TextView tvName=(TextView) root.findViewById(R.id.upload_filename);
				TextView tvDir=(TextView) root.findViewById(R.id.upload_dir);
				TextView tvPercent=(TextView) root.findViewById(R.id.upload_percent);
				ProgressBar progress = (ProgressBar) root.findViewById(R.id.uploading_progress);
				/*
				ivTotalTask = (ImageView)root.findViewById(R.id.upload_progress_total);
				ImageView ivFinishedTask=(ImageView)root.findViewById(R.id.upload_progress_finished);
				*/
				Button btnCancel=(Button)root.findViewById(R.id.btn_cancel);
				DataInfo record = records.get(position);
				
				tvName.setText(record.getName());
				//tvDir.setText(record.destDir);
				int percent = (int) (record.getProgress()*100/record.getFileSize());
				//int percent = record.getSliceDone()*100/record.getSliceTotal();
				tvPercent.setText("已完成"+ percent +"%");
				progress.setProgress(percent);
				/*
				if (progressLength>0) {
					if (ivTotalTask.getViewTreeObserver().isAlive()) {
						ivTotalTask.getViewTreeObserver().removeOnPreDrawListener(listener);
					}
					setWidth(ivFinishedTask, record);
				}else {
					ivTotalTask.getViewTreeObserver().addOnPreDrawListener(listener);
				}
				*/
				return root;
			}
			public void setWidth(ImageView ivFinishedTask, DataInfo record) {
				if (ivFinishedTask!=null) {
					LayoutParams params = ivFinishedTask.getLayoutParams();
					//params.width=record.finishedSize*progressLength/100;
					ivFinishedTask.setLayoutParams(params);
				}
			}
			OnPreDrawListener listener=new OnPreDrawListener() {
				public boolean onPreDraw() {
					progressLength=ivTotalTask.getMeasuredWidth();
					return true;
				}
			};
		}
	}
}
