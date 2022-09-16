if (!myportal)
	var myportal = {};

if (!myportal.modalVacancyDialog) {
	myportal.modalVacancyDialog = {
			init: function(options) {
				var myDiv = document.getElementById(options.ccid);
				
				var a;
				$(jq(options.ccid)).dialog({
					height: 500,
					width: 500,
					modal: true,
					autoOpen: false
				});
				myDiv.open = function(elem) {
					a = elem;
					$(jq(options.ccid)).dialog('open');
					return false;
				};
				
				myDiv.close = function() {
					$(jq(options.ccid)).dialog('close');
					a.focus();
					return true;
				};
//				console.log('mydiv');
//				console.log(myDiv);
//				console.log($(jq(options.ccid)));
			}
	};
};