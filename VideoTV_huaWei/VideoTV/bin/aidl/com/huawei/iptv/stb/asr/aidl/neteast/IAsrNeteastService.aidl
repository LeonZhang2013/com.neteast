package com.huawei.iptv.stb.asr.aidl.neteast;

import com.huawei.iptv.stb.asr.aidl.neteast.Callback;

interface IAsrNeteastService
{

    void setCallback(Callback callback);

    String execute(String json);

}