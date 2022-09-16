/**
 * Fa partire una richiesta ajax al server per calcolare lo stato occupazionale
 * del lavoratore
 * 
 * @param user_id
 * @returns
 */
function fetchStatoOccupazionale(user_id) {
	// esegue la chiamata
	$.ajax({
		url : "/" + contextName + "/secure/rest/services/stato_occupazionale?user_id=" + user_id,
		dataType : "json",
		cache : false,
		success : function(data) {
			// nascondi il waiting loader
			$("#answer .wait").hide();
			if (data.status == 'error') {
				// in caso di errore mostralo
				$("#answer .error").append("<b>" + data.description + "</b>").fadeIn();

			} else {
				// inserisci tutti i dati enlla tabella
				var table = $("#answer table")
				var statoOcc = data.datiStatoOccupazionale;
				$(".codiceFiscale", table).text(statoOcc.CodiceFiscale);
				$(".cognome", table).text(statoOcc.Cognome);
				$(".nome", table).text(statoOcc.Nome);
				$(".dataNascita", table).text(readFormat(statoOcc.DataNascita));
				$(".comuneNascita", table).text(statoOcc.ComNascita);
				$(".comuneResidenza", table).text(statoOcc.Residenza.DescrComune);
				$(".indirizzoResidenza", table).text(statoOcc.Residenza.Indirizzo);
				$(".comuneDomicilio", table).text(statoOcc.Domicilio.DescrComune);
				$(".indirizzoDomicilio", table).text(statoOcc.Domicilio.Indirizzo);
				$(".statoOcc", table).text(statoOcc.StatoOccupazionale.DescrizioneSO);
				$(".codStatoOcc", table).text(statoOcc.StatoOccupazionale.Codice);
				$(".mesiAnzianita", table).text(statoOcc.StatoOccupazionale.MesiAnzianita);
				$(".dataDid", table).text(readFormat(statoOcc.StatoOccupazionale.DataDid));

				// A21 -DISOCCUPATO
				// A212 - PRECARIO
				// A213 - IN ATTIVITA' SENZA CONTRATTO
				// A22 - INOCCUPATO
				// A223 - IN ATTIVITA' SENZA CONTRATTO
				// B2 - IN MOBILITA' OCCUPATO
				var codiciDiInteresse = [ "A21", "A212", "A213", "A22", "A223", "B2" ];
				var i, codiceTrovato = false;
				for (i = 0; i < codiciDiInteresse.length; i += 1) {
					if (codiciDiInteresse[i] == statoOcc.StatoOccupazionale.CodiceSO) {
						codiceTrovato = true;
						break;
					}
				}

				if (!codiceTrovato) {
					$("#mesiAnzianita").hide();
				}

				$("#answer .ok").fadeIn();
				table.fadeIn();
				$(".printButton").fadeIn();
			}
			$(".backButtonStyle").fadeIn();
		},
		error : function(e, xhr) {
			$("#answer .error").append("<b>Errore durante la richiesta dati al CPI. Riprova più tardi.</b>").fadeIn();
			$(".backButtonStyle").fadeIn();
		}
	});
}

/**
 * Fa partire una richiesta ajax al server per stampare lo stato occupazionale
 * del lavoratore
 * 
 * @param user_id
 * @returns
 */
function fetchStampaStatoOccupazionale(user_id) {
	// window.open("/MyPortal/secure/rest/services/stampa_stato_occupazionale?user_id="+
	// user_id, "_blank");
	var url = "/" + contextName + "/secure/rest/services/stampa_stato_occupazionale?user_id=" + user_id;

	fetchStampa(url)
}

function fetchStampaPercorsoLav(user_id) {

	var url = "/" + contextName + "/secure/rest/services/stampa_percorso_lavoratore?user_id=" + user_id
	url += "&data_inizio=" + $(jq("percorso:data_inizio:inputText")).val();
	// Verifica se data di fine definita in quanto non più in gestione
	if ($(jq("percorso:data_fine:inputText")).val()) url += "&data_fine=" + $(jq("percorso:data_fine:inputText")).val();
	// window.open(url, "_blank");

	fetchStampa(url);
}

/**
 * Fa partire una richiesta ajax al server per la conferma periodica di
 * disoccupazione del lavoratore
 * 
 * @param user_id
 * @returns
 */
function fetchConfermaPeriodica(user_id) {
	// esegue la chiamata
	$
			.ajax({
				url : "/" + contextName + "/secure/rest/services/conferma_periodica?user_id=" + user_id,
				dataType : "json",
				cache : false,
				success : function(data) {
					// nascondi il waiting loader
					$("#answer .wait").hide();

					if (data.codice === "OK") {
						$("#answer .ok_no_style").append("<b>" + data.descrizione + "</b><br/>").fadeIn();
					}

					var errori = data.Errori;
					if (errori != null) {
						for (var i = 0; i < errori.length; i++) {
							var descrizioneErrore = errori[i].descrizioneErrore;

							/*
							 * per ogni errore crea un div con il messaggio di
							 * errore nella pagina
							 */
							$("#answer")
									.append(
											"<div >"
													+ "<img class=\"rightMargin\" src=\"/MyPortal/resources/images/error.png\" alt=\"error\"></img>"
													+ "<b>" + descrizioneErrore + "</b><br/></div>").fadeIn();
						}
					}
					$(".backButtonStyle").fadeIn();
				},
				error : function(e, xhr) {
					$("#answer .error").append("<b>Errore durante la richiesta dati al CPI. Riprova più tardi.</b>")
							.fadeIn();
					$(".backButtonStyle").fadeIn();
				}
			});
}

