package com.i4biz.mygarden.web.tiles;

import com.i4biz.mygarden.domain.news.News;
import com.i4biz.mygarden.service.news.INewsService;
import org.apache.commons.io.IOUtils;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.request.AbstractRequest;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.servlet.ServletRequest;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class NewsFullContentPreparer extends AbstractPreparer {
    @Override
    public void doExecute(Request request, AttributeContext attributeContext) {
        try {
            AbstractRequest req = (AbstractRequest) request;
            WebApplicationContext applicationContext = (WebApplicationContext) req.getApplicationContext().getApplicationScope().get(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

            INewsService newsService = applicationContext.getBean(INewsService.class);

            String newsId = req.getParam().get("id");

            News news = newsService.findById(Long.parseLong(newsId));

            String newsURILocal = "http://localhost:8080/public/news/"+newsId;
            String newsURIPublic = "https://egardening.ru/public/news/"+newsId;
            URL url = new URL(newsURILocal + "/full-content.html");
            InputStreamReader is = new InputStreamReader(url.openStream());
            String content = IOUtils.toString(is);
            content = content.replaceAll("_path_", newsURIPublic);


            ((ServletRequest) request).getRequest().setAttribute("title", news.getTitle());
            ((ServletRequest) request).getRequest().setAttribute("content", content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}