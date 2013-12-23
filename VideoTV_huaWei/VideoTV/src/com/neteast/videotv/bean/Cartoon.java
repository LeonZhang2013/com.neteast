package com.neteast.videotv.bean;

import android.text.TextUtils;
import com.google.common.collect.Lists;
import com.neteast.lib.bean.StreamingMediaRaw;
import com.neteast.lib.bean.VideoDetailRaw;
import com.neteast.lib.bean.VideoInfoRaw;
import com.neteast.videotv.dao.AsyncPosterQuery;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-2
 * Time: 下午9:17
 * To change this template use File | Settings | File Templates.
 */
class Cartoon extends MultiCountVideo{

    Cartoon(VideoDetailRaw rawData) {
        super(rawData);
    }

    @Override
    protected void setBaseInfo(VideoInfoRaw videoInfo) {
        baseInfo = Lists.newArrayList();
        baseInfo.add(safeGetBaseInfo("作者：",formatString(videoInfo.getAuthor() ) ) );
        baseInfo.add(safeGetBaseInfo("片长：",videoInfo.getTimeSpan() )+"分钟");
        baseInfo.add(safeGetBaseInfo("地区：",videoInfo.getAreaName() ));
        baseInfo.add(safeGetBaseInfo("年份：",videoInfo.getPublishAge() ));
        baseInfo.add(safeGetBaseInfo("类型：",videoInfo.getDrama() ));
    }

    private boolean completed;

    @Override
    public void setTitle(String originTitle) {
        StreamingMediaRaw.MediaRaw lastestMedia = getLastestMedia();
        if (lastestMedia!=null &&!TextUtils.isEmpty(lastestMedia.getSeries())){
            if (videoInfo.getCount()>0){
                try {
                    Integer reallyCount = Integer.valueOf(lastestMedia.getSeries());
                    if (reallyCount==videoInfo.getCount()){
                        completed=true;
                        this.title=originTitle+"(全集)";
                        return;
                    }
                }catch (NumberFormatException e){}
            }
            this.title=originTitle+"(更新至"+lastestMedia.getSeries()+"集)";
        }else {
            this.title=originTitle;
        }
    }
    @Override
    public StreamingMediaRaw.MediaRaw getLastestMedia() {

        String origin = getCurrentOrigin();
        if (origin==null){
            return null;
        }

        List<String> pageTitles = getPageTitles(origin);

        List<StreamingMediaRaw.MediaRaw> medias;

        if (pageTitles==null||pageTitles.size()==0) {
            medias=getMedias(origin, null);
        }else {
            String pageTitle = pageTitles.get(pageTitles.size() - 1);
            medias=getMedias(origin, pageTitle);
        }

        if (medias==null || medias.size()==0){
            return null;
        }

        return medias.get(medias.size()-1);

    }

    @Override
    public String getPlayButtonText() {
        if (completed && getFirstMedia()!=null){
            return "第"+getFirstMedia().getSeries()+"集";
        }
        return super.getPlayButtonText();    //To change body of overridden methods use File | Settings | File Templates.
    }


    @Override
    public StreamingMediaRaw.MediaRaw getDefaultPlayMedia() {
        if (completed){
            return getFirstMedia();
        }
        return super.getDefaultPlayMedia();    //To change body of overridden methods use File | Settings | File Templates.
    }

    private StreamingMediaRaw.MediaRaw getFirstMedia() {
        String origin = getCurrentOrigin();
        if (origin==null){
            return null;
        }

        List<String> pageTitles = getPageTitles(origin);

        List<StreamingMediaRaw.MediaRaw> medias;

        if (pageTitles==null||pageTitles.size()==0) {
            medias=getMedias(origin, null);
        }else {
            String pageTitle = pageTitles.get(0);
            medias=getMedias(origin, pageTitle);
        }

        if (medias==null || medias.size()==0){
            return null;
        }

        return medias.get(0);
    }
}
