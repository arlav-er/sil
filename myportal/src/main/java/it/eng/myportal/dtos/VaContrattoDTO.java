package it.eng.myportal.dtos;

/**
 * DataTransferObject della sezione CONTRATTO della Vacancy Relazione 1-n
 * 
 * @author Rodi A.
 */
public class VaContrattoDTO extends AbstractUpdatablePkDTO implements IVacancySection, IHasUniqueValue {
	private static final long serialVersionUID = 990909811327853517L;

	private String codice;
	private String descrizione;
	private Integer idVaDatiVacancy;

	@Override
	public String getUniqueValue() {
		return codice;
	}

	@Override
	public Integer getIdVaDatiVacancy() {
		return idVaDatiVacancy;
	}

	@Override
	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
