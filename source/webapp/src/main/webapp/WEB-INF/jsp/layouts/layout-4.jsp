<%@ page contentType="text/html;charset=UTF-8" language="java" %><%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="cacheTimeout" scope="request"><tiles:getAsString name="cacheTimeout"/></c:set><c:if test="${cacheTimeout > 0}"><%
    javax.servlet.http.HttpServletResponse httpResponse = (HttpServletResponse) response;
    httpResponse.setHeader("Cache-Control", "private");
    Long cacheTimeout = System.currentTimeMillis() + Long.parseLong((String)request.getAttribute("cacheTimeout"));
    httpResponse.setDateHeader("Expires", cacheTimeout);
    httpResponse.setDateHeader("Max-Age", cacheTimeout);
%></c:if><!DOCTYPE html>
<html>
<head>
    <title><tiles:getAsString name="title" ignore="true"/></title>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>

    <link rel="stylesheet" href="static/wait.css"/>
    <link rel="stylesheet" href="static/w3.css"/>
    <link rel="stylesheet" href="static/w3-theme-teal.css"/>
    <link rel="stylesheet" href="static/font-awesome.min.css"/>
    <link rel="stylesheet" href="static/custom.css"/>
    <link rel="shortcut icon" href="tools.ico" type="image/x-icon">
    <link rel="stylesheet" href="static/help-system.css"/>

    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <script type="text/javascript" src="static/date.js"><!-- --></script>
    <script type="text/javascript" src="static/jquery-3.1.1.min.js"><!-- --></script>
    <script type="text/javascript" src="static/common_nav.js"><!-- --></script>
    <script type="text/javascript" src="static/help-system.js"><!-- --></script>

    <%@include file="ga.jsp"%>
    <%@include file="ad-sence.jsp"%>
</head>
<body>

<!-- Navigation bar -->

<div class="w3-top w3-card-2" id="mainHeader" style="width:100%;     z-index: 1;">
    <div class="w3-bar w3-theme-dark w3-left-align w3-large">
        <a class="w3-bar-item w3-button w3-opennav w3-right w3-hide-large  w3-large w3-theme-l1" href="javascript:void(0)" onclick="openSideNav()"><i class="fa fa-bars"></i></a>
        <a href="index.jsp" class="w3-bar-item w3-button w3-theme-l1  w3-padding-0" ><img src="static/logo.png" style="height:41px"  title="На главную"></a>
        <a role="ROLE_USER" style="display:none" href="my_work_plan.tiles" class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-slim">МОЙ ПЛАН РАБОТ</a>
        <a role="ROLE_USER" style="display:none" href="my_plant.tiles"
           class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-slim">МОЙ ОГОРОД</a>
        <a role="ROLE_USER" style="display:none" href="species_inventory_user.tiles" class="w3-bar-item w3-button w3-hide-small w3-hide-medium w3-slim">МОЯ БИБЛИОТЕКА</a>
        <a href="species_inventory_system.tiles" class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-slim">КУЛЬТУРЫ</a>
        <a href="fertilizers_inventory.tiles" class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-slim">УДОБРЕНИЯ</a>
        <a href="work_inventory.tiles" class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-slim">РАБОТЫ</a>
        <!--a href="help_species_inventory.tiles" title="Помощь" class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-slim"><span class="fa fa-question-circle w3-xlarge"></span></a-->
        <a role="" anonymous="visible" style="display:none" title="Войти" onclick="doAction('login');"
           class="w3-bar-item w3-button w3-hide-small w3-hide-medium w3-right w3-slim"><span
                class="fa fa-sign-in w3-xlarge">&emsp;</span>Войти</a>
        <a role="ROLE_USER" anonymous="hidden" style="display:none" title="Выйти" onclick="logout();"
           class="w3-bar-item w3-button w3-hide-small w3-hide-medium w3-right w3-slim"><span
                class="fa fa-sign-out w3-xlarge">&emsp;</span>Выйти</a>
        <a role="ROLE_USER" anonymous="hidden" style="display:none" title="Личный кабинет" href="user_profile.tiles"
           class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-right w3-slim"><span
                class="fa fa-user w3-xlarge">&emsp;</span>Личный кабинет</a>
        <a role="" anonymous="visible" style="display:none" href="user_registration.tiles" title="Регистрация"
           class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-right w3-slim"><span
                class="fa fa-user-plus w3-xlarge">&emsp;</span>Регистрация</a>
    </div>
</div>

