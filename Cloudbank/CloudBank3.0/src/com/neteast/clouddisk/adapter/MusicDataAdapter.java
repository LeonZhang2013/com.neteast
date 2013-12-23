package com.neteast.clouddisk.adapter;

import java.util.ArrayList;
import java.util.List;

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
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.utils.DownLoadApplication;
import com.neteast.clouddisk.utils.ImageDownloader2;


public class MusicDataAdapter extends BaseAdapter{
	private List<DataInfo> list;
	private Context context;
	private GestureDetector click;
	private LibCloud libCloud;
	private DataInfo dataInfo;
	private int lastPosition = -1;
	private int mPage = 0;
	private ImageDownloader2 mImageDownloader2;
	//ÿҳ��ʾ��Item����
	public static final int SIZE = Params.RECOMMEND_DATA_PER_PAGE_NUM;
	public MusicDataAdapter(Context context, List<DataInfo> result,int page) {
		this.context = context;
		mPage = page;
		list = new ArrayList<DataInfo>();
		int i = page * SIZE;
		int iEnd = i + SIZE;
		while ((i < result.size()) && (i < iEnd)) {
			list.add(result.get(i));
			i++;
		}
		//libCloud = LibCloud.getInstance(context);
		DownLoadApplication download = (DownLoadApplication)context.getApplicationContext();
		mImageDownloader2= download.getImageDownloader();
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
		if (convertView == null ){
			convertView = LayoutInflater.from(context).inflate(
					R.layout.recommendmusicelementlayout, null);
		}
		if(position == lastPosition) return convertView;
		lastPosition = position;
		//System.out.println(" VideoDataAdapter  position = " + position );
		TextView signerName = (TextView) convertView.findViewById(R.id.timeTextView);
		TextView textName = (TextView) convertView.findViewById(R.id.nameTextView);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
		ImageView downBtn = (ImageView) convertView.findViewById(R.id.musicdownloadbtn);
		downBtn.setTag(dataInfo);
		if(dataInfo.getStatus() == 1){
			downBtn.setImageResource(R.drawable.downloaded);
			downBtn.setClickable(false);
		}
		if(mPage==0){/*只加载第一页图片*/
			String imageurl = dataInfo.getImage();
			if(imageurl!=null && imageurl.length()>0){

				mImageDownloader2.download(imageurl, imageView,150,150);
				
			}
		}
		String singer = dataInfo.getSinger();
		
		textName.setText(dataInfo.getName());
		signerName.setText(singer);
		return convertView;
	}

	public DataInfo getDataInfo() {
		return dataInfo;
	}

	public void setDataInfo(DataInfo dataInfo) {
		this.dataInfo = dataInfo;
	}

}
