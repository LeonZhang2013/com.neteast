package com.neteast.clouddisk.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.text.Html;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.lib.net.WeiboException;

import com.neteast.clouddisk.R;
import com.neteast.clouddisk.activity.RecommendPictureActivity.GetDataTask;
import com.neteast.clouddisk.activity.RecommendVideoActivity.GetMovieTypeAsync;
import com.neteast.clouddisk.adapter.NewsListAdapter;
import com.neteast.clouddisk.adapter.VideoDataAdapter;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.service.MusicUtils;
import com.neteast.clouddisk.utils.DataHelpter;
import com.neteast.clouddisk.utils.DownLoadApplication;
import com.neteast.clouddisk.utils.ImageDownloader2;
import com.neteast.clouddisk.utils.UIHelper;
import com.neteast.data_acquisition.DataAcqusition;

public class RecommendNewsActivity extends Activity {
	String[] sourceSite = new String[] { "腾讯", "搜狐", "新浪", "网易", "21CN", "奇艺" };
	String[] news = new String[] { "汽车险理赔流程指南及理赔基本常识09:46 ",
			"国外汽车召回的情况介绍15:29 ", "首推2.4L豪华版车型 上汽荣威950全系购车手册 ",
			"英媒：中美曾举行两次秘密军演全是杀手锏", "俄罗斯宣布售华苏35谈判已终止 因中国够买量少",
			"外媒称我用美技术发展尖端无人机对付美潜艇","汽车险理赔流程指南及理赔基本常识09:46 ",
			"国外汽车召回的情况介绍15:29 ", "首推2.4L豪华版车型 上汽荣威950全系购车手册 ",
			"英媒：中美曾举行两次秘密军演全是杀手锏", "俄罗斯宣布售华苏35谈判已终止 因中国够买量少",
			"外媒称我用美技术发展尖端无人机对付美潜艇" };
	LinearLayout recommendnewsView;
	LinearLayout sourceSiteLayout;
	LinearLayout todayfoucslayout;
	LinearLayout contentlayout =null ;
	private LinearLayout searchResultList;
	private LinearLayout searchResultTitle;
	private TextView currentTab;
	private TextView topnewstitle =null;
	private TextView topnewssource =null;
	private TextView topnewscontent =null;
	private TextView downnewstitle1 =null;
	private TextView downnewssource1 =null;
	private TextView downnewstitle2 =null;
	private TextView downnewssource2 =null;
	private TextView downnewstitle3 =null;
	private TextView downnewssource3 =null;
	private TextView downnewstitle4 =null;
	private TextView downnewssource4 =null;
	private TextView downnewstitle5 =null;
	private TextView downnewssource5 =null;
	private TextView downnewstitle6 =null;
	private TextView downnewssource6 =null;
	private ImageView topnewsimage=null;
	private TextView topnewsimagetitle = null;
	private ImageView todayNewsImage1 = null;
	private ImageView bottomNewsImage1 = null;
	private ImageView bottomNewsImage2 = null;
	private ImageView bottomNewsImage3 = null;
	private ImageView bottomNewsImage4 = null;
	private TextView  bottomNewsTitle1 = null;
	private TextView  bottomNewsTitle2 = null;
	private TextView  bottomNewsTitle3 = null;
	private TextView  bottomNewsTitle4 = null;

	private TextView todayNewsImage1Title = null;
	
	//private ImageDownloader2 mImageDownloader2;

	LibCloud libCloud;
	//List<Map<String, Object>> newsList = null;
	
	private List<DataInfo> searchlist = null;
	private int currentIndex = 0;
	private int searchflag = -1;
	
	public int getSelectTag(){
		return currentIndex; 
	}
	public String getSelectTagStr(){
		TextView textView = (TextView) sourceSiteLayout.getChildAt(currentIndex);
		return (String) textView.getText();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		libCloud = LibCloud.getInstance(this);
		//mImageDownloader2 = ((DownLoadApplication) getApplication()).getImageDownloader();
		setContentView(R.layout.recommendnews);
		Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY);
		sourceSiteLayout = (LinearLayout) findViewById(R.id.sourcesite);
		contentlayout = (LinearLayout) findViewById(R.id.newscontent);
		todayfoucslayout = (LinearLayout) findViewById(R.id.todayfoucsednews);
		topnewstitle =(TextView) findViewById(R.id.topnewstitle);
		topnewssource = (TextView)findViewById(R.id.topnewssource);
		searchResultTitle = (LinearLayout) findViewById(R.id.searchresultView);
		searchResultList = (LinearLayout) findViewById(R.id.search_result);
		recommendnewsView = (LinearLayout) findViewById(R.id.recommendnewsView);
		TextPaint tp = topnewstitle.getPaint(); 
		tp.setFakeBoldText(true); 
		topnewsimage = (ImageView) findViewById(R.id.topnewsimage);
		topnewsimagetitle = (TextView) findViewById(R.id.topnewsimagetitle);
		topnewscontent =(TextView) findViewById(R.id.topnewscontent);
		
