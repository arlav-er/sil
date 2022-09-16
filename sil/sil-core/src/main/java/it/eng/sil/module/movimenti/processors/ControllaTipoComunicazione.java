package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.LogMovimentoAppoggio;
import it.eng.sil.module.movimenti.M_MovSalvaConsultaGen;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.constant.DeTipoContrattoConstant;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.module.movimenti.enumeration.TipoTrasfEnum;
import it.eng.sil.security.TransactionProfileDataFilter;
import it.eng.sil.security.User;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.MovimentoBean;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleManager;

public class ControllaTipoComunicazione implements RecordProcessor {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(ControllaTipoComunicazione.class.getName());
	private String name;
	private String classname = this.getClass().getName();
	private TransactionQueryExecutor trans;
	private SourceBean request = null;
	private SourceBean response = null;
	SessionContainer sessione = null;
	RequestContainer reqCont = null;
	private boolean checkForzaValidazione = false;
	private int code = 0;
	private static final int erroreMobilita = -2;
	private static final int erroreControlli = -3;
	private String codiceFiscaleLav = "";
	private String codTipoMov = "";
	private String dataInizioMov = "";
	private boolean settatoMovimentoRett = false;
	private boolean varDatori = false;
	private SourceBean infoGenerale = null;

	public ControllaTipoComunicazione(String name, SourceBean req, SourceBean res, SessionContainer ses,
			RequestContainer reqCont, TransactionQueryExecutor transexec) throws Exception {
		this.name = name;
		this.trans = transexec;
		this.request = req;
		this.reqCont = reqCont;
		this.sessione = ses;
		this.response = res;
		checkForzaValidazione = ProcessorsUtils.checkForzaValidazione(trans);
		// LETTURA DALLA TS_GENERALE
		this.infoGenerale = DBLoad.getInfoGenerali();
	}

