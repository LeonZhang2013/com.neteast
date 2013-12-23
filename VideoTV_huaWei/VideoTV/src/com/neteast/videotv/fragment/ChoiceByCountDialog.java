package com.neteast.videotv.fragment;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import java.util.List;

import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.neteast.lib.bean.StreamingMediaRaw;
import com.neteast.videotv.R;
import com.neteast.videotv.bean.VideoDetail;
import com.neteast.videotv.ui.VideoDetailActivity;
/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-3
 * Time: 上午11:38
 * To change this template use File | Settings | File Templates.
 */
@EFragment(R.layout.frag_choice_by_count)
public class ChoiceByCountDialog extends TVDialog {

    private static VideoDetail mVideo;

    public static final ChoiceByCountDialog newDialog(VideoDetail video){
        mVideo=video;
        return new ChoiceByCountDialog_();
    }

    @ViewById(R.id.choice_title_scroll) HorizontalScrollView mTitleScroll;
    @ViewById(R.id.choice_title_container) LinearLayout mTitleContainer;
    @ViewById(R.id.choice_count_grid) GridLayout mCountGrid;

    private TextView mCurrentCountView;
    private TextView mCurrentTitleView;

    @AfterViews
    void updateUI(){
        setTitle();
    }


    private void setTitle() {
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

            mTitleContainer.addView(view,params);

            view.setOnClickListener(mClickTitleListener);
        }

        if (mTitleContainer.getChildCount()>0){
            setTitleSelecttion((TextView) mTitleContainer.getChildAt(0));
            updateCount(pageTitles.get(0));
        }
    }

    private void updateCount(String pageTitle) {
        int index=-1;
        for(StreamingMediaRaw.MediaRaw media : mVideo.getMedias(mVideo.getCurrentOrigin(),pageTitle)){
            index++;
            TextView view = (TextView) mCountGrid.getChildAt(index);
            view.setText(media.getSeries());
            view.setTag(media);
            view.setVisibility(VISIBLE);
            view.setOnClickListener(mClickCountListener);
        }

        TextView firstView = (TextView) mCountGrid.getChildAt(0);
        if (firstView.getVisibility()==VISIBLE){
            setItemSelecttion(firstView);
            firstView.requestFocus();
        }

        index++;
        for(int i=index;i<50;i++){
            mCountGrid.getChildAt(i).setVisibility(INVISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mVideo=null;
    }

    private boolean setItemSelecttion(TextView clickView) {
       if (mCurrentCountView==null){
           mCurrentCountView=clickView;
           mCurrentCountView.setActivated(true);
           return true;
       }
       if (mCurrentCountView!=clickView){
           mCurrentCountView.setActivated(false);
           clickView.setActivated(true);
           mCurrentCountView=clickView;
           return true;
       }

       return false;
    }
    private boolean setTitleSelecttion(TextView clickView) {
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

    private OnClickListener mClickTitleListener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView title=(TextView) v;
            boolean changed = setTitleSelecttion(title);
            if (changed){
                updateCount(title.getText().toString());
            }
        }
    };

    private OnClickListener mClickCountListener=new OnClickListener(){
        @Override
        public void onClick(View v) {
            setItemSelecttion((TextView) v);
            StreamingMediaRaw.MediaRaw media= (StreamingMediaRaw.MediaRaw) v.getTag();
            VideoDetailActivity activity = (VideoDetailActivity)getActivity();
            activity.onPlay(media);
        }
    };


}
