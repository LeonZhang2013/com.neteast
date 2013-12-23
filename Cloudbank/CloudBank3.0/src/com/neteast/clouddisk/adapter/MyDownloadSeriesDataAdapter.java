package com.neteast.clouddisk.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.lib.db.DataInfo;
import com.neteast.clouddisk.R;

public class MyDownloadSeriesDataAdapter  extends BaseAdapter{
	private List<DataInfo> list;
	private Context context;
	private DataInfo dataInfo;
	private int SIZE = 0;
	public MyDownloadSeriesDataAdapter(Context context, List<DataInfo> result,int page,int pageNum) {
		this.context = context;
		//this.list = result;
		SIZE = pageNum;
		list = new ArrayList<DataInfo>();
		int i = page * SIZE;
		int iEnd = i + SIZE;
		while ((i < result.size()) && (i < iEnd)) {
			list.add(result.get(i));
			i++;
		}
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
		final DataInfo dataInfo  = (DataInfo)list.get(position);
		dataInfo.setPosition(position);
		convertView = LayoutInflater.from(context).inflate(
				R.layout.mydownloadserieselementlayout, null);
		TextView textName = (TextView) convertView.findViewById(R.id.mydownloadSeriesItem);
		//String str = String.format(convertView.getResources().getString(R.string.mydownload_series_text),position);
		//textName.setText(str);
		textName.setText(dataInfo.getName());
		
		/*
		convertView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//Toast.makeText(context,"点击事件"+(String)map.get("name"), Toast.LENGTH_SHORT).show();
				setDataInfo(dataInfo);
				return click.onTouchEvent(event);
			}
		});
		*/
		return convertView;
	}
	public DataInfo getDataInfo() {
		return dataInfo;
	}

	public void setDataInfo(DataInfo dataInfo) {
		this.dataInfo = dataInfo;
	}

}
