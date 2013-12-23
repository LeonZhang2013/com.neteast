package com.neteast.videotv.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.neteast.videotv.R;
import static com.neteast.videotv.utils.Utils.dp2px;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-16
 * Time: 下午3:51
 */
public class ResetChannelView extends TextView {

    private int currentIndex;

    public ResetChannelView(Context context,int width,int height) {
        super(context);
        setFocusable(true);
        setClickable(true);
        setBackgroundResource(R.drawable.bg_blue_dark_grey);
        init(width,height);
    }

    private void init(int width, int height) {
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setTextSize(dp2px(getContext(), 22));
        setTextColor(getResources().getColorStateList(R.color.channel_text_color));
        setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(width,height);

        setLayoutParams(new GridLayout.LayoutParams(params));
    }

    public void setChannel(int currentIndex,String channelName){
        this.currentIndex=currentIndex;
        StringBuilder result=new StringBuilder();
        result.append(currentIndex+1);
        result.append(".");
        result.append(channelName);
        setText(result.toString());
    }

    public int getCurrentIndex(){
        return currentIndex;
    }

}
