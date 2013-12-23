package com.neteast.androidclient.newscenter.domain;

import com.neteast.androidclient.newscenter.domain.Packet.SendPacket;

import java.nio.ByteBuffer;
/**
 * 报文，客户端发送，告知服务器对应messageId的数据报文已经接收
 * @author emellend
 *
 */
public class PacketInfoDataReceived extends SendPacket {
    
    private byte cmd;
    /** 通信协议 */
    private int protocolVersion;
    /** 报文id */
    private int msgId;
    /** 保留字段长度 */
    private int reserveLen;
    /** 保留数据 */
    private byte[] reserve;

    public PacketInfoDataReceived(int msgId) {
        PACKE_SIZE_EXCEPT_RESERVE=13;
        cmd=PACKET_NEW_INFO_RECEIVE;
        this.protocolVersion = PROTOCOL_VERSION;
        this.msgId = msgId;
        this.reserveLen = 0;
    }
    
    @Override
    public byte[] getPacketData() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(PACKE_SIZE_EXCEPT_RESERVE + reserveLen);
        byteBuffer.put(cmd);
        byteBuffer.putInt(protocolVersion);
        byteBuffer.putInt(msgId);
        byteBuffer.putInt(reserveLen);
        if (reserveLen > 0) {
            byteBuffer.put(reserve);
        }
        byteBuffer.flip();
        return byteBuffer.array();
    }

    @Override
    public String toString() {
        return "报文Id为"+msgId+"的数据包已经收到。";
    }
}
