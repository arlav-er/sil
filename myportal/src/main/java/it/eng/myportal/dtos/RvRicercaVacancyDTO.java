package it.eng.myportal.dtos;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.eng.myportal.utils.ConstantsSingleton;
import it.eng.myportal.utils.ConstantsSingleton.DeProvenienza;

/**
 *
 * @author
 *
 */
public class RvRicercaVacancyDTO implements Serializable {
	private static final long serialVersionUID = 2007159797890733781L;

	private Integer idVaDatiVacancy;

	private Integer idAziendaInfo;

	/* la descrizione della vacancy */
	private String descrizione;
	/* la ragione sociale dell'azienda associata alla vacancy */
	private String datoreLavoro;
	/* indirizzo della sede lavorativa */
	private String indirizzoLavoro;
	private String codComLavoro;
	private String strComLavoro;
	private String strProvinciaLavoro;
	/* mansione */
	private String codMansione;
	private String strMansione;
	/* tipo contratto */
	private String codTipoContratto;
	private String strTipoContratto;
	/* id della vetrina dell'azienda collegata */
	private Integer idSvAzienda;
	/* numero di candidature della vacancy */
	private int numCandidati;
	/* Questa variabile viene impostata a 'true' se l'utente ha già inviato un' autocandidatura */
	private boolean hasCandidatura;
	/* data modifica */
	private Date dataModifica;
	/* data pubblicazione */
	private Date dataPubblicaz;
	/* data scadenza pubblicazione */
	private Date dataScadPubblicaz;
	/* rappresentazione dei giorni di pubblicazione */
	private String pubblicato;
	/* nuova vacancy */
	private boolean newVacancy;

	private boolean fromClicLavoro;
	private boolean flgCandidatura;

	/* variabile a true se la vacancy è sincronizzata con clic lavoro */
	private boolean flagInvioCl;
	
	//flag ido, feb 2020
	private boolean flagIDO;

	/* codice provenienza della vacancy */
	private String provenienza;

	public boolean equals(Object o) {
		if (!(o instanceof RvRicercaVacancyDTO))
			return false;
		Integer otherIdDatiVacancy = ((RvRicercaVacancyDTO) o).getIdVaDatiVacancy();
		if (otherIdDatiVacancy != null && this.getIdVaDatiVacancy() != null)
			return otherIdDatiVacancy.intValue() == this.getIdVaDatiVacancy().intValue();
		else
			return false;
	}

	public int hashCode() {
		if (this.getIdVaDatiVacancy() != null)
			return this.getIdVaDatiVacancy() % ConstantsSingleton.DEFAULT_BUCKET_SIZE;
		else
			return 0;
	}

	public String toString() {
		return String.format("RvVisualizzaRicercaVacancyDTO "
				+ "[idVaDatiVacancy=%s, descrizione=%s, datoreLavoro=%s, "
				+ "indirizzoLavoro=%s, codComLavoro=%s, strComLavoro=%s, "
				+ "codTipoContratto=%s, strTipoContratto=%s, hasCandidatura=%s]", idVaDatiVacancy, descrizione,
				datoreLavoro, indirizzoLavoro, codComLavoro, strComLavoro, codTipoContratto, strTipoContratto,
				hasCandidatura);
	}

	public Integer getIdVaDatiVacancy() {
		return idVaDatiVacancy;
	}

	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getDatoreLavoro() {
		return datoreLavoro;
	}

	public void setDatoreLavoro(String datoreLavoro) {
		this.datoreLavoro = datoreLavoro;
	}

	public String getIndirizzoLavoro() {
		return indirizzoLavoro;
	}

	public void setIndirizzoLavoro(String indirizzoLavoro) {
		this.indirizzoLavoro = indirizzoLavoro;
	}

	public String getCodComLavoro() {
		return codComLavoro;
	}

	public void setCodComLavoro(String codComLavoro) {
		this.codComLavoro = codComLavoro;
	}

	public String getStrComLavoro() {
		return strComLavoro;
	}

	public void setStrComLavoro(String strComLavoro) {
		this.strComLavoro = strComLavoro;
	}

	public String getStrProvinciaLavoro() {
		return strProvinciaLavoro;
	}

