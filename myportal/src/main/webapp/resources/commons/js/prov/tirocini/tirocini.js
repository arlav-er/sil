/**
 * Commons
 * 
 * Yuri: http://cnc.wikia.com/wiki/Yuri_Prime_%28Yuri%27s_Revenge%29
 * 
 * I am my own master, always!
 * 
 * User
 * 
 * @author: hatemalimam
 * @date: 25/11/2014
 */

nav = {		
	'list' : 'sections/_list.xhtml',
}

$(document).ready(function() {	
	comeToYuri();
	
	History.Adapter.bind(window, 'statechange', function() {
		var State = History.getState();
		updateSection([ {
			name : 'currentSection',
			value : nav[getStateParam('yuri')]
		} ]);
	});

	$(window).trigger('statechange');
});

function navigationStart() {
	var State = History.getState();
	$('#section').empty().append('<div class="col-md-offset-5"><div class="dataLoader"></div></div>');
}

/**
 * when History state navigation complete
 */
function navigationComplete() {
			
}

/**
 * read state params
 */
function getStateParam(name) {
	name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
	var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"), results = regex.exec(History.getState()['url']);
	return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}


/**
 * binds links with navYuri class to a HistoryState
 */
function comeToYuri() {
	$('.navYuri').click(function(event) {
		event.preventDefault();
		var currentNav = $(this).attr('href');
		History.pushState(null, 'Messaggi', "?yuri=" + currentNav);
	})
}

/**
 * Loading of collapse messages
 */
function contentLoading() {
	$('#content').empty().append('<div class="col-md-offset-5"><div class="dataLoader"></div></div>');
}

/**
 * END Commons
 */

function onActionStart() {
	$('.action-link').hide();
	$('.action-loader').show();
}

function onActionComplete() {
	$('.action-link').show();
	$('.action-loader').hide();
}