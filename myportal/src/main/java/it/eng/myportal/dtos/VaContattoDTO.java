package it.eng.myportal.dtos;

/**
 * DataTransferObject della sezione CONTATTO della Vacancy 1-1
 * 
 * @author Rodi A.
 */
public class VaContattoDTO extends AbstractUpdatablePkDTO implements
		IVacancySection {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3147126506820306005L;
	private Integer idVaDatiVacancy;
	private String nomeRiferimento;
	private String cognomeRiferimento;
	private String telRiferimento;
	private String faxRiferimento;
	private String mail;
	/*
	 * Questo attributo viene utilizzato solo nel caso in cui il dto rappresenti
	 * il caontatto alternativo Vale true se e' gia' stato memorizzato un
	 * contatto principale, false altrimenti
	 */
	private Boolean active;
	
	public VaContattoDTO() {
		active = false;
	}

	@Override
	public Integer getIdVaDatiVacancy() {
		return idVaDatiVacancy;
	}

	@Override
	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}

	public String getCognomeRiferimento() {
		return cognomeRiferimento;
	}

	public void setCognomeRiferimento(String cognomeRiferimento) {
		this.cognomeRiferimento = cognomeRiferimento;
	}

	public String getNomeRiferimento() {
		return nomeRiferimento;
	}

	public void setNomeRiferimento(String nomeRiferimento) {
		this.nomeRiferimento = nomeRiferimento;
	}

	public String getTelRiferimento() {
		return telRiferimento;
	}

	public void setTelRiferimento(String telRiferimento) {
		this.telRiferimento = telRiferimento;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getFaxRiferimento() {
		return faxRiferimento;
	}

	public void setFaxRiferimento(String faxRiferimento) {
		this.faxRiferimento = faxRiferimento;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}
