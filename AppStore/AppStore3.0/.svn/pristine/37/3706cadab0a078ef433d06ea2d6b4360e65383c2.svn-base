package com.hs.adapter;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.hs.activity.R;
import com.hs.domain.AppBean;
import com.hs.listener.PageListener;

/** @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2012-11-7 */
public class PageFragment extends Fragment {

	private PageListener mPageListener;
	private int mPageNum;

	public static Fragment newInstance(int index) {
		PageFragment item = new PageFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("page", index);
		item.setArguments(bundle);
		return item;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mPageNum = getArguments().getInt("page", 0);
		mPageListener = (PageListener) activity;
	}

	private GridView gridView;
	private LoadData mLoadData;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = null;
		v = inflater.inflate(mPageListener.getGridViewLayout(), null);
		gridView = (GridView) v.findViewById(R.id.grid_view);
		mLoadData = new LoadData();
		mLoadData.execute();
		return v;
	}

	class LoadData extends AsyncTask<Integer, Integer, List<AppBean>> {
		@Override
		protected List<AppBean> doInBackground(Integer... params) {
			return mPageListener.getData(mPageNum);
		}
		@Override
		protected void onPostExecute(List<AppBean> result) {
			gridView.setAdapter(mPageListener.getMyPageAdapter(result));
			gridView.setOnItemClickListener(mPageListener.getItemListener());
		}
	}

	@Override
	public void onDestroy() {
		mLoadData.cancel(true);
		super.onDestroy();
	}
}
