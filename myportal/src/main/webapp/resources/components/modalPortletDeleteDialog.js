if (!myportal)
	var myportal = {};

if (!myportal.modalPortletDeleteDialog) {
	myportal.modalPortletDeleteDialog = {
			init: function(options) {
				var myDiv = document.getElementById(options.ccid);
				var a;
				$(jq(options.ccid)).dialog({
					autoOpen: false,
					modal: true
				});
						
				myDiv.open = function(itemId,elem) {
					a = elem;
					$(jq(options.ccid + ':delete_form:id_delete')).val(itemId);
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