package com.neteast.clouddisk.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.param.Params;

public class SearchDataAdapter extends BaseAdapter{
	
	private List<DataInfo> list;
	private Context context;
	private LibCloud libCloud;
	private DataInfo dataInfo;
	private OnLongClickListener mLongClickListener;
	private OnClickListener mClickListener;
	// 每页显示的Item个数
	public static final int SIZE = Params.MYUPLOAD_DATA_PER_PAGE_NUM;
	public SearchDataAdapter(Context context, List<DataInfo> result,int page,View.OnLongClickListener mLongClickListener,View.OnClickListener mClickListener) {
		this.context = context;
		this.mLongClickListener = mLongClickListener ;
		this.mClickListener = mClickListener;
		list = new ArrayList<DataInfo>();
		int i = page * SIZE;
		int iEnd = i + SIZE;
		while ((i < result.size()) && (i < iEnd)) {
			list.add(result.get(i));
			i++;
		}
		libCloud = LibCloud.getInstance(context);
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
		final DataInfo dataInfo  = list.get(position);
		dataInfo.setPosition(position);
		convertView = LayoutInflater.from(context).inflate(
				R.layout.myuploadvideoelementlayout, null);
		TextView textName = (TextView) convertView.findViewById(R.id.nameTextView);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
		textName.setText(dataInfo.getName());
		
		System.out.println(" SearchDataAdapter filename " + dataInfo.getName() + "image" + dataInfo.getImage());
		String fileType = "1";
		fileType = dataInfo.getType();
		//int fileType = DataHelpter.GetFileType(dataInfo.getName());
		
		if(fileType.equals("1")){
			if(dataInfo.getImage()!=null && dataInfo.getImage().length()>0){
				libCloud.DisplayImage(dataInfo.getImage(), imageView);
				
			}
		}else if(fileType.equals("2")){	
			imageView.setImageResource(R.drawable.audioimage);
			if(dataInfo.getImage()!=null && dataInfo.getImage().length()>0){
				libCloud.DisplayImage(dataInfo.getImage(), imageView);
			}
		}else if(fileType.equals("3")){
			if(dataInfo.getImage()!=null && dataInfo.getImage().length()>0){
				libCloud.DisplayImage(dataInfo.getImage(), imageView);
			}
		}else if(fileType.equals("4")){
			imageView.setImageResource(R.drawable.ico_text);
		}
	
		
		FrameLayout fl = (FrameLayout)((LinearLayout)convertView).getChildAt(0);
		fl.setOnClickListener(mClickListener);
		fl.setOnLongClickListener(mLongClickListener);
		fl.setTag(dataInfo);
		/*
		convertView.setOnClickListener(mClickListener);
		convertView.setOnLongClickListener(mLongClickListener);
		convertView.setTag(dataInfo);
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
