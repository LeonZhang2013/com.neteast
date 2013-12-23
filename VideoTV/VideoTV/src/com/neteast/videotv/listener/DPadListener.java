package com.neteast.videotv.listener;

import android.view.KeyEvent;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-7-24
 * Time: 下午5:06
 * To change this template use File | Settings | File Templates.
 */
public abstract class DPadListener implements View.OnKeyListener {

    public boolean onClickLeft(){return false;}
    public boolean onClickRight(){return false;}
    public boolean onClickUp(){return false;}
    public boolean onClickDown(){return false;}
    public boolean onClickCenter(){return false;}

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_LEFT:
                return onClickLeft();
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                return onClickRight();
            case KeyEvent.KEYCODE_DPAD_UP:
                return onClickUp();
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return onClickDown();
            case KeyEvent.KEYCODE_DPAD_CENTER:
                return onClickCenter();
        }
        return false;
    }
}
