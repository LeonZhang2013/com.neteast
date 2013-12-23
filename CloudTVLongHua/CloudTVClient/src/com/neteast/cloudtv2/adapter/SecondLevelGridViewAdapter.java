package com.neteast.cloudtv2.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.neteast.cloudtv2.Constant;
import com.neteast.cloudtv2.R;
import com.neteast.cloudtv2.bean.VideoBean;
import com.neteast.cloudtv2.tools.ImageLoader;
import com.neteast.cloudtv2.tools.NetUtils;

/**
 *
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-2-6
 */
public class SecondLevelGridViewAdapter extends BaseAdapter{
	
	private List<VideoBean> mListData;
	private Context mContext;
	private ImageLoader mImageLoader;
	
	public SecondLevelGridViewAdapter(Context context) {
		mContext = context;
		mImageLoader = ImageLoader.getInstanse(context); 
	}
	
	public SecondLevelGridViewAdapter(Context context,List<VideoBean> listData) {
		mContext = context;
		mImageLoader = ImageLoader.getInstanse(context); 
		mListData = listData;
	}
	
	public void setData(List<VideoBean> listData){
		mListData = listData;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mListData==null ? 0 : mListData.size();
	}

	@Override
	public Object getItem(int position) {
		return mListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		VideoBean bean = mListData.get(position);
		if(bean==null) return convertView;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.video_grid_item, null);
			holder.image = (ImageView) convertView.findViewById(R.id.image_view);
			holder.videoName = (TextView) convertView.findViewById(R.id.video_name);
			holder.playDesc = (TextView) convertView.findViewById(R.id.play_desc);
			holder.playTime = (TextView) convertView.findViewById(R.id.play_time);
			holder.ellipsis = (TextView) convertView.findViewById(R.id.ellipsis);
			holder.descBtn = (Button) convertView.findViewById(R.id.desc_button);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		lineColor(convertView,position);
		Log.i("ii", NetUtils.CorrectPath(Constant.DEMAND_PATH)+bean.getImagePath());
		mImageLoader.setScaledImage(NetUtils.CorrectPath(Constant.DEMAND_PATH)+bean.getImagePath(), holder.image,2,System.currentTimeMillis());
		holder.videoName.setText(bean.getName());
		holder.playTime.setText(bean.getPlayTime());
		holder.playDesc.setText(bean.getDescrip());
		holder.descBtn.setTag(bean);
		if(bean.getDescrip().length()<=20)holder.ellipsis.setVisibility(View.GONE);
		return convertView;
	}
	
	class ViewHolder{
		ImageView image;
		TextView videoName;
		TextView playDesc;
		TextView playTime;
		TextView ellipsis;
		Button descBtn;
	}
	
	private void lineColor(View convertView,int position){
		if(position%6<3) {
		convertView.setBackgroundColor(Color.parseColor("#252525"));
		}else{ 
		convertView.setBackgroundColor(Color.parseColor("#000000"));
		}
	}

}
