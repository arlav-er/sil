package it.eng.sil.coop.webservices.clicLavoro.candidatura;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.dbaccess.sql.DBKeyGenerator;
import it.eng.afExt.utils.CF_utils;
import it.eng.afExt.utils.CfException;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.coop.DataSourceJNDI;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.messages.jmsmessages.InvioCandidaturaMyPortalMessage;
import it.eng.sil.coop.queues.OutQ;
import it.eng.sil.coop.webservices.clicLavoro.CLUtility;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.cigs.bean.BeanUtils;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;

public class CLCandidaturaInviaMassivo extends AbstractSimpleModule {

	private static final long serialVersionUID = 1L;
	private static final String CODTIPOCOMUNICAZIONE_CLICLAVORO = "01";
	private static final int CURRICULUM_SCARTATO = 1;
	private static final int CURRICULUM_INVIATO = 0;

	private static final String SERVIZIO_CLIC_LAVORO = "InvioCandidaturaMyPortal";

	static Logger _logger = Logger.getLogger(CLCandidaturaInviaMassivo.class.getName());

	public void service(SourceBean request, SourceBean response)
			throws SourceBeanException, EMFInternalError, ParseException, EMFUserError {

		SourceBean srcResponse = getResponseContainer().getServiceResponse();
		User objUser = (User) RequestContainer.getRequestContainer().getSessionContainer().getAttribute(User.USERID);
		int idSuccess = this.disableMessageIdSuccess();
		String pageName = Utils.notNull(request.getAttribute("PAGE"));
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		/// GESTIONE DATA SCADENZA
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date today = Calendar.getInstance().getTime();

		String dataInvio = df.format(today);

		int gg_scadenza = 0;

		SourceBean confgGiorniScad = (SourceBean) srcResponse.getAttribute("M_CONFIG_D_VAL_CURR.ROWS.ROW");
		String valoreGiorni = null;
		if (confgGiorniScad != null) {
			valoreGiorni = StringUtils.getAttributeStrNotNull(confgGiorniScad, "strvalore");
		}

		if (valoreGiorni != null && !valoreGiorni.isEmpty()) {
			gg_scadenza = Integer.valueOf(valoreGiorni).intValue();
		}

		Date maxDateScad = DateUtils.addDays(today, gg_scadenza);
		String dataScadenza = df.format(maxDateScad);

		/////////////////////////////////
		// ESTRAZIONE PARAMETRI PER WS //
		/////////////////////////////////

		SourceBean userWs = (SourceBean) QueryExecutor.executeQuery("GET_WS_CREDENTIALS",
				new Object[] { "SIL_CLICLAV_MYPORTAL" }, "SELECT", Values.DB_SIL_DATI);
		String username = (String) userWs.getAttribute("ROW.STRUSERID");
		String pwd = (String) userWs.getAttribute("ROW.cln_pwd");

		int countOk = 0;
		int countNOK = 0;

		/////////////////////////////
		// LETTURA DATI DA REQUEST //
		/////////////////////////////

		String codCpiCapoluogo = BeanUtils.getObjectToString(request, "codCPI", null);
		String codtipocomunicazionecl = BeanUtils.getObjectToString(request, "codtipocomunicazionecl", null);
		String codcandidatura = BeanUtils.getObjectToString(request, "codcandidatura", null);

		//////////////////////////////////////
		// LETTURA DATI DA MODULI ASSOCIATI //
		//////////////////////////////////////

		SourceBean countIncorso = (SourceBean) srcResponse.getAttribute("M_Check_Invio_InCorso.ROWS.ROW");
		BigDecimal numInCorso = ((BigDecimal) countIncorso.getAttribute("NUM"));
		int numInCorsoTmp = -1;
		if (numInCorso != null) {
			numInCorsoTmp = numInCorso.intValue();
		}

		if (numInCorsoTmp == 0) { // solo in tal caso non ci sono altre esecuzioni in corso

			String numElaborazioni = BeanUtils.getObjectToString(request, "NUMCVDACARICARE", null);
			Date datinizioelaborazione = new Date();
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String strdatinizioelaborazione = format.format(datinizioelaborazione);
			boolean checkInsert = false;
			checkInsert = inserisciTmpOperazioniMassive(numElaborazioni, strdatinizioelaborazione);
			if (!checkInsert) {
				reportOperation.reportFailure(MessageCodes.ClicLavoro.CODE_ERR_INTERNO);
				return;
			}

			Vector<SourceBean> vettRis = srcResponse.getAttributeAsVector("M_Load_Tmp_Massivo.ROWS.ROW");

			String cdnLavoratore = null;
			String codiceFiscale = null;
			String prgpattolavoratore = null;
			String stato = null;
			String codesito = null;
			String descresito = null;

			boolean hasError = false; // check per controllo errori formali prima dell'invio

			if (vettRis != null && !vettRis.isEmpty()) { // da eliminare perché il controllo sul numero necessario di
															// lavoratori per i quali effettuare l'invio avviene già
															// nella definizione della page in questione
				for (SourceBean object : vettRis) {

					cdnLavoratore = BeanUtils.getObjectToString(object, "cdnLavoratore", null);
					codiceFiscale = BeanUtils.getObjectToString(object, "codicefiscale", null);
					prgpattolavoratore = BeanUtils.getObjectToString(object, "prgpattolavoratore", null);
					// i campi successivi sono forse da eliminare non sono utili ai fini del filtraggio dei lavoratori
					// interessati all'invio
					/*
					 * stato = BeanUtils.getObjectToString(object, "stato", null); codesito =
					 * BeanUtils.getObjectToString(object, "codesito", null); descresito =
					 * BeanUtils.getObjectToString(object, "descresito", null);
					 */

					String tipoComunicazione = "";
					boolean checkErrorUpdate = false;

					if ("INVIO_MASSIVO_CV_PAGE".equalsIgnoreCase(pageName)) {
						tipoComunicazione = CLUtility.TIPOCOMUNICAZIONECL;
					}

					if ("INVIO_MASSIVO_CV_PAGE".equalsIgnoreCase(pageName)) {

						////// PRIMO BLOCCO CONTROLLI CHECK ///////

						hasError = checkCandidatura(cdnLavoratore);

						if (hasError) {

							int messageCode = MessageCodes.ClicLavoro.CV_NON_INVIABILE_UNVIO_MASSIVO;
							String problem = "CANDIDATURA GIA' INVIATA";
							updateError(cdnLavoratore, codiceFiscale, CURRICULUM_SCARTATO, problem, reportOperation,
									messageCode, null);
							countNOK++;
							continue;
						}

						hasError = checkCvValidoPeriodo(cdnLavoratore, dataInvio, dataScadenza);

						if (hasError) {
							int messageCode = MessageCodes.ClicLavoro.CV_NON_INVIABILE_UNVIO_MASSIVO;
							String problem = "NON ESISTE CV VALIDO PERIODO CANDIDATURA";
							updateError(cdnLavoratore, codiceFiscale, CURRICULUM_SCARTATO, problem, reportOperation,
									messageCode, null);
							countNOK++;
							continue;
						}

						hasError = checkCittadinanzaValida(cdnLavoratore);

						if (hasError) {
							int messageCode = MessageCodes.ClicLavoro.CV_NON_INVIABILE_UNVIO_MASSIVO;
							String problem = "Attenzione: la cittadinanza indicata per il lavoratore risulta essere una codifica scaduta";
							updateError(cdnLavoratore, codiceFiscale, CURRICULUM_SCARTATO, problem, reportOperation,
									messageCode, null);
							countNOK++;
							continue;
						}
					}
					/////////////////////////////////////////
					// ANALISI ANAGRAFICA LAVORATORE DA DB //
					/////////////////////////////////////////

					String titoloFiguraProf = null;

					Object[] inputParametersCF = new Object[1];
					inputParametersCF[0] = cdnLavoratore;
					SourceBean retCF = (SourceBean) QueryExecutor.executeQuery("GET_AN_LAVORATORE_ANAG",
							inputParametersCF, "SELECT", Values.DB_SIL_DATI);
					String cognome = (String) retCF.getAttribute("ROW.STRCOGNOME");
					String nome = (String) retCF.getAttribute("ROW.STRNOME");
					String sesso = (String) retCF.getAttribute("ROW.STRSESSO");
					String dataNascita = (String) retCF.getAttribute("ROW.DATNASC");
					String comuneNascita = (String) retCF.getAttribute("ROW.CODCOMNAS");
					boolean checkCittadinanza = retCF.containsAttribute("ROW.CODCITTADINANZA");
					boolean checkComuneDomicilio = retCF.containsAttribute("ROW.CODCOMDOM");

					// verifico se il nome e cognome del lavoratore sono presenti per consentire il
					// corretto caricamento del titoloFiguraProf
					hasError = checkTitoloCV(nome, cognome);
					if (hasError) {

						int messageCode = MessageCodes.ClicLavoro.CV_NON_INVIABILE_UNVIO_MASSIVO;
						String problem = "DATI MANCANTI NOME/COGNOME";
						updateError(cdnLavoratore, codiceFiscale, CURRICULUM_SCARTATO, problem, reportOperation,
								messageCode, null);
						countNOK++;
						continue;
					}

					titoloFiguraProf = "CV " + nome + " " + cognome;

					List<String> codAmbDiff = new ArrayList<String>();
					codAmbDiff.add("01"); // 01 - Ambito Regionale

					String codstatoinviocl = request.getAttribute("codstatoinviocl") == null ? ""
							: (String) request.getAttribute("codstatoinviocl");
					if ("PA".equals(codstatoinviocl)) {
						codstatoinviocl = "PA";
					} else {
						codstatoinviocl = "VA";
					}

					boolean res;
					String prgcandidatura = BeanUtils.getObjectToString(request, "prgcandidatura", null); // dovrebbe
																											// essere
																											// eliminato

					////// SECONDO BLOCCO CONTROLLI CHECK ///////

					if (!hasError) { // allora proseguo con ulteriori controlli bloccanti

						if (!checkCittadinanza) {

							String problem = "La cittadinanza indicata per il lavoratore risulta essere mancante";
							int messageCode = MessageCodes.ClicLavoro.CV_NON_INVIABILE_UNVIO_MASSIVO;
							updateError(cdnLavoratore, codiceFiscale, CURRICULUM_SCARTATO, problem, reportOperation,
									messageCode, null);
							countNOK++;
							continue;
						}

						if (!checkComuneDomicilio) {

							String problem = "Il comune di domicilio indicato per il lavoratore risulta essere mancante";
							int messageCode = MessageCodes.ClicLavoro.CV_NON_INVIABILE_UNVIO_MASSIVO;
							updateError(cdnLavoratore, codiceFiscale, CURRICULUM_SCARTATO, problem, reportOperation,
									messageCode, null);
							countNOK++;
							continue;
						}

						try {

							CF_utils.verificaCF(codiceFiscale, nome, cognome, sesso, dataNascita, comuneNascita);
						} catch (CfException e) {
							_logger.error("Eccezione nel formato del codice fiscale");
							int messageCode = MessageCodes.ClicLavoro.CV_NON_INVIABILE_UNVIO_MASSIVO;
							String problem = "Eccezione nel formato del codice fiscale";
							updateError(cdnLavoratore, codiceFiscale, CURRICULUM_SCARTATO, problem, reportOperation,
									messageCode, null);
							countNOK++;
							continue;
						}

						/////////////////////////////////
						// ANALISI RECAPITI LAVORATORE //
						/////////////////////////////////

						Object[] inputParametersRec = new Object[1];
						inputParametersRec[0] = cdnLavoratore;
						SourceBean retEmail = (SourceBean) QueryExecutor.executeQuery("CL_GET_RECAPITI",
								inputParametersRec, "SELECT", Values.DB_SIL_DATI);
						String email = (String) retEmail.getAttribute("ROW.EMAIL");

						if (email == null || ("").equalsIgnoreCase(email)) {

							String problem = "Email lavoratore mancante";
							int messageCode = MessageCodes.ClicLavoro.CV_NON_INVIABILE_UNVIO_MASSIVO;
							updateError(cdnLavoratore, codiceFiscale, CURRICULUM_SCARTATO, problem, reportOperation,
									messageCode, null);
							countNOK++;
							continue;
						}

						///////////////////////////////////////
						// ANALISI MANSIONE LAVORATORE //
						// Si controlla che al lavoratore //
						// sia collegata una mansione valida //
						///////////////////////////////////////
						/*
						 * 
						 * Object[] inputParamProfDes = new Object[2]; inputParamProfDes[0] = dataInvio;
						 * inputParamProfDes[1] = cdnLavoratore; SourceBean retProfDes = (SourceBean)
						 * t.executeQuery("CL_CONTROLLO_PROFESSIONE_DESIDERATA", inputParamProfDes,
						 * TransactionQueryExecutor.SELECT); Vector vecProfDes = retProfDes.getAttributeAsVector("ROW");
						 * if (vecProfDes.size() == 0) { Vector<String> paramsProfDes = new Vector<String>();
						 * reportOperation.reportFailure(MessageCodes.ClicLavoro.NON_ESISTE_LAV_PROFESSIONE_DES, "", "",
						 * paramsProfDes); t.rollBackTransaction(); return; } for (int i = 0; i < vecProfDes.size();
						 * i++) { SourceBean profIesima = (SourceBean)vecProfDes.get(i); boolean esisteProfDes =
						 * profIesima.containsAttribute("IDPROFESSIONE"); if (!esisteProfDes) { Vector<String>
						 * paramsProfDes = new Vector<String>();
						 * reportOperation.reportFailure(MessageCodes.ClicLavoro.NON_ESISTE_LAV_PROFESSIONE_DES, "", "",
						 * paramsProfDes); return; } }
						 */

					}
					///// FINE SECONDO BLOCCO CONTROLLI CHECK //////

					//////////////////////////////////////////
					// GENERAZIONE COD CANDIDATURA SE NUOVA //
					//////////////////////////////////////////

					if (!StringUtils.isFilledNoBlank(prgcandidatura)) {
						CLCandidaturaData clUtil = new CLCandidaturaData();
						codcandidatura = clUtil.getCodComunicazione(CLCandidaturaData.TIPO_CLICLAVORO_CANDIDATURA,
								null);
					}

					////////////////////////////////////
					// GENERAZIONE XML (CHECK FIELDS) //
					////////////////////////////////////

					checkErrorUpdate = false; // ridefinisco a false il check per gli errori di update per partire
												// nuovamente da una situazione pulita
					String xmlGenerato = null;
					try {
						xmlGenerato = buildCandidatura(codiceFiscale, codCpiCapoluogo, dataInvio, codAmbDiff,
								codcandidatura, dataScadenza, titoloFiguraProf, tipoComunicazione);

					} catch (MandatoryFieldException e) {
						_logger.error(e);
						int messageCode = MessageCodes.ClicLavoro.CODE_INPUT_ERRATO;
						String problem = e.getExceptionMessage();
						updateError(cdnLavoratore, codiceFiscale, CURRICULUM_SCARTATO, problem, reportOperation,
								messageCode, null);
						countNOK++;
						continue;
					} catch (FieldFormatException e) {
						_logger.error(e);
						int messageCode = MessageCodes.ClicLavoro.CODE_INPUT_ERRATO;
						String problem = e.getExceptionMessage();
						updateError(cdnLavoratore, codiceFiscale, CURRICULUM_SCARTATO, problem, reportOperation,
								messageCode, null);
						countNOK++;
						continue;
					} catch (EMFUserError e) {
						_logger.error(e);
						int messageCode = MessageCodes.ClicLavoro.CODE_INPUT_ERRATO;
						String problem = e.getSeverity() + " " + e.getMessage();
						updateError(cdnLavoratore, codiceFiscale, CURRICULUM_SCARTATO, problem, reportOperation,
								messageCode, null);
						countNOK++;
						continue;
					}

					/////////////////////////////////////////
					// SALVATAGGIO CANDIDATURA SU DATABASE //
					/////////////////////////////////////////

					Object[] fieldWhere = null;
					BigDecimal prgCand = null;

					// INSERT
					// inserire PRIMA transaction per ogni lavoratore
					TransactionQueryExecutor t = new TransactionQueryExecutor(getPool());
					t.initTransaction();

					try {

						BigDecimal new_prgcandidatura = this.doNextVal(request, response);

						fieldWhere = new Object[12];
						int i = 0;
						fieldWhere[i++] = new_prgcandidatura;
						fieldWhere[i++] = cdnLavoratore;
						fieldWhere[i++] = codtipocomunicazionecl;
						fieldWhere[i++] = codCpiCapoluogo;
						fieldWhere[i++] = dataInvio;
						fieldWhere[i++] = dataScadenza;
						fieldWhere[i++] = codcandidatura;
						fieldWhere[i++] = codstatoinviocl;
						fieldWhere[i++] = objUser.getCodut();
						fieldWhere[i++] = objUser.getCodut();
						fieldWhere[i++] = xmlGenerato;
						fieldWhere[i++] = titoloFiguraProf;

						Object objRes = t.executeQuery("CL_INSERT_CANDIDATURA", fieldWhere,
								TransactionQueryExecutor.INSERT);
						res = Boolean.TRUE.equals(objRes);

						if (res) {
							for (String codambitodiffusione : codAmbDiff) {
								res = insertAmbitoDiffusione(new_prgcandidatura, codambitodiffusione, objUser, t);
								res = res && Boolean.TRUE.equals(objRes);
							}
						}
						prgCand = new_prgcandidatura;
						/// response.setAttribute("new_prgcandidatura",new_prgcandidatura); // DA ELIMINARE ??????

						if (res) { // effettuo commit tabella CL_candidatura e CL_ambito_candidatura
							t.commitTransaction();

							/********************* GESTIONE INVIO CV SIL - PORTALE ********************/
							String rispostaWS = null;
							String msgError = null;
							String statoinviocl = codstatoinviocl;
							TransactionQueryExecutor tTmpInvioMassivo = null;
							checkErrorUpdate = false; // ridefinisco a false il check per gli errori di update per
														// partire nuovamente da una situazione pulita
							try {
								rispostaWS = sendToClicLavoroInvioMassivo(username, pwd, codstatoinviocl, xmlGenerato,
										prgCand);

								tTmpInvioMassivo = new TransactionQueryExecutor(getPool());
								tTmpInvioMassivo.initTransaction();

								if ("0".equals(rispostaWS)) {

									checkErrorUpdate = aggiornaTmpInvioMassivo(cdnLavoratore, codiceFiscale,
											CURRICULUM_INVIATO, null, tTmpInvioMassivo);
									if (checkErrorUpdate) {

										if ("PA".equalsIgnoreCase(codstatoinviocl)) {
											statoinviocl = "PI";
										} else {
											statoinviocl = "VI";
										}
										updateEsitoCandidaturaMsg(statoinviocl, msgError,
												(prgCand != null ? prgCand.toString() : null), tTmpInvioMassivo);

										tTmpInvioMassivo.commitTransaction();
										countOk++;
									} else {
										throw new Exception(
												"Errore aggior. TMP_INVIO_MASSIVO/CL_CANDIDATURA con esito 0");
										/*
										 * tTmpInvioMassivo.rollBackTransaction();
										 * reportOperation.reportFailure(MessageCodes.ClicLavoro.CODE_ERR_INTERNO);
										 * continue;
										 */
									}

								}
								// imposto il messaggio errore
								if (!"0".equals(rispostaWS)) { // caso degli errori
									if ("1".equals(rispostaWS)) {
										msgError = "Non è stato possibile importare il curriculum perché esistono piu utenze con lo stesso indirizzo email";
									} else if ("999".equals(rispostaWS)) {
										msgError = "Errore Generico";
									} else {
										msgError = "GRAVE cod errore non previsto: " + rispostaWS;
									}
									checkErrorUpdate = aggiornaTmpInvioMassivo(cdnLavoratore, codiceFiscale,
											CURRICULUM_SCARTATO, msgError, tTmpInvioMassivo);
									if (checkErrorUpdate) {
										if (rispostaWS != null && (rispostaWS.equals("1"))) { // solo se la risposta è
																								// una risposta
																								// "parlante" cioè di
																								// ritorno
																								// dalla corretta
																								// esecuzione del WS
																								// allora imposto lo
																								// stato come PRIMO
																								// INVIO
																								// COMPLETATO/VARIAZIONE
																								// DATI INVIATA
																								// altrimenti non ha
																								// senso cambiare lo
																								// stato in quanto
																								// l'invio è dovuto ad
																								// una mancata
																								// comunicazione con il
																								// WS e comunque in ogni
																								// caso il
																								// il CV non è stato
																								// acquisito del Portale
											if ("PA".equalsIgnoreCase(codstatoinviocl)) {
												statoinviocl = "PI";
											} else {
												statoinviocl = "VI";
											}
										}

										updateEsitoCandidaturaMsg(statoinviocl, msgError,
												(prgCand != null ? prgCand.toString() : null), tTmpInvioMassivo);

										/** se il ws mi ritorna un errore logghiamo l'errore in Reportoperation **/
										if (msgError != null && !msgError.isEmpty()) {
											int messageCode = MessageCodes.ClicLavoro.CODE_RESPONSE_WS_ERRATO;
											String problem = msgError;
											Vector<String> params = new Vector<String>();
											params.add(codiceFiscale);
											params.add(problem);
											reportOperation.reportFailure(messageCode, "", "", params);
											countNOK++;
										}
										/************************************************************************/

										tTmpInvioMassivo.commitTransaction();
									} else {
										throw new Exception(
												"Errore aggior. TMP_INVIO_MASSIVO/CL_CANDIDATURA con esito <> 0");
										/**
										 * tTmpInvioMassivo.rollBackTransaction();
										 * reportOperation.reportFailure(MessageCodes.ClicLavoro.CODE_ERR_INTERNO);
										 * continue;
										 */
									}
								}

							} catch (Exception e) {
								if (tTmpInvioMassivo != null) {
									tTmpInvioMassivo.rollBackTransaction();
								}
								int messageCode = MessageCodes.ClicLavoro.CODE_INPUT_ERRATO;
								String problem = e.getMessage();
								Vector<String> params = new Vector<String>();
								params.add(codiceFiscale);
								params.add(problem);
								reportOperation.reportFailure(messageCode, "", "", params);
								countNOK++;
								continue; // passo al lavoratore successivo
							}
							/********************************************************************/

						} else { // caso errore aggiornamento CL_CANDIDATURA / CL_AMBITO_CANDIDATURA -- inerente PRIMA
									// TRANSAZIONE PRE CHIAMATA WS
							if (t != null) {
								t.rollBackTransaction();
							}
							checkErrorUpdate = false;
							int messageCode = MessageCodes.ClicLavoro.CODE_ERR_INTERNO;
							String problem = "Errore interno";
							updateError(cdnLavoratore, codiceFiscale, CURRICULUM_SCARTATO, problem, reportOperation,
									messageCode, null);
							countNOK++;
							continue; // passo al lavoratore successivo
						}
					} catch (Exception e) {
						if (t != null) {
							t.rollBackTransaction();
						}
						checkErrorUpdate = false;
						int messageCode = MessageCodes.ClicLavoro.CODE_ERR_INTERNO;
						String problem = "Errore interno";
						updateError(cdnLavoratore, codiceFiscale, CURRICULUM_SCARTATO, problem, reportOperation,
								messageCode, null);
						countNOK++;
						continue; // passo al lavoratore successivo
					}
				} // CHIUDE IL CICLO PRINCIPALE
			} // CHIUDE IF SE DATI NON PRESENTI
			else {
				reportOperation.reportSuccess(idSuccess);
			}

			response.setAttribute("OK", countOk);
			response.setAttribute("NOK", countNOK);

			/** AGGIORNAMENTO TABELLA TMP_OPERAZIONI_MASSIVE **/
			boolean checkUpdate = false;
			checkUpdate = aggiornaTmpOperazioniMassive(strdatinizioelaborazione);
			if (!checkUpdate) {
				reportOperation.reportFailure(MessageCodes.ClicLavoro.CODE_ERR_INTERNO);
				return;
			}
			reportOperation.reportSuccess(idSuccess);
		} // chiudo if se esecuzione invio CV al portale già in corso
	}

