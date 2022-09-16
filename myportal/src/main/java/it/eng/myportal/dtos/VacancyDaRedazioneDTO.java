package it.eng.myportal.dtos;

import java.util.Date;

import it.eng.myportal.entity.enums.CodStatoVacancyEnum;

public class VacancyDaRedazioneDTO {

//	private String numRichiesta;
//	private String numAnno;
	private Integer idVaDatiVacancy;
	private String riferimento; //Così formato da campi vaDatiVacancy: numRichiesta / numAnno - provenienza
	private Date dtPubblicazione;
	private String nomeAzienda;
	private CodStatoVacancyEnum codStatoVacancyEnum;
	
	public Integer getIdVaDatiVacancy() {
		return idVaDatiVacancy;
	}
	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}
	public String getRiferimento() {
		return riferimento;
	}
	public void setRiferimento(String riferimento) {
		this.riferimento = riferimento;
	}
	public Date getDtPubblicazione() {
		return dtPubblicazione;
	}
	public void setDtPubblicazione(Date dtPubblicazione) {
		this.dtPubblicazione = dtPubblicazione;
	}
	public String getNomeAzienda() {
		return nomeAzienda;
	}
	public void setNomeAzienda(String nomeAzienda) {
		this.nomeAzienda = nomeAzienda;
	}
	public CodStatoVacancyEnum getCodStatoVacancyEnum() {
		return codStatoVacancyEnum;
	}
	public void setCodStatoVacancyEnum(CodStatoVacancyEnum codStatoVacancyEnum) {
		this.codStatoVacancyEnum = codStatoVacancyEnum;
	}
	
}
