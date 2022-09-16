if (!myportal)
	var myportal = {};

if (!myportal.modalpDialog) {
	myportal.modalpDialog = {
			init: function(options) {
				var divs = $("div[id="+options.ccid.replace(/:/g,"\\:")+"]");
				//bugfix per quando ci sono più modali con lo stesso id. problema di jquery ui!
				
				//myDiv = divs.shift()
				divs.each(function(i,el) {
					if (i == 0) {
						myDiv = el;
					}
					else {
						$(this).remove();
					}
				});
				
				
				var openFunction = options.open;
				
				var a;
				if (options.width=='') {
					$(jq(options.ccid)).dialog({
						autoOpen: false,
						modal: true
					});
				} else {
					$(jq(options.ccid)).dialog({
						autoOpen: false,
						modal: true,
						width: options.width,
						height: options.height,
						close: function() { disegnaBottoni(); }					
					});
				}
						
				myDiv.open = function(openP,elem) {
					if ('' != openFunction) {
					    window[openFunction](openP);
					}
					a = elem; 
					console.log('opening dialog');
					disegnaBottoni();
					$(jq(options.ccid)).dialog('open');
					return false;
				};
				
				myDiv.close = function() {
					$(jq(options.ccid)).dialog('close');
					if (navigator.appName != 'Microsoft Internet Explorer') {
						a.focus();
					}
					disegnaBottoni();
					return true;
				};
			}
	};
};