/**
 * Fa partire una richiesta ajax al server per richiedere l'elenco dei movimenti
 * da sanare del lavoratore
 * 
 * @param user_id
 * @returns
 */
function fetchElencoMovimenti(user_id) {
	$
			.ajax({
				url : "/" + contextName + "/secure/rest/services/elenco_movimenti?user_id=" + user_id,
				dataType : "json",
				cache : false,
				success : function(data) {
					$("#answer .wait").hide();
					if (data.status == 'error') {
						$("#answer .error").append("<b>" + data.description + "</b>");
						$("#answer .error").fadeIn();
						attivaLinkStampa();
					} else {
						// mostra i movimenti da sanare
						var foundOneOpen = elaboraElencoMovimenti(data);
						$("#answer .sana_redditi")
								.fadeIn(
										'slow',
										function() {
											if (foundOneOpen) {
												$("#answer .ok, .okButton").fadeIn();
											} else {
												$("#answer .warning b")
														.text("Attenzione.")
														.append("<br/>")
														.append(
																"E' già presente una sanatoria che non permette di effettuare la Dichiarazione di immediata disponibilità.")
														.append("<br/>")
														.append(
																"Ti preghiamo di recarti al Centro per l'impiego per correggere il problema.");
												$("#answer .warning").fadeIn();
												attivaLinkStampa();
											}
										});
					}
				},
				error : function(e, xhr) {
					$("#answer .error").append("<b>Errore durante la richiesta dati al CPI. Riprova più tardi.</b>")
							.fadeIn();
				}
			});
}

// tipi possibili di movimento
var tipiMovimento = {
	"AVV" : "Avviamento",
	"CES" : "Cessazione",
	"PRO" : "Proroga",
	"TRA" : "Trasformazione"
}

/**
 * Fa partire una richiesta ajax al server per richiedere i dati di invio a
 * cliclavoro del CV corrispondente a idCvDatiPersonali
 * 
 * @param idCvDatiPersonali
 * @returns
 */
function fetchDatiInvioClCV(idCvDatiPersonali) {
	// esegue la chiamata
	$.ajax({
		url : "/" + contextName + "/secure/rest/cliclavoro/getDatiInvioCliclavoroCV?idCvDatiPersonali="
				+ idCvDatiPersonali,
		dataType : "json",
		cache : false,
		success : function(data) {
			if (data.status == 'error') {
				/* visualizzo l'errore riportato dal servizio */
				$.jnotify(data.description, 'error', true);
			} else {
				// inserisci tutti i dati in modale, se presenti
				var panel = $("#dati_invio_cliclavoro_cv_modal");
				if (data.codComunicazione) {
					$("#codice_candidatura_view", panel).text(data.codComunicazione);
				} else {
					$("#codice_candidatura_label", panel).hide();
					$("#codice_candidatura_view", panel).hide();
				}
				if (data.codComunicazionePrec) {
					$("#codice_candidatura_prec_view", panel).text(data.codComunicazionePrec);
				} else {
					$("#codice_candidatura_prec_label", panel).hide();
					$("#codice_candidatura_prec_view", panel).hide();
				}
				if (data.motivoChiusura) {
					$("#motivo_chiusura_view", panel).text(data.motivoChiusura);
				} else {
					$("#motivo_chiusura_label", panel).hide();
					$("#motivo_chiusura_view", panel).hide();
				}
				if (data.statoInvio) {
					$("#stato_invio_view", panel).text(data.statoInvio);
				} else {
					$("#stato_invio_label", panel).hide();
					$("#stato_invio_view", panel).hide();
				}
				if (data.dtInvio) {
					$("#data_invio_view", panel).text(data.dtInvio);
				} else {
					$("#data_invio_label", panel).hide();
					$("#data_invio_view", panel).hide();
				}
				if (data.dtScadenza) {
					$("#data_scadenza_view", panel).text(data.dtScadenza);
				} else {
					$("#data_scadenza_label", panel).hide();
					$("#data_scadenza_view", panel).hide();
				}

				panel.show();
				panel.dialog({
					width : 500,
					modal : false,
					resizable : false
				});
			}
		},
		error : function(e, xhr) {
			/* visualizzo un errore generico */
			$.jnotify('Errore nel reperimento dei dati', 'error', true);
		}
	});
}

