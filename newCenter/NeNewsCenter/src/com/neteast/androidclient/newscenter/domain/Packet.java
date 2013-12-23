package com.neteast.androidclient.newscenter.domain;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

 abstract public class Packet {
     /** 访问bootStrap服务器 */
     public static final byte PACKET_BOOTSTRAP = -95;
     /** 访问bootStrap服务器，分配消息服务器成功 */
     public static final byte PACKET_BOOTSTRAP_SUCCESS = -79;
     /** 访问bootStrap服务器，分配消息服务器失败 */
     public static final byte PACKET_BOOTSTRAP_ERROR = -63;
     /** 登录 */
     public static final byte PACKET_LOGIN = -94;
     /** 登陆成功 */
     public static final byte PACKET_LOGIN_SUCCESS = -78;
     /** 登陆失败 */
     public static final byte PACKET_LOGIN_ERROR = -62;
     /** 保活 */
     public static final byte PACKET_KEEP_LIVE = -93;
     /** 服务器推送新消息 */
     public static final byte PACKET_NEW_INFO_PUSH = -92;
     /** 新消息已经接收 */
     public static final byte PACKET_NEW_INFO_RECEIVE = -76;
     /** 退出 */
     public static final byte PACKET_LOGOUT = -91;
     /** 再次访问bootStrap服务器 */
     public static final byte PACKET_REBOOTSTRAP = -90;
     
     /**当前的通讯协议版本号*/
     protected static final int PROTOCOL_VERSION=1;
     
    public Packet() {}
    
    /**
     * Packet的直接子类，为客户端自己发送的报文数据，有一个整合自己发送数据的通用接口
     * @author emellend
     *
     */
    public static abstract class SendPacket extends Packet{
        /**当前数据报文除了保留字段外的长度*/
        protected static int PACKE_SIZE_EXCEPT_RESERVE=0;
        
        private static final AtomicInteger MSG_COUNTER=new AtomicInteger(0);
        
        public SendPacket() {}
        
       /** 得到当前的消息Id，每次都比上次递增1*/
       protected int getMessageId(){
           return MSG_COUNTER.getAndIncrement();
       }
       /**每次请求BS时，就把MessageId初始化为0*/
        protected void initMessageId() {
            MSG_COUNTER.set(0);
        }
        
       abstract public byte[] getPacketData(); 
    }
    
    
    /**
     * Packet的直接子类，为接收的服务器发送的报文数据
     * @author emellend
     *
     */
    public static abstract class ReceivePacket extends Packet{
        public ReceivePacket(ByteBuffer buffer){}
    }
    
}

