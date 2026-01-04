package com.examCrux.webApp.exception;

public class YoutubeNotFoundException extends RuntimeException {
    public YoutubeNotFoundException(String message) {
        super(message);
    }
}
