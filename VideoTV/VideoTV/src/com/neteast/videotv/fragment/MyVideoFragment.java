package com.neteast.videotv.fragment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.common.base.Function;
import com.neteast.lib.bean.SearchResult.Type;
import com.neteast.lib.config.VideoType;
import com.neteast.videotv.R;
import com.neteast.videotv.dao.MySQLiteOpenHelper;
import com.neteast.videotv.dao.Poster;
import com.neteast.videotv.controller.MyVideoController;
import com.neteast.videotv.controller.VideoFlowController;
import com.neteast.videotv.listener.DPadListener;
import com.neteast.videotv.ui.MainActivity;
import com.neteast.videotv.ui.VideoDetailActivity;
import com.neteast.videotv.ui.widget.ChannelList;
import com.neteast.videotv.ui.widget.PosterView;
import com.neteast.videotv.utils.Utils;
import com.neteast.videotv.ui.widget.ChannelView;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-10-21
 * Time: 上午10:21
 */
public class MyVideoFragment extends VideoFlowFragment{

    private static final String[] CHANNELS =new String[]{"收藏","追剧","历史记录"};
    private MyVideoController mController;
    private SQLiteDatabase mDb;
    private boolean mNeedGenerateFilterView = true;
    private View mLastFilterView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(getActivity());
        mDb = helper.getWritableDatabase();

        mSearchResultFilterScroller.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    protected void initController() {
        mController = new MyVideoController(getActivity());
        mController.setSinglePageItem(getSinglePageNumber());
        mController.setPageListener(new VideoFlowController.PageListener<Poster>() {
            @Override
            public void onPageChanged(int currentPage, int totalPage, List<Poster> currentPageData) {
                if (needGenerateFilterView()) {
                    mNeedGenerateFilterView = false;
                    generateSearchFilterContent();
                }
                hiddenLoading();
                mCountInfo.setText(currentPage + "/" + totalPage);
                updatePage(currentPage, currentPageData);
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean needGenerateFilterView(){
        return mNeedGenerateFilterView && mController.getResultCount()>0 ;
    }

    private void generateSearchFilterContent() {
        addFilterView("全部 " + mController.getResultCount(), 0);
        List<Type> searchFilers = sortEntityByTypeId(mController.getSearchFilters(),new Function<Type, Integer>() {
            @Override
            public Integer apply(Type input) {
                return input.getTypeid();
            }
        });

        for (Type filter : searchFilers) {
            String title = VideoType.getNameByType(filter.getTypeid()) + " " + filter.getTypecount();
            addFilterView(title, filter.getTypeid());
        }
    }

    private void addFilterView(String title, int type) {
        TextView filterView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.view_search_filter_item, mSearchResultFilterContainer, false);
        filterView.setText(title);
        filterView.setTag(type);
        filterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int type = (Integer) v.getTag();
                updateFilterView(v);
                mController.filter(type);
            }
        });
        mSearchResultFilterContainer.addView(filterView);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mDb!=null){
            mDb.close();
            mDb = null;
        }
    }


    @Override
    protected void prepareChannelList() {
        mChannelList.setSelectionChangedListener(new ChannelList.SelectionChangedListener() {
            @Override
            public void onSelectionChanged(int lastSelectedIndex, int currentSelectedIndex) {
                showLoading();
                initFilterUI();
                reloadPageData(currentSelectedIndex);
            }
        });
        mChannelList.setAdapter(new ChannelAdapter(getActivity(),CHANNELS));
        mChannelList.setDefaultSelected();
    }

    private void reloadPageData(int currentSelectedIndex) {
        mController.reset();
        mController.execute(mDb,currentSelectedIndex);
    }

    private void initFilterUI() {
        mSearchResultFilterContainer.removeAllViews();
        mNeedGenerateFilterView = true;
    }

    @Override
    protected int getSinglePageNumber() {
        return 10;
    }

    @Override
    protected void onViewClicked(Object data) {
        Poster poster = (Poster) data;
        VideoDetailActivity.newVideoDetail(getActivity(),poster.getMovieId());
    }

    @Override
    protected boolean performScrollNext() {
        if (mController.hasNextPage()){
            mController.nextPage();
        }
        return true;
    }

    @Override
    protected boolean performScrollPrev() {
        if (mController.hasPrevPage()){
            mController.prevPage();
        }else {
            mChannelList.requestFocus();
        }
        return true;
    }

    @Override
    protected void setView(Object data, PosterView posterView) {
        Poster poster = (Poster) data;
        posterView.setImage(poster.getImage(),mImageLoader);
        posterView.setTitle(poster.getTitle());
        posterView.setCount(poster.getCount(),poster.getMaxSeries());
    }

    public class ChannelAdapter extends ArrayAdapter<String> {

        public ChannelAdapter(Context context, String[] datas) {
            super(context, 0, datas);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ChannelView channelView = new ChannelView(getContext());
            channelView.setChannel(getItem(position));
            channelView.setDpadListener(mChannelDpadListener);
            channelView.setOnFocusChangeListener(mFocusChangedListener);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            Utils.dp2px(getContext(), 55)
                    );
            params.gravity = Gravity.CENTER_VERTICAL;
            channelView.setLayoutParams(params);
            return channelView;
        }

        private DPadListener mChannelDpadListener = new DPadListener() {
            @Override
            public boolean onClickLeft() {
                MainActivity activity= (MainActivity) getActivity();
                activity.showLeftNav();
                return false;
            }
            @Override
            public boolean onClickRight() {
                MainActivity activity= (MainActivity) getActivity();
                activity.hiddenLeftNav();
                return false;
            }
        };

        private View.OnFocusChangeListener mFocusChangedListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    MainActivity activity= (MainActivity) getActivity();
                    activity.hiddenLeftNav();
                }
            }
        };
    }
}
