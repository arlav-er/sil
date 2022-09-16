package it.eng.sil.module.movimenti.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.bean.Documento;
import it.eng.sil.module.movimenti.RecordProcessor;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.DBStore;
import it.eng.sil.util.amministrazione.impatti.DidBean;
import it.eng.sil.util.amministrazione.impatti.MovimentoBean;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

public class GestioneVA18 implements RecordProcessor {
	private TransactionQueryExecutor trans;
	private String name;
	private String classname = this.getClass().getName();
	private SourceBean infoGenerali;
	private RequestContainer requestContainer = null;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GestioneVA18.class.getName());

	public GestioneVA18(String name, TransactionQueryExecutor transexec, SourceBean _infoGenerali,
			RequestContainer _request) {
		this.name = name;
		this.trans = transexec;
		this.infoGenerali = _infoGenerali;
		this.requestContainer = _request;
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

		StatoOccupazionaleBean statoOccupazionaleAperto = null;
		SourceBean cessazioneVA18 = null;
		try {
			cessazioneVA18 = (SourceBean) record.get("CESANNULLATAVA18");
			if (cessazioneVA18 != null) {
				cessazioneVA18 = cessazioneVA18.containsAttribute("ROW")
						? (SourceBean) cessazioneVA18.getAttribute("ROW")
						: cessazioneVA18;
				Object cdnLavoratore = record.get("CDNLAVORATORE");
				Vector statiOccupazionaliFinali = DBLoad.getStatiOccupazionali(cdnLavoratore, trans);
				if (statiOccupazionaliFinali.size() > 0) {
					SourceBean sbOccAperto = (SourceBean) statiOccupazionaliFinali
							.get(statiOccupazionaliFinali.size() - 1);
					statoOccupazionaleAperto = new StatoOccupazionaleBean(sbOccAperto);
					int numGiorniDidVA18 = 0;
					if (infoGenerali.containsAttribute("NUMGGDID")) {
						numGiorniDidVA18 = Integer.parseInt(infoGenerali.getAttribute("NUMGGDID").toString());
					}
					if (statoOccupazionaleAperto != null && statoOccupazionaleAperto.getStatoOccupaz().equals("C0")
							&& numGiorniDidVA18 > 0) {
						// lo stato occupazionale del lavoratore è Altro : cessato non rientrato (C0), esiste una
						// cessazione dichiarata o documentata
						// dal lavoratore annullata per lo stesso rapporto di lavoro con data precedente al movimento da
						// validare
						// (CESANNULLATAVA18 presente solo in contesto VALIDAZIONE cessazione e da configurazione solo
						// per Trento (gestione fatta in ControllaCessaz))
						String dataInizioSO = statoOccupazionaleAperto.getDataInizio();
						String dataInizioCesInput = (String) record.get("DATINIZIOMOV");
						if (!dataInizioSO.equals("") && !dataInizioCesInput.equals("") && DateUtils
								.compare(dataInizioSO, DateUtils.giornoSuccessivo(dataInizioCesInput)) == 0) {
							// la data inizio dell'ultima posizione occupazionale è uguale al giorno successivo al
							// movimento di cessazione
							String dataCessVA18 = cessazioneVA18.getAttribute(MovimentoBean.DB_DATA_INIZIO) != null
									? cessazioneVA18.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString()
									: "";
							Vector didsNew = DBLoad.getDichiarazioniDisponibilitaProtocollate(cdnLavoratore,
									"01/01/0001", trans);

							boolean esisteDidChiusa = false;
							BigDecimal prgDidChiusa = null;
							BigDecimal prgElencoAnag = null;
							BigDecimal numkloDid = null;
							int sizeDid = didsNew.size();
							String dataDichiarazione = "";
							String strNoteDid = "";
							if (sizeDid > 0) {
								SourceBean sbDid = (SourceBean) didsNew.get(sizeDid - 1);
								DidBean did = new DidBean(sbDid);
								dataDichiarazione = did.getDataInizio();
								String dataFineDid = did.getDataFine();
								String codMotivoFineAtto = did.getAttribute("codMotivoFineAtto") != null
										? did.getAttribute("codMotivoFineAtto").toString()
										: "";
								if (!dataCessVA18.equals("") && dataFineDid != null && !dataFineDid.equals("")
										&& codMotivoFineAtto.equalsIgnoreCase(Properties.DECADUTO_PER_AVVIAMENTO)
										&& DateUtils.compare(dataDichiarazione, dataCessVA18) > 0
										&& DateUtils.compare(dataDichiarazione, dataInizioCesInput) <= 0) {
									esisteDidChiusa = true;
									prgDidChiusa = did.getPrgDichDisponibilita();
									numkloDid = (BigDecimal) did.getAttribute("numKloDichDisp");
									prgElencoAnag = (BigDecimal) did.getAttribute("prgelencoanagrafico");
									strNoteDid = did.getAttribute("strnote") != null
											? did.getAttribute("strnote").toString()
											: "";
								}
							}
							if (esisteDidChiusa) {
								// esiste una D.I.D protocollata e chiusa con data dichiarazione successiva alla
								// cessazione annullata e precedente o uguale alla data cessazione che si sta validando
								String dataMassimaDid = DateUtils.aggiungiNumeroGiorni(dataDichiarazione,
										numGiorniDidVA18);
								if (DateUtils.compare(dataInizioCesInput, dataMassimaDid) <= 0) {
									// La D.I.D da spostare deve essere non oltre x giorni precedenti il nuovo
									// movimento di cessazione da CO da validare, dove X è un parametro in banca dati
									// (NUMGGDID).
									boolean esito = annullaDIDVA18(cdnLavoratore, prgDidChiusa, numkloDid);
									if (esito) {
										// inserimento nuova DID
										String dataDichiarazioneDID = DateUtils.giornoSuccessivo(dataInizioCesInput);
										BigDecimal prgNuovaDid = inserisciDIDVA18(cdnLavoratore, prgElencoAnag,
												prgDidChiusa, dataDichiarazioneDID, strNoteDid,
												statoOccupazionaleAperto);
										if (prgNuovaDid == null) {
											throw new Exception("Impossibile inserire la nuova DID VA18");
										} else {
											// Riassociazione e riapertura dell'eventuale patto associato alla did
											// annullata per spostamento D.I.D
											SourceBean patto = cercaPattoAssociato(prgDidChiusa, cdnLavoratore);
											if (patto != null) {
												BigDecimal prgpattolav = (BigDecimal) patto
														.getAttribute("prgpattolavoratore");
												BigDecimal numklopatto = (BigDecimal) patto
														.getAttribute("numklopattolavoratore");
												esito = riassociaApriPatto(prgpattolav, prgNuovaDid,
														dataDichiarazioneDID, numklopatto);
												if (!esito) {
													throw new Exception(
															"Impossibile riassociare il patto alla nuova DID");
												}
											}
											warnings.add(new Warning(MessageCodes.DID.NUOVA_DID_VA18_OK, ""));
										}
									} else {
										throw new Exception("Impossibile annullare la DID VA18");
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			return ProcessorsUtils.createResponse(name, classname, new Integer(MessageCodes.DID.NUOVA_DID_VA18_KO), "",
					warnings, nested);
		}

		return ProcessorsUtils.createResponse(name, classname, null, null, warnings, null);
	}

	public boolean annullaDIDVA18(Object cdnLavoratore, BigDecimal prgDid, BigDecimal numkloDid) throws Exception {
		Object cdnUser = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		Object params[] = new Object[5];
		params[0] = numkloDid;
		params[1] = cdnUser;
		params[2] = Properties.STATO_ATTO_VA18;
		params[3] = Properties.MOTIVO_FINE_ATTO_VA18;
		params[4] = prgDid;
		Boolean res = (Boolean) trans.executeQuery("annullaDid", params, "UPDATE");
		if (!res.booleanValue() || !res.booleanValue()) {
			return false;
		}

		// annullo il documento associato alla did
		String queryDocAssociato = "select DOC.prgdocumento prgdocumento " + " from am_documento DOC, "
				+ " am_documento_coll DOC_COLL " + " where DOC.CDNLAVORATORE = " + cdnLavoratore
				+ " AND DOC.prgDocumento = DOC_COLL.prgDocumento " + " AND DOC.CODTIPODOCUMENTO = 'IM' "
				+ " AND DOC_COLL.CDNCOMPONENTE = 25 AND TO_NUMBER(DOC_COLL.STRCHIAVETABELLA) = " + prgDid;
		SourceBean result = ProcessorsUtils.executeSelectQuery(queryDocAssociato, trans);
		Vector vDoc = result.getAttributeAsVector("ROW");
		if (vDoc.size() == 1) {
			SourceBean doc = (SourceBean) vDoc.get(0);
			Documento documento = new Documento((BigDecimal) doc.getAttribute("prgDocumento"));
			documento.setCodStatoAtto(Properties.STATO_ATTO_VA18);
			documento.setCodMotAnnullamentoAtto(Properties.MOTIVO_FINE_ATTO_DOC_VA18);
			documento.setNumKloDocumento(documento.getNumKloDocumento().add(new BigDecimal("1")));
			documento.setCdnUtMod((BigDecimal) cdnUser);
			documento.update(trans);
		}
		return true;
	}

	public BigDecimal inserisciDIDVA18(Object cdnLavoratore, BigDecimal prgElencoAnagrafico, BigDecimal prgDidOld,
			String dataDichiarazione, String strNote, StatoOccupazionaleBean statoOccupazionaleAperto) {
		BigDecimal prgDichDispoNew = null;
		try {
			BigDecimal cdnUser = (BigDecimal) requestContainer.getSessionContainer().getAttribute("_CDUT_");
			BigDecimal prgStatoOccupaz = statoOccupazionaleAperto.getPrgStatoOccupaz();
			UtilsConfig utility = new UtilsConfig("VA18");
			String strValoreReport = utility.getValoreConfigurazione();
			String rptStampa = "patto/" + strValoreReport;
			SourceBean res = (SourceBean) trans.executeQuery("QUERY_NEXTVAL_STATEMENT", null, "SELECT");
			if (res == null) {
				return null;
			} else {
				res = res.containsAttribute("ROW") ? (SourceBean) res.getAttribute("ROW") : res;
				prgDichDispoNew = (BigDecimal) res.getAttribute("DO_NEXTVAL");
				String codUltimoContratto = "";
				String codStatoAttoDid = Properties.STATO_ATTO_PROTOC;
				String tipoDichiarazione = Properties.TIPO_DICHIARAZIONE_DID;

				Object[] params = new Object[] { cdnLavoratore };
				SourceBean rowUltimoContratto = (SourceBean) trans.executeQuery("GET_ULTIMO_MOV", params, "SELECT");
				if (rowUltimoContratto != null) {
					rowUltimoContratto = (rowUltimoContratto.containsAttribute("ROW")
							? (SourceBean) rowUltimoContratto.getAttribute("ROW")
							: rowUltimoContratto);
					codUltimoContratto = rowUltimoContratto.getAttribute("CODCONTRATTO") != null
							? rowUltimoContratto.getAttribute("CODCONTRATTO").toString()
							: "";
				}
				it.eng.sil.coop.webservices.bean.DidBean didLav = new it.eng.sil.coop.webservices.bean.DidBean(
						prgDichDispoNew, dataDichiarazione, prgElencoAnagrafico, tipoDichiarazione, codStatoAttoDid);
				if (!codUltimoContratto.equals("")) {
					didLav.setUltimoContratto(codUltimoContratto);
				}
				boolean resDid = didLav.insertDidVA18(trans, prgDidOld, prgStatoOccupaz, strNote,
						Properties.UT_OPERATORE_IMPOSTAZIONI, cdnUser);
				if (!resDid) {
					return null;
				}
				it.eng.sil.coop.webservices.bean.LavoratoreBean lavService = new it.eng.sil.coop.webservices.bean.LavoratoreBean(
						(BigDecimal) cdnLavoratore, dataDichiarazione);

				requestContainer.getServiceRequest().updAttribute("datDichiarazione", dataDichiarazione);
				requestContainer.getServiceRequest().updAttribute("cdnLavoratore", cdnLavoratore.toString());
				requestContainer.getServiceRequest().updAttribute("intestazioneStampa",
						Properties.INTESTAZIONE_STAMPA_DID);

				DBStore.aggiornaStatoOccVA18(statoOccupazionaleAperto, requestContainer, dataDichiarazione,
						Properties.DISOCCUPATO, Properties.PROVENIENZA_STATO_OCC_DID, trans);

				didLav.aggiornaInfoDid(trans, lavService.getCdnLavoratore());

				resDid = didLav.protocollaDIDVA18(trans, lavService.getCodCpi(), lavService.getCdnLavoratore(),
						rptStampa, Properties.UT_OPERATORE_IMPOSTAZIONI, cdnUser, requestContainer.getServiceRequest());

				if (!resDid) {
					return null;
				}

				return prgDichDispoNew;
			}
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Cerca un patto stipulato a partire da una dichiarazione di immediata disponibilità
	 * 
	 * @param prgDichDisp
	 * @param cdnLav
	 * @return
	 * @throws Exception
	 */
	public SourceBean cercaPattoAssociato(BigDecimal prgDichDisp, Object cdnLav) throws Exception {
		SourceBean patto = null;
		// cerco il patto associato alla did
		String queryPattoAssociato = "select prgpattolavoratore, numklopattolavoratore " + " from am_patto_lavoratore "
				+ " where cdnlavoratore = " + cdnLav + " and prgdichdisponibilita = " + prgDichDisp
				+ " and codstatoatto in ('PP', 'PR') " + " order by datstipula desc";
		SourceBean result = ProcessorsUtils.executeSelectQuery(queryPattoAssociato, trans);
		if (result == null)
			throw new Exception("impossibile leggere i patti del lavoraotore");
		Vector patti = result.getAttributeAsVector("ROW");
		if (patti.size() > 0) {
			patto = (SourceBean) patti.get(0);
		}
		return patto;
	}

	public boolean riassociaApriPatto(BigDecimal prgpatto, BigDecimal prgDichDisp, String dataPatto,
			BigDecimal numklopatto) throws Exception {
		Object cdnUser = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		Object params[] = new Object[4];
		params[0] = prgDichDisp;
		params[1] = numklopatto.add(new BigDecimal(1));
		params[2] = cdnUser;
		params[3] = prgpatto;
		Boolean res = (Boolean) trans.executeQuery("RIASSOCIA_APRI_PATTO_VA18", params, "UPDATE");
		if (!res.booleanValue() || !res.booleanValue()) {
			return false;
		}
		return true;
	}

}