	/**
	 * 
	 * @param cdnlavoratore
	 * @param strcodicefiscale
	 * @param codesito
	 * @param stresito
	 * @param reportOperation
	 * @param messageCode
	 * @param paramFacolt
	 *            parametro facoltativo
	 * @throws EMFInternalError
	 */

	public void updateError(String cdnlavoratore, String strcodicefiscale, int codesito, String stresito,
			ReportOperationResult reportOperation, int messageCode, String paramFacolt) throws EMFInternalError {
		int messageCodeInterno = MessageCodes.ClicLavoro.CODE_ERR_INTERNO;
		boolean checkErrorUpdate = false;
		String problem = stresito;
		Vector<String> params = new Vector<String>();
		params.add(strcodicefiscale);
		params.add(problem);
		if (paramFacolt != null && !paramFacolt.isEmpty()) {
			params.add(paramFacolt);
		}
		checkErrorUpdate = aggiornaTmpInvioMassivoCheck(cdnlavoratore, codesito, stresito);
		if (checkErrorUpdate) {
			reportOperation.reportFailure(messageCode, "", "", params);
		} else {
			reportOperation.reportFailure(messageCodeInterno);
		}
	}

	/** AGIORNAMENTO UTILIZZATO IN TRANSAZIONE (DOPO CHIAMATA WS) **/
	public boolean aggiornaTmpInvioMassivo(String cdnlavoratore, String strcodicefiscale, int codesito, String stresito,
			TransactionQueryExecutor t) throws EMFInternalError {
		boolean res;
		int i = 0;
		Object[] fieldWhere = null;
		fieldWhere = new Object[3];
		fieldWhere[i++] = codesito;
		fieldWhere[i++] = stresito;
		fieldWhere[i++] = cdnlavoratore;
		Object objRes = t.executeQuery("UPDATE_TMP_INVIO_MASSIVO_CV", fieldWhere, TransactionQueryExecutor.UPDATE);
		res = Boolean.TRUE.equals(objRes);
		return res;
	}

