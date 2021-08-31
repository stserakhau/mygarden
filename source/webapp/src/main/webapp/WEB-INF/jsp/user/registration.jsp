<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="w3-container w3-lime w3-left-align w3-margin-bottom w3-text-white">
    <h1>Регистрация пользователя</h1>
</div>

    <div class=" w3-container w3-content">
        <form id="registrationForm" class="w3-container w3-padding  w3-card-4 w3-left-align">

            <div id="warnMsg" style="display: none;" class="w3-panel w3-leftbar w3-border-red w3-pale-red w3-card-2">
                <p>Пожалуйста, заполните все необходимые поля</p>
            </div>

            <div id="errorMsg" style="display: none;" class="w3-panel w3-leftbar w3-border-red w3-pale-red w3-card-2">
            </div>

            <p><label class="w3-label">Логин/E-mail<span class="w3-text-red">*</span></label>
                <input name="user.email" class="w3-input" type="email" required="required"></p>

            <p><label class="w3-label">Имя<span class="w3-text-red">*</span></label>
            <input name="userProfile.firstName" class="w3-input" type="text" required="required"></p>

            <p><label class="w3-label">Фамилия</label>
            <input name="userProfile.lastName" class="w3-input" type="text"></p>

            <p><label class="w3-label">Страна</label>
                <input list="countries" id="country" name="country" class="w3-select" onblur="loadCities('city', $('#country').val())">
                <datalist id="countries">
                </datalist>
            </p>

            <p><label class="w3-label">Город</label>
            <input list="cities" id="city" name="city" class="w3-select">
            <datalist id="cities">
            </datalist>
                <p class="w3-small w3-text-grey">*В первый месяц после указания Вашего местоположения возможно некорректное определение даты выполнения работ, если Вы задали его как зависимость от погоды. Если при регистрации Вы не указываете местоположение - месяц некорректных расчетов будет начинаться с момента его задания. Приносим свои извинения.</p>
            </p>

            <p><label class="w3-label">Вы используете приложение для...</label>
                <input list="occupations" name="userProfile.occupation" class="w3-select">
                <datalist id="occupations">
                    <option value="Своего сада и огорода"></option>
                    <option value="Питомника"></option>
                    <option value="Фермерского хозяйства"></option>
                </datalist>
            </p>

            <p><input name="userProfile.userAgreementAccepted" class="w3-check" type="checkbox" required="required">
            <label class="w3-label">Я принимаю условия <a href="user_agreement.tiles" target="userAgreement">пользовательского соглашения</a></label></p>
            <p><input name="userProfile.newsletter" class="w3-check" type="checkbox" required="required" checked="checked">
                <label class="w3-label">Я согласен получать новостную рассылку (1 раз в неделю)</label></p>
            <p>
                <button type="submit" class="w3-btn w3-theme-dark w3-right w3-margin-left">Зарегистрировать</button>
            </p>
        </form>
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
            <p>К сожалению, мы не можем найти Ваш город. Разрешите нам определить Ваше географическое местоположение по Вашему IP адресу?</p>

        </div>
        <footer class="w3-container w3-theme">
            <div class="w3-right-align w3-padding-16">
                <button type="button" onclick="$('#confirmLocationByIp').hide();"
                        class="w3-btn w3-light-grey w3-hover-white">Сохранить без местопольжения
                </button>
                <button type="button" onclick="findLocationByIP();" class="w3-btn w3-theme-dark w3-hover-white">
                    Разрешить определить местоположение по IP адресу
                </button>
            </div>
        </footer>
    </div>
</div>

<script type="text/javascript">
    document.afterInitializationUIEnvironmentFunc = function () {
        loadCountries("country");

        var user = getUser();
        if(user) {
            $("#registrationForm #country").val(user.countryName);
            $("#registrationForm #city").val(user.cityName);
        } else {
            $("#registrationForm #country").val("");
            $("#registrationForm #city").val("");
        }

        $("#registrationForm").submit(function () {
            $("#warnMsg").hide();
            $("#errorMsg").hide();

            var json = serializeFormToJsonTree($(this));
            if(json.validate){
                $("#warnMsg").show();
                return false;
            }
            $.ajax({
                type: "POST",
                url: '/api/users/register',
                data: JSON.stringify(json),
                contentType: "application/json; charset=utf-8",
                success: function (data) {
                    location.href = 'user_registration_successful.tiles';
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    var json = JSON.parse(jqXHR.responseText);
                    var errMsg = $("#errorMsg");
                    switch (json.message) {
                        case "user.exists" :
                            errMsg.html("<p>Пользователь с таким E-mail уже зарегистрирован</p>");
                            break;
                        default :
                            errMsg.html("<p>" + jqXHR.responseText + "</p>");
                            break;
                    }
                    errMsg.show();
                }
            });
            return false;
        });
    }
</script>