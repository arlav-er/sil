package it.eng.sil.action.report.patto;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;
import com.inet.report.Engine;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.SourceBeanUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.bean.ProtocollaException;
import it.eng.sil.module.AccessoSemplificato;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.security.User;
import it.eng.sil.util.amministrazione.impatti.PattoBean;

public class PattoAccordoADR extends AbstractSimpleReport {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4861711903186941827L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(PattoAccordoADR.class.getName());

	private SourceBean infoGenerali;

	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);

		String apriFile = (String) request.getAttribute("apriFileBlob");
		if ((apriFile != null) && apriFile.equalsIgnoreCase("true")) {
			BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
			this.openDocument(request, response, prgDoc);
		} else {
			try {
				boolean isPattoADR = false;
				String codTipoPatto = it.eng.sil.util.Utils.notNull(request.getAttribute("tipoMisura"));
				String strChiaveTabella = it.eng.sil.util.Utils.notNull(request.getAttribute("strChiaveTabella"));
				if (!strChiaveTabella.equals("")) {
					BigDecimal prgPattoLav = new BigDecimal(strChiaveTabella);
					Vector<String> programmi = PattoBean.checkAllProgrammi(prgPattoLav, null);
					if (programmi != null && !programmi.isEmpty()
							&& PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_ASSEGNO_RICOLLOCAZIONE)) {
						isPattoADR = true;
					}
				}
				if (!codTipoPatto.equalsIgnoreCase(PattoBean.DB_MISURE_ASSEGNO_RICOLLOCAZIONE) && !isPattoADR) {
					ProtocollaException ep = new ProtocollaException(0);
					ep.setCode(MessageCodes.Protocollazione.ERR_STAMPA_ADR_MISURA);
					throw ep;
				}

				setStrDescrizione("Patto lavoratore ADR");

				String tipoFile = (String) request.getAttribute("tipoFile");

				if (tipoFile != null) {
					setStrNomeDoc("pattoADR." + tipoFile);
				} else {
					setStrNomeDoc("pattoADR.pdf");
				}
				// ATTENZIONE: necessario comunque anche se il report viene generato con le api
				setReportPath("patto/patto_CC.rpt");

				eseguiStampaPattoConApi(request, response);
			}

			catch (Exception e) {
				it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nella stampa del patto/accordo", e);
				setOperationFail(request, response, e);
			}
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

				eng = makeEngine(request, response, _db);

				if (!strChiaveTabella.equals("")) {
					// CONTROLLO DOCUMENTI DOPPI PER IL PATTO
					String codTipoDoc = request.getAttribute("tipoDoc") != null
							? request.getAttribute("tipoDoc").toString()
							: "";
					if (codTipoDoc.equals("")) {
						codTipoDoc = Properties.DEFAULT_DOCUMENTO_PATTO;
					}
					// SE STO FACENDO UNA RISTAMPA DEVO ANNULLARE PRIMA IL DOCUMENTO PROTOCOLLATO
					if (ristampaPT != null && ristampaPT.equalsIgnoreCase("S")) {
						Object[] objDoc = new Object[3];
						objDoc[0] = codTipoDoc;
						objDoc[1] = new BigDecimal("14");
						objDoc[2] = strChiaveTabella;

						SourceBean sbDoc = (SourceBean) txExec.executeQuery("GET_DOCUMENTO_PATTO_X_RISTAMPA", objDoc,
								"SELECT");
						if (sbDoc != null && sbDoc.getAttributeAsVector("ROW") != null
								&& sbDoc.getAttributeAsVector("ROW").size() == 1) {
							SourceBean documento = (SourceBean) sbDoc.getAttribute("ROW");
							BigDecimal prgDoc = (BigDecimal) documento.getAttribute("prgdocumento");
							BigDecimal numklo = (BigDecimal) documento.getAttribute("numklodocumento");
							numklo = numklo.add(new BigDecimal(1));
							Object[] objAnnullaDoc = new Object[3];
							objAnnullaDoc[0] = numklo;
							objAnnullaDoc[1] = user.getCodut();
							objAnnullaDoc[2] = prgDoc;

							Boolean res = (Boolean) txExec.executeQuery("ANNULLA_DOCUMENTO_PATTO_RISTAMPA",
									objAnnullaDoc, "UPDATE");
							if (res == null || !res.booleanValue()) {
								throw new Exception(
										"Impossibile annullare il documento precedente per la ristampa patto/accordo");
							}
						} else {
							throw new Exception(
									"Impossibile annullare il documento precedente per la ristampa patto/accordo");
						}
					} else {
						Object[] objDoppi = new Object[3];
						objDoppi[0] = codTipoDoc;
						objDoppi[1] = new BigDecimal("14");
						objDoppi[2] = strChiaveTabella;
						SourceBean sbDocDoppi = (SourceBean) txExec.executeQuery("GET_DOCUMENTO_DOPPIO_PATTO", objDoppi,
								"SELECT");
						if (sbDocDoppi != null && sbDocDoppi.getAttribute("ROW") != null) {
							throw new Exception("Documento già presente per il patto");
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

				String dataStipulaPatto = request.containsAttribute("datStipula")
						? request.getAttribute("datStipula").toString()
						: "";
				if (!dataStipulaPatto.equals("")) {
					getDocumento().setDatInizio(dataStipulaPatto);
				}

				if (insertDocument(request, response, txExec, eng)) {
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
			} else if ((apri != null) && apri.equalsIgnoreCase("true")) {
				eng = makeEngine(request, response, _db);
				showDocument(request, response, eng);
			}
			/*
			 * codice spostato in updateInfoProtocolloPatto() if ((numProt != null) && annoProt != null) { // e' stata
			 * richiesta la protocollazione // leggi il patto // se non protocollato allora // si aggiorna il campo
			 * datultimoprotocollo _db.setSectionQuerySelect("QUERY_PATTO_APERTO"); SourceBean pattoAperto =
			 * _db.doSelect(request, response, false); String codStatoPatto =
			 * (String)pattoAperto.getAttribute("row.codStatoAtto"); boolean pattoProtocollato = codStatoPatto != null
			 * && codStatoPatto.equals("PR"); if (!pattoProtocollato) { request.updAttribute("datProtocolloInf",
			 * dataProtocollo); Object prgPatto = pattoAperto.getAttribute("row.prgPattoLavoratore");
			 * request.setAttribute("prgPattoLavoratore", prgPatto);
			 * _db.setSectionQueryUpdate("UPDATE_DAT_PROT_INF_LEGATE"); _db.doUpdate(request, response, false); }
			 * request.updAttribute("datUltimoProtocollo", dataProtocollo);
			 * _db.setSectionQueryUpdate("UPDATE_PATTO_DAT_ULTIMO_PROT"); _db.doUpdate(request, response, false); }
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
		// nel momento in cui si protocolla il patto bisogna aggiornare le date
		// di protocollo delle info legate
		if (/* !pattoProtocollato */true) {
			request.updAttribute("datProtocolloInf", dataProtocollo);
			Object prgPatto = pattoAperto.getAttribute("row.prgPattoLavoratore");
			request.setAttribute("prgPattoLavoratore", prgPatto);
			_db.setSectionQueryUpdate("UPDATE_DAT_PROT_INF_LEGATE");
			_db.doUpdate(request, response, false);
		}
		// a questo punto si aggiorna la data ultimo protocollo del patto
		request.updAttribute("datUltimoProtocollo", dataProtocollo);
		_db.setSectionQueryUpdate("UPDATE_PATTO_DAT_ULTIMO_PROT");
		_db.doUpdate(request, response, false);

	}

	private Engine makeEngine(SourceBean request, SourceBean response, AccessoSemplificato _db) throws Exception {
		String tipoDoc = it.eng.sil.util.Utils.notNull(request.getAttribute("tipoDoc"));
		SourceBean config = (SourceBean) getConfig();
		_db.disableMessageIdFail();
		_db.disableMessageIdSuccess();
		_db.setSectionQuerySelect("QUERY_STATO_OCCUPAZIONALE");
		SourceBean statoOccupazionale = _db.doSelect(request, response);
		String ristampaPT = (String) request.getAttribute("ristampaPT");
		// recupero il prgStatoOccupaz poiché serve alla query
		// UPDATE_PATTO_DAT_ULTIMO_PROT
		if (!request.containsAttribute("PRGSTATOOCCUPAZ") || request.getAttribute("PRGSTATOOCCUPAZ") == null) {
			request.setAttribute("PRGSTATOOCCUPAZ", statoOccupazionale.getAttribute("ROW.PRGSTATOOCCUPAZ"));
		}
		_db.setSectionQuerySelect("QUERY_GET_OPERATORE");
		SourceBean operatore = _db.doSelect(request, response);
		_db.setSectionQuerySelect("QUERY_APPUNTAMENTI");
		SourceBean appuntamenti = _db.doSelect(request, response);
		_db.setSectionQuerySelect("QUERY_AZIONI");
		SourceBean azioniConcordate = _db.doSelect(request, response);

		_db.setSectionQuerySelect("GET_TIPODOC");
		SourceBean ambitoDocumento = _db.doSelect(request, response, false);

		if (ristampaPT != null && ristampaPT.equalsIgnoreCase("S")) {
			_db.setSectionQuerySelect("QUERY_INFO_CPI_COMPETENTE");
			SourceBean rowCpi = _db.doSelect(request, response, false);
			_db.setSectionQuerySelect("QUERY_INFO_CPI_PATTO");
			SourceBean rowCpiPatto = _db.doSelect(request, response, false);
			if (rowCpi != null && rowCpiPatto != null) {
				String codCpiComp = (String) rowCpi.getAttribute("ROW.CODCPITIT");
				String codCpiPatto = (String) rowCpiPatto.getAttribute("ROW.CODCPI");
				BigDecimal numklopattolav = (BigDecimal) rowCpiPatto.getAttribute("ROW.NUMKLOPATTOLAVORATORE");
				if (codCpiComp != null && codCpiPatto != null && !codCpiComp.equalsIgnoreCase(codCpiPatto)) {
					// a questo punto si aggiorna il cpi del patto che si vuole riprotocollare
					numklopattolav = numklopattolav.add(new BigDecimal(1));
					request.updAttribute("NEWCODCPI", codCpiComp);
					request.updAttribute("NUMKLOPATTOLAVORATORE", numklopattolav);
					_db.setSectionQueryUpdate("UPDATE_CPI_PATTO");
					_db.doUpdate(request, response, false);
				}
			}
		}

		Vector appuntaments = appuntamenti.getAttributeAsVector("ROW");
		Vector azioniConcordats = azioniConcordate.getAttributeAsVector("ROW");
		Vector infoGeneraliV = null;

		if (tipoDoc.equalsIgnoreCase(Properties.DOCUMENTO_PATTO_GENERICO)) {
			_db.setSectionQuerySelect("QUERY_INFO_GENERALI_ACCORDO_GENERICO");
			infoGenerali = _db.doSelect(request, response);
			infoGeneraliV = infoGenerali.getAttributeAsVector("ROW");
		} else {
			_db.setSectionQuerySelect("QUERY_INFO_GENERALI");
			infoGenerali = _db.doSelect(request, response);
			infoGeneraliV = infoGenerali.getAttributeAsVector("ROW");
			if (infoGeneraliV.size() == 0) {
				// recupero le info generali considerando l'associazione patto con
				// la mobilità
				_db.setSectionQuerySelect("QUERY_INFO_GENERALI_PATTO_MOBILITA");
				infoGenerali = _db.doSelect(request, response);
				infoGeneraliV = infoGenerali.getAttributeAsVector("ROW");
			}
		}

		String tipoFile = (String) request.getAttribute("tipoFile");

		SourceBean beanRows = null;
		_db.setSectionQuerySelect("GET_CODREGIONE");
		beanRows = _db.doSelect(request, response, false);
		String regione = (String) beanRows.getAttribute("ROW.CODREGIONE");

		Class report = Class.forName("it.eng.sil.action.report.patto.ApiPattoAccordoADR");
		Method inizializzaMethod = report.getMethod("inizializza",
				new Class[] { SourceBean.class, Vector.class, SourceBean.class, Vector.class, String.class,
						String.class, String.class, String.class, String.class, String.class, SourceBean.class });

		Method getEngineMethod = report.getMethod("getEngine", new Class[] {});

		SourceBean infoGen = (SourceBean) infoGeneraliV.get(0);
		String ambito = (String) ambitoDocumento.getAttribute("ROW.RIFERIMENTO");
		String installAppPath = ConfigSingleton.getRootPath() + java.io.File.separatorChar;

		String strParam = null;
		strParam = (String) request.getAttribute("dataOraProt");

		String docInOut = null;
		docInOut = (String) request.getAttribute("docInOut");

		Object o = report.newInstance();

		inizializzaMethod.invoke(o, new Object[] { infoGen, appuntaments, operatore, azioniConcordats, installAppPath,
				tipoFile, ambito, strParam, docInOut, regione, config });

		return (Engine) getEngineMethod.invoke(o, new Object[] {});
	}

	public SourceBean doSelect(String stmName, Object params[]) throws Exception {
		SourceBean result = null;
		result = (SourceBean) QueryExecutor.executeQuery(stmName, params, "SELECT", Values.DB_SIL_DATI);
		return result;
	}

}
