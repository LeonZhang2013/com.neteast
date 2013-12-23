package com.neteast.lib.bean;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-7-26
 * Time: 下午2:11
 * To change this template use File | Settings | File Templates.
 */

public class VideoRaw {
    @Element(name = "MovieID")
    private long movieID;
    @Element(name = "DocID",required = false)
    private long docID;
    @Element(name = "MenuID",required = false)
    private long menuID;
    @Element(name = "TypeID",required = false)
    private int typeID;
    @Element(name = "Title",required = false)
    private String title;
    @Element(name = "Link",required = false)
    private String link;
    @Element(name = "Image",required = false)
    private String image;
    @Element(name = "Image1",required = false)
    private String image1;
    @Element(name ="Image2",required = false)
    private String image2;
    @Element(name = "Content",required = false)
    private String content;
    @Element(name = "Color",required = false)
    private int color;
    @Element(name = "periods",required = false)
    private String periods;
    @Element(name = "Drama",required = false)
    private String drama;
    @Element(name = "Count",required = false)
    private int count;
    @Element(name = "MaxSeries",required = false)
    private String maxSeries;


    public long getMovieID() {
        return movieID;
    }

    public void setMovieID(long movieID) {
        this.movieID = movieID;
    }

    public long getDocID() {
        return docID;
    }

    public void setDocID(long docID) {
        this.docID = docID;
    }

    public long getMenuID() {
        return menuID;
    }

    public void setMenuID(long menuID) {
        this.menuID = menuID;
    }

    public int getTypeID() {
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getPeriods() {
        return periods;
    }

    public void setPeriods(String periods) {
        this.periods = periods;
    }

    public String getDrama() {
        return drama;
    }

    public void setDrama(String drama) {
        this.drama = drama;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getMaxSeries() {
        return maxSeries;
    }

    public void setMaxSeries(String maxSeries) {
        this.maxSeries = maxSeries;
    }

    @Override
    public String toString() {
        return "Video{" +
                "movieID=" + movieID +
                ", docID=" + docID +
                ", menuID=" + menuID +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", image='" + image + '\'' +
                ", image1='" + image1 + '\'' +
                ", image2='" + image2 + '\'' +
                ", content='" + content + '\'' +
                ", color='" + color + '\'' +
                ", periods='" + periods + '\'' +
                ", drama='" + drama + '\'' +
                ", count=" + count +
                ", maxSeries=" + maxSeries +
                '}';
    }

    @Root(name = "result")
    public static class Result{
        @ElementList(entry = "item",inline = true,required = false)
        private List<VideoRaw> videoRawList;

        public List<VideoRaw> getVideoRawList() {
            return videoRawList;
        }

        public void setVideoRawList(List<VideoRaw> videoRawList) {
            this.videoRawList = videoRawList;
        }
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + color;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + count;
		result = prime * result + (int) (docID ^ (docID >>> 32));
		result = prime * result + ((drama == null) ? 0 : drama.hashCode());
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + ((image1 == null) ? 0 : image1.hashCode());
		result = prime * result + ((image2 == null) ? 0 : image2.hashCode());
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((maxSeries == null) ? 0 : maxSeries.hashCode());
		result = prime * result + (int) (menuID ^ (menuID >>> 32));
		result = prime * result + (int) (movieID ^ (movieID >>> 32));
		result = prime * result + ((periods == null) ? 0 : periods.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + typeID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VideoRaw other = (VideoRaw) obj;
		if (color != other.color)
			return false;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (count != other.count)
			return false;
		if (docID != other.docID)
			return false;
		if (drama == null) {
			if (other.drama != null)
				return false;
		} else if (!drama.equals(other.drama))
			return false;
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		if (image1 == null) {
			if (other.image1 != null)
				return false;
		} else if (!image1.equals(other.image1))
			return false;
		if (image2 == null) {
			if (other.image2 != null)
				return false;
		} else if (!image2.equals(other.image2))
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		if (maxSeries == null) {
			if (other.maxSeries != null)
				return false;
		} else if (!maxSeries.equals(other.maxSeries))
			return false;
		if (menuID != other.menuID)
			return false;
		if (movieID != other.movieID)
			return false;
		if (periods == null) {
			if (other.periods != null)
				return false;
		} else if (!periods.equals(other.periods))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (typeID != other.typeID)
			return false;
		return true;
	}
    
    
}
