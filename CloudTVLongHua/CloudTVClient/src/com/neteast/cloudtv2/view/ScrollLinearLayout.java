package com.neteast.cloudtv2.view;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.neteast.cloudtv2.R;

/** @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-1-9 */
public class ScrollLinearLayout extends LinearLayout {

	/** 自动滚动线程 */
	private AutoScrollThread scrollThread;
	/** 是否自动滚动 */
	private Integer mDirection;
	/** 滚动内布局 */
	private LinearLayout ll;
	/** 滚动方向 */
	public static final int LEFT = 0x111;
	/** 滚动方向 */
	public static final int RIGHT = 0x000;
	/** 滚动距离，单位像素。 */
	private final int moveSpace = 35;
	/** 滚得频率，单位微秒 */
	private final int moveSpeed = 10;
	/** 图片移动平偏移量 */
	private float offset;
	/** 上一次被移动的图片。 */
	private View mImage;
	/** 图片宽度，包括图片的margin值。 */
	private int mImagew;
	/** 自动滚动停顿时间 */
	private int gallerySleepTime = 5000;
	/** **/
	private DisplayMetrics mDisplayMetrics;
	/** 图片点击监听 */
	private OnPageChangeListener mOnPageChangeListener;
	/** 图片点击监听 */
	private OnCloseListener mOnCloseListener;
	/** **/
	private LinkedList<View> linkedImages;

	public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
		mOnPageChangeListener = onPageChangeListener;
	}
	
	public void setOnCloseListener(OnCloseListener onCloseListener) {
		mOnCloseListener = onCloseListener;
	}


	public void setDisplayMetrics(DisplayMetrics displayMetrics) {
		mDisplayMetrics = displayMetrics;
	}

	public ScrollLinearLayout(Context context) {
		super(context);
	}

	public ScrollLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/** 图片点击监听事件
	 * 
	 * @author LeonZhang
	 * @Email zhanglinlang1@163.com */
	public interface OnClickScrollItemListener {
		public void onClick(int imageIndex);
	}

	/** 翻页滑动事件。
	 * 
	 * @author LeonZhang
	 * @Email zhanglinlang1@163.com */
	public interface OnPageChangeListener {
		public void onClick(int pageIndex, boolean isLast);
	}
	
	/** 关闭事件。
	 * 
	 * @author LeonZhang
	 * @Email zhanglinlang1@163.com */
	public interface OnCloseListener {
		public void onClose();
	}

	private void startScroll() {
		if (mDirection == null)
			return;
		scrollThread = new AutoScrollThread(mDirection);
		scrollThread.start();
	}

	public void startScroll(int direction) {
		this.mDirection = direction;
		startScroll();
	}

	public void setImages(List<Integer> result) {
		ll = (LinearLayout) getChildAt(0);
		LayoutParams params = null;
		if (mDisplayMetrics != null) {
			params = new LayoutParams(mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels);
		} else {
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}
		linkedImages = new LinkedList<View>();
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < result.size(); i++) {
			View layout = inflater.inflate(R.layout.recommend_pic, null);
			layout.setLayoutParams(params);
			layout.setTag(i);
			ImageView image = (ImageView) layout.findViewById(R.id.image_view);
			image.setLayoutParams(params);
			image.setImageResource(result.get(i));
			ll.addView(layout);
			linkedImages.add(layout);
		}
		mImage = linkedImages.getFirst();
	}

	private float oldLoacl;
	private float movieCoordinate;
	private float pressCoordinate;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			movieCoordinate = event.getX();
			pressCoordinate = event.getX();
			oldLoacl = ll.getScrollX();
			mImagew = mImage.getWidth();
			break;
		case MotionEvent.ACTION_MOVE:
			offset = movieCoordinate - event.getX();
			if (linkedImages.size() == 1)
				break;
			if (oldLoacl + offset > 0) {
				ll.scrollTo((int) (oldLoacl + offset), 0);
			}
			if (ll.getScrollX() > mImagew) {
				leftCycle();
				ll.scrollTo(0, 0);
				oldLoacl = 0;
				movieCoordinate = event.getX();
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mDirection != null)
				startScroll();
			int moveDirection = (int) (pressCoordinate - event.getX());
			if (moveDirection > 1) {
				resetAnima(LEFT);
				break;
			}
			break;
		}
		return true;
	}

	public void resetAnima(int direction) {
		moveHandler.sendEmptyMessage(direction);
	}

	Handler moveHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (linkedImages.size() == 1){
				mOnCloseListener.onClose();
				return;
			}
			if (mImage == null)
				return;
			if (mImagew == 0)
				mImagew = mImage.getWidth();
			float currentX = ll.getScrollX();
			switch (msg.what) {
			case LEFT:
				currentX += moveSpace;
				if (currentX > mImagew) {
					leftCycle();
					currentX = 0;
					ll.scrollTo((int) currentX, 0);
					break;
				}
				ll.scrollTo((int) currentX, 0);
				sendEmptyMessageDelayed(LEFT, moveSpeed);
				break;
			}
		}
	};

	/** 向左循环（指尖向左滑动） */
	private void leftCycle() {
		mImage = linkedImages.removeFirst();
		ll.removeView(mImage);
		pageChange(mImage);
		if (ll.getChildCount() == 1) {
			scrollThread.stopMySlef();
			moveHandler.sendEmptyMessageDelayed(GONE, 5000);
		}
	}

	private void pageChange(View imageView) {
		int index = (Integer) imageView.getTag();
		if (mOnPageChangeListener != null) {
			mOnPageChangeListener.onClick(index, linkedImages.size() == 1);
		}
	}

	private class AutoScrollThread extends Thread {
		private boolean closeThread;
		private int direction;

		public void stopMySlef() {
			closeThread = true;
		}

		public AutoScrollThread(int direction) {
			this.direction = direction;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(gallerySleepTime);
				while (!closeThread) {
					if (direction == RIGHT) {
						resetAnima(RIGHT);
					} else {
						resetAnima(LEFT);
					}
					Thread.sleep(gallerySleepTime);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
