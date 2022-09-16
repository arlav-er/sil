package it.eng.myportal.dtos;

/**
 * DataTransferObject della sezione ORARIO della Vacancy.
 * La sezione orario è in relazione 1-n con la vacancy.
 * 
 * @author Rodi A.
 */
public class VaOrarioDTO extends AbstractUpdatablePkDTO implements IVacancySection, IHasUniqueValue {

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
