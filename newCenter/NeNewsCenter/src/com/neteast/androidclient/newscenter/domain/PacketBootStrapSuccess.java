package com.neteast.androidclient.newscenter.domain;

import com.neteast.androidclient.newscenter.domain.Packet.ReceivePacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
/**
 * 报文，服务器发送，BootStrap请求成功
 * @author emellend
 */
public class PacketBootStrapSuccess extends ReceivePacket {
    /** 推送服务器ip */
    private String serverIp;
    /** 推送服务器端口 */
    private short serverPort;

    public PacketBootStrapSuccess(ByteBuffer buffer) {
        super(buffer);
        //通信协议
        buffer.getInt();
        //报文id
       buffer.getInt();
        //推送服务器ip
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(buffer.get() & 0xff).append(".");
        }
        sb.deleteCharAt(sb.length() - 1);
        serverIp = sb.toString();
        //推送服务器端口
        serverPort = buffer.getShort();
        //心跳间隔
        buffer.getInt();
        //保留字段长度
        int reserveLen = buffer.getInt();
        //保留数据
        if (reserveLen > 0) {
            byte[]  reserve = new byte[reserveLen];
            for (int i = 0; i < reserveLen; i++) {
                reserve[i] = buffer.get();
            }
        }
    }
    
    /**得到登录的服务器地址*/
    public InetSocketAddress getLoginServerAddress() {
        return new InetSocketAddress(serverIp, serverPort);
    }
    
    @Override
    public String toString() {
        return "请求BS成功，消息推送服务器地址为" + serverIp + ":" + serverPort;
    }
    
}
