package com.colorhaake.traveler.plain_object;

/**
 * Created by colorhaake on 2017/3/25.
 */

public class Response<T> {
    public Error error;
    public T result;

    public class Error {
        public String code;
        public String message;
        @Override
        public String toString() {
            return "code = " + code + ", message = " + message;
        }
    }
}
