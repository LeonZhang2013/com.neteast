package com.neteast.clouddisk.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
import android.widget.Toast;

import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.lib.net.WeiboException;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.activity.FolderDrop.FloderDropListener;
import com.neteast.clouddisk.adapter.MyUploadFileDataAdapter;
import com.neteast.clouddisk.adapter.ViewPageAdapter;
import com.neteast.clouddisk.customerview.CustomerGridView;
import com.neteast.clouddisk.customerview.DragLayer;
import com.neteast.clouddisk.handler.DownloadToastHandler;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.utils.DataHelpter;
import com.neteast.clouddisk.utils.DownLoadApplication;
import com.neteast.clouddisk.utils.OnDropCallBack;
import com.neteast.clouddisk.utils.UIHelper;


public class MyUploadFileActivity extends ActivityGroup implements FloderDropListener
{
	
	private CustomerGridView gridView;
	private LinearLayout loadingView = null;

	private int parentCurrentPage = 1;
	private int currentPage = 1;
	private int totalPage = 1;
	private TextView currentPageNum = null;
	private TextView totalPageNum = null;
	private PopupWindow curpopw=null;
	private PopupWindow curEncryptpopw=null;
	private PopupWindow curinputpopw=null;
	private LibCloud libCloud;
	private List<Map<String, Object>> docList = null;
	//private LinearLayout seriesView;
	private Stack<String> order = new Stack<String>();
	private Button retbtn;

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
	
