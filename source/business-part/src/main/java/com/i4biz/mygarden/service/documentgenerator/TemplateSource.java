package com.i4biz.mygarden.service.documentgenerator;

import java.io.InputStream;
import java.net.URI;

public interface TemplateSource {
    InputStream getSource(String templateName, Object... args) throws SourceNotFoundException;

    URI getUri();

    public static class SourceNotFoundException extends Exception {
        public SourceNotFoundException(String message) {
            super(message);
        }

        public SourceNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
