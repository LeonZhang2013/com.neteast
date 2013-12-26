package com.hs.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class FourthAndFifthActivity extends FragmentActivity implements PageListener {

	private LibAppstore lib;
	private LinearLayout tablayout;
	private ViewPager myViewPager;
	private GetDataAsync async;
	private PagerAdapter mPagerAdapter;
	private int currentPage;
	private int totalPage;
	private TextView pageNum;
	private ImageButton leftButton, rightButton;
	private String currentCategoryId;
	private ConcurrentHashMap<Integer, List<AppBean>> currentData;
	private Map<String, ConcurrentHashMap<Integer, List<AppBean>>> fourthData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fourthlayout);
		getIntentData();
		initLayout();
		initdata();
	}
	
	private String getIntentData(){
		Intent intent = getIntent();
		return intent.getStringExtra("type");
	}

	/** 初始化界面布局。 */
	private void initLayout() {
		lib = LibAppstore.getInstance(this);
		tablayout = (LinearLayout) this.findViewById(R.id.tablayout);
		pageNum = (TextView) findViewById(R.id.pageNum);
		myViewPager = (ViewPager) findViewById(R.id.viewpager);

		FlipOnClickListener fClick = new FlipOnClickListener();
		leftButton = (ImageButton) findViewById(R.id.leftpagebutton);
		rightButton = (ImageButton) findViewById(R.id.rightpagebutton);
		leftButton.setOnClickListener(fClick);
		rightButton.setOnClickListener(fClick);
		myViewPager.setOnPageChangeListener(new MyOnPageChangeListener() {
			@Override
			public void onPageSelected(int index) {
				currentPage = index;
				pageChangeOption(index);
			}
		});
	}

	/** 初始化数据 */
	private void initdata() {
		fourthData = new ConcurrentHashMap<String, ConcurrentHashMap<Integer, List<AppBean>>>();
		mPagerAdapter = new MyPageAdapter(getSupportFragmentManager(), this);
		myViewPager.setAdapter(mPagerAdapter);
		GetAppCategroy category = new GetAppCategroy();
		category.execute();
	}

	/** 翻页按钮点击监听
	 * 
	 * @author LeonZhang
	 * @Email zhanglinlang1@163.com */
	private class FlipOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.leftpagebutton:
				currentPage--;
				if(currentPage<0){
					currentPage = 0;
					return;
				}
				break;
			case R.id.rightpagebutton:
				currentPage++;
				if(currentPage>=totalPage){
					currentPage = totalPage-1;
					return;
				}
				break;
			}
			myViewPager.setCurrentItem(currentPage);
			pageChangeOption(currentPage);
		}
	}

	//数据准备标志位，数据准备完成设为 true; 否者 false；
	private boolean complete;
	/** 设置某个自定义标签选中 */
	public void setSelect(View view) {
		complete = false;
		UIHelper.setCustomTabColor((TextView) view, tablayout, getResources());
		String id = (String) view.getTag();
		if (id == null || id.equals(""))
			return;
		currentCategoryId = id;
		currentData = fourthData.get(currentCategoryId);
		if (currentData != null) {
			initPageToZero();
			return;
		}
		if (async != null && async.getStatus() == AsyncTask.Status.RUNNING) {
			async.cancel(true); // 如果Task还在运行，则先取消它
		}
		async = new GetDataAsync();
		async.execute();
	}
	
	public void initPageToZero(){
		currentPage = 0;
		totalPage = UIHelper.getTotalPage(currentData.get(0).get(0).getTotal());
		pageChangeOption(0);
		myViewPager.setCurrentItem(0);
		complete = true;
		mPagerAdapter.notifyDataSetChanged();
	}

	/** 获取分类数据 */
	class GetAppCategroy extends AsyncTask<Integer, Integer, List<Map<String, Object>>> {
		@Override
		protected List<Map<String, Object>> doInBackground(Integer... params) {
			List<Map<String, Object>> list = null;
			try {
				list = lib.Get_category_title(getIntentData());
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			return list;
		}

		@Override
		protected void onPostExecute(List<Map<String, Object>> result) {
			if(result != null){
				inflateCategroyLayout(result);
			}else{
				Toast.makeText(FourthAndFifthActivity.this, "数据下载失败！请查看网络", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/** 填充分类布局数据
	 * 
	 * @param result */
	private void inflateCategroyLayout(List<Map<String, Object>> result) {

		for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
			final LinearLayout linear = (LinearLayout) LayoutInflater.from(FourthAndFifthActivity.this).inflate(R.layout.customtablayout,
					null);
			LayoutParams p = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
			linear.setLayoutParams(p);
			final TextView tab = (TextView) linear.getChildAt(0);
			Map<String, Object> map = iterator.next();
			tab.setText((String) map.get("ctitle"));
			tab.setTag(map.get("cid"));
			tab.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setSelect(v);
				}
			});
			tablayout.addView(linear);
		}
		if(tablayout.getChildAt(0)!=null){
			setSelect(((LinearLayout) tablayout.getChildAt(0)).getChildAt(0));
		}
	}

	/** 获取列表数据
	 * 
	 * @author Leon */
	class GetDataAsync extends AsyncTask<Integer, Integer, List<AppBean>> {
		private String gTypeId;
		
		@Override
		protected void onPreExecute() {
			gTypeId = currentCategoryId;
			super.onPreExecute();
		}

		@Override
		protected List<AppBean> doInBackground(Integer... params) {
			List<AppBean> list = new ArrayList<AppBean>();
			try {
				list = lib.Get_category_list(Params.APP_TYPE, 1, Params.NUM_PER_PAGE, gTypeId);
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			return list;
		}

		@Override
		protected void onPostExecute(List<AppBean> result) {
			currentData = new ConcurrentHashMap<Integer, List<AppBean>>();
			currentData.put(0, result);
			fourthData.put(gTypeId, currentData);
			initPageToZero();
		}
	}

	/** 改变翻页按钮的状态
	 * 
	 * @param pageIndex */
	private void pageChangeOption(int pageIndex) {
		pageNum.setText(pageIndex + 1 + "/" + totalPage);
		if (pageIndex > 1 && pageIndex < totalPage - 2) {
			return;
		}
		if (pageIndex >= totalPage - 1) {
			rightButton.setBackgroundResource(R.drawable.rightbutton2);
			rightButton.setClickable(false);
		} else {
			rightButton.setBackgroundResource(R.drawable.rightbutton1);
			rightButton.setClickable(true);
		}
		if (pageIndex <= 0) {
			leftButton.setBackgroundResource(R.drawable.leftbutton2);
			leftButton.setClickable(false);
		} else {
			leftButton.setBackgroundResource(R.drawable.leftbutton1);
			leftButton.setClickable(true);
		}
	}

	/** 需要显示几页数据 */
	@Override
	public int getPages() {
		return totalPage;
	}

	/** 获取index索引页数据。 */
	@Override
	public List<AppBean> getData(int pageIndex) {
		String typeId = currentCategoryId;//为了达到异步的效果，在进入该方法钱先将数据
		if(!complete) return null;
		List<AppBean> list;
		list = fourthData.get(typeId).get(pageIndex);//为了达到异步的效果，该处必须使用 fourthData.get(typeId)
		if (list != null) {
			return list;
		}
		try {
			list = lib.Get_category_list(Params.APP_TYPE, pageIndex + 1, Params.NUM_PER_PAGE, typeId);
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		fourthData.get(typeId).put(pageIndex, list);//为了达到异步的效果，该处必须使用 fourthData.get(typeId)
		return list;
	}

	/** 当前页数据的适配器 */
	@Override
	public BaseAdapter getMyPageAdapter(List<AppBean> list) {
		return new GridViewAdapter(this, list);
	}

	/** 当前页数据点击监听 */
	@Override
	public OnItemClickListener getItemListener() {
		return new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
				Intent intent = new Intent();
				intent.setClass(FourthAndFifthActivity.this, AppDetailActivity.class);
				intent.putExtra("id", String.valueOf(v.getId()));
				startActivity(intent);
			}
		};
	}

	@Override
	public int getGridViewLayout() {
		return R.layout.gridview;
	}
}
