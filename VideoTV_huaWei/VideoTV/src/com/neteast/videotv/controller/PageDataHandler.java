package com.neteast.videotv.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-10-14
 * Time: 上午11:04
 */
public class PageDataHandler<T> {

    private static final int DEFAULT_PAGE_ITEM_NUMBER=10;
    private int mSinglePageItem =DEFAULT_PAGE_ITEM_NUMBER;

    private int mCurrentPage = 1;
    private int mTotalPage = 1;
    private List<T> mData = new ArrayList<T>();

    public PageDataHandler() {
        reset();
    }

    public PageDataHandler(List<T> data) {
        setData(data);
    }

    public void setSinglePageItem(int singlePageItem){
        mSinglePageItem = singlePageItem;
    }

    public int getSinglePageItem() {
        return mSinglePageItem;
    }

    public List<T> getCurrentPageData(){
        return getPageData(mCurrentPage);
    }

    public boolean hasNextPage(){
        return mCurrentPage < getTotalPage();
    }

    public List<T> getNextPageData(){
        if (hasNextPage()){
            return getPageData(mCurrentPage + 1);
        }else {
            return Collections.emptyList();
        }
    }

    public boolean hasPrevPage(){
        return mCurrentPage > 1;
    }

    public List<T> getPrevPageData(){
        if (hasPrevPage()){
            return getPageData(mCurrentPage -1);
        }else {
            return Collections.emptyList();
        }
    }

    public List<T> nextPage(){
        if (hasNextPage()){
            mCurrentPage++;
            return getCurrentPageData();
        }
        return Collections.emptyList();
    }

    public List<T> prevPage(){
        if (hasPrevPage()){
            mCurrentPage--;
            return getCurrentPageData();
        }
        return Collections.emptyList();
    }

    public void reset(){
        mCurrentPage = 1;
        mTotalPage = 1;
        mData.clear();
    }

    public int getCurrentPage(){
        return mCurrentPage;
    }

    public int getTotalPage(){
        return mTotalPage;
    }

    public void setTotalPage(int totalPage){
        mTotalPage = totalPage;
    }

    List<T> getData(){
        return mData;
    }

    void setData(List<T> data){
        reset();
        if (data !=null){
            mData.addAll(data);
        }
        computePages(mData.size());
    }

    void appendData(List<T> data){
        if (data!=null){
            mData.addAll(data);
        }
    }

    void clearData(){
        mData.clear();
    }

    public void computePages(int totalCount) {
        int singlePageItem = getSinglePageItem();
        int totalPage = (totalCount % singlePageItem == 0 ? totalCount / singlePageItem : totalCount / singlePageItem + 1);
        if (totalPage == 0){
            totalPage =1;
        }
        setTotalPage(totalPage);
    }

    private List<T> getPageData(int page) {
        int singlePageItem = getSinglePageItem();
        List<T> data = getData();
        int startIndex = (page - 1) * singlePageItem;
        int endIndex = page * singlePageItem;
        if (endIndex > data.size()) {
            endIndex = data.size();
        }
        return data.subList(startIndex, endIndex);
    }

}
