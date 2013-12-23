package com.neteast.videotv.bean;

import android.text.TextUtils;
import com.neteast.lib.bean.StreamingMediaRaw;
import com.neteast.lib.bean.VideoDetailRaw;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-4
 * Time: 下午2:18
 * To change this template use File | Settings | File Templates.
 */
abstract class MultiCountVideo extends VideoDetail {

    MultiCountVideo(VideoDetailRaw rawData) {
        super(rawData);
    }


    @Override
    public void setTitle(String originTitle) {
        StreamingMediaRaw.MediaRaw lastestMedia = getLastestMedia();
        if (lastestMedia!=null &&!TextUtils.isEmpty(lastestMedia.getSeries())){
            this.title=originTitle+"(更新至"+lastestMedia.getSeries()+"集)";
        }else {
            this.title=originTitle;
        }
    }

    @Override
    public String getPlayButtonText() {
        StreamingMediaRaw.MediaRaw lastestMedia = getLastestMedia();
        if (lastestMedia!=null && !TextUtils.isEmpty(lastestMedia.getSeries())){
            return "第"+lastestMedia.getSeries()+"集";
        }else {
            return "播 放";
        }
    }

    @Override
    public String getCollectionText() {
        return "追 剧";
    }

    @Override
    public String getHasCollectionText() {
        return "已 追 剧";
    }

}
