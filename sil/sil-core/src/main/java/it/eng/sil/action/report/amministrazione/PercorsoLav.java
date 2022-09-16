/*
 * Creato il Jan 27, 2005
 */
package it.eng.sil.action.report.amministrazione;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.StringUtils;
import it.eng.sil.action.report.AbstractSimpleReport;
import it.eng.sil.module.AccessoSemplificato;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;

/**
 * Questo report ha la caretteristica del passaggio di molte informazioni al report. Il motivo e' la difficolta' di
 * creare col designer una query che estraesse tutti i dati o una parte di essi. Le informazioni in questione non sono
 * multiple (Es.: nome lavoratore, data anzianita' disoccupazione etc.) Vedi il codice della query
 * "GET_INFO_GEN_LAVORATORE"
 * 
 * @author savino
 */
public class PercorsoLav extends AbstractSimpleReport {
	/*
	 * protected final static String DELETE_OK = "DELETE_OK"; protected final static String INSERT_OK = "INSERT_OK";
	 * protected final static String INSERT_FAIL = "INSERT_FAIL"; private static final String TRUE = "TRUE"; protected
	 * final static String SELECT_OK = "SELECT_OK"; protected final static String SELECT_FAIL = "SELECT_FAIL";
	 */
	public void service(SourceBean request, SourceBean response) {
		super.service(request, response);
		try {
			// String cdnProfilo = String.valueOf(user.getCdnProfilo());
			String apriFile = (String) request.getAttribute("apriFileBlob");
			if (apriFile != null && apriFile.equalsIgnoreCase("true")) {
				BigDecimal prgDoc = new BigDecimal((String) request.getAttribute("prgDocumento"));
				this.openDocument(request, response, prgDoc);
			} else {
				setStrDescrizione("PercorsoLavoratore");
				String tipoFile = (String) request.getAttribute("tipoFile");
				if (tipoFile != null)
					setStrNomeDoc("PercorsoLavoratore." + tipoFile);
				else
					setStrNomeDoc("PercorsoLavoratore.pdf");

				/*
				 * Per la regione Valle d'Aosta il report è personalizzato nell'intestazione Si è deciso di suddividere
				 * le personalizzazioni per regioni in diversi file di report ATTENZIONE: in caso di bugfix apportare la
				 * modifica in tutti i file di report.
				 */
				AccessoSemplificato _db = new AccessoSemplificato(this);
				SourceBean beanRows = null;
				_db.setSectionQuerySelect("GET_CODREGIONE");
				beanRows = _db.doSelect(request, response, false);

				String regione = (String) beanRows.getAttribute("ROW.CODREGIONE");

				// nel caso della VALLE D'AOSTA (codregione=2) è stata creata una nuova stampa
				if (regione.equals("2"))
					setReportPath("Amministrazione/PercorsoLavoratore_VDA_CC.rpt");
				else if (regione.equals("10"))
					setReportPath("Amministrazione/PercorsoLavoratore_UMB_CC.rpt");
				// Trento regione=22
				else if (regione.equals("22"))
					setReportPath("Amministrazione/PercorsoLavoratore_TRENTO_CC.rpt");
				else
					setReportPath("Amministrazione/PercorsoLavoratore_CC.rpt");

				SourceBean row = null;
				// difficile ottenere queste informazioni con una query costruita col designer
				// le estraggo ora e le passo come parametri.
				AccessoSemplificato db = new AccessoSemplificato(this);
				db.setSectionQuerySelect("GET_INFO_GEN_LAVORATORE");
				row = db.doSelect(request, response);
				if (row == null)
					throw new Exception("impossibile leggere le informazioni da passare al report");
				String nome = null, cognome = null, codiceFiscale = null, dataNascita = null, indDomicilio = null,
						comuneDomicilio = null, statoOcc = null, dataInizioStOcc = null, dataAnzianitaDisocc = null,
						dataInizioDid = null, dataDa = null, dataA = null;
				//
				nome = Utils.notNull(row.getAttribute("row.strNome"));
				cognome = Utils.notNull(row.getAttribute("row.STRCOGNOME"));
				codiceFiscale = Utils.notNull(row.getAttribute("row.STRCODICEFISCALE"));
				dataNascita = Utils.notNull(row.getAttribute("row.datNasc"));
				indDomicilio = Utils.notNull(row.getAttribute("row.STRINDIRIZZODOM"));
				comuneDomicilio = Utils.notNull(row.getAttribute("row.comDomicilio"));
				statoOcc = Utils.notNull(row.getAttribute("row.statoOccupazionale"));
				dataAnzianitaDisocc = Utils.notNull(row.getAttribute("row.datAnzianitaDisoc"));
				dataInizioStOcc = Utils.notNull(row.getAttribute("row.datInizioSocc"));
				dataInizioDid = Utils.notNull(row.getAttribute("row.datInizioDid"));
				dataDa = Utils.notNull(request.getAttribute("dataInizio"));
				dataA = Utils.notNull(request.getAttribute("dataFine"));
				// se non e' stata indicata una intestazione allora per default si usa 'percorso lavoratore'
				// non dovrebbe mai accadere dato che il default viene impostato gia' nella pagina jsp
				String intestazione = Utils.notNull(request.getAttribute("intestazione")).toUpperCase();
				if (intestazione.length() == 0)
					intestazione = "PERCORSO LAVORATORE";
				// si imposta il vettore dei codici delle info da stampare
				// Es.: ["A", "B", "M"] se nelle impostazioni sono stati selezionati i relativi checkbox
				String codMonoInfo = "";
				Map prompts = new HashMap();
				Vector infoRichieste = request.getAttributeAsVector("TIPO_INFO");
				String _info[] = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "L", "M", "N", "O", "P",
						"Q", "R", "S" };

				for (int i = 0; i < _info.length; i++) {
					String infoPercorso = _info[i];
					boolean trovatoPer = false;
					for (int j = 0; j < infoRichieste.size() && !trovatoPer; j++) {
						if (infoPercorso.equalsIgnoreCase(infoRichieste.get(j).toString())) {
							prompts.put("info" + infoPercorso, infoRichieste.get(j));
							trovatoPer = true;
						}
					}
					if (!trovatoPer) {
						prompts.put("info" + infoPercorso, null);
					}
				}

				// for (int i = 0; i < infoRichieste.size(); i++) {
				// prompts.put("info"+_info[i], infoRichieste.get(i));
				// }
				String cdnLavoratore = (String) request.getAttribute("cdnLavoratore");
				String cdnLavoratoreEncrypt = (String) request.getAttribute("cdnLavoratoreEncrypt");
				User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);

				String paramCodB = StringUtils.getAttributeStrNotNull(request, "showQualCodB");
				if (paramCodB.equals(""))
					paramCodB = "S";

				String paramCodO = StringUtils.getAttributeStrNotNull(request, "showQualCodO");
				if (paramCodO.equals(""))
					paramCodO = "S";

				// passaggio parametri all'engine del report per nome

				prompts.put("cdnLavoratore", cdnLavoratore);
				prompts.put("cdnLavoratoreEncrypt", cdnLavoratoreEncrypt);
				prompts.put("codCpi", user.getCodRif());
				prompts.put("codiceFiscale", codiceFiscale);
				prompts.put("comuneDomicilio", comuneDomicilio);
				prompts.put("dataInizioAnz", dataAnzianitaDisocc);
				prompts.put("dataInizioDid", dataInizioDid);
				prompts.put("dataInizioStOcc", dataInizioStOcc);
				prompts.put("dataNascita", dataNascita);
				prompts.put("indirizzoDomicilio", indDomicilio);
				prompts.put("nomeLavoratore", cognome + " " + nome);
				prompts.put("statoOccupaz", statoOcc);
				prompts.put("titolo", intestazione);
				prompts.put("dataDa", dataDa);
				prompts.put("dataA", dataA);
				prompts.put("showQualCodB", paramCodB);
				prompts.put("showQualCodO", paramCodO);
				prompts.put("regione", regione);

				// se e' stata richiesta la protocollazione i parametri vengono aggiunti nella map
				addPromptFieldsProtocollazione(prompts, request);
				setPromptFields(prompts);
				// it.eng.sil.bean.Documento.setCheckPromptsEnabled(false);
				/*
				 * String strParam = null; // params.add(cdnLavoratore); //1 params.add(codMonoInfo); //2 String
				 * intestazione = Utils.notNull(request.getAttribute("intestazione")).toUpperCase(); if
				 * (intestazione.length()==0) intestazione = "percorso lavoratore";
				 * params.add(intestazione.toUpperCase()); //3 params.add(cognome + " "+nome); //4
				 * params.add(codiceFiscale); //5 params.add(dataNascita); //6 params.add(indDomicilio); //7
				 * params.add(comuneDomicilio); //8 params.add(statoOcc); //9 params.add(dataInizioStOcc); //10
				 * params.add(dataAnzianitaDisocc); //11 params.add(dataInizioDid); //12
				 * 
				 * strParam = (String)request.getAttribute("annoProt"); if (strParam != null && !strParam.equals("")) {
				 * setNumAnnoProt(new BigDecimal(strParam)); params.add(strParam); } else params.add("");
				 * 
				 * BigDecimal numProt = SourceBeanUtils.getAttrBigDecimal(request, "numProt", null); if (numProt !=
				 * null) { setNumProtocollo(numProt); params.add(numProt.toString()); } else params.add("");
				 * 
				 * strParam = (String)request.getAttribute("dataOraProt"); if (strParam != null && !strParam.equals(""))
				 * { this.setDatProtocollazione(strParam); params.add(strParam); } else params.add(""); // strParam =
				 * (String)request.getAttribute("docInOut"); if (strParam != null && !strParam.equals("")) {
				 * this.setCodMonoIO(strParam); if (strParam.equalsIgnoreCase("I")) params.add("Input"); else
				 * params.add("Output"); } else params.add("");
				 * 
				 * // params.add(user.getCodRif()); setParams(params);
				 */

				/*
				 * inutile e' gia' fatto dalla superclasse String tipoDoc = (String)request.getAttribute("tipoDoc"); if
				 * (tipoDoc != null) setCodTipoDocumento(tipoDoc);
				 */
				String salva = (String) request.getAttribute("salvaDB");
				String apri = (String) request.getAttribute("apri");
				if (salva != null && salva.equalsIgnoreCase("true")) {
					insertDocument(request, response);
				} else if (apri != null && apri.equalsIgnoreCase("true"))
					showDocument(request, response);
			}
		} catch (Exception e) {
			// e' necessario per indicare al publicher del risultato se visualizzare il messaggio di errore
			// o il report
			setOperationFail(request, response, e);
		}
	}

	/*
	 * public SourceBean doSelect(SourceBean request, SourceBean response, String queryName) throws Exception { String
	 * pool = getPool(); SourceBean statement = getSelectStatement(queryName);
	 * 
	 * SourceBean beanRows = null; beanRows = (SourceBean)QueryExecutor.executeQuery( getRequestContainer(),
	 * getResponseContainer(), pool, statement, "SELECT");
	 * 
	 * //ReportOperationResult reportOperation = new ReportOperationResult(this, response); try {
	 * LogUtils.logDebug("doSelect", "bean rows [" + beanRows.toXML(false, true) + "]", this);
	 * 
	 * response.setAttribute(SELECT_OK, TRUE); } catch (Exception ex) { response.setAttribute(SELECT_FAIL, TRUE);
	 * 
	 * //reportOperation.reportFailure(MessageCodes.General.GET_ROW_FAIL, ex, "doSelect", "method failed"); }
	 * 
	 * return beanRows; } protected String getPool() { return (String)getConfig().getAttribute("POOL"); }
	 * 
	 * /**
	 *
	 */
	/*
	 * protected SourceBean getSelectStatement(String queryName) { SourceBean beanQuery = null; beanQuery =
	 * (SourceBean)getConfig().getAttribute(queryName);
	 * 
	 * return beanQuery; }
	 */
}
