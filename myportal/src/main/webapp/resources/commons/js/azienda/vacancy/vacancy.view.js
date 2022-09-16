/**
 * Home
 * 
 * @author: hatemalimam
 * @date: 10/05/2017
 */
var VacancyView = MyPortal.createNS("MyPortal.VacancyView");

(function(VacancyView, $, undefined) {

	VacancyView.centerPosition = "46.0730767, 11.1174803";

	VacancyView.init = function() {
		jQuery("time.timeago").timeago();
		VacancyView.bindEvents();
		VacancyView.resizeWindowHandler();
		VacancyView.initMap();

		var urlParams = new URLSearchParams(window.location.search);

		if (urlParams.has('published'))
			MyPortal.successAlert('Vacancy pubblicata');

		if (urlParams.has('archived'))
			MyPortal.successAlert('Questa Vacancy e` stata archiviata');
	}

	VacancyView.bindEvents = function() {
		// modal events
		$(document).on('show.bs.modal', '.modal', centerModal);
		$(window).on("resize", function() {
			$('.modal:visible').each(centerModal);
			VacancyView.resizeWindowHandler();
		});

		VacancyView.initCVAllegatiFileUpload();
		VacancyView.initLetteraAllegatiFileUpload();

		if (MyPortal.VacancyView.expiredVacancy) {
			MyPortal.VacancyView.vacancyExpiredNotification();
		}

		$('.linkify').linkify({
			target : "_blank"
		});

		$('#confermaCandidaturaView-modal').on('shown.bs.modal', function() {
			PrimeFaces.zindex = 1051;
		});

		$('#confermaCandidaturaView-modal').on('hidden.bs.modal', function() {
			PrimeFaces.zindex = 1003;
		});
	}

	centerModal = function() {
		$(this).css('display', 'block');
		var $dialog = $(this).find(".modal-dialog"), offset = ($(window).height() - $dialog.height()) / 2, bottomMargin = parseInt(
				$dialog.css('marginBottom'), 10);
		if (offset < bottomMargin)
			offset = bottomMargin;
		$dialog.css("margin-top", offset);
	}

	function bindFileUploadEvents(widgetVar, messagesContainer, buttonFile) {
		try {
			widgetVar.chooseButton.find('input').change(function() {
				var a = widgetVar.jq.find('.ui-fileupload-content .ui-messages').get(0).outerHTML;
				var b = messagesContainer.html(a);
				buttonFile.tooltip('hide');
			})

			messagesContainer.on("click", ".ui-fileupload-cancel", function(event) {
				event.preventDefault()
				widgetVar.cancelButton.click();
				var b = messagesContainer.html("");
			});

			messagesContainer.on("click", ".ui-messages-close", function(event) {
				event.preventDefault()
				widgetVar.jq.find('.ui-messages-close').click();
				var b = messagesContainer.html("");
			});
		} catch (ex) {

		}

	}

	VacancyView.vacancyExpiredNotification = function() {
		swal({
			title : "Offerta Scaduta",
			text : "Offerta Scaduta, non puoi contattare il datore di lavoro.",
			type : "warning",
			confirmButtonColor : "#499358",
		});
	}

	VacancyView.initCVAllegatiFileUpload = function() {
		setTimeout(function() {
			if (PrimeFaces.widgets['cvAllegatoWV']) {
				bindFileUploadEvents(PF('cvAllegatoWV'), $('#cvAllegatoMessages'), $('#cvAllegatoBtnFile'));
			}
		}, 2000);
	}

	VacancyView.initLetteraAllegatiFileUpload = function() {
		setTimeout(function() {
			if (PrimeFaces.widgets['letteraAllegatoWV']) {
				bindFileUploadEvents(PF('letteraAllegatoWV'), $('#letteraAllegatoMessages'),
						$('#letteraAllegatoBtnFile'));
			}
		}, 2000);
	}

	VacancyView.inviaCompleted = function(args) {
		if (args && !args.validationFailed) {
			if (args.inviaSuccess) {
				$('#contatta-modal').modal('hide');
				swal({
					title : "Mail inviata",
					text : "Mail candidatura inviata correttamente",
					type : "success",
					confirmButtonColor : "#499358",
				});
			}
		}
	}

	VacancyView.updateSavedVacancy = function() {
		var vacancyId = $('.job').attr('id').split("-").pop();
		if (MyPortal.isVacancySaved(Number(vacancyId))) {
			$('.remove-vacancy').removeClass('hidden');
			$('.save-vacancy').addClass('hidden');
		} else {
			$('.save-vacancy').removeClass('hidden');
			$('.remove-vacancy').addClass('hidden');
		}
	}

	VacancyView.findCoordsByAddress = function(address) {
		geocoder = new google.maps.Geocoder();
		geocoder.geocode({
			'address' : address
		}, function(results, status) {
			if (status == google.maps.GeocoderStatus.OK) {
				var myLat = results[0]['geometry']['location'].lat();
				var myLng = results[0]['geometry']['location'].lng();
				coordsReceivedRC([ {
					name : 'lat',
					value : myLat
				}, {
					name : 'lng',
					value : myLng
				} ]);
			}
		});
	}

	VacancyView.coordsReceivedComplete = function() {
	}

	VacancyView.candidatiWarning = function() {
		swal(
				{
					title : "Candidati",
					text : "Puoi candidarti  per questa offerta, inviando il  tuo curriculum, se  ti trovi nella seguente condizione: <br />"
							+ "<br /> <p>iscritto/a ad uno dei Centri per l'Impiego della Regione Umbria  ai sensi del D.Lgs. 150/2015 con rilascio della Dichiarazione di Immediata Disponibilità,  alla data di candidatura</p>"
							+ " <br /> <p>Nel caso in cui tu sia titolare di un rapporto di  lavoro intermittente (o a chiamata) la tua candidatura sarà inoltrata all’azienda ma considerata valida solo se:</p>"
							+ "<br /> <ul>"
							+ "<li>non si siano superate le 180 giornate lavorative dalla data di avvio del contratto di lavoro</li>"
							+ "<li>presentata in un giorno in cui non sia prevista la prestazione lavorativa.</li>"
							+ "</ul>",
					showCancelButton : true,
					confirmButtonColor : "#499358",
					confirmButtonText : "Continua",
					cancelButtonText : "Annulla",
					closeOnConfirm : true,
					html : true,
					customClass : 'swal-wide',
					imageUrl : "https://serviziocresco.it/wp-content/uploads/2016/03/logo-cresco.png"
				}, function() {
					window.open(MyPortal.VacancyView.vacancyURL, '_blank');
				});

	}

	VacancyView.resizeWindowHandler = function() {
		if (MyPortal.isMobileView()) {
			if ($(".right-sec").find(".map-section").length === 0) {
				$('.map-section').detach().appendTo($('.right-sec'));
			}

		} else {
			if ($(".left-sec").find(".map-section").length === 0) {
				$('.map-section').detach().appendTo($('.left-sec'));
			}
		}
	}

	VacancyView.initMap = function() {
		var options = {
			dragging : true
		};

		if (MyPortal.isMobileView()) {
			options.dragging = false;
		}

		var mymap = L.map('vacancy-map', options).setView(
				[ MyPortal.VacancyView.centerPositionLat, MyPortal.VacancyView.centerPositionLng ], MyPortal.VacancyView.mapZoom);

		var markerIcon = L.icon({
			iconUrl : MyPortal.VacancyView.markerIcon,
			iconSize : [ 25, 41 ], // size of the icon
		});

		var OpenStreetMap_Mapnik = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
			attribution : '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
		}).addTo(mymap);

		L.marker([ MyPortal.VacancyView.centerPositionLat, MyPortal.VacancyView.centerPositionLng ], {
			icon : markerIcon
		}).addTo(mymap).bindPopup(MyPortal.VacancyView.markerTitle);

	}

	VacancyView.onInviaCandidaturaVacancyComplete = function(args) {
		VacancyView.number = args.number;
		if (args) {
			if (args.validationFailed) {
				// for now nothing
			} else {
				// PF('confermaCandidaturaViewDialog').hide();
				$('#confermaCandidaturaView-modal').modal('hide')
				swal({
					title : "Candidatura inviata correttamente",
					text : "Candidatura inviata correttamente con numero " + VacancyView.number,
					type : "success",
					confirmButtonColor : "#499358",
				}, function() {
        			refresh();
        		});
			}
		}
	}

	VacancyView.onArchiviaVacancyComplete = function(args) {
		if (args) {
			if (args.validationFailed) {
				// for now nothing
			} else {
				$('#archiviaVacancyView-modal').modal('hide')
				if(args.archived){
					swal({
						title : "Archiviazione avvenuta correttamente",
						type : "success",
						confirmButtonColor : "#499358",
					});
				}
			}
		}
	}
	
	VacancyView.onInviaCandidaturaVacancy = function() {
		PF('confermaCandidaturaViewDialog').show();
	}

	VacancyView.onNoCandidaturaVacancy = function() {
		PF('noCandidaturaDialog').show();
	}

	VacancyView.pageVacancyChangeEvent = function() {
		jQuery('time.timeago').timeago();
		$("html, body").animate({
			scrollTop : $("#vacancyResultsForm").offset().top
		}, "slow");
	}

}(window.MyPortal.VacancyView = window.VacancyView || {}, jQuery));

/**
 * END Home
 */

