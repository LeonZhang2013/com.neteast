package com.neteast.clouddisk.activity;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextPaint;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lib.cloud.LibCloud;
import com.lib.net.WeiboException;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.utils.DownLoadApplication;
import com.neteast.clouddisk.utils.ImageDownloader2;


public class NewsDetailsActivity extends Activity {
	LibCloud libCloud;
	ImageView image1=null;
	ImageView image2=null;
	TextView title = null;
	TextView image1title = null;
	TextView image2title = null;
	TextView content = null;
	TextView sourceView = null;
	private String source=null;
	private String sourceTime = null;
	private FrameLayout image1View = null;
	private FrameLayout image2View =null;
	private int parentindex;
	private int searchflag = -1;//用于判断是否是从搜索结果中进入
	//private ImageDownloader2 mImageDownloader2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newsdetailslayout);
		libCloud = LibCloud.getInstance(this);
		//mImageDownloader2 = ((DownLoadApplication) getApplication()).getImageDownloader();
		ImageButton button = (ImageButton) findViewById(R.id.newdetailsreturn);
		image1View = (FrameLayout) findViewById(R.id.newsdetailimage1View);
		image2View = (FrameLayout) findViewById(R.id.newsdetailimage2View);		
		image1 = (ImageView) findViewById(R.id.newsdetailimage1);
		image2 = (ImageView) findViewById(R.id.newsdetailimage2);
		sourceView = (TextView) findViewById(R.id.detailnewssource);
		image1.setVisibility(View.GONE);
		image2.setVisibility(View.GONE);
		image1View.setVisibility(View.GONE);
		image2View.setVisibility(View.GONE);
		title = (TextView) findViewById(R.id.newsdetailtitle);
		TextPaint tp = title.getPaint(); 
		tp.setFakeBoldText(true); 
		image1title = (TextView) findViewById(R.id.newsdetailimage1title);
		image2title = (TextView) findViewById(R.id.newsdetailimage2title);
		content = (TextView) findViewById(R.id.newsdetailcontent);
		Intent i = getIntent();
		String id = i.getStringExtra("id");
		source= i.getStringExtra("source");
		sourceTime = i.getStringExtra("sourceTime");
		parentindex = i.getIntExtra("currentIndex", 0);
		searchflag =  i.getIntExtra("searchflag", -1);
		System.out.println("searchflag = " + searchflag);
		
		//System.out.println("NewsDetailsActivity id =" + id);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				returnBack();
				
			}
		});
		GetDetailTask detail = new GetDetailTask();
		detail.execute(id);
	}
	private void returnBack(){
		RelativeLayout rl = (RelativeLayout) NewsDetailsActivity.this.getParent().getWindow()
				.findViewById(R.id.datacontainer);
		rl.removeAllViews();
		Intent intent = new Intent(NewsDetailsActivity.this, RecommendNewsActivity.class);
		
		intent.putExtra("currentIndex", parentindex);
		intent.putExtra("searchflag", searchflag);
		
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window sub = ((ActivityGroup) NewsDetailsActivity.this.getParent())
				.getLocalActivityManager().startActivity("NewsDetailsActivity",
						intent);
		View view = sub.getDecorView();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		view.setLayoutParams(params);
		rl.addView(view);
	}
	
	class GetDetailTask extends AsyncTask<String, Integer, List<Map<String, Object>>> {

		@SuppressWarnings("unchecked")
		@Override
		protected List<Map<String, Object>> doInBackground(String... params) {
			List<Map<String, Object>> detail = null;
			Map<String, Object> retmap=null;
			try {
				retmap = (Map<String, Object>) libCloud.Get_detail(params[0]);
			
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			if(retmap!=null  && retmap.get("code").equals("1")){
				detail = (List<Map<String, Object>>) retmap.get("detail");
				return detail;
				
			}else {
				return null;
			}
			
		}

		@Override
		protected void onPostExecute(List<Map<String, Object>> detail) {
			if(detail!=null){
				showDetails(detail);
			}
		}

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			returnBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	private void showDetails(List<Map<String, Object>> detail){
		String image1url = (String) detail.get(0).get("image1");
		String image2url = (String) detail.get(0).get("image2");
		
		
		if(image1url!=null && image1url.length()>0){
			image1View.setVisibility(View.VISIBLE);
			image1.setVisibility(View.VISIBLE);
			libCloud.DisplayImage(image1url, image1);
			//mImageDownloader2.download(image1url, image1);
		}
		if(image2url!=null && image2url.length()>0){
			image2View.setVisibility(View.VISIBLE);
			image2.setVisibility(View.VISIBLE);
			libCloud.DisplayImage(image2url, image2);
			//mImageDownloader2.download(image2url, image2);
		}

		/*
		String addtime = (String) detail.get(0).get("sourcetime");
		if(addtime ==null || addtime.length()<=0){
			addtime="";
		}
		System.out.println("addtime = " + addtime);
		*/
		if(sourceTime==null ||sourceTime.length()<=0 ){
			sourceTime = "";
		}
		String s = (String) detail.get(0).get("desc1");
		String r = s.replace("\r\n", "\n\n");
		sourceView.setText(source + "  " + sourceTime);
		title.setText(Html.fromHtml( (String) detail.get(0).get("name") ));
		image1title.setText(Html.fromHtml( (String) detail.get(0).get("name") )); 
		image2title.setText(Html.fromHtml( (String) detail.get(0).get("name") )); 
		content.setMovementMethod(ScrollingMovementMethod.getInstance());  
		content.setText(r);
	}
	@Override
	public void onDestroy()
	{
		//mImageDownloader2.clearCache();
		super.onDestroy();
	  
	}
}
