﻿//variabile globale di pagina che mi identifica se la mappa è mostrata o meno
var showMap = false;
var maps = {};	

var updateGeoMask = function() {
	try {
		var url = "/" + contextName + "/secure/rest/geo/getPoiMask?";
		url += "solr_param=" + encodeURIComponent(maps.solr_params + "&wt=json&omitHeader=true");
		
		var tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
			maxZoom: 19,
			attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, Points &copy 2012 LINZ'
		}),		latlng = L.latLng(maps.lat, maps.lng);
		
		if (maps.map_Vacancy){
			maps.zoom=maps.map_Vacancy._zoom
			map.off();
			map.remove();
		}
		
		map = L.map('map_Vacancy', {center: latlng, zoom: maps.zoom, layers: [tiles]});
		maps.map_Vacancy=map;
		
		$.ajax(url).complete(
						function(xhr) {

							let poi = JSON.parse(xhr.responseText);
							let lung = poi.response.docs.length;
							let markers = L.markerClusterGroup();

							for (let i = 0; i < lung; i++) {
								var flgIdo = poi.response.docs[i].flg_ido === "true"
								let latit = poi.response.docs[i].latitudine
								let longin = poi.response.docs[i].longitudine
								let descr = poi.response.docs[i].descrizione
								let id_vacancy = poi.response.docs[i].id_va_dati_vacancy

								if (!isNaN(latit) ){
									let marker = L.marker(new L.LatLng(latit  + Math.random()/1000 -0.0005, 
															longin + Math.random()/1000 -0.0005), { title: descr });

									if (flgIdo)
										var html=`<a href="/${contextName}/faces/secure/azienda/vacancies/view_pf.xhtml?id=${id_vacancy}">${descr}</a>`
									else
										var html=`<a href="/${contextName}/faces/secure/azienda/vacancies/visualizza.xhtml?id=${id_vacancy}">${descr}</a>`

									marker.bindPopup(html);
									markers.addLayer(marker);
								}
							}
							map.addLayer(markers);

						})
	} catch (e) {
		console && console.error("Error(updateGeoMask): " + e)
	}
}



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

var openConferma = function(params) {
	var itemId = params.id;
	var vacancyId = params.idVa;
	var clicLavoro = params.clicLavoro;
	$('[id$="confermaCandidatura\:modal_form\:objectId"]').val(itemId);
	$('[id$="confermaCandidatura\:modal_form\:vaId"]').val(vacancyId);
	$('[id$="confermaCandidatura\:modal_form\:clicLavoro"]').val(clicLavoro);
	if (clicLavoro) {
		$(jq("confermaCandidatura:modal_form:cv_cl_vacancy")).show();
		$(jq("confermaCandidatura:modal_form:cv_vacancy")).hide();
	} else {
		$(jq("confermaCandidatura:modal_form:cv_cl_vacancy")).hide();
		$(jq("confermaCandidatura:modal_form:cv_vacancy")).show();
	}
	removeFileAttach();
};

function mostraMappa() {
	showMap = true;

	$(jq('form_lista_offerte:buttonMap')).hide();
	$(jq('form_lista_offerte:buttonTable')).show();
	$(jq('mapStartPlace')).css('height', '');
	//$(jq('riquadro_map')).fadeIn();
	$(jq('form_lista_offerte:riquadro_table')).css('display', 'none');
	$(jq('form_lista_offerte:buttonMoreBottom')).css('display', 'none');
	$(jq("form_lista_offerte:resultTop")).css('display', 'none');
	$(jq("form_lista_offerte:resultBottom")).css('display', 'none');

	param = $(jq("form_lista_offerte:paramSOLR")).val();

	$(jq("mapPlace")).css("display", "")
	
	maps.solr_params=param;
	updateGeoMask();
	
} // mostraMappa

function mostraTabella() {
	showMap = false;

	$(jq("mapPlace")).css("display", "none")
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
		} else {
			if ($(jq("form_lista_offerte:emptyTable")).css('display') == 'none') {
				showMap = false;
			}
		}
	}
	if (data.status == 'success') {
		window.disegnaBottoni && disegnaBottoni();
		if (showMap) {

			if (window.tt) {
				tt.close()
			}
			mostraMappa();
		}
	}
}

function mostraLoaderOfferte(data) {
	if (data.status == 'begin') {
		$('#imgMore').show();
		$(jq('form_lista_offerte:buttonMoreBottom')).hide();
	} else if (data.status == 'success') {
		$('#imgMore').hide();
		$(jq('form_lista_offerte:buttonMoreBottom')).show();
	}
}

function setHiddenInputs() {
	$("[id$=dove_hidden]").val($("[id$=dove\\:inputText]").val());
	$("[id$=cosa_hidden]").val($("[id$=cosa\\:inputText]").val());
};

var riquadro_map = $(jq('riquadro_map'))

$(function() {

	window.disegnaBottoni && disegnaBottoni();

	$(jq('pulsante_riquadro_left')).css('display', 'none');

	// il pannello loader parte nascosto
	$(jq('loader_lista_offerte')).css('display', 'none');

	$(jq("form_lista_offerte:emptyTable")).css('display', 'none');
	
	
});