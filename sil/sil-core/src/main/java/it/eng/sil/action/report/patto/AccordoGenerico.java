package it.eng.sil.action.report.patto;

import java.lang.reflect.Method;
import java.math.BigDecimal;
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
import it.eng.sil.bean.ProtocollaException;
import it.eng.sil.module.AccessoSemplificato;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.security.User;
import it.eng.sil.util.amministrazione.impatti.PattoBean;

public class AccordoGenerico extends AbstractSimpleReport {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(AccordoGenerico.class.getName());

	private SourceBean infoGenerali;
	
	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);

		String apriFile = (String) request.getAttribute("apriFileBlob");
		if ((apriFile != null) && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			try {
				Object cdnLavoratore = request.getAttribute("cdnLavoratore");
				int codiceErrore = -1;
				
				//BigDecimal numProgrammiKO = PattoBean.checkProgrammiApertiSenzaAzioni(new BigDecimal(cdnLavoratore.toString()), null);
				//if (numProgrammiKO != null && numProgrammiKO.intValue()>0) {
				//	throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.Patto.ERR_PROGRAMMA_APERTO_NO_ATTIVITA);
				//}
				
				Object prgpattolavoratore = request.getAttribute("strChiaveTabella");
				Vector<String>programmi = PattoBean.checkProgrammi(new BigDecimal(prgpattolavoratore.toString()), null);
				
				if (programmi==null) {
					Vector v = new Vector(1);
					v.add("Controllo Recupero Programmi");
					throw new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.Patto.ERR_CHECKCONTROLLIPROTOCOLLAZIONE, v);
				}
				else {
					if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_ASSEGNO_RICOLLOCAZIONE) || 
						PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_INTERVENTO_OCCUPAZIONE)) {
						String datInizioNaspi = "";
						String datRiferimento150 = "";
						BigDecimal numIndiceSvant150 = null;
						BigDecimal importoADR = null;
						SourceBean patto = PattoBean.caricaPatto(prgpattolavoratore);
						if (patto != null) {
							patto = patto.containsAttribute("ROW")?(SourceBean)patto.getAttribute("ROW"):patto;
							datInizioNaspi = patto.containsAttribute("DATANASPI")?patto.getAttribute("DATANASPI").toString():"";
							datRiferimento150 = patto.containsAttribute("DATRIFERIMENTO150")?patto.getAttribute("DATRIFERIMENTO150").toString():"";
							numIndiceSvant150 = (BigDecimal)patto.getAttribute("NUMINDICESVANTAGGIO150");
							importoADR = (BigDecimal)patto.getAttribute("IMPORTOAR");	
						}
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
						codiceErrore = PattoBean.checkProtocollazioneAccordoMGGU(cdnLavoratore);
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
				
				setStrDescrizione("Accordo generico lavoratore");
	
				String tipoFile = (String) request.getAttribute("tipoFile");
	
				if (tipoFile != null) {
					setStrNomeDoc("accordo." + tipoFile);
				} else {
					setStrNomeDoc("accordo.pdf");
				}
				// ATTENZIONE: necessario comunque anche se il report viene generato con le api
				setReportPath("patto/accordo_CC.rpt");
				
				try {
					eseguiStampaAccordoConApi(request, response);
				} 
				catch (Exception e) {
					it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nella stampa accordo generico", e);
					setOperationFail(request, response, e);
				}
			}
			
			catch (EMFUserError tEx) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nella stampa accordo generico", tEx);
				setOperationFail(request, response, tEx);
			}
			catch (Exception e) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nella stampa accordo generico", e);
				setOperationFail(request, response, e);
			}
		}
	}

	private void eseguiStampaAccordoConApi(SourceBean request, SourceBean response) {
		String strChiaveTabella = it.eng.sil.util.Utils.notNull(request.getAttribute("strChiaveTabella"));
		if (!strChiaveTabella.equals("")) {
			setStrChiavetabella(strChiaveTabella);
		}
		BigDecimal numProt = SourceBeanUtils.getAttrBigDecimal(request, "numProt", null);
		String annoProt = (String) request.getAttribute("annoProt");
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
				
				eng = makeEngine(request, response, _db);
				if (!strChiaveTabella.equals("")) {
					// CONTROLLO DOCUMENTI DOPPI PER ACCORDO
					String codTipoDoc = request.getAttribute("tipoDoc") !=null?request.getAttribute("tipoDoc").toString():"";
					if (codTipoDoc.equals("")) {
						codTipoDoc = Properties.DOCUMENTO_PATTO_GENERICO;
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

							/*
							Object[] objAnnullaDoc = new Object[3];
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
							ProtocollaException ex = new ProtocollaException("Documento già presente per l'accordo");
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
				
				String dataStipulaAccordo = request.containsAttribute("datStipula")?request.getAttribute("datStipula").toString():"";
				if (!dataStipulaAccordo.equals("")) {
					getDocumento().setDatInizio(dataStipulaAccordo);
				}
				if(ristampaPT != null && ristampaPT.equalsIgnoreCase("S")){
					updateDocument(request, response, txExec, eng);
					//getDocumento().inserisciBlob(txExec);					
					//getDocumento().update(txExec);	
					txExec.commitTransaction();
				}
				else if (insertDocument(request, response, txExec, eng)) {
					if ((numProt != null) && annoProt != null) {
						if (!transazioneAttiva) {
							_db.enableTransactions(txExec);
							transazioneAttiva = true;
						}
						updateInfoProtocolloAccordo(request, response, _db);
					}
					if (!strChiaveTabella.equals("")) {
						if (!transazioneAttiva) {
							_db.enableTransactions(txExec);
							transazioneAttiva = true;
						}
						request.updAttribute("prgPattoLavoratore", strChiaveTabella);
						_db.setSectionQuerySelect("QUERY_ACCORDO_PROTOCOLLATO_STATO_OCC");
						SourceBean accordo = _db.doSelect(request, response, false);
						accordo = accordo.containsAttribute("ROW")?(SourceBean)accordo.getAttribute("ROW"):accordo;
						BigDecimal numklopattolav = (BigDecimal) accordo.getAttribute("numklopattolavoratore");
						numklopattolav = numklopattolav.add(new BigDecimal(1));
						request.updAttribute("numklopattolavoratore", numklopattolav);
						_db.setSectionQueryUpdate("UPDATE_ACCORDO_PROTOCOLLATO_STATO_OCC");
						_db.doUpdate(request, response, false);
					}
					txExec.commitTransaction();
					
//					if (!(ristampaPT != null && ristampaPT.equalsIgnoreCase("S"))) {
//						String codTipoDoc = request.getAttribute("tipoDoc") !=null?request.getAttribute("tipoDoc").toString():"";
//						if (codTipoDoc.equals("")) {
//							codTipoDoc = Properties.DEFAULT_DOCUMENTO_PATTO;
//						}
//						PattoBean.bonificaDocumentiDoppi(_db, strChiaveTabella, codTipoDoc);
//					}
										
				} else {
					throw new Exception("stampa accordo fallita");
				}
				
			} else if ((apri != null) && apri.equalsIgnoreCase("true")) {
				eng = makeEngine(request, response, _db);
				showDocument(request, response, eng);
			}
		} 
		catch (Exception e) {
			if (txExec != null)
				try {
					txExec.rollBackTransaction();
				} catch (EMFInternalError e1) {
					it.eng.sil.util.TraceWrapper.fatal(_logger,
							"Impossibile eseguire la rollBack nella transazione della stampa accordo",
							(Exception) e1);

				}
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nella stampa accordo", e);

			setOperationFail(request, response, e);
		}
	}

	/**
	 * 
	 */
	private void updateInfoProtocolloAccordo(SourceBean request, SourceBean response, AccessoSemplificato _db)
			throws SourceBeanException {
		String dataProtocollo = (String) request.getAttribute("dataOraProt");
		_db.setSectionQuerySelect("QUERY_PATTO_APERTO");
		SourceBean pattoAperto = _db.doSelect(request, response, false);
		String codStatoPatto = (String) pattoAperto.getAttribute("row.codStatoAtto");
		// nel momento in cui si protocolla bisogna aggiornare le date di protocollo delle info legate
		request.updAttribute("datProtocolloInf", dataProtocollo);
		Object prgPatto = pattoAperto.getAttribute("row.prgPattoLavoratore");
		request.setAttribute("prgPattoLavoratore", prgPatto);
		_db.setSectionQueryUpdate("UPDATE_DAT_PROT_INF_LEGATE");
		_db.doUpdate(request, response, false);
		// a questo punto si aggiorna la data ultimo protocollo accordo
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
		// recupero il prgStatoOccupaz poiché serve alla query UPDATE_PATTO_DAT_ULTIMO_PROT
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
		
		_db.setSectionQuerySelect("QUERY_INFO_GENERALI_ACCORDO_GENERICO");
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
		
		Vector impegniV = impegni.getAttributeAsVector("ROW");
		// generazione del report tramite api crystalclear
		String tipoFile = (String) request.getAttribute("tipoFile"); 
		
		String codice = "";
		if (regione.equals(Properties.RER) || regione.equals(Properties.CAL)) {  
			codice = "_RER_CAL";
		} else if (regione.equals(Properties.UMB)) {
			codice = "_UMB";
		} else if (regione.equals(Properties.VDA)) {
			codice = "_VDA";
		}
		
		Class report = Class.forName("it.eng.sil.action.report.patto.ApiAccordoGenerico"+codice);
		Method m = report.getMethod("inizializza", new Class[] { SourceBean.class, SourceBean.class, Vector.class, 
									SourceBean.class, Vector.class, Vector.class, Vector.class, String.class, String.class,SourceBean.class,
									Vector.class,Vector.class,String.class,String.class,String.class, String.class, 
									SourceBean.class, String.class, Vector.class, TransactionQueryExecutor.class});
		
		Method ret = report.getMethod("getEngine", new Class[] { });

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
		
		m.invoke(o , new Object[] { infoGen,statoOcc,appuntaments,operatore,azioniConcordats,ambitoProfs,impegniV,
				installAppPath,tipoFile,categoria181,titoloStudio,mov,ambito,strParam,docInOut, regione, documentoIdentita, privacy , entiAccreditati, null});
			
		return (Engine) ret.invoke(o , new Object[] {});
	}
	
	public SourceBean doSelect(String stmName, Object params[]) throws Exception {
		SourceBean result = null;
		result = (SourceBean)QueryExecutor.executeQuery(stmName, params, "SELECT", Values.DB_SIL_DATI);
		return result;
	}
}


// class AccordoGenerico
