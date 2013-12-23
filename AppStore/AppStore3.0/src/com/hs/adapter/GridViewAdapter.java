package com.hs.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hs.activity.R;
import com.hs.domain.AppBean;
import com.hs.params.Params;
import com.lib.appstore.LibAppstore;
import com.lib.net.WeiboException;

/** @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2012-11-2 */
public class GridViewAdapter extends BaseAdapter {

	private Context mContext;
	private List<AppBean> list;

	public GridViewAdapter(Context mContext, List<AppBean> list) {
		this.mContext = mContext;
		this.list = list;
	}

	@Override
	public int getCount() {
		if(list == null) return 0;
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		if(list == null) return null;
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(list == null) return null;
		ViewHolder mViewHolder;
		if (convertView == null) {
			mViewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.elementlayout, null);
			mViewHolder.mImageView = (ImageView) convertView.findViewById(R.id.elementimageView);
			mViewHolder.mAppName = (TextView) convertView.findViewById(R.id.app_name);
			mViewHolder.mAppType = (TextView) convertView.findViewById(R.id.app_type);
			mViewHolder.mStar = (RatingBar) convertView.findViewById(R.id.star_view);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		if(parent.getChildAt(position)==null){
			AppBean bean = list.get(position);
			LibAppstore.getInstance(mContext).DisplayImage(bean.getImage(), mViewHolder.mImageView);
			mViewHolder.mAppName.setText(bean.getTitle());
			mViewHolder.mAppType.setText(Params.getTypeName(bean.getType()));
			convertView.setId(bean.getId());
			if(bean.getRating().equals("")){
				mViewHolder.mStar.setRating(0);
			}else{
				try{
					mViewHolder.mStar.setRating(Float.parseFloat(bean.getRating()));
				}catch (Exception e) {
				}
			}
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView mImageView;
		TextView mAppName;
		TextView mAppType;
		RatingBar mStar;
	}
}
