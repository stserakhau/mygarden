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
    <meta name="keywords" content="умный сад, egardening, e-gardening, easy gardening, electronic gardening, блокнот садовода, календарь садовода, электронное садоводство, классификация удобрений, классификация садовых растений, огород на подоконнике, домашний огород">

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

<!-- Navigation bar -->
<nav id="top_nav">
    <div class="w3-bar w3-theme-dark w3-left-align w3-large">
        <a class="w3-bar-item w3-button w3-opennav w3-right w3-hide-large  w3-large w3-theme-l1" onclick="openSideNav()"><i class="fa fa-bars"></i></a>
        <a href="index.jsp" class="w3-bar-item w3-button w3-theme-l1  w3-padding-0" ><img src="static/logo.png" style="height:41px" title="На главную"></a>
        <a role="ROLE_USER" style="display:none" href="my_work_plan.tiles" class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-slim">МОЙ ПЛАН РАБОТ</a>
        <a role="ROLE_USER" style="display:none" href="species_inventory_user.tiles" class="w3-bar-item w3-button w3-hide-small w3-hide-medium w3-slim">МОЯ БИБЛИОТЕКА</a>
        <a href="species_inventory_system.tiles" class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-slim">КУЛЬТУРЫ</a>
        <a href="fertilizers_inventory.tiles" class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-slim">УДОБРЕНИЯ</a>
        <a href="work_inventory.tiles" class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-slim">РАБОТЫ</a>
        <!--a href="help_species_inventory.tiles" title="Помощь" class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-slim"><span class="fa fa-question-circle w3-xlarge"></span></a-->
        <a role="" anonymous="visible" style="display:none" title="Войти" onclick="doAction('login');"
           class="w3-bar-item w3-button w3-hide-small w3-hide-medium w3-right w3-slim"><span
                class="fa fa-sign-in">&emsp;</span>Войти</a>
        <a role="ROLE_USER" anonymous="hidden" style="display:none" title="Выйти" onclick="logout();"
           class="w3-bar-item w3-button w3-hide-small w3-hide-medium w3-right w3-slim"><span
                class="fa fa-sign-out">&emsp;</span>Выйти</a>
        <a role="ROLE_USER" anonymous="hidden" style="display:none" title="Личный кабинет" href="user_profile.tiles"
           class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-right w3-slim"><span
                class="fa fa-user w3-xlarge">&emsp;</span>Личный кабинет</a>
        <a role="" anonymous="visible" style="display:none" href="user_registration.tiles" title="Регистрация"
           class="w3-bar-item w3-button w3-hide-small w3-hide-medium  w3-right w3-slim"><span
                class="fa fa-user-plus w3-xlarge">&emsp;</span>Регистрация</a>
    </div>
</nav>

<!-- Side Navigation -->
<nav class="w3-hide-large w3-sidenav w3-theme-l5 w3-animate-left" style="display:none " id="mainSidenav">
    <a onclick="closeSideNav()" class="w3-closenav w3-xlarge w3-text-theme">Закрыть
        <i class="fa fa-remove"></i>
    </a>
    <a href="index.jsp" class="w3-xlarge w3-theme-l1"><img src="static/logo.png"  title="На главную"></a>
    <a role="ROLE_USER" style="display:none" href="my_work_plan.tiles" class="w3-xlarge w3-slim">МОЙ ПЛАН РАБОТ</a>
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


<div class="w3-mobile w3-hide-small" id="mainHeader" style="padding-top: 40px; line-height: 40px; overflow: hidden; z-index: 2;">
    <a href="index.jsp" class="w3-hide-small "><img src="static/eGardening.png" title="eGardening.ru" style="height:100%"></a>
    <div class="w3-right w3-wide w3-hide-medium" style="font-family:'Segoe UI',Arial,sans-serif">ЭЛЕКТРОННЫЙ БЛОКНОТ САДОВОДА</div>
</div>


