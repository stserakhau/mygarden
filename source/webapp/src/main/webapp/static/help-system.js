function simple_tooltip(target_items, name) {
    $.each($(target_items), function (i) {
        var _this = $(this);
        var helpContentPath = _this.attr("helpPath");
        $.get(
                "/help/" + helpContentPath,
            function (content) {

                $("<div class='" + name + "' id='" + name + i + "'><div>" + content + "</div></div>").appendTo($("body"));
                var my_tooltip = $("#" + name + i);

                _this.removeAttr("title").mouseover(function () {
                    my_tooltip.css({opacity: 0.8, display: "none"}).fadeIn(400);
                }).mousemove(function (kmouse) {
                    my_tooltip.css({left: kmouse.pageX - 125, top: kmouse.pageY + 15});
                }).mouseout(function () {
                    my_tooltip.fadeOut(400);
                });
            }
        );
    });
}
$(document).ready(function () {
    simple_tooltip("*[helpPath]", "tooltip");
});