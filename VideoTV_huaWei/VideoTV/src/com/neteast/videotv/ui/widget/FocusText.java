package com.neteast.videotv.ui.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.common.collect.Maps;
import com.neteast.videotv.R;
import com.neteast.videotv.listener.DPadListener;

import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-9-26
 * Time: 下午4:20
 */
public class FocusText extends FrameLayout {

    protected DPadListener mDpadListener=new DPadListener() {};
    private static final Map<String,Integer> ICON_TEXT = Maps.newHashMap();
    static {
        ICON_TEXT.put("海外大片", R.drawable.ic_hai_wai_da_pian);
        ICON_TEXT.put("影院热映", R.drawable.ic_ying_yuan_re_ying);
        ICON_TEXT.put("卫视热剧", R.drawable.ic_wei_shi_re_ju);
        ICON_TEXT.put("疯狂美剧", R.drawable.ic_feng_kuang_mei_ju);
        ICON_TEXT.put("日韩剧场", R.drawable.ic_ri_han_ju_chang);
        ICON_TEXT.put("喜剧电影", R.drawable.ic_xi_ju_dian_ying);
    }

    private Drawable mFocusDrawable;

    public FocusText(Context context) {
        super(context);
        init();
    }

    public FocusText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setWillNotDraw(false);
        mFocusDrawable = getResources().getDrawable(R.drawable.bg_home_focus);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mFocusDrawable.setBounds(0,0,getMeasuredWidth(),getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (hasFocus()){
            mFocusDrawable.draw(canvas);
            canvas.clipRect(5,5,getMeasuredWidth()-5,getMeasuredHeight()-5);
        }
        super.onDraw(canvas);
    }

    public void setDpadListener(DPadListener listener) {
        this.mDpadListener = listener;
    }

    public void setText(String text,int index){
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT,MATCH_PARENT));


        text = text.replaceAll("#","");
        int icon = getIconByText(text);

        TextView textView = new TextView(getContext());
        textView.setTextSize(getResources().getDimension(R.dimen.home_page_pic_text_size));

        FrameLayout.LayoutParams params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        int margin = getResources().getDimensionPixelOffset(R.dimen.home_page_pic_text_margin);
        params.setMargins(margin,margin,margin,margin);

        switch (index){
            case 1:
                textView.setText(twoLine(text));
                textView.setTextColor(Color.WHITE);
                textView.setCompoundDrawablesWithIntrinsicBounds(icon,0,0,0);
                params.gravity=Gravity.LEFT|Gravity.BOTTOM;
                imageView.setBackgroundColor(getResources().getColor(R.color.home_page_orange));
                break;
            case 2:
                textView.setText(twoLine(text));
                textView.setTextColor(Color.parseColor("#0a71bc"));
                textView.setCompoundDrawablesWithIntrinsicBounds(0,icon,0,0);
                params.gravity=Gravity.RIGHT|Gravity.TOP;
                imageView.setBackgroundColor(getResources().getColor(R.color.home_page_light_blue));
                break;
            case 9:
                textView.setText(twoLine(text));
                textView.setTextColor(Color.BLACK);
                textView.setCompoundDrawablesWithIntrinsicBounds(icon,0,0,0);
                params.gravity=Gravity.LEFT|Gravity.TOP;
                imageView.setBackgroundColor(getResources().getColor(R.color.home_page_yellow));
                break;
            case 11:
                textView.setText(twoLine(text));
                textView.setTextColor(Color.WHITE);
                textView.setCompoundDrawablesWithIntrinsicBounds(0,icon,0,0);
                params.gravity=Gravity.CENTER;
                imageView.setBackgroundColor(getResources().getColor(R.color.home_page_dark_blue));
                break;
            case 14:
                textView.setText(threeLine(text));
                textView.setTextColor(Color.WHITE);
                textView.setCompoundDrawablesWithIntrinsicBounds(icon,0,0,0);
                params.gravity= Gravity.LEFT|Gravity.TOP;
                imageView.setBackgroundColor(getResources().getColor(R.color.home_page_green));
                break;
            case 18:
                textView.setText(text);
                textView.setTextColor(Color.BLACK);
                textView.setCompoundDrawablesWithIntrinsicBounds(icon,0,0,0);
                params.gravity= Gravity.CENTER;
                imageView.setBackgroundColor(getResources().getColor(R.color.home_page_pinke));
                break;
        }
        addView(imageView);
        addView(textView,params);

    }


    private String twoLine(String text){
        int length = text.length();
        int index = length/2 + length%2;
        return text.substring(0,index)+"\n"+text.substring(index,length);
    }

    private String threeLine(String text){
        return "\n" + twoLine(text);
    }


    private int getIconByText(String text){
        Integer integer = ICON_TEXT.get(text);
        if (integer==null){
            return 0;
        }else {
            return integer;
        }
    }


    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus){
            zoomIn(this);
        }else{
            zoomOut(this);
        }
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

    private void zoomIn(View view){
        view.bringToFront();
        ((RelativeLayout)this.getParent()).invalidate();
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
}
