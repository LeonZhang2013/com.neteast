package com.neteast.clouddisk.adapter;

import java.util.List;
import java.util.Map;

import com.lib.db.DataInfo;
import com.neteast.clouddisk.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NewsListAdapter extends BaseAdapter {
	private LayoutInflater mInflater = null;
	private List<DataInfo> mList;
	public NewsListAdapter(Context context,  List<DataInfo> list) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		mList = list;
		System.out.println("NewsListAdapter mList size = " + mList.size());
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if(mList==null)
			return convertView;
		if (convertView == null && null != mInflater) {
			convertView = mInflater.inflate(R.layout.newslist_item, null);
			
		}
		final DataInfo  info  = mList.get(position);
		
		TextView title = (TextView) convertView.findViewById(R.id.news_title);
		TextView time = (TextView) convertView.findViewById(R.id.news_time);
		TextView source = (TextView) convertView.findViewById(R.id.news_source);
		title.setText(info.getName());
		time.setText(info.getSourceTime());
		source.setText(info.GetTag());
		return convertView;
	}
}
