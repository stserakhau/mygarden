document.user = null;


function setUser(user) {
    document.user = user;
    if (sessionStorage) {
        sessionStorage.setItem("user", JSON.stringify(user));
    }
}

function getUser() {
    if (sessionStorage) {
        var sessionUser = sessionStorage.getItem("user");
        document.user = JSON.parse(sessionUser);
    }
    return document.user;
}

function setStorageAttr(name, value) {
    if (sessionStorage) {
        sessionStorage.setItem(name, value);
    } else {
        throw "Browser doesn't support Session Storage."
    }
}

function getStorageAttr(name) {
    if (sessionStorage) {
        return sessionStorage.getItem(name);
    }
    throw "Browser doesn't support Session Storage."
}

var SESSION_TIMEOUT = 30*60;//30 minutes
var sessionTimeoutCounter = SESSION_TIMEOUT;

function resetUserInSessionStore() {
    setUser(null);
    isAuthenticated();
}

document.loginDialog = null;
document.loaderPopup = null;
document.anonymousMessage = null;

$(document).ready(function () {
    initializeUIEnvironment();
});

function createLoginDialog() {
    var element = $('<div id="login" class="w3-modal w3-padding-48" style="display: none;z-index:10000;">\
        <form action="/api/auth/login" method="post" class="w3-modal-content w3-card-4 w3-center">\
            <input type="hidden" name="anonymousUsername" value=""/>\
            <header class="w3-container w3-theme w3-left-align">\
                <span onclick="$(\'#login\').hide();" class="w3-closebtn">&times;</span>\
                <h2>Вход</h2>\
            </header>\
            <div class="w3-container w3-left-align w3-margin-bottom">\
                <div id="loginFormWarnMsg" style="display: none;" class="w3-panel w3-leftbar w3-border-red w3-pale-red w3-card-2"><p></p></div>\
                <p>\
                    <label class="w3-label">Логин/E-mail<span class="w3-text-red">*</span></label>\
                    <input name="email" class="w3-input" type="email" required="required">\
                </p>\
                <p>\
                    <label class="w3-label">Пароль<span class="w3-text-red">*</span></label>\
                    <input name="password" required="required" class="w3-input" type="password"/>\
                </p>\
                <p>\
                    <a class="w3-text-blue w3-left" href="user_forgot_password.tiles">Забыли пароль?</a>\
                    <a class="w3-text-blue w3-right" href="user_registration.tiles">Регистрация</a>\
                </p>\
            </div>\
            <footer class="w3-container w3-theme">\
                <div class="w3-right-align w3-padding-16">\
                    <button type="button" onclick="$(\'#login\').hide();" class="w3-btn w3-grey">Отмена</button>\
                    <button type="submit" class="w3-btn w3-theme-dark">Войти</button>\
                </div>\
            </footer>\
        </form>\
    </div>');
    element.appendTo(document.body);
    return element;
}

function createLoaderPopup() {
    var element = $('<div id="loaderPopup" style="display: none;">\
        <div class="cssload-square"><div><div><div><div><div></div></div></div></div></div><div><div><div><div><div></div></div></div></div></div></div>\
        <div class="cssload-square cssload-two"><div><div><div><div><div></div></div></div></div></div><div><div><div><div><div></div></div></div></div></div></div></div>');
    element.appendTo(document.body);
    return element;
}

function createAnonymousMessage() {
    var element = $('<div name="warnMsg" class="w3-panel w3-leftbar w3-border-red w3-red w3-card-2 w3-left-align"\
     style="position:fixed;left:0;right:0;bottom:0;opacity:0.7;filter:progid:DXImageTransform.Microsoft.Alpha(opacity=90);">\
        <p>У Вас имеются несохраненные данные. <a href="#" class="w3-hover-text-blue" onclick="doAction(\'login\')">Войдите</a> или <a href="user_registration.tiles" class="w3-hover-text-blue">зарегистрируйтесь</a> для того, чтобы сохранить изменения.</p>\
    </div>');
    element.appendTo(document.body);
    return element;
}

