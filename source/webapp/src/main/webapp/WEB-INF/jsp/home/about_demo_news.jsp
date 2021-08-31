<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>

<!-- Container (Video) -->
<!--div class="homepage-video w3-container w3-hover-opacity" style="height: 700px">
    <div class="w3-display-container w3-display-middle " style="height: 700px; width:1023px; position: relative">
        <div class="w3-display-middle w3-xlarge">
            <a href="species_inventory_system.tiles" class="w3-btn w3-xlarge w3-slim w3-hover-lime">Начать планировать...</a>

        </div>
    </div>
</div-->
<style>


</style>
<div class="down w3-text-jumbo" onclick="scrollWin()">
    <p><span class="fa fa-arrow-down" ></span></p>
</div>

<!-- First Parallax Image - About Project -->
<div class="bgimg-1 w3-display-container w3-opacity-min " id="about">
    <div class="w3-display-middle">
        <span class="w3-xxlarge w3-text-white w3-wide">О проекте</span>
    </div>
</div>
<!-- Container (About) -->

<div class="w3-center w3-text-grey">
    <div class="w3-row w3-display-container" style="min-height:850px">
        <div class="w3-content w3-display-middle w3-mobile">
            <h1>Что такое eGardening?</h1>
            <ul class="w3-ul ">
                <li class="w3-padding-16">Это онлайн приложение для планирования сада и огорода, но Вы не найдете здесь статей или видео с советами</li>
                <li class="w3-padding-16">Фактически, Вы находитесь в электронном блокноте, где можете записать советы по выращиванию, подкормкам, указать сроки и удобрения. После этого у Вас исчезнет необходимость листать блокнот, чтобы ничего не пропустить. Система вышлет Вам напоминание, в какой день что должно быть сделано</li>
                <li class="w3-padding-16">Здесь есть огромная библиотека удобрений с указаниями по применению и несовместимыми удобрениями, которую можно дополнить и изменить</li>
                <li class="w3-padding-16">Еще Вы можете найти готовые методы по выращиванию и уходу за растениями</li>
                <li class="w3-padding-16">В разработке уже находится графический редактор, чтобы Вы смогли не только составить план работ на сезон, но и разметить участок</li>
            </ul>
        </div>
    </div>
</div>


<!-- Second Parallax Image - For whom -->
<div class="bgimg-2 w3-display-container w3-opacity-min" id="audience">
    <div class="w3-display-middle">
        <span class="w3-xxlarge w3-text-white w3-wide">Аудитория</span>
    </div>
</div>

<!-- Container (For whom) -->
<div class="w3-container w3-center w3-text-grey w3-row">
    <div class="w3-third w3-mobile">
        <div class="w3-card w3-border-0 w3-display-container w3-margin">
            <div class="w3-display-top">

            <h3>Городские садоводы</h3></br>
                <img src="/static/empty.jpg" style="width: 80px;height: 80px">
                <p> Увлеченные городским или экологическим садоводством люди, экспериментирующие с выращиванием
                    плодоовощных культур, трав и пряностей для своего стола на подоконнике, на балконе, лоджии или садовом участке</p>
            </div>
        </div>
    </div>

    <div class="w3-third w3-mobile">
        <div class="w3-card w3-border-0 w3-display-container w3-margin">
            <div class="w3-display-top">
                <h3>Продвинутые дачники</h3></br>
                <img src="/static/empty.jpg" style="width: 80px;height: 80px">
                <p> Опытные садоводы, составляющие план посадок, работ и необходимых удобрений на сезон</p>
            </div>
        </div>
    </div>

    <div class="w3-third w3-mobile">
        <div class="w3-card w3-border-0 w3-display-container w3-margin">
            <div class="w3-display-top">
                <h3>Предприниматели</h3></br>
                <img src="/static/empty.jpg" style="width: 80px;height: 80px">
                <p> Питомники и другие производители посадочного материала, планирующие как количество черенков и план прививок, так и сроки время- и
                    трудозатратных работ.</p>
            </div>
        </div>
    </div>
</div>

<!-- Third Parallax Image - Plans-->
<div class="bgimg-3 w3-display-container w3-opacity-min" id="features">
    <div class="w3-display-middle">
        <span class="w3-xxlarge w3-text-white w3-wide">Возможности</span>
    </div>
</div>

<!-- Container (Plans) -->
<div class="w3-center w3-text-grey">
    <div class="w3-row w3-display-container" style="min-height:400px">
        <div class="w3-content w3-display-middle w3-mobile">
            <h1>Что Вы можете делать здесь?</h1>
            <ul class="w3-ul">
                <li class="">Планирование подсадок и ухода за ними различными способами</li>
                <li class="">Указание применения удобрений или их категорической несовместимости</li>
                <li class="">Создание собственной библиотеки растений с методами выращивания и ухода</li>
            </ul>
        </div>
    </div>

</div>

<!-- Fourth Parallax Image - News-->
<div class="bgimg-4 w3-display-container w3-opacity-min" id="news">
    <div class="w3-display-middle w3-xxlarge w3-text-white w3-wide">
        <span class="" >Новости</span>

    </div>
</div>

<!-- Container (News) -->
<div class="w3-container w3-left-align w3-text-grey" >
    <tiles:insertAttribute name="news" ignore="true"/>
</div>

<!-- Fifth Parallax Image - Contacts-->
<div class="bgimg-5 w3-display-container w3-opacity-min" id="contacts">
    <div class="w3-display-middle">
        <span class="w3-xxlarge w3-text-white w3-wide">Контакты</span>
    </div>
</div>

<div class="w3-container w3-padding w3-margin w3-left-align w3-text-grey" >
        <div class="w3-third w3-mobile">
            <div class="w3-card w3-border-0 w3-display-container w3-margin" style="min-height:250px">
                <div class="w3-display-middle">
                    <script type="text/javascript" src="//vk.com/js/api/openapi.js?152"></script>

                    <!-- VK Widget -->
                    <div id="vk_groups"></div>
                    <script type="text/javascript">
                        VK.Widgets.Group("vk_groups", {mode: 3, width: "250"}, 161148443);
                    </script>
                </div>
            </div>
        </div>

        <div class="w3-third w3-mobile">
            <div class="w3-card w3-border-0 w3-display-container w3-margin" style="min-height:250px">
                <div class="w3-display-middle">
                    <div id="ok_group_widget"></div>
                    <script type="text/javascript">
                        !function (d, id, did, st) {
                            var js = d.createElement("script");
                            js.src = "https://connect.ok.ru/connect.js";
                            js.onload = js.onreadystatechange = function () {
                                if (!this.readyState || this.readyState == "loaded" || this.readyState == "complete") {
                                    if (!this.executed) {
                                        this.executed = true;
                                        setTimeout(function () {
                                            OK.CONNECT.insertGroupWidget(id,did,st);
                                        }, 100);
                                    }
                                }};
                            d.documentElement.appendChild(js);
                        }(document,"ok_group_widget","%2054093304758360",'{"width":250,"height":285}');
                    </script>
                </div>
            </div>
        </div>

        <div class="w3-third">
            <div class="w3-card w3-border-0 w3-display-container w3-margin" style="min-height:250px">
                <div class="w3-display-middle">

                </div>
            </div>
        </div>

<m:feedbackForm>
    <h2 class="w3-text-theme">Связаться с нами:</h2>
</m:feedbackForm>
    </div>

