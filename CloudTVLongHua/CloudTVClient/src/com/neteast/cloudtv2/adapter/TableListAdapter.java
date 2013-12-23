package com.neteast.cloudtv2.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.neteast.cloudtv2.R;

/** @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-5-9 */
public class TableListAdapter extends BaseAdapter {

	private Context mContext;
	private List<List<String>> mDatas;
	private int[] mTableItemWs;
	private Integer mColor;

	public TableListAdapter(Context mContext, List<List<String>> mDatas, int[] tableItemWs) {
		super();
		this.mContext = mContext;
		this.mDatas = mDatas;
		this.mTableItemWs = tableItemWs;
	}

	public void setData(List<List<String>> mDatas) {
		if (mDatas != null){
			this.mDatas = mDatas;
		}else{
			this.mDatas = new ArrayList<List<String>>();
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mDatas == null)
			return 0;
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		if (mDatas == null)
			return null;
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (mDatas == null)
			return null;
		TableItem tableItem;
		if (convertView == null) {
			tableItem = new TableItem();
			LinearLayout ll = new LinearLayout(mContext);
			if (mColor != null) {
				ll.setBackgroundColor(mColor);
			} else {
				ll.setBackgroundResource(R.drawable.grey);
			}
			tableItem.text = new TextView[mTableItemWs.length];
			for (int i = 0; i < mTableItemWs.length; i++) {
				TextView t = new TextView(mContext);
				t.setTextSize(20);
				t.setBackgroundColor(Color.WHITE);
				t.setTextColor(Color.BLACK);
				t.setGravity(Gravity.CENTER_VERTICAL);
				t.setPadding(10, 5, 0, 5);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mTableItemWs[i],
						LinearLayout.LayoutParams.MATCH_PARENT);
				if (i != mTableItemWs.length - 1)
					params.setMargins(0, 0, 1, 0);
				ll.addView(t, params);
				tableItem.text[i] = t;
			}
			convertView = ll;
			convertView.setTag(tableItem);
		} else {
			tableItem = (TableItem) convertView.getTag();
		}
		
		List<String> data = mDatas.get(position);
		for (int i = 0; i < data.size(); i++) {
			if (tableItem.text.length > i) {
				tableItem.text[i].setText(data.get(i));
			}
		}
		return convertView;
	}

	static class TableItem {
		TextView[] text;
	}

	public void setDividerColor(int color) {
		mColor = color;
	}

}
