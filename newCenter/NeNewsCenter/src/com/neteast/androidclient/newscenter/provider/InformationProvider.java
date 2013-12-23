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

public class InformationProvider extends ContentProvider {
    
    
    private static HashMap<String, String> sInformationsProjectionMap;

    private static final int INFORMATIONS = 1;
    private static final int INFORMATION_ID = 2;

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
        qb.setTables(DatabaseHelper.INFORMATIONS_TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
        case INFORMATIONS:
            qb.setProjectionMap(sInformationsProjectionMap);
            break;

        case INFORMATION_ID:
            qb.setProjectionMap(sInformationsProjectionMap);
            qb.appendWhere(InformationColumns._ID + "=" + uri.getPathSegments().get(1));
            break;
            
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = InformationColumns.DEFAULT_SORT_ORDER;
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
            case INFORMATIONS:
                return InformationColumns.CONTENT_TYPE;
            case INFORMATION_ID:
                return InformationColumns.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        
        if (sUriMatcher.match(uri) != INFORMATIONS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(DatabaseHelper.INFORMATIONS_TABLE_NAME, InformationColumns.INFO_ID, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(InformationColumns.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        final String table=DatabaseHelper.INFORMATIONS_TABLE_NAME;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case INFORMATIONS:
            count = db.delete(table, where, whereArgs);
            break;

        case INFORMATION_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.delete(table, InformationColumns._ID + "=" + noteId
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
        final String table=DatabaseHelper.INFORMATIONS_TABLE_NAME;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case INFORMATIONS:
            count = db.update(table, values, where, whereArgs);
            break;

        case INFORMATION_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.update(table, values, InformationColumns._ID + "=" + noteId
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
        sUriMatcher.addURI(InformationColumns.AUTHORITY, "informations", INFORMATIONS);
        sUriMatcher.addURI(InformationColumns.AUTHORITY, "informations/#", INFORMATION_ID);

        sInformationsProjectionMap = new HashMap<String, String>();
        sInformationsProjectionMap.put(InformationColumns._ID, InformationColumns._ID);
        sInformationsProjectionMap.put(InformationColumns.INFO_ID, InformationColumns.INFO_ID);
        sInformationsProjectionMap.put(InformationColumns.INFO_TYPE_ID, InformationColumns.INFO_TYPE_ID);
        sInformationsProjectionMap.put(InformationColumns.LIMIT_TIME, InformationColumns.LIMIT_TIME);
        sInformationsProjectionMap.put(InformationColumns.SEND_TIME, InformationColumns.SEND_TIME);
        sInformationsProjectionMap.put(InformationColumns.TEXTCONTENT, InformationColumns.TEXTCONTENT);
        sInformationsProjectionMap.put(InformationColumns.URL, InformationColumns.URL);
        sInformationsProjectionMap.put(InformationColumns.PICTURE, InformationColumns.PICTURE);
        sInformationsProjectionMap.put(InformationColumns.APPLICATION_ID, InformationColumns.APPLICATION_ID);
        sInformationsProjectionMap.put(InformationColumns.APPLICATION_NAME, InformationColumns.APPLICATION_NAME);
        sInformationsProjectionMap.put(InformationColumns.FROM_USER_ID, InformationColumns.FROM_USER_ID);
        sInformationsProjectionMap.put(InformationColumns.FROM_USER_NAME, InformationColumns.FROM_USER_NAME);
        sInformationsProjectionMap.put(InformationColumns.INTERACTIVE_INFO_TYPE, InformationColumns.INTERACTIVE_INFO_TYPE);
        sInformationsProjectionMap.put(InformationColumns.USERID, InformationColumns.USERID);
    }
}
