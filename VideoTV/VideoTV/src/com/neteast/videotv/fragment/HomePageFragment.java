package com.neteast.videotv.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.HorizontalScrollView;
import com.android.volley.Response;
import com.google.common.collect.Lists;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.neteast.lib.bean.VideoRaw;
import com.neteast.videotv.R;
import com.neteast.videotv.TVApplication;
import com.neteast.videotv.dao.MySQLiteOpenHelper;
import com.neteast.videotv.dao.VideoRawDao;
import com.neteast.videotv.listener.BackPressListener;
import com.neteast.videotv.listener.DPadListener;
import com.neteast.videotv.ui.MainActivity;
import com.neteast.videotv.ui.VideoDetailActivity_;
import com.neteast.videotv.ui.widget.FocusText;
import com.neteast.videotv.ui.widget.PosterView;
import com.neteast.videotv.utils.MyLog;

import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: emellend Date: 13-9-26 Time: 下午2:07 */
@EFragment(R.layout.frag_homepage)
public class HomePageFragment extends NetFragment implements BackPressListener {
	private static final String API = TVApplication.API_HOME_SCROLL_IMAGE;
	@ViewById(R.id.homepage_1_1)
	PosterView image1_1;
	@ViewById(R.id.homepage_1_2)
	FocusText image1_2;
	@ViewById(R.id.homepage_1_3)
	FocusText image1_3;
	@ViewById(R.id.homepage_1_4)
	PosterView image1_4;
	@ViewById(R.id.homepage_1_5)
	PosterView image1_5;
	@ViewById(R.id.homepage_1_6)
	PosterView image1_6;
	@ViewById(R.id.homepage_1_7)
	PosterView image1_7;

	@ViewById(R.id.homepage_2_1)
	PosterView image2_1;
	@ViewById(R.id.homepage_2_2)
	PosterView image2_2;
	@ViewById(R.id.homepage_2_3)
	FocusText image2_3;
	@ViewById(R.id.homepage_2_4)
	PosterView image2_4;
	@ViewById(R.id.homepage_2_5)
	FocusText image2_5;
	@ViewById(R.id.homepage_2_6)
	PosterView image2_6;

	@ViewById(R.id.homepage_3_1)
	PosterView image3_1;
	@ViewById(R.id.homepage_3_2)
	FocusText image3_2;
	@ViewById(R.id.homepage_3_3)
	PosterView image3_3;
	@ViewById(R.id.homepage_3_4)
	PosterView image3_4;
	@ViewById(R.id.homepage_3_5)
	PosterView image3_5;
	@ViewById(R.id.homepage_3_6)
	FocusText image3_6;
	@ViewById(R.id.homepage_3_7)
	PosterView image3_7;
	@ViewById(R.id.homepage_3_8)
	PosterView image3_8;
	@ViewById(R.id.homepage_3_9)
	PosterView image3_9;
	@ViewById(R.id.homepage_3_10)
	PosterView image3_10;

	@ViewById(R.id.scroll)
	HorizontalScrollView mScroll;
	private List<VideoRaw> mData;
	private static final int PAGE_SCROLL_DURATION = 700;
	private SQLiteDatabase db;

	@AfterViews
	void initAnimation() {
		image1_5.setDPadListener(new ScrollNextPageListener(image2_1, image2_1));
		image1_6.setDPadListener(new ScrollNextPageListener(image2_1, image2_1));
		image1_7.setDPadListener(new ScrollNextPageListener(image2_1, image2_2));

		image2_1.setDPadListener(new ScrollPrevPageListener(image1_1, image1_5));
		image2_2.setDPadListener(new ScrollPrevPageListener(image1_1, image1_7));

		image2_4.setDPadListener(new ScrollNextPageListener(image3_1, image3_1));
		image2_6.setDPadListener(new ScrollNextPageListener(image3_1, image3_3));

		image3_1.setDPadListener(new ScrollPrevPageListener(image2_1, image2_4));
		image3_2.setDpadListener(new ScrollPrevPageListener(image2_1, image2_4));
		image3_3.setDPadListener(new ScrollPrevPageListener(image2_1, image2_6));
		LoadData();
	}

