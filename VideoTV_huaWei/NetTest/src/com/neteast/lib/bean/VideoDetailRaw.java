package com.neteast.lib.bean;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-7-26
 * Time: 下午3:48
 * To change this template use File | Settings | File Templates.
 */
@Root(name = "result",strict = false)
public class VideoDetailRaw {

    @ElementList(name = "movie_list")
    private List<VideoInfoRaw> infoList;
    @Element(name = "http_list")
    private StreamingMediaRaw streamingMediaRaw;
    
    public List<VideoInfoRaw> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<VideoInfoRaw> infoList) {
        this.infoList = infoList;
    }

    public StreamingMediaRaw getStreamingMediaRaw() {
        return streamingMediaRaw;
    }

    public void setStreamingMediaRaw(StreamingMediaRaw streamingMediaRaw) {
        this.streamingMediaRaw = streamingMediaRaw;
    }

    public VideoInfoRaw getVideoInfo(){
        if (infoList!=null && infoList.size()>0){
            return infoList.get(0);
        }
        return null;
    }
}
