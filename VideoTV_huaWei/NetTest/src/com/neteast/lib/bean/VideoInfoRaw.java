package com.neteast.lib.bean;

import org.simpleframework.xml.Element;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-7-26
 * Time: 下午3:16
 * To change this template use File | Settings | File Templates.
 */
public class VideoInfoRaw {

    @Element(name = "MovieID" )
    private int movieId;
    @Element(name = "MovieName")
    private String movieName;
    @Element(name = "Alias",required = false)
    private String alias;
    @Element(name = "TypeID",required = false)
    private int typeId;
    @Element(name = "DramaType",required = false)
    private String dramaType;
    @Element(name = "Drama",required = false)
    private String drama;
    @Element(name = "TimeSpan",required = false)
    private String timeSpan;
    @Element(name = "Director",required = false)
    private String director;
    @Element(name = "Actor",required = false)
    private String actor;
    @Element(name = "AreaID",required = false)
    private int areaID;
    @Element(name = "AreaName",required = false)
    private String areaName;
    @Element(name = "PublishAge",required = false)
    private String publishAge;
    @Element(name = "Poster",required = false)
    private String poster;
    @Element(name = "Poster2",required = false)
    private String poster2;
    @Element(name = "Host",required = false)
    private String host;
    @Element(name = "Author",required = false)
    private String author;
    @Element(name = "Count",required = false)
    private int count;
    @Element(name = "Description",required = false)
    private String description;
    @Element(name = "Commentary",required = false)
    private String commentary;
    @Element(name = "Tags",required = false)
    private String tags;
    @Element(name = "Mark",required = false)
    private String mark;

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getDramaType() {
        return dramaType;
    }

    public void setDramaType(String dramaType) {
        this.dramaType = dramaType;
    }

    public String getDrama() {
        return drama;
    }

    public void setDrama(String drama) {
        this.drama = drama;
    }

    public String getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(String timeSpan) {
        this.timeSpan = timeSpan;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public int getAreaID() {
        return areaID;
    }

    public void setAreaID(int areaID) {
        this.areaID = areaID;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getPublishAge() {
        return publishAge;
    }

    public void setPublishAge(String publishAge) {
        this.publishAge = publishAge;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getPoster2() {
        return poster2;
    }

    public void setPoster2(String poster2) {
        this.poster2 = poster2;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "movieId=" + movieId +
                ", movieName='" + movieName + '\'' +
                ", alias='" + alias + '\'' +
                ", typeId=" + typeId +
                ", dramaType=" + dramaType +
                ", drama='" + drama + '\'' +
                ", timeSpan=" + timeSpan +
                ", director='" + director + '\'' +
                ", actor='" + actor + '\'' +
                ", areaID=" + areaID +
                ", areaName='" + areaName + '\'' +
                ", publishAge='" + publishAge + '\'' +
                ", poster='" + poster + '\'' +
                ", poster2='" + poster2 + '\'' +
                ", host='" + host + '\'' +
                ", author='" + author + '\'' +
                ", count=" + count +
                ", description='" + description + '\'' +
                ", commentary='" + commentary + '\'' +
                ", tags='" + tags + '\'' +
                ", mark=" + mark +
                '}';
    }
}
