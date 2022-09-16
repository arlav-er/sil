package it.eng.sil.module.amministrazione.redditoAttivazione;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.util.amministrazione.impatti.DBLoad;
import it.eng.sil.util.amministrazione.impatti.MovimentoBean;
import it.eng.sil.util.amministrazione.impatti.StatoOccupazionaleBean;

public class DomandaBean {

	private String dataInizioPrestazione = null;
	private String dataFinePrestazione = null;
	private BigDecimal cdnLavoratore = null;
	private BigDecimal durataPrestazione = null;
	private LavoratoreBean lavoratore = null;
	private BigDecimal numkloRA = null;
	private TransactionQueryExecutor txExec = null;
	private String motivoVerifica = "";

	public DomandaBean(SourceBean domanda, TransactionQueryExecutor txExec) throws Exception {
		setDataInizioPrestazione(domanda.containsAttribute("datInizioPrestazione")
				? domanda.getAttribute("datInizioPrestazione").toString()
				: "");
		setDataFinePrestazione(
				domanda.containsAttribute("datFinePrestazione") ? domanda.getAttribute("datFinePrestazione").toString()
						: "");
		setCdnLavoratore(
				domanda.containsAttribute("cdnLavoratore") ? (BigDecimal) domanda.getAttribute("cdnLavoratore") : null);
		setDurataPrestazione(domanda.containsAttribute("NUMDURATAPRESTAZIONE")
				? (BigDecimal) domanda.getAttribute("NUMDURATAPRESTAZIONE")
				: null);
		setTransaction(txExec);
		setNumkloRA((BigDecimal) domanda.getAttribute("NUMKLORA"));
		setLavoratoreBean(new LavoratoreBean(getCdnLavoratore(), getTransaction()));
	}

	public void setDataInizioPrestazione(String value) {
		this.dataInizioPrestazione = value;
	}

	public String getDataInizioPrestazione() {
		return this.dataInizioPrestazione;
	}

	public void setDataFinePrestazione(String value) {
		this.dataFinePrestazione = value;
	}

	public String getDataFinePrestazione() {
		return this.dataFinePrestazione;
	}

	public void setCdnLavoratore(BigDecimal value) {
		this.cdnLavoratore = value;
	}

	public BigDecimal getCdnLavoratore() {
		return this.cdnLavoratore;
	}

	public void setNumkloRA(BigDecimal value) {
		this.numkloRA = value;
	}

	public BigDecimal getNumkloRA() {
		return this.numkloRA;
	}

	public void setLavoratoreBean(LavoratoreBean value) {
		this.lavoratore = value;
	}

	public LavoratoreBean getLavoratoreBean() {
		return this.lavoratore;
	}

	public void setTransaction(TransactionQueryExecutor value) {
		this.txExec = value;
	}

	public TransactionQueryExecutor getTransaction() {
		return this.txExec;
	}

	public void setDurataPrestazione(BigDecimal value) {
		this.durataPrestazione = value;
	}

	public BigDecimal getDurataPrestazione() {
		return this.durataPrestazione;
	}

	public void setMotivoVerifica(String value) {
		this.motivoVerifica = value;
	}

	public String getMotivoVerifica() {
		return this.motivoVerifica;
	}

	public String concediAutorizzazione() throws Exception {
		// Residenza
		String codStatoResidenza = isResidenteLavoratore();
		if (!codStatoResidenza.equalsIgnoreCase(Decodifica.Stato.AUTORIZZATO)) {
			return codStatoResidenza;
		}
		// Disoccupazione e competenza
		if (!isDisoccupato() || !isCompetente()) {
			return Decodifica.Stato.NON_AUTORIZZATO;
		}
		// Mobilita in deroga
		String codStatoMobilita = isInMobilitaDeroga();
		if (!codStatoMobilita.equalsIgnoreCase(Decodifica.Stato.AUTORIZZATO)) {
			return codStatoMobilita;
		}
		// Controllo durata prestazione
		if (getDurataPrestazione() == null
				|| getDurataPrestazione().compareTo(Decodifica.Costanti.CHECK_DURATA_MAX_PRESTAZIONE) > 0) {
			setMotivoVerifica(Decodifica.StatoVerifica.SUPERAMENTO_DURATA);
			return Decodifica.Stato.DA_VERIFICARE;
		}
		return Decodifica.Stato.AUTORIZZATO;
	}

