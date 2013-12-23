package com.neteast.lib.bean;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-13
 * Time: 下午3:29
 */
public class FilterRaw {

    final private List<String> typeNames;
    final private Multimap<String,VideoRaw> typeItems;

    public FilterRaw() {
        this.typeNames = Lists.newArrayList();
        this.typeItems= ArrayListMultimap.create();
    }

    public void addType(String typeName,VideoRaw typeItem){

        if (typeName==null || typeItem==null){
            return;
        }

        if (!typeNames.contains(typeName)){
            typeNames.add(typeName);
        }

        typeItems.put(typeName,typeItem);
    }

    public void addTypes(String typeName,List<VideoRaw> typeitems){

        if (typeName==null || typeitems==null || typeitems.size()==0){
            return;
        }
        if (!typeNames.contains(typeName)){
            typeNames.add(typeName);
        }

        typeItems.putAll(typeName, typeitems);
    }

    List<String> getTypeNames() {
        return typeNames;
    }

    Multimap<String, VideoRaw> getTypeItems() {
        return typeItems;
    }
}
