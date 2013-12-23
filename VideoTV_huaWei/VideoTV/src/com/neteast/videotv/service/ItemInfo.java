package com.neteast.videotv.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Base media info
 * @author xKF75472
 * 
 */
class ItemInfo implements Parcelable
{

    /** id/index */
    int _id;

    /** 内容ID */
    String contentId;

    /** 视频/直播频道名称 */
    String title;

    /** 视频地址 */
    String url;

    /** 应用来源(IPTV, OTT, MyMedia) */
    String sourceApp;

    public int getId()
    {
        return _id;
    }

    public void setId(int id)
    {
        this._id = id;
    }

    public String getContentId()
    {
        return contentId;
    }

    public void setContentId(String contentId)
    {
        this.contentId = contentId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getSourceApp()
    {
        return sourceApp;
    }

    public void setSourceApp(String sourceApp)
    {
        this.sourceApp = sourceApp;
    }
    
    public static final Parcelable.Creator<ItemInfo> CREATOR = new Creator<ItemInfo>()
    {

        public ItemInfo[] newArray(int size)
        {
            return new ItemInfo[size];
        }

        public ItemInfo createFromParcel(Parcel source)
        {
            return new ItemInfo(source);
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
    }
    
    public void readFromParcel(Parcel source)
    {
        _id = source.readInt();
        contentId = source.readString();
        title = source.readString();
        url = source.readString();
        sourceApp = source.readString();
    }
    
    public ItemInfo()
    {
        
    }
    
    public ItemInfo(Parcel source)
    {
        readFromParcel(source);
    }

}
