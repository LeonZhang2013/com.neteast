package com.neteast.androidclient.newscenter.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class CloudAccountProvider extends ContentProvider {
    private static HashMap<String, String> sAccountsProjectionMap;

    private static final int ACCOUNTS = 1;
    private static final int ACCOUNT_ID = 2;

    private static final UriMatcher sUriMatcher;
    private DatabaseHelper mOpenHelper;
    @Override
    public boolean onCreate() {
        mOpenHelper=new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(DatabaseHelper.ACCOUNTS_TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
        case ACCOUNTS:
            qb.setProjectionMap(sAccountsProjectionMap);
            break;

        case ACCOUNT_ID:
            qb.setProjectionMap(sAccountsProjectionMap);
            qb.appendWhere(CloudAccountColumns._ID + "=" + uri.getPathSegments().get(1));
            break;
            
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = CloudAccountColumns.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case ACCOUNTS:
                return CloudAccountColumns.CONTENT_TYPE;
            case ACCOUNT_ID:
                return CloudAccountColumns.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sUriMatcher.match(uri) != ACCOUNTS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(DatabaseHelper.ACCOUNTS_TABLE_NAME, CloudAccountColumns._ID, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(CloudAccountColumns.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        final String table=DatabaseHelper.ACCOUNTS_TABLE_NAME;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case ACCOUNTS:
            count = db.delete(table, where, whereArgs);
            break;

        case ACCOUNT_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.delete(table, CloudAccountColumns._ID + "=" + noteId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        final String table=DatabaseHelper.ACCOUNTS_TABLE_NAME;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case ACCOUNTS:
            count = db.update(table, values, where, whereArgs);
            break;

        case ACCOUNT_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.update(table, values, CloudAccountColumns._ID + "=" + noteId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
    
    static{
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(CloudAccountColumns.AUTHORITY, "info", ACCOUNTS);
        sUriMatcher.addURI(CloudAccountColumns.AUTHORITY, "info/#", ACCOUNT_ID);

        sAccountsProjectionMap = new HashMap<String, String>();
        sAccountsProjectionMap.put(CloudAccountColumns._ID, CloudAccountColumns._ID);
        sAccountsProjectionMap.put(CloudAccountColumns.USERID, CloudAccountColumns.USERID);
        sAccountsProjectionMap.put(CloudAccountColumns.TOKEN, CloudAccountColumns.TOKEN);
        sAccountsProjectionMap.put(CloudAccountColumns.ACCOUNT, CloudAccountColumns.ACCOUNT);
        sAccountsProjectionMap.put(CloudAccountColumns.IS_CURRENT, CloudAccountColumns.IS_CURRENT);
        sAccountsProjectionMap.put(CloudAccountColumns.LAST_BROADCASTID, CloudAccountColumns.LAST_BROADCASTID);
        sAccountsProjectionMap.put(CloudAccountColumns.LAST_UNICASTID, CloudAccountColumns.LAST_UNICASTID);
        sAccountsProjectionMap.put(CloudAccountColumns.DATA1, CloudAccountColumns.DATA1);
        sAccountsProjectionMap.put(CloudAccountColumns.DATA2, CloudAccountColumns.DATA2);
        sAccountsProjectionMap.put(CloudAccountColumns.DATA3, CloudAccountColumns.DATA3);
        sAccountsProjectionMap.put(CloudAccountColumns.DATA4, CloudAccountColumns.DATA4);
        sAccountsProjectionMap.put(CloudAccountColumns.DATA5, CloudAccountColumns.DATA5);

    }
}
