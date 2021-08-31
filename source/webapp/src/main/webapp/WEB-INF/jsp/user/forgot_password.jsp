<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="w3-container w3-lime w3-left-align w3-margin-bottom w3-text-white">
    <h1 id="forgotPasswordPageTitle">Забыли пароль?</h1>
</div>
<div class="w3-row-padding w3-container">

        <div id="forgotPasswordSuccessMsg" style="display: none;">
            <div class="w3-container w3-lime w3-text-white w3-margin-bottom">
                <h1 class="w3-xxxlarge" style="padding-top: 124px">Письмо для сброса пароля отправлено</h1>
                <h4 class="w3-xlarge"> Осталось всего несколько шагов для установки пароля</h4>
                <span class="fa fa-arrow-circle-down w3-jumbo" style="padding-top: 70px"></span>
            </div>

            <div class="w3-row-padding w3-container">
                <div class="w3-quarter w3-container w3-border-0 w3-card">
                    <p></p>
                </div>

                <div class="w3-half w3-container w3-left-align w3-padding-top">
                    <div class="w3-row w3-margin-top w3-border-theme w3-padding-32" style="border-bottom-style: dotted">
                        <span class="fa fa-envelope-o w3-jumbo w3-margin-right w3-left w3-text-theme" style="height:100%; width:20%"></span>
                        <span class="w3-xlarge w3-text-theme" style="width:80%">Откройте Ваш почтовый ящик</span><br>
                        <span style="width:80%"> Проверьте входящую почту в почтовом ящике, который Вы только что указали для сброса пароля</span>
                    </div>
                    <div class="w3-row w3-margin-top w3-border-theme w3-padding-32" style="border-bottom-style: dotted">
                        <span class="fa fa-file-text-o w3-jumbo w3-margin-right w3-left w3-text-theme" style="height:100%; width:20%"></span>
                        <span class="w3-xlarge w3-text-theme" style="width:80%">Откройте письмо о сбросе пароля</span><br>
                        <span style="width:80%">  Откройте письмо "Восстановление пароля eGardening.ru", отправитель
                info@egardening.ru. Если письма нет в списке входящих, проверьте спам</span>
                    </div>
                    <div class="w3-row w3-margin-top w3-border-theme w3-padding-32" style="border-bottom-style: dotted">
                        <span class="fa fa-external-link w3-jumbo w3-margin-right w3-left w3-text-theme" style="height:100%; width:20%"></span>
                        <span class="w3-xlarge w3-text-theme" style="width:80%">Перейдите по ссылке в письме</span><br>
                        <span style="width:80%">Нажмите на ссылку в письме для перехода. Введите новый пароль</span>
                    </div>
                    <p class="w3-center"><a class="w3-text-blue" href="index.jsp">На главную</a></p>

                </div>



            </div>
        </div>

        <form id="forgotPasswordForm" class="w3-container w3-padding  w3-card-4 w3-left-align">
            <div id="warnMag" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2">
                <p>Пожалуйста, введите Email, с которым Вы зарегистрированы в системе</p>
            </div>
            <div id="errorMsg" style="display: none;" class="w3-panel w3-leftbar w3-border-red w3-pale-red w3-card-2">
                <p></p>
            </div>
            <p>
                <label class="w3-label">E-mail<span class="w3-text-red">*</span></label>
                <input name="email" class="w3-input" type="email" required="required">
            </p>
            <p>
                <input class="w3-btn w3-theme-dark w3-right w3-margin-left" type="submit" value="Выслать новый пароль">
                <a href="index.jsp" class="w3-btn w3-grey w3-right w3-margin-left">Отмена</a>
            </p>
        </form>
    </div>

</div>

<script type="text/javascript">
    document.afterInitializationUIEnvironmentFunc = function () {
        $("#forgotPasswordForm").submit(function () {
            $("#warnMag").hide();
            $("#errorMsg").hide();

            $.ajax({
                type: "POST",
                url: '/api/users/forgotPassword',
                data: $( this ).serialize(),
                success: function (data) {
                    $("#forgotPasswordSuccessMsg").show();
                    $("#forgotPasswordPageTitle").hide();
                    $("#forgotPasswordForm").hide();
                    $("#errorMsg").hide();
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    var json = JSON.parse(jqXHR.responseText);
                    var errMsg = $("#errorMsg");
                    switch (json.message) {
                        case "email.notfound" :
                            errMsg.html("<p>Пользователь с таким E-mail не зарегистрирован в системе</p>");
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
