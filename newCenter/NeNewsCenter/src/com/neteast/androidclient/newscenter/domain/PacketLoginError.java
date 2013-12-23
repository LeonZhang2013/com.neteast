package com.neteast.androidclient.newscenter.domain;

import com.neteast.androidclient.newscenter.domain.Packet.ReceivePacket;
import com.neteast.androidclient.newscenter.utils.LogUtil;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * 报文，服务器发送的，登录失败
 * @author emellend
 *
 */
public class PacketLoginError extends ReceivePacket {
    /** 错误编号 */
    private int errorId;
    /** 错误描述信息 */
    private String errorDesc;

    public PacketLoginError(ByteBuffer buffer) {
        super(buffer);
        //通信协议
        buffer.getInt();
        //报文id
        buffer.getInt();
        //错误编号
        errorId = buffer.getInt();
        //错误描述信息
        byte[] error = new byte[20];
        for (int i = 0; i < 20; i++) {
            error[i] = buffer.get();
        }
        try {
            errorDesc = new String(error, "utf-8");
        } catch (UnsupportedEncodingException e) {
            LogUtil.printException(e);
        }
        //保留数据
        int reserveLen = buffer.getInt();
        if (reserveLen > 0) {
            byte[] reserve = new byte[reserveLen];
            for (int i = 0; i < reserveLen; i++) {
                reserve[i] = buffer.get();
            }
        }
    }
    
    public String getErrorDesc() {
        return TextUtils.isEmpty(errorDesc)?"消息推送服务器异常，登录失败":errorDesc;
    }
    
    @Override
    public String toString() {
        return "登录失败 [errorId=" + errorId + ", errorDesc=" + getErrorDesc() +  "]";
    }
}
