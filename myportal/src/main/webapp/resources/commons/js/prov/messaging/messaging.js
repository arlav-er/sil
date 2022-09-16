/**
 * Commons
 * 
 * Yuri: http://cnc.wikia.com/wiki/Yuri_Prime_%28Yuri%27s_Revenge%29
 * 
 * I am my own master, always!
 * 
 * @author: hatemalimam
 * @date: 25/11/2014
 */

nav = {
	'notificationsReceived' : 'sections/notifications/_notificationsReceived.xhtml',
	'notificationsSent' : 'sections/notifications/_notificationsSent.xhtml',
	'sendNotification' : 'sections/notifications/_sendNotification.xhtml',

	'support' : 'sections/support/_support.xhtml',
	'supportMessage' : 'sections/support/_supportMessage.xhtml',

	'consulting' : 'sections/consulting/_consulting.xhtml',
	'consultingMessage' : 'sections/consulting/_consultingMessage.xhtml',

	'contactReceived' : 'sections/contact/_contactReceived.xhtml',
	'contactSent' : 'sections/contact/_contactSent.xhtml',
	'contactReceivedMessage' : 'sections/contact/_contactReceivedMessage.xhtml',
	'contactSentMessage' : 'sections/contact/_contactSentMessage.xhtml'
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
	if ($('.navYuri[href$="' + getStateParam('yuri') + '"]').length) {
		$('.navYuri').removeClass('active');
		$('.navYuri[href$="' + getStateParam('yuri') + '"]').addClass('active');
	}
}

/**
 * when History state navigation complete
 */
function navigationComplete() {
	comeToYuri();

	if (getStateParam('yuri') === 'supportMessage' || getStateParam('yuri') === 'consultingMessage'
			|| getStateParam('yuri') === 'contactReceivedMessage' || getStateParam('yuri') === 'contactSentMessage') {
		showMessageWithTicketId([ {
			name : 'ticketId',
			value : getStateParam('t')
		} ]);
	}

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
 * read state params
 */
function getStateParam(name) {
	name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
	var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"), results = regex.exec(History.getState()['url']);
	return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

/**
 * Loading of collapse messages
 */
function messagesLoading() {
	$('#content').empty().append('<div class="col-md-offset-5"><div class="dataLoader"></div></div>');
}

/**
 * END Commons
 */

/** _sendNotification */
function sendNotificationComplete(xhr, status, args) {
	if (!args.validationFailed) {
		$('#sendNotificationForm').addClass('animated fadeOut');
		History.pushState(null, 'Messaggi', "?yuri=notificationsSent");
		sucessMessage('yes');
	}
}

/** _end _sendNotification */

/**
 * _supportMessage
 * 
 */

function showMessageComplete(xhr, status, args) {
	$('.message-date').timeago();
	if (args.notFound) {
		$('#ticket').empty();
		$('.notfound').removeClass('hidden').fadeIn();
	}

	if (args.enableReply) {
		$('#formReply').removeClass('hidden');
	} else {
		$('#formReply').addClass('hidden');
	}
}

function ticketLoading() {
	$('#ticket').empty().append('<div class="col-md-offset-5"><div class="dataLoader"></div></div>');
}

function fetchMessages() {
	showMessageWithTicketId([ {
		name : 'ticketId',
		value : getStateParam('t')
	} ]);
}

/**
 * END _supportMessage
 * 
 */

/**
 * _support
 * 
 */
function showMessageFromMessagesSupport(messageTicketId) {
	History.pushState(null, 'Messaggi', "?yuri=supportMessage&t=" + messageTicketId);
}

/**
 * END _support
 * 
 */

/**
 * _consulting
 * 
 */
function showMessageFromMessagesConsulting(messageTicketId) {
	History.pushState(null, 'Messaggi', "?yuri=consultingMessage&t=" + messageTicketId);
}

/**
 * END _consulting
 * 
 */

/**
 * _contactReceived
 * 
 */
function showMessageFromMessagesContactReceived(messageTicketId) {
	History.pushState(null, 'Messaggi', "?yuri=contactReceivedMessage&t=" + messageTicketId);
}

/**
 * END _contactReceived
 * 
 */

/**
 * _contactSent
 * 
 */
function showMessageFromMessagesContactSent(messageTicketId) {
	History.pushState(null, 'Messaggi', "?yuri=contactSentMessage&t=" + messageTicketId);
}

/**
 * END _contactSent
 * 
 */