/**
 * Fa partire una richiesta ajax al server per richiedere i dati di invio a
 * cliclavoro della vacancy corrispondente a idVaDatiVacancy
 * 
 * @param idVaDatiVacancy
 * @returns
 */
function fetchDatiInvioClVA(idVaDatiVacancy) {
	// esegue la chiamata
	$
			.ajax({
				url : "/" + contextName + "/secure/rest/cliclavoro/getDatiInvioCliclavoroVA?idVaDatiVacancy="
						+ idVaDatiVacancy,
				dataType : "json",
				cache : false,
				success : function(data) {
					if (data.status == 'error') {
						/* visualizzo l'errore riportato dal servizio */
						$.jnotify(data.description, 'error', true);
					} else {
						// inserisci tutti i dati in modale, se presenti
						var panel = $("#dati_invio_cliclavoro_va_modal");
						if (data.codComunicazione) {
							$("#codice_candidatura_view", panel).text(data.codComunicazione);
						} else {
							$("#codice_candidatura_label", panel).hide();
							$("#codice_candidatura_view", panel).hide();
						}
						if (data.codComunicazionePrec) {
							$("#codice_candidatura_prec_view", panel).text(data.codComunicazionePrec);
						} else {
							$("#codice_candidatura_prec_label", panel).hide();
							$("#codice_candidatura_prec_view", panel).hide();
						}
						if (data.motivoChiusura) {
							$("#motivo_chiusura_view", panel).text(data.motivoChiusura);
						} else {
							$("#motivo_chiusura_label", panel).hide();
							$("#motivo_chiusura_view", panel).hide();
						}
						if (data.statoInvio) {
							$("#stato_invio_view", panel).text(data.statoInvio);
						} else {
							$("#stato_invio_label", panel).hide();
							$("#stato_invio_view", panel).hide();
						}
						if (data.dtInvio) {
							$("#data_invio_view", panel).text(data.dtInvio);
						} else {
							$("#data_invio_label", panel).hide();
							$("#data_invio_view", panel).hide();
						}
						if (data.dtScadenza) {
							$("#data_scadenza_view", panel).text(data.dtScadenza);
						} else {
							$("#data_scadenza_label", panel).hide();
							$("#data_scadenza_view", panel).hide();
						}

						panel.show();
						panel.dialog({
							width : 500,
							modal : false,
							resizable : false
						});
					}
				},
				error : function(e, xhr) {
					/* visualizzo un errore generico */
					$.jnotify('Errore nel reperimento dei dati', 'error', true);
				}
			});
}

/**
 * Elabora le informazioni sui movimenti arrivate dal SIL e le mostra all'utente
 * 
 * @param ultimoPeriodo
 */
function elaboraElencoMovimenti(ultimoPeriodo) {
	var rapportoLavorativo = ultimoPeriodo.RapportoLavorativo;
	var datiAzienda = rapportoLavorativo.DatiAzienda;

	var lastRapporto = $("<span>");
	lastRapporto.append(
			nilJSON(rapportoLavorativo.DataInizioRapporto) ? "-" : readFormat(rapportoLavorativo.DataInizioRapporto))
			.append(" - ").append(
					nilJSON(rapportoLavorativo.DataFineRapporto) ? "-"
							: readFormat(rapportoLavorativo.DataFineRapporto)).append(" ").append(
					$("<b>").text(datiAzienda.RagioneSociale)).append(" CF: " + datiAzienda.CodiceFiscale).append(
					$("<br/>")).append("Retribuzione totale nel periodo: non dichiarata");

	$('.lastMovimentoBox').append(lastRapporto);

	// variabile che determina se vi è almeno un mvimento con il reddito da
	// sanare
	var foundOneOpen = false;
	// per ogni rapporto, estrai i movimenti e scorrili
	var movimenti = rapportoLavorativo.Movimento instanceof Array ? rapportoLavorativo.Movimento
			: [ rapportoLavorativo.Movimento ];
	for (var j = 0; j < movimenti.length; j++) {
		// controllo che non sia una cessazione
		// if (movimento.TipoMovimento == "CES") continue;
		var movimento = movimenti[j];
		var row = $('<tr name="Mov">');
		row.append($('<td class="dataInizio">').text(
				nilJSON(movimento.DataInizio) ? "-" : readFormat(movimento.DataInizio)));
		row.append($('<td class="dataFine">').text(nilJSON(movimento.DataFine) ? "-" : readFormat(movimento.DataFine)));
		var tipoMovTxt = nilJSON(movimento.TipoMovimento) ? "-" : "" + tipiMovimento[movimento.TipoMovimento];
		tipoMovTxt += " - ";
		tipoMovTxt += nilJSON(movimento.DescTipoContratto) ? "-" : movimento.DescTipoContratto;
		row.append($('<td class="tipoMovimento">').text(tipoMovTxt));
		// componi la descrizione dell'azinda
		var descAzienda = "";
		descAzienda += datiAzienda.RagioneSociale;
		descAzienda += " - ";
		descAzienda += datiAzienda.Indirizzo + " - " + datiAzienda.Cap + " " + datiAzienda.ComuneSede + " ("
				+ datiAzienda.TargaSede + ")";
		row.append($('<td class="descAzienda">').text(descAzienda));

		var redditoCol = $('<td class="reddito">').append(
				$('<input type="hidden" name="prgMovimento"/>').val(movimento.ChiaveMovimento)).append(
				$('<input type="hidden" name="numklomov"/>').val(movimento.CodLock));
		// mostra il campo di input solo se il reddito non è stato sanato
		if ("N" == movimento.RedditoSanato) {
			redditoCol.append($('<input type="text" name="RedditoMensileSanato" maxlength="8"/>').width("100px"));
			foundOneOpen = true;
		} else {
			redditoCol.text(nilJSON(movimento.RetribuzioneMensile) ? "-" : movimento.RetribuzioneMensile + "€");
		}

		row.append(redditoCol);
		row.append($('<td class="totale">'))
		$("#answer .sana_redditi table tbody").append(row);

	}

	calcolaColonnaTotale();

	return foundOneOpen;
}

