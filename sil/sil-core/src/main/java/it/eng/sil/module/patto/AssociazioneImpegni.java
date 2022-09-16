/*
 * Creato il 21-set-04
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.patto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.TransactionQueryExecutor;

/**
 * @author De Simone
 * 
 *         Classe per l'associazione automatica degli impegni per le azioni w gli appuntamenti
 */
public class AssociazioneImpegni {
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

	private TransactionQueryExecutor transExec = null;
	private SourceBean request = null;
	private String codLstTab = "";
	private String prgPattoLav = "";

	/**
	 * Costruttore
	 * 
	 * @param prgPatto,
	 *            PrgPattoLavoratore
	 * @param cod,
	 *            codLstTab
	 * @param req,
	 *            request
	 * @param trEx,
	 *            TransactionQueryExecutor
	 */
	public AssociazioneImpegni(String prgPatto, String cod, SourceBean req, TransactionQueryExecutor trEx) {
		transExec = trEx;
		request = req;
		codLstTab = cod;
		prgPattoLav = prgPatto;
	}

	/**
	 * Metodo per l'inserimento degli impegni associati
	 */
	public boolean insertImpegni() throws Exception {

		SourceBean sb = null;
		boolean areInserted = false;

		try {
			List pks = getPKeys(codLstTab, request);
			try {
				insertImpegniAssociati(pks.get(0).toString(), codLstTab, transExec);
			} catch (Exception ex) {
				throw new Exception(ex.getMessage());
			}
			areInserted = true;
		} catch (Exception e) {
			areInserted = false;
		}
		return areInserted;
	}// end insertImpegni

	/**
	 * Metodo per l'inserimento in am_lav_patto_scelta degli impegni associati all'azione ed al servizio. Gli impegni
	 * associati vengono letti dalla DE_AZIONE_IMPEGNO e DE_SERVIZIO_IMPEGNO
	 */
	public void insertImpegniAssociati(String cod, String codLstTab, TransactionQueryExecutor transExec)
			throws Exception {
		ArrayList impegni = null;
		String associazione = "";
		String codiceImpegno = "";
		if (codLstTab.equals(OR_PER))
			associazione = "AZIONE";
		else if (codLstTab.equals(AG_LAV))
			associazione = "SERVIZIO";
		impegni = getImpegni(cod, associazione, transExec);
		if (!impegni.isEmpty()) {
			for (int j = 0; j < impegni.size(); j++) {
				codiceImpegno = (String) impegni.get(j);
				Object params[] = new Object[5];
				params[0] = prgPattoLav; // PRG_PATTO_LAVORATORE;
				params[1] = DE_IMPE;
				params[2] = codiceImpegno;
				params[3] = "";// STRCHIAVETABELLA2
				params[4] = "";// STRCHIAVETABELLA3

				Boolean res = (Boolean) transExec.executeQuery("INS_LAV_PATTO_SCELTA_COMPLETO", params, "INSERT");
				if (!res.booleanValue()) {
					throw new Exception("Inserimento impegno associato fallito");
				}
			} // for(int j=0;j<impegni.size();j++){
		} // if(!impegni.isEmpty()){
	}

	/**
	 * Recupera i servizi associati all'azione o servizio
	 */
	private ArrayList getImpegni(String prg, String associazione, TransactionQueryExecutor transExec) throws Exception {
		Vector rows = null;
		ArrayList codici = null;
		String querySel = "";
		String[] strChiaveTabella = getStrChiaveTabella(prg);
		if (associazione.equals("AZIONE"))
			querySel = "GET_IMPEGNI_AZIONI_COL";
		else if (associazione.equals("SERVIZIO"))
			querySel = "GET_IMPEGNI_SERVIZI_COL";

		Object params[] = null;

		/*
		 * PROGRESSIVO=PRGPERCORSO PER LE AZIONI; PROGRESSIVO=PRGAPPUNTAMENTO PER I SERVIZI
		 */
		if (associazione.equals("AZIONE")) {
			params = new Object[1];
			params[0] = strChiaveTabella[0];
		}
		if (associazione.equals("SERVIZIO")) {
			params = new Object[3];
			params[0] = strChiaveTabella[0];// cdnlavoratore
			params[1] = strChiaveTabella[1];// codcpi
			params[2] = strChiaveTabella[2];// progressivo
		}

		SourceBean sb = (SourceBean) transExec.executeQuery(querySel, params, "SELECT");
		if (sb == null) {
			throw new Exception("selezione fallita");
		}
		rows = sb.getAttributeAsVector("ROW");
		codici = new ArrayList(rows.size());
		SourceBean s = null;
		int k = 0;
		for (int i = 0; i < rows.size(); i++) {
			s = (SourceBean) rows.get(i);
			String cod = s.getAttribute("CODIMPEGNO").toString();
			if (!impegnoPresente(cod)) {
				codici.add(k++, cod);
			}
		}
		System.out.println(codici);
		codici.removeAll(impegniAggiunti);
		System.out.println(codici);
		impegniAggiunti.addAll(codici);
		System.out.println(impegniAggiunti);
		return codici;
	}

