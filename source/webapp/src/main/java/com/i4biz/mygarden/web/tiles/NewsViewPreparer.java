package com.i4biz.mygarden.web.tiles;

import com.i4biz.mygarden.dao.pagination.PageRequest;
import com.i4biz.mygarden.dao.pagination.PageResponse;
import com.i4biz.mygarden.domain.news.NewsView;
import com.i4biz.mygarden.service.news.INewsService;
import com.i4biz.mygarden.utils.PageRequestUtils;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.request.AbstractRequest;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.servlet.ServletRequest;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;

public class NewsViewPreparer extends AbstractPreparer {
    @Override
    public void doExecute(Request request, AttributeContext attributeContext) {
        AbstractRequest req = (AbstractRequest) request;
        WebApplicationContext applicationContext = (WebApplicationContext) req.getApplicationContext().getApplicationScope().get(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        INewsService newsService = applicationContext.getBean(INewsService.class);

        PageRequest<NewsView> pageRequest = PageRequestUtils
                .buildPageRequest(NewsView.class, request.getParamValues());

        pageRequest.setOrderSpecification(new HashMap<String, String>(){{
            put("publishingDate", "desc");
        }});

        PageResponse<NewsView> page = newsService.page(pageRequest);

        ((ServletRequest) request).getRequest().setAttribute("newsPage", page);
    }
}