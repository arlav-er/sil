$(function() {				
		mexs = $('#globalMessagesContent li.errorMessage');
		mexs.each(function(index,element) {
			if(element.innerHTML.indexOf("UIForm") == -1) {		
				$.jnotify(element.innerHTML, 'error',true);
			}
		});

		mexs = $('#globalMessagesContent li.infoMessage');
		mexs.each(function(index,element) {		
			//sticky = element.innerHTML.length > 100;
			//$.jnotify(element.innerHTML, 'info',sticky);
			$.jnotify(element.innerHTML, 'info', true);
		});


		mexs = $('#globalMessagesContent li.warnMessage');
		mexs.each(function(index,element) {		
			//sticky = element.innerHTML.length > 100;
			//$.jnotify(element.innerHTML, 'info',sticky);
			$.jnotify(element.innerHTML, 'warning', true);
		});
		
	});
	
				
	function getKey(e) {
		var key;
		if (navigator.appName=="Netscape") key = e.keyCode;
		else key = window.event.keyCode;
		
		if (key == 27) {
			$('.jnotify-close').click();
		}
	} 
						
	document.onkeypress = getKey;	