function isAuthenticated() {
    $.ajax({
        type: "get",
        url: "api/auth/is_authenticated",
        success: function (user, status) {
            user = user == "" ? null : user;
            setUser(user == null ? null : user);
            var authUserRole = getUser() == null ? "" : user.role;
            applySecurityRole(authUserRole);
            if (document.afterInitializationUIEnvironmentFunc) {
                document.afterInitializationUIEnvironmentFunc();
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            applySecurityRole("");
            resetUserInSessionStore();
        }
    });
}

function initializeUIEnvironment() {
    document.loginDialog = createLoginDialog();
    document.loginDialog.hide();

    document.loaderPopup = createLoaderPopup();
    document.loaderPopup.hide();

    document.anonymousMessage = createAnonymousMessage();
    if (isAnonymous()) {
        document.anonymousMessage.show();
    } else {
        document.anonymousMessage.hide();
    }

    $(document).ajaxStart(function () {
        sessionTimeoutCounter = SESSION_TIMEOUT;
        document.loaderPopup.show();
        document.reloadPageOnError = false;
    }).ajaxSuccess(function (event, xhr, settings) {
        document.loaderPopup.hide();
    }).ajaxError(function (event, jqxhr, settings, thrownError) {
        document.loaderPopup.hide();

        if (document.reloadPageOnError) {
            document.location.reload();
        }
    });

    setInterval(function() {
        sessionTimeoutCounter--;
        if(sessionTimeoutCounter<=0) {
            logout();
        }
    }, 1000);

    if (getUser() == null) {
        isAuthenticated();
    } else {
        applySecurityRole(getUser().role);
        if (document.afterInitializationUIEnvironmentFunc) {
            document.afterInitializationUIEnvironmentFunc();
        }
    }

    var frm = document.loginDialog.find("form");
    frm.submit(function (ev) {
        $.ajax({
            type: "post",
            url: "/api/auth/login",
            data: frm.serialize(),
            success: function (user) {
                setUser(user);
                applySecurityRole(user.role);
                if (document.loginDialog.action && document.loginDialog.action != 'login') {
                    if (document.loginDialog.arg1 != null) {
                        document.loginDialog.action(document.loginDialog.arg1);
                    } else {
                        document.loginDialog.action();
                    }
                    document.loginDialog.hide();
                } else {
                    gotoMyWorkPlan()
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                var formMsg = frm.find("#loginFormWarnMsg");
                formMsg.html("<p>Неверное имя пользователя или пароль</p>");
                formMsg.show();
                setUser(null);
            }
        });

        return false;
    });
}

function applySecurityRole(role) {
    var isAnon = isAnonymous();
    var selector = "*[role]";
    $.each($(selector), function (index, value) {
        var anonymous = this.getAttribute("anonymous");

        var r = this.getAttribute("role");
        if (r == role) {
            $(this).show();
            if (isAnon && "hidden" == anonymous) {
                $(this).hide();
            }
        } else {
            $(this).hide();
            if (isAnon && "visible" == anonymous) {
                $(this).show();
            }
        }
    });
}

function doActionIfAnonCreateAndLogin(action, arg1) {
    if (getUser() != null) {
        if (isAnonymous()) {
            document.anonymousMessage.show();
        }
        if (action) {
            if (arg1) {
                action(arg1);
            } else {
                action();
            }
        }
    } else {
        $.ajax({
            type: "post",
            url: "/api/auth/createAnonymousAndLogin",
            data: {},
            success: function (user) {
                setUser(user);
                applySecurityRole(user.role);
                if (isAnonymous()) {
                    document.anonymousMessage.show();
                }
                if (action) {
                    if (arg1) {
                        action(arg1);
                    } else {
                        action();
                    }
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                setUser(null);
            }
        });
    }
}

function isAnonymous() {
    if (getUser() != null) {
        return getUser().email.indexOf('@') == -1;
    }
    return false;
}

function doAction(action, arg1) {
    if ('login' == action || getUser() == null) {
        document.loginDialog.action = action;
        document.loginDialog.arg1 = arg1 ? arg1 : null;
        if (isAnonymous()) {
            document.loginDialog.find("input[name='anonymousUsername']")
                .val(getUser().email);
        }
        document.loginDialog.show();
    } else {
        if (action) {
            if (arg1) {
                action(arg1);
            } else {
                action();
            }
        } else {
            gotoMyWorkPlan();
        }
    }
}

function gotoMyWorkPlan() {
    window.location.href = 'my_work_plan.tiles';
}

function logout() {
    setUser(null);
    $.post("api/auth/logout", function (data, status) {
        window.location.pathname = "index.jsp";
    });
}

// Side navigation
function openSideNav() {  //w3_open()
    var x = document.getElementById("mainSidenav");
    x.style.width = "100%";
    x.style.height = "100%";
    x.style.textAlign = "center";
    x.style.fontSize = "50px";
    x.style.paddingTop = "0%";
    x.style.display = "block";
    x.style.zIndex = "1000";
}
function closeSideNav() {  //w3_close()
    document.getElementById("mainSidenav").style.display = "none";
}
// hide navigation bar and replace with an icon
function hideNavBar() {   //w3_TBD()
    var x = document.getElementById("hiddenNavBar");
    if (x.className.indexOf("w3-show") == -1) {
        x.className += " w3-show";
    } else {
        x.className = x.className.replace(" w3-show", "");
    }
}

// Side filter on work plan
function openSideFilter() {  //w3_open()
    var x = document.getElementById("mainSideFilter");
    x.style.width = "100%";
    x.style.textAlign = "center";
    x.style.paddingTop = "0%";
    x.style.display = "block";
}
function closeSideFilter() {  //w3_close()
    $("#mainSideFilter").hide();
}
// hide filter panel and replace with an icon
function hideNavBar() {   //w3_TBD()
    var x = document.getElementById("hiddenNavBar");
    if (x.className.indexOf("w3-show") == -1) {
        x.className += " w3-show";
    } else {
        x.className = x.className.replace(" w3-show", "");
    }
}

function startPlannerWizard() {
    alert('Planner Wizard Started!!!');
}

function getUrlVars() {
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for (var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1].replace('#', '');
    }
    return vars;
}

var level = -1;
function populateFormFromJsonTree(form, json, path) {
    if (!path) {
        path = "";
    }
    level++;
    for (var prop in json) {
        var value = json[prop];
        var propType = typeof value;
        var pth = path + (level > 0 ? '.' : '') + prop;
        if (propType === "object") {
            populateFormFromJsonTree(form, value, pth)
        } else {
            $.each(form.find("*[name='" + pth + "']"), function (i, obj) {
                switch (obj.tagName) {
                    case "UL" :
                        $(obj).attr("value", value);
                        break;
                    case "INPUT" :
                        var type = obj.getAttribute("type");
                        switch(type) {
                            case "radio":
                                var val = $(obj).attr("value");
                                $(obj).prop("checked", val == value)
                                break;
                            default:
                                $(obj).val(value);
                                break;
                        }
                        break;
                    default:
                        $(obj).val(value);
                        break;
                }
            });
        }
    }
    level--;
}
function serializeFormToJsonTree(form) {
    var json = {};

    $.each(form.find("*[name]"), function (i, el) {
        if ($(el).attr("disabled") == 'disabled') {
            return;
        }
        var path = $(el).attr("name");

        var value;
        switch (el.tagName) {
            case "INPUT":
                switch (el.type) {
                    case "radio":
                        if (el.checked) {
                            value = $(el).val();
                        } else {
                            return;
                        }
                        break;
                    case "checkbox":
                        value = el.checked;
                        break;
                    default:
                        value = $(el).val();
                }
                break;
            case "TEXTAREA":
                value = $(el).val();
                break;
            case "SELECT":
                value = $(el).val();
                break;
            default :
                value = $(el).attr("value");
        }
        if(value) {
            if (value.trim) {
                value = value.trim();
            }
        } else {
            value = '';
        }

        if (el.getAttribute("required") != null && value == '') {
            json.validation = "required";
        }

        var gtId = el.getAttribute("greaterThanId");
        if (gtId != null && value != '') {
            var startDt = document.getElementById(gtId).value;
            var endDt = el.value;

            if(new Date(startDt).getTime() > new Date(endDt).getTime()) {
                json.validation = "shouldBeGreaterThan_" + gtId;
            }
        }
        var propertyPath = path.split(".");
        var rootJson = json;
        for (var i = 0; i < propertyPath.length; i++) {
            var isLast = propertyPath.length - 1 == i;
            var prop = propertyPath[i];

            if (isLast) {
                rootJson[prop] = value;
            } else {
                if (!rootJson[prop]) {
                    rootJson[prop] = {};
                }
                rootJson = rootJson[prop];
            }
        }
    });

    return json;
}

function loadCountries(countryInputListId) {
    var country = $("#" + countryInputListId);
    var countries = $("#" + country.attr("list"));
    if (countries) {
        countries.find("*").remove();
        $.get(
            'api/regions/countries/',
            function (items) {
                $.each(items, function (i, item) {
                    $('<option>', {
                        value: item.name,
                        text: item.name
                    }).appendTo(countries);
                });
            }
        )
    }
}

function loadCities(cityInputListId, countryName) {
    var city = $("#" + cityInputListId);
    var cities = $("#" + city.attr("list"));
    cities.find("*").remove();
    $.get(
        'api/regions/citiesByCountry/?countryName=' + countryName,
        function (items) {
            $.each(items, function (i, item) {
                $('<option>', {
                    value: item.name,
                    text: item.name + '('+item.region+')'
                }).appendTo(cities);
            });
        }
    )
}

/*==============================================================================*/
/*=====================    EDIT SPECIES FORM    ================================*/
/*==============================================================================*/
var EDIT_SPECIES_WINDOW = $(
    '<div class="w3-modal w3-padding-48">\
    <form class="w3-modal-content w3-card-4 w3-center">\
    <header class="w3-container w3-theme w3-left-align">\
        <span action="close" class="w3-closebtn">&times;</span>\
        <h2>Добавить культуру</h2>\
    </header>\
    <div class="w3-container w3-left-align">\
    <!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2">\
    <p>Выберите класс растений или введите новый и укажите название культуры.  <a class="w3-text-blue" href="help_edit_species.tiles">Подробнее...</a></p>\
    </div-->\
    <div name="errorMsg" class="w3-panel w3-leftbar w3-border-red w3-pale-red w3-card-2"></div>\
    <p><label class="w3-label">Класс растений<span class="w3-text-red">*</span></label>\
        <input name="speciesCatalog.name" list="speciesCategories" class="w3-select" required="required" placeholder="Выберите класс, куда будет добавлено новое растение">\
        <datalist id="speciesCategories"></datalist>\
    </p>\
    <input type="hidden" name="species.id"/>\
    <p><label class="w3-label">Название культуры<span class="w3-text-red">*</span></label>\
        <input name="species.name" class="w3-input" type="text" required="required"></p>\
    <p><label class="w3-label">Описание</label>\
        <textarea name="species.description" rows="6" class="w3-input"></textarea></p>\
    <p><input class="w3-check" id="species_options" type="checkbox">\
    <label class="w3-label">Цветение c </label><input name="species.options.floweringFrom" id="floweringFrom" type="date" required="" disabled>&emsp;\
    <label class="w3-label">по </label><input name="species.options.floweringTo" id="floweringTo" greaterThanId="floweringFrom" required="" type="date" disabled></p>\
    <p class="w3-small w3-text-grey">*Указанное время цветения будет использоваться при планировании клумб непрерывного цветения.</p>\
    </div>\
    <footer class="w3-container w3-theme">\
    <div class="w3-right-align w3-padding-16">\
    <button action="close" type="button" class="w3-btn w3-grey">Отмена</button>\
    <button type="submit" class="w3-btn w3-theme-dark">Сохранить</button>\
    </div></footer></form></div>');

EDIT_SPECIES_WINDOW.initialize = function() {
    EDIT_SPECIES_WINDOW.appendTo(document.body);
    EDIT_SPECIES_WINDOW.find("*[action='close']").click(function(){
        EDIT_SPECIES_WINDOW.hide();
    });
    var floweringFrom = EDIT_SPECIES_WINDOW.floweringFrom = EDIT_SPECIES_WINDOW.find("input[name='species.options.floweringFrom']");
    var floweringTo = EDIT_SPECIES_WINDOW.floweringTo = EDIT_SPECIES_WINDOW.find("input[name='species.options.floweringTo']");

    EDIT_SPECIES_WINDOW.find("input[id='species_options']").change(function(){
        if($(this).prop("checked")) {
            floweringFrom.prop("disabled", "");
            floweringTo.prop("disabled", "");
        } else {
            floweringFrom.prop("disabled", "disabled");
            floweringTo.prop("disabled", "disabled");
        }
    });
    floweringFrom.prop("disabled", "disabled");

    EDIT_SPECIES_WINDOW.find("form").submit(function () {
        var json = serializeFormToJsonTree($(this));

        if(json.validation) {
            EDIT_SPECIES_WINDOW.processError({responseJSON:{message:"error.species." + json.validation}},null,null);
            return false;
        }

        $.ajax({
            type: "post",
            url: "api/species/",
            data: JSON.stringify(json),
            contentType: "application/json; charset=utf-8",
            success: function (species) {
                location.reload();
            },
            error: EDIT_SPECIES_WINDOW.processError
        });

        return false;
    });
    $.get(
        "api/species/catalog/my",
        function (items) {
            var speciesCategoriesList = EDIT_SPECIES_WINDOW.find("#speciesCategories");
            for (var i = 0; i < items.length; i++) {
                var item = items[i];
                $("<option value='" + item.name + "'>" + item.name + "</option>").appendTo(speciesCategoriesList);
            }
        }
    );
};
EDIT_SPECIES_WINDOW.processError = function(jqXHR, textStatus, errorThrown) {
    document.reloadPageOnError = false;
    var addSpeciesErrorFormMsg = EDIT_SPECIES_WINDOW.find("div[name='errorMsg']");
    var addSpeciesInfoFormMsg = EDIT_SPECIES_WINDOW.find("div[name='infoMsg']");

    var msg = jqXHR.responseJSON.message;

    switch(msg) {
        case "error.species.exists":
            msg = "Такая культура уже есть в Вашей библиотеке";
            break;
        case "error.species.required":
            msg = "Пожалуйста, заполните все необходимые поля.";
            break;
        case "error.species.shouldBeGreaterThan_floweringFrom":
            msg = "Неверно задано время цветения";
            break;
    }
    addSpeciesInfoFormMsg.hide();
    addSpeciesErrorFormMsg.html("<p>"+msg+"</p>");
    addSpeciesErrorFormMsg.slideDown("fast");
};
EDIT_SPECIES_WINDOW.edit = function(speciesId) {
    var form = EDIT_SPECIES_WINDOW.find("form");
    var addSpeciesErrorFormMsg = EDIT_SPECIES_WINDOW.find("div[name='errorMsg']");
    var addSpeciesInfoFormMsg = EDIT_SPECIES_WINDOW.find("div[name='infoMsg']");
    addSpeciesErrorFormMsg.hide();
    addSpeciesInfoFormMsg.show();

    if(speciesId == null) {
        populateFormFromJsonTree(form, {speciesCatalog: {name: ""}, species: {id:"", name: "", description: "", options:{}}});
        EDIT_SPECIES_WINDOW.find("header > h2").html("Добавить культуру");
        EDIT_SPECIES_WINDOW.show();
        EDIT_SPECIES_WINDOW.find("input[name='speciesCatalog.name']").focus();
    } else {
        $.get(
            'api/species/'+speciesId,
            function(data) {
                EDIT_SPECIES_WINDOW.find("header > h2").html("Редактировать культуру");
                if(data.species.options && (data.species.options.floweringFrom || data.species.options.floweringTo)){
                    EDIT_SPECIES_WINDOW.find("input[id='species_options']").prop("checked", true).change();
                }
                populateFormFromJsonTree(form, data);
//                EDIT_SPECIES_WINDOW.floweringTo.prop("min",  EDIT_SPECIES_WINDOW.floweringFrom.prop("value"));
//                EDIT_SPECIES_WINDOW.floweringFrom.prop("max",  EDIT_SPECIES_WINDOW.floweringTo.prop("value"));
                EDIT_SPECIES_WINDOW.show();
                EDIT_SPECIES_WINDOW.find("input[name='speciesCatalog.name']").focus();
            }
        );
    }
};

/*==============================================================================*/
/*======================= COPY SYSTEM SPECIES TO MY LIB ========================*/
/*==============================================================================*/
var COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW = $(
    '<form class="w3-modal w3-padding-48">\
        <input type="hidden" name="overwriteSpeciesIfExist" value="false"/>\
        <div class="w3-modal-content w3-card-4 w3-center">\
        <header class="w3-container w3-theme w3-left-align">\
            <span action="close" class="w3-closebtn">&times;</span>\
            <h2>Копировать культуру</h2>\
        </header>\
        <div class="w3-container w3-left-align w3-padding">\
        <!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2">\
            <p>Пожалуйста, выберите класс для копирования в своей библиотеке и измените название культуры, если\
            необходимо. <a class="w3-text-blue" href="help_copy_species.tiles">Подробнее...</a></p>\
        </div-->\
        <div name="errorMsg" class="w3-panel w3-leftbar w3-border-red w3-pale-red w3-card-2"></div>\
        <p><label class="w3-label">Название класса<span class="w3-text-red">*</span></label>\
        <input name="speciesCatalog.name" list="speciesCategories" class="w3-select" required="required" placeholder="Выберите класс в Вашей библиотеке для копирования растения">\
        <datalist id="speciesCategories"></datalist>\
        <p><label class="w3-label">Название культуры<span class="w3-text-red">*</span></label>\
            <input name="species.name" class="w3-input w3-border" type="text" value="" required="required"></p>\
        </div>\
        <footer class="w3-container w3-theme">\
            <div class="w3-right-align w3-padding-16">\
                <button type="button" action="close" class="w3-btn w3-grey">Отмена</button>\
                <button type="submit" class="w3-btn w3-theme-dark">Копировать</button>\
            </div>\
        </footer></div>\
    </form>');

COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.CONFIRM_COPY = $(
    '<div class="w3-modal w3-padding-48">\
        <div class="w3-modal-content w3-card-4 w3-center">\
        <header class="w3-container w3-theme w3-left-align">\
            <span action="close" class="w3-closebtn">&times;</span>\
            <h2><span class="fa fa-warning w3-text-red"></span></h2>\
        </header>\
        <div class="w3-container w3-left-align">\
        <p><b><oldSpeciesName>"название_культуры"</oldSpeciesName></b> уже есть в Вашей библиотеке. Копировать в нее наши методы или Вы измените название?\
        </div>\
    <footer class="w3-container w3-theme">\
        <div class="w3-right-align w3-padding-16">\
            <button type="button" action="close" class="w3-btn w3-grey">Изменю</button>\
            <button type="button" onclick="COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.submitOverwrite()" class="w3-btn w3-theme-dark">Копировать</button>\
        </div>\
    </footer>\
    </div></div>');
COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.CONFIRM_COPY.display = function (speciesName) {
    COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.CONFIRM_COPY.find("speciesName").html(speciesName);
    COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.CONFIRM_COPY.show();
};


COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.initialize = function (config) {
    COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.config = config;
    COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.appendTo(document.body);
    COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.CONFIRM_COPY.appendTo(document.body);
    COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.find("*[action='close']").click(function () {
        COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.hide();
    });
    COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.CONFIRM_COPY.find("*[action='close']").click(function () {
        COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.CONFIRM_COPY.hide();
    });

    COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.submit(function () {
        var json = serializeFormToJsonTree($(this));

        if(json.validation) {
            COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.processError({responseJSON:{message:"error.species.required"}},null,null);
            return false;
        }

        doActionIfAnonCreateAndLogin(function () {
            $.ajax({
                type: "post",
                url: "api/species/copySystemToMy/" + COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.systemSpeciesId,
                data: JSON.stringify(json),
                contentType: "application/json; charset=utf-8",
                success: function (mySpeciesId) {
                    window.location.href = "select_user_work_template.tiles?backTo=system&speciesId=" + mySpeciesId;
                },
                error: COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.processError
            });
        });

        return false;
    });
};
COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.submitOverwrite = function (){
    COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.find("input[name='overwriteSpeciesIfExist']").val("true");
    COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.submit();
};
COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.processError = function(jqXHR, textStatus, errorThrown) {
    document.reloadPageOnError = false;
    var addSpeciesErrorFormMsg = COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.find("div[name='errorMsg']");
    var addSpeciesInfoFormMsg = COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.find("div[name='infoMsg']");

    var msg = jqXHR.responseJSON.message;

    switch(msg) {
        case "error.species.exists":
            var newSpeciesName = COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.find("input[name='species.name']").val();
            COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.CONFIRM_COPY.display(newSpeciesName);
            return;
        case "error.species.required":
            msg = "Пожалуйста, заполните все необходимые поля.";
            break;
    }
    addSpeciesInfoFormMsg.hide();
    addSpeciesErrorFormMsg.html("<p>"+msg+"</p>");
    addSpeciesErrorFormMsg.show();
};
COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.display = function (systemSpeciesId) {
    $.get(
        "api/species/catalog/my",
        function (items) {
            var speciesCategoriesList = COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.find("#speciesCategories");
            speciesCategoriesList.html('');
            for (var i = 0; i < items.length; i++) {
                var item = items[i];
                $("<option value='" + item.name + "'>" + item.name + "</option>").appendTo(speciesCategoriesList);
            }
        }
    );

    COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.systemSpeciesId = systemSpeciesId;

    var speciesName = COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.config.speciesName;
    COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.find("input[name='overwriteSpeciesIfExist']").val("false");
    COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.find("speciesName").html(speciesName);
    COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.CONFIRM_COPY.find("oldSpeciesName").html(speciesName);
    COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.find("input[name='species.name']").val(speciesName);

    COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.find("div[name='errorMsg']").hide();
    COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.find("div[name='infoMsg']").show();

    COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.show();
};

COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.copySystemSpeciesToMyLib = function () {
    var systemSpeciesId = COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.attr("systemSpeciesId");
    $.ajax({
        url: "/api/species/copySystemToMy/" + systemSpeciesId,
        type: "put",
        success: function (userSpeciesId) {
            if (COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.config.callback) {
                COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.config.callback(userSpeciesId);
            }
            COPY_SYSTEM_SPECIES_TO_MY_LIB_WINDOW.hide();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert(jqXHR.responseJSON.message);
        }
    });
};

/*==============================================================================*/
/*==================    EDIT SPECIES CLASS FORM    =============================*/
/*==============================================================================*/
var EDIT_SPECIES_CLASS_WINDOW = $('<form class="w3-modal w3-padding-48">\
    <input type="hidden" name="id"/>\
    <div class="w3-modal-content w3-card-4 w3-center">\
    <header class="w3-container w3-theme w3-left-align">\
    <span action="close" class="w3-closebtn">&times;</span><h2>Добавить класс</h2></header>\
<div class="w3-container w3-left-align w3-padding">\
    <!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2">\
    <p>Введите или отредактируйте название класса. Обратите внимание, вместо семейства (пасленовые, крестоцветные) можно использовать любую метку. Например, "Теплица 1", "Теплица 2", "Центральная клумба", "Палисадник", и т.д. <a class="w3-text-blue" href="help_edit_species_class.tiles">Подробнее...</a></p>\
    </div-->\
    <div name="errorMsg" class="w3-panel w3-leftbar w3-border-red w3-pale-red w3-card-2"></div>\
    <p><label class="w3-label">Название класса<span class="w3-text-red">*</span></label>\
    <input name="name" class="w3-input w3-border" type="text" value="" required="required"></p>\
    </div>\
    <footer class="w3-container w3-theme">\
    <div class="w3-right-align w3-padding-16">\
    <button onclick="EDIT_SPECIES_CLASS_WINDOW.DELETE_FORM.open()" name="deleteBtn" type="button" class="w3-btn w3-grey w3-hover-red w3-left">Удалить</button>\
    <button action="close" type="button" class="w3-btn w3-grey">Отмена</button>\
    <button type="submit" class="w3-btn w3-theme-dark ">Сохранить</button>\
    </div></footer></div></form>');
EDIT_SPECIES_CLASS_WINDOW.DELETE_FORM = $(
    '<form class="w3-modal w3-padding-48">\
    <div class="w3-modal-content w3-card-4 w3-center"><header class="w3-container w3-theme w3-left-align">\
    <span action="close" class="w3-closebtn">&times;</span><h2><span class="fa fa-question-circle w3-text-white"></span></h2></header>\
    <div class="w3-container w3-left-align"><p>Вы действительно хотите удалить класс "<b><className>Название Класса</className></b>"?</p></div>\
    <footer class="w3-container w3-theme">\
    <div class="w3-right-align w3-padding-16">\
    <button action="close" type="button" class="w3-btn w3-grey">Отмена</button>\
    <button type="submit" class="w3-btn w3-theme-dark">Удалить</button>\
    </div></footer></div></form>');

EDIT_SPECIES_CLASS_WINDOW.initialize = function(){
    EDIT_SPECIES_CLASS_WINDOW.appendTo(document.body);
    EDIT_SPECIES_CLASS_WINDOW.DELETE_FORM.appendTo(document.body);
    EDIT_SPECIES_CLASS_WINDOW.find("*[action='close']").click(function(){
        EDIT_SPECIES_CLASS_WINDOW.hide();
    });
    EDIT_SPECIES_CLASS_WINDOW.DELETE_FORM.find("*[action='close']").click(function(){
        EDIT_SPECIES_CLASS_WINDOW.DELETE_FORM.hide();
    });
    EDIT_SPECIES_CLASS_WINDOW.find("div[name='infoMsg']").show();
    EDIT_SPECIES_CLASS_WINDOW.find("div[name='errorMsg']").hide();
    EDIT_SPECIES_CLASS_WINDOW.submit(function () {
        var url = "api/species/catalog/";
        var type = "POST";
        var json = serializeFormToJsonTree($(this));

        if(json.validation) {
            EDIT_SPECIES_CLASS_WINDOW.processError({responseJSON:{message:"error.speciesCatalog.required"}},null,null);
            return false;
        }
        if (json.id) {
            type = "PUT";
            url += json.id;
        }
        $.ajax({
            type: type,
            url: url,
            data: JSON.stringify(json),
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                location.reload();
            },
            error: EDIT_SPECIES_CLASS_WINDOW.processError
        });
        return false;
    });
    EDIT_SPECIES_CLASS_WINDOW.DELETE_FORM.submit(function(){
        var json = serializeFormToJsonTree(EDIT_SPECIES_CLASS_WINDOW);

        $.ajax({
            type: "DELETE",
            url: "api/species/catalog/" + json.id,
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                location.reload();
            },
            error: EDIT_SPECIES_CLASS_WINDOW.processError
        });

        return false;
    });
};
EDIT_SPECIES_CLASS_WINDOW.processError = function(jqXHR, textStatus, errorThrown){
    document.reloadPageOnError = false;
    EDIT_SPECIES_CLASS_WINDOW.DELETE_FORM.hide();
    var msg = jqXHR.responseJSON.message;

    switch(msg) {
        case "error.speciesCatalog.exists":
            msg = "Такой класс уже есть в Вашей библиотеке";
            break;
        case "error.speciesCatalog.required":
            msg = "Пожалуйста, заполните все необходимые поля.";
            break;
    }
    EDIT_SPECIES_CLASS_WINDOW.find("div[name='infoMsg']").hide();
    EDIT_SPECIES_CLASS_WINDOW.find("div[name='errorMsg']")
        .html("<p>"+msg+"</p>").slideDown("fast");
};
EDIT_SPECIES_CLASS_WINDOW.edit = function(speciesClassId){
    var delBtn = EDIT_SPECIES_CLASS_WINDOW.find("button[name='deleteBtn']");
    EDIT_SPECIES_CLASS_WINDOW.find("div[name='infoMsg']").show();
    EDIT_SPECIES_CLASS_WINDOW.find("div[name='errorMsg']").hide();
    if (speciesClassId != null) {
        EDIT_SPECIES_CLASS_WINDOW.find("header h2").html("Редактировать класс")
        $.get(
            '/api/species/catalog/' + speciesClassId,
            function (data) {
                populateFormFromJsonTree(EDIT_SPECIES_CLASS_WINDOW, data);
                EDIT_SPECIES_CLASS_WINDOW.DELETE_FORM
                    .find("className")
                    .html(data.name);
                delBtn.show();
                EDIT_SPECIES_CLASS_WINDOW.show();
                EDIT_SPECIES_CLASS_WINDOW.find("input[name='name']").focus();
            }
        );
    } else {
        EDIT_SPECIES_CLASS_WINDOW.find("header h2").html("Добавить класс")
        var def = {
            "id": "",
            "name": ""
        };
        populateFormFromJsonTree(EDIT_SPECIES_CLASS_WINDOW, def);
        delBtn.hide();
        EDIT_SPECIES_CLASS_WINDOW.show();
        EDIT_SPECIES_CLASS_WINDOW.find("input[name='name']").focus();
    }
};
EDIT_SPECIES_CLASS_WINDOW.DELETE_FORM.open = function() {
    EDIT_SPECIES_CLASS_WINDOW.DELETE_FORM.show();
};

