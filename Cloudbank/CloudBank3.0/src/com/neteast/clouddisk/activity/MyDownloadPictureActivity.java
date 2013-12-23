package com.neteast.clouddisk.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.lib.net.WeiboException;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.activity.FolderDrop.FloderDropListener;
import com.neteast.clouddisk.adapter.MyDownloadPictureDataAdapter;
import com.neteast.clouddisk.adapter.ViewPageAdapter;
import com.neteast.clouddisk.customerview.CustomerGridView;
import com.neteast.clouddisk.customerview.DragLayer;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.utils.DataHelpter;
import com.neteast.clouddisk.utils.DownLoadApplication;
import com.neteast.clouddisk.utils.ImageDownloader2;
import com.neteast.clouddisk.utils.OnDropCallBack;
import com.neteast.clouddisk.utils.UIHelper;



public class MyDownloadPictureActivity extends ActivityGroup implements FloderDropListener
{

	private CustomerGridView gridView;
	private LinearLayout loadingView = null;

	private int parentCurrentPage = 1;
	private int currentPage = 1;
	private int totalPage = 1;
	private TextView currentPageNum = null;
	private TextView totalPageNum = null;
	private PopupWindow curEncryptpopw=null;
	private PopupWindow curinputpopw=null;
	private LibCloud libCloud;
	int flingDirection = 1;
	private List<Map<String, Object>> pictureList = null;
	List<DataInfo> picDataList =null;
	private PopupWindow curpopw=null;
	private DragLayer mDragLayer;
	
	TextView totalText1 =null;
	TextView usedText1 =null;
	TextView nousedText1 = null;
	
	Button returnbutton;
	String path="";
	private Button editbutton;
	private Button cancelbutton;
	boolean dragcondition = false;
	/** ViewPager控件，横屏滑动*/
	private ViewPager filePager;
	
	/** ViewPager的视图列表*/
	private List<View> mListViews;
	
