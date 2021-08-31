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
    <script type="text/javascript" src="static/jquery-3.1.1.min.js"><!-- --></script>
    <script type="text/javascript" src="static/date.js"><!-- --></script>
    <script type="text/javascript" src="static/common_nav.js"><!-- --></script>
    <script type="text/javascript" src="static/help-system.js"><!-- --></script>

    <%@include file="ga.jsp"%>
    <%@include file="ad-sence.jsp"%>
</head>
<body>
<!-- Side Navigation -->
<nav class="w3-hide-medium w3-hide-large w3-sidenav w3-white w3-card-2 w3-animate-left" style="display:none" id="mainSidenav">
    <a href="javascript:void(0)" onclick="closeSideNav()" class="w3-closenav w3-xlarge w3-text-theme">Закрыть
        <i class="fa fa-remove"></i>
    </a>
    <a role="ROLE_USER" style="display:none" href="index.jsp"><i class="fa fa-home w3-xlarge"></i></a>
    <a role="ROLE_USER" style="display:none" href="my_work_plan.tiles" class="w3-xlarge"><i class="fa fa-calendar"></i> Мой план работ</a>
    <a role="ROLE_USER" style="display:none" href="species_inventory_user.tiles" class="w3-xlarge"><i class="fa fa-book"></i> МОЯ БИБЛИОТЕКА</a>
    <a href="species_inventory_system.tiles" class="w3-xlarge"><i class="fa fa-book"></i> Культуры</a>
    <a href="fertilizers_inventory.tiles" class="w3-xlarge"><i class="fa fa-flask"></i> Удобрения</a>
    <a href="work_inventory.tiles" class="w3-xlarge"><i class="fa fa-gavel"></i> Работы</a>
</nav>

<!-- Navigation bar -->
<div style="z-index: 10">
    <ul class="w3-hide-small w3-navbar w3-large w3-theme-dark w3-left-align" style="position: fixed; top:0px; width:100%; z-index: 10;">
        <li style="width:56px"><a class="w3-hide-medium w3-hide-large w3-padding-16 w3-hover-white" title="Меню"><i onclick="openSideNav()" class="fa fa-bars w3-xlarge w3-opennav"></i></a></li>
        <li style="width:56px"><a class="w3-padding-16 w3-hover-white" href="index.jsp" title="Главная"><i class="fa fa-home w3-xlarge"></i></a></li>
        <li role="ROLE_USER" style="display:none"><a class="w3-hide-small w3-padding-16 w3-hover-white" href="my_work_plan.tiles"><i class="fa fa-calendar"></i> Мой план работ</a></li>
        <li role="ROLE_USER" style="display:none"><a class="w3-padding-16 w3-hover-white" href="species_inventory_user.tiles"><i class="fa fa-book"></i> МОЯ БИБЛИОТЕКА</a></li>
        <li><a class="w3-padding-16 w3-hover-white" href="species_inventory_system.tiles"><i class="fa fa-book"></i> Культуры</a></li>
        <li><a class="w3-padding-16 w3-hover-white" href="fertilizers_inventory.tiles"><i class="fa fa-flask"></i> Удобрения</a></li>
        <li><a class="w3-padding-16 w3-hover-white" href="work_inventory.tiles"><i class="fa fa-gavel"></i> Работы</a>
        </li>
        <li><a class="w3-padding-16 w3-hover-white" href="work_inventory.tiles"><i class="fa fa-question-circle"></i> Помощь</a></li>
        <li class="w3-right" style="width:56px">
            <a role="" anonymous="visible" style="display:none" title="Войти" onclick="doAction('login');"
               class="w3-padding-16 w3-hover-white"><i class="fa fa-sign-in w3-xlarge"></i></a>
            <a role="ROLE_USER" anonymous="hidden" style="display:none" title="Выйти" onclick="logout();"
               class="w3-padding-16 w3-hover-white"><i class="fa fa-sign-out w3-xlarge"></i></a>
        </li>

        <li class="w3-right" style="width:56px">
            <a role="ROLE_USER" anonymous="hidden" style="display:none" class="w3-padding-16 w3-hover-white"
               href="user_profile.tiles" title="Личный кабинет"><i class="fa fa-user w3-xlarge"></i></a>
            <a role="" anonymous="visible" style="display:none" class="w3-padding-16 w3-hover-white"
               href="user_registration.tiles" title="Регистрация"><i class="fa fa-pencil-square w3-xlarge"></i></a>
        </li>
    </ul>
    <ul class="w3-hide-large w3-hide-medium w3-navbar w3-large w3-theme-dark w3-left-align">
        <li style="width:56px"><a class="w3-hide-large w3-hide-medium w3-padding-16 w3-hover-white" title="Меню"><i onclick="openSideNav()" class="fa fa-bars w3-xlarge w3-opennav"></i></a></li>
        <li class="w3-right" style="width:56px">
            <a role="" style="display:none" title="Войти" onclick="doAction('login');"
               class="w3-padding-16 w3-hover-white"><i class="fa fa-sign-in w3-xlarge"></i></a>
            <a role="ROLE_USER" style="display:none" title="Выйти" onclick="logout();" class="w3-padding-16 w3-hover-white"><i class="fa fa-sign-out w3-xlarge"></i></a>
        </li>

        <li class="w3-right" style="width:56px"><a class="w3-padding-16 w3-hover-white" href="user_profile.tiles" title="Личный кабинет"><i class="fa fa-user w3-xlarge"></i></a></li>
        <li class="w3-right" style="width:56px"><a class="w3-padding-16 w3-hover-white" href="user_registration.tiles" title="Регистрация"><i class="fa fa-address-card w3-xlarge"></i></a></li>
    </ul>
</div>
<!-- Header -->

<!-- Page content -->
<div class="w3-row-padding w3-center w3-margin-bottom" style="margin-top:72px">
    <tiles:insertAttribute name="body" ignore="true"/>
</div>

<!-- Footer -->
<footer class="w3-container w3-theme-dark">
    <div class="w3-layout-container">
        <div class="w3-container w3-layout-col w3-small">
            <p><a href="about.tiles" class="w3-hover-text-blue">О проекте</a></p>
        </div>
        <div class="w3-container w3-layout-col w3-small">
            <p><a href="cooperation.tiles" class="w3-hover-text-blue">Сотрудничество</a></p>
        </div>
        <div class="w3-container w3-layout-col w3-small">
            <p><a href="ads.tiles" class="w3-hover-text-blue">Реклама на портале</a></p>
        </div>
        <div class="w3-container w3-layout-col w3-small">
            <p><a href="feedback.tiles" class="w3-hover-text-blue">Контакты</a></p>
        </div>
    </div>
    <div style="position:relative;bottom:40px;" class="w3-tooltip w3-right">
        <span class="w3-text w3-theme w3-padding">Наверх</span>&nbsp;
        <a class="w3-text-white" href="#mainHeader"><span class="w3-xlarge">
            <i class="fa fa-chevron-circle-up"></i></span></a>
    </div>
    <div class="w3-container w3-small w3-center">
        <p><i class="fa fa-copyright"></i> Ирина Терехова.  Использование материалов портала возможно только при явном указании источника и наличии активной ссылки.</p>
    </div>
</footer>
</body>
</html>