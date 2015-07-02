package com.changhong.tvserver.touying.image.loader.exception;

/**
 * Created by Jack Wang
 */
public class ThreadKillException extends RuntimeException {

    public ThreadKillException() {
    }

    public ThreadKillException(String detailMessage) {
        super(detailMessage);
    }

    public ThreadKillException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ThreadKillException(Throwable throwable) {
        super(throwable);
    }

}
