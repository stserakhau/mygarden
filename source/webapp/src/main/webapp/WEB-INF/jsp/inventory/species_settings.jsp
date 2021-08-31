<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--jsp:include page="../ads/news4columns.jsp"/-->


<div id="pageHeader" class="w3-container w3-lime w3-left-align w3-margin-bottom w3-text-white">
    <h1><a href="select_user_work_template.tiles?speciesId="><%--Картофель--%></a>
        <span>Шаблоны</span></h1>
</div>
<!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2 w3-left-align">
    <p>Вы видите все шаблоны выращивания, созданные для культуры. Нажмите на название шаблона для просмотра работ в нем или копирования в другую культуру. Нажмите <b>Добавить в мой план</b> напротив нужного шаблона для того, чтобы запланировать работы по нему. <a class="w3-text-blue" href="help_species_inventory.tiles">Подробнее...</a></p>
</div-->

<div class="w3-row w3-container w3-margin-bottom  w3-text-grey">
    <div class="w3-half">
        <ul id="userWorksList" class="w3-ul w3-large w3-left-align">
        </ul>
    </div>

    <div class="w3-half w3-padding-tiny">
        <div class="w3-container w3-padding-0 w3-margin-bottom">
            <h2 class="w3-text-theme">Список работ<span><a class="w3-btn-floating-large w3-theme-dark w3-right"
                                                           onclick="$('#editSpeciesTasks').show();"
                                                           title="Значения по умолчанию для данной культуры"><b>+</b></a>&emsp;</span></h2>
        </div>
        <ul id="userSpeciesTaskList" class="w3-container w3-card-4 w3-ul w3-left-align"></ul>
    </div>
</div>

<div class="w3-row w3-container w3-margin-bottom ">
    <div class="w3-padding-tiny w3-card-4">
        <p>Gantt</p>
    </div>
</div>


<!-- Add Work Defaults for the Species Modal-->
<div id="editSpeciesTasks" class="w3-modal w3-padding-48">
    <form id="editSpeciesTasksForm" class="w3-modal-content w3-card-4 w3-center">
        <input type="hidden" name="speciesTask.id"/>
        <input type="hidden" name="speciesTask.speciesId" value="${species.id}"/>
        <header class="w3-container w3-theme w3-left-align">
            <span onclick="$('#editSpeciesTasks').hide();" class="w3-closebtn">&times;</span>
            <h2>Добавить значения по умолчанию</h2>
        </header>
        <div class="w3-container w3-left-align w3-padding">
            <!--div id="warnMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2">
                <p>Укажите, какие значения будут выставлены по умолчанию при добавлении данной работы в Ваш план работ
                    для культуры ${species.name}. Обратите внимание, Вы можете задать разные значения для одной и той
                    же работы в разных шаблонах.</p>
            </div-->

            <p><label class="w3-label">Выбрать шаблон...</label>
                <input list="userWorksData" id="userWorkName" name="userWorkName" required="required" class="w3-select" placeholder="Выберите шаблон или введите новый, если искомый отсутствует в списке">
                <datalist id="userWorksData">
                    <option value="Для раннего употребления"></option>
                    <option value="Для хранения"></option>
                    <option value="Для размножения"></option>
                </datalist>
            </p>
            <p><label class="w3-label">Выбрать работу...</label>
                <input list="tasks" id="taskName" name="taskName" required="required" class="w3-select" placeholder="Выберите работу или введите новую, если искомя отсутствует в списке">
                <datalist id="tasks">
                    <option value="..."></option>
                    <option value="Список ВСЕХ работ из базы, включая пользовательские"></option>
                    <option value="..."></option>
                </datalist>
            </p>
            <p><label class="w3-label">Коментарии</label>
                <textarea id="speciesTaskDescription"  rows="6" name="speciesTask.description" class="w3-border-theme"
                          rows="4"
                          placeholder="Ваши коментарии, заметки для выполнения работы в выбранном шаблоне. Например, подготовка почвы осенью и весной, для картофеля или огурцов, конечно, будет отличаться. Запишите все, что нужно не забыть!"></textarea>
            </p>

            <div class="w3-container w3-left-align">
                <ul id="selectConfigurationType" name="definition.planingType" value="planByDate" class="w3-navbar w3-light-gray">
                    <li class="w3-hover-text-white w3-hover-gray w3-light-gray w3-padding" value="planByDate">Указать точную дату</li>
                    <li class="w3-hover-text-white w3-hover-gray w3-light-gray w3-padding" value="planByWeather">Зависит от погоды</li>
                    <li class="w3-hover-text-white w3-hover-gray w3-light-gray w3-padding" value="planByAnotherTask">После другой работы</li>
                </ul>

                <div id="planByDate" class="w3-container w3-card-2 w3-margin-bottom" style="display:none">
                    <p>
                        <label>Введите дату:</label>
                        <input class="w3-input w3-border" type="date" name="definition.startDate" required="required"/>
                    </p>
                </div>

                <div id="planByWeather" class="w3-container w3-card-2 w3-margin-bottom " style="display:none">
                    <div class="w3-row">
                        <div class="w3-third w3-padding-right">
                            <p><label>Среднемесячная температура ℃:</label>
                                <input class="w3-input w3-border" type="number" name="definition.averageTemperature"
                                       required="required"></p>
                        </div>
                        <div class="w3-third w3-padding-right">
                            <p><label>Но не ранее: </label>
                                <input class="w3-input w3-border" type="date" name="definition.startDate"
                                       required="required"></p>
                        </div>
                        <div class="w3-third w3-padding-right">
                            <p><label>И не позднее:</label>
                                <input class="w3-input w3-border" type="date" name="definition.endDate"
                                       required="required"></p>
                        </div>
                    </div>
                </div>

                <div id="planByAnotherTask" class="w3-container w3-card-2 w3-margin-bottom " style="display:none">
                    <p><label><input class="w3-border" type="number" min="0" max="365"
                                     name="definition.countDaysAfterPrevEvent" required="required"
                                     style="width:100px; padding:8px"> дней после работы:</label></p>
                    <select class="w3-select" name="definition.previousTaskId" required="required">
                        <option value="">Выбрать работу</option>
                    </select>
                </div>
            </div>

        </div>
        <footer class="w3-container w3-theme">
            <div class="w3-right-align w3-padding-16">
                <button type="button" onclick="$('#editSpeciesTasks').hide();" class="w3-btn w3-grey w3-hover-white">Отмена</button>
                <button type="submit" class="w3-btn w3-theme-dark w3-hover-white">Сохранить</button>
            </div>
        </footer>
    </form>
