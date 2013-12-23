package com.neteast.androidclient.newscenter.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class CloudAccountColumns implements BaseColumns{
    
    public static final String AUTHORITY = "com.neteast.androidclient.newscenter";
    
    private CloudAccountColumns() {}

    /**
     * The content:// style URL for this table
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/info");
    
    /**
     * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
     */
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.neteast.info";

    /**
     * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
     */
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.neteast.info";

    /**
     * The default sort order for this table
     */
    public static final String DEFAULT_SORT_ORDER = "is_current DESC";

    /**
     * 用户id
     * <P>Type: INTEGER</P>
     */
    public static final String USERID = "userid";

    /**
     * 用户token
     * <P>Type: TEXT</P>
     */
    public static final String TOKEN = "token";

    /**
     * 用户帐号
     * <P>Type: TEXT</P>
     */
    public static final String ACCOUNT = "account";

    /**
     * 是否是当前的用户。
     * <P>Type: TEXT </P>
     */
    public static final String IS_CURRENT = "is_current";
    /**
     * 当前用户接收到的最后的广播Id。
     * <P>Type: INTEGER </P>
     */
    public static final String LAST_BROADCASTID = "broadcastid";
    /**
     * 当前用户接收到的最后的单播Id。
     * <P>Type: INTEGER </P>
     */
    public static final String LAST_UNICASTID = "unicastid";
    /**
     * 保留字段
     * <P>Type:TEXT</P>
     */
    public static final String DATA1 = "data1";
    
    /**
     * 保留字段
     * <P>Type:TEXT</P>
     */
    public static final String DATA2 = "data2";
    
    /**
     * 保留字段
     * <P>Type:TEXT</P>
     */
    public static final String DATA3 = "data3";
    
    /**
     * 保留字段
     * <P>Type:TEXT</P>
     */
    public static final String DATA4 = "data4";
    
    /**
     * 保留字段
     * <P>Type:TEXT</P>
     */
    public static final String DATA5 = "data5";

}
