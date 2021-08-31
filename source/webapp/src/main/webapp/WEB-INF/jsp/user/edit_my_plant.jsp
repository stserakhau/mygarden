<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="../static/fabric.js"></script>

<div class="w3-container w3-lime w3-left-align w3-margin-bottom w3-text-white">
    <h1 id="title">Мои клумбы</h1>
</div>

<canvas id="plant"></canvas>

<div style="position: fixed;top:100px;right:0; bottom: 220px; width:250px;border:1px solid black;overflow-y: auto; background-color: white;">
    <a class="w3-btn w3-theme w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile" onclick="save()">Сохранить</a><br/>
    <a class="w3-btn w3-theme w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile" onclick="CREATE_PLANT_REGION_WINDOW.open()">Разметка участка</a><br/>

    <div id="speciesPanel" style="text-align: left; padding: 5px;">
    </div>
</div>

<div id="objectProperties" style="display:none; position: fixed;bottom: 10px;right:0; height:200px; width:250px;border:1px solid black;overflow-y: auto; background-color: white;">
    <div style="text-align: right;">
        <a class="w3-btn w3-theme w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile" onclick="deleteActiveObject()">Удалить</a><br/>
    </div>
    <div></div>
</div>

<script type="text/javascript">
    var plantCanvas = null;
    function createPlant(plant) {
        var width = plant.width;
        var height = plant.height;

        var c = document.getElementById("plant")
        c.width = width;
        c.height = height;

        plantCanvas =  new fabric.Canvas('plant', {
            width: width,
            height: height,
            selection: true,
            stopContextMenu: true
        });
        plantCanvas.setBackgroundColor({source:'static/plant/gravel.jpg'}, plantCanvas.renderAll.bind(plantCanvas));

        plantCanvas.observe('mouse:down', function () {
            var selectedObject = plantCanvas.getActiveObject();
            if(selectedObject) {
                $("#objectProperties").show();
                loadObjectProperties(selectedObject);
            } else {
                $("#objectProperties").hide();
            }
        });

        if (plant.model != null) {
            plantCanvas.loadFromJSON(plant.model, function () {
                plantCanvas.renderAll();
            }, function (o, object) {
                console.log(o, object)
            })
        }
    }

    function deleteActiveObject() {
        var activeObjects = plantCanvas.getActiveObjects();
        if (activeObjects.length>0) {
            activeObjects.forEach(function(object) {
                plantCanvas.remove(object);
            });
            plantCanvas.discardActiveObject().renderAll();
        }
    }

    function addSpecies(speciesId, imageURL) {
        addFigure({
            type: 'image',
            url : imageURL,
            width: 50,
            height: 50,
            customType: "SPECIES",
            customId: speciesId
        });
    }
    function addPicture(imageURL) {
        addFigure({
            type: 'image',
            url : imageURL,
            width: 50,
            height: 50
        });
    }

    function addFigure(json) {
        switch(json.type) {
            case 'circle' :
                var figure = new fabric.Circle({
                    radius: json.radius,
                    fill: 'green',
                    left: 100,
                    top: 100
                });
                _addCustomProperties(figure, json);
                plantCanvas.add(figure);
                break;
            case 'rect' :
                var figure = new fabric.Rect({
                    top: 100,
                    left:100,
                    width: json.width,
                    height: json.height,
                    opacity: 0.5,
                    fill: "white"
                });
                figure.on({
                    'scaling': function(e) {
                        var obj = this;
                        var w = obj.width * obj.scaleX;
                        var h = obj.height * obj.scaleY;
                        obj.set({
                            'height'     : h,
                            'width'      : w,
                            'scaleX'     : 1,
                            'scaleY'     : 1
                        });
                    }
                });

                _addCustomProperties(figure, json);
                plantCanvas.add(figure);
                break;
            case 'image' :
                fabric.Image.fromURL(json.url, function(oImg) {
                    _addCustomProperties(oImg, json);
                    // scale image down, and flip it, before adding it onto canvas
                    oImg.scaleToWidth(json.width).scaleToHeight(json.height);
                    plantCanvas.add(oImg);
                });
                break;
            default:
                alert('Неизвестная фигура: ' + json.type);
                return;
        }
    }

    function _addCustomProperties(figure, json) {
        if(figure) {
//            figure.selectable = json.selectable;
            if(json.customType) {
                figure.customType = json.customType;
                figure.customId = json.customId;
                figure.toObject = (function (toObject) {
                    return function () {
                        return fabric.util.object.extend(toObject.call(this), {
                            customType: this.customType,
                            customId: this.customId
                        });
                    };
                })(figure.toObject);
            }
        }
    }

    function save() {
        plantCanvas.width = plantCanvas.getWidth();
        plantCanvas.height = plantCanvas.getHeight();
        var json = JSON.stringify(plantCanvas);
        $.ajax({
            url: "/api/plant/" + plantId + "/model",
            type: "post",
            data: json,
            contentType: "application/json; charset=utf-8",
            success: function (data) {},
            error: function (jqXHR, textStatus, errorThrown) {
                if (textStatus == 'error') {
                    alert(errorThrown);
                } else {
                    alert(jqXHR.responseJSON.message);
                }
            }
        });
    }

    function loadObjectProperties(selectedObject) {
        var id = selectedObject.get("customId");
        var type = selectedObject.get("customType")
        if(id && type) {
            var infoPanel = $("#objectProperties>div:last");
            switch (type) {
                case 'SPECIES' :
                    $.get('api/species/' + id, function (item) {
                        infoPanel.html('');

                        $("<table><thead>\
                    <tr><th style='text-align:right'>Каталог:</th><td style='text-align:left'>" + item.speciesCatalog.name + "</td></tr>\
                    <tr><th style='text-align:right'>Название:</th><td style='text-align:left'>" + item.species.name + "</td></tr>\
                    </thead><tbody/></table>").appendTo(infoPanel);
                        var opts = item.species.options;
                        if (opts) {
                            var tbody = infoPanel.find("table>tbody");
                            for (var key in opts) {
                                $("<tr><th style='text-align:right'>" + key + "</th><td style='text-align:left'>" + opts[key] + "</td></tr>").appendTo(tbody);
                            }
                        }
                    });
                    break;
            }
        }
    }

    var plantId = getUrlVars()['plantId'];
    document.afterInitializationUIEnvironmentFunc = initializePage;

    function initializePage() {
        doAction(function () {
            loadPlant();
            loadMySpecies();

            CREATE_PLANT_REGION_WINDOW.initialize({
                callback : addFigure
            });
        });
    }

    function loadPlant() {
        $.get("api/plant/" + plantId, function (plant) {
            $("#title").html(plant.name);

            createPlant(plant);
        });
    }

    function loadMySpecies() {
        $.get(
                'api/species/?pageNum=0&pageSize=1000&order.catalogName=asc&order.name=asc',
                function (items) {
                    var speciesPanel = $("#speciesPanel");
                    speciesPanel.html('');
                    var catalogId = null;
                    var catalog = null;
                    var catalogSubList = null;
                    for (var i = 0; i < items.length; i++) {
                        var item = items[i];

                        if (item.catalogId != catalogId) {
                            var root = $("<div></div>");
                            root.appendTo(speciesPanel);

                            catalog = $("<div style='font-weight: bold; cursor: hand'>" + item.catalogName + "</div>");
                            catalog.appendTo(root);

                            catalogSubList = $("<div style='font-weight: normal; display:none'></div>");
                            catalogSubList.appendTo(root);

                            catalog.prop("catalogSubList", catalogSubList);
                            catalog.click(function() {
                                var subList = $(this).prop("catalogSubList");
                                if(subList.is(":visible")) {
                                    subList.hide();
                                } else {
                                    subList.show();
                                }
                            });

                            catalogId = item.catalogId;
                        }
                        loadSpeciesImages({
                                    speciesId: item.id,
                                    container: catalogSubList,
                                    species: item
                                }, function(images, conf) {
                                    var imagePath;
                                    if(images.length > 0) {
                                        var img = images[0];
                                        imagePath = 'api/data-storage/' + img.id;
                                    } else {
                                        imagePath = 'static/carousel-2.png';
                                    }

                                    $("<a href='#' onclick='addSpecies("+conf.species.id+",\"" + imagePath + "\")' style='margin-left:10px; display: block;'>" +
                                            "<img id='species" + conf.species.id + "' src='"+imagePath+"' width='24' height='24'>" + conf.species.name + "</a>")
                                            .appendTo(conf.container);
                                }
                        )
                    }
                }
        );
    }

</script>