package it.eng.myportal.dtos;

/**
 * DataTransferObject della sezione PATENTINO della Vacancy La sezione PATENTINO è in relazione 1-n con la vacancy.
 * 
 * @author Rodi A.
 */
public class VaPatentinoDTO extends AbstractUpdatablePkDTO implements IVacancySection, IHasUniqueValue {
	private static final long serialVersionUID = 7820517248438831079L;

	private String codice;
	private String descrizione;
	private String opzIndispensabile;
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

	public String getOpzIndispensabile() {
		return opzIndispensabile;
	}

	public void setOpzIndispensabile(String opzIndispensabile) {
		this.opzIndispensabile = opzIndispensabile;
	}

}
