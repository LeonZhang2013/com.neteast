package com.neteast.videotv.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.FragmentById;
import com.googlecode.androidannotations.annotations.ViewById;
import com.neteast.videotv.R;
import com.neteast.videotv.TVApplication;
import com.neteast.videotv.fragment.HomePageFragment_;
import com.neteast.videotv.fragment.LeftNavFragment;
import com.neteast.videotv.fragment.MyVideoFragment;
import com.neteast.videotv.fragment.SearchFragment_;
import com.neteast.videotv.fragment.SearchResultFragment;
import com.neteast.videotv.fragment.SettingFragment_;
import com.neteast.videotv.fragment.TopicFragment;
import com.neteast.videotv.fragment.VideoListFragment;
import com.neteast.videotv.listener.BackPressListener;
import com.neteast.videotv.utils.ApkManager;
import com.neteast.videotv.utils.ApkManager.ApkUpdateListener;
import com.neteast.videotv.utils.MyLog;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

	@FragmentById(R.id.leftNav)
	LeftNavFragment mLeftNavFragment;
	@ViewById(R.id.start_image_layout)
	View mStartImageLaytout;
	private Fragment mCurrentFragment;

	@AfterViews
	public void showHomePage() {
		MyLog.deleteLog();
		updateApplication(false);
		new WelcomePage().Loading(mStartImageLaytout);
		replaceFragment(new HomePageFragment_());
	}

	public void showTopic() {
		replaceFragment(new TopicFragment());
	}

	public void showTopic(String name) {
		TopicFragment topicFragment = new TopicFragment();
		topicFragment.selectChannel(name);
		replaceFragment(topicFragment);
	}

	public void showChannel() {
		replaceFragment(new VideoListFragment());
	}

	public void showMyVideo() {
		replaceFragment(new MyVideoFragment());
	}

	public void showSearch() {
		replaceFragment(new SearchFragment_());
	}

	public void showSetting() {
		replaceFragment(new SettingFragment_());
	}

	@Override
	public void onBackPressed() {
		if (getWindow().getCurrentFocus() == null) {
			mLeftNavFragment.onBackPressed();
			return;
		}
		if (mLeftNavFragment.isVisible() && mLeftNavFragment.hasFocused()) {
			mLeftNavFragment.onBackPressed();
			return;
		}

		BackPressListener listener = (BackPressListener) mCurrentFragment;
		if (!listener.onBackPressed()) {
			showLeftNav();
			mLeftNavFragment.requestFocus();
		}
	}

	public void doSearch(String keyword) {
		SearchResultFragment search = SearchResultFragment.search(keyword);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.content, search);
		ft.commit();
		mCurrentFragment = search;
	}

	public void showLeftNav() {
		if (mLeftNavFragment.isVisible()) {
			return;
		}
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.show(mLeftNavFragment).setCustomAnimations(R.anim.slide_in_left,
				R.anim.slide_out_left);
		ft.commit();
	}

	public void hiddenLeftNav() {
		if (mLeftNavFragment.isHidden()) {
			return;
		}
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.hide(mLeftNavFragment).setCustomAnimations(R.anim.slide_in_left,
				R.anim.slide_out_left);
		ft.commit();
	}

	private void replaceFragment(Fragment fragment) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.content, fragment);
		ft.commit();
		mCurrentFragment = fragment;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 升级应用程序
	 */
	private void updateApplication(final boolean prompt) {
		new ApkManager(this, new ApkUpdateListener() {
			@Override
			public void onComplete(String message) {
				if (prompt) {
					Toast.makeText(getApplicationContext(), message,
							Toast.LENGTH_LONG).show();
					// Utils.showToast(mContext, message);
				}
			}
		}).startUpdate();
	}

	
	
	
	public interface MyOnTouchEvent{
		 boolean onTouchEvent(MotionEvent event);
	}
	
	private MyOnTouchEvent mMyOnTouchEvent;
	public void setMyOnTouchEvent(MyOnTouchEvent mOnTouchEvent){
		mMyOnTouchEvent = mOnTouchEvent;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mMyOnTouchEvent!=null){
			mMyOnTouchEvent.onTouchEvent(event);
		}
		return super.onTouchEvent(event);
	}

}
