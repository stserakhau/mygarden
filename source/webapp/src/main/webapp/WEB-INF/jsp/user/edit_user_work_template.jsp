<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div id="pageHeader" class="w3-container w3-lime w3-left-align w3-margin-bottom w3-text-white">
    <h1>${userWork.patternName}</h1>
</div>
<!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2 w3-left-align">
    <p>Вы находитесь в форме редактирования шаблона. Слева Вы видите все раоты в системе. Справа - те работы, которые добавили в шаблон, и которые потом будут предлагаться по умолчанию при добавлении его в Ваш план работ. <a class="w3-text-blue" href="help_view_template.tiles">Подробнее...</a></p>
</div-->
<div class="w3-container w3-padding-0 w3-margin-bottom">
    <a href="select_user_work_template.tiles?speciesId=${userWork.speciesId}"
       class="w3-btn w3-grey w3-hover-white w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile">
        <span class="fa fa-chevron-left">&emsp;</span>Назад</a>
    <a onclick="EDIT_USER_WORK_TEMPLATE_WINDOW.edit(${userWork.id});"
       class="w3-btn w3-theme w3-hover-white w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile">
        <span class="fa fa-edit">&emsp;</span>Редактировать метод
    </a>
    <a onclick="COPY_USER_WORK_TEMPLATE_WINDOW.edit(${userWork.id});"
       class="w3-btn w3-theme w3-hover-white w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile">
        <span class="fa fa-copy">&emsp;</span>Копировать метод
    </a>
    <a onclick="DELETE_USER_WORK_WINDOW.display(${userWork.id}, '${userWork.patternName}')"
       class="w3-btn w3-theme w3-hover-red w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile">
        <span class="fa fa-trash">&emsp;</span>Удалить метод
    </a>
</div>

<div class="w3-row w3-container w3-margin-bottom w3-text-gray">
    <div id="workModal" class="w3-third">
    <c:choose><c:when test="${empty userWork.userId}">
        <p class="w3-text-grey" style="font-style: italic">Редактирование системных методов запрещено. Создайте свой метод, либо скопируйте текущий для редактирования работ в нем</p>
    </c:when><c:otherwise>
        <jsp:include page="work_list.jsp">
            <jsp:param name="addMethodName" value="showWorkToMyPlanModal"/>
        </jsp:include>
    </c:otherwise></c:choose>
    </div>

    <div class="w3-twothird w3-padding-tiny">
        <div class="w3-container w3-padding-0 w3-margin-bottom">
            <h2 class="w3-text-theme">Работы в методе</h2>
        </div>
        <ul id="userSpeciesTaskList" class="w3-container w3-card-4 w3-ul w3-left-align"></ul>
    </div>

    <div style="clear:both;padding-top:30px; " id="GanttChartDIV"></div>
</div>

<script type="text/javascript">
    var GANNT_CHART = null;

    document.afterInitializationUIEnvironmentFunc = initializePage;

    function initializePage() {
        doAction(function () {
            EDIT_USER_WORK_TEMPLATE_WINDOW.initialize(${userWork.speciesId});
            COPY_USER_WORK_TEMPLATE_WINDOW.initialize({
                speciesId:${userWork.speciesId}
            });

            DELETE_USER_WORK_WINDOW.initialize({
                speciesName:'${userWork.speciesName}',
                content: 'Вы действительно хотите удалить метод <patternName style="font-weight: bold;">patternName</patternName> для культуры  <speciesName style="font-weight: bold;">speciesName</speciesName>?',
                callback: function(){
                    location.href='select_user_work_template.tiles?speciesId=${userWork.speciesId}';
                }
            });
            EDIT_USER_TASK_WINDOW.initialize({
                enableDeleteButton: true,
                speciesName: '${userWork.speciesName}',
                callback: refreshUserTaskList
            });

            initializeGeneralTasksList();

            google.charts.load('current', {'packages':['gantt']});
            google.charts.setOnLoadCallback(function () {
                GANNT_CHART = new google.visualization.Gantt(document.getElementById('GanttChartDIV'));
                refreshUserTaskList();
            });
        });
    }

    function showEditMyTask(userTaskId) {
        EDIT_USER_TASK_WINDOW.edit(userTaskId, null, null);
    }

    function showWorkToMyPlanModal(taskId, actionTaskName) {
        $.get(
                        "api/species/tasks/fertilizers?pageNum=0&pageSize=1000&order.fertilizerName=asc&query=(taskId eq " + taskId + " and speciesId eq ${userWork.speciesId})",
                function(data){
                    var fertilizers = [];
                    for(var i=0;i<data.length;i++) {
                        fertilizers.push({id : data[i].fertilizerId, name : data[i].fertilizerName});
                    }

                    EDIT_USER_TASK_WINDOW.edit(null, '${userWork.speciesName}', function (data) {
                        data.id = "";
                        data.userWorkId = ${userWork.id};
                        data.status = "CREATED";
                        data.taskId = taskId;
                        data.taskName = actionTaskName;
                        data.planingType = "planByDate";
                        data.startDate = "";
                        data.endDate = "";
                        data.averageTemperature = "";
                        data.countDaysAfterPrevEvent = "";
                        data.dependsFromTaskId = "";
                        data.comment = "";
                        data.fertilizers = fertilizers;
                    });
                }
        );
    }

    function refreshUserTaskList() {
        var userSpeciesTaskList = $("#userSpeciesTaskList");
        userSpeciesTaskList.html('');

        $.get(
                "/api/user/task/${userWork.id}/all",
                function(userTasks) {
                    if (userTasks.length == 0) {
                        userSpeciesTaskList.html('<li class="w3-text-grey" style="font-style: italic">В данном методе нет еще ни одной работы</li>');
                        return;
                    }
                    var data = new google.visualization.DataTable();
                    data.addColumn('string', 'Task ID');
                    data.addColumn('string', 'Task Name');
                    data.addColumn('string', 'Resource');
                    data.addColumn('date', 'Start Date');
                    data.addColumn('date', 'End Date');
                    data.addColumn('number', 'Duration');
                    data.addColumn('number', 'Percent Complete');
                    data.addColumn('string', 'Dependencies');
                    for (var i = 0; i < userTasks.length; i++) {
                        var userTask = userTasks[i];
                        var taskName = userTask.taskName;

                        var start = userTask.startDate;
                        var startddmmyyyy = parseDate(start).toString("dd.MM.yyyy")
                        var end = userTask.endDate;
                        var endddmmyyyy = parseDate(end).toString("dd.MM.yyyy")
                        var singleDate = start==end;
                        $('<li class="w3-padding-32 w3-hover-light-grey" style="line-height:50px;cursor:pointer;" onclick="showEditMyTask(' + userTask.id + ')">\
                            <div class="w3-row" style="padding: 0 20px 0 0">\
                                <span class="w3-right">' + startddmmyyyy + (singleDate ? "" : "&nbsp;/&nbsp;" + endddmmyyyy) + '</span>\
                                <span class="w3-large w3-left">' + taskName + '</span><br>\
                                <p>'+userTask.comment+'</p>\
                            </div></li>').appendTo(userSpeciesTaskList);

                        data.addRow([''+i, taskName, null,
                            new Date(start.substr(0,4), (start.substr(5,2)-1), start.substr(8,2),0,0,0),
                            new Date(end.substr(0,4), (end.substr(5,2)-1), end.substr(8,2),23,59,59),
                            null, 0, null]);
                    }

                    GANNT_CHART.draw(data, {
                        height: 42 * userTasks.length
                    });
                }
        );
    }
</script>
