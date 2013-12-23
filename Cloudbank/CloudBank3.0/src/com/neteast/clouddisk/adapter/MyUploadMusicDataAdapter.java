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

public class MyUploadMusicDataAdapter extends BaseAdapter{
	private List<DataInfo> list;
	private Context context;
	private DataInfo dataInfo;
	private LibCloud libCloud;
	private OnLongClickListener mLongClickListener;
	private OnClickListener mClickListener;
	// 每页显示的Item个数
	public static final int SIZE = Params.MYUPLOAD_DATA_PER_PAGE_NUM;
	
	public MyUploadMusicDataAdapter(Context context, List<DataInfo> result,int page,View.OnLongClickListener mLongClickListener,View.OnClickListener mClickListener) {
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
	public List<DataInfo> getList(){
		return list;
	}
	public DataInfo getDataInfo() {
		return dataInfo;
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
		//final Map<String,Object> map  = (Map<String,Object>)list.get(position);
		final DataInfo  dataInfo = (DataInfo)list.get(position);
		dataInfo.setPosition(position);
		convertView = LayoutInflater.from(context).inflate(
				R.layout.myuploadmusicelementlayout, null);
		//convertView.setTag(info);
		TextView textName = (TextView) convertView.findViewById(R.id.nameTextView);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
		ImageView imageView1 = (ImageView) convertView.findViewById(R.id.imageView1);
		View v = convertView
				.findViewById(R.id.encrypt);
		FrameLayout fileitemView = (FrameLayout) convertView.findViewById(R.id.fileitemView);
		FrameLayout folderitemView = (FrameLayout) convertView.findViewById(R.id.folderitemView);
		if ((dataInfo.GetIsDir() != null) && (dataInfo.GetIsDir().equals("1"))) {
			//imageView.setImageResource(R.drawable.ico_video);
			folderitemView.setVisibility(View.VISIBLE);
			fileitemView.setVisibility(View.GONE);
			//libCloud.DisplayImage(dataInfo.getThumb(), imageView1);
			v =  convertView.findViewById(R.id.folderencrypt);
		} else {
			/*
			if(dataInfo.getThumb()!=null && dataInfo.getThumb().length()>0)
				libCloud.DisplayImage(dataInfo.getThumb(),imageView);
			*/
		}
		if ((dataInfo.getSecurity() != null) && (dataInfo.getSecurity().equals("1"))) {
			v.setVisibility(View.VISIBLE);
		}
		textName.setText(dataInfo.getName());
		
		
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

	public void setDataInfo(DataInfo dataInfo) {
		this.dataInfo = dataInfo;
	}
}
