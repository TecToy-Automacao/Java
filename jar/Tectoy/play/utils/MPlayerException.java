package com.tectoy.play.utils;

public class MPlayerException extends Exception{
    public MPlayerException(String detailMessage) {
        super(detailMessage);
    }

    public MPlayerException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public MPlayerException(Throwable throwable) {
        super(throwable);
    }
}
