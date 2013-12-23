package com.neteast.clouddisk.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.neteast.clouddisk.utils.DownLoadApplication;
import com.neteast.clouddisk.utils.ImageDownloader2;

public class VideoDataAdapter  extends BaseAdapter{
	private List<DataInfo> list;
	private Context context;
	private LibCloud libCloud;

	private DataInfo dataInfo;
	private int lastPosition = -1;
	private OnClickListener mClickListener;
	private int mPage = 0;
	private int mdisplayImage = 0;

	// 每页显示的Item个数
	public static final int SIZE = Params.RECOMMEND_DATA_PER_PAGE_NUM;
	
	private ImageDownloader2 mImageDownloader2;
	
	public VideoDataAdapter(Context context, List<DataInfo> result,int page,int displayImage,View.OnClickListener mClickListener) {
		this.context = context;
		this.mClickListener = mClickListener;
		list = new ArrayList<DataInfo>();
		mPage = page;
		mdisplayImage = displayImage;
		int i = page * SIZE;
		int iEnd = i + SIZE;
		while ((i < result.size()) && (i < iEnd)) {
			list.add(result.get(i));
			i++;
		}
		libCloud = LibCloud.getInstance(context);
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
		
		final DataInfo  dataInfo = (DataInfo)list.get(position);	
		if (convertView == null ){
			convertView = LayoutInflater.from(context).inflate(R.layout.recommendvideoelementlayout, null);
		}
		
		
		if(position == lastPosition) return convertView;
		
		lastPosition = position;
		
		
		//System.out.println(" VideoDataAdapter11111  position = " + position + "lastPosition " + lastPosition + "mPage = " + mPage);
		
		TextView textName = (TextView) convertView.findViewById(R.id.nameTextView);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
		TextView gradeText = (TextView) convertView.findViewById(R.id.grade);
		dataInfo.setPosition(position);
		
		if(mdisplayImage ==1){/*加载图片*/
			String imageurl = dataInfo.getImage();
			//System.out.println("recommend video  imageurl = " + imageurl);
			if(imageurl!=null && imageurl.length()>0){
				/*
				int index = imageurl.lastIndexOf("/");
				String filename=imageurl.substring(index + 1);
				String smallName = filename.replace(".", "_s.");
				String smallurl = imageurl.substring(0,index+1)+smallName;
				libCloud.DisplayImage(smallurl,imageView);
				*/
				//libCloud.DisplayImage(imageurl,imageView);
				
				mImageDownloader2.download(imageurl, imageView);
			}
		}
		textName.setText(dataInfo.getName());
		gradeText.setText(dataInfo.getRemark());
		
		FrameLayout fl = (FrameLayout)((LinearLayout)convertView).getChildAt(0);
		fl.setOnClickListener(mClickListener);
		fl.setTag(dataInfo);
		//printCurTime("end videoDataAdapter get View");
		return convertView;
	}

	public DataInfo getDataInfo() {
		return dataInfo;
	}

	public void setDataInfo(DataInfo dataInfo) {
		this.dataInfo = dataInfo;
	}
	
}
