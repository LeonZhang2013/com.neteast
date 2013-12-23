package com.neteast.androidclient.newscenter.exception;

public class ShareArgumentsException extends Exception{

    private static final long serialVersionUID = -1993132819095843868L;


    public ShareArgumentsException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ShareArgumentsException(String detailMessage) {
        super(detailMessage);
    }

    public ShareArgumentsException(Throwable throwable) {
        super(throwable);
    }

    public ShareArgumentsException() {
    }

}