		downnewstitle1 =(TextView) findViewById(R.id.downnewstitle1);
		tp = downnewstitle1.getPaint(); 
		tp.setFakeBoldText(true); 
		downnewssource1 =(TextView) findViewById(R.id.downnewssource1);
		
		downnewstitle2 =(TextView) findViewById(R.id.downnewstitle2);
		tp = downnewstitle2.getPaint(); 
		tp.setFakeBoldText(true); 
		downnewssource2 =(TextView) findViewById(R.id.downnewssource2);
		
		downnewstitle3 =(TextView) findViewById(R.id.downnewstitle3);
		tp = downnewstitle3.getPaint(); 
		tp.setFakeBoldText(true); 
		downnewssource3 =(TextView) findViewById(R.id.downnewssource3);
		
		downnewstitle4 =(TextView) findViewById(R.id.downnewstitle4);
		tp = downnewstitle4.getPaint(); 
		tp.setFakeBoldText(true); 
		downnewssource4 =(TextView) findViewById(R.id.downnewssource4);
		
		downnewstitle5 =(TextView) findViewById(R.id.downnewstitle5);
		tp = downnewstitle5.getPaint(); 
		tp.setFakeBoldText(true); 
		downnewssource5 =(TextView) findViewById(R.id.downnewssource5);
		
		downnewstitle6 =(TextView) findViewById(R.id.downnewstitle6);
		tp = downnewstitle6.getPaint(); 
		tp.setFakeBoldText(true); 
		downnewssource6 =(TextView) findViewById(R.id.downnewssource6);
		
	
		todayNewsImage1 = (ImageView) findViewById(R.id.todaynewsimage1);

		todayNewsImage1Title = (TextView)findViewById(R.id.todaynewsimage1title);
		
		bottomNewsImage1 = (ImageView) findViewById(R.id.bottomimg1);
		bottomNewsImage2 = (ImageView) findViewById(R.id.bottomimg2);
		bottomNewsImage3 = (ImageView) findViewById(R.id.bottomimg3);
		bottomNewsImage4 = (ImageView) findViewById(R.id.bottomimg4);
		bottomNewsTitle1 = (TextView)findViewById(R.id.bottomimgtitle1);
		bottomNewsTitle2 = (TextView)findViewById(R.id.bottomimgtitle2);
		bottomNewsTitle3 = (TextView)findViewById(R.id.bottomimgtitle3);
		bottomNewsTitle4 = (TextView)findViewById(R.id.bottomimgtitle4);
		
