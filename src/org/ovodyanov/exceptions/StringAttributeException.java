package org.ovodyanov.exceptions;

public class StringAttributeException extends Exception{
    public StringAttributeException() {
    }

    public StringAttributeException(String message) {
        super(message);
    }

    public StringAttributeException(String message, Throwable cause) {
        super(message, cause);
    }

    public StringAttributeException(Throwable cause) {
        super(cause);
    }

    public StringAttributeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