/**
 * Determina se un oggetto JSON è nullo verificando se possiede l'attributo
 * xsi:nil ed è impostato a 'true'
 * 
 * @param jsonObject
 * @returns {Boolean}
 */
function nilJSON(jsonObject) {
	return jsonObject["xsi:nil"] == true;
}

function readFormat(date) {
	if (!date.length)
		return "";
	var parts = date.match(/(\d+)/g);
	var newD = new Date(parts[0], parts[1] - 1, parts[2]);
	var day = newD.getDate();
	if (day < 10)
		day = '0' + day;
	var month = newD.getMonth() + 1;
	if (month < 10)
		month = '0' + month;
	var year = newD.getFullYear();
	return day + '/' + month + '/' + year;
}

/**
 * Esegue una verifica sui valori inseriti dall'utente e, se necessario, mostra
 * una modale con un avvertimento.
 * 
 * @param user_id
 * @returns {Boolean}
 */
function checkAndSanaReddito(user_id, cod_regione, cod_provincia, dataFineDisabilMsgAppunt) {
	if (true) {
		$('#reddito_alto_panel').dialog({
			modal : true
		});
	} else {
		sanaReddito(user_id, cod_regione, cod_provincia, dataFineDisabilMsgAppunt);
	}
	return false;
}

/**
 * 
 */
function calcolaColonnaTotale() {
	var totalDer = 0;
	$("#answer .sana_redditi table tbody tr").each(function() {
		var dataInizioStr = $(this).find(".dataInizio").text();
		var dataFineStr = $(this).find(".dataFine").text();
		var importoStr = $(this).find(".reddito").text();
		var importo = parseInt(importoStr)
		if (dataInizioStr != '-' && dataFineStr != '-' && !isNaN(importo)) {

			var dataInizioParts = dataInizioStr.split("/");
			var dataFineParts = dataFineStr.split("/");

			var dataInizio = new Date(dataInizioParts[2], (dataInizioParts[1] - 1), dataInizioParts[0]);
			var dataFine = new Date(dataFineParts[2], (dataFineParts[1] - 1), dataFineParts[0]);
			var millis = (dataFine - dataInizio);
			var millisinmonth = (1000 * 60 * 60 * 24 * 30);
			var nmesi = (millis / millisinmonth);
			var totale = (nmesi * importo);
			totalDer += totale;
			$(this).find(".totale").text(totale.toFixed(0) + "€");
		}
	});
	var totalDerEl = $(
			"<div><b>Totale derivante dalla somma dei redditi nei singoli periodi: " + totalDer.toFixed(0)
					+ "€</b></div>").css("text-align", "right").css("margin-top", "10px")
	$("#answer .sana_redditi table").after(totalDerEl);
}

/**
 * Funzione eseguita quando l'utente vuole sanare il proprio reddito.
 * 
 * @param user_id
 */
