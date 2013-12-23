package com.neteast.androidclient.newscenter.domain;

import com.neteast.androidclient.newscenter.domain.Packet.SendPacket;
import android.content.Context;
import java.nio.ByteBuffer;
/**
 * 报文，客户端发送，告知服务器当前用户已经退出登录
 * @author emellend
 *
 */
public class PacketLogout extends SendPacket {
    
    byte cmd ;
    /** 通信协议 */
    public int protocolVersion;
    /** 报文id */
    public int msgId;
    /** 用户id */
    public int userId;
    /** 保留字段长度 */
    public int reserveLen;
    /** 保留数据 */
    public byte[] reserve;
    
    public PacketLogout(Context context) {
        PACKE_SIZE_EXCEPT_RESERVE=17;
        this.protocolVersion = PROTOCOL_VERSION;
        this.msgId = getMessageId();
        this.userId = CloudAccount.getInstance(context).getUserId();
        this.reserveLen = 0;
    }
    
    @Override
    public byte[] getPacketData() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(PACKE_SIZE_EXCEPT_RESERVE + reserveLen);
        byteBuffer.put(cmd);
        byteBuffer.putInt(protocolVersion);
        byteBuffer.putInt(msgId);
        byteBuffer.putInt(userId);
        byteBuffer.putInt(reserveLen);
        if (reserveLen > 0) {
            byteBuffer.put(reserve);
        }
        byteBuffer.flip();
        return byteBuffer.array();
    }

    @Override
    public String toString() {
        return "用户"+userId+"退出";
    }

}
