package com.neteast.videotv.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import com.google.common.base.Function;
import com.neteast.lib.bean.SearchResult;
import com.neteast.lib.config.VideoType;
import com.neteast.videotv.R;
import com.neteast.videotv.controller.APIBuilder;
import com.neteast.videotv.controller.VideoFlowController;
import com.neteast.videotv.controller.VideoListController;
import com.neteast.videotv.ui.MainActivity;
import com.neteast.videotv.ui.VideoDetailActivity;
import com.neteast.videotv.ui.widget.PosterView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-10-18
 * Time: 下午5:04
 */
public class SearchResultFragment extends VideoFlowFragment {

    private static final int NO_NEED_FILTER = 0 ;

    private String mKeyword;
    private int mFilterType = NO_NEED_FILTER;
    private boolean mNeedGenerateSearchFilter = true;

    private VideoListController mController;


    public static final SearchResultFragment search(String keyword){
        SearchResultFragment f = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString("keyword",keyword);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mKeyword = getArguments().getString("keyword");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initSearchResultUI();
        return view;
    }

    private void initSearchResultUI(){
        mChannelListScroll.setVisibility(View.GONE);
        mSearchResultFilterScroller.setVisibility(View.VISIBLE);
        mSearchInfo.setVisibility(View.VISIBLE);
        mSearchInfo.setText("");
    }

    @Override
    protected void initController() {
        mController = new VideoListController(getActivity());
        mController.setSinglePageItem(getSinglePageNumber());
        mController.setPreLoadPageNumber(PRE_LOAD_PAGE_NUMBER);
        mController.setPageListener(new VideoFlowController.PageListener<SearchResult.SearchRaw>() {
            @Override
            public void onPageChanged(int currentPage, int totalPage, List currentPageData) {
                if (mNeedGenerateSearchFilter) {
                    mNeedGenerateSearchFilter = false;
                    generateSearchFilterContent();
                }
                hiddenLoading();
                if (currentPageData.size()==0 && currentPage ==1){
                    Toast.makeText(getActivity(),mController.getSearchResultInfo(),Toast.LENGTH_LONG).show();
                    showSearch();
                }else {
                    mSearchInfo.setText(mController.getSearchResultInfo());
                    mCountInfo.setText(currentPage + "/" + totalPage);
                    updatePage(currentPage, currentPageData);
                }
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            }
        });
        reLoadPageData();
    }



    private void generateSearchFilterContent() {
        mSearchResultFilterContainer.removeAllViews();

        LayoutInflater inflater = getActivity().getLayoutInflater();

        List<SearchResult.Type> searchFilers = sortEntityByTypeId(mController.getSearchFilters(),
                new Function<SearchResult.Type, Integer>() {
                    @Override
                    public Integer apply(SearchResult.Type input) {
                        return input.getTypeid();
                    }
        });

        for (SearchResult.Type filter : searchFilers) {
            TextView filterView = (TextView) inflater.inflate(R.layout.view_search_filter_item, mSearchResultFilterContainer, false);
            filterView.setText(VideoType.getNameByType(filter.getTypeid()) + " " + filter.getTypecount());
            filterView.setTag(filter.getTypeid());
            filterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFilterType = (Integer) v.getTag();
                    updateFilterView(v);
                    reLoadPageData();
                }
            });
            mSearchResultFilterContainer.addView(filterView);
        }

    }

    private void reLoadPageData() {
        showLoading();
        mController.reset();
        mController.execute(generateApi(), SearchResult.class);
    }

    private String generateApi() {
        String keyword = mKeyword;
        try {
            keyword = URLEncoder.encode(mKeyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {}

        int currentPage = mController.getHasLoadedPage() +1;
        APIBuilder builder = new APIBuilder()
                .search(keyword)
                .singlePageItem(getSinglePageNumber())
                .preLoadPage(PRE_LOAD_PAGE_NUMBER)
                .currentPage(currentPage);
        if (mFilterType!= NO_NEED_FILTER){
            builder.type(mFilterType);
        }
        return builder.create();
    }


    @Override
    protected void prepareChannelList() {}

    @Override
    protected int getSinglePageNumber() {
        return 10;
    }

    @Override
    protected void onViewClicked(Object data) {
        SearchResult.SearchRaw raw = (SearchResult.SearchRaw) data;
        VideoDetailActivity.newVideoDetail(getActivity(), raw.getMovieID());
    }

    @Override
    protected boolean performScrollNext() {
        if (!mController.hasNextPage()){
            return true;
        }
        if (mController.needLoadData()) {
            showLoading();
            mController.execute(generateApi(),SearchResult.class);
            return true;
        }
        mController.nextPage();
        return true;
    }

    @Override
    protected boolean performScrollPrev() {
        if (mController.hasPrevPage()){
            mController.prevPage();
            return true;
        }else {
            return false;
        }
    }

    @Override
    protected void setView(Object data, PosterView posterView) {
        SearchResult.SearchRaw raw = (SearchResult.SearchRaw) data;
        posterView.setTitle(raw.getMovieName());
        if (raw.getTypeID() == VideoType.SHORT_VIDEO) {
        	posterView.setImage(raw.getPoster2(),mImageLoader);
        	posterView.getImage().setScaleType(ScaleType.FIT_CENTER);
		}else{
			posterView.setImage(raw.getPoster(),mImageLoader);
			posterView.getImage().setScaleType(ScaleType.FIT_XY);
		}
        switch (raw.getTypeID()){
            case VideoType.TV:
            case VideoType.CARTOON:
                try{
                    posterView.setCount(raw.getCount(),Integer.parseInt(raw.getMaxSeries()));
                }catch (Exception e){
                    posterView.setCount(0,0);
                }
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mFilterType != NO_NEED_FILTER){
            backNoFilterResult();
            return true;
        }
        showSearch();
        return true;
    }

    private void showSearch() {
        MainActivity activity= (MainActivity) getActivity();
        activity.showSearch();
    }

    private void backNoFilterResult() {
        mFilterType = NO_NEED_FILTER;
        updateFilterView(null);
        reLoadPageData();
    }
}
