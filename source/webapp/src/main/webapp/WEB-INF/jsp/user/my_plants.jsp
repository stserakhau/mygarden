<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="../static/fabric.js"></script>
<div class="w3-container w3-lime w3-left-align w3-margin-bottom w3-text-white">
    <h1>Ландшафтный редактор</h1>
</div>

<div class="w3-row w3-container w3-margin-bottom w3-text-grey">
    <div class="w3-quarter w3-container w3-padding-16" style="overflow: auto; max-width: 600px">
        <ul class="w3-ul w3-center">
            <li class="w3-left-align">
                <b>Ваши участки</b>
                <a onclick="CREATE_PLANT_WINDOW.show();" class="w3-right w3-text-blue w3-hover-text-red" style="cursor:pointer;">
                    <span class="fa fa-plus"> Новый</span>
                </a>
                <div id="plantsList" class="w3-accordion"></div>
            </li>
            <li id="bedsManagement" class="w3-left-align" style="display:none;">
                <b>Ваши клумбы</b><a onclick="CREATE_PLANT_REGION_WINDOW.open();" style="cursor:hand;" class="w3-right w3-text-blue w3-hover-text-red"><span class="fa fa-plus"> Новая</span></a>
                <div class="w3-accordion">
                </div>
            </li>
            <li id="speciesManagement" class="w3-left-align" style="display:none;"><b>Ваши растения</b>
                <div id="speciesPanel" class="w3-accordion">
                    <a href="#" onclick="soilAccordion('Demo5')" class="w3-btn-block w3-left-align w3-light-gray">Однолетние цветы</a>
                    <div id="Demo5" class="w3-accordion-content w3-container"><ul class="w3-ul w3-center">
                            <li class="w3-left-align">
                                <img src="#" class="w3-circle"  style="width:40px;height:40px;"> Астра
                                <a href="#" class="w3-right w3-text-blue w3-hover-text-red"><span class="fa fa-plus"> Добавить</span></a>
                            </li>
                            <li class="w3-left-align"><img src="#" class="w3-circle"  style="width:40px;height:40px;"> Бархатцы<a href="#" class="w3-right w3-text-blue w3-hover-text-red"><span class="fa fa-plus"> Добавить</span></a></li>
                            <li class="w3-left-align"><img src="#" class="w3-circle"  style="width:40px;height:40px;"> Василек голубой<a href="#" class="w3-right w3-text-blue w3-hover-text-red"><span class="fa fa-plus"> Добавить</span></a></li>
                            <li class="w3-left-align"><img src="#" class="w3-circle"  style="width:40px;height:40px;"> ...<a href="#" class="w3-right w3-text-blue w3-hover-text-red"><span class="fa fa-plus"> Добавить</span></a></li>
                        </ul>
                    </div>
                </div>
            </li>
        </ul>
    </div>
    <div id="contentContainer" class="w3-half" style="max-width: 1200px; display:none;">
        <div class="w3-container w3-padding-0 w3-margin-bottom w3-text-grey">
            <a class="w3-btn w3-theme w3-left w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile"
               onclick="savePlant()"><span class="fa fa-save"></span> Сохранить рисунок</a>
        </div>
        <div class="w3-container w3-padding-0 w3-text-grey">
            <div id="viewport" style="overflow: auto; right: 5px; position: absolute; left: 25%; bottom: 80px; top: 220px;">
                <canvas id="plant"></canvas>
            </div>
        </div>
    </div>

    <div id="objectProperties" style="display:none; position: fixed;bottom: 10px;right:0; height:200px; width:250px;border:1px solid black;overflow-y: auto; background-color: white;">
        <div style="text-align: right;">
            <a class="w3-btn w3-theme w3-margin-bottom w3-margin-right w3-padding-8 w3-mobile" onclick="deleteActiveObject()">Удалить</a><br/>
        </div>
        <div></div>
    </div>
    <div class="w3-quarter">
    </div>
</div>

