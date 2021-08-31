<%@ page import="org.apache.commons.io.IOUtils" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.net.URL" %>
<%@ page import="com.i4biz.mygarden.domain.news.NewsView" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="w3-quarter w3-mobile w3-padding-tiny" style="min-width:300px">
    <c:forEach var="item" items="${content}">
        <div class="w3-border w3-border-grey w3-margin-top w3-margin-bottom" style="min-height: 200px; max-height: 600px">
                <img src="public/news/${item.id}/small-image.jpg" alt='${item.title}' align="left" style="width:100%">
                <h6>${item.title}</h6>
                <p><%
                    NewsView news = (NewsView)pageContext.getAttribute("item");
                    String newsURI = "http://localhost:8080/public/news/"+news.getId();
                    URL url = new URL(newsURI+"/short-content.html");
                    InputStreamReader is = new InputStreamReader(url.openStream());
                    String content = IOUtils.toString(is);
                    content = content.replaceAll("_path_", newsURI);
                    out.write(content);
                %>
                    <span>&emsp;</span><a href="news_full.tiles?id=${item.id}" class="w3-hover-text-blue" target="news${item.id}">Читать полностью...</a></p>
                </p>
        </div>
    </c:forEach>
</div>