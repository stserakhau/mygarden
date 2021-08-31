<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="w3-container w3-lime w3-left-align w3-margin-bottom w3-text-white">
    <h1>Библиотека растений</h1>
</div>
<!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2 w3-left-align">
    <p>Вы находитесь в системной библиотеке растений. Нажмите на название растения для того, чтобы посмотреть его шаблоны выращивания или скопировать в свою библиотеку. <a class="w3-text-blue" href="help_species_inventory.tiles">Подробнее...</a></p>
</div-->
<div class="w3-container w3-left-align w3-margin-bottom w3-border-grey">
    <form class="w3-margin-bottom w3-left-align w3-padding-tiny" method="get">
        <label class="w3-label">Введите дату с:</label>
        <select class="w3-input w3-border" style="max-width:200px" name="month" onchange="submit()">
            <c:forEach var="month" items="${months}" varStatus="st">
                <option value="${st.index > 0 ? st.index : ''}" ${param.month eq st.index ? 'selected' : ''}>${month.value}</option>
            </c:forEach>
        </select>
        <label class="w3-label">Выберите работу:</label>
        <select class="w3-select" name="taskId" onchange="submit()">
            <option value="">Без работы</option>
            <c:if test="${not empty availableTasks}">
                <c:forEach var="item" items="${availableTasks}">
                    <option value="${item.id}" ${param.taskId eq item.id ? 'selected' : ''}>${item.name}</option>
                </c:forEach>
            </c:if>
        </select>
    </form>
</div>

<div class="w3-row">
<div class="w3-col w3-threequarter">
<c:choose>
    <c:when test="${empty speciesInventory}">Нет данных</c:when>
    <c:otherwise>
        <c:forEach var="catalog" items="${speciesInventory}">
            <div class="w3-col l4 m6 s12 w3-padding-tiny">
                <h2 class="w3-text-theme w3-padding">${catalog.key.value}</h2>
                <ul class="w3-ul w3-hoverable w3-left-align">
                    <c:forEach var="species" items="${catalog.value}">
                        <li style="cursor: pointer;" onclick="openSpecies(${species.id})"
                            title="Нажмите для просмотра">
                            <span>${species.name}</span>
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
    function openSpecies(systemSpeciesId) {
        window.location.href = "select_system_work_template.tiles?speciesId=" + systemSpeciesId;
    }

    document.afterInitializationUIEnvironmentFunc = initPage;
    function initPage() {
        doAction(function () {
        });
    }
</script>