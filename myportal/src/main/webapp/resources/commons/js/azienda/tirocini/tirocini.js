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
	'create' : 'sections/_create.xhtml',
	'edit' : 'sections/_create.xhtml',
	'view' : 'sections/_view.xhtml',
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
	comeToYuri();
	
	if (getStateParam('yuri') === 'edit') {
		initEdit([ {
			name : 'tirociniId',
			value : getStateParam('t')
		} ]);
	}
	
	if (getStateParam('yuri') === 'view') {
		initView([ {
			name : 'tirociniId',
			value : getStateParam('t')
		} ]);
	}
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
		History.pushState(null, 'Delega Soggetto Promotore', "?yuri=" + currentNav);
	})
}

/**
 * Loading of collapse content
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

function editTirocini(tirociniId) {
	History.pushState(null, 'Richiesta preselezione tirocinanti in Garanzia Giovani', "?yuri=edit&t=" + tirociniId);
}


function onSyncComplete(xhr, status, args) {
	if(args.tirociniId) {
		History.pushState(null, 'Richiesta preselezione tirocinanti in Garanzia Giovani', "?yuri=view&t=" + args.tirociniId);		
	}
}

function onSendComplete() {
	History.pushState(null, 'Richiesta preselezione tirocinanti in Garanzia Giovani', "?yuri=list");		
	sucessMessage('La Richiesta è inviata');
}