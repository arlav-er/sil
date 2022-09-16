$(document).ready(function() {
	$('#xEditingArea iframe').contents().find('body').keydown(function(e) {
		var key = e.which;
		if (key == 13) // the enter key code
		{
			console.log(this.offsetHeight);

			return true;
		}
	});

});
