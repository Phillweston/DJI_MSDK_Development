package com.ew.autofly.internal.common.error;


public class YFException extends Exception{
    public YFException() {
    }

    public YFException(String message) {
        super(message);
    }

    public YFException(String message, Throwable cause) {
        super(message, cause);
    }

    public YFException(Throwable cause) {
        super(cause);
    }

}