	public boolean verificaDecadenza(String dataRif) throws Exception {
		// Tra la data di inizio della prestazione e la data della verifica esiste una comunicazione
		// obbligatoria o dichiarata/documentata dal lavoratore alla data della verifica.
		boolean decadeMovimento = decadenzaMovimento(dataRif);
		if (decadeMovimento) {
			return true;
		}
		// Tra la data inizio della prestazione RA e la data del controllo il beneficiario ha perso
		// lo stato di disoccupazione.
		boolean decadeStatoOccupazionale = decadenzaStatoOccupazionale(dataRif);
		if (decadeStatoOccupazionale) {
			return true;
		}
		// Il lavoratore non deve percepire una mobilità in deroga.
		boolean decadeMobDeroga = decadenzaMobilitaDeroga(dataRif);
		if (decadeMobDeroga) {
			return true;
		}
		return false;
	}

	private String isResidenteLavoratore() throws Exception {
		List dids = getLavoratoreBean().getDid();
		boolean didAperta = false;
		String dataDichiarazione = "";
		String dataInsDid = "";
		String dataInsDidTruncate = "";
		String comuneResidenza = "";
		for (int i = 0; (i < dids.size() && !didAperta); i++) {
			SourceBean did = (SourceBean) dids.get(i);
			String dataFineDid = did.containsAttribute("DATFINE") ? did.getAttribute("DATFINE").toString() : "";
			String dataInizioDid = did.containsAttribute("DATDICHIARAZIONE")
					? did.getAttribute("DATDICHIARAZIONE").toString()
					: "";
			if ((did.containsAttribute("CODSTATOATTO"))
					&& (did.getAttribute("codStatoAtto").toString().equalsIgnoreCase(Decodifica.StatoAtto.PROTOCOLLATO))
					&& (DateUtils.compare(dataInizioDid, getDataInizioPrestazione()) <= 0)
					&& (dataFineDid.equals("") || DateUtils.compare(dataFineDid, getDataInizioPrestazione()) >= 0)) {
				dataDichiarazione = did.containsAttribute("DATDICHIARAZIONE")
						? did.getAttribute("DATDICHIARAZIONE").toString()
						: "";
				dataInsDid = did.containsAttribute("dataInsDid") ? did.getAttribute("dataInsDid").toString() : "";
				dataInsDidTruncate = did.containsAttribute("dtmInsTrunc") ? did.getAttribute("dtmInsTrunc").toString()
						: "";
				didAperta = true;
			}
		}

		SourceBean infoLav = null;
		SourceBean infoLavLG = null;
		Vector rowsLg = null;
		boolean isResidente = false;

		if (didAperta) {
			infoLav = getLavoratoreBean().getInfoLav();
			// dtmInsLav = dd/mm/yyyy
			String dtmInsLav = infoLav.containsAttribute("DTMINS") ? infoLav.getAttribute("DTMINS").toString() : "";
			// dtmModLav = dd/mm/yyyy hh24:mi:ss
			String dtmModLav = infoLav.containsAttribute("DTMMOD") ? infoLav.getAttribute("DTMMOD").toString() : "";
			// dataModificaAnag = dd/mm/yyyy
			String dataModificaAnag = infoLav.containsAttribute("DATAMOD") ? infoLav.getAttribute("DATAMOD").toString()
					: "";
			// dataTrasferimento = dd/mm/yyyy
			String dataTrasferimento = infoLav.containsAttribute("DATTRASFERIMENTO")
					? infoLav.getAttribute("DATTRASFERIMENTO").toString()
					: "";

			if (!dataInsDidTruncate.equals("") && !dataDichiarazione.equals("") && !dataTrasferimento.equals("")
					&& DateUtils.compare(dataInsDidTruncate, dataDichiarazione) == 0
					&& DateUtils.compare(dataTrasferimento, dataDichiarazione) == 0) {
				setMotivoVerifica(Decodifica.StatoVerifica.RESIDENZA_TRASFERIMENTO);
				return Decodifica.Stato.DA_VERIFICARE;
			}

			if (!dtmInsLav.equals("") && !dataDichiarazione.equals("")
					&& DateUtils.compare(dtmInsLav, dataDichiarazione) <= 0) {
				// lavoratore presente in anagrafica alla data dichiarazione
				if (!dataModificaAnag.equals("") && DateUtils.compare(dataDichiarazione, dataModificaAnag) >= 0) {
					comuneResidenza = infoLav.containsAttribute("CODCOMRES")
							? infoLav.getAttribute("CODCOMRES").toString()
							: "";
				} else {
					rowsLg = getLavoratoreBean().getLGInfoLav(dataDichiarazione);
					if (rowsLg.size() > 0) {
						infoLavLG = (SourceBean) rowsLg.get(0);
						comuneResidenza = infoLavLG.containsAttribute("CODCOMRES")
								? infoLavLG.getAttribute("CODCOMRES").toString()
								: "";
					}
				}
			} else {
				if (!dtmInsLav.equals("") && !dataDichiarazione.equals("")
						&& DateUtils.compare(dtmInsLav, dataDichiarazione) > 0) {
					// lavoratore non presente in anagrafica alla data dichiarazione
					BigDecimal cdnUtIns = infoLav.containsAttribute("CDNUTINS")
							? (BigDecimal) infoLav.getAttribute("CDNUTINS")
							: null;
					if (cdnUtIns == null || cdnUtIns.compareTo(Decodifica.Costanti.CDNUT_PORTING) == 0) {
						setMotivoVerifica(Decodifica.StatoVerifica.RESIDENZA_PORTING);
						return Decodifica.Stato.DA_VERIFICARE;
					}
					Date dtmInsDid = DateUtils.getDate(dataInsDid);
					Date dtmModAnag = DateUtils.getDate(dtmModLav);
					if (DateUtils.compare(dtmInsDid, dtmModAnag) >= 0) {
						comuneResidenza = infoLav.containsAttribute("CODCOMRES")
								? infoLav.getAttribute("CODCOMRES").toString()
								: "";
					}
				}
			}

			//
			if (!comuneResidenza.equals("")) {
				isResidente = isResidenteProvincia(comuneResidenza);
				if (isResidente) {
					return Decodifica.Stato.AUTORIZZATO;
				} else {
					setMotivoVerifica(Decodifica.StatoVerifica.LAV_NON_RESIDENTE);
					return Decodifica.Stato.NON_AUTORIZZATO;
				}
			} else {
				setMotivoVerifica(Decodifica.StatoVerifica.RESIDENZA_DATI_ASSENTI);
				return Decodifica.Stato.DA_VERIFICARE;
			}
		} else {
			setMotivoVerifica(Decodifica.StatoVerifica.LAV_NON_297);
			return Decodifica.Stato.NON_AUTORIZZATO;
		}
	}

