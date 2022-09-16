/*
 * Created on 15-ott-07
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.module.movimenti.trasferimentoRamoAz;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import javax.xml.parsers.SAXParser;

import org.apache.log4j.Level;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.dispatching.module.ModuleFactory;
import com.engiweb.framework.dispatching.module.ModuleIFace;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.message.MessageBundle;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.Values;
import it.eng.sil.bean.Documento;
import it.eng.sil.bean.protocollo.ProtocolloDocumentoUtil;
import it.eng.sil.coop.services.InviaMigrazioni;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.MultipleTransactionQueryExecutorWrapper;
import it.eng.sil.module.movimenti.TrasferimentoRamoAzDBLogger;
import it.eng.sil.module.movimenti.trasfRamoAzienda.TrasferisciRamoAzienda;
import it.eng.sil.util.StatementUtils;
import it.eng.sil.util.Utils;

/**
 * Trasferimento ramo azienda: comunicazione proveniente dall'NCR tramite CO Vardatori
 * 
 * @author mancinid/savino
 */
public class TrasferimentoRamoAzModule extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(TrasferimentoRamoAzModule.class);

	private MultipleTransactionQueryExecutorWrapper tex;

	public void service(SourceBean request, SourceBean response) throws Exception {
		String codFiscaleAzPrecedente = null;
		TrasferimentoRamoAzDBLogger dbLogger = null;
		try {

			InputStream xmlInputStream;
			String datiXML = (String) request.getAttribute(TrasferimentoRamoAzRequestParams.TRA_DATI_XML);
			SAXParser parser = (SAXParser) request.getAttribute(TrasferimentoRamoAzRequestParams.TRA_SAX_PARSER);
			TrasferimentoRamoAzHandler trasfRamoAzHandler = (TrasferimentoRamoAzHandler) request
					.getAttribute(TrasferimentoRamoAzRequestParams.TRA_SAX_HANDLER);

			codFiscaleAzPrecedente = trasfRamoAzHandler.getCodFiscaleAzPrecedente();
			String dataInizioTrasferimento = trasfRamoAzHandler.getDataInizioTrasferimento();
			HashMap datiTestata = TrasferimentoRamoAzUtils.getDatiTestataVardatori(trasfRamoAzHandler);
			TreeMap sedeAttuale = trasfRamoAzHandler.getSedeAttuale();
			TreeMap sedePrecedente = trasfRamoAzHandler.getSedePrecedente();

			String codComunicazione = trasfRamoAzHandler.getCodiceComunicazione();
			String codTipoTrasf = trasfRamoAzHandler.getCodiceTrasferimento();
			String strCfAzPrecedente = trasfRamoAzHandler.getCodFiscaleAzPrecedente();
			String datInizio = trasfRamoAzHandler.getDataInizioTrasferimento();
			String tipoComunicazione = trasfRamoAzHandler.getTipoComunicazione();
			boolean rettificaAnnullamento = "03".equals(tipoComunicazione) || "04".equals(tipoComunicazione);

			dbLogger = new TrasferimentoRamoAzDBLogger(codComunicazione, codTipoTrasf, strCfAzPrecedente, datInizio,
					datiXML);

			// --------------------------------------------------------
			// --------------------------------------------------------

			/*
			 * Elaborazione in cui effettuo il parsing dell'XML UNICO VARDATORI e vado a recuperare le singole parti
			 * necessarie per l'inserimento della Trasformazione Ramo Azienda. Evito di caricare tutti i dati in memoria
			 * affinché non ci siamo problemi di out of memory nel caso in cui il file XML UNICO VARDATORI sia molto
			 * grande. L'oggetto più pesante caricato in memoria è codFiscLavList: si tratta di un ArrayList dei codici
			 * fiscali di tutti i lavoratori appartenenti ad una determinata SedeLavoro.
			 */
			request.delAttribute("ckeckboxMovimenti");
			int numeroSediLavoro = trasfRamoAzHandler.getNumeroSedeLavoro();
			// ---------------- APERTURA TRANSAZIONE ------------------------------
			this.tex = new MultipleTransactionQueryExecutorWrapper(Values.DB_SIL_DATI);
			// ABILITAZIONE PER IL MODULO DELLA TRANSACTION STRATEGY
			enableTransactions(this.tex);
			// ----------------- LEGGERE ATTENTAMENTE ------------------------------
			// DISABILITO LE CHIAMATE ALLA COMMIT, ROLLBACK E CLOSE CONNECTION FATTE DAI MODULI PARTECIPANTI ALLA
			// TRANSAZIONE
			this.tex.disableTXOperation();
			//
			// ----------------- LEGGERE ATTENTAMENTE ------------------------------
			// inerisco il tex nel request container in modo che i moduli chiamati possano recuperarlo ed operare nella
			// stessa transazione
			getRequestContainer().setAttribute("TQE_OBJECT", tex);
			// ---------------------------------------------------------------------
			CICLO_SEDI: for (int i = 0; i < numeroSediLavoro; i++) { // Ciclo sulla nuova "SedeLavoro"
				TrasferimentoRamoAzHandler trasfRamoAzHandlerNSL = new TrasferimentoRamoAzHandler(i + 1);
				xmlInputStream = new ByteArrayInputStream(datiXML.getBytes());
				parser.parse(xmlInputStream, trasfRamoAzHandlerNSL);

				// Unità aziendale della "sedeAttuale":
				TreeMap nuovaSede = trasfRamoAzHandlerNSL.getNuovaSedeNSL();
				ArrayList codFiscLavList = trasfRamoAzHandlerNSL.getCodFiscLavListNSL();

				// INIT REALE DELLA TRANSAZIONE
				this.tex.TRUE_InitTransaction();

				boolean nuovaSedeRegistrata = false;
				// ---------------------------------------------------------------------

				// ---------------------------------------------------------------------
				for (int k = 0; k < codFiscLavList.size(); k++) { // Ciclo sui "Lavoratori"
					if (k > 0) {
						// PER OGNI LAVORATORE SI USA UNA NUOVA TRANSAZIONE.
						// Chiaramente se si tratta del primo lavoratore della sede la transazione e' stata appena
						// inizializzata
						// e non debbo reinizializzarla.
						this.tex.TRUE_InitTransaction();
					}
					if (k == 0 || !nuovaSedeRegistrata) {
						String prgUnitaDestinaz = ricerca_unita("TRA_GET_PRGUNITA",
								(String) request.getAttribute(TrasferimentoRamoAzRequestParams.PRGAZIENDADESTINAZIONE),
								nuovaSede, request, response);
						nuovaSedeRegistrata = !("".equals(prgUnitaDestinaz));
						if (!nuovaSedeRegistrata) {
							prgUnitaDestinaz = inserimento_unita("TRA_GET_PRGUNITA", trasfRamoAzHandler, nuovaSede,
									request, response);
						}
						if ("".equals(prgUnitaDestinaz)) {
							// impossibile inserire l'unita' azienda
							// si esegue la rollback VERA e si passa ad un'altra sede.
							setMotivoerrore(response, "Impossibile inserire l'unita azienda della sede lavoro: "
									+ getDatiPrincipaliSede(nuovaSede));
							_logger.error("Impossibile inserire l'unita azienda della sede lavoro: "
									+ getDatiPrincipaliSede(nuovaSede));
							dbLogger.logResult("Impossibile inserire l'unita azienda della sede lavoro: "
									+ getDatiPrincipaliSede(nuovaSede));

							tex.TRUE_RollBackTransaction();
							// si passa alla prossima sede lavoro
							continue CICLO_SEDI;
						}
						request.delAttribute(TrasferimentoRamoAzRequestParams.PRGUNITADESTINAZIONE);
						request.setAttribute(TrasferimentoRamoAzRequestParams.PRGUNITADESTINAZIONE, prgUnitaDestinaz);
					}

					boolean traError = false;
					String codFiscLavoratore = (String) codFiscLavList.get(k);

					TrasferimentoRamoAzHandler trasfRamoAzHandlerCF = new TrasferimentoRamoAzHandler(codFiscLavoratore);
					xmlInputStream = new ByteArrayInputStream(datiXML.getBytes());
					parser.parse(xmlInputStream, trasfRamoAzHandlerCF);

					HashMap datiLav = trasfRamoAzHandlerCF.getDatiLavCF();
					HashMap contrattoLav = trasfRamoAzHandlerCF.getContrattoLavCF();

					if (rettificaAnnullamento) {
						String prgMovimentoApp = insRettifica_annullamento_appoggio(datiTestata, nuovaSede,
								sedePrecedente, trasfRamoAzHandlerCF);
						if (!prgMovimentoApp.equals("")) {
							boolean esitoValidazioneMassiva = validazioneMassiva(request, response, prgMovimentoApp,
									trasfRamoAzHandlerCF, codFiscLavoratore);
							if (!esitoValidazioneMassiva) {
								setMotivoerrore(response,
										"Errore nella validazione del movimento di rettifica/annullamento per il lavoratore "
												+ getDatiPrincipaliLav(datiLav, codFiscLavoratore));
								dbLogger.logResult(
										"Errore nella validazione del movimento di rettifica/annullamento per il lavoratore "
												+ getDatiPrincipaliLav(datiLav, codFiscLavoratore));
								traError = true;
							}
						} else {
							setMotivoerrore(response,
									"Errore nell'inserimento del movimento di annullamento/rettifica per il lavoratore "
											+ getDatiPrincipaliLav(datiLav, codFiscLavoratore));
							dbLogger.logResult(
									"Errore nell'inserimento del movimento di annullamento/rettifica per il lavoratore "
											+ getDatiPrincipaliLav(datiLav, codFiscLavoratore));
							traError = true;
						}
					} else {
						// uso una connessione fuori dalla transazione
						SourceBean movimentoAperto = getMovimentoAperto(codFiscLavoratore, codFiscaleAzPrecedente,
								dataInizioTrasferimento);

						String dataInizioMovAperto = (String) movimentoAperto
								.getAttribute("ROW." + TrasferimentoRamoAzRequestParams.DATINIZIOMOV);
						if ((dataInizioMovAperto != null)
								&& (DateUtils.compare(dataInizioMovAperto, dataInizioTrasferimento) >= 0)) {
							setMotivoerrore(response,
									"La data inizio del trasferimento (" + dataInizioTrasferimento
											+ ") deve essere successiva alla data inizio del movimento aperto ("
											+ dataInizioMovAperto + ") per il lavoratore "
											+ getDatiPrincipaliLav(datiLav, codFiscLavoratore));
							dbLogger.logResult("La data inizio del trasferimento (" + dataInizioTrasferimento
									+ ") deve essere successiva alla data inizio del movimento aperto ("
									+ dataInizioMovAperto + ") per il lavoratore "
									+ getDatiPrincipaliLav(datiLav, codFiscLavoratore));
							traError = true;
						}

						if (!traError && !movimentoAperto.containsAttribute("ROW")) { // Non esiste un movimento aperto
																						// di provenienza, quindi lo
																						// inserisco prima di inserire
																						// la TRA
							// uso una connessione fuori dalla transazione
							SourceBean movimentoApertoInOgniAz = getMovimentoApertoInOgniAz(codFiscLavoratore,
									dataInizioTrasferimento);

							if (movimentoApertoInOgniAz.containsAttribute("ROW")) {
								setMotivoerrore(response,
										"Alla data di inizio trasferimento " + dataInizioTrasferimento
												+ " il lavoratore " + getDatiPrincipaliLav(datiLav, codFiscLavoratore)
												+ " ha già un movimento aperto presso l'azienda "
												+ getDatiPrincipaliAz(movimentoApertoInOgniAz));
								dbLogger.logResult("Alla data di inizio trasferimento " + dataInizioTrasferimento
										+ " il lavoratore " + getDatiPrincipaliLav(datiLav, codFiscLavoratore)
										+ " ha già un movimento aperto presso l'azienda "
										+ getDatiPrincipaliAz(movimentoApertoInOgniAz));
								traError = true;
							} else {
								String dataInizioMov = (String) contrattoLav
										.get(TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO[6]);
								if (DateUtils.compare(dataInizioMov, dataInizioTrasferimento) < 0) {

									// Se ho più di una unità aziendale scelgo quella che abbia eventualmente il
									// movimento (anche se non valido) più recente
									// uso una connessione fuori dalla transazione
									TreeMap unitaSedePrecedente = getUnitaAzXNuovoMov(codFiscaleAzPrecedente,
											codFiscLavoratore);
									if (unitaSedePrecedente != null) {
										sedePrecedente = unitaSedePrecedente;
									}

									// Inserisco il nuovo movimento (di provenienza) nella tabella di appoggio
									// AM_MOVIMENTO_APPOGGIO
									// (NOTA: per inserire un movimento di avviamento non sono necessari i parametri
									// "sedeAttuale" e "nuovaSede")
									// USO LA CONNESSIONE IN TRANSAZIONE
									String prgMovApp = insAvviamentoFittizio(datiTestata, sedeAttuale, sedePrecedente,
											nuovaSede, datiLav, contrattoLav);
									boolean esitoInsertAvvFittizio = prgMovApp.equals("") ? false : true;

									if (esitoInsertAvvFittizio) {
										// ---------------------------------------------------------------------------
										// Validazione massiva del record inserito nella tabella AM_MOVIMENTO_APPOGGIO
										// usando la funzionalità già esistente in ValidaMovimenti.java
										// USO LA CONNESSIONE IN TRANSAZIONE
										boolean esitoValidazioneMassiva = validazioneMassiva(request, response,
												prgMovApp, trasfRamoAzHandlerCF, codFiscLavoratore);

										if (esitoValidazioneMassiva) { // Ora il movimento di provenienza sarà aperto
											// USO LA CONNESSIONE IN TRANSAZIONE
											movimentoAperto = getMovimentoAperto(codFiscLavoratore,
													codFiscaleAzPrecedente, dataInizioTrasferimento);
											if (!movimentoAperto.containsAttribute("ROW")) {
												traError = true;
												setMotivoerrore(response,
														"Impossibile recuperare il movimento di avviamento fittizio validato per il lavoratore "
																+ getDatiPrincipaliLav(datiLav, codFiscLavoratore));
												dbLogger.logResult(
														"Impossibile recuperare il movimento di avviamento fittizio validato per il lavoratore CF "
																+ getDatiPrincipaliLav(datiLav, codFiscLavoratore));
											}
										} else {
											setMotivoerrore(response,
													"Errore nella validazione del movimento di provenienza per il lavoratore "
															+ getDatiPrincipaliLav(datiLav, codFiscLavoratore));
											dbLogger.logResult(
													"Errore nella validazione del movimento di provenienza per il lavoratore "
															+ getDatiPrincipaliLav(datiLav, codFiscLavoratore));
											traError = true;
										}
									} else {
										setMotivoerrore(response,
												"Errore nell'inserimento del movimento di provenienza per il lavoratore "
														+ getDatiPrincipaliLav(datiLav, codFiscLavoratore));
										dbLogger.logResult(
												"Errore nell'inserimento del movimento di provenienza per il lavoratore "
														+ getDatiPrincipaliLav(datiLav, codFiscLavoratore));
										traError = true;
									}
								} else {
									setMotivoerrore(response,
											"La data inizio del movimento di provenienza (" + dataInizioMov
													+ ") deve essere precedente alla data inizio del trasferimento ("
													+ dataInizioTrasferimento + ") per il lavoratore "
													+ getDatiPrincipaliLav(datiLav, codFiscLavoratore));
									dbLogger.logResult("La data inizio del movimento di provenienza (" + dataInizioMov
											+ ") deve essere precedente alla data inizio del trasferimento ("
											+ dataInizioTrasferimento + ") per il lavoratore "
											+ getDatiPrincipaliLav(datiLav, codFiscLavoratore));
									traError = true;
								}
							}
						}

						if (!traError) {
							// INSERIMENTO TRASFORMAZIONE
							// Imposto nella request tutti i parametri necessari per l'inserimento del movimento
							setMovimentoAttributes(request, movimentoAperto, nuovaSede, datiTestata, contrattoLav);
							// Inserisco il movimento (TRA)
							boolean esitoInsertTrasferimento = insTrasferimento(request, response);
							if (esitoInsertTrasferimento) {
								// Cancellazione generale dei files DOCAREA dalla request:
								ProtocolloDocumentoUtil.cancellaFileDocarea();
							} else {
								traError = true;
								setMotivoerrore(response,
										"Errore nell'inserimento della Trasformazione Ramo Azienda per il lavoratore CF "
												+ getDatiPrincipaliLav(datiLav, codFiscLavoratore));
							}
						}
					}
					if (traError) {
						_logger.error("Errore nella Trasformazione Ramo Azienda");
						this.tex.TRUE_RollBackTransaction();
					} else {
						this.tex.TRUE_CommitTransaction();
						nuovaSedeRegistrata = true;
					}
				} // ciclo lavoratori di una sede
			} // ciclo sedi
		} catch (Exception e) {
			if (this.tex != null) {
				// this.tex.enableTransaction();
				this.tex.TRUE_RollBackTransaction();
			}
			_logger.error("Errore grave nell'inserimento della Trasformazione Ramo Azienda per l'azienda "
					+ codFiscaleAzPrecedente, e);
			setMotivoerrore(response, "Errore grave nell'inserimento della Trasformazione Ramo Azienda per l'azienda "
					+ codFiscaleAzPrecedente);
			if (dbLogger != null)
				dbLogger.logResult("Errore grave nell'inserimento della Trasformazione Ramo Azienda per l'azienda CF "
						+ codFiscaleAzPrecedente + ": " + e.getMessage());
			else {
				// debbo lanciare l'eccezione per permettere il log sul db
				throw e;
			}
		} finally {
			// chiusura connessione
			if (this.tex != null)
				this.tex.TRUE_CloseConnection();
			// ELIMINO DAL REQUEST CONTAINER L'OGGETTO TRANSACTION QUERY EXECUTOR WRAPPER
			getRequestContainer().delAttribute("TQE_OBJECT");
			// bye: alla prossima....
			this.tex = null;
		}
	}

	private boolean validazioneMassiva(SourceBean request, SourceBean response, String prgMovApp,
			TrasferimentoRamoAzHandler trasfRamoAzHandlerCF, String cfLavoratore) throws Exception {
		// Imposto nella request i dati mancanti necessari per la protocollazione:
		setProtSysdateValues(request);

		request.delAttribute("ckeckboxMovimenti");
		request.setAttribute("ckeckboxMovimenti", prgMovApp);

		request.delAttribute("AZIONE");
		request.setAttribute("AZIONE", "validaSelezionati");

		request.delAttribute(TrasferimentoRamoAzRequestParams.AUTOMATIC_TRA);
		request.setAttribute(TrasferimentoRamoAzRequestParams.AUTOMATIC_TRA, "TRUE");

		// Imposto il DefaultRequestContext da passare all'oggetto (modulo) validaMovimenti:
		DefaultRequestContext drc = new DefaultRequestContext(getRequestContainer(), getResponseContainer());
		// --------------------------------------------------------------------------------
		// USO DELLA TRANSAZIONE: NON VIENE PASSATA IN QUANTO IL MODULO NON LA USA DIRETTAMENTE,
		// MA LA USA IL THREAD BACKGROUNDVALIDATOR CHE LA RECUPERA DAL REQUESTCONTAINER
		ModuleIFace validaMovimenti = ModuleFactory.getModule("M_MovValidaMovimenti");
		((AbstractModule) validaMovimenti).setRequestContext(drc);

		_logger.debug("INIZIO VALIDAZIONE MOVIMENTO ...\n...... PRGMOVIMENTOAPP(s): " + prgMovApp);

		// Chiamata al modulo per la validazione massiva dei record inseriti in AM_MOVIMENTO_APPOGGIO
		// (inserimento del movimento in AM_MOVIMENTO):
		validaMovimenti.service(request, response);

		_logger.debug("FINE VALIDAZIONE MOVIMENTO ...");

		String resultValidazMassiva = null;
		if (trasfRamoAzHandlerCF.getTipoComunicazione().equals("04")) {
			// ANNULLAMENTO
			String codComunicazionePrec = trasfRamoAzHandlerCF.getCodiceComunicazionePrec();
			Object params[] = new Object[] { codComunicazionePrec, cfLavoratore, codComunicazionePrec, cfLavoratore };
			resultValidazMassiva = StatementUtils.getStringByStatement(this.tex,
					"SELECT_RESULT_ANNULLAMENTO_VALID_MASSIVA", params);
		} else // per 01 e 03 va bene la stessa query
			resultValidazMassiva = StatementUtils.getStringByStatement(this.tex, "SELECT_RESULT_VALID_MASSIVA",
					new Object[] { prgMovApp });

		return resultValidazMassiva.equals("") || this.tex.isRollbackCathed() ? false : true;

	}

	// Metodo che riproduce la funzionalità già esistente in TrasferisciRamoAzienda.service:
	private boolean insTrasferimento(SourceBean request, SourceBean response) throws Exception {
		boolean esitoInsMov = false;

		String prgMov = (String) request.getAttribute(TrasferimentoRamoAzRequestParams.PRGMOVIMENTO);
		String numklomov = (String) request.getAttribute(TrasferimentoRamoAzRequestParams.NUMKLOMOV);

		SourceBean result = new SourceBean("RESULT");
		SourceBean rows = new SourceBean("ROWS");
		result.setAttribute(rows);

		// Per il movimento controllo che la data di trasferimento non sia
		// esterna alla durata del rapporto:

		// cancello i precedenti valori in request
		request.delAttribute("PRGMOVIMENTOPREC");
		request.delAttribute("NUMKLOMOVPREC");

		// Imposto il valore corrente del prgmovimento in request e il numklomov
		request.setAttribute("PRGMOVIMENTOPREC", prgMov);
		request.setAttribute("NUMKLOMOVPREC", numklomov);

		// recupero dati del movimento originale ************************
		SourceBean datiMov = doSelect(request, response);

		SourceBean rowMov = (SourceBean) datiMov.getAttribute("ROW");
		String dataInAvviamento = (String) rowMov.getAttribute("DATINIZIOAVV");
		if (dataInAvviamento == null) {
			dataInAvviamento = (String) rowMov.getAttribute("DATINIZIOMOV");
		}
		String dataFineMov = (String) rowMov.getAttribute("DATFINEMOV");
		String tipoMovimento = (String) rowMov.getAttribute("TIPOMOVIMENTO");
		String dataTrasf = StringUtils.getAttributeStrNotNull(request, "DATTRASFERIMENTO");

		// Controllo i movimenti a tempo determinato
		if (tipoMovimento.equalsIgnoreCase("D")) {
			// Se la data di fine movimento è minore della data del trasferimento (dataInMovimento)OR
			// la data di inizio avviamento è maggiore della data del trasferimento (dataInMovimento),
			// allora blocco il trasferimento

			// DateUtils.compare ritorna -1 se date1 < date2, 0 se date1 = date2, 1 se date1 > date2
			if ((DateUtils.compare(dataFineMov, dataTrasf) == -1)
					|| (DateUtils.compare(dataInAvviamento, dataTrasf) == 1)) {
				rowMov.setAttribute("MOTIVOERRORE",
						MessageBundle.getMessage(String.valueOf(MessageCodes.TrasferimentoRamoAzienda.ERR_DATE_TRASF)));
				rows.setAttribute(rowMov);
			}
			// Controllo i movimenti a tempo Indeterminato
		} else if (tipoMovimento.equalsIgnoreCase("I")) {
			// Se la data di trasferimento è precedente l'inizio dell'avviamento blocco il trasferimento
			if (DateUtils.compare(dataInAvviamento, dataTrasf) == 1) {
				rowMov.setAttribute("MOTIVOERRORE",
						MessageBundle.getMessage(String.valueOf(MessageCodes.TrasferimentoRamoAzienda.ERR_DATA_TRASF)));
				rows.setAttribute(rowMov);
			}
		}

		// Se nel SourceBean RESULT.ROWS è presente l'attributo ROW
		// vuol dire che ci sono lavoratori che non possono essere trasferiti
		boolean trasfNonPossibile = rows.containsAttribute("ROW");

		if (!trasfNonPossibile) {
			// cancello i precedenti valori in request
			request.delAttribute("PRGMOVIMENTOPREC");
			request.delAttribute("NUMKLOMOVPREC");
			request.delAttribute("PRGMOVTRA");

			// Imposto il valore corrente del prgmovimento in request e il numklomov
			request.setAttribute("PRGMOVIMENTOPREC", prgMov);
			request.setAttribute("NUMKLOMOVPREC", numklomov);

			// recupero dati del movimento originale ***********************
			datiMov = doSelect(request, response);

			rowMov = (SourceBean) datiMov.getAttribute("ROW");

			dataInAvviamento = (String) rowMov.getAttribute("DATINIZIOAVV");
			if (dataInAvviamento == null) {
				dataInAvviamento = (String) rowMov.getAttribute("DATINIZIOMOV");
			}

			// Controllo che l'attributo non sia già presente in request, se è così
			// prima cancello il vecchio valore e poi ne inserisco uno nuovo
			if (request.containsAttribute("DATINIZIOAVV")) {
				request.delAttribute("DATINIZIOAVV");
			}
			request.setAttribute("DATINIZIOAVV", dataInAvviamento);

			boolean trasferimentoProtocollato = false;
			boolean movPrecedenteAggiornato = false;
			boolean trasferimentoInserito = false;
			Documento doc = null;

			// Inserisco la trasformazione
			// Scelgo la query di inserimento della trasformazione
			setSectionQueryInsert("QUERY_INSERT_TRA");
			trasferimentoInserito = doInsert(request, response, "PRGMOVTRA");

			if (trasferimentoInserito) {
				// Imposto nella request i dati mancanti necessari per la protocollazione:
				setProtSysdateValues(request);

				doc = new Documento();

				// Imposto il DefaultRequestContext da passare all'oggetto (modulo) trasferisciRamoAzienda:
				DefaultRequestContext drc = new DefaultRequestContext(getRequestContainer(), getResponseContainer());
				TrasferisciRamoAzienda trasferisciRamoAzienda = new TrasferisciRamoAzienda();
				((AbstractModule) trasferisciRamoAzienda).setRequestContext(drc);
				trasferimentoProtocollato = trasferisciRamoAzienda.protocolla(
						(BigDecimal) request.getAttribute("PRGMOVTRA"), "MVTRA", request, rowMov, doc, this.tex);
			}

			// Eseguo l'update del movimento precedente

			if (trasferimentoProtocollato) {
				movPrecedenteAggiornato = doUpdate(request, response);
			}
			// Salvataggio dei risultati
			if (!trasferimentoProtocollato || !movPrecedenteAggiornato) {

				// Se ho recuperato il movimento imposto il motivo del fallimento nel trasferimento
				if (rowMov != null) {
					if (!trasferimentoInserito) {
						rowMov.setAttribute("MOTIVOERRORE", MessageBundle
								.getMessage(String.valueOf(MessageCodes.TrasferimentoRamoAzienda.ERR_INSER_TRA)));
					} else if (!trasferimentoProtocollato) {
						rowMov.setAttribute("MOTIVOERRORE", MessageBundle
								.getMessage(String.valueOf(MessageCodes.TrasferimentoRamoAzienda.ERR_PROT_TRA)));
					} else if (!movPrecedenteAggiornato) {
						rowMov.setAttribute("MOTIVOERRORE", MessageBundle
								.getMessage(String.valueOf(MessageCodes.TrasferimentoRamoAzienda.ERR_UPDATE_PREC)));
					}
					rows.setAttribute(rowMov);
				}
			} else {
				esitoInsMov = true;
			}
		}

		rows.setAttribute("CURRENT_PAGE", new Integer(1));
		rows.setAttribute("NUM_PAGES", new Integer(1));
		rows.setAttribute("NUM_RECORDS", new Integer(rows.getAttributeAsVector("ROW").size()));
		rows.setAttribute("ROWS_X_PAGE", new Integer(2147483647));
		response.setAttribute(result);

		return esitoInsMov;
	}

	/**
	 * Metodo per inserire il nuovo movimento in AM_MOVIMENTO_APPOGGIO
	 * 
	 * @return il prgMovimento del movimento appena inserito, o stringa vuota in caso di errore
	 * @throws Exception
	 */
	private String insAvviamentoFittizio(HashMap datiTestata, TreeMap sedeAttuale, TreeMap sedePrecedente,
			TreeMap nuovaSede, HashMap datiLavoratore, HashMap contrattoLavoratore) throws Exception {
		String prgMovApp = "";

		RequestContainer rc = RequestContainer.getRequestContainer();
		SessionContainer sc = rc.getSessionContainer();
		BigDecimal codiceUtente = (BigDecimal) sc.getAttribute(Values.CODUTENTE);

		SourceBean nuovoMovimento = null;

		nuovoMovimento = getUniSilSourceBean(datiTestata, sedeAttuale, sedePrecedente, nuovaSede, datiLavoratore,
				contrattoLavoratore);

		// Uso la funzionalità già esistente in InviaMigrazioni per inserire il nuovo movimento in AM_MOVIMENTO_APPOGGIO
		InviaMigrazioni inviaMigrazioni = new InviaMigrazioni();
		// PASSO IL TRANSACTION QUERY EXECUTOR
		SourceBean result = inviaMigrazioni.inserisciMovimento(nuovoMovimento, codiceUtente, "MOVIMENTO", this.tex);

		String resultResponse = Utils.notNull(result.getAttribute("RESPONSE"));
		if (resultResponse.equalsIgnoreCase("OK")) {
			// Recupero il PRGMOVIMENTOAPP per la validazione massiva (per l'inserimento in AM_MOVIMENTO)
			prgMovApp = (String) sc.getAttribute("PRGMOVIMENTOAPP");
		}

		return prgMovApp;
	}

	/**
	 * Contruisco il SourceBean UniSil a partire dal Vardatori.xml:
	 * 
	 * @return il SourceBean che rappresenta il documento xml in formato unisil utilizzato dal SIL
	 * @throws Exception
	 */
	private static SourceBean getUniSilSourceBean(HashMap datiTestata, TreeMap sedeAttuale, TreeMap sedePrecedente,
			TreeMap nuovaSede, HashMap datiLavoratore, HashMap contrattoLavoratore) throws Exception {

		SourceBean mov = null;
		// mappatura comunicazione obbligatoria vardatori - com. unisil
		mov = TrasferimentoRamoAzUtils.getUniSilMovimentoAvv(datiTestata, sedePrecedente, datiLavoratore,
				contrattoLavoratore);

		if (_logger.isEnabledFor(Level.DEBUG))
			_logger.debug(mov.toString());

		SourceBean result = new SourceBean("MOVIMENTI");
		result.setAttribute(mov);

		result = TrasferimentoRamoAzUtils.preparaSourceBean(result);

		return result;
	}

	private String insRettifica_annullamento_appoggio(HashMap datiTestata, TreeMap nuovaSede, TreeMap sedePrecedente,
			TrasferimentoRamoAzHandler trasfRamoAzHandlerCF) throws Exception {

		String prgMovApp = "";
		SourceBean nuovoMovimento = new SourceBean("MOVIMENTI");

		HashMap datiLavoratore = trasfRamoAzHandlerCF.getDatiLavCF();
		HashMap contrattoLavoratore = trasfRamoAzHandlerCF.getContrattoLavCF();

		RequestContainer rc = RequestContainer.getRequestContainer();
		SessionContainer sc = rc.getSessionContainer();
		BigDecimal codiceUtente = (BigDecimal) sc.getAttribute(Values.CODUTENTE);

		SourceBean mov = null;
		// se si tratta di una rettifica devo prima creare il movimento di avviamento fittizio
		boolean pippo = false;
		// TODO query di controllo moviemnto avviamento fittizio
		mov = TrasferimentoRamoAzUtils.getUniSilMovimentoAvv(datiTestata, sedePrecedente, datiLavoratore,
				contrattoLavoratore);

		if (_logger.isEnabledFor(Level.DEBUG))
			_logger.debug(mov.toString());

		nuovoMovimento.setAttribute(mov);

		// mappatura comunicazione obbligatoria vardatori - com. unisil
		mov = TrasferimentoRamoAzUtils.getUniSilMovimentoTra(datiTestata, nuovaSede, sedePrecedente, datiLavoratore,
				contrattoLavoratore);

		if (_logger.isEnabledFor(Level.DEBUG))
			_logger.debug(mov.toString());

		nuovoMovimento.setAttribute(mov);

		nuovoMovimento = TrasferimentoRamoAzUtils.preparaSourceBean(nuovoMovimento);

		// Uso la funzionalità già esistente in InviaMigrazioni per inserire il nuovo movimento in AM_MOVIMENTO_APPOGGIO
		InviaMigrazioni inviaMigrazioni = new InviaMigrazioni();
		// PASSO IL TRANSACTION QUERY EXECUTOR
		SourceBean result = inviaMigrazioni.inserisciMovimento(nuovoMovimento, codiceUtente, "MOVIMENTO", this.tex);

		String resultResponse = Utils.notNull(result.getAttribute("RESPONSE"));
		if (resultResponse.equalsIgnoreCase("OK")) {
			// Recupero il PRGMOVIMENTOAPP per la validazione massiva (per l'inserimento in AM_MOVIMENTO)
			prgMovApp = (String) sc.getAttribute("PRGMOVIMENTOAPP");
		}
		// TODO aggiornare il campo FLGASSDACESS del movimento di avviamento fittizio
		return prgMovApp;

	}

	private SourceBean getMovimentoAperto(String codFiscLavoratore, String codFiscAzPrecedente,
			String dataInzioTrasferim) throws Exception {
		ArrayList statementParams = new ArrayList();

		statementParams.add(codFiscLavoratore); // CF lav.
		statementParams.add(codFiscAzPrecedente); // CF az. prec.
		statementParams.add(dataInzioTrasferim);
		statementParams.add(dataInzioTrasferim);

		// Recupero il movimento aperto per il lavoratore
		SourceBean movimentoAperto = (SourceBean) this.tex.executeQuery("TRA_GET_MOVIMENTO_APERTO",
				statementParams.toArray(), "SELECT");
		return movimentoAperto;
	}

	private SourceBean getMovimentoApertoInOgniAz(String codFiscLavoratore, String dataInzioTrasferim)
			throws Exception {
		ArrayList statementParams = new ArrayList();

		statementParams.add(codFiscLavoratore); // CF lav.
		statementParams.add(dataInzioTrasferim);
		statementParams.add(dataInzioTrasferim);

		// Recupero il movimento aperto per il lavoratore
		SourceBean movimentoAperto = StatementUtils.getSourceBeanByStatement("TRA_GET_MOV_APERTO_IN_OGNI_AZ",
				statementParams);

		return movimentoAperto;
	}

	private void setMovimentoAttributes(SourceBean request, SourceBean movimentoAperto, TreeMap nuovaSede,
			HashMap datiTestata, HashMap contrattoLavoratore) throws SourceBeanException {
		String prgMovimentoPrec = Utils
				.notNull(movimentoAperto.getAttribute("ROW." + TrasferimentoRamoAzRequestParams.PRGMOVIMENTO));
		request.delAttribute(TrasferimentoRamoAzRequestParams.PRGMOVIMENTO);
		request.setAttribute(TrasferimentoRamoAzRequestParams.PRGMOVIMENTO, prgMovimentoPrec);

		/* **************** Imposto a mano il "codice motivo cessazione": ********** */
		request.delAttribute(TrasferimentoRamoAzRequestParams.CODMVCESSAZIONE);
		request.setAttribute(TrasferimentoRamoAzRequestParams.CODMVCESSAZIONE, "T1");

		String codMonoMovDich = Utils
				.notNull(movimentoAperto.getAttribute("ROW." + TrasferimentoRamoAzRequestParams.CODMONOMOVDICH));
		request.delAttribute(TrasferimentoRamoAzRequestParams.CODMONOMOVDICH);
		request.setAttribute(TrasferimentoRamoAzRequestParams.CODMONOMOVDICH, codMonoMovDich);

		String numGGTraMovComunicazione = Utils.notNull(
				movimentoAperto.getAttribute("ROW." + TrasferimentoRamoAzRequestParams.NUMGGTRAMOVCOMUNICAZIONE));
		request.delAttribute(TrasferimentoRamoAzRequestParams.NUMGGTRAMOVCOMUNICAZIONE);
		request.setAttribute(TrasferimentoRamoAzRequestParams.NUMGGTRAMOVCOMUNICAZIONE, numGGTraMovComunicazione);

		BigDecimal numKloMov = (BigDecimal) movimentoAperto
				.getAttribute("ROW." + TrasferimentoRamoAzRequestParams.NUMKLOMOV);

		if (!prgMovimentoPrec.equals("") && numKloMov != null) {
			request.delAttribute(TrasferimentoRamoAzRequestParams.NUMKLOMOV);
			request.setAttribute(TrasferimentoRamoAzRequestParams.NUMKLOMOV, numKloMov.toString());
		}
		String indirizzo = (String) nuovaSede.get(TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[1]);
		String ragioneSociale = (String) datiTestata.get(
				TrasferimentoRamoAzXmlConst.RAGIONE_SOCIALE_DATORE + TrasferimentoRamoAzXmlConst.SUFFIX_AZ_ATTUALE);
		if (indirizzo == null)
			indirizzo = "Non indicato";
		String strEnteRilascio = ragioneSociale + " - " + indirizzo;
		if (strEnteRilascio.length() > 200)
			strEnteRilascio = strEnteRilascio.substring(0, 200);
		request.updAttribute("STRENTERILASCIO", strEnteRilascio);

		String codTipoTrasf = (String) datiTestata.get(TrasferimentoRamoAzXmlConst.CODICE_TRASFERIMENTO);
		if (codTipoTrasf != null)
			request.updAttribute(TrasferimentoRamoAzRequestParams.CODTIPOTRASF, codTipoTrasf);

		String codTipoContratto = (String) contrattoLavoratore.get(TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO[0]);
		if (codTipoContratto != null)
			request.updAttribute(TrasferimentoRamoAzRequestParams.CODTIPOCONTRATTO, codTipoContratto);

		String codSoggetto = (String) datiTestata.get(TrasferimentoRamoAzXmlConst.TIPO_DELEGATO);
		if (codSoggetto != null)
			request.updAttribute(TrasferimentoRamoAzRequestParams.CODSOGGETTO, codSoggetto);

		String codEnte = (String) contrattoLavoratore.get(TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO[2]);
		if (codEnte != null)
			request.updAttribute(TrasferimentoRamoAzRequestParams.CODENTE, codEnte);

		String strCodiceEntePrev = (String) contrattoLavoratore.get(TrasferimentoRamoAzXmlConst.ATTRIBUTI_CONTRATTO[5]);
		if (strCodiceEntePrev != null)
			request.updAttribute(TrasferimentoRamoAzRequestParams.STRCODICEENTEPREV, strCodiceEntePrev);

		String codComunicazione = (String) datiTestata.get(TrasferimentoRamoAzXmlConst.CODICE_COMUNICAZIONE);
		if (codComunicazione != null)
			request.updAttribute(TrasferimentoRamoAzRequestParams.CODCOMUNICAZIONE, codComunicazione);
	}

	private void setProtSysdateValues(SourceBean request) throws SourceBeanException, EMFInternalError {
		SourceBean protSysdateValues = StatementUtils.getSourceBeanByStatement("TRA_GET_SYSDATE_VALUES");

		String dataProt = Utils.notNull(protSysdateValues.getAttribute("ROW.DATA"));
		request.delAttribute(TrasferimentoRamoAzRequestParams.DATA_PROT);
		request.setAttribute(TrasferimentoRamoAzRequestParams.DATA_PROT, dataProt);

		String numAnnoProt = Utils.notNull(protSysdateValues.getAttribute("ROW.ANNO"));
		request.delAttribute(TrasferimentoRamoAzRequestParams.NUM_ANNO_PROT);
		request.setAttribute(TrasferimentoRamoAzRequestParams.NUM_ANNO_PROT, numAnnoProt);

		String oraProt = Utils.notNull(protSysdateValues.getAttribute("ROW.ORA"));
		request.delAttribute(TrasferimentoRamoAzRequestParams.ORA_PROT);
		request.setAttribute(TrasferimentoRamoAzRequestParams.ORA_PROT, oraProt);

		request.delAttribute(TrasferimentoRamoAzRequestParams.NUM_PROT);
		request.setAttribute(TrasferimentoRamoAzRequestParams.NUM_PROT, "0");

		request.delAttribute(TrasferimentoRamoAzRequestParams.TIPO_PROT);
		request.setAttribute(TrasferimentoRamoAzRequestParams.TIPO_PROT, "S");
	}

	private ArrayList getUnitaAziendaleParams(String prgAzienda, TreeMap sede) {
		String codcom = (String) sede.get(TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[0]);
		String indirizzo = (String) sede.get(TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[1]);
		String cap = (String) sede.get(TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[2]);
		ArrayList params = new ArrayList();
		params.add(prgAzienda);
		params.add(indirizzo);
		params.add(codcom);
		params.add(cap);

		return params;
	}

	/**
	 * Metodo che setta l'attributo RESULT.ROWS.ROW.MOTIVOERRORE (nella response). Notare che la response può contenere
	 * più elementi RESULT, per questo motivo mi scorro il Vector degli attributi RESULT:
	 */
	private void setMotivoerrore(SourceBean sb, String value) throws SourceBeanException {
		/*
		 * boolean inserito = false; Vector resultVector = sb.getAttributeAsVector("RESULT"); for (int i=0;
		 * i<resultVector.size(); i++) { SourceBean resultSb = (SourceBean) resultVector.get(i); if
		 * (resultSb.containsAttribute("ROWS.ROW.MOTIVOERRORE")) { resultSb.setAttribute("ROWS.ROW.MOTIVOERRORE",
		 * value); inserito = true; } } if (!inserito) { sb.setAttribute("RESULT.ROWS.ROW.MOTIVOERRORE", value); }
		 */
		sb.setAttribute("MOTIVO_ERRORE_TRASF", value);
	}

	private TreeMap getUnitaAzXNuovoMov(String codFiscAzienda, String codFiscLavoratore) throws EMFInternalError {
		TreeMap unita = new TreeMap();
		ArrayList arr = new ArrayList();
		arr.add(codFiscLavoratore);
		arr.add(codFiscAzienda);
		arr.add(codFiscLavoratore);
		arr.add(codFiscAzienda);
		SourceBean sb = StatementUtils.getSourceBeanByStatement("TRA_GET_UNITAAZ_PER_NUOVO_MOV", arr);

		if (sb.containsAttribute("ROW")) {
			for (int i = 0; i < TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE.length; i++) {
				String sbValue = Utils.notNull(sb.getAttribute(
						"ROW." + StringUtils.replace(TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[i], "-", "")));
				if (!sbValue.equals("")) {
					unita.put(TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[i], sbValue);
				}
			}
			for (int i = 0; i < TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI.length; i++) {
				String sbValue = Utils.notNull(sb.getAttribute(
						"ROW." + StringUtils.replace(TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI[i], "-", "")));
				if (!sbValue.equals("")) {
					unita.put(TrasferimentoRamoAzXmlConst.ELEMENTI_RECAPITI[i], sbValue);
				}
			}

			return unita;
		}

		return null;
	}

	/**
	 * Controllo esistenza dell'unità aziendale e in caso negativo la inserisco. Tale metodo ritorna il progressivo
	 * dell'unità aziendale.
	 * 
	 * @return il prgUnita dell'unita' azienda esistente o inserita, stringa vuota in caso di errore
	 */
	private String ricerca_unita(String statementName, String prgAzienda, TreeMap unita, SourceBean request,
			SourceBean response) throws Exception {
		ArrayList unitaAziendaleParams = getUnitaAziendaleParams(prgAzienda, unita);
		String prg = StatementUtils.getStringByStatement(statementName, unitaAziendaleParams);
		return prg;
	}

	private String inserimento_unita(String statementName, TrasferimentoRamoAzHandler azHandler, TreeMap unita,
			SourceBean request, SourceBean response) throws Exception {
		String prgAzienda = (String) request.getAttribute(TrasferimentoRamoAzRequestParams.PRGAZIENDADESTINAZIONE);
		ArrayList unitaAziendaleParams = getUnitaAziendaleParams(prgAzienda, unita);
		String prg = "";
		TrasferimentoRamoAzUtils.setUnitaParamsInRequest(request, unita, azHandler.getCodFiscaleAzAttuale());

		DefaultRequestContext drc = new DefaultRequestContext(getRequestContainer(), getResponseContainer());
		ModuleIFace moduloInsertUnitaAzienda = ModuleFactory.getModule("M_InsertUnitaAzienda");
		((AbstractModule) moduloInsertUnitaAzienda).setRequestContext(drc);
		((AbstractSimpleModule) moduloInsertUnitaAzienda).enableTransactions(this.tex);

		/* ************ CHIAMATA AL MODULO ************ */
		moduloInsertUnitaAzienda.service(request, response);

		String insertOk = Utils.notNull(response.getAttribute("INSERT_OK"));
		if (insertOk.equalsIgnoreCase("TRUE")) { // Se è andato a buon fine l'inserimento dell'unità aziendale
			prg = StatementUtils.getStringByStatement(this.tex, statementName, unitaAziendaleParams.toArray());
		}
		return prg;
	}

	private String getDatiPrincipaliLav(HashMap datiLav, String codFiscLav) {
		return (String) datiLav.get(TrasferimentoRamoAzXmlConst.ELEMENTI_DATILAVORATORE[1]) + " "
				+ (String) datiLav.get(TrasferimentoRamoAzXmlConst.ELEMENTI_DATILAVORATORE[0]) + " c.f. " + codFiscLav;
	}

	private String getDatiPrincipaliAz(SourceBean movAperto) {
		return Utils.notNull(movAperto.getAttribute("ROW.RagioneSocialeAzienda")) + " c.f. "
				+ Utils.notNull(movAperto.getAttribute("ROW.CodiceFiscaleAzienda")) + " cap "
				+ Utils.notNull(movAperto.getAttribute("ROW.STRCAP")) + " comune "
				+ Utils.notNull(movAperto.getAttribute("ROW.comune")) + " indirizzo "
				+ Utils.notNull(movAperto.getAttribute("ROW.STRINDIRIZZO")) + " prgAzienda "
				+ Utils.notNull(movAperto.getAttribute("ROW.prgAzienda")) + " prgUnita "
				+ Utils.notNull(movAperto.getAttribute("ROW.prgUnita"));
	}

	private String getDatiPrincipaliSede(TreeMap datiSede) {
		return " Comune: " + (String) datiSede.get(TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[0]) + " Indirizzo: "
				+ (String) datiSede.get(TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[1]) + " cap: "
				+ (String) datiSede.get(TrasferimentoRamoAzXmlConst.ELEMENTI_SEDE[2]);
	}
}
