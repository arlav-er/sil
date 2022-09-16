
(function() {
	
	if(!this.myportal) 				myportal = {};
	if(!myportal.helpButton) 	myportal.helpButton = {};

		
	$(function(){$("body").bind("mousedown", function(e) {
		var node;
		
		e 		= e || event;
		node	=	e.target || e.srcElement;				
				
		if(myportal.helpButton.tip) 
			myportal.helpButton.tip.style.display = "none";				 		
	});});	
	
	myportal.helpButton.show = function(e) {
		var node;
		
		e 	 = e || event;
		node = e.target || e.srcElement;				
		node = myportal.helpButton.tip = $(node).next()[0];
		node.style.display = "";
		
		e.stopPropagation && e.stopPropagation();
	};
})();