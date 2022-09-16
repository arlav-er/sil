!window.console && (console = { log: function() {}});


//crea una dataTable sugli elementi scelti a partire dalla query passata come parametro
//ogni tabella viene esaminata singolarmente x la proprietà notSortable
function generateDataTable(table_jq, options) {
	defaults={
		"bJQueryUI" : true,
		"bFilter" : false,
		"bPaginate" : false,
		"bInfo" : false,
		"bLengthChange" : false,
		"bRetrieve":true,
		"oLanguage": {
			"sLengthMenu": "Mostra _MENU_ righe per schermata",
			"sZeroRecords": "Nessun elemento da mostrare",
			"sInfo": "Pagina _START_ di _END_ sul totale di _TOTAL_",
			"sInfoEmpty": "Mostro 0 to 0 su 0 record",
			"sInfoFiltered": "(filtrati da _MAX_ record totali)",
			"sSearch": "Filtro veloce:",
			"oPaginate" : {
				"sNext" : "Prossimi",
				"sPrevious": "Precedenti",
			},
			"oClasses" : {
				"sPagePrevEnabled" : "fg-button ui-state-default ui-corner-left yellowButton",
				"sPagePrevDisabled" : "fg-button ui-state-default ui-corner-left ui-state-disabled yellowButton",				
			}
		},
//		"fnDrawCallback": disegnaBottoni,
		"fnInfoCallback": function( oSettings, iStart, iEnd, iMax, iTotal, sPre ) {
    	return "Pagina " + (Math.floor(iStart / oSettings._iDisplayLength) + 1) + " di "+ (Math.ceil(iTotal / oSettings._iDisplayLength));
  	}		
	};

	tableArr=$(table_jq);
//	tableObj=$(table_id);
	tableArr.each(function(index, table){
		tableObj=$(table);
		tFirstRow=tableObj.children('tbody');
		tFirstRow=tFirstRow.children('tr:first');
		tFirstRow=tFirstRow.children('td');
		noSortalbeIdx=new Array();
		tFirstRow.each(
				function(index) {
					//alert('tbody tr:first td:'+index + ': ' + $(this).text().trim()+', notSortable:'+$(this).hasClass( 'notSortable' )+'\n');
					if($(this).hasClass( 'notSortable' )){
						noSortalbeIdx.push(index);
					}
				});
		settings = $.extend(true,{}, defaults, options);
		//alert("before sortable settings:"+JSON.stringify(settings));
		//aggiungo l'impostazione per colonne non sortabili
		if(!jQuery.isEmptyObject(noSortalbeIdx)){
			curr_aColDef=settings.aoColumnDefs;
			bSortableObj={ "bSortable": false, "aTargets": noSortalbeIdx };
			if(curr_aColDef){
				curr_aColDef.push(bSortableObj);
			} else {
				settings.aoColumnDefs = [bSortableObj];
			}
		}
		//alert("settings:"+JSON.stringify(settings));
		tableObj.dataTable(settings);

		//rimuove esplicitamente header e footer
		if (settings["bJQueryUI"] && !settings["bFilter"]) {
			tableObj.prev().remove();
			tableObj.next().remove();
		}
	});

}


/**
 * @param data
 * @param inputObj
 * @param options
 *
 * Author Girotti S.
Da includere in utils.js: --utilizzo interno--
gestione delle suggestion in caso di dati vuoti;
modifica lo style dell'input relativo
ed inserisce una voce 'Nessun dato trovato'

i defaults possono essere sovrascritti (anche solo parzialmente)
popolando l'oggetto  options
 */
function dataOrEmpty(data,inputObj,options){
	defaults={
				id: '',
				label: 'Nessun dato trovato',
				value: '',
				cssDataEmpty:function (inputObj){
					inputObj.css({"background-color":"pink"});
				},
				cssDataFull:function (inputObj){
					inputObj.css({"background-color": ""});
				}
			};
	settings = $.extend({}, defaults, options);
	settings.cssDataFull(inputObj);
	if(	data.length ==1 && data[0].id=='' ) {
		settings.cssDataEmpty(inputObj);
		return data;
	}
	return data;
	}

