/*
 * DBStore.java
 *
 * Created on 06 ottobre 2004
 */

package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.movimenti.constant.Properties;

public class DBStore {

	/**
	 * PER IL MOMENTO MANTENGO LA GESTIONE DELLA CHIUSURA DEL RECORD PRECEDENTE TRAMITE PROCEDURA CHIAMATA DAL TRIGGER
	 * DI AFTER INSERT i campi necessari: DATFINE,CODSTATOOCCUPAZ, CDNUTMOD, NUMKLOSTATOOCCUPAZ, PRGSTATOOCCUPAZ
	 */
	public static void chiudiStatoOcc(StatoOccupazionaleBean statoOcc, String dataChiusura,
			RequestContainer requestContainer, TransactionQueryExecutor transExec) throws Exception {
		SessionContainer session = requestContainer.getSessionContainer();
		Object cdnUser = session.getAttribute("_CDUT_");
		Object numKlo = statoOcc.getNumKlo().add(new BigDecimal("1"));
		Object codStatoAtto = null;
		Object prgStatoOcc = statoOcc.getProgressivoDB();
		Object params[] = new Object[] { codStatoAtto, dataChiusura, cdnUser, numKlo, prgStatoOcc };
		Boolean res = (Boolean) transExec.executeQuery("UPDATE_STATO_OCCUPAZ_DATAFINE", params, "UPDATE");
		if (!res.booleanValue())
			throw new Exception("Impossibile chiudere lo stato occupazionale");
	}

	/**
	 * Chiude lo Stato occupazionale a partire da prgStatoOccupaz Utilizzato per il batch dei tirocini senza contratto
	 */
	public static void chiudiStatoOcc(BigDecimal prgStatoOccupaz, BigDecimal numKloStatoOccupaz, String dataChiusura,
			BigDecimal user, TransactionQueryExecutor transExec) throws Exception {
		Object cdnUser = user;
		Object numKlo = numKloStatoOccupaz.add(new BigDecimal("1"));
		Object codStatoAtto = null;
		Object prgStatoOcc = prgStatoOccupaz;
		Object params[] = new Object[] { codStatoAtto, dataChiusura, cdnUser, numKlo, prgStatoOcc };
		Boolean res = (Boolean) transExec.executeQuery("UPDATE_STATO_OCCUPAZ_DATAFINE", params, "UPDATE");
		if (!res.booleanValue())
			throw new Exception("Impossibile chiudere lo stato occupazionale");
	}

	public static void apriStatoOcc(StatoOccupazionaleBean statoOcc, RequestContainer requestContainer,
			TransactionQueryExecutor transExec) throws Exception {
		Object cdnUser = null;
		SessionContainer session = requestContainer.getSessionContainer();
		cdnUser = session.getAttribute("_CDUT_");
		Object numKlo = statoOcc.getNumKlo().add(new BigDecimal("1"));
		Object codStatoAtto = null;
		Object prgStatoOcc = statoOcc.getProgressivoDB();
		Object params[] = new Object[] { codStatoAtto, null, cdnUser, numKlo, prgStatoOcc };
		Boolean res = null;
		res = (Boolean) transExec.executeQuery("UPDATE_STATO_OCCUPAZ_DATAFINE", params, "UPDATE");
		if (!res.booleanValue())
			throw new Exception("Impossibile chiudere lo stato occupazionale");
	}

	/**
	 * Inserisce un nuovo record ed aggiorna la chiave nell' oggetto statoOccupazionale. Se lo stato occupazionale
	 * inziale e' DISOCCUPATO o INOCCUPATO allora viene riportata la data di anzianita' di disoccupazione. Il
	 * requestContainer serve per ottenere il cdnut che si trova nella sessione statement "INS_STATO_OCCUPAZ_CONTROLLI"
	 */
	public static void creaNuovoStatoOcc(StatoOccupazionaleBean statoOcc, String dataInizio,
			RequestContainer requestContainer, TransactionQueryExecutor transExec) throws Exception {
		SourceBean row = null;
		Object cdnUser = null;
		Object prg = null;
		String dataAnzianita = null;
		String numAnzianitaPrec297 = null;
		String codMonoCalcoloAnzianitaPrec297 = null;
		String numMesiSosp = null;
		String dataCalcoloAnzianita = null;
		String dataCalcoloMesiSosp = null;
		row = (SourceBean) transExec.executeQuery("AM_STATO_OCC_NEXTVAL", new Object[0], "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere il progressivo per la tabella am_stato_occupaz");
		prg = row.getAttribute("ROW.PRGSTATOOCCUPAZ");
		cdnUser = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		if (statoOcc.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_D
				|| statoOcc.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_I) {
			int soccin = statoOcc.getStatoOccupazionaleInizialeC();
			if (soccin < 0)
				dataAnzianita = dataInizio;
			else {
				int raggIniziale = StatoOccupazionaleBean.deStatoOccRagg[soccin];
				if ((raggIniziale == StatoOccupazionaleBean.RAGG_D || raggIniziale == StatoOccupazionaleBean.RAGG_I)
						|| (statoOcc.getDataCalcoloAnzianita() != null && !statoOcc.getDataCalcoloAnzianita().equals("")
								&& DateUtils.compare(dataInizio, statoOcc.getDataCalcoloAnzianita()) >= 0)) {
					dataAnzianita = statoOcc.getDataAnzianita();
					dataCalcoloAnzianita = statoOcc.getDataCalcoloAnzianita();
					dataCalcoloMesiSosp = statoOcc.getDataCalcoloMesiSosp();
				} else {
					dataAnzianita = dataInizio;
					dataCalcoloAnzianita = dataInizio;
					String d2 = "29/01/2003";
					dataCalcoloMesiSosp = DateUtils.compare(dataInizio, d2) > 0 ? dataInizio : d2;
				}
			}
			numAnzianitaPrec297 = statoOcc.getNumAnzianitaPrec297();
			codMonoCalcoloAnzianitaPrec297 = statoOcc.getCodMonoCalcoloAnzianitaPrec297();
			numMesiSosp = statoOcc.getNumMesiSosp();
			/*
			 * se il contesto e' quello della dichiarazione si sanamento di mancato superamento allora bisogna a partire
			 * dalla data della did interessata azzerare in numero di mesi di sospensione
			 */
			if (requestContainer.getServiceRequest().containsAttribute(StatoOccupazionaleBean.DATA_RESET_MESI_SOSP)) {
				String dataDichiarazione = (String) requestContainer.getServiceRequest()
						.getAttribute(StatoOccupazionaleBean.DATA_RESET_MESI_SOSP);
				if (dataDichiarazione != null && !dataDichiarazione.equals("")
						&& DateUtils.compare(dataDichiarazione, dataInizio) <= 0)
					numMesiSosp = "0";
			}
			/*
			 * Se lo stato e' di sospensione e la data inizio dello stato occupazionale precede la data calcolo mesi
			 * sospensione allora bisogna aggiornarla.
			 * 
			 */
			if (statoOcc.getStatoOccupazionale() == StatoOccupazionaleBean.B1) {
				if (dataCalcoloMesiSosp != null && !dataCalcoloMesiSosp.equals("")
						&& DateUtils.compare(dataInizio, dataCalcoloMesiSosp) < 0) {
					dataCalcoloMesiSosp = dataInizio;
					statoOcc.setDataCalcoloMesiSosp(dataCalcoloMesiSosp);
				}
			}

			if (dataCalcoloAnzianita == null || dataCalcoloAnzianita.equals("")) {
				dataCalcoloAnzianita = dataAnzianita;
			}
			if (dataCalcoloMesiSosp == null || dataCalcoloMesiSosp.equals("")) {
				dataCalcoloMesiSosp = dataAnzianita;
			}
		}

		// creo sul db un nuovo stato occupazionale solo se dataInizio non è in data futura
		if (DateUtils.compare(dataInizio, DateUtils.getNow()) <= 0) {
			Object params[] = new Object[18];
			params[0] = prg;
			params[1] = statoOcc.getCdnLavoratore();
			params[2] = StatoOccupazionaleBean.decode(statoOcc.getStatoOccupazionale());
			params[3] = dataInizio;
			params[4] = dataAnzianita;
			params[5] = statoOcc.getIndennizzato();
			params[6] = statoOcc.getPensionato();
			params[7] = numMesiSosp;
			params[8] = null; // strnote
			params[9] = null; // data richiesta revisione
			params[10] = null; // data ricorso giurisdizionale
			params[11] = cdnUser;
			params[12] = cdnUser;
			params[13] = numAnzianitaPrec297;
			params[14] = codMonoCalcoloAnzianitaPrec297;
			params[15] = dataCalcoloAnzianita;
			params[16] = dataCalcoloMesiSosp;
			params[17] = statoOcc.getCodMonoProvenienza();

			Boolean res = (Boolean) transExec.executeQuery("INS_STATO_OCCUPAZ_CONTROLLI", params, "INSERT");
			if (!res.booleanValue())
				throw new Exception("Impossibile inserire il nuovo stato occupazionale dell'utente");

			statoOcc.setProgressivoDB((BigDecimal) prg);
			if (statoOcc instanceof StatoOccupazionaleBean)
				((StatoOccupazionaleBean) statoOcc).getSource().updAttribute("prgStatoOccupaz", prg);
			statoOcc.setDataInizio(dataInizio);
			statoOcc.setDataAnzianita(dataAnzianita);
			if (statoOcc.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_D
					|| statoOcc.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_I) {
				if (statoOcc.getDataCalcoloAnzianita() == null || statoOcc.getDataCalcoloAnzianita().equals("")) {
					statoOcc.setDataCalcoloAnzianita(dataCalcoloAnzianita);
				}
				if (statoOcc.getDataCalcoloMesiSosp() == null || statoOcc.getDataCalcoloMesiSosp().equals("")) {
					statoOcc.setDataCalcoloMesiSosp(dataCalcoloMesiSosp);
				}
			}
			statoOcc.setDataFine(null);
			statoOcc.setDataRichiestaRevisione(null);
			statoOcc.setDataRicorsoGiurisdizionale(null);
			statoOcc.setChanged(true);
		}
	}

