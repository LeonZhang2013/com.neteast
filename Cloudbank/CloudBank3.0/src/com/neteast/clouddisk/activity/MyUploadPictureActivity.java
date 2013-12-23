package com.neteast.clouddisk.activity;

import java.io.Serializable;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.lib.net.WeiboException;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.activity.FolderDrop.FloderDropListener;
import com.neteast.clouddisk.adapter.MyUploadPictureDataAdapter;
import com.neteast.clouddisk.adapter.ViewPageAdapter;
import com.neteast.clouddisk.customerview.CustomerGridView;
import com.neteast.clouddisk.customerview.DragLayer;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.utils.DataHelpter;
import com.neteast.clouddisk.utils.OnDropCallBack;
import com.neteast.clouddisk.utils.UIHelper;

public class MyUploadPictureActivity extends ActivityGroup implements FloderDropListener
{

	private CustomerGridView gridView;
	private LinearLayout loadingView = null;

	private int parentCurrentPage = 1;
	private int currentPage = 1;
	private int totalPage = 1;
	private TextView currentPageNum = null;
	private TextView totalPageNum = null;
	private TextView currentTab;
	private PopupWindow curpopw=null;
	private PopupWindow curEncryptpopw=null;
	private PopupWindow curinputpopw=null;
	private LibCloud libCloud; 
	List<Map<String,Object>>  pictureList = null;
	List<DataInfo> picDataList =null;
	private Stack<String> order = new Stack<String>();
	Button retbtn;

	TextView totalText1 =null;
	TextView usedText1 =null;
	TextView nousedText1 = null;
	
	List<DataInfo> refreshList = null;
	private Button recordbtn;
	private Button editbutton;
	private Button cancelbutton;
	private Button newfolderbtn ;
	private DragLayer mDragLayer;
	boolean dragcondition = false;
	/** ViewPager控件，横屏滑动*/
	private ViewPager filePager;
	
	/** ViewPager的视图列表*/
	private List<View> mListViews;
	
	/** ViewPager的适配器. */
	private ViewPageAdapter viewPageAdapter;
	private static final int PAGE_SIZE = Params.MYUPLOAD_DATA_PER_PAGE_NUM;
	private FolderDrop mFolderDrop;
	//private ImageDownloader2 mImageDownloader2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myuploadpicture);
		libCloud = LibCloud.getInstance(this);
		//mImageDownloader2 = ((DownLoadApplication) getApplication()).getImageDownloader();
		mDragLayer = (DragLayer)findViewById(R.id.myuploadpicture_page);
		currentPageNum = (TextView) findViewById(R.id.curentpage);
		totalPageNum = (TextView) findViewById(R.id.totalpage);
		//gridView = (CustomerGridView) findViewById(R.id.mygridview);
		
		totalText1 = (TextView) findViewById(R.id.totalText1);
		usedText1 = (TextView) findViewById(R.id.usedText1);
		nousedText1 = (TextView) findViewById(R.id.nousedText1);
		
		filePager = (ViewPager) findViewById(R.id.myuploadPictureViewPager);
		loadingView = (LinearLayout)findViewById(R.id.loading);
		
