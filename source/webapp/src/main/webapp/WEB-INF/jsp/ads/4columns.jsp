<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div id="adsContainter4" class="w3-row w3-margin-bottom l3 m6 s12">
    <div class="w3-col l3 m6 s12 w3-padding-tiny">
        <div class="w3-card" style="min-height: 100px; min-width: 100px">
            <p>Ads1</p>
        </div>
    </div>
    <div class="w3-col w3-col l3 m6 s12 w3-padding-tiny">
        <div class="w3-card" style="min-height: 100px; min-width: 100px">
            <p>Ads2</p>
        </div>
    </div>
    <div class="w3-col w3-col l3 m6 s12 w3-padding-tiny">
        <div class="w3-card" style="min-height: 100px;min-width: 100px">
            <p>Ads3</p>
        </div>
    </div>
    <div class="w3-col w3-col l3 m6 s12 w3-padding-tiny">
        <div class="w3-card" style="min-height: 100px; min-width: 100px">
            <p>Ads4</p>
        </div>
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function(){
        var el = $("#adsContainter4");
        el.html('');
        for(var i=0;i<4;i++) {
            $('<div class="w3-col l3 m6 s12 w3-padding-tiny">\
                <div class="w3-card" style="min-height: 100px; min-width: 100px">\
                    <p>Реклама '+ Math.random() + '</p>\
                </div>\
            </div>').appendTo(el);
        }
    });
</script>