package com.hs.adapter;

import java.util.List;
import java.util.Map;

import com.hs.activity.R;

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

public class CommentDataAdapter extends BaseAdapter {
	private LayoutInflater mInflater = null;
	private List<Map<String, Object>> mList;
	public CommentDataAdapter(Context context,  List<Map<String, Object>> list) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		mList = list;

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
			convertView = mInflater.inflate(R.layout.item_comment, null);
			
		}
		final Map<String, Object> map  = mList.get(position);
		
		TextView name = (TextView) convertView.findViewById(R.id.comment_name);
		TextView content = (TextView) convertView.findViewById(R.id.comment_content);
		TextView time = (TextView) convertView.findViewById(R.id.coment_time);
		name.setText((String)map.get("username"));
		content.setText((String)map.get("content"));
		time.setText((String)map.get("addtime"));
		return convertView;
	}
}