		Button uploadbtn =(Button)findViewById(R.id.bt_upload);
		uploadbtn.setOnClickListener(new OnClickListener(){
		public void onClick(View v) {
			((MyUploadActivity) MyUploadPictureActivity.this
					.getParent()).startUploadFileActivity(3);
				
		}});
		newfolderbtn =(Button)findViewById(R.id.bt_edit);
		newfolderbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(curpopw!=null)
					curpopw.dismiss();
				shownewgallerymenu(null);
				//showeditmenu(null);
				//Toast.makeText(getApplicationContext(),(String)di.get("name"), 1000);
				
		}});
		recordbtn =(Button)findViewById(R.id.bt_record);
		recordbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				((MyUploadActivity) MyUploadPictureActivity.this
						.getParent()).startUploadRecordActivity(3);
					
			}});
		retbtn = (Button) findViewById(R.id.returnbtn);
		retbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				order.pop();
				currentPage = parentCurrentPage;
				changeForder();

			}
		});
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(90,
				LinearLayout.LayoutParams.WRAP_CONTENT);


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
		Button recyclerbtn = (Button) this.findViewById(R.id.bt_recycler);
		recyclerbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MyUploadActivity) MyUploadPictureActivity.this
						.getParent()).startRecyclerActivity(3);
			}
		});
		mFolderDrop = (FolderDrop)findViewById(R.id.folder_drop);
		mFolderDrop.setFloderDropListener(this);
		mFolderDrop.setVisibility(View.GONE);
		
		GetCapacityAsync capacity = new GetCapacityAsync();
		capacity.execute();
		
		/*
		order.push("");
		GetDataTask gdt = new GetDataTask();
		gdt.execute(currentPage, order.peek());
		*/
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(order.size()>1){
				order.pop();
				currentPage = parentCurrentPage;
				changeForder();
				return true;
			}else{
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override   
	protected void onResume() {    
	    super.onResume(); 
	    System.out.println("myuploadPic onResume :" );
	    pictureList = null;
	    if(order.size()==0){
	    	order.push("");
	    }
	    /*
	    String path = "";
	    if(order.size()>0){
	    	path = order.peek();
	    }
	    */
		GetDataTask gdt = new GetDataTask();
		//gdt.execute(currentPage, path);
		gdt.execute(currentPage, order.peek());
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		//mImageDownloader2.clearCache();
	
	}
	private void setGrid(Context context, List<DataInfo> list) {
		int PageCount = (int) Math.ceil(((double)list.size())/PAGE_SIZE);
		System.out.println("setGrid  PageCount = " + PageCount);
		for (int i = 0; i < PageCount; i++) {
			gridView = (CustomerGridView) LayoutInflater.from(context).inflate(
					R.layout.myupload_grid, null);
			gridView.setAdapter(new MyUploadPictureDataAdapter(context, list,i,mLongClickListener,mClickListener));
			gridView.setDataList(list);
			gridView.setDragger(mDragLayer);
			gridView.setOnDropListener(new  OnDropCallBack(){   
				public  void  onDropCompleted(DataInfo sourceInfo,DataInfo destInfo) {              
					//System.out.println("sourceInfo : name = " + sourceInfo.getName() + "destInfo name=" + destInfo.getName()); 
					mFolderDrop.setVisibility(View.GONE);					
					if (destInfo == null || sourceInfo == null) {
						return ;
					}
					if(destInfo==sourceInfo) return ;
					
					Map<String, Object> retmap=null;
					try {
						if(sourceInfo.GetIsDir()!=null && sourceInfo.GetIsDir().equals("1")){
							return ;
						}
						if((destInfo.GetIsDir() != null) && (destInfo.GetIsDir().equals("1"))){
							retmap = libCloud.Move_file(sourceInfo.getId(), destInfo.getId(), sourceInfo.getName());
							if(retmap!=null && retmap.get("code").equals("1")){
								MyUploadPictureDataAdapter adapter1 = (MyUploadPictureDataAdapter) ((CustomerGridView) mListViews.get(currentPage -1)).getAdapter();
								adapter1.getList().remove(sourceInfo.getPosition());
								adapter1.notifyDataSetChanged();																
								if(pictureList !=null){
									pictureList.clear();
								}
								pictureList = null;
								GetDataTask gdt = new GetDataTask();
								gdt.execute(currentPage,"");
							}
						}
					} catch (WeiboException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}   
			}); 
				
			// 去掉点击时的黄色背景
			//gridView.setSelector(R.color.transparent);
			mListViews.add(gridView);
		}
	}
	private View.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			boolean condition = false;
			//TODO:判断条件，是否是拖拽事件
			System.out.println("MyDownloadMusicDataAdapter on Long Clicked");
			if(dragcondition){
				if(order.size()>1){
					mFolderDrop.setVisibility(View.VISIBLE);
				}
				gridView.startDrag(v);
			}
			else{
				DataInfo info = (DataInfo) v.getTag();
				showpopmenu(info,null,info.getSecurity());
			}
			return true;
		}
	};
	
	private View.OnClickListener mClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			DataInfo info = (DataInfo) arg0.getTag();
			if(info.getSecurity()!=null &&info.getSecurity().equals("1")){
				showInputPsw(info,getResources().getString(R.string.pswd_input_notices));
			}else{
				judgeOperate(info);
			}
		}
	};

	public void addDataToGridView(final List<DataInfo> result) {
		refreshList = result;
		mListViews = new ArrayList<View>();
		setGrid(this, result);
		viewPageAdapter = new ViewPageAdapter(this,mListViews,1);
		filePager.setAdapter(viewPageAdapter);
		
		filePager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int page) {
				totalPage = DataHelpter.computeTotalPage(result.size(),PAGE_SIZE);
				currentPage = page + 1;
				currentPageNum.setText(currentPage+"");
				totalPageNum.setText(totalPage+"");
				/*
				myHandler.removeMessages(PAGE_CHANGED);			
				Message msg = new Message();
				msg.what = PAGE_CHANGED;
				Bundle b = new Bundle();
				b.putInt("page", page);
				b.putInt("cpage", currentPage);
				msg.setData(b);
				myHandler.sendMessageDelayed(msg, 1000);
				*/
			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			public void onPageScrollStateChanged(int arg0) {}
		});
		filePager.setCurrentItem(currentPage-1);
	}	
	/*
	private void displayImageView(int page){
		GridView v = (GridView) mListViews.get(page);
		MyUploadPictureDataAdapter adapter1 = (MyUploadPictureDataAdapter) v.getAdapter();
		for(int i=0;i<v.getCount();i++){
			View childv = v.getChildAt(i);	
			DataInfo info = (DataInfo) adapter1.getItem(i);
			if(childv!=null){
				ImageView imageView = (ImageView) childv.findViewById(R.id.imageView);
				String imageurl = info.getImage();
				if(imageurl!=null && imageurl.length()>0){
					mImageDownloader2.download(imageurl, imageView);
				}
			}
		}
	}
	*/ 