/**
 * @param url
 * @param inputObj
 * @param options
 * @returns {Function}
 * Author Girotti S.
Da includere in utils.js:
gestione delle sorgenti di suggestion con cache;

richiede:
l'url sorgente dei dati,
l'oggetto input in cui monitorare il parametro di ricerca,
	Opzionale
	modifica dei parametri di default della gestione di risultati vuoti
 */
var cache = {},lastXhr;
function autoCompleteSource(url,inputObj,options,extraParamF){
	return function( request, response ) {
		var cacheUrl = url+request.term;
		var callUrl = url; 
		if (extraParamF) {
			var extraParams = window[extraParamF]('');
			cacheUrl += extraParams;
			for (var i=0;i<extraParams.length;i++) {
				if (i == 0) {
					callUrl += "?";
				} else {
					callUrl += "&";
				}
				callUrl += extraParams[i][0] + "=" + extraParams[i][1];
			}
		}
		if ( cacheUrl in cache ) {
			response(dataOrEmpty(cache[ cacheUrl ],inputObj,options));
			return;
			}
		lastXhr = $.getJSON( callUrl, request, function( data, status, xhr ) {
			cache[ cacheUrl ] = data;
			if ( xhr === lastXhr ) {
				response(dataOrEmpty(cache[ cacheUrl ],inputObj,options));
			}
		});
	};//function( request, response )

}//function autoCompleteSource(request, response,url,inputObj,hInputObj)



/**
 * @param hidden_component_cod
 * @returns {Function}
 * <br>
 * in caso di + autoComplete sulla stessa pagina ci consente di distinguere il componente su cui inserire l'id<br>
 *
 */
function autoCompleteSelect(hidden_component_cod,component_str){

}


function autoCompleteClose(component_msg) {
	$(component_msg).empty();
}

/**
 * @param component_id
 * @param suggestion_url
 * <br>
 * genera un autocomplete su un elemento con id:component_id, utilizzando la url:suggestion_url<br>
 *
 */
function generateAutocomplete(myDiv, suggestion_url, onSelectF, extraParamF) {
	component_id = myDiv.id;
	$(myDiv.inputText).autocomplete({
		source:  autoCompleteSource(suggestion_url,$(myDiv.inputText),{}, extraParamF),
		minLength: 2,
		mustMatch: false,
		select: function(event,ui) {
			myDiv.inputHidden.value = ui.item.id;
			myDiv.inputText.value = ui.item.value;
			myDiv.itemSelected = true;
			myDiv.isOpen = true;

			$(myDiv.inputText).change();
			
			/*
			 * Callback della funzione al momento della selezione di un elemento
			 */
			if (onSelectF) {
				window[onSelectF](ui.item.descrizioneTipoTitolo);
			}
		},
		close: function() {
			autoCompleteClose(myDiv.message);
		},
		open: function() {

		},
	});
}

function generateModalDialog(query) {
	$(query).dialog( "destroy" );

	$(query).dialog({ autoOpen: false,
					modal: true,
					resizable: false,
					show: "blind",
					hide: "explode" });
}

//Richiama l'effetto fadeIn su tutti i tag che contengoni l'id passato come parametro
function runEffectError(id) {
	$(id).fadeIn(800);
};


