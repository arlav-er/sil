package it.eng.myportal.dtos;

import java.util.Date;

import it.eng.myportal.entity.enums.CodStatoVacancyEnum;

public class OfferteMiniDTO extends AbstractUpdatablePkDTO {

	private static final long serialVersionUID = -2698965085713786797L;

	private String riferimento;
	private String attivita;
	private Date dtmIns;
	private Date dtScadenzaPubblicazione;
	private Integer idVaDatiVacancy;
	private boolean nonModificabile;
	private String codProvenienza;
	private CodStatoVacancyEnum statoVacancy;
	private Boolean flgIdo;
	private String cfOrRagSocAzienda;
	private String operatore;

	public OfferteMiniDTO() {
		super();
	}

	public String getRiferimento() {
		return riferimento;
	}

	public void setRiferimento(String riferimento) {
		this.riferimento = riferimento;
	}

	public String getAttivita() {
		return attivita;
	}

	public void setAttivita(String attivita) {
		this.attivita = attivita;
	}

	public Date getDtmIns() {
		return dtmIns;
	}

	public void setDtmIns(Date dtmIns) {
		this.dtmIns = dtmIns;
	}

	public Date getDtScadenzaPubblicazione() {
		return dtScadenzaPubblicazione;
	}

	public void setDtScadenzaPubblicazione(Date dtScadenzaPubblicazione) {
		this.dtScadenzaPubblicazione = dtScadenzaPubblicazione;
	}

	public Integer getIdVaDatiVacancy() {
		return this.idVaDatiVacancy;
	}

	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}

	public String getAttivitaPrincipaleEscape() {
		return attivita.replace("'", "\\'");
	}

	public boolean isNonModificabile() {
		return nonModificabile;
	}

	public void setNonModificabile(boolean nonModificabile) {
		this.nonModificabile = nonModificabile;
	}

	public CodStatoVacancyEnum getStatoVacancy() {
		return statoVacancy;
	}

	public void setStatoVacancy(CodStatoVacancyEnum statoVacancy) {
		this.statoVacancy = statoVacancy;
	}

	public boolean isInLavorazione() {
		return statoVacancy.equals(CodStatoVacancyEnum.LAV);
	}

	public boolean isInPubblica() {
		return statoVacancy.equals(CodStatoVacancyEnum.PUB);
	}
	
	public boolean isProvenienzaSil() {
		return "SIL".equals(codProvenienza);
	}

	public Boolean getFlgIdo() {
		return flgIdo;
	}

	public void setFlgIdo(Boolean flgIdo) {
		this.flgIdo = flgIdo;
	}

	public String getCodProvenienza() {
		return codProvenienza;
	}

	public void setCodProvenienza(String codProvenienza) {
		this.codProvenienza = codProvenienza;
	}

	public String getCfOrRagSocAzienda() {
		return cfOrRagSocAzienda;
	}

	public void setCfOrRagSocAzienda(String cfOrRagSocAzienda) {
		this.cfOrRagSocAzienda = cfOrRagSocAzienda;
	}

	public String getOperatore() {
		return operatore;
	}

	public void setOperatore(String operatore) {
		this.operatore = operatore;
	}

}
