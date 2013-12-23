package com.neteast.clouddisk.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.utils.DownLoadApplication;
import com.neteast.clouddisk.utils.ImageDownloader2;

public class PictureDataAdapter  extends BaseAdapter{
	private List<DataInfo> list;
	private Context context;
	private GestureDetector click;
	private LibCloud libCloud;
	private DataInfo dataInfo;
	//每页显示的Item个数
	public static final int SIZE = Params.RECOMMEND_PICTRUE_PER_PAGE_NUM;
	private int lastPosition = -1;
	private int mPage = 0;
	private ImageDownloader2 mImageDownloader2;
	
	public PictureDataAdapter(Context context, List<DataInfo> result,int page) {
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
		
		final DataInfo dataInfo = (DataInfo)list.get(position);
		if (convertView == null ){
			convertView = LayoutInflater.from(context).inflate(
					R.layout.recommendpictureelementlayout, null);
		}
		if(position == lastPosition) return convertView;
		lastPosition = position;
		//System.out.println(" VideoDataAdapter  position = " + position );
		dataInfo.setPosition(position);
		FrameLayout imageLayout = (FrameLayout) convertView.findViewById(R.id.imageLayout);
		FrameLayout imagesLayout = (FrameLayout) convertView.findViewById(R.id.imagesLayout);
		TextView imagesnum = (TextView) convertView.findViewById(R.id.imagesnumText);
		ImageView imageView ;
		if(dataInfo.GetIsDir()!=null && dataInfo.GetIsDir().equals("1")){
			imagesLayout.setVisibility(View.VISIBLE);
			imageLayout.setVisibility(View.GONE);
			imageView = (ImageView) convertView.findViewById(R.id.imageView1);
			List<String> images = dataInfo.getImages();
			imagesnum.setText(images.size()+"");
		}else{
			imageLayout.setVisibility(View.VISIBLE);
			imagesLayout.setVisibility(View.GONE);
			imageView = (ImageView) convertView.findViewById(R.id.imageView);
		}

		String thumb = dataInfo.getThumb();
		//System.out.println("image url = " + thumb);
		if(mPage==0){
			if(thumb!=null&&thumb.length()>0 ){
				//libCloud.DisplayImage(thumb, imageView);
				mImageDownloader2.download(thumb, imageView);
			}
		}
		return convertView;
	}

	public DataInfo getDataInfo() {
		return dataInfo;
	}

	public void setDataInfo(DataInfo dataInfo) {
		this.dataInfo = dataInfo;
	}

}
