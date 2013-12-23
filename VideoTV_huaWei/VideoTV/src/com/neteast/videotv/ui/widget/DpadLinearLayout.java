package com.neteast.videotv.ui.widget;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import com.neteast.videotv.listener.DPadListener;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-9
 * Time: 下午4:59
 */
public abstract class DpadLinearLayout extends LinearLayout {

    protected DPadListener mDpadListener=new DPadListener() {};


    public DpadLinearLayout(Context context) {
        super(context);
        setFocusable(true);
        setClickable(true);
        init();
    }

    protected abstract void init();

    public void setDpadListener(DPadListener listener) {
        this.mDpadListener = listener;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_LEFT:
                return mDpadListener.onClickLeft();
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                return mDpadListener.onClickRight();
            case KeyEvent.KEYCODE_DPAD_UP:
                return mDpadListener.onClickUp();
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return mDpadListener.onClickDown();
        }
        return super.onKeyDown(keyCode, event);
    }
}
