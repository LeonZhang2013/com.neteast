package com.neteast.androidclient.newscenter.domain;

import com.neteast.androidclient.newscenter.domain.Packet.ReceivePacket;
import com.neteast.androidclient.newscenter.exception.PacketDamageException;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.zip.CRC32;
/**
 * 报文，服务器发送，含有Information数据
 * @author emellend
 *
 */
public class PacketInfoData extends ReceivePacket {
    
    private static HashMap<String, byte[]> sDataBuffer=new HashMap<String, byte[]>();
    
    /** 报文id */
    private int msgId;
    /** 1 广播, 0 单播 */
    private byte BorU;
    
    /** 消息id */
    private long infoId;
    
    /** 本消息的udp包总个数 */
    private int udpPacketCount;
    /** udp包编号，从0开始 */
    private int udpPacketNo;
    
    
    public PacketInfoData(Context context,ByteBuffer buffer) throws PacketDamageException{
        super(buffer);
        byte[] datas = parseData(buffer);
        putDataInCache(datas);
        refreshInformationId(context);
    }
    
    /**
     * 解析数据
     * @param buffer
     * @return
     */
    private byte[] parseData(ByteBuffer buffer) throws PacketDamageException{
        //通信协议
        buffer.getInt();
        //报文id
        msgId = buffer.getInt();
        //消息类型
        BorU = buffer.get();
        //消息Id
        infoId = buffer.getLong();
        //本消息的udp包总个数
        udpPacketCount = buffer.getInt();
        // 当前udp包的编号
        udpPacketNo = buffer.getInt();
        //crc校验码
        long crc = Long.valueOf(buffer.getInt() + "");
        if (crc < 0) {
            long temp = 4294967296l;
            crc = temp + crc;
        }
        //数据长度
        int dataLen = buffer.getInt();
        //获取数据
        byte[] datas = new byte[dataLen];
        for (int i = 0; i < dataLen; i++) {
            datas[i] = buffer.get();
        }
        // crc校验
        CRC32 crc32 = new CRC32();
        crc32.update(datas);

        long crc32V = crc32.getValue();
        if (crc32V != crc) {
            //若不相等，则数据已经损坏
            throw new PacketDamageException("id为"+msgId+"的packet数据损坏");
        }
        
        //保留字段长度
        int reserveLen = buffer.getInt();
        if (reserveLen > 0) {
            //保留数据
            byte[] reserve = new byte[reserveLen];
            for (int i = 0; i < reserveLen; i++) {
                reserve[i] = buffer.get();
            }
        }
        return datas;
    }
    /**
     * 将收到的消息的id更新到当前用户
     */
    private void refreshInformationId(Context context) {
        if (BorU==1) {
            CloudAccount.getInstance(context).setBroadcastId(infoId);
        }else {
            CloudAccount.getInstance(context).setUnicastId(infoId);
        }
    }
    /**
     * 将数据包放进缓存
     * @param datas
     */
    private void putDataInCache(byte[] datas) {
        String key=getDataBufferKey();
        sDataBuffer.put(key+udpPacketNo, datas);
    }
    /**
     * 得到当前消息在缓存中的key
     * @return
     */
    private String getDataBufferKey() {
        return BorU+""+infoId;
    }
    /**
     * 获取MsgId
     * @return
     */
    public int getMsgId() {
        return msgId;
    }
    
    /**
     * 数据是否完整
     * @return
     */
    public boolean isDataComplete() {
        
        boolean isSinglePacket=(1==udpPacketCount);
        if (isSinglePacket) {
            return true;
        }
        
        boolean isAllPacketReceived=false;
        for(int i=0;i<udpPacketCount;i++){
            byte[] data = sDataBuffer.get(getDataBufferKey()+i);
            if (data==null) {
                isAllPacketReceived=false;
                break;
            }
            isAllPacketReceived=true;
        }
        return isAllPacketReceived;
    }
    
    public String getData() {
        boolean dataRight=true;
        ByteArrayOutputStream buffer=new ByteArrayOutputStream();
        
        for(int i=0;i<udpPacketCount;i++){
            byte[] data = sDataBuffer.remove(getDataBufferKey()+i);
            try {
                buffer.write(data);
            } catch (IOException e) {
                dataRight=false;
                break;
            }
        }
        if (dataRight) {
            byte[] datas = buffer.toByteArray();
            try {
                buffer.close();
            } catch (IOException e) {}
            return new String(datas);
        }
        return null;
    }

    @Override
    public String toString() {
        String type=(BorU==1?"广播":"单播");
        return type+"数据包，报文id" + msgId + "，消息id" + infoId
                + ", 拆分为" + udpPacketCount + "个包发送，当前为第" + (udpPacketNo+1) + "个包。";
    }
    
    public static void clearCache() {
        sDataBuffer.clear();
    }
}
