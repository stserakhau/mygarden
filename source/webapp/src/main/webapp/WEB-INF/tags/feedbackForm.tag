<%@ tag body-content="scriptless" pageEncoding="UTF-8" %>
<div class="w3-content w3-container w3-padding w3-left-align w3-text-grey" >
    <jsp:doBody/>
    <form id="feedbackForm">
        <div name="errorMsg" class="w3-panel w3-leftbar w3-border-red w3-pale-red w3-card-2" style="display: none;"></div>
        <div name="infoMsg" class="w3-panel w3-leftbar w3-border-yellow w3-pale-yellow w3-card-2" style="display: none;"></div>
        <div class="w3-row-padding" style="margin:0 -16px 8px -16px">
            <div class="w3-half">
                <input name="name" class="w3-input w3-border" type="text" placeholder="Ваше имя" required="required">
            </div>
            <div class="w3-half">
                <input name="email" class="w3-input w3-border" type="text" placeholder="Email" required="required">
            </div>
        </div>
        <textarea name="content" rows="4" style="width:100%" placeholder="Напишите нам, мы будем рады любым Вашим замечаниям и предложениям"></textarea>
        <button class="w3-button w3-theme-dark w3-right w3-mobile" type="submit">
            <i class="fa fa-paper-plane"></i> Отправить сообщение
        </button>
    </form>
</div>
<script type="text/javascript">
    $(document).ready(function() {
        FEEDBACK_FORM.attachBehaviour(
                $("#feedbackForm")
        );
    });
</script>