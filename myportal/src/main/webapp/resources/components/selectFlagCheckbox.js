if (!myportal)
	var myportal = {};

if (!myportal.selectFlagCheckbox) {
	myportal.selectFlagCheckbox = {
			init: function(ccid) {				
				var myDiv = document.getElementById(ccid);
				myDiv.effect = document.getElementById(ccid + ":effect");
				myDiv.inputText = document.getElementById(ccid + ':checkbox');
				myDiv.message = document.getElementById(ccid + ':message');				
			}
	};
};