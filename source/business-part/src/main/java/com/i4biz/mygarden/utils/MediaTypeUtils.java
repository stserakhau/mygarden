package com.i4biz.mygarden.utils;

import org.springframework.http.MediaType;

public class MediaTypeUtils {
    public static MediaType getMimeType(String resourceFileName) {
        String extension = resourceFileName.substring(resourceFileName.lastIndexOf(".")+1);
        switch (extension) {
            case "xml" : return MediaType.APPLICATION_XML;
            case "png" : return MediaType.IMAGE_PNG;
            case "gif" : return MediaType.IMAGE_GIF;
            case "jpg" : return MediaType.IMAGE_JPEG;
            case "txt" : return MediaType.TEXT_PLAIN;
            case "json" : return MediaType.APPLICATION_JSON;
            case "html" : return MediaType.TEXT_HTML;
            default:
                return MediaType.ALL;
        }
    }
}
