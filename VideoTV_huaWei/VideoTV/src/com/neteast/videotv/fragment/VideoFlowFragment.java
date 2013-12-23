package com.neteast.videotv.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.neteast.lib.config.VideoType;
import com.neteast.videotv.R;
import com.neteast.videotv.listener.BackPressListener;
import com.neteast.videotv.listener.DPadListener;
import com.neteast.videotv.ui.widget.ChannelList;
import com.neteast.videotv.ui.widget.PosterView;
import com.neteast.videotv.utils.VolleyCallback;

import java.util.List;

/**
 * 精选专区、 视频分类、我的视频、一键搜索的父类。
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-10-17
 * Time: 下午5:38
 */

public abstract class VideoFlowFragment extends Fragment implements BackPressListener{

    protected static final int PRE_LOAD_PAGE_NUMBER = 10;
    protected static final int REQUEST_TIME_OUT = 10*1000;

    protected DialogFragment mLoading;

    protected GridLayout mGrid1;
    protected GridLayout mGrid2;
    protected TextView mCountInfo;
    protected View mViewFlipper;


    protected RadioGroup mSortSelection;
    protected View mFilter;
    protected TextView mFilterInfo;

    protected View mSearchResultFilterScroller;
    protected LinearLayout mSearchResultFilterContainer;
    protected TextView mSearchInfo;
    protected ChannelList mChannelList;
    protected View mChannelListScroll;

    protected int lastPage = 1;
    protected RequestQueue mRequestQueue;
    protected ImageLoader mImageLoader;
    protected View mLastFilterView;


