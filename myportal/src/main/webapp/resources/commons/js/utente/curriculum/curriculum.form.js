/**
 * Curriculum
 * 
 * Yuri: http://cnc.wikia.com/wiki/Yuri_Prime_%28Yuri%27s_Revenge%29
 * 
 * I am my own master, always!
 * 
 * @author: hatemalimam
 * @date: 25/03/2020
 * @updated: 25/03/2020
 */

var Curriculum = MyPortal.createNS("MyPortal.Curriculum");

(function(Curriculum, $, undefined) {

	Curriculum.pageTitle = 'Modifica il tuo Curriculum';
	Curriculum.initForm = function() {

		var navForm = {
			'datiPrincipali' : 'X2VkaXQvcGYvX3RhYl9kYXRpX3ByaW5jaXBhbGlfcGYueGh0bWw=',
			'esperienze' : 'X2VkaXQvcGYvX2VzcGVyaWVuemUvX21haW4ueGh0bWw=',
			'istruzioni' : 'X2VkaXQvcGYvX2lzdHJ1emlvbmkvX21haW4ueGh0bWw=',
			'formazione' : 'X2VkaXQvcGYvX2Zvcm1hemlvbmUvX21haW4ueGh0bWw=',
			'lingue' : 'X2VkaXQvcGYvX2xpbmd1ZS9fbWFpbi54aHRtbA==',
			'competenzeDigitali' : 'X2VkaXQvcGYvX2NvbXBldGVuemVEaWdpdGFsaS9fbWFpbi54aHRtbA==',
			'abilitazioni' : 'X2VkaXQvcGYvX2FiaWxpdGF6aW9uaS9fbWFpbi54aHRtbA==',
			'competenzeTrasversali' : 'X2VkaXQvcGYvX2NvbXBldGVuemVUcmFzdmVyc2FsaS9fbWFpbi54aHRtbA==',
			'professioniDesiderate' : 'X2VkaXQvcGYvX3Byb2Zlc3Npb25pL19tYWluLnhodG1s',
			'altreInfo' : 'X2VkaXQvcGYvX2FsdHJlSW5mby9fbWFpbi54aHRtbA==',
		};

		MyPortal.Nav.comeToYuri(Curriculum.pageTitle);

		History.Adapter.bind(window, 'statechange', function() {
			var State = History.getState();

			updateSection([ {
				name : 'currentSection',
				value : navForm[MyPortal.Nav.getStateParam('yuri')]
			} ]);
		});

		if (!MyPortal.Nav.getStateParam('yuri')) {
			Curriculum.navDatiPrincipali();
		}

		$(window).trigger('statechange');

		$('.progress .progress-bar').css("width", function() {
			return $(this).attr("aria-valuenow") + "%";
		})

		bindEvents();

	}

	function bindEvents() {
		$("body").on("click", ".toggle-section-help", function() {
			
			adjustSectionsHeights();
			
			setTimeout(function() {
				adjustSectionsHeights();	
			}, 200)
			
			setTimeout(function() {
				adjustSectionsHeights();	
			}, 500)
			
		});

		$("#editUserUploadMessages").on("click", ".ui-messages-close, .ui-messages-error-icon", function(event) {
			if (PrimeFaces.widgets['uploadFileWV']) {
				event.preventDefault()
				PrimeFaces.widgets.uploadFileWV.jq.find('.ui-messages-close').click();
				var b = $('#editUserUploadMessages').html("");
			}
		});

		bindEventsArticleControls();
		MyPortal.initTooltip();

		setTimeout(function() {
			if (PrimeFaces.widgets['uploadFileWV']) {
				PrimeFaces.widgets.uploadFileWV.chooseButton.find('input').change(
						function() {
							var a = PrimeFaces.widgets.uploadFileWV.jq.find('.ui-fileupload-content .ui-messages').get(
									0).outerHTML;
							var b = $('#editUserUploadMessages').html(a);
							$('.btn-file').tooltip('hide');
						})
			}
		}, 1000);

		$("#editUserUploadMessages").on("click", ".ui-fileupload-cancel, .ui-messages-error-icon", function(event) {
			if (PrimeFaces.widgets['uploadFileWV']) {
				event.preventDefault()
				PrimeFaces.widgets.uploadFileWV.cancelButton.click();
				var b = $('#editUserUploadMessages').html("");
			}
		});

		$("#editUserUploadMessages").on("click", ".ui-messages-close, .ui-messages-error-icon", function(event) {
			if (PrimeFaces.widgets['uploadFileWV']) {
				event.preventDefault()
				PrimeFaces.widgets.uploadFileWV.jq.find('.ui-messages-close').click();
				var b = $('#editUserUploadMessages').html("");
			}
		});
	}

	Curriculum.navigationStart = function() {
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
	Curriculum.navigationComplete = function(args, xhr, status) {
		MyPortal.initTooltip();
		// MyPortal.Curriculum.bindSortableEvents();

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

		checkProgressValueRC();

		unbindEventsArticleControls();
		bindEventsArticleControls();
		// Error Handling
		if (status == "error") {
			MyPortal.unexpectedError();
		}
	}

	Curriculum.checkProgressValueComplete = function(args) {
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

		History.pushState(null, Curriculum.pageTitle, params);
	}

	Curriculum.navDatiPrincipali = function() {
		historyPushYuriState("datiPrincipali");
	}

	Curriculum.navEsperienze = function() {
		historyPushYuriState("esperienze");
	}

	Curriculum.navAbilitazioni = function() {
		historyPushYuriState("abilitazioni");
	}

	Curriculum.navIstruzioni = function() {
		historyPushYuriState("istruzioni");
	}

	Curriculum.navFormazione = function() {
		historyPushYuriState("formazione");
	}

	Curriculum.navLingue = function() {
		historyPushYuriState("lingue");
	}

	Curriculum.navCompetenzeDigitali = function() {
		historyPushYuriState("competenzeDigitali");
	}

	Curriculum.navProfessioniDesiderate = function() {
		historyPushYuriState("professioniDesiderate");
	}

	Curriculum.navDatiPrincipali = function() {
		historyPushYuriState("datiPrincipali");
	}

	Curriculum.navCompetenzeTrasversali = function() {
		historyPushYuriState("competenzeTrasversali");
	}

	Curriculum.navAltreInfo = function() {
		historyPushYuriState("altreInfo");
	}

	Curriculum.navigateDatiPrincipaliComplete = function(args) {
		if (args) {
			if (args.validationFailed) {
				// for now nothing
			} else {
				if (args.datiPersonaliId) {
					History.pushState(null, Curriculum.pageTitle, "?yuri=esperienze&id=" + args.datiPersonaliId);
				} else {
					Curriculum.navEsperienze();
				}

			}
		}
	}

	Curriculum.navigateAltreInfoComplete = function(args) {
		if (args) {
			if (args.validationFailed) {
				// for now nothing
			} else {
				Curriculum.navDatiPrincipali();
			}
		}
	}

	Curriculum.navigateAbilitazioniComplete = function(args) {
		if (args) {
			if (args.validationFailed) {
				// for now nothing
			} else {
				Curriculum.navCompetenzeTrasversali();
			}
		}
	};

	Curriculum.navigateAltreCompetenzeComplete = function(args) {
		if (args) {
			if (args.validationFailed) {
				// for now nothing
			} else {
				Curriculum.navProfessioniDesiderate();
			}
		}
	}

	Curriculum.navigateCompetenzeDigitaliComplete = function(args) {
		if (args) {
			if (args.validationFailed) {
				// for now nothing
			} else {
				Curriculum.navAbilitazioni();
			}
		}
	};

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
		var navMenuHeight = $('.curriculum-menu').height() + 250;

		var newHeight = navMenuHeight > contentHeight ? navMenuHeight : contentHeight;
		$('.curriculum-main-content').css('height', newHeight + "px");
		$('.curriculum-sidebar').css('height', newHeight + "px");
	}

	function disableBrowserAutocomplete() {
		$('.form-horizontal').prop('autocomplete', 'off');
		$('.form-horizontal input').prop('autocomplete', 'off');
	}

	Curriculum.bindSortableEvents = function() {
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

	Curriculum.notifySave = function() {
		MyPortal.sucessMessage("Salvataggio avvenuto con successo.");
	}

	Curriculum.callAddNewArticleComplete = function(args, xhr, status) {
		$('.add-new-btn').addClass('hidden');
		$('.syncForm').removeClass('hidden').prepend('<hr/>');
		MyPortal.initTooltip();
		adjustSectionsHeights();
		disableBrowserAutocomplete();
	}

	Curriculum.syncArticleComplete = function(args, xhr, status) {
		if (!args.validationFailed) {
			updateArticles();
			$('.add-new-btn').removeClass('hidden');
			$('.syncForm').addClass('hidden');
			bindEventsArticleControls();
			adjustSectionsHeights();
			Curriculum.notifySave();

		} else {
			// Message errror
		}

	};

	Curriculum.cancelArticleComplete = function(args, xhr, status) {
		$('.add-new-btn').removeClass('hidden');
		$('.syncForm').addClass('hidden');
		bindEventsArticleControls();
		adjustSectionsHeights();
		// Curriculum.bindSortableEvents();
	}

	Curriculum.callEditArticleComplete = function(args, xhr, status, element) {
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

	Curriculum.removeIstruzione = function() {
		swal({
			title : "Sei sicuro di voler eliminare l'istruzione ?",
			type : 'warning',
			showCancelButton : true,
			confirmButtonColor : "#c82b2e",
			cancelButtonText : "Annulla",
		}, function() {
			removeIstruzione();
		});
	}

	Curriculum.removeProfessione = function() {
		swal({
			title : "Sei sicuro di voler eliminare la professione ?",
			type : 'warning',
			showCancelButton : true,
			confirmButtonColor : "#c82b2e",
			cancelButtonText : "Annulla",
		}, function() {
			removeProfessione();
		});
	}

	Curriculum.onCopiaCurriculumComplete = function(args) {
		if (args) {
			if (args.validationFailed) {
				// for now nothing
			} else {
				$('#copyCurriculum-modal').modal('hide')
			}
		}
	}

}(window.MyPortal.Curriculum = window.Curriculum || {}, jQuery));
