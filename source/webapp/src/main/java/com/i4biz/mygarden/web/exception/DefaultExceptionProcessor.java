package com.i4biz.mygarden.web.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefaultExceptionProcessor implements IExceptionProcessor {
    private int responseCode;

    private String customMessage;

    public DefaultExceptionProcessor() {
        responseCode = 500;
    }

    public DefaultExceptionProcessor(int responseCode) {
        this.responseCode = responseCode;
    }

    public DefaultExceptionProcessor(int responseCode, String customMessage) {
        this.responseCode = responseCode;
        this.customMessage = customMessage;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException {
        String exceptionMsg = exception.getMessage();

        String msg = customMessage != null ? customMessage : exceptionMsg;

        StringBuilder json = new StringBuilder("{");
        json.append("\"errorCode\":\"").append(responseCode).append("\",");
        json.append("\"message\":\"").append(msg).append("\"");

//        StringBuilder message = new StringBuilder(256);
//        Throwable ex = exception;
//        int stopCounter = 5;
//        do {
//            message.append(ex.getMessage()).append("\n");
//            ex = ex.getCause();
//            stopCounter--;
//        } while (ex != null && stopCounter > 0);
//        json.append(",\"exceptionMessage\":\"").append(message.toString()).append("\"");

        json.append("}");
        response.setStatus(responseCode);
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().print(json.toString());
    }
}
