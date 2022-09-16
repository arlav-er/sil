/**
 * CurriculumSearch
 * 
 * @author: hatemalimam
 * @date: 28/04/2020
 */
var CurriculumSearch = MyPortal.createNS("MyPortal.CurriculumSearch");

(function(CurriculumSearch, $, undefined) {

	CurriculumSearch.init = function() {
		jQuery("time.timeago").timeago();

		$('.collapse').collapse('hide');
		MyPortal.CurriculumSearch.bindEvents();

		setTimeout(function() {
			$('.ui-datagrid').fadeIn();
		}, 1000);

	}

	CurriculumSearch.bindEvents = function() {
		jQuery("time.timeago").timeago();
		MyPortal.initTooltip();
	}

	CurriculumSearch.removeRefParam = function() {
		window.history.replaceState('', '', MyPortal.removeURLParam(window.location.href, 'ref'));
	}

	CurriculumSearch.pageChangeEvent = function() {
		jQuery('time.timeago').timeago();
		$("html, body").animate({
			scrollTop : $("#curriculumResultsForm").offset().top
		}, "slow");
	}

	CurriculumSearch.doSearchComplete = function(args) {
		MyPortal.CurriculumSearch.bindEvents();
	}

	CurriculumSearch.inviaCompleted = function(args) {
		if (args && !args.validationFailed) {
			if (args.inviaSuccess) {
				$('#contatta-modal').modal('hide');
				swal({
					title : "Messaggio inviato",
					text : "Messaggio  inviato correttamente",
					type : "success",
					confirmButtonColor : "#499358",
				});
			} else {
				$('.contatta-error').removeClass("hidden");
			}
		}
	}
}(window.MyPortal.CurriculumSearch = window.CurriculumSearch || {}, jQuery));

/**
 * END CurriculumSearch
 */

