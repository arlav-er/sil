if (!myportal)
	var myportal = {};

if (!myportal.inputText) {
	myportal.inputText = {
			init: function(ccid, type,helpMessage, helpPosition, minDateFree) {
				var myDiv = document.getElementById(ccid);
				myDiv.dateSelected = false;
				myDiv.menuOpen = false;
				myDiv.effect = document.getElementById(ccid + ":effect");
				myDiv.inputText = document.getElementById(ccid + ':inputText');
				myDiv.message = document.getElementById(ccid + ':message');
				
				myDiv.onevent = function(data) {
						//TODO
				}

				if ("date" == type || 'dataNascita' == type ) {
					var datepickerDefaults={
							minDate: minDateFree?null:0,
							dateFormat : 'dd/mm/yy',
							changeYear : true,
							changeMonth : true,
							showAnim : 'slideDown',
							yearRange: '-50:+50',
							showOn: "both",
							buttonImage: "/" + contextName + "/resources/images/calendar_"+regione+".gif",
							buttonImageOnly: true,
							onSelect : function(dateText, inst) {
								// quando seleziono una data dal calendario cancella eventuali
								// errori di validazione segnalati
								// do per scontato che vada bene...
								myDiv.dateSelected = true;
								//$(jq(myDiv.message.id)).empty();
								$(this).focus();
								
								if(typeof window.setValueChanged === "function")
									setValueChanged()
																
								setTimeout(function() {
									myDiv.dateSelected = false;
								},1000);
							},
							beforeShow : function(input,inst) {
								myDiv.menuOpen = true;
							},
							onClose : function(dateText, inst) {
								myDiv.menuOpen = false;
							}
						
						};
					datepickerExtra = {};
					if ('dataNascita' == type ) {
						datepickerExtra = {
								yearRange: '-120:-1',
								maxDate: '-10y',
								defaultDate: '-18y'
						};
					}
					datepickerSettings = $.extend({}, datepickerDefaults, datepickerExtra);

					$(jq(myDiv.inputText.id)).datepicker(datepickerSettings);
				}
				else if (helpMessage != '') {
						attachHelp(
								$(jq(myDiv.help.id)),
								$(jq(myDiv.inputText.id)),
								helpPosition,
								5,
								40);
				}
			}
	};
};