function sanaReddito(user_id, cod_regione, cod_provincia, dataFineDisabilMsgAppunt) {
	// nascondi tutto e mostra l'ajax loader
	$('#reddito_alto_panel').dialog('close');
	$("#answer .ok").fadeOut();
	$("#answer .sana_redditi")
			.fadeOut(
					'slow',
					function() {
						var el = $("#answer .wait");
						$("#answer .wait b").html(
								"<b>Stiamo sanando la tua situazione reddituale. Ti preghiamo di attendere...</b>");
						el
								.fadeIn(
										'slow',
										function() {
											// costruisce un json da inviare al
											// servizio REST contenente i dati
											// dei movimenti da sanare.
											var table = $('.sana_redditi table');
											var jsonList = [];
											table.find("tr[name='Mov']").each(
													function() {
														var prgMovimento = $(this).find('input[name=prgMovimento]')
																.val();
														var numklomov = $(this).find('input[name=numklomov]').val();
														var redditoMensileSanato = $(this).find(
																'input[name=RedditoMensileSanato]').val();
														jsonList.push({
															"prgMovimento" : prgMovimento,
															"RedditoMensileSanato" : redditoMensileSanato,
															"numklomov" : numklomov
														});
													});
											var jsonObject = {
												"Mov" : jsonList
											};
											// invia la richiesta
											$
													.ajax({
														url : "/" + contextName
																+ "/secure/rest/services/sana_reddito?user_id="
																+ user_id,
														dataType : "json",
														data : "json=" + JSON.stringify(jsonObject),
														cache : false,
														type : "POST",
														success : function(data) {
															$("#answer .wait").hide();
															if (data.status == 'error') {
																// in caso di
																// errore
																// mostralo
																// all'utente
																$("#answer .error").append(
																		"<b>" + data.description + "</b><br/>");
																$("#answer .error")
																		.append(
																				"<b>Ti invitiamo a recarti al Centro per l'impiego di riferimento per risolvere il problema.</b>");
																$("#answer .error").fadeIn();
																attivaLinkStampa();
															}
															// nel caso gli
															// venga concesso di
															// correggere i dati
															// nuovamente
															// riproponigli la
															// schermata e
															// mostra un alert
															// con l'errore.
															else if (data.status == 'redo') {
																$("#answer .sana_redditi").fadeIn();
																alert(data.description);
															} else {
																$("#answer .sanatoria_ok")
																		.fadeIn(
																				'slow',
																				function() {
																					$("#answer .sana_redditi table tr")
																							.remove();
																					$("#answer .wait b")
																							.html(
																									"Stiamo stipulando la Dichiarazione di immediata disponibilità con il Centro per l'impiego di riferimento....");

																					stipulaNuovaDid(user_id,
																							cod_regione, cod_provincia,
																							dataFineDisabilMsgAppunt);
																				});
															}
														},
														error : function(e, xhr) {
															$("#answer .wait").hide();
															$("#answer .error")
																	.append(
																			"<b>Errore durante la richiesta dati al CPI. Riprova più tardi.</b>")
																	.fadeIn();
															attivaLinkStampa();
														}
													});
										});
					});
}

function stipulaNuovaDid(user_id, rischio_disoccupazione, data_licenziamento, data_lettera_licenziamento,
		cod_regione, cod_provincia, dataFineDisabilMsgAppunt, showEarnedBadge) {
	$('.ok').hide();
	$('.warning').hide();
	$('.error').hide();
	$('.didAttiva').hide();
	$('.errorStampa').hide();
	disattivaLinkStampa();
	// NB: Lo if(false) serve perchè ora (luglio 2016) nessuna regione usa più la funziona DID
	// temporanea che non chiamava il SIL. Lasciamo comunque la funzione fetchStipulaDidTemp disponibile
	// nel caso che in futuro qualcuno ci chieda di nuovo di usarla.
	if (false) {
		$("#waitDidRer").fadeIn('slow', function() {
			// Solo per RER
			fetchStipulaDidTemp(user_id, cod_regione, cod_provincia, dataFineDisabilMsgAppunt);
		});
	} else {
		$("#waitDid").fadeIn('slow', function() {
			fetchStipulaDid(user_id, rischio_disoccupazione, data_licenziamento, data_lettera_licenziamento,
					cod_regione, cod_provincia, dataFineDisabilMsgAppunt, showEarnedBadge);
		});
	}
	return true;
}

function fetchStipulaDidTemp(user_id, cod_regione, cod_provincia, dataFineDisabilMsgAppunt) {
	$
			.ajax({
				url : "/" + contextName + "/secure/rest/services/stipula_did_rer?user_id=" + user_id,
				dataType : "json",
				cache : false,
				success : function(data) {
					$('.sanatoria_ok').hide();
					$("#answer .wait")
							.fadeOut(
									'slow',
									function() {
										if (data.status == 'success') {
											var currentTime = new Date();
											var month = ("0" + (currentTime.getMonth() + 1)).slice(-2);
											var day = ("0" + currentTime.getDate()).slice(-2);
											var year = currentTime.getFullYear();
											var el = $("<li class=\"stampaDid\"><div class=\"disabled\"><a href=\"#\" class=\"toggleLink\" onclick=\"fetchStampa('/"
													+ contextName
													+ "/secure/rest/services/get_stampa?stampa_id="
													+ data.fileId
													+ "');return false;\">Dichiarazione del "
													+ day
													+ "/"
													+ month
													+ "/"
													+ year
													+ "</a><span class=\"toggleLink\">Dichiarazione del "
													+ day
													+ "/"
													+ month + "/" + year + "</span></div></li>");
											// var msg = 'ATTENZIONE! <br /> La
											// Dichiarazione di Immediata
											// Disponibilità su Lavoro per Te è
											// stata rilasciata in data '
											// + day + '/' + month + '/' + year;
											// $.jnotify(msg, 'info', true);

											$(".elencoStampeDid ul").prepend(el);
											$(".stampaDid ul").prepend(el);
											$('.stampaDid').removeAttr('style');
											attivaLinkStampa();
											$("#answer .ok").fadeIn('slow');
											$(".searchButton").button("enable");
											fetchStampa("/" + contextName
													+ "/secure/rest/services/get_stampa?stampa_id=" + data.fileId);
											$("#waitStampa").fadeOut('slow');
											disegnaBottoni();
										}
										attivaLinkStampa();
									});
				},
				error : function(e, xhr) {
					$('.sanatoria_ok').hide();
					$("#answer .error").append("<b>Errore durante la richiesta dati al CPI. Riprova più tardi.</b>")
							.fadeIn();
					attivaLinkStampa();
					disegnaBottoni();
				}
			});
}