	public static void creaNuovoStatoOccManuale(StatoOccupazionaleBean statoOcc, String dataInizio,
			RequestContainer requestContainer, TransactionQueryExecutor transExec) throws Exception {
		SourceBean row = null;
		Object cdnUser = null;
		Object cdnUserIns = null;
		Object cdnUserMod = null;
		String dtmIns = "";
		String dtmMod = "";
		String dataOggi = DateUtils.getNow();
		Object prg = null;
		row = (SourceBean) transExec.executeQuery("AM_STATO_OCC_NEXTVAL", new Object[0], "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere il progressivo per la tabella am_stato_occupaz");
		prg = row.getAttribute("ROW.PRGSTATOOCCUPAZ");
		cdnUserIns = statoOcc.getAttribute("cdnutins");
		cdnUserMod = statoOcc.getAttribute("cdnutmod");
		cdnUser = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		if (cdnUserIns == null) {
			cdnUserIns = cdnUser;
		}
		if (cdnUserMod == null) {
			cdnUserMod = cdnUser;
		}
		dtmIns = statoOcc.getAttribute("dtmins") != null ? statoOcc.getAttribute("dtmins").toString() : "";
		dtmMod = statoOcc.getAttribute("dtmmod") != null ? statoOcc.getAttribute("dtmmod").toString() : "";
		if (dtmIns.equals("")) {
			dtmIns = dataOggi;
		}
		if (dtmMod.equals("")) {
			dtmMod = dataOggi;
		}

		// creo sul db un nuovo stato occupazionale solo se dataInizio non è in data futura
		if (DateUtils.compare(dataInizio, DateUtils.getNow()) <= 0) {
			Object params[] = new Object[18];
			params[0] = prg;
			params[1] = statoOcc.getCdnLavoratore();
			params[2] = StatoOccupazionaleBean.decode(statoOcc.getStatoOccupazionale());
			params[3] = dataInizio;
			params[4] = null;
			params[5] = statoOcc.getDataAnzianita();
			params[6] = statoOcc.getIndennizzato();
			params[7] = statoOcc.getCodMonoProvenienza();
			params[8] = statoOcc.getNumAnzianitaPrec297();
			params[9] = statoOcc.getAttribute("strnote") != null ? statoOcc.getAttribute("strnote").toString() : null;
			params[10] = dtmIns;
			params[11] = dtmMod;
			params[12] = cdnUserIns;
			params[13] = cdnUserMod;
			params[14] = statoOcc.getPensionato();
			params[15] = statoOcc.getNumMesiSosp();
			params[16] = statoOcc.getDataCalcoloMesiSosp();
			params[17] = statoOcc.getDataCalcoloAnzianita();

			Boolean res = (Boolean) transExec.executeQuery("INS_STATO_OCCUPAZ_MANUALE", params, "INSERT");
			if (!res.booleanValue())
				throw new Exception("Impossibile inserire il nuovo stato occupazionale manuale dell'utente");

			statoOcc.setProgressivoDB((BigDecimal) prg);
			if (statoOcc instanceof StatoOccupazionaleBean) {
				((StatoOccupazionaleBean) statoOcc).getSource().updAttribute("prgStatoOccupaz", prg);
			}
			statoOcc.setDataFine(null);
			statoOcc.setDataRichiestaRevisione(null);
			statoOcc.setDataRicorsoGiurisdizionale(null);
		}
	}

	public static void aggiornaStatoOccVA18(StatoOccupazionaleBean statoOccupazionaleAperto,
			RequestContainer requestContainer, String dataInizio, String codStatoOccupaz, String provenienza,
			TransactionQueryExecutor transExec) throws Exception {
		BigDecimal prgStatoOcc = statoOccupazionaleAperto.getPrgStatoOccupaz();
		SessionContainer session = requestContainer.getSessionContainer();
		Object cdnUser = session.getAttribute("_CDUT_");
		Object numKlo = statoOccupazionaleAperto.getNumKlo().add(new BigDecimal("1"));
		String numMesiSosp = "0";
		String dataAnzianita = dataInizio;
		String dataCalcoloAnzianita = dataInizio;
		String dataCalcoloMesiSosp = dataInizio;

		statoOccupazionaleAperto.setDataCalcoloMesiSosp(dataCalcoloMesiSosp);
		statoOccupazionaleAperto.setDataAnzianita(dataAnzianita);
		statoOccupazionaleAperto.setDataCalcoloAnzianita(dataCalcoloAnzianita);
		statoOccupazionaleAperto.setNumMesiSosp("0");

		Object params[] = new Object[] { codStatoOccupaz, provenienza, dataAnzianita, dataCalcoloAnzianita,
				dataCalcoloMesiSosp, numMesiSosp, cdnUser, numKlo, prgStatoOcc };
		Boolean res = (Boolean) transExec.executeQuery("UPDATE_STATO_OCCUPAZ_VA18", params, "UPDATE");
		if (!res.booleanValue())
			throw new Exception("Impossibile chiudere lo stato occupazionale");
	}

