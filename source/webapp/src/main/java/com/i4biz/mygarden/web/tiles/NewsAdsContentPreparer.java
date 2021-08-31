package com.i4biz.mygarden.web.tiles;

import com.i4biz.mygarden.dao.pagination.PageRequest;
import com.i4biz.mygarden.dao.pagination.PageResponse;
import com.i4biz.mygarden.domain.news.NewsView;
import com.i4biz.mygarden.service.news.INewsService;
import com.i4biz.mygarden.utils.PageRequestUtils;
import org.apache.tiles.Attribute;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.request.AbstractRequest;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.jsp.JspRequest;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;

public class NewsAdsContentPreparer extends AbstractPreparer {
    @Override
    protected void doExecute(Request request, AttributeContext attributeContext) {
        AbstractRequest req = (AbstractRequest) request;
        WebApplicationContext applicationContext = (WebApplicationContext) req.getApplicationContext().getApplicationScope().get(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        INewsService newsService = applicationContext.getBean(INewsService.class);

        PageRequest<NewsView> pageRequest = PageRequestUtils
                .buildPageRequest(NewsView.class, request.getParamValues());

        Attribute rowsCountAttr = attributeContext.getCascadedAttribute("AD_ROWS_COUNT");
        int rows = rowsCountAttr==null ? 1 : Integer.parseInt((String)rowsCountAttr.getValue());

        pageRequest.setPageSize(rows * 4);
        pageRequest.setOrderSpecification(new HashMap<String, String>(){{
            put("publishingDate", "desc");
        }});

        PageResponse<NewsView> page = newsService.page(pageRequest);


        ((JspRequest) request).getRequestScope().put("content", page.getItems());
    }
}
