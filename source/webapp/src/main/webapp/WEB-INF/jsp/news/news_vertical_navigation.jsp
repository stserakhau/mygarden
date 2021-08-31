<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.i4biz.mygarden.domain.news.NewsView" %>
<%@ page import="org.apache.commons.io.IOUtils" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.net.URL" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div style="overflow: auto; max-height: 600px">
    <ul class="w3-ul w3-center">
        <li class="w3-light-gray w3-hover-white  w3-left-align"><b>Новости проекта</b><a href="news.tiles?query=(state eq 'DEPLOYED' and newsGroup eq 'EGARDENING')" class="w3-right w3-text-blue">Читать все...</a></li>
        <c:forEach var="news" items="${egardeningNews}">
        <li class="w3-hover-light-gray  w3-left-align w3-margin-bottom">
            <div class="w3-left-align">
                <img src="public/news/${news.id}/small-image.jpg" style="width:100%" alt="${news.title}" title="${news.title}">
                <h4 class=" w3-text-theme">${news.title}</h4>

                <article>
                    <%
                        NewsView news = (NewsView)pageContext.getAttribute("news");
                        String newsURI = "http://localhost:8080/public/news/"+news.getId();
                        URL url = new URL(newsURI+"/short-content.html");
                        InputStreamReader is = new InputStreamReader(url.openStream());
                        String content = IOUtils.toString(is);
                        content = content.replaceAll("_path_", newsURI);
                        out.write(content);
                    %>
                    <span>&emsp;</span><a href="news_full.tiles?id=${news.id}" class="w3-text-blue">Читать...</a></article>

            </div>
        </li>
        </c:forEach>
        <li class="w3-light-gray w3-hover-white  w3-left-align"><b>Новости партнеров</b><a href="news.tiles?query=(state eq 'DEPLOYED' and newsGroup eq 'PARTNER')" class="w3-right w3-text-blue">Читать все...</a></li>
        <c:forEach var="news" items="${partnerNews}">
        <li class="w3-hover-light-gray  w3-left-align">
            <div class="w3-left-align">
                <img src="public/news/${news.id}/small-image.jpg" style="width:100%" alt="${news.title}" title="${news.title}">
                <h4 class=" w3-text-theme">${news.title}</h4>

                <article>
                    <%
                        NewsView news = (NewsView)pageContext.getAttribute("news");
                        String newsURI = "http://localhost:8080/public/news/"+news.getId();
                        URL url = new URL(newsURI+"/short-content.html");
                        InputStreamReader is = new InputStreamReader(url.openStream());
                        String content = IOUtils.toString(is);
                        content = content.replaceAll("_path_", newsURI);
                        out.write(content);
                    %>
                    <span>&emsp;</span><a href="news_full.tiles?id=${news.id}" class="w3-text-blue">Читать...</a></article>
            </div>
        </li>
        </c:forEach>
    </ul>
</div>