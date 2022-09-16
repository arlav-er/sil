package it.eng.myportal.dtos;

/**
 * DataTransferObject della sezione ALBO della Vacancy 1-n
 * 
 * @author Rodi A.
 */
public class VaAlboDTO extends AbstractUpdatablePkDTO implements IVacancySection, IHasUniqueValue {

	private String codice;
	private String descrizione;
	private Integer idVaDatiVacancy;
	private String opzIndispensabile;

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

	public String getOpzIndispensabile() {
		return opzIndispensabile;
	}

	public void setOpzIndispensabile(String opzIndispensabile) {
		this.opzIndispensabile = opzIndispensabile;
	}

}
