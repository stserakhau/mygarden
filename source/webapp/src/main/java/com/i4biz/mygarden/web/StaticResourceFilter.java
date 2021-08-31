package com.i4biz.mygarden.web;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

@Service("staticResourceFilter")
public class StaticResourceFilter extends OncePerRequestFilter {
    private static String publicPrefix = "/public/";

    @Value("${staticContent.folder}")
    File staticContent;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String resourcePath = uri.substring(publicPrefix.length());
        response.addHeader("Cache-Control", "public max-age=86400");
        File resource = new File(staticContent, resourcePath);

        FileInputStream fis = new FileInputStream(resource);

        OutputStream os = response.getOutputStream();

        IOUtils.copy(fis, os);
        os.flush();
        fis.close();
    }
}
