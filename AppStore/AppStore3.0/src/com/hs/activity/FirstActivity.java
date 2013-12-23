package com.hs.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.hs.params.Params;
import com.hs.utils.NetWorkHandler;
import com.hs.utils.Tools;
import com.lib.appstore.LibAppstore;
import com.lib.net.WeiboException;

public class FirstActivity extends Activity implements OnGestureListener{


	int currentPage = 1, totalPage = 0, numperPage = 12;
	LibAppstore lib;
	ViewFlipper viewFlipper;
	View clickedView;
	GestureDetector clickGesture,slideGesture;
	int currentMaxPage = 1;// 已经使用过的最大页数
	ProgressDialog progress;
	boolean complete = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.firstlayout);
		
		/*
		boolean net = NetWorkHandler.isNetworkAvailable(this,"mngapps.wasu.com.cn:8360");//检查网络是否可用
		if(!net){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("错误信息！");
			builder.setMessage("没有网络连接，应用仓库即将退出");
			builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					FirstActivity.this.finish();
				}
			});
			builder.create().show();
		}
		*/
	
		progress = new ProgressDialog(FirstActivity.this);
		viewFlipper = (ViewFlipper) findViewById(R.id.firstViewFlipper);
		slideGesture = new GestureDetector(this);
		clickGesture = new GestureDetector(new GuestEventListener());
		lib = LibAppstore.getInstance(this);
		LinearLayout linear = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.firstdatalayout, null);
		viewFlipper.addView(linear);
		GetWonderfulListTask gslt = new GetWonderfulListTask(linear);
		gslt.execute(currentPage,numperPage);
		
		String id = null;
		Intent i = getIntent();
		id = i.getStringExtra("id");
		if(id!=null){
			Intent intent = new Intent();
			intent.setClass(FirstActivity.this, PopWindowActivity.class);
			intent.putExtra("id", id);
			startActivity(intent);
		}
		
	}


	class GetWonderfulListTask extends 
	AsyncTask<Integer, Integer, List<Map<String, Object>>>{

		private LinearLayout linear;
		public GetWonderfulListTask(LinearLayout linear) {
			this.linear = linear;
		}

		@Override
		protected void onPreExecute() {
			complete= false;
		}

		@Override
		protected List<Map<String, Object>> doInBackground(Integer... params) {
			List<Map<String, Object>> list = new 
			ArrayList<Map<String, Object>>();
			try {
				list = lib.Get_hot_list(params[0], params[1]);
				if(list.size()<1)
					return null; //add zcy
				int a,total=0 ;
				if(!list.get(0).get("total").equals(""))
					total= Integer.parseInt((String) list.get(0).get("total"));
				
				a=total /numperPage;
				if (total% numperPage != 0) {
					totalPage = a + 1;
				} else {
					totalPage = a;
				}
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			return list;
		}

		@Override
		protected void onPostExecute(List<Map<String, Object>> result) {
			if(((DownLoadApplication)getApplication()).getCurrentTabIndex()==0){
				addDataToLayout(result, linear);
			}
			complete = true;
		}

	}

	public void addDataToLayout(List<Map<String, Object>> result, LinearLayout linear) {
		int i = 0;
		int j = 0;
		if(result ==null)
			return; //add zcy
		int lastIndex = viewFlipper.getChildCount() - 1;
		LinearLayout linearLay = (LinearLayout) viewFlipper.getChildAt(lastIndex);
		for (Iterator<Map<String, Object>> iterator = result.iterator(); iterator.hasNext();) {
			RelativeLayout elementlayout = (RelativeLayout) LayoutInflater
			.from(this).inflate(R.layout.elementlayout, null);
			elementlayout.setLayoutParams(new LayoutParams(0,
					LayoutParams.WRAP_CONTENT, 1));
			elementlayout.setPadding(35, 0, 0, 0);
			ImageView iv = (ImageView) elementlayout.getChildAt(0);// ͼƬ
			Map<String, Object> map =   iterator.next();
			lib.DisplayImage((String) map.get("image"), iv);
			elementlayout.setTag(map.get("id"));
			elementlayout.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					clickedView = v;
					return clickGesture.onTouchEvent(event);
				}
			});
			LinearLayout textAndRate = (LinearLayout) elementlayout
			.getChildAt(1);
			TextView name = (TextView) textAndRate.getChildAt(0);// 名称
			TextView cate = (TextView) textAndRate.getChildAt(1);// 分类
			LinearLayout ratebarAndTimes = (LinearLayout)textAndRate.getChildAt(2);
			RatingBar rb = (RatingBar) ratebarAndTimes.getChildAt(0);
			if(map.get("rating").equals(""))
				rb.setRating(0);
			else
				rb.setRating(Float.parseFloat((String)map.get("rating")));
			//times.setText("(2256份评分)");
			name.setText((String) map.get("title"));
			cate.setText(Params.getTypeName((String) map.get("type")) + map.get("ctitle"));
			i++;
			if (i > Params.NUM_PER_ROW) {
				i = 1;
				j++;
			}
			((LinearLayout) linearLay.getChildAt(j)).addView(elementlayout);
			if (!iterator.hasNext()) {
				int z = Params.NUM_PER_ROW - i;
				for (int x = 0; x < z; x++) {
					RelativeLayout reduElement = (RelativeLayout) LayoutInflater
					.from(this)
					.inflate(R.layout.elementlayout, null);
					reduElement.removeAllViews();
					reduElement.setLayoutParams(new LayoutParams(0,
							LayoutParams.WRAP_CONTENT, 1));
					((LinearLayout) linearLay.getChildAt(j)).addView(reduElement);
				}
			}
		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return slideGesture.onTouchEvent(event);

	}


	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		//listversion(1);

	}
	

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if(e1.getX() - e2.getX() > 50){
			if(!complete){
				return true;
			}
			if (currentPage >= totalPage) {
				return true;
			}
			currentPage++;
			if(currentPage > currentMaxPage){
				currentMaxPage++;
				LinearLayout linear = (LinearLayout) LayoutInflater.from(this)
				.inflate(R.layout.firstdatalayout, null);
				viewFlipper.addView(linear);
				GetWonderfulListTask gslt = new GetWonderfulListTask(linear);
				gslt.execute(currentPage,numperPage);
			}
			viewFlipper.setInAnimation(AnimationUtils.
					loadAnimation(FirstActivity.this, R.anim.push_left_in));
			viewFlipper.setOutAnimation(AnimationUtils.
					loadAnimation(FirstActivity.this, R.anim.push_left_out));
			viewFlipper.showNext();
		}
		if(e2.getX() - e1.getX() > 50){
			if (currentPage <= 1) {
				return true;
			}
			currentPage--;
			viewFlipper.setInAnimation(AnimationUtils.
					loadAnimation(FirstActivity.this,
							R.anim.push_right_in));
			viewFlipper.setOutAnimation(AnimationUtils.
					loadAnimation(FirstActivity.this,
							R.anim.push_right_out));
			viewFlipper.showPrevious();
		}
		return false;
	}


	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		slideGesture.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	class GuestEventListener implements OnGestureListener{

		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			Intent intent = new Intent();
			intent.setClass(FirstActivity.this, PopWindowActivity.class);
			intent.putExtra("id", (String)(clickedView.getTag()));
			startActivity(intent);
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {

		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return false;
		}

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
}