function changeCssCollapseDiv(component_id, componentBrake_id) {
    if ($( component_id).hasClass('ui-state-default')) {
  		$( component_id ).removeClass("ui-state-default");
		$( component_id ).addClass("ui-state-active");
		$( component_id ).removeClass("ui-corner-all");
		$( component_id).addClass("ui-corner-top");

		$( componentBrake_id ).removeClass("ui-icon ui-icon-triangle-1-e");
		$( componentBrake_id ).addClass("ui-icon ui-icon-triangle-1-s");
	}
	else if ($( component_id ).hasClass('ui-state-active')) {
		$( component_id ).removeClass("ui-state-active");
		$( component_id ).addClass("ui-state-default");
		$( component_id ).removeClass("ui-corner-top");
		$( component_id ).addClass("ui-corner-all");

		$( componentBrake_id ).removeClass("ui-icon ui-icon-triangle-1-s");
		$( componentBrake_id ).addClass("ui-icon ui-icon-triangle-1-e");
	}

	return false;
}

function changeAttrCollapseDiv(component_id) {
	if ($( component_id).attr('aria-selected')=='true') {

  		$( component_id ).attr('aria-selected','false');
	}
	else {

		$( component_id ).attr('aria-selected','true');
	}



	return false;
}

/* Disegna correttamente i bottoni rendendoli belli belli belli in modo assurdo con JQueryUI
 * http://upload.wikimedia.org/wikipedia/it/0/0b/Zoolander.png
 * Inoltre fa si che i pulsanti con classe 'disableButton' siano disattivabili
 *
 *

/*
 * FIXME Da perfezionare
 */
function editCallback(data, form_id) {
	if (data.status == 'success') {
		$( '[id$=' || form_id || ']' ).show();
	}
}

function jq(id) {
	return "#" + id.replace(/:/g,"\\:");
}

//grazie germania!!

//devo mostrare il messaggio di conferma?
var confirmOk = false;
//riga correntemente selezionata
var currentDeleteElem = null;

function removeRow(elem,form_id) {
  if (!confirmOk) {
	      currentDeleteElem = elem;
	      $(jq(form_id+'_dialog-delete')).dialog('open');
  }
  return confirmOk;
};

/**
 *Visualizza un div di aiuto per un dato campo di input
 *Prende in input 4 parametri:
 *	- il div di aiuto da mostrare
 *	- l'elemento di input cui si riferisce
 *	- offset verticale dall'angolo in alto a destra del componente di input
 *	- offset orizzontale dall'angolo in alto a destra del componente di input
 */
function attachHelp(helpElement, otherElement, position, yOffset, xOffset) {
	otherElement.focus(function() {
		var otherPosition = otherElement.position();
		var otherWidth = otherElement.width();
		var otherHeight = otherElement.height();
		var helpWidth = helpElement.width();
		var helpHeight = helpElement.height();
		var imgYPosition = 0;
		var imgXPosition = 0;
		var helpYPosition = 0;
		var helpXPosition = 0;

		if (position == 'top') {
			helpElement.addClass('field-help-top');
			helpYPosition = otherPosition.top - helpHeight - yOffset;
			helpXPosition = otherPosition.left + xOffset;
			imgYPosition = helpHeight - 16 - 5;
			imgXPosition = 5;
		} else if (position == 'bottom') {
			helpElement.addClass('field-help-bottom');
			helpYPosition = otherPosition.top + otherHeight + yOffset;
			helpXPosition = otherPosition.left + xOffset;
			imgYPosition = 5;
			imgXPosition = 5;
		} else if (position == 'right') {
			helpElement.addClass('field-help-right');
			helpYPosition = otherPosition.top + yOffset;
			helpXPosition = otherPosition.left + otherWidth + xOffset;
			imgYPosition = 5;
			imgXPosition = 5;
		} else if (position == 'left') {
			helpElement.addClass('field-help-left');
			helpYPosition = otherPosition.top + yOffset;
			helpXPosition = otherPosition.left - helpWidth - xOffset;
			imgYPosition = 5;
			imgXPosition = helpWidth - 5;
		}

		helpElement.css('top', helpYPosition);
		helpElement.css('left', helpXPosition);
		helpElement.css('background-position', imgXPosition + ' ' + imgYPosition);

		helpElement.corner("10px");

		helpElement.css('display', '');
	});
	otherElement.blur(function() {
		helpElement.css('display', 'none');
	});
};

