package com.neteast.lib.bean;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-2
 * Time: 下午4:19
 * To change this template use File | Settings | File Templates.
 */
public class RelationRaw {
    @Element
    private long  MovieID;
    @Element
    private String MovieName;
    @Element(required = false)
    private String Director;
    @Element(required = false)
    private String Actor;
    @Element(required = false)
    private String Author;
    @Element(required = false)
    private String Host;
    @Element(required = false)
    private String Type;
    @Element(required = false)
    private int TypeID;
    @Element(required = false)
    private String Area;
    @Element(required = false)
    private String AreaID;
    @Element(required = false)
    private String PublishAge;
    @Element(required = false)
    private int Count;
    @Element(required = false)
    private String Poster;
    @Element(required = false)
    private String Poster2;
    @Element(required = false)
    private String MaxSeries;
    @Element(required = false)
    private String DramaType;
    @Element(required = false)
    private String Drama;
    @Element(required = false)
    private String Commentary;
    @Element(required = false)
    private String TimeSpan;
    @Element(required = false)
    private int weight;

    public long getMovieID() {
        return MovieID;
    }

    public void setMovieID(long movieID) {
        MovieID = movieID;
    }

    public String getMovieName() {
        return MovieName;
    }

    public void setMovieName(String movieName) {
        MovieName = movieName;
    }

    public String getDirector() {
        return Director;
    }

    public void setDirector(String director) {
        Director = director;
    }

    public String getActor() {
        return Actor;
    }

    public void setActor(String actor) {
        Actor = actor;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getHost() {
        return Host;
    }

    public void setHost(String host) {
        Host = host;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public int getTypeID() {
        return TypeID;
    }

    public void setTypeID(int typeID) {
        TypeID = typeID;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public String getAreaID() {
        return AreaID;
    }

    public void setAreaID(String areaID) {
        AreaID = areaID;
    }

    public String getPublishAge() {
        return PublishAge;
    }

    public void setPublishAge(String publishAge) {
        PublishAge = publishAge;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public String getPoster() {
        return Poster;
    }

    public void setPoster(String poster) {
        Poster = poster;
    }

    public String getPoster2() {
        return Poster2;
    }

    public void setPoster2(String poster2) {
        Poster2 = poster2;
    }

    public String getMaxSeries() {
        return MaxSeries;
    }

    public void setMaxSeries(String maxSeries) {
        MaxSeries = maxSeries;
    }

    public String getDramaType() {
        return DramaType;
    }

    public void setDramaType(String dramaType) {
        DramaType = dramaType;
    }

    public String getDrama() {
        return Drama;
    }

    public void setDrama(String drama) {
        Drama = drama;
    }

    public String getCommentary() {
        return Commentary;
    }

    public void setCommentary(String commentary) {
        Commentary = commentary;
    }

    public String getTimeSpan() {
        return TimeSpan;
    }

    public void setTimeSpan(String timeSpan) {
        TimeSpan = timeSpan;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Relation{" +
                "MovieID=" + MovieID +
                ", MovieName='" + MovieName + '\'' +
                ", Director='" + Director + '\'' +
                ", Actor='" + Actor + '\'' +
                ", Author='" + Author + '\'' +
                ", Host='" + Host + '\'' +
                ", Type='" + Type + '\'' +
                ", TypeID='" + TypeID + '\'' +
                ", Area='" + Area + '\'' +
                ", AreaID='" + AreaID + '\'' +
                ", PublishAge='" + PublishAge + '\'' +
                ", Count='" + Count + '\'' +
                ", Poster='" + Poster + '\'' +
                ", Poster2='" + Poster2 + '\'' +
                ", MaxSeries='" + MaxSeries + '\'' +
                ", DramaType='" + DramaType + '\'' +
                ", Drama='" + Drama + '\'' +
                ", Commentary='" + Commentary + '\'' +
                ", TimeSpan='" + TimeSpan + '\'' +
                ", weight=" + weight +
                '}';
    }

    @Root(name = "result")
    public static final class Result{
        @ElementList(entry = "relation_item",inline = true)
        List<RelationRaw> relationRaws;

        public List<RelationRaw> getRelationRaws() {
            return relationRaws;
        }

        public void setRelationRaws(List<RelationRaw> relationRaws) {
            this.relationRaws = relationRaws;
        }
    }

}
