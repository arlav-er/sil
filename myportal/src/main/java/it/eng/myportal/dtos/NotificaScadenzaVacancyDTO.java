package it.eng.myportal.dtos;

import java.util.Date;

public class NotificaScadenzaVacancyDTO implements IDTO{

	private static final long serialVersionUID = -5749199776570443041L;
	
	private Integer idVaDatiVacancy;
	private Integer numAnno;
	private Integer numRichiesta;
	private String codProvenienzaVacancy;
	private String attivitaPrincipale;
	private Date dtScadenzaPubblicazione;
	private Date dtPubblicazione;
	private String email;
	
	public NotificaScadenzaVacancyDTO(Integer idVaDatiVacancy, Integer numAnno, Integer numRichiesta,
			String codProvenienzaVacancy, String attivitaPrincipale, Date dtPubblicazione, Date dtScadenzaPubblicazione, String email) {
		super();
		this.idVaDatiVacancy = idVaDatiVacancy;
		this.numAnno = numAnno;
		this.numRichiesta = numRichiesta;
		this.codProvenienzaVacancy = codProvenienzaVacancy;
		this.attivitaPrincipale = attivitaPrincipale;
		this.dtPubblicazione = dtPubblicazione;
		this.dtScadenzaPubblicazione = dtScadenzaPubblicazione;
		this.email = email;
	}
	
	public Integer getIdVaDatiVacancy() {
		return idVaDatiVacancy;
	}

	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}

	public Integer getNumAnno() {
		return numAnno;
	}

	public void setNumAnno(Integer numAnno) {
		this.numAnno = numAnno;
	}

	public Integer getNumRichiesta() {
		return numRichiesta;
	}

	public void setNumRichiesta(Integer numRichiesta) {
		this.numRichiesta = numRichiesta;
	}

	public String getCodProvenienzaVacancy() {
		return codProvenienzaVacancy;
	}

	public void setCodProvenienzaVacancy(String codProvenienzaVacancy) {
		this.codProvenienzaVacancy = codProvenienzaVacancy;
	}

	public Date getDtScadenzaPubblicazione() {
		return dtScadenzaPubblicazione;
	}

	public void setDtScadenzaPubblicazione(Date dtScadenzaPubblicazione) {
		this.dtScadenzaPubblicazione = dtScadenzaPubblicazione;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAttivitaPrincipale(){
		return attivitaPrincipale;
	}

	public void setAttivitaPrincipale(String attivitaPrincipale) {
		this.attivitaPrincipale = attivitaPrincipale;
	}

	public Date getDtPubblicazione() {
		return dtPubblicazione;
	}

	public void setDtPubblicazione(Date dtPubblicazione) {
		this.dtPubblicazione = dtPubblicazione;
	}


}
