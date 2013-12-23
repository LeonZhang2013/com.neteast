package com.neteast.clouddisk.adapter;

import java.util.List;

import com.lib.db.DataInfo;
import com.neteast.data_acquisition.DataAcqusition;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class VarietyAdapter extends BaseAdapter{
	private List list;
	private Context context;
	private GestureDetector click;
	private DataInfo dataInfo;
	public VarietyAdapter(Context context, List result, GestureDetector click) {
		this.context = context;
		list = result;
		this.click = click;
		 
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		TextView tv  = new TextView(context);
		tv.setTextSize(18);
		tv.setSingleLine();
		final DataInfo di = (DataInfo)list.get(position);
		/*
			int len = di.getName().length();
			if (len > 35) {
				tv.setText(di.getName().substring(0,35) + "..");
			} else {
				tv.setText(di.getName());
			} 
		*/
		tv.setText(di.getName());
		convertView = tv;
		tv.setOnTouchListener(new OnTouchListener(){
			
			@Override
			public boolean onTouch(View arg0, MotionEvent ev) {
				setDataInfo(di);
				return click.onTouchEvent(ev);
			}
			
		});
		return convertView;
	}

	public DataInfo getDataInfo() {
		return dataInfo;
	}

	public void setDataInfo(DataInfo dataInfo) {
		this.dataInfo = dataInfo;
	}
}
