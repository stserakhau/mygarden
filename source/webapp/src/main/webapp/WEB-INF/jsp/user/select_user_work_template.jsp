<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div id="pageHeader" class="w3-container w3-lime w3-left-align w3-margin-bottom w3-text-white">
    <h1>${species.name}</h1>
</div>

<!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2 w3-left-align">
    <p>Вы видите методы для культуры, созданные Вами, либо скопированные из системной библиотеки. <a class="w3-text-blue" href="help_view_species_templates.tiles">Подробнее...</a></p>
</div-->

<div class="w3-container w3-padding-0 w3-margin-bottom">
    <a id="backButton" href="species_inventory_user.tiles"
       accesskey="b" class="w3-btn w3-grey  w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile"><span class="fa fa-chevron-left">&emsp;</span>Назад</a>
    <a onclick="EDIT_SPECIES_WINDOW.edit(${species.id})" accesskey="e"
       class="w3-btn w3-theme  w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile"><span class="fa fa-edit">&emsp;</span>Редактировать культуру</a>
    <a onclick="$('#confirmDeleteSpecies').show();" accesskey="d"
       class="w3-btn w3-theme w3-hover-red w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile"><span class="fa fa-trash">&emsp;</span>Удалить культуру</a>
    <a onclick="EDIT_USER_WORK_TEMPLATE_WINDOW.edit(null);" accesskey="a"
       class="w3-btn w3-theme w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile"><span class="fa fa-plus">&emsp;</span>Мой метод</a>

</div>

<div class="w3-row w3-container w3-left-align w3-small">
    <div class="w3-threequarter">
    ${species.description}
    </div>
    <div class="w3-quarter">
        <span id="imagesList">
            <p><img src="static/carousel-2.png" class="w3-card-2" style="max-width:230px"/></p>
        </span>
        <p><a class="w3-text-blue" href="#" onclick="UPLOAD_FILE_WINDOW.open()">Загрузить фото</a></p>
    </div>
</div>

<div class="w3-row w3-container w3-margin-bottom">
    <div>
        <ul id="userWorksList" class="w3-ul w3-left-align"></ul>
    </div>
</div>

<!-- Confirm deleting Species Modal-->
<form id="confirmDeleteSpecies" action="" method="post" class="w3-modal w3-padding-48">
    <div class="w3-modal-content w3-card-4 w3-center">
        <header class="w3-container w3-theme w3-left-align">
            <span onclick="$('#confirmDeleteSpecies').hide()" class="w3-closebtn">&times;</span>
            <h2><span class="fa fa-question-circle w3-text-white"></span></h2>
        </header>
        <div class="w3-container w3-left-align">
            <p>Вы действительно хотите удалить из своей библиотеки <b>${species.name}</b> со всеми методами выращивания?</p>

            <p>
                <input id="removeCatalogIfEmpty" name="removeCatalogIfEmpty" class="w3-check" type="checkbox" value="${species.catalogId}">
                <label class="w3-label" for="removeCatalogIfEmpty">Удалить класс, если в нем не осталось растений</label>
            </p>

        </div>
        <footer class="w3-container w3-theme">
            <div class="w3-right-align w3-padding-16">
                <button type="button" class="w3-btn w3-grey" onclick="$('#confirmDeleteSpecies').hide();">Отмена</button>
                <button type="submit" class="w3-btn w3-theme-dark ">Удалить</button>
            </div>
        </footer>
    </div>
</form>

<!-- Confirm restoring default settings for Species Modal-->
<!--form id="confirmRestoreSpeciesDefaults" action="" method="post" class="w3-modal w3-padding-48">
    <div class="w3-modal-content w3-card-4 w3-center">
        <header class="w3-container w3-theme w3-left-align">
            <span onclick="$('#confirmRestoreSpeciesDefaults').hide()" class="w3-closebtn">&times;</span>
            <h2>Подтвердить восстановление системных настроек</h2>
        </header>
        <div class="w3-container w3-left-align">
            <p>Вы действительно хотите восстановить системные настройки для культуры <b>${species.name}</b>? Все
                измененные или добавленные методы выращивания будут удалены, удаленные - восстановлены. Отменить
                восстановление будет невозможно. Нажмите кнопку <b>восстановить</b>, если хотите
                продолжить. <a class="w3-text-blue" href="#">Подробнее...</a></p>

        </div>
        <footer class="w3-container w3-theme">
            <div class="w3-right-align w3-padding-16">
                <button type="button" class="w3-btn w3-grey " onclick="$('#confirmRestoreSpeciesDefaults').hide();">Отмена</button>
                <button type="submit" class="w3-btn w3-theme-dark ">Восстановить</button>
            </div>
        </footer>
    </div>
</form-->

