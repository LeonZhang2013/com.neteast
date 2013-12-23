package com.neteast.androidclient.newscenter.exception;
/**
 * 数据报文损坏异常
 * @author emellend
 *
 */
public class PacketDamageException extends Exception {

    private static final long serialVersionUID = -1083202990399086365L;

    public PacketDamageException() {
        // TODO Auto-generated constructor stub
    }

    public PacketDamageException(String detailMessage) {
        super(detailMessage);
        // TODO Auto-generated constructor stub
    }

    public PacketDamageException(Throwable throwable) {
        super(throwable);
        // TODO Auto-generated constructor stub
    }

    public PacketDamageException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        // TODO Auto-generated constructor stub
    }

}
