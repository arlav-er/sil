package it.eng.myportal.ws.mysap.pojo;

import java.util.ArrayList;
import java.util.List;

import it.eng.myportal.dtos.DeMansioneDTO;
import it.eng.myportal.dtos.DeMansioneMinDTO;
import it.eng.myportal.dtos.GenericFiltroDecodeDTO;

public class NewCvProfDesiderateDTO {

	private DeMansioneMinDTO deMansioneMin;
	private DeMansioneDTO deMansione;
	private String descrizioneProfessione;
	private Boolean flagEspSettore;
	private String breveDescrProfessione;
	private Boolean flagDispTrasferte;
	private Boolean flagDispMezzoProprio;

	private List<GenericFiltroDecodeDTO> listaOrario;
	private List<GenericFiltroDecodeDTO> listaRapportoLavoro;
	private List<DeMansioneMinDTO> listaDeMansioneMin;

	public NewCvProfDesiderateDTO() {
		super();
		deMansioneMin = new DeMansioneMinDTO();
		deMansione = new DeMansioneDTO();
		listaOrario = new ArrayList<GenericFiltroDecodeDTO>();
		listaRapportoLavoro = new ArrayList<GenericFiltroDecodeDTO>();
		setListaDeMansioneMin(new ArrayList<DeMansioneMinDTO>());
	}

	public DeMansioneMinDTO getDeMansioneMin() {
		return deMansioneMin;
	}

	public void setDeMansioneMin(DeMansioneMinDTO deMansioneMin) {
		this.deMansioneMin = deMansioneMin;
	}

	public String getDescrizioneProfessione() {
		return descrizioneProfessione;
	}

	public void setDescrizioneProfessione(String descrizioneProfessione) {
		this.descrizioneProfessione = descrizioneProfessione;
	}

	public Boolean getFlagEspSettore() {
		return flagEspSettore;
	}

	public void setFlagEspSettore(Boolean flagEspSettore) {
		this.flagEspSettore = flagEspSettore;
	}

	public String getBreveDescrProfessione() {
		return breveDescrProfessione;
	}

	public void setBreveDescrProfessione(String breveDescrProfessione) {
		this.breveDescrProfessione = breveDescrProfessione;
	}

	public Boolean getFlagDispTrasferte() {
		return flagDispTrasferte;
	}

	public void setFlagDispTrasferte(Boolean flagDispTrasferte) {
		this.flagDispTrasferte = flagDispTrasferte;
	}

	public Boolean getFlagDispMezzoProprio() {
		return flagDispMezzoProprio;
	}

	public void setFlagDispMezzoProprio(Boolean flagDispMezzoProprio) {
		this.flagDispMezzoProprio = flagDispMezzoProprio;
	}

	public List<GenericFiltroDecodeDTO> getListaOrario() {
		return listaOrario;
	}

	public void setListaOrario(List<GenericFiltroDecodeDTO> listaOrario) {
		this.listaOrario = listaOrario;
	}

	public List<GenericFiltroDecodeDTO> getListaRapportoLavoro() {
		return listaRapportoLavoro;
	}

	public void setListaRapportoLavoro(List<GenericFiltroDecodeDTO> listaRapportoLavoro) {
		this.listaRapportoLavoro = listaRapportoLavoro;
	}

	public DeMansioneDTO getDeMansione() {
		return deMansione;
	}

	public void setDeMansione(DeMansioneDTO deMansione) {
		this.deMansione = deMansione;
	}

	public List<DeMansioneMinDTO> getListaDeMansioneMin() {
		return listaDeMansioneMin;
	}

	public void setListaDeMansioneMin(List<DeMansioneMinDTO> list) {
		this.listaDeMansioneMin = list;
	}

}