	/**
	 * Controlla se l'impegno in questione è già stato inserito in am_lav_patto_scelta
	 */
	private boolean impegnoPresente(String codImpegno) throws Exception {
		boolean impegnoPresente = false;
		String querySel = "GET_IMPEGNO_PATTO";
		Object params[] = new Object[2];

		params[0] = prgPattoLav;
		params[1] = codImpegno;

		SourceBean sb = (SourceBean) transExec.executeQuery(querySel, params, "SELECT");
		Vector rows = sb.getAttributeAsVector("ROW");
		if ((rows != null) && !rows.isEmpty())
			impegnoPresente = true;

		return impegnoPresente;
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
	 * Restituisce le pk (costituita da un solo campo) dei records da associare al patto
	 */
	private List getPKeys(String table, SourceBean request) {
		Object o = request.getAttribute("PRG_" + table);
		List pkeys = null;

		if (o instanceof String) {
			pkeys = new ArrayList();
			pkeys.add(o);
		} else if (o instanceof BigDecimal) {
			pkeys = new ArrayList();
			pkeys.add(o);
		} else {
			pkeys = (List) o;
		}

		return pkeys;
	}

	/**
	 * Cancellazioni eventuali impegni associati e memorizzati precedentemente
	 * 
	 * @param prg,
	 *            prgLavPattoScelta
	 */

	public void cancellaImpegniAssociati(String prg) throws Exception {
		String querySel = "";
		SourceBean tmp = null;
		SourceBean impegniMemo = null;
		Vector impegni = null;
		String prgPattoLavoratore = "";
		boolean canDelete = false;
		boolean nonCancellareImp = false;
		String prgLavPattoSceltaImp = "";

		// Recupero le info sull'associazione da cancellare
		Object params[] = new Object[1];
		/*
		 * params[0] = prg; SourceBean amLav = (SourceBean)transExec.executeQuery("GET_INFO_LAV_PATTO_SCELTA", params,
		 * "SELECT"); if(amLav==null) throw new Exception("Errore nella cancellazione delle associazioni.");
		 * 
		 * codLstTab = (String)amLav.getAttribute("ROW.CODLSTTAB");
		 */
		if (codLstTab.equals("OR_PER")) {
			// Azioni
			querySel = "GET_IMPEGNI_LEGATI_AZIONE";
			// Il prglavpattoscelta è già presente nella request
		} else {
			if (codLstTab.equals("AG_LAV")) {
				// Servizi
				querySel = "GET_IMPEGNI_LEGATI_SERVIZIO";
			} else
				nonCancellareImp = true;
		}
		if (!nonCancellareImp) {
			params[0] = prg;
			tmp = (SourceBean) transExec.executeQuery(querySel, params, "SELECT");
			Vector rows = tmp.getAttributeAsVector("ROW");
			if ((rows != null) && !rows.isEmpty()) {
				for (int i = 0; i < rows.size(); i++) {
					SourceBean sb = (SourceBean) rows.get(i);
					String imp = sb.getAttribute("CODIMPEGNO").toString();
					prgPattoLavoratore = sb.getAttribute("PRGPATTOLAVORATORE").toString();

					// Per ogni impegno controllo se è memorizzato in
					// am_lav_patto_scelta
					params = null;
					params = new Object[2];
					params[0] = prgPattoLavoratore;
					params[1] = imp;

					impegniMemo = (SourceBean) transExec.executeQuery("GET_IMPEGNO_PATTO", params, "SELECT");
					impegni = impegniMemo.getAttributeAsVector("ROW");

					if ((impegni != null) && !impegni.isEmpty()) {
						SourceBean s = (SourceBean) impegni.get(0);
						prgLavPattoSceltaImp = s.getAttribute("PRGLAVPATTOSCELTA").toString();

						params = null;
						params = new Object[4];
						params[0] = request.getAttribute("CDNLAVORATORE");
						params[1] = imp;
						params[2] = prgPattoLavoratore;
						params[3] = prg;

						// Se l'impegno è in am_lav_patto_scelta, verifico a chi
						// altro è collegato
						SourceBean impServ = (SourceBean) transExec.executeQuery("GET_IMPEGNI_SERVIZI", params,
								"SELECT");
						Vector vec = null;
						if (impServ != null) {
							vec = impServ.getAttributeAsVector("ROW");
						}

						SourceBean impAz = (SourceBean) transExec.executeQuery("GET_IMPEGNI_AZIONI", params, "SELECT");
						Vector vecAz = null;
						if (impAz != null) {
							vecAz = impAz.getAttributeAsVector("ROW");
						}
						// Se vi sono altri legami, allora non lo devo
						// cancellare
						if ((vecAz != null) && !vecAz.isEmpty())
							continue;
						if ((vec != null) && !vec.isEmpty())
							continue;
						canDelete = true;
					} else
						continue;

					if (canDelete) {
						Object param[] = new Object[1];
						param[0] = prgLavPattoSceltaImp;
						Boolean ris = (Boolean) transExec.executeQuery("DELETE_IMPEGNO", param, "DELETE");
						if (!ris.booleanValue()) {
							throw new Exception("Errore nella cancellazione dell'impegno associato.");
						}
					} // end if(canDelete){
				} // end for
			} // end if((rows!=null) && !rows.isEmpty()){
		} // end if(!nonCancellareImp){
	}// end cancellaImpegniAssociati

	/**
	 * Recupera la data stimata dell'azione
	 */
	private String getDataStimataAzione(String prgPercorso) throws Exception {
		String dataStimata = "";
		Object params[] = new Object[1];
		params[0] = prgPercorso;
		SourceBean sb = (SourceBean) transExec.executeQuery("GET_DATI_AZIONE", params, "SELECT");
		if ((sb != null) && !sb.getAttribute("ROW.DATSTIMATA").equals(""))
			dataStimata = (String) sb.getAttribute("ROW.DATSTIMATA");

		return dataStimata;
	}

	public void aggiornaDataScadenzaPatto(ArrayList dateAzioni) throws Exception {
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

	public ArrayList getDataStimata(String data) {
		ArrayList arl = new ArrayList();
		arl.add(0, data);
		return arl;
	}
}
