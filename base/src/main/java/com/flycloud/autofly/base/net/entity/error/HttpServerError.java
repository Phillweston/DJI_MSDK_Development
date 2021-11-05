package com.flycloud.autofly.base.net.entity.error;


public class HttpServerError extends Exception {

    public HttpServerError() {
    }

    public HttpServerError(String message) {
        super(message);
    }

    public HttpServerError(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpServerError(Throwable cause) {
        super(cause);
    }
}