<script type="text/javascript">
    var c = document.getElementById("plant")
    var plantScale = 50; // 50 pixels = 1 meter
    var plantCanvas = null;
    var viewPort = null;
    function createPlant(plant) {
        if(plantCanvas != null) {
            $('#objectProperties').hide();
            plantCanvas.clear();
            plantCanvas.dispose();
        }

        viewPort = document.getElementById("viewport");

        var width = plant.width;
        var height = plant.height;


        c.width = width;
        c.height = height;

        plantCanvas =  new fabric.Canvas('plant', {
            width: width,
            height: height,
            selection: true,
            stopContextMenu: true,
            preserveObjectStacking: true
        });
        plantCanvas.setBackgroundColor("#004400", plantCanvas.renderAll.bind(plantCanvas));
//        plantCanvas.setBackgroundColor({source:'static/plant/gravel.jpg'}, plantCanvas.renderAll.bind(plantCanvas));

        plantCanvas.on("object:modified", function(options){
            var obj = options.target;
            if(obj.customType=="SPECIES") {
                return;
            }
            obj.width = obj.width * obj.scaleX;
            obj.height = obj.height * obj.scaleY;
            obj.scaleX = 1;
            obj.scaleY = 1;
//            obj.zoomX = 1;
//            obj.zoomY = 1;
            plantCanvas.renderAll();
        });

        plantCanvas.observe('mouse:down', function () {
            var selectedObject = plantCanvas.getActiveObject();
            if(selectedObject) {
                loadObjectProperties(selectedObject);
            } else {
                $("#objectProperties").hide();
            }
        });

        if (plant.model != null) {
            plantCanvas.loadFromJSON(plant.model, function () {
                plantCanvas.renderAll();

                for(var i=0; i<plantCanvas._objects.length; i++) {
                    var obj = plantCanvas._objects[i];
                    attachToObject(obj);
                }

                afterPlantLoaded(plant.model.objects)
            }, function (o, object) {
                console.log(o, object)
            })
        } else {
            afterPlantLoaded(null)
        }

        $("#bedsManagement").show();
        $("#speciesManagement").show();
        $("#contentContainer").show();
    }

    function afterPlantLoaded(items) {
        var bedsList = $("#bedsManagement>div");
        bedsList.html('');
        if(items==null) {
            return;
        }
        for(var i=0; i < items.length; i++) {
            var item = items[i];
            if(item.customType=="BED") {
                $('<a href="#" onclick="selectPlantObject(\'' + item.uid + '\')" class="w3-btn-block w3-left-align w3-light-gray">' + item.title + '</a>')
                        .appendTo(bedsList)
            }
        }
    }

    function selectPlantObject(uid) {
        for(var i=0; i < plantCanvas._objects.length; i++) {
            var obj = plantCanvas.item(i);
            if(obj.uid == uid) {
                loadObjectProperties(obj);
                plantCanvas.setActiveObject(obj);
                animatedScrollTo(viewPort,
                        obj.left - viewPort.offsetWidth / 2 + obj.width / 2,
                        obj.top - viewPort.offsetHeight / 2 + obj.height / 2,
                        400
                );
                plantCanvas.renderAll();
                break;
            }
        }
    }

    function deleteActiveObject() {
        var activeObjects = plantCanvas.getActiveObjects();
        if (activeObjects.length>0) {
            activeObjects.forEach(function(object) {
                plantCanvas.remove(object);
            });
            plantCanvas.discardActiveObject().renderAll();
            afterPlantLoaded(plantCanvas._objects)
        }
        $('#objectProperties').hide();
    }

    function addSpecies(speciesId, imageURL) {
        addFigure({
            type: 'species',
            url : imageURL,
            left: viewPort.scrollLeft,
            top: viewPort.scrollTop,
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
            case 'bed-rect' :
                fabric.Image.fromURL("static/plant/gravel.jpg", function(oImg) {
                    json.customType = "BED";
                    // scale image down, and flip it, before adding it onto canvas
                    oImg
                            .set({
                                left: 100,
                                top: 100,
                                width: new Number(json.width),
                                height: new Number(json.height),
                                hasControls : true,
                                clipTo: function(ctx) {clipToRectBed(this, ctx);}
                            });
                    _addCustomProperties(oImg, json);

                    plantCanvas.add(oImg);
                    plantCanvas.sendToBack(oImg);
                    afterPlantLoaded(plantCanvas._objects)
                });
                break;
            case 'bed-circle' :
                fabric.Image.fromURL("static/plant/gravel.jpg", function(oImg) {
                    json.customType = "BED";
                    // scale image down, and flip it, before adding it onto canvas
                    oImg
                            .set({
                                left: 100,
                                top: 100,
                                width: json.radius*2,
                                height: json.radius*2,
                                hasControls : true,
                                clipTo: function(ctx) {clipToCircleBed(this, ctx);}
                            });
                    _addCustomProperties(oImg, json);

                    plantCanvas.add(oImg);
                    plantCanvas.sendToBack(oImg);
                    afterPlantLoaded(plantCanvas._objects)
                });
                break;
            case 'image' :
                fabric.Image.fromURL(json.url, function(oImg) {
                    // scale image down, and flip it, before adding it onto canvas
                    oImg
                            .scaleToWidth(json.width)
                            .scaleToHeight(json.height)
                            .set({
                                hasControls : false
                            });
                    _addCustomProperties(oImg, json);
                    plantCanvas.add(oImg);
                });
                break;
            case 'species' :
                fabric.Image.fromURL(json.url, function(oImg) {
                    // scale image down, and flip it, before adding it onto canvas
                    oImg
                            .scaleToWidth(50)
                            .scaleToHeight(50)
                            .set({
                                clipTo: function(ctx) {clipToCircleSpecies(this, ctx);},
                                hasControls : false,
                                left: json.left,
                                top: json.top
                            });
                    _addCustomProperties(oImg, json);
                    plantCanvas.add(oImg);
                });
                break;

            default:
                alert('Неизвестная фигура: ' + json.type);
                return;
        }
    }

    function clipToCircleSpecies(obj, ctx) {
        ctx.arc(0, 0, obj.width / 2, 0, Math.PI * 2, true);
    }

    function clipToCircleBed(obj, ctx) {
        var pat = c.getContext("2d").createPattern(obj._element, "repeat");
        ctx.arc(0, 0, obj.width / 2, 0, Math.PI * 2, true);
        ctx.fillStyle = pat;
        ctx.fill();

    }

    function clipToRectBed(obj, ctx) {
        var pat = c.getContext("2d").createPattern(obj._element, "repeat");
        ctx.rect(-obj.width/2, -obj.height/2, obj.width, obj.height);
        ctx.fillStyle = pat;
        ctx.fill();
    }

    function _addCustomProperties(figure, json) {
        if(figure) {
            if(json.customType) {
                figure.uid = makeid();
                figure.customType = json.customType;
                figure.customId = json.customId;
                if(json.title) {figure.title = json.title;}
                if(json.hasControls) {figure.hasControls = json.hasControls;}
                attachToObject(figure);
            }
        }
    }

    function attachToObject(obj) {
        obj.toObject = (function (toObject) {
            return function () {
                return fabric.util.object.extend(toObject.call(this), {
                    customType: this.customType,
                    customId: this.customId,
                    uid: this.uid,
                    hasControls: this.hasControls ? this.hasControls : false,
                    title: this.title ? this.title : false
                });
            };
        })(obj.toObject);
    }

    var currentPlantId = null;
    function savePlant() {
        if(currentPlantId==null) {
            return;
        }

        plantCanvas.width = plantCanvas.getWidth();
        plantCanvas.height = plantCanvas.getHeight();
        var json = JSON.stringify(plantCanvas);
        $.ajax({
            url: "/api/plant/" + currentPlantId + "/model",
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
        var type = selectedObject.get("customType")
        if(type) {
            var infoPanel = $("#objectProperties>div:last");
            switch (type) {
                case 'SPECIES' :
                    var id = selectedObject.get("customId");
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
                case 'BED' :
                    infoPanel.html('');

                    $("<table><thead>\
                    <tr><th style='text-align:right'>Клумба:</th><td style='text-align:left'>" + selectedObject.title + "</td></tr>\
                    </thead><tbody/></table>").appendTo(infoPanel);

                    var tbody = infoPanel.find("table>tbody");
                    for (var key in selectedObject) {
                        if("width height radius".indexOf(key)>-1) {
                            var value;
                            if("width height radius".indexOf(key)>-1) {
                                value = selectedObject[key] / plantScale + " м";
                            } else {
                                value = selectedObject[key];
                            }

                            $("<tr><th style='text-align:right'>" + key + "</th><td style='text-align:left'>" + value + "</td></tr>").appendTo(tbody);
                        }
                    }

                    break;
            }
        }

        $("#objectProperties").show();
    }

    function loadPlant(plantId) {
        event.stopPropagation();
        $.get("api/plant/" + plantId, function (plant) {
            currentPlantId = plantId;
            createPlant(plant);
        });
    }
</script>
<script type="text/javascript">
    function soilAccordion(id) {
        var x = document.getElementById(id);
        if (x.className.indexOf("w3-show") == -1) {
            x.className += " w3-show";
        } else {
            x.className = x.className.replace(" w3-show", "");
        }
    }

    document.afterInitializationUIEnvironmentFunc = initializePage;

    function initializePage() {
        doAction(function () {
            CREATE_PLANT_WINDOW.initialize({
                scale: plantScale,
                callback:refreshPlants
            });

            CREATE_PLANT_REGION_WINDOW.initialize({
                scale: plantScale,
                callback : addFigure
            });

            refreshPlants();

            loadMySpecies();
        });
    }

    function refreshPlants(selectedPlantId) {
        var plants = $("#plantsList");

        $.get("api/plant/?order.name=asc", function(data){
            plants.html("");
            for(var i=0;i<data.length;i++) {
                var p = data[i];
                var width = p.width / plantScale;
                var height = p.height / plantScale;
                $('<a href="#" onclick="soilAccordion(\'plantAcc'+ p.id +'\')" class="w3-btn-block w3-left-align w3-light-gray">'+ p.name +' <span style="float:right" onclick="loadPlant(' + p.id + ')">>></span></a>\
                <div id="plantAcc'+ p.id +'" class="w3-accordion-content w3-container">\
                    <form id="plant'+ p.id +'" action="/api/plant/'+ p.id+'" onsubmit="return updatePlantSize(this)">\
                        <input type="hidden" name="id" value="'+ p.id +'"/>\
                        <p><label>Название:</label><input class="w3-right" type="text" name="name" value="'+ p.name +'" required maxlength="100" placeholder="текущее_название"></p>\
                        <p><label>Длинна(м):</label><input class="w3-right" type="number" name="width" value="'+ width +'" required min="0" maxlength="5" placeholder="текущее_значение"></p>\
                        <p><label>Ширина(м):</label><input class="w3-right" type="number" name="height" value="'+ height +'" required min="0" maxlength="5" placeholder="текущее_значение"></p>\
                        <button class="w3-btn w3-theme-dark w3-right" type="submit">Изменить</button>\
                        <button class="w3-btn w3-theme-dark w3-right w3-hover-red" type="">Удалить</button>\
                    </form>\
                </div>').appendTo(plants);
            }
        });
    }

    function updatePlantSize(form) {
        var frm = $(form);
        var json = serializeFormToJsonTree(frm);
        doActionIfAnonCreateAndLogin(function(){
            json.width = json.width * plantScale;
            json.height = json.height * plantScale;
            $.ajax({
                url: "/api/plant/" + json.id,
                type: "put",
                data: JSON.stringify(json),
                contentType: "application/json; charset=utf-8",
                success: function (data) {
                    frm.parent().hide();
                    refreshPlants(json.id);
                    loadPlant(json.id)
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
    }

    function loadMySpecies() {
        $.get(
                'api/species/my?pageNum=0&pageSize=1000&order.catalogName=asc&order.name=asc',
                function (items) {
                    var speciesPanel = $("#speciesPanel");
                    speciesPanel.html('');
                    var catalogId = null;
                    var catalog = null;
                    var catalogSubList = null;
                    for (var i = 0; i < items.length; i++) {
                        var item = items[i];

                        if (item.catalogId != catalogId) {
                            var root = speciesPanel
//                            root.appendTo(speciesPanel);

                            catalog = $('<a style="cursor:hand" class="w3-btn-block w3-left-align w3-light-gray">' + item.catalogName + '</a>');
                            catalog.appendTo(root);

                            catalogSubList = $('<div class="w3-accordion-content w3-container"><ul class="w3-ul w3-center"></ul></div>');
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
                                    container: catalogSubList.find("ul"),
                                    species: item
                                }, function(images, conf) {
                                    var imagePath;
                                    if(images.length > 0) {
                                        var img = images[0];
                                        imagePath = 'api/data-storage/' + img.id;
                                    } else {
                                        imagePath = 'static/carousel-2.png';
                                    }

                                    $("<li class='w3-left-align'><img src='"+imagePath+"' width='40' height='40' class='w3-circle'> " + conf.species.name +
                                           "<a style='cursor:hand' onclick='addSpecies("+conf.species.id+",\"" + imagePath + "\")' class='w3-right w3-text-blue w3-hover-text-red'>" +
                                            "<span class='fa fa-plus'> Добавить</span></a>\
                                        </li>").appendTo(conf.container);
                                }
                        )
                    }
                }
        );
    }
</script>