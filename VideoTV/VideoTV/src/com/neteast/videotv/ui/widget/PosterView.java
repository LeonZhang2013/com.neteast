package com.neteast.videotv.ui.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.neteast.videotv.R;
import com.neteast.videotv.listener.DPadListener;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-10-14
 * Time: 下午3:00
 */
public class PosterView extends FrameLayout {

    private DPadListener mDPadListener;
    private NetworkImageView mImageView;
    private TextView mTitle;
    private TextView mCount;
    private boolean showCount;

    public PosterView(Context context) {
        super(context);
        init();
    }

    public PosterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PosterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        setClickable(true);
        setFocusable(true);
        setWillNotDraw(false);
        View root = LayoutInflater.from(getContext()).inflate(R.layout.view_scale_image, this, false);
        mImageView = (NetworkImageView) root.findViewById(R.id.image);
        mTitle = (TextView) root.findViewById(R.id.title);
        mCount = (TextView) root.findViewById(R.id.count);
        addView(root);
    }


    public void setImage(String url, ImageLoader loader){
        mImageView.setImageUrl(url,loader);
    }
    
    

    public NetworkImageView getImage() {
		return mImageView;
	}

	public void setmImageView(NetworkImageView mImageView) {
		this.mImageView = mImageView;
	}

	public void setTitle(String text){
        mTitle.setText(text);
        mTitle.setVisibility(VISIBLE);
    }

    /**
     *
     * @param count
     * @param maxSeries
     */
    public void setCount(int count,int maxSeries){
        showCount = maxSeries>0;
        if (maxSeries >= count){
            mCount.setText(String.format("全%d集",maxSeries));
        }else {
            mCount.setText(String.format("更新至%d集",maxSeries));
        }
    }
    
    
	@Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus){
            zoomIn(this);
            if (showCount){
                mCount.setVisibility(VISIBLE);
            }
            mTitle.setSelected(true);
        }else {
            zoomOut(this);
            mCount.setVisibility(GONE);
            mTitle.setSelected(false);
        }
    }

    private void zoomIn(View view){
    	this.bringToFront();
    	try{
    		((RelativeLayout)this.getParent()).invalidate();
    	}catch (Exception e) {
    		((GridLayout)this.getParent()).invalidate();
		}
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.2f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.2f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX,scaleY);
        set.start();
    }

    private void zoomOut(View view){
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.2f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.2f, 1.0f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX,scaleY);
        set.start();
    }

    public DPadListener getDPadListener() {
        return mDPadListener;
    }

    public void setDPadListener(DPadListener mDPadListener) {
        this.mDPadListener = mDPadListener;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mDPadListener!=null){
            switch (keyCode){
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    return mDPadListener.onClickLeft();
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    return mDPadListener.onClickRight();
                case KeyEvent.KEYCODE_DPAD_UP:
                    return mDPadListener.onClickUp();
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    return mDPadListener.onClickDown();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

/*    *//**
     * 像下一层布局传递信息
     *//*
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//super.onTouchEvent(event);
		return false;
	}*/
    
}
