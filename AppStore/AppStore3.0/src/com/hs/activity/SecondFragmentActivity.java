package com.hs.activity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hs.adapter.GridViewAdapter;
import com.hs.adapter.MyPageAdapter;
import com.hs.domain.AppBean;
import com.hs.listener.MyOnPageChangeListener;
import com.hs.listener.PageListener;
import com.hs.params.Params;
import com.hs.utils.UIHelper;
import com.lib.appstore.LibAppstore;
import com.lib.net.WeiboException;

/** @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2012-11-1 */
public class SecondFragmentActivity extends FragmentActivity implements PageListener {

	private int totalPage;
	private TextView mCurrentPageView;
	private ViewPager mPager;
	private LibAppstore lib;
	private ConcurrentHashMap<Integer, List<AppBean>> mSecondData;
	private int mPageIndex = 0;
	private MyPageAdapter mPageAdapter;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.second_fragmentlayout);
		initLayout();
		initData();
	}

	private void initData() {
		mPageAdapter = new MyPageAdapter(getSupportFragmentManager(), this);
		mSecondData = new ConcurrentHashMap<Integer, List<AppBean>>();
		mPager.setAdapter(mPageAdapter);
		FlipClickListener fc = new FlipClickListener();
		leftButton.setOnClickListener(fc);
		rightButton.setOnClickListener(fc);
		new requestDataThread().execute();
	}

	private ImageButton leftButton;
	private ImageButton rightButton;

	private void initLayout() {
		mCurrentPageView = (TextView) findViewById(R.id.pageNum);
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener() {
			@Override
			public void onPageSelected(int index) {
				mPageIndex = index;
				changButtenColor(mPageIndex);
			}
		});
		leftButton = (ImageButton) findViewById(R.id.leftpagebutton);
		rightButton = (ImageButton) findViewById(R.id.rightpagebutton);
		changButtenColor(mPageIndex);
	}

	class FlipClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.leftpagebutton:
				mPageIndex--;
				if (mPageIndex < 0) {
					mPageIndex = 0;
					return;
				}
				break;
			case R.id.rightpagebutton:
				mPageIndex++;
				if (mPageIndex > totalPage - 1) {
					mPageIndex = totalPage - 1;
					return;
				}
				break;

			default:
				break;
			}
			mPager.setCurrentItem(mPageIndex);
			changButtenColor(mPageIndex);
		}
	}

	private void changButtenColor(int pageIndex) {
		mCurrentPageView.setText(pageIndex + 1 + "/" + totalPage);
		if (pageIndex <= 0) {
			leftButton.setBackgroundResource(R.drawable.leftbutton2);
		} else {
			leftButton.setBackgroundResource(R.drawable.leftbutton1);
		}
		if (pageIndex >= totalPage - 1) {
			rightButton.setBackgroundResource(R.drawable.rightbutton2);
		} else {
			rightButton.setBackgroundResource(R.drawable.rightbutton1);
		}
	}

	class requestDataThread extends AsyncTask<Integer, Integer, List<AppBean>> {
		@Override
		protected List<AppBean> doInBackground(Integer... params) {
			List<AppBean> list = null;
			try {
				lib = LibAppstore.getInstance(SecondFragmentActivity.this);
				list = lib.Get_latest_list(1, Params.NUM_PER_PAGE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list;
		}

		@Override
		protected void onPostExecute(List<AppBean> result) {
			try {
				if (result != null) {
					totalPage = UIHelper.getTotalPage(result.get(0).getTotal());
					mPageAdapter.notifyDataSetChanged();
					changButtenColor(0);
				} else {
					Toast.makeText(SecondFragmentActivity.this, "数据下载失败！请查看网络", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				Toast.makeText(SecondFragmentActivity.this, "数据下载失败！请查看网络", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}

	@Override
	public int getPages() {
		return totalPage;
	}

	@Override
	public BaseAdapter getMyPageAdapter(List<AppBean> list) {
		return new GridViewAdapter(this, list);
	}

	@Override
	public OnItemClickListener getItemListener() {
		return new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int itemIndex, long arg3) {
				Intent intent = new Intent();
				intent.setClass(SecondFragmentActivity.this, AppDetailActivity.class);
				intent.putExtra("id", String.valueOf(v.getId()));
				startActivity(intent);
			}
		};
	}

	@Override
	public List<AppBean> getData(int pageIndex) {
		List<AppBean> list = null;
		list = mSecondData.get(pageIndex);
		if (list != null) {
			return list;
		}
		try {
			lib = LibAppstore.getInstance(SecondFragmentActivity.this);
			list = lib.Get_latest_list(pageIndex + 1, Params.NUM_PER_PAGE);
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		mSecondData.put(pageIndex, list);
		return list;
	}

	@Override
	public int getGridViewLayout() {
		return R.layout.gridview;
	}
}
