﻿﻿

var loadOpenStreetMap= function(mapDiv, mapContainer, maps) {
	try {
		var url = "/" + contextName + "/secure/rest/geo/getPoiVacanciesValide?";
		url += "solr_param=" + encodeURIComponent(maps.solr_params + "&wt=json&omitHeader=true");
		
		var tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
			maxZoom: 19,
			attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, Points &copy 2012 LINZ'
		}),		latlng = L.latLng(maps.lat, maps.lng);
		
		var map = L.map(mapDiv, {center: latlng, zoom: maps.zoom, layers: [tiles]});
		$(jq(mapContainer)).css("display", "")
		map.invalidateSize();
		
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
		console && console.error("Error(loadOpenStreetMap): " + e)
	}
	
	
}

var loadOnePoiOpenStreetMap= function(mapDiv, mapContainer, maps) {	
		if (maps.lat == "" || maps.lng == "" )
			return;
		
		var tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
			maxZoom: 19,
			attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, Points &copy 2012 LINZ'
		}),		latlng = L.latLng(maps.lat, maps.lng);
		
		var map = L.map(mapDiv, {center: latlng, zoom: maps.zoom, layers: [tiles]});

		L.marker([maps.lat, maps.lng]).addTo(map);

		
		$(jq(mapContainer)).css("display", "")
		map.invalidateSize();
		
		
}