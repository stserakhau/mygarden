<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div id="pageHeader" class="w3-container w3-lime w3-left-align w3-margin-bottom w3-text-white">
    <h1>${species.name}</h1>
</div>
<!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2 w3-left-align">
    <p>Вы видите системные методы для культуры. Вы не можете их редактировать, только добавить в свой план, либо скопировать в свою библиотеку. <a class="w3-text-blue" href="help_view_species_templates.tiles">Подробнее...</a></p>
</div-->

<div class="w3-container w3-padding-0 w3-margin-bottom">
    <a id="backButton" href="species_inventory_system.tiles"
       accesskey="b" class="w3-btn w3-grey w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile"><span class="fa fa-chevron-left">&emsp;</span>Назад</a>
    <a id="copyBtn" onclick="copySystemSpeciesToMy()"
       class="w3-btn w3-theme w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile"><span class="fa fa-copy">&emsp;</span>Копировать в мою библиотеку</a>
</div>
<div class="w3-container w3-padding-0 w3-margin-bottom w3-left-align w3-small">
    <div class="w3-threequarter">
        ${species.description}
    </div>
    <div class="w3-quarter">
        <span id="imagesList">
            <p><img src="static/carousel-2.png" class="w3-card-2" style="max-width:230px"/></p>
        </span>
    </div>
</div>

<div id="authorsList" class="w3-dropdown-hover w3-left w3-padding-16" >
    <button class="w3-button" title="Нажмите для выбора ресурса"><span><img src="static/logo_pic - eGardening.png"></span>&emsp;<i class="fa fa-angle-double-down w3-large w3-text-grey"></i></button>
    <div class="w3-dropdown-content w3-bar-block w3-border" style="right:0">
        <a href="#" authorId="" onclick="selectAuthor('')" class="w3-bar-item w3-button w3-hover-light-gray w3-center w3-large w3-text-grey w3-padding-24" style="font-style: italic">Показать все методы</a>
        <a href="#" authorId="1" onclick="selectAuthor('1')" class="w3-bar-item w3-button w3-hover-light-gray" title="Системные методы eGardening.ru"><img src="static/logo_pic - eGardening.png"></a>
    </div>
</div>

<div class="w3-row w3-container w3-margin-bottom">
    <div>
        <ul id="userWorksList" class="w3-ul w3-left-align"></ul>
    </div>
</div>

