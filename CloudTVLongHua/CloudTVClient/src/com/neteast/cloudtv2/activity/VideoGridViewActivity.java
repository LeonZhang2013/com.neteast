package com.neteast.cloudtv2.activity;

import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.neteast.cloudtv2.Constant;
import com.neteast.cloudtv2.R;
import com.neteast.cloudtv2.adapter.SecondLevelGridViewAdapter;
import com.neteast.cloudtv2.bean.VideoBean;
import com.neteast.cloudtv2.play.PlayerActivity;
import com.neteast.cloudtv2.tools.MyLog;
import com.neteast.cloudtv2.tools.NetUtils;

/**
 *
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-3-25
 */
public class VideoGridViewActivity extends Activity{

	private GridView mGridView;
	private List<VideoBean> mGridData;
	private SecondLevelGridViewAdapter mGridViewAdapter;
	private ProgressBar mProgressBar;
	private Handler mHandler;
	private final int REQUEST_GRIDDATA_THREAD_SCCUESS = 10;
	private final int REQUEST_GRIDDATA_THREAD_FAILED = 11;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_grid_layout);
		String title = getIntent().getStringExtra("title");
		((TextView)findViewById(R.id.activity_title)).setText(title);
		initLayout();
		initData();
	}
	
	private void initLayout() {
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		mGridView = (GridView) findViewById(R.id.grid_view_layout);
		mGridViewAdapter = new SecondLevelGridViewAdapter(this);
		mGridView.setAdapter(mGridViewAdapter);
		
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				VideoBean bean = mGridData.get(position);
				String playPath = Constant.DEMAND_PATH + bean.getPlayPath();
				MyLog.writeLog("bean = "+bean.getName()+"  path = "+playPath);
				Intent intent = new Intent(VideoGridViewActivity.this, PlayerActivity.class);
				intent.putExtra("playPath", playPath);
				startActivity(intent);
			}
		});
		
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				mProgressBar.setVisibility(View.GONE);
				switch (msg.what) {
				case REQUEST_GRIDDATA_THREAD_SCCUESS:
					mGridData = (List<VideoBean>) msg.obj;
					mGridViewAdapter.setData(mGridData);
					break;
				case REQUEST_GRIDDATA_THREAD_FAILED:
					Toast.makeText(getApplicationContext(), (String)msg.obj, Toast.LENGTH_LONG).show();
					break;
				}
			}
		};
	}
	
	private RequestGridDataThread mRequestGridDataThread;
	private void initData(){
		mProgressBar.setVisibility(View.VISIBLE);
		mRequestGridDataThread = new RequestGridDataThread(getIntent().getStringExtra("path"));
		mRequestGridDataThread.start();
	}
	
	class RequestGridDataThread extends Thread {

		private String path;
		private boolean isStop = false;
		public RequestGridDataThread(String path) {
			this.path =path;  
		}
		
		public void stopThread(){
			isStop = true;
		}
		@Override
		public void run() {
			List<VideoBean> listdata = null;
			Message msg = mHandler.obtainMessage();
			try {
				InputStream in = NetUtils.requestData(path);
				listdata = VideoBean.parserData(in);
				msg.what = REQUEST_GRIDDATA_THREAD_SCCUESS;
				msg.obj = listdata;
			} catch (Exception e) {
				msg.what = REQUEST_GRIDDATA_THREAD_FAILED;
				msg.obj = e.getMessage();
			}
			if(!isStop)mHandler.sendMessage(msg);
		}
	}
	
	public void onClickBack(View view) {
		this.finish();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	public void showDescrip(View view) {
		View contentView = LayoutInflater.from(this).inflate(R.layout.descrip_content, null);
		final PopupWindow pop = new PopupWindow(contentView, 475, 345, true);
		pop.setOutsideTouchable(false);
		pop.setFocusable(true);
		pop.setBackgroundDrawable(new BitmapDrawable());
		VideoBean bean = (VideoBean) view.getTag();
		ImageButton button = (ImageButton) contentView.findViewById(R.id.close_desc_btn);
		TextView title = (TextView) contentView.findViewById(R.id.desc_title_text);
		TextView content = (TextView) contentView.findViewById(R.id.desc_content_text);

		title.setText(bean.getName());
		content.setText("        " + bean.getDescrip());

		pop.showAsDropDown(view);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pop.dismiss();
			}
		});
	}

}
