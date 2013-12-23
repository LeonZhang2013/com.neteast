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
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.lib.cloud.LibCloud;
import com.lib.db.DataInfo;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.utils.ImageDownloader2;

public class ViewPageAdapter extends PagerAdapter{

	public List<View> mListViews;
	private LibCloud libCloud;
	private int mType = 0;

	public ViewPageAdapter(Context context,List<View> mListViews,int type) {
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
		PictureDataAdapter adapter1 = null ;
		MyDownloadVideoDataAdapter adapter2 = null;
		MyDownloadMusicDataAdapter adapter3 = null;
		MyDownloadPictureDataAdapter adapter4 = null;
		GridView v = (GridView) mListViews.get(position);
		if(mType == Params.RECOMMEND_PICTURE){
			adapter1 = (PictureDataAdapter) v.getAdapter();
		}else if(mType == Params.MYDOWNLOAD_VIDEO){
			adapter2 = (MyDownloadVideoDataAdapter) v.getAdapter();
		}else if(mType == Params.MYDOWNLOAD_MUSIC){
			adapter3 = (MyDownloadMusicDataAdapter) v.getAdapter();
		}else if(mType == Params.MYDOWNLOAD_PICTURE){
			adapter4 = (MyDownloadPictureDataAdapter) v.getAdapter();
		}
		if(v!=null){
			for(int i=0;i<v.getCount();i++){
				View childv = v.getChildAt(i);
				if(childv!=null){
					ImageView imageView = null;
					if(mType == Params.RECOMMEND_PICTURE && adapter1!=null){
						DataInfo info = (DataInfo) adapter1.getItem(i);
						if(info.GetIsDir()!=null && info.GetIsDir().equals("1")){
							imageView = (ImageView) childv.findViewById(R.id.imageView1);
						}
					}else if(mType == Params.MYDOWNLOAD_VIDEO ||mType == Params.MYDOWNLOAD_MUSIC || mType == Params.MYDOWNLOAD_PICTURE){ 
							
						DataInfo info =null;
						if(mType == Params.MYDOWNLOAD_VIDEO && adapter2!=null){
							info = (DataInfo) adapter2.getItem(i);
						}else if(mType == Params.MYDOWNLOAD_MUSIC && adapter3!=null){
							info = (DataInfo) adapter3.getItem(i);
						}else if(mType == Params.MYDOWNLOAD_PICTURE && adapter4!=null){
							info = (DataInfo) adapter4.getItem(i);
						}
						
						if(info.GetIsDir()!=null && info.GetIsDir().equals("1")){
							//System.out.println("ViewPageAdapter destroy is Dir");
							LinearLayout mergeView = (LinearLayout) childv.findViewById(R.id.mergeView);
							TableLayout tl = (TableLayout) mergeView.getChildAt(0);
							TableRow tr1 = (TableRow) tl.getChildAt(0);
							TableRow tr2 = (TableRow) tl.getChildAt(1);
							TableRow tr3 = (TableRow) tl.getChildAt(2);
							String images = info.getThumb();
							String[] imageArray = images.split(",");
							for (int j = 0; j < imageArray.length; j++) {
								//System.out.println("j = " + j);
								String smallurl = imageArray[j];
								//System.out.println("j = " + j + "imgeurl = " + smallurl);
								switch (j) {
								case 0:
									((ImageView) tr1.getChildAt(0)).setImageDrawable(null);			
									break;
								case 1:
									((ImageView) tr1.getChildAt(1)).setImageDrawable(null);	
									break;
								case 2:
									((ImageView) tr1.getChildAt(2)).setImageDrawable(null);				
									break;
								case 3:
									((ImageView) tr2.getChildAt(0)).setImageDrawable(null);	
									break;
								case 4:
									((ImageView) tr2.getChildAt(1)).setImageDrawable(null);	
									break;
								case 5:
									((ImageView) tr2.getChildAt(2)).setImageDrawable(null);	
									break;
								case 6:
									((ImageView) tr3.getChildAt(0)).setImageDrawable(null);	
									break;
								case 7:
									((ImageView) tr3.getChildAt(1)).setImageDrawable(null);	
									break;
								case 8:
									((ImageView) tr3.getChildAt(2)).setImageDrawable(null);	
									break;
								}
							}
						}else{
							imageView = (ImageView) childv.findViewById(R.id.imageView);
						}
					}
					else{
						imageView = (ImageView) childv.findViewById(R.id.imageView);
					}
					if(imageView!=null){
						imageView.setImageDrawable(null);
					}
				}
			}
		}
		
		((ViewPager) collection).removeView(mListViews.get(position));	
		view = null;
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
