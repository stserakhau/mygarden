<%@ page import="org.apache.commons.io.IOUtils" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.net.URL" %>
<%@ page import="com.i4biz.mygarden.domain.news.NewsView" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="w3-row w3-margin-bottom l3 m6 s12">
    <c:forEach var="item" items="${content}">
        <div class="w3-col l3 m6 s12 w3-padding-tiny w3-mobile">
            <div class="w3-card-2" style="height: 320px;">
                <img src="public/news/${item.id}/small-image.jpg" alt='${item.title}' align="left" style="width:100%">
                <div class="w3-padding-tiny">
                    <h6>${item.title}</h6>
                <%
                    NewsView news = (NewsView)pageContext.getAttribute("item");
                    String newsURI = "http://localhost:8080/public/news/"+news.getId();
                    URL url = new URL(newsURI+"/short-content.html");
                    InputStreamReader is = new InputStreamReader(url.openStream());
                    String content = IOUtils.toString(is);
                    content = content.replaceAll("_path_", newsURI);
                    out.write(content);
                %>

                <a href="news_full.tiles?id=${item.id}" class="w3-hover-text-blue" target="news${item.id}">Читать полностью...</a>
                </div>
            </div>
        </div>
    </c:forEach>
</div>