	/** AGIORNAMENTO UTILIZZATO NON IN TRANSAZIONE (DOPO OGNI CHECK PRIMA DI CHIAMARE WS) **/
	public boolean aggiornaTmpInvioMassivoCheck(String cdnlavoratore, int codesito, String stresito)
			throws EMFInternalError {
		boolean res;
		int i = 0;
		Object[] fieldWhere = null;
		fieldWhere = new Object[3];
		fieldWhere[i++] = codesito;
		fieldWhere[i++] = stresito;
		fieldWhere[i++] = cdnlavoratore;
		Object objRes = QueryExecutor.executeQuery("UPDATE_TMP_INVIO_MASSIVO_CV", fieldWhere,
				TransactionQueryExecutor.UPDATE, Values.DB_SIL_DATI);
		res = Boolean.TRUE.equals(objRes);
		return res;
	}

	public boolean inserisciTmpOperazioniMassive(String numelaborazioni, String datinizioelaborazione)
			throws EMFInternalError {
		boolean res;
		int i = 0;
		Object[] fieldWhere = null;
		fieldWhere = new Object[2];
		fieldWhere[i++] = numelaborazioni;
		fieldWhere[i++] = datinizioelaborazione;
		Object objRes = QueryExecutor.executeQuery("INSERT_TMP_OPERAZIONI_MASSIVE", fieldWhere,
				TransactionQueryExecutor.INSERT, Values.DB_SIL_DATI);
		res = Boolean.TRUE.equals(objRes);
		return res;
	}

