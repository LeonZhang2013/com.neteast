package com.neteast.clouddisk.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.lib.net.WeiboException;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.activity.FolderDrop.FloderDropListener;
import com.neteast.clouddisk.adapter.MyUploadVideoDataAdapter;
import com.neteast.clouddisk.adapter.TransDataAdapter;
import com.neteast.clouddisk.adapter.ViewPageAdapter;
import com.neteast.clouddisk.customerview.CustomerGridView;
import com.neteast.clouddisk.customerview.DragLayer;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.utils.DataHelpter;
import com.neteast.clouddisk.utils.OnDropCallBack;
import com.neteast.clouddisk.utils.UIHelper;
import com.neteast.data_acquisition.MyLog;

public class MyUploadVideoActivity extends ActivityGroup implements FloderDropListener {

	private CustomerGridView gridView;
	private CustomerGridView transgridView;
	private LinearLayout loadingView = null;

	private int parentCurrentPage = 1;
	private int currentPage = 1;
	private int totalPage = 1;
	private TextView currentPageNum = null;
	private TextView totalPageNum = null;
	private PopupWindow curEncryptpopw = null;
	private PopupWindow curinputpopw = null;
	private PopupWindow curTranscodepopw = null;
	private LibCloud libCloud;
	private Stack<String> order = new Stack<String>();
	private List<Map<String, Object>> videoList = null;
	List<DataInfo> refreshList = null;

	TextView totalText1 = null;
	TextView usedText1 = null;
	TextView nousedText1 = null;

	Button retbtn;
	private Button recordbtn;
	private Button editbutton;
	private Button cancelbutton;
	private Button newfolderbtn;
	private Button recyclerbtn;
	private PopupWindow curpopw = null;
	private DragLayer mDragLayer;
	boolean dragcondition = false;
	/** ViewPager控件，横屏滑动 */
	private ViewPager filePager;

	/** ViewPager的视图列表 */
	private List<View> mListViews;

	/** ViewPager的适配器. */
	private ViewPageAdapter viewPageAdapter;
	private static final int PAGE_SIZE = Params.MYUPLOAD_DATA_PER_PAGE_NUM;
	private FolderDrop mFolderDrop;

	/** ViewPager控件，横屏滑动 */
	private ViewPager transPager;

	/** ViewPager的视图列表 */
	private List<View> transListViews;

	/** ViewPager的适配器. */
	private ViewPageAdapter transPageAdapter;
	private static final int TRANS_PAGE_SIZE = Params.TRANSCODE_PER_PAGE_NUM;

	private String[] transType = new String[] { "640", "1280", "1920", "1024" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myuploadvideo);
		libCloud = LibCloud.getInstance(this);
		mDragLayer = (DragLayer) findViewById(R.id.myuploadvideo_page);
		currentPageNum = (TextView) findViewById(R.id.curentpage);
		totalPageNum = (TextView) findViewById(R.id.totalpage);
		// gridView = (CustomerGridView) findViewById(R.id.mygridview);
		totalText1 = (TextView) findViewById(R.id.totalText1);
		usedText1 = (TextView) findViewById(R.id.usedText1);
		nousedText1 = (TextView) findViewById(R.id.nousedText1);
		filePager = (ViewPager) findViewById(R.id.myuploadVideoViewPager);
		loadingView = (LinearLayout) findViewById(R.id.loading);

