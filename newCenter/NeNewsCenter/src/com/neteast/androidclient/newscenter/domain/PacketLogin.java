package com.neteast.androidclient.newscenter.domain;

import com.neteast.androidclient.newscenter.domain.Packet.SendPacket;

import android.content.Context;

import java.nio.ByteBuffer;
import java.util.HashSet;

/**
 * 报文，客户端发送，请求登录
 * @author emellend
 */
public class PacketLogin extends SendPacket {

    protected byte cmd=PACKET_LOGIN;
    /** 通信协议 */
    protected int protocolVersion=PROTOCOL_VERSION;
    /** 报文id */
    protected int msgId;
    /** 用户id */
    protected int userId;
    /** 广播id */
    protected long broadcastId;
    /** 单播id */
    protected long unicastId;
    /** 保留字段长度 */
    protected int reserveLen;
    /** 保留字段 */
    protected byte[] reserve;
    /** user_optinfo的长度 */
    protected int userOptinfoLen;
    /** 在两次心跳之间用户点击了哪些消息 */
    protected HashSet<Long> userOptinfo=new HashSet<Long>();
    
    public PacketLogin(Context context) {
        PACKE_SIZE_EXCEPT_RESERVE=37;
        CloudAccount account = CloudAccount.getInstance(context);
        msgId=getMessageId();
        userId=account.getUserId();
        broadcastId=account.getBroadcastId();
        unicastId=account.getUnicastId();
        reserveLen=0;
        userOptinfoLen=0;
    }

    @Override
    public byte[] getPacketData() {
        ByteBuffer buffer = ByteBuffer.allocate(PACKE_SIZE_EXCEPT_RESERVE + userOptinfoLen+ reserveLen);
        buffer.put(cmd);
        buffer.putInt(protocolVersion);
        buffer.putInt(msgId);
        buffer.putInt(userId);
        buffer.putLong(broadcastId);
        buffer.putLong(unicastId);
        buffer.putInt(userOptinfoLen);
        if (userOptinfoLen > 0) {
            for (Long i : userOptinfo) {
                buffer.putLong(i);
            }
        }
        buffer.putInt(reserveLen);
        if (reserveLen > 0) {
            buffer.put(reserve);
        }
        buffer.flip();
        return buffer.array();
    }

    @Override
    public String toString() {
        return "用户"+userId+"登录，最新广播消息为" + broadcastId + ", 最新单播消息为"+ unicastId;
    }

}
