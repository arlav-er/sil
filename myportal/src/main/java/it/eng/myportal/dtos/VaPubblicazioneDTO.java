package it.eng.myportal.dtos;

public class VaPubblicazioneDTO extends AbstractUpdatablePkDTO implements IVacancySection {
	private static final long serialVersionUID = 1480986713994005794L;

	private Integer idVaDatiVacancy;
	private String datiAzienda;
	private String contenuto;
	private String luogo;
	private String formazione;
	private String contratto;
	private String conoscenze;
	private String caratteristiche;
	private String orario;
	private String candidatura;

	@Override
	public Integer getIdVaDatiVacancy() {
		return idVaDatiVacancy;
	}

	@Override
	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}

	public String getDatiAzienda() {
		return datiAzienda;
	}

	public void setDatiAzienda(String datiAzienda) {
		this.datiAzienda = datiAzienda;
	}

	public String getContenuto() {
		return contenuto;
	}

	public void setContenuto(String contenuto) {
		this.contenuto = contenuto;
	}

	public String getLuogo() {
		return luogo;
	}

	public void setLuogo(String luogo) {
		this.luogo = luogo;
	}

	public String getFormazione() {
		return formazione;
	}

	public void setFormazione(String formazione) {
		this.formazione = formazione;
	}

	public String getContratto() {
		return contratto;
	}

	public void setContratto(String contratto) {
		this.contratto = contratto;
	}

	public String getConoscenze() {
		return conoscenze;
	}

	public void setConoscenze(String conoscenze) {
		this.conoscenze = conoscenze;
	}

	public String getCaratteristiche() {
		return caratteristiche;
	}

	public void setCaratteristiche(String caratteristiche) {
		this.caratteristiche = caratteristiche;
	}

	public String getOrario() {
		return orario;
	}

	public void setOrario(String orario) {
		this.orario = orario;
	}

	public String getCandidatura() {
		return candidatura;
	}

	public void setCandidatura(String candidatura) {
		this.candidatura = candidatura;
	}

}
