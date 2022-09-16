package it.eng.sil.module.patto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.action.report.UtilsConfig;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.module.movimenti.constant.Properties;
import it.eng.sil.module.patto.bean.PacchettoAdulti;
import it.eng.sil.util.amministrazione.impatti.PattoBean;

/**
 * Gestisce l' inserimento nella tabella am_lav_patto_scelta di piu' record associati a tabelle diverse. Le chiavi
 * esterne sono individuate dal parametro PRG(<codice tabella cod_lst_tab>), per cui bisognera' estrarre il codice per
 * capire di quale tabella si tratta, inserire nella request i dati cosi' ricavati e fare tante insert quante sono le
 * chiavi passate.
 * 
 */
public class InsertAmLavPattoScelta extends AbstractSimpleModule {
	/**
	 * Variabili che rappresentano i cod_lst_tab delle tabelle che possono essere associate al patto. (ogni variabile
	 * deve essere aggiunta all' array tabelleCollegate)
	 */
	private final static String AM_IND_T = "AM_IND_T";
	private final static String AM_CM_IS = "AM_CM_IS";
	private final static String PR_STU = "PR_STU";
	private final static String PR_ESP_L = "PR_ESP_L";
	private final static String PR_COR = "PR_COR";
	private final static String PR_MAN = "PR_MAN";
	private final static String AM_MB_IS = "AM_MB_IS";
	private final static String AM_OBBFO = "AM_OBBFO";
	private final static String AM_EX_PS = "AM_EX_PS";
	private final static String PR_IND = "PR_IND";
	private final static String DE_IMPE = "DE_IMPE";
	private final static String OR_PER = "OR_PER";
	private final static String AG_LAV = "AG_LAV";

	private List impegniAggiunti = new ArrayList();
	/**
	 * E' la tabella dei cod_Lst_tab delle tabelle collegate.
	 */
	private final static String[] tabelleCollegate = { AM_IND_T, AM_CM_IS, PR_STU, PR_ESP_L, PR_COR, PR_MAN, AM_MB_IS,
			AM_OBBFO, AM_EX_PS, PR_IND, DE_IMPE, OR_PER, AG_LAV };
	private static final String[] strChiaveTabellaName = { "PRG_TAB", "STRCHIAVETABELLA2", "STRCHIAVETABELLA3" };

	private int codiceErrore = -1;
	private String dettaglioErrore = "";

	/**
	 * 
	 */
	public void service(SourceBean request, SourceBean response) throws Exception {
		SourceBean sb = null;
		String codTipoPatto = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		TransactionQueryExecutor transExec = null;

		disableMessageIdFail();
		disableMessageIdSuccess();

		if (!request.containsAttribute("PRG_PATTO_LAVORATORE")) {
			setSectionQuerySelect("QUERY_SELECT");
			sb = doSelect(request, response);
			BigDecimal prgPatto = (BigDecimal) sb.getAttribute("ROW.PRGPATTOLAVORATORE");
			codTipoPatto = sb.containsAttribute("ROW.codtipopatto") ? sb.getAttribute("ROW.codtipopatto").toString()
					: "";
			request.setAttribute("PRG_PATTO_LAVORATORE", prgPatto.toString());
		} else {
			setSectionQuerySelect("QUERY_SELECT");
			sb = doSelect(request, response);
			codTipoPatto = sb.containsAttribute("ROW.codtipopatto") ? sb.getAttribute("ROW.codtipopatto").toString()
					: "";
		}

		setSectionQueryInsert("QUERY_INSERT");

		String[] tables = getCodLstTab(request);

		try {
			for (int j = 0; j < tables.length; j++) {
				transExec = new TransactionQueryExecutor(getPool(), this);
				enableTransactions(transExec);
				transExec.initTransaction();
				List pks = getPKeys(tables[j], request);
				insertPatto(pks, tables[j], request, response, transExec, codTipoPatto);
				if (codiceErrore < 0) {
					// In questo caso non ci sono anomalie che hanno bloccato l'operazione
					transExec.commitTransaction();
				} else {
					throw new Exception("Errore Legame col patto/accordo");
				}
			}
			reportOperation.reportSuccess(MessageCodes.Patto.LEGAME_RIUSCITO);
		} catch (Exception e) {
			try {
				if (transExec != null) {
					transExec.rollBackTransaction();
				}
			} catch (EMFInternalError ie) {
				reportOperation.reportFailure(MessageCodes.Patto.ERR_LEGAME, e, "execute()",
						"fallita associazione al patto");
			}
			if (codiceErrore < 0) {
				reportOperation.reportFailure(MessageCodes.Patto.ERR_LEGAME, e, "execute()",
						"fallita associazione al patto");
			} else {
				reportOperation.reportFailure(codiceErrore, true, dettaglioErrore);
			}
		}
	}

