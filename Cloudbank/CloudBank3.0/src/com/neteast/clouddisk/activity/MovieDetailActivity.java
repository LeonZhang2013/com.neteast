package com.neteast.clouddisk.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.lib.net.WeiboException;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.adapter.DetailAdapter;
import com.neteast.clouddisk.adapter.DetailEveryAdapter;
import com.neteast.clouddisk.adapter.SourceDataAdapter;
import com.neteast.clouddisk.adapter.VarietyAdapter;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.utils.MediaPlayerHelper;
import com.neteast.clouddisk.utils.OrderComparator;
import com.neteast.clouddisk.utils.UIHelper;
import com.neteast.data_acquisition.DataAcqusition;

public class MovieDetailActivity extends ActivityGroup {
	
	private LibCloud libCloud;
	private int MovieType = 1;
	private String MovieId = "0";
	private DataInfo minfo = null; 
	private PopupWindow pw = null;
	private int type = 1;// 上中下旬
	private ImageButton addtodiskbt = null;
	private ImageButton onlineplaybt = null;
	private ImageButton onlineplaydisablebt = null;
	private ImageButton onlineplayView = null;
	private Animation leftIn, rightIn;
	
	private String mDescription =null;
	private GestureDetector clickGesture;
	private GestureDetector clickGridGesture;
	private GestureDetector serialPerGridGesture;
	private GridView gvx ;
	private GridView gv ;
	private DetailAdapter da ;
	private int HDtype = 1;
	private SourceDataAdapter sourceadapter;
	private int maxLength = 100;
	private List<String> sourceTag= new ArrayList<String>();
	private List<DataInfo> seriesList = new ArrayList<DataInfo>();
	private List<DataInfo> varityList = new ArrayList<DataInfo>();
	private int total = 1;
	private int current = 1;
	private List<DataInfo> chapterList =  new ArrayList<DataInfo>();
	private VarietyAdapter va;
	private Map<String, Object> result = null;
	private TextView myear;
	private TextView mmonth;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private List<DataInfo> hdvarityList =  new ArrayList<DataInfo>();
	private DetailEveryAdapter dea;
	private String currentTag ="";
	private String crrentHDTag = "";
	private String currentChapter="";
	private HashMap<String, List<DataInfo>> seriaInfoList = new HashMap<String,List<DataInfo>>();
	private HashMap<String, List<DataInfo>> hdPerList = new HashMap<String,List<DataInfo>>();
	private ImageButton hdplaybt;
	private int titleCurrent = 1;
	private int titleTotal = 1;
	private int returnType = 0;//用于点击返回按钮判断返回哪个模块
	private int searchflag = -1;//用于判断是否是从搜索结果中进入
	private int currentPage = 1;
	private int totalPage = 1;
	private int returnCurrentPage = 1;
	private LinearLayout detailView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.movie_detail);
		
		clickGesture = new GestureDetector(new ClickGestureListener());
		clickGridGesture = new GestureDetector(new ClickGridGestureListener());
		serialPerGridGesture = new GestureDetector(new SerialGridGestureListener());
		
		libCloud = LibCloud.getInstance(this);
		leftIn = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
		rightIn = AnimationUtils.loadAnimation(this, R.anim.push_right_in);
		MovieType = this.getIntent().getIntExtra("MovieType", 1);
		returnType = this.getIntent().getIntExtra("currentIndex", 0);
		returnCurrentPage  = this.getIntent().getIntExtra("currentPage", 1);
		searchflag =  this.getIntent().getIntExtra("searchflag", -1);
		minfo  =  (DataInfo) this.getIntent().getSerializableExtra("dataInfo");
		MovieId = minfo.getResid();
		//MovieId = this.getIntent().getStringExtra("MovieId");
		System.out.println("MovieDetailActivity  MovieType = " + MovieType + "searchflag =" + searchflag);
		if(MovieType==1){
			setContentView(R.layout.movie_detail);
			maxLength = 450;
		}else if(MovieType==2){
			maxLength = 100;
			setContentView(R.layout.series_detail);
			gv = (GridView) findViewById(R.id.detailgridviewcontent1);
			gv.setOnTouchListener(new OnTouchListener(){
				@Override
				public boolean onTouch(View v, MotionEvent ev) {
					return clickGridGesture.onTouchEvent(ev);
				}
			});
			gvx= (GridView) findViewById(R.id.detailgridviewcontent2);
			gvx.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent ev) {
					return serialPerGridGesture.onTouchEvent(ev);
				}

			});
		}else if(MovieType==3){
			maxLength = 100;
			type =1;
			setContentView(R.layout.variety_detail);
			gvx= (GridView) findViewById(R.id.detailgridviewcontent);
			gvx.setOnTouchListener(new OnTouchListener(){
				@Override
				public boolean onTouch(View v, MotionEvent ev) {
					return clickGridGesture.onTouchEvent(ev);
				}
				
			});
		}else if(MovieType == 4){
			maxLength = 450;
			setContentView(R.layout.sport_detail);
		}
		//MovieId="5065";
		detailView =  (LinearLayout) findViewById(R.id.detialPageView);
		detailView.setVisibility(View.GONE);
		System.out.println("Movie Detail  MovieType = " + MovieType + "MovieId" + MovieId);
		
		ImageButton resdownloadbtn = (ImageButton) findViewById(R.id.detailresdownload);
		//resdownloadbtn.setVisibility(View.INVISIBLE);
		
		GetMovieDetailTask detailTask = new GetMovieDetailTask();
		detailTask.execute();
	
		GetHDPlayTask hdplaytask = new GetHDPlayTask();
		hdplaytask.execute();
	
	}
	private void init(Map<String, Object> result){
		this.result = result;
		System.out.println("init detail = " + result);
		if(MovieType==1){
			initMovieView(result);
		}else if(MovieType==2){
			initSeriesView(result);
			/*
			GetDataTask gdt = new GetDataTask();
			gdt.execute();
			*/
		}else if(MovieType==3){
			initVarietyView(result);
			/*
			GetDataTask gdt = new GetDataTask();
			gdt.execute();
			*/
		}else if(MovieType == 4){
			initSportsView(result);
			//GetDataTask gdt = new GetDataTask();
			//gdt.execute();
		}
		detailView.setVisibility(View.VISIBLE);
	}
	class GetMovieDetailTask extends AsyncTask <Object, Integer, Map<String, Object>> {
		@SuppressWarnings("unchecked")
		@Override
		protected Map<String, Object>  doInBackground(Object... params) {
			Map<String,Object> map = null;
			try {
				map = libCloud.Get_movie_detail(MovieId, "", 0);
				
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			
			return  map ;
		}
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			if(result!=null){
				init(result);
			}
		}
	}
	class GetDataTask extends AsyncTask<Object, Integer, List<Map<String,Object>>> {
		@SuppressWarnings("unchecked")
		@Override
		protected List<Map<String,Object>> doInBackground(Object... params) {
			List<Map<String,Object>> list = null;
			try {
				Map<String,Object>  m = libCloud.Get_detail(MovieId);
				if(m.get("code").equals("1")){
					list= (List<Map<String,Object>>)m.get("detail");
				}
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			if(list==null){
				list= new ArrayList<Map<String,Object>>();
			}
			return  list ;
		}
		@Override
		protected void onPostExecute(List<Map<String,Object>> result) {
			initHDSeriaInfoList(result);
		}
	}
	class GetHDPlayTask extends AsyncTask<Object, Integer, List<Map<String,Object>>> {
		@SuppressWarnings("unchecked")
		@Override
		protected List<Map<String,Object>> doInBackground(Object... params) {
			List<Map<String,Object>> list = null;
			Map<String,Object>  m = libCloud.get_hdplayInfo(MovieId);
			System.out.println("m = " + m);
			if(m!=null &&  m.containsKey("list")){
				list= (List<Map<String,Object>>)m.get("list");
			}
			if(list==null){
				list= new ArrayList<Map<String,Object>>();
			}
			return  list ;
		}
		@Override
		protected void onPostExecute(List<Map<String,Object>> result) {
			initHDSeriaInfoList(result);
			//inithdplay(result);
		}
	}
	/*
	 * 初始化高清电视剧信息
	 */
	private void initHDSeriaInfoList(List<Map<String,Object>> rel){
		hdplaybt = (ImageButton) findViewById(R.id.detailhdplay);
		System.out.println("hd series info  list size = " + rel.size());
		if(rel.size() <=0){
			hdplaybt.setImageResource(R.drawable.hdplay_disablebtn);
			hdplaybt.setClickable(false);
		}else{
			hdplaybt.setImageResource(R.drawable.hdplaybtn);
			hdplaybt.setClickable(true);
			if(MovieType==1){
				System.out.println("movietype = " + MovieType);
				Map<String,Object> map = rel.get(0);
				String url = (String) map.get("playurl");
				String movieid = (String)map.get("movieid");
				String videoid = (String)map.get("videoid");
				if(url!=null && url.length()>0){
					minfo.setUrl(url);
				}else{
					minfo.setUrl("");
				}
				minfo.setMovieId(movieid);
				minfo.setVideoid(videoid);
			}
			else {

			List<DataInfo> l = new ArrayList<DataInfo>();
			int index=1;
			for(int i=0;i<rel.size();i++){
				Map<String,Object> m  = (Map<String,Object>)rel.get(i);
				String title = (String) m.get("title");
				String series = (String) m.get("series");
				String playurl = (String) m.get("playurl");
				String movieid = (String)m.get("movieid");
				String videoid = (String)m.get("videoid");
				DataInfo di = new DataInfo();
				di.setUrl(playurl);
				di.setName(title);
				di.SetSeries(series);
				di.SetSeries(""+index);
				di.setMovieId(movieid);
				di.setVideoid(videoid);
				l.add(di);
				index++;
			}
			
			
			if(MovieType==3){
				hdvarityList = l;
			}else{
				if(total<Params.PER_CHAPTER_COUNT){
					hdPerList.put("1-"+total,l);
					crrentHDTag = "1-"+total;
				}else{
					crrentHDTag = "1-"+Params.PER_CHAPTER_COUNT;
					int flag = 1;
					for (int i = 1; i <= total; i++) {
						if (i % Params.PER_CHAPTER_COUNT == 0) {
							String s = ((flag - 1)
									* Params.PER_CHAPTER_COUNT + 1)
									+ "-" + flag * Params.PER_CHAPTER_COUNT;
							hdPerList.put(s, l.subList((flag - 1)
									* Params.PER_CHAPTER_COUNT, flag
									* Params.PER_CHAPTER_COUNT));
							flag++;
						}
					}
					String s = ((flag - 1) * Params.PER_CHAPTER_COUNT + 1)
							+ "-" + total;
					hdPerList.put(s, l.subList((flag - 1)
							* Params.PER_CHAPTER_COUNT, total));
				}
			}
			
			/*
			Map<String,Object> m  = (Map<String,Object>)rel.get(0);
			if(m!=null){
				String[] perUrls = ((String)m.get("huasuplayurl")).split(";");
				int total = perUrls.length;
				List<DataInfo> l = new ArrayList<DataInfo>();
				int index=1;
				for(String url:perUrls){
					DataInfo di = new DataInfo();
					di.setUrl(url);
					di.SetSeries(""+index);
					if(MovieType==3){
						//////////////////未含有分集信息标题
						di.setName("第"+index+"集");
					}
					l.add(di);
					index++;
				}
				if(MovieType==3){
					hdvarityList = l;
				}else{
					if(total<Params.PER_CHAPTER_COUNT){
						hdPerList.put("1-"+total,l);
						crrentHDTag = "1-"+total;
					}else{
						crrentHDTag = "1-"+Params.PER_CHAPTER_COUNT;
						int flag = 1;
						for (int i = 1; i <= total; i++) {
							if (i % Params.PER_CHAPTER_COUNT == 0) {
								String s = ((flag - 1)
										* Params.PER_CHAPTER_COUNT + 1)
										+ "-" + flag * Params.PER_CHAPTER_COUNT;
								hdPerList.put(s, l.subList((flag - 1)
										* Params.PER_CHAPTER_COUNT, flag
										* Params.PER_CHAPTER_COUNT));
								flag++;
							}
						}
						String s = ((flag - 1) * Params.PER_CHAPTER_COUNT + 1)
								+ "-" + total;
						hdPerList.put(s, l.subList((flag - 1)
								* Params.PER_CHAPTER_COUNT, total));
					}
				}
				
			}
			*/
			}
		}
	}
	private void inithdplay(List<Map<String,Object>> result){
		hdplaybt = (ImageButton) findViewById(R.id.detailhdplay);
		if(result.size() <=0){
			hdplaybt.setImageResource(R.drawable.hdplay_disablebtn);
			hdplaybt.setClickable(false);
		}else{
			if(MovieType==1){
				Map<String,Object> map = result.get(0);
				String url = (String) map.get("playurl");
				if(url!=null && url.length()>0){
					minfo.setUrl(url);
				}else{
					minfo.setUrl("");
				}
			}
		}
	}
	private void initControlBar(){
		hdplaybt = (ImageButton) findViewById(R.id.detailhdplay);
		onlineplaybt = (ImageButton) findViewById(R.id.detailsourceico);
		onlineplaydisablebt = (ImageButton) findViewById(R.id.detailonlineplaydisable);
		onlineplaydisablebt.setVisibility(View.GONE);
		//ImageButton downloadbt = (ImageButton) findViewById(R.id.detailresdownload);
		addtodiskbt = (ImageButton) findViewById(R.id.detailaddtodisk);
		onlineplayView = (ImageButton) findViewById(R.id.detailonlineplay);
		/*
		if(minfo.getUrl()==null ||minfo.getUrl().length()==0){
			hdplaybt.setImageResource(R.drawable.hdplay_disablebtn);
			hdplaybt.setClickable(false);
		}
		*/
		if(minfo.getStatus()==1){
			addtodiskbt.setImageResource(R.drawable.detail_downloaded);
			addtodiskbt.setClickable(false);
		}
	}
	private void initTitle(){
		ImageView img = (ImageView)findViewById(R.id.movie_cover_image);
		//final TextView title = (TextView)findViewById(R.id.movie_title);
		libCloud.DisplayImage(minfo.getImage(), img);
		//title.setText(minfo.getName());
	}
	@SuppressWarnings("unchecked")
	private void initMovieInfo(Map<String,Object> result){
		if (result == null)
			return;
		String movieId =null;
		String movieName = null;
		String Alias = null;
		String TypeId =null;
		String Drama = null;
		String Duration = null;
		String Director = null;
		String Actor = null;
		String AreaName = null;
		String PublishAge = null;
		Map<String,Object> movieList = (Map<String, Object>) result.get("movie_list");
		List<Map<String, Object>>  movieItemList = null;
		if(movieList!=null){
			movieItemList = (List<Map<String, Object>>) movieList.get("movie_item");
		}
		if(movieItemList!=null){
			Map<String, Object> movieItem = movieItemList.get(0);
			movieId = (String) movieItem.get("MovieID");
			movieName = (String) movieItem.get("MovieName");
			if(minfo.getName()!=null && minfo.getName().length()>0){
				minfo.setName(movieName);
				final TextView title = (TextView)findViewById(R.id.movie_title);
				title.setText(minfo.getName());
			}
			Alias = (String) movieItem.get("Alias");
			TypeId = (String) movieItem.get("TypeID");
			Drama = (String)movieItem.get("Drama");
			Duration = (String)movieItem.get("TimeSpan");
			Director = (String)movieItem.get("Director");
			Actor = (String)movieItem.get("Actor");
			AreaName = (String)movieItem.get("AreaName");
			PublishAge = (String)movieItem.get("PublishAge");
			mDescription = (String)movieItem.get("Description");
			System.out.println("Duration = " + Duration + "PublishAge = " + PublishAge);
			TextView directView = (TextView)findViewById(R.id.movie_director);
			if(directView!=null){
				Director = Director.replace(";"," ");
				System.out.println("Director = " + Director);
				directView.setText(Director);
			}
			TextView actorView = (TextView)findViewById(R.id.movie_starring);
			if(actorView!=null){
				Actor = Actor.replace(";", " ");
				System.out.println("Actor  = " + Actor);
				actorView.setText(Actor);
			}
			TextView areaView = (TextView)findViewById(R.id.movie_area);
			if(areaView!=null){
				areaView.setText(AreaName);
			}
			
			TextView dramaView = (TextView)findViewById(R.id.movie_type);
			if(dramaView!=null){
				Drama = Drama.replace(",", " ");
				dramaView.setText(Drama);
			}
			TextView aliasView = (TextView)findViewById(R.id.movie_alias);
			if(aliasView!=null){
				aliasView.setText(Alias);
			}
			TextView durationView = (TextView)findViewById(R.id.movie_duration);
			if(durationView!=null){
				durationView.setText(Duration+getResources().getString(R.string.minute_text));
			}
			TextView publishAgeView = (TextView)findViewById(R.id.movie_years);
			if(publishAgeView!=null){
				if(MovieType == 4){
					publishAgeView.setText(PublishAge);
				}else{
					publishAgeView.setText(PublishAge + getResources().getString(R.string.year_text));
				}
			}
			TextView contentView = (TextView)findViewById(R.id.movie_content);
			if(contentView!=null){
				contentView.setText(mDescription);
				if(mDescription.length()<maxLength){
					findViewById(R.id.movie_content_more).setVisibility(View.INVISIBLE);
				}
			}
		}
	}
	@SuppressWarnings("unchecked")
	private void initSeriesList(Map<String,Object> result){
		seriesList.clear();
		Map<String,Object> seriesMap = (Map<String, Object>) result.get("http_list");
		List<Map<String, Object>>  seriesItemList = null;
		if(seriesMap!=null){
			String Count = (String) seriesMap.get("http_count");
			int itemCount = Integer.parseInt(Count);
			if(itemCount<=0){
				//onlineplayView.setBackgroundResource(R.drawable.onlineplay_disablebtn);
				//onlineplayView.setClickable(false);
				onlineplayView.setVisibility(View.GONE);
				onlineplaybt.setVisibility(View.GONE);
				onlineplaydisablebt.setVisibility(View.VISIBLE);
				return ;
			}
			seriesItemList = (List<Map<String, Object>>) seriesMap.get("http_item");
			for(int i=0;i< seriesItemList.size();i++){
				Map<String,Object> itemHttpInfo = null;
				itemHttpInfo = (Map<String, Object>) seriesItemList.get(i);
				if(itemHttpInfo!=null){
					DataInfo info = new DataInfo();
					info.setName((String) itemHttpInfo.get("title"));
					//info.setUrl((String) itemHttpInfo.get("swfurl"));
					info.setUrl((String) itemHttpInfo.get("mp4url"));
					info.SetTag((String) itemHttpInfo.get("tag"));
					info.SetTagName((String) itemHttpInfo.get("tagname"));
					info.SetSeries((String) itemHttpInfo.get("series"));
					info.setMovieId((String) itemHttpInfo.get("mid"));
					info.setVideoid((String) itemHttpInfo.get("id"));
					if(info.getUrl()!=null && info.getUrl().length()>0){
						//System.out.println("online play item : title= " + info.getName() + "tag = " + info.GetTag() +"mp4url = " + info.getUrl() );
						seriesList.add(info);
					}
					//System.out.println("title:"+ title + "tag" + tag+ "playurl:" + playurl + "series:"+series +"swfurl:"+swfurl);
				}
			}
			if(seriesList.size()<=0){
				onlineplayView.setVisibility(View.GONE);
				onlineplaybt.setVisibility(View.GONE);
				onlineplaydisablebt.setVisibility(View.VISIBLE);
			}
		}
	}
	private void initSourceTagList(){
		sourceTag.clear();
		if(seriesList.size()>0){
			for(int i=0;i< seriesList.size();i++){
				DataInfo info = seriesList.get(i);

				if(!exist(info.GetTag())&& !info.GetTag().equals("emule")){
					sourceTag.add(info.GetTag());
				}
			}
		}
		if(sourceTag.size()>0){
			setOnlinePlayIco(sourceTag.get(0));
		}
		//电视剧初始化数据
		if(MovieType==2){
			chapterList.clear();
	    	for(DataInfo di:seriesList){
	    		if(di.GetTag().equals(sourceTag.get(0))){
	    			chapterList.add(di);
	    		}
	    	}
	    	fillPerChapter(chapterList);
	    	Set<String> s = seriaInfoList.keySet();
	    	List<String> list = new ArrayList<String>(s);
	    	//Collections.sort(list);
	    	Collections.sort(list, new OrderComparator());
	    	if(list.isEmpty()){
	    		currentChapter = "";
	    	}else{ 
	    		currentChapter = list.get(0);
	    	}
	    	int size = list.size();
	    	titleCurrent = 1;
	    	titleTotal = 1;
			totalPage = size;
			currentPage = 1;
			if (size < Params.SERIA_TITLE_COUNT) {
				da = new DetailAdapter(this, list, clickGesture, 1);
			} else {
				titleTotal = size % Params.SERIA_TITLE_COUNT == 0 ? size
						/ Params.SERIA_TITLE_COUNT : size
						/ Params.SERIA_TITLE_COUNT + 1;
				da = new DetailAdapter(this, list.subList(0,
						Params.SERIA_TITLE_COUNT), clickGesture, 1);
			}
			gv.setAdapter(da); 
			da.notifyDataSetChanged();
		}else if(MovieType==3){
			varityList.clear();
			if(sourceTag.size()==0){
				currentTag ="";
			}else{
				currentTag =sourceTag.get(0);
			}
			for (DataInfo di : seriesList) {
				if (di.GetTag().equals(currentTag)) {
					varityList.add(di);
				}
			}
			int size = varityList.size();
			total = size % Params.PER_VARITY_COUNT == 0 ? size
					/ Params.PER_VARITY_COUNT : size / Params.PER_VARITY_COUNT
					+ 1;
			if (size < Params.PER_VARITY_COUNT) {
				va = new VarietyAdapter(MovieDetailActivity.this,
						varityList.subList(0, size), clickGesture);
			} else {
				va = new VarietyAdapter(MovieDetailActivity.this,
						varityList.subList(0, Params.PER_VARITY_COUNT),
						clickGesture);
			}
			gvx.setAdapter(va);
			va.notifyDataSetChanged();
		}		
	}
	/*
	 * 初始化分集信息
	 */
	private void fillPerChapter(List<DataInfo> list){
		 seriaInfoList.clear();
		 int size = list.size();
		 if(size==0){
			 return;
		 }
		 List<DataInfo> perList = new ArrayList<DataInfo>();
		 int flag = Params.PER_CHAPTER_COUNT+1;
		 int res= 1;
		 for(int i=0;i<size;i++){
			DataInfo di = list.get(i);
			String src = di.GetSeries();
			if (src != null && !src.equals("")) {
				try {
					res = Integer.parseInt(src);
				} catch (NumberFormatException e) {
					String regex = "\\d+";
					Pattern p = Pattern.compile(regex);
					Matcher m = p.matcher(src);
					boolean flags = true;
					String rel = "";
					while (m.find() && flags) {
						rel = m.group();
						flags = false;
					}
					if (rel.equals("")) {
						continue;
					}
					res = Integer.parseInt(rel);
				}
				if (res < flag) {
					perList.add(di);
				} else {
					String t = (flag - Params.PER_CHAPTER_COUNT) + "-"
							+ (flag - 1);
					if (perList.size() != 0) {
						seriaInfoList.put(t, perList);
					}
					perList = new ArrayList<DataInfo>();
					perList.add(di);
					flag += Params.PER_CHAPTER_COUNT;
				}
			}
		}
		String t = (flag - Params.PER_CHAPTER_COUNT) + "-" + res;
		if (perList.size() != 0) {// 为空不再添加进去
			seriaInfoList.put(t, perList);
		}
	}
	/*
	 * 判断视频来源栏是否相同 
	 */
	private boolean exist(String tag){
		for(String s:sourceTag){
			if(s.equals(tag)){
				return true;
			}
		}
		return false;
	}
	/*
	 * 设置上中下旬
	 */
	public void setType(View v) {
		type = Integer.parseInt(((String) v.getTag()));
		showVarietyInfo(result);
	}
	private void initMovieView(Map<String,Object> result){
		if(result==null) return ;
		initTitle();
		initMovieInfo(result);
		initControlBar();
		initSeriesList(result);
		initSourceTagList();
	}
	private void initSeriesView(Map<String,Object> result){
		if(result == null){
			return;
		}
		initTitle();
		initMovieInfo(result);
		initControlBar();
		initSeriesList(result);
		initSourceTagList();
		List<DataInfo>  list = seriaInfoList.get(currentChapter);
		if(list==null){
			list = new ArrayList<DataInfo>();
		}
		dea = new DetailEveryAdapter(this,list,HDtype);
		gvx.setAdapter(dea);
		dea.notifyDataSetChanged();
		
	}
	private void initVarietyView(final Map<String, Object> result) {
		if(result == null){
			return;
		}
		initTitle();
		initMovieInfo(result);
		initControlBar();
		myear = (TextView) findViewById(R.id.detail_years_text);
		mmonth = (TextView) findViewById(R.id.detail_month_text);
		 final Calendar c = Calendar.getInstance(); 
		int mYear = c.get(Calendar.YEAR); // ��ȡ��ǰ���
		int mMonth = c.get(Calendar.MONTH) + 1;// ��ȡ��ǰ�·�
	    myear.setText(String.valueOf(mYear));
		if (mMonth < 10){
			mmonth.setText("0" + String.valueOf(mMonth));
		}else{
			mmonth.setText(String.valueOf(mMonth));
		}
	     ImageButton yearup = (ImageButton)findViewById(R.id.detail_year_up);
	     ImageButton yeardown = (ImageButton)findViewById(R.id.detail_year_down);
	     ImageButton monthup = (ImageButton)findViewById(R.id.detail_month_up);
	     ImageButton monthdown = (ImageButton)findViewById(R.id.detail_month_down);
	     yearup.setOnClickListener(new OnClickListener(){
	    	 public void onClick(View v) {
			 int value = Integer.parseInt ((String) myear.getText());
			 if(value< 2100){
				 value++;
				 myear.setText(String.valueOf(value));
			 }
				showVarietyInfo(result);
					
			}
	     });
	     yeardown.setOnClickListener(new OnClickListener(){
	    	 public void onClick(View v) {
	    		 int value = Integer.parseInt ((String) myear.getText());
				 if(value> 1900){
					 value--;
					 myear.setText(String.valueOf(value));
				 }
				 showVarietyInfo(result);
					
			}
	     });
	     monthup.setOnClickListener(new OnClickListener(){
	    	 public void onClick(View v) {
	    		 int value = Integer.parseInt ((String) mmonth.getText());
				 if(value < 12){
					 value++;
					 if(value<10)
						 mmonth.setText("0"+String.valueOf(value));
					 else
						 mmonth.setText(String.valueOf(value));
				 }	
				showVarietyInfo(result);
			}
	     });
	     monthdown.setOnClickListener(new OnClickListener(){
	    	 public void onClick(View v) {
	    		 int value = Integer.parseInt ((String) mmonth.getText());
				 if(value > 1){
					 value--;
					 if(value<10)
						 mmonth.setText("0" + String.valueOf(value));
					 else 
						 mmonth.setText(String.valueOf(value));
				 }
				 showVarietyInfo(result);
					
			}
	     });
	    initSeriesList(result);
		initSourceTagList();
	    showVarietyInfo(result);
	   
		
	}
	private String getDateString(){
		return myear.getText().toString()+mmonth.getText().toString();
	}
	@SuppressWarnings("unchecked")
	private void  showVarietyInfo(Map<String, Object> result){
		current = 1;
		total = 1;
		seriesList.clear();
		Date begin = new Date();
		Date middle1 = new Date();
		Date middle2 = new Date();
		Date ends = new Date();
		try {
			String nowDate = getDateString();
			begin = sdf.parse( nowDate+ "01");
			middle1 = sdf.parse(nowDate + "10");
			middle2 = sdf.parse(nowDate + "20");
			ends = sdf.parse(nowDate+ "31");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(result==null){
			System.out.println("showVarietyInfo result is NULL ");
		}else{
			System.out.println("showVarietyInfo result is not NULL!!!!!!! ");
		}
		Map<String,Object> seriesMap = (Map<String, Object>) result.get("http_list");
		List<Map<String, Object>>  seriesItemList = null;
		if(seriesMap!=null){
			String Count = (String) seriesMap.get("http_count");
			int itemCount = Integer.parseInt(Count);
			if(itemCount<=0){
				//onlineplayView.setBackgroundResource(R.drawable.onlineplay_disablebtn);
				//onlineplayView.setClickable(false);
				onlineplayView.setVisibility(View.GONE);
				onlineplaybt.setVisibility(View.GONE);
				return ;
			}
			seriesItemList = (List<Map<String, Object>>) seriesMap.get("http_item");
			for(int i=0;i< seriesItemList.size();i++){
				Map<String,Object> itemHttpInfo = null;
				itemHttpInfo = (Map<String, Object>) seriesItemList.get(i);
				if(itemHttpInfo!=null){
					String str = (String)itemHttpInfo.get("series") ;
					if(str.equals("")){
						continue;
					}
					Date d = new Date();
					try {
						d = sdf.parse(str);
					} catch (ParseException e) {
					}
		
					if (judge(d, begin, middle1, middle2, ends)) {
						DataInfo info = new DataInfo();
						info.setName((String) itemHttpInfo.get("title"));
						//info.setUrl((String) itemHttpInfo.get("swfurl"));
						info.setUrl((String)itemHttpInfo.get("mp4url"));
						info.SetTag((String) itemHttpInfo.get("tag"));
						info.SetTagName((String) itemHttpInfo.get("tagname"));
						info.setMovieId((String) itemHttpInfo.get("mid"));
						info.setVideoid((String) itemHttpInfo.get("id"));
						if(info.getUrl()!=null && info.getUrl().length()>0){
							seriesList.add(info);
						}
					}
				}
			}
		}
		varityList.clear();
    	for(DataInfo di:seriesList){
    		if(di.GetTag().equals(currentTag)){
    			varityList.add(di);
    		}
    	}
    	int size = varityList.size();
		total = size % Params.PER_VARITY_COUNT == 0 ? size
				/ Params.PER_VARITY_COUNT : size / Params.PER_VARITY_COUNT + 1;
		if (size < Params.PER_VARITY_COUNT) {
			va = new VarietyAdapter(MovieDetailActivity.this,
					varityList.subList(0, size), clickGesture);
		} else {
			va = new VarietyAdapter(MovieDetailActivity.this,
					varityList.subList(0, Params.PER_VARITY_COUNT),
					clickGesture);
		}
		gvx.setAdapter(va); 
		va.notifyDataSetChanged();
	}
	private boolean judge(Date target, Date d1, Date d2, Date d3, Date d4) {
		if (type == 1) {
			return target.before(d2) && target.after(d1);
		} else if (type == 2) {
			return target.before(d3) && target.after(d2);
		} else {
			return target.before(d4) && target.after(d3);
		}
	}
	private void initSportsView(Map<String,Object> result){
		if(result == null) return;
		initTitle();
		initControlBar();
		initMovieInfo(result);
		initSeriesList(result);
		initSourceTagList();
	}
	
	private void showDetailWin(String value,View v) {
		if (pw != null) {
			pw.dismiss();
			pw = null;
		}
		LinearLayout layout = (LinearLayout)LayoutInflater
				.from(this).inflate(R.layout.popdetailslayout, null);
		
		pw = new PopupWindow(layout);
		// pw.setFocusable(true);
		pw.setOutsideTouchable(true);
		pw.setBackgroundDrawable(new BitmapDrawable());
		
		// pw.showAsDropDown(, 0, 0);
		pw.showAtLocation(v, Gravity.CENTER, 776, 619);
		pw.update(v, 300, -50, 776, 619);
		
		TextView tv = (TextView) layout.findViewById(R.id.details);
		tv.setMovementMethod(ScrollingMovementMethod.getInstance()); 
		tv.setText(value);
		ImageButton ib = (ImageButton) layout.findViewById(R.id.popclose);
		ib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pw.dismiss();
			}
		});
	}
	private void showSourceWin(View v) {
		if (pw != null) {
			pw.dismiss();
			pw = null;
		}
		LinearLayout layout = (LinearLayout)LayoutInflater
				.from(this).inflate(R.layout.sourcepoplayout, null);
		
		pw = new PopupWindow(layout);
		pw.setFocusable(true);
		pw.setOutsideTouchable(true);
		pw.setBackgroundDrawable(new BitmapDrawable());
	
		//LinearLayout titlelnear=(LinearLayout)layout.findViewById(R.id.typegridline);
		//ViewGroup.LayoutParams lparas = titlelnear.getLayoutParams(); 
		
		GridView titlegridv=(GridView)layout.findViewById(R.id.sourcegriditem);
		
		//System.out.println("mThumbIds size " + mThumbIds.length);
	     titlegridv.setNumColumns(sourceTag.size());
		 titlegridv.setHorizontalSpacing(10);  
		 titlegridv.setStretchMode(GridView.NO_STRETCH);
		sourceadapter = new SourceDataAdapter(titlegridv.getContext(),sourceTag);  
		titlegridv.setAdapter(sourceadapter);  
		
		titlegridv.setOnItemClickListener(sourceListOnItemClick); 
		
		pw.showAtLocation(v, Gravity.TOP, 0, 0);
		pw.update(v, -150, -110, 589, 67);

	}
	
	private void setOnlinePlayIco(String tag){
   		if(tag.equals("56")){
			onlineplaybt.setImageResource(R.drawable.ico56);
			onlineplaybt.setTag(tag);
		}else if(tag.equals("6")){
			onlineplaybt.setImageResource(R.drawable.ico6);
			onlineplaybt.setTag(tag);
		}else if(tag.equals("hualu5")){
			onlineplaybt.setImageResource(R.drawable.icohualu5);
			onlineplaybt.setTag(tag);
		}else if(tag.equals("joy")){
			onlineplaybt.setImageResource(R.drawable.icojoy);
			onlineplaybt.setTag(tag);
		}else if(tag.equals("ku6")){
			onlineplaybt.setImageResource(R.drawable.icoku6);
			onlineplaybt.setTag(tag);
		}else if(tag.equals("letv")){
			onlineplaybt.setImageResource(R.drawable.icoletv);
			onlineplaybt.setTag(tag);
		}else if(tag.equals("m1905")){
			onlineplaybt.setImageResource(R.drawable.icom1905);
			onlineplaybt.setTag(tag);
		}else if(tag.equals("pptv")){
			onlineplaybt.setImageResource(R.drawable.icopptv);
			onlineplaybt.setTag(tag);
		}else if(tag.equals("qiyi")){
			onlineplaybt.setImageResource(R.drawable.icoqiyi);
			onlineplaybt.setTag(tag);
		}else if(tag.equals("qq")){
			onlineplaybt.setImageResource(R.drawable.icoqq);
			onlineplaybt.setTag(tag);
		}else if(tag.equals("sina")){
			onlineplaybt.setImageResource(R.drawable.icosina);
			onlineplaybt.setTag(tag);
		}else if(tag.equals("smgbb")){
			onlineplaybt.setImageResource(R.drawable.icosmgbb);
			onlineplaybt.setTag(tag);
		}else if(tag.equals("sohu")){
			onlineplaybt.setImageResource(R.drawable.icosohu);
			onlineplaybt.setTag(tag);
		}else if(tag.equals("tudou")){
			onlineplaybt.setImageResource(R.drawable.icotudou);
			onlineplaybt.setTag(tag);
		}else if(tag.equals("youku")){
			onlineplaybt.setImageResource(R.drawable.icoyouku);
			onlineplaybt.setTag(tag);
		}
	}
	
	AdapterView.OnItemClickListener sourceListOnItemClick = new AdapterView.OnItemClickListener() {   
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {   
        	//HashMap<String, Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
        	//String strtid=(String)item.get("typeid");
        	//Integer tid = Integer.parseInt(strtid);
        	
        	String tag = sourceTag.get(arg2);
        	//System.out.println("source item select tag = " + tag + "MovieType = " + MovieType);
        	setOnlinePlayIco(tag);
        	if(MovieType==2){
	        	chapterList.clear();
	        	for(DataInfo di:seriesList){
	        		if(di.GetTag().equals(tag)){
	        			chapterList.add(di);
	        		}
	        	}
	        	//System.out.println("chapterList size = " + chapterList.size());
	        	currentTag = tag;
	        	fillPerChapter(chapterList);
	        	Set<String> s = seriaInfoList.keySet();
		    	List<String> list = new ArrayList<String>(s);
		    	//Collections.sort(list);
		    	Collections.sort(list, new OrderComparator());
		    	if(list.isEmpty()){
		    		currentChapter = "";
		    	}else{ 
		    		currentChapter = list.get(0);
		    	}
		    	int size = list.size();
		    	titleCurrent = 1;
		    	titleTotal = 1;
		    	//System.out.println("list size = " + list.size());
				if (size < Params.SERIA_TITLE_COUNT) {
					da = new DetailAdapter(MovieDetailActivity.this, list,
							clickGesture, 1);
				} else {
					titleTotal = size % Params.SERIA_TITLE_COUNT == 0 ? size
							/ Params.SERIA_TITLE_COUNT : size
							/ Params.SERIA_TITLE_COUNT + 1;
					da = new DetailAdapter(MovieDetailActivity.this,
							list.subList(0, Params.SERIA_TITLE_COUNT),
							clickGesture, 1);
				}
				gv.setAdapter(da);
				da.notifyDataSetChanged();
				//List<DataInfo>  listx = seriaInfoList.get(1+"-"+Params.PER_CHAPTER_COUNT);
				List<DataInfo>  listx = seriaInfoList.get(list.get(0));
				//System.out.println("seriaInfoList = " + seriaInfoList);
				if(listx==null){
					listx = new ArrayList<DataInfo>();
				}
				//System.out.println("listx size = " + listx.size());
				dea = new DetailEveryAdapter(MovieDetailActivity.this,listx,HDtype);
				gvx.setAdapter(dea);
				dea.notifyDataSetChanged();
        	}else if(MovieType==3){
        		currentTag = tag;
        		showVarietyInfo(result); 
        	}
        	if(pw!=null){
        		pw.dismiss();
        	}
        }   
           
    };
    private DataInfo  getPlayUrl(String tag){
    	for(int i=0;i<seriesList.size();i++){
    		DataInfo info = seriesList.get(i);
    		//System.out.println("info : tag = " + info.GetTag() + "url = " + info.getUrl());
    		if(info.GetTag().equals(tag)){
    			return info;
    		}
    	}
    	return null;
    }
    private boolean handleonFling(MotionEvent e1, MotionEvent e2){
    	if(MovieType==3){
			List<DataInfo> l = null;
			if(HDtype==1){
				l = varityList;
			}else{
				l = hdvarityList;
			}
			if (e1.getX() - e2.getX() > Params.distance) {
				if (current >= total) {
					return true;
				}
				current++;
				int start = (current - 1) * Params.PER_VARITY_COUNT;
				int end = current * Params.PER_VARITY_COUNT;
				int size = l.size();
				if (size < Params.PER_VARITY_COUNT) {
					va = new VarietyAdapter(MovieDetailActivity.this,
							l.subList(0, size), clickGesture);
				} else {
					if (current == total) {
						va = new VarietyAdapter(MovieDetailActivity.this,
								l.subList(start, size), clickGesture);
					} else {
						va = new VarietyAdapter(MovieDetailActivity.this,
								l.subList(start, end), clickGesture);
					}
				}
				gvx.setAdapter(va);
				va.notifyDataSetChanged();
				gvx.startAnimation(leftIn);
			} else if (e2.getX() - e1.getX() > Params.distance) {
				if (current <= 1) {
					return true;
				}
				current--;
				int start = (current - 1) * Params.PER_VARITY_COUNT;
				int end = current * Params.PER_VARITY_COUNT;
				if (current == total) {
					end = l.size();
				}
				va = new VarietyAdapter(MovieDetailActivity.this, l.subList(
						start, end), clickGesture);
				gvx.setAdapter(va);
				va.notifyDataSetChanged();
				gvx.startAnimation(rightIn);
			}
		}else if(MovieType==2){
			Map<String,List<DataInfo>> m = null;
			if(HDtype==1){
				m= seriaInfoList;
			}else{
				m = hdPerList;
			}
			if (e1.getX() - e2.getX() > Params.distance) {
				if (titleCurrent >= titleTotal) {
					return true;
				}
				titleCurrent++;
				int start = (titleCurrent - 1) * Params.SERIA_TITLE_COUNT;
				int end = titleCurrent * Params.SERIA_TITLE_COUNT;
				Set<String> s = m.keySet();
		    	List<String> list = new ArrayList<String>(s);
		    	//Collections.sort(list);
		    	Collections.sort(list, new OrderComparator());
		    	int size = list.size(); 
		    	List<String> l  = null;
				if (size < Params.SERIA_TITLE_COUNT) {
					da = new DetailAdapter(MovieDetailActivity.this, list,
							clickGesture, 1);
				} else {
					if (titleCurrent == titleTotal) {
						l = list.subList(start, size);
						 
					} else {
						l = list.subList(start, end);
					}
					da = new DetailAdapter(MovieDetailActivity.this, l,
							clickGesture, 1);
				}
				gv.setAdapter(da); 
				da.notifyDataSetChanged();
				gv.startAnimation(leftIn);
				///////////////
				List<DataInfo>  listx = m.get(l.get(0));
				if(listx==null){
					listx = new ArrayList<DataInfo>();
				}
				dea = new DetailEveryAdapter(MovieDetailActivity.this,listx,HDtype);
				gvx.setAdapter(dea);
				dea.notifyDataSetChanged();
			} else if (e2.getX() - e1.getX() > Params.distance) {
				if (titleCurrent <= 1) {
					return true;
				}
				titleCurrent--;
				Set<String> s = m.keySet();
		    	List<String> list = new ArrayList<String>(s);
		    	//Collections.sort(list);
		    	Collections.sort(list, new OrderComparator());
				int start = (titleCurrent - 1) * Params.SERIA_TITLE_COUNT;
				int end = titleCurrent * Params.SERIA_TITLE_COUNT;
				if (titleCurrent == titleTotal) {
					end = list.size();
				}
				da = new DetailAdapter(MovieDetailActivity.this, list.subList(
						start, end), clickGesture, 2);
				
				gv.setAdapter(da);
				da.notifyDataSetChanged();
				gv.startAnimation(rightIn);
				// /////////
				List<String> ll = list.subList(start, end);
				List<DataInfo> listx = m.get(ll.get(ll.size() - 1));
				if (listx == null) {
					listx = new ArrayList<DataInfo>();
				}
				dea = new DetailEveryAdapter(MovieDetailActivity.this,listx,HDtype);
				gvx.setAdapter(dea);
				dea.notifyDataSetChanged();
			}
		}
		return true;
    }
	class ClickGridGestureListener implements OnGestureListener {
		@Override
		public boolean onDown(MotionEvent arg0) {
			return true;
		}
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float arg2,
				float arg3) {
			return handleonFling(e1, e2);
		}
		@Override
		public void onLongPress(MotionEvent arg0) {

		}
		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
				float arg3) {
			return false;
		}
		@Override
		public void onShowPress(MotionEvent arg0) {}

		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			return true;
		}
	}

	class SerialGridGestureListener implements OnGestureListener {
		@Override
		public boolean onDown(MotionEvent arg0) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float arg2,
				float arg3) {
			if (e1 == null || e2 == null) {
				return true;
			}
			Map<String, List<DataInfo>> m = null;
			if (HDtype == 1) {
				m = seriaInfoList;
			} else {
				m = hdPerList;
			}
			if (e1.getX() - e2.getX() > Params.distance) {
				if (currentPage >= totalPage) {
					return true;
				}
				currentPage++;
				Set<String> s = m.keySet();
				List<String> list = new ArrayList<String>(s);
				Collections.sort(list, new OrderComparator());
				dea = new DetailEveryAdapter(MovieDetailActivity.this,
						m.get(list.get(currentPage - 1)),HDtype);
				gvx.setAdapter(dea);
				gvx.setAnimation(leftIn);
				dea.notifyDataSetChanged();
				if (currentPage % Params.SERIA_TITLE_COUNT == 1) {
					handleonFling(e1, e2);
				} else {
					change(gv, (currentPage - 1) % Params.SERIA_TITLE_COUNT);
				}

			} else if (e2.getX() - e1.getX() > Params.distance) {
				if (currentPage <= 1) {
					return true;
				}
				currentPage--;
				Set<String> s = m.keySet();
				List<String> list = new ArrayList<String>(s);
				Collections.sort(list, new OrderComparator());
				dea = new DetailEveryAdapter(MovieDetailActivity.this,
						m.get(list.get(currentPage - 1)),HDtype);
				gvx.setAdapter(dea);
				gvx.setAnimation(rightIn);
				dea.notifyDataSetChanged();
				if (currentPage % Params.SERIA_TITLE_COUNT == 0) {
					handleonFling(e1, e2);
				} else {
					change(gv, (currentPage - 1) % Params.SERIA_TITLE_COUNT);
				}
			}
			return true;
		}

		private void change(GridView gv, int index) {
			for (int i = 0; i < gv.getChildCount(); i++) {
				((RelativeLayout) gv.getChildAt(i)).getChildAt(1)
						.setVisibility(View.INVISIBLE);
			}
			((RelativeLayout) gv.getChildAt(index)).getChildAt(1)
					.setVisibility(View.VISIBLE);
		}
		@Override
		public void onLongPress(MotionEvent arg0) { }

		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
				float arg3) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent arg0) {
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			return true;
		}
    	
    }
	class ClickGestureListener implements OnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return handleonFling(e1, e2);
		}

		@Override
		public void onLongPress(MotionEvent e) { }

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) { }

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			if (MovieType == 2) {
				System.out.println("MovieType = " + MovieType + "HDtype =" + HDtype);
				if (HDtype == 1) {
					String key = da.getKey();
					List<DataInfo> list = seriaInfoList.get(key);
					dea = new DetailEveryAdapter(MovieDetailActivity.this, list,HDtype);
					gvx.setAdapter(dea);
					dea.notifyDataSetChanged();
				} else {
					String key = da.getKey();
					List<DataInfo> list = hdPerList.get(key);
					dea = new DetailEveryAdapter(MovieDetailActivity.this, list,HDtype);
					dea.notifyDataSetChanged();
					gvx.setAdapter(dea);
				}
			} else if (MovieType == 3) {
				System.out.println("MovieType = " + MovieType + "HDtype =" + HDtype);
				if(HDtype == 1){
					/*
					DataInfo di = va.getDataInfo();
					Intent it = new Intent();
					it.setClass(MovieDetailActivity.this, FlashViewActivity.class);
					it.putExtra("url", di.getUrl());
					System.out.println("########播放#########" + di.getUrl());
					startActivity(it);
					*/
					DataInfo di = va.getDataInfo();
					if(di.getUrl()!=null || di.getUrl().length()>0){
						MediaPlayerHelper.play(MovieDetailActivity.this, di.getUrl(),di.getMovieId(),di.getVideoid(),"1");
						DataAcqusition.setOnlineorderRecord(di.getMovieId(), di.getVideoid(), 1);
						Log.i("Other", "detailOnlinePlay...........................");
						/*
						Intent it = new Intent(); 
				    	it.setClass(MovieDetailActivity.this,VideoPlaybackActivity.class);
				    	Uri uri = Uri.parse(di.getUrl());
				    	it.setData(uri);
				    	it.putExtra("position", 0);
				    	startActivity (it);
				    	*/
					}else{
						UIHelper.displayToast(getResources().getString(R.string.url_invalid), MovieDetailActivity.this);
					}
					
				}else if(HDtype == 2){
					DataInfo di = va.getDataInfo();
					if(di.getUrl()!=null || di.getUrl().length()>0){	
						MediaPlayerHelper.play(MovieDetailActivity.this, di.getUrl(),di.getMovieId(),di.getVideoid(),"2");
						DataAcqusition.setOnlineorderRecord(di.getMovieId(), di.getVideoid(), 2);
						Log.i("Other", "detailHDPlay...........................");
						/*
						Intent it = new Intent(); 
				    	it.setClass(MovieDetailActivity.this,VideoPlaybackActivity.class);
				    	Uri uri = Uri.parse(di.getUrl());
				    	it.setData(uri);
				    	it.putExtra("position", 0);
				    	startActivity (it);
				    	*/
					}else{
						UIHelper.displayToast(getResources().getString(R.string.url_invalid), MovieDetailActivity.this);
					}
				}
			}
			return true;
		}
	}

	/*
	 * 点击添加至网盘按钮
	 */
	public void detailAddtodiskClick(View v) {
		DownLoadAddAsync downloadadd = new DownLoadAddAsync();
		downloadadd.execute();
	}
    /*
     * 点击高清观看按钮
     */
	public void detailHDPlayClick(View v) {
		HDtype = 2;
		if(MovieType==1){
			if(minfo.getUrl()!=null && minfo.getUrl().length()>0){
				MediaPlayerHelper.play(MovieDetailActivity.this, minfo.getUrl(),minfo.getMovieId(),minfo.getVideoid(),"2");
				DataAcqusition.setOnlineorderRecord(minfo.getMovieId(), minfo.getVideoid(), 2);
				Log.i("Other", "detailHDPlay...........................");
				/*
				Intent it = new Intent(); 
		    	it.setClass(MovieDetailActivity.this,VideoPlaybackActivity.class);
		    	Uri uri = Uri.parse(minfo.getUrl());
		    	it.setData(uri);
		    	it.putExtra("position", 0);
		    	startActivity (it);
		    	*/
			}else{
				UIHelper.displayToast(getResources().getString(R.string.url_invalid), MovieDetailActivity.this);
			}
		}else if(MovieType==2){
			findViewById(R.id.commonfocus).setVisibility(View.INVISIBLE);
			findViewById(R.id.hdfocus).setVisibility(View.VISIBLE);
			onlineplaybt.setClickable(false);
			HDtype = 2;
			/////////
			Set<String> s = hdPerList.keySet();
	    	List<String> list = new ArrayList<String>(s);
	    	//Collections.sort(list);
	    	Collections.sort(list, new OrderComparator());
	    	if(list.isEmpty()){
	    		currentChapter = "";
	    	}else{ 
	    		currentChapter = list.get(0);
	    	}
	    	int size = list.size();
	    	titleCurrent = 1;
	    	titleTotal = 1;
			totalPage = size;
			currentPage = 1;
			if (size < Params.SERIA_TITLE_COUNT) {
				da = new DetailAdapter(this, list, clickGesture, 1);
			} else {
				titleTotal = size % Params.SERIA_TITLE_COUNT == 0 ? size
						/ Params.SERIA_TITLE_COUNT : size
						/ Params.SERIA_TITLE_COUNT + 1;
				da = new DetailAdapter(this, list.subList(0,
						Params.SERIA_TITLE_COUNT), clickGesture, 1);
			}
			gv.setAdapter(da); 
			da.notifyDataSetChanged();
			dea = new DetailEveryAdapter(this, hdPerList.get(currentChapter),HDtype);
			gvx.setAdapter(dea);
			dea.notifyDataSetChanged();
		}else if(MovieType==3){
			findViewById(R.id.commonfocus).setVisibility(View.INVISIBLE);
			findViewById(R.id.hdfocus).setVisibility(View.VISIBLE);
			findViewById(R.id.commonelement).setVisibility(View.GONE);
			findViewById(R.id.fillelement).setVisibility(View.VISIBLE);
			
			onlineplaybt.setClickable(false);
			HDtype = 2;
			int size = hdvarityList.size();
			current = 1;
			total = size % Params.PER_VARITY_COUNT == 0 ? size
					/ Params.PER_VARITY_COUNT : size / Params.PER_VARITY_COUNT
					+ 1;
			if (size < Params.PER_VARITY_COUNT) {
				va = new VarietyAdapter(MovieDetailActivity.this,
						hdvarityList.subList(0, size), clickGesture);
			} else {
				va = new VarietyAdapter(MovieDetailActivity.this,
						hdvarityList.subList(0, Params.PER_VARITY_COUNT),
						clickGesture);
			}
			gvx.setAdapter(va);
			va.notifyDataSetChanged();
		}else if(MovieType == 4){
			
		}
	}
	 /*
     * 点击在线观看按钮
     */
	public void detailOnlinePlayClick(View v) {
		String tag = (String) onlineplaybt.getTag();
		HDtype = 1;
		if(MovieType==1 || MovieType == 4){
			DataInfo info = getPlayUrl(tag);
			if(info.getUrl()!=null && info.getUrl().length()>0){
				MediaPlayerHelper.play(MovieDetailActivity.this, info.getUrl(),info.getMovieId(),info.getVideoid(),"1");
				DataAcqusition.setOnlineorderRecord(info.getMovieId(), info.getVideoid(), 1);
				Log.i("Other", "detailOnlinePlay...........................");
				/*
				Intent it = new Intent(); 
		    	it.setClass(MovieDetailActivity.this,VideoPlaybackActivity.class);
		    	Uri uri = Uri.parse(url);
		    	it.setData(uri);
		    	it.putExtra("position", 0);
		    	startActivity (it);
		    	*/
			}else{
				UIHelper.displayToast(getResources().getString(R.string.url_invalid), MovieDetailActivity.this);
			}
		}else if(MovieType==2){
			HDtype = 1;
			findViewById(R.id.commonfocus).setVisibility(View.VISIBLE);
			findViewById(R.id.hdfocus).setVisibility(View.INVISIBLE);
			onlineplaybt.setClickable(true);
			Set<String> s = seriaInfoList.keySet();
	    	List<String> list = new ArrayList<String>(s);
	    	//Collections.sort(list);
	    	Collections.sort(list, new OrderComparator());
	    	if(list.isEmpty()){
	    		currentChapter = "";
	    	}else{ 
	    		currentChapter = list.get(0);
	    	}
	    	int size = list.size();
	    	titleCurrent = 1;
	    	titleTotal = 1;
			totalPage = size;
			currentPage = 1;
			if (size < Params.SERIA_TITLE_COUNT) {
				da = new DetailAdapter(this, list, clickGesture, 1);
			} else {
				titleTotal = size % Params.SERIA_TITLE_COUNT == 0 ? size
						/ Params.SERIA_TITLE_COUNT : size
						/ Params.SERIA_TITLE_COUNT + 1;
				da = new DetailAdapter(this, list.subList(0,
						Params.SERIA_TITLE_COUNT), clickGesture, 1);
			}
			gv.setAdapter(da);
			da.notifyDataSetChanged();
			// ///////////////////////////////
			List<DataInfo> listx = seriaInfoList.get(1 + "-"
					+ Params.PER_CHAPTER_COUNT);
			if (listx == null) {
				listx = seriaInfoList.get("1-" + chapterList.size());
				if (listx == null) {
					listx = seriaInfoList.get("1-1");
					if (listx == null) {
						listx = new ArrayList<DataInfo>();
					}
				}
			}
			dea = new DetailEveryAdapter(this, listx,HDtype);
			gvx.setAdapter(dea);
		} else if (MovieType == 3) {
			HDtype = 1;
			findViewById(R.id.commonfocus).setVisibility(View.VISIBLE);
			findViewById(R.id.hdfocus).setVisibility(View.INVISIBLE);
			findViewById(R.id.commonelement).setVisibility(View.VISIBLE);
			findViewById(R.id.fillelement).setVisibility(View.GONE);
			onlineplaybt.setClickable(true);
			int size = varityList.size();
			total = size % Params.PER_VARITY_COUNT == 0 ? size
					/ Params.PER_VARITY_COUNT : size / Params.PER_VARITY_COUNT
					+ 1;
			if (size < Params.PER_VARITY_COUNT) {
				va = new VarietyAdapter(MovieDetailActivity.this,
						varityList.subList(0, size), clickGesture);
			} else {
				va = new VarietyAdapter(MovieDetailActivity.this,
						varityList.subList(0, Params.PER_VARITY_COUNT),
						clickGesture);
			}
			gvx.setAdapter(va);
		}
		/*
		else if(MovieType == 4){
			
		}
		*/
	}
	public void detailOnlineSelClick(View v){
		showSourceWin(v);
	}
	 /*
     * 点击资源下载按钮
     */
	public void detailResDownloadClick(View v) {
		Log.i("detailResDownloadClick", "detailResDownloadClick");
		Intent it = new Intent(); 
    	it.setClass(MovieDetailActivity.this,MiaoSouWebViewActivity.class);
    	it.putExtra("name", minfo.getName());
    	startActivity (it);
	}
	/*
	 * 点击返回按钮
	 */
	public void detailReturnClick(View v){
		if (pw != null) {
			pw.dismiss();
			pw = null;
		}
		RecommendActivity ma = (RecommendActivity)this.getParent();
		ma.getContainer().removeAllViews();
		Intent intent = new Intent(ma, RecommendVideoActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("focusIndex", returnType);
		intent.putExtra("curType", MovieType);
		intent.putExtra("currentPage", returnCurrentPage);
		intent.putExtra("searchflag", searchflag);
		View view = ma.getLocalActivityManager().startActivity("video", intent)
				.getDecorView();
		Context ctx = view.getContext();
		ma.setContext(ctx);
		LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		view.setLayoutParams(param);
		ma.getContainer().addView(view);
	}
	/*
	 * 点击分享
	 */
	public void detailShareClick(View v){
		Log.i("detailShareClick", "detailShareClick");
		
		Intent intent=new Intent("com.neteast.share");
		intent.putExtra("title", minfo.getName());
		intent.putExtra("movieid", Integer.parseInt(minfo.getResid()));
		intent.putExtra("moviename",minfo.getName());
		intent.putExtra("url", minfo.getResid()+":"+minfo.getId()+":"+MovieType + ";"+minfo.getUrl());
		intent.putExtra("picture", minfo.getImage());
		intent.putExtra("msglevel", 1);
		intent.putExtra("applicationid", 18);
		startActivity(intent);
	}
	/*
	 * 查看更多详情
	 */
	public void moreContentClick(View v){
		Log.i("moreContentClick", "moreContentClick");
		showDetailWin(mDescription,v);
	}
	@Override
	public void onDestroy()
	{
		if(pw!=null) pw.dismiss();
		super.onDestroy();
	   
	}
	@Override
	public void onPause()
	{
		if(pw!=null) pw.dismiss();
		super.onPause();
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		System.out.println("##############thisiis detail");
		if(keyCode == KeyEvent.KEYCODE_BACK){
			detailReturnClick(null); 
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	private void showDownloadedToast(int imageId){
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.mytoast,(ViewGroup) findViewById(R.id.myToast));
		   ImageView image = (ImageView) layout
		     .findViewById(R.id.tvImageToast);
		   image.setImageResource(imageId);
		   Toast toast = new Toast(getApplicationContext());
		   toast.setGravity(Gravity.CENTER, 0, 0);
		   toast.setDuration(Toast.LENGTH_LONG);
		   toast.setView(layout);
		   toast.show();
	}
	
	class DownLoadAddAsync extends AsyncTask<Object, Integer, String> {
		
		@Override
		protected String doInBackground(Object... params) {
			String code = "0";
			try {
				Map m = libCloud.Mydownload_add(Params.RECOMMEND_VIDEO, minfo.getId());
				if(m!=null){
					code = (String) m.get("code");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return code;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("1")) {	
				addtodiskbt.setImageResource(R.drawable.detail_downloaded);
				addtodiskbt.setClickable(false);
				showDownloadedToast(R.drawable.downloaded_toast);
				minfo.setStatus(1);
			}else if(result.equals("401")){
				UIHelper.showInvalidDialog(MovieDetailActivity.this.getParent().getParent(),getResources().getString(R.string.space_full));
				//UIHelper.displayToast(getResources().getString(R.string.space_full), MovieDetailActivity.this);
			}else if(result.equals("801")){
				UIHelper.showInvalidDialog(MovieDetailActivity.this.getParent().getParent(),getResources().getString(R.string.invalid_user_message));
			}
			else {
				UIHelper.displayToast(getResources().getString(R.string.download_failed_text), MovieDetailActivity.this);
			}
		}

	}
}