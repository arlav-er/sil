/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(function() {
	// popover init
	pop();
	$("input:password").password();
});

function pop() {
	$("[data-toggle=popover]").popover();
}

function requiredAttentionSeeker() {
	$('.ui-outputlabel-rfi').each(function() {
		inputId = $(this).parent().attr('for')
		$(PrimeFaces.escapeClientId(inputId)).focusout(function() {
			if (!$(this).val()) {
				$(this).parent().parent().find('.ui-outputlabel-rfi').addClass('animated shake')
			}
		})
		$(PrimeFaces.escapeClientId(inputId)).focusin(function() {
			if (!$(this).val()) {
				$(this).parent().parent().find('.ui-outputlabel-rfi').removeClass('animated shake');
			}
		})
	})
}

// javascript function which scroll to the first message in page
function scrollToFirstMessage() {
	try {
		PrimeFaces.scrollTo($('.ui-message :first-child').eq(0).parent().prev().attr('id'));
	} catch (err) {
		// No Message was found!
	}
}

function sucessMessage(message) {
	$.UIkit.notify('<i class="fa fa-check fa-success fa-success"></i> ' + message, {
		status : 'success'
	});
}

function warnMesssage(message) {
	$.UIkit.notify('<i class="fa fa-exclamation-triangle fa-infor"></i> ' + message, {
		status : 'warning'
	});
}

function infoMesssage(message) {
	$.UIkit.notify('<i class="fa fa-info"></i> ' + message, {
		status : 'info'
	});
}

function dangerMesssage(message) {
	$.UIkit.notify('<i class="fa fa-frown-o fa-errorRed"></i> ' + message, {
		status : 'danger'
	});
}

PrimeFaces.locales['it'] = {
	closeText : 'Chiudi',
	prevText : 'Precedente',
	nextText : 'Prossimo',
	monthNames : [ 'Gennaio', 'Febbraio', 'Marzo', 'Aprile', 'Maggio', 'Giugno', 'Luglio', 'Agosto', 'Settembre',
			'Ottobre', 'Novembre', 'Dicembre' ],
	monthNamesShort : [ 'Gen', 'Feb', 'Mar', 'Apr', 'Mag', 'Giu', 'Lug', 'Ago', 'Set', 'Ott', 'Nov', 'Dic' ],
	dayNames : [ 'Domenica', 'Luned�', 'Marted�', 'Mercoled�', 'Gioved�', 'Venerd�', 'Sabato' ],
	dayNamesShort : [ 'Dom', 'Lun', 'Mar', 'Mer', 'Gio', 'Ven', 'Sab' ],
	dayNamesMin : [ 'D', 'L', 'M', 'M ', 'G', 'V ', 'S' ],
	weekHeader : 'Sett',
	firstDay : 1,
	isRTL : false,
	showMonthAfterYear : false,
	yearSuffix : '',
	timeOnlyTitle : 'Solo Tempo',
	timeText : 'Ora',
	hourText : 'Ore',
	minuteText : 'Minuto',
	secondText : 'Secondo',
	currentText : 'Data Odierna',
	ampm : false,
	month : 'Mese',
	week : 'Settimana',
	day : 'Giorno',
	allDayText : 'Tutto il Giorno'
};




/*
 * funzioni per il logout dal MyCas 
 */
function cleanCookies() {       
    var cks = (document.cookie + "").split(";");
    
    var d = new Date(2000, 1)
    for(var a = 0; a < cks.length; a++)
            document.cookie = cks[a].split("=")[0] + "=-1;path=/;expires=" + d.toGMTString() + ";"; 
}

function doLogout() {		
	cleanCookies();
	logout();		
}