	private boolean isDisoccupato() throws Exception {
		List statiOccupazionali = getLavoratoreBean().getStatiOccupazionali();
		StatoOccupazionaleBean sOcc = DBLoad.getStatoOccupazionale(statiOccupazionali, getDataInizioPrestazione());
		if (sOcc.getStatoOccupazionale() == StatoOccupazionaleBean.A21
				|| sOcc.getStatoOccupazionale() == StatoOccupazionaleBean.A213) {
			return true;
		} else {
			setMotivoVerifica(Decodifica.StatoVerifica.LAV_NON_297);
			return false;
		}
	}

	private boolean isCompetente() throws Exception {
		Object[] inputParametersCpi = new Object[3];
		inputParametersCpi[0] = getCdnLavoratore();
		inputParametersCpi[1] = getDataInizioPrestazione();
		inputParametersCpi[2] = getDataInizioPrestazione();
		SourceBean cpiLav = (SourceBean) getTransaction().executeQuery("GET_CPI_AN_LAVORATORE_COMPETENTE_DATA",
				inputParametersCpi, "SELECT");
		if (cpiLav != null) {
			cpiLav = cpiLav.containsAttribute("ROW") ? (SourceBean) cpiLav.getAttribute("ROW") : cpiLav;
			String codTipoCpi = (String) cpiLav.getAttribute("codmonotipocpi");
			if (codTipoCpi != null && codTipoCpi.equalsIgnoreCase("C")) {
				return true;
			}
		}
		// in questo caso non si ha la competenza sul lavoratore
		setMotivoVerifica(Decodifica.StatoVerifica.LAV_NON_COMPETENZA);
		return false;
	}