/**
 * Fa partire una richiesta ajax al server per richiedere la stipula della DID
 * del lavoratore
 * 
 * @param user_id
 * @returns
 */
function fetchStipulaDid(user_id, rischio_disoccupazione, data_licenziamento, data_lettera_licenziamento,
		cod_regione, cod_provincia, dataFineDisabilMsgAppunt, showEarnedBadge) {
	$
			.ajax({
				url : "/" + contextName + "/secure/rest/services/stipula_did?user_id=" + user_id 
					+ "&rischio_disoccupazione=" + rischio_disoccupazione 
					+ "&data_licenziamento=" + data_licenziamento
					+ "&data_lettera_licenziamento=" + data_lettera_licenziamento,
				dataType : "json",
				cache : false,
				success : function(data) {
					$('.sanatoria_ok').hide();
					$("#answer .wait")
							.fadeOut(
									'slow',
									function() {
										if (data.status == 'errorDIDpresente') {
											$("#answer").css("width", "900px");
											$("#answer .didAttiva .did-msg-content").empty();
											$("#answer .didAttiva").css("width", "900px");
											$("#answer .didAttiva .did-msg-content").append("<b>" + data.description + "</b><br/>");
											$("#answer .didAttiva .did-msg-content")
													.append(
															"<b>Se necessiti dell'attestazione del tuo stato di disoccupazione puoi contattare il Centro per l'Impiego di riferimento.</b><br/>");
											$("#answer .didAttiva").fadeIn('slow');
											attivaLinkStampa();
										} else if (data.status == 'errorAnagrafica') {
											$("#answer").css("width", "900px");
											$("#answer .error").css("width", "900px");
											$("#answer .error .did-msg-content").empty();
											$("#answer .error .did-msg-content").append("<b>" + data.description + "</b><br/>");
											$("#answer .error .did-msg-content")
													.append(
															"<b>Vai nel tuo profilo per inserire i dati mancanti.</b>");
											$("#answer .error").fadeIn('slow');
											attivaLinkStampa();
										} else if (data.status == 'errorUmbria') {
											$("#answer").css("width", "900px");
											$("#answer .error").css("width", "900px");
											$("#answer .error .did-msg-content").empty();
											$("#answer .error .did-msg-content").append("<b>" + data.description + "</b><br/><br/>");
											$("#answer .error .did-msg-content")
													.append(
															"<b>Dalla tua scrivania, nella sezione Gestione Appuntamenti, prenota subito un appuntamento con il tuo Centro per l’impiego così da verificare la tua situazione amministrativa, rilasciare la DID e stipulare il Patto di Servizio personalizzato.</b>");
											$("#answer .error").fadeIn('slow');
											attivaLinkStampa();
										} else if (data.status == 'error') {
											$("#answer").css("width", "900px");
											$("#answer .error .did-msg-content").empty();
											$("#answer .error").css("width", "900px");
											$("#answer .error .did-msg-content").append("<b>" + data.description + "</b><br/>");
											$("#answer .error .did-msg-content")
													.append(
															"<b>Ti invitiamo a recarti al Centro per l'impiego di riferimento se necessiti di maggiori informazioni.</b>");
											$("#answer .error").fadeIn('slow');
											attivaLinkStampa();
										} else if (data.status == 'movimenti') {

											/*
											 * per via della legge Fornero la
											 * sanatoria dei redditi e'
											 * momentaneamente sospesa
											 */
											$("#answer").css("width", "900px");
											$("#answer .error").css("width", "900px");
											$("#answer .error .did-msg-content").empty();
											$("#answer .error .did-msg-content")
													.append(
															"<b>"
																	+ "&Egrave; possibile che il tuo Stato Occupazionale non sia compatibile con la domanda."
																	+ "</b><br/>");
											$("#answer .error .did-msg-content")
													.append(
															"<b>Ti invitiamo a recarti al Centro per l'impiego di riferimento se necessiti di maggiori informazioni.</b>");
											$("#answer .error").fadeIn('slow');
											attivaLinkStampa();

											/*
											 * var el = $("#answer .wait");
											 * $("#answer .wait b") .html( "<b>E'
											 * necessario sanare la propria
											 * situazione.</b><br/><b>Stiamo
											 * contattando il Centro per
											 * l'impiego per sapere quali dati
											 * puoi aiutarci a correggere.</b>");
											 * el.fadeIn('slow', function() {
											 * fetchElencoMovimenti(user_id);
											 * });
											 */
										} else {
											var currentTime = new Date();
											var month = ("0" + (currentTime.getMonth() + 1)).slice(-2);
											var day = ("0" + currentTime.getDate()).slice(-2);
											var year = currentTime.getFullYear();
											var el = $("<li class=\"stampaDid\"><div class=\"disabled\"><a href=\"#\" class=\"toggleLink\" onclick=\"fetchStampa('/"
													+ contextName
													+ "/secure/rest/services/get_stampa?stampa_id="
													+ data.fileId
													+ "');return false;\">Dichiarazione del "
													+ day
													+ "/"
													+ month
													+ "/"
													+ year
													+ "</a><span class=\"toggleLink\">Dichiarazione del "
													+ day
													+ "/"
													+ month + "/" + year + "</span></div></li>");
											// console.log($(".elencoStampeDid
											// ul"));

											var msg = "";
											if (cod_regione == 8) {// emilia
												// romagna
												// per bologna disabilitazione
												// messaggio appuntamente
												var currentDate = new Date()
												// alert(currentDate);

												var dateComponents = dataFineDisabilMsgAppunt.split("/");
												var mydate = new Date(dateComponents[2], dateComponents[1] - 1,
														dateComponents[0]);
												// alert(mydate);

												var currentTime = new Date();
												if (currentDate <= mydate && cod_provincia == 37) {
													var msg = 'ATTENZIONE! <br /> La Dichiarazione di Immediata Disponibilità su Lavoro per Te è stata rilasciata in data '
															+ day + '/' + month + '/' + year
													'';
													$.jnotify(msg, 'info', true);
												} else {
													var msg = 'ATTENZIONE! <br /> A seguito della Dichiarazione di Immediata Disponibilità da te rilasciata su Lavoro per Te in data '
															+ day
															+ '/'
															+ month
															+ '/'
															+ year
															+ ', ti è stato fissato un appuntamento presso il CPI a cui hai rilasciato la dichiarazione (indicato nella stampa della DID) per un colloquio con un operatore del CPI. La data e l\'ora dell\'appuntamento ti verranno comunicati tramite e-mail e/o SMS. Qualora non ti pervenisse nessuna informazione a riguardo è necessario che contatti il CPI. Ti ricordiamo che il colloquio con un operatore del CPI è obbligatorio per mantenere lo stato occupazionale derivante dalla tua Dichiarazione.';
													$.jnotify(msg, 'info', true);
												}
											} else if (cod_regione == 10) { // umbria
												var msg = 'ATTENZIONE! <br /> A seguito della Dichiarazione di Immediata Disponibilità da te rilasciata su Lavoro per Te in data '
														+ day
														+ '/'
														+ month
														+ '/'
														+ year
														+ ', ti è stato fissato un appuntamento presso il CPI a cui hai rilasciato la dichiarazione (indicato nella stampa della DID) per un colloquio con un operatore del CPI. La data e l\'ora dell\'appuntamento ti verranno comunicati tramite e-mail e/o SMS. Qualora non ti pervenisse nessuna informazione a riguardo è necessario che contatti il CPI entro 3 mesi dalla data di stipula DID. Ti ricordiamo che il colloquio con un operatore del CPI è obbligatorio per mantenere lo stato occupazionale derivante dalla tua Dichiarazione.';
												$.jnotify(msg, 'info', true);
											}

											$(".elencoStampeDid ul").prepend(el);
											$(".stampaDid ul").prepend(el);
											$('.stampaDid').removeAttr('style');
											$("#answer .ok").fadeIn('slow');
											fetchStampa("/" + contextName
													+ "/secure/rest/services/get_stampa?stampa_id=" + data.fileId);
											$("#waitStampa").fadeOut('slow');
											attivaLinkStampa();
											
											// Gamification disattivata, per il momento, quindi non mostro il modal.
											//if(showEarnedBadge) {
											//	$('#dialogBadgeOttenuto').dialog({width:'400px', zIndex:'999999'});
											//}
										}
									});
				},
				error : function(e, xhr) {
					$('.sanatoria_ok').hide();
					$("#answer .error").append("<b>Errore durante la richiesta dati al CPI. Riprova più tardi.</b>")
							.fadeIn();
					attivaLinkStampa();
				}
			});
}