	public boolean aggiornaTmpOperazioniMassive(String datinizioelaborazione) throws EMFInternalError {
		boolean res;
		int i = 0;
		Object[] fieldWhere = null;
		fieldWhere = new Object[1];
		fieldWhere[i++] = datinizioelaborazione;
		Object objRes = QueryExecutor.executeQuery("UPDATE_TMP_OPERAZIONI_MASSIVE", fieldWhere,
				TransactionQueryExecutor.UPDATE, Values.DB_SIL_DATI);
		res = Boolean.TRUE.equals(objRes);
		return res;
	}

	public boolean updateEsitoCandidaturaMsg(String stato, String msgError, String prgCandidatura,
			TransactionQueryExecutor t) throws EMFInternalError {
		/*
		 * String prg = null; if (prgCandidatura != null) { prg = prgCandidatura.toString(); }
		 */
		boolean res;
		int i = 0;
		Object[] fieldWhere = null;
		fieldWhere = new Object[3];
		fieldWhere[i++] = stato;
		fieldWhere[i++] = msgError;
		fieldWhere[i++] = prgCandidatura;
		Object objRes = t.executeQuery("UPDATE_CL_CANDIDATURA_MASSIVO", fieldWhere, TransactionQueryExecutor.UPDATE);
		res = Boolean.TRUE.equals(objRes);
		return res;
	}

