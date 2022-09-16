package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.util.QueryExecutor;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.Values;

public class CmBean extends SourceBean {

	public static final String DB_FLG_AUTOMATISMI_CM = "FLGESCLUDIDAAUTCM";

	private List iscrizioni = null;

	public CmBean(SourceBean sb) throws SourceBeanException {
		super(sb);
	}

	public CmBean(Object cdnLavoratore) throws Exception {
		super("ROWS");
		this.iscrizioni = getIscrizioniCM(cdnLavoratore, null);
	}

	public CmBean(Object cdnLavoratore, TransactionQueryExecutor executor) throws Exception {
		super("ROWS");
		this.iscrizioni = getIscrizioniCM(cdnLavoratore, executor);
	}

	public SourceBean getSource() {
		try {
			return new SourceBean(this);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Restituisce tutte le iscrizioni CM protocollate del lavoratore
	 * 
	 * @param cdnLavoratore
	 * @param executor
	 * @return
	 * @throws Exception
	 */
	public static List getIscrizioniCM(Object cdnLavoratore, TransactionQueryExecutor executor) throws Exception {
		String encryptKey = System.getProperty("_ENCRYPTER_KEY_");
		if (RequestContainer.getRequestContainer() != null) {
			SessionContainer sessione = RequestContainer.getRequestContainer().getSessionContainer();
			encryptKey = (String) sessione.getAttribute("_ENCRYPTER_KEY_");
		}
		Object params[] = { encryptKey, cdnLavoratore };
		SourceBean row = null;
		if (executor != null) {
			row = (SourceBean) executor.executeQuery("GET_TUTTE_ISCRIZIONI_CM_LAV_PROTOCOLLATE", params, "SELECT");
		} else {
			row = (SourceBean) QueryExecutor.executeQuery("GET_TUTTE_ISCRIZIONI_CM_LAV_PROTOCOLLATE", params, "SELECT",
					Values.DB_SIL_DATI);
		}
		if (row == null)
			throw new Exception("impossibile leggere le iscrizioni del lavoraotore");
		return new ArrayList(row.getAttributeAsVector("ROW"));
	}

	public boolean isDisabile(String dataRif) throws Exception {
		boolean ris = false;
		if (this.iscrizioni != null) {
			for (int i = 0; i < iscrizioni.size() && !ris; i++) {
				SourceBean iscr = (SourceBean) iscrizioni.get(i);
				String dataFine = StringUtils.getAttributeStrNotNull(iscr, "DATFINE");
				String dataInizio = StringUtils.getAttributeStrNotNull(iscr, "DATINIZIO");
				String tipoCM = StringUtils.getAttributeStrNotNull(iscr, "CODMONOTIPORAGG");
				if (tipoCM.equalsIgnoreCase("D")) {
					if (DateUtils.compare(dataRif, dataInizio) >= 0
							&& (dataFine == null || dataFine.equals("") || DateUtils.compare(dataRif, dataFine) <= 0)) {
						ris = true;
					}
				}
			}
		}
		return ris;
	}

	/**
	 * Gestione riapertura o chiusura delle iscrizioni al CM a seconda del raggruppamento dell stato occupazionale
	 * aperto. Ritorna 1 se c'è stata chiusura, 2 se c'è stata riapertura
	 * 
	 * @param statiOcc
	 * @param cdnLavoratore
	 * @param iscrizioniCM
	 * @param transExec
	 * @return
	 * @throws Exception
	 */
	public static int gestisciChiusuraRiaperturaCM(Vector statiOcc, Object cdnLavoratore, List iscrizioniCM,
			TransactionQueryExecutor transExec) throws Exception {
		SourceBean sOccAperto = (SourceBean) statiOcc.get(statiOcc.size() - 1);
		String codStatoOccupazRagg = StringUtils.getAttributeStrNotNull(sOccAperto, "codStatoOccupazRagg");
		String dataInizioSOcc = StringUtils.getAttributeStrNotNull(sOccAperto, "datinizio");
		BigDecimal cdnUser = (BigDecimal) RequestContainer.getRequestContainer().getSessionContainer()
				.getAttribute("_CDUT_");
		int ret = 0;
		boolean continua = true;
		if (codStatoOccupazRagg.equalsIgnoreCase(StatoOccupazionaleBean.COD_RAGG_A)
				|| codStatoOccupazRagg.equalsIgnoreCase(StatoOccupazionaleBean.COD_RAGG_O)) {
			// gestione eventuale chiusura iscrizioni
			Vector vettCMAperte = new Vector();
			Vector vettCM = getChiusuraCM(iscrizioniCM, vettCMAperte);
			int vettCMSize = vettCM.size();
			if (vettCMSize > 0) {
				for (int i = statiOcc.size() - 2; (i >= 0 && continua); i--) {
					sOccAperto = (SourceBean) statiOcc.get(i);
					codStatoOccupazRagg = StringUtils.getAttributeStrNotNull(sOccAperto, "codStatoOccupazRagg");
					if (codStatoOccupazRagg.equalsIgnoreCase(StatoOccupazionaleBean.COD_RAGG_A)
							|| codStatoOccupazRagg.equalsIgnoreCase(StatoOccupazionaleBean.COD_RAGG_O)) {
						dataInizioSOcc = StringUtils.getAttributeStrNotNull(sOccAperto, "datinizio");
					} else {
						continua = false;
					}
				}
				for (int k = 0; k < vettCMSize; k++) {
					Object prgIscr = vettCM.get(k);
					chiudiIscrizioni(prgIscr, DateUtils.giornoPrecedente(dataInizioSOcc), transExec);
				}
				if (vettCMAperte.size() == 0) {
					// Il lavoratore non ha più un record protocollato aperto CM, e in questo caso
					// devo settare TS_CONGIF_TAB.flgAbilita='N' sul record avente cdnconfigtab=cdnlavoratore
					aggiornaConfigTab(cdnLavoratore, "N", transExec);
				}
				aggiornaSchedaIscrLavCM(cdnLavoratore.toString(), cdnUser, transExec);
				ret = 1;
			}
		} else {
			if (codStatoOccupazRagg.equalsIgnoreCase(StatoOccupazionaleBean.COD_RAGG_D)
					|| codStatoOccupazRagg.equalsIgnoreCase(StatoOccupazionaleBean.COD_RAGG_I)) {
				// gestione eventuale riapertura iscrizioni
				for (int i = statiOcc.size() - 2; (i >= 0 && continua); i--) {
					sOccAperto = (SourceBean) statiOcc.get(i);
					codStatoOccupazRagg = StringUtils.getAttributeStrNotNull(sOccAperto, "codStatoOccupazRagg");
					if (codStatoOccupazRagg.equalsIgnoreCase(StatoOccupazionaleBean.COD_RAGG_D)
							|| codStatoOccupazRagg.equalsIgnoreCase(StatoOccupazionaleBean.COD_RAGG_I)) {
						dataInizioSOcc = StringUtils.getAttributeStrNotNull(sOccAperto, "datinizio");
					} else {
						continua = false;
					}
				}
				Vector vettCM = getRiaperturaCM(iscrizioniCM, dataInizioSOcc);
				int vettCMSize = vettCM.size();
				if (vettCMSize > 0) {
					for (int k = 0; k < vettCMSize; k++) {
						Object prgIscr = vettCM.get(k);
						riapriIscrizioni(prgIscr, transExec);
					}
					// Sono state fatte delle riaperture per quel lavoratore, e quindi bisogna
					// settare TS_CONGIF_TAB.flgAbilita='S' sul record avente cdnconfigtab=cdnlavoratore
					aggiornaConfigTab(cdnLavoratore, "S", transExec);
					aggiornaSchedaIscrLavCM(cdnLavoratore.toString(), cdnUser, transExec);
					ret = 2;
				}
			}
		}
		return ret;
	}

	/**
	 * Restituisce tutte le iscrizioni che hanna data fine nulla e che devono essere chiuse
	 * 
	 * @param iscrizioniCM
	 * @return
	 */
	public static Vector getChiusuraCM(List iscrizioniCM, Vector vettIscrizioniAperte) {
		Vector ris = new Vector();
		for (int i = 0; i < iscrizioniCM.size(); i++) {
			SourceBean iscr = (SourceBean) iscrizioniCM.get(i);
			String dataFine = StringUtils.getAttributeStrNotNull(iscr, "DATFINE");
			String flagEscludi = StringUtils.getAttributeStrNotNull(iscr, "FLGESCLUDIDAAUTCM");
			Object prg = iscr.getAttribute("PRGCMISCR");
			if (dataFine.equals("")) {
				if (!flagEscludi.equalsIgnoreCase("S")) {
					ris.add(prg);
				} else {
					vettIscrizioniAperte.add(prg);
				}
			}
		}
		return ris;
	}

	/**
	 * Restituisce al massimo un'iscrizione tipo ragg = 'D' e un'iscrizione tipo ragg = 'A' con data fine valorizzata.
	 * Inoltre per ogni raggruppamento ('D' e 'A'), se esiste un'iscrizione con data fine nulla, non verrà riaperta
	 * nessuna iscrizione relativa a quel raggruppamento.
	 * 
	 * @param iscrizioniCM
	 * @return
	 */
	public static Vector getRiaperturaCM(List iscrizioniCM, String dataInizioSocc) throws Exception {
		Vector risDisabile = new Vector();
		Vector risAltraCat = new Vector();
		Vector risTotale = new Vector();
		boolean riaperturaDisabile = true;
		boolean riaperturaAltraCat = true;
		for (int i = 0; i < iscrizioniCM.size(); i++) {
			SourceBean iscr = (SourceBean) iscrizioniCM.get(i);
			String dataFine = StringUtils.getAttributeStrNotNull(iscr, "DATFINE");
			String flag = StringUtils.getAttributeStrNotNull(iscr, "FLGAUTOMATICO");
			String tipoCM = StringUtils.getAttributeStrNotNull(iscr, "CODMONOTIPORAGG");
			Object prg = iscr.getAttribute("PRGCMISCR");
			if (dataFine.equals("")) {
				if (tipoCM.equalsIgnoreCase("D")) {
					riaperturaDisabile = false;
				} else {
					riaperturaAltraCat = false;
				}
			} else {
				if (DateUtils.compare(dataInizioSocc, dataFine) <= 0 && flag.equalsIgnoreCase("S")) {
					if (tipoCM.equalsIgnoreCase("D")) {
						if (risDisabile.size() == 0) {
							risDisabile.add(prg);
						}
					} else {
						if (risAltraCat.size() == 0) {
							risAltraCat.add(prg);
						}
					}
				}
			}
		}
		// aggiungo eventuale riapertura iscrizione disabile
		if (riaperturaDisabile) {
			if (risDisabile.size() > 0) {
				risTotale.add(risDisabile.get(0));
			}
		}
		// aggiungo eventuale riapertura iscrizione altra categoria
		if (riaperturaAltraCat) {
			if (risAltraCat.size() > 0) {
				risTotale.add(risAltraCat.get(0));
			}
		}
		return risTotale;
	}

	/**
	 * Effettua la chiusura dell'iscrizione identificata da prg
	 * 
	 * @param vettCM
	 * @param dataChiusura
	 * @param transExec
	 * @throws Exception
	 */
	public static void chiudiIscrizioni(Object prg, String dataChiusura, TransactionQueryExecutor transExec)
			throws Exception {
		Object userID = RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
		Object params[] = new Object[5];
		params[0] = dataChiusura;
		params[1] = dataChiusura;
		params[2] = "CM1";
		params[3] = userID;
		params[4] = prg;
		Boolean res = (Boolean) transExec.executeQuery("UPD_CM_ISCR_CHIUSURA", params, "UPDATE");
		if (!res.booleanValue())
			throw new Exception("Impossibile effettuare la chiusura CM");
	}

	/**
	 * Effettua la riapertura dell' iscrizione identificata da prg
	 * 
	 * @param vettCM
	 * @param transExec
	 * @throws Exception
	 */
	public static void riapriIscrizioni(Object prg, TransactionQueryExecutor transExec) throws Exception {
		Object userID = RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
		Object params[] = new Object[2];
		params[0] = userID;
		params[1] = prg;
		Boolean res = (Boolean) transExec.executeQuery("UPD_CM_ISCR_RIAPERTURA", params, "UPDATE");
		if (!res.booleanValue())
			throw new Exception("Impossibile effettuare la riapertura CM");
	}

	/**
	 * Aggiorna il flag FLGABILITA nella tabella TS_CONFIG_TAB relativo al lavoratore
	 * 
	 * @param cdnLavoratore
	 * @param flag
	 * @param transExec
	 * @throws Exception
	 */
	public static void aggiornaConfigTab(Object cdnLavoratore, String flag, TransactionQueryExecutor transExec)
			throws Exception {
		Object params[] = new Object[2];
		params[0] = flag;
		params[1] = cdnLavoratore;
		Boolean res = (Boolean) transExec.executeQuery("UPD_TS_CONFIG_TAB_CM", params, "UPDATE");
		if (!res.booleanValue())
			throw new Exception("Impossibile aggiorna la tabella TS_CONFIG_TAB");
	}

	public static void aggiornaSchedaIscrLavCM(String p_cdnLavoratore, BigDecimal p_cdnUtMod,
			TransactionQueryExecutor transExec) throws Exception {
		Connection connection = transExec.getDataConnection().getInternalConnection();
		CallableStatement stmt = connection.prepareCall("{ call PG_GESTAMM.pdModSchedaAnagProf(?, ?, sysdate) }");
		stmt.setBigDecimal(1, new BigDecimal(p_cdnLavoratore));
		stmt.setBigDecimal(2, p_cdnUtMod);
		stmt.execute();
	}

}