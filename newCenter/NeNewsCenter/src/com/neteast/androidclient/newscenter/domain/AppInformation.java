package com.neteast.androidclient.newscenter.domain;

import com.neteast.androidclient.newscenter.view.NewsWidget;
import org.json.JSONObject;
import android.content.Context;
import android.database.Cursor;

class AppInformation extends Information {

    public AppInformation(Cursor cursor) {
        super(cursor);
    }
    
    public AppInformation(JSONObject jobj) {
        super(jobj);
    }
    
    @Override
    public String getTitle() {
        return applicationName;
    }

    @Override
    protected int getActionCode() {
        return applicationId;
    }

    @Override
    protected int getInfoType() {
        return APP_INFO;
    }


    @Override
    public void notifyHasNewInfo(Context context) {
        NewsWidget.getIntance(context).addAppNumsNum();
    }
    
}
