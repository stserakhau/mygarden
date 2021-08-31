<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--jsp:include page="../ads/news4columns.jsp"/-->


<div class="w3-container w3-lime w3-left-align w3-margin-bottom w3-text-white">
    <h1>Библиотека удобрений</h1>
</div>
<!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2 w3-left-align">
    <p>Вы находитесь в библиотеке удобрений. Здесь содержатся как системные удобрения, так и те, которые Вы создали самостоятельно. Нажмите на удобрение для того, чтобы посмотреть его настройки. Нажмите на название класса для его редактирования. <a class="w3-text-blue" href="help_fertilizer_inventory.tiles">Подробнее...</a></p>
</div-->

<div class="w3-container w3-padding-0 w3-margin-bottom w3-text-grey">
    <a class="w3-btn w3-theme w3-hover-white w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile"
       accesskey="a"
       onclick="createFertilizer();"><span class="fa fa-plus"></span> Добавить удобрение</a>
    <a class="w3-btn w3-theme w3-hover-white w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile"
       accesskey="c"
       onclick="createFertilizerCatalog()"><span class="fa fa-plus"></span> Добавить класс</a>
    <a class="w3-text-blue w3-hover-text-red w3-left w3-margin-bottom w3-padding-8 w3-right w3-mobile" href="#"
    role="ROLE_USER" style="display:none"
    onclick="$('#confirmDefaultSettings').show();"><span class="fa fa-refresh">&emsp;</span>Восстановить системные настройки</a>
</div>

<div class="w3-row">
    <div class="w3-col w3-threequarter">

<c:choose>
    <c:when test="${empty fertilizersInventory}">Нет данных</c:when>
    <c:otherwise>
        <c:forEach var="catalog" items="${fertilizersInventory}">
            <div class="w3-col l4 m6 s12 w3-padding-tiny">
                <h2>
                    <c:choose>
                        <c:when test="${empty catalog.key.key}">
                            <a class="w3-text-theme w3-padding">${catalog.key.value}</a>
                        </c:when>
                        <c:otherwise>
                            <a class="w3-text-theme w3-hover-theme w3-padding w3-btn-block w3-white"
                               title="Нажмите для редактирования"
                               onclick="editFertilizerCatalog(${catalog.key.key});">${catalog.key.value}</a>
                        </c:otherwise>
                    </c:choose>
                </h2>
                <ul class="w3-ul w3-hoverable w3-left-align">
                    <c:forEach var="enr" items="${catalog.value}">
                        <li style="cursor: pointer;" title="Нажмите для редактирования"
                            onclick="viewSettings(${enr.id})">
                            ${enr.name}
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </c:forEach>
    </c:otherwise>
</c:choose>
    </div>
    <div class="w3-col w3-quarter w3-mobile"   style="min-width:300px">
        <div class="w3-border w3-border-grey w3-margin-top w3-margin-bottom" style="min-height: 200px">
            <p>Ads</p>
        </div>
        <div class="w3-border w3-border-grey w3-margin-top w3-margin-bottom" style="min-height: 200px">
            <p>Ads</p>
        </div>
        <div class="w3-border w3-border-grey w3-margin-top w3-margin-bottom" style="min-height: 200px">
            <p>Ads</p>
        </div>
    </div>
</div>

<!-- Confirm Default Settings Modal-->
<div id="confirmDefaultSettings" class="w3-modal w3-padding-48">
    <div class="w3-modal-content w3-card-4 w3-center">
        <header class="w3-container w3-theme w3-left-align">
            <span onclick="$('#confirmDefaultSettings').hide();" class="w3-closebtn">&times;</span>
            <h2><i class="fa fa-question-circle w3-xxlarge" style="font-size:48px"></i> Восстановить начальные настройки</h2>
        </header>
        <div class="w3-container w3-left-align">
            <p>Вы действительно хотите восстановить системные настройки для библиотеки удобрений? Система удалит все Ваши изменения</p>
        </div>
        <footer class="w3-container w3-theme">
            <div class="w3-right-align w3-padding-16">
                <button onclick="$('#confirmDefaultSettings').hide();" class="w3-btn w3-grey w3-hover-white">Отмена</button>
                <button onclick="resetFertilizerUserSettings();" class="w3-btn w3-theme-dark w3-hover-white">Восстановить</button>
            </div>
        </footer>
    </div>
</div>

<script type="text/javascript">
    document.afterInitializationUIEnvironmentFunc = initPage;

    function initPage() {
        EDIT_FERTILIZER_WINDOW.initialize({disableValidationIncompatibleFertilizer : true});
        EDIT_FERTILIZER_CATALOG_WINDOW.initialize();
    }

    function createFertilizerCatalog() {
        EDIT_FERTILIZER_CATALOG_WINDOW.edit(null);
    }
    function editFertilizerCatalog(catalogId) {
        EDIT_FERTILIZER_CATALOG_WINDOW.edit(catalogId);
    }
    function createFertilizer() {
        EDIT_FERTILIZER_WINDOW.edit(null);
    }
    function viewSettings(id) {
        window.location.href = 'fertilizer_settings.tiles?fertilizerId=' + id;
    }

    function resetFertilizerUserSettings() {
        doAction(function () {
            $.ajax({
                type: "post",
                url: "api/fertilizer/resetUserSettings",
                success: function (fertilizer) {
                    $('#confirmDefaultSettings').hide();
                    window.location.reload();
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    var msg = jqXHR.responseJSON.message;
                    alert(msg);
                }
            });
        });
    }

</script>

