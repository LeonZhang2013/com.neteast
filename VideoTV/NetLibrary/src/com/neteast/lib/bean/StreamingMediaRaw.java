package com.neteast.lib.bean;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-7-26
 * Time: 下午3:36
 * To change this template use File | Settings | File Templates.
 */
public class StreamingMediaRaw {
    @Element(name = "HttpTags",required = false)
    private String tags;
    @Element(name = "http_count",required = false)
    private int mediaTotalCount;
    @ElementList(entry = "http_item",required = false,inline = true)
    private List<MediaRaw> mediaRawList;

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getMediaTotalCount() {
        return mediaTotalCount;
    }

    public void setMediaTotalCount(int mediaTotalCount) {
        this.mediaTotalCount = mediaTotalCount;
    }

    public List<MediaRaw> getMediaRawList() {
        return mediaRawList;
    }

    public void setMediaRawList(List<MediaRaw> mediaRawList) {
        this.mediaRawList = mediaRawList;
    }

    @Override
    public String toString() {
        return "StreamingMedia{" +
                "tags='" + tags + '\'' +
                ", mediaTotalCount=" + mediaTotalCount +
                ", mediaList=" + mediaRawList +
                '}';
    }

    public static class MediaRaw {
        @Element(name = "mid",required = true)
        private long movieId;
        @Element(name = "id",required = true)
        private long mediaId;
        @Element(name = "title",required = true)
        private String title;
        @Element(name = "url",required = false)
        private String url;
        @Element(name = "tag",required = false)
        private String tag;
        @Element(name = "md5url",required = false)
        private String md5URL;
        @Element(name = "tagname",required = false)
        private String tagName;
        @Element(name = "swfurl",required = false)
        private String swfURL;
        @Element(name = "series",required = false)
        private String series;
        @Element(name = "mp4url",required = false)
        private String mp4URL;
        @Element(name = "cleartitle",required = false)
        private String clearTitle;
        @Element(name = "Quality",required = false)
        private int quality;

        public long getMovieId() {
            return movieId;
        }

        public void setMovieId(long movieId) {
            this.movieId = movieId;
        }

        public long getMediaId() {
            return mediaId;
        }

        public void setMediaId(long mediaId) {
            this.mediaId = mediaId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getMd5URL() {
            return md5URL;
        }

        public void setMd5URL(String md5URL) {
            this.md5URL = md5URL;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public String getSwfURL() {
            return swfURL;
        }

        public void setSwfURL(String swfURL) {
            this.swfURL = swfURL;
        }

        public String getSeries() {
            return series;
        }

        public void setSeries(String series) {
            this.series = series;
        }

        public String getMp4URL() {
            return mp4URL;
        }

        public void setMp4URL(String mp4URL) {
            this.mp4URL = mp4URL;
        }

        public String getClearTitle() {
            return clearTitle;
        }

        public void setClearTitle(String clearTitle) {
            this.clearTitle = clearTitle;
        }

        public int getQuality() {
            return quality;
        }

        public void setQuality(int quality) {
            this.quality = quality;
        }

        @Override
        public String toString() {
            return "Media{" +
                    "movieId=" + movieId +
                    ", mediaId=" + mediaId +
                    ", title='" + title + '\'' +
                    ", url='" + url + '\'' +
                    ", tag='" + tag + '\'' +
                    ", md5URL='" + md5URL + '\'' +
                    ", tagName='" + tagName + '\'' +
                    ", swfURL='" + swfURL + '\'' +
                    ", series='" + series + '\'' +
                    ", mp4URL='" + mp4URL + '\'' +
                    ", clearTitle='" + clearTitle + '\'' +
                    ", quality=" + quality +
                    '}';
        }
    }
}
