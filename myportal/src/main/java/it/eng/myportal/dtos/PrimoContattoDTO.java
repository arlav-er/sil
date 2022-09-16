package it.eng.myportal.dtos;

public class PrimoContattoDTO implements IDTO {

	private static final long serialVersionUID = 4858452445557551066L;

	/**
	 * Id dell'azienda che contatta
	 */
	private Integer idPfPrincipalAzienda;

	/**
	 * Id del curriculum visualizzato dall'azienda per contattare il lavoratore
	 */
	private Integer idCvDatiPersonali;

	/**
	 * Eventuale id della vacancy
	 */
	private Integer idVaDatiVacancy;

	/**
	 * Messaggio inviato dall'azienda al lavoratore
	 */
	private String messaggio;

	public Integer getIdPfPrincipalAzienda() {
		return idPfPrincipalAzienda;
	}

	public void setIdPfPrincipalAzienda(Integer idPfPrincipalAzienda) {
		this.idPfPrincipalAzienda = idPfPrincipalAzienda;
	}

	public Integer getIdCvDatiPersonali() {
		return idCvDatiPersonali;
	}

	public void setIdCvDatiPersonali(Integer idCvDatiPersonali) {
		this.idCvDatiPersonali = idCvDatiPersonali;
	}

	public String getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
	}

	public Integer getIdVaDatiVacancy() {
		return idVaDatiVacancy;
	}

	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}

}