class GetDataTask extends AsyncTask<Object, Integer, List<DataInfo>> {
		@Override  
		protected void onPreExecute() {  
			//第一个执行方法  
			loadingView.setVisibility(View.VISIBLE);
			super.onPreExecute();  
		}
		@Override
		protected List<DataInfo> doInBackground(Object... params) {
			Map<String, Object> retmap=null;
			try {
				if(pictureList==null){
					retmap = libCloud.Get_file_list(Params.RECOMMEND_PICTURE,
							"", "" + params[1]);

					if (retmap == null)
						return null;
					if (!retmap.get("code").equals("1")) {
						return null;
					}

					pictureList = (List<Map<String, Object>>) retmap
							.get("filelist");
					if(pictureList==null)
						return null;
					//System.out.println("myuploadpic list size :" + l.size());
				}
				
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			Map<String, Object> m = DataHelpter.fillUploadData(pictureList,
					params);
			if (m == null) {
				return new ArrayList<DataInfo>();
			}
 
			totalPage = (Integer) m.get("totalpage");
			return (List<DataInfo>) m.get("result");
		}

		@Override
		protected void onPostExecute(List<DataInfo> result) {

			for(int i=0; i<result.size(); i++){
				System.out.println(result.get(i).toString());
			}
			
			if (result != null) {
				addDataToGridView(result);
				picDataList = result;
			} else {
				addDataToGridView(new ArrayList<DataInfo>());
				pictureList = new ArrayList<Map<String, Object>>();
				currentPage = 1;
				totalPage = 1;
			}
			loadingView.setVisibility(View.GONE);
			retbtn.setClickable(true);
			currentPageNum.setText(currentPage+"");
			totalPageNum.setText(totalPage+"");
		}

	}
	class GetCapacityAsync extends AsyncTask<Object, Integer, Map<String, Object> >{
		@Override
		protected Map<String, Object>  doInBackground(Object... params) {
			Map<String, Object> retmap=null;
			try {
				retmap = libCloud.Get_capacity();	
			}
			catch (WeiboException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return retmap;
		}

		@Override
		protected void onPostExecute(Map<String, Object> result) {
			String totalSpace = null;
			String usedSpace = null;
			if(result==null) return ;
			if(result.get("code").equals("1")){
				totalSpace = (String) result.get("totalspace");
				usedSpace = (String) result.get("usedspace");
				if(totalSpace==null){
					totalSpace="0";
				}if(usedSpace==null){
					usedSpace="0";
				}
				String usedGB = "";
				long size = Long.parseLong(usedSpace);
				long total = Long.parseLong(totalSpace);
				System.out.println("size = " + size);
				usedGB = DataHelpter.getSpaceStr(size);
				String leave = DataHelpter.getSpaceStr(total*1024*1024*1024-size);
				totalText1.setText(totalSpace+"GB");
				usedText1.setText(usedGB);
				nousedText1.setText(leave);
				
			}else if(result.get("code").equals("202")){
				UIHelper.displayToast("userid or token is invalid",MyUploadPictureActivity.this);
			}
		}	
	}
	public void changeForder() {
		currentPageNum.setText("1");
		totalPageNum.setText("1");
		pictureList = null;
		String str = "";
		
		if(order.size()>0){
			str = order.peek();
		}else{
			str="";
		}
		GetDataTask gdt = new GetDataTask();
		gdt.execute(currentPage, str);
		retbtn.setClickable(false);
		if (retbtn.getVisibility() == View.INVISIBLE) {
			retbtn.setVisibility(View.VISIBLE);
		}
		if (str != null && str.equals("")) {
			retbtn.setVisibility(View.INVISIBLE);
			recordbtn.setVisibility(View.VISIBLE);
			newfolderbtn.setClickable(true);
		}else{
			recordbtn.setVisibility(View.GONE);
			newfolderbtn.setClickable(false);
		}
	}