<script type="text/javascript">
    var backTo = getUrlVars()['backTo'];
    if (backTo == 'system') {
        $("#backButton").attr("href", "species_inventory_system.tiles");
    }

    var USER_WORKS_LIST = $('#userWorksList');

    document.afterInitializationUIEnvironmentFunc = initializePage;

    function initializePage() {
        doAction(function() {
            EDIT_SPECIES_WINDOW.initialize();
            EDIT_USER_WORK_TEMPLATE_WINDOW.initialize(${species.id});

            refreshImagesList(function(items){
                var config = {
                    speciesId:${species.id},
                    callback: function() {
                        refreshImagesList(function(items){
                            if(items.length>0) {
                                config.fileId = items[0].id;
                            }
                        });
                    }
                };
                if(items.length>0) {
                    config.fileId = items[0].id;
                }
                UPLOAD_FILE_WINDOW.initialize(config);
            });
            refreshGrowingOptions();
            initConfirmDeleteSpeciesForm();
        });
    }

    function refreshImagesList(callback) {
        loadSpeciesImages({
                    speciesId:${species.id},
                    container:$("#imagesList")
                },
            function(items, conf) {
                if(items.length>0) {
                    $("#imagesList").html("");
                }
                for(var i=0;i<items.length; i++) {
                    var item = items[i];
                    var imagePath = 'api/data-storage/' + item.id + "?ts="+new Date();
                    $("<p><img src='"+imagePath+"' class='w3-card-2' title='${species.name}' alt='${species.name}' style='max-width:230px'></p>")
                        .appendTo(conf.container);
                }
                if(callback) {
                    callback(items)
                }
            }
        )
    }

    function refreshGrowingOptions() {
        $.get(
                "/api/user/work/?pageNum=0&pageSize=100&order.patternName=asc&query=(pattern eq true and speciesId eq ${species.id})",
                function(items) {
                    USER_WORKS_LIST.html('');
                    if(items.length==0) {
                        $('<div class="w3-row m12 s12 w3-text-grey">\
                        <div class="w3-threequarter w3-hover-light-gray w3-margin-bottom" style="cursor: pointer">\
                                <img src="static/empty.jpg" class="w3-margin-right w3-left" style="width:80px;height:80px">\
                                <span class="w3-large">У Вас нет ни одного метода для данной культуры.</span>'
                                + '<br>\
                        <span class="w3-small">Вы можете добавить культуру в план и выбрать нужные работы и без использования метода. При этом, любой выбранный Вами список работ может быть сохранен как Ваш метод на будущее</span>\
                        </div>\
                        <div class="w3-quarter">\
                            <a href="add_species_work.tiles?speciesId=${species.id}&back=select_user_work_template.tiles%3FspeciesId%3D${species.id}" title="Добавить в мой план" \
                            class="w3-btn w3-theme-dark w3-hover-lime w3-padding-16 w3-large w3-mobile w3-margin-bottom"  style="max-width:230px">\
                            <span class="fa fa-plus w3-large w3-hide-large w3-hide-small"></span><span class="w3-hide-medium ">Хочу посадить!</span><span></span></a>\
                        </div>\
                        </div>').appendTo(USER_WORKS_LIST)
                        return;
                    }
                    for(var i=0; i<items.length; i++) {
                        var item = items[i];

                        $('<div class="w3-row m12 s12">\
                        <div onclick="location.href=\'edit_user_work_template.tiles?userWorkId='+item.id+'\'" class="w3-threequarter w3-hover-light-gray w3-margin-bottom" title="Нажмите для просмотра и редактирования" style="cursor: pointer">\
                                <img src="static/empty.jpg" class="w3-margin-right w3-left" style="width:80px;height:80px">\
                                <span class="w3-large">' + item.patternName + '</span>'
                        + '<br>\
                        <span class="w3-small">' + item.patternDescription + '</span>\
                        </div>\
                        <div class="w3-quarter">\
                            <a href="add_species_work.tiles?speciesId='+item.speciesId+'&userPatternId='+item.id+'&back=select_user_work_template.tiles%3FspeciesId%3D${species.id}" title="Добавить в мой план" \
                            class="w3-btn w3-theme-dark w3-hover-lime w3-padding-16 w3-large w3-mobile w3-margin-bottom">\
                            <span class="fa fa-plus w3-large w3-hide-large w3-hide-small"></span><span class="w3-hide-medium ">Хочу!</span><span></span></a>\
                        </div>\
                        </div>').appendTo(USER_WORKS_LIST);
                    }
                }
        );
    }

    function initConfirmDeleteSpeciesForm() {
        var form = $("#confirmDeleteSpecies");
        form.submit(function(){
            $.ajax({
                type: "DELETE",
                url: "/api/species/${species.id}",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader("removeCatalogIfEmpty", form.find("#removeCatalogIfEmpty").prop("checked"));
                },
                success: function(data) {
                    $("#confirmDeleteSpecies").hide();
                    window.location = 'species_inventory_user.tiles?nocache='+(new Date()).getTime();
                }
            });
            return false;
        });
    }
</script>
