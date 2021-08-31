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

<div class="w3-container" id="mainHeader" style="height: 160px     position: relative; background-color: #ffffff; padding-top: 20px; line-height: 50px; overflow: hidden; z-index: 2;">
    <a href="index.jsp"><img src="static/eGardening.png"></a>
    <div class="w3-right w3-hide-small w3-hide-medium w3-wide" style="font-family:'Segoe UI',Arial,sans-serif">САЙТ ПРОДВИНУТЫХ САДОВОДОВ</div>
</div>


<!-- Navigation bar -->

<div class="top_nav w3-card-2" id="top_nav" style="overflow:auto; width:100% ">
    <div class="w3-bar w3-theme-dark w3-left-align w3-large">
        <a class="w3-bar-item w3-button w3-opennav w3-right w3-hide-large  w3-large w3-theme-l1" href="javascript:void(0)" onclick="openSideNav()"><i class="fa fa-bars"></i></a>
        <a href="index.jsp" class="w3-bar-item w3-button w3-theme-l1  w3-padding-0" ><img src="static/logo.png" style="height:41px"></a>
        <a role="ROLE_USER" style="display:none" href="my_work_plan.tiles" class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-slim">МОЙ ПЛАН РАБОТ</a>
        <a role="ROLE_USER" style="display:none" href="species_inventory_user.tiles" class="w3-bar-item w3-button w3-hide-small w3-hide-medium w3-slim">МОЯ БИБЛИОТЕКА</a>
        <a href="species_inventory_system.tiles" class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-slim">КУЛЬТУРЫ</a>
        <a href="fertilizers_inventory.tiles" class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-slim">УДОБРЕНИЯ</a>
        <a role="ROLE_USER" style="display:none" href="work_inventory.tiles"
           class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-slim">РАБОТЫ</a>
        <a href="help_species_inventory.tiles" title="Помощь" class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-slim"><span class="fa fa-question-circle w3-xlarge"></span></a>
        <a role="" style="display:none" title="Войти" onclick="doAction('login');"
           class="w3-bar-item w3-button w3-hide-small w3-hide-medium w3-right w3-slim"><span
                class="fa fa-sign-in">&emsp;</span>Войти</a>
        <a role="ROLE_USER" style="display:none" title="Выйти" onclick="logout();" class="w3-bar-item w3-button w3-hide-small w3-hide-medium w3-right w3-slim"><span class="fa fa-sign-out">&emsp;</span>Выйти</a>
        <a role="ROLE_USER" style="display:none" title="Личный кабинет" href="user_profile.tiles" class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-right w3-slim"><span class="fa fa-user w3-xlarge">&emsp;</span>Личный кабинет</a>
        <a role="" style="display:none" href="user_registration.tiles" title="Регистрация" class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-right w3-slim"><span class="fa fa-user-plus w3-xlarge">&emsp;</span>Регистрация</a>
    </div>
</div>

<!-- Side Navigation -->
<nav class="w3-hide-large w3-sidenav w3-theme-l5 w3-animate-left" style="display:none" id="mainSidenav">
    <a href="javascript:void(0)" onclick="closeSideNav()" class="w3-closenav w3-xlarge w3-text-theme">Закрыть
        <i class="fa fa-remove"></i>
    </a>
    <a href="index.jsp" class="w3-xlarge w3-theme-l1"><img src="static/logo.png"></a>
    <a role="ROLE_USER" style="display:none" href="my_work_plan.tiles" class="w3-xlarge w3-slim">МОЙ ПЛАН РАБОТ</a>
    <a role="ROLE_USER" style="display:none" href="species_inventory_user.tiles" class="w3-xlarge w3-slim">МОЯ БИБЛИОТЕКА</a>
    <a href="species_inventory_system.tiles" class="w3-xlarge w3-slim">КУЛЬТУРЫ</a>
    <a href="fertilizers_inventory.tiles" class="w3-xlarge w3-slim">УДОБРЕНИЯ</a>
    <a role="ROLE_USER" style="display:none" href="work_inventory.tiles" class="w3-xlarge w3-slim">РАБОТЫ</a>
    <a href="help_species_inventory.tiles" title="Помощь" class="w3-xlarge w3-slim"><span class="fa fa-question-circle w3-xxlarge">&emsp;</span></a>
    <a role="" style="display:none" title="Войти" onclick="doAction('login');" class="w3-xlarge w3-slim"><span
            class="fa fa-sign-in w3-xlarge">&emsp;</span>Войти</a>
    <a role="ROLE_USER" style="display:none" title="Выйти" onclick="logout();" class="w3-xlarge w3-slim"><span class="fa fa-sign-out w3-xlarge">&emsp;</span>Выйти</a>
    <a role="ROLE_USER" style="display:none" title="Личный кабинет" href="user_profile.tiles" class="w3-xlarge w3-slim"><span class="fa fa-user w3-xlarge">&emsp;</span>Личный кабинет</a>
    <a role="" style="display:none" href="user_registration.tiles" title="Регистрация" class="w3-xlarge w3-slim"><span class="fa fa-user-plus w3-xlarge">&emsp;</span>Регистрация</a>
