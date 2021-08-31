<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--jsp:include page="../ads/news4columns.jsp"/-->


<div class="w3-container w3-lime w3-left-align w3-margin-bottom w3-text-white">
    <h1>${fertilizer.name}</h1>
</div>
<!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2 w3-left-align">
    <p>Забывали ли Вы когда-нибудь, для чего использовать дорогой препарат, который купили по чьему-то совету? Тогда эта страница создана для Вас. <a class="w3-text-blue" href="help_view_fertilizer_settings.tiles">Подробнее...</a></p>
</div-->

<div class="w3-container w3-padding-0 w3-margin-bottom  w3-text-grey">
    <a href="fertilizers_inventory.tiles"
       accesskey="b"
       class="w3-btn w3-grey w3-hover-white w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile">
        <span class="fa fa-chevron-left">&emsp;</span>Назад</a>
    <c:if test="${fertilizer.ownerId!=null}">
        <a class="w3-btn w3-theme w3-hover-white w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile"
           accesskey="e"
           onclick="EDIT_FERTILIZER_WINDOW.edit(${fertilizer.id});"><span class="fa fa-edit">&emsp;</span> Редактировать удобрение</a>

        <a class="w3-btn w3-theme w3-hover-red w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile"
           accesskey="d"
           onclick="showConfirmDeleteFertilizer()"><span class="fa fa-trash">&emsp;</span> Удалить удобрение</a>
    </c:if>
    <c:if test="${fertilizer.ownerId == null }">
        <a class="w3-btn w3-theme w3-hover-white w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile"
           accesskey="e"
           onclick="EDIT_FERTILIZER_WINDOW.edit(${fertilizer.id});"><span class="fa fa-edit">&emsp;</span> Несовместимые удобрения</a>
        <a class="w3-text-blue w3-hover-text-red w3-left w3-margin-bottom w3-padding-8 w3-right w3-mobile" href="#"
           role="ROLE_USER" style="display:none"
           onclick="$('#confirmDefaultSettings').show();"><span class="fa fa-refresh">&emsp;</span>Восстановить
            системные настройки</a>
    </c:if>
</div>
<div class="w3-row w3-container w3-margin-bottom">
    <div class="w3-threequarter">
        <div class="w3-third">
            <div class="w3-card w3-border-0 w3-padding-tiny">
                <h2 class="w3-text-theme">Выберите культуру</h2>
                <ul id="fertilizerSpecies" class="w3-ul w3-right-align" style="overflow: auto; max-height: 550px;">
                </ul>
            </div>
        </div>
        <div class="w3-twothird">
            <div class="w3-card w3-border-0 w3-padding-tiny">
                <h2 class="w3-text-theme">Удобрение используется при...<span>
                    <a class="w3-btn-floating-large w3-theme-dark w3-right" onclick="openEditFertilizerForm(null);"
                       accesskey="a"
                       title="Всегда использовать удобрение при выполнении работы для культуры"><i class="fa fa-plus w3-large"></i></a></span>
                </h2>
                <ul id="workList" class="w3-ul w3-card-4 w3-left-align"></ul>
            </div>
        </div>
    </div>
    <div class="w3-quarter">
        <div class="w3-card w3-border-0" style="min-height:600px">
            <p></p>
        </div>
    </div>
</div>

<!-- Confirm deleting Fertilizer Modal-->
<form id="confirmDeleteFertilizer" action="/api/fertilizer/{fertilizerId}/delete" method="post" class="w3-modal w3-padding-48">
    <div class="w3-modal-content w3-card-4 w3-center">
        <header class="w3-container w3-theme w3-left-align">
            <span onclick="$('#confirmDeleteFertilizer').hide()" class="w3-closebtn">&times;</span>
            <h2><span class="fa fa-question-circle w3-text-white"></span></h2>
        </header>
        <div class="w3-container w3-left-align">
            <p>
                Вы действительно хотите удалить удобрение <b>${fertilizer.name}</b>?<br/>
                <br/>
                <input name="deleteClass" type="checkbox" id="deleteClass"/> <label for="deleteClass">Удалить класс при удалении последнего удобрения в нем</label>
            </p>
        </div>
        <footer class="w3-container w3-theme">
            <div class="w3-right-align w3-padding-16">
                <button type="button" class="w3-btn w3-grey w3-hover-white" onclick="$('#confirmDeleteFertilizer').hide();">Отмена</button>
                <button type="submit" class="w3-btn w3-theme-dark w3-hover-white">Удалить</button>
            </div>
        </footer>
    </div>
</form>