function rinnovaPatto(user_id) {
	$('.ok').hide();
	$('.warning').hide();
	$('.error').hide();
	$('.errorStampa').hide();
	disattivaLinkStampa();
	$("#waitDid").fadeIn('slow', function() {
		fetchRinnovoPatto(user_id);
	});
	return true;
}

/**
 * Fa partire una richiesta ajax al server per richiedere il rinnovo del patto
 * 
 * @param user_id
 * @returns
 */
function fetchRinnovoPatto(user_id) {
	$
			.ajax({
				url : "/" + contextName + "/secure/rest/services/rinnovo_patto?user_id=" + user_id,
				dataType : "json",
				cache : false,
				success : function(data) {
					$("#answer .wait")
							.fadeOut(
									'slow',
									function() {
										if (data.status == 'error') {
											$("#answer .error").empty();
											$("#answer .error").append(
													"<img src=\"/MyPortal/resources/images/error.png\" alt=\"error\">");
											$("#answer .error").append("<br />");
											$("#answer .error").append("<b>Impossibile elaborare la richiesta.</b>");
											$("#answer .error").append("<br />");
											$("#answer .error").append("<b>" + data.description + "</b><br/>");
											$("#answer .error").fadeIn('slow');
											attivaLinkStampa();
										} else {
											var fielId = data.fileId;
											var currentTime = new Date();
											var month = ("0" + (currentTime.getMonth() + 1)).slice(-2);
											var day = ("0" + currentTime.getDate()).slice(-2);
											var year = currentTime.getFullYear();
											var el = $("<li class=\"stampaDid\"><div class=\"disabled\"><a href=\"#\" class=\"toggleLink\" onclick=\"fetchStampa('/"
													+ contextName
													+ "/secure/rest/services/get_stampa?stampa_id="
													+ fielId
													+ "');return false;\">Rinnovo patto del "
													+ day
													+ "/"
													+ month
													+ "/"
													+ year
													+ "</a><span class=\"toggleLink\">Rinnovo patto del "
													+ day
													+ "/"
													+ month + "/" + year + "</span></div></li>");
											$(".elencoStampeDid ul").prepend(el);
											// $(".stampaDid ul").prepend(el);
											$('.stampaDid').removeAttr('style');
											attivaLinkStampa();
											$("#answer .ok").fadeIn('slow');
											fetchStampa("/" + contextName
													+ "/secure/rest/services/get_stampa?stampa_id=" + fielId);
											$("#waitStampa").fadeOut('slow');
										}
									});
				},
				error : function(e, xhr) {
					$("#answer .error").append("<b>Errore durante la richiesta dati al CPI. Riprova più tardi.</b>")
							.fadeIn();
					attivaLinkStampa();
				}
			});
}

