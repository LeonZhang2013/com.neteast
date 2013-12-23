package com.hs.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.hs.listener.PageListener;

/**
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2012-11-7
 */
public class MyPageAdapter extends FragmentStatePagerAdapter {
	private PageListener mCallback;

	public MyPageAdapter(FragmentManager fm, PageListener listener) {
		super(fm);
		mCallback = listener;
	}

	@Override
	public Fragment getItem(int arg0) {
		return PageFragment.newInstance(arg0);
	}

	@Override
	public int getCount() {
		return mCallback.getPages();
	}
	
	@Override  
    public int getItemPosition(Object object) {  
     return POSITION_NONE;  
    } 
	
}
