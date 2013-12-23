package com.hs.activity;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hs.adapter.MyPageAdapter;
import com.hs.adapter.SixthGridViewAdapter;
import com.hs.db.AppDao;
import com.hs.domain.AppBean;
import com.hs.listener.MyOnPageChangeListener;
import com.hs.listener.PageListener;
import com.hs.params.Params;
import com.hs.utils.AppData;
import com.hs.utils.ToastHandler;
import com.hs.utils.Tools;
import com.hs.utils.UIHelper;

public class SixthFragmentActivity extends FragmentActivity implements PageListener {

	private ViewPager mViewPager;
	private LinearLayout tablayout;
	private MyPageAdapter mPageAdapter;
	private Resources res;
	private AppDao dao;
	private DownLoadApplication mApplication;
	private LinearLayout mUpdateAllBtn;
	private int currentPage;
	private int totalPage;
	private ImageButton leftButton, rightButton;
	private TextView pageNum;
	int[] upgradeIds = null;
	private static int currentCate;
	public final static int CATE_DOWNLOAD = 0, CATE_INSTALLED = 1, CATE_UPGRADEABLE = 2;

	private AppData mAppData;

	public static void setCategory(int currentCate) {
		SixthFragmentActivity.currentCate = currentCate;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sixth_fragmentlayout);
		initLayout();
		initData();
	}

	@Override
	protected void onResume() {
		LoadData();
	}

	private void initLayout() {
		pageNum = (TextView) findViewById(R.id.pageNum);
		mViewPager = (ViewPager) findViewById(R.id.view_pager_id);
		tablayout = (LinearLayout) findViewById(R.id.tablayout);
		leftButton = (ImageButton) findViewById(R.id.leftpagebutton);
		rightButton = (ImageButton) findViewById(R.id.rightpagebutton);
		mUpdateAllBtn = (LinearLayout) findViewById(R.id.allbutton);
		FliperBtnListener filperListener = new FliperBtnListener();
		leftButton.setOnClickListener(filperListener);
		rightButton.setOnClickListener(filperListener);
	}

	private class FliperBtnListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.leftpagebutton:
				if (currentPage <= 0) {
					currentPage = 0;
				} else {
					currentPage--;
				}
				break;
			case R.id.rightpagebutton:
				if (currentPage > totalPage) {
					if (totalPage <= 1) {
						totalPage = 1;
						currentPage = 0;
					} else {
						currentPage = totalPage - 1;
					}
				} else {
					currentPage++;
				}
				break;
			}
			flipPageChange(currentPage, totalPage);
		}
	}

	private void flipPageChange(int currentPage, int totalPage) {
		if (mViewPager == null)
			return;
		mViewPager.setCurrentItem(currentPage);
		if (totalPage == -1) {
			pageNum.setText("加载中...");
		} else if (totalPage == 0) {
			pageNum.setText("0/0");
		} else {
			pageNum.setText((currentPage + 1) + "/" + totalPage);
		}

		if (currentPage <= 0) {
			leftButton.setEnabled(false);
		} else {
			leftButton.setEnabled(true);
		}
		if (currentPage >= totalPage - 1) {
			rightButton.setEnabled(false);
		} else {
			rightButton.setEnabled(true);
		}
	}

	/** 初始化常用数据。 */
	private void initData() {
		mAppData = AppData.getInstance(this);
		mPageAdapter = new MyPageAdapter(getSupportFragmentManager(), this);
		mViewPager.setAdapter(mPageAdapter);
		res = getResources();
		dao = AppDao.getInstance(this);
		mApplication = (DownLoadApplication) getApplication();
		setSelect(((LinearLayout) tablayout.getChildAt(currentCate)).getChildAt(0));
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener() {
			@Override
			public void onPageSelected(int pageIndex) {
				currentPage = pageIndex;
				flipPageChange(pageIndex, totalPage);
			}
		});
	}

	/** 加载数据。 */
	private void LoadData() {
		super.onResume();
		flipPageChange(currentPage, totalPage);
		if (totalPage <= currentPage) {
			rightButton.setBackgroundResource(R.drawable.rightbutton2);
			rightButton.setEnabled(false);
		} else {
			rightButton.setBackgroundResource(R.drawable.rightbutton1);
			rightButton.setEnabled(true);
		}
	}

	/** 设置某个自定义标签选中
	 * 
	 * @param view */
	public void setSelect(View view) {
		int cate = Integer.parseInt((String) view.getTag());
		currentCate = cate;
		currentPage = 0;
		mViewPager.removeAllViews();
		if (currentCate == 0) {// 我的下载
			mUpdateAllBtn.setVisibility(View.GONE);
			totalPage = getTotalPage(mAppData.getDownloadCount());
		} else if (currentCate == 1) {// 已安装
			mUpdateAllBtn.setVisibility(View.GONE);
			totalPage = getTotalPage(mAppData.getInstalledCount());
		} else { // 可更新
			mUpdateAllBtn.setVisibility(View.VISIBLE);
			totalPage = getTotalPage(mAppData.getUpgradeCount());
		}
		UIHelper.setCustomTabColor((TextView) view, tablayout, res);
		flipPageChange(currentPage, totalPage);
		refreshTabLayoutData();
		mPageAdapter.notifyDataSetChanged();
	}

	private void refreshTabLayoutData() {
		((TextView) ((LinearLayout) tablayout.getChildAt(0)).getChildAt(0)).setText("我的下载(" + mAppData.getDownloadCount() + ")");
		((TextView) ((LinearLayout) tablayout.getChildAt(1)).getChildAt(0)).setText("已安装(" + mAppData.getInstalledCount() + ")");
		((TextView) ((LinearLayout) tablayout.getChildAt(2)).getChildAt(0)).setText("可更新(" + mAppData.getUpgradeCount() + ")");
	}

	private int getTotalPage(int count) {
		int totalPage = 0;
		int a = count / 10;
		if (count % 10 != 0) {
			totalPage = a + 1;
		} else {
			totalPage = a;
		}
		if (totalPage == 0) {
			totalPage = 1;
		}
		return totalPage;
	}

	public void installClick(View view) {
		final AppBean bean = (AppBean) view.getTag();
		File file = new File(Params.DOWNLOAD_FILE_PATH + bean.getApkName());
		if (!file.exists()) {
			ToastHandler.toastDisplay("安装失败！文件不存在!", this);
			dao.deleteApp(bean.getId());
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.putExtra("IsCreateShortCut", true); // 设置产生快捷方式
		intent.putExtra("ExpandedInstallFlg", true); // 设置禁止自启动标准
		intent.setDataAndType(Uri.parse("file://" + file.toString()), "application/vnd.android.package-archive");
		startActivityForResult(intent, 0);
		OptionButtonHandler ibh = new OptionButtonHandler();
		mAppData.setAppActionHandler(ibh);
	}

	public void deleteClick(View view) {
		final AppBean bean = (AppBean) view.getTag();
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = 200;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(params);
		dialog.setTitle("删除");
		dialog.setMessage("你确定要删除" + bean.getTitle() + "?");
		dialog.setButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mAppData.deleteDownloadingApp(bean);
				dialog.dismiss();
				refreshTabLayoutData();
				mPageAdapter.notifyDataSetChanged();
			}
		});
		dialog.setButton2("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/** 卸载按钮事件处理
	 * 
	 * @param view */
	public void uninstallClick(View view) {
		AppBean bean = (AppBean) view.getTag();
		String packName = bean.getPackageName();
		Intent intent = new Intent(Intent.ACTION_DELETE);
		Uri packageUri = Uri.parse("package:" + packName);
		intent.setData(packageUri);
		startActivityForResult(intent, 0);
		OptionButtonHandler uibn = new OptionButtonHandler();
		mAppData.setAppActionHandler(uibn);
	}

	/** 运行按钮事件处理
	 * 
	 * @param view */
	public void runClick(View view) {
		AppBean bean = (AppBean) view.getTag();
		String packageName = bean.getPackageName();
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageName);
		List<ResolveInfo> apps = getPackageManager().queryIntentActivities(resolveIntent, PackageManager.GET_ACTIVITIES);
		if (apps.size() == 0) {
			Toast.makeText(this, "无法启动该应用", Toast.LENGTH_SHORT).show();
			return;
		}
		ResolveInfo ri = apps.get(0);

		if (ri != null) {
			String className = ri.activityInfo.name;
			ComponentName cn = new ComponentName(packageName, className);
			Intent intent = new Intent();
			intent.setComponent(cn);
			startActivityForResult(intent, 1);
		}
	}

	/** 处理单个应用升级
	 * 
	 * @param view */
	@SuppressWarnings("unchecked")
	public void upgradeClick(View view) {
		AppBean bean = (AppBean) view.getTag();
		if (!Tools.hasFreeSpace(bean.getSize())) {
			Toast.makeText(SixthFragmentActivity.this, "SD卡存储空间不足", Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			mAppData.UpdateApp(bean);
			mPageAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
		view.setEnabled(false);
	}

	/** 处理单个应用忽略
	 * 
	 * @param view */
	@SuppressWarnings("unchecked")
	public void ignoredClick(View view) {
		ToggleButton toggle = (ToggleButton) view;
		AppBean bean = (AppBean) view.getTag();
		if (toggle.isChecked()) {// 如果是true 当前处于忽略状态，否者 false
			mAppData.updateIgnoreStatus(bean.getId(), false);
		} else {
			mAppData.updateIgnoreStatus(bean.getId(), true);
		}
		refreshTabLayoutData();
	}

	/** 升级所有升级
	 * 
	 * @param view */
	public void upgradeAllClick(View view) {
		List<AppBean> list = mAppData.getUpgradeList();
		if (list.size() == 0)
			return;
		float updateTotalSize = 0.0f;
		for(int i=0; i<list.size();i++){
			updateTotalSize += Tools.unitTranslate(list.get(i).getSize());
		}
		long freeSpace = Tools.getSDCardFreeSpace();
		String titleStr = "更新所有应用";
		String yes = "确定";
		String no = "退出";
		if(freeSpace<updateTotalSize){
			titleStr = "剩余空间不足";
			yes = "";
		}
		Builder d = new AlertDialog.Builder(SixthFragmentActivity.this);
		d.setTitle(titleStr);
		d.setMessage("剩余空间："+Tools.unitTranslate(freeSpace)+"  下载需要空间："+Tools.unitTranslate((long)updateTotalSize));
		if(yes.length()>0)
		d.setPositiveButton(yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				for(int i=0; i< mAppData.getUpgradeList().size();i++){
					try {
						mAppData.download( mAppData.getUpgradeList().get(i));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		d.setNegativeButton(no, null);
		d.show(); 
	}

	/** 忽略全部
	 * 
	 * @param view */
	public void ignoredAllClick(View view) {
		if (upgradeIds != null && upgradeIds.length != 0) {
			dao.updateIgnoreStatus(upgradeIds, System.currentTimeMillis());
			setSelect(((LinearLayout) tablayout.getChildAt(2)).getChildAt(0));
			((TextView) ((LinearLayout) tablayout.getChildAt(2)).getChildAt(0)).setText("可更新(" + 0 + ")");
		}
		mPageAdapter.notifyDataSetChanged();
	}

	@Override
	public int getPages() {
		return totalPage;
	}

	@Override
	public List<AppBean> getData(int pageIndex) {
		List<AppBean> list = null;
		switch (currentCate) {
		case CATE_DOWNLOAD:
			list = getPageData(pageIndex, mAppData.getDownloadList());
			break;
		case CATE_INSTALLED:
			list = getPageData(pageIndex, mAppData.getInstalledList());
			break;
		case CATE_UPGRADEABLE:
			list = getPageData(pageIndex, mAppData.getUpgradeList());
			upgradeIds = new int[mAppData.getUpgradeList().size()];
			for (int i = 0; i < mAppData.getUpgradeList().size(); i++) {
				upgradeIds[i] = (Integer) list.get(i).getId();
			}
			break;
		}
		return list;
	}

	private List<AppBean> getPageData(int pageIndex, List<AppBean> list) {
		int start = pageIndex * 10;
		int end = (pageIndex + 1) * 10;
		int maxSize = list.size();
		if (maxSize < end)
			end = maxSize;
		return list.subList(start, end);
	}

	@Override
	public BaseAdapter getMyPageAdapter(List<AppBean> list) {
		return new SixthGridViewAdapter(this, list, currentCate);
	}

	@Override
	public OnItemClickListener getItemListener() {
		return null;
	}

	@Override
	public int getGridViewLayout() {
		return R.layout.gridview2;
	}

	@SuppressLint("HandlerLeak")
	class OptionButtonHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Params.INSTALL_SUCCEED:
				refreshTabLayoutData();
				totalPage = getTotalPage(mAppData.getDownloadCount());
				flipPageChange(currentPage, totalPage);
				mPageAdapter.notifyDataSetChanged();
				break;
			case Params.UNINSTALL_SUCCEED:
				refreshTabLayoutData();
				totalPage = getTotalPage(mAppData.getInstalledCount());
				flipPageChange(currentPage, totalPage);
				mPageAdapter.notifyDataSetChanged();
				break;
			}

		}
	}
}
