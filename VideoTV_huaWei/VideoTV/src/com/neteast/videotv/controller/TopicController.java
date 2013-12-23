package com.neteast.videotv.controller;

import android.app.Activity;
import android.util.Log;

import com.google.common.collect.Lists;
import com.neteast.lib.bean.SearchResult.Type;
import com.neteast.lib.bean.VideoRaw;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-10-10
 * Time: 下午4:31
 */
public class TopicController extends VideoFlowController<VideoRaw,VideoRaw.Result> {
	
    private List<VideoRaw> mData = Lists.newArrayList();
    private List<Type> mTypes = Lists.newArrayList();
    
    public TopicController(Activity activity) {
        super(activity);
    }

    public List<Type> getTypes(){
        return mTypes;
    }

    public void filter(int type){
        if (type==0){
            mPageDataHandler.setData(mData);
            notifyDataChanged(getCurrentPageData());
            return;
        }

        mPageDataHandler.setData(getDataByType(type));
        notifyDataChanged(getCurrentPageData());
    }

    private List<VideoRaw> getDataByType(int typeId){
    	Log.i("TopicController", "typeId = "+typeId);
        List<VideoRaw> data = Lists.newArrayList();
        for (VideoRaw videoRaw : mData) {
            if (videoRaw.getTypeID() == typeId){
            	Log.i("TopicController", "videoRaw = "+videoRaw.toString());
                data.add(videoRaw);
            }
        }
        return data;
    }

    @Override
    protected void onLoadDataSuccess(VideoRaw.Result response) {
        List<VideoRaw> videoRawList = response.getVideoRawList();
        mData.clear();
        mData.addAll(videoRawList);
        setTypeOnlyFirstTime();
        mPageDataHandler.setData(videoRawList);
        notifyDataChanged(getCurrentPageData());
    }

    @Override
    public void reset() {
        mData.clear();
        mTypes.clear();
        super.reset();
    }

    private void setTypeOnlyFirstTime() {
        if (mTypes.isEmpty()) {
            for (VideoRaw videoRaw : mData) {
                Type type = getType(videoRaw.getTypeID());
                if (type == null){
                    type = new Type();
                    type.setTypeid(videoRaw.getTypeID());
                    type.setTypecount(1);
                    mTypes.add(type);
                }else {
                    type.setTypecount(type.getTypecount()+1);
                }
            }
        }
    }

    private Type getType(int typeId){
        for (Type type : mTypes) {
            if (type.getTypeid() == typeId){
                return type;
            }
        }
        return null;
    }

}
