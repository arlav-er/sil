if (!myportal)
	var myportal = {};

if (!myportal.infoSectionPanel) {
	myportal.infoSectionPanel = {
		init : function(options) {
			$(jq(options.targetId)).html(options.infoMessage);
			console.log($(jq(options.infoButton)));
			$(jq(options.infoButton)).attr('tabindex','0');
			$(jq(options.infoButton)).click(function() {
				/*
				 * Apre e chiude il pannello delle
				 * schede informative
				 */
				$(jq(options.targetId)).slideToggle(function() {
					/* non appena il pannello e' comparso la pagina
					 * il browser fa scroll
					 */
					$('html, body').animate({
						scrollTop : $(jq(options.targetId)).offset().top
					}, 500);
				});
			});
			$(jq(options.infoButton)).keypress(function(e) {
				/*
				 * Apre e chiude il pannello delle
				 * schede informative
				 */
				console.log(e.keyCode);
				if(e.keyCode == 13) {
				$(jq(options.targetId)).slideToggle(function() {
					/* non appena il pannello e' comparso la pagina
					 * il browser fa scroll
					 */
					$('html, body').animate({
						scrollTop : $(jq(options.targetId)).offset().top
					}, 500);
				});
				}
			});
		}
	};
};