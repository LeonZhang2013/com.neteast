package com.neteast.clouddisk.adapter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lib.db.DataInfo;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.activity.FlashViewActivity;
import com.neteast.clouddisk.activity.MovieDetailActivity;
import com.neteast.clouddisk.activity.SearchResultActivity1;
import com.neteast.clouddisk.activity.VideoPlaybackActivity;
import com.neteast.clouddisk.utils.MediaPlayerHelper;
import com.neteast.clouddisk.utils.UIHelper;
import com.neteast.data_acquisition.DataAcqusition;

public class DetailEveryAdapter extends BaseAdapter{
	private List<DataInfo> list;
	private Context context;
	private int mtype = 1;
	public DetailEveryAdapter(Context context, List<DataInfo> result,int type) {
		this.context = context;
		list = result;
		this.mtype = type;
		 
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
		TextView tv  = new TextView(context);
		tv.setTextSize(18);
		final DataInfo di = list.get(position);
		String   src     =  di.GetSeries();
		String   regex   =   "\\d+"; 
		Pattern   p=   Pattern.compile(regex); 
		Matcher   m   =   p.matcher(src); 
		boolean flag = true;
		String rel = "";
		while(m.find()&&flag){ 
			rel= m.group(); 
			flag = false;

		}
		tv.setText("第"+rel+"集");
		convertView = tv;
		convertView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(mtype == 1){
					/*
					Intent it = new Intent();  
			    	it.setClass(context,FlashViewActivity.class);
			    	it.putExtra("url", di.getUrl());
			    	context.startActivity(it); 
					*/
					if(di.getUrl()!=null && di.getUrl().length()>0){
						MediaPlayerHelper.play(context, di.getUrl(),di.getMovieId(),di.getVideoid(),"1");
						DataAcqusition.setOnlineorderRecord(di.getMovieId(), di.getVideoid(), 1);
						Log.i("Other", "detailOnlinePlay...........................");
						/*
						Intent it = new Intent(); 
				    	it.setClass(context,VideoPlaybackActivity.class);
				    	Uri uri = Uri.parse(di.getUrl());
				    	it.setData(uri);
				    	it.putExtra("position", 0);
				    	context.startActivity (it);
				    	*/
					}else{
						UIHelper.displayToast(context.getResources().getString(R.string.url_invalid), context);
					}
					
				}else if(mtype == 2){
					if(di.getUrl()!=null && di.getUrl().length()>0){
						MediaPlayerHelper.play(context, di.getUrl(),di.getMovieId(),di.getVideoid(),"2");
						DataAcqusition.setOnlineorderRecord(di.getMovieId(), di.getVideoid(), 2);
						Log.i("Other", "detailHDPlay...........................");
						/*
						Intent it = new Intent(); 
				    	it.setClass(context,VideoPlaybackActivity.class);
				    	Uri uri = Uri.parse(di.getUrl());
				    	it.setData(uri);
				    	it.putExtra("position", 0);
				    	context.startActivity (it);
				    	*/
					}else{
						UIHelper.displayToast(context.getResources().getString(R.string.url_invalid), context);
					}
				}
			}
			
		});
		return convertView;
	}
}