</nav>

<!-- Page content -->
<div class="w3-row w3-padding-0 w3-center" style="margin-bottom: 150px; margin-top: 72px; min-height: 600px">

        <div class="w3-quarter">
            <div style="overflow: auto; max-height: 1000px">
            <ul class="w3-ul w3-text-theme w3-center">
                <li class="w3-hover-white  w3-left-align w3-theme-light"><a href="help_species_inventory.tiles"><b>Культуры</b></a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_add_species_class.tiles">Добавить класс культур</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_edit_species_class.tiles">Редактировать класс культур</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_delete_species_class.tiles">Удалить класс культур</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_add_species.tiles">Добавить культуру</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_edit_species.tiles">Редактировать культуру</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_delete_species.tiles">Удалить культуру</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_copy_species.tiles">Копировать культуру</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_view_species_templates.tiles">Посмотреть культуру и ее шаблоны выращивания</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_add_template.tiles">Добавить шаблон</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_view_template.tiles">Посмотреть шаблон</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_edit_template.tiles">Редактировать шаблон</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;&emsp;</span><a href="help_add_template_work.tiles">Добавить работу в шаблон</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;&emsp;</span><a href="help_edit_template_work.tiles">Редактировать работу в шалоне</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;&emsp;</span><a href="help_delete_template_work.tiles">Удалить работу из шаблона</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_copy_template.tiles">Копировать шаблон</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_delete_template.tiles">Удалить шаблон</a></li>
                <li class="w3-hover-light-gray  w3-left-align w3-theme-light"><span></span><a href="help_work_inventory.tiles"><b>Работы</b></a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_add_work.tiles">Добавить работу</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_edit_work.tiles">Редактировать работу</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_delete_work.tiles">Удалить работу</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_view_work_species.tiles">Посмотреть культуры для работы</a></li>
                <li class="w3-hover-light-gray  w3-left-align w3-theme-light"><span></span><a href="help_fertilizer_inventory.tiles"><b>Удобрения</b></a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_add_fertilizer_class.tiles">Добавить класс удобрений</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_edit_fertilizer_class.tiles">Редактировать класс удобрений</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_delete_fertilizer_class.tiles">Удалить класс удобрений</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_add_fertilizer.tiles">Добавить удобрение</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_delete_fertilizer.tiles">Удалить удобрение</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_view_fertilizer_settings.tiles">Посмотреть настройки удобрения</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_add_fertilizer_settings.tiles">Добавить настройки удобрения</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_delete_fertilizer_settings.tiles">Удалить настройки удобрения</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_restore_fertilizer_settings.tiles">Сбросить настройки удобрения</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_restore_fertilizer_library_settings.tiles">Сбросить настройки библиотеки удобрений</a></li>

                <li class="w3-hover-white  w3-left-align w3-theme-light"><a href="help_my_work_plan.tiles"><b>Мой план работ</b></a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_add_species_plan.tiles">Добавить культуру в мой план работ</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;&emsp;</span><a href="help_save_plan_as_template.tiles">Сохранить план работ как шаблон</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_edit_species_plan.tiles">Редактировать план работ для культуры</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_delete_species_plan.tiles">Удалить план работ для культуры</a></li>
                <li class="w3-hover-light-gray  w3-left-align"><span>&emsp;</span><a href="help_delete_work_from_species_plan.tiles">Удалить работу из плана для культуры</a></li>

            </ul>
            </div>
        </div>
        <div class="w3-half">
            <tiles:insertAttribute name="body" ignore="true"/>
        </div>
        <div class="w3-quarter">
        </div>
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


<script>
    var h_hght = 150; // высота шапки
    var h_mrg = 0;    // отступ когда шапка уже не видна

    $(function(){

        var elem = $('#top_nav');
        var top = $(this).scrollTop();

        if(top > h_hght){
            elem.css('top', h_mrg);
        }

        $(window).scroll(function(){
            top = $(this).scrollTop();

            if (top+h_mrg < h_hght) {
                elem.css('top', (h_hght-top));
            } else {
                elem.css('top', h_mrg);
            }
        });

    });
</script>