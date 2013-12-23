package com.neteast.lib.bean;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-7-31
 * Time: 下午5:04
 * To change this template use File | Settings | File Templates.
 */
@Root(strict = false,name = "R")
public class FLVCDRaw {

    @ElementList(entry = "V",inline = true)
    private List<V> items;

    public List<V> getItems() {
        return items;
    }

    public void setItems(List<V> items) {
        this.items = items;
    }

    public static final class V{
        @Element(name = "U")
        private String playUrl;

        public String getPlayUrl() {
            return playUrl;
        }

        public void setPlayUrl(String playUrl) {
            this.playUrl = playUrl;
        }

        @Override
        public String toString() {
            return "N{" +
                    "playUrl='" + playUrl + '\'' +
                    '}';
        }
    }
}
