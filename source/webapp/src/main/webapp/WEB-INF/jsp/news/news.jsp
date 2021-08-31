<%@ page import="com.i4biz.mygarden.domain.news.NewsView" %>
<%@ page import="org.apache.commons.io.IOUtils" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.net.URL" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<c:forEach var="news" items="${newsPage.items}">
    <div class="w3-row-padding w3-container w3-left-align w3-border-bottom w3-text-grey  w3-margin-bottom" style="border-bottom-style: dotted">
        <img src="public/news/${news.id}/small-image.jpg" alt='${news.title}' align="left" style="width:100%" alt="${news.title}" title="${news.title}">
        <h1>${news.title}<span class="w3-right w3-small w3-text-gray"><fmt:formatDate value="${news.publishingDate}" pattern="dd/MM/yyyy"/></span></h1>
        <p>
            <%
                NewsView news = (NewsView)pageContext.getAttribute("news");
                String newsURILocal = "http://localhost:8080/public/news/"+news.getId();
                String newsURIPublic = "https://egardening.ru/public/news/"+news.getId();
                URL url = new URL(newsURILocal+"/short-content.html");
                InputStreamReader is = new InputStreamReader(url.openStream());
                String content = IOUtils.toString(is);
                content = content.replaceAll("_path_", newsURIPublic);
                out.write(content);
            %>
            <span>&emsp;</span><a href="news_full.tiles?id=${news.id}" class="w3-text-blue">Читать полностью...</a></p>
    </div>
</c:forEach>