	public SourceBean processRecord(Map record) throws SourceBeanException {
		// vettore delle warnings da restituire
		ArrayList warnings = new ArrayList();
		// Vettore dei risultati annidati da restituire
		ArrayList nested = new ArrayList();
		// Se il record è nullo non lo posso elaborare, ritorno l'errore
		if (record == null) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.ERR_INSERT_DATA),
					"Record da elaborare nullo.", null, null);
		}
		String dettaglioErrore = "";
		SourceBean result = null;
		Object resultDel = null;
		String selectquery = "";
		String deletequery = "";
		Object cdnLav = null;
		Vector v = null;
		SourceBean rowMovComunicazPrec = null;
		Object prgMovComunicazPrec = null;
		Object prgMovComunicazPrecAssCVE = null;
		Object prgMovSuccComunicazPrec = null;
		String codTipoComunic = null;
		SourceBean rowDoc = null;
		BigDecimal prgMovimentoDaAnnullare = null;
		Vector vMovAnnullare = null;

		String contestoVal = record.get("CONTEXT") != null ? record.get("CONTEXT").toString() : "";
		try {
			codTipoComunic = (String) record.get("CODTIPOCOMUNIC");
			String codComunicazione = (String) record.get("CODCOMUNICAZIONEPREC");
			String codComunicazioneCurr = (String) record.get("CODCOMUNICAZIONE");
			String codfisc = record.get("STRCODICEFISCALE") != null ? record.get("STRCODICEFISCALE").toString() : "";
			String codTipoMovVal = record.get("CODTIPOMOV") != null ? record.get("CODTIPOMOV").toString() : "";
			Object prgMovApp = record.get("PRGMOVIMENTOAPP");
			Object prgMovAppCVE = record.get("PRGMOVIMENTOAPPCVE");
			String codTipoAss = record.get("CODTIPOASS") != null ? (String) record.get("CODTIPOASS") : "";
			if (codTipoComunic != null && (codTipoComunic.equals(MessageCodes.General.ANNULLAMENTO_COMUNICAZIONE_PREC)
					|| codTipoComunic.equals(MessageCodes.General.ANNULLAMENTO_DA_UFFICIO_COMUNICAZIONE_PREC)
					|| codTipoComunic.equals(MessageCodes.General.RETTIFICA_COMUNICAZIONE_PREC))) {

				if ((codTipoComunic.equals(MessageCodes.General.RETTIFICA_COMUNICAZIONE_PREC))
						&& (contestoVal.equalsIgnoreCase("valida") || contestoVal.equalsIgnoreCase("validaArchivio"))) {

					SourceBean movApp = (SourceBean) request.getAttribute("M_MovGetDettInizialeMovApp.ROWS.ROW");
					if (movApp == null) {
						dettaglioErrore = "E' stato cancellato il movimento di rettifica in mancanza di competenza amministrativa";
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_CODCOMUNICAZ_NOT_FOUND), dettaglioErrore,
								warnings, nested);
					}
				}

				if (codComunicazione == null || codComunicazione.equals("")) {
					if (codTipoComunic.equals(MessageCodes.General.ANNULLAMENTO_COMUNICAZIONE_PREC)
							|| codTipoComunic.equals(MessageCodes.General.ANNULLAMENTO_DA_UFFICIO_COMUNICAZIONE_PREC)) {
						dettaglioErrore = "Non è possibile proseguire nell'annullamento della comunicazione precedentemente inviata.";
					} else {
						dettaglioErrore = "Non è possibile proseguire nella rettifica della comunicazione precedentemente inviata.";
					}
					return ProcessorsUtils.createResponse(name, classname,
							new Integer(MessageCodes.ImportMov.ERR_CODCOMUNICAZ_NOT_FOUND), dettaglioErrore, warnings,
							nested);
				} else {
					String contesto = "";
					LogMovimentoAppoggio logMov = new LogMovimentoAppoggio(reqCont, trans);
					if (codTipoComunic.equals(MessageCodes.General.ANNULLAMENTO_COMUNICAZIONE_PREC)
							|| codTipoComunic.equals(MessageCodes.General.ANNULLAMENTO_DA_UFFICIO_COMUNICAZIONE_PREC)) {
						contesto = "Contesto:annullamento della comunicazione precedente.";
					} else {
						contesto = "Contesto:rettifica della comunicazione precedente.";
					}
					// Query per la ricerca del movimento da annullare o rettificare nella tabella AM_MOVIMENTO
					selectquery = "SELECT MOV1.PRGMOVIMENTO, MOV1.PRGMOVIMENTOSUCC , MOV2.PRGMOVIMENTO PRGMOVIMENTOPREC, MOV1.CDNLAVORATORE, "
							+ "MOV1.CODTIPOMOV, MOV2.CODTIPOMOV CODTIPOMOVPREC, MOV2.CODMOTANNULLAMENTO, "
							+ "LAV.STRCODICEFISCALE, TO_CHAR(MOV1.DATINIZIOMOV,'DD/MM/YYYY') DATINIZIOMOV, MOV1.NUMKLOMOV, AZ.CODTIPOAZIENDA, "
							+ "MOV2.NUMKLOMOV NUMKLOMOVPREC, MOV1.CODTIPOCONTRATTO, "
							+ "MOV1.FLGINTERASSPROPRIA, MOV1.PRGAZIENDAUTILIZ, TO_CHAR(MOV1.DATINIZIORAPLAV,'DD/MM/YYYY') DATINIZIORAPLAV "
							+ "FROM AM_MOVIMENTO MOV1, AM_MOVIMENTO MOV2, AN_LAVORATORE LAV, AN_AZIENDA AZ "
							+ "WHERE MOV1.PRGAZIENDA = AZ.PRGAZIENDA AND MOV1.CDNLAVORATORE = LAV.CDNLAVORATORE "
							+ "AND MOV1.PRGMOVIMENTOPREC = MOV2.PRGMOVIMENTO (+) AND MOV1.CODSTATOATTO = 'PR' "
							+ "AND ( (MOV1.CODTIPOMOV <> 'AVV') OR (MOV1.CODTIPOMOV = 'AVV' AND NVL(MOV1.CODMOTANNULLAMENTO, ' ') NOT IN ('CVE', 'PVE', 'TVE')) ) "
							+ "AND MOV1.CODCOMUNICAZIONE = '" + codComunicazione + "' ORDER BY MOV1.DATINIZIOMOV DESC";

					result = null;
					try {
						result = ProcessorsUtils.executeSelectQuery(selectquery, trans);
					} catch (Exception e) {
						return ProcessorsUtils.createResponse(name, classname,
								new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
								"Impossibile cercare il movimento con codcomunicazione = " + codComunicazione, warnings,
								nested);
					}
					v = result.getAttributeAsVector("ROW");
					if (v.size() == 0) {
						// nomeTabella = tabella dove è stata trovata la comunicazione precedente da annullare o
						// rettificare
						// nel caso in cui non si trova nella tabella AM_MOVIMENTO
						String nomeTabella = "";
						// Query per la ricerca del movimento da annullare o
						// rettificare nella tabella AM_MOVIMENTO_APPOGGIO
						selectquery = "SELECT PRGMOVIMENTOAPP, PRGMOVIMENTOAPPCVE, CODTIPOCONTRATTO "
								+ "FROM AM_MOVIMENTO_APPOGGIO WHERE PRGMOVIMENTOAPP <> " + prgMovApp
								+ " AND NVL(FLGASSDACESS, 'N') <> 'S' AND CODCOMUNICAZIONE = '" + codComunicazione
								+ "'";
						result = null;
						try {
							result = ProcessorsUtils.executeSelectQuery(selectquery, trans);
						} catch (Exception e) {
							return ProcessorsUtils.createResponse(name, classname,
									new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
									"Impossibile cercare il movimento con codcomunicazione = " + codComunicazione,
									warnings, nested);
						}
						v = result.getAttributeAsVector("ROW");
						if (v.size() == 0) {
							if (checkForzaValidazione) {
								// devo ripetere la ricerca anche nella tabella
								// di archivio AM_MOV_APP_ARCHIVIO
								selectquery = "SELECT PRGMOVIMENTOAPP, PRGMOVIMENTOAPPCVE, CODTIPOCONTRATTO "
										+ "FROM AM_MOV_APP_ARCHIVIO WHERE PRGMOVIMENTOAPP <> " + prgMovApp
										+ " AND NVL(FLGASSDACESS, 'N') <> 'S' AND CODCOMUNICAZIONE = '"
										+ codComunicazione + "'";
								result = null;
								try {
									result = ProcessorsUtils.executeSelectQuery(selectquery, trans);
								} catch (Exception e) {
									return ProcessorsUtils.createResponse(name, classname,
											new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
											"Impossibile cercare il movimento con codcomunicazione = "
													+ codComunicazione,
											warnings, nested);
								}
								v = result.getAttributeAsVector("ROW");
								if (v.size() > 0) {
									nomeTabella = "AM_MOV_APP_ARCHIVIO";
								}
							}
						} else {
							nomeTabella = "AM_MOVIMENTO_APPOGGIO";
						}
						if (v.size() == 0) {
							String dettaglio = "";
							// se codTipoComunic = "04", in questo caso bisogna
							// eliminare solo il movimento
							// che si stava validando dalla tabella di appoggio
							// se codTipoComunic = "03", allora bisogna
							// effettuare la validazione del movimento
							if (codTipoComunic.equals(MessageCodes.General.ANNULLAMENTO_COMUNICAZIONE_PREC)
									|| codTipoComunic
											.equals(MessageCodes.General.ANNULLAMENTO_DA_UFFICIO_COMUNICAZIONE_PREC)) {
								dettaglio = "La ricerca è stata effettuata nella lista dei movimenti e nella lista dei movimenti da validare per effettuare l'annullamento della comunicazione precedente.";
								return ProcessorsUtils.createResponse(name, classname,
										new Integer(MessageCodes.ImportMov.ERR_CODCOMUNICAZ_NOT_FOUND), dettaglio,
										warnings, nested);
							} else {
								dettaglio = "La ricerca è stata effettuata nella lista dei movimenti e nella lista dei movimenti da validare per effettuare la rettifica della comunicazione precedente.";
								warnings.add(new Warning(MessageCodes.ImportMov.WAR_CODCOMUNICAZ_NOT_FOUND, dettaglio));
							}
						} else {
							if (v.size() >= 1) {
								String prgMovDaCanc = "";
								String codTipoContrattoPrec = "";
								// in questo caso bisogna eliminare il movimento
								// con codComunicazione trovato
								// nella tabella "nomeTabella"
								if (logMov != null) {
									logMov.loadColonneTabella(nomeTabella);
								}
								boolean contrattoApprendistatoModificato = false;
								for (int i = 0; i < v.size(); i++) {
									rowMovComunicazPrec = (SourceBean) v.get(i);
									prgMovComunicazPrec = rowMovComunicazPrec.getAttribute("PRGMOVIMENTOAPP");
									prgMovComunicazPrecAssCVE = rowMovComunicazPrec.getAttribute("PRGMOVIMENTOAPPCVE");
									codTipoContrattoPrec = rowMovComunicazPrec.containsAttribute("CODTIPOCONTRATTO")
											? rowMovComunicazPrec.getAttribute("CODTIPOCONTRATTO").toString()
											: "";
									if (prgMovDaCanc.equals("")) {
										prgMovDaCanc = prgMovComunicazPrec.toString();
									} else {
										prgMovDaCanc = prgMovDaCanc + "," + prgMovComunicazPrec.toString();
									}
									if (prgMovComunicazPrecAssCVE != null) {
										prgMovDaCanc = prgMovDaCanc + "," + prgMovComunicazPrecAssCVE.toString();
									}
									// Nel caso di annullamento, il movimento che si sta cancellando (comunicazione
									// precedente)
									// lo memorizzo nella tabella di log (LG_AM_MOVIMENTO_APPOGGIO)
									if (logMov != null) {
										try {
											logMov.lg_Movimento_App_Cancellato(prgMovComunicazPrec);
										} catch (Exception error) {
											it.eng.sil.util.TraceWrapper.debug(_logger,
													"ControllaTipoComunicazione::processRecord(): query di inserimento log fallita!",
													(Exception) error);
										}
									}
									if (!contrattoApprendistatoModificato && codTipoComunic
											.equals(MessageCodes.General.RETTIFICA_COMUNICAZIONE_PREC)) {
										contrattoApprendistatoModificato = checkRettificheApprendistato(codTipoAss,
												codTipoContrattoPrec, codTipoMovVal, warnings, record);
									}
								}
								deletequery = "DELETE FROM " + nomeTabella + " WHERE PRGMOVIMENTOAPP in ("
										+ prgMovDaCanc + ")";
								try {
									resultDel = trans.executeQueryByStringStatement(deletequery, null,
											TransactionQueryExecutor.DELETE);
								} catch (EMFInternalError error) {
									it.eng.sil.util.TraceWrapper.debug(_logger,
											"ControllaTipoComunicazione::processRecord(): query di rimozione del movimento fallita!",
											(Exception) error);

									return ProcessorsUtils.createResponse(name, classname,
											new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
											"Impossibile rimuovere i movimenti trovati con quel codice comunicazione dalla lista dei movimenti da validare.",
											warnings, nested);
								}
								// Esamino il risultato
								if (!((resultDel instanceof Boolean)
										&& (((Boolean) resultDel).booleanValue() == true))) {
									it.eng.sil.util.TraceWrapper.debug(_logger,
											"ControllaTipoComunicazione::processRecord(): query di rimozione del movimento fallita!",
											(Exception) resultDel);

									return ProcessorsUtils.createResponse(name, classname,
											new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
											"Impossibile rimuovere il movimento trovato con quel codice comunicazione dalla lista dei movimenti da validare.",
											warnings, nested);
								}
								warnings.add(new Warning(MessageCodes.ImportMov.WAR_MOV_APP_CODCOMUNICAZPREC_CANC,
										contesto));
							}
						}
						// Se non è stato annullato nessun movimento nella tabella AM_MOVIMENTO e vengo dalla
						// validazione manuale, faccio visualizzare la lista dei movimenti da validare.
						if (response != null
								&& (codTipoComunic.equals(MessageCodes.General.ANNULLAMENTO_COMUNICAZIONE_PREC)
										|| codTipoComunic.equals(
												MessageCodes.General.ANNULLAMENTO_DA_UFFICIO_COMUNICAZIONE_PREC))
								&& (contestoVal.equalsIgnoreCase("valida")
										|| contestoVal.equalsIgnoreCase("validaArchivio"))) {
							response.setAttribute("VISLISTARISMOVVALIDARE", "TRUE");
						}
					} else {
						// Comunicazione da annullare o rettificare si trova nella tabella AM_MOVIMENTO
						// In questo caso bisogna procedere all'annullamento o alla rettifica di una comunicazione
						// precedente
						// (nel caso si tratti di una cessazione (oppure proroga o trasformazione) veloce,
						// bisogna annullare o rettificare anche l'eventuale AVV)

						// Controllo se si tratta di un varDatori (v.size > 1 e sono tutte trasformazioni o avviamenti
						// fittizi
						// (solo in quel caso avviamento e trasformazione possono avere lo stesso codice comunicazione))
						if (v.size() > 1) {
							boolean exitTipoMov = false;
							if (codTipoMovVal.equalsIgnoreCase("TRA")) {
								setVarDatori(checkVarDatori(v));
							} else {
								String codTipoMovCom = "";
								SourceBean rowPrimoMov = (SourceBean) v.get(0);
								codTipoMovCom = rowPrimoMov.getAttribute("CODTIPOMOV") != null
										? rowPrimoMov.getAttribute("CODTIPOMOV").toString()
										: "";
								for (int i = 1; (i < v.size()) && (!exitTipoMov); i++) {
									SourceBean rowMov = (SourceBean) v.get(i);
									String codTipoMov = rowMov.getAttribute("CODTIPOMOV") != null
											? rowMov.getAttribute("CODTIPOMOV").toString()
											: "";
									if (!codTipoMov.equalsIgnoreCase(codTipoMovCom)) {
										exitTipoMov = true;
									}
								}
							}
							if (!getVarDatori()) {
								if (exitTipoMov) {
									// segnalo errore
									return ProcessorsUtils.createResponse(name, classname,
											new Integer(MessageCodes.ImportMov.ERR_MOV_DOPPI_CODCOMUNICAZ),
											"L'errore si è verificato durante la ricerca nella lista dei movimenti.",
											warnings, nested);
								}
							}
							vMovAnnullare = carciaMovimentiDaAnnullare(v);
						} else {
							// MODIFICA 24/10/2008 (ANNULLAMENTO E RETTIFICA DA BLOCCARE SE IL MOVIMENTO E' DI
							// SOMMINISTRAZIONE
							// ED HA PIU' DI UNA MISSIONE. Il vettore v in questo caso contiene un solo movimento)
							try {
								boolean bloccaOperazione = controllaMovimentoSomministrazione(v);
								if (bloccaOperazione) {
									if (codTipoComunic.equals(MessageCodes.General.ANNULLAMENTO_COMUNICAZIONE_PREC)
											|| codTipoComunic.equals(
													MessageCodes.General.ANNULLAMENTO_DA_UFFICIO_COMUNICAZIONE_PREC)) {
										return ProcessorsUtils.createResponse(name, classname,
												new Integer(MessageCodes.ImportMov.ERR_ANNULLAMENTO_COMUNICAZ_PREC),
												"Non è possibile annullare un movimento avente più di una missione al suo interno. Intervenire manualmente sui movimenti in banca dati",
												warnings, nested);
									} else {
										return ProcessorsUtils.createResponse(name, classname,
												new Integer(MessageCodes.ImportMov.ERR_RETTIFICA_COMUNICAZ_PREC),
												"Non è possibile annullare un movimento avente più di una missione al suo interno. Intervenire manualmente sui movimenti in banca dati",
												warnings, nested);
									}
								}
								SourceBean rowMovAnnullare = (SourceBean) v.get(0);
								prgMovimentoDaAnnullare = (BigDecimal) rowMovAnnullare.getAttribute("PRGMOVIMENTO");
							} catch (Exception error) {
								it.eng.sil.util.TraceWrapper.debug(_logger,
										"ControllaTipoComunicazione::processRecord(): query recupero missioni fallita!",
										(Exception) error);
								return ProcessorsUtils.createResponse(name, classname,
										new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
										"Impossibile recuperare le missioni.", warnings, nested);
							}
						}

						// Vado avanti se ho trovato una solo comunicazione da annullare o rettificare o se si tratta di
						// un varDatori,
						// oppure se si tratta di un TRASFRAMOAZ
						if (codTipoComunic.equals(MessageCodes.General.ANNULLAMENTO_COMUNICAZIONE_PREC)
								|| codTipoComunic
										.equals(MessageCodes.General.ANNULLAMENTO_DA_UFFICIO_COMUNICAZIONE_PREC)) {
							SourceBean puResult = null;
							String infoMovimento = "";
							boolean eseguiForzatura = false;
							if ((!contestoVal.equalsIgnoreCase("validazioneMassiva"))) {
								eseguiForzatura = true;
							}
							int codiceErrore = annullaComunicazioniPrecedenti(record, v, contestoVal, codTipoComunic);
							if (codiceErrore < 0) {
								dettaglioErrore = "Non è stato possibile effettuare l'annullamento della comunicazione precedente.";
								infoMovimento = "Tipo Movimento = " + getCodTipoMov() + " Lavoratore = "
										+ getCodiceFiscaleLav() + " Data inizio = " + getDataInizioMov();
								switch (codiceErrore) {
								case -1:
									return ProcessorsUtils.createResponse(name, classname,
											new Integer(MessageCodes.ImportMov.ERR_ANNULLAMENTO_COMUNICAZ_PREC),
											"La comunicazione da annullare ha un movimento successivo." + infoMovimento,
											warnings, nested);
								case -2:
									return ProcessorsUtils.createResponse(name, classname,
											new Integer(MessageCodes.ImportMov.ERR_ANNULLAMENTO_COMUNICAZ_PREC_SANATA),
											infoMovimento, warnings, nested);
								case -3:
									return ProcessorsUtils.createResponse(name, classname,
											new Integer(MessageCodes.ImportMov.ERR_ANNULLAMENTO_COMUNICAZ_PREC),
											dettaglioErrore + " Impossibile cercare il documento associato."
													+ infoMovimento,
											warnings, nested);
								case -4:
									puResult = ProcessorsUtils.createResponse(name, classname,
											new Integer(MessageCodes.Mobilita.USCITA_MOBILITA),
											dettaglioErrore + infoMovimento, warnings, nested);
									if (eseguiForzatura && RequestContainer.getRequestContainer().getServiceRequest()
											.containsAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE)) {
										String valoreFlagMsgStatoOccMan = RequestContainer.getRequestContainer()
												.getServiceRequest()
												.containsAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE")
														? RequestContainer.getRequestContainer().getServiceRequest()
																.getAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE")
																.toString()
														: "";
										ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?",
												"ripetiInserimento", new String[] { "", "", valoreFlagMsgStatoOccMan },
												true);
									}
									return puResult;
								case -5:
									puResult = ProcessorsUtils.createResponse(name, classname, new Integer(getCode()),
											dettaglioErrore + infoMovimento, warnings, nested);
									if (eseguiForzatura && RequestContainer.getRequestContainer().getServiceRequest()
											.containsAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE)) {
										String forzaRicostruzione = RequestContainer.getRequestContainer()
												.getServiceRequest()
												.getAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE)
												.toString();
										ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?",
												"continuaRicalcolo", new String[] { forzaRicostruzione }, true);
									}
									return puResult;
								case -6:
									puResult = ProcessorsUtils.createResponse(name, classname, new Integer(getCode()),
											dettaglioErrore + infoMovimento, warnings, nested);
									if (eseguiForzatura && RequestContainer.getRequestContainer().getServiceRequest()
											.containsAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE)) {
										String valoreFlagMsgStatoOccMan = RequestContainer.getRequestContainer()
												.getServiceRequest()
												.containsAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE")
														? RequestContainer.getRequestContainer().getServiceRequest()
																.getAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE")
																.toString()
														: "";
										ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?",
												"ripetiInserimento", new String[] { "", "", valoreFlagMsgStatoOccMan },
												true);

									}
									return puResult;
								case -8:
									return ProcessorsUtils.createResponse(name, classname,
											new Integer(
													MessageCodes.ImportMov.ERR_RIALLINEAMENTO_PERIODI_INTERMITTENTI),
											dettaglioErrore + infoMovimento, warnings, nested);
								default:
									return ProcessorsUtils.createResponse(name, classname,
											new Integer(MessageCodes.ImportMov.ERR_ANNULLAMENTO_COMUNICAZ_PREC),
											dettaglioErrore + infoMovimento, warnings, nested);

								}
							}
							// WARNING DI AVVENUTO ANNULLAMENTO
							warnings.add(new Warning(MessageCodes.ImportMov.WAR_ANNULLAMENTO_COMUNICAZ_PREC, ""));
						} else {
							// Rettifica comunicazione precedente
							SourceBean puResult = null;
							String infoMovimento = "";
							boolean eseguiForzatura = false;
							if ((!contestoVal.equalsIgnoreCase("validazioneMassiva"))) {
								eseguiForzatura = true;
							}
							int codiceErrore = rettificaComunicazioniPrecedenti(record, v, contestoVal, warnings);
							if (codiceErrore < 0) {
								dettaglioErrore = "Non è stato possibile effettuare la rettifica della comunicazione precedente.";
								infoMovimento = "Tipo Movimento = " + getCodTipoMov() + " Lavoratore = "
										+ getCodiceFiscaleLav() + " Data inizio = " + getDataInizioMov();
								switch (codiceErrore) {
								case -1:
									return ProcessorsUtils.createResponse(name, classname,
											new Integer(MessageCodes.ImportMov.ERR_RETTIFICA_COMUNICAZ_PREC),
											"La comunicazione da rettificare ha un movimento successivo."
													+ infoMovimento,
											warnings, nested);
								case -2:
									return ProcessorsUtils.createResponse(name, classname,
											new Integer(MessageCodes.ImportMov.ERR_RETTIFICA_COMUNICAZ_PREC_SANATA),
											infoMovimento, warnings, nested);
								case -3:
									return ProcessorsUtils.createResponse(name, classname,
											new Integer(MessageCodes.ImportMov.ERR_RETTIFICA_COMUNICAZ_PREC),
											dettaglioErrore + " Impossibile cercare il documento associato"
													+ infoMovimento,
											warnings, nested);
								case -4:
									puResult = ProcessorsUtils.createResponse(name, classname,
											new Integer(MessageCodes.Mobilita.USCITA_MOBILITA),
											dettaglioErrore + infoMovimento, warnings, nested);
									if (eseguiForzatura && RequestContainer.getRequestContainer().getServiceRequest()
											.containsAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE)) {
										String valoreFlagMsgStatoOccMan = RequestContainer.getRequestContainer()
												.getServiceRequest()
												.containsAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE")
														? RequestContainer.getRequestContainer().getServiceRequest()
																.getAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE")
																.toString()
														: "";
										ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?",
												"ripetiInserimento", new String[] { "", "", valoreFlagMsgStatoOccMan },
												true);
									}
									return puResult;
								case -5:
									puResult = ProcessorsUtils.createResponse(name, classname, new Integer(getCode()),
											dettaglioErrore + infoMovimento, warnings, nested);
									if (eseguiForzatura && RequestContainer.getRequestContainer().getServiceRequest()
											.containsAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE)) {
										String forzaRicostruzione = RequestContainer.getRequestContainer()
												.getServiceRequest()
												.getAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE)
												.toString();
										ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?",
												"continuaRicalcolo", new String[] { forzaRicostruzione }, true);
									}
									return puResult;
								case -6:
									puResult = ProcessorsUtils.createResponse(name, classname, new Integer(getCode()),
											dettaglioErrore + infoMovimento, warnings, nested);
									if (eseguiForzatura && RequestContainer.getRequestContainer().getServiceRequest()
											.containsAttribute(StatoOccupazionaleManager.CONFIRM_STATO_OCCUPAZIONALE)) {
										String valoreFlagMsgStatoOccMan = RequestContainer.getRequestContainer()
												.getServiceRequest()
												.containsAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE")
														? RequestContainer.getRequestContainer().getServiceRequest()
																.getAttribute("FLAG_MESSAGGI_STATO_OCC_MANUALE")
																.toString()
														: "";
										ProcessorsUtils.addConfirm(puResult, "Vuoi forzare l' operazione?",
												"ripetiInserimento", new String[] { "", "", valoreFlagMsgStatoOccMan },
												true);

									}
									return puResult;
								default:
									return ProcessorsUtils.createResponse(name, classname,
											new Integer(MessageCodes.ImportMov.ERR_RETTIFICA_COMUNICAZ_PREC),
											dettaglioErrore + infoMovimento, warnings, nested);
								}
							}
							// WARNING DI AVVENUTO ANNULLAMENTO PER RETTIFICA
							warnings.add(new Warning(MessageCodes.ImportMov.WAR_RETTIFICA_COMUNICAZ_PREC, contesto));
							// Codice aggiunto per la gestione della rettifica di una CO arrivata sul polo con perdita
							// di competenza per la CO attuale
							record.put("RETTIFICAEFFETTUATA", "1");
						}
					}

					if (codTipoComunic.equals(MessageCodes.General.ANNULLAMENTO_COMUNICAZIONE_PREC)
							|| codTipoComunic.equals(MessageCodes.General.ANNULLAMENTO_DA_UFFICIO_COMUNICAZIONE_PREC)) {
						// Se arrivo a questo punto devo cancellare il movimento che si stava validando
						logMov.loadColonneTabella("AM_MOVIMENTO_APPOGGIO");
						logMov.lg_Movimento_App_Cancellato(prgMovApp);
						if (contestoVal.equalsIgnoreCase("validaArchivio")) {
							deletequery = "DELETE FROM AM_MOV_APP_ARCHIVIO WHERE PRGMOVIMENTOAPP = " + prgMovApp;
						} else {
							deletequery = "DELETE FROM AM_MOVIMENTO_APPOGGIO WHERE PRGMOVIMENTOAPP = " + prgMovApp;
						}

						try {
							resultDel = trans.executeQueryByStringStatement(deletequery, null,
									TransactionQueryExecutor.DELETE);
						} catch (EMFInternalError error) {
							it.eng.sil.util.TraceWrapper.debug(_logger,
									"ControllaTipoComunicazione::processRecord(): query di rimozione del movimento fallita!",
									(Exception) error);
							return ProcessorsUtils.createResponse(name, classname,
									new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
									"Impossibile rimuovere il movimento che si riferiva all'annullamento di una comunicazione precedente dalla lista dei movimenti da validare.",
									warnings, nested);
						}
						// Esamino il risultato
						if (!((resultDel instanceof Boolean) && (((Boolean) resultDel).booleanValue() == true))) {
							it.eng.sil.util.TraceWrapper.debug(_logger,
									"ControllaTipoComunicazione::processRecord(): query di rimozione del movimento fallita!",
									(Exception) resultDel);
							return ProcessorsUtils.createResponse(name, classname,
									new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
									"Impossibile rimuovere il movimento che si riferiva all'annullamento di una comunicazione precedente dalla lista dei movimenti da validare.",
									warnings, nested);
						}
						// Verifico se rimuovere l'eventuale avviamento veloce.
						if (prgMovAppCVE != null) {
							if (contestoVal.equalsIgnoreCase("validaArchivio")) {
								deletequery = "DELETE FROM AM_MOV_APP_ARCHIVIO WHERE PRGMOVIMENTOAPP = " + prgMovAppCVE;
							} else {
								deletequery = "DELETE FROM AM_MOVIMENTO_APPOGGIO WHERE PRGMOVIMENTOAPP = "
										+ prgMovAppCVE;
							}
							try {
								resultDel = trans.executeQueryByStringStatement(deletequery, null,
										TransactionQueryExecutor.DELETE);
							} catch (EMFInternalError error) {
								it.eng.sil.util.TraceWrapper.debug(_logger,
										"ControllaTipoComunicazione::processRecord(): query di rimozione del movimento fallita!",
										(Exception) error);
								return ProcessorsUtils.createResponse(name, classname,
										new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
										"Impossibile rimuovere il movimento che si riferiva all'annullamento di una comunicazione precedente dalla lista dei movimenti da validare.",
										warnings, nested);
							}
							// Esamino il risultato
							if (!((resultDel instanceof Boolean) && (((Boolean) resultDel).booleanValue() == true))) {
								it.eng.sil.util.TraceWrapper.debug(_logger,
										"ControllaTipoComunicazione::processRecord(): query di rimozione del movimento fallita!",
										(Exception) resultDel);
								return ProcessorsUtils.createResponse(name, classname,
										new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
										"Impossibile rimuovere il movimento che si riferiva all'annullamento di una comunicazione precedente dalla lista dei movimenti da validare.",
										warnings, nested);
							}
						} else {
							if (!codTipoMovVal.equalsIgnoreCase("AVV")) {
								v = estraiMovimentoAvvFittizio(codTipoComunic, codComunicazioneCurr, contestoVal,
										codfisc);
								if (v.size() == 1) {
									SourceBean rowMov = (SourceBean) v.get(0);
									Object prgMovAppoggioFittizio = rowMov.getAttribute("PRGMOVIMENTOAPP");
									if (contestoVal.equalsIgnoreCase("validaArchivio")) {
										deletequery = "DELETE FROM AM_MOV_APP_ARCHIVIO WHERE PRGMOVIMENTOAPP = "
												+ prgMovAppoggioFittizio;
									} else {
										deletequery = "DELETE FROM AM_MOVIMENTO_APPOGGIO WHERE PRGMOVIMENTOAPP = "
												+ prgMovAppoggioFittizio;
									}
									try {
										resultDel = trans.executeQueryByStringStatement(deletequery, null,
												TransactionQueryExecutor.DELETE);
									} catch (EMFInternalError error) {
										it.eng.sil.util.TraceWrapper.debug(_logger,
												"ControllaTipoComunicazione::processRecord(): query di rimozione del movimento fallita!",
												(Exception) error);
										return ProcessorsUtils.createResponse(name, classname,
												new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
												"Impossibile rimuovere il movimento di avviamento fittizio che si riferiva all'annullamento di una comunicazione precedente dalla lista dei movimenti da validare.",
												warnings, nested);
									}
									// Esamino il risultato
									if (!((resultDel instanceof Boolean)
											&& (((Boolean) resultDel).booleanValue() == true))) {
										it.eng.sil.util.TraceWrapper.debug(_logger,
												"ControllaTipoComunicazione::processRecord(): query di rimozione del movimento fallita!",
												(Exception) resultDel);
										return ProcessorsUtils.createResponse(name, classname,
												new Integer(MessageCodes.ImportMov.ERR_UPDATE_DATA),
												"Impossibile rimuovere il movimento di avviamento fittizio che si riferiva all'annullamento di una comunicazione precedente dalla lista dei movimenti da validare.",
												warnings, nested);
									}
								} else {
									if (v.size() > 1) {
										return ProcessorsUtils.createResponse(name, classname,
												new Integer(MessageCodes.ImportMov.ERR_ANNULLAMENTO_COMUNICAZ_PREC),
												"Non è stato individuato un unico avviamento fittizio da rimuovere.",
												warnings, nested);
									}
								}
							}
						}
						// Annullamento: bisogna riallineare i periodi intermittenti nel caso di lavoro intermittente
						String codContratto = record.containsKey("CODCONTRATTO") ? record.get("CODCONTRATTO").toString()
								: "";
						if (codContratto.equalsIgnoreCase(Properties.CONTRATTO_LAVORO_INTERMITTENTE)) {
							// Bisogna riallineare eventuali periodi intermittenti
							User usr = (User) sessione.getAttribute(User.USERID);
							BigDecimal userid = new BigDecimal(usr.getCodut());
							if (prgMovimentoDaAnnullare != null) {
								boolean esitoOperazione = MovimentoBean.riallineamenoPeriodiIntermittenti(
										prgMovimentoDaAnnullare, null, null, codTipoMovVal, warnings, userid, "ANNULLA",
										trans, null);
								if (!esitoOperazione) {
									return ProcessorsUtils.createResponse(name, classname,
											new Integer(
													MessageCodes.ImportMov.ERR_RIALLINEAMENTO_PERIODI_INTERMITTENTI),
											"", warnings, nested);
								}
							} else {
								if (vMovAnnullare != null && vMovAnnullare.size() > 0) {
									for (int iVmov = 0; iVmov < vMovAnnullare.size(); iVmov++) {
										SourceBean rowMovAnn = (SourceBean) vMovAnnullare.get(iVmov);
										BigDecimal prgMovimentoAn = rowMovAnn.getAttribute("PRGMOVIMENTO") != null
												? (BigDecimal) rowMovAnn.getAttribute("PRGMOVIMENTO")
												: null;
										boolean esitoOperazione = MovimentoBean.riallineamenoPeriodiIntermittenti(
												prgMovimentoAn, null, null, codTipoMovVal, warnings, userid, "ANNULLA",
												trans, null);
										if (!esitoOperazione) {
											return ProcessorsUtils.createResponse(name, classname, new Integer(
													MessageCodes.ImportMov.ERR_RIALLINEAMENTO_PERIODI_INTERMITTENTI),
													"", warnings, nested);
										}
									}
								}
							}
						}
						warnings.add(new Warning(MessageCodes.ImportMov.WAR_MOV_APP_CODCOMUNICAZ_CANC, ""));
						return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested, true);
					} else {
						// Rettifica comunicazione precedente. Cerco il prgmovimento rettificato in due casi:
						// 1)quando si sta rettificando il primo movimento del varDatori (si metteno stato atto AR
						// tutti)
						// 2)quando si stanno rettificando gli altri (la funzione rettificaComunicazioniPrecedenti non
						// viene chiamata più
						// perché sono stati tutti messi a AR durante la prima rettifica)
						if ((getVarDatori() || !getMovimentoRett())) {
							String codfiscLav = record.get("STRCODICEFISCALE") != null
									? record.get("STRCODICEFISCALE").toString()
									: "";
							String codfiscAZ = record.get("STRAZCODICEFISCALE") != null
									? record.get("STRAZCODICEFISCALE").toString()
									: "";
							String codComUnitaAz = record.get("CODUACOM") != null ? record.get("CODUACOM").toString()
									: "";
							codfiscLav = codfiscLav.toUpperCase();
							codfiscAZ = codfiscAZ.toUpperCase();
							codComUnitaAz = codComUnitaAz.toUpperCase();

							String codTipoMov = record.get("CODTIPOMOV") != null ? record.get("CODTIPOMOV").toString()
									: "";
							// Query per la ricerca del movimento rettificato da collegare all'eventuale movimento
							// protocollato
							selectquery = "SELECT MOV1.PRGMOVIMENTO "
									+ " FROM AM_MOVIMENTO MOV1, AN_LAVORATORE LAV, AN_AZIENDA AZ, AN_UNITA_AZIENDA UAZ "
									+ " WHERE MOV1.CDNLAVORATORE = LAV.CDNLAVORATORE "
									+ " AND MOV1.PRGAZIENDA = AZ.PRGAZIENDA AND AZ.PRGAZIENDA = UAZ.PRGAZIENDA AND MOV1.PRGUNITA = UAZ.PRGUNITA "
									+ " AND MOV1.CODSTATOATTO = 'AR' AND MOV1.CODTIPOMOV = '" + codTipoMov
									+ "' AND MOV1.CODCOMUNICAZIONE = '" + codComunicazione + "' "
									+ " AND UPPER(LAV.STRCODICEFISCALE) = '" + codfiscLav + "' "
									+ " AND UPPER(AZ.STRCODICEFISCALE) = '" + codfiscAZ + "' AND UPPER(UAZ.CODCOM) = '"
									+ codComUnitaAz + "' ";
							result = null;
							try {
								result = ProcessorsUtils.executeSelectQuery(selectquery, trans);
							} catch (Exception e) {
								return ProcessorsUtils.createResponse(name, classname,
										new Integer(MessageCodes.ImportMov.ERR_REC_DATI_DB),
										"Impossibile cercare il movimento rettificato da collegare a quello protocollato",
										warnings, nested);
							}
							v = result.getAttributeAsVector("ROW");
							if (v.size() == 1) {
								SourceBean movRett = (SourceBean) v.get(0);
								Object prgMovRett = movRett.getAttribute("PRGMOVIMENTO");
								// imposto il prgmovimento rettificato che andrà nel movimento protocollato
								record.put("PRGMOVIMENTORETT", prgMovRett);
							}
						}
						return ProcessorsUtils.createResponse(name, classname, null, null, warnings, nested);
					}
				}
			} else {
				// la validazione del movimento non si riferisce all'annullamento o rettifica di una comunicazione
				// precedente
				// Modifica data 02/10/2007
				if (codTipoComunic != null && codTipoComunic.equalsIgnoreCase("00")) {
					record.put("CODMOTANNULLAMENTO", "MAG");
				}
				if (codTipoComunic != null && (codTipoComunic.equalsIgnoreCase("07")
						|| codTipoComunic.equalsIgnoreCase("08") || codTipoComunic.equalsIgnoreCase("09"))) {
					record.put("CODMOTANNULLAMENTO", "URG");
				}
				if (codTipoComunic != null && codTipoComunic.equalsIgnoreCase("06")) {
					record.put("CODMOTANNULLAMENTO", "IUF");
				}
				return null;
			}
		} catch (Exception e) {
			dettaglioErrore = "";
			if (codTipoComunic != null) {
				if (codTipoComunic.equals(MessageCodes.General.ANNULLAMENTO_COMUNICAZIONE_PREC)
						|| codTipoComunic.equals(MessageCodes.General.ANNULLAMENTO_DA_UFFICIO_COMUNICAZIONE_PREC)) {
					dettaglioErrore = "Non è stato possibile annullare la comunicazione precedente.";
				} else {
					if (codTipoComunic.equals(MessageCodes.General.RETTIFICA_COMUNICAZIONE_PREC)) {
						dettaglioErrore = "Non è stato possibile rettificare la comunicazione precedente.";
					}
				}
			}
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.ImportMov.VALIDAZIONE_FAIL),
					dettaglioErrore, warnings, nested);
		}
	}

	public Boolean controllaPermessi(SessionContainer sessione, Object cdnLav) {
		Boolean permettiImpatti = new Boolean(true);
		if (cdnLav != null) {
			String _page = "MovListaValidazionePage";
			User usr = (User) sessione.getAttribute(User.USERID);
			TransactionProfileDataFilter tProfile = new TransactionProfileDataFilter(usr, _page,
					trans.getDataConnection());
			tProfile.setCdnLavoratore((BigDecimal) cdnLav);
			permettiImpatti = new Boolean(tProfile.canEditLavoratore());
		}
		return permettiImpatti;
	}

	/**
	 * 
	 * @param record
	 * @param v
	 * @param contesto
	 * @return -1 se troviamo un movimento che ha un successivo -2 se il movimento da annullare è coinvolto in una
	 *         dichiarazione reddituale -3 errore nella query per la ricerca documento da annullare -4 errore periodo di
	 *         mobilità -5 stati occupazionali manuali -6 did non stipulabile -7 errore generico
	 * @throws Exception
	 */
	public int annullaComunicazioniPrecedenti(Map record, Vector v, String contesto, String codTipoComunic)
			throws Exception {
		int vSize = v.size();
		if (codTipoComunic.equals(MessageCodes.General.ANNULLAMENTO_COMUNICAZIONE_PREC)) {
			request.updAttribute("CODSTATOATTO", "AN");
			request.updAttribute("CODMOTANNULLAMENTO", "ERR");
		}
		if (codTipoComunic.equals(MessageCodes.General.ANNULLAMENTO_DA_UFFICIO_COMUNICAZIONE_PREC)) {
			request.updAttribute("CODSTATOATTO", "AU");
			request.updAttribute("CODMOTANNULLAMENTO", "AIU");
		}
		for (int i = 0; i < vSize; i++) {
			Object cdnLav = null;
			SourceBean rowDoc = null;
			String dettaglio = "";
			Vector vettDocumenti = new Vector();
			Vector prgdocs = new Vector();
			Vector vettDocumentiPrec = new Vector();
			Vector prgdocsPrec = new Vector();
			SourceBean rowMovComunicazPrec = (SourceBean) v.get(i);
			Object prgMovComunicazPrec = rowMovComunicazPrec.getAttribute("PRGMOVIMENTO");
			Object prgMovSuccComunicazPrec = rowMovComunicazPrec.getAttribute("PRGMOVIMENTOSUCC");
			String strCodiceFiscaleLav = rowMovComunicazPrec.containsAttribute("STRCODICEFISCALE")
					? rowMovComunicazPrec.getAttribute("STRCODICEFISCALE").toString()
					: "";
			String codTipoMov = rowMovComunicazPrec.containsAttribute("CODTIPOMOV")
					? rowMovComunicazPrec.getAttribute("CODTIPOMOV").toString()
					: "";
			String datInizioMov = rowMovComunicazPrec.containsAttribute("DATINIZIOMOV")
					? rowMovComunicazPrec.getAttribute("DATINIZIOMOV").toString()
					: "";
			if (!getVarDatori() && prgMovSuccComunicazPrec != null && !prgMovSuccComunicazPrec.toString().equals("")) {
				// Non è possibile annullare un movimento che ha un successivo
				setCodiceFiscaleLav(strCodiceFiscaleLav);
				setCodTipoMov(codTipoMov);
				setDataInizioMov(datInizioMov);
				return -1;
			}
			// Controllo che il movimento non sia coinvolto in
			// una dichiarazione reddituale
			String selectQueryDich = "select dich.prgdichlav from am_dich_lav dich, am_dich_lav_dettaglio dett "
					+ " where dich.prgdichlav = dett.prgdichlav and dich.codstatoatto = 'PR' and dett.prgmovimento = "
					+ prgMovComunicazPrec;
			SourceBean rowDich = ProcessorsUtils.executeSelectQuery(selectQueryDich, trans);
			if (rowDich != null && rowDich.getAttributeAsVector("ROW").size() > 0) {
				// Non è possibile annullare un movimento coinvolto in una dichiarazione reddituale
				setCodiceFiscaleLav(strCodiceFiscaleLav);
				setCodTipoMov(codTipoMov);
				setDataInizioMov(datInizioMov);
				return -2;
			}
			if ((i == 0) && (contesto.equalsIgnoreCase("valida") || contesto.equalsIgnoreCase("validaArchivio"))) {
				// dopo la validazione facciamo visualizzare il primo movimento annullato
				record.put("PRGMOVIMENTO", prgMovComunicazPrec);
			}
			// recupero documento (deve essere annullato anche il documento associato)
			String selectquery = "select doc.prgdocumento from am_documento doc, am_documento_coll doc_coll "
					+ "where doc_coll.prgdocumento = doc.prgdocumento and doc.codtipodocumento IN ('MVTRA', 'MVPRO', 'MVCES', 'MVAVV') "
					+ "AND doc_coll.strChiaveTabella = " + prgMovComunicazPrec
					+ " order by doc.DATPROTOCOLLO, doc.prgdocumento ";
			try {
				rowDoc = ProcessorsUtils.executeSelectQuery(selectquery, trans);
				if (rowDoc != null) {
					vettDocumenti = rowDoc.getAttributeAsVector("ROW");
				}
			} catch (Exception e) {
				// errore query ricerca documento
				setCodiceFiscaleLav(strCodiceFiscaleLav);
				setCodTipoMov(codTipoMov);
				setDataInizioMov(datInizioMov);
				return -3;
			}
			for (int k = 0; k < vettDocumenti.size(); k++) {
				SourceBean sb = (SourceBean) vettDocumenti.get(k);
				prgdocs.add(sb.getAttribute("prgdocumento"));
			}
			sessione.setAttribute("PRGDOCUMENTI", prgdocs);
			cdnLav = rowMovComunicazPrec.getAttribute("CDNLAVORATORE");
			String codMotAnnullamento = rowMovComunicazPrec.containsAttribute("CODMOTANNULLAMENTO")
					? rowMovComunicazPrec.getAttribute("CODMOTANNULLAMENTO").toString()
					: "";
			Object prgMovimentoPrec = rowMovComunicazPrec.getAttribute("PRGMOVIMENTOPREC");
			String codTipoMovPrec = rowMovComunicazPrec.containsAttribute("CODTIPOMOVPREC")
					? rowMovComunicazPrec.getAttribute("CODTIPOMOVPREC").toString()
					: "";
			Boolean permettiImpatti = controllaPermessi(sessione, cdnLav);
			request.updAttribute("PRGMOVIMENTO", prgMovComunicazPrec);
			request.updAttribute("cdnLavoratore", cdnLav);
			request.updAttribute("PERMETTIIMPATTI", permettiImpatti.toString());
			M_MovSalvaConsultaGen obj = new M_MovSalvaConsultaGen();
			int errore = obj.eseguiAnnullamento(reqCont, request, sessione, trans);
			if (errore < 0) {
				if (errore == erroreMobilita) {
					setCodiceFiscaleLav(strCodiceFiscaleLav);
					setCodTipoMov(codTipoMov);
					setDataInizioMov(datInizioMov);
					return -4;
				} else {
					setCodiceFiscaleLav(strCodiceFiscaleLav);
					setCodTipoMov(codTipoMov);
					setDataInizioMov(datInizioMov);
					if (errore == erroreControlli) {
						setCode(obj.getCodiceErrore());
						if (getCode() == MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE
								|| getCode() == MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE
								|| getCode() == MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI
								|| getCode() == MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA) {
							return -5;
						} else {
							return -6;
						}
					} else {
						return -7;
					}
				}
			} else {
				if ((prgMovimentoPrec != null)
						&& (codTipoMov.equalsIgnoreCase("CES") || codTipoMov.equalsIgnoreCase("PRO")
								|| codTipoMov.equalsIgnoreCase("TRA"))
						&& (codTipoMovPrec.equalsIgnoreCase("AVV"))
						&& (codMotAnnullamento.equalsIgnoreCase("CVE") || codMotAnnullamento.equalsIgnoreCase("PVE")
								|| codMotAnnullamento.equalsIgnoreCase("TVE"))) {
					// In questo caso annullo anche l'avviamento veloce.
					// Recupero documento dell'avviamento veloce (deve essere annullato anche il documento associato)
					selectquery = "select doc.prgdocumento from am_documento doc, am_documento_coll doc_coll "
							+ "where doc_coll.prgdocumento = doc.prgdocumento and doc.codtipodocumento IN ('MVTRA', 'MVPRO', 'MVCES', 'MVAVV') "
							+ "AND doc_coll.strChiaveTabella = " + prgMovimentoPrec
							+ " order by doc.DATPROTOCOLLO, doc.prgdocumento ";
					try {
						rowDoc = ProcessorsUtils.executeSelectQuery(selectquery, trans);
						if (rowDoc != null) {
							vettDocumentiPrec = rowDoc.getAttributeAsVector("ROW");
						}
					} catch (Exception e) {
						// errore query ricerca documento
						setCodiceFiscaleLav(strCodiceFiscaleLav);
						setCodTipoMov(codTipoMov);
						setDataInizioMov(datInizioMov);
						return -3;
					}
					for (int k = 0; k < vettDocumentiPrec.size(); k++) {
						SourceBean sb = (SourceBean) vettDocumentiPrec.get(k);
						prgdocsPrec.add(sb.getAttribute("prgdocumento"));
					}
					sessione.setAttribute("PRGDOCUMENTI", prgdocsPrec);
					request.updAttribute("PRGMOVIMENTO", prgMovimentoPrec);
					errore = obj.eseguiAnnullamento(reqCont, request, sessione, trans);
					if (errore < 0) {
						if (errore == erroreMobilita) {
							setCodiceFiscaleLav(strCodiceFiscaleLav);
							setCodTipoMov(codTipoMov);
							setDataInizioMov(datInizioMov);
							return -4;
						} else {
							setCodiceFiscaleLav(strCodiceFiscaleLav);
							setCodTipoMov(codTipoMov);
							setDataInizioMov(datInizioMov);
							if (errore == erroreControlli) {
								setCode(obj.getCodiceErrore());
								if (getCode() == MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE
										|| getCode() == MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE
										|| getCode() == MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI
										|| getCode() == MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA) {
									return -5;
								} else {
									return -6;
								}
							} else {
								return -7;
							}
						}
					}
				}
			}
		} // fine for
		return 0;
	}

	/**
	 * 
	 * @param record
	 * @param v
	 * @param contesto
	 * @return -1 se troviamo un movimento che ha un successivo -2 se il movimento da annullare è coinvolto in una
	 *         dichiarazione reddituale -3 errore nella query per la ricerca documento da annullare -4 errore periodo di
	 *         mobilità -5 stati occupazionali manuali -6 did non stipulabile -7 errore generico
	 * @throws Exception
	 */
	public int rettificaComunicazioniPrecedenti(Map record, Vector v, String contesto, ArrayList warnings)
			throws Exception {
		// rettifica comunicazione precedente
		request.updAttribute("CODSTATOATTO", "AR");
		request.updAttribute("CODMOTANNULLAMENTO", "ERR");
		String codTipoAss = record.get("CODTIPOASS") != null ? (String) record.get("CODTIPOASS") : "";
		String codTipoMovVal = record.get("CODTIPOMOV") != null ? record.get("CODTIPOMOV").toString() : "";
		String dataFineNewMov = (String) record.get("DATFINEMOV");
		String datInizioNewMov = (String) record.get("DATINIZIOMOV");
		boolean contrattoApprendistatoModificato = false;
		User usr = (User) sessione.getAttribute(User.USERID);
		BigDecimal userid = new BigDecimal(usr.getCodut());
		int vSize = v.size();
		for (int i = 0; i < vSize; i++) {
			Object cdnLav = null;
			SourceBean rowDoc = null;
			Vector vettDocumenti = new Vector();
			Vector prgdocs = new Vector();
			Vector vettDocumentiPrec = new Vector();
			Vector prgdocsPrec = new Vector();
			SourceBean rowMovComunicazPrec = (SourceBean) v.get(i);
			Object prgMovComunicazPrec = rowMovComunicazPrec.getAttribute("PRGMOVIMENTO");
			Object prgMovSuccComunicazPrec = rowMovComunicazPrec.getAttribute("PRGMOVIMENTOSUCC");
			String strCodiceFiscaleLav = rowMovComunicazPrec.containsAttribute("STRCODICEFISCALE")
					? rowMovComunicazPrec.getAttribute("STRCODICEFISCALE").toString()
					: "";
			String codTipoMov = rowMovComunicazPrec.containsAttribute("CODTIPOMOV")
					? rowMovComunicazPrec.getAttribute("CODTIPOMOV").toString()
					: "";
			String codTipoContrattoPrec = rowMovComunicazPrec.containsAttribute("CODTIPOCONTRATTO")
					? rowMovComunicazPrec.getAttribute("CODTIPOCONTRATTO").toString()
					: "";
			String datInizioMov = rowMovComunicazPrec.containsAttribute("DATINIZIOMOV")
					? rowMovComunicazPrec.getAttribute("DATINIZIOMOV").toString()
					: "";
			if (!getVarDatori() && prgMovSuccComunicazPrec != null && !prgMovSuccComunicazPrec.toString().equals("")) {
				// Non è possibile rettificare un movimento che ha un successivo
				setCodiceFiscaleLav(strCodiceFiscaleLav);
				setCodTipoMov(codTipoMov);
				setDataInizioMov(datInizioMov);
				return -1;
			}
			// Controllo che il movimento non sia coinvolto in una dichiarazione reddituale
			String selectQueryDich = "select dich.prgdichlav from am_dich_lav dich, am_dich_lav_dettaglio dett "
					+ " where dich.prgdichlav = dett.prgdichlav and dich.codstatoatto = 'PR' and dett.prgmovimento = "
					+ prgMovComunicazPrec;
			SourceBean rowDich = ProcessorsUtils.executeSelectQuery(selectQueryDich, trans);
			if (rowDich != null && rowDich.getAttributeAsVector("ROW").size() > 0) {
				setCodiceFiscaleLav(strCodiceFiscaleLav);
				setCodTipoMov(codTipoMov);
				setDataInizioMov(datInizioMov);
				return -2;
			}

			if ((i == 0) && (contesto.equalsIgnoreCase("valida") || contesto.equalsIgnoreCase("validaArchivio"))) {
				// Memorizzo il primo movimento rettificato. Questo movimento sarà visualizzato nel caso di rettifica
				// e di mancanza di competenza amministrativa (decreto 15/06/2012, le rettifiche arrivano a tutte le
				// prov)
				record.put("PRGMOVRETTCOMPETENZA", prgMovComunicazPrec);
			}

			// recupero documento (deve essere annullato anche il documento associato)
			String selectquery = "select doc.prgdocumento from am_documento doc, am_documento_coll doc_coll "
					+ "where doc_coll.prgdocumento = doc.prgdocumento and doc.codtipodocumento IN ('MVTRA', 'MVPRO', 'MVCES', 'MVAVV') "
					+ "AND doc_coll.strChiaveTabella = " + prgMovComunicazPrec
					+ " order by doc.DATPROTOCOLLO, doc.prgdocumento ";
			try {
				rowDoc = ProcessorsUtils.executeSelectQuery(selectquery, trans);
				if (rowDoc != null) {
					vettDocumenti = rowDoc.getAttributeAsVector("ROW");
				}
			} catch (Exception e) {
				setCodiceFiscaleLav(strCodiceFiscaleLav);
				setCodTipoMov(codTipoMov);
				setDataInizioMov(datInizioMov);
				return -3;
			}
			for (int k = 0; k < vettDocumenti.size(); k++) {
				SourceBean sb = (SourceBean) vettDocumenti.get(k);
				prgdocs.add(sb.getAttribute("prgdocumento"));
			}
			sessione.setAttribute("PRGDOCUMENTI", prgdocs);
			cdnLav = rowMovComunicazPrec.getAttribute("CDNLAVORATORE");
			String codMotAnnullamento = rowMovComunicazPrec.containsAttribute("CODMOTANNULLAMENTO")
					? rowMovComunicazPrec.getAttribute("CODMOTANNULLAMENTO").toString()
					: "";
			Object prgMovimentoPrec = rowMovComunicazPrec.getAttribute("PRGMOVIMENTOPREC");
			String codTipoMovPrec = rowMovComunicazPrec.containsAttribute("CODTIPOMOVPREC")
					? rowMovComunicazPrec.getAttribute("CODTIPOMOVPREC").toString()
					: "";
			Boolean permettiImpatti = controllaPermessi(sessione, cdnLav);
			request.updAttribute("PRGMOVIMENTO", prgMovComunicazPrec);
			request.updAttribute("cdnLavoratore", cdnLav);
			request.updAttribute("PERMETTIIMPATTI", permettiImpatti.toString());

			if (!contrattoApprendistatoModificato) {
				contrattoApprendistatoModificato = checkRettificheApprendistato(codTipoAss, codTipoContrattoPrec,
						codTipoMovVal, warnings, record);
			}

			if (!getVarDatori()) {
				record.put("PRGMOVIMENTORETT", prgMovComunicazPrec);
				setMovimentoRett(true);
			}
			// Bisogna riallineare eventuali periodi intermittenti
			boolean esitoOperazione = MovimentoBean.riallineamenoPeriodiIntermittenti(
					new BigDecimal(prgMovComunicazPrec.toString()), datInizioNewMov, dataFineNewMov, codTipoMovVal,
					warnings, userid, "RETTIFICA", trans, record);
			if (!esitoOperazione) {
				return -8;
			}
			//
			M_MovSalvaConsultaGen obj = new M_MovSalvaConsultaGen();
			int errore = obj.eseguiAnnullamento(reqCont, request, sessione, trans);

			if (errore < 0) {
				if (errore == erroreMobilita) {
					setCodiceFiscaleLav(strCodiceFiscaleLav);
					setCodTipoMov(codTipoMov);
					setDataInizioMov(datInizioMov);
					return -4;
				} else {
					setCodiceFiscaleLav(strCodiceFiscaleLav);
					setCodTipoMov(codTipoMov);
					setDataInizioMov(datInizioMov);
					if (errore == erroreControlli) {
						setCode(obj.getCodiceErrore());
						if (getCode() == MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE
								|| getCode() == MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE
								|| getCode() == MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI
								|| getCode() == MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA) {
							return -5;
						} else {
							return -6;
						}
					} else {
						return -7;
					}
				}
			} else {
				if ((prgMovimentoPrec != null)
						&& (codTipoMov.equalsIgnoreCase("CES") || codTipoMov.equalsIgnoreCase("PRO")
								|| codTipoMov.equalsIgnoreCase("TRA"))
						&& (codTipoMovPrec.equalsIgnoreCase("AVV"))
						&& (codMotAnnullamento.equalsIgnoreCase("CVE") || codMotAnnullamento.equalsIgnoreCase("PVE")
								|| codMotAnnullamento.equalsIgnoreCase("TVE"))) {
					// In questo caso rettifico anche l'avviamento veloce.
					// Recupero documento dell'avviamento veloce (deve essere annullato anche il documento associato)
					selectquery = "select doc.prgdocumento from am_documento doc, am_documento_coll doc_coll "
							+ "where doc_coll.prgdocumento = doc.prgdocumento and doc.codtipodocumento IN ('MVTRA', 'MVPRO', 'MVCES', 'MVAVV') "
							+ "AND doc_coll.strChiaveTabella = " + prgMovimentoPrec
							+ " order by doc.DATPROTOCOLLO, doc.prgdocumento ";
					try {
						rowDoc = ProcessorsUtils.executeSelectQuery(selectquery, trans);
						if (rowDoc != null) {
							vettDocumentiPrec = rowDoc.getAttributeAsVector("ROW");
						}
					} catch (Exception e) {
						setCodiceFiscaleLav(strCodiceFiscaleLav);
						setCodTipoMov(codTipoMov);
						setDataInizioMov(datInizioMov);
						return -3;
					}
					for (int k = 0; k < vettDocumentiPrec.size(); k++) {
						SourceBean sb = (SourceBean) vettDocumentiPrec.get(k);
						prgdocsPrec.add(sb.getAttribute("prgdocumento"));

					}
					sessione.setAttribute("PRGDOCUMENTI", prgdocsPrec);
					request.updAttribute("PRGMOVIMENTO", prgMovimentoPrec);
					record.put("PRGMOVIMENTORETTASSCVE", prgMovimentoPrec);
					errore = obj.eseguiAnnullamento(reqCont, request, sessione, trans);
					if (errore < 0) {
						if (errore == erroreMobilita) {
							setCodiceFiscaleLav(strCodiceFiscaleLav);
							setCodTipoMov(codTipoMov);
							setDataInizioMov(datInizioMov);
							return -4;
						} else {
							if (errore == erroreControlli) {
								setCodiceFiscaleLav(strCodiceFiscaleLav);
								setCodTipoMov(codTipoMov);
								setDataInizioMov(datInizioMov);
								if (errore == erroreControlli) {
									setCode(obj.getCodiceErrore());
									if (getCode() == MessageCodes.StatoOccupazionale.STATO_OCCUPAZIONALE_MANUALE
											|| getCode() == MessageCodes.StatoOccupazionale.CONSERVA_STATO_OCCUPAZIONALE_MANUALE
											|| getCode() == MessageCodes.StatoOccupazionale.STATO_OCC_CON_MESI_SOSP_ANZ_NON_CALCOLATI
											|| getCode() == MessageCodes.StatoOccupazionale.STATO_OCC_CON_DATA_ANZIANITA_ERRATA) {
										return -5;
									} else {
										return -6;
									}
								}
							} else {
								return -7;
							}
						}
					} else {
						// Rettifica dell'avviamento veloce è andata a buon fine. Allora devo conservare in validazione
						// manuale
						// l'avvenuta rettifica dell'avviamento veloce, per consentire in rettifica l'inserimento
						// dell'eventuale
						// avviamento veloce creato a partire dalla comunicazione di rettifica che è arrivata
						record.put("RETTIFICA_AVV_VCE", "TRUE");
					}
				} else {
					// non siamo nel caso di cessazione (oppure proroga o trasformazione) veloce e nel caso di
					// TRASFRAMOAZ (varDatori),
					// conservo l'eventuale prgMovimentoPrec a cui collegare eventualmente il nuovo movimento nel caso
					// di rettifica
					if (!getVarDatori() && prgMovimentoPrec != null) {
						record.put("PRGMOVIMENTOPREC_RETTIFICACOMUNICAZIONE", prgMovimentoPrec);
					}
				}
			}
		}
		return 0;
	}

	public boolean checkVarDatori(Vector v) {
		int vSize = v.size();
		for (int i = 0; i < vSize; i++) {
			SourceBean rowMov = (SourceBean) v.get(i);
			String codTipoMov = rowMov.getAttribute("CODTIPOMOV") != null ? rowMov.getAttribute("CODTIPOMOV").toString()
					: "";
			if ((!codTipoMov.equalsIgnoreCase("TRA")) && (!codTipoMov.equalsIgnoreCase("AVV"))) {
				return false;
			}
		}
		return true;
	}

	public Vector carciaMovimentiDaAnnullare(Vector v) {
		int vSize = v.size();
		Vector vMov = new Vector();
		for (int i = 0; i < vSize; i++) {
			SourceBean rowMov = (SourceBean) v.get(i);
			BigDecimal prgMov = rowMov.getAttribute("PRGMOVIMENTO") != null
					? (BigDecimal) rowMov.getAttribute("PRGMOVIMENTO")
					: null;
			vMov.add(prgMov);
		}
		return vMov;
	}

	public void setCode(int val) {
		this.code = val;
	}

	public int getCode() {
		return this.code;
	}

	public void setCodiceFiscaleLav(String val) {
		this.codiceFiscaleLav = val;
	}

	public String getCodiceFiscaleLav() {
		return this.codiceFiscaleLav;
	}

	public void setCodTipoMov(String val) {
		this.codTipoMov = val;
	}

	public String getCodTipoMov() {
		return this.codTipoMov;
	}

	public void setDataInizioMov(String val) {
		this.dataInizioMov = val;
	}

	public String getDataInizioMov() {
		return this.dataInizioMov;
	}

	public void setVarDatori(boolean val) {
		this.varDatori = val;
	}

	public boolean getVarDatori() {
		return this.varDatori;
	}

	public void setMovimentoRett(boolean val) {
		this.settatoMovimentoRett = val;
	}

	public boolean getMovimentoRett() {
		return this.settatoMovimentoRett;
	}

	public Vector estraiMovimentoAvvFittizio(String codTipoComunic, String codComunicazione, String contesto,
			String codiceFiscale) throws Exception {
		String selectquery = "";
		String nomeTabella = "";
		SourceBean result = null;
		codiceFiscale = codiceFiscale.toUpperCase();
		if (contesto.equalsIgnoreCase("validaArchivio")) {
			nomeTabella = "AM_MOV_APP_ARCHIVIO";
		} else {
			nomeTabella = "AM_MOVIMENTO_APPOGGIO";
		}
		selectquery = "SELECT PRGMOVIMENTOAPP, TO_CHAR(DATINIZIOMOV, 'DD/MM/YYYY') DATINIZIO, "
				+ " TO_CHAR(DATFINEMOV, 'DD/MM/YYYY') DATFINE, "
				+ " AN_AZIENDA.PRGAZIENDA, AN_LAVORATORE.CDNLAVORATORE " + " FROM " + nomeTabella
				+ " , AN_AZIENDA, AN_LAVORATORE " + " WHERE " + nomeTabella
				+ ".STRCODICEFISCALE = AN_LAVORATORE.STRCODICEFISCALE(+) AND " + nomeTabella
				+ ".STRAZCODICEFISCALE = AN_AZIENDA.STRCODICEFISCALE(+) " + " AND CODCOMUNICAZIONE = '"
				+ codComunicazione + "' " + " AND CODTIPOCOMUNIC = '" + codTipoComunic
				+ "' AND CODTIPOMOV = 'AVV' AND NVL(FLGASSDACESS, 'N') = 'S' " + " AND UPPER(" + nomeTabella
				+ ".STRCODICEFISCALE) = '" + codiceFiscale + "'";
		result = ProcessorsUtils.executeSelectQuery(selectquery, trans);
		return result.getAttributeAsVector("ROW");
	}

	/**
	 * 
	 * @param v
	 *            (vettore che contiene il movimento da controllare)
	 * @return
	 */
	public boolean controllaMovimentoSomministrazione(Vector v) throws Exception {
		boolean risultato = false;
		SourceBean movimento = (SourceBean) v.get(0);
		String codTipoAzienda = StringUtils.getAttributeStrNotNull(movimento, "CODTIPOAZIENDA");
		String flgAssPropria = StringUtils.getAttributeStrNotNull(movimento, "FLGINTERASSPROPRIA");
		boolean notAssPropria = "N".equalsIgnoreCase(flgAssPropria);
		String datInizioMis = StringUtils.getAttributeStrNotNull(movimento, "DATINIZIORAPLAV");
		Object prgMovimento = movimento.getAttribute("PRGMOVIMENTO");
		Object prgAziendaUtiliz = movimento.getAttribute("PRGAZIENDAUTILIZ");
		if (notAssPropria && codTipoAzienda.equalsIgnoreCase("INT") && prgAziendaUtiliz != null
				&& !datInizioMis.equals("")) {
			// controllo se ci sono più missioni per quel movimento
			String selectquery = "SELECT COUNT(PRGMOVIMENTO) MISSIONI FROM AM_MOVIMENTO_MISSIONE WHERE PRGMOVIMENTO = "
					+ prgMovimento;
			SourceBean result = ProcessorsUtils.executeSelectQuery(selectquery, this.trans);
			Integer missioni = new Integer(result.getAttribute("ROW.MISSIONI").toString());
			if (missioni.intValue() > 1) {
				risultato = true;
			}
		}
		return risultato;
	}

	public boolean checkRettificheApprendistato(String codTipoAss, String codTipoContrattoPrec, String codTipoMovVal,
			ArrayList warnings, Map record) throws Exception {
		boolean res = false;
		if (codTipoAss.equalsIgnoreCase(DeTipoContrattoConstant.APPRENDISTATO_EX_ART16)
				|| codTipoAss.equalsIgnoreCase(DeTipoContrattoConstant.APPRENDISTATO_DIRITTO_DOVERE_ISTRUZIONE)
				|| codTipoAss.equalsIgnoreCase(DeTipoContrattoConstant.APPRENDISTATO_PROFESSIONALIZZANTE)
				|| codTipoAss.equalsIgnoreCase(DeTipoContrattoConstant.APPRENDISTATO_ALTA_FORMAZIONE)) {
			if (codTipoContrattoPrec
					.equalsIgnoreCase(DeTipoContrattoConstant.APPRENDISTATO_QUALIFICA_DIPLOMA_PROFESSIONALE)
					|| codTipoContrattoPrec.equalsIgnoreCase(
							DeTipoContrattoConstant.APPRENDISTATO_PROFESSIONALIZZANTE_O_CONTRATTO_DI_MESTIERE)
					|| codTipoContrattoPrec
							.equalsIgnoreCase(DeTipoContrattoConstant.APPRENDISTATO_ALTA_FORMAZIONE_RICERCA)
					|| codTipoContrattoPrec.equalsIgnoreCase(
							DeTipoContrattoConstant.APPRENDISTATO_QUALIFICA_DIPLOMA_PROFESSIONALE_IN_MOBILITA)
					|| codTipoContrattoPrec
							.equalsIgnoreCase(DeTipoContrattoConstant.APPRENDISTATO_CONTRATTO_DI_MESTIERE_IN_MOBILITA)
					|| codTipoContrattoPrec.equalsIgnoreCase(
							DeTipoContrattoConstant.APPRENDISTATO_ALTA_FORMAZIONE_RICERCA_IN_MOBILITA)) {
				record.put("CODTIPOASS", codTipoContrattoPrec);
				record.put("CODMONOTEMPO", "I");
				res = true;
				warnings.add(new Warning(MessageCodes.ImportMov.WAR_RETTIFICA_CONTRATTO_APPRENDISTATO, ""));
				if (codTipoMovVal.equalsIgnoreCase("PRO")) {
					record.put("CODTIPOMOV", "TRA");
					record.put("CODTIPOTRASF", TipoTrasfEnum.PROSECUZIONE_PERIODO_FORMATIVO.getCodice());
				}
			}
		}
		return res;
	}

}