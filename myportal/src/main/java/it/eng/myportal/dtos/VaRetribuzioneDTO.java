package it.eng.myportal.dtos;


/**
 * DataTransferObject della sezione RETRIBUZIONE della Vacancy
 * La tabella è inrelazione 1-1. L'id corrisponde all'id della vacancy
 * 
 * @author Rodi A.
 */
public class VaRetribuzioneDTO extends AbstractUpdatablePkDTO implements IVacancySection {
	
	private String codice;
	private String descrizione;
		
	@Override
	public Integer getIdVaDatiVacancy() {		
		return getId();
	}
	
	@Override
	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		setId(idVaDatiVacancy);
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
