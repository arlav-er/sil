/**
 * CurriculumView
 * 
 * @author: hatemalimam
 * @date: 01/04/2020
 */
var CurriculumView = MyPortal.createNS("MyPortal.CurriculumView");

(function(CurriculumView, $, undefined) {

	CurriculumView.init = function() {
		bindEvents();

		$('#curriculum-nav').onePageNav({
			changeHash : false,
			scrollThreshold : 0.2,
			filter : ':not(.external)'
		});

		$(".curriculum-nav-sticky").sticky({
			topSpacing : 10
		});
		$(".curriculum-sidebox").sticky({
			topSpacing : 10
		});
		MyPortal.initTooltip();

	}

	bindEvents = function() {
		// modal events
		$(document).on('show.bs.modal', '.modal', centerModal);
		$(window).on("resize", function() {
			$('.modal:visible').each(centerModal);
		});

		$('#valuta-modal').on('shown.bs.modal', function() {
			PrimeFaces.zindex = 1051;
		});

		$('#valuta-modal').on('hidden.bs.modal', function() {
			PrimeFaces.zindex = 1003;
		});

		readMoreListEvent();
	}

	centerModal = function() {
		$(this).css('display', 'block');
		var $dialog = $(this).find(".modal-dialog"), offset = ($(window).height() - $dialog.height()) / 2, bottomMargin = parseInt(
				$dialog.css('marginBottom'), 10);
		if (offset < bottomMargin)
			offset = bottomMargin;
		$dialog.css("margin-top", offset);
	}

	readMoreListEvent = function() {
		$('ul.readmore').hideMaxListItems({
			'max' : 3,
			'moreText' : '+ Mostra più dettagli ([COUNT])',
			'lessText' : '- Nascondi dettagli',
		});
	}

	CurriculumView.valutaOnComplete = function(args) {
		if (args) {
			if (!args.validationFailed) {
				$('#valuta-modal').modal('hide');
				swal({
					title : "Punteggio assegnato con successo",
					type : "success",
					confirmButtonColor : "#c82b2e"
				});
			}
		}
	}
	
	CurriculumView.schedaValutaOnComplete = function(args) {
		if (args) {
			if (!args.validationFailed) {
				$('#schedaValutazione-modal').modal('hide');
				swal({
					title : "Scheda aggiornata con successo",
					type : "success",
					confirmButtonColor : "#c82b2e"
				});
			}
		}
	}
	
	CurriculumView.sendEmailLavoratoreOnComplete = function(args) {
		if (args) {
			if(!args.errore){
                if (!args.validationFailed) {
                    $('#sendEmailLavoratore-modal').modal('hide');
                    swal({
                        title : "Messaggio/i inviati correttamente!",
                        type : "success",
                        confirmButtonColor : "#c82b2e"
                    });
                }
			}else{
                $('#sendEmailLavoratore-modal').modal('hide');
				// for now nothing

			}

		}
	}

}(window.MyPortal.CurriculumView = window.CurriculumView || {}, jQuery));

/**
 * END CurriculumView
 */

