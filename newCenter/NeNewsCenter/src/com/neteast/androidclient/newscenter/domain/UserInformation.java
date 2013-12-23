package com.neteast.androidclient.newscenter.domain;

import com.neteast.androidclient.newscenter.view.NewsWidget;

import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;

class UserInformation extends Information {
    
    public UserInformation(Cursor cursor) {
        super(cursor);
    }
    
    public UserInformation(JSONObject jobj) {
        super(jobj);
    }
    
    @Override
    public String getTitle() {
        return "来自：<font color=#ff7200>"
                + fromUserName + "</font>";
    }

    @Override
    protected int getActionCode() {
        if (interactiveInfoType>0) {
            return interactiveInfoType;
        }else {
            return applicationId;
        }
    }

    @Override
    protected int getInfoType() {
        return USER_INFO;
    }

    @Override
    public void notifyHasNewInfo(Context context) {
        NewsWidget.getIntance(context).addUserNumsNum();
    }
}
