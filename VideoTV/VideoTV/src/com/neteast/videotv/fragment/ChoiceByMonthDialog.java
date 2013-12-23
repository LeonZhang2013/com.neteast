package com.neteast.videotv.fragment;

import java.util.List;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.neteast.lib.bean.StreamingMediaRaw;
import com.neteast.lib.config.VideoType;
import com.neteast.videotv.R;
import com.neteast.videotv.bean.VideoDetail;
import com.neteast.videotv.ui.VideoDetailActivity;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-3
 * Time: 下午5:10
 * To change this template use File | Settings | File Templates.
 */
@EFragment(R.layout.frag_choice_by_month)
public class ChoiceByMonthDialog extends TVDialog {

    @ViewById(R.id.month_container) LinearLayout mMonthContainer;
    @ViewById(R.id.month_left_panel) LinearLayout mLeftPanel;
    @ViewById(R.id.month_right_panel) LinearLayout mRightPanel;


    private static VideoDetail mVideo;

    private View mCurrentTitleView;
    private View mCurrentItemView;

    public static final ChoiceByMonthDialog newDialog(VideoDetail videoDetail){
        mVideo=videoDetail;
        return new ChoiceByMonthDialog_();
    }

    @AfterViews
    void ininUI(){
        setTitles();
    }

    private void setTitles() {
        List<String> pageTitles = mVideo.getPageTitles(mVideo.getCurrentOrigin());

        TextView view;
        for(String title:pageTitles){

            view = new TextView(getActivity());
            view.setText(title);
            view.setTextSize(getResources().getDimensionPixelOffset(R.dimen.choice_count_text_size));
            view.setTextColor(getResources().getColorStateList(R.color.nav_sub_text));
            view.setBackgroundResource(R.drawable.bg_title_can_focus);
            view.setFocusable(true);
            view.setClickable(true);
            view.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin=getResources().getDimensionPixelOffset(R.dimen.choice_count_text_margin_left);
            params.gravity=Gravity.CENTER_VERTICAL;

            mMonthContainer.addView(view,params);

            view.setOnClickListener(myClickTitleListener);
        }

        if (mMonthContainer.getChildCount()>0){
            setTitleSelecttion(mMonthContainer.getChildAt(0));
            updateCount(pageTitles.get(0));
        }
    }

    private void updateCount(String title) {
        List<StreamingMediaRaw.MediaRaw> medias = mVideo.getMedias(mVideo.getCurrentOrigin(), title);
        LinearLayout panel;
        int leftIndex=0;
        int rightIndex=0;
        for(int i=0,size=medias.size();i<size;i++){
            StreamingMediaRaw.MediaRaw mediaRaw = medias.get(i);

            if (i%2==0){
                panel=mLeftPanel;
                leftIndex++;
            } else {
                panel=mRightPanel;
                rightIndex++;
            }
            View child = panel.getChildAt(i/2);
            child.setVisibility(View.VISIBLE);
            TextView titleView= (TextView) child.findViewById(R.id.title);
            titleView.setText(formatText(mediaRaw));
            child.setTag(mediaRaw);
            child.setOnClickListener(myClickItemListener);
        }
        View firstView = mLeftPanel.getChildAt(0);
        if (firstView.getVisibility()==View.VISIBLE){
            setItemSelecttion(firstView);
            firstView.requestFocus();
        }

        for(int i=leftIndex;i<15;i++){
            mLeftPanel.getChildAt(i).setVisibility(View.INVISIBLE);
        }
        for(int i=rightIndex;i<15;i++){
            mRightPanel.getChildAt(i).setVisibility(View.INVISIBLE);
        }
    }

    private String formatText(StreamingMediaRaw.MediaRaw mediaRaw) {
    	
    	if (mVideo.getType() == VideoType.RECORD) {
			return mediaRaw.getTitle();
		}
    	
        StringBuilder result=new StringBuilder();
        String series=mediaRaw.getSeries();
        result.append("第");
        result.append(series.substring(0,4));
        result.append("-");
        result.append(series.substring(4,6));
        result.append("-");
        result.append(series.substring(6,8)+"期");

        if (!TextUtils.isEmpty(mediaRaw.getClearTitle())){
            result.append(" ");
            result.append(mediaRaw.getClearTitle());
        }
        return result.toString();
    }

    /**
     * 设置当前选中的图标为活跃状态
     * @param clickView
     * @return 选中图标是否改变
     */
    private boolean setItemSelecttion(View clickView) {
        if (mCurrentItemView==null){
            mCurrentItemView=clickView;
            mCurrentItemView.setActivated(true);
            return true;
        }
        if (mCurrentItemView!=clickView){
            mCurrentItemView.setActivated(false);
            clickView.setActivated(true);
            mCurrentItemView=clickView;
            return true;
        }

        return false;
    }
    /**
     * 设置当前选中的图标为活跃状态
     * @param clickView
     * @return 选中图标是否改变
     */
    private boolean setTitleSelecttion(View clickView) {
        if (mCurrentTitleView==null){
            mCurrentTitleView=clickView;
            mCurrentTitleView.setActivated(true);
            return true;
        }
        if (mCurrentTitleView!=clickView){
            mCurrentTitleView.setActivated(false);
            clickView.setActivated(true);
            mCurrentTitleView=clickView;
            return true;
        }

        return false;
    }

    private View.OnClickListener myClickTitleListener =new  View.OnClickListener(){
        @Override
        public void onClick(View v) {
            TextView title=(TextView) v;
            boolean changed = setTitleSelecttion(title );
            if (changed){
                updateCount(title.getText().toString());
            }
        }
    };

    private View.OnClickListener myClickItemListener =new  View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setItemSelecttion(v);
            StreamingMediaRaw.MediaRaw media = (StreamingMediaRaw.MediaRaw) v.getTag();
            VideoDetailActivity activity = (VideoDetailActivity)getActivity();
            activity.onPlay(media);

        }
    };

}
