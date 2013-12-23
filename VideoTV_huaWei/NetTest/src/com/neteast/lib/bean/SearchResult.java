package com.neteast.lib.bean;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-5
 * Time: 下午4:41
 * To change this template use File | Settings | File Templates.
 */
@Root(name = "result")
public class SearchResult {

    @ElementList(name ="matches",required = false)
    private List<SearchRaw> mVideos;
    @ElementList(name ="filter")
    private List<Filter> mFilters;
    @ElementList(name ="typecount",required = false)
    private List<Type> mTypes;
    @Element(name = "item_title")
    private Title mTitle;

    public List<SearchRaw> getmVideos() {
        return mVideos;
    }

    public void setmVideos(List<SearchRaw> mVideos) {
        this.mVideos = mVideos;
    }

    public List<Filter> getmFilters() {
        return mFilters;
    }

    public void setmFilters(List<Filter> mFilters) {
        this.mFilters = mFilters;
    }

    public List<Type> getmTypes() {
        return mTypes;
    }

    public void setmTypes(List<Type> mTypes) {
        this.mTypes = mTypes;
    }

    public Title getmTitle() {
        return mTitle;
    }

    public void setmTitle(Title mTitle) {
        this.mTitle = mTitle;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "mVideos=" + mVideos +
                ", mFilters=" + mFilters +
                ", mTypes=" + mTypes +
                ", mTitle=" + mTitle +
                '}';
    }

    public static final class SearchRaw{
        @Element
        private long MovieID;
        @Element(required = false)
        private String MovieName;
        @Element(required = false)
        private String Poster;
        @Element(required = false)
        private String Poster2;
        @Element(required = false)
        private String PublishAge;
        @Element(required = false)
        private String AuditDate;
        @Element(required = false)
        private String Area;
        @Element(required = false)
        private int AreaID;
        @Element(required = false)
        private int Count;
        @Element(required = false)
        private int TypeID;
        @Element(required = false)
        private String DramaType;
        @Element(required = false)
        private String Drama;
        @Element(required = false)
        private int VideoID;
        @Element(required = false)
        private String TimeSpan;
        @Element(required = false)
        private String Director;
        @Element(required = false)
        private String Author;
        @Element(required = false)
        private String Station;
        @Element(required = false)
        private String Host;
        @Element(required = false)
        private String Actor;
        @Element(required = false)
        private String Mark;
        @Element(required = false)
        private String Commentary;
        @Element(required = false)
        private String Weight;
        @Element(required = false)
        private int VisitCount;
        @Element(required = false)
        private int EditCount;
        @Element(required = false)
        private int Quality;
        @Element(required = false)
        private String MaxSeries;

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

        public String getPublishAge() {
            return PublishAge;
        }

        public void setPublishAge(String publishAge) {
            PublishAge = publishAge;
        }

        public String getAuditDate() {
            return AuditDate;
        }

        public void setAuditDate(String auditDate) {
            AuditDate = auditDate;
        }

        public String getArea() {
            return Area;
        }

        public void setArea(String area) {
            Area = area;
        }

        public int getAreaID() {
            return AreaID;
        }

        public void setAreaID(int areaID) {
            AreaID = areaID;
        }

        public int getCount() {
            return Count;
        }

        public void setCount(int count) {
            Count = count;
        }

        public int getTypeID() {
            return TypeID;
        }

        public void setTypeID(int typeID) {
            TypeID = typeID;
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

        public int getVideoID() {
            return VideoID;
        }

        public void setVideoID(int videoID) {
            VideoID = videoID;
        }

        public String getTimeSpan() {
            return TimeSpan;
        }

        public void setTimeSpan(String timeSpan) {
            TimeSpan = timeSpan;
        }

        public String getDirector() {
            return Director;
        }

        public void setDirector(String director) {
            Director = director;
        }

        public String getAuthor() {
            return Author;
        }

        public void setAuthor(String author) {
            Author = author;
        }

        public String getStation() {
            return Station;
        }

        public void setStation(String station) {
            Station = station;
        }

        public String getHost() {
            return Host;
        }

        public void setHost(String host) {
            Host = host;
        }

        public String getActor() {
            return Actor;
        }

        public void setActor(String actor) {
            Actor = actor;
        }

        public String getMark() {
            return Mark;
        }

        public void setMark(String mark) {
            Mark = mark;
        }

        public String getCommentary() {
            return Commentary;
        }

        public void setCommentary(String commentary) {
            Commentary = commentary;
        }

        public String getWeight() {
            return Weight;
        }

        public void setWeight(String weight) {
            Weight = weight;
        }

        public int getVisitCount() {
            return VisitCount;
        }

        public void setVisitCount(int visitCount) {
            VisitCount = visitCount;
        }

        public int getEditCount() {
            return EditCount;
        }

        public void setEditCount(int editCount) {
            EditCount = editCount;
        }

        public int getQuality() {
            return Quality;
        }

        public void setQuality(int quality) {
            Quality = quality;
        }

        public String getMaxSeries() {
            return MaxSeries;
        }

        public void setMaxSeries(String maxSeries) {
            MaxSeries = maxSeries;
        }

        @Override
        public String toString() {
            return "SearchRaw{" +
                    "MovieID=" + MovieID +
                    ", MovieName='" + MovieName + '\'' +
                    ", TypeID=" + TypeID +
                    ", DramaType='" + DramaType + '\'' +
                    ", Drama='" + Drama + '\'' +
                    '}';
        }
    }

    public static final class Filter{
        @Element
        private String type;
        @Element
        private String id;
        @Element
        private String text;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return "Filter{" +
                    "type='" + type + '\'' +
                    ", id='" + id + '\'' +
                    ", text='" + text + '\'' +
                    '}';
        }
    }

    public static final class Type{
        @Element
        private int typeid;
        @Element
        private int  typecount;

        public int getTypeid() {
            return typeid;
        }

        public void setTypeid(int typeid) {
            this.typeid = typeid;
        }

        public int getTypecount() {
            return typecount;
        }

        public void setTypecount(int typecount) {
            this.typecount = typecount;
        }

        @Override
        public String toString() {
            return "Type{" +
                    "typeid=" + typeid +
                    ", typecount=" + typecount +
                    '}';
        }
    }

    public static final class Title{
        @Element
        private int total;
        @Element
        private int current_page;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        @Override
        public String toString() {
            return "Title{" +
                    "total=" + total +
                    ", current_page=" + current_page +
                    '}';
        }

        public int getCurrent_page() {
            return current_page;
        }

        public void setCurrent_page(int current_page) {
            this.current_page = current_page;
        }
    }
}
