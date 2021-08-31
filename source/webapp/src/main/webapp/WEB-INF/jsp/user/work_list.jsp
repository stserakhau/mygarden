<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div id="worksList" class="w3-card w3-border-0 w3-center" style="${param.style}">
    <div class="w3-bar w3-left-align w3-padding" style="width:100%">
        <input id="workSearch123" class=" w3-input w3-bar-item w3-light-gray w3-border-bottom" style="width:80%" placeholder="Название работы" onchange="workListNext(0)">
        <a class="w3-bar-item w3-button w3-theme-dark" style="width:20%" onclick="workListNext(0)"><span class="fa fa-search"></span></a>
    </div>
    <div class="w3-container">
        <p name="nothingFound" class="w3-text-dark-gray" style="font-style: italic">Ничего не найдено.
            <a onclick="$('#workSearch123').val('').change();" class="w3-text-blue"><span class="fa fa-rotate-left"></span> Назад</a></p>
        <ul name="workItems" class="w3-ul"></ul>
        <div class="w3-center">
            <ul class="w3-pagination w3-light-grey">
                <li><a onclick="workListNext(0, true);">&#10094;&#10094; Первая</a></li>
                <li><a name="back" onclick="workListNext(-1, false);">&#10094; Назад</a></li>
                <li><a class="w3-theme" onclick="workListNext(0, true);">1</a></li>
                <li><a onclick="workListNext(1, true);">2</a></li>
                <li><a name="next" onclick="workListNext(1, false);">Вперед &#10095;</a></li>
                <li><a onclick="workListLast();">Последняя &#10095;&#10095;</a></li>
            </ul>
        </div>
    </div>
</div>
<script type="text/javascript">
    var workList = $("#worksList");
    var workListPageSizeDef = 10;
    var pn = 0;
    var totalRows = 0;
    var totalPages = 1;

    function initializeGeneralTasksList() {
        loadWorkList(0, workListPageSizeDef);
    }

    function workListLast() {
        var lastPageNum = Math.round(totalRows / workListPageSizeDef);
        workListNext(lastPageNum, true)
    }

    function workListNext(dPageNum, realPageNum) {
        if(realPageNum == true) {
            pn = dPageNum;
        } else {
            pn += dPageNum;
        }
        pn = pn < 0 ? 0 : pn >= totalPages ? totalPages-1 : pn;
        loadWorkList(pn, workListPageSizeDef)
    }

    function drawWorkPageButtons() {
        totalPages = Math.ceil(totalRows / workListPageSizeDef);
        var pagination = workList.find("ul.w3-pagination");
        pagination.find("*").remove();

        $('<li><a href="javascript:void(0)" onclick="workListNext(0, true);">&#10094;&#10094; Первая</a></li>\
           <li><a href="javascript:void(0)" name="back" onclick="workListNext(-1, false);">&#10094; Назад</a></li>').appendTo(pagination);
        for(var i=0;i<totalPages;i++) {
            if(i==pn) {
                $('<li><a href="javascript:void(0)" class="w3-theme" onclick="workListNext(' + i + ', true);">' + (i + 1) + '</a></li>').appendTo(pagination);
            } else {
                $('<li><a href="javascript:void(0)" onclick="workListNext(' + i + ', true);">' + (i + 1) + '</a></li>').appendTo(pagination);
            }
        }
        $('<li><a href="javascript:void(0)" name="next" onclick="workListNext(1, false);">Вперед &#10095;</a></li>\
           <li><a href="javascript:void(0)" onclick="workListLast();">Последняя &#10095;&#10095;</a></li>').appendTo(pagination);

    }

    function loadWorkList(pageNum, pageSize, callback) {
        var workItems = workList.find("*[name='workItems']");

        var url = "api/tasks/available/page?pageNum=" + pageNum + "&pageSize=" + pageSize + "&order.name=asc";
        var searchStr = $('#workSearch123').val();
        searchStr = "*" + searchStr.trim() + "*";
        if(searchStr != '') {
            url += "&query=(name contains '" + encodeURIComponent(searchStr) + "')";
        }

        $.get(
                url,
                function (page) {
                    var tasks = page.items;
                    totalRows = page.totalCount;

                    drawWorkPageButtons();

                    workItems.find("*").remove();

                    if(tasks.length==0) {
                        workList.find('*[name="nothingFound"]').show();
                        return;
                    }
                    workList.find('*[name="nothingFound"]').hide();

                    for (var i = 0; i < tasks.length; i++) {
                        var task = tasks[i];

                        var taskName = task.name + (task.ownerId!=null ? ' <b>(моя)</b>' : '');

                        var uiItem = $('<li class="w3-hover-light-grey" itemId='+task.id+'>\
                                            <div class="w3-row">\
                                                <div class="w3-threequarter">\
                                                    <span class="w3-large w3-left">' + taskName + '</span>\
                                                </div>\
                                                <div class="w3-quarter">\
                                                    <span class="fa fa-arrow-circle-right w3-xxlarge w3-right w3-margin-right w3-hover-text-theme" \
                                                    title="Добавить в мой метод" onclick="${param.addMethodName}(' + task.id + ', \'' + taskName + '\')"></span>\
                                                </div>\
                                            </div>\
                                        </li>');
                        uiItem.appendTo(workItems);
                    }

                    if(callback) {
                        callback();
                    }
                }
        );
    }
</script>