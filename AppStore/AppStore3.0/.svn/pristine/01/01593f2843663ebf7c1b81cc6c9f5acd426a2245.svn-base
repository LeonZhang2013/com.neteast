package com.hs.view;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.hs.activity.R;
import com.hs.view.ScrollLinearLayout.OnPageChangeListener;
import com.lib.net.ImageLoader;

/** @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-1-9 */
public class ScrollLinearLayout1 extends HorizontalScrollView {

	/** 自动滚动线程标志位。 true时，关闭自动滚动效果 */
	private boolean closeThread;
	/** 自动滚动效果等待，true时继续等待，false开始滚动 */
	private boolean threadWait;
	/** 滚动内布局 */
	private LinearLayout ll;
	/** 图片间隔 */
	private int margin = 14;
	/** 自动滚动停顿时间 */
	private int gallerySleepTime = 5000;
	/** 图片容器 */
	private LinkedList<View> linkedImage;
	/** 滚动方向 */
	public static final int LEFT = 0x111;
	/** 滚动方向 */
	public static final int RIGHT = 0x000;
	/** 滚动距离，单位像素。 */
	private final int moveSpace = 20;
	/** 滚得频率，单位微秒 */
	private final int moveSpeed = 10;
	/** 图片移动平偏移量 */
	private float offset;
	/** 上一次被移动的图片。 */
	private View mImage;
	/** 图片宽度，包括图片的margin值。 */
	private int mImagew = 649;
	/** 图片提前加载位置。 */
	private int mImagewOffset = 50;
	/** 翻页监听 */
	private OnPageChangeListener mOnPageChangeListener;

	public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
		mOnPageChangeListener = onPageChangeListener;
	}

	public ScrollLinearLayout1(Context context) {
		super(context);
	}

	public ScrollLinearLayout1(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public interface OnClickScrollItemListener {
		public void onClick(ImageView view, int imageIndex);
	}

	public void setImages(List<String> imageList) {
		ll = (LinearLayout) getChildAt(0);
		ImageLoader imageLoader = ImageLoader.getInstanse(getContext());
		LinearLayout.LayoutParams paramImageBg = new LinearLayout.LayoutParams(609, 345);
		paramImageBg.leftMargin = 40;
		LinearLayout.LayoutParams paramImage = new LinearLayout.LayoutParams(602, 336);
		linkedImage = new LinkedList<View>();
		for (int i = 0; i < imageList.size(); i++) {
			LinearLayout layout = new LinearLayout(getContext());
			layout.setBackgroundResource(R.drawable.detaill_scroll_gb);
			layout.setGravity(Gravity.CENTER_HORIZONTAL);
			layout.setLayoutParams(paramImageBg);
			layout.setTag(i);

			ImageView image = new ImageView(getContext());
			image.setScaleType(ScaleType.FIT_XY);
			image.setLayoutParams(paramImage);
			imageLoader.setImage(imageList.get(i), image);
			layout.addView(image);
			linkedImage.add(layout);
			ll.addView(layout);
		}
		mImage = linkedImage.get(0);
	}
	


	private ListenerThread listenerThread;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (listenerThread != null)
				listenerThread.stopSlef();
			break;
		case MotionEvent.ACTION_MOVE:
			pageChange();
			break;
		case MotionEvent.ACTION_UP:
			listenerThread = new ListenerThread();
			listenerThread.start();
			break;
		}
		return super.onTouchEvent(event);
	}

	private static int mImageindex;

	private class ListenerThread extends Thread {
		private boolean stop = true;

		public void stopSlef() {
			stop = false;
		}

		@Override
		public void run() {
			long startTime = System.currentTimeMillis();
			while (stop) {
				try {
					Thread.sleep(50);
					h.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Log.i("ll", "time = " + (System.currentTimeMillis() - startTime));
				if (System.currentTimeMillis() - startTime > 2000)
					break;
			}
		}
	}

	Handler h = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			pageChange();
		}
	};

	private void pageChange() {

		Log.i("ll", getScrollX() + " - " + ll.getScrollX());

		int index = (getScrollX() + mImagew / 2) / mImagew;
		if (mImageindex != index) {
			mImageindex = index;
			mOnPageChangeListener.onClick(mImageindex);
		}

	}
}
