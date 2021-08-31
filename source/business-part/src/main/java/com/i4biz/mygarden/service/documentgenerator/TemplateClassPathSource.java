package com.i4biz.mygarden.service.documentgenerator;

import java.io.InputStream;
import java.net.URI;

public class TemplateClassPathSource implements TemplateSource {
    private final String contextPath;

    public TemplateClassPathSource(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public InputStream getSource(String resourcePath, Object... args) throws SourceNotFoundException {
        String refPath;
        if (resourcePath.startsWith("/")) {
            refPath = resourcePath;
        } else if (resourcePath.startsWith("../")) {
            int lastSlash = contextPath.lastIndexOf('/');
            String folder = contextPath.substring(0, lastSlash);
            refPath = folder + "/" + resourcePath.substring(3);
        } else {
            refPath = contextPath + "/" + resourcePath;
        }
        InputStream is = TemplateClassPathSource.class.getResourceAsStream(refPath);

        if (is == null) {
            throw new SourceNotFoundException("Resource " + resourcePath + " not found.");
        }

        return is;
    }

    @Override
    public URI getUri() {
        throw new RuntimeException("Not supported");
    }
}