	private String isInMobilitaDeroga() throws Exception {

		Object[] inputParameters = new Object[2];
		inputParameters[0] = getCdnLavoratore();
		inputParameters[1] = getDataInizioPrestazione();
		SourceBean row = null;
		row = (SourceBean) getTransaction().executeQuery("GET_RA_MOBILITA_IN_DEROGA", inputParameters, "SELECT");
		if (row != null) {
			Vector iscrizioni = row.getAttributeAsVector("ROW");
			if (iscrizioni.size() > 0) {
				setMotivoVerifica(Decodifica.StatoVerifica.LAV_ISCRITTO_MOB_DEROGA);
				return Decodifica.Stato.NON_AUTORIZZATO;
			} else {
				inputParameters = new Object[1];
				inputParameters[0] = getCdnLavoratore();
				row = (SourceBean) getTransaction().executeQuery("GET_DATA_NASCITA_LAVORATORE", inputParameters,
						"SELECT");
				if (row != null) {
					row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
					String dataNascitaLavoratore = row.containsAttribute("datnasc")
							? row.getAttribute("datnasc").toString()
							: "";
					BigDecimal anniLav = new BigDecimal(
							DateUtils.getEta(dataNascitaLavoratore, getDataInizioPrestazione()));
					if (anniLav.compareTo(Decodifica.Costanti.CHECK_ETA_MOB_DEROGA) < 0) {
						return Decodifica.Stato.AUTORIZZATO;
					} else {
						setMotivoVerifica(Decodifica.StatoVerifica.PERCETTORE_MOB_DEROGA);
						return Decodifica.Stato.DA_VERIFICARE;
					}

				} else {
					throw new Exception("Errore estrazione mobilità in deroga");
				}
			}
		} else {
			throw new Exception("Errore estrazione mobilità in deroga");
		}
	}

	private boolean decadenzaMobilitaDeroga(String dataRif) throws Exception {
		Object[] inputParameters = new Object[5];
		inputParameters[0] = getCdnLavoratore();
		inputParameters[1] = getDataInizioPrestazione();
		inputParameters[2] = dataRif;
		inputParameters[3] = getDataInizioPrestazione();
		inputParameters[4] = getDataInizioPrestazione();
		SourceBean row = null;
		row = (SourceBean) getTransaction().executeQuery("GET_DECADENZA_MOBILITA_IN_DEROGA_RA", inputParameters,
				"SELECT");
		if (row != null) {
			Vector iscrizioni = row.getAttributeAsVector("ROW");
			if (iscrizioni.size() > 0) {
				setMotivoVerifica(Decodifica.StatoVerifica.LAV_PERCEPISCE_MOB_DEROGA);
				return true;
			}
		} else {
			throw new Exception("Errore estrazione mobilità in deroga");
		}
		return false;
	}

