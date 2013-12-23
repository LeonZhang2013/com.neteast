package com.neteast.videotv.controller;

import android.app.Activity;
import java.util.ArrayList;
import java.util.List;
import static com.neteast.lib.bean.SearchResult.*;

import com.google.common.collect.Lists;
import com.neteast.lib.bean.SearchResult;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-10-12
 * Time: 上午10:24
 */
public class VideoListController extends VideoFlowController<SearchResult.SearchRaw,SearchResult>{
    private String mSearchInfo;
    private List<Type> mTypes = Lists.newArrayList();

    public VideoListController(Activity activity) {
        super(activity);
    }

    @Override
    protected void onLoadDataSuccess(SearchResult searchResult) {
        mSearchInfo = generateSearchInfo(searchResult);

        setTypeOnlyFirstTime(searchResult);

        mPageDataHandler.appendData(searchResult.getmVideos());
        mPageDataHandler.computePages(searchResult.getmTitle().getTotal());

        searchResult.getmVideos().clear();

        notifyDataChanged(getCurrentPageData());
    }

    private String generateSearchInfo(SearchResult searchResult) {
        StringBuilder builder = new StringBuilder();
        List<Filter> filters = searchResult.getmFilters();
        for (Filter filter : filters) {
            if ("search".equals(filter.getType())){
                builder.append("关键字 \"").append(filter.getText()).append("\" ");
            }
            if ("type".equals(filter.getType())){
                builder.append("(").append(filter.getText()).append(")");
            }
        }
        builder.append("共有 ").append(searchResult.getmTitle().getTotal()).append(" 个结果");
        return builder.toString();
    }

    private void setTypeOnlyFirstTime(SearchResult result){
        if (mTypes.isEmpty() && result.getmTypes()!=null){
            mTypes.addAll(result.getmTypes());
        }
    }

    public void clearSearchFilter(){
        mTypes.clear();
    }

    public String getSearchResultInfo(){
        return mSearchInfo;
    }

    public List<Type> getSearchFilters(){
        return mTypes;
    }

}
