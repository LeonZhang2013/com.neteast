package com.neteast.videotv.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.neteast.videotv.R;
import com.neteast.videotv.utils.Utils;

import static com.neteast.videotv.utils.Utils.*;
import static android.view.ViewGroup.LayoutParams.*;
/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-9
 * Time: 下午5:38
 */
public class ChannelView extends DpadLinearLayout {

    private TextView channel;

    public ChannelView(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        setOrientation(HORIZONTAL);
        setBackgroundResource(R.drawable.channel_bg);

        int channelViewHeight = dp2px(getContext(), 55);
        LinearLayout.LayoutParams layoutParams=new LayoutParams(MATCH_PARENT,channelViewHeight);
        layoutParams.gravity= Gravity.CENTER_VERTICAL;
        setLayoutParams(layoutParams);

        ImageView indicator=new ImageView(getContext());
        indicator.setImageResource(R.drawable.channel_indicator);
        indicator.setDuplicateParentStateEnabled(true);
        int imageWidth= Utils.dp2px(getContext(),15);
        LinearLayout.LayoutParams indicatorParams=new LayoutParams(imageWidth,imageWidth);
        indicatorParams.leftMargin=dp2px(getContext(),12);
        indicatorParams.gravity=Gravity.CENTER_VERTICAL;
        addView(indicator,indicatorParams);

        channel = new TextView(getContext());
        channel.setDuplicateParentStateEnabled(true);
        channel.setSingleLine();
        channel.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        channel.setTextSize(dp2px(getContext(), 18));
        channel.setTextColor(getResources().getColorStateList(R.color.channel_text_color));

        LinearLayout.LayoutParams channelParams=new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        channelParams.leftMargin=dp2px(getContext(),16);
        channelParams.gravity=Gravity.CENTER_VERTICAL;

        addView(channel,channelParams);
    }

    public String getChannel(){
        return channel.getText().toString();
    }

    public void setChannel(String value){
        channel.setText(value);
    }

}
