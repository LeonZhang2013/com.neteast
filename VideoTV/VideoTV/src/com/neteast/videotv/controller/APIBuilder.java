package com.neteast.videotv.controller;

import com.neteast.videotv.TVApplication;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-10-12
 * Time: 上午10:48
 */
public class APIBuilder {

   // private static final String BASE_API="http://124.207.5.60:9301/Mobile/moviesearch/httpcheck/tv";


    private static final int SORT_BY_HOT = 1;
    private static final int SORT_BY_TIME = 2;

    private int sort = SORT_BY_HOT;
    private int preLoadPage = 1 ;
    private int singlePageItem = 10 ;
    private int currentPage = 1;
    private int type;
    private int drama;
    private String filter;
    private String keyword;

    public String create() {
        int ps = preLoadPage*singlePageItem;
        int p = ( currentPage%preLoadPage == 0 ? currentPage/preLoadPage : currentPage/preLoadPage +1);
        StringBuilder result =new StringBuilder();
        result.append(TVApplication.API_SEARCH);
        result.append("/sort/").append(sort);
        result.append("/PS/").append(ps);
        result.append("/p/").append(p);

        if (keyword!=null){
            result.append("/search/").append(keyword);
        }

        if (type > 0){
            result.append("/type/").append(type);
        }
        if (drama>0){
            result.append("/drama/").append(drama);
        }
        if (filter!=null){
            result.append(filter);
        }
        return result.toString();
    }

    public APIBuilder sortByHot() {
        sort = SORT_BY_HOT;
        return this;
    }

    public APIBuilder sortByTime(){
        sort = SORT_BY_TIME;
        return this;
    }

    public APIBuilder preLoadPage(int preLoadPage) {
        this.preLoadPage =  preLoadPage;
        return this;
    }

    public APIBuilder singlePageItem(int singlePageItem) {
        this.singlePageItem = singlePageItem;
        return this;
    }

    public APIBuilder currentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public APIBuilder type(int type) {
        this.type = type;
        return this;
    }

    public APIBuilder drama(int drama) {
        this.drama = drama;
        return this;
    }

    public APIBuilder filter(String filter){
        this.filter =filter;
        return this;
    }

    public APIBuilder search(String keyword){
        this.keyword = keyword;
        return this;
    }
}
