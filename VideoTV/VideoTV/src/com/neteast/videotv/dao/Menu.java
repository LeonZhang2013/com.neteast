package com.neteast.videotv.dao;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-16
 * Time: 上午11:02
 */
public class Menu {

    private long menuId;
    private String title;
    private String action;
    private int defaultIndex;
    private int currentIndex;

    public long getMenuId() {
        return menuId;
    }

    public void setMenuId(long menuId) {
        this.menuId = menuId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getDefaultIndex() {
        return defaultIndex;
    }

    public void setDefaultIndex(int defaultIndex) {
        this.defaultIndex = defaultIndex;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public static class Result{
        private List<Menu> menus;

        public List<Menu> getMenus() {
            return menus;
        }

        public void setMenus(List<Menu> menus) {
            this.menus = menus;
        }
    }
}
