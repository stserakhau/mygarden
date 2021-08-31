<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="w3-container w3-lime w3-left-align w3-margin-bottom w3-text-white">
    <h1>Установить пароль</h1>
</div>

<div id="regSuccessMsg" style="display: none;"
     class="w3-panel w3-leftbar w3-border-green w3-pale-green w3-card-2">
    <p>Регистрация подтверждена. Установите пароль</p>
</div>

<form id="resetPassword" method="post" action="/api/users/reset_password" onsubmit="return onResetPassword();"
      class="w3-container w3-padding  w3-card-4 w3-left-align">

    <div id="warnMag" class="w3-panel w3-leftbar w3-border-red w3-pale-red w3-card-2" style="display: none;">
        <p>Пароли не совпадают</p>
    </div>

    <input type="hidden" name="registrationCode" value="${param.registrationCode}"/>

    <p><label class="w3-label">Пароль<span class="w3-text-red">*</span></label>
        <input id="pwd1" name="password" class="w3-input" type="password" required="required"></p>

    <p><label class="w3-label">Пароль еще раз<span class="w3-text-red">*</span></label>
        <input id="pwd2" name="passwordRedo" class="w3-input" type="password" required="required"></p>

    <p>
        <input class="w3-btn w3-theme-dark w3-right w3-margin-left" type="submit" value="Установить">
        <a href="#" class="w3-btn w3-grey w3-right w3-margin-left" type="reset">Отмена</a>
    </p>

</form>


<script type="text/javascript">
    function onResetPassword() {
        var pwd1 = $('#pwd1').val();
        var pwd2 = $('#pwd2').val();

        if (pwd1 != pwd2) {
            $("#warnMag").show();
            return false;
        }

        return true;
    }
</script>