/**
 * Vacancy
 * 
 * Yuri: http://cnc.wikia.com/wiki/Yuri_Prime_%28Yuri%27s_Revenge%29
 * 
 * I am my own master, always!
 * 
 * @author: hatemalimam
 * @date: 08/04/2020
 * @updated: 08/04/2020
 */

var Vacancy = MyPortal.createNS("MyPortal.Vacancy");

(function(Vacancy, $, undefined) {

	Vacancy.pageTitle = 'Vacancy';
	Vacancy.initForm = function() {

		var navForm = {
			'datiGenerali' : 'X2VkaXQvcGYvX2RhdGlHZW5lcmFsaS9fbWFpbi54aHRtbA==',
			'condizioniProposte' : 'X2VkaXQvcGYvX2NvbmRpemlvbmlQcm9wb3N0ZS9fbWFpbi54aHRtbA==',
			'istruzioni' : 'X2VkaXQvcGYvX2lzdHJ1emlvbmkvX21haW4ueGh0bWw=',
			'lingue' : 'X2VkaXQvcGYvX2xpbmd1ZS9fbWFpbi54aHRtbA==',
			'competenzeDigitali' : 'X2VkaXQvcGYvX2NvbXBldGVuemVEaWdpdGFsaS9fbWFpbi54aHRtbA==',
			'abilitazioni' : 'X2VkaXQvcGYvX2FiaWxpdGF6aW9uaS9fbWFpbi54aHRtbA==',
			'competenzeTrasversali' : 'X2VkaXQvcGYvX2NvbXBldGVuemVUcmFzdmVyc2FsaS9fbWFpbi54aHRtbA==',
			'altreInfo' : 'X2VkaXQvcGYvX2FsdHJlSW5mby9fbWFpbi54aHRtbA==',
			'contatto' : 'X2VkaXQvcGYvX2NvbnRhdHRvL19tYWluLnhodG1s',
		};

		var urlParams = new URLSearchParams(window.location.search);

		if (urlParams.has('prorogata')){
			MyPortal.successAlert('Vacancy prorogata');
		}
		
		MyPortal.Nav.comeToYuri(Vacancy.pageTitle);

		History.Adapter.bind(window, 'statechange', function() {
			var State = History.getState();

			updateSection([ {
				name : 'currentSection',
				value : navForm[MyPortal.Nav.getStateParam('yuri')]
			} ]);
		});

		if (!MyPortal.Nav.getStateParam('yuri')) {
			Vacancy.navDatiGenerali();
		}

		$(window).trigger('statechange');

		$('.progress .progress-bar').css("width", function() {
			return $(this).attr("aria-valuenow") + "%";
		})

		bindEvents();

	}

	function bindEvents() {
		bindEventsArticleControls();
		MyPortal.initTooltip();
	}

	Vacancy.navigationStart = function() {
		var State = History.getState();
		$('#section')
				.empty()
				.append(
						'<div class="row"><div class="col-md-12 text-center col-md-offset-5" style="width: 800px"><div  class="dataLoader"></div></div></div>');
		if (MyPortal.Nav.getStateParam('yuri') === 'all') {
			$('.all').addClass('active-red');
			$('.tl-wrap').addClass('active-red')
		} else {
			$('.navYuri').parent().removeClass('active');
			$('.' + MyPortal.Nav.getStateParam('yuri')).parent().addClass('active');
			$('.all').removeClass('active');
		}
	}

	/**
	 * when History state navigation complete
	 */
	Vacancy.navigationComplete = function(args, xhr, status) {
		MyPortal.initTooltip();
		// MyPortal.Vacancy.bindSortableEvents();

		if ($('.curriculum-main-row').offset().top < $(window).scrollTop()) {
			$('html, body').animate({
				scrollTop : $('.curriculum-main-row').offset().top
			}, 800);
		}

		setTimeout(function() {
			$('[data-toggle="confirm"]').jConfirm({
				question : "Sei sicuro di voler eliminare ?",
				confirm_text : 'Sì',
				deny_text : 'No',
				theme : 'white',
			}).on('confirm', function(e) {
				$(this).parent().find('.remove-article').click();
			});
		}, 1000);

		disableBrowserAutocomplete();

		adjustSectionsHeights();
		
		unbindEventsArticleControls();
		bindEventsArticleControls();
		// Error Handling
		if (status == "error") {
			MyPortal.unexpectedError();
		}
	}

	Vacancy.checkProgressValueComplete = function(args) {
		if (args) {
			if (args.progressValue) {
				var currentProgressValue = $('.progress .progress-bar').attr("aria-valuenow");
				var nextProgressValue = args.progressValue;
				if (nextProgressValue !== currentProgressValue) {
					$('.progress .progress-bar').attr("aria-valuenow", nextProgressValue);
					$('.progress .progress-bar').removeAttr("style");
					$('.progress-to').text(nextProgressValue + '%')
					$('.progress .progress-bar').css("width", function() {
						return $(this).attr("aria-valuenow") + "%";
					})
				}
			}
		}
	}

	function historyPushYuriState(sectionId) {
		var urlParams = new URLSearchParams(window.location.search);
		var curriculumId = urlParams.get('id');
		var params = "?yuri=" + sectionId;

		if (urlParams.has('id'))
			params = params + "&id=" + curriculumId;

		History.pushState(null, Vacancy.pageTitle, params);
	}

	Vacancy.navDatiGenerali = function() {
		historyPushYuriState("datiGenerali");
	}

	Vacancy.navCondizioniProposte = function() {
		historyPushYuriState("condizioniProposte");
	}

	Vacancy.navCompetenzeTrasversali = function() {
		historyPushYuriState("competenzeTrasversali");
	}

	Vacancy.navCompetenzeDigitali = function() {
		historyPushYuriState("competenzeDigitali");
	}

	Vacancy.navIstruzioni = function() {
		historyPushYuriState("istruzioni");
	}

	Vacancy.navAltreInfo = function() {
		historyPushYuriState("altreInfo");
	}

	Vacancy.navAbilitazioni = function() {
		historyPushYuriState("abilitazioni");
	}

	Vacancy.navContatto = function() {
		historyPushYuriState("contatto");
	}

	Vacancy.navigateIstruzioniComplete = function(args) {
		if (args) {
			if (args.validationFailed) {
				// for now nothing
			} else {
				Vacancy.navIstruzioni();
			}
		}
	};

	Vacancy.navigateAltreInfoComplete = function(args) {
		if (args) {
			if (args.validationFailed) {
				// for now nothing
			} else {
				Vacancy.navContatto();
			}
		}
	}

	Vacancy.navigateCompetenzeDigitaliComplete = function(args) {
		if (args) {
			if (args.validationFailed) {
				// for now nothing
			} else {
				Vacancy.navAbilitazioni();
			}
		}
	};

	Vacancy.navigateContattiComplete = function(args) {
		if (args) {
			if (args.validationFailed) {
				// for now nothing
			} else {
				Vacancy.navDatiGenerali();
			}
		}
	};

	Vacancy.navigateDatiGeneraliComplete = function(args) {
		if (args) {
			if (args.validationFailed) {
				// for now nothing
			} else {
				if (args.vacancyId) {
					History.pushState(null, Vacancy.pageTitle, "?yuri=condizioniProposte&id=" + args.vacancyId);
				} else if (args.errore) {
					Vacancy.navDatiGenerali();
				} else {
					Vacancy.navCondizioniProposte();
				}

			}
		}
	}

	Vacancy.navigateCompetenzeTrasversaliComplete = function(args) {
		if (args) {
			if (args.validationFailed) {
				// for now nothing
			} else {
				Vacancy.navAltreInfo();
			}
		}
	}

	function bindEventsArticleControls() {
		$('body').on('mouseenter', 'article.media', function() {
			$(this).find('.not-article-controls').addClass('hidden');
			$(this).find('.article-controls').removeClass('hidden').addClass('animated fadeIn');
			var isLastChild = $(this).is(':last-child');
			var isFirstChild = $(this).is(':first-child');

			if (isLastChild) {
				$(this).find('.article-control-down').hide();
			} else {
				$(this).find('.article-control-down').show();
			}

			if (isFirstChild) {
				$(this).find('.article-control-up').hide();
			} else {
				$(this).find('.article-control-up').show();
			}
		});

		$('body').on('mouseleave', 'article.media', function() {
			$(this).find('.not-article-controls').removeClass('hidden').addClass('animated fadeIn');
			$(this).find('.article-controls').addClass('hidden');
		})

		$('body').on('focusin', 'article.media', function() {

			$('article.media').not(this).each(function() {
				$(this).find('.not-article-controls').removeClass('hidden').addClass('animated fadeIn');
				$(this).find('.article-controls').addClass('hidden');
			});

			$(this).find('.not-article-controls').addClass('hidden');
			$(this).find('.article-controls').removeClass('hidden').addClass('animated fadeIn');
			var isLastChild = $(this).is(':last-child');
			var isFirstChild = $(this).is(':first-child');

			if (isLastChild) {
				$(this).find('.article-control-down').hide();
			} else {
				$(this).find('.article-control-down').show();
			}

			if (isFirstChild) {
				$(this).find('.article-control-up').hide();
			} else {
				$(this).find('.article-control-up').show();
			}

		})

		setTimeout(function() {
			$('[data-toggle="confirm"]').jConfirm({
				question : "Sei sicuro di voler eliminare ?",
				confirm_text : 'Sì',
				deny_text : 'No',
				theme : 'white',
			}).on('confirm', function(e) {
				$(this).parent().find('.remove-article').click();
			});
		}, 1000)

	}

	function unbindEventsArticleControls() {
		$('body').off('mouseenter', 'article.media');
		$('body').off('mouseleave', 'article.media');
		$('body').off('focusin', 'article.media');
		$('.not-article-controls').removeClass('hidden').addClass('animated fadeIn');
		$('.article-controls').addClass('hidden');
	}

	function resetArticleControls() {
		$('.article-controls').addClass('hidden');
		$('.not-article-controls').removeClass('hidden');
		bindEventsArticleControls();
	}

	function adjustSectionsHeights() {
		var contentHeight = $('.main-content').height() + 50;
		var navMenuHeight = $('.curriculum-menu').height() + 200;

		var newHeight = navMenuHeight > contentHeight ? navMenuHeight : contentHeight;
		$('.curriculum-main-content').css('height', newHeight + "px");
		$('.curriculum-sidebar').css('height', newHeight + "px");
	}

	function disableBrowserAutocomplete() {
		$('.form-horizontal').prop('autocomplete', 'off');
		$('.form-horizontal input').prop('autocomplete', 'off');
	}

	Vacancy.bindSortableEvents = function() {
		$('.articles').sortable({
			cursor : "-webkit-grabbing",
		}).on("sortstop", sortstopEvent);

		$('article.media .article-control-down').on('click', function() {
			var article = $(this).closest('article.media');
			article.hide().insertAfter(article.next()).fadeIn();
			sortstopEvent();
		});

		$('article.media .article-control-up').on('click', function() {
			var article = $(this).closest('article.media');
			article.hide().insertBefore(article.prev()).fadeIn();
			sortstopEvent();
		});

		function sortstopEvent(event, ui) {
			var sortedArray = $('.articles').sortable("toArray");
			sortArticles([ {
				name : 'sortedArray',
				value : sortedArray
			} ]);

			$('article.media').each(function() {
				var isLastChild = $(this).is(':last-child');
				var isFirstChild = $(this).is(':first-child');

				if (isLastChild) {
					$(this).find('.article-control-down').hide();
				} else {
					$(this).find('.article-control-down').show();
				}

				if (isFirstChild) {
					$(this).find('.article-control-up').hide();
				} else {
					$(this).find('.article-control-up').show();
				}
			})

		}
	}

	Vacancy.notifySave = function() {
		MyPortal.sucessMessage("Salvataggio avvenuto con successo.");
	}

	Vacancy.callAddNewArticleComplete = function(args, xhr, status) {
		$('.add-new-btn').addClass('hidden');
		$('.syncForm').removeClass('hidden').prepend('<hr/>');
		MyPortal.initTooltip();
		adjustSectionsHeights();
		disableBrowserAutocomplete();
	}

	Vacancy.syncArticleComplete = function(args, xhr, status) {
		if (!args.validationFailed) {
			updateArticles();
			$('.add-new-btn').removeClass('hidden');
			$('.syncForm').addClass('hidden');
			bindEventsArticleControls();
			adjustSectionsHeights();
			Vacancy.notifySave();

		} else {
			// Message errror
		}

	};

	Vacancy.cancelArticleComplete = function(args, xhr, status) {
		$('.add-new-btn').removeClass('hidden');
		$('.syncForm').addClass('hidden');
		bindEventsArticleControls();
		adjustSectionsHeights();
		// Vacancy.bindSortableEvents();
	}

	Vacancy.callEditArticleComplete = function(args, xhr, status, element) {
		unbindEventsArticleControls();
		$('.add-new-btn').addClass('hidden');
		$('.syncForm').removeClass('hidden');

		var article = $(PrimeFaces.escapeClientId(element)).parents().eq(3);
		$('#syncFormContent').insertAfter(article).fadeIn().append('<hr/>').prepend('<hr/>');
		article.hide();
		MyPortal.initTooltip();
		// $('.articles').sortable('disable');
		adjustSectionsHeights();

		disableBrowserAutocomplete();
	}

	Vacancy.navigateAbilitazioniComplete = function(args) {
		if (args) {
			if (args.validationFailed) {
				// for now nothing
			} else {
				Vacancy.navCompetenzeTrasversali();
			}
		}
	};

	Vacancy.removeIstruzione = function() {
		swal({
			title : "Sei sicuro di voler eliminare l'istruzione?",
			type : 'warning',
			showCancelButton : true,
			confirmButtonColor : "#c82b2e",
			cancelButtonText : "Annulla",
		}, function() {
			removeIstruzione();
		});
	}

	Vacancy.onPubblicaVacancyComplete = function(args) {
		if (args) {
			if (args.validationFailed) {
				// for now nothing
			} else {
				$('#publishVacancy-modal').modal('hide')
			}
		}
	}
	Vacancy.onCopiaVacancyComplete = function(args) {
		if (args) {
			if (args.validationFailed) {
				// for now nothing
			} else {
				$('#vacancies_copy').modal('hide')
			}
		}
	}

	Vacancy.onDeleteVacancyComplete = function(args) {
		if (args) {
			if (args.deletedSuccessfuly) {
				swal({
					title : "La Vacancy e' stata eliminata con successo",
					type : "success",
					confirmButtonColor : "#c82b2e"
				}, function() {
					redirectHomeRC();
				});
			} else {

			}
		}
	}

	Vacancy.onArchiviaVacancyComplete = function(args) {
		if (args) {
			if (args.validationFailed) {
				// for now nothing
			} else {
				$('#archiviaVacancy-modal').modal('hide')
			}
		}
	}
	
	Vacancy.onProrogaVacancyComplete = function(args) {
		if (args) {
			if (args.validationFailed) {
				// for now nothing
			} else {
				$('#prorogaVacancy-modal').modal('hide')
			}
		}
	}

}(window.MyPortal.Vacancy = window.Vacancy || {}, jQuery));
