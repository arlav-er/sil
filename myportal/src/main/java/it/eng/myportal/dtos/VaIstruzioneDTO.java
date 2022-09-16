package it.eng.myportal.dtos;


/**
 * DataTransferObject della sezione ISTRUZIONE della Vacancy
 * 
 * @author D'Angelo E.
 */
public class VaIstruzioneDTO extends AbstractUpdatablePkDTO implements IVacancySection, IHasUniqueValue {

	private static final long serialVersionUID = -7733528620541663540L;

	private Integer idVaDatiVacancy;

	private DeTitoloDTO titolo;
	private String votazione;
	private String specifica;
	private String opzConseguito;
	private String opzIndispensabile;

	public VaIstruzioneDTO() {
		titolo = new DeTitoloDTO();
	}

	public Integer getIdVaDatiVacancy() {
		return idVaDatiVacancy;
	}

	@Override
	public String getUniqueValue() {
		return titolo.getId();
	}

	public String getVotazione() {
		return votazione;
	}

	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}

	public void setVotazione(String votazione) {
		this.votazione = votazione;
	}

	public DeTitoloDTO getTitolo() {
		return titolo;
	}

	public void setTitolo(DeTitoloDTO titolo) {
		this.titolo = titolo;
	}

	public String getSpecifica() {
		return specifica;
	}

	public void setSpecifica(String specifica) {
		this.specifica = specifica;
	}

	public String getOpzConseguito() {
		return opzConseguito;
	}

	public void setOpzConseguito(String opzConseguito) {
		this.opzConseguito = opzConseguito;
	}

	public String getOpzIndispensabile() {
		return opzIndispensabile;
	}

	public void setOpzIndispensabile(String opzIndispensabile) {
		this.opzIndispensabile = opzIndispensabile;
	}
}