	private DownLoadApplication download;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myuploadfile);
		libCloud = LibCloud.getInstance(this);
		
		download = (DownLoadApplication) getApplication();
		
		mDragLayer = (DragLayer)findViewById(R.id.myuploadfile_page);
		currentPageNum = (TextView) findViewById(R.id.curentpage);
		totalPageNum = (TextView) findViewById(R.id.totalpage);
		//gridView = (CustomerGridView) findViewById(R.id.mygridview);
		
		totalText1 = (TextView) findViewById(R.id.totalText1);
		usedText1 = (TextView) findViewById(R.id.usedText1);
		nousedText1 = (TextView) findViewById(R.id.nousedText1);
		
		filePager = (ViewPager) findViewById(R.id.myuploadFileViewPager);
		loadingView = (LinearLayout)findViewById(R.id.loading);
		
		//seriesView.setOnTouchListener(this);
		//seriesView.setLongClickable(true);
		Button uploadbtn =(Button)findViewById(R.id.bt_upload);
		uploadbtn.setOnClickListener(new OnClickListener(){
		public void onClick(View v) {
			((MyUploadActivity) MyUploadFileActivity.this
					.getParent()).startUploadFileActivity(4);
				
		}});
		newfolderbtn =(Button)findViewById(R.id.bt_edit);
		newfolderbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(curpopw!=null)
					curpopw.dismiss();
				shownewgallerymenu(null);
		}});
		retbtn = (Button) findViewById(R.id.returnbtn);
		retbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				order.pop();
				currentPage = parentCurrentPage;
				changeForder();

			}
		});
		recordbtn =(Button)findViewById(R.id.bt_record);
		recordbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				((MyUploadActivity) MyUploadFileActivity.this
						.getParent()).startUploadRecordActivity(4);
					
			}});

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
				((MyUploadActivity) MyUploadFileActivity.this
						.getParent()).startRecyclerActivity(4);
			}
		});
		mFolderDrop = (FolderDrop)findViewById(R.id.folder_drop);
		mFolderDrop.setFloderDropListener(this);
		mFolderDrop.setVisibility(View.GONE);
		
		GetCapacityAsync capacity = new GetCapacityAsync();
		capacity.execute();
		
		order.push("");
		GetDataTask gdt = new GetDataTask();
		gdt.execute(currentPage, order.peek());
		
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
	private void setGrid(Context context, List<DataInfo> list) {
		int PageCount = (int) Math.ceil(((double)list.size())/PAGE_SIZE);
		System.out.println("setGrid  PageCount = " + PageCount);
		for (int i = 0; i < PageCount; i++) {
			gridView = (CustomerGridView) LayoutInflater.from(context).inflate(
					R.layout.myupload_grid, null);
			gridView.setAdapter(new MyUploadFileDataAdapter(context, list,i,mLongClickListener,mClickListener));
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
								MyUploadFileDataAdapter adapter1 = (MyUploadFileDataAdapter) ((CustomerGridView) mListViews.get(currentPage -1)).getAdapter();
								adapter1.getList().remove(sourceInfo.getPosition());
								adapter1.notifyDataSetChanged();																
								if(docList!=null){
									docList.clear();
								}
								docList = null;
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
			System.out.println("MyDownloadMusicDataAdapter on Item Clicked");
			DataInfo info = (DataInfo) arg0.getTag();
			System.out.println("RecommendVideoActivity on item clicked" + "name = " + info.getName() + "url = " + info.getUrl() );	
			
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
			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			public void onPageScrollStateChanged(int arg0) {}
		});
		filePager.setCurrentItem(currentPage-1);
	}	
	

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
				if(docList==null){
					retmap = libCloud.Get_file_list(Params.RECOMMEND_APP, "",
							"" + params[1]);
					if (retmap == null)
						return null;
					if (!retmap.get("code").equals("1")) {
					
						return null;
					}
					docList = (List<Map<String, Object>>) retmap
							.get("filelist");
					if(docList==null)
						return null;
				}
				
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			Map<String, Object> m = DataHelpter.fillUploadData(docList, params);
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
				docList = new ArrayList<Map<String, Object>>();
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
			UIHelper.displayToast("userid or token is invalid",MyUploadFileActivity.this);
		}
	}	
}	
	public void changeForder() {
		currentPageNum.setText("1");
		totalPageNum.setText("1");
		docList = null;
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
		}else{
			recordbtn.setVisibility(View.GONE);
			newfolderbtn.setClickable(false);
		}
	}

	private String  downloadFile(String urlStr, String downloadName) {
		RandomAccessFile file = null;
		String path = Params.DOWNLOAD_FILE_PATH+ downloadName;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestProperty("Accept-Language", Params.ACCEPT_LANGUAGE);
			conn.setRequestProperty("Charset", Params.ChARSET);
			conn.setRequestProperty("User-Agent", "NetFox");
			conn.setRequestProperty("Range", "bytes=0-");
			conn.setRequestProperty("Connetion", "Keep-Alive");
			int fileSize = conn.getContentLength();
			if(fileSize < 0)
				return null;
			File f = new File(Params.DOWNLOAD_FILE_PATH);
			if (!f.exists()) {
				f.mkdir();
			}
			file = new RandomAccessFile(path, "rw");
			file.setLength(fileSize);

			InputStream in = conn.getInputStream();
			byte[] buffer = new byte[1024];
			int len;
			file.seek(0);
			while ((len = in.read(buffer)) != -1) {
				file.write(buffer, 0, len);
			}
			file.close();
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;

	}


	/*
	 * 判断点击后操作
	 */
	private void judgeOperate(DataInfo di){
		String isDir = di.GetIsDir();
		if (isDir != null && isDir.equals("1")) {
			parentCurrentPage = currentPage;
			currentPage = 1;
			order.push(di.getId());// error
			changeForder();
		} else {
			String urlStr = di.getUrl();
			if (urlStr != null && !urlStr.equals("")) {
				String downloadName = urlStr.substring(
						urlStr.lastIndexOf("/"), urlStr.length());
				
				DownloadToastHandler handler = new DownloadToastHandler(this, null);
				download.getDownloadList().put(di.getId(), handler);
				try {
					download.download(di);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				String path = downloadFile(urlStr, downloadName);
				if(path!=null && path.length()>0){
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setAction(Intent.ACTION_VIEW);
					File f = new File(path);
					Uri uri = Uri.fromFile(f);
					intent.setDataAndType(uri,new openfile().getType(f));
					try{
						startActivity(intent);
					}catch(Exception ee){
						Toast.makeText(MyUploadFileActivity.this, getResources().getString(R.string.noapptoopenthefile_notices),
								Toast.LENGTH_SHORT).show();
					}
				}else {
					Toast.makeText(MyUploadFileActivity.this, getResources().getString(R.string.cannotopenfile_notices),
							Toast.LENGTH_SHORT).show();
				}
				*/
			} else {
				Toast.makeText(MyUploadFileActivity.this, getResources().getString(R.string.cannotopenfile_notices),
						Toast.LENGTH_SHORT).show();
			}
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
		popmenu.showAtLocation(findViewById(R.id.myuploadfile_page), Gravity.CENTER, 0, 0);
	}
	 
	/*
	 * 显示密码输入框
	 */
	private void showInputPsw(final DataInfo di ,String msg){
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
					UIHelper.displayToast(getResources().getString(R.string.pswd_cannot_empty),MyUploadFileActivity.this);
				}else{
					//Map<String,Object> result = libCloud.Set_file_password(id,pswStr);
					if(pswStr.trim().equals(di.getPasswd())){
						judgeOperate(di);
						popmenu.dismiss();
					}else{
						UIHelper.displayToast(getResources().getString(R.string.pswd_error),MyUploadFileActivity.this);
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
		popmenu.showAtLocation(findViewById(R.id.myuploadfile_page), Gravity.CENTER, 0, 0);
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
					UIHelper.displayToast(getResources().getString(R.string.pswd_cannot_empty),MyUploadFileActivity.this);
				}else{
					//Map<String,Object> result = libCloud.Set_file_password(id,pswStr);
					if(pswStr.trim().equals(di.getPasswd())){
						popmenu.dismiss();
						showDelNoteDialog(di);
						
					}else{
						UIHelper.displayToast(getResources().getString(R.string.pswd_error),MyUploadFileActivity.this);
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
		popmenu.showAtLocation(findViewById(R.id.myuploadfile_page), Gravity.CENTER, 0, 0);
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
					UIHelper.displayToast(getResources().getString(R.string.pswd_cannot_empty),MyUploadFileActivity.this);
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
								Map<String,Object> m= docList.get(index);
								m.put("security", "0");
								GetDataTask gdt = new GetDataTask();
								gdt.execute(currentPage);
							}else{
								UIHelper.displayToast(getResources().getString(R.string.pswd_setting_failed),MyUploadFileActivity.this);
							}
						} catch (WeiboException e) {
							e.printStackTrace();
						}
						
					}else{
						UIHelper.displayToast(getResources().getString(R.string.pswd_error),MyUploadFileActivity.this);
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
		popmenu.showAtLocation(findViewById(R.id.myuploadfile_page), Gravity.CENTER, 0, 0);
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
					UIHelper.displayToast(getResources().getString(R.string.pswd_cannot_empty),MyUploadFileActivity.this);
				}else{
					//Map<String,Object> result = libCloud.Set_file_password(id,pswStr);
					if(pswStr.trim().equals(di.getPasswd())){
						popmenu.dismiss();
						showRenameWin(null,di);
						
					}else{
						UIHelper.displayToast(getResources().getString(R.string.pswd_error),MyUploadFileActivity.this);
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
		popmenu.showAtLocation(findViewById(R.id.myuploadfile_page), Gravity.CENTER, 0, 0);
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
		popmenu.showAtLocation(findViewById(R.id.myuploadfile_page), Gravity.RIGHT|Gravity.TOP, 30, 170);
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
						newmap = libCloud.Create_dir(Params.RECOMMEND_APP, key,
								order.peek());
					} catch (WeiboException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				curpopw=null;
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
						docList.add(0, newmap);
						GetDataTask gdt = new GetDataTask();
						gdt.execute(currentPage);
						/*
						ListAdapter ad = gridView.getAdapter();
						((MyUploadFileDataAdapter) ad).notifyDataSetChanged();
						*/
					}else{
						UIHelper.displayToast("文件夹已存在", MyUploadFileActivity.this);
					}
				}
			}
		});
		Button cancelbtn = (Button) popmenuWindow.findViewById(R.id.bt_cancel);
		cancelbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				curpopw=null;
			}});
		popmenu.showAtLocation(findViewById(R.id.myuploadfile_page), Gravity.CENTER, 20, -20);
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
						Map<String,Object> m = (Map<String,Object>)docList.get(index);
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
		popmenu.showAtLocation(findViewById(R.id.myuploadfile_page), Gravity.CENTER, 20, -20);
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
		//ImageButton passwordQueDing = (ImageButton) passwordWindow.findViewById(R.id.passwordqueding);
		Button playbtn = (Button) popmenuWindow.findViewById(R.id.bt_play);
		playbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				System.out.println("show pop menu: "+di.getName());
				//Toast.makeText(getApplicationContext(),(String)di.get("name"), 1000);
				popmenu.dismiss();
				if(di.getSecurity()!=null &&di.getSecurity().equals("1")){
					showInputPsw(di,getResources().getString(R.string.pswd_input_notices));
				}else{
					judgeOperate(di);
				}
			}
		});
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
							Map<String,Object> m= docList.get(index);
							m.put("security", "0");
							GetDataTask gdt = new GetDataTask();
							gdt.execute(currentPage);
						}else{
							UIHelper.displayToast(getResources().getString(R.string.pswd_setting_failed),MyUploadFileActivity.this);
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
		popmenu.showAtLocation(findViewById(R.id.myuploadfile_page),
				Gravity.CENTER, 0, 0);
	}
	/*
	 * 显示加密对话框
	 */
	private void showEncryptMenu(final DataInfo di){
		if(curEncryptpopw!=null){
			curEncryptpopw.dismiss();
		}
		View pWin= getLayoutInflater().inflate(R.layout.myuploadpopencryptmenu, null);
		final PopupWindow popmenu = new PopupWindow(pWin, 301,317);
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
					UIHelper.displayToast(getResources().getString(R.string.pswd_cannot_empty),MyUploadFileActivity.this);
					return ;
				}
				if(pswStr.equals(pswConfirmStr)){
					try {
						Map<String,Object> result = libCloud.Set_file_password(di.getId(),pswStr);
						if(((String)result.get("code")).equals("1")){
							di.setSecurity("1");
							int index = (currentPage-1)*PAGE_SIZE + di.getPosition();
							Map<String,Object> m= docList.get(index);
							m.put("security", "1");
							m.put("password", pswStr);
							GetDataTask gdt = new GetDataTask();
							gdt.execute(currentPage);
							/*
							ListAdapter ad = gridView.getAdapter();
							((MyUploadFileDataAdapter)ad).notifyDataSetChanged();
							*/
						}else{
							UIHelper.displayToast(getResources().getString(R.string.pswd_setting_failed),MyUploadFileActivity.this);
						}
					} catch (WeiboException e) {
						e.printStackTrace();
						UIHelper.displayToast(getResources().getString(R.string.pswd_setting_failed),MyUploadFileActivity.this);
					}
					popmenu.dismiss();
				}else{
					UIHelper.displayToast(getResources().getString(R.string.pswd_isnot_same),MyUploadFileActivity.this);
				}
			}});
		cancelbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				
			}});
		popmenu.showAtLocation(findViewById(R.id.myuploadfile_page), Gravity.CENTER, 0, 0);
	}
	
	public void onDropOutFoder(Object dragInfo){
		//System.out.println("=============================drop outside of folder ==================================");
		if(order.size()>1){
			mFolderDrop.setVisibility(View.GONE);
			if(dragInfo!=null){
				DataInfo info = (DataInfo) dragInfo;
				Map<String, Object> retmap=null;
				//System.out.println("drop outside info name "+ info.getName() + "position=" + info.getPosition());
				try {
					retmap = libCloud.Move_file(info.getId(), "0", info.getName());
				} catch (WeiboException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MyUploadFileDataAdapter adapter1 = (MyUploadFileDataAdapter) ((CustomerGridView) mListViews.get(currentPage -1)).getAdapter();
				adapter1.getList().remove(info.getPosition());
				adapter1.notifyDataSetChanged();
			}
		}
	}
	/*删除提示对话框*/
	private void showDelNoteDialog(final DataInfo di) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MyUploadFileActivity.this.getParent());
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
		
		int index = (currentPage - 1)
				* PAGE_SIZE
				+ di.getPosition();
		docList.remove(index);
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
				Map<String,Object> m= docList.get(index);
				m.put("security", "1");
				m.put("password", newpswd);
				
				GetDataTask gdt = new GetDataTask();
				gdt.execute(currentPage);
				UIHelper.showToast(MyUploadFileActivity.this,getResources().getString(R.string.resetpswd_sucess));
			}else{
	
				UIHelper.showToast(MyUploadFileActivity.this,getResources().getString(R.string.resetpswd_failed));
			}
		}
	}
}