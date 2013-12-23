package com.neteast.cloudtv2.adapter;

import io.vov.utils.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.neteast.cloudtv2.Constant;
import com.neteast.cloudtv2.R;
import com.neteast.cloudtv2.bean.Channelbean;
import com.neteast.cloudtv2.tools.ImageLoader;

/**
 *
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-2-6
 */
public class FirstLevelGridViewAdapter extends BaseAdapter{
	
	private List<Channelbean> mListData;
	private Context mContext;
	private ImageLoader mImageLoader;
	
	public FirstLevelGridViewAdapter(Context context) {
		mContext = context;
		mImageLoader = ImageLoader.getInstanse(context); 
	}
	
	public FirstLevelGridViewAdapter(Context context,List<Channelbean> listData) {
		mContext = context;
		mImageLoader = ImageLoader.getInstanse(context); 
		mListData = listData;
	}
	
	public void setData(List<Channelbean> listData){
		mListData = listData;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if(mListData==null) return 0;
		return mListData.size();
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
		Channelbean bean = mListData.get(position);
		if(bean==null) return convertView;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.channel_item, null);
			holder.image = (ImageView) convertView.findViewById(R.id.channel_image_v);
			holder.name = (TextView) convertView.findViewById(R.id.channel_name_v);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		if(bean.getIcon().startsWith("assets")){
			holder.image.setImageBitmap(getImageFromAssetsFile(bean.getIcon()));
		}else{
			if(bean.getM3u8()!=null){
				mImageLoader.setScaledImage(Constant.PHOTO_PATH+bean.getIcon(), holder.image,1,System.currentTimeMillis());
			}else{
				mImageLoader.setScaledImage(Constant.DEMAND_PATH+bean.getIcon(), holder.image,1,System.currentTimeMillis());
			}
		}
		holder.name.setText(bean.getName());
		return convertView;
	}
	
	class ViewHolder{
		ImageView image;
		TextView name;
	}
	
	private Bitmap getImageFromAssetsFile(String fileName)  
	  {  
	      Bitmap image = null;  
	      AssetManager am = mContext.getResources().getAssets();  
	      try  
	      {  
	          InputStream is = am.open(fileName);  
	          image = BitmapFactory.decodeStream(is);  
	          is.close();  
	      }  
	      catch (IOException e)  
	      {  
	          e.printStackTrace();  
	      }  
	  
	      return image;  
	  
	  } 
}