/*==============================================================================*/
/*========================    EDIT USER WORK TEMPLATE  =========================*/
/*==============================================================================*/
var EDIT_USER_WORK_TEMPLATE_WINDOW = $(
    '<form class="w3-modal w3-padding-48">\
    <input type="hidden" name="id"/>\
    <input type="hidden" name="speciesId"/>\
    <input type="hidden" name="pattern" value="true"/>\
    <div class="w3-modal-content w3-card-4 w3-center">\
    <header class="w3-container w3-theme w3-left-align">\
    <span action="close" class="w3-closebtn">&times;</span>\
    <h2>Редактировать мой метод</h2></header>\
    <div class="w3-container w3-left-align w3-padding">\
    <!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2"><p>Укажите название метода и описание. Выберите, как Вам удобнее задать время выполнения работы и укажите, когда Вы планируете выполнять работу для культуры. Запишите свои пометки к выполнению работы. <a class="w3-text-blue" href="help_add_template.tiles">Подробнее...</a></p></div-->\
    <div name="errorMsg" class="w3-panel w3-leftbar w3-border-red w3-pale-red w3-card-2"></div>\
    <p><label class="w3-label">Название метода:<span class="w3-text-red">*</span></label>\
    <input class="w3-input w3-border" type="text" name="patternName" required="required" placeholder="Например: Уход за виноградом по Декабревой"/>\
    </p>\
    <p><label class="w3-label">Описание:</label>\
    <textarea name="patternDescription" rows="6" class="w3-select w3-border-theme" placeholder="Здесь запишите краткое описание метода, пометьте автора, или добавьте ссылку на статью"></textarea>\
    </p>\
    </div>\
    <footer class="w3-container w3-theme"><div class="w3-right-align w3-padding-16">\
    <button type="button" action="close" class="w3-btn w3-grey">Отмена</button>\
    <button type="submit" class="w3-btn w3-theme-dark">Сохранить</button>\
    </div></footer></div></form>');

EDIT_USER_WORK_TEMPLATE_WINDOW.initialize = function(speciesId) {
    EDIT_USER_WORK_TEMPLATE_WINDOW.speciesId = speciesId;
    EDIT_USER_WORK_TEMPLATE_WINDOW.find("input[name='speciesId']").val(EDIT_USER_WORK_TEMPLATE_WINDOW.speciesId);
    EDIT_USER_WORK_TEMPLATE_WINDOW.appendTo(document.body);
    EDIT_USER_WORK_TEMPLATE_WINDOW.find("*[action='close']").click(function(){
        EDIT_USER_WORK_TEMPLATE_WINDOW.hide();
    });
    EDIT_USER_WORK_TEMPLATE_WINDOW.submit(function () {
        var goJson = serializeFormToJsonTree($(this));

        if(goJson.validation) {
            EDIT_USER_WORK_TEMPLATE_WINDOW.processError({responseJSON:{message:"error.go.required"}},null,null);
            return false;
        }
        $.ajax({
            type: "POST",
            url: "/api/user/work/pattern",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(goJson),
            success: function(data) {
                location.href = 'edit_user_work_template.tiles?userWorkId=' + data;
            },
            error: EDIT_USER_WORK_TEMPLATE_WINDOW.processError
        });
        return false;
    });
};
EDIT_USER_WORK_TEMPLATE_WINDOW.processError = function(jqXHR, textStatus, errorThrown) {
    document.reloadPageOnError = false;
    var addGOTemplateErrorFormMsg = EDIT_USER_WORK_TEMPLATE_WINDOW.find("div[name='errorMsg']");
    var addGOTemplateInfoFormMsg = EDIT_USER_WORK_TEMPLATE_WINDOW.find("div[name='infoMsg']");

    var msg = jqXHR.responseJSON.message;

    switch(msg) {
        case "error.pattern.exists":
            msg = "Такой метод для культуры уже существует";
            break;
        case "error.go.required":
            msg = "Пожалуйста, заполните все необходимые поля.";
            break;
    }
    addGOTemplateInfoFormMsg.hide();
    addGOTemplateErrorFormMsg.html("<p>"+msg+"</p>");
    addGOTemplateErrorFormMsg.slideDown("fast");
};
EDIT_USER_WORK_TEMPLATE_WINDOW.edit = function (userWorkId) {
    if(userWorkId==null) {
        EDIT_USER_WORK_TEMPLATE_WINDOW.find("header h2").html("Добавить мой метод");
    } else {
        EDIT_USER_WORK_TEMPLATE_WINDOW.find("header h2").html("Редактировать мой метод");
    }

    var addUWTemplateErrorFormMsg = EDIT_USER_WORK_TEMPLATE_WINDOW.find("div[name='errorMsg']");
    var addUWTemplateInfoFormMsg = EDIT_USER_WORK_TEMPLATE_WINDOW.find("div[name='infoMsg']");
    if(userWorkId == null) {
        populateFormFromJsonTree(EDIT_USER_WORK_TEMPLATE_WINDOW, {id:"", speciesId: EDIT_USER_WORK_TEMPLATE_WINDOW.speciesId, patternName:"", patternDescription:""});
        addUWTemplateErrorFormMsg.hide();
        addUWTemplateInfoFormMsg.show();
        EDIT_USER_WORK_TEMPLATE_WINDOW.show();
        EDIT_USER_WORK_TEMPLATE_WINDOW.find("input[name='name']").focus();
    } else {
        $.get(
            "/api/user/work/" + userWorkId,
            function (userWork) {
                populateFormFromJsonTree(EDIT_USER_WORK_TEMPLATE_WINDOW, userWork);
                addUWTemplateErrorFormMsg.hide();
                addUWTemplateInfoFormMsg.show();
                EDIT_USER_WORK_TEMPLATE_WINDOW.show();
                EDIT_USER_WORK_TEMPLATE_WINDOW.find("input[name='name']").focus();
            }
        );
    }
};
/*==============================================================================*/
/*========================    COPY USER WORK TEMPLATE  =========================*/
/*==============================================================================*/
var COPY_USER_WORK_TEMPLATE_WINDOW = $(
    '<form class="w3-modal w3-padding-48">\
    <input type="hidden" name="id"/>\
    <input type="hidden" name="speciesId"/>\
    <input type="hidden" name="pattern" value="true"/>\
    <div class="w3-modal-content w3-card-4 w3-center">\
    <header class="w3-container w3-theme w3-left-align">\
    <span action="close" class="w3-closebtn">&times;</span>\
    <h2>Копировать метод</h2></header>\
    <div class="w3-container w3-left-align w3-padding">\
    <!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2"><p>Введите название для нового метода. Все работы и удобрения будут скопированы в новый метод и доступны для редактирования. <a class="w3-text-blue" href="help_copy_template.tiles">Подробнее...</a></p></div-->\
    <div name="errorMsg" class="w3-panel w3-leftbar w3-border-red w3-pale-red w3-card-2"></div>\
    <p><label class="w3-label">Культура:<span class="w3-text-red">*</span></label>\
    <select class="w3-input w3-border" name="speciesId" required="required"/>\
    </p>\
    <p><label class="w3-label">Имя метода:<span class="w3-text-red">*</span></label>\
    <input class="w3-input w3-border" type="text" name="patternName" required="required"/>\
    </p>\
    <p><label class="w3-label">Описание:</label>\
    <textarea name="patternDescription" rows="6"  class="w3-select w3-border-theme"></textarea>\
    </p></div>\
    <footer class="w3-container w3-theme"><div class="w3-right-align w3-padding-16">\
    <button type="button" action="close" class="w3-btn w3-grey">Отмена</button>\
    <button type="submit" class="w3-btn w3-theme-dark">Сохранить</button>\
    </div></footer></div></form>');

COPY_USER_WORK_TEMPLATE_WINDOW.initialize = function(config) {
    COPY_USER_WORK_TEMPLATE_WINDOW.config = config.speciesId;
    COPY_USER_WORK_TEMPLATE_WINDOW.find("input[name='speciesId']").val(COPY_USER_WORK_TEMPLATE_WINDOW.config.speciesId);
    COPY_USER_WORK_TEMPLATE_WINDOW.appendTo(document.body);
    COPY_USER_WORK_TEMPLATE_WINDOW.find("*[action='close']").click(function(){
        COPY_USER_WORK_TEMPLATE_WINDOW.hide();
    });
    COPY_USER_WORK_TEMPLATE_WINDOW.submit(function () {
        var goJson = serializeFormToJsonTree($(this));

        if(goJson.validation) {
            COPY_USER_WORK_TEMPLATE_WINDOW.processError({responseJSON:{message:"error.go.required"}},null,null);
            return false;
        }
        var actionURL = "/api/user/work/pattern/clone";
        $.ajax({
            type: "POST",
            url: actionURL,
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(goJson),
            success: function(userWork) {
                location.href = 'select_user_work_template.tiles?speciesId=' + userWork.speciesId;
            },
            error: COPY_USER_WORK_TEMPLATE_WINDOW.processError
        });
        return false;
    });
    $.get(
        "api/species/my?pageNum=0&pageSize=1000&order.name=asc",
        function (items) {
            var mySpeciesList = COPY_USER_WORK_TEMPLATE_WINDOW.find("select[name='speciesId']");
            for (var i = 0; i < items.length; i++) {
                var item = items[i];
                $("<option value='" + item.id + "'>" + item.name + "</option>").appendTo(mySpeciesList);
            }
        }
    );
};
COPY_USER_WORK_TEMPLATE_WINDOW.processError = function(jqXHR, textStatus, errorThrown) {
    document.reloadPageOnError = false;
    var addGOTemplateErrorFormMsg = COPY_USER_WORK_TEMPLATE_WINDOW.find("div[name='errorMsg']");
    var addGOTemplateInfoFormMsg = COPY_USER_WORK_TEMPLATE_WINDOW.find("div[name='infoMsg']");

    var msg = jqXHR.responseJSON.message;

    switch(msg) {
        case "error.pattern.exists":
            msg = "Такой метод для культуры уже существует";
            break;
        case "error.go.required":
            msg = "Пожалуйста, заполните все необходимые поля.";
            break;
    }
    addGOTemplateInfoFormMsg.hide();
    addGOTemplateErrorFormMsg.html("<p>"+msg+"</p>");
    addGOTemplateErrorFormMsg.slideDown("fast");
};
COPY_USER_WORK_TEMPLATE_WINDOW.editAsTemplate = function (userWorkId) {
    COPY_USER_WORK_TEMPLATE_WINDOW.find("header h2").html("Сохранить как мой метод");
    var addUWTemplateErrorFormMsg = COPY_USER_WORK_TEMPLATE_WINDOW.find("div[name='errorMsg']");
    var addUWTemplateInfoFormMsg = COPY_USER_WORK_TEMPLATE_WINDOW.find("div[name='infoMsg']");
    $.get(
        "/api/user/work/" + userWorkId,
        function (userWork) {
            userWork.patternDescription = "";
            userWork.pattern = true;
            populateFormFromJsonTree(COPY_USER_WORK_TEMPLATE_WINDOW, userWork);
            addUWTemplateErrorFormMsg.hide();
            addUWTemplateInfoFormMsg.show();
            COPY_USER_WORK_TEMPLATE_WINDOW.show();
            COPY_USER_WORK_TEMPLATE_WINDOW.find("input[name='name']").focus();
        }
    );
};
COPY_USER_WORK_TEMPLATE_WINDOW.edit = function (userWorkId) {
    COPY_USER_WORK_TEMPLATE_WINDOW.find("header h2").html("Копировать метод");
    var addUWTemplateErrorFormMsg = COPY_USER_WORK_TEMPLATE_WINDOW.find("div[name='errorMsg']");
    var addUWTemplateInfoFormMsg = COPY_USER_WORK_TEMPLATE_WINDOW.find("div[name='infoMsg']");
    $.get(
        "/api/user/work/" + userWorkId,
        function (userWork) {
            userWork.patternDescription = "";
            userWork.pattern = true;
            populateFormFromJsonTree(COPY_USER_WORK_TEMPLATE_WINDOW, userWork);
            addUWTemplateErrorFormMsg.hide();
            addUWTemplateInfoFormMsg.show();
            COPY_USER_WORK_TEMPLATE_WINDOW.show();
            COPY_USER_WORK_TEMPLATE_WINDOW.find("input[name='name']").focus();
        }
    );
};

/*==============================================================================*/
/*==================    EDIT FERTILIZER CLASS FORM    ==========================*/
/*==============================================================================*/
var EDIT_FERTILIZER_CATALOG_WINDOW = $('<form class="w3-modal w3-padding-48">\
    <input type="hidden" name="id"/>\
    <div class="w3-modal-content w3-card-4 w3-center">\
    <header class="w3-container w3-theme w3-left-align">\
    <span action="close" class="w3-closebtn">&times;</span><h2>Добавить класс</h2></header>\
<div class="w3-container w3-left-align w3-padding">\
    <!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2">\
    <p>Введите или отредактируйте название класса. Обратите внимание, вместо класса (азотсодержащие, фосфорсодержащие, микроудобрения, и т.д.), удобрения можно группировать и по форме выпуска (жидкие, твердые), и по производителю. <a class="w3-text-blue" href="help_add_fertilizer_class.tiles">Подробнее...</a></p>\
    </div-->\
    <div name="errorMsg" class="w3-panel w3-leftbar w3-border-red w3-pale-red w3-card-2"></div>\
    <p><label class="w3-label">Название класса<span class="w3-text-red">*</span></label>\
    <input name="name" class="w3-input w3-border" type="text" value="" required="required"></p>\
    </div>\
    <footer class="w3-container w3-theme">\
    <div class="w3-right-align w3-padding-16">\
    <button onclick="EDIT_FERTILIZER_CATALOG_WINDOW.DELETE_FORM.open()" name="deleteBtn" type="button" class="w3-btn w3-grey w3-hover-red w3-left">Удалить</button>\
    <button action="close" type="button" class="w3-btn w3-grey">Отмена</button>\
    <button type="submit" class="w3-btn w3-theme-dark">Сохранить</button>\
    </div></footer></div></form>');
