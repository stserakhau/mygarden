<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--jsp:include page="../ads/news4columns.jsp"/-->


<div class="w3-container w3-lime w3-left-align w3-margin-bottom">
    <h2 class="w3-text-white">Сельскохозяйственный инвентарь</h2>
</div>
<!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2 w3-left-align">
    <p>Вы находитесь в библиотеке Вил и Тракторов. Здесь содержатся как причендалы так и машины для копания, закапывания, откапывания и разбрасывания. <a class="w3-text-blue" href="help_fertilizer_inventory.tiles">Подробнее...</a></p>
</div-->

<div class="w3-container w3-padding-0 w3-margin-bottom w3-text-grey">
    <a class="w3-btn w3-theme w3-hover-white w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile"
       accesskey="a"
       role="ROLE_USER" style="display:none"
       onclick="createInventory();"><span class="fa fa-plus"></span> Добавить инвентарь</a>
    <a class="w3-btn w3-theme w3-hover-white w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile"
       accesskey="c"
       role="ROLE_USER" style="display:none"
       onclick="createInventoryGroup()"><span class="fa fa-plus"></span> Добавить группу</a>
</div>