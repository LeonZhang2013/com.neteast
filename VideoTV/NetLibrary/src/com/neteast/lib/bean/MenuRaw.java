package com.neteast.lib.bean;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-7-26
 * Time: 下午3:05
 * To change this template use File | Settings | File Templates.
 */
public class MenuRaw {
    @Element(name = "MenuID",required = false)
    private long  menuId;
    @Element(name = "Title",required = false)
    private String  title;
    @Element(name = "LinkUrl",required = false)
    private String  linkUrl;
    @Element(name = "Keywords",required = false)
    private String  keywords;
    @Element(name = "Description",required = false)
    private String  description;
    @Element(name = "Type",required = false)
    private int  type;

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

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "menuId=" + menuId +
                ", title='" + title + '\'' +
                ", linkUrl='" + linkUrl + '\'' +
                ", keywords='" + keywords + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                '}';
    }

    @Root(name = "result",strict = false)
    public static class Result{
        @ElementList(entry = "item",inline = true,required = true)
        private List<MenuRaw> menuList;

        public List<MenuRaw> getMenuList() {
            return menuList;
        }

        public void setMenuList(List<MenuRaw> menuList) {
            this.menuList = menuList;
        }
    }
}
