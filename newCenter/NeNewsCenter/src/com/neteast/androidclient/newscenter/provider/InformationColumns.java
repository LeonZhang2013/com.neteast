package com.neteast.androidclient.newscenter.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class InformationColumns implements BaseColumns{
    
    private InformationColumns() {}
    
    public static final String AUTHORITY = "com.neteast.newscenter.information.provider";
    /**
     * The content:// style URL for this table
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/informations");

    /**
     * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
     */
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.neteast.information";

    /**
     * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
     */
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.neteast.information";

    /**
     * The default sort order for this table
     */
    public static final String DEFAULT_SORT_ORDER = "send_time DESC";

    /**
     * 消息id，消息的唯一标志 
     * <P>Type: INTEGER</P>
     */
    public static final String INFO_ID = "info_id";

    /**
     * 消息类型，可以为系统广播，应用通知，或者互动消息
     * <P>Type: INTEGER</P>
     */
    public static final String INFO_TYPE_ID = "info_type_id";

    /**
     * 用户Id，该条消息由哪个用户发送。
     * <P>Type: INTEGER</P>
     */
    public static final String FROM_USER_ID = "from_user_id";

    /**
     * 用户名，该条消息由哪个用户发送。
     * <P>Type: TEXT </P>
     */
    public static final String FROM_USER_NAME = "from_user_name";
    
    /**
     * 应用Id，该条消息点击后跳转至哪个应用。
     * <P>Type: INTEGER</P>
     */
    public static final String APPLICATION_ID = "application_id";
    
    /**
     * 应用名，该条消息点击后跳转至哪个应用。
     * <P>Type: TEXT</P>
     */
    public static final String APPLICATION_NAME = "application_name";
    
    /**
     * 互动类型，进一步细分消息由一个应用做什么处理
     * <P>Type: INTEGER</P>
     */
    public static final String INTERACTIVE_INFO_TYPE = "interactive_info_type";
    
    /**
     * 消息内容
     * <P>Type: TEXT </P>
     */
    public static final String TEXTCONTENT = "textcontent";
    
    /**
     * 消息跳转携带的信息
     * <P>Type: TEXT</P>
     */
    public static final String URL = "url";
    
    /**
     * 图片链接
     * <P>Type: TEXT </P>
     */
    public static final String PICTURE = "picture";
    
    /**
     * 消息有效时间的毫秒数
     * <P>Type: INTEGER</P>
     */
    public static final String LIMIT_TIME = "limit_time";
    
    /**
     * 消息发自服务器的时间戳
     * <P>Type: INTEGER</P>
     */
    public static final String SEND_TIME = "send_time";
    
    /**
     * accounts表的userid
     * <P>Type: INTEGER</P>
     */
    public static final String USERID = "userid";

}