<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div id="pageHeader" class="w3-container w3-lime w3-left-align w3-margin-bottom w3-text-white">
    <h1>Настройки профиля</h1>
</div>
<div class="w3-row-padding w3-container">
    <div class="w3-quarter w3-container">
        <ul id="tabsNav" class="w3-ul w3-right-align">
            <li name="personalInfo" class="w3-theme w3-padding w3-hover-light-grey" onclick="showTab('personalInfo')">Персональные данные</li>
            <li name="changePassword" class="w3-padding w3-hover-light-grey" onclick="showTab('changePassword')">Смена пароля</li>
            <li name="accountSettings" class="w3-padding w3-hover-light-grey" onclick="showTab('notificationSettings')">Настройки уведомлений</li>
            <li name="subscription" class="w3-padding w3-hover-light-grey w3-hide" onclick="showTab('subscription')">Подписка</li>
        </ul>

    </div>
    <div id="tabs" class="w3-half w3-container">
        <div id="updateSuccessMsg" style="display: none;" class="w3-panel w3-leftbar w3-border-green w3-pale-green w3-card-2">
            <p>Информация сохранена успешно</p>
        </div>

        <form name="personalInfo" method="put" action="api/users/current/" style="display: none;" class="w3-container w3-padding w3-card-8 w3-left-align">
            <div name="formMessage" style="display: none;" class="w3-panel w3-leftbar w3-border-red w3-pale-red w3-card-2">
                <p>Пожалуйста, заполните все необходимые поля</p>
            </div>

            <p><label class="w3-label w3-disabled">Логин/E-mail<span class="w3-text-red">*</span></label>
                <input name="user.email" class="w3-input w3-disabled" type="email" required="required" readonly></p>

            <p><label class="w3-label">Имя<span class="w3-text-red">*</span></label>
                <input name="userProfile.firstName" class="w3-input" type="text" required="required"></p>

            <p><label class="w3-label">Фамилия</label>
                <input name="userProfile.lastName" class="w3-input" type="text"></p>

            <p><label class="w3-label">Страна</label>
                <input list="countries" id="country" name="country" class="w3-select" onblur="loadCities('city', $('#country').val())">
                <datalist id="countries">
                    <option value="Австралия"></option>
                    <option value="Австрия"></option>
                    <option value="Азербайджан"></option>
                    <option value="..."></option>
                    <option value="Беларусь"></option>
                    <option value="..."></option>
                    <option value="Россия"></option>
                    <option value="..."></option>
                </datalist>
            </p>

            <p><label class="w3-label">Город</label>
                <input list="cities" id="city" name="city" class="w3-select">
                <datalist id="cities">
                    <option value="Гомель"></option>
                    <option value="Гродно"></option>
                    <option value="Витебск"></option>
                    <option value="Могилев"></option>
                    <option value="Минск"></option>
                    <option value="..."></option>
                </datalist>
            <p class="w3-small w3-text-grey">*В первый месяц после указания Вашего местоположения возможно некорректное определение даты выполнения работ, если Вы указываете его как зависимость от погоды. Более того, так как Ваше местоположение является необязательным атрибутом при регистрации и может быть указано позже, месяц некорректных расчетов будет начинаться с момента его указания. Приносим свои извинения.</p>

            </p>

            <p><label class="w3-label">Вы используете приложение для...</label>
                <input list="occupations" name="userProfile.occupation" class="w3-select">
                <datalist id="occupations">
                    <option value="Своего сада и огорода"></option>
                    <option value="Питомника"></option>
                    <option value="Фермерского хозяйства"></option>
                </datalist>
            </p>

            <p>
                <label class="w3-label"><a href="user_agreement.tiles" target="userAgreement">Пользовательское соглашение</a></label>
            </p>

            <p>
                <button type="submit" class="w3-btn w3-theme-dark w3-right w3-margin-left  w3-mobile">Сохранить</button>
                <button type="button" onclick="showTab('personalInfo')"
                        class="w3-right w3-btn w3-light-grey w3-hover-white w3-mobile">Отмена
                </button>
            </p>
        </form>

        <form name="changePassword" style="display: none;" class="w3-container w3-padding  w3-card-8 w3-left-align">
            <div name="formMessage" style="display: none;"
                 class="w3-panel w3-leftbar w3-border-red w3-pale-red w3-card-2">
                <p></p>
            </div>

            <p><label class="w3-label">Старый пароль<span class="w3-text-red">*</span></label>
                <input name="oldPassword" class="w3-input" type="password" required="required"></p>
            <p><label class="w3-label">Новый пароль<span class="w3-text-red">*</span></label>
                <input name="newPassword" class="w3-input" type="password" required="required"></p>
            <p><label class="w3-label">Повторите новый пароль<span class="w3-text-red">*</span></label>
                <input name="retypeNewPassword" class="w3-input" type="password" required="required"></p>
            <p>
                <input class="w3-btn w3-theme-dark w3-right w3-margin-left" type="submit" value="Сохранить">
                <button type="button" onclick="showTab('personalInfo')"
                        class="w3-right w3-btn w3-light-grey w3-hover-white w3-mobile">Отмена
                </button>
            </p>

        </form>

        <form name="notificationSettings" method="put" action="api/users/current/notification_settings" style="display: none;" class="w3-container w3-padding  w3-card-8 w3-left-align">
            <div name="formMessage" class="w3-panel w3-leftbar w3-border-red w3-pale-red w3-card-2" style="display: none;">
                <p>Пожалуйста, заполните все необходимые поля</p>
            </div>

            <p><input id="jobNotificationCB" control="input[name='jobNotificationDaysBefore']" class="w3-check" type="checkbox">
                <label for="jobNotificationCB" class="w3-label">Получать уведомления о предстоящих работах за <input name="jobNotificationDaysBefore" required="required" type="number" min="1" max="3" style="width:50px"> дней</label>
            </p>
            <%--<p><input id="temperatureNotificationCB" control="input[name='temperatureLowLevelNotification'], input[name='temperatureLowLevelNotificationDaysBefore']" class="w3-check" type="checkbox">
                <label for="temperatureNotificationCB" class="w3-label">Получать уведомления о температуре ниже <input name="temperatureLowLevelNotification" required="required" type="number" min="-10" max="30" style="width:50px"> &#x2103; за
                    <input name="temperatureLowLevelNotificationDaysBefore" required="required" type="number" min="1" max="3" style="width:50px"> дней</label>
            </p>--%>
            <p><input id="newsletter" name="newsletter" class="w3-check" type="checkbox">
                <label for="newsletter" class="w3-label">Получать новостную рассылку (1 раз в неделю)</label>
            </p>
            <p>
                <button class="w3-btn w3-theme-dark w3-right w3-margin-left" type="submit">Сохранить</button>
                <button type="button" onclick="showTab('notificationSettings')"
                        class="w3-right w3-btn w3-light-grey w3-hover-white w3-mobile">Отмена
                </button>
            </p>
        </form>

        <form name="subscription" method="post" action="" style="display: none;" class="w3-container w3-padding  w3-card-8 w3-left-align">
            <p><label class="w3-label">Скоро...</label>
            <p>
                <input class="w3-btn w3-theme-dark w3-right w3-margin-left" type="button" value="Выбрать подписку">
            </p>
        </form>
    </div>

    <div class="w3-quarter w3-container w3-card w3-border-0">
        <p></p>
    </div>
