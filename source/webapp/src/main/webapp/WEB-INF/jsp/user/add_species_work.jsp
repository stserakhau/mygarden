<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="pageHeader" class="w3-container w3-lime w3-left-align w3-margin-bottom w3-text-white">
    <h1><span>Хочу вырастить </span>
        <c:choose><c:when test="${not empty pattern}">${pattern.patternName}</c:when><c:when test="${not empty species}">${species.name}</c:when></c:choose>
    </h1>
</div>
<!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2 w3-left-align">
    <p>Слева Вы видите список работ для культуры, добавленных Вами в шаблон выращивания. Вы можете добавлять их в свой план работ все вместе, выбрать какие-то отдельные работы из списка, добавить одну и ту же работу нексолько раз. Попробуйте задать другую дату для посева семян в грунт - все последующие работы сдвинутся соответственно.<a class="w3-text-blue" href="help_add_work_to_plan.tiles">Подробнее...</a></p>
</div-->

<div class="w3-row w3-container w3-margin-bottom w3-text-grey">
    <div class="w3-third">
        <div class="w3-padding-tiny">
            <c:choose>
                <c:when test="${not empty param.back}">
                    <a href="${param.back}"
                       class="w3-left w3-margin w3-padding-8 w3-text-grey w3-hover-text-lime" title="Назад">
                        <span class="fa fa-chevron-left w3-xxlarge"></span></a>
                </c:when>
                <c:otherwise>
                    <a href="select_system_work_template.tiles?speciesId=${species.id}"
                       class="w3-left w3-margin w3-padding-8 w3-text-grey w3-hover-text-lime" title="Назад">
                        <span class="fa fa-chevron-left w3-xxlarge"></span></a>
                </c:otherwise>
            </c:choose>
            <a id="addAllWorkBtn" class="w3-right w3-margin w3-padding-8  w3-text-theme w3-hover-text-lime"
               onclick="addAllWorks()" title="Добавить все работы"><span class="fa fa-arrow-circle-right w3-xxlarge"></span></a>
            <c:if test="${pattern != null}">
                <a class="w3-right w3-margin w3-padding-8 w3-text-theme w3-hover-text-lime"
                   onclick="FAST_PLAN_WINDOW.open()" title="Запланировать в 1 клик"><span class="fa fa-flag-checkered w3-xxlarge"></span></a>
                <a class="w3-right w3-margin w3-padding-8 w3-text-theme w3-hover-text-lime"
                   onclick="showWorkModal()" title="Найти работу"><span class="fa fa-search w3-xxlarge"></span></a>
            </c:if>

        </div>

        <ul id="userWorkTaskList" class="w3-container w3-ul w3-left-align"></ul>

        <jsp:include page="work_list.jsp">
            <jsp:param name="addMethodName" value="showWorkToMyPlanModalInt"/>
            <jsp:param name="style" value="display:none;"/>
        </jsp:include>
    </div>

    <div class="w3-twothird w3-padding-tiny">
        <div class="w3-container w3-padding-0 w3-margin-bottom">
            <h2 class="w3-text-theme">Выбранные работы</h2>
            <a id="saveAsTemplate" class="w3-right w3-theme w3-hover-white w3-padding w3-margin-bottom w3-mobile"
               style="display: none;"
               onclick="COPY_USER_WORK_TEMPLATE_WINDOW.editAsTemplate(USER_WORK_ID)"><span
                    class="fa fa-save w3-large">&emsp;</span>Сохранить как мой метод</a>
        </div>

        <ul id="userSpeciesTaskList" class="w3-container w3-card-4 w3-ul w3-left-align"></ul>
    </div>

    <div style="clear:both;padding-top:30px; overflow: auto" id="GanttChartDIV"></div>
</div>