	/*
	 * 判断点击后操作
	 */
	private void judgeOperate(DataInfo di){
		String isDir = di.GetIsDir();
		if(isDir!=null &&isDir.equals("1")){
			parentCurrentPage = currentPage;
			currentPage = 1;
			order.push(di.getId());////////////////////////////////////////////
			changeForder();
		}else{
			int index = (currentPage - 1)*PAGE_SIZE + di.getPosition();
			Intent it = new Intent();
			it.setClass(MyUploadPictureActivity.this, ImageSwitcher1.class);
			if(order.size()>1){
				it.putExtra("result", (Serializable) picDataList);
			}else{
				List<DataInfo> imagesList = null;
				imagesList = new ArrayList<DataInfo>();
	    		imagesList.add(di);
	    		it.putExtra("result", (Serializable) imagesList);
	    		index = 0;
			}
			it.putExtra("position", index);
			it.putExtra("displayType", 1);
			startActivity(it);
		}
	}
	/*显示找回密码输入框*/
	private void showForgetPsw(final DataInfo di){
		if(curinputpopw!=null){
			curinputpopw.dismiss();
		}
		View pWin= getLayoutInflater().inflate(R.layout.forgetpswdmenu, null);
		final PopupWindow popmenu = new PopupWindow(pWin, 347, 319);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());
		curinputpopw = popmenu;
		
		Button okbtn = (Button) pWin.findViewById(R.id.bt_ok);
		Button cancelbtn = (Button) pWin.findViewById(R.id.bt_cancel);

		final EditText usernameet = (EditText) pWin.findViewById(R.id.edit_username);
		final EditText login_pswdet =  (EditText) pWin.findViewById(R.id.edit_login_pswd);
		final EditText new_pswdet =  (EditText) pWin.findViewById(R.id.edit_newpswd);
		final EditText new_pswd_againet =  (EditText) pWin.findViewById(R.id.edit_newpswd_again);
		final TextView tipstv = (TextView)pWin.findViewById(R.id.input_tips);
		tipstv.setVisibility(View.INVISIBLE);
	
		okbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				String username = usernameet.getText().toString().trim(); 
				String loginpswd = login_pswdet.getText().toString().trim();
				String newpswd = new_pswdet.getText().toString().trim();
				String newpswdagain = new_pswd_againet.getText().toString().trim();
				if(username.equals("")){
					tipstv.setText(R.string.username_cannotempty);
					tipstv.setVisibility(View.VISIBLE);
					//UIHelper.showToast(MyUploadVideoActivity.this, getResources().getString(R.string.username_cannotempty));
				}else if(loginpswd.equals("")){
					tipstv.setText(R.string.loginpswd_cannotempty);
					tipstv.setVisibility(View.VISIBLE);
					//UIHelper.showToast(MyUploadVideoActivity.this, getResources().getString(R.string.loginpswd_cannotempty));
				}else if(newpswd.equals("")){
					tipstv.setText(R.string.newpswd_cannotempty);
					tipstv.setVisibility(View.VISIBLE);
					//UIHelper.showToast(MyUploadVideoActivity.this, getResources().getString(R.string.newpswd_cannotempty));
				}else if(newpswdagain.equals("")){
					tipstv.setText(R.string.newpswdagain_cannotempty);
					tipstv.setVisibility(View.VISIBLE);
					//UIHelper.showToast(MyUploadVideoActivity.this, getResources().getString(R.string.newpswdagain_cannotempty));
				}
				else if(!newpswd.equals(newpswdagain)){
					tipstv.setText(R.string.pswd_notsame);
					tipstv.setVisibility(View.VISIBLE);
				}else{
					popmenu.dismiss();
					resetPasswdTask resetpswd = new resetPasswdTask();
					resetpswd.execute(username,loginpswd,newpswd,di);
				}
			}});
		cancelbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				
			}});		
		popmenu.showAtLocation(findViewById(R.id.myuploadpicture_page), Gravity.CENTER, 0, 0);
	}
	 
	/*
	 * 显示密码输入框
	 */
	private void showInputPsw(final DataInfo di,String msg){
		if(curinputpopw!=null){
			curinputpopw.dismiss();
		}
		View pWin= getLayoutInflater().inflate(R.layout.myuploadinputpswmenu, null);
		final PopupWindow popmenu = new PopupWindow(pWin, 330, 260);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句
		curinputpopw = popmenu;
		Button okbtn = (Button) pWin.findViewById(R.id.bt_ok);
		Button cancelbtn = (Button) pWin.findViewById(R.id.bt_cancel);
		Button forgetpwdbtn = (Button) pWin.findViewById(R.id.bt_forgetpwd);
		final EditText psw = (EditText) pWin.findViewById(R.id.psw);
		TextView msgs = (TextView) pWin.findViewById(R.id.msg);
		msgs.setText(msg);
		okbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				String pswStr = psw.getText().toString();
				if(pswStr.trim().equals("")){
					UIHelper.displayToast(getResources().getString(R.string.pswd_cannot_empty),MyUploadPictureActivity.this);
				}else{
					//Map<String,Object> result = libCloud.Set_file_password(id,pswStr);
					if(pswStr.trim().equals(di.getPasswd())){
						judgeOperate(di);
						popmenu.dismiss();
					}else{
						UIHelper.displayToast(getResources().getString(R.string.pswd_error),MyUploadPictureActivity.this);
					}
					
				}
			}});
		cancelbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				
			}});
		forgetpwdbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				popmenu.dismiss();
				showForgetPsw(di);
			}
		});
		popmenu.showAtLocation(findViewById(R.id.myuploadpicture_page), Gravity.CENTER, 0, 0);
	}
	/*
	 * 删除文件显示密码输入框
	 */
	private void showDelInputPsw(final DataInfo di,String msg){
		if(curinputpopw!=null){
			curinputpopw.dismiss();
		}
		View pWin= getLayoutInflater().inflate(R.layout.myuploadinputpswmenu, null);
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
		okbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				String pswStr = psw.getText().toString();
				if(pswStr.trim().equals("")){
					UIHelper.displayToast(getResources().getString(R.string.pswd_cannot_empty),MyUploadPictureActivity.this);
				}else{
					//Map<String,Object> result = libCloud.Set_file_password(id,pswStr);
					if(pswStr.trim().equals(di.getPasswd())){
						popmenu.dismiss();
						showDelNoteDialog(di);
					}else{
						UIHelper.displayToast(getResources().getString(R.string.pswd_error),MyUploadPictureActivity.this);
					}
					
				}
			}});
		cancelbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				
			}});
		Button forgetpwdbtn = (Button) pWin.findViewById(R.id.bt_forgetpwd);
		forgetpwdbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				popmenu.dismiss();
				showForgetPsw(di);
			}
		});
		popmenu.showAtLocation(findViewById(R.id.myuploadpicture_page), Gravity.CENTER, 0, 0);
	}
	/*
	 * 取消加密显示密码输入框
	 */
	private void showDecryptionInputPsw(final DataInfo di,String msg){
		if(curinputpopw!=null){
			curinputpopw.dismiss();
		}
		View pWin= getLayoutInflater().inflate(R.layout.myuploadinputpswmenu, null);
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
		okbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				String pswStr = psw.getText().toString();
				if(pswStr.trim().equals("")){
					UIHelper.displayToast(getResources().getString(R.string.pswd_cannot_empty),MyUploadPictureActivity.this);
				}else{
					//Map<String,Object> result = libCloud.Set_file_password(id,pswStr);
					if(pswStr.trim().equals(di.getPasswd())){
						popmenu.dismiss();
						Map<String, Object> result = null;
						try {
							result = libCloud.Set_file_password(di.getId(),"");
							if(result!=null &&((String)result.get("code")).equals("1")){
								di.setSecurity("0");
								int index = (currentPage-1)*PAGE_SIZE + di.getPosition();
								Map<String,Object> m= pictureList.get(index);
								m.put("security", "0");
								GetDataTask gdt = new GetDataTask();
								gdt.execute(currentPage);
							}else{
								UIHelper.displayToast(getResources().getString(R.string.pswd_setting_failed),MyUploadPictureActivity.this);
							}
						} catch (WeiboException e) {
							e.printStackTrace();
						}
						
					}else{
						UIHelper.displayToast(getResources().getString(R.string.pswd_error),MyUploadPictureActivity.this);
					}
					
				}
			}});
		cancelbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				
			}});
		Button forgetpwdbtn = (Button) pWin.findViewById(R.id.bt_forgetpwd);
		forgetpwdbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				popmenu.dismiss();
				showForgetPsw(di);
			}
		});
		popmenu.showAtLocation(findViewById(R.id.myuploadpicture_page), Gravity.CENTER, 0, 0);
	}
	/*
	 * 重命名显示密码输入框
	 */
	private void showRenameInputPsw(final DataInfo di,String msg){
		if(curinputpopw!=null){
			curinputpopw.dismiss();
		}
		View pWin= getLayoutInflater().inflate(R.layout.myuploadinputpswmenu, null);
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
		okbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				String pswStr = psw.getText().toString();
				if(pswStr.trim().equals("")){
					UIHelper.displayToast(getResources().getString(R.string.pswd_cannot_empty),MyUploadPictureActivity.this);
				}else{
					//Map<String,Object> result = libCloud.Set_file_password(id,pswStr);
					if(pswStr.trim().equals(di.getPasswd())){
						popmenu.dismiss();
						showRenameWin(null,di);
						
					}else{
						UIHelper.displayToast(getResources().getString(R.string.pswd_error),MyUploadPictureActivity.this);
					}
					
				}
			}});
		cancelbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				
			}});
		Button forgetpwdbtn = (Button) pWin.findViewById(R.id.bt_forgetpwd);
		forgetpwdbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				popmenu.dismiss();
				showForgetPsw(di);
			}
		});
		popmenu.showAtLocation(findViewById(R.id.myuploadpicture_page), Gravity.CENTER, 0, 0);
	}
	
	public void showeditmenu(View view){
		View popmenuWindow = getLayoutInflater().inflate(R.layout.editmenu, null);
		final PopupWindow popmenu = new PopupWindow(popmenuWindow, 105, 135);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句
		curpopw=popmenu;
		Button newbtn = (Button) popmenuWindow.findViewById(R.id.bt_new);
		newbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				
				//Toast.makeText(getApplicationContext(),(String)di.get("name"), 1000);
				shownewgallerymenu(popmenu);
			}});
		Button pastebtn = (Button) popmenuWindow.findViewById(R.id.bt_paste);
		pastebtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				curpopw=null;
			}});
		popmenu.showAtLocation(findViewById(R.id.myuploadpicture_page), Gravity.RIGHT|Gravity.TOP, 30, 170);
	}
	public void shownewgallerymenu(PopupWindow pmenu){
		if(pmenu !=null)
			pmenu.dismiss();
		
		View popmenuWindow = getLayoutInflater().inflate(R.layout.newgallerymenu, null);
		final PopupWindow popmenu = new PopupWindow(popmenuWindow, 310, 265);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句
		curpopw=popmenu;
		final EditText inputName = (EditText) popmenuWindow.findViewById(R.id.input_name);
		Button okbtn = (Button) popmenuWindow.findViewById(R.id.bt_ok);
		okbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				
				//Toast.makeText(getApplicationContext(),(String)di.get("name"), 1000);
				popmenu.dismiss();
				Editable text = inputName.getText();
				Map<String, Object> newmap = new HashMap<String, Object>();
				if (text != null) {
					String key = text.toString().trim();
					try {
						newmap = libCloud.Create_dir(Params.RECOMMEND_PICTURE,
								key, order.peek());
					} catch (WeiboException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				curpopw = null;
				if ((newmap.get("code") != null)
						&& (newmap.get("code").equals("1"))) {
					if(newmap.get("isnew").equals("1")){
						DataInfo df = new DataInfo();
						df.setName(text.toString().trim());
						df.SetIsDir("1");
						df.setUrl((String) newmap.get("server"));
					df.setId((String) newmap.get("fileid"));
						refreshList.add(0, df);
						if(refreshList.size()>12){
							refreshList.remove(refreshList.size() - 1);
							totalPage++;
						}
						newmap.put("name", text.toString().trim());
						String url = (String) newmap.get("fileid");
						newmap.put("sourceurl", url);
						String id = (String) newmap.get("fileid");
						newmap.put("id", id);
						newmap.put("isdir", "1");
						pictureList.add(0, newmap);
						GetDataTask gdt = new GetDataTask();
						gdt.execute(currentPage);
						/*
						ListAdapter ad = gridView.getAdapter();
						((MyUploadPictureDataAdapter) ad).notifyDataSetChanged();
						*/
					}else{
						UIHelper.displayToast("文件夹已存在", MyUploadPictureActivity.this);
					}
				}
			}});
		Button cancelbtn = (Button) popmenuWindow.findViewById(R.id.bt_cancel);
		cancelbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				curpopw=null;
			}});
		popmenu.showAtLocation(findViewById(R.id.myuploadpicture_page), Gravity.CENTER, 20, -20);
	}
	
	private void showRenameWin(PopupWindow pmenu,final DataInfo info){
		if(pmenu !=null)
			pmenu.dismiss();
		
		View popmenuWindow = getLayoutInflater().inflate(R.layout.renamemenu, null);
		final PopupWindow popmenu = new PopupWindow(popmenuWindow, 403, 400);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());// 鍝嶅簲杩斿洖閿繀椤荤殑璇彞
		curpopw=popmenu;
		final EditText inputName = (EditText) popmenuWindow.findViewById(R.id.rename_input_name);
		Button okbtn = (Button) popmenuWindow.findViewById(R.id.bt_ok);
		okbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				
				//Toast.makeText(getApplicationContext(),(String)di.get("name"), 1000);
				popmenu.dismiss();
				
				Editable text = inputName.getText();
				if (text != null) {
					final String name = text.toString().trim();
					try {
						libCloud.Move_file(info.getId(), "0", name);
						int index = (currentPage-1)*PAGE_SIZE + info.getPosition();
						Map<String,Object> m = (Map<String,Object>)pictureList.get(index);
						m.put("name", name);
						//videoList.add(index,m);
					} catch (WeiboException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
				GetDataTask gdt = new GetDataTask();
				gdt.execute(currentPage);
				
				curpopw=null;
			}});
		Button cancelbtn = (Button) popmenuWindow.findViewById(R.id.bt_cancel);
		cancelbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				curpopw=null;
			}});
		popmenu.showAtLocation(findViewById(R.id.myuploadpicture_page), Gravity.CENTER, 20, -20);
	}
	
	public void showpopmenu(final DataInfo di,View view,String type){
		if(curpopw!=null)
			curpopw.dismiss();
		System.out.println("show pop menu");
		
		View popmenuWindow = getLayoutInflater().inflate(R.layout.myuploadpopmenu, null);

		final PopupWindow popmenu = new PopupWindow(popmenuWindow, 237, 194);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句
		curpopw= popmenu;
		//ImageButton passwordQueDing = (ImageButton) passwordWindow.findViewById(R.id.passwordqueding);
		Button playbtn = (Button) popmenuWindow.findViewById(R.id.bt_play);
		playbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				/*System.out.println("show pop menu: " + di.getName());
				// Toast.makeText(getApplicationContext(),(String)di.get("name"),
				// 1000);
				popmenu.dismiss();
				Intent it = new Intent();
				it.setClass(MyUploadPictureActivity.this, ImageSwitcher1.class);
				it.putExtra("result", (Serializable) picDataList);
				// Uri uri = Uri.parse(di.getUrl());
				// it.setData(uri);
				it.putExtra("position", di.getPosition());
				startActivity(it);*/
				popmenu.dismiss();
				if(di.getSecurity()!=null &&di.getSecurity().equals("1")){
					showInputPsw(di,getResources().getString(R.string.pswd_input_notices));
				}else{
					judgeOperate(di);
				}
			}
		});
		/*
		 * Button transcodebtn = (Button)
		 * popmenuWindow.findViewById(R.id.bt_transcode);
		 * transcodebtn.setOnClickListener(new OnClickListener(){ public void
		 * onClick(View v) { popmenu.dismiss(); }}); Button sharebtn = (Button)
		 * popmenuWindow.findViewById(R.id.bt_share);
		 * sharebtn.setOnClickListener(new OnClickListener(){ public void
		 * onClick(View v) { popmenu.dismiss(); }});
		 */
		Button renamebtn = (Button) popmenuWindow.findViewById(R.id.bt_rename);
		renamebtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				if(di.getSecurity()!=null &&di.getSecurity().equals("1")){
					showRenameInputPsw(di,getResources().getString(R.string.pswd_input_notices));
				}else{
					showRenameWin(null,di);
				}
			}});
		Button deletebtn = (Button) popmenuWindow.findViewById(R.id.bt_delete);
		deletebtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				if(di.getSecurity()!=null &&di.getSecurity().equals("1")){
					showDelInputPsw(di,getResources().getString(R.string.pswd_input_notices));
				}else{
					showDelNoteDialog(di);
				}
			}});
		/*
		Button cutbtn = (Button) popmenuWindow.findViewById(R.id.bt_cut);
		cutbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
			}});
		Button copybtn = (Button) popmenuWindow.findViewById(R.id.bt_copy);
		copybtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
			}});
		*/	
		Button attrbtn = (Button) popmenuWindow.findViewById(R.id.bt_attr);
		if(type!=null && type.equals("1")){
			attrbtn.setBackgroundResource(R.drawable.menuitem_decode);
			attrbtn.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					popmenu.dismiss();
					if(di.getSecurity()!=null &&di.getSecurity().equals("1")){
						showDecryptionInputPsw(di,getResources().getString(R.string.pswd_input_notices));
					}else{
					Map<String, Object> result = null;
					try {
						result = libCloud.Set_file_password(di.getId(),"");
						if(result!=null &&((String)result.get("code")).equals("1")){
							di.setSecurity("0");
							int index = (currentPage-1)*PAGE_SIZE + di.getPosition();
							Map<String,Object> m= pictureList.get(index);
							m.put("security", "0");
							GetDataTask gdt = new GetDataTask();
							gdt.execute(currentPage);
						}else{
							UIHelper.displayToast(getResources().getString(R.string.pswd_setting_failed),MyUploadPictureActivity.this);
						}
					} catch (WeiboException e) {
						e.printStackTrace();
						}
					}
				}});
		}else{
			attrbtn.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					popmenu.dismiss();
					showEncryptMenu(di);
				}});
		}
		popmenu.showAtLocation(findViewById(R.id.myuploadpicture_page),
				Gravity.CENTER, 0, 0);
	}
	/*
	 * 显示加密对话�?
	 */
	private void showEncryptMenu(final DataInfo di){
		if(curEncryptpopw!=null){
			curEncryptpopw.dismiss();
		}
		View pWin= getLayoutInflater().inflate(R.layout.myuploadpopencryptmenu, null);
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
		okbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				String pswStr = psw.getText().toString();
				String pswConfirmStr = pswconfirm.getText().toString();
				if(pswStr.trim().equals("")||pswConfirmStr.trim().equals("")){
					UIHelper.displayToast(getResources().getString(R.string.pswd_cannot_empty),MyUploadPictureActivity.this);
					return ;
				}
				if(pswStr.equals(pswConfirmStr)){
					try {
						Map<String,Object> result = libCloud.Set_file_password(di.getId(),pswStr);
						if(((String)result.get("code")).equals("1")){
							di.setSecurity("1");
							int index = (currentPage-1)*PAGE_SIZE + di.getPosition();
							Map<String,Object> m= pictureList.get(index);
							m.put("security", "1");
							m.put("password", pswStr);
							GetDataTask gdt = new GetDataTask();
							gdt.execute(currentPage);
							/*
							ListAdapter ad = gridView.getAdapter();
							((MyUploadPictureDataAdapter)ad).notifyDataSetChanged();
							*/
						}else{
							UIHelper.displayToast(getResources().getString(R.string.pswd_setting_failed),MyUploadPictureActivity.this);
						}
					} catch (WeiboException e) {
						e.printStackTrace();
						UIHelper.displayToast(getResources().getString(R.string.pswd_setting_failed),MyUploadPictureActivity.this);
					}
					popmenu.dismiss();
				}else{
					UIHelper.displayToast(getResources().getString(R.string.pswd_isnot_same),MyUploadPictureActivity.this);
				}
			}});
		cancelbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				
			}});
		popmenu.showAtLocation(findViewById(R.id.myuploadpicture_page), Gravity.CENTER, 0, 0);
	}
	public void onDropOutFoder(Object dragInfo){
		System.out.println("=============================drop outside of folder ==================================");
		if(order.size()>1){
			mFolderDrop.setVisibility(View.GONE);
			if(dragInfo!=null){
				DataInfo info = (DataInfo) dragInfo;
				Map<String, Object> retmap=null;
				System.out.println("drop outside info name "+ info.getName() + "position=" + info.getPosition());
				try {
					retmap = libCloud.Move_file(info.getId(), "0", info.getName());
				} catch (WeiboException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MyUploadPictureDataAdapter adapter1 = (MyUploadPictureDataAdapter) ((CustomerGridView) mListViews.get(currentPage -1)).getAdapter();
				adapter1.getList().remove(info.getPosition());
				adapter1.notifyDataSetChanged();
			}
		}
	}
	/*删除提示对话框*/
	private void showDelNoteDialog(final DataInfo di) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MyUploadPictureActivity.this.getParent());
		builder.setTitle(R.string.system_tips);
		builder.setMessage(R.string.msg_del_file);
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
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
	private void doDelFile(DataInfo di){
		try {
			libCloud.Delete_file(di.getId());
		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int index = (currentPage-1)*PAGE_SIZE + di.getPosition();
		pictureList.remove(index);
		GetDataTask gdt = new GetDataTask();
		gdt.execute(currentPage,order.peek());
		
		GetCapacityAsync capacity = new GetCapacityAsync();
		capacity.execute();
	}
	class resetPasswdTask extends AsyncTask<Object, Integer, String> {
		DataInfo info =null;
		String newpswd="";
		@Override
		protected String doInBackground(Object... params) {
			String username = (String) params[0];
			String loginpswd = (String) params[1];
			newpswd = (String) params[2];
			info = (DataInfo) params[3];
			String code="0";
			Map<String, Object> retmap=null;
			try {
				System.out.println("username = " + username + "loginpswd =" + loginpswd + "newpswd = " + newpswd + "fileid = " + info.getId());
				retmap = libCloud.reset_filepasswd(username, loginpswd, newpswd, info.getId());
				if(retmap!=null && retmap.get("code").equals("1")){
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
			if(result.equals("1")){
				int index = (currentPage-1)*PAGE_SIZE + info.getPosition();
				Map<String,Object> m= pictureList.get(index);
				m.put("security", "1");
				m.put("password", newpswd);
				
				GetDataTask gdt = new GetDataTask();
				gdt.execute(currentPage);
				UIHelper.showToast(MyUploadPictureActivity.this,getResources().getString(R.string.resetpswd_sucess));
			}else{
	
				UIHelper.showToast(MyUploadPictureActivity.this,getResources().getString(R.string.resetpswd_failed));
			}
		}
	}
	private final static int PAGE_CHANGED = 0;
	Handler myHandler = new Handler(){
		    
		@Override
		public void handleMessage(Message msg) {
				// TODO Auto-generated method stub	
			switch(msg.what){	
				case PAGE_CHANGED:
					int page = msg.getData().getInt("page");
					int cpage = msg.getData().getInt("cpage");
					System.out.println("hadnleMessage page = " + page + "cpage = " + cpage);
					//displayImageView(page);
					break;
			}	
			super.handleMessage(msg);
		}	
	};
}
