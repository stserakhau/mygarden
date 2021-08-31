<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="static/w3.css"/>

<div class="w3-container w3-lime w3-left-align w3-margin-bottom w3-text-white">
    <h1>Мой план работ</h1>
</div>
<!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2 w3-left-align">
    <p>Выберите критерий поиска справа - на ближайшие выходные или неделю, выберите как Вам удобнее получить данные - сгруппированными по культуре или по дате. <a class="w3-text-blue" href="help_my_work_plan.tiles">Подробнее...</a></p>
</div-->

<div class="w3-row">
    <div class="w3-quarter w3-hide-medium w3-hide-small"  style="min-width: 300px">
        <form id="searchForm1" class="w3-margin-bottom w3-left-align w3-padding-tiny">
            <p><label class="w3-label">Введите дату с:</label>
                <input class="w3-input w3-border" type="date" required="required" name="fromDate"></p>
            <p><label class="w3-label">Введите дату по:</label>
                <input class="w3-input w3-border" type="date" required="required" name="toDate"></p>
            <p><label class="w3-label">Выберите культуру:</label>
                <select class="w3-select" name="speciesId">
                    <option value="">Все культуры</option>
                </select></p>
            <p><label class="w3-label">Группировать по:</label>
            <p><input class="w3-radio" type="radio" name="groupMethod" value="groupByDate">
                <label>Дате</label></p>
            <p><input class="w3-radio" type="radio" name="groupMethod" value="groupByName" checked>
                <label>Культуре</label></p>
            <button type="submit" class="w3-btn-block w3-theme-dark w3-mobile">Показать работы&emsp;<span
                    class="fa fa-arrow-circle-right w3-large"></span></button>
        </form>
    </div>


    <!-- Side filter -->
 <nav class="w3-hide-large w3-sidenav w3-theme-l5 w3-animate-left" style="display:none; z-index: 2" id="mainSideFilter">
        <a href="javascript:void(0)" onclick="closeSideFilter()" class="w3-closenav w3-xlarge w3-text-theme">Закрыть
            <i class="fa fa-remove"></i>
        </a>
        <form id="searchForm2" class="w3-margin-bottom w3-left-align w3-padding-tiny">
            <p><label class="w3-label">Введите дату с:</label>
                <input class="w3-input w3-border" type="date" required="required" name="fromDate"></p>
            <p><label class="w3-label">Введите дату по:</label>
                <input class="w3-input w3-border" type="date" required="required" name="toDate"></p>
            <p><label class="w3-label">Выберите культуру:</label>
                <select class="w3-select" name="speciesId">
                    <option value="">Все культуры</option>
                </select></p>
            <p><label class="w3-label">Группировать по:</label>
            <p><input class="w3-radio" type="radio" name="groupMethod" value="groupByDate">
                <label>Дате</label></p>
            <p><input class="w3-radio" type="radio" name="groupMethod" value="groupByName" checked>
                <label>Культуре</label></p>
            <button type="submit" class="w3-btn-block w3-theme-dark w3-mobile">Показать работы&emsp;<span
                    class="fa fa-arrow-circle-right w3-large"></span></button>
        </form>
    </nav>

    <div class="w3-half w3-padding-tiny" style="min-width: 300px">
        <p><a id="last12" class="w3-text-blue w3-hover-text-red w3-center w3-padding-8 w3-center w3-mobile" href="#"
                  onclick="setDateFilter('last12')">Показать работы за последний год</a></p>
        <p><a id="last6" class="w3-text-blue w3-hover-text-red w3-center w3-padding-8 w3-center w3-mobile" href="#"
              onclick="setDateFilter('last6')">Показать работы за последние 6 месяцев</a></p>
        <p><a id="last3" class="w3-text-blue w3-hover-text-red w3-center w3-padding-8 w3-center w3-mobile" href="#"
              onclick="setDateFilter('last3')">Показать работы за последние 3 месяца</a></p>
        <p><a id="last1" class="w3-text-blue w3-hover-text-red w3-center w3-padding-8 w3-center w3-mobile" href="#"
              onclick="setDateFilter('last1')">Показать работы за последний месяц</a></p>
        <p><a id="downloadCSV" class="w3-left w3-margin w3-padding-8"
              href="#"><span class="fa fa-th"></span>&emsp;CSV <span class="fa fa-chevron-right"></span></a></p>
        <p><a id="downloadICS" class="w3-left w3-margin w3-padding-8"
              href="#"><span class="fa fa-calendar"></span>&emsp;ICS <span class="fa fa-chevron-right"></span></a></p>

        <div class="filter w3-left w3-hide-large  w3-xlarge w3-grey" title="Сортировать работы" href="javascript:void(0)" onclick="openSideFilter()"><i class="fa fa-filter"></i></div>


        <div id="contentContainer" style="clear:both;"></div>
        <div style="text-align: center;" class="w3-margin-top">
            <ul class="w3-pagination w3-light-grey">
                <li><a href="#" onclick="goToPage(0, false);">❮❮ Первая</a></li>
                <li><a href="#" name="back" onclick="goToPage(-1, true);">❮ Назад</a></li>
                <li><select id="pageNumbers" onchange="goToPage(this.selectedIndex, false);"></select></li>
                <li><a href="#" name="next" onclick="goToPage(1, true);">Вперед ❯</a></li>
                <li><a href="#" onclick="goToPage(TOTAL_PAGES-1, false);">Последняя ❯❯</a></li>
            </ul>
        </div>
    </div>

    <div class="w3-quarter">
        <tiles:insertDefinition name="news4columnsVert"/>
    </div>
