package it.eng.myportal.dtos;

public class VaCandidatoDTO extends AbstractUpdatableDTO implements IVacancySection {
	private static final long serialVersionUID = -6036125238369180597L;

	private Integer idVaDatiVacancy;
	private VaEsperienzeDTO vaEsperienze;
	private VaPubblicazioneDTO vaCaratteristiche;

	@Override
	public Integer getIdVaDatiVacancy() {
		return idVaDatiVacancy;
	}

	@Override
	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}

	public VaEsperienzeDTO getVaEsperienze() {
		return vaEsperienze;
	}

	public void setVaEsperienze(VaEsperienzeDTO vaEsperienze) {
		this.vaEsperienze = vaEsperienze;
	}

	public VaPubblicazioneDTO getVaCaratteristiche() {
		return vaCaratteristiche;
	}

	public void setVaCaratteristiche(VaPubblicazioneDTO vaCaratteristiche) {
		this.vaCaratteristiche = vaCaratteristiche;
	}
}