EDIT_FERTILIZER_CATALOG_WINDOW.DELETE_FORM = $(
    '<form class="w3-modal w3-padding-48">\
    <div class="w3-modal-content w3-card-4 w3-center"><header class="w3-container w3-theme w3-left-align">\
    <span action="close" class="w3-closebtn">&times;</span>\
    <h2><span class="fa fa-question-circle w3-text-white"></span></h2></header>\
    <div class="w3-container w3-left-align"><p>Вы действительно хотите удалить класс <b><className>название_класса</className></b>?</p></div>\
    <footer class="w3-container w3-theme">\
    <div class="w3-right-align w3-padding-16">\
    <button action="close" type="button" class="w3-btn w3-grey">Отмена</button>\
    <button type="submit" class="w3-btn w3-theme-dark">Удалить</button>\
    </div></footer></div></form>');

EDIT_FERTILIZER_CATALOG_WINDOW.initialize = function () {
    EDIT_FERTILIZER_CATALOG_WINDOW.appendTo(document.body);
    EDIT_FERTILIZER_CATALOG_WINDOW.DELETE_FORM.appendTo(document.body);
    EDIT_FERTILIZER_CATALOG_WINDOW.find("*[action='close']").click(function () {
        EDIT_FERTILIZER_CATALOG_WINDOW.hide();
    });
    EDIT_FERTILIZER_CATALOG_WINDOW.DELETE_FORM.find("*[action='close']").click(function () {
        EDIT_FERTILIZER_CATALOG_WINDOW.DELETE_FORM.hide();
    });
    EDIT_FERTILIZER_CATALOG_WINDOW.find("div[name='infoMsg']").show();
    EDIT_FERTILIZER_CATALOG_WINDOW.find("div[name='errorMsg']").hide();
    EDIT_FERTILIZER_CATALOG_WINDOW.submit(function () {
        var url = "api/fertilizer/catalog/";
        var type = "POST";
        var json = serializeFormToJsonTree($(this));

        if(json.validation) {
            EDIT_FERTILIZER_CATALOG_WINDOW.processError({responseJSON: {message: "error.fertilizerCatalog.required"}}, null, null);
            return false;
        }
        if (json.id) {
            type = "PUT";
            url += json.id;
        }
        doActionIfAnonCreateAndLogin(function () {
            $.ajax({
                type: type,
                url: url,
                data: JSON.stringify(json),
                contentType: "application/json; charset=utf-8",
                success: function (data) {
                    location.reload();
                },
                error: EDIT_FERTILIZER_CATALOG_WINDOW.processError
            });
        });
        return false;
    });
    EDIT_FERTILIZER_CATALOG_WINDOW.DELETE_FORM.submit(function () {
        var json = serializeFormToJsonTree(EDIT_FERTILIZER_CATALOG_WINDOW);

        $.ajax({
            type: "DELETE",
            url: "api/fertilizer/catalog/" + json.id,
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                location.reload();
            },
            error: EDIT_FERTILIZER_CATALOG_WINDOW.processError
        });

        return false;
    });
};
EDIT_FERTILIZER_CATALOG_WINDOW.processError = function (jqXHR, textStatus, errorThrown) {
    document.reloadPageOnError = false;
    EDIT_FERTILIZER_CATALOG_WINDOW.DELETE_FORM.hide();
    var msg = '';
    if (jqXHR.status == 404) {
        msg = 'error.not.authorized';
    } else {
        msg = jqXHR.responseJSON.message;
    }

    switch(msg) {
        case "error.classContainsFertilizer":
            msg = "Нельзя удалить класс с удобрениями.";
            break;
        case "error.fertilizerCatalog.exists":
            msg = "Такой класс уже есть в Вашей библиотеке";
            break;
        case "error.fertilizerCatalog.required":
            msg = "Пожалуйста, заполните все необходимые поля.";
            break;
        case "error.not.authorized":
            msg = "Пожалуйста, <a href='javascript:void(0)' onclick='doAction(\"login\")'>войдите</a> в свой профиль или <a href='user_registration.tiles'>зарегистрируйтесь</a> для того, чтобы добавить новый класс удобрений.";
            break;
    }
    EDIT_FERTILIZER_CATALOG_WINDOW.find("div[name='infoMsg']").hide();
    EDIT_FERTILIZER_CATALOG_WINDOW.find("div[name='errorMsg']")
        .html("<p>"+msg+"</p>").slideDown("fast");
};
EDIT_FERTILIZER_CATALOG_WINDOW.edit = function (fertilizerClassId) {
    var delBtn = EDIT_FERTILIZER_CATALOG_WINDOW.find("button[name='deleteBtn']");
    EDIT_FERTILIZER_CATALOG_WINDOW.find("div[name='infoMsg']").show();
    EDIT_FERTILIZER_CATALOG_WINDOW.find("div[name='errorMsg']").hide();
    if (fertilizerClassId != null) {
        EDIT_FERTILIZER_CATALOG_WINDOW.find("header h2").html("Редактировать класс")
        $.get(
            '/api/fertilizer/catalog/' + fertilizerClassId,
            function (data) {
                populateFormFromJsonTree(EDIT_FERTILIZER_CATALOG_WINDOW, data);
                EDIT_FERTILIZER_CATALOG_WINDOW.find("className").html(data.name);
                EDIT_FERTILIZER_CATALOG_WINDOW.DELETE_FORM
                    .find("className")
                    .html(data.name);
                delBtn.show();
                EDIT_FERTILIZER_CATALOG_WINDOW.show();
                EDIT_FERTILIZER_CATALOG_WINDOW.find("input[name='name']").focus();
            }
        );
    } else {
        EDIT_FERTILIZER_CATALOG_WINDOW.find("header h2").html("Добавить класс")
        var def = {
            "id": "",
            "name": ""
        };
        populateFormFromJsonTree(EDIT_FERTILIZER_CATALOG_WINDOW, def);
        delBtn.hide();
        EDIT_FERTILIZER_CATALOG_WINDOW.show();
        EDIT_FERTILIZER_CATALOG_WINDOW.find("input[name='name']").focus();
    }
};
EDIT_FERTILIZER_CATALOG_WINDOW.DELETE_FORM.open = function () {
    EDIT_FERTILIZER_CATALOG_WINDOW.DELETE_FORM.show();
};

/*==============================================================================*/
/*=====================    EDIT FERTILIZER FORM    =============================*/
/*==============================================================================*/
var EDIT_FERTILIZER_WINDOW = $(
    '<div class="w3-modal w3-padding-48">\
    <form class="w3-modal-content w3-card-4 w3-center">\
    <header class="w3-container w3-theme w3-left-align">\
        <span action="close" class="w3-closebtn">&times;</span>\
        <h2>Добавить удобрение</h2>\
    </header>\
    <div class="w3-container w3-left-align">\
    <!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2">\
    <p>Выберите класс удобрений и введите название удобрения. Обратите внимание, новый класс можно создать при добавлении или редактировании удобрения. <a class="w3-text-blue" href="help_add_fertilizer.tiles">Подробнее...</a></p>\
    </div-->\
    <div name="errorMsg" class="w3-panel w3-leftbar w3-border-red w3-pale-red w3-card-2"></div>\
    <p><label class="w3-label">Класс удобрений<span class="w3-text-red">*</span></label>\
        <input name="catalog.name" list="fertilizerCategories" class="w3-select" required="required" placeholder="Выберите класс или введите новое название">\
        <datalist id="fertilizerCategories"></datalist>\
    </p>\
    <input type="hidden" name="item.id"/>\
    <p><label class="w3-label">Название удобрения<span class="w3-text-red">*</span></label>\
        <input name="item.name" class="w3-input" type="text" required="required"></p>\
    <p><h2 class="w3-text-theme">Несовместимые удобрения:<span>\
    <div fertilizerList="" class="w3-container w3-center">\
        <a readOnly="hide" onclick="SELECT_FERTILIZER_WINDOW.edit(EDIT_FERTILIZER_WINDOW.fertilizers)" class="w3-btn-floating-large w3-theme-d1" title="Добавить несовместимые удобрения"><span class="w3-large fa fa-plus"></span></a>\
        <ul class="w3-ul w3-center"></ul>\
    </div>\
    </div>\
    <footer class="w3-container w3-theme">\
    <div class="w3-right-align w3-padding-16">\
    <button action="close" type="button" class="w3-btn w3-grey">Отмена</button>\
    <button readOnly="hide" type="submit" class="w3-btn w3-theme-dark">Сохранить</button>\
    </div></footer></form></div>');

EDIT_FERTILIZER_WINDOW.fertilizers = [];
EDIT_FERTILIZER_WINDOW.disableValidationIncompatibleFertilizer = false;

EDIT_FERTILIZER_WINDOW.initialize = function(config) {
    EDIT_FERTILIZER_WINDOW.appendTo(document.body);
    EDIT_FERTILIZER_WINDOW.find("*[action='close']").click(function(){
        EDIT_FERTILIZER_WINDOW.hide();
    });
    EDIT_FERTILIZER_WINDOW.find("form").submit(function () {

        var json = serializeFormToJsonTree($(this));

        if (json.validation) {
            EDIT_FERTILIZER_WINDOW.processError({responseJSON: {message: "error.fertilizer.required"}}, null, null);
            return false;
        }
        json.restrictedFertilizers = EDIT_FERTILIZER_WINDOW.fertilizers;
        var savedData = JSON.stringify(json);
        doActionIfAnonCreateAndLogin(function () {
            $.ajax({
                type: "post",
                url: "api/fertilizer/",
                data: savedData,
                contentType: "application/json; charset=utf-8",
                success: function (fertilizer) {
                    location.reload();
                },
                error: EDIT_FERTILIZER_WINDOW.processError
            });
        });
        return false;
    });

    $.get(
        "api/fertilizer/catalog",
        function (items) {
            var fertilizerCategoriesList = $("#fertilizerCategories");
            for (var i = 0; i < items.length; i++) {
                var item = items[i];
                $("<option value='" + item.name + "'>" + item.name + "</option>").appendTo(fertilizerCategoriesList);
            }
        }
    );
    var skipFertilizerIds = [];
    if(config) {
        if(config.currentFertilizerId) {
            skipFertilizerIds = [config.currentFertilizerId];
        }
    }
    SELECT_FERTILIZER_WINDOW.initialize({
        skipValidationForFertilizersIds: skipFertilizerIds,
        disableValidation: config.disableValidationIncompatibleFertilizer ? config.disableValidationIncompatibleFertilizer : false,
        persisDataFunc: function (selectedFertilizers, callback) {
            EDIT_FERTILIZER_WINDOW.fertilizers = selectedFertilizers;
            EDIT_FERTILIZER_WINDOW._refreshFertilizersList();
            callback();
        },
        pageSize: 10
    });
};

EDIT_FERTILIZER_WINDOW._refreshFertilizersList = function () {
    var fertilizers = EDIT_FERTILIZER_WINDOW.fertilizers;
    var fertilizerList = EDIT_FERTILIZER_WINDOW.find("div[fertilizerList] > ul");
    fertilizerList.html('');
    if (fertilizers) {
        if (fertilizers.length > 0) {
            for (var i = 0; i < fertilizers.length; i++) {
                $("<li>" + fertilizers[i].name + "&nbsp;<b readOnly='hide' onclick='EDIT_FERTILIZER_WINDOW.deleteFertilizerByIndex(" + i + ")' style='cursor:pointer;color:darkred;'>[x]</b></li>").appendTo(fertilizerList);
            }
        }
    }
};
EDIT_FERTILIZER_WINDOW.deleteFertilizerByIndex = function (index) {
    var fertilizers = EDIT_FERTILIZER_WINDOW.fertilizers;
    fertilizers.splice(index, 1);
    EDIT_FERTILIZER_WINDOW._refreshFertilizersList();
};

EDIT_FERTILIZER_WINDOW.processError = function(jqXHR, textStatus, errorThrown) {
    document.reloadPageOnError = false;
    var errorMsg = EDIT_FERTILIZER_WINDOW.find("div[name='errorMsg']");
    var infoMsg = EDIT_FERTILIZER_WINDOW.find("div[name='infoMsg']");

    var msg = '';
    if (jqXHR.status == 404) {
        msg = 'error.not.authorized';
    } else {
        msg = jqXHR.responseJSON.message;
    }

    switch(msg) {
        case "error.fertilizer.exists":
            msg = "Такое удобрение уже есть в Вашей библиотеке";
            break;
        case "error.fertilizer.required":
            msg = "Пожалуйста, заполните все необходимые поля";
            break;
        case "error.not.authorized":
            msg = "Пожалуйста, <a href='javascript:void(0)' onclick='doAction(\"login\")'>войдите</a> в свой профиль или <a href='user_registration.tiles'>зарегистрируйтесь</a> для того, чтобы добавить новый класс удобрений.";
            break;
    }
    infoMsg.hide();
    errorMsg.html("<p>"+msg+"</p>");
    errorMsg.slideDown("fast");
};
EDIT_FERTILIZER_WINDOW.edit = function(fertilizerId) {
    var form = EDIT_FERTILIZER_WINDOW.find("form");
    var errorMsg = EDIT_FERTILIZER_WINDOW.find("div[name='errorMsg']");
    var infoMsg = EDIT_FERTILIZER_WINDOW.find("div[name='infoMsg']");
    errorMsg.hide();
    infoMsg.show();
    if(fertilizerId==null) {
        populateFormFromJsonTree(form, {catalog: {name: ""}, item: {id:"", name: ""}});
        EDIT_FERTILIZER_WINDOW.find('header > h2').html('Добавить удобрение');
        EDIT_FERTILIZER_WINDOW.show();
        EDIT_FERTILIZER_WINDOW.find("input[name='catalog.name']").focus();
        EDIT_FERTILIZER_WINDOW.fertilizers = [];
        EDIT_FERTILIZER_WINDOW._refreshFertilizersList();
        EDIT_FERTILIZER_WINDOW.postLoadData(false);
    } else {
        $.get(
            'api/fertilizer/'+fertilizerId,
            function(data1) {
                populateFormFromJsonTree(form, data1);
                $.get(
                        'api/fertilizer/restrictions/'+fertilizerId,
                    function(data2) {
                        EDIT_FERTILIZER_WINDOW.fertilizers = data2;
                        EDIT_FERTILIZER_WINDOW._refreshFertilizersList();
                        EDIT_FERTILIZER_WINDOW.postLoadData(data1.item.ownerId==null);
                    }
                );

                EDIT_FERTILIZER_WINDOW.show();
                EDIT_FERTILIZER_WINDOW.find("input[name='catalog.name']").focus();
            }
        );
    }
};

