package com.neteast.videotv.bean;

import com.neteast.lib.bean.VideoRaw;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-7-29
 * Time: 下午5:48
 * To change this template use File | Settings | File Templates.
 */
public class HomePageFrame implements SimpleImageFrame{
    private long movieId;
    private String title;
    private String countInfo;
    private boolean showCountInfo;
    private String comment;
    private String actors;
    private String type;
    private String imageUrl;
    private VideoRaw mRawData;

    public HomePageFrame(VideoRaw rawData) {
        this.mRawData = rawData;
        movieId=rawData.getMovieID();
        title=rawData.getTitle();
        countInfo=String.format("(更新到%d集)",rawData.getCount());
        showCountInfo=rawData.getCount()>0;
        String[] split = rawData.getContent().split("\\*");
        comment=split[0];
        if (split.length>1){
            actors=split[1];
        } else {
            actors="";
        }
        type="类型："+rawData.getDrama();
        imageUrl=mRawData.getImage();
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCountInfo() {
        return countInfo;
    }

    public void setCountInfo(String countInfo) {
        this.countInfo = countInfo;
    }

    public boolean isShowCountInfo() {
        return showCountInfo;
    }

    public void setShowCountInfo(boolean showCountInfo) {
        this.showCountInfo = showCountInfo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HomePageFrame frame = (HomePageFrame) o;

        if (movieId != frame.movieId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (movieId ^ (movieId >>> 32));
    }
}
