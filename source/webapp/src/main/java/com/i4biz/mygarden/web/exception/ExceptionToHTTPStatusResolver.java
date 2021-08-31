package com.i4biz.mygarden.web.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class ExceptionToHTTPStatusResolver extends AbstractHandlerExceptionResolver {
    private static Logger LOG = LoggerFactory.getLogger(ExceptionToHTTPStatusResolver.class);

    private IExceptionProcessor defaultExceptionProcessor = new DefaultExceptionProcessor();

    private Map<Class, IExceptionProcessor> exceptionProcessorMapping;

    public void setExceptionProcessorMapping(Map<Class, IExceptionProcessor> exceptionProcessorMapping) {
        this.exceptionProcessorMapping = exceptionProcessorMapping;
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request,
                                              HttpServletResponse response, Object handler, Exception ex) {
        IExceptionProcessor exceptionProcessor = exceptionProcessorMapping.get(ex.getClass());
        LOG.error("Resolving exception", ex);
        ex.printStackTrace();
        if (exceptionProcessor == null) {
            exceptionProcessor = this.defaultExceptionProcessor;
        }

        try {
            exceptionProcessor.process(request, response, ex);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
        }

        return new ModelAndView();
    }
}