</div>

<!-- Confirm Default Settings Modal-->
<div id="confirmDefaultSettings" class="w3-modal w3-padding-48">
    <div class="w3-modal-content w3-card-4 w3-center">
        <header class="w3-container w3-theme w3-left-align">
            <span onclick="$('#confirmDefaultSettings').show();"
                  class="w3-closebtn">&times;</span>

            <h2><i class="fa fa-question-circle w3-xxlarge" style="font-size:48px"></i> Восстановить начальные настройки
            </h2>
        </header>
        <div class="w3-container w3-left-align">
            <p>Вы действительно хотите восстановить начальные настройки для вида ${species.name}? Система
                удалит все добавленные шаблоны, и восстановит измененные. Отменить сброс будет невозможно. Нажмите
                кнопку <b>Восстановить</b>, если хотите продолжить.  <a class="w3-text-blue" href="#">Подробнее...</a></p>

        </div>
        <footer class="w3-container w3-theme">
            <div class="w3-right-align w3-padding-16">
                <button class="w3-btn w3-grey w3-hover-white">Отмена</button>
                <button class="w3-btn w3-theme-dark w3-hover-white">Восстановить</button>
            </div>
        </footer>

    </div>
</div>

<!-- Confirm deleting template for the Species Modal-->
<div id="confirmDeletion" class="w3-modal w3-padding-48">
    <div class="w3-modal-content w3-card-4 w3-center">
        <header class="w3-container w3-theme w3-left-align">
            <span onclick="$('#confirmDeletion').hide();" class="w3-closebtn">&times;</span>

            <h2><span class="fa fa-question-circle w3-text-white"></span></h2>
        </header>
        <div class="w3-container w3-left-align">
            <p>Вы действительно хотите удалить метод для культуры <b>${species.name}</b>?</p>

        </div>
        <footer class="w3-container w3-theme">
            <div class="w3-right-align w3-padding-16">
                <button class="w3-btn w3-grey w3-hover-white" onclick="$('#confirmDeletion').hide();">Отмена</button>
                <button class="w3-btn w3-theme-dark w3-hover-white" onclick="deleteSpeciesTask()">Удалить</button>
            </div>
        </footer>

    </div>
</div>