function attivaLinkStampa() {
	$('.disabled span.toggleLink').attr('style', 'display:none');
	$('.disabled a.toggleLink').attr('style', 'display:inline');
}

function disattivaLinkStampa() {
	$('.disabled span.toggleLink').attr('style', 'display:inline');
	$('.disabled a.toggleLink').attr('style', 'display:none');
}

/**
 * Fa partire una richiesta ajax al server per prenotare un appuntamento per YG
 * 
 * @param user_id
 * @returns
 */
function fetchFissaAppuntamentoYG(user_id, idYgAdesione, identificativoSlot) {
	// esegue la chiamata
	$.ajax({
		/*
		 * al servizio di prenotazione ripasso tutti i dati anche se
		 * probabilmente molti sono inutili dato che ho l'identificativo dello
		 * slot. Ciononostante si e' oculatamente deciso di lasciare anche tutti
		 * i dati di prima senza un motivo apparente.
		 */
		url : "/" + contextName + "/secure/rest/services/fissa_appuntamento?user_id=" + user_id + "&id_yg_adesione="
				+ idYgAdesione + "&identificativo_slot=" + identificativoSlot,
		dataType : "json",
		cache : false,
		success : function(data) {
			// nascondi il waiting loader
			$("#answer .wait").hide();
			if (data.status == 'error') {
				// in caso di errore mostralo
				$("#answer .error").append("<br /> <b>" + data.description + "</b>").fadeIn();
			} else {
				var description = data.description;
				$(jq("description")).append(description);
				var dataAppuntamento = data.datiAppuntamento.dataAppuntamento;
				$(jq("dataAppuntamento:outputText")).append(dataAppuntamento);
				var oraAppuntamento = data.datiAppuntamento.oraAppuntamento;
				$(jq("oraAppuntamento:outputText")).append(oraAppuntamento);
				var denominazioneCPI = data.datiAppuntamento.denominazioneCPI;
				var indirizzoCPIstampa = data.datiAppuntamento.indirizzoCPIstampa;
				$(jq("cpi:outputText")).append(denominazioneCPI + "<br />" + indirizzoCPIstampa);
				var siglaOperatore = data.datiAppuntamento.siglaOperatore;
				$(jq("siglaOperatore:outputText")).append(siglaOperatore);
				if (data.datiAppuntamento.hasOwnProperty('ambiente')) {
					var ambiente = data.datiAppuntamento.ambiente;
					$(jq("ambiente:outputText")).append(ambiente);
				} else {
					$(jq("ambiente")).hide();
				}
				$("#answer .appuntamento_ok").fadeIn();
			}
			$(".backButtonStyle").fadeIn();
		},
		error : function(e, xhr) {
			$("#answer .wait").hide();
			$("#answer .error").append(
					"<b>Si &eacute; verificato un errore imprevisto nell'esecuzione del servizio.</b>").fadeIn();
			$(".backButtonStyle").fadeIn();
		}
	});
}
