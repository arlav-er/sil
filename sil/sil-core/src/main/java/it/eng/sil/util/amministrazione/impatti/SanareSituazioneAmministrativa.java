/*
 * Creato il 13-ottobre-04
 *
 */
package it.eng.sil.util.amministrazione.impatti;

import java.math.BigDecimal;
import java.util.Vector;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;

/**
 * @author savino
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class SanareSituazioneAmministrativa {

	private TransactionQueryExecutor txExec = null;
	private RequestContainer requestContainer = null;
	private SourceBean response = null;
	private SourceBean request = null;
	private SourceBean[] movimentiRicostruiti = null;
	private SourceBean movimentoIniziale = null;
	public static final String FLAG_SOLO_DETTAGLIO = "FLAG_SOLO_DETTTAGLIO";
	public static final String FLAG_LIMITE_SUP = "FLAG_LIMITE_SUP";
	public static final String FLAG_LIMITE_INF = "FLAG_LIMITE_INF";

	public int codiceErrore = 0;
	public String descErrore = null;

	public SanareSituazioneAmministrativa(RequestContainer requestContainer, SourceBean response) throws Exception {
		this.requestContainer = requestContainer;
		this.response = response;
		this.request = requestContainer.getServiceRequest();
		this.movimentiRicostruiti = ricostruisciMovimenti();
	}

	public SourceBean[] getMovimenti() {
		return this.movimentiRicostruiti;
	}

	/**
	 * Crea un array di SourceBean a partire dalla request, ovvero dai dati passati dalla jsp relativi ai movimenti che
	 * debbono essere sanati (o meno).
	 * 
	 * @return
	 * @throws Exception
	 */
	public SourceBean[] ricostruisciMovimenti() throws Exception {
		String numeroMovimenti = (String) request.getAttribute("numeroMovimenti");
		int n = Integer.parseInt(numeroMovimenti);
		SourceBean row = null;
		Vector v = new Vector(n);
		BigDecimal cdnLavoratore = new BigDecimal((String) request.getAttribute("cdnLavoratore"));
		for (int i = 0; i < n; i++) {
			row = new SourceBean("ROW");
			String dataInizio = (String) request.getAttribute("dataInizio_" + i);
			String dataFine = (String) request.getAttribute("dataFine_" + i);
			String numKloMov = (String) request.getAttribute("numKloMov_" + i);
			String retribuzione = (String) request.getAttribute("retribuzione_" + i);
			String retribuzioneSanata = (String) request.getAttribute("retribuzioneSanata_" + i);
			String prgMovimento = (String) request.getAttribute("prgMovimento_" + i);
			String prgStatoOccupaz = (String) request.getAttribute("prgStatoOccupaz_" + i);
			String codTipoMov = (String) request.getAttribute("CODTIPOMOV_" + i);
			String codTipoAss = (String) request.getAttribute("CODTIPOASS_" + i);
			String codMonoTempo = (String) request.getAttribute("codMonoTempo_" + i);
			String flagSoloGenerica = (String) request.getAttribute("solo_dettaglio_" + i);
			String prgMovimentoSucc = (String) request.getAttribute("prgMovimentoSucc_" + i);

			if (dataInizio != null && dataInizio.length() > 0)
				row.setAttribute(MovimentoBean.DB_DATA_INIZIO, dataInizio);
			if (dataFine != null && dataFine.length() > 0)
				row.setAttribute(MovimentoBean.DB_DATA_FINE, dataFine);
			if (numKloMov != null && numKloMov.length() > 0)
				row.setAttribute(MovimentoBean.DB_NUM_K_LOCK, new BigDecimal(numKloMov));
			if (retribuzione != null && retribuzione.length() > 0)
				row.setAttribute(MovimentoBean.DB_RETRIBUZIONE, new BigDecimal(retribuzione));
			if (retribuzioneSanata != null && retribuzioneSanata.length() > 0) {
				retribuzioneSanata = retribuzioneSanata.replace(",", ".");
				row.setAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA, new BigDecimal(retribuzioneSanata));
			}
			if (prgMovimento != null && prgMovimento.length() > 0)
				row.setAttribute(MovimentoBean.DB_PRG_MOVIMENTO, new BigDecimal(prgMovimento));
			if (prgStatoOccupaz != null && prgStatoOccupaz.length() > 0)
				row.setAttribute(MovimentoBean.DB_PRG_STATO_OCCUPAZ, new BigDecimal(prgStatoOccupaz));
			if (codTipoMov != null && codTipoMov.length() > 0)
				row.setAttribute(MovimentoBean.DB_COD_MOVIMENTO, codTipoMov);
			if (codTipoAss != null && codTipoAss.length() > 0)
				row.setAttribute(MovimentoBean.DB_COD_ASSUNZIONE, codTipoAss);
			if (codMonoTempo != null && codMonoTempo.length() > 0)
				row.setAttribute(MovimentoBean.DB_COD_MONO_TEMPO, codMonoTempo);
			if (cdnLavoratore != null)
				row.setAttribute(MovimentoBean.DB_CDNLAVORATORE, cdnLavoratore);
			if (flagSoloGenerica != null)
				row.setAttribute(FLAG_SOLO_DETTAGLIO, "1");
			if (prgMovimentoSucc != null && prgMovimentoSucc.length() > 0)
				row.setAttribute(MovimentoBean.DB_PRG_MOVIMENTO_SUCC, new BigDecimal(prgMovimentoSucc));

			v.add(row);
		}
		return (SourceBean[]) v.toArray(new SourceBean[0]);
	}

	/**
	 * Aggiunge alle informazioni prelevate dal db quelle pervenute dalla jsp.
	 * 
	 * @param movS
	 *            i movimenti con le informazioni pervenute dalla jsp
	 * @param movAgg
	 *            i movimenti letti da db
	 * 
	 * @return i movimenti letti da db aggiornati con le informazioni provenienti dalla jsp, le informazioni necessarie
	 *         per sanare la situazione amministrativa del lavoratore.
	 * @throws Exception
	 */
	public Vector preparaMovimenti(SourceBean[] movS, Vector movAgg, SourceBean did) throws Exception {
		SourceBean request = RequestContainer.getRequestContainer().getServiceRequest();
		String limite = (String) request.getAttribute("limite");
		String tipoDichiarazione = (String) request.getAttribute("tipoDichiarazione");
		String o = (String) request.getAttribute("checkSelezionato");
		int checkSelezionato = Integer.parseInt(o);
		boolean superamentoLimite = limite.equals("sup");
		boolean dichiarazioneDett = tipoDichiarazione.equals("dett");
		String codTipoDich = (dichiarazioneDett ? "DD" : "DG") + (superamentoLimite ? "RS" : "RN");

		for (int i = 0; i < movS.length; i++) {
			BigDecimal prg = (BigDecimal) movS[i].getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
			BigDecimal retribuzione = (BigDecimal) movS[i].getAttribute(MovimentoBean.DB_RETRIBUZIONE);
			BigDecimal retribuzioneSanata = (BigDecimal) movS[i].getAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA);

			SourceBean mov = cercaMovimento(prg, movAgg);

			// aggiungere queste informazioni ad ogni movimento interessato
			// quindi se la dich e' generica tutti i movimenti a partire da
			// quello ceccato avranno le stesse
			// info, se di dettaglio allora solo i movimenti che hanno la
			// decretribuzionemen e la
			// decretribuzionemensanata diverse dovranno essere aggiornati con
			// queste info.
			// questo permettera' in fase di update del movimento di aggiornare
			// i campi relativi alla op. di
			// sanatoria ed eventualmente di eseguire delle scelte in fase di
			// calcolo reddito
			if (movS[i].containsAttribute(MovimentoBean.FLAG_OP_SANARE)) {
				if (dichiarazioneDett) {
					if (superamentoLimite) {
						if (movimentoIniziale == null)
							movimentoIniziale = mov;
					}
				}
				if (inPeriodoDid(mov, did)) {
					mov.setAttribute(MovimentoBean.FLAG_OP_SANARE, movS[i].getAttribute(MovimentoBean.FLAG_OP_SANARE));
					mov.setAttribute(MovimentoBean.FLAG_OP_SANARE_DETTAGLIO,
							movS[i].getAttribute(MovimentoBean.FLAG_OP_SANARE_DETTAGLIO));
					mov.setAttribute(MovimentoBean.FLAG_OP_SANARE_SUPERAMENTO,
							movS[i].getAttribute(MovimentoBean.FLAG_OP_SANARE_SUPERAMENTO));
				} else {
					mov.setAttribute(MovimentoBean.FLAG_OP_SANARE_NON_GESTIRE, "1");
				}

			}
		}
		return movAgg;
	}

	public SourceBean[] preparaMovimenti(SourceBean[] movS) throws Exception {
		SourceBean request = RequestContainer.getRequestContainer().getServiceRequest();
		String limite = (String) request.getAttribute("limite");
		String tipoDichiarazione = (String) request.getAttribute("tipoDichiarazione");
		String o = (String) request.getAttribute("checkSelezionato");
		String viewTuttiMovimenti = (String) request.getAttribute("viewTuttiMovimenti");
		int checkSelezionato = 0;
		int prgMovimentoDid = 0;
		int posDid = -1;
		try {
			checkSelezionato = Integer.parseInt(o);
		} catch (Exception e) {
			request.updAttribute("checkSelezionato", "0");
		}
		o = (String) request.getAttribute("PRGMOVINIZIODID");
		try {
			prgMovimentoDid = Integer.parseInt(o);
		} catch (Exception e) {
		}
		boolean superamentoLimite = limite.equals("sup");
		boolean dichiarazioneDett = tipoDichiarazione.equals("dett");
		String codTipoDich = (dichiarazioneDett ? "DD" : "DG") + (superamentoLimite ? "RS" : "RN");
		String dataSitSanata = (String) request.getAttribute("datSitSanata");
		// Data inizio del movimento che si interseca con la did
		String dataInizioMovimentoDid = request.containsAttribute("DATAINIZIOMOVIMENTODID")
				? request.getAttribute("DATAINIZIOMOVIMENTODID").toString()
				: "";
		boolean movimentoDidRaggiunto = false;

		/*
		 * Decreto 05/11/2019 SourceBean recuperaMovimentoBean = null; CalcoloRetribuzione calcRet = null; String
		 * codorario = null; String codccnl = null; String numOreSett = null; String numLivello = null;
		 */

		for (int i = 0; i < movS.length; i++) {
			BigDecimal prg = (BigDecimal) movS[i].getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
			BigDecimal retribuzione = (BigDecimal) movS[i].getAttribute(MovimentoBean.DB_RETRIBUZIONE);
			BigDecimal retribuzioneSanata = (BigDecimal) movS[i].getAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA);
			SourceBean mov = movS[i];
			String dataInizioMov = mov.getAttribute(MovimentoBean.DB_DATA_INIZIO).toString();
			if ((prgMovimentoDid == prg.intValue() || (!dataInizioMovimentoDid.equals("")
					&& DateUtils.compare(dataInizioMov, dataInizioMovimentoDid) >= 0)) && (!movimentoDidRaggiunto)) {
				movimentoDidRaggiunto = true;
				posDid = i;
			}

			// Decreto 05/11/2019
			/*
			 * if(getCodiceErrore() == 0) {
			 * 
			 * recuperaMovimentoBean = MovimentoBean.recuperaMovimento(prg); //Vector rows =
			 * recuperaMovimentoBean.getAttributeAsVector("ROW"); SourceBean response = new
			 * SourceBean("SERVICE_RESPONSE"); if(recuperaMovimentoBean != null) { // se il prgmovimento è presente
			 * 
			 * codorario = recuperaMovimentoBean.containsAttribute("ROW.CODORARIO") ?
			 * recuperaMovimentoBean.getAttribute("ROW.CODORARIO").toString() : null; codccnl =
			 * recuperaMovimentoBean.containsAttribute("ROW.CODCCNL") ?
			 * (recuperaMovimentoBean.getAttribute("ROW.CODCCNL").toString()) : null;
			 * 
			 * numOreSett = recuperaMovimentoBean.containsAttribute("ROW.NUMORESETT") ?
			 * (recuperaMovimentoBean.getAttribute("ROW.NUMORESETT").toString()) : null; numLivello =
			 * recuperaMovimentoBean.containsAttribute("ROW.NUMLIVELLO") ?
			 * (recuperaMovimentoBean.getAttribute("ROW.NUMLIVELLO").toString()) : null;
			 * 
			 * calcRet = new CalcoloRetribuzione(codorario,numOreSett,codccnl,numLivello);
			 * if(calcRet.checkCalcoloRetribuzione()) { // se true trattasi di nuovo codiceccnl - fare il calcolo e
			 * verificare calcRet.calcoloCompensoRetribuzione(response);
			 * 
			 * String esito = null; String retribuzioneCalcolata = null; BigDecimal retribuzioneNumber = null;
			 * 
			 * if(response.getAttribute("ESITO") != null ) { esito = response.getAttribute("ESITO").toString();
			 * 
			 * if(esito.equalsIgnoreCase("KO")) { this.codiceErrore =
			 * MessageCodes.ControlliMovimentiDecreto.ERR_COMPENSO_RETRIBUZIONE_SANATORIA; this.descErrore =
			 * dataInizioMov;
			 * 
			 * }else { // se OK if(response.getAttribute("RETRIBUZIONE") != null &&
			 * !(response.getAttribute("RETRIBUZIONE").toString()).isEmpty()) { retribuzioneCalcolata =
			 * response.getAttribute("RETRIBUZIONE").toString(); retribuzioneNumber = BigDecimal.valueOf(new
			 * Double(retribuzioneCalcolata));
			 * 
			 * if(retribuzioneSanata == null || (retribuzioneSanata != null && (retribuzioneSanata.multiply(new
			 * BigDecimal("12"))).compareTo(retribuzioneNumber) < 0)) { this.codiceErrore =
			 * MessageCodes.ControlliMovimentiDecreto.ERR_COMPENSO_RETRIBUZIONE_SANATORIA_CALCOLO; this.descErrore =
			 * dataInizioMov; } } } }else { this.codiceErrore =
			 * MessageCodes.ControlliMovimentiDecreto.ERR_COMPENSO_RETRIBUZIONE_SANATORIA; this.descErrore =
			 * dataInizioMov; } }
			 * 
			 * } }
			 */
			///////////////////////////////////////////////////////

			// aggiungere queste informazioni ad ogni movimento interessato
			// quindi se la dich e' generica tutti i movimenti a partire da
			// quello ceccato avranno le stesse
			// info, se di dettaglio allora solo i movimenti che hanno la
			// decretribuzionemen e la
			// decretribuzionemensanata diverse dovranno essere aggiornati con
			// queste info.
			// questo permettera' in fase di update del movimento di aggiornare
			// i campi relativi alla op. di
			// sanatoria ed eventualmente di eseguire delle scelte in fase di
			// calcolo reddito
			if (dichiarazioneDett) {
				if (superamentoLimite) {
					// superamento limite
					if (viewTuttiMovimenti.equals("1") || movimentoDidRaggiunto) {
						if (mov.getAttribute(MovimentoBean.DB_DATA_SIT_SANATA) == null) {
							if (movimentoIniziale == null)
								movimentoIniziale = mov;
							if (retribuzioneSanata != null)
								mov.updAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA, retribuzioneSanata);
							mov.setAttribute(MovimentoBean.FLAG_OP_SANARE, "S");
							mov.setAttribute(MovimentoBean.FLAG_OP_SANARE_DETTAGLIO, "S");
							mov.setAttribute(MovimentoBean.FLAG_OP_SANARE_SUPERAMENTO, "S");
							mov.setAttribute(MovimentoBean.DB_COD_TIPO_DICH, codTipoDich);
							mov.setAttribute(MovimentoBean.DB_DATA_SIT_SANATA, dataSitSanata);
						}
					}
				} else {
					// Mancato superamento
					if (viewTuttiMovimenti.equals("1") || movimentoDidRaggiunto) {
						if (mov.getAttribute(MovimentoBean.DB_DATA_SIT_SANATA) == null) {
							if (movimentoIniziale == null)
								movimentoIniziale = mov;
							if (retribuzioneSanata != null)
								mov.updAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA, retribuzioneSanata);
							mov.setAttribute(MovimentoBean.FLAG_OP_SANARE, "S");
							mov.setAttribute(MovimentoBean.FLAG_OP_SANARE_DETTAGLIO, "S");
							mov.setAttribute(MovimentoBean.FLAG_OP_SANARE_SUPERAMENTO, "N");
							mov.setAttribute(MovimentoBean.DB_COD_TIPO_DICH, codTipoDich);
							mov.setAttribute(MovimentoBean.DB_DATA_SIT_SANATA, dataSitSanata);
						}
					}
				}
			} else { // dichiarazione generica
				if (superamentoLimite) {
					if (i >= checkSelezionato) {
						if (mov.getAttribute(MovimentoBean.DB_DATA_SIT_SANATA) == null
								&& mov.getAttribute(FLAG_SOLO_DETTAGLIO) == null) {
							if (movimentoIniziale == null)
								movimentoIniziale = mov;
							mov.setAttribute(MovimentoBean.FLAG_OP_SANARE, "S");
							mov.setAttribute(MovimentoBean.FLAG_OP_SANARE_DETTAGLIO, "N");
							mov.setAttribute(MovimentoBean.FLAG_OP_SANARE_SUPERAMENTO, "S");
							mov.setAttribute(MovimentoBean.DB_COD_TIPO_DICH, codTipoDich);
							mov.setAttribute(MovimentoBean.DB_DATA_SIT_SANATA, dataSitSanata);
						}
					} else {
						if (movimentoDidRaggiunto) {
							if (mov.getAttribute(MovimentoBean.DB_DATA_SIT_SANATA) == null
									&& mov.getAttribute(FLAG_SOLO_DETTAGLIO) == null) {
								if (movimentoIniziale == null)
									movimentoIniziale = mov;
								mov.updAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA, new BigDecimal(0));
								mov.setAttribute(MovimentoBean.FLAG_OP_SANARE, "S");
								mov.setAttribute(MovimentoBean.FLAG_OP_SANARE_DETTAGLIO, "N");
								mov.setAttribute(MovimentoBean.FLAG_OP_SANARE_SUPERAMENTO, "N");
								mov.setAttribute(MovimentoBean.DB_COD_TIPO_DICH, codTipoDich);
								mov.setAttribute(MovimentoBean.DB_DATA_SIT_SANATA, dataSitSanata);
							}
						}
					}
				} else {
					//
					if (i >= checkSelezionato || movimentoDidRaggiunto) {
						if (mov.getAttribute(MovimentoBean.DB_DATA_SIT_SANATA) == null
								&& mov.getAttribute(FLAG_SOLO_DETTAGLIO) == null) {
							if (movimentoIniziale == null)
								movimentoIniziale = mov;
							mov.updAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA, new BigDecimal(0));
							mov.setAttribute(MovimentoBean.FLAG_OP_SANARE, "S");
							mov.setAttribute(MovimentoBean.FLAG_OP_SANARE_DETTAGLIO, "N");
							mov.setAttribute(MovimentoBean.FLAG_OP_SANARE_SUPERAMENTO, "N");
							mov.setAttribute(MovimentoBean.DB_COD_TIPO_DICH, codTipoDich);
							mov.setAttribute(MovimentoBean.DB_DATA_SIT_SANATA, dataSitSanata);
						}
					}
				}
			}
		}
		// verifico se eventuali movimenti precedenti alla did devono essere
		// coinvolti nella dichiarazione
		// di superamento limite di dettaglio (devono essere coinvolti quando
		// anno un movimento successivo
		// che si interseca con la did)
		if (!dichiarazioneDett && superamentoLimite && posDid > 0) {
			BigDecimal prgMovSucc = null;
			SourceBean movApp = null;
			BigDecimal prg = null;
			SourceBean mov = null;
			boolean bSanaPrecDid = false;
			boolean bTrovatoMovSucc = false;
			for (int i = 0; i < posDid; i++) {
				mov = (SourceBean) movS[i];
				prgMovSucc = (BigDecimal) mov.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_SUCC);
				bSanaPrecDid = false;
				while (prgMovSucc != null) {
					bTrovatoMovSucc = false;
					// bisogna vedere se tra i movimenti successivi c'è
					// intersezione con la did
					for (int j = i + 1; j < movS.length; j++) {
						movApp = (SourceBean) movS[j];
						prg = (BigDecimal) movApp.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
						if (prgMovSucc.equals(prg)) {
							bTrovatoMovSucc = true;
							if (j >= posDid) {
								bSanaPrecDid = true;
							} else {
								prgMovSucc = (BigDecimal) movApp.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO_SUCC);
							}
							break;
						}
					}
					if (!bTrovatoMovSucc || bSanaPrecDid) {
						prgMovSucc = null;
					}
				}

				if (!mov.containsAttribute(MovimentoBean.FLAG_OP_SANARE) && bSanaPrecDid) {
					mov.updAttribute(MovimentoBean.DB_RETRIBUZIONE_SANATA, new BigDecimal(0));
					mov.setAttribute(MovimentoBean.FLAG_OP_SANARE, "S");
					mov.setAttribute(MovimentoBean.FLAG_OP_SANARE_DETTAGLIO, "N");
					mov.setAttribute(MovimentoBean.FLAG_OP_SANARE_SUPERAMENTO, "N");
					mov.setAttribute(MovimentoBean.DB_COD_TIPO_DICH, codTipoDich);
					mov.setAttribute(MovimentoBean.DB_DATA_SIT_SANATA, dataSitSanata);
				}
			}
		}
		return movS;
	}

	/**
	 * restituisce il movimento letto da db corrispondente al movimento pervenuto dalla jsp contenente le informazioni
	 * per sanare la situazione amministrativa del lavoratore.
	 * 
	 * @param prg
	 * @param movAgg
	 * @return
	 * @throws Exception
	 */
	public SourceBean cercaMovimento(BigDecimal prg, Vector movAgg) throws Exception {
		SourceBean mov = null;
		for (int j = 0; j < movAgg.size(); j++) {
			SourceBean m = (SourceBean) movAgg.get(j);
			BigDecimal prgM = (BigDecimal) m.getAttribute(MovimentoBean.DB_PRG_MOVIMENTO);
			if (prg.longValue() == prgM.longValue()) {
				mov = m;
				break;
			}
		}
		return mov;
	}

	public SourceBean getMovimentoIniziale(Vector rows) throws Exception {
		SourceBean ret = null;
		String tipo = (String) RequestContainer.getRequestContainer().getServiceRequest()
				.getAttribute("tipoDichiarazione");
		if (tipo.equals("dett"))
			ret = movimentoIniziale;
		else {
			int checkSel = Integer.parseInt((String) RequestContainer.getRequestContainer().getServiceRequest()
					.getAttribute("checkSelezionato"));
			ret = cercaMovimento(
					(BigDecimal) movimentiRicostruiti[checkSel].getAttribute(MovimentoBean.DB_PRG_MOVIMENTO), rows);
		}
		return ret;
	}

	public SourceBean getMovimentoIniziale() throws Exception {
		return movimentoIniziale;
	}

	private boolean inPeriodoDid(SourceBean mov, SourceBean did) throws Exception {
		boolean ret = false;
		if (did == null)
			ret = true;
		else {
			String dataDichiarazione = (String) did.getAttribute("datDichiarazione");
			String dataFineDid = (String) did.getAttribute("datFine");
			String dataInizioMov = (String) mov.getAttribute("datInizioMov");
			String dataFineMov = (String) mov.getAttribute("datFineMovEffettiva");
			ret = DateUtils.compare(dataDichiarazione, dataInizioMov) >= 0 && (dataFineMov == null
					|| dataFineMov.trim().equals("") || DateUtils.compare(dataDichiarazione, dataFineMov) <= 0);
			if (!ret)
				ret = DateUtils.compare(dataDichiarazione, dataInizioMov) <= 0;
		}
		return ret;
	}

	public int getCodiceErrore() {
		return codiceErrore;
	}

	public void setCodiceErrore(int codiceErrore) {
		this.codiceErrore = codiceErrore;
	}

	public String getDescErrore() {
		return descErrore;
	}

	public void setDescErrore(String descErrore) {
		this.descErrore = descErrore;
	}
}