EDIT_FERTILIZER_WINDOW.postLoadData = function(readonly) {
    if(readonly) {
        EDIT_FERTILIZER_WINDOW.find('header > h2').html('Несовместимые удобрения');
        EDIT_FERTILIZER_WINDOW.find("*[readOnly='hide']").hide();
        EDIT_FERTILIZER_WINDOW.find("input").prop("disabled", true);
    } else {
        EDIT_FERTILIZER_WINDOW.find('header > h2').html('Редактировать удобрение');
        EDIT_FERTILIZER_WINDOW.find("*[readOnly='hide']").show();
        EDIT_FERTILIZER_WINDOW.find("input").prop("disabled", false);
    }
};

/*==============================================================================*/
/*===========================    EDIT TASK FORM    =============================*/
/*==============================================================================*/
var EDIT_TASK_WINDOW = $(
    '<form class="w3-modal w3-padding-48">\
    <div class="w3-modal-content w3-card-4 w3-center">\
    <header class="w3-container w3-theme w3-left-align">\
        <span action="close" class="w3-closebtn">&times;</span>\
        <h2>Редактировать работу</h2>\
    </header>\
    <div class="w3-container w3-left-align w3-padding">\
    <input name="id" type="hidden"/>\
    <!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2"><p>Введите новое или отредактируйте название работы. <a class="w3-text-blue" href="help_add_work.tiles">Подробнее...</a></p></div-->\
    <div name="errorMsg" class="w3-panel w3-leftbar w3-border-red w3-pale-red w3-card-2"></div>\
    <p><label class="w3-label">Название работы<span class="w3-text-red w3-label">*</span></label>\
    <input name="name" class="w3-input w3-border" type="text" value="" required="required"></p>\
    </div>\
    <footer class="w3-container w3-theme"><div class="w3-right-align w3-padding-16">\
        <button type="button" action="close" class="w3-btn w3-grey">Отмена</button>\
        <button type="submit" class="w3-btn w3-theme-dark">Сохранить</button>\
    </div></footer></div></form>');

EDIT_TASK_WINDOW.initialize = function (config) {
    EDIT_TASK_WINDOW.config = config;
    EDIT_TASK_WINDOW.appendTo(document.body);
    EDIT_TASK_WINDOW.find("*[action='close']").click(function () {
        EDIT_TASK_WINDOW.hide();
    });

    EDIT_TASK_WINDOW.submit(function () {
        var form = $(this);
        var json = serializeFormToJsonTree(form);
        var url = "api/tasks/users/";
        var action = "POST";
        if (json.id == "") {
            action = "POST";
        } else {
            action = "PUT";
            url += json.id;
        }
        doActionIfAnonCreateAndLogin(function () {
            $.ajax({
                url: url,
                type: action,
                data: JSON.stringify(json),
                contentType: "application/json; charset=utf-8",
                success: function (data, textStatus, jqXHR) {
                    if (EDIT_TASK_WINDOW.config.callback) {
                        EDIT_TASK_WINDOW.config.callback();
                    }
                    EDIT_TASK_WINDOW.hide();
                },
                error: EDIT_TASK_WINDOW.processError
            });
        });
        return false;
    });
};
EDIT_TASK_WINDOW.processError = function (jqXHR, textStatus, errorThrown) {
    document.reloadPageOnError = false;
    var errorMsg = EDIT_TASK_WINDOW.find("div[name='errorMsg']");
    var infoMsg = EDIT_TASK_WINDOW.find("div[name='infoMsg']");

    var msg = jqXHR.responseJSON.message;

    switch (msg) {
        case "error.systemWork.exists":
            msg = "Такая работа уже есть в списке";
            break;
        case "error.work.exists":
            msg = "Такая работа уже есть в списке";
            break;
        case "error.work.required":
            msg = "Пожалуйста, заполните все необходимые поля";
            break;
    }
    infoMsg.hide();
    errorMsg.html("<p>" + msg + "</p>");
    errorMsg.slideDown("fast");
};
EDIT_TASK_WINDOW.edit = function (taskId) {
    var errorMsg = EDIT_TASK_WINDOW.find("div[name='errorMsg']");
    var infoMsg = EDIT_TASK_WINDOW.find("div[name='infoMsg']");
    errorMsg.hide();
    infoMsg.show();

    if (taskId == null) {
        populateFormFromJsonTree(EDIT_TASK_WINDOW, {id: "", name: ""});
        EDIT_TASK_WINDOW.find('header>h2').html('Добавить работу');
        EDIT_TASK_WINDOW.show();
        EDIT_TASK_WINDOW.find("input[name='name']").focus();
    } else {
        $.get(
            "api/tasks/users/" + taskId,
            function (data) {
                populateFormFromJsonTree(EDIT_TASK_WINDOW, data);
                EDIT_TASK_WINDOW.find('header>h2').html('Редактировать работу');
                EDIT_TASK_WINDOW.show();
                EDIT_TASK_WINDOW.find("input[name='name']").focus();
            }
        );
    }
};
/*==============================================================================*/
/*========================    INPUT LOCATION FORM    ===========================*/
/*==============================================================================*/
var EDIT_USER_LOCATION_WINDOW = $(
    '<form class="w3-modal w3-padding-48"><div class="w3-modal-content w3-card-4">\
    <header class="w3-container w3-theme w3-left-align">\
        <span action="close" class="w3-closebtn">&times;</span>\
        <h2>Ввести местополжение</h2>\
    </header>\
    <div name="errorMsg" class="w3-panel w3-leftbar w3-border-red w3-pale-red w3-card-2"></div>\
    <!--div id="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2">\
        <p>Пожалуйста, укажите Ваше местоположение. Иначе, задание времени выполнения работы по погодным условиям \
        невозможно. Данные о местоположении будут сохранены в Вашем профайле и доступны для редактирования. \
        <a class="w3-text-blue" href="help_add_template_work.tiles">Подробнее...</a></p>\
    </div-->\
    <div class="w3-container w3-left-align">\
    <p><label class="w3-label">Страна<span class="w3-text-red">*</span></label>\
        <input list="countries" name="country" id="country" class="w3-select"  required="required" onblur="loadCities(\'city\', $(this).val())">\
            <datalist id="countries"></datalist>\
        </p>\
        <p><label class="w3-label">Город<span class="w3-text-red">*</span></label>\
            <input list="cities" name="city" id="city" class="w3-select" required="required">\
            <datalist id="cities"></datalist>\
            <p class="w3-small w3-text-grey">*В первый месяц после указания Вашего местоположения возможно некорректное определение даты выполнения работ, если Вы указываете его как зависимость от погоды. Более того, так как Ваше местоположение является необязательным атрибутом при регистрации и может быть указано позже, месяц некорректных расчетов будет начинаться с момента его указания. Приносим свои извинения.</p>\
            </p>\
        </div>\
        <footer class="w3-container w3-theme">\
            <div class="w3-right-align w3-padding-16">\
                <button type="button" action="close" class="w3-btn w3-light-grey">Отмена</button>\
                <button type="submit" class="w3-btn w3-theme-dark">Сохранить</button>\
            </div>\
        </footer>\
    </div></form>');

EDIT_USER_LOCATION_WINDOW.initialize = function (config) {
    EDIT_USER_LOCATION_WINDOW.config = config;
    EDIT_USER_LOCATION_WINDOW.appendTo(document.body);
    EDIT_USER_LOCATION_WINDOW.find("*[action='close']").click(function () {
        EDIT_USER_LOCATION_WINDOW.hide();
    });

    EDIT_USER_LOCATION_WINDOW.submit(function () {
        var form = $(this);
        var json = serializeFormToJsonTree(form);
        if(json.validation) {
            EDIT_USER_TASK_WINDOW.processError({responseJSON:{message:"error.location.required"}},null,null);
        }
        $.ajax({
            url: "api/userProfile/updateLocation",
            type: "post",
            data: form.serialize(),
            success: function (data, textStatus, jqXHR) {
                // resetUserInSessionStore();
                var user = getUser();
                user.cityName = json.city;
                user.countryName = json.country;
                setUser(user);
                if (EDIT_USER_LOCATION_WINDOW.config.callback) {
                    EDIT_USER_LOCATION_WINDOW.config.callback();
                }
                EDIT_USER_LOCATION_WINDOW.hide();
            },
            error: EDIT_USER_LOCATION_WINDOW.processError
        });
        return false;
    });

    loadCountries("country");
    if (getUser() != null) {
        loadCities('city', getUser().countryName);
    }
};
EDIT_USER_LOCATION_WINDOW.processError = function (jqXHR, textStatus, errorThrown) {
    document.reloadPageOnError = false;
    var errorMsg = EDIT_USER_LOCATION_WINDOW.find("div[name='errorMsg']");
    var infoMsg = EDIT_USER_LOCATION_WINDOW.find("div[name='infoMsg']");

    var msg = jqXHR.responseJSON.message;

    switch (msg) {
        case "error.location.required":
            msg = "Пожалуйста, заполните все необходимые поля.";
            break;
    }
    infoMsg.hide();
    errorMsg.html("<p>" + msg + "</p>");
    errorMsg.slideDown("fast");
};
EDIT_USER_LOCATION_WINDOW.edit = function (callback) {
    EDIT_USER_LOCATION_WINDOW.config.callback = callback ? callback : null;

    var errorMsg = EDIT_USER_LOCATION_WINDOW.find("div[name='errorMsg']");
    var infoMsg = EDIT_USER_LOCATION_WINDOW.find("div[name='infoMsg']");
    errorMsg.hide();
    infoMsg.show();


    populateFormFromJsonTree(EDIT_USER_LOCATION_WINDOW, {country: getUser().countryName, city: getUser().cityName});
    EDIT_USER_LOCATION_WINDOW.show();
    EDIT_USER_LOCATION_WINDOW.find("input[name='name']").focus();

};

/*==============================================================================*/
/*=============================   EDIT USER TASK   =============================*/
/*==============================================================================*/
var EDIT_USER_TASK_WINDOW = $(
    '<form class="w3-modal w3-padding-48">\
        <input type="hidden" name="id"/>\
        <input type="hidden" name="userWorkId"/>\
        <input type="hidden" name="status"/>\
        <input type="hidden" name="taskId"/>\
        <input type="hidden" name="taskName"/>\
    <div class="w3-modal-content w3-card-4 w3-center">\
        <header class="w3-container w3-theme w3-left-align">\
            <span action="close" class="w3-closebtn">&times;</span>\
            <h2>"Название_работы"</h2>\
        </header>\
        <div class="w3-container w3-left-align w3-padding">\
            <div name="errorMsg" class="w3-panel w3-leftbar w3-border-red w3-pale-red w3-card-2"></div>\
            <!--div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2">\
                <p>Выберите, как Вам удобнее задать время выполнения работы и укажите, когда Вы планируете выполнять\
                работу для культуры <speciesName style="font-weight:bold;">${species.name}</speciesName>. Запишите свои заметки.\
                <a class="w3-text-blue" href="help_add_template_work.tiles">Подробнее...</a></p>\
            </div-->\
            <p><label class="w3-label">Комментарии</label>\
                <textarea name="comment" rows="6" class="w3-border-theme" style="width:100%"></textarea>\
            </p>\
        </div>\
        <div class="w3-container w3-left-align">\
            <ul id="selectConfigurationType" name="planingType" value="planByDate" class="w3-navbar w3-light-gray">\
                <li class="w3-hover-text-white w3-hover-gray w3-light-gray w3-padding" value="planByDate">Указать точную дату</li>\
                <li class="w3-hover-text-white w3-hover-gray w3-light-gray w3-padding" value="planByWeather">Зависит от погоды</li>\
                <li class="w3-hover-text-white w3-hover-gray w3-light-gray w3-padding" value="planByAnotherTask">После другой работы</li>\
            </ul>\
            <div id="planByDate" class="w3-container w3-card-2 w3-margin-bottom" style="display:none">\
                <p><label>Введите дату:</label>\
                   <input class="w3-input w3-border" type="date" name="startDate" required="required"/></p>\
            </div>\
            <div id="planByWeather" class="w3-container w3-card-2 w3-margin-bottom " style="display:none">\
                <div class="w3-row">\
                    <div class="w3-third w3-padding-right">\
                        <p><label>Среднемесячная температура ℃:</label>\
                            <input class="w3-input w3-border" type="number" name="averageTemperature" required="required"></p>\
                        </div>\
                        <div class="w3-third w3-padding-right">\
                            <p><label>Но не ранее: </label>\
                                <input class="w3-input w3-border" type="date" name="startDate" required="required"></p>\
                            </div>\
                            <div class="w3-third w3-padding-right">\
                                <p><label>И не позднее:</label>\
                                    <input class="w3-input w3-border" type="date" name="endDate" required="required"></p>\
                                </div>\
                            </div>\
                        </div>\
                        <div id="planByAnotherTask" class="w3-container w3-card-2 w3-margin-bottom " style="display:none">\
                            <p><label><input class="w3-border" type="number" min="0" max="365" name="countDaysAfterPrevEvent" required="required"\
                            style="width:100px; padding:8px"> дней после работы:</label></p>\
                                <select class="w3-select" name="dependsFromTaskId" required="required">\
                                    <option value="">Выбрать работу</option>\
                                </select>\
                            </div>\
                        </div>\
                        <div fertilizerList="" class="w3-container">\
                            <a onclick="SELECT_FERTILIZER_WINDOW.edit(EDIT_USER_TASK_WINDOW.fertilizers)" class="w3-btn-floating-large w3-theme-d1" title="Добавить удобрение для культуры"><span class="w3-large fa fa-plus"></span></a>\
                            <ul class="w3-ul w3-center"></ul>\
                        </div>\
                        <footer class="w3-container w3-theme">\
                            <div class="w3-right-align w3-padding-16">\
                                <button onclick="EDIT_USER_TASK_WINDOW.DELETE_FORM.open()" name="deleteBtn" type="button" class="w3-btn w3-grey w3-hover-red w3-left">Удалить</button>\
                                <button type="button" action="close" class="w3-btn w3-grey">Отмена</button>\
                                <button type="submit" class="w3-btn w3-theme-dark">Сохранить</button>\
                            </div>\
                        </footer>\
                    </div>\
                </form>');

EDIT_USER_TASK_WINDOW.DELETE_FORM = null;

