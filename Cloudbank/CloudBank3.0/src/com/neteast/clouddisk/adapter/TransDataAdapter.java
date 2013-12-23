package com.neteast.clouddisk.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.param.Params;

public class TransDataAdapter  extends BaseAdapter{
	private List<DataInfo> list;
	private Context context;
	private LibCloud libCloud;

	private DataInfo dataInfo;
	private int lastPosition = -1;
	private int mPage = 0;


	// 每页显示的Item个数
	public static final int SIZE = Params.TRANSCODE_PER_PAGE_NUM;
	
	public TransDataAdapter(Context context, List<DataInfo> result,int page) {
		this.context = context;
		list = new ArrayList<DataInfo>();
		mPage = page;
		int i = page * SIZE;
		int iEnd = i + SIZE;
		while ((i < result.size()) && (i < iEnd)) {
			list.add(result.get(i));
			i++;
		}
		libCloud = LibCloud.getInstance(context);
	}
	
	public List<DataInfo> getList(){
		return list;
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
		
		final DataInfo  dataInfo = (DataInfo)list.get(position);	
		if (convertView == null ){
			convertView = LayoutInflater.from(context).inflate(
					R.layout.transcodeelementlayout, null);
		}
		/*
		if(position == lastPosition) return convertView;
		
		lastPosition = position;
		*/
		//System.out.println(" VideoDataAdapter11111  position = " + position + "lastPosition " + lastPosition);
		
		TextView textName = (TextView) convertView.findViewById(R.id.nameTextView);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
		ImageView transbtn = (ImageView) convertView.findViewById(R.id.transcodebtn);
		ProgressBar pb= (ProgressBar) convertView.findViewById(R.id.transcodeprogress);
		dataInfo.setPosition(position);
		transbtn.setTag(dataInfo);
		String imageurl = dataInfo.getThumb();
		if(imageurl!=null && imageurl.length()>0){
			libCloud.DisplayImage(imageurl,imageView);
		}
		if(dataInfo.GetTag().equals("640")){
			textName.setText("标清 640x480");
		}else if(dataInfo.GetTag().equals("1280")){
			textName.setText("高清 1280x720");
		}else if(dataInfo.GetTag().equals("1920")){
			textName.setText("全高清 1920*1280");
		}else if(dataInfo.GetTag().equals("1024")){
			textName.setText("ipad专用1024*768");
		}
		//********** ***********************************
		pb.setVisibility(View.GONE);
		transbtn.setImageResource(R.drawable.transplaybtn);
		//*************************************************
/*		System.out.println("datainfo status = " + dataInfo.getStatus() + "datainfo percent + " + dataInfo.getProgress());
		if(dataInfo.getStatus()==3){
			transbtn.setImageResource(R.drawable.transcodeing);
			pb.setVisibility(View.VISIBLE);
			pb.setProgress((int) dataInfo.getProgress());
		}
		else if(dataInfo.getStatus()==5 || dataInfo.getStatus()==4){
			pb.setVisibility(View.GONE);
			transbtn.setImageResource(R.drawable.transplaybtn);
		}*/
		return convertView;
	}

	public DataInfo getDataInfo() {
		return dataInfo;
	}

	public void setDataInfo(DataInfo dataInfo) {
		this.dataInfo = dataInfo;
	}
	
}