	private boolean isResidenteProvincia(String codComune) throws Exception {
		Object[] inputParameters = new Object[1];
		inputParameters[0] = codComune;
		SourceBean row = (SourceBean) getTransaction().executeQuery("COMUNE_RESIDENZA_RA", inputParameters, "SELECT");
		if (row != null) {
			row = row.containsAttribute("ROW") ? (SourceBean) row.getAttribute("ROW") : row;
			String codCom = (String) row.getAttribute("CODCOM");
			if (codCom != null && !codCom.equals("")) {
				return true;
			}
		}
		return false;
	}

	private boolean decadenzaMovimento(String dataRif) throws Exception {
		List movimenti = getLavoratoreBean().getMovimenti();
		int numMovimenti = movimenti.size();
		for (int i = 0; i < numMovimenti; i++) {
			SourceBean mov = (SourceBean) movimenti.get(i);
			String codTipoMov = mov.containsAttribute("codtipomov") ? mov.getAttribute("codtipomov").toString() : "";
			if (!codTipoMov.equalsIgnoreCase(MovimentoBean.COD_CESSAZIONE)) {
				String dataInizioMov = mov.containsAttribute("datiniziomov")
						? mov.getAttribute("datiniziomov").toString()
						: "";
				String dataFineMov = mov.containsAttribute("datFineMovEffettiva")
						? mov.getAttribute("datFineMovEffettiva").toString()
						: "";
				if ((!dataInizioMov.equals("") && DateUtils.compare(dataInizioMov, getDataInizioPrestazione()) >= 0
						&& DateUtils.compare(dataInizioMov, dataRif) <= 0)
						|| (!dataInizioMov.equals("")
								&& DateUtils.compare(dataInizioMov, getDataInizioPrestazione()) < 0
								&& (dataFineMov.equals("")
										|| DateUtils.compare(dataFineMov, getDataInizioPrestazione()) >= 0))) {
					setMotivoVerifica(Decodifica.StatoVerifica.LAV_PERDITA_DISOCCUPAZIONE);
					return true;
				}
			}
		}
		return false;
	}

	private boolean decadenzaStatoOccupazionale(String dataRif) throws Exception {
		List statiOcc = getLavoratoreBean().getStatiOccupazionali();
		int numSOcc = statiOcc.size();
		for (int i = 0; i < numSOcc; i++) {
			SourceBean socc = (SourceBean) statiOcc.get(i);
			String dataInizio = socc.containsAttribute("datinizio") ? socc.getAttribute("datinizio").toString() : "";
			String dataFine = socc.containsAttribute("datfine") ? socc.getAttribute("datfine").toString() : "";
			if ((!dataInizio.equals("") && DateUtils.compare(dataInizio, getDataInizioPrestazione()) >= 0
					&& DateUtils.compare(dataInizio, dataRif) <= 0)
					|| (!dataInizio.equals("") && DateUtils.compare(dataInizio, getDataInizioPrestazione()) < 0
							&& (dataFine.equals("") || DateUtils.compare(dataFine, getDataInizioPrestazione()) >= 0))) {
				String codStatoOccupaz = socc.containsAttribute("codstatooccupaz")
						? socc.getAttribute("codstatooccupaz").toString()
						: "";
				if (!codStatoOccupaz.equalsIgnoreCase(Decodifica.Costanti.DISOCCUPATO)
						&& !codStatoOccupaz.equalsIgnoreCase(Decodifica.Costanti.ATTIVITA_SENZA_CONTRATTO)) {
					setMotivoVerifica(Decodifica.StatoVerifica.LAV_PERDITA_DISOCCUPAZIONE);
					return true;
				}
			}
		}
		return false;
	}

}
