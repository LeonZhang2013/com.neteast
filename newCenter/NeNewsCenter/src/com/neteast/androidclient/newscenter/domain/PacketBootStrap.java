package com.neteast.androidclient.newscenter.domain;

import com.neteast.androidclient.newscenter.domain.Packet.SendPacket;

import android.content.Context;

import java.nio.ByteBuffer;

/**
 * 报文，客户端发送，BootStrap请求
 * @author emellend
 *
 */
public class PacketBootStrap extends SendPacket {

    private byte cmd = PACKET_BOOTSTRAP;
    /** 通信协议 */
    private int protocolVersion;//
    /** 用户id，guest为0 */
    private int userId;
    /** 消息盒子版本号 长度10 */
    private byte[] clientVersion;//
    /** 终端类型描述 长度10 */
    private byte[] terminalVersion;//
    /** 报文id */
    private int msgId;//
    /** 保留字段长度 */
    private int reserveLen;
    /** 保留字段 */
    private byte[] reserve;
    
    public PacketBootStrap(Context context) {
        PACKE_SIZE_EXCEPT_RESERVE=37;
        this.protocolVersion = PROTOCOL_VERSION;
        this.userId = CloudAccount.getInstance(context).getUserId();
        this.clientVersion = "1".getBytes();
        this.terminalVersion = "android".getBytes();
        initMessageId();
        this.msgId = getMessageId();
        this.reserveLen = 0;
    }

    @Override
    public byte[] getPacketData() {
        ByteBuffer buffer = ByteBuffer.allocate(PACKE_SIZE_EXCEPT_RESERVE + reserveLen);
        buffer.put(cmd);
        buffer.putInt(protocolVersion);
        buffer.putInt(userId);
        buffer.put(clientVersion);
        buffer.put(terminalVersion);
        buffer.putInt(msgId);
        buffer.putInt(reserveLen);
        if (reserveLen > 0) {
            buffer.put(reserve);
        }
        buffer.flip();
        return buffer.array();
    }

   @Override
   public String toString() {
       return "用户"+userId+" 请求BS";
   }

}
