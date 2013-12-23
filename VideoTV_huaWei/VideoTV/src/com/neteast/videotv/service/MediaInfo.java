package com.neteast.videotv.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * MediaInfo
 * @author xKF75472
 *
 */
public class MediaInfo extends ItemInfo implements Parcelable
{

    /** 剧集中的第几集 */
    private int episodeNumber;
    
    /** 时长(HH:MM:SS) */
    private String length;
    
    /** 清晰度 */
    private String definition;
    
    /** 演员 */
    private String playactor;
    
    /** 导演 */
    private String director;

    /** 简介 */
    private String summary;

    /** 节目类型(爱情, 动作) */
    private String type;
    
    /** 视频来源(SDCard, 奇艺, 优酷, 土豆等) */
    private String sourceVideo;
    
    /** 缩略图 */
    private String thumbImageUrl;

    /** 评分 */
    private double score;
    
    /** 节目类型 */
    private String programCategory;
    
    public int getEpisodeNumber()
    {
        return episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber)
    {
        this.episodeNumber = episodeNumber;
    }

    public String getLength()
    {
        return length;
    }

    public void setLength(String length)
    {
        this.length = length;
    }

    public String getDefinition()
    {
        return definition;
    }

    public void setDefinition(String definition)
    {
        this.definition = definition;
    }

    public String getPlayactor()
    {
        return playactor;
    }

    public void setPlayactor(String playactor)
    {
        this.playactor = playactor;
    }

    public String getDirector()
    {
        return director;
    }

    public void setDirector(String director)
    {
        this.director = director;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getSourceVideo()
    {
        return sourceVideo;
    }

    public void setSourceVideo(String sourceVideo)
    {
        this.sourceVideo = sourceVideo;
    }

    public String getThumbImageUrl()
    {
        return thumbImageUrl;
    }

    public void setThumbImageUrl(String thumbImageUrl)
    {
        this.thumbImageUrl = thumbImageUrl;
    }

    public double getScore()
    {
        return score;
    }

    public void setScore(double score)
    {
        this.score = score;
    }
    
    public String getProgramCategory()
    {
        return programCategory;
    }

    public void setProgramCategory(String programCategory)
    {
        this.programCategory = programCategory;
    }
    
    public static final Parcelable.Creator<MediaInfo> CREATOR = new Creator<MediaInfo>()
    {

        public MediaInfo[] newArray(int size)
        {
            return new MediaInfo[size];
        }

        public MediaInfo createFromParcel(Parcel source)
        {
            return new MediaInfo(source);
        }
    };

    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(_id);
        dest.writeString(contentId);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(sourceApp);
        
        dest.writeInt(episodeNumber);
        dest.writeString(length);
        dest.writeString(definition);
        dest.writeString(playactor);
        dest.writeString(director);
        dest.writeString(summary);
        dest.writeString(type);
        dest.writeString(sourceVideo);
        dest.writeString(thumbImageUrl);
        dest.writeDouble(score);
        dest.writeString(programCategory);
    }
    
    public void readFromParcel(Parcel source)
    {
        _id = source.readInt();
        contentId = source.readString();
        title = source.readString();
        url = source.readString();
        sourceApp = source.readString();
        
        episodeNumber = source.readInt();
        length = source.readString();
        definition = source.readString();
        playactor = source.readString();
        director = source.readString();
        summary = source.readString();
        type = source.readString();
        sourceVideo = source.readString();
        thumbImageUrl = source.readString();
        score = source.readDouble();
        programCategory = source.readString();
    }

    public MediaInfo()
    {

    }

    public MediaInfo(Parcel source)
    {
        readFromParcel(source);
    }

}