<!-- Side Navigation -->
<nav class="w3-hide-large w3-sidenav w3-theme-l5 w3-animate-left" style="display:none" id="mainSidenav">
    <a href="javascript:void(0)" onclick="closeSideNav()" class="w3-closenav w3-xlarge w3-text-theme">Закрыть
        <i class="fa fa-remove"></i>
    </a>
    <a href="index.jsp" class="w3-xlarge w3-theme-l1"><img src="static/logo.png"  title="На главную"></a>
    <a role="ROLE_USER" style="display:none" href="my_work_plan.tiles" class="w3-xlarge w3-slim">МОЙ ПЛАН РАБОТ</a>
    <a role="ROLE_USER" style="display:none" href="my_plant.tiles" class="w3-xlarge w3-slim">МОЙ ОГОРОД</a>
    <a role="ROLE_USER" style="display:none" href="species_inventory_user.tiles" class="w3-xlarge w3-slim">МОЯ БИБЛИОТЕКА</a>
    <a href="species_inventory_system.tiles" class="w3-xlarge w3-slim">КУЛЬТУРЫ</a>
    <a href="fertilizers_inventory.tiles" class="w3-xlarge w3-slim">УДОБРЕНИЯ</a>
    <a href="work_inventory.tiles" class="w3-xlarge w3-slim">РАБОТЫ</a>
    <!--a href="help_species_inventory.tiles" title="Помощь" class="w3-xlarge w3-slim"><span class="fa fa-question-circle w3-xxlarge">&emsp;</span></a-->
    <a role="" anonymous="visible" style="display:none" title="Войти" onclick="doAction('login');"
       class="w3-xlarge w3-slim"><span class="fa fa-sign-in w3-xlarge">&emsp;</span>Войти</a>
    <a role="ROLE_USER" anonymous="hidden" style="display:none" title="Выйти" onclick="logout();"
       class="w3-xlarge w3-slim"><span class="fa fa-sign-out w3-xlarge">&emsp;</span>Выйти</a>
    <a role="ROLE_USER" anonymous="hidden" style="display:none" title="Личный кабинет" href="user_profile.tiles"
       class="w3-xlarge w3-slim"><span class="fa fa-user w3-xlarge">&emsp;</span>Личный кабинет</a>
    <a role="" anonymous="visible" style="display:none" href="user_registration.tiles" title="Регистрация"
       class="w3-xlarge w3-slim"><span class="fa fa-user-plus w3-xlarge">&emsp;</span>Регистрация</a>
</nav>

<!-- Page content -->
<div class="w3-row w3-row-padding w3-center" style="margin-top: 72px; margin-bottom: 150px; min-height: 600px">
    <tiles:insertAttribute name="ads" ignore="true"/>
    <tiles:insertAttribute name="body" ignore="true"/>
</div>

<!-- Footer -->
<footer class="w3-container w3-theme-dark">
    <div class="w3-layout-container">
        <div class="w3-container w3-layout-col w3-small">
            <p><a href="/index.jsp#about" class="w3-hover-text-blue">О проекте</a></p>
        </div>
        <div class="w3-container w3-layout-col w3-small">
            <p><a href="user_agreement.tiles" class="w3-hover-text-blue">Пользовательское соглашение</a></p>
        </div>
        <div class="w3-container w3-layout-col w3-small">
            <p><a href="cooperation.tiles" class="w3-hover-text-blue">Сотрудничество</a></p>
        </div>
        <div class="w3-container w3-layout-col w3-small">
            <p><a href="ads.tiles" class="w3-hover-text-blue">Реклама на сайте</a></p>
        </div>
        <div class="w3-container w3-layout-col w3-small">
            <p><a href="/news.tiles" class="w3-hover-text-blue">Новости</a></p>
        </div>
        <div class="w3-container w3-layout-col w3-small">
            <p><a href="/index.jsp#contacts" class="w3-hover-text-blue">Контакты</a></p>
        </div>
    </div>
    <div style="position:relative;bottom:40px;" class="w3-tooltip w3-right">
        <span class="w3-text w3-theme w3-padding">Наверх</span>&nbsp;
        <a class="w3-text-white" href="#mainHeader"><span class="w3-xlarge">
            <i class="fa fa-chevron-circle-up"></i></span></a>
    </div>
    <div class="w3-container w3-small w3-center">
        <p><i class="fa fa-copyright"></i> eGardening.ru.  Использование материалов портала возможно только при явном указании источника и наличии активной ссылки.</p>
    </div>
</footer>
</body>
</html>
