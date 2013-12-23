package com.neteast.clouddisk.fragment;

import java.util.ArrayList;
import java.util.List;

import com.neteast.clouddisk.R;
import com.neteast.clouddisk.utils.Generator;
import com.neteast.clouddisk.utils.Record;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 显示上传记录的fragment
 * @author tiangh
 * 2012-4-27 下午4:52:54
 */
public class UploadRecordFragment extends Fragment {
	
	private TextView tvPage;
	
	private ViewPager viewPager;
	private final static List<Record> records=new ArrayList<Record>();
	
	private final static Double SINGLE_PAGE_ITEMS=6.00;
	private static int totalPages;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_upload_record, null);
		//tvPage = (TextView) root.findViewById(R.id.page);
		//viewPager = (ViewPager) root.findViewById(R.id.pager);
		//这是生成测试数据，真正使用时删除。
		records.clear();
		records.addAll(Generator.genRecords(50));
		totalPages=computeTotalPage(records.size());
		
		tvPage.setText("1/"+totalPages);
		viewPager.setAdapter(new UploadRecordAdapter(getFragmentManager()));
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int page) {
				totalPages = computeTotalPage(records.size());
				tvPage.setText((page+1)+"/"+totalPages);
			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			public void onPageScrollStateChanged(int arg0) {}
		});
		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	/**
	 * 真正使用时，用该方法设置数据
	 * @param initRecords
	 */
	public void setRecords(List<Record> initRecords) {
		if (initRecords!=null) {
			records.clear();
			records.addAll(initRecords);
			totalPages=computeTotalPage(records.size());
		}
	}
	
	public int computeTotalPage(int totalItems){
		if (totalItems<=0) {
			return 1;
		}
		return (int) Math.ceil(((double)totalItems)/SINGLE_PAGE_ITEMS);
	}
	
	
	public static class UploadRecordAdapter extends FragmentPagerAdapter {
		
		
		public UploadRecordAdapter(FragmentManager fm) {
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
				return inflater.inflate(R.layout.list_record, null);
			}
			
			@Override
			public void onActivityCreated(Bundle savedInstanceState) {
				super.onActivityCreated(savedInstanceState);
				List<Record> pageData = getPageData(mNum);
				setListAdapter(new SinglePageAdapter(getActivity(), pageData));
			}
			
			@Override
			public void onListItemClick(ListView l, View v, int position, long id) {
				super.onListItemClick(l, v, position, id);
			}
			
			public static List<Record> getPageData(int page){
				List<Record> data=new ArrayList<Record>();
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
			
			private List<Record> records;
			private LayoutInflater inflater;
			public SinglePageAdapter(Context context,List<Record> records){
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
				View root = inflater.inflate(R.layout.item_upload_record, null);
				/*
				ImageView ivICO=(ImageView) root.findViewById(R.id.record_ico);
				TextView tvName=(TextView) root.findViewById(R.id.record_name);
				TextView tvSize=(TextView) root.findViewById(R.id.record_filesize);
				TextView tvTime=(TextView) root.findViewById(R.id.record_uploadtime);
				Record record = records.get(position);
				ivICO.setImageResource(record.fileType.getResource());
				tvName.setText(record.fileName);
				tvSize.setText(record.fileSize);
				tvTime.setText(record.uploadTime);
				*/
				return root;
			}
		}
	}
}
