/**
 * 
 */

/*
 * Gamification object
 */

(function(Gamification, $, undefined) {

	Gamification.init = function() {

		$('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
			var target = $(e.target).attr("href") // activated tab
			if (target == "#badge") {
				var mixer = $('#availableBadgesGroup').mixItUp();
				$('#availableBadgesGroup .col-sm-4').removeAttr('style');
			} else if (target == "#percorsi") {
				$('#availableBadgesGroup').mixItUp('destroy', true);
			}
		});

		// modal events
		$(document).on('show.bs.modal', '.modal', centerModal);
		$(window).on("resize", function() {
			$('.modal:visible').each(centerModal);
		});

		prepareBadgesRC();
		preparePercorsiRC();

	};

	centerModal = function() {
		$(this).css('display', 'block');
		var $dialog = $(this).find(".modal-dialog"), offset = ($(window).height() - $dialog.height()) / 2, bottomMargin = parseInt(
				$dialog.css('marginBottom'), 10);
		if (offset < bottomMargin)
			offset = bottomMargin;
		$dialog.css("margin-top", offset);
	}

	Gamification.version = function() {
		return "1.0";
	};

	Gamification.initTooltip = function() {
		$('[data-toggle="tooltip"]').tooltip();
	};

	Gamification.ellipsisPercorsi = function() {
		$(".steps h2 .title").each(function() {
			if ($(this).text().length > 17)
				$(this).text($(this).text().substring(0, 17) + '...');
		});
	}

	Gamification.ellipsisBadges = function() {
		$(".badgebox-title").each(function() {
			if ($(this).text().length > 20)
				$(this).text($(this).text().substring(0, 20) + '...');
		});

		$(".badgebox-text p").each(function() {
			if ($(this).text().length > 50)
				$(this).text($(this).text().substring(0, 50) + '...');
		});
	}

	Gamification.prepareBadgesStart = function() {
		$('#badge')
				.prepend(
						'<div id="loader" class="col-md-offset-7" style="margin-top: 100px;"><div class="dataLoader"></div></div>');
	}

	Gamification.prepareBadgesComplete = function(args) {
		$('#loader').remove();

		if ($('.carousel').length) {
			var flky = new Flickity('.carousel', {
				accessibility : true,
				adaptiveHeight : false,
				autoPlay : false,
				cellAlign : 'center',
				cellSelector : undefined,
				contain : false,
				draggable : true,
				dragThreshold : 3,
				freeScroll : false,
				friction : 0.2,
				groupCells : false,
				initialIndex : 0,
				percentPosition : true,
				prevNextButtons : true,
				pageDots : true,
				resize : true,
				rightToLeft : false,
				setGallerySize : true,
				watchCSS : false,
				wrapAround : true
			});
		}

		$('.progress .progress-bar').progressbar();
		Gamification.ellipsisBadges();
		$('[data-toggle="tooltip"]').tooltip();
		$('.count').counterUp();

		$('#availableBadgesGroup').mixItUp();

		var height = $(".row-lines-v").height();
		$(".row-lines-v").css('height', height + "px");

		$('.percentage').easyPieChart({
			animate : 1000,
			lineWidth : 4,
			barColor : "#CB1D15",
			onStep : function(value) {
				this.$el.find('span').text(Math.round(value));
			},
			onStop : function(value, to) {
				this.$el.find('span').text(Math.round(to));
			}
		});
	}

	Gamification.preparePercorsiComplete = function(args) {
		Gamification.ellipsisPercorsi();
		$('[data-toggle="tooltip"]').tooltip();
	}

}(window.Gamification = window.Gamification || {}, jQuery));

$(function() {
	Gamification.init();
});
/**
 * read state params
 */
