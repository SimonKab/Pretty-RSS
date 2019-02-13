package com.simonk.project.prettyrss.network.exceptions;

public class InvalidRssPathException extends Exception {

    public InvalidRssPathException() {
        super();
    }

    public InvalidRssPathException(String message) {
        super(message);
    }

    public InvalidRssPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRssPathException(Throwable cause) {
        super(cause);
    }

}
