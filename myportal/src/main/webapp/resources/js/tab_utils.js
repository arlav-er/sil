var switchingTab = false;
var tabIndex = null;
var previousTab = 0;
var checkBeforeSwitch = true; // determina se devo eseguire il controllo
var sbut = null;
var valueChanged = false; // determina se sono avvenute delle modifiche ai
							// campi di input
$(function() {

	// Tabs
	$(function() {
		jQuery('#tabs')
				.tabs(
						{
							show : function(event,ui) {
						      	$(ui.panel).html('<img src="/' + contextName + '/resources/images/ajax-loader-squares.gif" alt="Caricamento dati..."/><br/><b>Caricamento dati...</b>');																
							},
							select : function(event, ui) { // funzione eseguita
															// quando si
															// seleziona una tab								
								sbut = null;
								tabIndex = null;
								if (checkBeforeSwitch) { // se bisogna
															// eseguire il check
															// dei dati
									var selected = $('#tabs').tabs('option',
											'selected');
									var selectedtab = '#tabs > div:eq('
											+ selected + ')';
									previousTab = selected;
									sbut = $(selectedtab).find("[id*=\\:currentButton]"); // il
																				// pulsante
																				// da
																				// premere
																				// è il
																				// currentButton
									if (sbut.length == 0)
										sbut = $(selectedtab).find("[id*=\\:updateButton]"); // il
																					// pulsante
																					// da
																					// premere
																					// è il
																					// updateButton

									if (sbut && valueChanged) {
										valueChanged = false;

										if (!jQuery('#dialog-confirm').dialog(
												'isOpen')) {
											$('#dialog-confirm').data('tabId',
													ui.index).data('ui', ui)
													.dialog('open');
											return false;
										}
									}
									return true;
								} else {
									checkBeforeSwitch = true;
									valueChanged = false;
									return true;
								}
							},
							ajaxOptions : {
								error : function(xhr, status, index, anchor) {
									$(anchor.hash).html(
											"Impossibile caricare il contenuto della tab.");
								},
								data : {},
								success : function(data, textStatus) {			
								},
							}
						});

		jQuery('#dialog-confirm').dialog({
			autoOpen : false,
			resizable : false,
			modal : true,
			buttons : [ {
				text : 'Si. Salva prima le modifiche.',
				click : function() {
					// a new tab must be selected before
					// the confirmation dialog is closed
					$('[id$=tabClick]').attr('value', 'true');
					sbut[0].click();
					
					switchingTab = true;
					var ui = $(this).data('ui');
					tabIndex = ui.index;
					$(this).dialog('close');
					return false;

				}
			}, {
				text : 'No.Procedi senza salvare le modifiche',
				click : function() {
					var tabId = $(this).data('tabId');
					$('#tabs').tabs('select', tabId);
					$(this).dialog('close');
				}/*,
				class : 'secondButton'   --> Non funziona con I.E.*/
			} ]
		});
	});

});

function checkSwitch(p) {
	valueChanged = false;
	$('[id$=tabClick]').attr('value', '');

	// alert('checkSwitch: ' + p);
	if (p == 'true') {
		checkBeforeSwitch = false;
		if (tabIndex == null) {
			var selected = $('#tabs').tabs('option', 'selected');
			$('#tabs').tabs("select", selected + 1);
		} else {
			$('#tabs').tabs("select", tabIndex);
		}
	}
}

// elementi ai quali è collegato, attualmente, un listener
var currentListen;

function setValueChanged() {
	valueChanged = true;
	currentListen.unbind('change');
	// elimina il listener.
	// la variabile e' stata settata quindi non e' piu' necessario rimanere in
	// ascolto sull'evento
	// appesantendo il browser...appesantendo...

}

function attachListener() {

	// tramite questo script aggancio ad ogni sorgente di input nella pagina un
	// listener per il cambiamento dei dati.
	// se avvengono dei cambiamenti la variabile 'valueChanged' viene settata a
	// true ed il listenr rimosso.
	// la variabile viene utilizzata successivamente per veririficare se sia il
	// caso di mostrare all'utente un messaggio
	// per il salvataggio dei dati.
	var selected = $('#tabs').tabs('option', 'selected');
	var selectedtab = '#tabs > div:eq(' + selected + ')';

	// setter effetto FADE, però si sputtanano le width delle colonne. Chissà 
	// perchè? by Pegoraro
	// $( '#tabs' ).tabs( "option", "fx", { opacity: 'toggle' } );

	// alert(selectedtab);
	currentListen = $(selectedtab).find(' :input');
	currentListen.change(function() {
		setValueChanged();
	});
}

/*
 * Funzione di callback per i pulsanti che mostrano un pannello. Il pulsante è
 * quello dell'inserimento di nuovi elementi in un master-detail. Si occupa
 * anche del focus e dello scrolling. Il pannello deve avere id 'insert-form'
 */
function showPanelCallback(data) {
	if (data.status == 'success') {
		$("[id$=insert_form]").show();
		$("[id$=insert_form] :input:visible:enabled:first").focus();
		if ($("[id$=insert_form] :input:visible:enabled:first").offset()) {
			$('html, body').animate({scrollTop : $("[id$=insert_form] :input:visible:enabled:first").offset().top}, 500);
		}
	}
}

function refreshNumVacancyCompatibili(data) {
	btn=$(jq('cappello:hidden_btn'));
	btn.click();
}

function showPanelCallbackAndRefresh(data) {
	showPanelCallback(data);
	refreshNumVacancyCompatibili(data);
}


