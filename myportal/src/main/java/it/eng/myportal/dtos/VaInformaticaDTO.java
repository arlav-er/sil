package it.eng.myportal.dtos;

import it.eng.myportal.utils.Utils;


/**
 * Rappresenta la sezione INFORMATICA della vacancy.
 * In relazione 1-1: l'id corrisponde ad idVaDatiVacancy
 * @author Rodi A.
 * 
 */
public class VaInformaticaDTO extends AbstractUpdatablePkDTO implements IVacancySection {

	private String descrizione;

	@Override
	public Integer getIdVaDatiVacancy() {
		return getId();
	}

	@Override
	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		setId(idVaDatiVacancy);
	}

	public String getDescrizione() {
		descrizione = Utils.nullIfEmptyHTML(descrizione);
		
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
