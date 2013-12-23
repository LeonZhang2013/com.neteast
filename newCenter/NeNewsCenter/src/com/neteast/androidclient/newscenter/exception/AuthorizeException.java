package com.neteast.androidclient.newscenter.exception;

public class AuthorizeException extends Exception {

    private static final long serialVersionUID = 2810169772566539374L;

    private int statusCode = -1;
    
    public AuthorizeException(String msg) {
        super(msg);
    }

    public AuthorizeException(Exception cause) {
        super(cause);
    }

    public AuthorizeException(String msg, int statusCode) {
        super(msg);
        this.statusCode = statusCode;
    }

    public AuthorizeException(String msg, Exception cause) {
        super(msg, cause);
    }

    public AuthorizeException(String msg, Exception cause, int statusCode) {
        super(msg, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
    
    
    public AuthorizeException() {
        super(); 
    }

    public AuthorizeException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public AuthorizeException(Throwable throwable) {
        super(throwable);
    }

    public AuthorizeException(int statusCode) {
        super();
        this.statusCode = statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

}
