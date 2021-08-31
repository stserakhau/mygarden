<%@ page import="org.apache.commons.io.IOUtils" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.net.URL" %>
<%@ page import="com.i4biz.mygarden.domain.news.NewsView" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:forEach var="item" items="${content}">
    <div class="w3-card w3-border-0 w3-padding w3-margin-top w3-margin-bottom" style="height: 380px">
        <img src="public/news/${item.id}/small-image.jpg" alt='${news.title}' title='${news.title}' align="left" style="width:100%">
        <p>${item.title}</p>
        <%
            NewsView news = (NewsView) pageContext.getAttribute("item");
            String newsURI = "http://localhost:8080/public/news/" + news.getId();
            URL url = new URL(newsURI + "/short-content.html");
            InputStreamReader is = new InputStreamReader(url.openStream());
            String content = IOUtils.toString(is);
            content = content.replaceAll("_path_", newsURI);
            out.write(content);
        %>
        <span>&emsp;</span><a href="news_full.tiles?id=${item.id}" class="w3-text-blue" target="news${item.id}">Читать
        полностью...</a></p>
    </div>
</c:forEach>