package com.neteast.androidclient.newscenter.domain;

import com.neteast.androidclient.newscenter.domain.Packet.ReceivePacket;

import java.nio.ByteBuffer;
/**
 * 报文，服务器发送，登录成功
 * @author emellend
 *
 */
public class PacketLoginSuccess extends ReceivePacket {
    /** 心跳间隔 */
    private int keepaliveTime;

    public PacketLoginSuccess(ByteBuffer buffer) {
        super(buffer);
        //通信协议
        buffer.getInt();
        //报文id
        buffer.getInt();
        //心跳间隔
        keepaliveTime = buffer.getInt();
        //保留数据
        int reserveLen = buffer.getInt();
        if (reserveLen > 0) {
            byte[] reserve = new byte[reserveLen];
            for (int i = 0; i < reserveLen; i++) {
                reserve[i] = buffer.get();
            }
        }
    }
    /**
     * 得到保活的心跳间隔，单位秒
     * @return
     */
    public int getKeepaliveTime() {
        return keepaliveTime;
    }

    @Override
    public String toString() {
        return "登录成功，每隔"+ keepaliveTime +  "秒开始保活";
    }
}
