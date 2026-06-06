package com.ats.candidate.domain.exception;

public class AttachmentParsingException extends RuntimeException {
    public AttachmentParsingException(String message) {
        super(message);
    }

    public AttachmentParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