<!-- show Details on the work Modal-->
<!--div id="showDetailsModal" class="w3-modal w3-padding-48">
    <div class="w3-modal-content w3-card-4 w3-center">
        <header class="w3-container w3-theme w3-left-align">
            <span onclick="$('#showDetailsModal').hide();" class="w3-closebtn">&times;</span>

            <h2>"название_работы"</h2>
        </header>
        <div class="w3-container w3-left-align">
            <p>Коментарии - текст, без заголовка</p>
            <div class="w3-grey"><h4>Расчетная дата</h4></div>
            <p>DD/MM/YYYY, возможен пересчет в зависимости от погодных условий</p>
            <p>DD/MM/YYYY, возможен пересчет в зависимости от погодных условий</p>
            <div class="w3-grey"><h4>Удобрения</h4></div>
            <ul class="w3-ul">
                <li>Удобрение 1</li>
                <li>Удобрение 2</li>
            </ul>
        </div>
        <footer class="w3-container w3-theme">
            <div class="w3-right-align w3-padding-16">
                <button class="w3-btn w3-grey w3-hover-white" onclick="$('#showDetailsModal').hide();">Отмена</button>
                <button class="w3-btn w3-theme-dark w3-hover-white" onclick="alert('Delete')">Удалить</button>
            </div>
        </footer>

    </div>
</div-->

<!-- View work details-->
<!--div id="viewWorkDetails" class="w3-modal w3-padding-48">
    <div class="w3-modal-content w3-card-4">
        <header class="w3-container w3-theme w3-left-align">
            <span onclick="$('#viewWorkDetails').hide();" class="w3-closebtn">&times;</span>
            <h2>Детали работы название_работы</h2>
        </header>
        <div class="w3-container w3-left-align">
            <p>Описание Описание Описание Описание Описание Описание Описание Описание</p>
            <p><label><b>Критерий:</b> Точная дата</label>
            <p><label><b>Критерий:</b> Среднемесячная температура 12℃, но не ранее DD/MM/YYYY и не позднее DD/MM/YYYY</label>
            <p><label><b>Критерий:</b> N дней после работы "название_работы"</label>
            <p><label><b>Расчетная дата выполнения:</b> DD/MM/YYYY</label>
            <p><label>Среднемесячная температура ℃:</label>
            <p><label><b>Удобрения:</b></label>
            <ul class="w3-ul">
            <li>Удобрение 1</li>
            <li>...</li>
            <li>Удобрение N</li>

        </ul>
            </p>
        </div>
        <footer class="w3-container w3-theme">
            <div class="w3-right-align w3-padding-16">
                <button onclick="$('#viewWorkDetails').hide();" class="w3-btn w3-theme-dark w3-hover-white">Закрыть</button>
            </div>
        </footer>
    </div>
</div-->


