package com.neteast.clouddisk.fragment;

import com.neteast.clouddisk.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.TextView;

public class SpaceFragment extends Fragment {
	
	private TextView tvCapacity;
	private ImageView ivSpaceTotal;
	private ImageView ivSpaceUsed;
	private int totalWidth;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_space, null);
		tvCapacity = (TextView) root.findViewById(R.id.capacity);
		ivSpaceTotal = (ImageView) root.findViewById(R.id.space_total);
		ivSpaceUsed = (ImageView) root.findViewById(R.id.space_used);
		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ivSpaceTotal.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
	}
	
	private OnPreDrawListener preDrawListener=new OnPreDrawListener() {
		public boolean onPreDraw() {
			totalWidth = ivSpaceTotal.getMeasuredWidth();
			if (totalWidth>0) {
				setUsedSpace(percent);
				ivSpaceTotal.getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
			}
			return true;
		}
	};
	private int percent;
	/**
	 * 设置已经使用空间占总空间的百分数，如65
	 * @param percent
	 */
	public void setUsedSpace(int percent) {
		this.percent=percent;
		if (ivSpaceUsed!=null) {
			Log.i("test", percent*totalWidth/100+"");
			LayoutParams params = ivSpaceUsed.getLayoutParams();
			params.width=percent*totalWidth/100;
			ivSpaceUsed.setLayoutParams(params);
		}
	}
	/**
	 * 设置容量的文字说明
	 * @param text
	 */
	public void setCapacity(String text) {
		if (!TextUtils.isEmpty(text)) {
			tvCapacity.setText(text);
		}
	}
}
