//variabile globale di pagina che mi identifica se la mappa è mostrata o meno
var showMap = false;
var minimizeMap = function () {
    fadeIn();
    document.getElementById("panelMapPlace").style.left = "235px";
    google.maps.event.trigger(maps.map_Vacancy, "resize");
};
var expandMap = function () {
    fadeOut();
    document.getElementById("panelMapPlace").style.left = "10px";
    google.maps.event.trigger(maps.map_Vacancy, "resize");
};
var updateGeoMask = function (map) {
    try {
        var url = "/" + contextName + "/secure/rest/geo/getPoiMask?";
        url += "solr_param=" + encodeURIComponent(map.solr_params + "&wt=json&omitHeader=true");
        $.ajax(url).complete(function (xhr) {
            var poi = JSON.parse(xhr.responseText);
            var lung = poi.response.docs.length;
            var locations = [];
            var markers = [];
            var _loop_1 = function (i) {
                var latit = poi.response.docs[i].latitudine;
                var longin = poi.response.docs[i].longitudine;
                var descr = poi.response.docs[i].descrizione;
                var id_vacancy = poi.response.docs[i].id_va_dati_vacancy;
                var offerta = {
                    latit: latit,
                    longin: longin,
                    descr: descr
                };
                locations.push(offerta);
                var marker = new google.maps.Marker({
                    position: new google.maps.LatLng(locations[i].latit + Math.random() / 1000 - 0.0005, locations[i].longin + Math.random() / 1000 - 0.0005),
                    map: map,
                    title: locations[i].descr
                });
                markers.push(marker);
                google.maps.event
                    .addListener(marker, 'click', function () {
                    window.location.href = "/" + contextName + "/faces/secure/azienda/vacancies/visualizza.xhtml?id=" + id_vacancy;
                });
            };
            for (var i = 0; i < lung; i++) {
                _loop_1(i);
            }
            // creo nuova mappa
            var newMap = new google.maps.Map(document
                .getElementById('riquadro_map'), {
                zoom: map.getZoom(),
                center: new google.maps.LatLng(map.getCenter()
                    .lat(), map.getCenter().lng()),
                mapTypeId: google.maps.MapTypeId.ROADMAP
            });
            var options = {
                imagePath: '/' + contextName + '/resources/images/mc/m'
            };
            var markerCluster = new MarkerClusterer(newMap, markers, options);
        });
    }
    catch (e) {
        console && console.error("Error(updateGeoMask): " + e);
    }
};
function fadeOut() {
    $(jq("pulsante_riquadro_left")).css('display', 'inline');
    $(jq("pulsante_riquadro_right")).css('display', 'none');
    $(jq("riquadroGroup")).fadeOut('fast');
    $(jq("mapPlace")).css('width', '890px');
}
function fadeIn() {
    $(jq("pulsante_riquadro_left")).css('display', 'none');
    $(jq("pulsante_riquadro_right")).fadeIn();
    $(jq("mapPlace")).css('width', '650px');
    $(jq("riquadroGroup")).fadeIn();
}
function imageCandidato(id) {
    var src = id.src;
    src = src.substring(0, src.lastIndexOf('/'));
    id.src = src + "/candidato.png";
}
function imageNonCandidato(id) {
    var src = id.src;
    src = src.substring(0, src.lastIndexOf('/'));
    id.src = src + "/nonCandidato.png";
}
function imageSegnala(id) {
    var src = id.src;
    src = src.substring(0, src.lastIndexOf('/'));
    id.src = src + "/share_to_friend_pink.png";
}
function imageNonSegnala(id) {
    var src = id.src;
    src = src.substring(0, src.lastIndexOf('/'));
    id.src = src + "/share_to_friend.png";
}
function removeFileAttach() {
    $('#confermaCandidatura\\:modal_form\\:original_file_name_dsplay').text('');
    $('#confermaCandidatura\\:modal_form\\:original_file_name').val('');
    $('#confermaCandidatura\\:modal_form\\:file_name').val('');
    $('#fileupload').val('');
    return false;
}
var openConferma = function (params) {
    var itemId = params.id;
    var vacancyId = params.idVa;
    var clicLavoro = params.clicLavoro;
    $('[id$="confermaCandidatura\:modal_form\:objectId"]').val(itemId);
    $('[id$="confermaCandidatura\:modal_form\:vaId"]').val(vacancyId);
    $('[id$="confermaCandidatura\:modal_form\:clicLavoro"]').val(clicLavoro);
    if (clicLavoro) {
        $(jq("confermaCandidatura:modal_form:cv_cl_vacancy")).show();
        $(jq("confermaCandidatura:modal_form:cv_vacancy")).hide();
    }
    else {
        $(jq("confermaCandidatura:modal_form:cv_cl_vacancy")).hide();
        $(jq("confermaCandidatura:modal_form:cv_vacancy")).show();
    }
    removeFileAttach();
};
function mostraMappa() {
    showMap = true;
    var map = maps.map_Vacancy;
    if (!arguments.callee.__geo) {
        var adr = document.getElementById("address");
        var options = {
            // types: ["(cities)"],
            componentRestrictions: {
                country: "it"
            }
        };
    }
    $(jq('form_lista_offerte:buttonMap')).hide();
    $(jq('form_lista_offerte:buttonTable')).show();
    $(jq('mapStartPlace')).css('height', '');
    $(jq('riquadro_map')).fadeIn();
    $(jq('form_lista_offerte:riquadro_table')).css('display', 'none');
    $(jq('form_lista_offerte:buttonMoreBottom')).css('display', 'none');
    $(jq("form_lista_offerte:resultTop")).css('display', 'none');
    $(jq("form_lista_offerte:resultBottom")).css('display', 'none');
    $(jq('mapPlace')).append(riquadro_map);
    param = $(jq("form_lista_offerte:paramSOLR")).val();
    maps.map_Vacancy.solr_update(param);
    $(jq("mapPlace")).css("display", "");
    google.maps.event.trigger(map, "resize");
    if (!arguments.callee.__geo) {
        map.recenter();
        arguments.callee.__geo = true;
    }
} // mostraMappa
function mostraTabella() {
    showMap = false;
    $(jq("mapPlace")).css("display", "none");
    $(jq("form_lista_offerte:riquadro_table")).fadeIn();
    $(jq("riquadro_map")).css('display', 'none');
    $(jq("form_lista_offerte:buttonTable")).css('display', 'none');
    $(jq("form_lista_offerte:buttonMap")).css('display', 'inline');
    $(jq("form_lista_offerte:resultTop")).css('display', 'inline');
    $(jq("form_lista_offerte:resultBottom")).css('display', 'inline');
    $(jq('form_lista_offerte:buttonMoreBottom')).css('display', 'inline');
}
/**
 * Metodo agganciato all'evento ajax dei checkbox relativi ai filtri di 2°
 * livello
 */