/**
 * Crea un textEditor utilizzanto CLEditor http://premiumsoftware.net/cleditor/
 * @param l'elemento textarea a cui applicare il css e il js
 * @param opzioni, opzionale. Guardare la documentazione di CLEditor per informazioni
 */
function textEditor(options) {
	var editorOpt = {
			controls: "bold italic | bullets numbering | outdent indent | alignleft center alignright justify | undo redo",
			width: options.width,
			elem: options.element
	};
	//if (arguments.length == 2) {
	//	editorOpt = arguments[1];
	//}

	return options.element.cleditor(editorOpt);
};

/**
 * Carica un'immagine ingrandita in una dialog
 * @param uri, path dell'immagine da caricare
 * @param id_dialog, id del <div> dialog
 * @param id_image, id dell'immagine
 */
function previewImage(uri, id_dialog, id_image) {

	//Get the HTML Elements
	imageDialog = $('#'+id_dialog);
	imageTag = $('#'+id_image);

	//Set the image src
	imageTag.attr('src', uri);

	//When the image has loaded, display the dialog
	imageTag.load(function(){
		imageDialog.dialog({
			buttons : true,
		    modal: true,
		    resizable: false,
		    draggable: false,
		    escClose: true,
		    width: 'auto',
		    title: 'Anteprima immagine'
		  });
	  });

}

function insertImgElement(query) {
	$(query).html("<img style='display:none' alt='none'></img>");
}

/**
 * Fa partire una richiesta ajax al server per richiedere il numero di
 * messaggi ancora da leggere. Si occupa anche di aggiornare l'icona nella testata della pagina.
 * @param user_id
 * @returns
 */
function pollNotifications(user_id) {
	/*$.ajax({		
<<<<<<< HEAD
		  url: "/" + contextName + "/secure/rest/polling/notifications?user="+user_id,
=======
		  url: "/" + contextName + "/faces/secure/rest/polling/notifications?user="+user_id,
>>>>>>> refs/heads/1.9.4_bootstrap_messaging
		  dataType: "json",
		  cache : false,
		  success: function(data){
			if (data.status == 'new') {	//ci sono nuovi messaggi da leggere
		    	//$('[id$=messages_icon]').attr('src','/MyPortal/resources/images/icons/email-active.png');
		    	$('.messages_area b').text(" ("+data.count+")");
			}
			else if (data.status == 'none') {
				//$('[id$=messages_icon]').attr('src','/MyPortal/resources/images/icons/email.png');
				//$('#messages_area').append('');
			}
			else if (data.status == 'error') {
				//$('[id$=messages_icon]').attr('src','/MyPortal/resources/images/icons/email.png');
				$('.messages_area b').text("(?)");
			}
		  },
		  error: function(e, xhr) {
			  //$(jq('[id$=messages_icon]')).attr('src','/MyPortal/resources/images/icons/email.png');
			  $('.messages_area b').text("(?)");			
		});*/
}



/**
 * @param tabSelector
 */
function selectMasterTab(tabSelector) {
	$(tabSelector).addClass('selected');
}

//Funzione di controllo numero massimo di Curriculum Vitae attraverso una variabile definita nel bean Constants 
function checkMaxCurr(maxCurr, numCurr) {
    if(numCurr >= maxCurr){
        $.jnotify(("Non è possibile inserire più di "+maxCurr+" Curriculum Vitae"), "warn");
        return false;
    }
    return true;
}




/*
 * Funzione di callback per i pulsanti che mostrano un pannello. Il pulsante è
 * quello dell'inserimento di nuovi elementi in un master-detail. Si occupa
 * anche del focus e dello scrolling. Il pannello deve avere id 'insert-form'
 */
