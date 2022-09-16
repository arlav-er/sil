package it.eng.sil.action.report.disponibilita;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.dispatching.module.AbstractModule;
import com.engiweb.framework.dispatching.module.ModuleFactory;
import com.engiweb.framework.dispatching.module.ModuleIFace;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.AccessoSemplificato;
import it.eng.sil.module.firma.grafometrica.FirmaDocumenti;
import it.eng.sil.module.movimenti.InfoLavoratore;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.module.pi3.Pi3Constants;
import it.eng.sil.module.pi3.ProtocolloPi3DBManager;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;
import it.eng.sil.util.amministrazione.impatti.Controlli;
import it.eng.sil.util.amministrazione.impatti.ControlliException;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.DBStore;
import it.eng.sil.util.amministrazione.impatti.DIDManager;
import it.eng.sil.util.amministrazione.impatti.DidBean;
import it.eng.sil.util.amministrazione.impatti.ListaMobilita;
import it.eng.sil.util.amministrazione.impatti.MobilitaBean;
import it.eng.sil.util.amministrazione.impatti.MovimentoBean;
import it.eng.sil.util.amministrazione.impatti.PattoBean;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

public class DichiarazioneDisponibilita extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(DichiarazioneDisponibilita.class.getName());

	private static final String TRUE = "TRUE";
	private final static String CODMONOCPICOMP = "C";

	private boolean cambioCPICompetenza = false;

	/**
	 * Indica "delete/insert/update" andato a buon fine.
	 */
	protected final static String DELETE_OK = "DELETE_OK";
	protected final static String INSERT_OK = "INSERT_OK";
	protected final static String INSERT_FAIL = "INSERT_FAIL";
	protected final static String UPDATE_OK = "UPDATE_OK";
	protected final static String UPDATE_FAIL = "UPDATE_FAIL";

	/**
	 * Indica "select" andato a buon fine.
	 */
	protected final static String SELECT_OK = "SELECT_OK";
	protected final static String SELECT_FAIL = "SELECT_FAIL";

	AccessoSemplificato _db = new AccessoSemplificato(this);

	public void service(SourceBean request, SourceBean response) {

		super.service(request, response);

		String apriFile = (String) request.getAttribute("apriFileBlob");

		if ((apriFile != null) && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			try {
				String tipoFile = (String) request.getAttribute("tipoFile");
				Object cdnLavoratore = request.getAttribute("cdnLavoratore");
				Object prgDichDisp = request.getAttribute("prgDichDisp");

				if (tipoFile != null) {
					setStrNomeDoc("DichiarazioneImmediataDisponibilita." + tipoFile);
				} else {
					setStrNomeDoc("DichiarazioneImmediataDisponibilita.pdf");
				}

				// documento di input
				setCodMonoIO("I");
				setStrDescrizione("Dichiarazione immediata disponibilità");

				// controllo per regione reintrodotto per logo unione europea
				SourceBean beanRows = null;
				_db.setSectionQuerySelect("GET_CODREGIONE");
				beanRows = _db.doSelect(request, response, false);
				String regione = (String) beanRows.getAttribute("ROW.CODREGIONE");

				_db.setSectionQuerySelect("GET_RPTDID");
				SourceBean beanRowsrpt = _db.doSelect(request, response, false);
				String nomerpt = (String) beanRowsrpt.getAttribute("ROW.RPTDID");

				// NUOVA GESTIONE STAMPE, se c'è una riga 'custom' per questa provincia usala
				if (beanRowsrpt == null || nomerpt == null || nomerpt.length() <= 0)
					setReportPath("patto/DichImmDisp_CC.rpt");// DEFAULT
				else
					setReportPath("patto/" + nomerpt);

				// impostazione parametri del report
				Map prompts = new HashMap();

				prompts.put("par_DichDisp", prgDichDisp);
				prompts.put("cdnLavoratore", cdnLavoratore);
				prompts.put("regione", regione);

				User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
				String codCpiRifUtente = user.getCodRif();
				prompts.put("codCpi", codCpiRifUtente);

				// solo se e' richiesta la protocollazione i parametri vengono inseriti nella Map
				addPromptFieldsProtocollazione(prompts, request);
				prompts.put("intestazioneStampa", "OFF");

				// ora si chiede di usare il passaggio dei parametri per nome e
				// non per posizione (col vettore, passaggio di default)
				setPromptFields(prompts);

				// Settaggio strchiavetabella
				String strChiaveTabella = (String) request.getAttribute("strChiaveTabella");
				if ((strChiaveTabella != null) && !strChiaveTabella.equals("")) {
					setStrChiavetabella(strChiaveTabella);
				}

				String salva = (String) request.getAttribute("salvaDB");
				String apri = (String) request.getAttribute("apri");

				if ((salva != null) && salva.equalsIgnoreCase("true")) {
					// Savino 22/09/05: codStatoAtto del documento
					// purtroppo nella jsp il codStatoAtto, nella chiamata della
					// stampa del documento,
					// e' posta a PR. Per evitare di modificare la pagina jsp
					// modifico qui il valore.
					// Attenzione: al momento in cui scrivo il codice PA non e'
					// gestito dal documento
					String codStatoAtto = (String) request.getAttribute("codStatoAtto");
					if (request.getAttribute("dataOraProt") == null) {
						// se si vuole protocollare la data e' sicuramente
						// valorizzata.
						// non e' stata richiesta la protocollazione: lo stato
						// dell'atto rimane invariato: PA
						// anche se verra' sovrascritto

						getDocumento().setCodStatoAtto("PA");
						try {
							request.updAttribute("codStatoAtto", "PA");
						} catch (SourceBeanException e) {
						}
					} // else il default e' PR
					if (insertDocument(request, response, codCpiRifUtente)) {

						// GESTIONE FIRMA GRAFOMETRICA
						if (request.getAttribute("firmaGrafometrica") != null
								&& request.getAttribute("firmaGrafometrica").toString().equals("OK")) {

							try {

								Documento actualDocument = getDocumento();
								Documento prgDocumento = new Documento(actualDocument.getPrgDocumento());
								actualDocument.setNumKloDocumento(prgDocumento.getNumKloDocumento());
								_logger.debug("[DID] --> firmaGrafometrica --> NumKlo: "
										+ actualDocument.getNumKloDocumento());

								// VERIFICA SE IL TIPO DI DOCUMENTO E' PREDISPOSTO PER ESSERE FIRMATO GRAFOMETRICAMENTE
								ProtocolloPi3DBManager dbManager = new ProtocolloPi3DBManager();
								// boolean isDocumentFirmabile =
								// dbManager.isAllegatoDocumentoFirmato(actualDocument.getPrgDocumento().toString());
								BigDecimal prgTemplateStampa = dbManager
										.getPrgTemplateStampa(Pi3Constants.PI3_DOCUMENT_TYPE_DID);
								boolean isDocumentTypeFirmabile = dbManager
										.isDocumentTypeGraphSignature(prgTemplateStampa.toString());

								if (isDocumentTypeFirmabile) {

									TransactionQueryExecutor txExecutorFirma = null;

									try {
										FirmaDocumenti firma = new FirmaDocumenti();
										txExecutorFirma = new TransactionQueryExecutor(getPool(), this);
										String ipOperatore = request.getAttribute("ipOperatore").toString();
										boolean esitoFirma = firma.firmaDocumento(request, response, user,
												txExecutorFirma, actualDocument, ipOperatore);
										if (!esitoFirma) {
											response.updAttribute("messageResult",
													"Errore durante la firma grafometrica del documento.");
											throw new Exception("Errore durante la firma grafometrica del documento.");
										} else {
											setOperationSuccess(request, response);
										}
									} catch (Exception firmaEx) {
										setOperationFail(request, response, firmaEx);
									} finally {
										if (txExecutorFirma != null) {
											txExecutorFirma.closeConnTransaction();
											_logger.debug(
													"DICHIARAZIONE DISPONIBILITA - Gestione Firma Grafometrica - TransactionQueryExecutor close...");
										}
									}

								} else {
									it.eng.sil.util.TraceWrapper.debug(_logger,
											"DichiarazioneDisponibilita:service(): Tipo del Documento non firmabile grafometricamente, si procede alla trasformazione del documento normalmente senza applicazione dei campi firma",
											null);
								}

							} catch (Exception firmaEx) {
								it.eng.sil.util.TraceWrapper.error(_logger,
										"DichiarazioneDisponibilita:service():errore recupero firmabilita' grafometrica del tipo di documento",
										firmaEx);
								setOperationFail(request, response, firmaEx);
							}

						}

						// Gestione invio SAP
						ModuleIFace moduleGestioneInvioSAP = null;
						try {
							// Marianna Borriello settembre 2017: invio automatico sap dipendete da configurazione,
							// valore SAP_DID
							// Se 0 allora l'invio in automatico della SAP a seguito della Stipula della DID e' attivo,
							// se 1 allora l'invio automatico dopo la stipula della DID deve essere disabilitato.
							_db.setSectionQuerySelect("CONFIG_INVIO_SAP");
							SourceBean beanConfigInvioSap = _db.doSelect(request, response, false);
							String valoreConfig = (String) beanConfigInvioSap.getAttribute("ROW.STRVALORE");
							if (StringUtils.isFilledNoBlank(valoreConfig) && valoreConfig.equalsIgnoreCase("0")) {
								RequestContainer requestContainer = getRequestContainer();
								SourceBean serviceRequest = requestContainer.getServiceRequest();
								serviceRequest.setAttribute("INVIASAPFROMDID", "S");
								DefaultRequestContext drc = new DefaultRequestContext(getRequestContainer(),
										getResponseContainer());
								moduleGestioneInvioSAP = ModuleFactory.getModule("M_GestioneInvioSap");
								((AbstractModule) moduleGestioneInvioSAP).setRequestContext(drc);
								moduleGestioneInvioSAP.service(getRequestContainer().getServiceRequest(), response);
								DidBean.aggiornaNoteDid(user, prgDichDisp);
							} else if (StringUtils.isFilledNoBlank(valoreConfig)
									&& valoreConfig.equalsIgnoreCase("1")) {
								it.eng.sil.util.TraceWrapper.warn(_logger,
										"DichiarazioneDisponibilita: inviaSAP non effettuato da configurazione", null);
							}
						} catch (Exception me) {
							if (!response.containsAttribute("ERRORENOLOG")) {
								it.eng.sil.util.TraceWrapper.error(_logger,
										"DichiarazioneDisponibilita:service():errore inviaSAP", me);
							}
						}
						// Aggiorna competenza indice regionale solo per RER
						if (cambioCPICompetenza) {
							try {
								// Gestione configurazione aggiornamento competenza indice regionale (IR solo per RER)
								UtilsConfig utility = new UtilsConfig("AGCOMPIR");
								String configIR = utility.getConfigurazioneDefault_Custom();
								if (configIR.equals(Properties.CUSTOM_CONFIG)) {
									DidBean.aggiornaCompetenzaIR(user, cdnLavoratore);
								}
							} catch (Exception irex) {
								it.eng.sil.util.TraceWrapper.error(_logger,
										"DichiarazioneDisponibilita:service():aggiornaCompetenzaIR", irex);
							}
						}
					}
				} else if ((apri != null) && apri.equalsIgnoreCase("true")) {
					showDocument(request, response);
				} else {
					String errorCode = (String) response.getAttribute("errorCode");
					if (!StringUtils.isEmpty(errorCode)) {
						if (errorCode.equalsIgnoreCase(MessageCodes.General.CONCORRENZA + "")) {
							_logger.error(MessageCodes.General.CONCORRENZA);
							setOperationFail(request, response, MessageCodes.General.CONCORRENZA);
							try {
								response.setAttribute("ECCEZIONEPROTOCOLLA", MessageCodes.General.CONCORRENZA);
							} catch (SourceBeanException e) {
								e.printStackTrace();
							}
						}
					} else {

						Exception ex = new Exception("stampa del patto fallita");
						it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nella stampa del patto", ex);
						setOperationFail(request, response, ex);
					}
				}
			} catch (EMFUserError ue) {
				setOperationFail(request, response, ue);
				reportFailure(ue, "DichiarazioneDisponibilita.service()", "");
			}
		}

		// else
	}

	public boolean insertDocument(SourceBean request, SourceBean response, String codCpiRif) {
		TransactionQueryExecutor txExecutor = null;
		boolean result = false;
		Boolean resUpDID = new Boolean(false);
		// se presente questo campo e' stata richiesta la protocollazione
		boolean protocolla = request.containsAttribute("dataOraProt");
		Object cdnLavoratore = request.getAttribute("cdnlavoratore");
		try {
			txExecutor = new TransactionQueryExecutor(getPool(), this);
			txExecutor.initTransaction();

			String dataDichiarazione = (String) request.getAttribute("datDichiarazione");
			if (dataDichiarazione == null)
				dataDichiarazione = DateUtils.getNow();
			request.delAttribute("datInizio");
			request.setAttribute("datInizio", dataDichiarazione);
			// Settaggio data Inizio validità documento
			setDatInizio(dataDichiarazione);
			request.updAttribute("prgDichDisponibilita", new BigDecimal((String) request.getAttribute("prgDichDisp")));
			request.updAttribute("numKloDichDisp", new BigDecimal((String) request.getAttribute("numKloDichDisp")));

			Object[] obj = new Object[1];
			obj[0] = request.getAttribute("prgDichDisp");

			SourceBean row = (SourceBean) txExecutor.executeQuery("GET_INFO_DID", obj, "SELECT");
			if (row != null) {
				row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
				String flgRischioDisocc = Utils.notNull(row.getAttribute("FLGRISCHIODISOCCUPAZIONE"));
				if (flgRischioDisocc.equalsIgnoreCase(Values.FLAG_TRUE)) {
					request.updAttribute("flgRischioDisoccupazione", flgRischioDisocc);
				}
				String flgLavAutonomo = Utils.notNull(row.getAttribute("FLGLAVOROAUTONOMO"));
				if (flgLavAutonomo.equalsIgnoreCase(Values.FLAG_TRUE)) {
					request.updAttribute("flgLavoroAutonomo", flgLavAutonomo);
				}
			}

			if (protocolla) {
				// forzatura in ricostruzione storia per i messaggi riguardanti
				// stati occupazionali manuali
				if (!request.containsAttribute("CONTINUA_CALCOLO_SOCC"))
					request.setAttribute("CONTINUA_CALCOLO_SOCC", "true");
				else
					request.updAttribute("CONTINUA_CALCOLO_SOCC", "true");
				// forzatura in ricostruzione storia sulla mobilità
				if (!request.containsAttribute("FORZA_CHIUSURA_MOBILITA"))
					request.setAttribute("FORZA_CHIUSURA_MOBILITA", "true");
				else
					request.updAttribute("FORZA_CHIUSURA_MOBILITA", "true");
				List statiOccupaizonali = DIDManager.aggiornaDaDID(request, getRequestContainer(), response,
						txExecutor);
				StatoOccupazionaleBean statoOcc = (StatoOccupazionaleBean) statiOccupaizonali.get(0);
				request.getAttribute("prgStatoOccupaz");
				request.setAttribute("prgStatoOccupaz", statoOcc.getProgressivoDB());

				Boolean res = (Boolean) txExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
						getSelectStatement("QUERY_UPDATE_DICH_DISP"), "UPDATE");
				BigDecimal numKlo = (BigDecimal) request.getAttribute("NUMKLODICHDISP");
				numKlo = numKlo.add(new BigDecimal(1));
				request.updAttribute("NUMKLODICHDISP", numKlo);
				if (!request.containsAttribute("RICOSTRUZIONE_STORIA_ESEGUITA")) {
					// In questa fase devo eliminare i possibili stati
					// occupazionali che hanno la stessa dataInizio;
					// tra quelli con dataInizio uguale prendo il
					// prgStatoOccupaz max, se e solo se la protocollazione
					// della
					// did non ha fatto scattare la ricostruzione storia. Nel
					// caso contrario questa operazione è stata già fatta
					// alla fine della ricostruzione storia.
					Vector statiOccupazionaliNew = DBLoad.getStatiOccupazionali(cdnLavoratore, txExecutor);
					SourceBean tmp1 = null;
					SourceBean tmp2 = null;
					Vector listStOccDaCancellare = new Vector();
					for (int iCont = 0; iCont < statiOccupazionaliNew.size(); iCont++) {
						if (iCont < statiOccupazionaliNew.size() - 1) {
							tmp1 = (SourceBean) statiOccupazionaliNew.get(iCont);
							tmp2 = (SourceBean) statiOccupazionaliNew.get(iCont + 1);
							if (tmp1.getAttribute(StatoOccupazionaleBean.DB_DAT_INIZIO).toString()
									.equals(tmp2.getAttribute(StatoOccupazionaleBean.DB_DAT_INIZIO).toString())) {
								listStOccDaCancellare.add(tmp1);
							}
						}
					}
					if (listStOccDaCancellare.size() > 0) {
						Vector didsNew = DBLoad.getDichiarazioniDisponibilitaProtocollate(cdnLavoratore, "01/01/0001",
								txExecutor);
						Vector movimentiApertiNew = DBLoad.getMovimentiLavoratoreProtocollati(cdnLavoratore,
								txExecutor);
						Vector rowsMobilita = null;
						ListaMobilita mobilita = new ListaMobilita(cdnLavoratore, txExecutor);
						rowsMobilita = mobilita.getMobilita();
						resettaStatiOccupazionaliNormalizzati(statiOccupazionaliNew, listStOccDaCancellare,
								movimentiApertiNew, didsNew, rowsMobilita, txExecutor, request);
					}
				}

				// Si aggiornano alcuni campi della DID per lo stato occupaz.
				if (res.booleanValue()) {
					int ggSospResidui = 0;
					// Selezione dei campi necessari da inserire nella request
					// per la query
					SourceBean selRis = (SourceBean) txExecutor.executeQuery(getRequestContainer(),
							getResponseContainer(), getSelectStatement("QUERY_GET_CAMPI_STATO_OCCUPAZ_PROT_DID"),
							"SELECT");
					if (selRis != null) {
						BigDecimal t = null;
						BigDecimal t1 = null;
						BigDecimal t2 = null;

						request.delAttribute("CODSTATOOCCUPAZ");
						request.setAttribute("CODSTATOOCCUPAZ",
								StringUtils.getAttributeStrNotNull(selRis, "ROW.codstatooccupaz"));
						request.delAttribute("FLGINDENNIZZATO");
						request.setAttribute("FLGINDENNIZZATO",
								StringUtils.getAttributeStrNotNull(selRis, "ROW.flgindennizzato"));

						request.delAttribute("NUMMESISOSP");
						if (selRis.containsAttribute("ROW.NUMMESISOSP")) {
							t = (BigDecimal) selRis.getAttribute("ROW.NUMMESISOSP");
							t1 = (BigDecimal) selRis.getAttribute("ROW.mesiSospFornero2014");
							t2 = (BigDecimal) selRis.getAttribute("ROW.mesi_rischio_disocc");
							int numGGRestantiRischioDisocc = 0;
							Object mesiRischioDisoccCompleto = selRis.getAttribute("ROW.mesi_rischio_disocc_completo");
							if (mesiRischioDisoccCompleto != null && !mesiRischioDisoccCompleto.equals("")) {
								String[] rischioDisocc = mesiRischioDisoccCompleto.toString().split("-");
								if (rischioDisocc.length == 2) {
									numGGRestantiRischioDisocc = new Integer(rischioDisocc[1]).intValue();
								}
							}
							Object mesiSospForneroCompleto = selRis.getAttribute("ROW.mesiSospFornero2014_completo");
							int numGGRestantiSospFornero2014 = 0;
							if (mesiSospForneroCompleto != null && !mesiSospForneroCompleto.equals("")) {
								String[] sospFornero = mesiSospForneroCompleto.toString().split("-");
								if (sospFornero.length == 4) {
									numGGRestantiSospFornero2014 = new Integer(sospFornero[3]).intValue();
								}
							}
							ggSospResidui = numGGRestantiSospFornero2014 + numGGRestantiRischioDisocc;
							int mesiAggiuntivi = ggSospResidui / 30;
							BigDecimal tSosp = new BigDecimal(mesiAggiuntivi);

							if (t != null || t1 != null || t2 != null) {
								if (t != null) {
									tSosp = tSosp.add(t);
								}
								if (t1 != null) {
									tSosp = tSosp.add(t1);
								}
								if (t2 != null) {
									tSosp = tSosp.add(t2);
								}
							}
							if (tSosp != null) {
								request.setAttribute("NUMMESISOSP", tSosp.toString());
							}
						}

						if (selRis.containsAttribute("ROW.mesi_anz_calc")) {
							request.delAttribute("NUMMESIANZIANITA");
							t = (BigDecimal) selRis.getAttribute("ROW.mesi_anz_calc");
							BigDecimal tgiorniAnz = (BigDecimal) selRis.getAttribute("ROW.giorni_anz");
							if (tgiorniAnz == null) {
								tgiorniAnz = new BigDecimal(0);
							}
							int meseDiffAnzianitaGiorni = 0;
							int numGGAnzResidui = tgiorniAnz.intValue();
							if (numGGAnzResidui >= (ggSospResidui % 30)) {
								numGGAnzResidui = numGGAnzResidui - (ggSospResidui % 30);
							} else {
								if ((ggSospResidui % 30) > 0) {
									numGGAnzResidui = numGGAnzResidui + (30 - (ggSospResidui % 30));
									meseDiffAnzianitaGiorni = 1;
								}
							}
							if (t != null) {
								if (t.intValue() > 0 && meseDiffAnzianitaGiorni > 0) {
									t = t.subtract(new BigDecimal(meseDiffAnzianitaGiorni));
								}
								request.setAttribute("NUMMESIANZIANITA", t.toString());
							}
						}
						request.delAttribute("DATANZIANITADISOC");
						request.setAttribute("DATANZIANITADISOC",
								StringUtils.getAttributeStrNotNull(selRis, "ROW.DATANZIANITADISOC").toString());

						if (selRis.containsAttribute("ROW.mesi_anz_prec")) {
							request.delAttribute("NUMANZIANITAPREC297");
							t = (BigDecimal) selRis.getAttribute("ROW.mesi_anz_prec");
							request.setAttribute("NUMANZIANITAPREC297", t.toString());
						}

						request.delAttribute("CODMONOCALCOLOANZIANITAPREC297");
						request.setAttribute("CODMONOCALCOLOANZIANITAPREC297", StringUtils
								.getAttributeStrNotNull(selRis, "ROW.CODMONOCALCOLOANZIANITAPREC297").toString());
						request.delAttribute("DACALCOLOMESISOSP");
						request.setAttribute("DACALCOLOMESISOSP",
								StringUtils.getAttributeStrNotNull(selRis, "ROW.DATCALCOLOMESISOSP").toString());
						request.delAttribute("DATCALCOLOANZIANITA");
						request.setAttribute("DATCALCOLOANZIANITA",
								StringUtils.getAttributeStrNotNull(selRis, "ROW.DATCALCOLOANZIANITA").toString());

						if (selRis.containsAttribute("ROW.NUMMESISOSPPREC")) {
							request.delAttribute("NUMMESISOSPPREC");
							t = (BigDecimal) selRis.getAttribute("ROW.NUMMESISOSPPREC");
							request.setAttribute("NUMMESISOSPPREC", t.toString());
						}

						// Recupero delle info necessarie calcolate
						SourceBean temp = null;
						Vector statoOccRows = null;
						Vector cat181Rows = null;
						Vector laureaRows = null;
						Vector movimenti = null;

						temp = (SourceBean) txExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
								getSelectStatement("QUERY_GETSTATOOCCUPAZIONALE"), "SELECT");
						if (temp != null)
							statoOccRows = temp.getAttributeAsVector("ROW");

						temp = (SourceBean) txExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
								getSelectStatement("QUERY_GET181CAT"), "SELECT");
						if (temp != null)
							cat181Rows = temp.getAttributeAsVector("ROW");

						temp = (SourceBean) txExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
								getSelectStatement("QUERY_GetLaureaPerCat181"), "SELECT");
						if (temp != null)
							laureaRows = temp.getAttributeAsVector("ROW");

						temp = (SourceBean) txExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
								getSelectStatement("QUERY_GETMOVIMENTI"), "SELECT");
						if (temp != null)
							movimenti = temp.getAttributeAsVector("ROW");
						SourceBean rowTmp = null;

						// Parte per le info aggiuntive per lo stato occupaz
						// **************************************************
						BigDecimal mesiInattivita = null;
						String dataFineMovimento = null;
						String cat181 = "";
						String disoccInoccText = "";
						String codStatoOccRagg = null;
						String sesso = null;
						BigDecimal mesiAnz = null;
						BigDecimal giorniAnzResidui = null;
						BigDecimal numMesiSosp = null;
						Object nMesiSospFornero = null;
						Object nMesiRischioDisocc = null;
						BigDecimal numMesiSospFornero = null;
						BigDecimal mesiRischioDisocc = null;
						BigDecimal numMesiSospPrec = null;
						BigDecimal mesiAnzPrec = null;
						Object mesiRischioDisoccCompleto = null;
						BigDecimal numGGRestantiRischioDisocc = null;
						BigDecimal numGGRestantiSospFornero = null;
						BigDecimal ggSospForneroRischioDisocc = null;

						if (statoOccRows != null && !statoOccRows.isEmpty()) {
							rowTmp = (SourceBean) statoOccRows.elementAt(0);
							mesiAnz = (BigDecimal) rowTmp.getAttribute("MESI_ANZ");
							if (mesiAnz == null) {
								mesiAnz = new BigDecimal(0);
							}
							giorniAnzResidui = (BigDecimal) rowTmp.getAttribute("GIORNI_ANZ");
							if (giorniAnzResidui == null) {
								giorniAnzResidui = new BigDecimal(0);
							}
							mesiAnzPrec = (BigDecimal) rowTmp.getAttribute("MESI_ANZ_PREC");
							if (mesiAnzPrec == null) {
								mesiAnzPrec = new BigDecimal(0);
							}
							numMesiSosp = (BigDecimal) rowTmp.getAttribute("NUMMESISOSP");
							if (numMesiSosp == null) {
								numMesiSosp = new BigDecimal(0);
							}
							numMesiSospPrec = (BigDecimal) rowTmp.getAttribute("NUMMESISOSPPREC");
							if (numMesiSospPrec == null) {
								numMesiSospPrec = new BigDecimal(0);
							}
							nMesiSospFornero = rowTmp.getAttribute("mesiSospFornero2014");

							if (nMesiSospFornero != null && !nMesiSospFornero.equals("")) {
								String[] sospFornero = nMesiSospFornero.toString().split("-");
								if (sospFornero.length == 4) {
									numMesiSospFornero = new BigDecimal(sospFornero[0]);
									numGGRestantiSospFornero = new BigDecimal(sospFornero[3]);
								} else {
									numMesiSospFornero = new BigDecimal(0);
									numGGRestantiSospFornero = new BigDecimal(0);
								}
							} else {
								numMesiSospFornero = new BigDecimal(0);
								numGGRestantiSospFornero = new BigDecimal(0);
							}

							nMesiRischioDisocc = rowTmp.getAttribute("mesi_rischio_disocc");
							if (nMesiRischioDisocc == null) {
								mesiRischioDisocc = new BigDecimal(0);
							} else {
								mesiRischioDisocc = new BigDecimal(nMesiRischioDisocc.toString());
							}

							mesiRischioDisoccCompleto = rowTmp.getAttribute("mesi_rischio_disocc_completo");
							if (mesiRischioDisoccCompleto != null && !mesiRischioDisoccCompleto.equals("")) {
								String[] rischioDisocc = mesiRischioDisoccCompleto.toString().split("-");
								if (rischioDisocc.length == 2) {
									numGGRestantiRischioDisocc = new BigDecimal(rischioDisocc[1]);
								} else {
									numGGRestantiRischioDisocc = new BigDecimal(0);
								}
							} else {
								numGGRestantiRischioDisocc = new BigDecimal(0);
							}
							ggSospForneroRischioDisocc = numGGRestantiSospFornero.add(numGGRestantiRischioDisocc);
							int mesiAggiuntivi = (ggSospForneroRischioDisocc.intValue()) / 30;
							int meseDiffAnzianitaGiorni = 0;
							if (giorniAnzResidui.intValue() >= (ggSospForneroRischioDisocc.intValue() % 30)) {
								giorniAnzResidui = giorniAnzResidui.subtract(ggSospForneroRischioDisocc);
							} else {
								if ((ggSospForneroRischioDisocc.intValue()) % 30 > 0) {
									BigDecimal appAnz = new BigDecimal(
											30 - (ggSospForneroRischioDisocc.intValue() % 30));
									giorniAnzResidui = giorniAnzResidui.add(appAnz);
									meseDiffAnzianitaGiorni = 1;
								}
							}

							mesiAnz = mesiAnz.add(mesiAnzPrec)
									.subtract(numMesiSosp.add(numMesiSospPrec).add(numMesiSospFornero)
											.add(mesiRischioDisocc).add(new BigDecimal(mesiAggiuntivi)));

							if (mesiAnz.intValue() > 0 && meseDiffAnzianitaGiorni > 0) {
								mesiAnz = mesiAnz.subtract(new BigDecimal(meseDiffAnzianitaGiorni));
							}

							codStatoOccRagg = (String) rowTmp.getAttribute("codstatooccupazragg");

							InfoLavoratore infoLav = new InfoLavoratore(
									(BigDecimal) rowTmp.getAttribute("cdnLavoratore"));
							sesso = infoLav.getSesso();// (String)rowTmp.getAttribute("STRSESSO");
						}
						// movimenti
						if (movimenti != null && movimenti.size() > 0) {
							rowTmp = (SourceBean) movimenti.get(0);
							dataFineMovimento = (String) rowTmp.getAttribute("DATAFINEMOVIMENTO");
							mesiInattivita = (BigDecimal) rowTmp.getAttribute("mesiInattivita");
						}
						BigDecimal eta = new BigDecimal("-1");
						String flgObbSco = null;
						String flgLaurea = "";
						String annoNascita = null;
						String donnaInReinserimento = "";
						if (cat181Rows != null && !cat181Rows.isEmpty()) {
							rowTmp = (SourceBean) cat181Rows.elementAt(0);
							eta = (BigDecimal) rowTmp.getAttribute("ANNI");
							flgObbSco = (String) rowTmp.getAttribute("FLGOBBLIGOSCOLASTICO");
							annoNascita = (String) rowTmp.getAttribute("datNasc");
						}
						flgLaurea = laureaRows != null && !laureaRows.isEmpty() ? "S" : "N";
						cat181 = Controlli.getCat181(annoNascita, dataDichiarazione, flgObbSco, flgLaurea);
						String flg40790 = "";
						String codCat181 = null;
						if (cat181 != null && cat181.equalsIgnoreCase("GIOVANE"))
							codCat181 = "G";
						else if (cat181 != null && cat181.equalsIgnoreCase("ADOLESCENTE"))
							codCat181 = "A";

						if (mesiAnz == null) {
							mesiAnz = new BigDecimal(0);
						}
						// BigDecimal totMesiAnz = new
						// BigDecimal(String.valueOf(mesiAnzPrec.add(mesiAnz)));
						// totMesiAnz =
						// totMesiAnz.subtract(numMesiSospPrec.add(numMesiSosp));
						BigDecimal totMesiAnz = mesiAnz;
						disoccInoccText = Controlli.disoccInoccLungaDurata(codStatoOccRagg, totMesiAnz, codCat181);// mesiAnz
						String codMonoDisoccInocc = "";
						if ((disoccInoccText != null) && disoccInoccText.equals("Disoccupato di lunga durata"))
							codMonoDisoccInocc = "D";
						else if ((disoccInoccText != null) && disoccInoccText.equals("Inoccupato di lunga durata"))
							codMonoDisoccInocc = "I";

						boolean donnaInReinserimentoB = Controlli.donnaInInserimentoLavorativo(codStatoOccRagg,
								mesiInattivita, sesso);
						if (donnaInReinserimentoB)
							donnaInReinserimento = "S";
						else
							donnaInReinserimento = "N";
						if (totMesiAnz != null && totMesiAnz.compareTo(new BigDecimal(24)) >= 0) {// mesiAnz
							flg40790 = "S";
						}
						request.delAttribute("FLG40790");
						if (flg40790 != null)
							request.setAttribute("FLG40790", flg40790);
						request.delAttribute("FLGCAT181DONNAREINSLAV");
						if (donnaInReinserimento != null)
							request.setAttribute("FLGCAT181DONNAREINSLAV", donnaInReinserimento);
						request.delAttribute("CODMONOCAT181LUNGADURATA");
						if (codMonoDisoccInocc != null)
							request.setAttribute("CODMONOCAT181LUNGADURATA", codMonoDisoccInocc);
						request.delAttribute("CODMONOCAT181ETA");
						if (codCat181 != null)
							request.setAttribute("CODMONOCAT181ETA", codCat181);
						// **************************************************

						resUpDID = (Boolean) txExecutor.executeQuery(getRequestContainer(), getResponseContainer(),
								getSelectStatement("QUERY_UPDATE_DICH_DISP_IFO_STATO_OCCUPAZ"), "UPDATE");
					}
				}

				if (!res.booleanValue())
					throw new Exception(
							"impossibile associare il nuovo stato occupazionale alla dichiarazione di immediata disponibilta'");

				if (!resUpDID.booleanValue())
					throw new Exception(
							"impossibile associare le informazioni aggiuntive per lo stato occupazionale alla dichiarazione di immediata disponibilta'");
			}

			Object[] objDoppi = new Object[3];
			objDoppi[0] = "IM";
			objDoppi[1] = new BigDecimal("25");
			objDoppi[2] = getDocumento().getChiaveTabella();
			SourceBean sbDocDoppi = (SourceBean) txExecutor.executeQuery("GET_DOCUMENTO_DOPPIO_DID", objDoppi,
					"SELECT");
			if (sbDocDoppi != null && sbDocDoppi.getAttribute("ROW") != null) {
				throw new Exception("Documento già presente per la did");
			}

			insert(txExecutor);

			// il cpi di riferimento diventa competente qualora non lo fosse prima della DID
			User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
			cambioCPICompetenza = DidBean.aggiornaCPI(cdnLavoratore, codCpiRif, new BigDecimal(user.getCodut()),
					txExecutor);

			// PER PROTOCOLLAZIONE PI3: AGGIUNTA DOCUMENTO ASSOCIATO CARTA D'IDENTITA' IN ALLEGATI
			SourceBean beanRowsIdentify = null;
			BigDecimal prgDocumentoIdentify = null;
			_db.setSectionQuerySelect("GET_DOC_IDENTIFICAZIONE_ASSOCIATO");
			beanRowsIdentify = _db.doSelect(request, response, false);
			Vector vRows = beanRowsIdentify.getAttributeAsVector("ROW");
			if (vRows.size() > 0) {
				prgDocumentoIdentify = (BigDecimal) ((SourceBean) vRows.get(0)).getAttribute("PRGDOCUMENTO");

				Documento actualDocument = getDocumento();

				Object params[] = new Object[6];
				params[0] = actualDocument.getPrgDocumento(); // prgDocPadre
				params[1] = prgDocumentoIdentify; // prgDocCurr della Carta di Identita
				params[2] = "N"; // PresaVisione = "N" o "S"?
				params[3] = "N"; // CaricamentoSuccessivo = "N" o "S"?
				params[4] = user.getCodut();
				params[5] = user.getCodut();

				Boolean res = (Boolean) txExecutor.executeQuery("INSERT_DOC_ALLEGATO_STAMPA_PARAM", params, "INSERT");
			}

			txExecutor.commitTransaction();

			//
			setOperationSuccess(response, response);

			result = true;
		}

		catch (EMFInternalError emf) {
			if (txExecutor != null) {
				try {
					txExecutor.rollBackTransaction();
				} catch (EMFInternalError e) {
					e.printStackTrace();
				}
			}
			_logger.error(emf.getMessage());

			if (emf.getNativeException() instanceof SQLException) {
				if (((SQLException) emf.getNativeException()).getErrorCode() == MessageCodes.General.CONCORRENZA) {
					setOperationFail(request, response, MessageCodes.General.CONCORRENZA);
					try {
						response.setAttribute("ECCEZIONEPROTOCOLLA", MessageCodes.General.CONCORRENZA);
					} catch (SourceBeanException e) {
						_logger.error(e.getMessage());
					}
				} else {
					setOperationFail(request, response, MessageCodes.General.OPERATION_FAIL);
					try {
						response.setAttribute("ECCEZIONEPROTOCOLLA", MessageCodes.General.OPERATION_FAIL);
					} catch (SourceBeanException e) {
						_logger.error(e.getMessage());
					}
				}
			} else {
				setOperationFail(request, response, MessageCodes.General.OPERATION_FAIL);
				try {
					response.setAttribute("ECCEZIONEPROTOCOLLA", MessageCodes.General.OPERATION_FAIL);
				} catch (SourceBeanException e) {
					_logger.error(e.getMessage());
				}
			}
		}

		catch (ControlliException ce) {
			try {
				if (txExecutor != null) {
					txExecutor.rollBackTransaction();
				}
				setOperationFail(request, response);
				reportFailure(ce, "DichiarazioneDisponibilita:insertDocument()",
						"impossibile inserire e protocollare la dichiarazione di immediata disponibilita'");

				// response.setAttribute("operationResult", "ERROR");
				it.eng.sil.util.TraceWrapper.debug(_logger, "DichiarazioneDisponibilita:insertDocument()",
						(Exception) ce);
			} catch (Exception txe) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "DichiarazioneDisponibilita:insertDocument()", txe);

			}

			result = false;
		} catch (Exception e) {
			try {
				if (txExecutor != null) {
					txExecutor.rollBackTransaction();
				}
				setOperationFail(request, response, e);
				reportFailure(MessageCodes.General.INSERT_FAIL, e, "DichiarazioneDisponibilita:insertDocument()",
						"impossibile inserire e protocollare la dichiarazione di immediata disponibilita'");

				// response.setAttribute("operationResult", "ERROR");
				it.eng.sil.util.TraceWrapper.debug(_logger, "DichiarazioneDisponibilita:insertDocument()", e);
			} catch (Exception txe) {
				it.eng.sil.util.TraceWrapper.debug(_logger, "DichiarazioneDisponibilita:insertDocument()", txe);

			}

			result = false;
		}

		return result;
	}

	/**
	 * 
	 */
	protected String getPool() {
		return (String) getConfig().getAttribute("POOL");
	}

	/**
	 * 
	 */
	protected SourceBean getSelectStatement(String queryName) {
		SourceBean beanQuery = null;
		beanQuery = (SourceBean) getConfig().getAttribute(queryName);

		return beanQuery;
	}

	private void resettaStatiOccupazionaliNormalizzati(Vector listaCompleta, Vector listaCancella, Vector movimenti,
			Vector dids, Vector vetMobilita, TransactionQueryExecutor txExecutor, SourceBean request) throws Exception {
		BigDecimal prgStatoOcc = null;
		int i = 0;
		int j = 0;
		int posizione = 0;
		SourceBean sbApp = null;
		BigDecimal prgNewStatoOccupaz = null;
		StatoOccupazionaleBean soApp = null;
		SourceBean sbCancella = null;
		SourceBean sbCompleta = null;
		String dataInizioCancella = "";
		String dataInizioCompleta = "";
		// devo ricavare il nuovo stato occupazionale(quello che ha la
		// stessa data Inizio, elemento che appartiene al
		// vettore listaCompleta e non appartiene al vettore listaCancella)
		Vector vettCorrispondenzeStatoOcc = new Vector();
		for (; i < listaCancella.size(); i++) {
			sbCancella = (SourceBean) listaCancella.get(i);
			dataInizioCancella = sbCancella.getAttribute(StatoOccupazionaleBean.DB_DAT_INIZIO).toString();
			for (; j < listaCompleta.size(); j++) {
				sbCompleta = (SourceBean) listaCompleta.get(j);
				dataInizioCompleta = sbCompleta.getAttribute(StatoOccupazionaleBean.DB_DAT_INIZIO).toString();
				if (dataInizioCancella.equals(dataInizioCompleta)) {
					posizione = j;
				}
			}
			sbCompleta = (SourceBean) listaCompleta.get(posizione);
			vettCorrispondenzeStatoOcc.add(sbCompleta);
		}
		i = 0;
		for (; i < listaCancella.size(); i++) {
			sbApp = (SourceBean) vettCorrispondenzeStatoOcc.get(i);
			soApp = new StatoOccupazionaleBean(sbApp);
			prgNewStatoOccupaz = soApp.getPrgStatoOccupaz();
			SourceBean sb = (SourceBean) listaCancella.get(i);
			StatoOccupazionaleBean so = new StatoOccupazionaleBean(sb);
			prgStatoOcc = so.getPrgStatoOccupaz();
			// deassocio i movimenti che hanno lo stato occupazionale che si
			// deve cancellare
			dereferenziaMovimentiNormalizzati(movimenti, so, soApp, txExecutor);
			List pattiAssociatiSO = DBLoad.getPattoAssociatoStatoOccupaz(prgStatoOcc, txExecutor);
			if (pattiAssociatiSO.size() > 0) {
				dereferenziaPatti(pattiAssociatiSO, prgNewStatoOccupaz, txExecutor);
			}

			// deassocio did che hanno lo stato occupazionale che si deve
			// cancellare
			DidBean did = null;
			BigDecimal prgDichDispoInIns = new BigDecimal(request.getAttribute("prgDichDisp").toString());
			for (int k = 0; k < dids.size(); k++) {
				did = new DidBean((SourceBean) dids.get(k));
				if (did.getPrgStatoOccupaz() != null && did.getPrgStatoOccupaz().equals(prgStatoOcc)) {
					BigDecimal numKlo = (BigDecimal) did.getAttribute("numKloDichDisp");
					numKlo = numKlo.add(new BigDecimal(1));
					DBStore.riassociaDIDSOccInStoria(did.getPrgDichDisponibilita(), prgNewStatoOccupaz, numKlo,
							RequestContainer.getRequestContainer(), txExecutor);
					// aggiorno NUMKLODICHDISP se sto aggiornando lo stato
					// occupaz della did in inserimento
					// per eventuali aggiornamenti successivi
					// (QUERY_UPDATE_DICH_DISP_IFO_STATO_OCCUPAZ)
					if (prgDichDispoInIns.equals(did.getPrgDichDisponibilita())) {
						request.updAttribute("NUMKLODICHDISP", numKlo);
					}
				}
			}

			// deassocio le mobilità che hanno lo stato occupazionale che si
			// deve cancellare
			MobilitaBean mobilita = null;
			for (int k = 0; k < vetMobilita.size(); k++) {
				mobilita = new MobilitaBean((SourceBean) vetMobilita.get(k));
				if (mobilita.getPrgStatoOccupaz() != null && mobilita.getPrgStatoOccupaz().equals(prgStatoOcc)) {
					mobilita.aggiornaStatoOccupaz(prgNewStatoOccupaz, RequestContainer.getRequestContainer(),
							txExecutor);
				}
			}

			// cancellazione stato occupazionale
			DBStore.cancellaStatoOccupazionale(prgStatoOcc, txExecutor);
		}
	}

	private void dereferenziaMovimentiNormalizzati(List movimenti, StatoOccupazionaleBean statoOccupazionale,
			StatoOccupazionaleBean soNew, TransactionQueryExecutor txExecutor) throws Exception {
		int i = 0;
		BigDecimal prgStatoOcc = statoOccupazionale.getPrgStatoOccupaz();
		BigDecimal prgso = null;
		_logger.debug(
				"SituazioneAmministrativa.dereferenziaMovimentiNormalizzati():ricerca per prgStOcc:" + prgStatoOcc);

		for (; i < movimenti.size(); i++) {
			SourceBean sbEvento = (SourceBean) movimenti.get(i);
			if (sbEvento.getAttribute("codtipomov").toString().equals("AVV")
					|| sbEvento.getAttribute("codtipomov").toString().equals("TRA")
					|| sbEvento.getAttribute("codtipomov").toString().equals("PRO")
					|| sbEvento.getAttribute("codtipomov").toString().equals("CES")) {
				MovimentoBean mb = new MovimentoBean(sbEvento);
				if (mb.virtuale() || mb.getAttribute(MovimentoBean.DB_PRG_STATO_OCCUPAZ) == null)
					continue;
				prgso = (BigDecimal) mb.getAttribute(MovimentoBean.DB_PRG_STATO_OCCUPAZ);
				if (prgStatoOcc.equals(prgso)) {
					mb.setStatoOccupazionale(soNew);
					DBStore.aggiornaMovimento(mb, RequestContainer.getRequestContainer(), txExecutor);
				}
			}
		}
	}

	private void dereferenziaPatti(List patti, BigDecimal prgStatoOcc, TransactionQueryExecutor txExecutor)
			throws Exception {
		for (int i = 0; i < patti.size(); i++) {
			if (patti.get(i) instanceof PattoBean) {
				PattoBean patto = (PattoBean) patti.get(i);
				DBStore.aggiornaPatto297(patto, prgStatoOcc, RequestContainer.getRequestContainer(), txExecutor);
			} else {
				SourceBean sbPatto = (SourceBean) patti.get(i);
				DBStore.aggiornaPatto297(sbPatto, prgStatoOcc, RequestContainer.getRequestContainer(), txExecutor);
			}
		}
	}

}