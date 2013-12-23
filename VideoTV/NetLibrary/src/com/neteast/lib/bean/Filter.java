package com.neteast.lib.bean;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-13
 * Time: 下午4:08
 */
public class Filter {
    private static final String REG="/\\w*/\\w*";

    private List<String> typeNames;
    private Multimap<String,FilterItem> typeItems;

    public Filter(FilterRaw filterRaw) {
        init(filterRaw);
    }

    private void init(FilterRaw filterRaw) {

        typeNames= Lists.newArrayList();
        typeNames.addAll(filterRaw.getTypeNames());

        typeItems= ArrayListMultimap.create();
        Multimap<String,VideoRaw> rawItems = filterRaw.getTypeItems();

        for (String typeName : typeNames) {

            typeItems.put(typeName,new FilterItem("全部","",typeName));

            Collection<VideoRaw> videoRaws = rawItems.get(typeName);
            for (VideoRaw videoRaw : videoRaws) {

                String link = videoRaw.getLink();

                if (link!=null){
                    link=link.replaceFirst(REG,"");
                    typeItems.put(typeName,new FilterItem(videoRaw.getTitle(),link,typeName));
                }
            }
        }
    }

    public List<String> getTypeNames() {
        return typeNames;
    }

    public List<FilterItem> getTypeItems(String typeName){
        return (List<FilterItem>) typeItems.get(typeName);
    }

    public static class FilterItem{
        String title;
        String link;
        String type;

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }

        public String getType() {
            return type;
        }

        public FilterItem(String title, String link,String type) {
            this.title = title;
            this.link = link;
            this.type = type;
        }

        @Override
        public String toString() {
            return type+" "+title+" "+link;
        }
    }
}