	public void setStrProvinciaLavoro(String strProvinciaLavoro) {
		this.strProvinciaLavoro = strProvinciaLavoro;
	}

	public String getCodMansione() {
		return codMansione;
	}

	public void setCodMansione(String codMansione) {
		this.codMansione = codMansione;
	}

	public String getStrMansione() {
		return strMansione;
	}

	public void setStrMansione(String strMansione) {
		this.strMansione = strMansione;
	}

	public String getCodTipoContratto() {
		return codTipoContratto;
	}

	public void setCodTipoContratto(String codTipoContratto) {
		this.codTipoContratto = codTipoContratto;
	}

	public String getStrTipoContratto() {
		return strTipoContratto;
	}

	public void setStrTipoContratto(String strTipoContratto) {
		this.strTipoContratto = strTipoContratto;
	}

	public Integer getIdSvAzienda() {
		return idSvAzienda;
	}

	public void setIdSvAzienda(Integer idSvAzienda) {
		this.idSvAzienda = idSvAzienda;
	}

	public boolean isHasCandidatura() {
		return hasCandidatura;
	}

	public void setHasCandidatura(boolean hasCandidatura) {
		this.hasCandidatura = hasCandidatura;
	}

	public int getNumCandidati() {
		return numCandidati;
	}

	public void setNumCandidati(int numCandidati) {
		this.numCandidati = numCandidati;
	}

	public Date getDataModifica() {
		return dataModifica;
	}

	public void setDataModifica(Date dataModifica) {
		this.dataModifica = dataModifica;
	}

	public Date getDataPubblicaz() {
		return dataPubblicaz;
	}

	public void setDataPubblicaz(Date dataPubblicaz) {
		this.dataPubblicaz = dataPubblicaz;
	}

	public Date getDataScadPubblicaz() {
		return dataScadPubblicaz;
	}

	public void setDataScadPubblicaz(Date dataScadPubblicaz) {
		this.dataScadPubblicaz = dataScadPubblicaz;
	}

	public boolean isNewVacancy() {
		return newVacancy;
	}

	public void setNewVacancy(boolean newVacancy) {
		this.newVacancy = newVacancy;
	}

	public String descrizioneLenght(int lenght) {
		if (this.descrizione != null && this.descrizione.length() > lenght) {
			this.descrizione = this.descrizione.substring(0, lenght);
		}
		return descrizione;
	}

	public String getPubblicato() {
		Date now = new Date();
		long diff = now.getTime() - dataModifica.getTime();
		long ggDiff = (diff / 86400000);

		if (ggDiff <= 0) {
			pubblicato = "oggi";
		} else if (ggDiff == 1) {
			pubblicato = ggDiff + " giorno fa";
		} else {
			pubblicato = ggDiff + " giorni fa";
		}
		return pubblicato;
	}

	public String getDataScadenzaString() {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(dataScadPubblicaz);
	}

	public void setPubblicato(String pubblicato) {
		this.pubblicato = pubblicato;
	}

	/**
	 * @return the idAziendaInfo
	 */
	public Integer getIdAziendaInfo() {
		return idAziendaInfo;
	}

	/**
	 * @param idAziendaInfo
	 *            the idAziendaInfo to set
	 */
	public void setIdAziendaInfo(Integer idAziendaInfo) {
		this.idAziendaInfo = idAziendaInfo;
	}

	public boolean isFlagInvioCl() {
		return flagInvioCl;
	}

	public void setFlagInvioCl(boolean flagInvioCl) {
		this.flagInvioCl = flagInvioCl;
	}

	public String getProvenienza() {
		return provenienza;
	}

	public void setProvenienza(String provenienza) {
		this.provenienza = provenienza;
	}

	/**
	 * Determina se la vacancy proviene da clic lavoro o meno
	 * 
	 * @return
	 */
	public boolean isFromClicLavoro() {
		return DeProvenienza.COD_MINISTERO.equals(provenienza);
	}

	public boolean isFlgCandidatura() {
		return flgCandidatura;
	}

	public void setFlgCandidatura(boolean flgCandidatura) {
		this.flgCandidatura = flgCandidatura;
	}

	public boolean isFlagIdo() {
		return flagIDO;
	}

	public void setFlagIdo(boolean flagIDO) {
		this.flagIDO = flagIDO;
	}

}