		initImageView();
		
	
		bottomNewsImage1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DataInfo info = (DataInfo) v.getTag();
				if (info != null) {
					DataAcqusition.clickDetail(info.getType(), info.getResid());
					shownewsDetails(info,searchflag);
				}
			}
		});
		bottomNewsImage2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DataInfo info = (DataInfo) v.getTag();
				if (info != null) {
					DataAcqusition.clickDetail(info.getType(), info.getResid());
					shownewsDetails(info,searchflag);
				}
			}
		});
		bottomNewsImage3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DataInfo info = (DataInfo) v.getTag();
				if (info != null) {
					DataAcqusition.clickDetail(info.getType(), info.getResid());
					shownewsDetails(info,searchflag);
				}
			}
		});
		bottomNewsImage4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DataInfo info = (DataInfo) v.getTag();
				if (info != null) {
					DataAcqusition.clickDetail(info.getType(), info.getResid());
					shownewsDetails(info,searchflag);
				}
			}
		});
		
		topnewstitle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DataInfo info = (DataInfo) v.getTag();
				if (info != null) {
					DataAcqusition.clickDetail(info.getType(), info.getResid());
					shownewsDetails(info,searchflag);
				}
			}
		});
		topnewscontent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DataInfo info = (DataInfo) v.getTag();
				if (info != null) {
					DataAcqusition.clickDetail(info.getType(), info.getResid());
					shownewsDetails(info,searchflag);
				}
			}
		});
			
		downnewstitle1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DataInfo info = (DataInfo) v.getTag();
				if (info != null) {
					DataAcqusition.clickDetail(info.getType(), info.getResid());
					shownewsDetails(info,searchflag);
				}
			}
		});
		
		downnewstitle2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DataInfo info = (DataInfo) v.getTag();
				if (info != null) {
					DataAcqusition.clickDetail(info.getType(), info.getResid());
					shownewsDetails(info,searchflag);
				}
			}
		});
		downnewstitle3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DataInfo info = (DataInfo) v.getTag();
				if (info != null) {
					DataAcqusition.clickDetail(info.getType(), info.getResid());
					shownewsDetails(info,searchflag);
				}
			}
		});
		downnewstitle4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DataInfo info = (DataInfo) v.getTag();
				if (info != null) {
					DataAcqusition.clickDetail(info.getType(), info.getResid());
					shownewsDetails(info,searchflag);
				}
			}
		});
		downnewstitle5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DataInfo info = (DataInfo) v.getTag();
				if (info != null) {
					DataAcqusition.clickDetail(info.getType(), info.getResid());
					shownewsDetails(info,searchflag);
				}
			}
		});
		downnewstitle6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DataInfo info = (DataInfo) v.getTag();
				if (null != info) {
					DataAcqusition.clickDetail(info.getType(), info.getResid());	
					shownewsDetails(info,searchflag);
				}
			}
		});

		todayNewsImage1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DataInfo info = (DataInfo) v.getTag();
				if (info != null) {
					DataAcqusition.clickDetail(info.getType(), info.getResid());
					shownewsDetails(info,searchflag);
				}
			}
		});
		
		Button returnbtn = (Button) findViewById(R.id.searchreturn);
		returnbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				recommendnewsView.setBackgroundResource(R.drawable.recommendnewsbg);
				sourceSiteLayout.setVisibility(View.VISIBLE);
				searchResultTitle.setVisibility(View.GONE);
				searchResultList.setVisibility(View.GONE);
				contentlayout.setVisibility(View.VISIBLE);	
				searchflag = -1;
				recommendStart();
				
			}
		});
		
		TextView searchresulttv = (TextView) findViewById(R.id.searchresultText);

		
		Intent i = getIntent();
		
		currentIndex = i.getIntExtra("currentIndex", 0);
		searchflag = i.getIntExtra("searchflag",-1);
		System.out.println("searchflag = " + searchflag);
		if (searchflag >= 0) {
			contentlayout.setVisibility(View.GONE);
			recommendnewsView.setBackgroundResource(R.drawable.newsdetailbg);
			searchResultTitle.setVisibility(View.VISIBLE);
			searchResultList.setVisibility(View.VISIBLE);
			sourceSiteLayout.setVisibility(View.INVISIBLE);
			currentIndex = searchflag;
			List<Map<String, Object>> newsTags = DataHelpter.getNewsTags();
			if (newsTags != null && newsTags.size() > 0) {
				initTagView(newsTags);
			}
			Intent intent = this.getIntent();
			searchlist = (List<DataInfo>) intent.getSerializableExtra("result");
			if (searchlist != null && searchlist.size() > 0) {
				addtoListView(searchlist);
				DataHelpter.setNewsSearchList(searchlist);
			} else {
				searchlist = DataHelpter.getNewsSearchList();
				if (searchlist != null) {
					addtoListView(searchlist);
				}
			}
			String value;
			if (searchlist != null && searchlist.size() > 0) {
				 value= String.format(getResources().getString(R.string.search_result_text), searchlist.size());
			
			} else {
				value = String.format(getResources().getString(R.string.search_result_text), 0);
			}
			searchresulttv.setText(value);
		
		}else{
			searchResultTitle.setVisibility(View.GONE);
			searchResultList.setVisibility(View.GONE);
			sourceSiteLayout.setVisibility(View.VISIBLE);
			contentlayout.setVisibility(View.VISIBLE);
			recommendnewsView.setBackgroundResource(R.drawable.recommendnewsbg);
			List<Map<String, Object>> newsTags = DataHelpter.getNewsTags();
			if (newsTags != null && newsTags.size() > 0) {
				initTagView(newsTags);
				recommendStart();
			} else {
				GetNewsTypeAsync newstask = new GetNewsTypeAsync();
				newstask.execute();
			}
		}

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void initImageView() {
		topnewsimage.setImageDrawable(null);
		todayNewsImage1.setImageDrawable(null);
		todayNewsImage1Title.setText("");
		bottomNewsImage1.setImageDrawable(null);
		bottomNewsImage2.setImageDrawable(null);
		bottomNewsImage3.setImageDrawable(null);
		bottomNewsImage4.setImageDrawable(null);
		bottomNewsTitle1.setText("");
		bottomNewsTitle2.setText("");
		bottomNewsTitle3.setText("");
		bottomNewsTitle4.setText("");
	}
	
	private void initTagView(List<Map<String, Object>> datatype){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(90, LinearLayout.LayoutParams.WRAP_CONTENT);
		if(datatype!=null){
			for (int i = 0; i < datatype.size(); i++) {
				TextView tv = new TextView(this);
				tv.setTag(i);
				//tv.setText(sourceSite[i]);
				tv.setText((CharSequence) datatype.get(i).get("name"));
				tv.setLayoutParams(params);
				tv.setGravity(Gravity.CENTER_HORIZONTAL);
				tv.setTextAppearance(this, R.style.tabtextstyle);
				if (i != 0) {
					params.leftMargin = 20;
				}
				tv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						currentIndex = ((Integer) v.getTag()).intValue();
						if (currentTab != v) {
							currentTab = (TextView) v;
							UIHelper.titleStyleDependState(sourceSiteLayout, RecommendNewsActivity.this, (TextView) v);
							initImageView();
							/*
							 * GetDataTask gdt = new GetDataTask();
							 * gdt.execute(currentTab);
							 */
							recommendStart();
						}
					}
				});
				System.out.println("add music type = " + (CharSequence) datatype.get(i).get("name"));
				sourceSiteLayout.addView(tv);
			}
		}
		
		if(datatype!=null && datatype.size()>0){
			TextView textView;
			textView = (TextView) sourceSiteLayout.getChildAt(currentIndex);
			textView.setTextAppearance(this, R.style.tabtextselectstyle);
			textView.setBackgroundResource(R.drawable.titlefocus);
			currentTab = textView;
			//curType = 1;
		}else{
			TextView textView = new TextView(this);
			textView.setText(sourceSite[0]);
			textView.setTag(0);
			currentTab = textView;
		}
	}
	
	private void recommendStart(){
		
		GetDataTask imagetask = new GetDataTask();
		imagetask.execute(currentTab,"1",6);
		
		GetDataTask texttask = new GetDataTask();
		texttask.execute(currentTab,"2",16);
	}
	
	/**
	 * 向搜索列表添加数据
	 * @param result
	 */
	private void addtoListView(List<DataInfo> result){
		ListView newsList =(ListView)findViewById(R.id.news_search_list);
		newsList.setOnItemClickListener(mOnItemClick);
		NewsListAdapter newsadapter = new NewsListAdapter(RecommendNewsActivity.this,result);
		newsList.setAdapter(newsadapter);
	}
	
	OnItemClickListener mOnItemClick = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			DataInfo info = searchlist.get(arg2);
			if (info != null) {
				shownewsDetails(info, searchflag);
			}
		}
	};
	
	/**
	 * 显示新闻详情
	 * @param info
	 * @param searchflag
	 */
	private void shownewsDetails(DataInfo info,int searchflag) {
		RelativeLayout rl = (RelativeLayout) this.getParent().getWindow().findViewById(R.id.datacontainer);
		rl.removeAllViews();
		if(info==null) return ;
		String id = info.getId();
		String source = info.GetTag();
		String sourceTime = info.getSourceTime();
		Intent intent = new Intent(this, NewsDetailsActivity.class);
		intent.putExtra("id", id);
		intent.putExtra("source", source);
		intent.putExtra("sourceTime", sourceTime);
		intent.putExtra("currentIndex", currentIndex);
		intent.putExtra("searchflag", searchflag);
		System.out.println("show news detail searchflag = " + searchflag);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window sub = ((ActivityGroup) this.getParent()).getLocalActivityManager().startActivity("NewsDetailsActivity", intent);
		View v = sub.getDecorView();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		v.setLayoutParams(params);
		rl.addView(v);
	}
	
	/**
	 * 此方法没被使用
	 * @param view
	 */
	public void showNewsDetails(View view) {
		RelativeLayout rl = (RelativeLayout) this.getParent().getWindow().findViewById(R.id.datacontainer);
		rl.removeAllViews();
		Intent intent = new Intent(this, NewsDetailsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window sub = ((ActivityGroup) this.getParent()).getLocalActivityManager().startActivity("NewsDetailsActivity", intent);
		View v = sub.getDecorView();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		v.setLayoutParams(params);
		rl.addView(v);

	}
	
	class GetDataTask extends AsyncTask<Object, Integer, List<DataInfo>>{
		int index = 0;
		String getImages = "0";
		@SuppressWarnings("unchecked")
		@Override
		protected List<DataInfo> doInBackground(Object... params) {
			String source;
			TextView tab = (TextView) params[0];
			getImages = (String) params[1];
			int num = (Integer) params[2];
			source = (String) tab.getText();
			index = ((Integer)tab.getTag()).intValue();
			System.out.println("RecommendNewsActivity doInBackground source :"+source + "getimage = " + getImages + "num = " + num);
			try {
				//DataHelpter.printCurTime("Recommend News request start ");
				//newsList = DataHelpter.getNewsList(source);
				List<Map<String, Object>> newsList = null;
				Map<String,Object> map = null;
				String reqtime = "";
				if (getImages.equals("1")) {
					map =  DataHelpter.getimageNewsList(source);
					if (map != null) {
						newsList = (List<Map<String, Object>>) map.get("recommend");
					}
				} else {
					map =  DataHelpter.gettextNewsList(source);
					if (map != null) {
						newsList = (List<Map<String, Object>>) map.get("recommend");
					}
				}
				if (map == null || newsList == null) {
					Map<String ,Object> map1 = libCloud.Get_recommend_list(Params.RECOMMEND_NEWS, source,"",getImages,num);
					if (map1 != null) {
						String code = (String) map1.get("code");
						if(code.equals("1")){
							newsList = (List<Map<String, Object>>) map1.get("recommend");
							reqtime = (String) map1.get("reqtime");
						}
					}
					if (getImages.equals("1")) {
						DataHelpter.setimageNewsList(newsList,source,reqtime);
					} else {
						DataHelpter.settextNewsList(newsList,source,reqtime);
					}
				} else {
					
					reqtime = (String) map.get("reqtime");
					Map<String ,Object> map1 = libCloud.Get_recommend_list(Params.RECOMMEND_NEWS, source,reqtime);
					List<Map<String, Object>> updateList = null ;
					if (map1 != null) {
						String code = (String) map1.get("code");
						if (code.equals("1")) {
							updateList = (List<Map<String, Object>>) map1.get("recommend");
							reqtime = (String) map1.get("reqtime");
						}
					}
					if (updateList == null) {
						updateList = new ArrayList<Map<String, Object>>();
					}
					Map<String,Object> map2 = null;
					if (getImages.equals("1")) {
						DataHelpter.updateimageNewsList(updateList,source,reqtime);
						map2 =  DataHelpter.getimageNewsList(source);
					} else {
						DataHelpter.updatetextNewsList(updateList,source,reqtime);
						map2 =  DataHelpter.gettextNewsList(source);
					}
					//System.out.println("map2 = " + map2);
					if (map2 != null) {
						System.out.println("get new from cache!!!");
						newsList = (List<Map<String, Object>>) map2.get("recommend");
					}
					//System.out.println("newsList = " + newsList);
				}
				Map<String, Object> m = DataHelpter.fillData(newsList,false);
				if (m == null) {
					return new ArrayList<DataInfo>();
				}
				return (List<DataInfo>)m.get("result");

			} catch (WeiboException e) {
				e.printStackTrace();
			}
			return new ArrayList<DataInfo>();
		}

		@Override
		protected void onPostExecute(List<DataInfo> result) {
			if(index == currentIndex){
				if(getImages.equals("1")){
					addDataToImageView(result);
				}else{
					addDataToTextView(result);
				}
			}
		}

	}
	
	private void addDataToImageView(List<DataInfo> result){
		int len = result.size();
		for (int j = 0; j < len; j++) {
			DataInfo datainfo = result.get(j);
			//System.out.println("add to image view resid = " + datainfo.getId());
			if (j == 0) {
				topnewsimage.setVisibility(View.VISIBLE);
				libCloud.DisplayImage(datainfo.getImage(), topnewsimage);
				//mImageDownloader2.download(datainfo.getImage(), topnewsimage);
				//topnewsimagetitle.setText(datainfo.getName());
				topnewstitle.setText(datainfo.getName());
				
				String s = datainfo.getDesc();
				//String r = s.replace("\r\n", "\n\n");
				if (s.length() > 100) {
					topnewscontent.setText(s.substring(0, 100));
				} else {
					topnewscontent.setText(s);
				}
				topnewssource.setText(datainfo.GetTag() +"  "+ datainfo.getSourceTime());
				
				topnewstitle.setTag(datainfo);
				topnewscontent.setTag(datainfo);
				
	
			} else if (j == 1) {
				bottomNewsImage1.setVisibility(View.VISIBLE);
				bottomNewsTitle1.setVisibility(View.VISIBLE);
				bottomNewsTitle1.setText(datainfo.getName());
				libCloud.DisplayImage(datainfo.getImage(), bottomNewsImage1);
				//mImageDownloader2.download(datainfo.getImage(), bottomNewsImage1);
				bottomNewsImage1.setTag(datainfo);
			} else if (j == 2) {
				bottomNewsImage2.setVisibility(View.VISIBLE);
				bottomNewsTitle2.setVisibility(View.VISIBLE);
				bottomNewsTitle2.setText(datainfo.getName());
				libCloud.DisplayImage(datainfo.getImage(), bottomNewsImage2);
				//mImageDownloader2.download(datainfo.getImage(), bottomNewsImage2);
				bottomNewsImage2.setTag(datainfo);
			} else if (j == 3) {
				bottomNewsImage3.setVisibility(View.VISIBLE);
				bottomNewsTitle3.setVisibility(View.VISIBLE);
				bottomNewsTitle3.setText(datainfo.getName());
				libCloud.DisplayImage(datainfo.getImage(), bottomNewsImage3);
				//mImageDownloader2.download(datainfo.getImage(), bottomNewsImage3);
				bottomNewsImage3.setTag(datainfo);
			} else if (j == 4) {
				bottomNewsImage4.setVisibility(View.VISIBLE);
				bottomNewsTitle4.setVisibility(View.VISIBLE);
				bottomNewsTitle4.setText(datainfo.getName());
				libCloud.DisplayImage(datainfo.getImage(), bottomNewsImage4);
				//mImageDownloader2.download(datainfo.getImage(), bottomNewsImage4);
				bottomNewsImage4.setTag(datainfo);
			} else if (j == 5) {
				todayNewsImage1Title.setVisibility(View.VISIBLE);
				todayNewsImage1.setVisibility(View.VISIBLE);
				libCloud.DisplayImage(datainfo.getImage(), todayNewsImage1);
				//mImageDownloader2.download(datainfo.getImage(), todayNewsImage1);
				todayNewsImage1Title.setText(datainfo.getName());
				todayNewsImage1.setTag(datainfo);
			}
			
		}
	}
	
	private void addDataToTextView(List<DataInfo> result){
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(280, LinearLayout.LayoutParams.WRAP_CONTENT);
		params1.leftMargin = 10;
		params1.topMargin = 10;
		params1.rightMargin = 20;


		int len = result.size();
		
		todayfoucslayout.removeAllViews();
		for (int j = 0; j < len; j++) {
			DataInfo datainfo = result.get(j);
			//System.out.println("add to text view resid = " + datainfo.getId());
			if (j == 0) {
				downnewstitle1.setText(datainfo.getName());
				downnewstitle1.setTag(datainfo);
				downnewssource1.setText(datainfo.GetTag() +"  "+ datainfo.getSourceTime());
				
			} else if (j == 1) {
				downnewstitle2.setText(datainfo.getName());
				downnewstitle2.setTag(datainfo);
				downnewssource2.setText(datainfo.GetTag() + "  "+datainfo.getSourceTime());
				
			} else if (j == 2) {
				downnewstitle3.setText(datainfo.getName());
				downnewstitle3.setTag(datainfo);
				downnewssource3.setText(datainfo.GetTag() + "  "+datainfo.getSourceTime());
				
			} else if (j == 3) {
				downnewstitle4.setText(datainfo.getName());
				downnewstitle4.setTag(datainfo);
				downnewssource4.setText(datainfo.GetTag() + "  "+datainfo.getSourceTime());
				
			} else if (j == 4) {
				downnewstitle5.setText(datainfo.getName());
				downnewstitle5.setTag(datainfo);
				downnewssource5.setText(datainfo.GetTag() + "  "+datainfo.getSourceTime());
				
			} else if (j == 5) {
				downnewstitle6.setText(datainfo.getName());
				downnewstitle6.setTag(datainfo);
				downnewssource6.setText(datainfo.GetTag() + "  "+datainfo.getSourceTime());
				
			} else {

				if (j < 22) {
					
					TextView tv = new TextView(this);
					
					tv.setText(Html.fromHtml("&#8226;" +" "+datainfo.getName()));
					//tv.setText(Html.fromHtml(datainfo.getName()));
					tv.setTextAppearance(this, R.style.newstodaycontentstyle);
					tv.setSingleLine(true);
					tv.setLayoutParams(params1);		
					tv.setEllipsize(TextUtils.TruncateAt.END);
					
					tv.setTag(datainfo);
					tv.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							DataInfo info = (DataInfo) v.getTag();
							if (info != null) {
								DataAcqusition.clickDetail(info.getType(), info.getResid());
								shownewsDetails(info,searchflag);
							}
						}
						});
		
					todayfoucslayout.addView(tv);
				}
			
			}
			
		}
	}
	
	
	public void addDataToGridView(List<DataInfo> result) {
		DataHelpter.printCurTime("Recommend News display start ");
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(280, LinearLayout.LayoutParams.WRAP_CONTENT);
		params1.leftMargin = 10;
		params1.topMargin = 10;
		params1.rightMargin = 20;


		int len = result.size();
		
		todayfoucslayout.removeAllViews();
		
		if (len < 3) {
			topnewsimage.setVisibility(View.INVISIBLE);
			topnewsimagetitle.setText("");
			topnewstitle.setText("");
			topnewscontent.setText("");

			
			downnewstitle1.setText("");
			downnewssource1.setText("");

			
			downnewstitle2.setText("");
			downnewssource2.setText("");
			
			downnewstitle3.setText("");
			downnewssource3.setText("");
			
			downnewstitle4.setText("");
			downnewssource4.setText("");
			
			downnewstitle5.setText("");
			downnewssource5.setText("");
			
			downnewstitle6.setText("");
			downnewssource6.setText("");

		}
		for (int j = 0; j < len; j++) {
			DataInfo datainfo = result.get(j);
			if (j == 0) {
				topnewsimage.setVisibility(View.VISIBLE);
				libCloud.DisplayImage(datainfo.getImage(), topnewsimage);
				//mImageDownloader2.download(datainfo.getImage(), topnewsimage);
				//topnewsimagetitle.setText(datainfo.getName());
				topnewstitle.setText(datainfo.getName());
				
				String s = datainfo.getDesc();
				//String r = s.replace("\r\n", "\n\n");
				topnewscontent.setText(s.substring(0, 100));
				topnewssource.setText(datainfo.GetTag() +"  "+ datainfo.getSourceTime());
				
				topnewstitle.setTag(datainfo);
				topnewscontent.setTag(datainfo);
				
				/*
				libCloud.DisplayImage(datainfo.getImage(), todayNewsImage1);
				libCloud.DisplayImage(datainfo.getImage(), bottomNewsImage1);
				libCloud.DisplayImage(datainfo.getImage(), bottomNewsImage2);
				libCloud.DisplayImage(datainfo.getImage(), bottomNewsImage3);
				*/
			} else if (j == 1) {
				downnewstitle1.setText(datainfo.getName());
				downnewstitle1.setTag(datainfo);
				downnewssource1.setText(datainfo.GetTag() +"  "+ datainfo.getSourceTime());
				
			} else if (j == 2) {
				downnewstitle2.setText(datainfo.getName());
				downnewstitle2.setTag(datainfo);
				downnewssource2.setText(datainfo.GetTag() + "  "+datainfo.getSourceTime());
				
			} else if (j == 3) {
				downnewstitle3.setText(datainfo.getName());
				downnewstitle3.setTag(datainfo);
				downnewssource3.setText(datainfo.GetTag() + "  "+datainfo.getSourceTime());
				
			} else if (j == 4) {
				downnewstitle4.setText(datainfo.getName());
				downnewstitle4.setTag(datainfo);
				downnewssource4.setText(datainfo.GetTag() + "  "+datainfo.getSourceTime());
				
			} else if (j == 5) {
				downnewstitle5.setText(datainfo.getName());
				downnewstitle5.setTag(datainfo);
				downnewssource5.setText(datainfo.GetTag() + "  "+datainfo.getSourceTime());
				
			} else if (j == 6) {
				downnewstitle6.setText(datainfo.getName());
				downnewstitle6.setTag(datainfo);
				downnewssource6.setText(datainfo.GetTag() + "  "+datainfo.getSourceTime());
				
			} else if (j == 7) {
				bottomNewsImage1.setVisibility(View.VISIBLE);
				bottomNewsTitle1.setVisibility(View.VISIBLE);
				bottomNewsTitle1.setText(datainfo.getName());
				libCloud.DisplayImage(datainfo.getImage(), bottomNewsImage1);
				//mImageDownloader2.download(datainfo.getImage(), bottomNewsImage1);
				bottomNewsImage1.setTag(datainfo);
			} else if (j == 8) {
				bottomNewsImage2.setVisibility(View.VISIBLE);
				bottomNewsTitle2.setVisibility(View.VISIBLE);
				bottomNewsTitle2.setText(datainfo.getName());
				libCloud.DisplayImage(datainfo.getImage(), bottomNewsImage2);
				//mImageDownloader2.download(datainfo.getImage(), bottomNewsImage2);
				bottomNewsImage2.setTag(datainfo);
			} else if (j == 9) {
				bottomNewsImage3.setVisibility(View.VISIBLE);
				bottomNewsTitle3.setVisibility(View.VISIBLE);
				bottomNewsTitle3.setText(datainfo.getName());
				libCloud.DisplayImage(datainfo.getImage(), bottomNewsImage3);
				//mImageDownloader2.download(datainfo.getImage(), bottomNewsImage3);
				bottomNewsImage3.setTag(datainfo);
			} else if (j == 10) {
				bottomNewsImage4.setVisibility(View.VISIBLE);
				bottomNewsTitle4.setVisibility(View.VISIBLE);
				bottomNewsTitle4.setText(datainfo.getName());
				libCloud.DisplayImage(datainfo.getImage(), bottomNewsImage4);
				//mImageDownloader2.download(datainfo.getImage(), bottomNewsImage4);
				bottomNewsImage4.setTag(datainfo);
			} else if (j == 11) {
				todayNewsImage1Title.setVisibility(View.VISIBLE);
				todayNewsImage1.setVisibility(View.VISIBLE);
				libCloud.DisplayImage(datainfo.getImage(), todayNewsImage1);
				//mImageDownloader2.download(datainfo.getImage(), todayNewsImage1);
				todayNewsImage1Title.setText(datainfo.getName());
				todayNewsImage1.setTag(datainfo);
			} else {
				/*
				if(j>6 && (j%3)==0){
					TextView tv = new TextView(this);
					tv.setText("");
					todayfoucslayout.addView(tv);
				}
				*/
				if (j < 22) {
					
					TextView tv = new TextView(this);
					
					tv.setText(Html.fromHtml("&#8226;" +" "+datainfo.getName()));
					//tv.setText(Html.fromHtml(datainfo.getName()));
					tv.setTextAppearance(this, R.style.newstodaycontentstyle);
					tv.setSingleLine(true);
					tv.setLayoutParams(params1);		
					tv.setEllipsize(TextUtils.TruncateAt.END);                   ;
					
					tv.setTag(datainfo);
					tv.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							DataInfo info = (DataInfo) v.getTag();
							if (null != info) {
								DataAcqusition.clickDetail(info.getType(), info.getResid());
								shownewsDetails(info,searchflag);
							}
						}
					});
		
					todayfoucslayout.addView(tv);
				}
			}
			
		}
		//DataHelpter.printCurTime("Recommend News display end");
	}
	
	@Override
	public void onDestroy() {
		//mImageDownloader2.clearCache();
		//libCloud.ClearCache();
		libCloud.ClearMemoryCache();
		super.onDestroy();
	  
	}
	
	class GetNewsTypeAsync extends AsyncTask<Object, Integer, List<Map<String, Object>> >{
		@Override
		protected List<Map<String, Object>>  doInBackground(Object... params) {
			Map<String, Object> retmap=null;
			List<Map<String, Object>> datatype = null;
			try {
				retmap = libCloud.Get_datatype(Params.RECOMMEND_NEWS);
				if (retmap != null) {
					if (retmap.get("code").equals("1")) {
						datatype = (List<Map<String, Object>>) retmap.get("datatype");
						System.out.println("music datatype = " + datatype);
					}
				}
			}
			catch (WeiboException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return datatype;
		}

		@Override
		protected void onPostExecute( List<Map<String, Object>> result) {
			DataHelpter.setNewsTags(result);
			initTagView(result);
			recommendStart();
		}
		
	}
}