</div>


<script type="text/javascript">
    var nodeStateMap = {};
    function openHide(el, refId) {
        var label = $(el).find("span[name='updown']");

        var refEl = $("#" + refId);
        if (refEl.hasClass("w3-show")) {
            refEl.removeClass("w3-show");
            label.removeClass("fa-angle-double-up");
            label.addClass("fa-angle-double-down");
            delete nodeStateMap[refId];
        } else {
            refEl.addClass("w3-show");
            label.removeClass("fa-angle-double-down");
            label.addClass("fa-angle-double-up");
            nodeStateMap[refId]=el;
        }
    }
    function openHide1(el, refId) {
        var label = $(el).find("span");

        var refEl = $("#" + refId);
        if (refEl.hasClass("w3-show")) {
            refEl.removeClass("w3-show");
            label.removeClass("fa-angle-double-up");
            label.addClass("fa-angle-double-down");
            delete nodeStateMap[refId];
        } else {
            refEl.addClass("w3-show");
            label.removeClass("fa-angle-double-down");
            label.addClass("fa-angle-double-up");
            nodeStateMap[refId] = el;
        }
    }
    function initializeTreeState() {
        for(var id in nodeStateMap){
            nodeStateMap[id].click();
        }
    }

    document.afterInitializationUIEnvironmentFunc = initializePage;

    var SEARCH_FORM1 = null;
    var SEARCH_FORM2 = null;
    var SEARCH_FORM_STORAGE_ATTR = "my_work_plan_SearchForm";

    function initializePage() {
        doAction(function(){
            $("#last3, #last6, #last12").hide();
            EDIT_USER_TASK_WINDOW.initialize({
                callback : function() {
                    SEARCH_FORM1.submit();
                }
            });
            initSearchForm(function(){
                var savedSearchForm = getStorageAttr(SEARCH_FORM_STORAGE_ATTR);
                if(savedSearchForm==null) {
                    setDateFilter('next6months')
                } else {
                    populateFormFromJsonTree(SEARCH_FORM1, JSON.parse(savedSearchForm));
                    populateFormFromJsonTree(SEARCH_FORM2, JSON.parse(savedSearchForm));
                    SEARCH_FORM1.submit();
                }
            });
        });
    }

    var TODAY = new Date();
    TODAY.setHours(23, 59, 59, 999);

    function setDateFilter(filterType) {
        var fromDay = new Date();
        fromDay.setHours(0, 0, 0, 0);

        var toDay = new Date(TODAY.getTime());

        switch (filterType) {
            case 'last12':
                fromDay.setMonth(fromDay.getMonth() - 12);
                $("#last12").hide()
                break;
            case 'last6':
                fromDay.setMonth(fromDay.getMonth() - 6);
                $("#last6").hide()
                $("#last12").show()
                break;
            case 'last3':
                fromDay.setMonth(fromDay.getMonth() - 3);
                $("#last3").hide()
                $("#last6").show()
                break;
            case 'last1':
                fromDay.setMonth(fromDay.getMonth() - 1);
                $("#last1").hide()
                $("#last3").show()
                break;
            case 'next7':
                fromDay = TODAY;
                toDay.setDate(fromDay.getDate() + 7);
                break;
            case 'next6months':
                fromDay = TODAY;
                toDay.setDate(fromDay.getDate() + 180);
                break;
        }

        var fromDate = SEARCH_FORM1.find("input[name='fromDate']");
        fromDate.val(fromDay.toISOString().substr(0, 10));
        var toDate = SEARCH_FORM1.find("input[name='toDate']");
        toDate.val(toDay.toISOString().substr(0, 10));

        var fromDate2 = SEARCH_FORM2.find("input[name='fromDate']");
        fromDate2.val(fromDay.toISOString().substr(0, 10));
        var toDate2 = SEARCH_FORM2.find("input[name='toDate']");
        toDate2.val(toDay.toISOString().substr(0, 10));

        SEARCH_FORM1.submit();
    }

    function initSearchForm(callback) {
        SEARCH_FORM1 = $("#searchForm1");
        SEARCH_FORM2 = $("#searchForm2");

        var speciesDropDown1 = SEARCH_FORM1.find("select[name='speciesId']");
        var speciesDropDown2 = SEARCH_FORM2.find("select[name='speciesId']");
        $.get(
                'api/species/?pageNum=0&pageSize=1000&order.name=asc',
                function (data) {
                    speciesDropDown1.html('<option value="">Все культуры</option>');
                    speciesDropDown2.html('<option value="">Все культуры</option>');
                    for (var i = 0; i < data.length; i++) {
                        $("<option value='" + data[i].id + "'>" + data[i].name + "</option>")
                                .appendTo(speciesDropDown1);
                        $("<option value='" + data[i].id + "'>" + data[i].name + "</option>")
                                .appendTo(speciesDropDown2);
                    }

                    if(callback) {
                        callback();
                    }
                }
        );

        $("#searchForm1, #searchForm2").submit(function () {
            var csvUrl = "/api/user/task/scroll/exportCSV?pageNum=0&pageSize=1000";
            var icsUrl = "/api/user/task/scroll/exportICS?pageNum=0&pageSize=1000";
            var url = "/api/user/task/scroll?pageNum=0&pageSize=1000";
            var part = "";
            var formData = serializeFormToJsonTree($(this));

            setStorageAttr(SEARCH_FORM_STORAGE_ATTR, JSON.stringify(formData))

            var groupMethod = formData.groupMethod;
            switch (groupMethod) {
                case "groupByName" :
                    part += "&order.speciesName=asc&order.patternName=asc&order.calculatedDate=asc";
                    break;
                case "groupByDate":
                    part += "&order.calculatedDate=asc";
                    break;
            }
            var fromDate = formData.fromDate;
            var toDate = formData.toDate;
            var speciesId = formData.speciesId;

            var filterExist = false;
            part += "&query=(pattern eq false ";
            if (fromDate != '' && toDate != '') {
                part += "and calculatedDate ge '" + fromDate + "' and calculatedDate le '" + toDate + "'";
                filterExist = true;
            }

            if (speciesId != '') {
                if (filterExist) {
                    part += " and ";
                }
                part += "speciesId eq " + speciesId;
            }
            part += ")";

            url += part;
            csvUrl += part;
            $("#downloadCSV").attr("href", csvUrl);
            $("#downloadICS").attr("href", icsUrl);
            $.get(
                    url,
                    function (data) {
                        var contentContainer = $("#contentContainer");
                        contentContainer.html('');
                        eval(groupMethod)(contentContainer, data);
                        initializeTreeState();
                        closeSideFilter();
                    }
            );
            return false;
        });
    }

    var pageNumbers = $("#pageNumbers");
    var GROUP_PAGE_SIZE = 20;
    var CURRENT_PAGE_NUM = 0;
    var TOTAL_PAGES = 0;

    function goToPage(di, relative) {
        var contentContainer = $("#contentContainer");
        contentContainer.find("page:eq("+CURRENT_PAGE_NUM+")").hide();

        var newPageNum = relative ? CURRENT_PAGE_NUM + di : di;
        if(newPageNum >= TOTAL_PAGES) {
            newPageNum = TOTAL_PAGES-1;
        }
        if(newPageNum < 0) {
            newPageNum = 0;
        }
        contentContainer.find("page:eq("+newPageNum+")").show();
        CURRENT_PAGE_NUM = newPageNum;
        pageNumbers.prop('selectedIndex',newPageNum);
    }

    function groupByName(contentContainer, userTasks) {
        var nameTaskMap = {};
        for (var i = 0; i < userTasks.length; i++) {
            var ut = userTasks[i];
            var key = ut.speciesName+"_"+ut.patternName+"_"+ut.userWorkId;
            var nameTaskList = nameTaskMap[key];
            if (nameTaskList) {
            } else {
                nameTaskList = [];
                nameTaskMap[key] = nameTaskList;
            }
            nameTaskList.push(ut);
        }

        var itemCounter = 0;
        var pageCounter = 0;
        pageNumbers.html('');
        var pageContainer = null;

        for (var key in nameTaskMap) {
            if (itemCounter == 0) {
                $("<option value='"+pageCounter+"'>"+(pageCounter+1)+"</option>").appendTo(pageNumbers);
                pageContainer = $("<page></page>");
                pageContainer.appendTo(contentContainer)
                if (pageCounter == CURRENT_PAGE_NUM) {
                    pageContainer.show();
                } else {
                    pageContainer.hide();
                }
                pageCounter++;
            }
            itemCounter++;

            var nameTaskList = nameTaskMap[key];
            var userWorkId = nameTaskList[0].userWorkId;
            var speciesBlockId = 'sr' + userWorkId;
            var patternName = nameTaskList[0].patternName;
            var speciesName = nameTaskList[0].speciesName;
            $("<button name='"+speciesBlockId+"' class='w3-btn-block w3-left-align w3-theme-l4 w3-card-2 w3-hover-light-gray'  onclick='openHide(this, \"" + speciesBlockId + "\")'>\
                <div class='w3-row'>\
                <div class='w3-quarter'>\
                <a class='fa fa-edit w3-xlarge w3-text-theme w3-hover-text-grey' style='text-decoration: none;' title='Редактировать план' href='add_species_work.tiles?userWorkId="+userWorkId+"&back=my_work_plan.tiles'></a>&emsp;&emsp;\
                <span class='fa fa-trash w3-xlarge w3-text-theme w3-hover-text-red' title='Удалить план' onclick='deleteUserWork(" + nameTaskList[0].userWorkId + ", \"" + speciesName + "\", \"" + patternName + "\")'></span>\
                </div>\
                <div class='w3-threequarter w3-left'>\
                    <label  title='" + speciesName + " (" + patternName + ")' class='w3-label'>" + patternName + "</label>\
                    <span name='updown' class='fa fa-angle-double-down w3-text-theme w3-large'></span>\
                </div>\
            </button>").appendTo(pageContainer);

            var speciesContainer = $("<div id='" + speciesBlockId + "' class='w3-container w3-hide w3-padding-0'></div>");
            speciesContainer.appendTo(pageContainer);

            for (var j = 0; j < nameTaskList.length; j++) {
                var ut = nameTaskList[j];
                var workId = speciesBlockId + "wid" + ut.id;

                var fertilizers = ut.fertilizers ? ut.fertilizers : [];

                $("<button name='"+workId+"' class='w3-btn-block w3-left-align w3-light-grey1 w3-card-2 w3-hover-light-gray' title='" + ut.taskName + "' onclick='openHide(this, \"" + workId + "\")'>\
                    <div class='w3-row'>\
                        <div class='w3-quarter'>\
                            <span class='fa fa-close w3-xlarge w3-hover-text-red w3-text-grey' title='Удалить работу из моего плана' onclick='showDeleteTaskForm(" + ut.id + ", \"" + speciesName + "\", \"" + ut.taskName + "\", \"" + workId + "\");'></span>\
                        </div>\
                        <div class='w3-threequarter' >\
                            <label class='w3-label w3-left'>" + ut.taskName + "&emsp;</label><span name='updown' class='fa fa-angle-double-down w3-text-theme w3-large'></span>\
                        </div>\
                        <div class='w3-right w3-text-grey'>" + formatDate(ut.calculatedDate, 'dd.MM.yyyy') + "</div>\
                        <br/>\
                    </div>\
                </button>")
                        .appendTo(speciesContainer);

                var fertilizersContent = "<b>Удобрения: </b>";
                if(fertilizers.length>0) {
                    for (var k = 0; k < fertilizers.length; k++) {
                        var fert = ut.fertilizers[k];
                        fertilizersContent += fert.name + ((k < fertilizers.length - 1) ? ", " : "");
                    }
                }

                $("<div id='" + workId + "' name='"+workId+"' class='w3-container w3-hide'>\
                    <div class='w3-row w3-left-align w3-margin-top w3-margin-bottom'>" +
                        ( ut.taskComment!='' ? ("<p style='margin:0'>" + ut.taskComment + "</p>") : "") + "<hr/>" +
                        fertilizersContent +
                        "</div>\
                    </div>").appendTo(speciesContainer);

            }
            if (itemCounter == GROUP_PAGE_SIZE) {
                itemCounter = 0;
            }
        }
        TOTAL_PAGES = pageCounter;
    }

    function deleteUserWork(userWorkId, speciesName, patternName) {
        DELETE_USER_WORK_WINDOW.initialize({
            speciesName: speciesName,
            content: 'Вы действительно хотите удалить план работ для <patternName style="font-weight: bold;">patternName</patternName>?',
            callback: function () {
                SEARCH_FORM1.submit();
            }
        });

        DELETE_USER_WORK_WINDOW.display(userWorkId, patternName);
    }

    function showDeleteTaskForm(userTaskId, speciesName, taskName, workUIId) {
        DELETE_USER_TASK_WINDOW.initialize({
            speciesName : speciesName,
            callback : function() {
                SEARCH_FORM1.submit();
            }
        });
        DELETE_USER_TASK_WINDOW.display(userTaskId, taskName);
    }
    function groupByDate(contentContainer, userTasks) {
        var dateTaskMap = {};
        for (var i = 0; i < userTasks.length; i++) {
            var ut = userTasks[i];

            var dateTaskList = dateTaskMap[ut.calculatedDate];
            if (dateTaskList) {
            } else {
                dateTaskList = [];
                dateTaskMap[ut.calculatedDate] = dateTaskList;
            }
            dateTaskList.push(ut);
        }

        var itemCounter = 0;
        var pageCounter = 0;
        pageNumbers.html('');
        var pageContainer = null;

        for (var date in dateTaskMap) {
            if (itemCounter == 0) {
                $("<option value='"+pageCounter+"'>"+(pageCounter+1)+"</option>").appendTo(pageNumbers);
                pageContainer = $("<page></page>");
                pageContainer.appendTo(contentContainer)
                if (pageCounter == CURRENT_PAGE_NUM) {
                    pageContainer.show();
                } else {
                    pageContainer.hide();
                }
                pageCounter++;
            }
            itemCounter++;
            var dateId = "date" + replaceAll(date, "-", "");
            $("<button name='"+dateId+"' onclick='openHide1(this, \"" + dateId + "\")' class='w3-btn-block w3-left-align w3-theme-l4 w3-card-2 w3-hover-light-gray' title='Нажмите для просмотра'>\
                <div class='w3-quarter w3-right'><label>" + formatDate(date, 'dd.MM.yyyy') + "&emsp;<span class='fa fa-angle-double-down'></span></label></div>\
               </button>").appendTo(pageContainer);

            var dateContainer = $("<div id='" + dateId + "' class='w3-container w3-hide w3-padding-0'></div>");
            dateContainer.appendTo(pageContainer);

            var dateTaskList = dateTaskMap[date];
            for (var i = 0; i < dateTaskList.length; i++) {
                var item = dateTaskList[i];

                var dateItemId = dateId + "_" + item.id;
                $("<button name='"+dateItemId+"'  class='w3-btn-block w3-left-align w3-light-grey1 w3-card-2 w3-hover-light-gray' onclick='openHide(this, \"" + dateItemId + "\")' title='" + item.speciesName + " (" + item.patternName + ")'>\
                        <div class='w3-row w3-left'>\
                        <div class='w3-quarter'>\
                        <a class='fa fa-edit w3-xlarge w3-text-theme w3-hover-text-grey' style='text-decoration: none;' title='Редактировать план' href='add_species_work.tiles?userWorkId="+item.userWorkId+"&back=my_work_plan.tiles'></a>&emsp;&emsp;\
                        <span class='fa fa-close w3-xlarge w3-text-theme w3-hover-text-red' title='Удалить работу из моего плана' onclick='showDeleteTaskForm(" + item.id + ", \"" + item.speciesName + "\", \"" + item.taskName + "\");'></span>\
                        </div>\
                        <div class='w3-threequarter' >\
                            <label class='w3-label w3-large'>" + item.patternName + "</label><span name='updown' class='fa fa-angle-double-down w3-text-theme w3-large'></span>\
                            <br/>\
                            <label class='w3-left-align'>" + item.taskName + "</label>\
                        </div>\
                        </div>\
                    </button>").appendTo(dateContainer);

                var fertilizers = item.fertilizers ? item.fertilizers : [];
                var fertilizersContent = "<b>Удобрения: </b>";
                if(fertilizers.length>0) {
                    for (var k = 0; k < fertilizers.length; k++) {
                        var fert = item.fertilizers[k];
                        fertilizersContent += fert.name + ((k < fertilizers.length - 1) ? ", " : "");
                    }
                }

                $("<div id='" + dateItemId + "' name='workDetails' class='w3-container w3-hide'>\
                    <div class='w3-row w3-left-align w3-margin-top w3-margin-bottom'>\
                        <div>" +
                        ( ut.taskComment!='' ? ("<p style='margin:0'>" + ut.taskComment + "</p>") : "") + "<hr/>" +
                         fertilizersContent +
                        "\
                    </div></div>").appendTo(dateContainer)
            }

            if (itemCounter == GROUP_PAGE_SIZE) {
                itemCounter = 0;
            }
        }

        TOTAL_PAGES = pageCounter;
    }
</script>