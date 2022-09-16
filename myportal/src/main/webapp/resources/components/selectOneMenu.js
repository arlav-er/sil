if (!myportal)
	var myportal = {};

if (!myportal.selectOneMenu) {
	myportal.selectOneMenu = {
			init: function(options) {
				//alert('init options:'+JSON.stringify(options));
				var ccid = options.ccid;
				var disabled = options.disabled;				
				var disableInput = options.disableInput;
				var styleClass = options.styleClass;
				var style = options.style;
//				per usi futuri
//				var selectOneMenuExtra = options.selectOneMenuExtra;
				
				
				combo = $(jq(ccid+':combobox')).combobox({					
				});
				
				
				btn=$(jq(ccid+':combobox:button'));
				
				btn.attr('tabindex',0);
				
				if (styleClass != '') {
					$(jq(ccid+':combobox:input')).addClass(styleClass);
				}
				
				if (style != '') {
					$(jq(ccid+':combobox:input')).attr('style',style);
				}

				if (disableInput == true) {
					
					$(jq(ccid+':combobox:input')).click(function(){$(jq(ccid+':combobox:input')).blur();});
					$(jq(ccid+':combobox:input')).keypress(function(e){
					
						var prev = $(jq(ccid+':combobox:input')).prop('value');
					
						
					
						if (e.keyCode!=9 && e.keyCode!=40 && e.keyCode!=38) {
							$(jq(ccid+':combobox:input')).blur().attr('value',prev);
							setTimeout(function(){$(jq(ccid+':combobox:input')).attr('value',prev);},50);
							$(jq(ccid+':combobox:button')).focus();
						}
						
						});
					btn.prop('tabindex',0);
				}
				if (disabled == true) {
					$(jq(ccid+':combobox:input')).prop('disabled',true).css('cursor','default');
					btn.prop('disabled',true).css('cursor','default');
					$(jq(ccid+':combobox:input')).addClass('ui-state-disabled');
					$(jq(ccid+':combobox:button')).addClass('ui-state-disabled')										
					.data('disabled',true);
				}
			}
	};
	
};