	private void LoadData() {
		System.out.println("API === "+API);
		MySQLiteOpenHelper helper = new MySQLiteOpenHelper(getActivity());
		db = helper.getWritableDatabase();
		mData = Lists.newArrayList();
		mData.addAll(VideoRawDao.getAll(db));
		if (mData != null && mData.size() > 0)
			updateUI();
		executeXmlRequest(API, VideoRaw.Result.class, onLoadDataSuccess);
	}

	private void updateUI() {
		View[] views = new View[] { image1_1, image1_2, image1_3, image1_4, image1_5, image1_6, image1_7, image2_1, image2_2,
				image2_3, image2_4, image2_5, image2_6, image3_1, image3_2, image3_3, image3_4, image3_5, image3_6, image3_7,
				image3_8, image3_9, image3_10 };
		for (int i = 0; i < 23; i++) {
			VideoRaw raw = mData.get(i);
			if (raw.getMovieID() != 0) {
				PosterView image = (PosterView) views[i];
				image.setTag(raw);
				image.setOnClickListener(mImageClickListener);
				image.setTitle(raw.getTitle());
				image.setImage(raw.getImage(), getImageLoader());
				String periods = raw.getPeriods();
				try {
					String[] values = periods.split("\\*");
					image.setCount(Integer.parseInt(values[0]), Integer.parseInt(values[0]));
				} catch (Exception e) {
					image.setCount(0, 0);
				}
			} else {
				FocusText text = (FocusText) views[i];
				text.setText(raw.getTitle(), i);
				text.setTag(raw);
				text.setOnClickListener(mChannelClickListener);
			}
		}
	}

	private View.OnClickListener mImageClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			VideoRaw raw = (VideoRaw) v.getTag();
			VideoDetailActivity_.newVideoDetail(getActivity(), raw.getMovieID());
		}
	};

	private View.OnClickListener mChannelClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			VideoRaw raw = (VideoRaw) v.getTag();
			String channelName = raw.getTitle().replaceAll("#", "");
			MainActivity activity = (MainActivity) getActivity();
			activity.showTopic(channelName);
		}
	};

	@Override
	public boolean onBackPressed() {
		return false;
	}

	private class ScrollPrevPageListener extends DPadListener {

		private View anchorView;
		private View needFocusView;

		private ScrollPrevPageListener(View anchorView, View needFocusView) {
			this.anchorView = anchorView;
			this.needFocusView = needFocusView;
		}

		@Override
		public boolean onClickLeft() {
			int targetX = anchorView.getLeft();
			ObjectAnimator scrollX = ObjectAnimator.ofInt(mScroll, "scrollX", targetX);
			scrollX.setDuration(PAGE_SCROLL_DURATION);
			scrollX.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					needFocusView.requestFocus();
				}
			});
			scrollX.start();
			return true;
		}
	}

	private class ScrollNextPageListener extends DPadListener {

		private View anchorView;
		private View needFocusView;

		private ScrollNextPageListener(View anchorView, View needFocusView) {
			this.anchorView = anchorView;
			this.needFocusView = needFocusView;
		}

		@Override
		public boolean onClickRight() {
			int targetX = anchorView.getLeft();
			ObjectAnimator scrollX = ObjectAnimator.ofInt(mScroll, "scrollX", targetX);
			scrollX.setDuration(PAGE_SCROLL_DURATION);
			scrollX.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					needFocusView.requestFocus();
				}
			});
			scrollX.start();
			return true;
		}
	}

	private Response.Listener<VideoRaw.Result> onLoadDataSuccess = new Response.Listener<VideoRaw.Result>() {
		@Override
		public void onResponse(VideoRaw.Result response) {
			List<VideoRaw> videoRawList = response.getVideoRawList();
			if (!mData.equals(videoRawList)) {
				mData = videoRawList;
				VideoRawDao.deleteAll(db);
				VideoRawDao.addAll(db, mData);
				updateUI();
			} 
		}
	};
}
