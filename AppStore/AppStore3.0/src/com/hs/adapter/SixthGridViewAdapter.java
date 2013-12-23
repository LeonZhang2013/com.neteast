package com.hs.adapter;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.hs.activity.R;
import com.hs.domain.AppBean;
import com.hs.params.Params;
import com.hs.utils.AppData;
import com.hs.utils.Tools;
import com.lib.net.ImageLoader;

/** @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2012-11-2 */
public class SixthGridViewAdapter extends BaseAdapter {

	private Context mContext;
	private List<AppBean> list;
	private AppData mAppData;
	private int mCategory;

	public SixthGridViewAdapter(Context mContext, List<AppBean> list, int category) {
		this.mContext = mContext;
		this.list = list;
		this.mCategory = category;
		mAppData = AppData.getInstance(mContext);
	}

	@Override
	public int getCount() {
		if (list == null)
			return 0;
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		if (list == null)
			return null;
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (list == null)
			return null;
		switch (mCategory) {
		case 0:
			convertView = LoadingDownloadData(position, convertView, parent);
			break;
		case 1:
			convertView = LoadingInstalledData(position, convertView, parent);
			break;
		case 2:
			convertView = LoadingUpgradeData(position, convertView, parent);
			break;
		}
		return convertView;
	}

	/** 加载下载的数据
	 * 
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return */
	private View LoadingDownloadData(int position, View convertView, ViewGroup parent) {
		ViewHolder mViewHolder;
		if (convertView == null) {
			mViewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.sixthdatalayout1, null);
			mViewHolder.mImageView = (ImageView) convertView.findViewById(R.id.elementimageView);
			mViewHolder.mAppName = (TextView) convertView.findViewById(R.id.appname);
			mViewHolder.mAppVersion = (TextView) convertView.findViewById(R.id.appversion);
			mViewHolder.mAppSize = (TextView) convertView.findViewById(R.id.appsize);
			mViewHolder.mComplete = (TextView) convertView.findViewById(R.id.complete);
			mViewHolder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.downloadprogress);
			mViewHolder.btn1 = (Button) convertView.findViewById(R.id.installbutton);
			mViewHolder.btn2 = (Button) convertView.findViewById(R.id.deletebutton);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		if (parent.getChildAt(position) == null) {// 防止重复加载
			AppBean bean = list.get(position);
			mViewHolder.mAppName.setText((String) bean.getTitle());
			mViewHolder.mAppVersion.setText("版本：" + bean.getVersion());
			mViewHolder.mAppSize.setText("大小：" + bean.getSize());
			ImageLoader.getInstanse(mContext).setImage((String) bean.getImage(), mViewHolder.mImageView);
			int downloadstatus = bean.getDownloadStatus();
			int id = bean.getId();
			if (bean.getStatus() == 1 && downloadstatus == 2) {
				mViewHolder.btn1.setEnabled(false);
			}
			if (downloadstatus == 1) {
				DownloadProgressHandler handler = new DownloadProgressHandler(mViewHolder.mProgressBar, mViewHolder.btn1,
						mViewHolder.mComplete);
				mAppData.addHandler(id, handler);
				convertView.setId(id);
				mViewHolder.btn1.setEnabled(false);
				mViewHolder.mProgressBar.setProgress(bean.getPercent());
				RelativeLayout rl = (RelativeLayout) mViewHolder.mProgressBar.getParent();
				TextView rateTextView = (TextView) rl.getChildAt(1);
				rateTextView.setText(bean.getPercent() + "%");
			}
			if (downloadstatus == 2) {
				mViewHolder.mComplete.setVisibility(View.VISIBLE);
				((RelativeLayout) mViewHolder.mProgressBar.getParent()).setVisibility(View.GONE);
			}
			if (downloadstatus == -2) {
				mViewHolder.mComplete.setVisibility(View.VISIBLE);
				mViewHolder.mComplete.setText("下载失败");
				mViewHolder.btn1.setEnabled(false);
				((RelativeLayout) mViewHolder.mProgressBar.getParent()).setVisibility(View.GONE);
			}
			mViewHolder.btn1.setTag(bean);
			mViewHolder.btn2.setTag(bean);
		}
		convertView.setId(position);
		return convertView;
	}

	/** 加载安装的数据
	 * 
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return */
	private View LoadingInstalledData(int position, View convertView, ViewGroup parent) {
		ViewHolder mViewHolder;
		if (convertView == null) {
			mViewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.sixthdatalayout2, null);
			mViewHolder.mImageView = (ImageView) convertView.findViewById(R.id.elementimageView);
			mViewHolder.mAppName = (TextView) convertView.findViewById(R.id.appname);
			mViewHolder.mAppVersion = (TextView) convertView.findViewById(R.id.appversion);
			mViewHolder.mAppSize = (TextView) convertView.findViewById(R.id.appsize);
			mViewHolder.btn1 = (Button) convertView.findViewById(R.id.runbutton);
			mViewHolder.btn2 = (Button) convertView.findViewById(R.id.uninstallbutton);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		if (parent.getChildAt(position) == null) {// 防止重复加载
			AppBean bean = list.get(position);
			mViewHolder.mAppName.setText(bean.getTitle());
			mViewHolder.mAppVersion.setText("版本：" + bean.getVersion());
			mViewHolder.mAppSize.setText("大小：" + bean.getSize());
			if (bean.getId() == 0) {
				PackageManager pm = mContext.getPackageManager();
				try {
					Drawable ico = pm.getApplicationIcon(bean.getPackageName());
					mViewHolder.mImageView.setImageDrawable(ico);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				ImageLoader.getInstanse(mContext).setImage(bean.getImage(), mViewHolder.mImageView);
			}
			mViewHolder.btn1.setTag(bean);
			mViewHolder.btn2.setTag(bean);
		}
		return convertView;
	}

	/** 加载更新的数据
	 * 
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return */
	private View LoadingUpgradeData(int position, View convertView, ViewGroup parent) {
		ViewHolder mViewHolder;
		if (convertView == null) {
			mViewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.sixthdatalayout4, null);
			mViewHolder.mImageView = (ImageView) convertView.findViewById(R.id.elementimageView);
			mViewHolder.mAppName = (TextView) convertView.findViewById(R.id.appname);
			mViewHolder.mAppVersion = (TextView) convertView.findViewById(R.id.appversion);
			mViewHolder.mAppSize = (TextView) convertView.findViewById(R.id.appsize);
			mViewHolder.btn1 = (Button) convertView.findViewById(R.id.upgradebutton);
			mViewHolder.btn3 = (ToggleButton) convertView.findViewById(R.id.ignorebutton);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		if (parent.getChildAt(position) == null) {// 防止重复加载
			AppBean bean = list.get(position);
			if (Tools.isUpdate(bean.getIgnored())) {
				mViewHolder.btn3.setChecked(true);
			} else {
				mViewHolder.btn3.setChecked(false);
			}
			mViewHolder.mAppName.setText(bean.getTitle());
			mViewHolder.mAppVersion.setText("版本：" + bean.getVersion());
			mViewHolder.mAppSize.setText("大小：" + bean.getSize());
			
			ImageLoader.getInstanse(mContext).setImage(bean.getImage(), mViewHolder.mImageView);
			
			mViewHolder.btn1.setTag(bean);
			mViewHolder.btn3.setTag(bean);
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView mImageView;
		TextView mAppName;
		TextView mAppVersion;
		TextView mAppSize;
		TextView mComplete;
		ProgressBar mProgressBar;
		Button btn1;
		Button btn2;
		ToggleButton btn3;
	}
	
	class DownloadProgressHandler extends Handler {
		private ProgressBar pb;
		private Button bt;
		private TextView tv;
		public DownloadProgressHandler(ProgressBar pb,Button bt,TextView tv){
			this.pb = pb;
			this.bt = bt;
			this.tv = tv;
		}
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0x444){
				Bundle b = msg.getData();
				String rate = (String) b.get("rate");
				int rateNum = Integer.parseInt(rate);
				RelativeLayout rl = (RelativeLayout) pb.getParent();
				TextView rateTextView = (TextView)rl.getChildAt(1);
				if(rateNum==Params.DOWN_LOAD_COMPLETE){
					rl.setVisibility(View.GONE);
					((LinearLayout)rl.getParent()).getChildAt(1).setVisibility(View.VISIBLE);
					if(tv!=null){
						tv.setVisibility(View.VISIBLE);
					}
					if(bt!=null){
						bt.setEnabled(true);
					}
				}
				if(rateNum == Params.DOWN_LOAD_FAILED){
					rl.setVisibility(View.GONE);
					if(tv!=null){
						tv.setText("下载失败");
						tv.setVisibility(View.VISIBLE);
					}
				}
				rateTextView.setText(rateNum+"%");
				pb.setProgress(Integer.parseInt(rate));
			}
		}
	}
	
}
