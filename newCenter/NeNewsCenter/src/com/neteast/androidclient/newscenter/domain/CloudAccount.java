package com.neteast.androidclient.newscenter.domain;

import com.neteast.androidclient.newscenter.provider.CloudAccountColumns;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;

/**
 * 云宽带用户账户信息
 * @author emellend
 */
public class CloudAccount {
    
    private static final String USER_IS_CURRENT="true";
    private static final String USER_NOT_CURRENT="false";
    
    private static final int GUEST_ID=0;
    private static final String GUEST_TOKEN="";
    private static final String GUEST_ACCOUNT="Guest";
    
    public synchronized static CloudAccount getInstance(Context context) {
        if (mSelf==null) {
            mSelf=new CloudAccount(context);
        }
        return mSelf;
    }

    private static CloudAccount mSelf;
    private Cursor mCursor;
    private Context mContext;
    
    private int mUserId;
    private String mToken;
    private String mAccount;
    private long mUnicastId;
    private ChangeObserver mChangeObserver;
    
    private CloudAccount(Context context) {
        mContext=context;
        mCursor=getCurrentAccount();
        
        mChangeObserver = new ChangeObserver();
        mCursor.registerContentObserver(mChangeObserver);
        
        if (mCursor.moveToFirst()) {
            refreshData();
        }else {
            insetNewCurrent(GUEST_ID, GUEST_TOKEN, GUEST_ACCOUNT);
        }
    }
    /**
     * 更新数据
     */
    private void refreshData() {
        mUserId=mCursor.getInt(mCursor.getColumnIndex(CloudAccountColumns.USERID));
        mToken=mCursor.getString(mCursor.getColumnIndex(CloudAccountColumns.TOKEN));
        mAccount=mCursor.getString(mCursor.getColumnIndex(CloudAccountColumns.ACCOUNT));
        mUnicastId=mCursor.getLong(mCursor.getColumnIndex(CloudAccountColumns.LAST_UNICASTID));
    }
    /**
     * 得到当前用户
     * @return
     */
    private Cursor getCurrentAccount() {
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor =resolver.query(
                CloudAccountColumns.CONTENT_URI, 
                null, 
                CloudAccountColumns.IS_CURRENT+"=?", 
                new String[]{USER_IS_CURRENT}, 
                CloudAccountColumns.DEFAULT_SORT_ORDER);
        return cursor;
    }
    /**
     * 新增一个用户，该用户为当前用户
     * @param id
     * @param token
     * @param account
     */
    private void insetNewCurrent(int id,String token,String account) {
        ContentValues values=new ContentValues();
        values.put(CloudAccountColumns.USERID, id);
        values.put(CloudAccountColumns.TOKEN, token);
        values.put(CloudAccountColumns.ACCOUNT, account);
        values.put(CloudAccountColumns.IS_CURRENT, USER_IS_CURRENT);
        mContext.getContentResolver().insert(CloudAccountColumns.CONTENT_URI, values);
    }
    
    /**
     * 设置所有的用户帐号为非当前帐号
     * @param context
     */
    private void setAllUserNotCurrent() {
        ContentValues values=new ContentValues();
        values.put(CloudAccountColumns.IS_CURRENT, USER_NOT_CURRENT);
        mContext.getContentResolver().update(CloudAccountColumns.CONTENT_URI, values, null, null);
    }
    /**
     * 设置id的用户为当前用户
     * @param id
     */
    private void setUserIsCurrent(int id) {
        ContentValues values=new ContentValues();
        values.put(CloudAccountColumns.IS_CURRENT, USER_IS_CURRENT);
        mContext.getContentResolver().update(CloudAccountColumns.CONTENT_URI, values, CloudAccountColumns.USERID+"=?" , new String[]{String.valueOf(id)});
    }
    
    public int getUserId() {
        return mUserId;
    }

    public String getToken() {
        return mToken;
    }

    public String getAccount() {
        return mAccount;
    }

    public long getBroadcastId() {
        SharedPreferences sp = mContext.getSharedPreferences("cloudaccount", Context.MODE_PRIVATE);
        return sp.getLong("broadcastId", 0l);
    }

    public long getUnicastId() {
        return mUnicastId;
    }
    /**
     * 当前是否是Guest登录
     * @return
     */
    public boolean isGuest() {
        return mUserId==GUEST_ID;
    }
    
    /**
     * 用户登录
     * @param id
     * @param token
     * @param account
     */
    public void login(int id,String token,String account) {
        setAllUserNotCurrent();
        Cursor cursor = getUserById(id);
        if (cursor.moveToFirst()) {
            setUserIsCurrent(id);
        }else {
            insetNewCurrent(id, token,account);
        }
    }
    /**
     * 用户退出
     */
    public void logout() {
        setAllUserNotCurrent();
        setUserIsCurrent(GUEST_ID);
    }
    /**
     * 查询数据库，得到id为输入id的记录
     * @param id
     * @return Cursor
     */
    private Cursor getUserById(long id) {
        Cursor cursor = mContext.getContentResolver().query(
                CloudAccountColumns.CONTENT_URI, 
                null,
                CloudAccountColumns.USERID + "=?", 
                new String[] {String.valueOf(id)}, 
                CloudAccountColumns.DEFAULT_SORT_ORDER);
        return cursor;
    }
    
    /**
     * 设置当前用户的最新广播消息id
     * @param broadcastId
     */
    public void setBroadcastId(long broadcastId) {
        Editor edit = mContext.getSharedPreferences("cloudaccount", Context.MODE_PRIVATE).edit();
        edit.putLong("broadcastId", broadcastId);
        edit.commit();
    }
    /**
     * 设置当前用户的最新单播消息id
     * @param unicastId
     */
    public void setUnicastId(long unicastId) {
        ContentValues values=new ContentValues();
        values.put(CloudAccountColumns.LAST_UNICASTID, unicastId);
        mContext.getContentResolver().update(CloudAccountColumns.CONTENT_URI, values, CloudAccountColumns.USERID+"=?", new String[]{String.valueOf(mUserId)});
    }
    
    private class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(new Handler());
        }
        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }
        @Override
        public void onChange(boolean selfChange) {
            mCursor.requery();
            if (mCursor.moveToFirst()) {
                refreshData();
            }
        }
    }
    
    /**
     * 停止监听，释放资源
     */
    public void release() {
        if (mCursor!=null) {
            mCursor.unregisterContentObserver(mChangeObserver);
            mCursor.close();
        }
        mSelf=null;
    }
}