function showPanel(panel_id) {
	var panel = $('[id$='+panel_id+']');
	panel.show();
	var firstField = panel.find(":input:visible:enabled:first"); 
	firstField.focus();
	if (firstField && firstField.offset()) {
		$('html, body').animate({scrollTop : firstField.offset().top}, 500);
	}
	else if(panel.offset()) {
		$('html, body').animate({scrollTop : panel.offset().top}, 500);
	}
}



function changeTdWithTh() {

	$('td.visualizza_left').each(function() {
		var th = $('<th>');
		var sn = $('<div>');
		sn.css("text-align", "left");
		th.append(sn);
		sn.text($(this).text()).css('width','200px');
		$(this).replaceWith(th);
	});
}

/**
 * Rilegge il css della pagina. Da usare quando si cambia il tema del portale
 * 
 * @param data
 */
function refreshCss(data) {
	if (data.status == 'success') {
		//alert("refreshCss");
		location.reload();
	}
}


//
function fetchStampaSare(idAzienda) {	
	
	myHandler = function() {
		var content = document.getElementById('ifrm').contentDocument.firstChild.childNodes[1].innerHTML;
		 
		  $("#waitStampa").hide();
		  $("#answer .ok").hide();
		  if (content.indexOf('HTTP Status 500 - ')!=-1) {
			  
			  $("#answer .errorStampa").fadeIn();
			  
		  }
		};
	
	$('.errorStampa').hide();
	$("#waitStampa").fadeIn();
	$('#ifrm').remove();
	var el = document.createElement("iframe");
	el.setAttribute('id', 'ifrm');
    el.setAttribute('style',"visibility:hidden");
	el.setAttribute('src', "/" + contextName + '/secure/rest/services/getAccreditamentoAzienda?aziendaInfoId='+idAzienda);

	 if ( $.browser.msie && $.browser.version < 9) {
		 //alert("8");
		 el.onreadystatechange = myHandler;		    
	 } 
	 else if ( $.browser.msie && $.browser.version >= 9) {
		 //alert("9");
		 el.onreadystatechange = myHandler;		    
	 }
	 else {
		 el.onload = myHandler; 
	 }
	document.body.appendChild(el);
		
}

function fetchStampa(source) {		
	myHandler = function() {		
		var content = document.getElementById('ifrm').contentDocument.firstChild.childNodes[1].innerHTML;
		 
		  $("#waitStampa").hide();
		  $("#answer .ok").hide();
		  if (content.indexOf('HTTP Status 500 - ')!=-1) {
			  
			  $("#answer .errorStampa").fadeIn();
			  
		  }
		};
	
	$('.errorStampa').hide();
	$("#waitStampa").fadeIn();
	$('#ifrm').remove();
	var el = document.createElement("iframe");
	el.setAttribute('id', 'ifrm');
    el.setAttribute('style',"visibility:hidden");
	el.setAttribute('src', source);
	 if ( $.browser.msie && $.browser.version < 9) {
		 //alert("8");
		 el.onreadystatechange = myHandler;		    
	 } 
	 else if ( $.browser.msie && $.browser.version >= 9) {
		 //alert("9");
		 el.onreadystatechange = myHandler;		    
	 }
	 else {
		 el.onload = myHandler; 
	 }
	document.body.appendChild(el);		
}

PrimeFaces.locales['it'] = {
		closeText : 'Chiudi',
		prevText : 'Precedente',
		nextText : 'Prossimo',
		monthNames : [ 'Gennaio', 'Febbraio', 'Marzo', 'Aprile', 'Maggio',
				'Giugno', 'Luglio', 'Agosto', 'Settembre', 'Ottobre', 'Novembre',
				'Dicembre' ],
		monthNamesShort : [ 'Gen', 'Feb', 'Mar', 'Apr', 'Mag', 'Giu', 'Lug', 'Ago',
				'Set', 'Ott', 'Nov', 'Dic' ],
		dayNames : [ 'Domenica', 'Lunedì', 'Martedì', 'Mercoledì', 'Giovedì',
				'Venerdì', 'Sabato' ],
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
