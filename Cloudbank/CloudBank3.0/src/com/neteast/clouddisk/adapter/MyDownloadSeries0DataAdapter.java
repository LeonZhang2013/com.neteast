package com.neteast.clouddisk.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.neteast.clouddisk.R;

public class MyDownloadSeries0DataAdapter  extends BaseAdapter{
	private List<String> list;
	private Context context;
	//private GestureDetector click;
	public MyDownloadSeries0DataAdapter(Context context, List<String> result) {
		this.context = context;
		//this.click = click;
		this.list = result;
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
		final String value  = list.get(position);
		convertView = LayoutInflater.from(context).inflate(
				R.layout.mydownloadserieselementlayout, null);
		TextView textName = (TextView) convertView.findViewById(R.id.mydownloadSeriesItem);
		//String str = String.format(convertView.getResources().getString(R.string.mydownload_series_text),position);
		textName.setText(value);
		
		/*
		convertView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//Toast.makeText(context,"点击事件"+(String)map.get("name"), Toast.LENGTH_SHORT).show();
				return click.onTouchEvent(event);
			}
		});
		*/
		return convertView;
	}


}
