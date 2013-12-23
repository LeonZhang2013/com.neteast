
package com.neteast.androidclient.newscenter.domain;

import com.neteast.androidclient.newscenter.domain.Packet.ReceivePacket;
import com.neteast.androidclient.newscenter.utils.LogUtil;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * 报文，服务器发送，BootStrap请求失败
 * @author emellend
 */
public class PacketBootStrapError extends ReceivePacket {

    /** 错误编号 */
    private int errorId;

    /** 错误描述信息 */
    private String errorDesc;


    public PacketBootStrapError(ByteBuffer buffer) {
        super(buffer);
        //获取通信协议
        buffer.getInt();
        //获取报文id
        buffer.getInt();
        //获取错误编码
        errorId = buffer.getInt();
        // 获取错误描述信息
        byte[] error = new byte[20];
        for (int i = 0; i < 20; i++) {
            error[i] = buffer.get();
        }
        try {
            errorDesc = new String(error, "utf-8");
        } catch (UnsupportedEncodingException e) {
            LogUtil.printException(e);
        }
        //获取保留数据长度
        int  reserveLen = buffer.getInt();
        //获取保留数据
        if (reserveLen > 0) {
            byte[] reserve = new byte[reserveLen];
            for (int i = 0; i < reserveLen; i++) {
                reserve[i] = buffer.get();
            }
        }
    }
    
    public String getErrorMessage() {
        return TextUtils.isEmpty(errorDesc)?"BS服务器出错":errorDesc;
    }

    @Override
    public String toString() {
        return "请求BS失败 [errorId=" + errorId + ", errorDesc=" + getErrorMessage() + "]";
    }
    
    
}
