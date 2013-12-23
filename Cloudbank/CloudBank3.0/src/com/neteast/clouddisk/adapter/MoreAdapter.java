package com.neteast.clouddisk.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.utils.DownLoadApplication;

public class MoreAdapter extends BaseAdapter{
	private List<DataInfo> list;
	private LibCloud libCloud;
	private Context context;
	private DataInfo dataInfo;
	private Map<String, DataInfo> appList;
	public MoreAdapter(Context context, List<DataInfo> result,
			GestureDetector click) {
		this.context = context;
		list = result;
		libCloud = LibCloud.getInstance(context);
		appList = ((DownLoadApplication) context.getApplicationContext())
				.getAppList();
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final DataInfo dataInfo = (DataInfo) list.get(position);
		convertView = LayoutInflater.from(context).inflate(
				R.layout.moreelementlayout, null);
		TextView textName = (TextView) convertView
				.findViewById(R.id.nameTextView);
		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.imageView);
		libCloud.DisplayImage(dataInfo.getImage(), imageView);
		textName.setText(dataInfo.getName());
		return convertView;
	}

	public DataInfo getDataInfo() {
		return dataInfo;
	}

	public void setDataInfo(DataInfo dataInfo) {
		this.dataInfo = dataInfo;
	}
}