<!-- Add Work for the Fertilizer Modal-->
<div id="addFertilizerSpeciesWork" class="w3-modal w3-padding-48">
    <form class="w3-modal-content w3-card-4 w3-center">
        <header class="w3-container w3-theme w3-left-align">
            <span onclick="$('#addFertilizerSpeciesWork').hide();" class="w3-closebtn">&times;</span>
            <h2><b>${fertilizer.name}</b> используется для...</h2>
        </header>
        <div class="w3-container w3-left-align">
            <!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2">
                <p>Укажите, для каких культур и при каких работах вы хотите использовать удобрение
                    <b>${fertilizer.name}</b>. После, при добавлении работы в план удобрение будет выбираться автоматически</p>
            </div-->
            <div name="errorMsg" class="w3-panel w3-leftbar w3-border-red w3-pale-red w3-card-2">
                <p id="errorEmpty">Пожалуйста, заполните все необходимые поля</p>
                <p id="errorWorkNotUnique">Такая комбинация уже добавлена для удобрения</p>
            </div>
            <input type="hidden" name="id"/>
            <input type="hidden" name="fertilizerId" value="${fertilizer.id}"/>
            <p>
                <label class="w3-label">Культура<span class="w3-text-red">*</span></label>
                <select name="speciesId" required="required" class="w3-select w3-validate w3-margin-bottom"></select>
            </p>
            <p>
                <label class="w3-label">Работа<span class="w3-text-red">*</span></label>
                <select name="taskId" required="required" class="w3-select w3-margin-bottom"></select>
            </p>
        </div>
        <footer class="w3-container w3-theme">
            <div class="w3-right-align w3-padding-16">
                <button type="button" onclick="$('#addFertilizerSpeciesWork').hide();" class="w3-btn w3-grey w3-hover-white">Отмена</button>
                <button type="submit" class="w3-btn w3-theme-dark w3-hover-white">Сохранить</button>
            </div>
        </footer>
    </form>
</div>

<!-- Confirm deleting Work for the Fertilizer Modal-->
<div id="confirmDeletion" class="w3-modal w3-padding-48">
    <div class="w3-modal-content w3-card-4 w3-center">
        <header class="w3-container w3-theme w3-left-align">
            <span onclick="$('#confirmDeletion').hide();" class="w3-closebtn">&times;</span>
            <h2><span class="fa fa-question-circle w3-text-white"></span></h2>
        </header>
        <div class="w3-container w3-left-align">
            <p>Вы действительно хотите удалить настройку?</p>

        </div>
        <footer class="w3-container w3-theme">
            <div class="w3-right-align w3-padding-16">
                <button onclick="$('#confirmDeletion').hide();" class="w3-btn w3-grey w3-hover-white">Отмена</button>
                <button onclick="deleteFertilizerSpeciesTask()" class="w3-btn w3-theme-dark w3-hover-white">Удалить
                </button>
            </div>
        </footer>
    </div>
</div>

<!-- Confirm Default Settings Modal-->
<div id="confirmDefaultSettings" class="w3-modal w3-padding-48">
    <div class="w3-modal-content w3-card-4 w3-center">
        <header class="w3-container w3-theme w3-left-align">
            <span onclick="$('#confirmDefaultSettings').hide();" class="w3-closebtn">&times;</span>

            <h2><span class="fa fa-question-circle w3-xxlarge" style="font-size:48px"></span></h2>
        </header>
        <div class="w3-container w3-left-align">
            <p>Вы действительно хотите восстановить начальные настройки для удобрения "<b>${fertilizer.name}</b>"? Все Ваши изменения будут удалены</p>

        </div>
        <footer class="w3-container w3-theme">
            <div class="w3-right-align w3-padding-16">
                <button onclick="$('#confirmDefaultSettings').hide();" class="w3-btn w3-grey w3-hover-white">Отмена
                </button>
                <button onclick="resetFertilizerUserSettings();" class="w3-btn w3-theme-dark  w3-hover-white">
                    Восстановить
                </button>
            </div>
        </footer>
    </div>
</div>

