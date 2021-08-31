package com.i4biz.mygarden.web.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IExceptionProcessor {
    void process(HttpServletRequest request, HttpServletResponse response, Exception ex) throws IOException;
}