	private String sendToClicLavoroInvioMassivo(String username, String pwd, String codstatoinviocl, String xmlGenerato,
			BigDecimal prgCand) throws EMFInternalError, Exception {
		_logger.info("sendToClicLavoroInvioMassivo");

		DataSourceJNDI dataSourceJndi = new DataSourceJNDI();
		String dataSourceJndiName = dataSourceJndi.getJndi();
		OutQ outQ = new OutQ();
		TestataMessageTO testataMessaggio = new TestataMessageTO();
		testataMessaggio.setServizio(SERVIZIO_CLIC_LAVORO);

		InvioCandidaturaMyPortalMessage sendCandidaturaMessage = new InvioCandidaturaMyPortalMessage(username, pwd,
				codstatoinviocl, prgCand.toString());

		// mando il messaggio
		sendCandidaturaMessage.setTestata(testataMessaggio);
		sendCandidaturaMessage.setDataSourceJndi(dataSourceJndiName);
		sendCandidaturaMessage.setDatiRichiestaXml(xmlGenerato);

		return sendCandidaturaMessage.callWebserviceMassivo(); // sendCandidaturaMessage.callWebserviceInvioMassivo();
	}

	private boolean insertAmbitoDiffusione(Object prgcandidatura, String codambitodiffusione, User objUser,
			TransactionQueryExecutor t) throws EMFInternalError {
		int cdnUt = objUser.getCodut();
		Object[] fieldWhere;
		int i;
		Object objRes;
		BigDecimal prgambitocandidatura = DBKeyGenerator.getNextSequence(getPool(), "s_cl_ambito_candidatura");
		i = 0;
		fieldWhere = new Object[5];
		fieldWhere[i++] = prgambitocandidatura;
		fieldWhere[i++] = prgcandidatura;
		fieldWhere[i++] = codambitodiffusione;
		fieldWhere[i++] = cdnUt;
		fieldWhere[i++] = cdnUt;
		objRes = t.executeQuery("CL_INSERT_AMBITO_CANDIDATURA", fieldWhere, TransactionQueryExecutor.INSERT);
		return Boolean.TRUE.equals(objRes);
	}

