package com.i4biz.mygarden.service.documentgenerator;

import java.util.Map;

public interface IDocumentGeneratorService {
    byte[] createDocument(String templateName, byte[] contentData, Map<String, Object> params, Object... args) throws DocumentGeneratorServiceException;

    public static class DocumentGeneratorServiceException extends Exception {

        private static final long serialVersionUID = -2213269418619997178L;

        public DocumentGeneratorServiceException() {
        }

        public DocumentGeneratorServiceException(String message) {
            super(message);
        }

        public DocumentGeneratorServiceException(String message, Throwable cause) {
            super(message, cause);
        }

        public DocumentGeneratorServiceException(Throwable cause) {
            super(cause);
        }
    }
}