EDIT_USER_TASK_WINDOW.initialize = function (config) {
    EDIT_USER_TASK_WINDOW.DELETE_FORM = DELETE_USER_TASK_WINDOW;
    EDIT_USER_TASK_WINDOW.config = config;
    if(!EDIT_USER_TASK_WINDOW.config.DELTA_YEAR) {
        EDIT_USER_TASK_WINDOW.config.DELTA_YEAR = 0;
    }
    EDIT_USER_TASK_WINDOW.appendTo(document.body);

    EDIT_USER_TASK_WINDOW.find("*[name='deleteBtn']").hide();

    EDIT_USER_TASK_WINDOW.DELETE_FORM.initialize({
        speciesName: config.speciesName,
        callback: function(){
            EDIT_USER_TASK_WINDOW.hide();
            config.callback()
        }
    });

    EDIT_USER_LOCATION_WINDOW.initialize({
        callback:function(){

        }
    });

    EDIT_USER_TASK_WINDOW.find("*[action='close']").click(function () {
        EDIT_USER_TASK_WINDOW.hide();
    });
    {
        EDIT_USER_TASK_WINDOW.eventType = $("#selectConfigurationType");
        var items = EDIT_USER_TASK_WINDOW.eventType.find("li");
        EDIT_USER_TASK_WINDOW.eventType.change(function () {
            var selectedValue = $(this).attr('value');
            $.each(items, function (i, obj) {
                var o = $(obj);
                var itemValue = o.attr('value');
                var itemContent = $("#" + itemValue);
                if (itemValue == selectedValue) {
                    o.removeClass("w3-light-gray");
                    o.addClass("w3-dark-gray");
                    itemContent.show();
                    itemContent.find("*[name]").removeAttr("disabled");
                } else {
                    o.addClass("w3-light-gray");
                    o.removeClass("w3-dark-gray");
                    itemContent.hide();
                    itemContent.find("*[name]").attr("disabled", "disabled");
                }
            });
        });
        items.click(function () {
            var selectedValue = $(this).attr('value');
            EDIT_USER_TASK_WINDOW.eventType.attr('value', selectedValue);
            EDIT_USER_TASK_WINDOW.eventType.change();
        });
    }

    EDIT_USER_TASK_WINDOW.submit(function () {
        var form = $(this);
        var json = serializeFormToJsonTree(form);
        json.fertilizers = EDIT_USER_TASK_WINDOW.fertilizers;

        if(json.validation) {
            EDIT_USER_TASK_WINDOW.processError({responseJSON:{message:"error.userTask.required"}},null,null);
            return false;
        }
        if (json.planingType == 'planByWeather') {
            var startDate = new Date(json.startDate);
            var endDate = new Date(json.endDate);
            if (startDate > endDate){
                EDIT_USER_TASK_WINDOW.processError({responseJSON: {message: "error.invalid.dateRange"}}, null, null);
                return false;
            }
        }
        if (json.planingType == 'planByWeather') {
            if (getUser().cityName == null || getUser().cityName == "") {
                EDIT_USER_LOCATION_WINDOW.edit();
                return false;
            }
        }

        EDIT_USER_TASK_WINDOW._saveFormData(json);

        return false;
    });
    SELECT_FERTILIZER_WINDOW.initialize({
        skipValidationForFertilizersIds: [],
        disableValidation: false,
        persisDataFunc: function (selectedFertilizers, callback) {
            EDIT_USER_TASK_WINDOW.fertilizers = selectedFertilizers;
            EDIT_USER_TASK_WINDOW._refreshFertilizersList();
            callback();
        },
        pageSize: 10
    });
};
EDIT_USER_TASK_WINDOW._saveFormData = function(json) {
    $.ajax({
        url: "/api/user/task/",
        type: "post",
        data: JSON.stringify(json),
        contentType: "application/json; charset=utf-8",
        success: function (data, textStatus, jqXHR) {
            if (EDIT_USER_TASK_WINDOW.config.callback) {
                EDIT_USER_TASK_WINDOW.config.callback();
            }
            EDIT_USER_TASK_WINDOW.hide();
        },
        error: EDIT_USER_TASK_WINDOW.processError
    });
};

EDIT_USER_TASK_WINDOW.processError = function (jqXHR, textStatus, errorThrown) {
    document.reloadPageOnError = false;
    var errorMsg = EDIT_USER_TASK_WINDOW.find("div[name='errorMsg']");
    var infoMsg = EDIT_USER_TASK_WINDOW.find("div[name='infoMsg']");

    var msg = jqXHR.responseJSON.message;

    switch (msg) {
        case "error.invalid.dateRange":
            msg = "Неверно задан временной период";
            break;
        case "error.userTask.required":
            msg = "Пожалуйста, заполните все необходимые поля";
            break;
    }
    infoMsg.hide();
    errorMsg.html("<p>" + msg + "</p>");
    errorMsg.slideDown("fast");
};
EDIT_USER_TASK_WINDOW.editJson = function (userTask, speciesName, prepopulateDataProcessingFunc) {
    EDIT_USER_TASK_WINDOW.find("speciesName").html(speciesName);
    var errorMsg = EDIT_USER_TASK_WINDOW.find("div[name='errorMsg']");
    var infoMsg = EDIT_USER_TASK_WINDOW.find("div[name='infoMsg']");
    errorMsg.hide();
    infoMsg.show();

    EDIT_USER_TASK_WINDOW._updateForm(userTask, prepopulateDataProcessingFunc);
};
EDIT_USER_TASK_WINDOW.edit = function (userTaskId, speciesName, prepopulateDataProcessingFunc) {
    EDIT_USER_TASK_WINDOW.DELETE_FORM.config.userTaskId = userTaskId;
    EDIT_USER_TASK_WINDOW.find("speciesName").html(speciesName);
    var errorMsg = EDIT_USER_TASK_WINDOW.find("div[name='errorMsg']");
    var infoMsg = EDIT_USER_TASK_WINDOW.find("div[name='infoMsg']");
    errorMsg.hide();
    infoMsg.show();

    if (userTaskId == null) {
        var userTask = {};
        if(EDIT_USER_TASK_WINDOW.config.enableDeleteButton) {
            EDIT_USER_TASK_WINDOW.find("*[name='deleteBtn']").hide();
        }
        EDIT_USER_TASK_WINDOW._updateForm(userTask, prepopulateDataProcessingFunc);
    } else {
        $.get(
            "api/user/task/" + userTaskId,
            function (userTask) {
                if(EDIT_USER_TASK_WINDOW.config.enableDeleteButton) {
                    EDIT_USER_TASK_WINDOW.find("*[name='deleteBtn']").show();
                }
                userTask.calculatedDate = addDeltaYear(userTask.calculatedDate, EDIT_USER_TASK_WINDOW.config.DELTA_YEAR);
                userTask.startDate = addDeltaYear(userTask.startDate, EDIT_USER_TASK_WINDOW.config.DELTA_YEAR);
                userTask.endDate = addDeltaYear(userTask.endDate, EDIT_USER_TASK_WINDOW.config.DELTA_YEAR);
                EDIT_USER_TASK_WINDOW.DELETE_FORM.config.userTaskId = userTaskId;
                EDIT_USER_TASK_WINDOW.DELETE_FORM.config.taskName = userTask.taskName;
                EDIT_USER_TASK_WINDOW._updateForm(userTask, prepopulateDataProcessingFunc)
            }
        );
    }
};
EDIT_USER_TASK_WINDOW._updateForm = function (userTask, prepopulateDataProcessingFunc) {
    if (prepopulateDataProcessingFunc) {
        prepopulateDataProcessingFunc(userTask)
    }
    EDIT_USER_TASK_WINDOW.fertilizers = userTask.fertilizers;
    $.get(
        "api/user/task/" + userTask.userWorkId + "/all",
        function (userTasks) {
            var dependsFromTask = EDIT_USER_TASK_WINDOW.find("select[name='dependsFromTaskId']")
            dependsFromTask.html('');
            var nameMap = EDIT_USER_TASK_WINDOW.config.patternTaskNameMap;
            for (var i = 0; i < userTasks.length; i++) {
                var ut = userTasks[i];
                if (ut.id == userTask.id) {
                    continue;
                }
                var selected = false;
                if(nameMap && userTask.dependsFromTaskId!=null) {
                    var el = nameMap[userTask.dependsFromTaskId];
                    if(el) {
                        var index = el.index;
                        selected = i == index;
                    }
                }
                if(selected) {
                    userTask.dependsFromTaskId = ut.id;
                }

                $("<option value='" + ut.id + "'>" + ut.taskName + " (" + formatDate(ut.calculatedDate, 'dd.MM.yyyy') + ")" + "</option>")
                    .appendTo(dependsFromTask);

            }

            populateFormFromJsonTree(EDIT_USER_TASK_WINDOW, userTask);
            EDIT_USER_TASK_WINDOW.eventType.change();
            EDIT_USER_TASK_WINDOW.find('header>h2').html(userTask.taskName);
            EDIT_USER_TASK_WINDOW._refreshFertilizersList();
            EDIT_USER_TASK_WINDOW.show();
            EDIT_USER_TASK_WINDOW.find("input[name='comment']").focus();
        }
    );
};
EDIT_USER_TASK_WINDOW._refreshFertilizersList = function () {
    var fertilizers = EDIT_USER_TASK_WINDOW.fertilizers;
    var list = EDIT_USER_TASK_WINDOW.find("div[fertilizerList]");
    var dataList = list.find("ul");
    dataList.html('');
    if (fertilizers) {
        if (fertilizers.length > 0) {
            for (var i = 0; i < fertilizers.length; i++) {
                $("<li>" + fertilizers[i].name + "&nbsp;<b onclick='EDIT_USER_TASK_WINDOW.deleteFertilizerByIndex(" + i + ")' style='cursor:pointer;color:darkred;'>[x]</b></li>").appendTo(dataList);
            }
        }
    }
};
EDIT_USER_TASK_WINDOW.deleteFertilizerByIndex = function (index) {
    var fertilizers = EDIT_USER_TASK_WINDOW.fertilizers;
    fertilizers.splice(index, 1);
    EDIT_USER_TASK_WINDOW._refreshFertilizersList();
};

/*==============================================================================*/
/*===========================   DELETE USER TASK   =============================*/
/*==============================================================================*/
var DELETE_USER_TASK_WINDOW = $(
    '<div class="w3-modal w3-padding-48">\
        <div class="w3-modal-content w3-card-4 w3-center">\
    <header class="w3-container w3-theme w3-left-align">\
        <span action="close" class="w3-closebtn">&times;</span>\
        <h2><span class="fa fa-question-circle w3-text-white"></span></h2>\
    </header>\
    <div class="w3-container w3-left-align">\
    <p>Вы действительно хотите удалить работу <taskName style="font-weight: bold;">"название_работы"</taskName>?</p></div>\
<footer class="w3-container w3-theme">\
    <div class="w3-right-align w3-padding-16">\
        <button class="w3-btn w3-grey" action="close">Отмена</button>\
        <button class="w3-btn w3-theme-dark" onclick="DELETE_USER_TASK_WINDOW.deleteUserTask();">Удалить</button>\
    </div>\
</footer></div></div>');

DELETE_USER_TASK_WINDOW.initialize = function (config) {
    DELETE_USER_TASK_WINDOW.config = config;
    DELETE_USER_TASK_WINDOW.appendTo(document.body);
    DELETE_USER_TASK_WINDOW.find("*[action='close']").click(function () {
        DELETE_USER_TASK_WINDOW.hide();
    });
};

DELETE_USER_TASK_WINDOW.open = function () {
    DELETE_USER_TASK_WINDOW.attr("userTaskId", DELETE_USER_TASK_WINDOW.config.userTaskId);
    DELETE_USER_TASK_WINDOW.find("speciesName").html(DELETE_USER_TASK_WINDOW.config.speciesName);
    DELETE_USER_TASK_WINDOW.find("taskName").html(DELETE_USER_TASK_WINDOW.config.taskName);
    DELETE_USER_TASK_WINDOW.show();
};
DELETE_USER_TASK_WINDOW.display = function (userTaskId, taskName) {
    DELETE_USER_TASK_WINDOW.attr("userTaskId", userTaskId);
    DELETE_USER_TASK_WINDOW.find("speciesName").html(DELETE_USER_TASK_WINDOW.config.speciesName);
    DELETE_USER_TASK_WINDOW.find("taskName").html(taskName);
    DELETE_USER_TASK_WINDOW.show();
};

