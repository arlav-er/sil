package it.eng.myportal.dtos;

import java.util.Date;

public class NotificaInLavVacancyDTO implements IDTO {

	private static final long serialVersionUID = -5749199776570443041L;

	private Integer idVaDatiVacancy;
	private Integer numAnno;
	private Integer numRichiesta;
	private String codProvenienzaVacancy;
	private String attivitaPrincipale;

	private Date dtmIns;
	private Date dtmMod;

	private String email;

	public NotificaInLavVacancyDTO(Integer idVaDatiVacancy, Integer numAnno, Integer numRichiesta,
			String codProvenienzaVacancy, String attivitaPrincipale, Date dtmIns, Date dtmMod, String email) {
		super();
		this.idVaDatiVacancy = idVaDatiVacancy;
		this.numAnno = numAnno;
		this.numRichiesta = numRichiesta;
		this.codProvenienzaVacancy = codProvenienzaVacancy;
		this.attivitaPrincipale = attivitaPrincipale;
		this.setDtmIns(dtmIns);
		this.setDtmMod(dtmMod);
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAttivitaPrincipale() {
		return attivitaPrincipale;
	}

	public void setAttivitaPrincipale(String attivitaPrincipale) {
		this.attivitaPrincipale = attivitaPrincipale;
	}

	public Date getDtmIns() {
		return dtmIns;
	}

	public void setDtmIns(Date dtmIns) {
		this.dtmIns = dtmIns;
	}

	public Date getDtmMod() {
		return dtmMod;
	}

	public void setDtmMod(Date dtmMod) {
		this.dtmMod = dtmMod;
	}

}
