package com.i4biz.mygarden.web.tiles;

import com.i4biz.mygarden.dao.pagination.PageRequest;
import com.i4biz.mygarden.domain.news.Group;
import com.i4biz.mygarden.domain.news.NewsView;
import com.i4biz.mygarden.domain.news.State;
import com.i4biz.mygarden.expression.ExpressionNode;
import com.i4biz.mygarden.expression.ExpressionNodeUtils;
import com.i4biz.mygarden.service.news.INewsService;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.request.AbstractRequest;
import org.apache.tiles.request.Request;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;

public class NewsVerticalNavigatorPreparer extends AbstractPreparer {
    @Override
    public void doExecute(Request request, AttributeContext attributeContext) {
        AbstractRequest req = (AbstractRequest) request;
        WebApplicationContext applicationContext = (WebApplicationContext) req.getApplicationContext().getApplicationScope().get(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        INewsService newsService = applicationContext.getBean(INewsService.class);

        ExpressionNode group;

        PageRequest<NewsView> pageRequest = new PageRequest(
                0,
                3,
                ExpressionNodeUtils.and(
                        group = new ExpressionNode("newsGroup", "eq", Group.EGARDENING),
                        new ExpressionNode("state", "eq", State.DEPLOYED)
                ),
                new HashMap(){{
                    put("publishingDate","desc");
                }}
        );

        List<NewsView> egardeningNews = newsService.scroll(pageRequest);

        group.right.value = Group.PARTNER;
        List<NewsView> partnerNews = newsService.scroll(pageRequest);

        request.getContext("request").put("egardeningNews", egardeningNews);
        request.getContext("request").put("partnerNews", partnerNews);
    }
}