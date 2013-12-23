package com.neteast.clouddisk.adapter;

import java.util.List;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.neteast.clouddisk.R;
import com.neteast.clouddisk.activity.MovieDetailActivity;

public class DetailAdapter extends BaseAdapter{
	private List<String>  list;
	private Context context;
	private GestureDetector click;
	private String  key;
	private int derict=1;
	public DetailAdapter(Context context, List<String> result,
			GestureDetector click,int derict) {
		this.context = context;
		this.click = click;
		list = result;
		this.derict = derict;
		 
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
	public View getView(final int position, View convertView,final  ViewGroup parent) {
		RelativeLayout fl = (RelativeLayout)LayoutInflater.from(context).inflate(R.layout.titleelement, null);
		TextView tv  = (TextView)fl.findViewById(R.id.title); 
		final ImageView bg  = (ImageView)fl.findViewById(R.id.elementbg); 
		if(derict==2){
			if(position==getCount()-1){
				bg.setVisibility(View.VISIBLE);
			}
		}else{
			if(position==0){
				bg.setVisibility(View.VISIBLE);
			} 
		}
		tv.setText(list.get(position)+"");
		convertView = fl;
		tv.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent ev) {
				for(int i=0;i<parent.getChildCount();i++){
					((RelativeLayout)parent.getChildAt(i)).getChildAt(1).setVisibility(View.INVISIBLE);
				}
				((MovieDetailActivity)context).setCurrentPage(position+1);
				bg.setVisibility(View.VISIBLE);
				setKey(list.get(position)+"");
				return click.onTouchEvent(ev);
			}
		});
		return convertView;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	 
}