<script type="text/javascript">
    var FERTILIZER_SPECIES_LIST = $("#fertilizerSpecies");
    var WORK_LIST_BODY = $("#workList");

    var form = $("#addFertilizerSpeciesWork")
    var fertilizerSpeciesTask = form.find("input[name='id']")
    var speciesList = form.find("select[name='speciesId']")
    var worksList = form.find("select[name='taskId']")

    document.afterInitializationUIEnvironmentFunc = initPage;

    function initPage() {
        EDIT_FERTILIZER_WINDOW.initialize({
            disableValidationIncompatibleFertilizer : true,
            currentFertilizerId:${fertilizer.id}
        });
        refreshFertilizerSpecies(function (data) {
            if (data.length > 0) {
                selectSpecies(data[0].id);
            }
        });
        initializeFormSpecies();
    }

    function initializeFormSpecies() {
        $.get(
                "api/species/?pageNum=0&pageSize=1000&order.name=asc",
                function (items) {
                    speciesList.html("<option value=''>Выберите культуру</option>");

                    var my = $("<optgroup label='Моя библиотека'/>");
                    var system = $("<optgroup label='Общая библиотека'/>");
                    var myExists = false;
                    var sysExists = false;
                    for (var i = 0; i < items.length; i++) {
                        var item = items[i];
                        var option = $("<option value='" + item.id + "'>" + item.name + "</option>");
                        if (item.ownerId == null) {
                            sysExists = true;
                            option.appendTo(system);
                        } else {
                            myExists = true;
                            option.appendTo(my);
                        }
                    }
                    if (myExists) {
                        my.appendTo(speciesList);
                    }
                    if (sysExists) {
                        system.appendTo(speciesList);
                    }
                }
        );

        $.get(
                "api/tasks/available/scroll?pageNum=0&pageSize=1000&order.name=asc",
                function (items) {
                    worksList.html("<option value=''>Выберите работу</option>");
                    var my = $("<optgroup label='Моя библиотека'/>");
                    var system = $("<optgroup label='Общая библиотека'/>");
                    var myExists = false;
                    var sysExists = false;
                    for (var i = 0; i < items.length; i++) {
                        var item = items[i];
                        var option = $("<option value='" + item.id + "'>" + item.name + "</option>");
                        if (item.ownerId == null) {
                            sysExists = true;
                            option.appendTo(system);
                        } else {
                            myExists = true;
                            option.appendTo(my);
                        }
                    }
                    if (myExists) {
                        my.appendTo(worksList);
                    }
                    if (sysExists) {
                        system.appendTo(worksList);
                    }
                }
        );


        form.submit(function(e) {
            var url = 'api/fertilizer/${fertilizer.id}/species/'+speciesList.val();
            var json = serializeFormToJsonTree(form);

            if (worksList.val() == '') {
                processError({responseJSON: {message: "error.fertilizerWork.required"}}, null, null);
                return false;
            }
            doActionIfAnonCreateAndLogin(function () {
                $.ajax({
                    type: "POST",
                    url: url,
                    data: JSON.stringify(json),
                    contentType: "application/json; charset=utf-8",
                    success: function (data) {
                        refreshFertilizerSpecies(function () {
                            selectSpecies(speciesList.val());
                            $('#addFertilizerSpeciesWork').hide();
                        });
                    },
                    error: processError
                });
            });
            return false;
        });
    }

    function processError(jqXHR, textStatus, errorThrown) {
        var msg = '';
        if (jqXHR.status == 404) {
            msg = 'error.not.authorized';
        } else {
            msg = jqXHR.responseJSON.message;
        }


        switch (msg) {
            case "error.fertilizerWork.exists":
                msg = "Такая комбинация уже добавлена для удобрения";
                break;
            case "error.fertilizerWork.required":
                msg = "Пожалуйста, заполните все необходимые поля.";
                break;
            case "error.fertilizer.species.duplicateSettings":
                msg = "Такая комбинация уже добавлена для удобрения";
                break;
            case "error.not.authorized":
                msg = "Пожалуйста, <a href='javascript:void(0)' onclick='doAction(\"login\")'>войдите</a> в свой профиль или <a href='user_registration.tiles'>зарегистрируйтесь</a> для того, чтобы добавить новый класс удобрений.";
                break;
        }
        form.find("div[name='infoMsg']").hide();
        form.find("div[name='errorMsg']")
                .html("<p>" + msg + "</p>").slideDown("fast");
    }

    function showConfirmDeleteFertilizer() {
        $("#confirmDeleteFertilizer")
                .attr('action', '/api/fertilizer/${fertilizer.id}/delete')
                .show();
    }

    function refreshFertilizerSpecies(callback) {
        FERTILIZER_SPECIES_LIST.find("*").remove();
        $.get(
                "api/fertilizer/${fertilizer.id}/species",
                function (items) {
                    for (var i = 0; i < items.length; i++) {
                        var item = items[i];
                        var speciesName = item.name + (item.ownerId!=null ? ' <b>(моё)</b>' : '');
                        $("<li itemId='" + item.id + "' class='w3-padding w3-hover-light-grey' onclick='selectSpecies(" + item.id + ");'>" + speciesName + "</li>").appendTo(FERTILIZER_SPECIES_LIST);
                    }

                    if(callback) {
                        callback(items);
                    }
                }
        );
    }
    function openEditFertilizerForm(fertilizerSpeciesTaskId) {
        if(fertilizerSpeciesTaskId == null) {
            speciesList.attr("disabled", false);
            worksList.attr("disabled", false);
            fertilizerSpeciesTask.val('');
            worksList.val('');
            speciesList.val('');
            populateFormFromJsonTree(form, {
                id:null,
                fertilizerId : ${fertilizer.id},
                speciesId : SELECTED_SPECIES_ID,
                taskId : null
            });
        } else {
            var json = SPECIES_TASK_FERTILIZER_CACHE[fertilizerSpeciesTaskId];
            speciesList.attr("disabled", true);
            worksList.attr("disabled", true);
            populateFormFromJsonTree(form, json);
        }
        form.find("div[name='infoMsg']").show();
        form.find("div[name='errorMsg']").hide();
        form.show();
    }

    var SELECTED_SPECIES_ID;
    function selectSpecies(speciesId) {
        SELECTED_SPECIES_ID = speciesId;
        speciesList.val(speciesId);
        speciesList.change();
        $.each(
                FERTILIZER_SPECIES_LIST.find("li"),
                function (i, item) {
                    var el = $(item);
                    var itemId = parseInt(el.attr("itemId"), 10);
                    if (itemId == SELECTED_SPECIES_ID) {
                        el.addClass("w3-theme");
                    } else {
                        el.removeClass("w3-theme");
                    }
                }
        );

        refreshFertilizerSpeciesWork();
    }

    var SPECIES_TASK_FERTILIZER_CACHE = {};

    function refreshFertilizerSpeciesWork() {
        WORK_LIST_BODY.find("*").remove();
        $.get(
                "api/fertilizer/${fertilizer.id}/species/" + SELECTED_SPECIES_ID,
                function (items) {
                    SPECIES_TASK_FERTILIZER_CACHE = {};
                    if(items.length == 0){
//                        refreshFertilizerSpecies(function (data) {
//                            if (data.length > 0) {
//                                selectSpecies(data[0].id);
//                            }
//                        });
                        return;
                    }
                    for (var i = 0; i < items.length; i++) {
                        var item = items[i];

                        var taskName = item.taskName + (item.taskOwnerId!=null ? ' <b>(моё)</b>' : '');

                        SPECIES_TASK_FERTILIZER_CACHE[item.id] = item;
                        if (item.ownerId == null) {
                            $("<li>" + taskName + "</li>").appendTo(WORK_LIST_BODY);
                        } else {
                            $("<li>" + taskName + "<span class='fa fa-trash w3-xlarge w3-right w3-hover-text-red' title='Удалить настройки' \
                               onclick='showDeleteFertilizerSpeciesTask(" + item.id + ",\"" + item.taskName + "\",\"" + item.speciesName + "\")'>&emsp;</span></li>")
                                    .appendTo(WORK_LIST_BODY);
                        }
                    }
                }
        );
    }
    function showDeleteFertilizerSpeciesTask(speciesTaskId, workName, speciesName) {
        $('#confirmDeletion').find("workName").html(workName);
        $('#confirmDeletion').find("speciesName").html(speciesName);
        $('#confirmDeletion')
                .attr("speciesTaskId", speciesTaskId)
                .show();
    }
    function deleteFertilizerSpeciesTask() {
        var speciesTaskId = $('#confirmDeletion')
                .attr("speciesTaskId")
        $.ajax({
            type: "DELETE",
            url: "api/fertilizer/${fertilizer.id}/species_task/" + speciesTaskId,
            success: function (resp) {
                refreshFertilizerSpeciesWork();
                $('#confirmDeletion').hide();
            }
        });
    }
    function resetFertilizerUserSettings() {
        doAction(function () {
            $.ajax({
                type: "post",
                url: "api/fertilizer/${fertilizer.id}/resetUserSettings",
                success: function (fertilizer) {
                    refreshFertilizerSpecies(function (data) {
                        if (data.length > 0) {
                            selectSpecies(data[0].id);
                        } else {
                            WORK_LIST_BODY.find("*").remove();
                        }
                        $('#confirmDefaultSettings').hide();
                    });
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    var msg = jqXHR.responseJSON.message;
                    alert(msg);
                }
            });
        });
    }
</script>