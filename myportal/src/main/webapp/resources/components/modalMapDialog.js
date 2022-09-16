/**
 * Se non è stato definito myportal allora definiscilo
 */
if (!myportal)
	var myportal = {};

if (!myportal.modalMap) {
	myportal.modalMap = {
		init : function(ccid) {
			var myDiv = document.getElementById(ccid);
			//var dialog_map = $("#" + dialog_id);
			//var myDialog = document.getElementById(ccid);
			myDiv.mapFrame = document.getElementById(ccid + ':map_frame'); 

			// opzioni di visualizzazione della mappa
			myDiv.myOptions = {
				zoom : 10,
				navigationControl : true,
				mapTypeId : google.maps.MapTypeId.ROADMAP
			}

			$(jq(myDiv.id)).dialog({
				height : 400,
				width : 400,
				modal : true,
				autoOpen : false,
				show : "blind"
			});

			myDiv.map = new google.maps.Map(myDiv.mapFrame, myDiv.myOptions);
		},

		showMap : function(ccid,address) {
			var myDiv = document.getElementById(ccid);
			var geocoder = new google.maps.Geocoder();

			geocoder.geocode({
				'address' : address
			}, function(results, status) {
				if (status == google.maps.GeocoderStatus.OK) {
					myDiv.map.setCenter(results[0].geometry.location);
					var marker = new google.maps.Marker({
						map : myDiv.map,
						position : results[0].geometry.location,
						title : address
					});
				}
			});
			$(jq(myDiv.id)).dialog('open');
			google.maps.event.trigger(myDiv.map, 'resize');
			return false;
		}
	}
}