	public static void chiudiDID(SourceBean did, String dataChiusura, String codMotivoFineAtto,
			RequestContainer requestContainer, TransactionQueryExecutor transExec) throws Exception {
		String prefix = did.containsAttribute("ROW") ? "ROW." : "";
		Object prg = did.containsAttribute("ROW") ? did.getAttribute("row.prgDichDisponibilita")
				: did.getAttribute("prgDichDisponibilita");
		BigDecimal numKlo = (BigDecimal) (did.containsAttribute("ROW") ? did.getAttribute("row.numKloDichDisp")
				: did.getAttribute("numKloDichDisp"));
		Object cdnUser = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		numKlo = new BigDecimal(numKlo.intValue() + 1);
		Object params[] = new Object[6];
		params[0] = dataChiusura;
		params[1] = dataChiusura;
		params[2] = codMotivoFineAtto;
		params[3] = cdnUser;
		params[4] = numKlo;
		params[5] = prg;
		Boolean res = (Boolean) transExec.executeQuery("CHIUSURA_DICH_DISP2", params, "UPDATE");
		if (res.booleanValue()) {
			did.updAttribute(prefix + "numKloDichDisp", numKlo);
			did.updAttribute(prefix + "flag_changed", "1");
			did.updAttribute(prefix + "datFineChanged", dataChiusura);
			did.updAttribute(prefix + "codMotivoFineAttoChanged", codMotivoFineAtto);
		}
		if (codMotivoFineAtto != null && codMotivoFineAtto.equalsIgnoreCase(Properties.DECADUTO_PER_AVVIAMENTO)) {
			// Chiusura eventuale azione A02 con tipo progetto 05 e data colloquio = data stipula did viene chiusa
			// automaticamente impostando
			// esito = Concluso, esito rendicontazione = Erogato, data fine = sysdate
			try {
				Object paramsLav[] = new Object[1];
				paramsLav[0] = prg;
				SourceBean rowLav = (SourceBean) transExec.executeQuery("GET_CDNLAVORATORE_FROM_DID", paramsLav,
						"SELECT");
				rowLav = rowLav.containsAttribute("ROW") ? (SourceBean) rowLav.getAttribute("ROW") : rowLav;
				Object cdnlavoratore = rowLav.getAttribute("cdnlavoratore");
				String cfLav = rowLav.getAttribute("strcodicefiscale").toString();
				String dataDid = (did.containsAttribute("ROW") && did.getAttribute("row.datdichiarazione") != null)
						? did.getAttribute("row.datdichiarazione").toString()
						: (did.getAttribute("datdichiarazione") != null
								? did.getAttribute("datdichiarazione").toString()
								: null);

				Boolean resAut = null;
				Object paramsGetInvioSap[] = new Object[2];
				paramsGetInvioSap[0] = cdnlavoratore;
				paramsGetInvioSap[1] = cfLav;
				SourceBean rowInvio = (SourceBean) transExec.executeQuery("GET_NUM_INVIO_AUT_SAP_DA_PROCESSARE",
						paramsGetInvioSap, "SELECT");
				rowInvio = rowInvio.containsAttribute("ROW") ? (SourceBean) rowInvio.getAttribute("ROW") : rowInvio;
				BigDecimal numInvio = (BigDecimal) rowInvio.getAttribute("numinvio");
				if (numInvio != null && numInvio.intValue() == 0) {
					Object paramsSap[] = new Object[2];
					paramsSap[0] = cdnlavoratore;
					paramsSap[1] = cfLav;
					resAut = (Boolean) transExec.executeQuery("INSERT_INVIO_AUT_SAP_DA_AUTOMATISMO", paramsSap,
							"INSERT");
				}

				Object paramsGetA02[] = new Object[3];
				paramsGetA02[0] = cdnlavoratore;
				paramsGetA02[1] = dataDid;
				paramsGetA02[2] = dataDid;
				SourceBean rowAzione = (SourceBean) transExec.executeQuery("GET_AZIONE_PRESA_CARICO_150_CHIUSURA_DID",
						paramsGetA02, "SELECT");
				rowAzione = rowAzione.containsAttribute("ROW") ? (SourceBean) rowAzione.getAttribute("ROW") : rowAzione;
				BigDecimal numAzione = (BigDecimal) rowAzione.getAttribute("numazioni");
				if (numAzione != null && numAzione.intValue() > 0) {
					Object paramsA02[] = new Object[4];
					paramsA02[0] = cdnUser;
					paramsA02[1] = cdnlavoratore;
					paramsA02[2] = dataDid;
					paramsA02[3] = dataDid;
					resAut = (Boolean) transExec.executeQuery("CHIUDI_AZIONE_PRESA_CARICO_150_CHIUSURA_DID", paramsA02,
							"UPDATE");
					if (resAut.booleanValue()) {
						Object paramsGetEv[] = new Object[2];
						paramsGetEv[0] = cdnlavoratore;
						paramsGetEv[1] = "RV";
						SourceBean rowEv = (SourceBean) transExec.executeQuery("GET_EVIDENZA_GENERICA_VALIDA",
								paramsGetEv, "SELECT");
						rowEv = rowEv.containsAttribute("ROW") ? (SourceBean) rowEv.getAttribute("ROW") : rowEv;
						BigDecimal numEvidenze = (BigDecimal) rowEv.getAttribute("numevidenze");
						if (numEvidenze != null && numEvidenze.intValue() == 0) {
							Object paramsEv[] = new Object[6];
							paramsEv[0] = cdnlavoratore;
							paramsEv[1] = 30;
							paramsEv[2] = "E' necessario verificare la posizione occupazionale del lavoratore e se necessario effettuare la revoca della DID attraverso il Cruscotto conferimento DID";
							paramsEv[3] = cdnUser;
							paramsEv[4] = cdnUser;
							paramsEv[5] = "RV";
							resAut = (Boolean) transExec.executeQuery("INSERT_EVIDENZA_GENERICA", paramsEv, "INSERT");
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void apriDID(Object prgDichDisponibilita, String dataDid, String codMotivoFineAtto,
			Object prgStatoOccupaz, Object numKlo, RequestContainer requestContainer,
			TransactionQueryExecutor transExec) throws Exception {

		SourceBean row = null;
		Object cdnUser = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		Object paramsSelect[] = new Object[1];
		paramsSelect[0] = prgDichDisponibilita;
		row = (SourceBean) transExec.executeQuery("GET_ELENCO_ANAGRAFICO_APERTO", paramsSelect, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere prgelencoanagrafico dalla tabella am_elenco_anagrafico");
		Object prgElencoAnagrafico = row.getAttribute("ROW.PRGELENCOANAGRAFICO");
		Object cdnlavoratore = row.getAttribute("ROW.CDNLAVORATORE");

		Object params[] = new Object[8];
		params[0] = null; // datfine
		params[1] = null; // codmotivofineatto
		params[2] = prgStatoOccupaz;
		params[3] = numKlo;
		params[4] = cdnUser;
		params[5] = prgElencoAnagrafico;
		params[6] = prgElencoAnagrafico;
		params[7] = prgDichDisponibilita;
		Boolean res = (Boolean) transExec.executeQuery("AGGIORNA_DICH_DISP", params, "UPDATE");

		Boolean resAut = null;
		Object paramsGetA02[] = new Object[3];
		paramsGetA02[0] = cdnlavoratore;
		paramsGetA02[1] = dataDid;
		paramsGetA02[2] = dataDid;
		SourceBean rowAzione = (SourceBean) transExec.executeQuery("GET_AZIONE_PRESA_CARICO_150_RIAPRI_DID",
				paramsGetA02, "SELECT");
		rowAzione = rowAzione.containsAttribute("ROW") ? (SourceBean) rowAzione.getAttribute("ROW") : rowAzione;
		BigDecimal numAzA02 = (BigDecimal) rowAzione.getAttribute("numazioni");
		if (numAzA02 != null && numAzA02.intValue() > 0) {
			Object paramsA02[] = new Object[4];
			paramsA02[0] = cdnUser;
			paramsA02[1] = cdnlavoratore;
			paramsA02[2] = dataDid;
			paramsA02[3] = dataDid;
			resAut = (Boolean) transExec.executeQuery("RIAPRI_AZIONE_PRESA_CARICO_150_RIAPRI_DID", paramsA02, "UPDATE");
		}
	}

	/**
	 * metodo invocato dopo la ricostruzione storia per associare la did ad un nuovo stato occupazionale.
	 * 
	 * @param prgDichDisponibilita
	 * @param prgStatoOccupaz
	 * @param numKlo
	 * @param requestContainer
	 * @param transExec
	 * @throws Exception
	 */
	public static void riassociaDIDSOccInStoria(Object prgDichDisponibilita, Object prgStatoOccupaz, Object numKlo,
			RequestContainer requestContainer, TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[4];
		params[0] = prgStatoOccupaz;
		params[1] = numKlo;
		params[2] = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		params[3] = prgDichDisponibilita;
		Boolean res = (Boolean) transExec.executeQuery("AGGIORNA_DICH_DISP_IMPATTI_STORIA", params, "UPDATE");
	}

	public static void riapriDID(Object prgDichDisponibilita, String dataDid, Object numKlo,
			RequestContainer requestContainer, TransactionQueryExecutor transExec) throws Exception {

		SourceBean row = null;
		Object cdnUser = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		Object paramsSelect[] = new Object[1];
		paramsSelect[0] = prgDichDisponibilita;
		row = (SourceBean) transExec.executeQuery("GET_ELENCO_ANAGRAFICO_APERTO", paramsSelect, "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere prgelencoanagrafico dalla tabella am_elenco_anagrafico");
		Object prgElencoAnagrafico = row.getAttribute("ROW.PRGELENCOANAGRAFICO");
		Object cdnlavoratore = row.getAttribute("ROW.CDNLAVORATORE");

		Object params[] = new Object[7];
		params[0] = null; // datfine
		params[1] = null; // codmotivofineatto
		params[2] = numKlo;
		params[3] = cdnUser;
		params[4] = prgElencoAnagrafico;
		params[5] = prgElencoAnagrafico;
		params[6] = prgDichDisponibilita;
		Boolean res = (Boolean) transExec.executeQuery("AGGIORNA_DICH_DISP2", params, "UPDATE");

		Boolean resAut = null;
		Object paramsGetA02[] = new Object[3];
		paramsGetA02[0] = cdnlavoratore;
		paramsGetA02[1] = dataDid;
		paramsGetA02[2] = dataDid;
		SourceBean rowAzione = (SourceBean) transExec.executeQuery("GET_AZIONE_PRESA_CARICO_150_RIAPRI_DID",
				paramsGetA02, "SELECT");
		rowAzione = rowAzione.containsAttribute("ROW") ? (SourceBean) rowAzione.getAttribute("ROW") : rowAzione;
		BigDecimal numAzA02 = (BigDecimal) rowAzione.getAttribute("numazioni");
		if (numAzA02 != null && numAzA02.intValue() > 0) {
			Object paramsA02[] = new Object[4];
			paramsA02[0] = cdnUser;
			paramsA02[1] = cdnlavoratore;
			paramsA02[2] = dataDid;
			paramsA02[3] = dataDid;
			resAut = (Boolean) transExec.executeQuery("RIAPRI_AZIONE_PRESA_CARICO_150_RIAPRI_DID", paramsA02, "UPDATE");
		}
	}

	public static void apriPatto(Object prgPattoLavoratore, String dataChiusura, String codMotivoFineAtto,
			Object prgStatoOccupaz, Object numKlo, RequestContainer requestContainer,
			TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[6];
		params[0] = null;
		params[1] = null;
		params[2] = prgStatoOccupaz;
		params[3] = numKlo;
		params[4] = prgPattoLavoratore;
		params[5] = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		Boolean res = (Boolean) transExec.executeQuery("AGGIORNA_PATTO", params, "UPDATE");
	}

	/**
	 * statement "CHIUSURA_PATTO"
	 */
	public static void chiudiPatto297(Object cdnLavoratore, String dataChiusura, String codMotivoFineAtto,
			RequestContainer requestContainer, TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[6];
		params[0] = dataChiusura;
		params[1] = dataChiusura;
		params[2] = codMotivoFineAtto;
		params[3] = codMotivoFineAtto;
		params[4] = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		params[5] = cdnLavoratore;
		Boolean res = (Boolean) transExec.executeQuery("CHIUSURA_PATTO", params, "UPDATE");
		// chiusura delle azioni nel programma SEP
		gestisciColloquiSepPatto297Aperto(cdnLavoratore, requestContainer, transExec);
	}

	public static void chiudiPatto297(SourceBean patto, String dataChiusura, String codMotivoFineAtto,
			RequestContainer requestContainer, TransactionQueryExecutor transExec) throws Exception {
		Object prgPattoLavoratore = patto.getAttribute("prgPattoLavoratore");
		BigDecimal numKlo = (BigDecimal) patto.getAttribute("numKloPattoLavoratore");
		String dataStipula = patto.containsAttribute(PattoBean.DB_DAT_INIZIO)
				? patto.getAttribute(PattoBean.DB_DAT_INIZIO).toString()
				: "";
		if (!dataStipula.equals("") && !dataChiusura.equals("") && dataChiusura != null
				&& DateUtils.compare(dataStipula, dataChiusura) > 0) {
			dataChiusura = dataStipula;
		}
		numKlo = numKlo.add(new BigDecimal(1));
		Object params[] = new Object[7];
		params[0] = dataChiusura;
		params[1] = dataChiusura;
		params[2] = codMotivoFineAtto;
		params[3] = codMotivoFineAtto;
		params[4] = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		params[5] = numKlo;
		params[6] = prgPattoLavoratore;
		Boolean res = (Boolean) transExec.executeQuery("CHIUSURA_PATTO2", params, "UPDATE");
		patto.updAttribute("numKloPattoLavoratore", numKlo);
		if (codMotivoFineAtto != null && !codMotivoFineAtto.equals("")) {
			patto.updAttribute(PattoBean.COD_MOTIVO_FINE_ATTO, codMotivoFineAtto);
		}
		// chiusura delle azioni nel programma SEP
		gestisciColloquiSep(prgPattoLavoratore, requestContainer, transExec);
	}

	public static void chiudiAccordoNo297(SourceBean accordo, String dataChiusura, String codMotivoFineAtto,
			RequestContainer requestContainer, TransactionQueryExecutor transExec) throws Exception {
		Object prgPattoLavoratore = accordo.getAttribute("prgPattoLavoratore");
		BigDecimal numKlo = (BigDecimal) accordo.getAttribute("numKloPattoLavoratore");
		numKlo = numKlo.add(new BigDecimal(1));
		Object params[] = new Object[5];
		params[0] = dataChiusura;
		params[1] = codMotivoFineAtto;
		params[2] = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		params[3] = numKlo;
		params[4] = prgPattoLavoratore;
		Boolean res = (Boolean) transExec.executeQuery("CHIUSURA_ACCORDO_NO_297", params, "UPDATE");
		accordo.updAttribute("numKloPattoLavoratore", numKlo);
		// chiusura delle azioni nel programma SEP
		gestisciColloquiSep(prgPattoLavoratore, requestContainer, transExec);
	}

	public static void gestisciColloquiSep(Object prgPattoLavoratore, RequestContainer requestContainer,
			TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[1];
		params[0] = prgPattoLavoratore;
		SourceBean rowAzione = (SourceBean) transExec.executeQuery("GET_COLLOQUIO_SEP_ASSOCIATO_PATTO_UMB", params,
				"SELECT");
		if (rowAzione != null) {
			Vector colloqui = rowAzione.getAttributeAsVector("ROW");
			if (colloqui != null && colloqui.size() > 0) {
				int sizeColloqui = colloqui.size();
				Object paramsUpd[] = new Object[2];
				paramsUpd[0] = requestContainer.getSessionContainer().getAttribute("_CDUT_");
				paramsUpd[1] = prgPattoLavoratore;
				Boolean res = (Boolean) transExec.executeQuery("UPDATE_COLLOQUIO_SEP_ASSOCIATO_PATTO_UMB", paramsUpd,
						"UPDATE");
				if (!res.booleanValue())
					throw new Exception("impossibile aggiornare il colloquio");
				for (int i = 0; i < sizeColloqui; i++) {
					SourceBean colloquio = (SourceBean) colloqui.get(i);
					BigDecimal prgColloquio = colloquio.getAttribute("prgcolloquio") != null
							? (BigDecimal) colloquio.getAttribute("prgcolloquio")
							: null;
					Object paramsData[] = new Object[1];
					paramsData[0] = prgColloquio;

					SourceBean azioniInCorso = (SourceBean) transExec.executeQuery("GET_ALL_AZIONI_PROGRAMMA_IN_CORSO",
							paramsData, "SELECT");
					azioniInCorso = azioniInCorso.containsAttribute("ROW")
							? (SourceBean) azioniInCorso.getAttribute("ROW")
							: azioniInCorso;
					BigDecimal numAzioni = (BigDecimal) azioniInCorso.getAttribute("numazioniincorso");
					if (numAzioni != null && numAzioni.intValue() == 0) {
						SourceBean colloquioBean = (SourceBean) transExec.executeQuery("GET_INFO_COLLOQUIO", paramsData,
								"SELECT");
						BigDecimal numklocoll = (BigDecimal) colloquioBean.getAttribute("ROW.NUMKLOCOLLOQUIO");
						numklocoll = numklocoll.add(new BigDecimal(1D));
						String dataConclusione = null;

						SourceBean rowData = (SourceBean) transExec.executeQuery("GET_MAX_DATA_CONCLUSIONE_PROGRAMMA",
								paramsData, "SELECT");
						if (rowData != null) {
							rowData = rowData.containsAttribute("ROW") ? (SourceBean) rowData.getAttribute("ROW")
									: rowData;
							dataConclusione = (String) rowData.getAttribute("datConclusione");
						}
						if (dataConclusione == null || dataConclusione.equals("")) {
							dataConclusione = DateUtils.getNow();
						}
						Object paramChiudi[] = new Object[4];
						paramChiudi[0] = dataConclusione;
						paramChiudi[1] = numklocoll;
						paramChiudi[2] = requestContainer.getSessionContainer().getAttribute("_CDUT_");
						;
						paramChiudi[3] = prgColloquio;

						Boolean resChiusura = (Boolean) transExec.executeQuery("UPDATE_RIAPRI_CHIUDI_PROGRAMMA",
								paramChiudi, "UPDATE");
						if (!resChiusura.booleanValue())
							throw new Exception("impossibile chiudere il programma");
					}
				}

			}
		}
	}

	public static void gestisciColloquiSepPatto297Aperto(Object cdnLavoratore, RequestContainer requestContainer,
			TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[1];
		params[0] = cdnLavoratore;
		SourceBean rowAzione = (SourceBean) transExec.executeQuery("GET_COLLOQUIO_SEP_ASSOCIATO_PATTO297_UMB_APERTO",
				params, "SELECT");
		if (rowAzione != null) {
			Vector colloqui = rowAzione.getAttributeAsVector("ROW");
			if (colloqui != null && colloqui.size() > 0) {
				int sizeColloqui = colloqui.size();
				Object paramsUpd[] = new Object[2];
				paramsUpd[0] = requestContainer.getSessionContainer().getAttribute("_CDUT_");
				paramsUpd[1] = cdnLavoratore;
				Boolean res = (Boolean) transExec.executeQuery("UPDATE_COLLOQUIO_SEP_ASSOCIATO_PATTO297_UMB_APERTO",
						paramsUpd, "UPDATE");
				if (!res.booleanValue())
					throw new Exception("impossibile aggiornare il colloquio");
				for (int i = 0; i < sizeColloqui; i++) {
					SourceBean colloquio = (SourceBean) colloqui.get(i);
					BigDecimal prgColloquio = colloquio.getAttribute("prgcolloquio") != null
							? (BigDecimal) colloquio.getAttribute("prgcolloquio")
							: null;
					Object paramsData[] = new Object[1];
					paramsData[0] = prgColloquio;

					SourceBean azioniInCorso = (SourceBean) transExec.executeQuery("GET_ALL_AZIONI_PROGRAMMA_IN_CORSO",
							paramsData, "SELECT");
					azioniInCorso = azioniInCorso.containsAttribute("ROW")
							? (SourceBean) azioniInCorso.getAttribute("ROW")
							: azioniInCorso;
					BigDecimal numAzioni = (BigDecimal) azioniInCorso.getAttribute("numazioniincorso");
					if (numAzioni != null && numAzioni.intValue() == 0) {
						SourceBean colloquioBean = (SourceBean) transExec.executeQuery("GET_INFO_COLLOQUIO", paramsData,
								"SELECT");
						BigDecimal numklocoll = (BigDecimal) colloquioBean.getAttribute("ROW.NUMKLOCOLLOQUIO");
						numklocoll = numklocoll.add(new BigDecimal(1D));
						String dataConclusione = null;

						SourceBean rowData = (SourceBean) transExec.executeQuery("GET_MAX_DATA_CONCLUSIONE_PROGRAMMA",
								paramsData, "SELECT");
						if (rowData != null) {
							rowData = rowData.containsAttribute("ROW") ? (SourceBean) rowData.getAttribute("ROW")
									: rowData;
							dataConclusione = (String) rowData.getAttribute("datConclusione");
						}
						if (dataConclusione == null || dataConclusione.equals("")) {
							dataConclusione = DateUtils.getNow();
						}
						Object paramChiudi[] = new Object[4];
						paramChiudi[0] = dataConclusione;
						paramChiudi[1] = numklocoll;
						paramChiudi[2] = requestContainer.getSessionContainer().getAttribute("_CDUT_");
						;
						paramChiudi[3] = prgColloquio;

						Boolean resChiusura = (Boolean) transExec.executeQuery("UPDATE_RIAPRI_CHIUDI_PROGRAMMA",
								paramChiudi, "UPDATE");
						if (!resChiusura.booleanValue())
							throw new Exception("impossibile chiudere il programma");
					}
				}

			}
		}
	}

	public static void apriAccordoNo297(SourceBean accordo, RequestContainer requestContainer,
			TransactionQueryExecutor transExec) throws Exception {
		Object prgPattoLavoratore = accordo.getAttribute("prgPattoLavoratore");
		BigDecimal numKlo = (BigDecimal) accordo.getAttribute("numKloPattoLavoratore");
		numKlo = numKlo.add(new BigDecimal(1));
		Object params[] = new Object[3];
		params[0] = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		params[1] = numKlo;
		params[2] = prgPattoLavoratore;
		Boolean res = (Boolean) transExec.executeQuery("APRI_ACCORDO_NO_297", params, "UPDATE");
		accordo.updAttribute("numKloPattoLavoratore", numKlo);
	}

	public static void trasformaPattoAccordoANP(SourceBean pattoAccordo, RequestContainer requestContainer,
			TransactionQueryExecutor transExec) throws Exception {
		Object prgPattoLavoratore = pattoAccordo.getAttribute("prgPattoLavoratore");
		BigDecimal numKlo = (BigDecimal) pattoAccordo.getAttribute("numKloPattoLavoratore");
		DataConnection conn = transExec.getDataConnection();
		StoredProcedureCommand command = (StoredProcedureCommand) conn
				.createStoredProcedureCommand("{ call ? := PG_UTILS_DID_PATTO.TRASFORMAPATTOANP(?,?) }");
		DataResult dr = null;

		ArrayList parameters = new ArrayList(5);
		parameters.add(conn.createDataField("result", Types.BIGINT, null));
		command.setAsOutputParameters(0);
		parameters.add(conn.createDataField("prgpattolavoratore", Types.BIGINT, prgPattoLavoratore));
		command.setAsInputParameters(1);
		parameters.add(conn.createDataField("pCdnUt", Types.BIGINT,
				requestContainer.getSessionContainer().getAttribute("_CDUT_")));
		command.setAsInputParameters(2);

		dr = command.execute(parameters);
		PunctualDataResult cdr = (PunctualDataResult) dr.getDataObject();
		DataField df = cdr.getPunctualDatafield();
		String codiceRit = df.getStringValue();
		if (codiceRit.equals("0")) {
			numKlo = numKlo.add(new BigDecimal(1));
			pattoAccordo.updAttribute("numKloPattoLavoratore", numKlo);
		}
	}

	/**
	 * DELETE_STATO_OCCUPAZIONALE
	 */
	public static void cancellaStatoOccupazionale(BigDecimal prgStatoOccupaz, TransactionQueryExecutor transExec)
			throws Exception {
		Object params[] = new Object[1];
		params[0] = prgStatoOccupaz;
		Boolean res = (Boolean) transExec.executeQuery("DELETE_STATO_OCCUPAZIONALE", params, "DELETE");
		if (!res.booleanValue())
			throw new Exception(
					"Impossibile cancellare lo stato occupazionale con prgStatoOccupaz = " + prgStatoOccupaz);
	}

	/**
	 * statement "UPDATE_DID_PER_SANARE_PERIODO_PREC"
	 */
	public static void aggiornaDID(SourceBean did, BigDecimal prgStatoOccupaz, RequestContainer requestContainer,
			TransactionQueryExecutor transExec) throws Exception {
		BigDecimal prgDichDisponibilita = (BigDecimal) did.getAttribute("prgDichDisponibilita");
		BigDecimal numKlo = (BigDecimal) did.getAttribute("numKloDichDisp");
		BigDecimal newNumKlo = new BigDecimal(numKlo.intValue() + 1);
		Object cdnUser = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		Object params[] = new Object[4];
		params[0] = prgStatoOccupaz;
		params[1] = newNumKlo;
		params[2] = cdnUser;
		params[3] = prgDichDisponibilita;
		Boolean res = (Boolean) transExec.executeQuery("UPDATE_DID_PER_SANARE_PERIODO_PREC", params, "UPDATE");
		if (!res.booleanValue())
			throw new Exception(
					"Impossibile aggiornare la dichiarazione di immediata disponibilita' con prgdichDisponibilita = "
							+ prgDichDisponibilita);
		did.updAttribute("numKloDichDisp", newNumKlo);
	}

	/**
	 * statement "UPDATE_PATTO_PER_SANARE_PERIODO_PREC"
	 */
	public static void aggiornaPatto297(SourceBean patto, BigDecimal prgStatoOccupaz, RequestContainer requestContainer,
			TransactionQueryExecutor transExec) throws Exception {
		BigDecimal prgPattoLavoratore = (BigDecimal) patto.getAttribute("prgPattoLavoratore");
		BigDecimal numKlo = (BigDecimal) patto.getAttribute("numKloPattoLavoratore");
		BigDecimal newNumKlo = new BigDecimal(numKlo.intValue() + 1);
		Object cdnUser = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		Object params[] = new Object[4];
		params[0] = prgStatoOccupaz;
		params[1] = newNumKlo;
		params[2] = cdnUser;
		params[3] = prgPattoLavoratore;
		Boolean res = (Boolean) transExec.executeQuery("UPDATE_PATTO_PER_SANARE_PERIODO_PREC", params, "UPDATE");
		if (!res.booleanValue())
			throw new Exception(
					"Impossibile aggiornare il patto del lavoratore con prgPattoLavoratore = " + prgPattoLavoratore);
		patto.updAttribute("numKloPattoLavoratore", newNumKlo);
	}

	/**
	 * metodo per l'aggiornamento del movimento utilizzato nel batch delle cessazioni giornaliere
	 */
	public static void aggiornaMovimentoPerBatch(SourceBean movimento, StatoOccupazionaleBean statoOcc,
			RequestContainer requestContainer, TransactionQueryExecutor transExec) throws Exception {
		Object prgMovimento = movimento.getAttribute("prgMovimento");
		if (prgMovimento != null && !prgMovimento.toString().equals("-1")) {
			BigDecimal numKlo = new BigDecimal(movimento.getAttribute("numKloMov").toString());
			BigDecimal newNumKlo = new BigDecimal(numKlo.intValue() + 1);
			Object codMonoBatch = movimento.getAttribute("codMonoBatch") == null ? ""
					: movimento.getAttribute("codMonoBatch");
			Object cdnUser = requestContainer.getSessionContainer().getAttribute("_CDUT_");
			Object params[] = new Object[5];
			params[0] = statoOcc.getProgressivoDB();
			params[1] = codMonoBatch;
			params[2] = newNumKlo;
			params[3] = cdnUser;
			params[4] = prgMovimento;
			Boolean res = (Boolean) transExec.executeQuery("UPDATE_MOVIMENTO_PER_BATCH", params, "UPDATE");
			if (!res.booleanValue())
				throw new Exception(
						"Impossibile aggiornare il movimento  con prgMovimento = " + (BigDecimal) prgMovimento);
		}
	}

	public static void aggiornaMovimento(MovimentoBean movimento, RequestContainer requestContainer,
			TransactionQueryExecutor transExec) throws Exception {
		StatoOccupazionaleBean so = movimento.getStatoOccupazionale();
		Object newPrgSO = so.getPrgStatoOccupaz();
		Object prg = movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
		if (prg != null && !prg.toString().equals("-1")) {
			BigDecimal newNumKlo = (BigDecimal) movimento.getAttribute(MovimentoBean.DB_NUM_K_LOCK);
			newNumKlo = newNumKlo.add(new BigDecimal(1));
			Object cdnUser = null;
			Boolean res = null;
			cdnUser = requestContainer.getSessionContainer().getAttribute("_CDUT_");
			Object params[] = null;
			String stmName = null;
			params = new Object[4];
			params[0] = newPrgSO;
			params[1] = newNumKlo;
			params[2] = cdnUser;
			params[3] = prg;
			stmName = "UPDATE_MOVIMENTO_PER_STATO_OCC";
			res = (Boolean) transExec.executeQuery(stmName, params, "UPDATE");
			if (!res.booleanValue())
				throw new Exception("Impossibile aggiornare il movimento  con prgMovimento = " + (BigDecimal) prg);
			movimento.updAttribute(MovimentoBean.DB_NUM_K_LOCK, newNumKlo);
		}
	}

	/**
	 * Viene chiamata nel metodo dereferenziaEventiAmmInRicostruzione della classe SituazioneAmministrativa
	 * 
	 * @param movimento
	 * @param cdnUser
	 * @param transExec
	 * @throws Exception
	 */
	public static void aggiornaMovimento(MovimentoBean movimento, Object cdnUser, TransactionQueryExecutor transExec)
			throws Exception {
		StatoOccupazionaleBean so = movimento.getStatoOccupazionale();
		Object newPrgSO = so.getPrgStatoOccupaz();
		Object prg = movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
		if (prg != null && !prg.toString().equals("-1")) {
			BigDecimal newNumKlo = (BigDecimal) movimento.getAttribute(MovimentoBean.DB_NUM_K_LOCK);
			newNumKlo = newNumKlo.add(new BigDecimal(1));
			Boolean res = null;
			Object params[] = null;
			String stmName = null;
			params = new Object[4];
			params[0] = newPrgSO;
			params[1] = newNumKlo;
			params[2] = cdnUser;
			params[3] = prg;
			stmName = "UPDATE_MOVIMENTO_PER_STATO_OCC";
			res = (Boolean) transExec.executeQuery(stmName, params, "UPDATE");
			if (!res.booleanValue())
				throw new Exception("Impossibile aggiornare il movimento  con prgMovimento = " + (BigDecimal) prg);
			movimento.updAttribute(MovimentoBean.DB_NUM_K_LOCK, newNumKlo);
		}
	}

	public static void aggiornaMovimento(SourceBean movimento, RequestContainer requestContainer,
			TransactionQueryExecutor transExec) throws Exception {
		Object newPrgSO = null;
		Object prg = movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
		if (prg != null && !prg.toString().equals("-1")) {
			BigDecimal newNumKlo = (BigDecimal) movimento.getAttribute(MovimentoBean.DB_NUM_K_LOCK);
			newNumKlo = newNumKlo.add(new BigDecimal(1));
			Object cdnUser = null;
			Boolean res = null;
			cdnUser = requestContainer.getSessionContainer().getAttribute("_CDUT_");
			Object params[] = null;
			String stmName = null;
			params = new Object[4];
			params[0] = newPrgSO;
			params[1] = newNumKlo;
			params[2] = cdnUser;
			params[3] = prg;
			stmName = "UPDATE_MOVIMENTO_PER_STATO_OCC";
			res = (Boolean) transExec.executeQuery(stmName, params, "UPDATE");
			if (!res.booleanValue())
				throw new Exception("Impossibile aggiornare il movimento  con prgMovimento = " + (BigDecimal) prg);
			movimento.updAttribute(MovimentoBean.DB_NUM_K_LOCK, newNumKlo);
		}
	}

	/**
	 * statement "UPDATE_MOVIMENTO_PER_SANARE_PERIODO_PREC" parametri presenti nel movimento: prgMovimento, numKloMov
	 */
	public static void cessaMovimento(SourceBean movimento, StatoOccupazionaleBean statoOcc,
			RequestContainer requestContainer, TransactionQueryExecutor transExec) throws Exception {
		BigDecimal numKlo = (BigDecimal) movimento.getAttribute(MovimentoBean.DB_NUM_K_LOCK);
		BigDecimal newNumKlo = new BigDecimal(numKlo.intValue() + 1);
		Object prgMovimento = movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
		Object dataFineEffettiva = movimento.getAttribute(MovimentoBean.DB_DATA_FINE_EFFETTIVA);
		Object cdnUser = null;
		Object codMonoTipoFine = movimento.getAttribute(MovimentoBean.DB_COD_MONO_TIPO_FINE);
		Object prgCessazione = movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_SUCC);
		cdnUser = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		Object params[] = new Object[7];
		params[0] = statoOcc.getProgressivoDB();
		params[1] = newNumKlo;
		params[2] = cdnUser;
		params[3] = prgCessazione;
		params[4] = codMonoTipoFine;
		params[5] = dataFineEffettiva;
		params[6] = prgMovimento;
		Boolean res = (Boolean) transExec.executeQuery("UPDATE_MOVIMENTO_PER_CESSAZIONE", params, "UPDATE");
		if (!res.booleanValue())
			throw new Exception(
					"Impossibile aggiornare il movimento  con prgMovimento cessato = " + (BigDecimal) prgMovimento);
	}

	/**
	 * Imposta il prgStatoOcc del movimento passato
	 */
	public static void aggiornaStatoOccMovimento(MovimentoBean movimento, RequestContainer requestContainer,
			TransactionQueryExecutor transExec) throws Exception {
		StatoOccupazionaleBean so = movimento.getStatoOccupazionale();
		Object newPrgSO = null;
		if (so != null) {
			newPrgSO = so.getPrgStatoOccupaz();
		} else {
			newPrgSO = "";
		}
		Object prg = movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
		BigDecimal newNumKlo = new BigDecimal(movimento.getAttribute(MovimentoBean.DB_NUM_K_LOCK).toString());
		newNumKlo = newNumKlo.add(new BigDecimal(1));
		Object cdnUser = null;
		Boolean res = null;
		cdnUser = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		Object params[] = new Object[3];
		params[0] = newPrgSO;
		params[1] = cdnUser;
		params[2] = prg;
		res = (Boolean) transExec.executeQuery("UPDATE_MOVIMENTO_STAT", params, "UPDATE");
		if (!res.booleanValue())
			throw new Exception("Impossibile aggiornare il movimento  con prgMovimento = " + (BigDecimal) prg);
		movimento.updAttribute(MovimentoBean.DB_NUM_K_LOCK, newNumKlo);
	}

	/**
	 * Aggiorna i campi del Prec e Succ al movimento che si sta annullando
	 */
	public static void aggiornaCampiMovimento(MovimentoBean movimento, Object campi[],
			RequestContainer requestContainer, TransactionQueryExecutor transExec, int modo) throws Exception {
		Object campoVariabile = null;
		Object prg = movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO) != null
				? movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO)
				: "";
		BigDecimal newNumKlo = new BigDecimal(movimento.getAttribute(MovimentoBean.DB_NUM_K_LOCK).toString());
		newNumKlo = newNumKlo.add(new BigDecimal(1));
		Object cdnUser = null;
		Boolean res = null;
		cdnUser = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		Object params[] = new Object[6];
		params[0] = campi[0];
		params[1] = campi[1];
		params[2] = campi[2];
		params[3] = newNumKlo;
		params[4] = cdnUser;
		params[5] = prg;
		if (modo == 0)
			// Aggiorna i campi del movimento precedente
			res = (Boolean) transExec.executeQuery("UPDATE_PROGRESSIVI_MOVIMENTO_PREC", params, "UPDATE");
		else
			// Aggiorna i campi del movimento successivo
			res = (Boolean) transExec.executeQuery("UPDATE_PROGRESSIVI_MOVIMENTO_SUCC", params, "UPDATE");
		if (!res.booleanValue())
			throw new Exception("Impossibile aggiornare il movimento  con prgMovimento = " + (BigDecimal) prg);
		movimento.updAttribute(MovimentoBean.DB_NUM_K_LOCK, newNumKlo);
	}

	/**
	 * Crea un nuovo stato occupazionale, utilizzato nel batch per tirocini senza contratto
	 */
	public static void creaNuovoStatoOccPerBatch(StatoOccupazionaleBean statoOcc, String dataInizio,
			RequestContainer requestContainer, TransactionQueryExecutor transExec) throws Exception {
		SourceBean row = null;
		Object cdnUser = null;
		Object prg = null;
		String dataAnzianita = null;
		String numAnzianitaPrec297 = null;
		String codMonoCalcoloAnzianitaPrec297 = null;
		String numMesiSosp = null;
		String dataCalcoloAnzianita = null;
		String dataCalcoloMesiSosp = null;
		row = (SourceBean) transExec.executeQuery("AM_STATO_OCC_NEXTVAL", new Object[0], "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere il progressivo per la tabella am_stato_occupaz");
		prg = row.getAttribute("ROW.PRGSTATOOCCUPAZ");
		cdnUser = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		if (statoOcc.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_D
				|| statoOcc.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_I) {
			int soccin = statoOcc.getStatoOccupazionaleInizialeC();
			if (soccin < 0)
				dataAnzianita = dataInizio;
			else {
				int raggIniziale = StatoOccupazionaleBean.deStatoOccRagg[soccin];
				if ((raggIniziale == StatoOccupazionaleBean.RAGG_D || raggIniziale == StatoOccupazionaleBean.RAGG_I)
						|| (statoOcc.getDataCalcoloAnzianita() != null && !statoOcc.getDataCalcoloAnzianita().equals("")
								&& DateUtils.compare(dataInizio, statoOcc.getDataCalcoloAnzianita()) >= 0)) {
					dataAnzianita = statoOcc.getDataAnzianita();
					dataCalcoloAnzianita = statoOcc.getDataCalcoloAnzianita();
					dataCalcoloMesiSosp = statoOcc.getDataCalcoloMesiSosp();
				} else {
					dataAnzianita = dataInizio;
					dataCalcoloAnzianita = dataInizio;
					String d2 = "29/01/2003";
					dataCalcoloMesiSosp = DateUtils.compare(dataInizio, d2) > 0 ? dataInizio : d2;
				}
			}
			numAnzianitaPrec297 = statoOcc.getNumAnzianitaPrec297();
			codMonoCalcoloAnzianitaPrec297 = statoOcc.getCodMonoCalcoloAnzianitaPrec297();
			numMesiSosp = statoOcc.getNumMesiSosp();

			if (dataCalcoloAnzianita == null || dataCalcoloAnzianita.equals("")) {
				dataCalcoloAnzianita = dataAnzianita;
			}
			if (dataCalcoloMesiSosp == null || dataCalcoloMesiSosp.equals("")) {
				dataCalcoloMesiSosp = dataAnzianita;
			}

		}
		Object params[] = new Object[18];
		params[0] = prg;
		params[1] = statoOcc.getCdnLavoratore();
		params[2] = StatoOccupazionaleBean.decode(statoOcc.getStatoOccupazionale());
		params[3] = dataInizio;
		params[4] = dataAnzianita;
		params[5] = statoOcc.getIndennizzato();
		params[6] = statoOcc.getPensionato();
		params[7] = statoOcc.getNumMesiSosp(); // nummesisosp
		params[8] = null; // strnote
		params[9] = statoOcc.getDataRichiestaRevisione(); // data richiesta revisione
		params[10] = statoOcc.getDataRicorsoGiurisdizionale(); // data ricorso giurisdizionale
		params[11] = cdnUser;
		params[12] = cdnUser;
		params[13] = numAnzianitaPrec297;
		params[14] = codMonoCalcoloAnzianitaPrec297;
		params[15] = dataCalcoloAnzianita;
		params[16] = dataCalcoloMesiSosp;
		params[17] = statoOcc.getCodMonoProvenienza();

		Boolean res = (Boolean) transExec.executeQuery("INS_STATO_OCCUPAZ_CONTROLLI", params, "INSERT");
		if (!res.booleanValue())
			throw new Exception("Impossibile inserire il nuovo stato occupazionale dell'utente");
		statoOcc.setProgressivoDB((BigDecimal) prg);
		if (statoOcc instanceof StatoOccupazionaleBean)
			((StatoOccupazionaleBean) statoOcc).getSource().updAttribute("prgStatoOccupaz", prg);
		statoOcc.setDataInizio(dataInizio);
		statoOcc.setDataAnzianita(dataAnzianita);
		if (statoOcc.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_D
				|| statoOcc.getStatoOccupazionaleRagg() == StatoOccupazionaleBean.RAGG_I) {
			if (statoOcc.getDataCalcoloAnzianita() == null || statoOcc.getDataCalcoloAnzianita().equals("")) {
				statoOcc.setDataCalcoloAnzianita(dataCalcoloAnzianita);
			}
			if (statoOcc.getDataCalcoloMesiSosp() == null || statoOcc.getDataCalcoloMesiSosp().equals("")) {
				statoOcc.setDataCalcoloMesiSosp(dataCalcoloMesiSosp);
			}
		}
		statoOcc.setDataFine(null);
		statoOcc.setDataRichiestaRevisione(null);
		statoOcc.setDataRicorsoGiurisdizionale(null);
		statoOcc.setChanged(true);
	}

	public static void creaDichiarazioneLavoratore(SourceBean request, BigDecimal prgMov,
			TransactionQueryExecutor transExec) throws Exception {
		String dataInizio = (String) request.getAttribute("datSitSanata");
		Object cdnLavoratore = request.getAttribute("cdnLavoratore");
		String note = (String) request.getAttribute("strNote");
		String codTipoDich = (String) request.getAttribute("codTipoDich");
		Object cdnUtente = RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
		SourceBean row = null;
		row = (SourceBean) transExec.executeQuery("AM_DICH_LAV_NEXTVAL", new Object[0], "SELECT");
		if (row == null)
			throw new Exception("impossibile leggere il progressivo per la tabella am_dich_lav");
		Object prg = row.getAttribute("ROW.PRGDICHLAV");
		Object[] params = new Object[8];
		params[0] = prg;
		params[1] = cdnLavoratore;
		params[2] = dataInizio;
		params[3] = prgMov;
		params[4] = note;
		params[5] = cdnUtente;
		params[6] = cdnUtente;
		params[7] = codTipoDich;
		Boolean res = (Boolean) transExec.executeQuery("INS_DICH_LAV", params, "INSERT");
		request.updAttribute("prgDichLav", prg);

	}

	public static void creaDichiarazioneLavDettaglio(SourceBean mov, TransactionQueryExecutor transExec)
			throws Exception {
		Object params[] = new Object[6];
		Object prgDichLav = mov.getAttribute("prgDichLav");
		Object prgMovimento = mov.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
		Object retribuzione = mov.getAttribute(MovimentoBean.DB_RETRIBUZIONE);
		Object retribuzioneSanata = mov.getAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA);
		Object cdnUtente = RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");

		params[0] = prgDichLav;
		params[1] = prgMovimento;
		params[2] = retribuzione;
		params[3] = cdnUtente;
		params[4] = cdnUtente;
		params[5] = retribuzioneSanata;
		transExec.executeQuery("INS_DICH_LAV_DETTAGLIO", params, "INSERT");
	}

	public static void aggiornaMovInfoSanare(SourceBean request, SourceBean mov, TransactionQueryExecutor transExec)
			throws Exception {
		Object prgDichLav = mov.getAttribute("prgDichLav");
		Object decRetribuzioneSanata = mov.getAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA);
		String datSitSanata = (String) mov.getAttribute(MovimentoBean.DB_DATA_SIT_SANATA);
		String codTipoDich = (String) mov.getAttribute(MovimentoBean.DB_COD_TIPO_DICH);
		String datInizioMovSuperamento = mov.containsAttribute("datInizioMovSupReddito")
				? mov.getAttribute("datInizioMovSupReddito").toString()
				: "";
		// se la data non è presente nel movimento la prendo
		// dalla request (nella jsp è stato aggiunto un campo nascosto)
		if (datInizioMovSuperamento.equals(""))
			datInizioMovSuperamento = request.containsAttribute("datInizioMovSupReddito")
					? request.getAttribute("datInizioMovSupReddito").toString()
					: "";
		Object prg = mov.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
		Object cdnUtente = RequestContainer.getRequestContainer().getSessionContainer().getAttribute("_CDUT_");
		BigDecimal numKlo = (BigDecimal) mov.getAttribute(MovimentoBean.DB_NUM_K_LOCK);
		numKlo = numKlo.add(new BigDecimal(1));
		Object params[] = new Object[8];
		params[0] = prgDichLav;
		params[1] = decRetribuzioneSanata;
		params[2] = datSitSanata;
		params[3] = codTipoDich;
		params[4] = numKlo;
		params[5] = cdnUtente;
		params[6] = datInizioMovSuperamento;
		params[7] = prg;
		//
		Boolean ret = (Boolean) transExec.executeQuery("UPDATE_MOVIMENTO_PER_SANARE_PERIODO_PREC", params, "UPDATE");
		if (!ret.booleanValue())
			throw new Exception("sdsdfsdfs");
		mov.updAttribute(MovimentoBean.DB_NUM_K_LOCK, numKlo);
	}

	/**
	 * Riapre il patto con prgPattoLavoratore uguale a quello passatogli
	 * 
	 * @param prgPattoLavoratore
	 * @param prgStatoOccupaz
	 * @param numKlo
	 * @param requestContainer
	 * @param transExec
	 * @throws Exception
	 */
	public static void riapriPatto(Object prgPattoLavoratore, Object prgStatoOccupaz, Object numKlo,
			RequestContainer requestContainer, TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[6];
		params[0] = null;
		params[1] = null;
		params[2] = prgStatoOccupaz;
		params[3] = numKlo;
		params[4] = requestContainer.getSessionContainer().getAttribute("_CDUT_");
		params[5] = prgPattoLavoratore;
		Boolean res = (Boolean) transExec.executeQuery("RIAPRI_PATTO", params, "UPDATE");
	}

	public static BigDecimal associaProrogheCollegate(SourceBean movCollegato, BigDecimal prgNuovoStatoOccupaz,
			TransactionQueryExecutor transExec) throws Exception {

		BigDecimal numklo = null;
		Object params[] = new Object[3];
		params[0] = prgNuovoStatoOccupaz;
		numklo = new BigDecimal(movCollegato.getAttribute(MovimentoBean.DB_NUM_K_LOCK).toString());
		numklo = numklo.add(new BigDecimal(1));
		params[1] = numklo;
		params[2] = new BigDecimal(movCollegato.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO).toString());
		Boolean res = (Boolean) transExec.executeQuery("RIASSOCIA_MOVIMENTO_PROROGA", params, "UPDATE");
		return numklo;
	}

	public static BigDecimal aggiornaDataFineMov(BigDecimal prgMov, String dataFine, BigDecimal user, BigDecimal numKlo,
			TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[5];
		params[0] = dataFine;
		params[1] = dataFine;
		params[2] = user;
		params[3] = numKlo;
		params[4] = prgMov;
		Boolean res = (Boolean) transExec.executeQuery("AGGIORNA_DATA_FINE_MOV", params, "UPDATE");
		return numKlo.add(new BigDecimal(1));
	}

	public static BigDecimal aggiornaDataFineMobilita(BigDecimal prgMobilita, String dataFine, String codMotivoFine,
			BigDecimal user, BigDecimal numKlo, TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[6];
		numKlo = numKlo.add(new BigDecimal("1"));
		params[0] = dataFine;
		params[1] = dataFine;
		params[2] = user;
		params[3] = numKlo;
		params[4] = codMotivoFine;
		params[5] = prgMobilita;
		Boolean res = (Boolean) transExec.executeQuery("AGGIORNA_DATA_FINE_MOBILITA", params, "UPDATE");
		return numKlo;
	}

	public static BigDecimal aggiornaMobilitaScorrimento(BigDecimal prgMobilita, String dataFine, String datMaxDiff,
			BigDecimal user, BigDecimal numKlo, TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[7];
		numKlo = numKlo.add(new BigDecimal("1"));
		params[0] = dataFine;
		params[1] = dataFine;
		params[2] = dataFine;
		params[3] = user;
		params[4] = numKlo;
		params[5] = datMaxDiff;
		params[6] = prgMobilita;
		Boolean res = (Boolean) transExec.executeQuery("AGGIORNA_MOBILITA_SCORRIMENTO", params, "UPDATE");
		return numKlo;
	}

	public static BigDecimal cancellaDecadenza(BigDecimal prgMobilita, BigDecimal user, BigDecimal numKlo,
			TransactionQueryExecutor transExec) throws Exception {
		Object params[] = new Object[3];
		numKlo = numKlo.add(new BigDecimal("1"));
		params[0] = user;
		params[1] = numKlo;
		params[2] = prgMobilita;
		Boolean res = (Boolean) transExec.executeQuery("CANCELLA_DECADENZA_TI", params, "UPDATE");
		return numKlo;
	}

	public static void aggiornaMovimentoIntermittente(SourceBean movimento, String dataFineSospensione, Object cdnUser,
			TransactionQueryExecutor transExec) throws Exception {
		Object newPrgSO = null;
		Object prg = movimento.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
		if (prg != null && !prg.toString().equals("-1")) {
			BigDecimal newNumKlo = (BigDecimal) movimento.getAttribute(MovimentoBean.DB_NUM_K_LOCK);
			newNumKlo = newNumKlo.add(new BigDecimal(1));
			Boolean res = null;
			Object params[] = null;
			String stmName = null;
			params = new Object[5];
			params[0] = newPrgSO;
			params[1] = dataFineSospensione;
			params[2] = newNumKlo;
			params[3] = cdnUser;
			params[4] = prg;
			stmName = "UPDATE_MOVIMENTO_PER_STATO_OCC_INTERMITTENTI";
			res = (Boolean) transExec.executeQuery(stmName, params, "UPDATE");
			if (!res.booleanValue())
				throw new Exception("Impossibile aggiornare il movimento  con prgMovimento = " + (BigDecimal) prg);
			movimento.updAttribute(MovimentoBean.DB_NUM_K_LOCK, newNumKlo);
		}
	}

}