		Button uploadbtn = (Button) findViewById(R.id.bt_upload);
		uploadbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				((MyUploadActivity) MyUploadVideoActivity.this.getParent()).startUploadFileActivity(1);

			}
		});
		newfolderbtn = (Button) findViewById(R.id.bt_edit);
		newfolderbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (curpopw != null)
					curpopw.dismiss();
				shownewgallerymenu(null);
				// showeditmenu(null);

				// Toast.makeText(getApplicationContext(),(String)di.get("name"), 1000);

			}
		});
		recordbtn = (Button) findViewById(R.id.bt_record);
		recordbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				((MyUploadActivity) MyUploadVideoActivity.this.getParent()).startUploadRecordActivity(1);

			}
		});
		retbtn = (Button) findViewById(R.id.returnbtn);
		retbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				order.pop();
				currentPage = parentCurrentPage;
				changeForder();

			}
		});

		editbutton = (Button) this.findViewById(R.id.mydownload_edit);
		cancelbutton = (Button) this.findViewById(R.id.mydownload_cancel);
		editbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dragcondition = true;
				cancelbutton.setVisibility(View.VISIBLE);
				editbutton.setVisibility(View.GONE);
			}
		});
		cancelbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dragcondition = false;
				cancelbutton.setVisibility(View.GONE);
				editbutton.setVisibility(View.VISIBLE);
			}
		});
		recyclerbtn = (Button) this.findViewById(R.id.bt_recycler);
		recyclerbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MyUploadActivity) MyUploadVideoActivity.this.getParent()).startRecyclerActivity(1);
			}
		});
		mFolderDrop = (FolderDrop) findViewById(R.id.folder_drop);
		mFolderDrop.setFloderDropListener(this);
		mFolderDrop.setVisibility(View.GONE);

		/*
		 * TextView textView = (TextView) sourceSiteLayout.getChildAt(0); textView.setTextAppearance(this,
		 * R.style.tabtextselectstyle); textView.setBackgroundResource(R.drawable.titlefocus); currentTab = textView;
		 */
		GetCapacityAsync capacity = new GetCapacityAsync();
		capacity.execute();

		order.push("");
		GetDataTask gdt = new GetDataTask();
		gdt.execute(currentPage, order.peek());

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (order.size() > 1) {
				order.pop();
				currentPage = parentCurrentPage;
				changeForder();
				return true;
			} else {
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setGrid(Context context, List<DataInfo> list) {
		int PageCount = (int) Math.ceil(((double) list.size()) / PAGE_SIZE);
		System.out.println("setGrid  PageCount = " + PageCount);
		for (int i = 0; i < PageCount; i++) {
			gridView = (CustomerGridView) LayoutInflater.from(context).inflate(R.layout.myupload_grid, null);
			gridView.setAdapter(new MyUploadVideoDataAdapter(context, list, i, mLongClickListener, mClickListener));
			gridView.setDataList(list);
			gridView.setDragger(mDragLayer);
			gridView.setOnDropListener(new OnDropCallBack() {
				public void onDropCompleted(DataInfo sourceInfo, DataInfo destInfo) {
					// System.out.println("sourceInfo : name = " + sourceInfo.getName() + "destInfo name=" + destInfo.getName());
					mFolderDrop.setVisibility(View.GONE);

					if (destInfo == null || sourceInfo == null) {
						return;
					}
					if (destInfo == sourceInfo)
						return;

					Map<String, Object> retmap = null;
					try {
						if (sourceInfo.GetIsDir() != null && sourceInfo.GetIsDir().equals("1")) {
							return;
						}
						if ((destInfo.GetIsDir() != null) && (destInfo.GetIsDir().equals("1"))) {
							retmap = libCloud.Move_file(sourceInfo.getId(), destInfo.getId(), sourceInfo.getName());
							if (retmap != null && retmap.get("code").equals("1")) {
								MyUploadVideoDataAdapter adapter1 = (MyUploadVideoDataAdapter) ((CustomerGridView) mListViews
										.get(currentPage - 1)).getAdapter();
								adapter1.getList().remove(sourceInfo.getPosition());
								adapter1.notifyDataSetChanged();
								if (videoList != null) {
									videoList.clear();
								}
								videoList = null;
								GetDataTask gdt = new GetDataTask();
								gdt.execute(currentPage, "");
							}
						}

					} catch (WeiboException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			// 去掉点击时的黄色背景
			// gridView.setSelector(R.color.transparent);
			mListViews.add(gridView);
		}
	}
/**
 * 12-03 18:34:14.468: I/System.out(25010): DataInfo [id=2897, name=IMG_201.., url=http://218.108.169.47:9203/00000000000000000000000000000000000000000000000081252437F73E396FAF3F6116C2AE1D567655034B0/IMG_2012-09-08 15.33.25.698.jpg, remark=null, image=null, resid=1, childtype=null, type=3, desc=null, packages=null, status=2, version=null, thumb=http://218.108.171.40/upload//2897_s.jpg, position=0, fileid=null, filesize=0, server=null, progress=0, singer=null, addtime=null, sourcetime=null, slicedone=0, slicetotal=0, isdir=0, security=0, pkgname=null, tag=null, tagName=null, series=null, movieid=null, passwd=, images=[], imgdownload=[], infoHash=null, ischecked=0, videoid=]

 */
	private View.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			boolean condition = false;
			// TODO:判断条件，是否是拖拽事件
			System.out.println("MyDownloadMusicDataAdapter on Long Clicked");
			if (dragcondition) {
				if (order.size() > 1) {
					mFolderDrop.setVisibility(View.VISIBLE);
				}
				gridView.startDrag(v);
			} else {
				DataInfo info = (DataInfo) v.getTag();
				showpopmenu(info, null, info.getSecurity());
			}
			return true;
		}
	};

	private View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			DataInfo info = (DataInfo) arg0.getTag();
			if (info.getStatus() == 1) {
				judgeOperate(info);
				//Toast.makeText(MyUploadVideoActivity.this, "还不能转码", Toast.LENGTH_SHORT).show();
			} else {
				// System.out.println("RecommendVideoActivity on item clicked" + "name = " + info.getName() + "url = " +
				// info.getUrl() );
				if (info.getSecurity() != null && info.getSecurity().equals("1")) {
					showInputPsw(info, getResources().getString(R.string.pswd_input_notices));
				} else {
					judgeOperate(info);
				}
			}
		}

	};

	public void addDataToGridView(final List<DataInfo> result) {
		refreshList = result;
		mListViews = new ArrayList<View>();
		setGrid(this, result);
		viewPageAdapter = new ViewPageAdapter(this, mListViews, 1);
		filePager.setAdapter(viewPageAdapter);

		filePager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int page) {
				totalPage = DataHelpter.computeTotalPage(result.size(), PAGE_SIZE);
				currentPage = page + 1;
				currentPageNum.setText(currentPage + "");
				totalPageNum.setText(totalPage + "");
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			public void onPageScrollStateChanged(int arg0) {
			}
		});
		filePager.setCurrentItem(currentPage - 1);
	}

	private void setTransGrid(Context context, List<DataInfo> list) {
		int PageCount = (int) Math.ceil(((double) list.size()) / TRANS_PAGE_SIZE);
		System.out.println("setTransGrid  PageCount = " + PageCount);
		for (int i = 0; i < PageCount; i++) {
			transgridView = (CustomerGridView) LayoutInflater.from(context).inflate(R.layout.transcode_grid, null);
			transgridView.setAdapter(new TransDataAdapter(context, list, i));

			// 去掉点击时的黄色背景
			transgridView.setSelector(R.color.transparent);
			transListViews.add(transgridView);
		}
	}

	public void addDataToTransGridView(List<DataInfo> list) {
		transListViews = new ArrayList<View>();
		setTransGrid(this, list);
		transPageAdapter = new ViewPageAdapter(this, transListViews, 2);
		transPager.setAdapter(transPageAdapter);
		transPager.setCurrentItem(0);
		transPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int page) {

			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	class GetDataTask extends AsyncTask<Object, Integer, List<DataInfo>> {

		@Override
		protected void onPreExecute() {
			// 第一个执行方法
			loadingView.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected List<DataInfo> doInBackground(Object... params) {
			Map<String, Object> retmap = null;
			try {
				if (videoList == null) {
					retmap = libCloud.Get_file_list(Params.RECOMMEND_VIDEO, "", "" + params[1]);
					if (retmap == null)
						return null;
					if (!retmap.get("code").equals("1")) {

						return null;
					}
					videoList = (List<Map<String, Object>>) retmap.get("filelist");
					if (videoList == null) {
						return null;
					}
				}

			} catch (WeiboException e) {
				e.printStackTrace();
			}
			Map<String, Object> m = DataHelpter.fillUploadData(videoList, params);
			if (m == null) {
				return new ArrayList<DataInfo>();
			}

			totalPage = (Integer) m.get("totalpage");
			return (List<DataInfo>) m.get("result");
		}

		@Override
		protected void onPostExecute(List<DataInfo> result) {

			if (result != null) {
				addDataToGridView(result);
			} else {
				addDataToGridView(new ArrayList<DataInfo>());
				videoList = new ArrayList<Map<String, Object>>();
				currentPage = 1;
				totalPage = 1;
			}
			loadingView.setVisibility(View.GONE);
			currentPageNum.setText(currentPage + "");
			totalPageNum.setText(totalPage + "");
			retbtn.setClickable(true);
		}

	}

	class GetCapacityAsync extends AsyncTask<Object, Integer, Map<String, Object>> {
		@Override
		protected Map<String, Object> doInBackground(Object... params) {
			Map<String, Object> retmap = null;
			try {
				retmap = libCloud.Get_capacity();
			} catch (WeiboException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return retmap;
		}

		@Override
		protected void onPostExecute(Map<String, Object> result) {
			String totalSpace = null;
			String usedSpace = null;
			if (result == null)
				return;
			if (result.get("code").equals("1")) {
				totalSpace = (String) result.get("totalspace");
				usedSpace = (String) result.get("usedspace");
				if (totalSpace == null) {
					totalSpace = "0";
				}
				if (usedSpace == null) {
					usedSpace = "0";
				}
				System.out.println("totalSpace = " + totalSpace + "usedSpace" + usedSpace);
				String usedGB = "";
				long size = Long.parseLong(usedSpace);
				Long total = Long.parseLong(totalSpace);
				System.out.println("size = " + size);
				usedGB = DataHelpter.getSpaceStr(size);
				String leave = DataHelpter.getSpaceStr(total * 1024 * 1024 * 1024 - size);

				totalText1.setText(totalSpace + "GB");
				usedText1.setText(usedGB);
				nousedText1.setText(leave);
				/*
				 * progressText.setText(usedGB+"/"+totalSpace+"G"); Long used = Long.parseLong(usedSpace); Long total =
				 * Long.parseLong(totalSpace); int value = (int) ((used*100)/(total*1024*1024*1024)); progress.setProgress(value);
				 */
			} else if (result.get("code").equals("202")) {
				UIHelper.displayToast("userid or token is invalid", MyUploadVideoActivity.this);
			}
		}

	}

	public void changeForder() {
		currentPageNum.setText("1");
		totalPageNum.setText("1");
		videoList = null;

		GetDataTask gdt = new GetDataTask();
		String str = order.peek();
		gdt.execute(currentPage, str);
		retbtn.setClickable(false);
		if (retbtn.getVisibility() == View.INVISIBLE) {
			retbtn.setVisibility(View.VISIBLE);
		}
		if (str != null && str.equals("")) {
			retbtn.setVisibility(View.INVISIBLE);
			recordbtn.setVisibility(View.VISIBLE);
			newfolderbtn.setClickable(true);
		} else {
			recordbtn.setVisibility(View.GONE);
			newfolderbtn.setClickable(false);
		}
	}

	/*
	 * 判断点击后操作
	 */
	private void judgeOperate(DataInfo di) {
		String isDir = di.GetIsDir();
		if (isDir != null && isDir.equals("1")) {
			parentCurrentPage = currentPage;
			currentPage = 1;
			order.push(di.getId());
			changeForder();
		} else {
			if (di.getUrl() == null || di.getUrl().length() == 0) {
				UIHelper.displayToast(getResources().getString(R.string.invalid_url), MyUploadVideoActivity.this);
			} else {
				// MediaPlayerHelper.play(MyUploadVideoActivity.this, di.getUrl(),"0","0","0");
				showTrancodeWin(di);
			}
		}
	}

	/* 显示找回密码输入框 */
	private void showForgetPsw(final DataInfo di) {
		if (curinputpopw != null) {
			curinputpopw.dismiss();
		}
		View pWin = getLayoutInflater().inflate(R.layout.forgetpswdmenu, null);
		final PopupWindow popmenu = new PopupWindow(pWin, 347, 319);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());
		curinputpopw = popmenu;

		Button okbtn = (Button) pWin.findViewById(R.id.bt_ok);
		Button cancelbtn = (Button) pWin.findViewById(R.id.bt_cancel);

		final EditText usernameet = (EditText) pWin.findViewById(R.id.edit_username);
		final EditText login_pswdet = (EditText) pWin.findViewById(R.id.edit_login_pswd);
		final EditText new_pswdet = (EditText) pWin.findViewById(R.id.edit_newpswd);
		final EditText new_pswd_againet = (EditText) pWin.findViewById(R.id.edit_newpswd_again);
		final TextView tipstv = (TextView) pWin.findViewById(R.id.input_tips);
		tipstv.setVisibility(View.INVISIBLE);

		okbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String username = usernameet.getText().toString().trim();
				String loginpswd = login_pswdet.getText().toString().trim();
				String newpswd = new_pswdet.getText().toString().trim();
				String newpswdagain = new_pswd_againet.getText().toString().trim();
				if (username.equals("")) {
					tipstv.setText(R.string.username_cannotempty);
					tipstv.setVisibility(View.VISIBLE);
					// UIHelper.showToast(MyUploadVideoActivity.this, getResources().getString(R.string.username_cannotempty));
				} else if (loginpswd.equals("")) {
					tipstv.setText(R.string.loginpswd_cannotempty);
					tipstv.setVisibility(View.VISIBLE);
					// UIHelper.showToast(MyUploadVideoActivity.this, getResources().getString(R.string.loginpswd_cannotempty));
				} else if (newpswd.equals("")) {
					tipstv.setText(R.string.newpswd_cannotempty);
					tipstv.setVisibility(View.VISIBLE);
					// UIHelper.showToast(MyUploadVideoActivity.this, getResources().getString(R.string.newpswd_cannotempty));
				} else if (newpswdagain.equals("")) {
					tipstv.setText(R.string.newpswdagain_cannotempty);
					tipstv.setVisibility(View.VISIBLE);
					// UIHelper.showToast(MyUploadVideoActivity.this,
					// getResources().getString(R.string.newpswdagain_cannotempty));
				} else if (!newpswd.equals(newpswdagain)) {
					tipstv.setText(R.string.pswd_notsame);
					tipstv.setVisibility(View.VISIBLE);
				} else {
					popmenu.dismiss();
					resetPasswdTask resetpswd = new resetPasswdTask();
					resetpswd.execute(username, loginpswd, newpswd, di);
				}
			}
		});
		cancelbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popmenu.dismiss();

			}
		});
		popmenu.showAtLocation(findViewById(R.id.myuploadvideo_page), Gravity.CENTER, 0, 0);
	}

	/*
	 * 显示密码输入框
	 */
	private void showInputPsw(final DataInfo di, String msg) {
		if (curinputpopw != null) {
			curinputpopw.dismiss();
		}
		View pWin = getLayoutInflater().inflate(R.layout.myuploadinputpswmenu, null);
		final PopupWindow popmenu = new PopupWindow(pWin, 330, 260);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());
		curinputpopw = popmenu;
		Button okbtn = (Button) pWin.findViewById(R.id.bt_ok);
		Button cancelbtn = (Button) pWin.findViewById(R.id.bt_cancel);
		Button forgetpwdbtn = (Button) pWin.findViewById(R.id.bt_forgetpwd);
		final EditText psw = (EditText) pWin.findViewById(R.id.psw);
		TextView msgs = (TextView) pWin.findViewById(R.id.msg);
		msgs.setText(msg);
		okbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String pswStr = psw.getText().toString();
				if (pswStr.trim().equals("")) {
					UIHelper.displayToast(getResources().getString(R.string.pswd_cannot_empty), MyUploadVideoActivity.this);
				} else {
					// Map<String,Object> result = libCloud.Set_file_password(id,pswStr);
					if (pswStr.trim().equals(di.getPasswd())) {
						judgeOperate(di);
						popmenu.dismiss();
					} else {
						UIHelper.displayToast(getResources().getString(R.string.pswd_error), MyUploadVideoActivity.this);
					}

				}
			}
		});
		cancelbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popmenu.dismiss();

			}
		});
		forgetpwdbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popmenu.dismiss();
				showForgetPsw(di);
			}
		});
		popmenu.showAtLocation(findViewById(R.id.myuploadvideo_page), Gravity.CENTER, 0, 0);
	}

	/*
	 * 删除文件显示密码输入框
	 */
	private void showDelInputPsw(final DataInfo di, String msg) {
		if (curinputpopw != null) {
			curinputpopw.dismiss();
		}
		View pWin = getLayoutInflater().inflate(R.layout.myuploadinputpswmenu, null);
		final PopupWindow popmenu = new PopupWindow(pWin, 330, 260);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());
		curinputpopw = popmenu;
		Button okbtn = (Button) pWin.findViewById(R.id.bt_ok);
		Button cancelbtn = (Button) pWin.findViewById(R.id.bt_cancel);

		final EditText psw = (EditText) pWin.findViewById(R.id.psw);
		TextView msgs = (TextView) pWin.findViewById(R.id.msg);
		msgs.setText(msg);
		okbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String pswStr = psw.getText().toString();
				if (pswStr.trim().equals("")) {
					UIHelper.displayToast(getResources().getString(R.string.pswd_cannot_empty), MyUploadVideoActivity.this);
				} else {
					// Map<String,Object> result = libCloud.Set_file_password(id,pswStr);
					if (pswStr.trim().equals(di.getPasswd())) {
						popmenu.dismiss();
						showDelNoteDialog(di);
					} else {
						UIHelper.displayToast(getResources().getString(R.string.pswd_error), MyUploadVideoActivity.this);
					}

				}
			}
		});
		cancelbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popmenu.dismiss();

			}
		});
		Button forgetpwdbtn = (Button) pWin.findViewById(R.id.bt_forgetpwd);
		forgetpwdbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popmenu.dismiss();
				showForgetPsw(di);
			}
		});
		popmenu.showAtLocation(findViewById(R.id.myuploadvideo_page), Gravity.CENTER, 0, 0);
	}

	/*
	 * 取消加密显示密码输入框
	 */
	private void showDecryptionInputPsw(final DataInfo di, String msg) {
		if (curinputpopw != null) {
			curinputpopw.dismiss();
		}
		View pWin = getLayoutInflater().inflate(R.layout.myuploadinputpswmenu, null);
		final PopupWindow popmenu = new PopupWindow(pWin, 330, 260);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());
		curinputpopw = popmenu;
		Button okbtn = (Button) pWin.findViewById(R.id.bt_ok);
		Button cancelbtn = (Button) pWin.findViewById(R.id.bt_cancel);
		final EditText psw = (EditText) pWin.findViewById(R.id.psw);
		TextView msgs = (TextView) pWin.findViewById(R.id.msg);
		msgs.setText(msg);
		okbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String pswStr = psw.getText().toString();
				if (pswStr.trim().equals("")) {
					UIHelper.displayToast(getResources().getString(R.string.pswd_cannot_empty), MyUploadVideoActivity.this);
				} else {
					// Map<String,Object> result = libCloud.Set_file_password(id,pswStr);
					if (pswStr.trim().equals(di.getPasswd())) {
						popmenu.dismiss();
						Map<String, Object> result = null;
						try {
							result = libCloud.Set_file_password(di.getId(), "");
							if (result != null && ((String) result.get("code")).equals("1")) {
								di.setSecurity("0");
								int index = (currentPage - 1) * PAGE_SIZE + di.getPosition();
								Map<String, Object> m = videoList.get(index);
								m.put("security", "0");
								GetDataTask gdt = new GetDataTask();
								gdt.execute(currentPage);
							} else {
								UIHelper.displayToast(getResources().getString(R.string.pswd_setting_failed),
										MyUploadVideoActivity.this);
							}
						} catch (WeiboException e) {
							e.printStackTrace();
						}

					} else {
						UIHelper.displayToast(getResources().getString(R.string.pswd_error), MyUploadVideoActivity.this);
					}

				}
			}
		});
		cancelbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popmenu.dismiss();

			}
		});
		Button forgetpwdbtn = (Button) pWin.findViewById(R.id.bt_forgetpwd);
		forgetpwdbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popmenu.dismiss();
				showForgetPsw(di);
			}
		});
		popmenu.showAtLocation(findViewById(R.id.myuploadvideo_page), Gravity.CENTER, 0, 0);
	}

	/*
	 * 重命名显示密码输入框
	 */
	private void showRenameInputPsw(final DataInfo di, String msg) {
		if (curinputpopw != null) {
			curinputpopw.dismiss();
		}
		View pWin = getLayoutInflater().inflate(R.layout.myuploadinputpswmenu, null);
		final PopupWindow popmenu = new PopupWindow(pWin, 330, 260);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());
		curinputpopw = popmenu;
		Button okbtn = (Button) pWin.findViewById(R.id.bt_ok);
		Button cancelbtn = (Button) pWin.findViewById(R.id.bt_cancel);
		final EditText psw = (EditText) pWin.findViewById(R.id.psw);
		TextView msgs = (TextView) pWin.findViewById(R.id.msg);
		msgs.setText(msg);
		okbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String pswStr = psw.getText().toString();
				if (pswStr.trim().equals("")) {
					UIHelper.displayToast(getResources().getString(R.string.pswd_cannot_empty), MyUploadVideoActivity.this);
				} else {
					// Map<String,Object> result = libCloud.Set_file_password(id,pswStr);
					if (pswStr.trim().equals(di.getPasswd())) {
						popmenu.dismiss();
						showRenameWin(null, di);

					} else {
						UIHelper.displayToast(getResources().getString(R.string.pswd_error), MyUploadVideoActivity.this);
					}

				}
			}
		});
		cancelbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popmenu.dismiss();

			}
		});
		Button forgetpwdbtn = (Button) pWin.findViewById(R.id.bt_forgetpwd);
		forgetpwdbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popmenu.dismiss();
				showForgetPsw(di);
			}
		});
		popmenu.showAtLocation(findViewById(R.id.myuploadvideo_page), Gravity.CENTER, 0, 0);
	}

	public void showeditmenu(View view) {
		View popmenuWindow = getLayoutInflater().inflate(R.layout.editmenu, null);
		final PopupWindow popmenu = new PopupWindow(popmenuWindow, 105, 135);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句
		curpopw = popmenu;
		Button newbtn = (Button) popmenuWindow.findViewById(R.id.bt_new);
		newbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				// Toast.makeText(getApplicationContext(),(String)di.get("name"), 1000);
				shownewgallerymenu(popmenu);
			}
		});
		Button pastebtn = (Button) popmenuWindow.findViewById(R.id.bt_paste);
		pastebtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popmenu.dismiss();
				curpopw = null;
			}
		});
		popmenu.showAtLocation(findViewById(R.id.myuploadvideo_page), Gravity.RIGHT | Gravity.TOP, 30, 170);
	}

	public void shownewgallerymenu(PopupWindow pmenu) {
		if (pmenu != null)
			pmenu.dismiss();

		View popmenuWindow = getLayoutInflater().inflate(R.layout.newgallerymenu, null);
		final PopupWindow popmenu = new PopupWindow(popmenuWindow, 310, 265);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句
		curpopw = popmenu;
		final EditText inputName = (EditText) popmenuWindow.findViewById(R.id.input_name);
		Button okbtn = (Button) popmenuWindow.findViewById(R.id.bt_ok);
		okbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				// Toast.makeText(getApplicationContext(),(String)di.get("name"), 1000);
				popmenu.dismiss();

				Editable text = inputName.getText();
				Map<String, Object> newmap = new HashMap<String, Object>();
				if (text != null) {
					String key = text.toString().trim();
					try {
						newmap = libCloud.Create_dir(Params.RECOMMEND_VIDEO, key, order.peek());
					} catch (WeiboException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				curpopw = null;
				if ((newmap.get("code") != null) && (newmap.get("code").equals("1"))) {
					if (newmap.get("isnew").equals("1")) {
						DataInfo df = new DataInfo();
						df.setName(text.toString().trim());
						df.SetIsDir("1");
						df.setUrl((String) newmap.get("server"));
						df.setId((String) newmap.get("fileid"));
						df.setSecurity("0");
						refreshList.add(0, df);
						if (refreshList.size() > 12) {
							refreshList.remove(refreshList.size() - 1);
							totalPage++;
						}
						newmap.put("name", text.toString().trim());
						String url = (String) newmap.get("fileid");
						newmap.put("sourceurl", url);
						String id = (String) newmap.get("fileid");
						newmap.put("id", id);
						newmap.put("isdir", "1");
						videoList.add(0, newmap);

						/*
						 * ListAdapter ad = gridView.getAdapter(); ((MyUploadVideoDataAdapter)ad).notifyDataSetChanged();
						 */
						GetDataTask gdt = new GetDataTask();
						gdt.execute(currentPage);
					} else {
						UIHelper.displayToast("文件夹已存在", MyUploadVideoActivity.this);
					}
				}
			}
		});
		Button cancelbtn = (Button) popmenuWindow.findViewById(R.id.bt_cancel);
		cancelbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popmenu.dismiss();
				curpopw = null;
			}
		});
		popmenu.showAtLocation(findViewById(R.id.myuploadvideo_page), Gravity.CENTER, 20, -20);
	}

	private void showRenameWin(PopupWindow pmenu, final DataInfo info) {
		if (pmenu != null)
			pmenu.dismiss();

		View popmenuWindow = getLayoutInflater().inflate(R.layout.renamemenu, null);
		final PopupWindow popmenu = new PopupWindow(popmenuWindow, 403, 400);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());// 鍝嶅簲杩斿洖閿繀椤荤殑璇彞
		curpopw = popmenu;
		final EditText inputName = (EditText) popmenuWindow.findViewById(R.id.rename_input_name);
		Button okbtn = (Button) popmenuWindow.findViewById(R.id.bt_ok);
		okbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				// Toast.makeText(getApplicationContext(),(String)di.get("name"), 1000);
				popmenu.dismiss();

				Editable text = inputName.getText();
				if (text != null) {
					final String name = text.toString().trim();
					try {
						libCloud.Move_file(info.getId(), "0", name);
						int index = (currentPage - 1) * PAGE_SIZE + info.getPosition();
						Map<String, Object> m = (Map<String, Object>) videoList.get(index);
						m.put("name", name);
						// videoList.add(index,m);
					} catch (WeiboException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				GetDataTask gdt = new GetDataTask();
				gdt.execute(currentPage);

				curpopw = null;
			}
		});
		Button cancelbtn = (Button) popmenuWindow.findViewById(R.id.bt_cancel);
		cancelbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popmenu.dismiss();
				curpopw = null;
			}
		});
		popmenu.showAtLocation(findViewById(R.id.myuploadvideo_page), Gravity.CENTER, 20, -20);
	}

	public void showpopmenu(final DataInfo di, View view, String type) {
		if (curpopw != null)
			curpopw.dismiss();
		System.out.println("show pop menu");
		String strtext = di.getName();

		View popmenuWindow = getLayoutInflater().inflate(R.layout.myuploadpopmenu, null);

		final PopupWindow popmenu = new PopupWindow(popmenuWindow, 237, 194);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句
		curpopw = popmenu;
		// ImageButton passwordQueDing = (ImageButton) passwordWindow.findViewById(R.id.passwordqueding);
		Button playbtn = (Button) popmenuWindow.findViewById(R.id.bt_play);
		playbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				/*
				 * System.out.println("show pop menu: "+di.getName());
				 * //Toast.makeText(getApplicationContext(),(String)di.get("name"), 1000); popmenu.dismiss(); Intent it = new
				 * Intent(); it.setClass(MyUploadVideoActivity.this,VideoPlaybackActivity.class); Uri uri =
				 * Uri.parse(di.getUrl()); it.setData(uri); it.putExtra("position", 0); startActivity (it);
				 */
				popmenu.dismiss();
				if (di.getSecurity() != null && di.getSecurity().equals("1")) {
					showInputPsw(di, getResources().getString(R.string.pswd_input_notices));

				} else {
					judgeOperate(di);
				}
			}
		});
		Button renamebtn = (Button) popmenuWindow.findViewById(R.id.bt_rename);
		renamebtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popmenu.dismiss();
				if (di.getSecurity() != null && di.getSecurity().equals("1")) {
					showRenameInputPsw(di, getResources().getString(R.string.pswd_input_notices));
				} else {
					showRenameWin(null, di);
				}
			}
		});
		Button deletebtn = (Button) popmenuWindow.findViewById(R.id.bt_delete);
		deletebtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popmenu.dismiss();
				if (di.getSecurity() != null && di.getSecurity().equals("1")) {
					showDelInputPsw(di, getResources().getString(R.string.pswd_input_notices));
				} else {
					showDelNoteDialog(di);
				}
			}
		});
		Button attrbtn = (Button) popmenuWindow.findViewById(R.id.bt_attr);
		if (type != null && type.equals("1")) {
			attrbtn.setBackgroundResource(R.drawable.menuitem_decode);
			attrbtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					popmenu.dismiss();
					if (di.getSecurity() != null && di.getSecurity().equals("1")) {
						showDecryptionInputPsw(di, getResources().getString(R.string.pswd_input_notices));
					} else {

						Map<String, Object> result = null;
						try {
							result = libCloud.Set_file_password(di.getId(), "");
							if (result != null && ((String) result.get("code")).equals("1")) {
								di.setSecurity("0");
								int index = (currentPage - 1) * PAGE_SIZE + di.getPosition();
								Map<String, Object> m = videoList.get(index);
								m.put("security", "0");
								GetDataTask gdt = new GetDataTask();
								gdt.execute(currentPage);
							} else {
								UIHelper.displayToast(getResources().getString(R.string.pswd_setting_failed),
										MyUploadVideoActivity.this);
							}
						} catch (WeiboException e) {
							e.printStackTrace();
						}
					}
				}
			});
		} else {
			attrbtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					popmenu.dismiss();
					showEncryptMenu(di);
				}
			});
		}
		popmenu.showAtLocation(findViewById(R.id.myuploadvideo_page), Gravity.CENTER, 0, 0);
	}

	/*
	 * 显示加密对话框
	 */
	private void showEncryptMenu(final DataInfo di) {
		if (curEncryptpopw != null) {
			curEncryptpopw.dismiss();
		}
		View pWin = getLayoutInflater().inflate(R.layout.myuploadpopencryptmenu, null);
		final PopupWindow popmenu = new PopupWindow(pWin, 301, 317);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句
		curEncryptpopw = popmenu;
		Button okbtn = (Button) pWin.findViewById(R.id.bt_ok);
		Button cancelbtn = (Button) pWin.findViewById(R.id.bt_cancel);
		final EditText psw = (EditText) pWin.findViewById(R.id.psw);
		final EditText pswconfirm = (EditText) pWin.findViewById(R.id.pswconfirm);
		okbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String pswStr = psw.getText().toString();
				String pswConfirmStr = pswconfirm.getText().toString();
				if (pswStr.trim().equals("") || pswConfirmStr.trim().equals("")) {
					UIHelper.displayToast(getResources().getString(R.string.pswd_cannot_empty), MyUploadVideoActivity.this);
					return;
				}
				if (pswStr.equals(pswConfirmStr)) {
					try {
						Map<String, Object> result = libCloud.Set_file_password(di.getId(), pswStr);
						if (((String) result.get("code")).equals("1")) {
							di.setSecurity("1");
							int index = (currentPage - 1) * PAGE_SIZE + di.getPosition();
							Map<String, Object> m = videoList.get(index);
							m.put("security", "1");
							m.put("password", pswStr);
							GetDataTask gdt = new GetDataTask();
							gdt.execute(currentPage);
							/*
							 * ListAdapter ad = gridView.getAdapter(); ((MyUploadVideoDataAdapter)ad).notifyDataSetChanged();
							 */
						} else {
							UIHelper.displayToast(getResources().getString(R.string.pswd_setting_failed),
									MyUploadVideoActivity.this);
						}
					} catch (WeiboException e) {
						e.printStackTrace();
						UIHelper.displayToast(getResources().getString(R.string.pswd_setting_failed), MyUploadVideoActivity.this);
					}
					popmenu.dismiss();
				} else {
					UIHelper.displayToast(getResources().getString(R.string.pswd_isnot_same), MyUploadVideoActivity.this);
				}
			}
		});
		cancelbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popmenu.dismiss();

			}
		});
		popmenu.showAtLocation(findViewById(R.id.myuploadvideo_page), Gravity.CENTER, 0, 0);
	}

	private void showTrancodeWin(DataInfo di) {
		if (curTranscodepopw != null) {
			if (curTranscodepopw.isShowing()) {
				System.out.println("cruTranscodepopw is showing ");
			} else {
				System.out.println("curTranscodepopw is not showing");
			}
		} else {
			System.out.println("cruTranscodepopw is null");
		}
		if (curTranscodepopw != null) {
			curTranscodepopw.dismiss();
		}
		View pWin = getLayoutInflater().inflate(R.layout.transcode, null);
		final PopupWindow popmenu = new PopupWindow(pWin, 782, 272);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句
		curTranscodepopw = popmenu;
		transPager = (ViewPager) pWin.findViewById(R.id.transcodeViewPager);

		// addDataToTransGridView(transList);

		popmenu.showAtLocation(findViewById(R.id.myuploadvideo_page), Gravity.CENTER, 0, 0);

		onPercentChanged(di.getId(), di.getThumb());
	}

	public void onDropOutFoder(Object dragInfo) {
		System.out.println("=============================drop outside of folder ==================================");
		if (order.size() > 1) {
			mFolderDrop.setVisibility(View.GONE);
			if (dragInfo != null) {
				DataInfo info = (DataInfo) dragInfo;
				Map<String, Object> retmap = null;
				System.out.println("drop outside info name " + info.getName() + "position=" + info.getPosition());
				try {
					retmap = libCloud.Move_file(info.getId(), "0", info.getName());
				} catch (WeiboException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MyUploadVideoDataAdapter adapter1 = (MyUploadVideoDataAdapter) ((CustomerGridView) mListViews
						.get(currentPage - 1)).getAdapter();
				adapter1.getList().remove(info.getPosition());
				adapter1.notifyDataSetChanged();
			}
		}
	}

	private Map<String, List<DataInfo>> m3u8PlayMap;

	/*
	 * 点击转码按钮
	 */
	public void onTranscodeClick(View v) {
		MyLog.writeLog("播放点击响应：");
		ImageView iv = (ImageView) v;
		DataInfo info = (DataInfo) v.getTag();
		MyLog.writeLog("info：" + info);
		Log.i("onTranscodeClick", "detailResDownloadClick + type = " + info.GetTag());
		if (info.getStatus() == 5 || info.getStatus() == 4) {
			if (info.getUrl() != null && info.getUrl().length() > 0) {

				// ************zll 加入************
				playUrl(info.getUrl().trim());
				// ***********************************
				MyLog.writeLog("播放url：" + info.getUrl().trim());
				// MediaPlayerHelper.play(MyUploadVideoActivity.this, info.getUrl(),"0","0","0");

				/*
				 * Intent it = new Intent(); it.setClass(MyUploadVideoActivity.this,VideoPlaybackActivity.class); Uri uri =
				 * Uri.parse(info.getUrl()); it.setData(uri); it.putExtra("position", 0); startActivity (it);
				 */
			} else {
				UIHelper.displayToast("无效的播放连接", MyUploadVideoActivity.this);
			}
		} else if (info.getStatus() == 2 || info.getStatus() ==3) {
			/*
			 * iv.setImageResource(R.drawable.transcodeing); TransCodeTask transTask = new TransCodeTask();
			 * System.out.println("transcodeing id = " + info.getId());
			 * transTask.execute(info.getId(),info.GetTag(),info.getThumb(),iv);
			 */

			M3u8Task m3u8task = new M3u8Task(info);
			m3u8task.execute();
			/*
			 * String url = "http://service.wasu.com.cn:9126/test/b.m3u8"; Intent itent = new Intent(Intent.ACTION_VIEW); Uri uri
			 * = Uri.parse(url); itent.setDataAndType(uri, "video/*"); startActivity(itent);
			 */
		}
	}

	private class M3u8Task extends AsyncTask<Object, Integer, List<DataInfo>> {
		private DataInfo info;

		M3u8Task(DataInfo info) {
			this.info = info;
		}

		@Override
		protected List<DataInfo> doInBackground(Object... params) {
			Map<String, Object> retmap;
			List<DataInfo> percentList = null;
			try {
				retmap = libCloud.Start_transcode(info.getId(), info.GetTag());
				if (retmap != null) {
					String code = (String) retmap.get("code");
					if (code != null && code.equals("1")) {
						Map<String, Object> dataList = null;
						dataList = libCloud.Get_transpercent(info.getId());
						if (retmap != null && retmap.get("code").equals("1")) {
							percentList = DataHelpter.fillTransData((List<Map<String, Object>>) retmap.get("percentlist"), info.getId(), info.getThumb());
						}
					}
				}
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			return percentList;
		}

		@Override
		protected void onPostExecute(List<DataInfo> result) {
			if (result == null)
				UIHelper.displayToast("文件已经过期", MyUploadVideoActivity.this);
			super.onPostExecute(result);
			String url = "";
			for (int i = 0; i < result.size(); i++) {
				DataInfo di = result.get(i);
				if (di.GetTag().equals(info.GetTag())) {
					url = di.getUrl();
					break;
				}
			}
			playUrl(url);
		}

	}

	private void playUrl(String url) {
		Intent intent = new Intent();
		Uri uri = Uri.parse(url);
		intent.setDataAndType(uri, "video/*");
		intent.setAction(Intent.ACTION_VIEW);
		startActivity(intent);
	}

	class TransPercentTask extends AsyncTask<Object, Integer, List<DataInfo>> {
		String fileid;
		String imageurl;

		@Override
		protected List<DataInfo> doInBackground(Object... params) {
			fileid = (String) params[0];
			imageurl = (String) params[1];
			Map<String, Object> retmap = null;
			List<DataInfo> percentList = null;
			try {
				retmap = libCloud.Get_transpercent(fileid);
				if (retmap != null && retmap.get("code").equals("1")) {
					percentList = DataHelpter.fillTransData((List<Map<String, Object>>) retmap.get("percentlist"), fileid,
							imageurl);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (percentList == null) {
				return new ArrayList<DataInfo>();
				// percentList = new ArrayList<DataInfo>();
			}
			
			List<DataInfo> transList = new ArrayList<DataInfo>();
			for (int i = 0; i < 4; i++) {
				DataInfo info = new DataInfo();
				info.setThumb(imageurl);
				info.SetTag(transType[i]);/* 0 :640x480; 1:1280x720;2:1920x1080;3:1024x768 */
				info.setStatus(2);
				info.setId(fileid);
				for (int j = 0; j < percentList.size(); j++) {
					DataInfo info1 = percentList.get(j);
					if (info.GetTag().equals(info1.GetTag())) {
						info.setProgress(info1.getProgress());
						info.setStatus(info1.getStatus());
						String url = info1.getUrl();
						if (url != null && url.length() > 0) {
							info.setUrl(url);
						}
					}
				}
				transList.add(info);
			}
			/*
			 * for(int i=0;i<4;i++){ if(DataHelpter.findDataByType(percentList,transType[i])==false){ DataInfo info = new
			 * DataInfo(); info.setThumb(imageurl); info.SetTag(transType[i]); info.setStatus(2); info.setId(fileid);
			 * percentList.add(info); } }
			 */
			return transList;
		}

		@Override
		protected void onPostExecute(List<DataInfo> result) {
			if (myHandler != null) {
				addDataToTransGridView(result);
				if (DataHelpter.needUpdatePercent(result)) {
					onUpdateTransPercent(fileid);

				}
			}
		}
	}

	class UpdatePercentTask extends AsyncTask<Object, Integer, List<DataInfo>> {
		String fileid;

		@Override
		protected List<DataInfo> doInBackground(Object... params) {
			fileid = (String) params[0];
			String imageurl = "1";
			Map<String, Object> retmap = null;
			List<DataInfo> percentList = null;
			try {
				retmap = libCloud.Get_transpercent(fileid);
				if (retmap != null && retmap.get("code").equals("1")) {
					percentList = DataHelpter.fillTransData((List<Map<String, Object>>) retmap.get("percentlist"), fileid,
							imageurl);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (percentList == null) {
				return new ArrayList<DataInfo>();
			}
			return percentList;
		}

		@Override
		protected void onPostExecute(List<DataInfo> result) {

			if (myHandler != null) {
				UpdateTransGridView(result);
				if (DataHelpter.needUpdatePercent(result) && curTranscodepopw.isShowing()) {
					Message msg = new Message();
					msg.arg1 = Integer.parseInt(fileid);
					msg.what = PERCENT_CHANGED;
					myHandler.sendMessageDelayed(msg, 1000);
				}
			}
		}
	}

	/*
	 * class TransCodeTask extends AsyncTask<Object, Integer, String> { String fileid = null; String imageurl =null; ImageView iv
	 * = null;
	 * @Override protected String doInBackground(Object... params) { fileid = (String) params[0]; String width =
	 * (String)params[1]; imageurl = (String)params[2]; iv = (ImageView) params[3]; Map<String, Object> retmap=null; String code =
	 * null; try { retmap = libCloud.Start_transcode(fileid,width); if(retmap!=null ){ code = (String) retmap.get("code"); } }
	 * catch (Exception e) { e.printStackTrace(); } return code; }
	 * @Override protected void onPostExecute(String result) { if(result!=null && result.equals("1")){
	 * onUpdateTransPercent(fileid); }else{ UIHelper.displayToast("转码失败",MyUploadVideoActivity.this);
	 * iv.setImageResource(R.drawable.transcode); } } }
	 */

	class resetPasswdTask extends AsyncTask<Object, Integer, String> {
		DataInfo info = null;
		String newpswd = "";

		@Override
		protected String doInBackground(Object... params) {
			String username = (String) params[0];
			String loginpswd = (String) params[1];
			newpswd = (String) params[2];
			info = (DataInfo) params[3];
			String code = "0";
			Map<String, Object> retmap = null;
			try {
				System.out.println("username = " + username + "loginpswd =" + loginpswd + "newpswd = " + newpswd + "fileid = "
						+ info.getId());
				retmap = libCloud.reset_filepasswd(username, loginpswd, newpswd, info.getId());
				if (retmap != null && retmap.get("code").equals("1")) {
					System.out.println("reset password retmap = " + retmap);
					code = (String) retmap.get("code");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return code;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("1")) {
				int index = (currentPage - 1) * PAGE_SIZE + info.getPosition();
				Map<String, Object> m = videoList.get(index);
				m.put("security", "1");
				m.put("password", newpswd);

				GetDataTask gdt = new GetDataTask();
				gdt.execute(currentPage);
				UIHelper.showToast(MyUploadVideoActivity.this, getResources().getString(R.string.resetpswd_sucess));
			} else {

				UIHelper.showToast(MyUploadVideoActivity.this, getResources().getString(R.string.resetpswd_failed));
			}
		}
	}

	private void onPercentChanged(String fileid, String imageurl) {
		TransPercentTask percentTask = new TransPercentTask();
		percentTask.execute(fileid, imageurl);
	}

	private void onUpdateTransPercent(String fileid) {
		UpdatePercentTask updatepercentTask = new UpdatePercentTask();
		updatepercentTask.execute(fileid);
	}

	private void UpdateTransGridView(List<DataInfo> list) {
		TransDataAdapter adapter1 = (TransDataAdapter) ((CustomerGridView) transListViews.get(0)).getAdapter();
		List<DataInfo> transList = adapter1.getList();
		for (int i = 0; i < transList.size(); i++) {
			DataInfo info = transList.get(i);
			for (int j = 0; j < list.size(); j++) {
				DataInfo info1 = list.get(j);
				if (info.GetTag().equals(info1.GetTag())) {
					info.setProgress(info1.getProgress());
					info.setStatus(info1.getStatus());
					String url = info1.getUrl();
					if (url != null && url.length() > 0) {
						info.setUrl(url);
					}
				}
			}
		}
		adapter1.notifyDataSetChanged();
	}

	private final static int PERCENT_CHANGED = 0;
	Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			String fileid;
			switch (msg.what) {

			case PERCENT_CHANGED:
				onUpdateTransPercent(msg.arg1 + "");
				break;
			default:
				break;
			}

			super.handleMessage(msg);
		}
	};

	/* 删除提示对话框 */
	private void showDelNoteDialog(final DataInfo di) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MyUploadVideoActivity.this.getParent());
		builder.setTitle(R.string.system_tips);
		builder.setMessage(R.string.msg_del_file);
		builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				doDelFile(di);
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();

			}
		});
		builder.show();
	}

	private void doDelFile(DataInfo di) {
		try {
			libCloud.Delete_file(di.getId());
		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int index = (currentPage - 1) * PAGE_SIZE + di.getPosition();
		videoList.remove(index);
		GetDataTask gdt = new GetDataTask();
		gdt.execute(currentPage, order.peek());

		GetCapacityAsync capacity = new GetCapacityAsync();
		capacity.execute();
	}
}