	/**
	 * Restituisce le pk (costituita da un solo campo) dei records da associare al patto
	 */
	private List getPKeys(String table, SourceBean request) {
		Object o = request.getAttribute("PRG_" + table);
		List pkeys = null;

		if (o instanceof String) {
			pkeys = new ArrayList();
			pkeys.add(o);
		} else {
			pkeys = (List) o;
		}

		return pkeys;
	}

	private void insertPatto(List pkeys, String codLstTab, SourceBean request, SourceBean response,
			TransactionQueryExecutor transExec, String codTipoPatto) throws Exception {
		ArrayList dateAzioni = new ArrayList(0);
		boolean aggiornaDataScadenzaPatto = request.containsAttribute("AGGIORNADATASCADPATTO");

		for (int i = 0; i < pkeys.size(); i++) {

			setParameters((String) pkeys.get(i), request);

			String dataStimataAzione = "";
			String flgMisurayei = "";
			String flgAdesioneGG = "";
			String codMonoPacchetto = "";

			if (codLstTab.equals(OR_PER)) {
				// il controllo scatta solo per il patto e legame con azione
				String prgPercorso = request.containsAttribute("PRG_TAB") ? request.getAttribute("PRG_TAB").toString()
						: "";
				String prgPattoLavoratore = request.containsAttribute("PRG_PATTO_LAVORATORE")
						? request.getAttribute("PRG_PATTO_LAVORATORE").toString()
						: "";
				BigDecimal prgPattoLav = null;
				Vector<String> programmi = null;
				if (!prgPattoLavoratore.equals("")) {
					prgPattoLav = new BigDecimal(prgPattoLavoratore);
					programmi = PattoBean.checkProgrammi(prgPattoLav, transExec);
				}
				if (prgPercorso != null && !prgPercorso.equals("")) {
					BigDecimal prgPercorsoKey = new BigDecimal(prgPercorso);
					SourceBean azione = getDataAzione(prgPercorsoKey, transExec);
					if (azione != null) {
						dataStimataAzione = azione.containsAttribute("ROW.DATSTIMATA")
								? azione.getAttribute("ROW.DATSTIMATA").toString()
								: "";
						flgMisurayei = azione.containsAttribute("ROW.flg_misurayei")
								? azione.getAttribute("ROW.flg_misurayei").toString()
								: "";
						flgAdesioneGG = azione.containsAttribute("ROW.flgAdesioneGG")
								? azione.getAttribute("ROW.flgAdesioneGG").toString()
								: "";
						codMonoPacchetto = azione.containsAttribute("ROW.CODMONOPACCHETTO")
								? azione.getAttribute("ROW.CODMONOPACCHETTO").toString()
								: "";
						String azioneRagg = azione.containsAttribute("ROW.descrizioneAzRagg")
								? azione.getAttribute("ROW.descrizioneAzRagg").toString()
								: "";
						String descAzione = azione.containsAttribute("ROW.descrizioneAz")
								? azione.getAttribute("ROW.descrizioneAz").toString()
								: "";
						String flgFormazioneAzione = azione.containsAttribute("ROW.flgFormazione")
								? azione.getAttribute("ROW.flgFormazione").toString()
								: "";
						String codazionesifer = azione.containsAttribute("ROW.codazionesifer")
								? azione.getAttribute("ROW.codazionesifer").toString()
								: "";

						if ((flgMisurayei.equalsIgnoreCase("S") || flgAdesioneGG.equalsIgnoreCase("S"))
								&& (azione.getAttribute("ROW.PRGPATTODISASSOCIATOFORMAZIONE") != null)) {
							BigDecimal prgPattoDiAssociato = (BigDecimal) azione
									.getAttribute("ROW.PRGPATTODISASSOCIATOFORMAZIONE");
							if (prgPattoLav != null && prgPattoDiAssociato.compareTo(prgPattoLav) != 0) {
								codiceErrore = MessageCodes.Patto.ERR_AZIONE_PRECEDENTEMENTE_ASSOCIATA_PATTO;
								if (dettaglioErrore == null || dettaglioErrore.equals("")) {
									dettaglioErrore = "Azione " + azioneRagg + " - " + descAzione + " - Data stimata "
											+ dataStimataAzione;
								} else {
									dettaglioErrore = dettaglioErrore + "<br>" + "Azione " + azioneRagg + " - "
											+ descAzione + " - Data stimata " + dataStimataAzione;
								}
							}
						}

						if (codiceErrore < 0) {
							if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_GARANZIA_GIOVANI)
									|| PattoBean.checkMisuraProgramma(programmi,
											PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA)) {
								UtilsConfig utility = new UtilsConfig(PacchettoAdulti.CONFIG_PA);
								String config = utility.getConfigurazioneDefault_Custom();
								if (config.equals(Properties.CUSTOM_CONFIG)) {
									codiceErrore = PattoBean.checkPacchettoAdultiGaranziaGiovani(codMonoPacchetto,
											flgFormazioneAzione, codazionesifer);
									if (codiceErrore > 0) {
										if (dettaglioErrore == null || dettaglioErrore.equals("")) {
											dettaglioErrore = "Azione " + azioneRagg + " - " + descAzione
													+ " - Data stimata " + dataStimataAzione;
										} else {
											dettaglioErrore = dettaglioErrore + "<br>" + "Azione " + azioneRagg + " - "
													+ descAzione + " - Data stimata " + dataStimataAzione;
										}
									}
								}
								if (PattoBean.checkMisuraProgramma(programmi,
										PattoBean.DB_MISURE_GARANZIA_GIOVANI_UMBRIA)) {
									if (codiceErrore < 0) {
										codiceErrore = PattoBean.checkazioniPattoGGU(flgMisurayei);
										if (codiceErrore > 0) {
											if (dettaglioErrore == null || dettaglioErrore.equals("")) {
												dettaglioErrore = "Azione " + azioneRagg + " - " + descAzione
														+ " - Data stimata " + dataStimataAzione;
											} else {
												dettaglioErrore = dettaglioErrore + "<br>" + "Azione " + azioneRagg
														+ " - " + descAzione + " - Data stimata " + dataStimataAzione;
											}
										}
									}
								} else {
									// patto con misura MGG
									if (codiceErrore < 0) {
										codiceErrore = PattoBean.checkazioniPattoGG(codMonoPacchetto);
										if (codiceErrore > 0) {
											if (dettaglioErrore == null || dettaglioErrore.equals("")) {
												dettaglioErrore = "Azione " + azioneRagg + " - " + descAzione
														+ " - Data stimata " + dataStimataAzione;
											} else {
												dettaglioErrore = dettaglioErrore + "<br>" + "Azione " + azioneRagg
														+ " - " + descAzione + " - Data stimata " + dataStimataAzione;
											}
										}
									}
								}
							}
						}

						if (codiceErrore < 0) {
							if (PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_OVER_30)
									|| PattoBean.checkMisuraProgramma(programmi, PattoBean.DB_MISURE_OVER_45)
									|| PattoBean.checkMisuraProgramma(programmi,
											PattoBean.DB_MISURE_INCLUSIONE_ATTIVA)) {
								codiceErrore = PattoBean.checkPacchettoAdultiOver3045INA(codMonoPacchetto,
										flgMisurayei);
								if (codiceErrore > 0) {
									if (dettaglioErrore == null || dettaglioErrore.equals("")) {
										dettaglioErrore = "Azione " + azioneRagg + " - " + descAzione
												+ " - Data stimata " + dataStimataAzione;
									} else {
										dettaglioErrore = dettaglioErrore + "<br>" + "Azione " + azioneRagg + " - "
												+ descAzione + " - Data stimata " + dataStimataAzione;
									}
								}
								/*
								 * if (codiceErrore < 0) { if ( (flgFormazioneAzione.equalsIgnoreCase(Values.FLAG_TRUE))
								 * && (codazionesifer.equalsIgnoreCase(PattoBean.AZ_SIFER_C06) ||
								 * codazionesifer.equalsIgnoreCase(PattoBean.AZ_SIFER_C07)) ) { codiceErrore =
								 * PattoBean.checkLimiteAzioniPacchettoAdulti(programmi, codMonoPacchetto,
								 * codazionesifer); if (codiceErrore > 0) { if (dettaglioErrore == null ||
								 * dettaglioErrore.equals("")) { dettaglioErrore = "Azione " + azioneRagg + " - " +
								 * descAzione + " - Data stimata " + dataStimataAzione; } else { dettaglioErrore =
								 * dettaglioErrore + "<br>" + "Azione " + azioneRagg + " - " + descAzione +
								 * " - Data stimata " + dataStimataAzione; } } } }
								 */
							}
						}
					}
				}
			}

			if (codiceErrore < 0) {

				Object params[] = new Object[5];
				params[0] = request.getAttribute("PRG_PATTO_LAVORATORE");
				params[1] = codLstTab;
				params[2] = request.getAttribute("PRG_TAB");
				params[3] = request.getAttribute("STRCHIAVETABELLA2");
				params[4] = request.getAttribute("STRCHIAVETABELLA3");

				Boolean res = (Boolean) transExec.executeQuery("INS_LAV_PATTO_SCELTA_COMPLETO", params, "INSERT");
				if (!res.booleanValue()) {
					throw new Exception("inserimento fallito");
				}
				if (codLstTab.equals(OR_PER) || codLstTab.equals(AG_LAV)) {
					insertImpegniAssociati((String) pkeys.get(i), codLstTab, request, response, transExec);
				}

				if (aggiornaDataScadenzaPatto && codLstTab.equals(OR_PER)) {
					dateAzioni.ensureCapacity(dateAzioni.size() + 1);
					dateAzioni.add(i, dataStimataAzione);
				}
			}
		}

		if (codiceErrore < 0) {
			if (aggiornaDataScadenzaPatto && codLstTab.equals(OR_PER)) {
				aggiornaDataScadenzaPatto(dateAzioni, request, transExec);
			}
		}
	}

	/**
	 * Metodo per l'inserimento in am_lav_patto_scelta degli impegni associati all'azione ed al servizio. Gli impegni
	 * associati vengono letti dalla DE_AZIONE_IMPEGNO e DE_SERVIZIO_IMPEGNO
	 */
	public void insertImpegniAssociati(String cod, String codLstTab, SourceBean request, SourceBean response,
			TransactionQueryExecutor transExec) throws Exception {
		Set impegni = null;
		String associazione = "";
		String codiceImpegno = "";
		if (codLstTab.equals(OR_PER))
			associazione = "AZIONE";
		else if (codLstTab.equals(AG_LAV))
			associazione = "SERVIZIO";
		impegni = getImpegni(cod, associazione, request, response);
		if (!impegni.isEmpty()) {
			Iterator impegniDaInserire = impegni.iterator();
			while (impegniDaInserire.hasNext()) {
				codiceImpegno = (String) impegniDaInserire.next();
				setParametriImp(request, codiceImpegno);
				// boolean res = doInsert(request, response);
				Object params[] = new Object[5];
				params[0] = request.getAttribute("PRG_PATTO_LAVORATORE");
				params[1] = request.getAttribute("COD_LST_TAB");
				params[2] = request.getAttribute("PRG_TAB");
				params[3] = request.getAttribute("STRCHIAVETABELLA2");
				params[4] = request.getAttribute("STRCHIAVETABELLA3");

				Boolean res = (Boolean) transExec.executeQuery("INS_LAV_PATTO_SCELTA_COMPLETO", params, "INSERT");
				if (!res.booleanValue()) {
					throw new Exception("Inserimento impegno associato fallito");
				}
			} // for(int j=0;j<impegni.size();j++){
		} // if(!impegni.isEmpty()){
	}

	private void setParametriImp(SourceBean request, String impegno) throws Exception {
		if (request.getAttribute("COD_LST_TAB") != null)
			request.updAttribute("COD_LST_TAB", DE_IMPE);
		else
			request.setAttribute("COD_LST_TAB", DE_IMPE);

		if (request.getAttribute("PRG_TAB") != null)
			request.updAttribute("PRG_TAB", impegno);
		else
			request.setAttribute("PRG_TAB", impegno);

		if (request.getAttribute("STRCHIAVETABELLA2") != null)
			request.updAttribute("STRCHIAVETABELLA2", "");
		else
			request.setAttribute("STRCHIAVETABELLA2", "");

		if (request.getAttribute("STRCHIAVETABELLA3") != null)
			request.updAttribute("STRCHIAVETABELLA3", "");
		else
			request.setAttribute("STRCHIAVETABELLA3", "");
	}

	/**
	 * Recupera i servizi associati all'azione o servizio
	 */
	private Set getImpegni(String prg, String associazione, SourceBean request, SourceBean response) throws Exception {
		Vector rows = null;
		Set codici = null;
		String[] strChiaveTabella = getStrChiaveTabella(prg);
		if (associazione.equals("AZIONE"))
			this.setSectionQuerySelect("QUERY_SELECT_IMPEGNI_CORR_AZIONI");
		else if (associazione.equals("SERVIZIO"))
			this.setSectionQuerySelect("QUERY_SELECT_IMPEGNI_CORR_SERVIZI");

		/*
		 * PROGRESSIVO=PRGPERCORSO PER LE AZIONI; PROGRESSIVO=PRGAPPUNTAMENTO PER I SERVIZI
		 */
		if (request.getAttribute("PROGRESSIVO") != null) {
			if (associazione.equals("AZIONE"))
				request.updAttribute("PROGRESSIVO", strChiaveTabella[0]);
		} else {
			if (associazione.equals("AZIONE"))
				request.setAttribute("PROGRESSIVO", strChiaveTabella[0]);
		}
		if (associazione.equals("SERVIZIO")) {
			if (request.getAttribute("CDNLAVORATORE") != null)
				request.updAttribute("CDNLAVORATORE", strChiaveTabella[0]);
			else
				request.setAttribute("CDNLAVORATORE", strChiaveTabella[0]);

			if (request.getAttribute("CODCPI") != null)
				request.updAttribute("CODCPI", strChiaveTabella[1]);
			else
				request.setAttribute("CODCPI", strChiaveTabella[1]);

			if (request.getAttribute("PROGRESSIVO") != null)
				request.updAttribute("PROGRESSIVO", strChiaveTabella[2]);
			else
				request.setAttribute("PROGRESSIVO", strChiaveTabella[2]);
		}

		SourceBean sb = (SourceBean) doSelect(request, response);
		rows = sb.getAttributeAsVector("ROW");
		codici = new HashSet(rows.size());
		SourceBean s = null;
		int k = 0;
		for (int i = 0; i < rows.size(); i++) {
			s = (SourceBean) rows.get(i);
			String cod = s.getAttribute("CODIMPEGNO").toString();
			if (!impegnoPresente(cod, request, response)) {
				codici.add(cod);
			}
		}
		// System.out.println(codici);
		codici.removeAll(impegniAggiunti);
		// System.out.println(codici);
		impegniAggiunti.addAll(codici);
		// System.out.println(impegniAggiunti);
		return codici;
	}

	/**
	 * Controlla se l'impegno in questione è già stato inserito in am_lav_patto_scelta
	 */
	private boolean impegnoPresente(String codImpegno, SourceBean request, SourceBean response) throws Exception {
		boolean impegnoPresente = false;
		String prgPattoLav = request.getAttribute("prg_patto_lavoratore").toString();

		this.setSectionQuerySelect("QUERY_SELECT_IMPEGNO_PATTO");
		if (request.getAttribute("CODIMPEGNOPATTO") != null) {
			request.updAttribute("CODIMPEGNOPATTO", codImpegno);
		} else {
			request.setAttribute("CODIMPEGNOPATTO", codImpegno);
		}
		if (request.getAttribute("PRGPATTOLAVORATOREIMP") != null) {
			request.updAttribute("PRGPATTOLAVORATOREIMP", prgPattoLav);
		} else {
			request.setAttribute("PRGPATTOLAVORATOREIMP", prgPattoLav);
		}
		SourceBean sb = (SourceBean) doSelect(request, response);
		Vector rows = sb.getAttributeAsVector("ROW");
		if ((rows != null) && !rows.isEmpty())
			impegnoPresente = true;

		return impegnoPresente;
	}

	/**
	 * Restituisce i cod_lst_tab delle tabelle da associare
	 */
	private String[] getCodLstTab(SourceBean request) {
		ArrayList tables = new ArrayList();

		for (int i = 0; i < tabelleCollegate.length; i++) {
			Object keys = request.getAttribute("PRG_" + tabelleCollegate[i]);

			if (keys != null) {
				tables.add(tabelleCollegate[i]);
			}
		}

		return (String[]) tables.toArray(new String[0]);
	}

	/**
	 * le chiavi possono essere nel formato: strchiavetabella,strchiavetabella2, strchiavetabella3 [nel caso in cui la
	 * chiave stessa contenga una virgola viene inserito come carattere di escape una ulteriore virgola per cui bisogna
	 * considerare questa situazione quando si va a splittare la stringa passata alla request]
	 */
	private String[] getStrChiaveTabella(String keys) throws Exception {
		/*
		 * ci pensero' in seguito per ora consideto che le chiavi alfanumeriche non abbiano il carattere ',' if
		 * (keys.indexOf(",,")>=0) { }
		 */
		StringTokenizer st = new StringTokenizer(keys, ",");
		int nKey = st.countTokens();
		String[] strChiaveTabella = new String[nKey];

		for (int i = 0; st.hasMoreElements(); i++)
			strChiaveTabella[i] = st.nextToken();

		return strChiaveTabella;
	}

	/**
	 * Imposta i parametri, le chiavi da legare, nella request
	 */
	private void setParameters(String keys, SourceBean request) throws Exception {
		String[] strChiaveTabella = getStrChiaveTabella(keys);

		for (int i = 0; i < strChiaveTabella.length; i++) {
			request.delAttribute(strChiaveTabellaName[i]);
			request.setAttribute(strChiaveTabellaName[i], strChiaveTabella[i]);
		}
	}

	private SourceBean getDataAzione(BigDecimal prgPercorso, TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[1];
		params[0] = prgPercorso;
		SourceBean sb = (SourceBean) transExec.executeQuery("GET_DATI_AZIONE_COLLEGATO_PATTO", params, "SELECT");
		if (sb != null) {
			return sb;
		}
		return null;
	}

	private void aggiornaDataScadenzaPatto(ArrayList dateAzioni, SourceBean request, TransactionQueryExecutor transExec)
			throws Exception {
		int maxIndex = 0;
		String max = "0";
		int i = 0;

		String d = (String) dateAzioni.get(i);
		max = d.substring(6, 10) + d.substring(3, 5) + d.substring(0, 2);
		i = i + 1;
		for (; i < dateAzioni.size(); i++) {
			d = (String) dateAzioni.get(i);
			String tmp = d.substring(6, 10) + d.substring(3, 5) + d.substring(0, 2);
			if (Integer.parseInt(tmp) > Integer.parseInt(max))
				maxIndex = i;
		}

		Object[] params = new Object[1];
		String numKloPattoLav = "";
		params[0] = request.getAttribute("PRG_PATTO_LAVORATORE");
		SourceBean sbTmp = (SourceBean) transExec.executeQuery("SELECT_NUMKLO_PATTO_LAV", params, "SELECT");
		if ((sbTmp != null) && !sbTmp.getAttribute("ROW.NUMKLOPATTOLAVORATORE").equals(""))
			numKloPattoLav = sbTmp.getAttribute("ROW.NUMKLOPATTOLAVORATORE").toString();

		params = null;
		params = new Object[3];
		params[0] = dateAzioni.get(maxIndex);
		params[1] = (new Integer(Integer.parseInt(numKloPattoLav) + 1));
		params[2] = request.getAttribute("PRG_PATTO_LAVORATORE");
		Boolean esito = (Boolean) transExec.executeQuery("UPDATE_DATA_SCAD_PATTO", params, "UPDATE");
		if (!esito.booleanValue())
			throw new Exception("Impossibile aggiornare la data di scadenza del patto.");
	}// end aggiornaDataScadenzaPatto()

}
