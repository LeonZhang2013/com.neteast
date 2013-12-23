package com.neteast.videotv.bean;

import com.google.common.collect.Lists;
import com.neteast.lib.bean.StreamingMediaRaw;
import com.neteast.lib.bean.VideoDetailRaw;
import com.neteast.lib.bean.VideoInfoRaw;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-1
 * Time: 上午11:32
 * To change this template use File | Settings | File Templates.
 */
class Movie extends VideoDetail{

    Movie(VideoDetailRaw rawData) {
        super(rawData);
    }

    @Override
    protected void setBaseInfo(VideoInfoRaw videoInfo) {
        baseInfo = Lists.newArrayList();
        baseInfo.add(safeGetBaseInfo("导演：",formatString(videoInfo.getDirector() ) ) );
        baseInfo.add(safeGetBaseInfo("主演：",formatString(videoInfo.getActor() ) ) );
        baseInfo.add(safeGetBaseInfo("片长：",videoInfo.getTimeSpan() )+"分钟");
        baseInfo.add(safeGetBaseInfo("地区：",videoInfo.getAreaName() ));
        baseInfo.add(safeGetBaseInfo("年份：",videoInfo.getPublishAge() ));
        baseInfo.add(safeGetBaseInfo("类型：",videoInfo.getDrama() ));
    }



    @Override
    public void setTitle(String originTitle) {
        this.title=originTitle;
    }

    @Override
    public String getPlayButtonText() {
        return "播 放";
    }

    @Override
    public String getCollectionText() {
        return "收 藏";
    }

    @Override
    public String getHasCollectionText() {
        return "已 收 藏";
    }
    @Override
    public boolean needSpit() {
        String origin = getCurrentOrigin();
        if (origin==null){
            return false;
        } 
        String pageTitle = null;
        List<String> pageTitles = streamingMedia.getPageTitles(origin);
        if(pageTitles!=null && pageTitles.size()>0){
        	pageTitle = pageTitles.get(0);
        }
        List<StreamingMediaRaw.MediaRaw> medias=streamingMedia.getMedias(origin,pageTitle);
        if (medias==null || medias.size()==0){
            return false;
        }
        return super.needSpit() && medias.size()>1;
    }
    
    @Override
    public StreamingMediaRaw.MediaRaw getLastestMedia() {
        String origin = getCurrentOrigin();
        if (origin==null){
            return null;
        } 
        String pageTitle = null;
        List<String> pageTitles = streamingMedia.getPageTitles(origin);
        if(pageTitles!=null && pageTitles.size()>0){
        	pageTitle = pageTitles.get(0);
        }
        List<StreamingMediaRaw.MediaRaw> medias=streamingMedia.getMedias(origin,pageTitle);
        if (medias==null || medias.size()==0){
            return null;
        }
        return medias.get(0);
    }
    
    

}
