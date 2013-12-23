package com.hs.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hs.params.Params;
import com.hs.utils.UIHelper;
import com.lib.appstore.LibAppstore;
import com.lib.net.WeiboException;

public class ThirdActivity extends Activity {
	LibAppstore lib;
	LinearLayout mColumns[] = new LinearLayout[3];
	LinearLayout tablayout;
	Resources res;
	DownLoadApplication download = null;
	ProgressDialog progress;
	GestureDetector clickGesture;
	View clickView;
	WindowManager wm;
	LinearLayout customeToast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.thirdlayout);

		progress = new ProgressDialog(ThirdActivity.this);
		lib = LibAppstore.getInstance(this);
		clickGesture = new GestureDetector(new ClickGestureListener());
		download = (DownLoadApplication) getApplication();
		mColumns[0] = (LinearLayout) findViewById(R.id.onecolumn);
		mColumns[1] = (LinearLayout) findViewById(R.id.twocolumn);
		mColumns[2] = (LinearLayout) findViewById(R.id.threecolumn);
		
		tablayout = (LinearLayout) this.findViewById(R.id.tablayout);
		res = this.getResources();
		wm = ThirdActivity.this.getWindowManager();
		customeToast = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.toastlayout, null);
		
		setSelect(((LinearLayout) tablayout.getChildAt(0)).getChildAt(0),0);
		setSelect(((LinearLayout) tablayout.getChildAt(1)).getChildAt(0),1);
		setSelect(((LinearLayout) tablayout.getChildAt(2)).getChildAt(0),2);
	}
	
	private void addDataLayout(int type, List<Map<String, Object>> list) {
		mColumns[type].removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			final Map<String, Object> map = (Map<String, Object>) list.get(i);
			LinearLayout linear = (LinearLayout) LayoutInflater.from(this)
			.inflate(R.layout.thirddatalayout, null);
			linear.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, 57));
			
			LinearLayout data = (LinearLayout) linear.getChildAt(0);
			data.setTag(map);
			data.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					clickView = v;
					return clickGesture.onTouchEvent(event);
				}
			});
			
			RelativeLayout elementlayout = (RelativeLayout) LayoutInflater
					.from(this).inflate(R.layout.hotelementlayout, null);
			
			ImageView num = (ImageView) elementlayout.findViewById(R.id.image);
			num.setImageResource(UIHelper.selectImage(i));
			TextView name = (TextView) elementlayout.findViewById(R.id.app_name);
			RatingBar grade = (RatingBar) elementlayout.findViewById(R.id.retings);
			TextView ver = (TextView) elementlayout.findViewById(R.id.release);
			TextView size = (TextView) elementlayout.findViewById(R.id.app_size);
			
			name.setText((String) map.get("title"));
			
			if (map.get("rating") != null) {
				grade.setRating(Float.parseFloat((String) map
						.get("rating")));
			} 
			ver.setText("版本："+(String) map.get("version"));
			size.setText("大小："+(String) map.get("size"));
			
			data.addView(elementlayout);
			mColumns[type].addView(linear);
		}
	}
	
	/**
	 * 设置某个自定义标签选中
	 * 
	 * @param view
	 */
	public void setSelect(View view ,int tag) {
		GetDataAsync gd = new GetDataAsync(tag);
		gd.execute((String)view.getTag());
	}
	

	class GetDataAsync extends AsyncTask<String, Integer, List<Map<String, Object>>> {
		private int datatype=0; //add zcy
		
		public GetDataAsync(int tag){
			datatype =tag;
		}
		@Override
		protected void onPreExecute() {
	
		}

		@Override
		protected List<Map<String, Object>> doInBackground(String... params) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			try {
				list = lib.Get_top_list(params[0], Params.TOP_OF_NUM);
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			return list;
		}

		@Override
		protected void onPostExecute(List<Map<String, Object>> result) {
			if(((DownLoadApplication)getApplication()).getCurrentTabIndex()==2){
				if(result.size()<1)//add zcy
					return;
				addDataLayout(datatype,result);
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	class ClickGestureListener implements OnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {

		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			try {
				wm.removeView(customeToast);
			} catch (Exception e2) {
			}
			Intent intent = new Intent();
			intent.setClass(ThirdActivity.this, PopWindowActivity.class);
			intent.putExtra("id", (String) ((Map<String,Object>) clickView.getTag()).get("id"));
			startActivityForResult(intent, 0);
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			//listversion(1);

		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return false;
		}

	}
	@Override
	protected void onPause() {
		super.onPause();
		try {
			wm.removeView(customeToast);
		} catch (Exception e) {
		}
	}
}
