/*Javascript incluso in tutte le pagina.
 *Racchiude tutto ciò che deve essere eseguito al caricamento di ogni pagina*/



jQuery.fn.dataTableExt.oSort['uk_date-asc'] = function(a, b) {
	var elA = $(a);
	var elB = $(b);
	var valA = a;
	var valB = b;
	if (elA.is("span")) {
		valA = elA.html();
	}
	if (elB.is("span")) {
		valB = elB.html();
	}
	var ukDatea = valA.split('/');
	var ukDateb = valB.split('/');

	var x = (ukDatea[2] + ukDatea[1] + ukDatea[0]) * 1;
	var y = (ukDateb[2] + ukDateb[1] + ukDateb[0]) * 1;

	return ((x < y) ? -1 : ((x > y) ? 1 : 0));
};

jQuery.fn.dataTableExt.oSort['uk_date-desc'] = function(a, b) {
	var elA = $(a);
	var elB = $(b);
	var valA = a;
	var valB = b;
	if (elA.is("span")) {
		valA = elA.html();
	}
	if (elB.is("span")) {
		valB = elB.html();
	}
	var ukDatea = valA.split('/');
	var ukDateb = valB.split('/');

	var x = (ukDatea[2] + ukDatea[1] + ukDatea[0]) * 1;
	var y = (ukDateb[2] + ukDateb[1] + ukDateb[0]) * 1;

	return ((x < y) ? 1 : ((x > y) ? -1 : 0));
};

//Maresta[
jQuery.fn.dataTableExt.oSort['data-ora-desc'] = function(a, b) {	
	var elA = $(a)
	var elB = $(b)

	var valA = a
	var valB = b
	
	if (elA.is("span")) valA = elA.html()	
	if (elB.is("span")) valB = elB.html()
	
	valA = valA.split("/")
	valB = valB.split("/")
	
	var d1 = valA[0]
	var m1 = valA[1]
	var y1 = valA[2].split(" ")[0]
	var h1 = valA[2].split(" ")[1]

	var d2 = valB[0]
	var m2 = valB[1]
	var y2 = valB[2].split(" ")[0]
	var h2 = valB[2].split(" ")[1]
	
	return new Date(y1 + "/" + m1 + "/" + d1 + " " + h1) > new Date(y2 + "/" + m2 + "/" + d2 + " " + h2) ? -1 : 1
}
jQuery.fn.dataTableExt.oSort['data-ora-asc'] = function(a, b) {	
	return ~jQuery.fn.dataTableExt.oSort['data-ora-desc'](a, b) + 1	
}//]

jQuery.fn.dataTableExt.oSort['date-euro-asc'] = function(a, b) {
    if (trim(a) != '') {
        var frDatea = trim(a).split(' ');
        var frTimea = frDatea[1].split(':');
        var frDatea2 = frDatea[0].split('/');
        var x = (frDatea2[2] + frDatea2[1] + frDatea2[0] + frTimea[0] + frTimea[1] + frTimea[2]) * 1;
    } else {
        var x = 10000000000000; // = l'an 1000 ...
    }
 
    if (trim(b) != '') {
        var frDateb = trim(b).split(' ');
        var frTimeb = frDateb[1].split(':');
        frDateb = frDateb[0].split('/');
        var y = (frDateb[2] + frDateb[1] + frDateb[0] + frTimeb[0] + frTimeb[1] + frTimeb[2]) * 1;                     
    } else {
        var y = 10000000000000;                    
    }
    var z = ((x < y) ? -1 : ((x > y) ? 1 : 0));
    return z;
};
 
jQuery.fn.dataTableExt.oSort['date-euro-desc'] = function(a, b) {
    if (trim(a) != '') {
        var frDatea = trim(a).split(' ');
        var frTimea = frDatea[1].split(':');
        var frDatea2 = frDatea[0].split('/');
        var x = (frDatea2[2] + frDatea2[1] + frDatea2[0] + frTimea[0] + frTimea[1] + frTimea[2]) * 1;                      
    } else {
        var x = 10000000000000;                    
    }
 
    if (trim(b) != '') {
        var frDateb = trim(b).split(' ');
        var frTimeb = frDateb[1].split(':');
        frDateb = frDateb[0].split('/');
        var y = (frDateb[2] + frDateb[1] + frDateb[0] + frTimeb[0] + frTimeb[1] + frTimeb[2]) * 1;                     
    } else {
        var y = 10000000000000;                    
    }                  
    var z = ((x < y) ? 1 : ((x > y) ? -1 : 0));                  
    return z;
};




$(function() {
	$('#javax_faces_developmentstage_messages').remove();

	disegnaBottoni();

	/**
	 * Fa partire il contatore del sessionTimeOut ad ogni ingresso pagina
	 */
	SessionTimeOut.startTimeOut();

	/**
	 * Resetta il contatore del sessionTimeOut ad ogni chiamata ajax. ridisegna
	 * anche i bottoni nel caso ve ne fosse bisogno
	 */
	$(document).ajaxSuccess(function(evt, request, settings) {
		SessionTimeOut.startTimeOut();
	});

	/**
	 * Resetta il contatore del sessionTimeOut ad ogni chiamata jsf attraverso
	 * l'override della funzione mojarra.ab
	 */
	mojarra.ab = function ab(c, d, g, a, b, f) {
		SessionTimeOut.startTimeOut();
		if (!f) {
			f = {};
		}
		if (g) {
			f["javax.faces.behavior.event"] = g;
		}
		if (a) {
			f.execute = a;
		}
		if (b) {
			f.render = b;
		}
		jsf.ajax.request(c, d, f);
	};

	if ($.browser.msie) {
		// console.log('$.browser.msie:'+$.browser.msie);
		$('#sectionsWrapper').css('min-height', '32px');
	} else if ($.browser.webkit) {
		// console.log('$.browser.webkit:'+$.browser.webkit);
		$('#sectionsWrapper').css('min-height', '27px');
	}
	
	
	

});