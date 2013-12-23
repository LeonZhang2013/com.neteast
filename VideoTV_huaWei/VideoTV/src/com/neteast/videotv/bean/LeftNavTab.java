package com.neteast.videotv.bean;

import com.google.common.collect.Lists;
import com.neteast.videotv.R;
import com.neteast.videotv.TVApplication;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-7-29
 * Time: 下午2:27
 * To change this template use File | Settings | File Templates.
 */
public class LeftNavTab {

    private int icon;
    private int text;
    private String api;
    private boolean hasSubList;

    private final static List<LeftNavTab> sNavTabs;

    static {
        sNavTabs= Lists.newArrayList();

        LeftNavTab tab = new LeftNavTab(R.drawable.ic_nav_homepage, R.string.nav_homepage, "LSTV_index");
        tab.hasSubList=false;
        sNavTabs.add(tab);

        tab = new LeftNavTab(R.drawable.ic_nav_featured_site, R.string.nav_featured_site, "LSTV_topic");
        tab.hasSubList=true;
        sNavTabs.add(tab);

        tab = new LeftNavTab(R.drawable.ic_nav_channel_category, R.string.nav_channel_category, "LSTV_channel");
        tab.hasSubList=true;
        sNavTabs.add(tab);

        tab = new LeftNavTab(R.drawable.ic_nav_my_video, R.string.nav_my_video, null);
        tab.hasSubList=true;
        sNavTabs.add(tab);

        tab = new LeftNavTab(R.drawable.ic_nav_search, R.string.nav_search, "LSTV_keyword");
        tab.hasSubList=false;
        sNavTabs.add(tab);

        tab = new LeftNavTab(R.drawable.ic_nav_setting, R.string.nav_setting, null);
        tab.hasSubList=false;
        sNavTabs.add(tab);
    }

    public static final List<LeftNavTab> getLeftNavTabs(){
         return sNavTabs;
    }

    private LeftNavTab(int icon, int text, String menuKey) {
        this.icon = icon;
        this.text = text;
        if (menuKey!=null){
            this.api = String.format(TVApplication.API_MENU_LIST,menuKey);
        }
    }


    public int getIcon() {
        return icon;
    }


    public int getText() {
        return text;
    }

    public String getApi() {
        return api;
    }

    public boolean isHasSubList() {
        return hasSubList;
    }

    public boolean needNetWork(){
        return  api!=null;
    }
}
