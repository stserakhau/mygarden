<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--jsp:include page="../ads/news4columns.jsp"/-->


<div class="w3-container w3-lime w3-left-align w3-margin-bottom w3-text-white">
    <h1>Мои растения</h1>
</div>
<!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2 w3-left-align">
    <p>Вы находитесь в своей библиотеке растений. Нажмите на название растения для того, чтобы посмотреть его шаблоны выращивания. Нажмите на название класса для его редактирования. <a class="w3-text-blue" href="help_species_inventory.tiles">Подробнее...</a></p>
</div-->

<div class="w3-container w3-padding-0 w3-margin-bottom  w3-text-grey">
    <a class="w3-btn w3-theme w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile"
       onclick="EDIT_SPECIES_WINDOW.edit(null);"><span class="fa fa-plus">&emsp;</span> Добавить культуру</a>
    <a class="w3-btn w3-theme w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile"
       onclick="EDIT_SPECIES_CLASS_WINDOW.edit(null);"><span class="fa fa-plus">&emsp;</span> Добавить класс</a>

</div>

<div class="w3-row">
    <div class="w3-col w3-threequarter">
<c:choose>
    <c:when test="${empty speciesInventory}">Нет данных</c:when>
    <c:otherwise>
        <c:forEach var="catalog" items="${speciesInventory}">
            <div class="w3-col l3 m4 s12">
                <h2>
                    <c:choose>
                        <c:when test="${empty catalog.key.key}">
                            <a class="w3-text-theme w3-padding">${catalog.key.value}</a>
                        </c:when>
                        <c:otherwise>
                            <a class="w3-text-theme w3-hover-theme w3-hover-shadow w3-padding"
                               title="Нажмите для редактирования"
                               onclick="EDIT_SPECIES_CLASS_WINDOW.edit(${catalog.key.key})">${catalog.key.value}</a>
                        </c:otherwise>
                    </c:choose>
                </h2>
                <ul class="w3-ul w3-hoverable w3-left-align">
                    <c:forEach var="species" items="${catalog.value}">
                        <li style="cursor: pointer;" title="Нажмите для редактирования"
                            onclick="window.location.href='select_user_work_template.tiles?speciesId=${species.id}';">
                                ${species.name}
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


<script type="text/javascript">
    document.afterInitializationUIEnvironmentFunc = initPage;

    function initPage() {
        doAction(function () {
            EDIT_SPECIES_WINDOW.initialize();
            EDIT_SPECIES_CLASS_WINDOW.initialize();
        });
    }
</script>

