package com.i4biz.mygarden.service.documentgenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

public class TemplateFileSource implements TemplateSource {
    private final File folder;

    public TemplateFileSource(File folder) {
        this.folder = folder;
    }

    @Override
    public InputStream getSource(String resourcePath, Object... args) throws SourceNotFoundException {
        try {
            File resource = new File(folder, resourcePath);

            return new FileInputStream(resource);
        } catch (FileNotFoundException e) {
            throw new SourceNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public URI getUri() {
        return folder.toURI();
    }
}
