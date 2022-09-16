package it.eng.myportal.dtos;

import java.util.ArrayList;
import java.util.List;

public class VaRapportoDiLavoroDTO extends AbstractUpdatableDTO implements IVacancySection {
	private static final long serialVersionUID = 3470431672209370365L;

	private Integer idVaDatiVacancy;
	private DeRetribuzioneDTO retribuzione;
	private List<IDecode> tipologieContratto;
	private List<IDecode> turni;
	private List<IDecode> orari;
	private String infoContratti;
	private String infoOrariTurni;

	public VaRapportoDiLavoroDTO() {
		super();
		retribuzione = new DeRetribuzioneDTO();
		tipologieContratto = new ArrayList<IDecode>();
		turni = new ArrayList<IDecode>();
		orari = new ArrayList<IDecode>();
	}

	public DeRetribuzioneDTO getRetribuzione() {
		return retribuzione;
	}

	public void setRetribuzione(DeRetribuzioneDTO retribuzione) {
		this.retribuzione = retribuzione;
	}

	public List<IDecode> getTipologieContratto() {
		return tipologieContratto;
	}

	public void setTipologieContratto(List<IDecode> tipologieContratto) {
		this.tipologieContratto = tipologieContratto;
	}

	public List<IDecode> getTurni() {
		return turni;
	}

	public List<String> getCodTurni() {
		List<String> codici = new ArrayList<String>();
		for (IDecode turno : turni) {
			codici.add(turno.getId());
		}
		return codici;
	}

	public void setTurni(List<IDecode> turni) {
		this.turni = turni;
	}

	public List<IDecode> getOrari() {
		return orari;
	}

	public void setOrari(List<IDecode> orari) {
		this.orari = orari;
	}

	@Override
	public Integer getIdVaDatiVacancy() {
		return idVaDatiVacancy;
	}

	@Override
	public void setIdVaDatiVacancy(Integer idVaDatiVacancy) {
		this.idVaDatiVacancy = idVaDatiVacancy;
	}

	public String getInfoContratti() {
		return infoContratti;
	}

	public void setInfoContratti(String infoContratti) {
		this.infoContratti = infoContratti;
	}

	public String getInfoOrariTurni() {
		return infoOrariTurni;
	}

	public void setInfoOrariTurni(String infoOrariTurni) {
		this.infoOrariTurni = infoOrariTurni;
	}

}