    protected abstract void initController();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_video_flow, container, false);


        mGrid1 = (GridLayout) root.findViewById(R.id.grid1);
        mGrid2 = (GridLayout) root.findViewById(R.id.grid2);

        mViewFlipper = root.findViewById(R.id.viewFlipper);
        mCountInfo = (TextView) root.findViewById(R.id.countInfo);

        mSortSelection = (RadioGroup) root.findViewById(R.id.sortSelector);
        mFilter = root.findViewById(R.id.filter);
        mFilterInfo = (TextView) root.findViewById(R.id.filterInfo);


        mSearchResultFilterContainer = (LinearLayout) root.findViewById(R.id.searchContainer);
        mSearchResultFilterScroller = root.findViewById(R.id.searchScroller);

        mSearchInfo = (TextView) root.findViewById(R.id.searchInfo);

        mChannelList = (ChannelList) root.findViewById(R.id.ChannelList);
        mChannelListScroll = root.findViewById(R.id.ChannelListScroll);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        VolleyCallback callback = (VolleyCallback) getActivity().getApplication();
        mRequestQueue = callback.getRequestQueue();
        mImageLoader = callback.getImageLoader();
        initController();
        prepareChannelList();
    }


    private PosterView getView(int i) {
        int w = getResources().getDimensionPixelOffset(R.dimen.image_width);
        int h = getResources().getDimensionPixelOffset(R.dimen.image_height);
        PosterView posterView = new PosterView(getActivity());
        GridLayout.LayoutParams params =new GridLayout.LayoutParams(
                new ViewGroup.LayoutParams(w,h)
        );
        int lineCount = getSinglePageNumber()/2;
        
        params.columnSpec = GridLayout.spec(i%lineCount);
        params.rowSpec = GridLayout.spec(i/lineCount);
        
        posterView.setLayoutParams(params);
        if (isLastColumn(i)){
            setLastColumnListener(posterView);
        }
        if (isFirstColumn(i)) {
            setFirstColumnListener(posterView);
        }
        setScalePivot(i, posterView);
        posterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewClicked(v.getTag());
            }
        });
        return posterView;
    }

    protected abstract void prepareChannelList();
    protected abstract int getSinglePageNumber();
    protected abstract void onViewClicked(Object data);

    protected void updateFilterView(View clickedView){
        if (clickedView == null){
            if (mLastFilterView != null){
                mLastFilterView.setActivated(false);
                mLastFilterView = null;
            }
            return;
        }

        if (mLastFilterView == null){
            clickedView.setActivated(true);
            mLastFilterView = clickedView;
            return;
        }
        if (mLastFilterView != clickedView){
            clickedView.setActivated(true);
            mLastFilterView.setActivated(false);
            mLastFilterView = clickedView;
        }
    }

    protected abstract boolean performScrollNext();

    protected abstract boolean performScrollPrev();

    protected boolean isFirstRow(int i){
        return i>= 0 && i< (getSinglePageNumber()/2);
    }

    protected boolean isSecondRow(int i){
        return i>= (getSinglePageNumber()/2) && i< getSinglePageNumber();
    }

    protected boolean isFirstColumn(int i) {
        return i % (getSinglePageNumber()/2) == 0;
    }

    protected boolean isLastColumn(int i) {
        int halfNum = getSinglePageNumber()/2;
        return i % halfNum == (halfNum -1);
    }

    private void setLastColumnListener(PosterView posterView) {
        posterView.setDPadListener(new DPadListener() {
            @Override
            public boolean onClickRight() {
                return performScrollNext();
            }
        });
    }

    private void setFirstColumnListener(PosterView posterView) {
        posterView.setDPadListener(new DPadListener() {
            @Override
            public boolean onClickLeft() {
                return performScrollPrev();
            }
        });
    }

    protected void updatePage(int currentPage, List currentPageData) {
        GridLayout frontView, backView;
        if (mGrid1.getVisibility()==View.VISIBLE){
            frontView= mGrid1;
            backView = mGrid2;
        }else {
            frontView = mGrid2;
            backView = mGrid1;
        }
        if (lastPage == currentPage){
            updateGridContent(frontView, currentPageData);
        }else {
            updateGridContent(backView, currentPageData);
            switchPage(frontView,backView,currentPage);
        }

    }

    protected void updateGridContent(GridLayout layout,List data){
        layout.removeAllViews();
        for(int i=0, size = data.size(); i<size; i++){
        	//Log.i("VideoFlowFragment", "data = "+data.get(i).toString());
            PosterView posterView = getView(i);
            Object item = data.get(i);
            setView(item, posterView);
            posterView.setTag(item);
            posterView.setVisibility(View.VISIBLE);
            layout.addView(posterView);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mRequestQueue.cancelAll(this);
    }

    @Override
    public boolean onBackPressed() {
        if (mChannelList.hasFocus()){
            return false;
        }
        return mChannelList.requestFocus();
    }

    protected abstract void setView(Object data, PosterView posterView);

    protected void switchPage(GridLayout frontView, GridLayout backView, int currentPage) {
        if (lastPage<currentPage){
            goNext(frontView,backView);
        }else if(lastPage > currentPage){
            goPrev(frontView,backView);
        }
        lastPage = currentPage;
    }

    private void goNext(final GridLayout frontView,final GridLayout backView){
        int width = mViewFlipper.getMeasuredWidth();
        frontView.setTranslationX(0);
        backView.setTranslationX(width);

        frontView.setVisibility(View.VISIBLE);
        backView.setVisibility(View.VISIBLE);

        ObjectAnimator v1 = ObjectAnimator.ofFloat(frontView, "translationX", -width);
        v1.setDuration(500);
        v1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                frontView.setVisibility(View.GONE);
                backView.getChildAt(0).requestFocus();

            }
        });

        ObjectAnimator v2 = ObjectAnimator.ofFloat(backView, "translationX", 0);
        v2.setDuration(500);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(v1,v2);
        set.start();
    }

    private void goPrev(final GridLayout frontView, final GridLayout backView){
        int width = mViewFlipper.getMeasuredWidth();

        backView.setTranslationX(-width);
        frontView.setTranslationX(0);

        frontView.setVisibility(View.VISIBLE);
        backView.setVisibility(View.VISIBLE);

        ObjectAnimator v1 = ObjectAnimator.ofFloat(backView, "translationX", 0);
        v1.setDuration(500);
        v1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                frontView.setVisibility(View.GONE);
                backView.getChildAt(0).requestFocus();
            }
        });

        ObjectAnimator v2 = ObjectAnimator.ofFloat(frontView, "translationX", width);
        v2.setDuration(500);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(v1,v2);
        set.start();
    }

    protected void showLoading(){
        if (mLoading == null){
            mLoading = new LoadingDialog();
        }
        mLoading.show(getFragmentManager(),"loading");
    }

    protected void hiddenLoading() {
        if (mLoading!=null){
            mLoading.dismiss();
        }
    }

    private void setScalePivot(int i, PosterView posterView) {
        float w = getResources().getDimension(R.dimen.image_width);
        float h = getResources().getDimension(R.dimen.image_height);

        if (isFirstRow(i)){
            if (isLastColumn(i)){
                posterView.setPivotX(w);
                posterView.setPivotY(0);
            }else {
                posterView.setPivotX(0);
                posterView.setPivotY(0);
            }
        }
        if (isSecondRow(i)){
            if (isLastColumn(i)){
                posterView.setPivotX(w);
                posterView.setPivotY(h);
            }else {
                posterView.setPivotX(0);
                posterView.setPivotY(h);
            }
        }
    }

    protected <T> List<T> sortEntityByTypeId(List<T> input, Function<T,Integer> function){
        List<Integer> typeSort =
                Lists.newArrayList(
                    VideoType.MOVIE, VideoType.TV, VideoType.VARIETY, VideoType.CARTOON, VideoType.RECORD,
                    VideoType.MUSIC,VideoType.NEWS,VideoType.ENTERTAINMENT, VideoType.FUNNY,VideoType.SPORTS,
                    VideoType.SHORT_VIDEO
                );

        Ordering<T> typeOrdering = Ordering.explicit(typeSort).onResultOf(function);
        return typeOrdering.sortedCopy(input);
    }
}