<script type="text/javascript">
    var USER_WORKS_LIST = $('#userWorksList');

    document.afterInitializationUIEnvironmentFunc = initializePage;

    function initializePage() {
        refreshAuthorsList(function() {
            var authorId = getUrlVars()['authorId'];
            authorId = authorId ? authorId : '';
            selectAuthor(authorId)
        });
        refreshImagesList();
        COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.initialize({
            speciesName: '${species.name}',
            callback: function (userSpeciesId) {
                window.location.href = 'select_user_work_template.tiles?speciesId=' + userSpeciesId;
            }
        });
    }

    function refreshImagesList() {
        $.get(
            "api/data-storage/files?query=(associatedEntityType eq 'SPECIES' and associatedEntityId eq ${species.id})",
            function(items) {
                if(items.length>0) {
                    $("#imagesList").html("");
                }
                for(var i=0;i<items.length; i++) {
                    var item = items[i];
                    var imagePath = 'api/data-storage/' + item.id;
                    $("<p><img src='"+imagePath+"' class='w3-card-2' title='${species.name}' alt='${species.name}' style='max-width:230px'></p>")
                        .appendTo("#imagesList");
                }
            }
        )
    }

    function copySystemSpeciesToMy() {
        COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.display(${species.id})
    }

    function refreshAuthorsList(callback) {
        var AUTHORS_LIST = $('#authorsList > div');
        $.get(
                "/api/user/work/authors/system/",
                function(authors) {
                    AUTHORS_LIST.html('<a href="#" authorId="" onclick="selectAuthor(\'\')" class="w3-bar-item w3-button w3-hover-light-gray w3-center w3-large w3-text-grey w3-padding-24" style="font-style: italic">Показать все методы</a>');
                    for(var i=0; i<authors.length; i++) {
                        var author = authors[i];
                        $('<a href="#" authorId="'+author.id+'" onclick="selectAuthor(\''+author.id+'\')" class="w3-bar-item w3-button w3-hover-light-gray w3-center w3-large w3-text-grey w3-padding-24" style="font-style: italic">'
                                + '<img src="static/authors/'+author.id+'.png" title="'+author.name+'">'
                                + '</a>').appendTo(AUTHORS_LIST);
                    }
                    if(callback) {
                        callback();
                    }
                }
        );
    }

    function selectAuthor(authorId) {
        refreshGrowingOptions(authorId, function() {
            var button = $('#authorsList > button > span');
            var selectedItem = $('#authorsList > div > a[authorId="'+authorId+'"]');
            button.html(selectedItem.html());
        });
    }

    function refreshGrowingOptions(authorId, callback) {
        var url;
        if(authorId) {
            url = "/api/user/work/system/?pageNum=0&pageSize=100&order.patternName=asc&query=(pattern eq true and speciesId eq ${species.id} and authorId eq "+authorId+")";
        } else {
            url = "/api/user/work/system/?pageNum=0&pageSize=100&order.patternName=asc&query=(pattern eq true and speciesId eq ${species.id})";
        }

        $.get(
                url,
                function(items) {
                    USER_WORKS_LIST.html('');
                    if(items.length==0) {
                        $('<div class="w3-row m12 s12">\
                        <div class="w3-threequarter w3-hover-light-gray w3-margin-bottom" style="cursor: pointer">\
                                <img src="static/empty.jpg" class="w3-margin-right w3-left" style="width:80px;height:80px">\
                                <span class="w3-large">У Вас нет ни одного метода для данной культуры.</span>'
                                + '<br>\
                        <span class="w3-small">Вы можете добавить культуру в план и выбрать нужные работы и без использования метода. При этом, любой выбранный Вами список работ может быть сохранен как Ваш метод на будущее</span>\
                        </div>\
                        <div class="w3-quarter">\
                            <a href="add_system_species_work.tiles?speciesId=${species.id}" title="Добавить в мой план" \
                            class="w3-btn w3-theme-dark w3-hover-lime w3-padding-16 w3-large w3-mobile w3-margin-bottom">\
                            <span class="fa fa-plus w3-large w3-hide-large w3-hide-small"></span><span class="w3-hide-medium ">Хочу посадить!</span><span></span></a>\
                        </div>\
                        </div>').appendTo(USER_WORKS_LIST)
                        return;
                    }
                    for(var i=0; i<items.length; i++) {
                        var item = items[i];

                        $('<div class="w3-row m12 s12">\
                        <div class="w3-threequarter w3-hover-light-gray w3-margin-bottom" style="cursor: pointer">\
                                <img src="static/empty.jpg" class="w3-margin-right w3-left" style="width:80px;height:80px">\
                                <span class="w3-large">' + item.patternName + '</span>'
                                + '<br>\
                        <span class="w3-small">' + item.patternDescription + '</span>\
                        </div>\
                        <div class="w3-quarter">\
                            <a href="add_system_species_work.tiles?speciesId='+item.speciesId+'&userPatternId='+item.id+'" title="Добавить в мой план" \
                            class="w3-btn w3-theme-dark w3-hover-lime w3-padding-16 w3-large w3-mobile w3-margin-bottom">\
                            <span class="fa fa-plus w3-large w3-hide-large w3-hide-small"></span><span class="w3-hide-medium ">Хочу!</span><span></span></a>\
                        </div>\
                        </div>').appendTo(USER_WORKS_LIST);
                    }

                    if(callback) {
                        callback();
                    }
                }
        );
    }
</script>
