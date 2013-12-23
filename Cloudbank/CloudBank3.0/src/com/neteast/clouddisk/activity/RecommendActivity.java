package com.neteast.clouddisk.activity;

import java.io.Serializable;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.lib.db.DataInfo;
import com.neteast.clouddisk.R;
public class RecommendActivity extends ActivityGroup {
	ImageButton settingButton;
	ImageButton videoButton, musiceButton, pictureButton, appbutton, newsButton;
	ImageView videoselect, musicselect, picutrueselect, appselect, newsselect;
	RelativeLayout container;
	private Context ctx;
	
	private String  curActivity="video";
	private int selectIndex = 1;
	
	public int getSelectIndex() {
		return selectIndex;
	}

	public void setSelectIndex(int selectIndex) {
		this.selectIndex = selectIndex;
	}
	public void setContext(Context context){
		ctx = context;
	}
	
	public int getSelectTag() {
		int tag = 0;
		if (ctx != null) {
			if (selectIndex == 1) {
				RecommendVideoActivity context = (RecommendVideoActivity) ctx;
				tag = context.getSelectTag();
			} else if (selectIndex == 2) {
				RecommendMusicActivity context = (RecommendMusicActivity) ctx;
				tag = context.getSelectTag();
			} else if (selectIndex == 3) {
				RecommendPictureActivity context = (RecommendPictureActivity) ctx;
				tag = context.getSelectTag();
			} else if (selectIndex == 4) {
				RecommendAppActivity context = (RecommendAppActivity) ctx;
				tag = context.getSelectTag();
			} else if (selectIndex == 5) {
				RecommendNewsActivity context = (RecommendNewsActivity) ctx;
				tag = context.getSelectTag();
			}
			// tag =
		}
		return tag;
	}
	
	public String getSelectTagStr() {
		String tag = "";
		if (ctx != null) {
			if (selectIndex == 1) {
				RecommendVideoActivity context = (RecommendVideoActivity) ctx;
				tag = context.getSelectTagStr();
			} else if (selectIndex == 2) {
				RecommendMusicActivity context = (RecommendMusicActivity) ctx;
				tag = context.getSelectTagStr();
			} else if (selectIndex == 3) {
				RecommendPictureActivity context = (RecommendPictureActivity) ctx;
				tag = context.getSelectTagStr();
			} else if (selectIndex == 4) {
				RecommendAppActivity context = (RecommendAppActivity) ctx;
				tag = context.getSelectTagStr();
			} else if (selectIndex == 5) {
				RecommendNewsActivity context = (RecommendNewsActivity) ctx;
				tag = context.getSelectTagStr();
			}
			// tag =
		}
		return tag;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommend);
		
		videoButton = (ImageButton) findViewById(R.id.videobutton);
		musiceButton = (ImageButton) findViewById(R.id.musicbutton);
		pictureButton = (ImageButton) findViewById(R.id.picturepbutton);
		appbutton = (ImageButton) findViewById(R.id.appbutton);
		newsButton = (ImageButton) findViewById(R.id.newsbutton);
		settingButton = (ImageButton) findViewById(R.id.settingbutton);
		videoselect = (ImageView) findViewById(R.id.cateselectimage1);
		musicselect = (ImageView) findViewById(R.id.cateselectimage2);
		picutrueselect = (ImageView) findViewById(R.id.cateselectimage3);
		appselect = (ImageView) findViewById(R.id.cateselectimage4);
		newsselect = (ImageView) findViewById(R.id.cateselectimage5);
		container = (RelativeLayout) findViewById(R.id.datacontainer);
		final LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
//		container.addView(getLocalActivityManager()
//				.startActivity("game", new Intent(PublicSpaceActivity.this, PublicVideoViewer.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//				.getDecorView());
		
		videoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectIndex=1;
				displaySelectButton(videoselect, musicselect, newsselect, picutrueselect, appselect);
				getLocalActivityManager().destroyActivity(curActivity, true); 
				container.removeAllViews();
				View view = getLocalActivityManager()
						.startActivity("video", new Intent(RecommendActivity.this, RecommendVideoActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();
				ctx = view.getContext();
				view.setLayoutParams(param);
				container.addView(view);
				curActivity="video";
			}
		});
		musiceButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectIndex = 2;
				displaySelectButton(musicselect, newsselect, picutrueselect, videoselect, appselect);
				getLocalActivityManager().destroyActivity(curActivity, true);  
				container.removeAllViews();
				View view = getLocalActivityManager()
						.startActivity("music", new Intent(RecommendActivity.this, RecommendMusicActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();
				ctx = view.getContext();
				view.setLayoutParams(param);
				container.addView(view);
				curActivity="music"; 
			}
		});
		pictureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectIndex = 3;
				displaySelectButton(picutrueselect, musicselect, newsselect, videoselect, appselect);
				getLocalActivityManager().destroyActivity(curActivity, true); 
				container.removeAllViews();
				View view = getLocalActivityManager()
						.startActivity("picture", new Intent(RecommendActivity.this, RecommendPictureActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();
				ctx = view.getContext();
				view.setLayoutParams(param);
				container.addView(view);
				curActivity="picture"; 
			}
		});
		appbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectIndex = 4;
				displaySelectButton(appselect, picutrueselect, musicselect,videoselect, newsselect);
				getLocalActivityManager().destroyActivity(curActivity, true); 
				container.removeAllViews();
				View view = getLocalActivityManager()
						.startActivity("app", new Intent(RecommendActivity.this, RecommendAppActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();
				ctx = view.getContext();
				container.addView(view);
				curActivity="app"; 
			}
		});
		newsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectIndex = 5;
				displaySelectButton(newsselect, picutrueselect, musicselect, videoselect, appselect);
				getLocalActivityManager().destroyActivity(curActivity, true); 
				container.removeAllViews();
				View view = getLocalActivityManager()
						.startActivity("news", new Intent(RecommendActivity.this, RecommendNewsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();
				view.setLayoutParams(param);
				ctx = view.getContext();
				container.addView(view);
				curActivity="news"; 
			}
		});
		settingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectIndex=6;
				getLocalActivityManager().destroyActivity(curActivity, true); 
				container.removeAllViews();
				View view = getLocalActivityManager()
						.startActivity("settings", new Intent(RecommendActivity.this, SettingActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();
				view.setLayoutParams(param);
				container.addView(view);
				curActivity="settings";
			}
		});
		
		if ("com.neteast.clouddisk.activity.music.widget".equals(getIntent().getAction())) {
			displaySelectButton(musicselect, videoselect, picutrueselect, appselect, newsselect);
			View view = getLocalActivityManager()
					.startActivity("music", new Intent(RecommendActivity.this, RecommendMusicActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
					.getDecorView();
			ctx = view.getContext();
			view.setLayoutParams(param);
			container.addView(view);
			curActivity="music"; 
		} else if("com.neteast.clouddisk.activity.picture.widget".equals(getIntent().getAction())) {
			displaySelectButton(picutrueselect, videoselect, musicselect, appselect, newsselect);
			View view = getLocalActivityManager()
					.startActivity("picture", new Intent(RecommendActivity.this, RecommendPictureActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
					.getDecorView();
			ctx = view.getContext();
			view.setLayoutParams(param);
			container.addView(view);
			curActivity="picture"; 
		} else {
			View view = getLocalActivityManager()
					.startActivity("video",  new Intent(RecommendActivity.this, RecommendVideoActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
					.getDecorView();
			view.setLayoutParams(param);
			ctx = view.getContext();
			container.addView(view);
			curActivity="video"; 
		}
		
		Intent i = getIntent();
		DataInfo info = (DataInfo) i.getSerializableExtra("dataInfo");
		if (info != null) {
			if (Integer.parseInt(info.getResid()) > 0) {
				int MovieType = Integer.parseInt(info.getType());
				startDetailActivity(MovieType, 0, info, 1, -1);
			}
		}
		
		//startVideoPlayer("http://218.108.168.41/1_322.mp4");
	}

	private void startVideoPlayer(String url) {
		Intent it = new Intent();
		it.setClass(RecommendActivity.this, VideoPlaybackActivity.class);
		Uri uri = Uri.parse(url);
		it.setData(uri);
		it.putExtra("position", 0);
		startActivity(it);
	}
	 
	public void startDetailActivity(int type, int index, DataInfo info, int currentPage, int searchflag) {
		
		final LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		container.removeAllViews();
			
		Intent intent = new Intent(this, MovieDetailActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("MovieType", type);
		intent.putExtra("currentIndex", index);
		intent.putExtra("currentPage", currentPage);
		intent.putExtra("searchflag", searchflag);
		intent.putExtra("dataInfo",(Serializable)info);	
		View view = getLocalActivityManager().startActivity("MovieDetail", intent).getDecorView();
		view.setLayoutParams(param);
		container.addView(view);
	}
	
	public void startSearchResult(int tag, Intent data) {
		final LayoutParams param = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		getLocalActivityManager().destroyActivity(curActivity, true); 
		container.removeAllViews();
		Class cls = null;
		String selectName = "video";	
		switch (selectIndex) {
			case 1:
				cls = RecommendVideoActivity.class;
				selectName = "video";
				break;
			case 2:
				cls = RecommendMusicActivity.class;
				selectName = "music";
				break;
			case 3:
				cls = RecommendPictureActivity.class;
				selectName = "picture";
				break;
			case 4:
				cls = RecommendAppActivity.class;
				selectName = "app";
				break;
			case 5:
				cls = RecommendNewsActivity.class;
				selectName = "news" ;
				break;
			}
		Intent intent = new Intent(RecommendActivity.this, cls);
		intent.putExtra("result", data.getSerializableExtra("result"));
		intent.putExtra("searchflag", tag);
		View view = getLocalActivityManager().startActivity(selectName, intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
		ctx = view.getContext();
		view.setLayoutParams(param);
		ctx = view.getContext();
		container.addView(view);
		curActivity=selectName; 
	}
    
	public RelativeLayout getContainer() {
		return container;
	}



	public void setContainer(RelativeLayout container) {
		this.container = container;
	}

	/**
	 * 显示或隐藏选中图片
	 * 
	 * @param v1
	 * @param v2
	 * @param v3
	 */
	private void displaySelectButton(ImageView v1, ImageView v2, ImageView v3, ImageView v4, ImageView v5) {
		v1.setVisibility(View.VISIBLE);
		v2.setVisibility(View.INVISIBLE);
		v3.setVisibility(View.INVISIBLE);
		v4.setVisibility(View.INVISIBLE);
		v5.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		System.out.println("##############this is recommend");
		if(keyCode == KeyEvent.KEYCODE_BACK){
			 return false;
		} 
		return super.onKeyDown(keyCode, event); 
	} 
}