	private boolean checkCvValidoPeriodo(String cdnLavoratore, String dataInvio, String dataScadenza)
			throws EMFInternalError {
		Object[] inputParameters = new Object[3];
		inputParameters[0] = cdnLavoratore;
		inputParameters[1] = dataInvio;
		inputParameters[2] = dataScadenza;
		SourceBean periodoValido = (SourceBean) QueryExecutor.executeQuery(
				"CL_ESISTE_CV_VALIDO_PERIODO_CANDIDATURA_MASSIVO", inputParameters, TransactionQueryExecutor.SELECT,
				getPool());
		boolean esiste = periodoValido.containsAttribute("ROW.esiste");
		if (!esiste) {
			return true;
		}
		return false;
	}

	/** Check controllo se candidatura già presente **/
	private boolean checkCandidatura(String cdnLavoratore) throws EMFInternalError {
		boolean esiste = false;
		Object[] inputParameters = new Object[1];
		inputParameters[0] = cdnLavoratore;
		SourceBean verifCandidatura = (SourceBean) QueryExecutor.executeQuery("SELECT_COUNT_CL_CANDIDATURA",
				inputParameters, TransactionQueryExecutor.SELECT, getPool());
		String numcandidatura = StringUtils.getAttributeStrNotNull(verifCandidatura, "ROW.numcandidatura");
		if (numcandidatura != null && !numcandidatura.equals("0")) {
			esiste = true;
		}
		return esiste;
	}