</div>

<!-- Confirm finding location by IP-->
<div id="confirmLocationByIp" class="w3-modal w3-padding-48">
    <div class="w3-modal-content w3-card-4">
        <header class="w3-container w3-theme w3-left-align">
            <span onclick="$('#confirmLocationByIp').hide();" class="w3-closebtn">&times;</span>
            <h2><span class="fa fa-question-circle w3-text-white"></span></h2>
        </header>
        <div class="w3-container w3-left-align">
            <p>К сожалению, мы не можем найти Ваш город. Разрешите нам определить Ваше географическое местоположение по IP адресу?</p>

        </div>
        <footer class="w3-container w3-theme">
            <div class="w3-right-align w3-padding-16">
                <button onclick="$('#confirmLocationByIp').hide();" class="w3-btn w3-light-grey w3-hover-white">Сохранить без местоположения</button>
                <button onclick="findLocationByIP();" class="w3-btn w3-theme-dark w3-hover-white">Разрешить</button>
            </div>
        </footer>
    </div>
</div>

<script type="text/javascript">
    document.afterInitializationUIEnvironmentFunc = function () {
        doAction(function () {
            $("form[name='personalInfo']").submit(function () {
                var form = $(this);
                var formMsg = $(this).find("div[name='formMessage']").hide();

                var json = serializeFormToJsonTree(form);

                if (json.validation) {
                    formMsg.addClass("w3-border-red").addClass("w3-pale-red").find("p").html("Пожалуйста, заполните все необходимые поля");
                    return false;
                }
                $.ajax(
                        {
                            url: form.attr("action"),
                            type: "PUT",
                            data: JSON.stringify(json),
                            contentType: "application/json; charset=utf-8",
                            success: function (data, textStatus, jqXHR) {
                                formMsg.removeClass("w3-border-red").removeClass("w3-pale-red");
                                formMsg.addClass("w3-border-theme").addClass("w3-theme-l5");
                                formMsg.find("p").html("Данные сохранены");
                                formMsg.slideDown("fast");
                                resetUserInSessionStore();
                            },
                            error: function (jqXHR, textStatus, errorThrown) {
                                formMsg.removeClass("w3-border-theme").removeClass("w3-theme-l5");
                                formMsg.addClass("w3-border-red").addClass("w3-pale-red");
                                formMsg.find("p").html(jqXHR.responseJSON.message);
                                formMsg.slideDown("fast");
                            }
                        });
                return false;
            });

            $("form[name='changePassword']").submit(function () {
                var form = $(this);
                var formMsg = $(this).find("div[name='formMessage']").hide();

                var inputOldPass = form.find("input[name='oldPassword']");
                var inputNewPass = form.find("input[name='newPassword']");
                var inputRetypePass = form.find("input[name='retypeNewPassword']");

                if (inputNewPass.val() != inputRetypePass.val()) {
                    formMsg.removeClass("w3-border-green").removeClass("w3-pale-green");
                    formMsg.addClass("w3-border-red").addClass("w3-pale-red");
                    formMsg.find("p").html("Пароль не совпадает");
                    formMsg.slideDown("fast");
                    inputNewPass.focus();
                    return false;
                }

                $.ajax(
                        {
                            url: "api/userProfile/resetPassword",
                            type: "POST",
                            data: form.serializeArray(),
                            success: function (data, textStatus, jqXHR) {
                                formMsg.removeClass("w3-border-red").removeClass("w3-pale-red");
                                formMsg.addClass("w3-border-green").addClass("w3-pale-green");
                                formMsg.find("p").html("Пароль изменен");
                                formMsg.slideDown("fast");
                                inputOldPass.focus();
                                inputOldPass.val("");
                                inputNewPass.val("");
                                inputRetypePass.val("");
                            },
                            error: function (jqXHR, textStatus, errorThrown) {
                                formMsg.removeClass("w3-border-green").removeClass("w3-pale-green");
                                formMsg.addClass("w3-border-red").addClass("w3-pale-red");
                                var msg = jqXHR.responseJSON.message;

                                switch (msg) {
                                    case "userProfile.changePassword.invalid":
                                        msg = "Неверный пароль";
                                        break;
                                }
                                formMsg.find("p").html(msg);
                                formMsg.slideDown("fast");
                                inputOldPass.focus();
                                document.reloadPageOnError = false;
                            }
                        });
                return false;
            });

            $("form[name='notificationSettings']").submit(function () {
                var form = $(this);
                var formMsg = $(this).find("div[name='formMessage']").hide();

                var json = serializeFormToJsonTree(form);
                $.ajax(
                        {
                            url: form.attr("action"),
                            type: "PUT",
                            data: JSON.stringify(json),
                            contentType: "application/json; charset=utf-8",
                            success: function (data, textStatus, jqXHR) {
                                formMsg.removeClass("w3-border-red").removeClass("w3-pale-red");
                                formMsg.addClass("w3-border-green").addClass("w3-pale-green");
                                formMsg.find("p").html("Данные сохранены");
                                formMsg.slideDown("fast");
                            },
                            error: function (jqXHR, textStatus, errorThrown) {
                                formMsg.removeClass("w3-border-green").removeClass("w3-pale-green");
                                formMsg.addClass("w3-border-red").addClass("w3-pale-red");
                                formMsg.find("p").html(jqXHR.responseJSON.message);
                                formMsg.slideDown("fast");
                            }
                        });
                return false;
            });

            $("#jobNotificationCB, #temperatureNotificationCB").change(function(){
                var checked = $(this).prop('checked');
                var controlsSelector = $(this).attr('control')
                if(checked) {
                    $(controlsSelector).removeAttr("disabled");
                } else {
                    $(controlsSelector).attr("disabled", "disabled").val('');
                }
            });

            showTab('personalInfo');
        });
    };

    function showTab(tabId){
        $.each(
                $("#tabs form"),
                function(i, item) {
                    var it = $(item);
                    var name = it.attr("name");
                    var tabNav = $("#tabsNav li[name='"+name+"']");
                    if(name==tabId) {
                        eval(tabId)(function(){
                            it.find("div[name='formMessage']").hide();
                            it.show();
                            tabNav.addClass("w3-theme");
                        });
                    } else {
                        it.hide();
                        tabNav.removeClass("w3-theme");
                    }
                }
        );
    }

    function personalInfo(callback) {
        var form = $("#tabs form[name='personalInfo']");
        $.get(
                "api/users/current/",
                function(data) {
                    populateFormFromJsonTree(form, data, "");

                    loadCountries("country");
                    loadCities('city', data.country);

                    callback();
                }
        );
    }
    function changePassword(callback) {
        callback();
    }
    function notificationSettings(callback) {
        var form = $("#tabs form[name='notificationSettings']");
        $.get(
                "api/users/current/notification_settings",
                function(data) {
                    populateFormFromJsonTree(form, data, "");

                    $("#jobNotificationCB").prop("checked", data.jobNotificationDaysBefore!=null);
                    $("#temperatureNotificationCB")
                            .prop("checked", (data.temperatureLowLevelNotification!=null));
                    $("#newsletter")
                            .prop("checked", (data.newsletter));


                    $("#jobNotificationCB, #temperatureNotificationCB").change();
                    callback();
                }
        );
    }
    function subscription(callback) {
        callback();
    }
</script>