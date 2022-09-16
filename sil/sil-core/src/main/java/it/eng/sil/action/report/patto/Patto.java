package it.eng.sil.action.report.patto;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;
import com.inet.report.Engine;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.bean.Documento;
import it.eng.sil.bean.ProtocollaException;
import it.eng.sil.module.AccessoSemplificato;
import it.eng.sil.module.firma.grafometrica.FirmaDocumenti;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.module.pi3.Pi3Constants;
import it.eng.sil.module.pi3.ProtocolloPi3DBManager;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;
import it.eng.sil.util.amministrazione.impatti.PattoBean;

public class Patto extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(Patto.class.getName());

	private SourceBean infoGenerali;
	
	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);

		String apriFile = (String) request.getAttribute("apriFileBlob");
		String salva = (String) request.getAttribute("salvaDB");
		
		
		if ((apriFile != null) && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			try {
				Object cdnLavoratore = request.getAttribute("cdnLavoratore");
				Object prgpattolavoratore = request.getAttribute("strChiaveTabella");
				SourceBean patto = PattoBean.caricaPatto(prgpattolavoratore);
				String entePatto = "";
				String datInizioNaspi = "";
				String datRiferimento150 = "";
				BigDecimal numIndiceSvant150 = null;
				BigDecimal importoADR = null;
				if (patto != null) {
					patto = patto.containsAttribute("ROW")?(SourceBean)patto.getAttribute("ROW"):patto;
					entePatto = patto.containsAttribute("STRENTECODICEFISCALE")?patto.getAttribute("STRENTECODICEFISCALE").toString():"";
					datInizioNaspi = patto.containsAttribute("DATANASPI")?patto.getAttribute("DATANASPI").toString():"";
					datRiferimento150 = patto.containsAttribute("DATRIFERIMENTO150")?patto.getAttribute("DATRIFERIMENTO150").toString():"";
					numIndiceSvant150 = (BigDecimal)patto.getAttribute("NUMINDICESVANTAGGIO150");
					importoADR = (BigDecimal)patto.getAttribute("IMPORTOAR");
					
				}
				int codiceErrore = -1;
				
				//BigDecimal numProgrammiKO = PattoBean.checkProgrammiApertiSenzaAzioni(new BigDecimal(cdnLavoratore.toString()), null);
				//if (numProgrammiKO != null && numProgrammiKO.intValue()>0) {
				//	throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.Patto.ERR_PROGRAMMA_APERTO_NO_ATTIVITA);
				//}
				
				Vector<String>programmi = PattoBean.checkProgrammi(new BigDecimal(prgpattolavoratore.toString()), null);
				if (programmi==null) {
					Vector v = new Vector(1);
					v.add("Controllo Recupero Programmi");
					throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.Patto.ERR_CHECKCONTROLLIPROTOCOLLAZIONE, v);
				}
				else {
					if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_ASSEGNO_RICOLLOCAZIONE) || 
						PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_INTERVENTO_OCCUPAZIONE)) {
						if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_ASSEGNO_RICOLLOCAZIONE)) {
							if (datInizioNaspi.equals("") || importoADR == null) {
								throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.Patto.ERR_PATTO_ADR);
							}
						}
						if (datRiferimento150.equals("") || numIndiceSvant150 == null) {
							throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.Patto.ERR_PATTO_ADR_POC);
						}
					}
					
					if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA)) {
						codiceErrore = PattoBean.checkProtocollazioneMGGU(cdnLavoratore);
						if (codiceErrore > 0) {
							throw new EMFUserError(EMFErrorSeverity.ERROR, codiceErrore);
						}
					} else if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_INTERVENTO_OCCUPAZIONE)) {
						Vector<String>serviziProgrammi = PattoBean.checkServiziProgrammi(new BigDecimal(prgpattolavoratore.toString()), null,
																						 PattoBean.DB_MISURE_INTERVENTO_OCCUPAZIONE);
						codiceErrore = PattoBean.checkProtocollazionePOC(prgpattolavoratore, serviziProgrammi);
						if (codiceErrore > 0) {
							throw new EMFUserError(EMFErrorSeverity.ERROR, codiceErrore);
						}					
					}
				}
				//controllo soggetto accreditato obbligatorio
				Vector programmiObbligoEnte = PattoBean.checkObbligoEnte(new BigDecimal(prgpattolavoratore.toString()), null);
				
				if (!programmiObbligoEnte.isEmpty()) {
					String strdescrizione = "";
					for (int i = 0; i < programmiObbligoEnte.size(); i++) {
						SourceBean info = (SourceBean) programmiObbligoEnte.get(i);
						String stringa = (String) info.getAttribute("STRDESCRIZIONE");	
						strdescrizione = strdescrizione + stringa + ", ";
					}						
					strdescrizione = strdescrizione.substring(0, strdescrizione.length()-2);
					Vector v = new Vector(1);
					v.add(strdescrizione);
					throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.Patto.ERR_OBBLIGOENTE, v);
				}
				
				// Controllo ente e azioni in base alla configurazione
				String config_ente = Properties.DEFAULT_CONFIG;
				Object[] paramsEnte = new Object[1];
				paramsEnte[0] = PattoBean.CONF_COLLEGA_AZ_ENTE;
				SourceBean rowConfigEnte = doSelect("ST_GETCONFIGURAZIONE_PROVINCIA", paramsEnte);
				if (rowConfigEnte != null) {
					config_ente = rowConfigEnte.containsAttribute("ROW.NUM")?rowConfigEnte.getAttribute("ROW.NUM").toString():Properties.DEFAULT_CONFIG;
				}
			
				if(config_ente.equals(Properties.CUSTOM_CONFIG)) {
					String[] descrizioniAzioni = {""};
					codiceErrore = PattoBean.checkMappaturaAzioneEnte(prgpattolavoratore, descrizioniAzioni);
					if (codiceErrore > 0) {
						throw new EMFUserError(EMFErrorSeverity.ERROR, codiceErrore, descrizioniAzioni[0]);
					}
					else if (codiceErrore < 0) {
						descrizioniAzioni[0] = "Controllo Mappatura Azione Ente";
						throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.Patto.ERR_CHECKCONTROLLIPROTOCOLLAZIONE, descrizioniAzioni[0]);
					}
				}
				
				setStrDescrizione("Patto lavoratore");
	
				String tipoFile = (String) request.getAttribute("tipoFile");
	
				if (tipoFile != null) {
					setStrNomeDoc("patto." + tipoFile);
				} else {
					setStrNomeDoc("patto.pdf");
				}
				// ATTENZIONE: necessario comunque anche se il report viene generato
				// con le api
				setReportPath("patto/patto_CC.rpt");
				
				String config_Stampa = Properties.DEFAULT_CONFIG;
				SourceBean rowConfig;
				try {
					rowConfig = doSelect("ST_CONF_STAMPA_PATTO", null);
					if (rowConfig != null) {
						config_Stampa = rowConfig.containsAttribute("ROW.NUM")?rowConfig.getAttribute("ROW.NUM").toString():Properties.DEFAULT_CONFIG;
						
					}		
					//Prende la conf
					
					if(config_Stampa.equals("1")) { //Trento 
						
						eseguiStampaPattoTrento(request, response);
						
						if ((salva != null) && salva.equalsIgnoreCase("true")) {
						
							//GESTIONE FIRMA GRAFOMETRICA
							if( request.getAttribute("firmaGrafometrica") != null &&  request.getAttribute("firmaGrafometrica").toString().equals("OK")){
								
								Documento actualDocument = getDocumento();
								Documento prgDocumento = new Documento(actualDocument.getPrgDocumento());
								actualDocument.setNumKloDocumento(prgDocumento.getNumKloDocumento());
								_logger.debug("[PATTO] --> firmaGrafometrica --> NumKlo: " + actualDocument.getNumKloDocumento());
							
								_logger.debug("[PATTO] --> Richiesta firma grafometrica");
								try{
									
									// VERIFICA SE IL TIPO DI DOCUMENTO E' PREDISPOSTO PER ESSERE FIRMATO GRAFOMETRICAMENTE
									ProtocolloPi3DBManager dbManager = new ProtocolloPi3DBManager();
									//boolean isDocumentFirmabile = dbManager.isAllegatoDocumentoFirmato(actualDocument.getPrgDocumento().toString());
									BigDecimal prgTemplateStampa = dbManager.getPrgTemplateStampa(Pi3Constants.PI3_DOCUMENT_TYPE_PATTO);
									boolean isDocumentTypeFirmabile = dbManager.isDocumentTypeGraphSignature(prgTemplateStampa.toString());
									_logger.debug("[PATTO] --> Verifica se documento predisposto per la firma: isDocumentTypeFirmabile = [" + isDocumentTypeFirmabile + "]");
									if (isDocumentTypeFirmabile){
										
										TransactionQueryExecutor txExecutorFirma = null;
										ArrayList messagesWarning = new ArrayList();
										try{
											FirmaDocumenti firma = new FirmaDocumenti();
											AccessoSemplificato _db = new AccessoSemplificato(this);
											txExecutorFirma = new TransactionQueryExecutor(_db.getPool());
											User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);
											String ipOperatore = request.getAttribute("ipOperatore").toString();
											boolean esitoFirma = firma.firmaDocumento(request, response, user, txExecutorFirma, actualDocument, ipOperatore);
											_logger.debug("[PATTO] --> Verifica esito firma: esitoFirma = [" + esitoFirma + "]");
											if(!esitoFirma){
												messagesWarning.add(MessageCodes.FirmaGrafometrica.WS_OPERATION_FAIL);
												
												throw new Exception();
	//											response.updAttribute("messageResult", "Errore durante la firma grafometrica del documento.");
	//			 								throw new Exception("Errore durante la firma grafometrica del documento.");
											}else{
												setOperationSuccess(request, response);
											}
											
											
										}catch(Exception firmaEx){
											if(!messagesWarning.contains(MessageCodes.FirmaGrafometrica.WS_OPERATION_FAIL)){
												//response.updAttribute("messageWarning", "Il Servizio di Firma Grafometrica non è al momento disponibile. Procedere con la firma autografa del documento");
													messagesWarning.add(MessageCodes.FirmaGrafometrica.WS_NON_RAGGIUNGIBILE);
												}
												_logger.debug("[PATTO] --> La Firma non andata a buon fine ma il processo di rendering va avanti");
												setOperationSuccess(request, response);
												//setOperationSuccessWithWarning(request, response,messagesWarning);
												return;	
										} finally {
											if (txExecutorFirma != null){
												txExecutorFirma.closeConnTransaction();
												_logger.debug("PATTO - Gestione Firma Grafometrica - TransactionQueryExecutor close...");
											}
										}
								
									} else{
										_logger.debug("[PATTO] --> Tipo del Documento non firmabile grafometricamente, si procede alla trasformazione del documento normalmente senza applicazione dei campi firma");
										it.eng.sil.util.TraceWrapper.debug(_logger, "Patto:service(): Tipo del Documento non firmabile grafometricamente, si procede alla trasformazione del documento normalmente senza applicazione dei campi firma", null);
									}
									
								}catch(Exception firmaEx){
									_logger.debug("[PATTO] --> errore recupero firmabilita' grafometrica del tipo di documento");
									it.eng.sil.util.TraceWrapper.error(_logger, "Patto:service():errore recupero firmabilita' grafometrica del tipo di documento", firmaEx);
									setOperationFail(request, response, firmaEx);
								}
							
							}else{
								setOperationSuccess(request, response);
							}
						
						}else{
							setOperationSuccess(request, response);
						}
					
					}  else {
						eseguiStampaPattoConApi(request, response);
					}
					
				}
				
				catch (EMFInternalError emf) {
					_logger.error(emf.getMessage());
					
					if (emf.getNativeException() instanceof SQLException) {
						if( ((SQLException)emf.getNativeException()).getErrorCode() == MessageCodes.General.CONCORRENZA ){
							setOperationFail(request, response, MessageCodes.General.CONCORRENZA);
							try {
								response.setAttribute("ECCEZIONEPROTOCOLLA", MessageCodes.General.CONCORRENZA);
							} catch (SourceBeanException e) {
								_logger.error(e.getMessage());
							}
						} else{
							setOperationFail(request, response, MessageCodes.General.OPERATION_FAIL);
							try {
								response.setAttribute("ECCEZIONEPROTOCOLLA", MessageCodes.General.OPERATION_FAIL);
							} catch (SourceBeanException e) {
								_logger.error(e.getMessage());
							}
						}
					} else{
						setOperationFail(request, response, MessageCodes.General.OPERATION_FAIL);
						try {
							response.setAttribute("ECCEZIONEPROTOCOLLA", MessageCodes.General.OPERATION_FAIL);
						} catch (SourceBeanException e) {
							_logger.error(e.getMessage());
						}
					}
				}
				
				catch (Exception e) {
					it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nella stampa del patto", e);
					setOperationFail(request, response, e);
				}
			}
			catch (SQLException sEx) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nella stampa del patto", sEx);
				setOperationFail(request, response, sEx);
			}
			catch (EMFUserError tEx) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nella stampa del patto", tEx);
				setOperationFail(request, response, tEx);
			}
			catch (Exception e) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nella stampa del patto", e);
				setOperationFail(request, response, e);
			}
		}
	}

	private void eseguiStampaPattoTrento(SourceBean request, SourceBean response)
			throws EMFUserError, SourceBeanException, EMFInternalError {
		setReportPath("patto/Patto_di_Servizio_TN_CC.rpt");

		// impostazione parametri del report
		Map prompts = new HashMap();
		
		String nomeUt = "";
		String cognomeUt = "";

		Object cdnLavoratore = request.getAttribute("cdnLavoratore");
		Object prgpattolavoratore = request.getAttribute("strChiaveTabella");
		String ristampaPT = (String) request.getAttribute("ristampaPT");
		AccessoSemplificato _db = new AccessoSemplificato(this);
		// per recuperare le informazioni utilizzate per la generazione
		// del report non e' necessario
		// l'utilizzo della transazione. Se necessaria nei passi
		// successivi verra' abilitata.
		_db.enableSimpleQuery();
		_db.disableMessageIdFail();
		_db.disableMessageIdSuccess();
		_db.setSectionQuerySelect("QUERY_GET_DATA_ISCRIZIONE_LISTA_PATTO");
		SourceBean dataIscrListaSb = _db.doSelect(request, response);
		
		String dataIscrLista = SourceBeanUtils.getAttrStr(dataIscrListaSb,
				"ROW.DATA_ISCRIZIONE_LISTA");
		
		_db.setSectionQuerySelect("QUERY_GET_OPERATORE");
		SourceBean operatore = _db.doSelect(request, response);
		nomeUt = SourceBeanUtils.getAttrStr(operatore, "ROW.STRNOMEUTENTE");
		cognomeUt = SourceBeanUtils.getAttrStr(operatore, "ROW.STRCOGNOMEUTENTE");
		String operatoreFirma = "da " + nomeUt + " " + cognomeUt;
		
		prompts.put("cdnLavoratore", cdnLavoratore);
		prompts.put("prgpattolavoratore", prgpattolavoratore);
		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		prompts.put("codCpi", user.getCodRif());
		Object dataProtObj = request.getAttribute("dataProt");
		String dataProt = "";
		if (dataProtObj != null) {
			dataProt = dataProtObj.toString();
		}
		prompts.put("dataProt", dataProt);

		Object annoProt = request.getAttribute("annoProt");
		prompts.put("numAnnoProt", annoProt);
		Object numProt = request.getAttribute("numProt");
		prompts.put("numProt", numProt);
		prompts.put("dataIscrLista", dataIscrLista);
		
		/***
		 * 
		 * Aggiunta SubReport Conferimento DID (solo se PRGCONFERIMENTODID e' valorizzato)
		 * 
		 */
		boolean noDataDichAvailable = true;
		_db.setSectionQuerySelect("QUERY_INFO_GENERALI");
		infoGenerali = _db.doSelect(request, response);
		Vector infoGeneraliV = infoGenerali.getAttributeAsVector("ROW");
		if (infoGeneraliV.size() > 0) {
			SourceBean infoGen = (SourceBean) infoGeneraliV.get(0);
			String datdichiarazione = (String) infoGen.getAttribute("datdichiarazione");
			if(!StringUtils.isEmpty(datdichiarazione)){
				request.setAttribute("datDidConf", datdichiarazione);
				noDataDichAvailable = false;
			}
		}
		
		if (noDataDichAvailable == false){
			_db.setSectionQuerySelect("QUERY_CONFERIMENTO_DID");
			SourceBean conferimentoDid = _db.doSelect(request, response);
			if (conferimentoDid != null){
				if (conferimentoDid.containsAttribute("ROW")){
					prompts.put("prgConferimentoDid", conferimentoDid.getAttribute("ROW.PRGCONFERIMENTODID").toString());
					
					// Dati profiling D.Lgs. 150/2015
					prompts.put("decProfiling", Utils.notNull(conferimentoDid.getAttribute("ROW.DECPROFILING")));					
					prompts.put("dataProfiling", Utils.notNull(conferimentoDid.getAttribute("ROW.DATAPROFILING")));
					prompts.put("numEta", Utils.notNull(conferimentoDid.getAttribute("ROW.NUMETA")));
					prompts.put("strSesso", Utils.notNull(conferimentoDid.getAttribute("ROW.STRSESSO")));
					prompts.put("provRes", Utils.notNull(conferimentoDid.getAttribute("ROW.nomeprovres")));
					
					// Dati sul nucleo familiare
					prompts.put("numNucleoFam", Utils.notNull(conferimentoDid.getAttribute("ROW.NUMNUCLEOFAM")));
					prompts.put("flgFigliaCarico", Utils.notNull(conferimentoDid.getAttribute("ROW.FLGFIGLIACARICO")));
					prompts.put("flgFigliMinorenni", Utils.notNull(conferimentoDid.getAttribute("ROW.FLGFIGLIMINORENNI")));
					
					// Esperienze precedenti
					prompts.put("FLGESPLAVORO", Utils.notNull(conferimentoDid.getAttribute("ROW.FLGESPLAVORO")));
					prompts.put("NUMMESIDISOCC", Utils.notNull(conferimentoDid.getAttribute("ROW.NUMMESIDISOCC")));
					prompts.put("cupDescrizione", Utils.notNull(conferimentoDid.getAttribute("ROW.cupDescrizione")));
					prompts.put("profDescrizione", Utils.notNull(conferimentoDid.getAttribute("ROW.profDescrizione")));
					
					// Altre informazioni
					prompts.put("pritDescrizione", Utils.notNull(conferimentoDid.getAttribute("ROW.pritDescrizione")));
					prompts.put("corsoDescrizione", Utils.notNull(conferimentoDid.getAttribute("ROW.corsoDescrizione")));
					prompts.put("NUMMESIRICERCALAV", Utils.notNull(conferimentoDid.getAttribute("ROW.NUMMESIRICERCALAV")));
					prompts.put("titoloDescrizione", Utils.notNull(conferimentoDid.getAttribute("ROW.titoloDescrizione")));
					
					// Dati ANPAL
					prompts.put("cupDescrizioneCal", Utils.notNull(conferimentoDid.getAttribute("ROW.cupDescrizioneCal")));
					prompts.put("NUMMESIDISOCC_CALC", Utils.notNull(conferimentoDid.getAttribute("ROW.NUMMESIDISOCC_CALC")));
				}
			}
		}

		addPromptFieldsProtocollazione(prompts, request);
		// firma operatore
		prompts.put("operatoreFirma", operatoreFirma);
		// ora si chiede di usare il passaggio dei parametri per nome e
		// non per posizione (col vettore, passaggio di default)
		setPromptFields(prompts);
		
		// Settaggio strchiavetabella
		String strChiaveTabella = (String) prgpattolavoratore;
		if ((strChiaveTabella != null) && !strChiaveTabella.equals("")) {
			setStrChiavetabella(strChiaveTabella);
		}

		String salva = (String) request.getAttribute("salvaDB");
		String apri = (String) request.getAttribute("apri");

		if ("true".equalsIgnoreCase(salva)) {
			if (request.containsAttribute("dataOraProt")) {
				request.updAttribute("codStatoAtto", "PR");
				getDocumento().setCodStatoAtto("PR");
			}
//			AccessoSemplificato _db = new AccessoSemplificato(this);
			TransactionQueryExecutor txExec = null;
			try {
				txExec = new TransactionQueryExecutor(_db.getPool());
				txExec.initTransaction();
				if (ristampaPT == null || !ristampaPT.equalsIgnoreCase("S")) {
					PattoBean.gestisciConcorrenza(request, txExec);
				}
				
				if (!StringUtils.isEmptyNoBlank(strChiaveTabella)) {
					// CONTROLLO DOCUMENTI DOPPI PER IL PATTO
					String codTipoDoc = request.getAttribute("tipoDoc") !=null?request.getAttribute("tipoDoc").toString():"";
					if (codTipoDoc.equals("")) {
						codTipoDoc = Properties.DEFAULT_DOCUMENTO_PATTO;
					}
					// SE STO FACENDO UNA RISTAMPA DEVO ANNULLARE PRIMA IL DOCUMENTO PROTOCOLLATO --> gennaio 2019 non si annulla piu
					if (ristampaPT != null && ristampaPT.equalsIgnoreCase("S")) {
						Object[] objDoc = new Object[3];
						objDoc[0] = codTipoDoc;
						objDoc[1] = new BigDecimal("14");
						objDoc[2] = strChiaveTabella;
						
						SourceBean sbDoc = (SourceBean) txExec.executeQuery("GET_DOCUMENTO_PATTO_X_RISTAMPA", objDoc, "SELECT");
						if (sbDoc != null && sbDoc.getAttributeAsVector("ROW") != null && sbDoc.getAttributeAsVector("ROW").size() == 1) {
							SourceBean documento = (SourceBean) sbDoc.getAttribute("ROW");
							BigDecimal prgDoc = (BigDecimal)documento.getAttribute("prgdocumento");
							BigDecimal numklo = (BigDecimal)documento.getAttribute("numklodocumento");
							numklo = numklo.add(new BigDecimal(1));
							BigDecimal numProtBd = (BigDecimal)documento.getAttribute("NUMPROTOCOLLO");
							BigDecimal numAnnoProtBd = (BigDecimal)documento.getAttribute("NUMANNOPROT");
							String dataOraProt = (String)documento.getAttribute("dataoraprot");
							/*serie di set molto importanti per la ristampa senza nuova protocollazione e con update del blob senza avere errori di ogni sorta*/
							getDocumento().setPrgDocumento(prgDoc);
							getDocumento().setNumKloDocumento(numklo);
							getDocumento().setNumProtocollo(numProtBd);
							getDocumento().setNumProtInserito(numProtBd);
							getDocumento().setNumAnnoProt(numAnnoProtBd);
							getDocumento().setDatProtocollazione(dataOraProt);
							getDocumento().setCodMonoIO("O");
							getDocumento().setTipoProt("N");
							getDocumento().setCrystalClearRelativeReportFile(getReportPath());
							
							getDocumento().emptyBLOBDoc(txExec, prgDoc);
							
							/*Object[] objAnnullaDoc = new Object[3];
							objAnnullaDoc[0] = numklo;
							objAnnullaDoc[1] = user.getCodut();
							objAnnullaDoc[2] = prgDoc;
							
							Boolean res = (Boolean) txExec.executeQuery("ANNULLA_DOCUMENTO_PATTO_RISTAMPA", objAnnullaDoc, "UPDATE");
							if (res == null || !res.booleanValue()) {
								throw new Exception("Impossibile annullare il documento precedente per la ristampa patto/accordo");
							}*/
						}
						else {
							throw new Exception("Impossibile trovare il documento precedente per la ristampa patto/accordo");
						}
					}
					else {
						Object[] objDoppi = new Object[3];
						objDoppi[0] = codTipoDoc;
						objDoppi[1] = new BigDecimal("14");
						objDoppi[2] = strChiaveTabella;
						SourceBean sbDocDoppi = (SourceBean) txExec.executeQuery("GET_DOCUMENTO_DOPPIO_PATTO", objDoppi, "SELECT");
						if (sbDocDoppi != null && sbDocDoppi.getAttribute("ROW") != null) {
							ProtocollaException ex = new ProtocollaException("Documento già presente per il patto");
							ex.setCode(MessageCodes.Protocollazione.ERR_DOC_PATTO_ESISTENTE);
							throw ex;
						}
					}
				}
				
				String dataStipulaPatto = request.containsAttribute("datStipula")?request.getAttribute("datStipula").toString():"";
				if (!dataStipulaPatto.equals("")) {
					getDocumento().setDatInizio(dataStipulaPatto);
				}
				
				if(ristampaPT != null && ristampaPT.equalsIgnoreCase("S")){
					Object[] objPtOnline = new Object[1];
					objPtOnline[0] = strChiaveTabella;
					
					SourceBean rowPattoRistampa = (SourceBean) txExec.executeQuery("GET_PATTO_RISTAMPA", objPtOnline, "SELECT");
						
					if (rowPattoRistampa != null) {
						String queryName = "UPDATE_PATTO_RISTAMPA";
						BigDecimal numklopattolavR = (BigDecimal)rowPattoRistampa.getAttribute("ROW.NUMKLOPATTOLAVORATORE");
						String flgPattoOnline = (String)rowPattoRistampa.getAttribute("ROW.FLGPATTOONLINE");
						String codMonoAccettazione = (String)rowPattoRistampa.getAttribute("ROW.CODMONOACCETTAZIONE");
						if(StringUtils.isFilledNoBlank(flgPattoOnline) && flgPattoOnline.equalsIgnoreCase("S") &&
								StringUtils.isFilledNoBlank(codMonoAccettazione) && codMonoAccettazione.equalsIgnoreCase("A")){
							queryName = "UPDATE_PATTO_ONLINE_RISTAMPA";
						}
						numklopattolavR = numklopattolavR.add(new BigDecimal(1));
						objPtOnline = new Object[3];
						objPtOnline[0] = numklopattolavR;
						objPtOnline[1] = user.getCodut();
						objPtOnline[2] = strChiaveTabella;
						
						Boolean res = (Boolean) txExec.executeQuery(queryName, objPtOnline, "UPDATE");
						if (res == null || !res.booleanValue()) {
							throw new Exception("Impossibile aggiornare le info protocollazione per la ristampa patto/accordo");
						}
						
						request.updAttribute("NUMKLOPATTOLAVORATORE", numklopattolavR);
					}
					
					getDocumento().inserisciBlob(txExec);
					updateDocument(request, response, txExec, null);
					txExec.commitTransaction();
				}
				else if (insertDocument(request, response, txExec)) {
					if ((numProt != null) && annoProt != null) {
						_db.enableTransactions(txExec);
						updateInfoProtocolloPatto(request, response, _db);
					}
					txExec.commitTransaction();
					
					if (!(ristampaPT != null && ristampaPT.equalsIgnoreCase("S"))) {
						String codTipoDoc = request.getAttribute("tipoDoc") !=null?request.getAttribute("tipoDoc").toString():"";
						if (codTipoDoc.equals("")) {
							codTipoDoc = Properties.DEFAULT_DOCUMENTO_PATTO;
						}
						PattoBean.bonificaDocumentiDoppi(_db, strChiaveTabella, codTipoDoc);
					}
					
				} else {
					if (txExec != null) {
						try {
							txExec.rollBackTransaction();
						} catch (EMFInternalError e1) {
							it.eng.sil.util.TraceWrapper
									.fatal(_logger,
											"Impossibile eseguire la rollBack nella transazione della stampa del patto",
											(Exception) e1);

						}
						
						String errorCode = (String)response.getAttribute("errorCode");
						if (!StringUtils.isEmpty(errorCode)){
							if (errorCode.equalsIgnoreCase(MessageCodes.General.CONCORRENZA+"")){
								_logger.error(MessageCodes.General.CONCORRENZA);
								setOperationFail(request, response, MessageCodes.General.CONCORRENZA);
								response.setAttribute("ECCEZIONEPROTOCOLLA", MessageCodes.General.CONCORRENZA);
								SQLException sqlException = new SQLException(null, null, MessageCodes.General.CONCORRENZA, null);
								throw new EMFInternalError(null, sqlException);
							}
						} else{
						
							Exception ex = new Exception(
									"stampa del patto fallita");
							it.eng.sil.util.TraceWrapper.fatal(_logger,
									"Errore nella stampa del patto", ex);
							setOperationFail(request, response, ex);
						}
					}
				}
				
			}
			
			catch (EMFInternalError emf) {
				
				if (emf.getNativeException() instanceof SQLException) {
					if( ((SQLException)emf.getNativeException()).getErrorCode() == MessageCodes.General.CONCORRENZA ){
						setOperationFail(request, response, MessageCodes.General.CONCORRENZA);
						response.setAttribute("ECCEZIONEPROTOCOLLA", MessageCodes.General.CONCORRENZA);
						SQLException sqlException = new SQLException(null, null, MessageCodes.General.CONCORRENZA, null);
						throw new EMFInternalError(null, sqlException);
					}
				}
			}
			
			catch (Exception ue) {
				if (txExec != null) {
					txExec.rollBackTransaction();
				}
				setOperationFail(request, response, ue);
				return;
			}
		
		} else if ("true".equalsIgnoreCase(apri)) {
			showDocument(request, response);
		}

	}

	private void eseguiStampaPattoConApi(SourceBean request, SourceBean response) {
		String strChiaveTabella = it.eng.sil.util.Utils.notNull(request.getAttribute("strChiaveTabella"));
		if (!strChiaveTabella.equals("")) {
			setStrChiavetabella(strChiaveTabella);
		}
		BigDecimal numProt = SourceBeanUtils.getAttrBigDecimal(request, "numProt", null);
		String annoProt = (String) request.getAttribute("annoProt");
		String dataProtocollo = (String) request.getAttribute("dataOraProt");
		String salva = (String) request.getAttribute("salvaDB");
		String apri = (String) request.getAttribute("apri");
		String ristampaPT = (String) request.getAttribute("ristampaPT");
		User user = (User) getRequestContainer().getSessionContainer().getAttribute("@@USER@@");
		TransactionQueryExecutor txExec = null;
		try {
			com.inet.report.Engine eng = null;
			AccessoSemplificato _db = new AccessoSemplificato(this);
			// per recuperare le informazioni utilizzate per la generazione
			// del report non e' necessario
			// l'utilizzo della transazione. Se necessaria nei passi
			// successivi verra' abilitata.
			_db.enableSimpleQuery();
			if ((salva != null) && salva.equalsIgnoreCase("true")) {
				// gestione codStatoAtto del documento collegato
				if (request.containsAttribute("dataOraProt")) {
					request.updAttribute("codStatoAtto", "PR");
					getDocumento().setCodStatoAtto("PR");
				}
				boolean transazioneAttiva = false;
				// abilito la transazione per l'inserimento del documento e per le eventuali operazioni successive
				txExec = new TransactionQueryExecutor(_db.getPool());
				txExec.initTransaction();
				if (ristampaPT != null && ristampaPT.equalsIgnoreCase("S")) {
					_db.enableTransactions(txExec);
					transazioneAttiva = true;
				}
				else {
					PattoBean.gestisciConcorrenza(request, txExec);
				}
				
				eng = makeEngine(request, response, _db);
				
				if (!strChiaveTabella.equals("")) {
					// CONTROLLO DOCUMENTI DOPPI PER IL PATTO
					String codTipoDoc = request.getAttribute("tipoDoc") !=null?request.getAttribute("tipoDoc").toString():"";
					if (codTipoDoc.equals("")) {
						codTipoDoc = Properties.DEFAULT_DOCUMENTO_PATTO;
					}
					// SE STO FACENDO UNA RISTAMPA DEVO ANNULLARE PRIMA IL DOCUMENTO PROTOCOLLATO --> gennaio 2019 non si annulla piu
					if (ristampaPT != null && ristampaPT.equalsIgnoreCase("S")) {
						Object[] objDoc = new Object[3];
						objDoc[0] = codTipoDoc;
						objDoc[1] = new BigDecimal("14");
						objDoc[2] = strChiaveTabella;
						SourceBean sbDoc = (SourceBean) txExec.executeQuery("GET_DOCUMENTO_PATTO_X_RISTAMPA", objDoc, "SELECT");
						if (sbDoc != null && sbDoc.getAttributeAsVector("ROW") != null && sbDoc.getAttributeAsVector("ROW").size() == 1) {
							SourceBean documento = (SourceBean) sbDoc.getAttribute("ROW");
							BigDecimal prgDoc = (BigDecimal)documento.getAttribute("prgdocumento");
							BigDecimal numklo = (BigDecimal)documento.getAttribute("numklodocumento");
							numklo = numklo.add(new BigDecimal(1));
							BigDecimal numProtBd = (BigDecimal)documento.getAttribute("NUMPROTOCOLLO");
							BigDecimal numAnnoProtBd = (BigDecimal)documento.getAttribute("NUMANNOPROT");
							String dataOraProt = (String)documento.getAttribute("dataoraprot");
							/*serie di set molto importanti per la ristampa senza nuova protocollazione e con update del blob senza avere errori di ogni sorta*/
							getDocumento().setPrgDocumento(prgDoc);
							getDocumento().setNumKloDocumento(numklo);
							getDocumento().setNumProtocollo(numProtBd);
							getDocumento().setNumProtInserito(numProtBd);
							getDocumento().setNumAnnoProt(numAnnoProtBd);
							getDocumento().setDatProtocollazione(dataOraProt);
							getDocumento().setCodMonoIO("O");
							getDocumento().setTipoProt("N");
							getDocumento().setCrystalClearRelativeReportFile(getReportPath());
							getDocumento().emptyBLOBDoc(txExec, prgDoc);

							/*Object[] objAnnullaDoc = new Object[3];
							objAnnullaDoc[0] = numklo;
							objAnnullaDoc[1] = user.getCodut();
							objAnnullaDoc[2] = prgDoc;
							
							Boolean res = (Boolean) txExec.executeQuery("ANNULLA_DOCUMENTO_PATTO_RISTAMPA", objAnnullaDoc, "UPDATE");
							if (res == null || !res.booleanValue()) {
								throw new Exception("Impossibile annullare il documento precedente per la ristampa patto/accordo");
							}*/
						}
						else {
							throw new Exception("Impossibile trovare il documento precedente per la ristampa patto/accordo");
						}
					}
					else {
						Object[] objDoppi = new Object[3];
						objDoppi[0] = codTipoDoc;
						objDoppi[1] = new BigDecimal("14");
						objDoppi[2] = strChiaveTabella;
						SourceBean sbDocDoppi = (SourceBean) txExec.executeQuery("GET_DOCUMENTO_DOPPIO_PATTO",
								objDoppi, "SELECT");
						if (sbDocDoppi != null && sbDocDoppi.getAttribute("ROW") != null) {
							ProtocollaException ex = new ProtocollaException("Documento già presente per il patto");
							ex.setCode(MessageCodes.Protocollazione.ERR_DOC_PATTO_ESISTENTE);
							throw ex;
						}
					}
				}
				// Affinche' il report possa essere generato due volte debbo
				// passare dei parametri di prompt
				Map prompts = new HashMap();
				// prompts.put("numProt", "");
				Vector infGenV = infoGenerali.getAttributeAsVector("row");
				SourceBean infGen = infGenV.size() == 0 ? new SourceBean("ROWS") : (SourceBean) infGenV.get(0);
				// prompts.put("pCpi", (String)infGen.getAttribute("CPI"));
				// prompts.put("pDataStipula",
				// (String)infGen.getAttribute("datStipula"));
				// prompts.put("pDataScadenza",
				// (String)infGen.getAttribute("datScadConferma"));
				// solo se e' richiesta la protocollazione i parametri
				// vengono inseriti nella Map
				// se manca anche solo un parametro il metodo lancia una
				// eccezione.
				try {
					addPromptFieldsProtocollazione(prompts, request);
				} catch (EMFUserError ue) {
					if (txExec != null) {
						txExec.rollBackTransaction();
					}
					setOperationFail(request, response, ue);
					return;
				}
				// ora si chiede di usare il passaggio dei parametri per
				// nome e
				// non per posizione (col vettore, passaggio di default)
				setPromptFields(prompts);
				
				String dataStipulaPatto = request.containsAttribute("datStipula")?request.getAttribute("datStipula").toString():"";
				if (!dataStipulaPatto.equals("")) {
					getDocumento().setDatInizio(dataStipulaPatto);
				}
				if(ristampaPT != null && ristampaPT.equalsIgnoreCase("S")){
					updateDocument(request, response, txExec, eng);
					//getDocumento().inserisciBlob(txExec);					
					//getDocumento().update(txExec);					
				}
				else if (insertDocument(request, response, txExec, eng)) {
					if ((numProt != null) && annoProt != null) {
						if (!transazioneAttiva) {
							_db.enableTransactions(txExec);
							transazioneAttiva = true;
						}
						updateInfoProtocolloPatto(request, response, _db);
					}
				} else {
					throw new Exception("stampa del patto fallita");
				}
				txExec.commitTransaction();
					
//				if (!(ristampaPT != null && ristampaPT.equalsIgnoreCase("S"))) {
//					String codTipoDoc = request.getAttribute("tipoDoc") !=null?request.getAttribute("tipoDoc").toString():"";
//					if (codTipoDoc.equals("")) {
//						codTipoDoc = Properties.DEFAULT_DOCUMENTO_PATTO;
//					}
//					PattoBean.bonificaDocumentiDoppi(_db, strChiaveTabella, codTipoDoc);
//				}

				
			} else if ((apri != null) && apri.equalsIgnoreCase("true")) {
				eng = makeEngine(request, response, _db);
				showDocument(request, response, eng);
			}
			/*
			 * codice spostato in updateInfoProtocolloPatto() if ((numProt !=
			 * null) && annoProt != null) { // e' stata richiesta la
			 * protocollazione // leggi il patto // se non protocollato
			 * allora // si aggiorna il campo datultimoprotocollo
			 * _db.setSectionQuerySelect("QUERY_PATTO_APERTO"); SourceBean
			 * pattoAperto = _db.doSelect(request, response, false); String
			 * codStatoPatto =
			 * (String)pattoAperto.getAttribute("row.codStatoAtto"); boolean
			 * pattoProtocollato = codStatoPatto != null &&
			 * codStatoPatto.equals("PR"); if (!pattoProtocollato) {
			 * request.updAttribute("datProtocolloInf", dataProtocollo);
			 * Object prgPatto =
			 * pattoAperto.getAttribute("row.prgPattoLavoratore");
			 * request.setAttribute("prgPattoLavoratore", prgPatto);
			 * _db.setSectionQueryUpdate("UPDATE_DAT_PROT_INF_LEGATE");
			 * _db.doUpdate(request, response, false); }
			 * request.updAttribute("datUltimoProtocollo", dataProtocollo);
			 * _db.setSectionQueryUpdate("UPDATE_PATTO_DAT_ULTIMO_PROT");
			 * _db.doUpdate(request, response, false); }
			 */
		} catch (Exception e) {
			if (txExec != null)
				try {
					txExec.rollBackTransaction();
				} catch (EMFInternalError e1) {
					it.eng.sil.util.TraceWrapper.fatal(_logger,
							"Impossibile eseguire la rollBack nella transazione della stampa del patto",
							(Exception) e1);

				}
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nella stampa del patto", e);

			setOperationFail(request, response, e);
		}
	}

	/**
	 * 
	 */
	private void updateInfoProtocolloPatto(SourceBean request, SourceBean response, AccessoSemplificato _db)
			throws SourceBeanException {
		// e' stata richiesta la protocollazione
		// leggi il patto
		// se non protocollato allora
		// si aggiorna il campo datultimoprotocollo
		String dataProtocollo = (String) request.getAttribute("dataOraProt");
		// Savino 21/10/2005: c'e' una funzionalita' al momento non usata, che
		// permette di tenere traccia dell'ultimo
		// protocollo. Quando si protocolla piu' di una volta il patto, bisogna
		// aggiornare la data ultimo protocollo.
		// Ma al momento questa possibilita' e' stata omessa. Il patto puo'
		// essere protocollato solo una volta.
		_db.setSectionQuerySelect("QUERY_PATTO_APERTO");
		SourceBean pattoAperto = _db.doSelect(request, response, false);
		String codStatoPatto = (String) pattoAperto.getAttribute("row.codStatoAtto");
		boolean pattoProtocollato = codStatoPatto != null && codStatoPatto.equals("PR");
		// nel momento in cui si protocolla il patto bisogna aggiornare le date di protocollo delle info legate
		request.updAttribute("datProtocolloInf", dataProtocollo);
		Object prgPatto = pattoAperto.getAttribute("row.prgPattoLavoratore");
		request.setAttribute("prgPattoLavoratore", prgPatto);
		_db.setSectionQueryUpdate("UPDATE_DAT_PROT_INF_LEGATE");
		_db.doUpdate(request, response, false);
		// estraggo il NUMKLOPATTOLAVORATORE e lo aggiorno
		BigDecimal numklopattolav = ((BigDecimal)pattoAperto.getAttribute("ROW.NUMKLOPATTOLAVORATORE")).add(new BigDecimal("1"));
		request.updAttribute("NUMKLOPATTOLAVORATORE", numklopattolav);
		// a questo punto si aggiorna la data ultimo protocollo del patto
		request.updAttribute("datUltimoProtocollo", dataProtocollo);
		_db.setSectionQueryUpdate("UPDATE_PATTO_DAT_ULTIMO_PROT");
		_db.doUpdate(request, response, false);
	}
	
	private Engine makeEngine(SourceBean request, SourceBean response, AccessoSemplificato _db) throws Exception {

		_db.disableMessageIdFail();
		_db.disableMessageIdSuccess();
		
		SourceBean beanRows = null;
		_db.setSectionQuerySelect("GET_CODREGIONE");
		beanRows = _db.doSelect(request, response, false);
		String regione = (String) beanRows.getAttribute("ROW.CODREGIONE");
		
		String cod = "";
		if (!regione.equals(Properties.TN)) {
			cod = "_API";	
		}
		
		_db.setSectionQuerySelect("QUERY_STATO_OCCUPAZIONALE");
		SourceBean statoOccupazionale = _db.doSelect(request, response);
		String ristampaPT = (String) request.getAttribute("ristampaPT");
		// recupero il prgStatoOccupaz poichè serve alla query
		// UPDATE_PATTO_DAT_ULTIMO_PROT
		if (!request.containsAttribute("PRGSTATOOCCUPAZ") || request.getAttribute("PRGSTATOOCCUPAZ") == null) {
			request.setAttribute("PRGSTATOOCCUPAZ", statoOccupazionale.getAttribute("ROW.PRGSTATOOCCUPAZ"));
		}
		_db.setSectionQuerySelect("QUERY_GET_OPERATORE");
		SourceBean operatore = _db.doSelect(request, response);
		_db.setSectionQuerySelect("QUERY_APPUNTAMENTI"+cod);
		SourceBean appuntamenti = _db.doSelect(request, response);
		//TODO vedere se c'è bisogno di api/ non api
		_db.setSectionQuerySelect("QUERY_SOGGACCREDITATO_API");
		SourceBean soggetti = _db.doSelect(request, response);
		_db.setSectionQuerySelect("QUERY_AZIONI"+cod);
		SourceBean azioniConcordate = _db.doSelect(request, response);
		_db.setSectionQuerySelect("QUERY_AMBITO_PROFESSIONALE");
		SourceBean ambitoProfessionale = _db.doSelect(request, response, false);
		_db.setSectionQuerySelect("GET_TIPODOC");
		SourceBean ambitoDocumento = _db.doSelect(request, response, false);
		_db.setSectionQuerySelect("QUERY_IMPEGNI");
		SourceBean impegni = _db.doSelect(request, response, false);
		
		SourceBean configPrivacy = null;
		_db.setSectionQuerySelect("CONFIG_PRIVACY");
		configPrivacy = _db.doSelect(request, response, false);
		String privacy = (String) configPrivacy.getAttribute("ROW.VALORENUM");
		
		if (ristampaPT != null && ristampaPT.equalsIgnoreCase("S")) {
			_db.setSectionQuerySelect("QUERY_INFO_CPI_COMPETENTE");
			SourceBean rowCpi = _db.doSelect(request, response, false);
			_db.setSectionQuerySelect("QUERY_INFO_CPI_PATTO");
			SourceBean rowCpiPatto = _db.doSelect(request, response, false);
			if (rowCpi != null && rowCpiPatto != null) {
				String codCpiComp = (String) rowCpi.getAttribute("ROW.CODCPITIT");
				String codCpiPatto = (String) rowCpiPatto.getAttribute("ROW.CODCPI");
				BigDecimal numklopattolav = (BigDecimal)rowCpiPatto.getAttribute("ROW.NUMKLOPATTOLAVORATORE");
				if (codCpiComp != null && codCpiPatto != null && !codCpiComp.equalsIgnoreCase(codCpiPatto)) {
					// a questo punto si aggiorna il cpi del patto che si vuole riprotocollare
					numklopattolav = numklopattolav.add(new BigDecimal(1));
					request.updAttribute("NEWCODCPI", codCpiComp);
					request.updAttribute("NUMKLOPATTOLAVORATORE", numklopattolav);
					_db.setSectionQueryUpdate("UPDATE_CPI_PATTO");
					_db.doUpdate(request, response, false);
				}
			}
			//luglio 2020: memorizzare dataRistampaPatto e se pattoOnline settare flgReinvioPtOnline a S
			_db.setSectionQuerySelect("QUERY_INFO_RISTAMPA_PATTO");
			SourceBean rowPattoRistampa = _db.doSelect(request, response, false);
			if (rowPattoRistampa != null) {
				String queryName = "UPDATE_RISTAMPA_PATTO";
				BigDecimal numklopattolavR = (BigDecimal)rowPattoRistampa.getAttribute("ROW.NUMKLOPATTOLAVORATORE");
				String flgPattoOnline = (String)rowPattoRistampa.getAttribute("ROW.FLGPATTOONLINE");
				String codMonoAccettazione = (String)rowPattoRistampa.getAttribute("ROW.CODMONOACCETTAZIONE");
				if(StringUtils.isFilledNoBlank(flgPattoOnline) && flgPattoOnline.equalsIgnoreCase("S") &&
						StringUtils.isFilledNoBlank(codMonoAccettazione) && codMonoAccettazione.equalsIgnoreCase("A")){
					queryName = "UPDATE_RISTAMPA_PATTO_ONLINE";
				}	
				numklopattolavR = numklopattolavR.add(new BigDecimal(1));
				request.updAttribute("NUMKLOPATTOLAVORATORE", numklopattolavR);
				_db.setSectionQueryUpdate(queryName);
				_db.doUpdate(request, response, false);
			}
		}
		
		_db.setSectionQuerySelect("QUERY_INFO_GENERALI");
		infoGenerali = _db.doSelect(request, response);
		_db.setSectionQuerySelect("GET_181_CAT");
		SourceBean cat181 = _db.doSelect(request, response, false);
		_db.setSectionQuerySelect("GET_LAUREA_X_CAT181");
		SourceBean laurea = _db.doSelect(request, response, false);
		_db.setSectionQuerySelect("GET_MOVIMENTI_LAVORATORE");
		SourceBean movimenti = _db.doSelect(request, response, false);
		_db.setSectionQuerySelect("GET_PATTO_DOCUMENTO_IDENTITA");
		SourceBean documentiIdentitaSourceBean = _db.doSelect(request, response, false);
		Vector documentiIdentitaVector = documentiIdentitaSourceBean.getAttributeAsVector("ROW");
		SourceBean documentoIdentita = null;
		if (documentiIdentitaVector != null && documentiIdentitaVector.size() > 0) {
			documentoIdentita = (SourceBean)documentiIdentitaVector.get(0);
		}
		
		Vector appuntaments = appuntamenti.getAttributeAsVector("ROW");
		Vector entiAccreditati = soggetti.getAttributeAsVector("ROW");
		Vector azioniConcordats = azioniConcordate.getAttributeAsVector("ROW");
		Vector ambitoProfs = ambitoProfessionale.getAttributeAsVector("ROW");
		Vector infoGeneraliV = infoGenerali.getAttributeAsVector("ROW");
		if (infoGeneraliV.size() == 0) {
			// recupero le info generali considerando l'associazione patto con
			// la mobilità
			_db.setSectionQuerySelect("QUERY_INFO_GENERALI_PATTO_MOBILITA");
			infoGenerali = _db.doSelect(request, response);
			infoGeneraliV = infoGenerali.getAttributeAsVector("ROW");
		}
		else {
			SourceBean infoGen = (SourceBean) infoGeneraliV.get(0);
			String datdichiarazione = (String) infoGen.getAttribute("datdichiarazione");
			if(!StringUtils.isEmpty(datdichiarazione)){
				request.setAttribute("datDidConf", datdichiarazione);
			}
		}
		
		
		_db.setSectionQuerySelect("QUERY_CONFERIMENTO_DID");
		SourceBean lastConferimentoDid = _db.doSelect(request, response);
		
		Vector impegniV = impegni.getAttributeAsVector("ROW");
		// generazione del report tramite api crystalclear
		String tipoFile = (String) request.getAttribute("tipoFile");
		
		//nel caso della VALLE D'AOSTA (codregione=2) è stata creata una nuova stampa
		
		String codice = "";
		
		String config_Stampa = Properties.DEFAULT_CONFIG;
		SourceBean rowConfig = doSelect("ST_CONF_STAMPA_PATTO", null);
		if (rowConfig != null) {
			config_Stampa = rowConfig.containsAttribute("ROW.NUM")?rowConfig.getAttribute("ROW.NUM").toString():Properties.DEFAULT_CONFIG;
			
		}		
		//Prende la conf
		
		if(config_Stampa.equals("3")) { //Umbria 
			codice = "UMB";
		} else if(config_Stampa.equals("2")) { //Valle d'aosta 
			codice = "VDA";
		} 
		
		//if(regione.equals("2")) { codice = "VDA";}
		Class report = Class.forName("it.eng.sil.action.report.patto.ApiPatto"+codice);
		Method inizializzaMethod = report.getMethod("inizializza", new Class[] { SourceBean.class, SourceBean.class, Vector.class, 
									SourceBean.class, Vector.class, Vector.class, Vector.class, String.class, String.class,SourceBean.class,
									Vector.class,Vector.class,String.class,String.class,String.class, String.class, 
									SourceBean.class, SourceBean.class, String.class, Vector.class, TransactionQueryExecutor.class});
		
		Method getEngineMethod = report.getMethod("getEngine", new Class[] { });

		SourceBean infoGen = (SourceBean) infoGeneraliV.get(0);
		SourceBean statoOcc = (SourceBean) statoOccupazionale.getAttribute("ROW");
		SourceBean categoria181 = it.eng.sil.util.amministrazione.impatti.DBLoad.getRowAttribute(cat181);
		String ambito = (String) ambitoDocumento.getAttribute("ROW.RIFERIMENTO");
		Vector titoloStudio = laurea.getAttributeAsVector("ROW");
		Vector mov = movimenti.getAttributeAsVector("ROW");
		String installAppPath = ConfigSingleton.getRootPath() + java.io.File.separatorChar;
		
		String strParam = null;
		strParam = (String) request.getAttribute("dataOraProt");
		
		String docInOut = "O";
		
		Object o = report.newInstance();
		
		inizializzaMethod.invoke(o , new Object[] { infoGen,statoOcc,appuntaments,operatore,azioniConcordats,ambitoProfs,impegniV,
				installAppPath,tipoFile,categoria181,titoloStudio,mov,ambito,strParam,docInOut, regione, documentoIdentita, 
				lastConferimentoDid, privacy,entiAccreditati, null });
		
		/*if(regione.equals("2")) {
			ApiPattoVDA report = new ApiPattoVDA();
			report.setInfoGenerali((SourceBean) infoGeneraliV.get(0));
			report.setOperatore(operatore);
			String installAppPath = ConfigSingleton.getRootPath() + java.io.File.separatorChar;
			report.setInstallAppPath(installAppPath);
			report.setFileType(tipoFile);
			
			report.start();
			return report.getEngine();
			
		} else {
			ApiPatto report = new ApiPatto();	
			report.setInfoGenerali((SourceBean) infoGeneraliV.get(0));
			report.setStatoOccupazionale((SourceBean) statoOccupazionale.getAttribute("ROW"));
			report.setAppuntamenti(appuntaments);
			report.setAzioniConcordate(azioniConcordats);
			report.setAmbitoProfessionale(ambitoProfs);
			report.setImpegni(impegniV);
			String installAppPath = ConfigSingleton.getRootPath() + java.io.File.separatorChar;
			report.setInstallAppPath(installAppPath);
			report.setFileType(tipoFile);
			report.setCat181(it.eng.sil.util.amministrazione.impatti.DBLoad.getRowAttribute(cat181));
			report.setLaurea(laurea.getAttributeAsVector("ROW"));
			report.setMovimenti(movimenti.getAttributeAsVector("ROW"));
			report.setRiferimentoDoc((String) ambitoDocumento.getAttribute("ROW.RIFERIMENTO"));*/
			
		return (Engine) getEngineMethod.invoke(o , new Object[] {});
	}
	
	public SourceBean doSelect(String stmName, Object params[]) throws Exception {
		SourceBean result = null;
		result = (SourceBean)QueryExecutor.executeQuery(stmName, params, "SELECT", Values.DB_SIL_DATI);
		return result;
	}
	
		
	
}

// class Patto
