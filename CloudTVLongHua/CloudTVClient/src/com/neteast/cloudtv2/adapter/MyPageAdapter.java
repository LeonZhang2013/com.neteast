package com.neteast.cloudtv2.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.neteast.cloudtv2.activity.ChannelGridViewActivity;
import com.neteast.cloudtv2.listener.PageListener;

/**
 *
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-3-25
 */
public class MyPageAdapter extends FragmentStatePagerAdapter{

	private PageListener mPageListener;
	
	public MyPageAdapter(FragmentManager fm,PageListener pageListener) {
		super(fm);
		this.mPageListener = pageListener;
	}

	@Override
	public Fragment getItem(int pageIndex) {
		return ChannelGridViewActivity.getInstance(pageIndex);
	}

	@Override
	public int getCount() {
		return mPageListener.getPages();
	}
	
	@Override  
    public int getItemPosition(Object object) {  
		return POSITION_NONE;  
    } 
}
