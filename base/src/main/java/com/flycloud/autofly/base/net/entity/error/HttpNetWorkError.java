package com.flycloud.autofly.base.net.entity.error;


public class HttpNetWorkError extends Exception {

    public HttpNetWorkError() {
    }

    public HttpNetWorkError(String message) {
        super(message);
    }

    public HttpNetWorkError(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpNetWorkError(Throwable cause) {
        super(cause);
    }
}