DELETE_USER_TASK_WINDOW.deleteUserTask = function() {
    var userTaskId = DELETE_USER_TASK_WINDOW.attr("userTaskId");
    $.ajax({
        url: "/api/user/task/"+userTaskId,
        type: "delete",
        success: function () {
            DELETE_USER_TASK_WINDOW.hide();
            if(DELETE_USER_TASK_WINDOW.config.callback) {
                DELETE_USER_TASK_WINDOW.config.callback();
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert(jqXHR.responseJSON.message);
        }
    });
};

/*==============================================================================*/
/*===========================   DELETE USER WORK   =============================*/
/*==============================================================================*/
var DELETE_USER_WORK_WINDOW = $(
    '<div class="w3-modal w3-padding-48">\
        <div class="w3-modal-content w3-card-4 w3-center">\
    <header class="w3-container w3-theme w3-left-align">\
        <span action="close" class="w3-closebtn">&times;</span>\
        <h2><span class="fa fa-question-circle w3-text-white"></span></h2>\
    </header>\
    <div class="w3-container w3-left-align">\
    <p></p>\
    </div>\
<footer class="w3-container w3-theme">\
    <div class="w3-right-align w3-padding-16">\
        <button class="w3-btn w3-grey" action="close">Отмена</button>\
        <button class="w3-btn w3-theme-dark" onclick="DELETE_USER_WORK_WINDOW.deleteUserTask();">Удалить</button>\
    </div>\
</footer></div></div>');

DELETE_USER_WORK_WINDOW.initialize = function (config) {
    DELETE_USER_WORK_WINDOW.config = config;
    DELETE_USER_WORK_WINDOW.appendTo(document.body);
    DELETE_USER_WORK_WINDOW.find("p").html(config.content);
    DELETE_USER_WORK_WINDOW.find("*[action='close']").click(function () {
        DELETE_USER_WORK_WINDOW.hide();
    });
};

DELETE_USER_WORK_WINDOW.display = function (userWorkId, patternName) {
    DELETE_USER_WORK_WINDOW.attr("userWorkId", userWorkId);
    DELETE_USER_WORK_WINDOW.find("speciesName").html(DELETE_USER_WORK_WINDOW.config.speciesName);
    DELETE_USER_WORK_WINDOW.find("patternName").html(patternName);
    DELETE_USER_WORK_WINDOW.show();
};

DELETE_USER_WORK_WINDOW.deleteUserTask = function() {
    var userWorkId = DELETE_USER_WORK_WINDOW.attr("userWorkId");
    $.ajax({
        url: "/api/user/work/" + userWorkId,
        type: "delete",
        success: function () {
            DELETE_USER_WORK_WINDOW.hide();
            if(DELETE_USER_WORK_WINDOW.config.callback) {
                DELETE_USER_WORK_WINDOW.config.callback();
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert(jqXHR.responseJSON.message);
        }
    });
};

/*==============================================================================*/
/*========================= SELECT FERTILIZERS FORM  ===========================*/
/*==============================================================================*/
var SELECT_FERTILIZER_WINDOW = $(
    '<div class="w3-modal w3-padding-48"><div class="w3-modal-content w3-card-4 w3-center">\
        <header class="w3-container w3-theme w3-left-align">\
        <span action="close" class="w3-closebtn">&times;</span>\
        <h2>Выберите удобрения</h2></header>\
    <div class="w3-bar w3-left-align w3-padding" style="width:100%">\
        <input fertilizerSearch="fertilizerSearch" class=" w3-input w3-bar-item w3-light-gray w3-border-bottom" style="width:80%" placeholder="Название работы" onchange="SELECT_FERTILIZER_WINDOW.fertilizerModalNext(0)">\
        <a class="w3-bar-item w3-button w3-theme-dark" style="width:20%" onclick="SELECT_FERTILIZER_WINDOW.fertilizerModalNext(0, true)"><span class="fa fa-search"></span></a></div>\
        <div class="w3-container">\
        <p name="nothingFound" class="w3-text-dark-gray" style="font-style: italic">Ничего не найдено. <a class="w3-text-blue" href="#" onclick="SELECT_FERTILIZER_WINDOW.resetSearch();"><span class="fa fa-rotate-left"></span> Назад</a></p>\
        <ul fertilizerItems="fertilizerItems" class="w3-ul"></ul>\
        <div class="w3-center">\
        <ul class="w3-pagination w3-light-grey">\
        <li><a href="#" onclick="SELECT_FERTILIZER_WINDOW.fertilizerModalNext(0, true);">&#10094;&#10094; Первая</a></li>\
    <li><a href="#" name="back" onclick="SELECT_FERTILIZER_WINDOW.fertilizerModalNext(-1, false);">&#10094; Назад</a></li>\
    <li><a class="w3-theme" href="#" onclick="SELECT_FERTILIZER_WINDOW.fertilizerModalNext(0, true);">1</a></li>\
        <li><a href="#" onclick="SELECT_FERTILIZER_WINDOW.fertilizerModalNext(1, true);">2</a></li>\
        <li><a href="#" name="next" onclick="SELECT_FERTILIZER_WINDOW.fertilizerModalNext(1, false);">Вперед &#10095;</a></li>\
    <li><a href="#" onclick="SELECT_FERTILIZER_WINDOW.fertilizerModalLast();">Последняя &#10095;&#10095;</a></li>\
    </ul></div></div>\
    <footer class="w3-container w3-theme">\
        <div class="w3-right-align w3-padding-16">\
        <button action="close" class="w3-btn w3-grey">Отмена</button>\
        <button onclick="SELECT_FERTILIZER_WINDOW.saveSelectedFertilizersToSpeciesWork()" class="w3-btn w3-theme-dark">Сохранить</button>\
    </div></footer></div></div>');

SELECT_FERTILIZER_WINDOW.CONFIRM_UNCOMPATIBLE = $(
    '<div class="w3-modal w3-padding-48">\
        <div class="w3-modal-content w3-card-4">\
            <header class="w3-container w3-theme w3-left-align">\
                <span action="close" class="w3-closebtn">&times;</span>\
                <h2><span class="fa fa-warning w3-text-red"></span></h2>\
            </header>\
            <div class="w3-container w3-left-align">\
                <p>Выбранные удобрения не рекомендуется использовать вместе - они либо нейтрализуют друг друга, либо вступают в реакцию и образуют плохорастворимые соединения</p>\
                <ul class="w3-ul"></ul>\
            </div>\
            <footer class="w3-container w3-theme">\
                <div class="w3-right-align w3-padding-16">\
                    <button action="close" class="w3-btn w3-light-grey">Отмена</button>\
                    <button onclick="SELECT_FERTILIZER_WINDOW.completeSaving();" class="w3-btn w3-theme-dark">Сохранить все равно</button>\
                </div>\
            </footer>\
        </div>\
    </div>');
SELECT_FERTILIZER_WINDOW.initialize = function (config) {
    SELECT_FERTILIZER_WINDOW.config = config;
    SELECT_FERTILIZER_WINDOW.pageSize = SELECT_FERTILIZER_WINDOW.config.pageSize ? SELECT_FERTILIZER_WINDOW.config.pageSize : 10;
    SELECT_FERTILIZER_WINDOW.appendTo(document.body);
    SELECT_FERTILIZER_WINDOW.CONFIRM_UNCOMPATIBLE.appendTo(document.body);
    SELECT_FERTILIZER_WINDOW.find("*[action='close']").click(function () {
        SELECT_FERTILIZER_WINDOW.hide();
    });
    SELECT_FERTILIZER_WINDOW.CONFIRM_UNCOMPATIBLE.find("*[action='close']").click(function () {
        SELECT_FERTILIZER_WINDOW.CONFIRM_UNCOMPATIBLE.hide();
    });
};
SELECT_FERTILIZER_WINDOW.processError = function (jqXHR, textStatus, errorThrown) {
    document.reloadPageOnError = false;
    var errorMsg = SELECT_FERTILIZER_WINDOW.find("div[name='errorMsg']");
    var infoMsg = SELECT_FERTILIZER_WINDOW.find("div[name='infoMsg']");

    var msg = jqXHR.responseJSON.message;

    switch (msg) {
        case "error.work.exists":
            msg = "Такая работа уже есть в Вашей библиотеке";
            break;
        case "error.work.required":
            msg = "Пожалуйста, заполните все необходимые поля";
            break;
    }
    infoMsg.hide();
    errorMsg.html("<p>" + msg + "</p>");
    errorMsg.slideDown("fast");
};
SELECT_FERTILIZER_WINDOW.fertilizerModalSelectedItems = {};
SELECT_FERTILIZER_WINDOW.skipValidationForFertilizersIds = [];
SELECT_FERTILIZER_WINDOW.SELECTED_FERTILIZERS = [];
SELECT_FERTILIZER_WINDOW.edit = function (fertilizers) {
    if (!SELECT_FERTILIZER_WINDOW.config) {
        return;
    }

    SELECT_FERTILIZER_WINDOW.fertilizerModalSelectedItems = {};

    if(fertilizers!=null) {
        SELECT_FERTILIZER_WINDOW.SELECTED_FERTILIZERS = fertilizers;
        for (var i = 0; i < fertilizers.length; i++) {
            var fertilizer = fertilizers[i];
            SELECT_FERTILIZER_WINDOW.fertilizerModalSelectedItems[fertilizer.id] = fertilizer.name;
        }
    }

    SELECT_FERTILIZER_WINDOW.pn = 0;
    SELECT_FERTILIZER_WINDOW.find('*[fertilizerSearch]').val('');
    SELECT_FERTILIZER_WINDOW.loadFertilizerModal(0, SELECT_FERTILIZER_WINDOW.pageSize, function () {
        SELECT_FERTILIZER_WINDOW.show();
    });
};
SELECT_FERTILIZER_WINDOW.saveSelectedFertilizersToSpeciesWork = function () {
    SELECT_FERTILIZER_WINDOW.selectedFertilizers = [];
    var selectedFertilizersIds = [];
    var selectedItems = SELECT_FERTILIZER_WINDOW.fertilizerModalSelectedItems;
    for (var fertilizerId in selectedItems) {
        if(SELECT_FERTILIZER_WINDOW.config.skipValidationForFertilizersIds.includes(parseInt(fertilizerId))) {
            continue;
        }
        SELECT_FERTILIZER_WINDOW.selectedFertilizers.push({
            id: parseInt(fertilizerId, 10),
            name : selectedItems[fertilizerId]
        });
        selectedFertilizersIds.push(parseInt(fertilizerId, 10));
    }
    SELECT_FERTILIZER_WINDOW.isIncompatibleFertilizersExists(
        selectedFertilizersIds,
        function(incompatibleFertilizers) {
            if(incompatibleFertilizers.length > 0) {
                var fertilizers = SELECT_FERTILIZER_WINDOW.CONFIRM_UNCOMPATIBLE.find("ul");
                fertilizers.html('');
                var counter = 0;
                for(var i=0;i<incompatibleFertilizers.length;i++) {
                    var incompatibleFertilizer = incompatibleFertilizers[i];
                    for(var j=0;j<selectedFertilizersIds.length;j++) {
                        if(selectedFertilizersIds[j]==incompatibleFertilizer.id) {
                            $("<li>" + incompatibleFertilizer.name + "</li>").appendTo(fertilizers);
                            counter ++;
                        }
                    }
                }
                if(counter==0) {
                    SELECT_FERTILIZER_WINDOW.completeSaving();
                } else {
                    SELECT_FERTILIZER_WINDOW.CONFIRM_UNCOMPATIBLE.show();
                }
            } else {
                SELECT_FERTILIZER_WINDOW.completeSaving();
            }
        }
    );
};

SELECT_FERTILIZER_WINDOW.completeSaving = function() {
    if (SELECT_FERTILIZER_WINDOW.config.persisDataFunc) {
        eval(SELECT_FERTILIZER_WINDOW.config.persisDataFunc)(
            SELECT_FERTILIZER_WINDOW.selectedFertilizers,
            function () {
                SELECT_FERTILIZER_WINDOW.hide();
                SELECT_FERTILIZER_WINDOW.CONFIRM_UNCOMPATIBLE.hide();
            }
        );
    } else {
        SELECT_FERTILIZER_WINDOW.hide();
        SELECT_FERTILIZER_WINDOW.CONFIRM_UNCOMPATIBLE.hide();
    }
};
SELECT_FERTILIZER_WINDOW.isIncompatibleFertilizersExists = function(selectedFertilizers, callback) {
    if(SELECT_FERTILIZER_WINDOW.config.disableValidation) {
        return callback([]);
    }
    if(selectedFertilizers.length==0) {
        return false;
    }

    $.ajax({
        type: "post",
        url: 'api/fertilizer/restrictions/',
        data: JSON.stringify(selectedFertilizers),
        contentType: "application/json; charset=utf-8",
        success: function (items) {
            callback(items);
        }
    });
};

SELECT_FERTILIZER_WINDOW.totalRows = 0;
SELECT_FERTILIZER_WINDOW.pn = 0;
SELECT_FERTILIZER_WINDOW.pageSize = 0;

SELECT_FERTILIZER_WINDOW.resetSearch = function () {
    SELECT_FERTILIZER_WINDOW.pn = 0;
    SELECT_FERTILIZER_WINDOW.find('*[fertilizerSearch]').val('').change();
};
SELECT_FERTILIZER_WINDOW.fertilizerModalLast = function () {
    var lastPageNum = Math.round(SELECT_FERTILIZER_WINDOW.totalRows / SELECT_FERTILIZER_WINDOW.pageSize);
    SELECT_FERTILIZER_WINDOW.fertilizerModalNext(lastPageNum, true)
};
SELECT_FERTILIZER_WINDOW.fertilizerModalNext = function (dPageNum, realPageNum) {
    var lastPageNum = Math.round(SELECT_FERTILIZER_WINDOW.totalRows / SELECT_FERTILIZER_WINDOW.pageSize);
    if (realPageNum == true) {
        SELECT_FERTILIZER_WINDOW.pn = dPageNum;
    } else {
        SELECT_FERTILIZER_WINDOW.pn += dPageNum;
    }
    if (SELECT_FERTILIZER_WINDOW.pn <= 0) {
        SELECT_FERTILIZER_WINDOW.pn = 0;
    } else if(SELECT_FERTILIZER_WINDOW.pn >= lastPageNum) {
        SELECT_FERTILIZER_WINDOW.pn = lastPageNum;
    }
    SELECT_FERTILIZER_WINDOW.loadFertilizerModal(SELECT_FERTILIZER_WINDOW.pn, SELECT_FERTILIZER_WINDOW.pageSize)
};
SELECT_FERTILIZER_WINDOW.drawFertilizerPageButtons = function () {
    var totalPages = Math.ceil(SELECT_FERTILIZER_WINDOW.totalRows / SELECT_FERTILIZER_WINDOW.pageSize);
    var pagination = SELECT_FERTILIZER_WINDOW.find("ul.w3-pagination");
    pagination.find("*").remove();

    $('<li><a href="#" onclick="SELECT_FERTILIZER_WINDOW.fertilizerModalNext(0, true);">&#10094;&#10094; Первая</a></li>\
           <li><a href="#" name="back" onclick="SELECT_FERTILIZER_WINDOW.fertilizerModalNext(-1, false);">&#10094; Назад</a></li>')
        .appendTo(pagination);
    for (var i = 0; i < totalPages; i++) {
        if (i == SELECT_FERTILIZER_WINDOW.pn) {
            $('<li><a class="w3-theme" href="#" onclick="SELECT_FERTILIZER_WINDOW.fertilizerModalNext(' + i + ', true);">' + (i + 1) + '</a></li>').appendTo(pagination);
        } else {
            $('<li><a href="#" onclick="SELECT_FERTILIZER_WINDOW.fertilizerModalNext(' + i + ', true);">' + (i + 1) + '</a></li>').appendTo(pagination);
        }
    }
    $('<li><a href="#" name="next" onclick="SELECT_FERTILIZER_WINDOW.fertilizerModalNext(1, false);">Вперед &#10095;</a></li>\
           <li><a href="#" onclick="SELECT_FERTILIZER_WINDOW.fertilizerModalLast();">Последняя &#10095;&#10095;</a></li>')
        .appendTo(pagination);
};
SELECT_FERTILIZER_WINDOW.loadFertilizerModal = function (pageNum, pageSize, callback) {
    var fertilizersItems = SELECT_FERTILIZER_WINDOW.find("*[fertilizerItems]");

    var url = "api/fertilizer/available/page?pageNum=" + pageNum + "&pageSize=" + pageSize + "&order.catalogName=asc&order.name=asc";
    var searchStr = SELECT_FERTILIZER_WINDOW.find('input[fertilizerSearch]').val();
    if (searchStr.trim() != '') {
        searchStr = "*"+encodeURIComponent(searchStr)+"*";
        url += "&query=(name contains '" + searchStr + "' or catalogName contains '" + searchStr + "')";
    }

    $.get(
        url,
        function (page) {
            var items = page.items;
            SELECT_FERTILIZER_WINDOW.totalRows = page.totalCount;

            SELECT_FERTILIZER_WINDOW.drawFertilizerPageButtons();

            fertilizersItems.html('');

            var previousCatalogName = null;
            var itemsCatalogNode = null;

            if (items.length == 0) {
                SELECT_FERTILIZER_WINDOW.find('*[name="nothingFound"]').show();
                return;
            }
            SELECT_FERTILIZER_WINDOW.find('*[name="nothingFound"]').hide();

            for (var i = 0; i < items.length; i++) {
                var item = items[i];

                var catalogName = item.catalogName;
                if (catalogName != previousCatalogName) {
                    var owner = item.catalogOwnerId==null ? "" : "&nbsp;<b>(моё)</b>"
                    itemsCatalogNode = $("<li><b>" + catalogName + owner + "</b></li>");
                    itemsCatalogNode.appendTo(fertilizersItems);
                }

                var fertilizerName = item.name;
                var owner = item.ownerId==null ? "" : "&nbsp;<b>(моё)</b>"
                var uiItem = $("<li class='w3-hover-light-grey' itemId=" + item.id + ">" + fertilizerName + owner + "</li>");
                if (SELECT_FERTILIZER_WINDOW.fertilizerModalSelectedItems[item.id] != null) {
                    uiItem.addClass("w3-green");
                }
                uiItem.appendTo(itemsCatalogNode);

                uiItem.click(function () {
                    var itemId = $(this).attr('itemId');
                    var itemName = $(this).html();
                    if ($(this).hasClass("w3-green")) {
                        $(this).removeClass("w3-green");
                        delete SELECT_FERTILIZER_WINDOW.fertilizerModalSelectedItems[itemId];
                    } else {
                        $(this).addClass("w3-green");
                        SELECT_FERTILIZER_WINDOW.fertilizerModalSelectedItems[itemId] = itemName;
                    }
                });
                previousCatalogName = catalogName;
            }

            if (callback) {
                callback();
            }
        }
    );
};

/*==============================================================================*/
/*=============================== FEEDBACK FORM  ===============================*/
/*==============================================================================*/
var FEEDBACK_FORM = new Object();
FEEDBACK_FORM.attachBehaviour = function(form) {
    FEEDBACK_FORM.errorMsg = form.find("div[name='errorMsg']");
    FEEDBACK_FORM.infoMsg = form.find("div[name='infoMsg']");
    form.submit(function(){
        var json = serializeFormToJsonTree(form);
        if(json.validation) {
            FEEDBACK_FORM.processError({responseJSON:{message:"error.feedback.required"}},null,null);
            return false;
        }
        $.ajax({
            url: "/api/feedback/",
            type: "post",
            data: JSON.stringify(json),
            contentType: "application/json; charset=utf-8",
            success: function (data, textStatus, jqXHR) {
                FEEDBACK_FORM.infoMsg.html("<p>Ваше сообщение отправлено</p>");
                FEEDBACK_FORM.infoMsg.show();
                FEEDBACK_FORM.errorMsg.hide();
                populateFormFromJsonTree(form, {
                    name:"",
                    email:"",
                    content:""
                }, null);
            },
            error: FEEDBACK_FORM.processError
        });
        return false;
    })
};

FEEDBACK_FORM.processError = function (jqXHR, textStatus, errorThrown) {
    var msg = jqXHR.responseJSON.message;

    switch (msg) {
        case "error.feedback.required":
            msg = "Пожалуйста, заполните все необходимые поля.";
            break;
        case "feedback.cantsendemail":
            msg = "Проблема с отправкой сообщения попробуйте связаться через почтовую систему <a href='mailto:feedback@egardening.ru'>feedback@egardening.ru</a>";
            break;
    }
    FEEDBACK_FORM.infoMsg.hide();
    FEEDBACK_FORM.errorMsg.html("<p>" + msg + "</p>");
    FEEDBACK_FORM.errorMsg.show();
};


/*==============================================================================*/
/*===========================   UPLOAD FILE FORM   =============================*/
/*==============================================================================*/
var UPLOAD_FILE_WINDOW = $(
    '<form class="w3-modal w3-padding-48" enctype="multipart/form-data">' +
    '    <div class="w3-modal-content w3-card-4 w3-center">' +
    '        <header class="w3-container w3-theme w3-left-align">' +
    '            <span action="close" class="w3-closebtn">&times;</span>' +
    '            <h2>Выберите изображение</h2>' +
    '        </header>' +
    '        <div class="w3-container w3-left-align">' +
    '                <p>' +
                        '<input class="w3-border w3-mobile" type="file" name="file" accept="*" required="required" style="width:80%"/>' +
    '            <p><label class="w3-text-grey w3-small">*Размер изображения не должен превышать 500Kb. Размер отображения на странице - 230х230px.</label></p>' +
    '        </div>' +
    '        <footer class="w3-container w3-theme">' +
    '            <div class="w3-right-align w3-padding-16">' +
    '                <button type="button" action="close" class="w3-btn w3-grey w3-mobile">Отмена</button>' +
    '                <button type="submit" class="w3-btn w3-theme-dark w3-mobile">Ок</button>' +
    '            </div>' +
    '        </footer>' +
    '    </div>' +
    '</form>');

UPLOAD_FILE_WINDOW.initialize = function (config) {
    UPLOAD_FILE_WINDOW.config = config;
    UPLOAD_FILE_WINDOW.appendTo(document.body);
    UPLOAD_FILE_WINDOW.find("*[action='close']").click(function () {
        UPLOAD_FILE_WINDOW.hide();
    });
    UPLOAD_FILE_WINDOW.submit(function(e){
        e.preventDefault();
        var formData = new FormData(this);
        var url = "/api/data-storage/SPECIES/"+UPLOAD_FILE_WINDOW.config.speciesId;
        if(UPLOAD_FILE_WINDOW.config.fileId) {
            url += "/"+UPLOAD_FILE_WINDOW.config.fileId;
        }
        $.ajax({
            url: url,
            type: "post",
            data: formData,
            enctype: 'multipart/form-data',
            contentType: false,
            cache : false,
            processData: false,
            success: function (data) {
                UPLOAD_FILE_WINDOW.hide();
                if(UPLOAD_FILE_WINDOW.config.callback) {
                    UPLOAD_FILE_WINDOW.config.callback();
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                if(textStatus=='error') {
                    alert(errorThrown);
                } else {
                    alert(jqXHR.responseJSON.message);
                }

            }
        });
        return false
    });
};

UPLOAD_FILE_WINDOW.open = function () {
    UPLOAD_FILE_WINDOW.show();
};
UPLOAD_FILE_WINDOW.display = function (userTaskId, taskName) {
    UPLOAD_FILE_WINDOW.show();
};

/*==============================================================================*/
/*===========================   FAST CREATE PLAN   =============================*/
/*==============================================================================*/
var FAST_PLAN_WINDOW = $(
    '<form class="w3-modal w3-padding-48">\
        <div class="w3-modal-content w3-card-4 w3-center">\
            <header class="w3-container w3-theme w3-left-align">\
                <span action="close" class="w3-closebtn">&times;</span>\
                <h2>Название_выбранного_метода</h2>\
            </header>\
            <div class="w3-container w3-left-align">\
            <p><label>Когда бы Вы хотели начать?</label>\
                <input class="w3-input w3-border" type="date" name="startDate" required="required"/></p>\
            </div>\
        <footer class="w3-container w3-theme">\
            <div class="w3-right-align w3-padding-16">\
                <button action="close" type="button" class="w3-btn w3-grey w3-mobile">Отмена</button>\
                <button onclick="" class="w3-btn w3-theme-dark w3-mobile">Рассчитать план</button>\
            </div>\
        </footer>\
        </div>\
    </form>');

FAST_PLAN_WINDOW.initialize = function (config) {
    FAST_PLAN_WINDOW.config = config;
    FAST_PLAN_WINDOW.config.opened = false;

    FAST_PLAN_WINDOW.appendTo(document.body);
    FAST_PLAN_WINDOW.find("*[action='close']").click(function () {
        FAST_PLAN_WINDOW.close();
    });
    FAST_PLAN_WINDOW.submit(function(e){
        e.preventDefault();
        var formData = $(this).serialize();
        doActionIfAnonCreateAndLogin(function(){
            $.ajax({
                url: "/api/user/work/pattern/"+FAST_PLAN_WINDOW.config.patternId,
                type: "post",
                data: formData,
                success: function (data) {
                    FAST_PLAN_WINDOW.close();
                    if(FAST_PLAN_WINDOW.config.callback) {
                        FAST_PLAN_WINDOW.config.callback();
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if(textStatus == 'error') {
                        alert(errorThrown);
                    } else {
                        alert(jqXHR.responseJSON.message);
                    }

                }
            });
        });
        return false
    });
};
FAST_PLAN_WINDOW.close = function() {
    FAST_PLAN_WINDOW.config.opened = false;
    FAST_PLAN_WINDOW.hide();
};
FAST_PLAN_WINDOW.open = function () {
    FAST_PLAN_WINDOW.config.opened = true;
    FAST_PLAN_WINDOW.find("header>h2").html(FAST_PLAN_WINDOW.config.title);
    FAST_PLAN_WINDOW.find("input").html("");
    FAST_PLAN_WINDOW.show();
};


/*==============================================================================*/
/*=============================   CREATE PLANT   ===============================*/
/*==============================================================================*/
var CREATE_PLANT_WINDOW = $(
    '<form class="w3-modal w3-padding-48">\
        <div class="w3-modal-content w3-card-4" style="width:600px !important;">\
            <header class="w3-container w3-theme w3-left-align">\
                <span action="close" class="w3-closebtn">&times;</span>\
                <h2>Добавить участок</h2>\
            </header>\
            <div class="w3-container w3-left-align" style="margin-top:15px;">\
                <input type="hidden" name="id" />\
                <p><label>Название:</label><input class="w3-right" type="text" name="name" required maxlength="100" placeholder="Деревня, дачный поселок, за домом, перел террасой, и т. д."></p>\
                <p><label>Длинна(м):</label><input class="w3-right" type="number" name="width" required min="0" maxlength="5" placeholder="20"></p>\
                <p><label>Ширина(м):</label><input class="w3-right" type="number" name="height" required min="0" maxlength="5" placeholder="20"></p>\
            </div>\
            <footer class="w3-container w3-theme">\
                <div class="w3-right-align w3-padding-16">\
                    <button class="w3-btn w3-grey" action="close">Отмена</button>\
                    <button class="w3-btn w3-theme-dark" onclick="">Сохранить</button>\
                </div>\
            </footer>\
        </div>\
    </form>');

CREATE_PLANT_WINDOW.initialize = function (config) {
    CREATE_PLANT_WINDOW.config = config;
    CREATE_PLANT_WINDOW.config.opened = false;

    CREATE_PLANT_WINDOW.appendTo(document.body);
    CREATE_PLANT_WINDOW.find("*[action='close']").click(function () {
        CREATE_PLANT_WINDOW.close();
    });
    CREATE_PLANT_WINDOW.submit(function(e){
        e.preventDefault();
        var json = serializeFormToJsonTree($(this));
        json.width = json.width * config.scale;
        json.height = json.height * config.scale;
        doActionIfAnonCreateAndLogin(function(){
            $.ajax({
                url: "/api/plant/",
                type: "post",
                data: JSON.stringify(json),
                contentType: "application/json; charset=utf-8",
                success: function (data) {
                    //redirect to href="edit_my_plant.tiles"
                    CREATE_PLANT_WINDOW.close();
                    if(CREATE_PLANT_WINDOW.config.callback) {
                        CREATE_PLANT_WINDOW.config.callback();
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if(textStatus == 'error') {
                        alert(errorThrown);
                    } else {
                        alert(jqXHR.responseJSON.message);
                    }

                }
            });
        });
        return false
    });
};
CREATE_PLANT_WINDOW.close = function() {
    CREATE_PLANT_WINDOW.config.opened = false;
    CREATE_PLANT_WINDOW.hide();
};
CREATE_PLANT_WINDOW.open = function (plantId) {
    CREATE_PLANT_WINDOW.config.opened = true;
    var title = "Создать участок";
    if(plantId) {
        title = "Редактировать участок";
    }
    CREATE_PLANT_WINDOW.find("header>h2").html(title);
    CREATE_PLANT_WINDOW.show();
};
/*==============================================================================*/
/*==========================   CREATE PLANT REGION  ============================*/
/*==============================================================================*/
var CREATE_PLANT_REGION_WINDOW = $(
    '<form class="w3-modal w3-padding-48">\
        <div class="w3-modal-content w3-card-4">\
            <header class="w3-container w3-theme w3-left-align">\
                <span action="close" class="w3-closebtn">&times;</span>\
                <h2>Новая клумба</h2>\
            </header>\
            <div class="w3-container w3-left-align">\
                <p><label>Название:</label><input name="title" type="text"></p>\
                <p>\
                    <input class="w3-radio" type="radio" name="type" value="bed-circle" checked> Круглая\
                    <input class="w3-radio" type="radio" name="type" value="bed-rect"> Квадратная\
                </p>\
                <p>\
                    <label bed-circle><input type="text" name="radius" placeholder="Радиус">м</label>\
                    <label bed-rect style="display:none"><input type="text" name="width" placeholder="Длина">м <input type="text" name="height" placeholder="Ширина">м</label>\
                </p>\
            </div>\
            <footer class="w3-container w3-theme">\
                <div class="w3-right-align w3-padding-16">\
                    <button type="button" action="close" class="w3-btn w3-grey">Отмена</button>\
                    <button class="w3-btn w3-theme-dark" onclick="">Сохранить</button>\
                </div>\
            </footer>\
        </div>\
    </form>');

CREATE_PLANT_REGION_WINDOW.initialize = function (config) {
    CREATE_PLANT_REGION_WINDOW.config = config;
    CREATE_PLANT_REGION_WINDOW.config.opened = false;

    CREATE_PLANT_REGION_WINDOW.appendTo(document.body);
    CREATE_PLANT_REGION_WINDOW.find("*[action='close']").click(function () {
        CREATE_PLANT_REGION_WINDOW.close();
    });

    CREATE_PLANT_REGION_WINDOW.find("input[name='type']").click(function(el){
        CREATE_PLANT_REGION_WINDOW.find("label > input").parent().hide();
        var type = $(this).val();
        CREATE_PLANT_REGION_WINDOW.find("label[" + type + "]").show();
    });

    CREATE_PLANT_REGION_WINDOW.submit(function(e){
        e.preventDefault();
        var json = serializeFormToJsonTree($(this));
        if(json.width) {
            json.width = json.width * config.scale
            json.height = json.height * config.scale
        } else if(json.radius) {
            json.radius = json.radius * config.scale
        }
        if(config.callback) {
            config.callback(json)
        }
        CREATE_PLANT_REGION_WINDOW.close();
        return false
    });
};
CREATE_PLANT_REGION_WINDOW.close = function() {
    CREATE_PLANT_REGION_WINDOW.config.opened = false;
    CREATE_PLANT_REGION_WINDOW.hide();
};
CREATE_PLANT_REGION_WINDOW.open = function () {
    CREATE_PLANT_REGION_WINDOW.config.opened = true;
    CREATE_PLANT_REGION_WINDOW.show();
};

/*==============================================================================*/
/*==============================================================================*/

function escapeRegExp(str) {
    return str.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
}
function replaceAll(str, find, replace) {
    return str.replace(new RegExp(escapeRegExp(find), 'g'), replace);
}

var defaultDateFormat = 'yyyy-MM-dd';
function parseDate(value, pattern) {
    if(value) {
        var format = defaultDateFormat;
        if (pattern) {
            format = pattern;
        }
        return Date.parseExact(value, format);
    }
    return null;
}
function formatDate(value, pattern) {
    if(pattern) {
        var date = Date.parseExact(value, defaultDateFormat);
        return date.toString(pattern);
    }
    return value.toString(defaultDateFormat);
}


function processTasksYear(patternTasks) {
    if(patternTasks.length==0) {
        return;
    }
    var firstTask = patternTasks[0];
    var lastTask = patternTasks[patternTasks.length-1];
    var firstTaskCalculationDate = parseDate(firstTask.calculatedDate);
    var firstTaskYear = firstTaskCalculationDate.getFullYear();
    var lastTaskCalculationDate = parseDate(lastTask.calculatedDate);
    var lastTaskYear = lastTaskCalculationDate.getFullYear();
    var now = new Date();
    var currentYear = now.getFullYear();
    var isLastTaskCalculationDateExpired = now.getTime() > lastTaskCalculationDate.getTime();
    var deltaYear = currentYear - firstTaskYear;
    if(isLastTaskCalculationDateExpired) {
        for (var i = 0; i < patternTasks.length; i++) {
            var task = patternTasks[i];
            var stDate = parseDate(task.startDate);
            var enDate = parseDate(task.endDate);
            var calcDate = parseDate(task.calculatedDate);

            stDate.setYear(stDate.getFullYear()+deltaYear);
            enDate.setYear(enDate.getFullYear()+deltaYear);
            calcDate.setYear(calcDate.getFullYear()+deltaYear);

            task.startDate = formatDate(stDate);
            task.endDate = formatDate(enDate);
            task.calculatedDate = formatDate(calcDate);
        }
    }

    return deltaYear;
}

function loadSpeciesImages(conf, callback) {
    $.get(
        "api/data-storage/files?query=(associatedEntityType eq 'SPECIES' and associatedEntityId eq " + conf.speciesId + ")",
        function(items) {
            callback(items, conf)
        }
    );
}

function addDeltaYear(dateStr, delta) {
    if (delta) {
        var date = parseDate(dateStr);
        date.setYear(date.getFullYear() + delta);
        return formatDate(date);
    }
    return dateStr;
}

function makeid() {
    var text = "";
    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for (var i = 0; i < 5; i++)
        text += possible.charAt(Math.floor(Math.random() * possible.length));

    return text;
}

scrollInProgress = false;

function animatedScrollTo(element, toX, toY, duration) {
    if(scrollInProgress) {
        return;
    }

    scrollInProgress = true;
    var
        startX = element.scrollLeft,
        startY = element.scrollTop,
        changeX = toX - startX,
        changeY = toY - startY,
        currentTime = 0,
        increment = 20;

    var animateScroll = function(){
        currentTime += increment;
        var valX = Math.easeInOutQuad(currentTime, startX, changeX, duration);
        var valY = Math.easeInOutQuad(currentTime, startY, changeY, duration);
        element.scrollLeft = valX;
        element.scrollTop = valY;
        if(currentTime < duration) {
            setTimeout(animateScroll, increment);
        } else {
            scrollInProgress = false;
        }
    };
    animateScroll();
}

//t = current time
//b = start value
//c = change in value
//d = duration
Math.easeInOutQuad = function (t, b, c, d) {
    t /= d/2;
    if (t < 1) return c/2*t*t + b;
    t--;
    return -c/2 * (t*(t-2) - 1) + b;
};