<div class="w3-display-container w3-text-grey w3-center w3-padding-32">
    <div id="whatDoYouWantToGrow" class="w3-display-container w3-section w3-content " style="max-width:400px; padding-left: 50px;">
        <button class="w3-button w3-light-gray w3-display-left" style="height:100%" onclick="plusDivs(-1)">&#10094;</button>
        <button class="w3-button w3-light-gray w3-display-right" style="height:100%" onclick="plusDivs(1)">&#10095;</button>
    </div>
    <div class="w3-display-container w3-section w3-content" style="max-width:500px;">
        <label id="carouselPatternName" class="w3-label w3-display-bottom">Название шаблона</label>
    </div>
    <div class="w3-display-container w3-section w3-content" style="max-width:500px;">
        <a class="w3-btn-block w3-margin-bottom w3-theme-dark" onclick="FAST_PLAN_WINDOW.open()">Хочу!</a>
        <a id="carouselEditPlan" class="w3-btn-block w3-margin-bottom w3-theme">Как выращивать?</a>
        <a class="w3-hover-text-blue" href="species_inventory_system.tiles">Все культуры...</a>
    </div>

    <script type="text/javascript">
        document.afterInitializationUIEnvironmentFunc = initializePage;

        var myIndex = 0;
        var slideIndex = 1;
        var carouselItems;

        function initializePage() {
            FAST_PLAN_WINDOW.initialize({
                callback: gotoMyWorkPlan
            });

            $.get(
                    "api/data-storage/homePageCarousel",
                    function(items) {
                        carouselItems = items;
                        renderCarouselItems();
                    }
            )
        }

        function scrollWin() {
            window.scrollBy(0, 500);
        }

        function carousel() {
            var i;
            var images = document.getElementsByClassName("mySlides");
            for (i = 0; i < images.length; i++) {
                images[i].style.display = "none";
            }
            myIndex++;
            if (myIndex > images.length) {myIndex = 1}
            images[myIndex-1].style.display = "block";
            x(images, myIndex-1);
            setTimeout(carousel, 6000); // Change image every 6 seconds
        }
        function plusDivs(n) {
            showDivs(slideIndex += n);
        }

        function showDivs(n) {
            var i;
            var images = document.getElementsByClassName("mySlides");
            if (n > images.length) {slideIndex = 1}
            if (n < 1) {slideIndex = images.length}
            for (i = 0; i < images.length; i++) {
                images[i].style.display = "none";
            }
            images[slideIndex-1].style.display = "block";

            x(images, slideIndex-1)
        }
        function x(images, index) {
            var patternIds = images[index].getAttribute('patternIds').split('|');
            var patternTitles = images[index].getAttribute('titles').split('|');
            var speciesId = images[index].getAttribute('speciesId');
            var total = patternIds.length;
            var randomIndex = Math.floor(total * Math.random());

            var selectedPatternTitle = patternTitles[randomIndex];
            var selectedPatternId = patternIds[randomIndex];
            if(FAST_PLAN_WINDOW.config.opened==false) {
                FAST_PLAN_WINDOW.config.title = selectedPatternTitle;
                FAST_PLAN_WINDOW.config.patternId = selectedPatternId;
            }
            $("#carouselPatternName").html(selectedPatternTitle);
            $("#carouselEditPlan").attr("href", "add_system_species_work.tiles?speciesId="+speciesId+"&userPatternId="+selectedPatternId)
        }
        function renderCarouselItems() {
            for(var i=0;i<carouselItems.length; i++) {
                var item = carouselItems[i];
                var fileId = item[0];
                var speciesId = item[1];
                var patternIds = item[2];
                var patternNames = item[3];
                var imagePath = 'api/data-storage/' + fileId;
                $("<img class='mySlides w3-display-center w3-mobile' src='"+imagePath+"' speciesId='"+speciesId+"' patternIds='"+patternIds+"' titles='" + patternNames + "' style='height:300px;position:relative;left:50pх'>")
                        .prependTo("#whatDoYouWantToGrow");
            }
            carousel();
            showDivs(slideIndex);
        }
    </script>

</div>



<!-- Page content -->
<div class="w3-row w3-padding-0 w3-center" style="bottom:0px">
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


<script>
    $(function(){
        // высота "шапки", px
        var h_hght = window.innerHeight;
        // высота блока с меню, px
        var h_nav = 40;
        var top;
        $(window).scroll(function(){
            // отступ сверху
            top = $(this).scrollTop();
            if((h_hght-top) <= h_nav){
                $('#top_nav').css('top','0');
            }
            else if(top < h_hght && top > 0){
                $('#top_nav').css({'bottom' : top, 'top':''});
            }
            else if(top < h_nav){
                $('#top_nav').css({'top':'','bottom':'0'});
            }
        });
    });
</script>