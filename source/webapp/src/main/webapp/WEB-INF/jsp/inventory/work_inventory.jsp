<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--jsp:include page="../ads/news4columns.jsp"/-->


<div class="w3-container w3-lime w3-left-align w3-margin-bottom w3-text-white">
    <h1>Садовые работы</h1>
</div>
<!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2 w3-left-align">
    <p>Вы находитесь в библиотеке работ. Здесь содержатся как системные работы, так и те, которые Вы создали самостоятельно. <a class="w3-text-blue" href="help_work_inventory.tiles">Подробнее...</a></p>
</div-->
<div class="w3-container w3-padding-0 w3-margin-bottom w3-text-grey">
    <a class="w3-btn w3-theme w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile"
       accesskey="a"
       onclick="EDIT_TASK_WINDOW.edit(null);"><span class="fa fa-plus"></span> Новая работа</a>

</div>
<div class="w3-row">
    <div class="w3-col w3-threequarter">
<div class="w3-row w3-container w3-margin-bottom">
    <div class="w3-threequarter">
        <div class="w3-half" style="min-width:300px">
            <div class="w3-card w3-border-0 w3-padding-tiny">
                <h2 class="w3-text-theme">Работы</h2>
                <ul id="systemTasks" class="w3-ul w3-left-align"></ul>
            </div>
        </div>

        <div class="w3-half" style="min-width:300px">
            <div class="w3-card w3-border-0 w3-padding-tiny">
                <h2 class="w3-text-theme">Мои работы</h2>
                <ul id="userTasks" class="w3-ul w3-left-align"></ul>
            </div>
        </div>
    </div>
    <div class="w3-quarter">
        <div class="w3-card w3-border-0" style="min-height:600px">
            <p></p>
        </div>
    </div>
</div>
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
        refreshSystemsTasks();
        refreshUsersTasks();
        EDIT_TASK_WINDOW.initialize({
            callback: refreshUsersTasks
        });
    }

    function refreshSystemsTasks() {
        $.get(
                "api/tasks/system/scroll?pageNum=0&pageSize=1000&order.name=asc",
                function (items) {
                    $("#systemTasks").html('');
                    for (var i = 0; i < items.length; i++) {
                        var it = items[i];
                        $("<li>" + it.name + "<span class='fa fa-list w3-right w3-xlarge w3-hover-text-theme' title='Список культур' onclick='showLinkedSpecies(" + it.id + ")'>&emsp;</span></li>")
                                .appendTo($("#systemTasks"));
                    }
                }
        );
    }
    function refreshUsersTasks() {
        $.get(
                "api/tasks/users/scroll?pageNum=0&pageSize=1000&order.name=asc",
                function (items) {
                    $("#userTasks").html('');
                    for (var i = 0; i < items.length; i++) {
                        var it = items[i];
                        $("<li>" + it.name + "<span class='fa fa-trash w3-xlarge w3-right w3-hover-text-red' title='Удалить работу' onclick='showDeleteTaskForm(" + it.id + ", \"" + it.name + "\")'>&emsp;</span>" +
                                "<span class='fa fa-edit w3-xlarge w3-right w3-hover-text-theme' title='Редактировать работу' onclick='EDIT_TASK_WINDOW.edit(" + it.id + ")'>&emsp;</span>" +
                                "<span class='fa fa-list w3-right w3-xlarge w3-hover-text-theme' title='Список культур' onclick='showLinkedSpecies(" + it.id + ")'>&emsp;</span></li>")
                                .appendTo($("#userTasks"));
                    }
                }
        );
    }

    function showLinkedSpecies(taskId) {
        $.get(
                "api/tasks/" + taskId + "/species",
                function (items) {
                    if (items.length == 0) {
                        $("#taskSpeciesList").html('<li>Работа не используется ни в одном методе</li>');
                    } else {
                        $("#taskSpeciesList").html('');
                        for (var i = 0; i < items.length; i++) {
                            var it = items[i];
                            $("<li>" + it.name + "</li>").appendTo($("#taskSpeciesList"));
                        }
                    }
                    $('#viewTaskSpecies').show();
                }
        );
    }

    function showDeleteTaskForm(taskId, taskName) {
        $('#confirmDeleteTask').find("b[name='workName']").html(taskName);
        $('#confirmDeleteTask').attr("taskId", taskId).show();
    }

    function deleteTask() {
        $.ajax(
                {
                    url: "api/tasks/users/" + $('#confirmDeleteTask').attr("taskId"),
                    type: "DELETE",
                    data: "",
                    contentType: "application/json; charset=utf-8",
                    success: function (data, textStatus, jqXHR) {
                        refreshUsersTasks();
                        $("#confirmDeleteTask").hide();
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        alert(jqXHR.responseJSON.message);
                    }
                }
        );
    }
</script>

<!-- View Species for the task Modal-->
<div id="viewTaskSpecies" class="w3-modal w3-padding-48">
    <div class="w3-modal-content w3-card-4 w3-center">
        <header class="w3-container w3-theme w3-left-align">
            <span onclick="$('#viewTaskSpecies').hide();" class="w3-closebtn">&times;</span>
            <h2>Эта работа выполняется для культур...</h2>
        </header>
        <div class="w3-container w3-left-align w3-padding">
            <ul id="taskSpeciesList" class="w3-ul"></ul>
        </div>
        <footer class="w3-container w3-theme">
            <div class="w3-right-align w3-padding-16">
                <button type="button" class="w3-btn w3-theme-dark w3-hover-white"
                        onclick="$('#viewTaskSpecies').hide();">Закрыть
                </button>
            </div>
        </footer>
    </div>
</div>

<!-- Confirm WOrk deletion Modal-->
<div id="confirmDeleteTask" class="w3-modal w3-padding-48">
    <div class="w3-modal-content w3-card-4 w3-center">
        <header class="w3-container w3-theme w3-left-align">
            <span onclick="$('#confirmDeleteTask').hide();" class="w3-closebtn">&times;</span>

            <h2><span class="fa fa-question-circle w3-text-white"></span></h2>
        </header>
        <div class="w3-container w3-left-align">
            <p>Вы действительно хотите удалить работу <b name="workName">"название работы"</b>?</p>
        </div>
        <footer class="w3-container w3-theme">
            <div class="w3-right-align w3-padding-16">
                <button type="button" onclick="$('#confirmDeleteTask').hide();" class="w3-btn w3-grey w3-hover-white">
                    Отмена
                </button>
                <button type="button" class="w3-btn w3-theme-dark w3-hover-white" onclick="deleteTask();">Удалить
                </button>
            </div>
        </footer>
    </div>
</div>
