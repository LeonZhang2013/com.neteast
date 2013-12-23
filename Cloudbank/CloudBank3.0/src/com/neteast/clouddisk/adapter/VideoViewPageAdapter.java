package com.neteast.clouddisk.adapter;
import java.util.List;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Adapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.utils.ImageDownloader2;

public class VideoViewPageAdapter extends PagerAdapter{

	public List<View> mListViews;
	private LibCloud libCloud;
	private int mType = 0;

	public VideoViewPageAdapter(Context context,List<View> mListViews,int type) {
		this.mListViews = mListViews;
		libCloud = LibCloud.getInstance(context);
		mType = type;
	}
	
	@Override
	public int getCount() {
		return mListViews.size();
	}
	public void setAdapter(List<View> mListViews){
		this.mListViews = mListViews;
	}
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (arg1);
	}

	 /**
     * 从指定的position创建page
     *
     * @param collection ViewPager容器
     * @param position The page position to be instantiated.
     * @return 返回指定position的page，这里不需要是一个view，也可以是其他的视图容器.
     */
	@Override
	public Object instantiateItem(View collection, int position) {
		((ViewPager) collection).addView(mListViews.get(position),0);
		return mListViews.get(position);
	}

	 /**
     * 从指定的position销毁page
     * 参数同上
     */
	@Override
	public void destroyItem(View collection, int position, Object view) {
		System.out.println(" ViewPageAdapter destroyItem : position =" + position );

		
		GridView v = (GridView) mListViews.get(position);
		if(v!=null){
			VideoDataAdapter adapter1 = (VideoDataAdapter) v.getAdapter();
			if(adapter1!=null){
				synchronized(adapter1){
					for(int i=0;i<v.getCount();i++){
						View childv = v.getChildAt(i);
						if(childv!=null){
							
							ImageView imageView = (ImageView) childv.findViewById(R.id.imageView);
							if(imageView!=null){
								//System.out.println("setImageView null ");
								imageView.setImageDrawable(null);
							}
							
						}
					}
					adapter1.notifyDataSetChanged();
				}
			}
		}
		
		((ViewPager) collection).removeView(mListViews.get(position));	
		view = null;
		//System.gc();
		Runtime.getRuntime().gc();
		
		
	}
	
	 /**
     * Called when the a change in the shown pages has been completed.  At this
     * point you must ensure that all of the pages have actually been added or
     * removed from the container as appropriate.
     * @param container The containing View which is displaying this adapter's
     * page views.
     */
	@Override
	public void finishUpdate(View arg0) {}
	

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
		
	}
}