	/** ViewPager的适配器. */
	private ViewPageAdapter viewPageAdapter;
	private static final int PAGE_SIZE = Params.MYDOWNLOAD_PICTURE_PER_PAGE_NUM;
	private FolderDrop mFolderDrop;
	private ImageDownloader2 mImageDownloader2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mydownloadpicture);
		libCloud = LibCloud.getInstance(this);
		mImageDownloader2 = ((DownLoadApplication) getApplication()).getImageDownloader();
		mDragLayer = (DragLayer)findViewById(R.id.mydownloadpicture);
		currentPageNum = (TextView) findViewById(R.id.curentpage);
		totalPageNum = (TextView) findViewById(R.id.totalpage);
		//gridView = (CustomerGridView) findViewById(R.id.mygridview);
		loadingView = (LinearLayout)findViewById(R.id.loading);
		totalText1 = (TextView) findViewById(R.id.totalText1);
		usedText1 = (TextView) findViewById(R.id.usedText1);
		nousedText1 = (TextView) findViewById(R.id.nousedText1);
		
		mFolderDrop = (FolderDrop)findViewById(R.id.folder_drop);
		mFolderDrop.setFloderDropListener(this);
		mFolderDrop.setVisibility(View.GONE);
		
		filePager = (ViewPager) findViewById(R.id.mydownloadPictureViewPager);
		/*LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(90,
				LinearLayout.LayoutParams.WRAP_CONTENT);*/

		returnbutton = (Button) this.findViewById(R.id.returnbutton);
		returnbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				path="";
				currentPage = parentCurrentPage;
				changeForder(path);
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
		
		GetCapacityAsync capacity = new GetCapacityAsync();
		capacity.execute();

		/*
		GetDataTask gdt = new GetDataTask();
		gdt.execute(currentPage,PAGE_SIZE);
		*/
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(!path.equals("")){
				path="";
				currentPage = parentCurrentPage;
				changeForder(path);
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
	    System.out.println("mydownloadPic onResume :" );
	    pictureList = null;
	    GetDataTask gdt = new GetDataTask();
		gdt.execute(currentPage,PAGE_SIZE);
	}
	
	public void addDataToGridView(final List<DataInfo> result) {

		mListViews = new ArrayList<View>();
		setGrid(this, result);
		viewPageAdapter = new ViewPageAdapter(this,mListViews,Params.MYDOWNLOAD_PICTURE);
		filePager.setAdapter(viewPageAdapter);
		filePager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageSelected(int page) {
				totalPage = DataHelpter.computeTotalPage(result.size(),PAGE_SIZE);
				currentPage = page + 1;
				currentPageNum.setText(currentPage+"");
				totalPageNum.setText(totalPage+"");
				
				Message msg = new Message();
				msg.what = PAGE_CHANGED;
				Bundle b = new Bundle();
				b.putInt("page", page);
				b.putInt("cpage", currentPage);
				msg.setData(b);
				myHandler.sendMessageDelayed(msg, 1000);
			}
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			public void onPageScrollStateChanged(int arg0) {}
		});
		filePager.setCurrentItem(currentPage-1);
	}
	private void changeForder(String pf){
		currentPageNum.setText("1");
		totalPageNum.setText("1");
		pictureList = null;
		
		GetDataTask gdt = new GetDataTask();
		path = pf;
		gdt.execute(currentPage,PAGE_SIZE);
		if(returnbutton.getVisibility()==View.INVISIBLE){
			returnbutton.setVisibility(View.VISIBLE);
		}
		if(path!=null&&(!path.equals(""))){
			returnbutton.setVisibility(View.VISIBLE);
		}else{
			returnbutton.setVisibility(View.GONE);
		}
	}
	@Override
	public void onDestroy()
	{
		mImageDownloader2.clearCache();
		super.onDestroy();
	    //System.out.println("***************** playback activity onDestroy\n");
	}
	private void setGrid(Context context, List<DataInfo> list) {
	//	int PageCount = (int) Math.ceil(list.size() / PAGE_SIZE);
		int PageCount = (int) Math.ceil(((double)list.size())/PAGE_SIZE);
		System.out.println("setGrid  PageCount = " + PageCount);
		for (int i = 0; i < PageCount; i++) {
			gridView = (CustomerGridView) LayoutInflater.from(context).inflate(
					R.layout.mydownload_grid, null);
			gridView.setAdapter(new MyDownloadPictureDataAdapter(context, list,i,mLongClickListener,mClickListener));
			gridView.setDataList(list);
			gridView.setNumColumns(7);
			gridView.setVerticalSpacing(20);
			gridView.setHorizontalSpacing(10);	
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
						if(destInfo.GetIsDir()!=null&&destInfo.GetIsDir().equals("1")){
							//retmap = libCloud.Mydownload_merge(Params.RECOMMEND_PICTURE, destInfo.GetPkgName(), "", sourceInfo.getResid());
							retmap = libCloud.Mydownload_merge(Params.RECOMMEND_PICTURE, destInfo.GetPkgName(), "", sourceInfo.getId());
						}else{
							//retmap = libCloud.Mydownload_merge(Params.RECOMMEND_PICTURE, "", destInfo.getResid(), sourceInfo.getResid());
							retmap = libCloud.Mydownload_merge(Params.RECOMMEND_PICTURE, "", destInfo.getId(), sourceInfo.getId());
						}
						if(retmap!=null && retmap.get("code").equals("1")){
							MyDownloadPictureDataAdapter adapter1 = (MyDownloadPictureDataAdapter) ((CustomerGridView) mListViews.get(currentPage -1)).getAdapter();
							adapter1.getList().remove(sourceInfo.getPosition());
							adapter1.notifyDataSetChanged();
							
							if(pictureList!=null){
								pictureList.clear();
							}
							pictureList = null;
							GetDataTask gdt = new GetDataTask();
							gdt.execute(currentPage,PAGE_SIZE);
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
	private void displayImageView(int page){
		GridView v = (GridView) mListViews.get(page);
		MyDownloadPictureDataAdapter adapter1 = (MyDownloadPictureDataAdapter) v.getAdapter();
		for(int i=0;i<v.getCount();i++){
			View childv = v.getChildAt(i);	
			DataInfo info = (DataInfo) adapter1.getItem(i);
			if(childv!=null){
				if (info.GetIsDir() != null && info.GetIsDir().equals("1")) {
					
					LinearLayout mergeView = (LinearLayout) childv.findViewById(R.id.mergeView);
					TableLayout tl = (TableLayout) mergeView.getChildAt(0);
					TableRow tr1 = (TableRow) tl.getChildAt(0);
					TableRow tr2 = (TableRow) tl.getChildAt(1);
					TableRow tr3 = (TableRow) tl.getChildAt(2);
					String images = info.getThumb();
					String[] imageArray = images.split(",");
					for (int j = 0; j < imageArray.length; j++) {
							String smallurl = imageArray[j];
							
							switch (j) {
							case 0:
								mImageDownloader2.download(smallurl, (ImageView)tr1.getChildAt(0));
								//libCloud.DisplayImage(smallurl, (ImageView)tr1.getChildAt(0));
								break;
							case 1:
								mImageDownloader2.download(smallurl, (ImageView)tr1.getChildAt(1));
								//libCloud.DisplayImage(smallurl, (ImageView)tr1.getChildAt(1));
								break;
							case 2:
								mImageDownloader2.download(smallurl, (ImageView)tr1.getChildAt(2));
								//libCloud.DisplayImage(smallurl, (ImageView)tr1.getChildAt(2));
								break;
							case 3:
								mImageDownloader2.download(smallurl, (ImageView)tr2.getChildAt(0));
								//libCloud.DisplayImage(smallurl, (ImageView)tr2.getChildAt(0));
								break;
							case 4:
								mImageDownloader2.download(smallurl, (ImageView)tr2.getChildAt(1));
								//libCloud.DisplayImage(smallurl, (ImageView)tr2.getChildAt(1));
								break;
							case 5:
								mImageDownloader2.download(smallurl, (ImageView)tr2.getChildAt(2));
								//libCloud.DisplayImage(smallurl, (ImageView)tr2.getChildAt(2));
								break;
							case 6:
								mImageDownloader2.download(smallurl, (ImageView)tr3.getChildAt(0));
								//libCloud.DisplayImage(smallurl, (ImageView)tr3.getChildAt(0));
								break;
							case 7:
								mImageDownloader2.download(smallurl, (ImageView)tr3.getChildAt(1));
								//libCloud.DisplayImage(smallurl, (ImageView)tr3.getChildAt(1));
								break;
							case 8:
								mImageDownloader2.download(smallurl, (ImageView)tr3.getChildAt(2));
								//libCloud.DisplayImage(smallurl, (ImageView)tr3.getChildAt(2));
								break;
							}
						}
					
				}else{
					ImageView imageView = (ImageView) childv.findViewById(R.id.imageView);
					String imageurl = info.getImage();
					if(imageurl!=null && imageurl.length()>0){
						
						//libCloud.DisplayImage(imageurl,imageView);
						//System.out.println("start download image url = " + imageurl);
					
						mImageDownloader2.download(imageurl, imageView);
					}
				}
			}
		}
	}
	private View.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
		
			//TODO:判断条件，是否是拖拽事件
			if(dragcondition){
				if(path!=null&&(!path.equals(""))){
					mFolderDrop.setVisibility(View.VISIBLE);
				}
				gridView.startDrag(v);
			}
			else{
				DataInfo info = (DataInfo) v.getTag();
				if(info.GetIsDir()!=null && info.GetIsDir().equals("1")){
					return true;
				}
				showpopmenu(info,null);
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
			
			if(info.getUrl()==null || info.getUrl().length()==0){
				UIHelper.displayToast(getResources().getString(R.string.invalid_url), MyDownloadPictureActivity.this);
			}else {
				if(info.getSecurity()!=null &&info.getSecurity().equals("1")){
					showInputPsw(info);
				}else{
					execute(info);
				}
			}
		}
	};
	class GetDataTask extends AsyncTask<Object, Integer, List<DataInfo>> {
		@Override  
	    protected void onPreExecute() {  
			//第一个执行方法  
			loadingView.setVisibility(View.VISIBLE);
		    super.onPreExecute();  
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected List<DataInfo> doInBackground(Object... params) {
			Map<String, Object> retmap=null;
			try {
				if(pictureList==null){ 
					retmap = libCloud.Mydownload_list(Params.RECOMMEND_PICTURE, path);
					System.out.println("mydownloadVideo GetDataTast code :" + retmap.get("code"));
					if(!retmap.get("code").equals("1")){
						System.out.println("mydownloadVideo return NULL,code="+retmap.get("code") );
						return null;
					}
					pictureList = (List<Map<String, Object>>) retmap.get("download");
					if(pictureList==null){
						return new ArrayList<DataInfo>();
					}
					List<String> paths = new ArrayList<String>();
					if("".equals(path)){
						for (Iterator iterator = pictureList.iterator(); iterator
								.hasNext();) {
							Map<String, Object> map = (Map<String, Object>) iterator.next();
							if(map.get("path")!=null&&(!"".equals(map.get("path")))){
								StringBuilder imageUrls=new StringBuilder();
								if(!paths.contains(map.get("path"))){
									paths.add((String)map.get("path"));
									/*---------------------------获取四幅图片start*/
									int i = 8;
									for (Iterator iterator2 = pictureList.iterator(); iterator2
											.hasNext();) {
										Map<String, Object> map2 = (Map<String, Object>) iterator2.next();
										if(i<0){
											break;
										}
										if(map2.get("path").equals(map.get("path"))){
											imageUrls.append(map2.get("image"));
											imageUrls.append(",");
											i--;
										}
									}
									map.put("image", imageUrls.toString());
									/*---------------------------获取四幅图片end*/
								}else{
									iterator.remove();
								}
								
							}
						}
					}else{
						for (Iterator iterator = pictureList.iterator(); iterator
								.hasNext();) {
							Map<String, Object> map = (Map<String, Object>) iterator.next();
							if(map.get("path")!=null&&(!"".equals(map.get("path")))){
								map.put("path", "");
							}
						}
					}
				}
				
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			Map<String, Object> m = DataHelpter.fillDownloadData(pictureList, params);
			if (m == null) {
				return new ArrayList<DataInfo>();
			}
 
			totalPage = (Integer) m.get("totalpage");
			return (List<DataInfo>) m.get("result");
		}

		@Override
		protected void onPostExecute( List<DataInfo> result ) {
			if(result!=null){
				addDataToGridView(result);
				picDataList = result;
			}else{
				currentPage = 1;
				totalPage = 1;
			}
			currentPageNum.setText(currentPage+"");
			totalPageNum.setText(totalPage+"");
			loadingView.setVisibility(View.GONE);
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
				UIHelper.displayToast("userid or token is invalid",MyDownloadPictureActivity.this);
			}
		}
		
	}
	
	/*
	 * 显示密码输入框
	 */
	private void showInputPsw(final DataInfo di){
		if(curinputpopw!=null){
			curinputpopw.dismiss();
		}
		View pWin= getLayoutInflater().inflate(R.layout.myuploadinputpswmenu, null);
		final PopupWindow popmenu = new PopupWindow(pWin, 301, 260);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());
		curinputpopw = popmenu;
		Button okbtn = (Button) pWin.findViewById(R.id.bt_ok);
		Button cancelbtn = (Button) pWin.findViewById(R.id.bt_cancel);
		final EditText psw = (EditText) pWin.findViewById(R.id.psw);
		
		okbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				String pswStr = psw.getText().toString();
				if(pswStr.trim().equals("")){
					UIHelper.displayToast(getResources().getString(R.string.pswd_cannot_empty),MyDownloadPictureActivity.this);
				}else{
					//Map<String,Object> result = libCloud.Set_file_password(id,pswStr);
					if(true){
						execute(di);
						popmenu.dismiss();
					}else{
						UIHelper.displayToast("getResources().getString(R.string.pswd_error)",MyDownloadPictureActivity.this);
					}
					
				}
			}});
		cancelbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				
			}});
		popmenu.showAtLocation(findViewById(R.id.mydownloadpicture), Gravity.CENTER, 0, 0);
	}
	/*
	 * 执行具体操作
	 */
	private void execute(DataInfo di){
		if(di.GetIsDir()!=null&&di.GetIsDir().equals("1")){
			parentCurrentPage = currentPage;
			currentPage = 1;
			changeForder(di.GetPkgName());
			return;
		}
		int index = (currentPage - 1)*PAGE_SIZE + di.getPosition();
		Intent it = new Intent(); 
    	it.setClass(MyDownloadPictureActivity.this,ImageSwitcher1.class);
    	if(path!=null&&(!path.equals(""))){
    		it.putExtra("result", (Serializable)picDataList);
    	}else{
    		List<DataInfo> imagesList = null;
			imagesList = new ArrayList<DataInfo>();
    		imagesList.add(di);
    		it.putExtra("result", (Serializable) imagesList);
    		index = 0;
    	}
    	it.putExtra("position", index);
    	it.putExtra("displayType", 2);
	    startActivity (it);
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
						//libCloud.Mydownload_rename(Params.RECOMMEND_PICTURE, info.getResid(), name);
						libCloud.Mydownload_rename(Params.RECOMMEND_PICTURE, info.getId(), name);
						int index = (currentPage-1)*PAGE_SIZE + info.getPosition();
						Map<String,Object> m = (Map<String,Object>)pictureList.get(index);
						m.put("resname", name);
						//videoList.add(index,m);
					} catch (WeiboException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
				GetDataTask gdt = new GetDataTask();
				gdt.execute(currentPage,PAGE_SIZE);
				
				curpopw=null;
			}});
		Button cancelbtn = (Button) popmenuWindow.findViewById(R.id.bt_cancel);
		cancelbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				curpopw=null;
			}});
		popmenu.showAtLocation(findViewById(R.id.mydownloadpicture), Gravity.CENTER, 20, -20);
	}
	
	public void showpopmenu(final DataInfo di,View view){
		if(curpopw!=null)
			curpopw.dismiss();
		System.out.println("show pop menu");
		
		View popmenuWindow = getLayoutInflater().inflate(R.layout.mydownloadpopmenu, null);

		final PopupWindow popmenu = new PopupWindow(popmenuWindow, 180, 91);
		popmenu.setFocusable(true);
		popmenu.setTouchable(true);
		popmenu.setOutsideTouchable(true);
		popmenu.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句
		curpopw= popmenu;
		//ImageButton passwordQueDing = (ImageButton) passwordWindow.findViewById(R.id.passwordqueding);
		Button playbtn = (Button) popmenuWindow.findViewById(R.id.bt_play);
		playbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				System.out.println("show pop menu: "+di.getName());
				//Toast.makeText(getApplicationContext(),(String)di.get("name"), 1000);
				popmenu.dismiss();
				execute(di);
			}});

		Button deletebtn = (Button) popmenuWindow.findViewById(R.id.bt_delete);
		deletebtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				showDelNoteDialog(di);
				
			}});

		popmenu.showAtLocation(findViewById(R.id.mydownloadpicture), Gravity.CENTER, 0, 0);
	}
	/*
	 * 显示加密对话框
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
					UIHelper.displayToast("getResources().getString(R.string.pswd_cannot_empty)",MyDownloadPictureActivity.this);
					return ;
				}
				if(pswStr.equals(pswConfirmStr)){
					try {
						Map<String,Object> result = libCloud.Set_file_password(di.getId(),pswStr);
						if(((String)result.get("code")).equals("1")){
							di.setSecurity("1");
							int index = (currentPage-1)*PAGE_SIZE + di.getPosition();
							Map<String,Object> m= pictureList.get(index);
							m.put("needpassword", "1");
							GetDataTask gdt = new GetDataTask();
							gdt.execute(currentPage,PAGE_SIZE);
							/*
							ListAdapter ad = gridView.getAdapter();
							((MyDownloadPictureDataAdapter)ad).notifyDataSetChanged();
							*/
						}else{
							UIHelper.displayToast("getResource().getString(R.string.pswd_setting_failed)",MyDownloadPictureActivity.this);
						}
					} catch (WeiboException e) {
						e.printStackTrace();
						UIHelper.displayToast(getResources().getString(R.string.pswd_setting_failed),MyDownloadPictureActivity.this);
					}
					popmenu.dismiss();
				}else{
					UIHelper.displayToast(getResources().getString(R.string.pswd_isnot_same),MyDownloadPictureActivity.this);
				}
			}});
		cancelbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				popmenu.dismiss();
				
			}});
		popmenu.showAtLocation(findViewById(R.id.mydownloadpicture), Gravity.CENTER, 0, 0);
	}
	public void onDropOutFoder(Object dragInfo){
		//System.out.println("=============================drop outside of folder ==================================");
		
		if(path!=null&&(!path.equals(""))){
			mFolderDrop.setVisibility(View.GONE);
			if(dragInfo!=null){
				DataInfo info = (DataInfo) dragInfo;
				Map<String, Object> retmap=null;
				//System.out.println("drop outside info name "+ info.getName() + "position=" + info.getPosition());
				try {
					//retmap = libCloud.Mydownload_depart(Params.RECOMMEND_MUSIC,path,info.getResid());
					retmap = libCloud.Mydownload_depart(Params.RECOMMEND_MUSIC,path,info.getId());
			
				} catch (WeiboException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MyDownloadPictureDataAdapter adapter1 = (MyDownloadPictureDataAdapter) ((CustomerGridView) mListViews.get(currentPage -1)).getAdapter();
				adapter1.getList().remove(info.getPosition());
				adapter1.notifyDataSetChanged();
			}
		}
	}
	/*删除提示对话框*/
	private void showDelNoteDialog(final DataInfo di) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MyDownloadPictureActivity.this.getParent());
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
			System.out.println("MydownloadPicture del Image id = " + di.getResid());
			libCloud.Mydownload_del(Params.RECOMMEND_PICTURE,di.getId(),di.getRemark());
			//libCloud.Mydownload_del(Params.RECOMMEND_PICTURE,di.getResid(),di.getRemark());
		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int index = (currentPage-1)*PAGE_SIZE + di.getPosition();
		pictureList.remove(index);
		
		List<DataInfo> pictureDataList = null;
		String source = di.getChildtype();
		Map<String,Object> ret =  DataHelpter.getPicList(source);
		if(ret!=null){
			pictureDataList = (List<DataInfo>) ret.get("recommend");
			DataHelpter.updatePicDownloadStatus(pictureDataList,di.getResid(),0,di.getRemark());
		}
		
		GetDataTask gdt = new GetDataTask();
		gdt.execute(currentPage,PAGE_SIZE);
		
		GetCapacityAsync capacity = new GetCapacityAsync();
		capacity.execute();
	}
	private final static int PAGE_CHANGED = 0;
    Handler myHandler = new Handler(){
    
		@Override
		public void handleMessage(Message msg) {
			String fileid ;
			switch(msg.what){
				case PAGE_CHANGED:
					int page = msg.getData().getInt("page");
					int cpage = msg.getData().getInt("cpage");
					System.out.println("hadnleMessage page = " + page + "cpage = " + cpage);
					displayImageView(page);
					break;
				default:
					break;
			}
			
			super.handleMessage(msg);
		}	
    };
}