<script type="text/javascript">
    var SELECTED_USER_WORK_ID = "null";
    var USER_WORKS_DEFINITIONS = {};
    var USER_WORKS_LIST = $("#userWorksList");
    var TASKS_LIST_BODY = $("#tasksList tbody");
    var EDIT_SPECIES_TASKS_MODAL = $("#editSpeciesTasks");

    var form = $("#editSpeciesTasksForm");
    var planingTypeElement = form.find("select[name='defaults.planingType']");

    document.afterInitializationUIEnvironmentFunc = initPage;

    function initPage() {
        doAction(function () {
            loadGrowingOptions();
            initializeFormSpecies();
        });
    }

    function initializeFormSpecies() {
        loadTasks("taskName");
        $("#taskName").blur(function(){
            $.get(
                    "api/species/${species.id}/tasks?pageNum=0&pageSize=1&query=(taskName eq '"+$("#taskName").val()+"')",
                    function(result) {
                        var description = result.length==0 ? "" : result[0].description;
                        if(description.trim()=="") {
                            $("#speciesTaskDescription").removeAttr("readonly");
                        } else {
                            $("#speciesTaskDescription").attr("readonly", "readonly");
                        }

                        $("#speciesTaskDescription").val(description)
                    }
            );
        });

        var selectItems = form.find("selectItem");
        planingTypeElement.change(function () {
            var type = $(this).val();
            $.each(selectItems, function (index, value) {
                var el = $(value);
                if (el.attr("itemId") == type) {
                    el.find("*[name]")
                            .attr("required", "required")
                            .removeAttr("disabled");
                    el.show();
                } else {
                    el.hide();
                    $.each(el.find("*[name]"), function (index, val) {
                        $(val).removeAttr("required")
                                .attr("disabled", "disabled")
                                .val("");
                    });
                }
            })
        });


        form.submit(function(e) {
            var url = '/api/species/${species.id}/tasks/';

            var json = serializeFormToJsonTree(form);
            var userWorksDefaults = json.defaults;
            delete json.defaults;

            var userWorkName = json.userWorkName;
            delete json.userWorkName;

            $.ajax({
                type: "POST",
                url: url,
                data: JSON.stringify(json),
                contentType: "application/json; charset=utf-8",
                success: function (speciesTaskId) {
                    var speciesGOs = USER_WORKS_DEFINITIONS["optionId" + SELECTED_USER_WORK_ID];
                    var updated = false;
                    for (var i = 0; i < speciesGOs.length; i++) {
                        var sgo = speciesGOs[i];
                        if (sgo.speciesTaskId == speciesTaskId) {
                            updated = true;
                            sgo.defaults = userWorksDefaults;
                            break;
                        }
                    }
                    if (!updated) {
                        speciesGOs.push({
                            speciesTaskId: speciesTaskId,
                            defaults: userWorksDefaults
                        });
                    }

                    if (SELECTED_USER_WORK_ID != 'null') {

                        $.ajax({
                            type: "PUT",
                            url: "api/species/${species.id}/userWorks/" + SELECTED_USER_WORK_ID,
                            data: JSON.stringify(speciesGOs),
                            contentType: "application/json; charset=utf-8",
                            success: function (data) {
                                $('#editSpeciesTasks').hide();
                                refreshSpeciesWork();
                            }
                        });
                    } else {
                        $('#editSpeciesTasks').hide();
                        refreshSpeciesWork();
                    }
                }
            });

            return false;
        });
    }

    function refreshSpeciesWork() {
        var userWorkTasksIds = getDefaultsSpeciesTasksIds();
        var url = "api/species/${species.id}/tasks?pageNum=0&pageSize=100";
        if(userWorkTasksIds && userWorkTasksIds.length > 0) {
            url+="&query=(id in "+JSON.stringify(userWorkTasksIds)+")"
        }
        $.get(
                url,
                function (items) {
                    TASKS_LIST_BODY.html("");
                    var previousWorks = $("#previousWorks");
                    previousWorks.html("<option>Выбрать работу</option>");
                    for (var i = 0; i < items.length; i++) {
                        var item = items[i];
                        $("<tr><td>" + item.taskName + "</td>\
                        <td>" + item.description + "</td>\
                        <td><i class='fa fa-edit w3-xxlarge' onclick='openEditSpeciesWorkForm("+item.id+")'></i>&emsp;<i class='fa fa-trash w3-xxlarge' onclick='deleteConfirmationSpeciesTask("+item.id+")'></i></td>\
                        </tr>").appendTo(TASKS_LIST_BODY);
                        $("<option value='"+item.id+"'>"+item.taskName+"</option>").appendTo(previousWorks);
                    }
                }
        )
    }

    function findSelectedDefaults(speciesTaskId) {
        var goDefs = USER_WORKS_DEFINITIONS["optionId"+SELECTED_USER_WORK_ID];
        if(goDefs) {
            for(var i=0;i<goDefs.length;i++) {
                var def = goDefs[i];
                if(def.speciesTaskId==speciesTaskId) {
                    return def.defaults;
                }
            }
        }
    }

    function openEditSpeciesWorkForm(speciesTaskId) {

        if(speciesTaskId == null) {
            $("#userWorkName").removeAttr("readonly");

            populateFormFromJsonTree(
                    form,
                    {
                        speciesTask : {
                            id : null,
                            speciesId : ${species.id},
                            taskId : null,
                            description : ""
                        },
                        taskName: "",
                        defaults: {
                            planingType: ""
                        }
                    }
            );
            planingTypeElement.change();
            EDIT_SPECIES_TASKS_MODAL.show();
            return;
        }

        $.get(
                "api/species/${species.id}/tasks/"+speciesTaskId,
                function(data) {
                    var defaults = {
                        planingType: ""
                    };
                    if(SELECTED_USER_WORK_ID!="null") {
                        $("#userWorkName").attr("readonly", "readonly");

                        defaults = findSelectedDefaults(speciesTaskId);
                    }

                    data.defaults = defaults;

                    populateFormFromJsonTree(form, data);

                    planingTypeElement.change();
                    $("#taskName").change();
                    EDIT_SPECIES_TASKS_MODAL.show();
                }
        );
    }



    function deleteConfirmationSpeciesTask(speciesTaskId, title) {
        $('#confirmDeletion')
                .attr("speciesTaskId", speciesTaskId)
                .show();
    }
    function deleteSpeciesTask() {
        var speciesTaskId = $('#confirmDeletion').removeAttr("speciesTaskId");

        $.ajax({
            type: "DELETE",
            url: "api/species/${species.id}/tasks/" + speciesTaskId,
            success: function (resp) {
                $('#confirmDeletion').hide();
                refreshSpeciesWork();
            }
        });
    }

    function loadGrowingOptions() {
        var input = $("#userWorkName");
        var data = $("#"+input.attr("list"));
        var userWorks = $("#userWorksList");
        $("<li optionId='null'>Все работы</li>").appendTo(userWorks);
        if(data) {
            data.find("*").remove();
            $.get(
                    'api/species/${species.id}/userWorks',
                    function (items) {
                        $.each(items, function (i, item) {
                            if (item.id) {
                                USER_WORKS_DEFINITIONS["optionId" + item.id] = item.definition ? JSON.parse(item.definition) : [];
                            }
                            $('<option>', {
                                value: item.name,
                                text: item.name
                            }).appendTo(data);

                            $("<li optionId="+item.id+">"+item.name+"</li>").appendTo(userWorks);
                        });

                        userWorks.find("li").click(function() {
                            var optionId = $(this).attr("optionId");
                            if(optionId=="null"){
                                selectGrowingOption($(this).html(), "null");
                            } else {
                                var goId = parseInt(optionId, 10);
                                selectGrowingOption($(this).text(), goId);
                            }
                        });
                    }
            )
        }
    }

    function selectGrowingOption(userWorkTitle, userWorkId) {
        SELECTED_USER_WORK_ID = userWorkId;
        var userWorkName = $("#userWorkName");
        userWorkName.val(userWorkId!="null" ? userWorkTitle : "");
        userWorkName.change();

        refreshSpeciesWork();

        $.each(
                USER_WORKS_LIST.find("li"),
                function (i, item) {
                    var el = $(item);
                    var optionId = el.attr("optionId");
                    var itemId = optionId!="null" ? parseInt(optionId, 10) : "null";
                    if (itemId == SELECTED_USER_WORK_ID) {
                        el.addClass("w3-theme");
                    } else {
                        el.removeClass("w3-theme");
                    }
                }
        );
    }

    function getDefaultsSpeciesTasksIds() {
        var speciesTasksIds = [];

        if(SELECTED_USER_WORK_ID && SELECTED_USER_WORK_ID!="null") {
            var defaults = USER_WORKS_DEFINITIONS["optionId" + SELECTED_USER_WORK_ID];
            for (var i = 0; i < defaults.length; i++) {
                var def = defaults[i];
                speciesTasksIds.push(def.speciesTaskId)
            }
        }
        return speciesTasksIds;
    }

    function loadTasks(listId) {
        var input = $("#"+listId);
        var data = $("#"+input.attr("list"));
        if(data) {
            data.find("*").remove();
            $.get(
                    'api/tasks/scroll?pageNum=0&pageSize=1000&order.name=asc',
                    function (items) {
                        $.each(items, function (i, item) {
                            $('<option>', {
                                value: item.name,
                                text: item.name
                            }).appendTo(data);
                        });
                    }
            )
        }
    }

</script>