function reloadMappa(data) {
    if (data.status == 'begin') {
        if ($(jq("form_lista_offerte:buttonTable")).css('display') == 'inline') {
            if (!showMap) {
                showMap = true;
            }
        }
        else {
            if ($(jq("form_lista_offerte:emptyTable")).css('display') == 'none') {
                showMap = false;
            }
        }
    }
    if (data.status == 'success') {
        window.disegnaBottoni && disegnaBottoni();
        if (showMap) {
            if (window.tt) {
                tt.close();
            }
            mostraMappa();
        }
    }
}
function mostraLoaderOfferte(data) {
    if (data.status == 'begin') {
        $('#imgMore').show();
        $(jq('form_lista_offerte:buttonMoreBottom')).hide();
    }
    else if (data.status == 'success') {
        $('#imgMore').hide();
        $(jq('form_lista_offerte:buttonMoreBottom')).show();
    }
}
function setHiddenInputs() {
    $("[id$=dove_hidden]").val($("[id$=dove\\:inputText]").val());
    $("[id$=cosa_hidden]").val($("[id$=cosa\\:inputText]").val());
}
;
var riquadro_map = $(jq('riquadro_map'));
$(function () {
    window.disegnaBottoni && disegnaBottoni();
    $(jq('pulsante_riquadro_left')).css('display', 'none');
    // il pannello loader parte nascosto
    $(jq('loader_lista_offerte')).css('display', 'none');
    $(jq("form_lista_offerte:emptyTable")).css('display', 'none');
});
