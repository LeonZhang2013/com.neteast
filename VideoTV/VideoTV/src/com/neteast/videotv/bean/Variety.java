package com.neteast.videotv.bean;

import com.google.common.collect.Lists;
import com.neteast.lib.bean.StreamingMediaRaw;
import com.neteast.lib.bean.VideoDetailRaw;
import com.neteast.lib.bean.VideoInfoRaw;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-2
 * Time: 下午9:24
 * To change this template use File | Settings | File Templates.
 */
class Variety extends MultiCountVideo {

    Variety(VideoDetailRaw rawData) {
        super(rawData);
    }

    @Override
    protected void setBaseInfo(VideoInfoRaw videoInfo) {
        baseInfo = Lists.newArrayList();
        baseInfo.add(safeGetBaseInfo("主持：",formatString(videoInfo.getHost()) ) );
        baseInfo.add(safeGetBaseInfo("片长：",videoInfo.getTimeSpan() )+"分钟");
        baseInfo.add(safeGetBaseInfo("地区：",videoInfo.getAreaName() ));
        baseInfo.add(safeGetBaseInfo("年份：",videoInfo.getPublishAge() ));
        baseInfo.add(safeGetBaseInfo("类型：",videoInfo.getDrama() ));
    }


    @Override
    public StreamingMediaRaw.MediaRaw getLastestMedia() {
        String origin = getCurrentOrigin();

        if (origin==null){
            return null;
        }

        List<String> pageTitles = getPageTitles(origin);

        List<StreamingMediaRaw.MediaRaw> medias;

        if (pageTitles==null || pageTitles.size()==0) {
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
