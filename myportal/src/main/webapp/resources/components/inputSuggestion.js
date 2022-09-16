/**
 * Se non è stato definito myportal allora definiscilo
 */
if (!myportal)
	var myportal = {};

/**
 * Se non è stata inizializzato, inizializza il contenitore di dialog. Questo
 * contenitore è un workaround al problema dei partial-update con i dialog di
 * jquery: Contiene tutti i dialog costruiti nella pagina, ogni volta che un
 * inputSuggestion viene renderizzato verifica che non vi siano dei dialog
 * precedenti da distruggere.
 * 
 */
if (!inputSuggestionDialogs) {
	var inputSuggestionDialogs = {};
}

/**
 * Definisce l'oggetto 'inputSuggestion'
 * 
 */
if (!myportal.inputSuggestion) {
	myportal.inputSuggestion = {
		init : function(ccid, requestContextPath, suggestionPath, helpMessage,
				helpPosition, showButton, modaltitle, onSelectF, onlyLeaves,filter, extraParamF, infoUrl) {
			var myDiv = document.getElementById(ccid);
			myDiv.isOpen = false;
			myDiv.itemSelected = false;
			myDiv.effect = document.getElementById(ccid + ":effect");
			myDiv.inputText = document.getElementById(ccid + ':inputText');
			myDiv.inputHidden = document.getElementById(ccid + ':inputHidden');
			myDiv.message = document.getElementById(ccid + ':message');
			myDiv.help = document.getElementById(ccid + ':help');
			myDiv.dialog = document.getElementById(ccid + ':dialog');
			myDiv.info = document.getElementById(ccid + ':info');
			myDiv.tree = document.getElementById(ccid + ':tree');

			// genera l'autocomplete agganciato al componente
			generateAutocomplete(myDiv, requestContextPath + "/secure/rest/"
					+ suggestionPath + "/suggestion", onSelectF,extraParamF);

			// se è stato definito un messaggio, disegna il componente
/*			if (helpMessage != '') {
				attachHelp($(jq(myDiv.help.id)), $(jq(myDiv.inputText.id)),
						helpPosition, 5, 40);
			}*/

			// disegna la modale e l'alberatura se c'è il pulsante a
			// disposizione
			if ('true' == showButton) {
				// cancella eventuali dialog già creati per questo stesso
				// elemento
				// (workaround)
				if (inputSuggestionDialogs[ccid]) {
					inputSuggestionDialogs[ccid].empty().remove();
				}
				inputSuggestionDialogs[ccid] = $(myDiv.dialog);

				// costruisci il dialog senza aprirlo
				$(jq(myDiv.dialog.id)).dialog({
					title : modaltitle,
					height : 300,
					width : 650,
					modal : true,
					autoOpen : false,
					show : "blind",
					buttons : {
						Ok : function() {
							el = $(myDiv.tree);
							selectedNodes = el.dynatree("getSelectedNodes");
							activeNode = el.dynatree("getActiveNode");
							if (activeNode.data.unselectable) {
								activeNode.toggleExpand();
								return false;
							}
							myDiv.inputText.focus();
							myDiv.inputText.value = activeNode.data.title;
							myDiv.inputText.blur();
							myDiv.inputHidden.value = activeNode.data.key;
							$(myDiv.dialog).dialog('close');
							$(myDiv.inputText).change();
						},
						Annulla : function() {
							$(myDiv.dialog).dialog('close');
						},
					}
				});
				
				//Maresta A.: info button part
				//-----------------------------------------------------------
				myDiv.openInfo = function(url) {															
					if(url && myDiv.inputHidden.value) {
						
						var infoUrl = requestContextPath + "/secure/rest/resolve/" + url;
						var map = arguments.callee.map;			
						var key = myDiv.inputHidden.value;

						if(!map) map = arguments.callee.map = {};
						if(map[key]) {
							$(myDiv.info).html(map[key]);
							$(myDiv.info).dialog('open');							
						}
						else 
							$.getJSON(infoUrl, { term: key },
								function(data, textStatus, jqXHR) {
									map[key] = data.descrizione;
									$(myDiv.info).html(data.descrizione);
									$(myDiv.info).dialog('open');
								});
					}
				};
				
				$(myDiv.info).dialog({
					title : "Informazione",
					modal : true,
					autoOpen : false,
					show : "blind",
					buttons : {
						Ok : function() {
							$(myDiv.info).dialog('close');
						}
					}
				});
				//-----------------------------------------------------------
				
				// crea l'albero
				$(jq(myDiv.tree.id)).dynatree(
						{
							title : "",
							persist : false,
							initAjax : {
								url : requestContextPath + "/secure/rest/"
										+ suggestionPath + "/albero?key=0" + "&filter=" + filter
							},
							ajaxDefaults : {
								cache : false,
								dataType : "json"
							},
							onLazyRead : function(node) {
								node.appendAjax({
									url : requestContextPath + "/secure/rest/"
											+ suggestionPath + "/albero?key="
											+ node.data.key + "&filter=" + filter
								});
							},
							onDblClick : function(node) { // quando seleziona
								// un nodo, segna i
								// valori.
								if (node.data.unselectable) {
									node.toggleExpand();
									return false;
								}
								myDiv.inputText.focus();
								myDiv.inputText.value = node.data.title;
								myDiv.inputText.blur();
								myDiv.inputHidden.value = node.data.key;
								$(myDiv.dialog).dialog('close');
								$(myDiv.inputText).change();
								/*
								 * Callback della funzione al momento della
								 * selezione di un elemento
								 */
								if (onSelectF) {
									window[onSelectF]
											(node.data.descrizioneTipoTitolo);
								}
							},
							
							onKeypress : function(node, e) { // quando
																// seleziona
								// un nodo, segna i
								// valori.
								if (e.keyCode == 13) {
									
									if (node.data.unselectable) {
										node.toggleExpand();
										$('.dynatree-active a').focus();
										return false;
									}
								
									myDiv.inputText.value = node.data.title;
									myDiv.inputHidden.value = node.data.key;
									$(myDiv.dialog).dialog('close');
									$(jq(ccid + ':cerca')).focus();
								}
							},
							classNames : {
								container : "dynatree-container",
							}
						});

				
			}

			/*
			 * Se il valore presente nell'inputtext e' vuoto allora cancella il
			 * codice presente nell'input hidden e la descrizione del tipo
			 * titolo
			 */
			function checkValue() {
				var label = $(jq(ccid + ':inputText')).prop('value');
				if (label == '') {
					$(jq(ccid + ':inputHidden')).attr('value', '');
					/*
					 * Callback della funzione al momento della selezione di un
					 * elemento
					 */
					if (onSelectF) {
						window[onSelectF]('');
					}
				}
			}

			$(jq(ccid + ':inputText')).change(function() {
				checkValue();
			});

			$(jq(ccid + ':inputText')).blur(function() {
				checkValue();
			});
			
			/*
			 *lasciare lo spazione non mettere null! altrimenti il valore non viene aggiornato al fallimento della validazione!!
			 */
			$(myDiv.inputText).keypress(function(e) {
				var backspaceKey = 8;
				var canckey = 46;
				if(e.charCode || e.keyCode ==  backspaceKey || e.keyCode == canckey)
					myDiv.inputHidden.value = " ";
				
			});
			
		},
	};	
};