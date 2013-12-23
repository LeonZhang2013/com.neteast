package com.hs.view;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hs.activity.R;
import com.lib.net.ImageLoader;

/** @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2013-1-9 */
public class ScrollLinearLayout extends LinearLayout {

	/** 自动滚动线程 */
	private AutoScrollThread scrollThread;
	/** 一次滚动几张图片 **/
	private int mScrollImageCount =3;
	/** 是否自动滚动 */
	private Integer mDirection;
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
	private final int moveSpace = 80;
	/** 滚得频率，单位微秒 */
	private final int moveSpeed = 10;
	/** 图片移动平偏移量 */
	private float offset;
	/** 上一次被移动的图片。 */
	private View mImage;
	/** 图片宽度，包括图片的margin值。 */
	private int mImagew;
	private final int mImagewOffset = 200;
	/** 图片点击监听 */
	private OnClickScrollItemListener mOnClickScrollItemListener;
	private OnPageChangeListener mOnPageChangeListener;
	
	public ScrollLinearLayout(Context context) {
		super(context);
	}

	public ScrollLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	/**
	 * 图片点击监听事件
	 *
	 * @author LeonZhang 
	 * @Email zhanglinlang1@163.com
	 */
	public interface OnClickScrollItemListener {
		public void onClick(int imageIndex);
	}
	
	/**
	 * 翻页滑动事件。
	 *
	 * @author LeonZhang 
	 * @Email zhanglinlang1@163.com
	 */
	public interface OnPageChangeListener {
		public void onClick(int pageIndex);
	}

	public void setOnClickScrollItemListener(OnClickScrollItemListener onClickScrollItemListener) {
		mOnClickScrollItemListener = onClickScrollItemListener;
	}
	
	public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener){
		mOnPageChangeListener = onPageChangeListener;
	}
	

	public void setImages(List<Map<String, Object>> result) {
		ll = (LinearLayout) getChildAt(0);
		ImageLoader imageLoader = ImageLoader.getInstanse(getContext());
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(margin, 0, 0, 0);
		linkedImage = new LinkedList<View>();
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < result.size(); i++) {
			View layout = inflater.inflate(R.layout.recommend_pic,null);
			layout.setLayoutParams(params);
			layout.setTag(i);
			ImageView image = (ImageView) layout.findViewById(R.id.image_view);
			imageLoader.setImage((String) (result.get(i).get("image_show")), image);
			ll.addView(layout);
			linkedImage.add(layout);
		}
		if(linkedImage.size()==0) return;
		mImage = linkedImage.get(0);
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
			mImagew = mImage.getWidth() + margin;
			if (scrollThread != null)
				stopScroll();
			break;
		case MotionEvent.ACTION_MOVE:
			if(linkedImage.size()<4) break;
			offset = movieCoordinate - event.getX();
			ll.scrollTo((int) (oldLoacl + offset), 0);
			if (ll.getScrollX() > (mImagew + mImagewOffset)) {
				leftCycle(1);
				ll.scrollTo(mImagewOffset, 0);
				oldLoacl = mImagewOffset;
				movieCoordinate = event.getX();
			} else if (ll.getScrollX() < mImagewOffset) {
				rightCycle(1);
				ll.scrollTo(mImagew + mImagewOffset, 0);
				oldLoacl = mImagew + mImagewOffset;
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
			if (moveDirection < -1) {
				resetAnima(RIGHT);
				break;
			}
			if (mOnClickScrollItemListener != null) {
				int imageIndex = (int) ((movieCoordinate + ll.getScrollX()) / mImagew);
				View view = linkedImage.get(imageIndex);
				mOnClickScrollItemListener.onClick((Integer) view.getTag());
			}
			break;

		}
		return true;
	}

	private void resetAnima(int direction) {
		moveHandler.sendEmptyMessage(direction);
	}

	Handler moveHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(mImage==null)return ;
			if(mImagew==0) mImagew = mImage.getWidth() + margin;
			int imageIndex = (Integer) mImage.getTag();
			float currentX = ll.getScrollX();
			switch (msg.what) {
			case LEFT:
				currentX += moveSpace;
				if (currentX >= mImagew + mImagewOffset) {
					leftCycle(1);
					currentX = mImagewOffset + moveSpace;
				}
				if(imageIndex %mScrollImageCount == 1&&mImagew-currentX<=moveSpace&&mImagew>=currentX){
					ll.scrollTo(mImagew, 0);
					break;
				}
				ll.scrollTo((int)currentX, 0);
				sendEmptyMessageDelayed(LEFT, moveSpeed);
				break;
			case RIGHT:
				currentX -= moveSpace;
				if (currentX <= mImagewOffset) {
					rightCycle(1);
					currentX = mImagew+mImagewOffset - moveSpace;
				}
				if(imageIndex %mScrollImageCount == 2&&currentX-mImagew<=moveSpace&&currentX>=mImagew){
					ll.scrollTo(mImagew, 0);
					break;
				}
				ll.scrollTo((int)currentX, 0);
				sendEmptyMessageDelayed(RIGHT, moveSpeed);
				break;
			}
		}
	};

	/** 向左循环（指尖向左滑动） */
	private void leftCycle(int scrollImageCount) {
		for (int i = 0; i < scrollImageCount; i++) {
			mImage = linkedImage.removeFirst();
			linkedImage.addLast(mImage);
			ll.removeView(mImage);
			ll.addView(mImage);
		}
		pageChange(mImage);
	}
	

	/** 向右循环（指尖向右滑动） */
	private void rightCycle(int scrollImageCount) {
		for (int i = 0; i < scrollImageCount; i++) {
			mImage = linkedImage.removeLast();
			linkedImage.addFirst(mImage);
			ll.removeView(mImage);
			ll.addView(mImage, 0);
		}
		pageChange(mImage);
	}
	
	private void pageChange(View imageView){
		int index = (Integer) imageView.getTag();
		if(mOnPageChangeListener!=null){
			int pageIndex = index/mScrollImageCount-1;
			if(pageIndex<0){
				int pageMax = linkedImage.size()%3==0? linkedImage.size()/3-1 :linkedImage.size()/3;
				pageIndex = pageMax;
			}
			mOnPageChangeListener.onClick(pageIndex);
		}
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

	public void stopScroll() {
		if (scrollThread != null) {
			scrollThread.stopMySlef();
			scrollThread = null;
		}
	}
}
