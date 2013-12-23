package com.neteast.clouddisk.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.utils.DownLoadApplication;
import com.neteast.clouddisk.utils.ImageDownloader2;

public class MyDownloadVideoDataAdapter  extends BaseAdapter{
	private List<DataInfo> list;
	private Context context;
	private LibCloud libCloud;
	private GestureDetector click;
	private DataInfo dataInfo;
	private OnLongClickListener mLongClickListener;
	private OnClickListener mClickListener;
	private int lastPosition = -1;
	private int mPage = -1;
	// 每页显示的Item个数
	public static final int SIZE = Params.MYUPLOAD_DATA_PER_PAGE_NUM;
	private ImageDownloader2 mImageDownloader2;
	public MyDownloadVideoDataAdapter(Context context, List<DataInfo> result,int page,View.OnLongClickListener mLongClickListener,View.OnClickListener mClickListener) {
		this.context = context;
		//this.click = click;
		//this.list = result;
		this.mLongClickListener = mLongClickListener ;
		this.mClickListener = mClickListener;
		list = new ArrayList<DataInfo>();
		int i = page * SIZE;
		int iEnd = i + SIZE;
		while ((i < result.size()) && (i < iEnd)) {
			list.add(result.get(i));
			i++;
		}
		mPage = page;
		libCloud = LibCloud.getInstance(context);
		DownLoadApplication download = (DownLoadApplication)context.getApplicationContext();
		mImageDownloader2= download.getImageDownloader();
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

		if (convertView == null ){
			convertView = LayoutInflater.from(context).inflate(
				R.layout.mydownloadvideoelementlayout, null);
		}
	//	System.out.println("MyDownloadVideoDataAdapter position = " + position);
		final DataInfo  dataInfo = (DataInfo)list.get(position);
	/*
		if(position == lastPosition) return convertView;
		lastPosition = position;
	*/	
		dataInfo.setPosition(position);
		TextView textName = (TextView) convertView
				.findViewById(R.id.nameTextView);
		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.imageView);
		LinearLayout itemView = (LinearLayout) convertView.findViewById(R.id.itemView);
		LinearLayout mergeView = (LinearLayout) convertView.findViewById(R.id.mergeView);

		if (dataInfo.GetIsDir() != null && dataInfo.GetIsDir().equals("1")) {
			mergeView.setVisibility(View.VISIBLE);
			mergeView.removeAllViews();
			itemView.setVisibility(View.GONE);
			String images = dataInfo.getThumb();
			String[] imageArray = images.split(",");
			TableLayout tl = (TableLayout) LayoutInflater.from(context)
					.inflate(R.layout.tablelayout, null);
			TableRow tr1 = (TableRow) tl.getChildAt(0);
			TableRow tr2 = (TableRow) tl.getChildAt(1);
			TableRow tr3 = (TableRow) tl.getChildAt(2);
			if(mPage == 0){
				for (int i = 0; i < imageArray.length; i++) {
					String smallurl = imageArray[i];
					switch (i) {
					case 0:
						mImageDownloader2.download(smallurl, (ImageView)tr1.getChildAt(0));
						//libCloud.DisplayImage(smallurl, (ImageView)tr1.getChildAt(0));
						break;
					case 1:
						mImageDownloader2.download(smallurl, (ImageView)tr1.getChildAt(1));
						//libCloud.DisplayImage(smallurl, (ImageView)tr1.getChildAt(1));
						break;
					case 2:
						mImageDownloader2.download(smallurl, (ImageView)tr1.getChildAt(2));
						//libCloud.DisplayImage(smallurl, (ImageView)tr1.getChildAt(2));
						break;
					case 3:
						mImageDownloader2.download(smallurl, (ImageView)tr2.getChildAt(0));
						//libCloud.DisplayImage(smallurl, (ImageView)tr2.getChildAt(0));
						break;
					case 4:
						mImageDownloader2.download(smallurl, (ImageView)tr2.getChildAt(1));
						//libCloud.DisplayImage(smallurl, (ImageView)tr2.getChildAt(1));
						break;
					case 5:
						mImageDownloader2.download(smallurl, (ImageView)tr2.getChildAt(2));
						//libCloud.DisplayImage(smallurl, (ImageView)tr2.getChildAt(2));
						break;
					case 6:
						mImageDownloader2.download(smallurl, (ImageView)tr3.getChildAt(0));
						//libCloud.DisplayImage(smallurl, (ImageView)tr3.getChildAt(0));
						break;
					case 7:
						mImageDownloader2.download(smallurl, (ImageView)tr3.getChildAt(1));
						//libCloud.DisplayImage(smallurl, (ImageView)tr3.getChildAt(1));
						break;
					case 8:
						mImageDownloader2.download(smallurl, (ImageView)tr3.getChildAt(2));
						//libCloud.DisplayImage(smallurl, (ImageView)tr3.getChildAt(2));
						break;
					}
				}
			}
			mergeView.addView(tl);
			textName.setVisibility(View.INVISIBLE);
		} else {
			if(mPage ==0){
			String imageurl = dataInfo.getThumb();
				if(imageurl!=null && imageurl.length()>0){
					//String smallurl = imageurl;
					//libCloud.DisplayImage(smallurl, imageView);
					mImageDownloader2.download(imageurl, imageView);
				}
			}
			textName.setText(dataInfo.getName());
		}
		FrameLayout fl = (FrameLayout)((LinearLayout)convertView).getChildAt(0);
		fl.setOnClickListener(mClickListener);
		fl.setOnLongClickListener(mLongClickListener);
		fl.setTag(dataInfo);
		
		return convertView;
	}
	public DataInfo getDataInfo() {
		return dataInfo;
	}

	public void setDataInfo(DataInfo dataInfo) {
		this.dataInfo = dataInfo;
	}

}