<script type="text/javascript">
    var USER_PATTERN_ID = null;
    var USER_WORK_ID = null;

    var PATTERN_TASKS = [];
    var PATTERN_TASKS_NAME_MAP = {};
    var USER_WORK_TASK_LIST = $("#userWorkTaskList");

    var USER_SPECIES_TASK_LIST = $("#userSpeciesTaskList");

    var GANNT_CHART = null;

    document.afterInitializationUIEnvironmentFunc = initializePage;

    function initializePage() {
        doAction(function () {
            <c:if test="${pattern != null}">
            FAST_PLAN_WINDOW.initialize({
                title : '${pattern.patternName}',
                patternId : ${pattern.id},
                callback: gotoMyWorkPlan
            });
            </c:if>

            USER_WORK_ID = getUrlVars()['userWorkId'];
            USER_PATTERN_ID = getUrlVars()['userPatternId'];
            EDIT_USER_TASK_WINDOW.initialize({
                speciesName: '${species.name}',
                callback : refreshUserWorkTaskList
            });
            COPY_USER_WORK_TEMPLATE_WINDOW.initialize({
                speciesId:${species.id}
            });

            if(USER_PATTERN_ID) {
                loadUserPatternTasks(USER_PATTERN_ID);
            } else {
                redrawPatternTasks();
            }
            if(google) {
                google.charts.load('current', {'packages': ['gantt']});
                google.charts.setOnLoadCallback(function () {
                    GANNT_CHART = new google.visualization.Gantt(document.getElementById('GanttChartDIV'));

                    if (USER_WORK_ID) {
                        refreshUserWorkTaskList();
                    }
                });
            } else {
                if (USER_WORK_ID) {
                    refreshUserWorkTaskList();
                }
            }
        });
    }

    var counter = 0;
    function addAllWorks() {
        counter = 0;
        var patternTask = PATTERN_TASKS[counter];
        if (patternTask.id == null) {
            showWorkToMyPlanByIndex(
                    counter,
                    function () {
                        refreshUserWorkTaskList();
                        doNextWork();
                    }
            );
        } else {
            showWorkToMyPlanModal(
                    patternTask.id,
                    function () {
                        refreshUserWorkTaskList();
                        doNextWork();
                    }
            );
        }
    }

    function doNextWork() {
        counter++;
        var patternTask = PATTERN_TASKS[counter];
        if(counter < PATTERN_TASKS.length) {
            if (patternTask.id == null) {
                showWorkToMyPlanByIndex(
                        counter,
                        function () {
                            refreshUserWorkTaskList();
                            doNextWork();
                        }
                );
            } else {
                showWorkToMyPlanModal(
                        patternTask.id,
                        function () {
                            refreshUserWorkTaskList();
                            doNextWork();
                        }
                );
            }
        } else {
            refreshUserWorkTaskList();
            EDIT_USER_TASK_WINDOW.hide();
        }
    }

    function loadUserPatternTasks(userPatternId) {
        $.get(
            "api/user/task/"+userPatternId+"/all",
            function(patternTasks) {
                var deltaYear = processTasksYear(patternTasks);
                EDIT_USER_TASK_WINDOW.config.DELTA_YEAR = deltaYear;
                PATTERN_TASKS = patternTasks;
                redrawPatternTasks();
            }
        );
    }

    function redrawPatternTasks() {
        if (PATTERN_TASKS.length == 0) {
            USER_WORK_TASK_LIST.html('');
            initializeGeneralTasksList();//in included work_list.jsp
            if(workList) {
                workList.show()
            }//in included work_list.jsp
            $("#addAllWorkBtn").hide();
            $("#findWorkBtn").hide();
            return;
        }
        if(workList) {
            workList.hide()
        }//in included work_list.jsp
        $("#addAllWorkBtn").show();
        $("#findWorkBtn").show();
        USER_WORK_TASK_LIST.html('');
        var cache = {};
        for (var i = 0; i < PATTERN_TASKS.length; i++) {
            var patternTask = PATTERN_TASKS[i];
            cache[patternTask.id] = patternTask;
            PATTERN_TASKS_NAME_MAP[patternTask.id] = {index: i, name: patternTask.taskName};
            var descr = "-";

            if (patternTask) {
                switch (patternTask.planingType) {
                    case "planByDate":
                        descr = "<span class='fa fa-thumb-tack' title='Указана точная дата'> </span>"
                        + (patternTask.calculatedDate=='' ? "" : parseDate(patternTask.calculatedDate).toString("dd.MM.yyyy"));
                        break;
                    case "planByWeather":
                        descr = "<span class='fa fa-sun-o' title='Зависит от погоды'> </span>"
                        + patternTask.averageTemperature + " C ("
                            + parseDate(patternTask.startDate).toString("dd.MM.yyyy") + " - "
                            + parseDate(patternTask.endDate).toString("dd.MM.yyyy") + ")";
                        break;
                    case "planByAnotherTask":

                        var dependsFrom = cache[patternTask.dependsFromTaskId];
                        descr = "<span class='fa fa-gears' title='Зависит от другой работы'> </span>"
                            + patternTask.countDaysAfterPrevEvent + " дней после " + dependsFrom.taskName;
                        break;
                }
            }

            var taskName = patternTask.taskName;

            $('<li class="w3-padding-16 w3-hover-light-gray">\
                    <div class="w3-row"><div class="w3-threequarter">\
                    <span class="w3-large w3-left">' + taskName + '</span>\
                    <span class="w3-right w3-small">' + descr + '</span>\
                <br><span class="w3-small w3-left" style="display: inline-block;">' + patternTask.comment + '</span></div>\
                <div class="w3-quarter">\
                    <span class="fa fa-arrow-circle-right w3-xxlarge w3-right w3-margin-right w3-text-grey w3-hover-text-lime" title="Добавить работу в план"' +
                    (patternTask.id == null ? ('onclick="showWorkToMyPlanByIndex(' + i + ', refreshUserWorkTaskList)">') : ('onclick="showWorkToMyPlanModal(' + patternTask.id + ', refreshUserWorkTaskList)">')) +
                    '</span>\
                </div></div></li>').appendTo(USER_WORK_TASK_LIST);
        }
    }

    function refreshUserWorkTaskList() {
        var cache = {};
        $.get(
                "api/user/task/"+USER_WORK_ID+"/all",
                function(userTasks) {
                    if(userTasks.length > 0) {
                        $("#saveAsTemplate").show();
                    }
                    USER_SPECIES_TASK_LIST.html('');

                    var data;
                    try {
                        data = new google.visualization.DataTable();
                        data.addColumn('string', 'Task ID');
                        data.addColumn('string', 'Task Name');
                        data.addColumn('string', 'Resource');
                        data.addColumn('date', 'Start Date');
                        data.addColumn('date', 'End Date');
                        data.addColumn('number', 'Duration');
                        data.addColumn('number', 'Percent Complete');
                        data.addColumn('string', 'Dependencies');
                    } catch(e) {
                        data = null;
                    }
                    for (var i = 0; i < userTasks.length; i++) {
                        var ut = userTasks[i];
                        cache[ut.id] = ut;
                        var calculatedDate = ut.calculatedDate;
                        if(false) {
                            switch (ut.planingType) {
                                case "planByDate":
                                    calculatedDate = "По дате - " + calculatedDate;
                                    break;
                                case "planByWeather":
                                    calculatedDate = "По погоде - " + ut.averageTemperature + " C (" + ut.startDate + " - " + ut.endDate + ")";
                                    break;
                                case "planByAnotherTask":
                                    var dependsFrom = cache[ut.dependsFromTaskId];
                                    calculatedDate = "По другой работе - " + ut.countDaysAfterPrevEvent + " дней после " + dependsFrom.taskName;
                                    break;
                            }
                        }
                        $('<li class="w3-padding-32 w3-hover-light-grey" style="line-height:50px;cursor:pointer;" onclick="showEditMyTask(' + ut.id + ')">\
            <div class="w3-row" style="padding: 0 20px 0 0">\
                <span class="w3-right">' + formatDate(calculatedDate, 'dd.MM.yyyy') + '</span>\
                <span class="w3-large w3-left">' + ut.taskName + '</span><br>\
                <p>' + ut.comment + '</p>\
            </div></li>').appendTo(USER_SPECIES_TASK_LIST);
                        if(data) {
                            data.addRow(['' + i, ut.taskName, null,
                                new Date(calculatedDate.substr(0, 4), (calculatedDate.substr(5, 2) - 1), calculatedDate.substr(8, 2), 0, 0, 0),
                                new Date(calculatedDate.substr(0, 4), (calculatedDate.substr(5, 2) - 1), calculatedDate.substr(8, 2), 23, 59, 59),
                                null, 0, null]);
                        }
                    }

                    if(userTasks.length>0) {
                        if(data) {
                            GANNT_CHART.draw(data, {
                                height: userTasks.length * 50
                            });
                        }
                    }
                }
        );
    }


    function showEditMyTask(userTaskId) {
        $("#skipBtn").hide();
        EDIT_USER_TASK_WINDOW.config = {
            callback: refreshUserWorkTaskList,
            enableDeleteButton : true,
            DELTA_YEAR : 0
        };
        EDIT_USER_TASK_WINDOW.edit(userTaskId, '${species.name}')
    }

    function showWorkToMyPlanByIndex(patternTaskIndex, callback) {
        EDIT_USER_TASK_WINDOW.config = callback ? {
            callback: callback,
            DELTA_YEAR : EDIT_USER_TASK_WINDOW.config.DELTA_YEAR
        } : {
            callback : refreshUserWorkTaskList,
            DELTA_YEAR : EDIT_USER_TASK_WINDOW.config.DELTA_YEAR
        };
        var json = PATTERN_TASKS[patternTaskIndex];
        if (callback) {
            $("#skipBtn").show();
        } else {
            $("#skipBtn").hide();
        }
        initUserWork(function () {
            EDIT_USER_TASK_WINDOW.editJson(json, '${species.name}', function (json) {
                json.id = null;
                json.userWorkId = USER_WORK_ID;
            });
        });
    }

    function showWorkToMyPlanModalInt(taskId, taskName) {
        EDIT_USER_TASK_WINDOW.config = {
            callback : refreshUserWorkTaskList,
            DELTA_YEAR : EDIT_USER_TASK_WINDOW.config.DELTA_YEAR
        };
        initUserWork(function(){
            EDIT_USER_TASK_WINDOW.edit(null, '${species.name}', function (data) {
                data.id = "";
                data.userWorkId = USER_WORK_ID;
                data.status = "CREATED";
                data.taskId = taskId;
                data.taskName = taskName;
                data.planingType = "planByDate";
                data.startDate = "";
                data.endDate = "";
                data.averageTemperature = "";
                data.countDaysAfterPrevEvent = "";
                data.dependsFromTaskId = "";
                data.comment = "";
                data.fertilizers = [];
            });
        });
    }

    function showWorkToMyPlanModal(userTaskId, callback) {
        var deltaYear;
        if(EDIT_USER_TASK_WINDOW.config.DELTA_YEAR) {
           deltaYear = EDIT_USER_TASK_WINDOW.config.DELTA_YEAR;
        } else {
            deltaYear = 0;
        }

        EDIT_USER_TASK_WINDOW.config = callback ? {
            callback: callback,
            DELTA_YEAR : deltaYear,
            patternTaskNameMap : PATTERN_TASKS_NAME_MAP
        } : {
            callback : refreshUserWorkTaskList,
            DELTA_YEAR : deltaYear
        };
        if(callback) {
            $("#skipBtn").show();
        } else {
            $("#skipBtn").hide();
        }
        initUserWork(function(){
            EDIT_USER_TASK_WINDOW.edit(userTaskId, '${species.name}', function(json){
                json.id = null;
                json.userWorkId = USER_WORK_ID;
            });
        });
    }

    function initUserWork(callback) {
        if(USER_WORK_ID!=null) {
            callback();
            return;
        }

        var userWorkJson = {
            id: USER_PATTERN_ID,
            speciesId: ${species.id},
            patternName: '${pattern.patternName==null ? species.name : pattern.patternName}'
        };

        var jsonStr = JSON.stringify(userWorkJson);
        $.ajax({
            url: "/api/user/work/pattern/init",
            type: "post",
            contentType: "application/json; charset=utf-8",
            data: jsonStr,
            success: function (userWorkId) {
                USER_WORK_ID = userWorkId;
                if(callback) {
                    callback();
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert(jqXHR.responseJSON.message);
            }
        });
    }
</script>

<!-- Work list Modal-->
<div id="workModal" class="w3-modal w3-padding-48">
    <div class="w3-modal-content w3-card-4 w3-center">
        <header class="w3-container w3-theme w3-left-align">
            <span onclick="$('#workModal').hide();" class="w3-closebtn">&times;</span>

            <h2>Выберите работу</h2>
        </header>

        <div class="w3-bar w3-left-align w3-padding" style="width:100%">
            <input id="workSearch" class=" w3-input w3-bar-item w3-light-gray w3-border-bottom" style="width:80%" placeholder="Название работы" onchange="workModalNext(0)">
            <a class="w3-bar-item w3-button w3-theme-dark" style="width:20%" onclick="workModalNext(0)"><span class="fa fa-search"></span></a>
        </div>
        <div class="w3-container">
            <p name="nothingFound" class="w3-text-dark-gray" style="font-style: italic">Ничего не найдено. <a
                    class="w3-text-blue" href="#" onclick="$('#workSearch').val('').change();"><span
                    class="fa fa-rotate-left"></span> Назад</a></p>
            <ul id="workItems" class="w3-ul"></ul>
            <div class="w3-center">
                <ul class="w3-pagination w3-light-grey">
                    <li><a href="javascript:void(0)" onclick="workModalNext(0, true);">&#10094;&#10094; Первая</a></li>
                    <li><a href="javascript:void(0)" name="back" onclick="workModalNext(-1, false);">&#10094; Назад</a></li>
                    <li><a class="w3-theme" href="javascript:void(0)" onclick="workModalNext(0, true);">1</a></li>
                    <li><a href="javascript:void(0)" onclick="workModalNext(1, true);">2</a></li>
                    <li><a href="javascript:void(0)" name="next" onclick="workModalNext(1, false);">Вперед &#10095;</a></li>
                    <li><a href="javascript:void(0)" onclick="workModalLast();">Последняя &#10095;&#10095;</a></li>
                </ul>
            </div>
        </div>

        <footer class="w3-container w3-theme">
            <div class="w3-right-align w3-padding-16">
                <button class="w3-btn w3-grey w3-hover-white" onclick="$('#workModal').hide();">Отмена</button>
                <button onclick="addSelectedWorksToGeneralList()" class="w3-btn w3-theme-dark w3-hover-white">Добавить выбранное</button>
            </div>
        </footer>

    </div>
</div>

<script type="text/javascript">
    var workModal = $("#workModal");
    var workModalPageSizeDef = 10;
    var workModalSelectedItems = {};
    var CUSTOM_TASKS_CACHE = {};
    var pn = 0;
    var totalRows = 0;
    var totalPages = 1;

    function showWorkModal() {
        loadWorkModal(0, workModalPageSizeDef, function(){
            workModal.show();
        });
    }

    function addSelectedWorksToGeneralList() {
        for(var taskId in workModalSelectedItems){
            var task = workModalSelectedItems[taskId];

            var patternTask = {
                id: null,
                userWorkId: null,
                status: "CREATED",
                taskId : task.id,
                taskName: task.name,
                planingType: "planByDate",
                fertilizers: [],
                averageTemperature: null,
                startDate: "",
                endDate: "",
                calculatedDate: "",
                comment: "",
                countDaysAfterPrevEvent: null,
                dependsFromTaskId: null
            };
            PATTERN_TASKS.push(patternTask);
        }

        redrawPatternTasks();

        workModal.hide();
    }

    function workModalLast() {
        var lastPageNum = Math.round(totalRows / workModalPageSizeDef);
        workModalNext(lastPageNum, true)
    }

    function workModalNext(dPageNum, realPageNum) {
        if(realPageNum == true) {
            pn = dPageNum;
        } else {
            pn += dPageNum;
        }
        pn = pn < 0 ? 0 : pn >= totalPages ? totalPages-1 : pn;
        loadWorkModal(pn, workModalPageSizeDef)
    }

    function drawModalWorkPageButtons() {
        totalPages = Math.ceil(totalRows / workModalPageSizeDef);
        var pagination = workModal.find("ul.w3-pagination");
        pagination.find("*").remove();

        $('<li><a href="javascript:void(0)" onclick="workModalNext(0, true);">&#10094;&#10094; Первая</a></li>\
           <li><a href="javascript:void(0)" name="back" onclick="workModalNext(-1, false);">&#10094; Назад</a></li>').appendTo(pagination);
        for(var i=0;i<totalPages;i++) {
            if(i==pn) {
                $('<li><a class="w3-theme" href="javascript:void(0)" onclick="workModalNext(' + i + ', true);">' + (i + 1) + '</a></li>').appendTo(pagination);
            } else {
                $('<li><a href="javascript:void(0)" onclick="workModalNext(' + i + ', true);">' + (i + 1) + '</a></li>').appendTo(pagination);
            }
        }
        $('<li><a href="javascript:void(0)" name="next" onclick="workModalNext(1, false);">Вперед &#10095;</a></li>\
           <li><a href="javascript:void(0)" onclick="workModalLast();">Последняя &#10095;&#10095;</a></li>').appendTo(pagination);

    }

    function loadWorkModal(pageNum, pageSize, callback) {
        var workItems = workModal.find("#workItems");

        var url = "api/tasks/available/page?pageNum=" + pageNum + "&pageSize=" + pageSize + "&order.name=asc";
        var searchStr = $('#workSearch').val();
        if(searchStr.trim() != '') {
            url += "&query=(name contains '*" + encodeURIComponent(searchStr) + "*')";
        }

        $.get(
                url,
                function (page) {
                    var items = page.items;
                    totalRows = page.totalCount;

                    drawModalWorkPageButtons();

                    workItems.find("*").remove();

                    if(items.length==0) {
                        workModal.find('*[name="nothingFound"]').show();
                        return;
                    }
                    workModal.find('*[name="nothingFound"]').hide();

                    for (var i = 0; i < items.length; i++) {
                        var item = items[i];
                        CUSTOM_TASKS_CACHE[item.id] = item;

                        var uiItem = $("<li class='w3-hover-light-grey' itemId="+item.id+">" + item.name + "</li>");
                        uiItem.appendTo(workItems);
                        if(workModalSelectedItems[item.id]!=null) {
                            uiItem.addClass("w3-theme-light");
                        }
                        uiItem.click(function () {
                            var itemId = $(this).attr('itemId');
                            if ($(this).hasClass("w3-green")) {
                                $(this).removeClass("w3-green");
                                delete workModalSelectedItems[itemId];
                            } else {
                                $(this).addClass("w3-green");
                                workModalSelectedItems[itemId] = CUSTOM_TASKS_CACHE[itemId];
                            }
                        });
                    }

                    if(callback) {
                        callback();
                    }
                }
        );
    }
</script>