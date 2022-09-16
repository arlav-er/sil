if (!myportal)
	var myportal = {};

if (!myportal.modalPortletCopyDialog) {
	myportal.modalPortletCopyDialog = {
			init: function(options) {
				var myDiv = document.getElementById(options.ccid);
				var a;
				$(jq(options.ccid)).dialog({
					autoOpen: false,
					modal: true
				});
						
				myDiv.open = function(itemId, itemDescrizione,elem) {
					a = elem; 
					//console.log(itemId);
					//console.log(itemDescrizione);
					//console.log(elem);
					var copyItemDescrizione = itemDescrizione;
					$(jq(options.ccid + ':copy_form:descrizione_copia')).val(copyItemDescrizione);
					//console.log(jq(options.ccid + ':copy_form:descrizione_copia'));
					$(jq(options.ccid + ':copy_form:id_copia')).val(itemId);
					$(jq(options.ccid)).dialog('open');
					return false;
				};
				
				myDiv.close = function() {
					$(jq(options.ccid)).dialog('close');
					a.focus();
					return true;
				};
			}
	};
};