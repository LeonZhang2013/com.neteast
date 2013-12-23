
package com.neteast.androidclient.newscenter.domain;

import android.content.Context;
import java.util.HashSet;

/**
 * 报文，客户端发送，心跳保活
 * @author emellend
 *
 */
public class PacketKeepLive extends PacketLogin {
    private static final HashSet<Long> sUserOptinfo=new HashSet<Long>();
    
    public PacketKeepLive(Context context) {
        super(context);
        this.cmd = PACKET_KEEP_LIVE;
        this.userOptinfoLen = sUserOptinfo.size()*8;
        this.userOptinfo .addAll(sUserOptinfo);
        sUserOptinfo.clear();
    }
    
    /**
     * 添加用户点击操作的记录
     * @param touchedInfoId
     */
    public static void addUserOption(long touchedInfoId) {
        sUserOptinfo.add(touchedInfoId);
    }

    @Override
    public String toString() {
        return "用户"+userId+"，第"+ msgId + "次保活，最新广播消息为" + broadcastId + "，最新单播消息为"
                + unicastId + "，点击过的消息" + userOptinfo.toString();
    }
}