	private boolean checkTitoloCV(String nome, String cognome) {
		boolean esiste = true;
		if (cognome != null && !cognome.isEmpty() && nome != null && !nome.isEmpty()) {
			esiste = false;
		}
		return esiste;
	}

	private boolean checkCittadinanzaValida(String cdnLavoratore) throws EMFInternalError {

		Object[] inputParameters = new Object[1];
		inputParameters[0] = cdnLavoratore;
		SourceBean cittadinanzaValidaSB = (SourceBean) QueryExecutor.executeQuery("CL_CHECK_CITTADINANZA_VALIDA",
				inputParameters, TransactionQueryExecutor.SELECT, getPool());
		boolean esiste = cittadinanzaValidaSB.containsAttribute("ROW.esiste");
		if (!esiste) {
			return true;
		}
		return false;
	}

	/**
	 * Costruisce l'xml da inviare a clic lavoro relativo alla candidatura di una persona fisica
	 * 
	 * @param codiceFiscale
	 *            codice fiscale della persona della quale si vogliono inviare le informazioni
	 * @return xml costruito e validato, pronto per essere inviato al webService di clicLavoro
	 * @throws CLCandidaturaException
	 *             in caso di errori durante la costruzione dell'xml
	 * @throws FieldFormatException
	 * @throws MandatoryFieldException
	 */
	public String buildCandidatura(String codiceFiscale, String codCPI, String dataInvio, List<String> codAmbDiff,
			String tipoComunicazione) throws EMFUserError, MandatoryFieldException, FieldFormatException {
		CLCandidaturaData cLCandidaturaData = new CLCandidaturaData(codiceFiscale, codCPI, dataInvio, codAmbDiff,
				tipoComunicazione);
		cLCandidaturaData.costruisci();

		return cLCandidaturaData.generaXML();
	}

	public String buildCandidatura(String codiceFiscale, String codCPI, String dataInvio, List<String> codAmbDiff,
			String codcandidatura, String dataScad, String titolo, String tipoComunicazione)
			throws EMFUserError, MandatoryFieldException, FieldFormatException {
		CLCandidaturaDataInvioMassivo cLCandidaturaDatainvioMassivo = new CLCandidaturaDataInvioMassivo(codiceFiscale,
				codCPI, dataInvio, codAmbDiff, codcandidatura, dataScad, titolo, tipoComunicazione);
		cLCandidaturaDatainvioMassivo.costruisci();

		return cLCandidaturaDatainvioMassivo.generaXML();
	}
}
