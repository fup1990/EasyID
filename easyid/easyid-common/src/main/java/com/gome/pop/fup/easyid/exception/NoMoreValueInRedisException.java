package com.gome.pop.fup.easyid.exception;

/**
 * Created by fupeng-ds on 2017/8/3.
 */
public class NoMoreValueInRedisException extends RuntimeException{

    public NoMoreValueInRedisException(String message) {
        super(message);
    }
}
