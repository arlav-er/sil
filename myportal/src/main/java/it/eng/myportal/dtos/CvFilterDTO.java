package it.eng.myportal.dtos;

import java.util.ArrayList;
import java.util.List;

public class CvFilterDTO {

	DeComuneDTO comune;
	List<GenericDecodeDTO> lingue;
	List<DeTitoloDTO> titoli;
	DeMansioneDTO gruppoProf;

	String nome;
	String cognome;
	String descrizioneTitolo;
	String cf = new String();

	boolean trasferte;
	boolean automunito;
	boolean motomunito;
	private int maxResults;
	private int startResultsFrom;

	public CvFilterDTO() {
		super();
		comune = new DeComuneDTO();
		gruppoProf = new DeMansioneDTO();
		lingue = new ArrayList<GenericDecodeDTO>();
		titoli = new ArrayList<DeTitoloDTO>();
		descrizioneTitolo = "";
	}

	public CvFilterDTO(String nome, String cognome, String cf, DeComuneDTO comune, List<GenericDecodeDTO> lingue, List<DeTitoloDTO> titoli, String eta,
			boolean trasferte, boolean automunito, boolean motomunito) {
		super();
		this.comune = comune;
		this.lingue = lingue;
		this.titoli = titoli;
		this.trasferte = trasferte;
		this.automunito = automunito;
		this.motomunito = motomunito;
		this.cf = cf;
		this.nome = nome;
		this.cognome = cognome;
		this.descrizioneTitolo = "";
	}

	public DeComuneDTO getComune() {
		return comune;
	}

	public void setComune(DeComuneDTO comune) {
		this.comune = comune;
	}

	public List<GenericDecodeDTO> getLingue() {
		return lingue;
	}

	public void setLingue(List<GenericDecodeDTO> lingue) {
		this.lingue = lingue;
	}

	public List<DeTitoloDTO> getTitoli() {
		return titoli;
	}

	public void setTitoli(List<DeTitoloDTO> titoli) {
		this.titoli = titoli;
	}


	public boolean isTrasferte() {
		return trasferte;
	}

	public void setTrasferte(boolean trasferte) {
		this.trasferte = trasferte;
	}

	public boolean isAutomunito() {
		return automunito;
	}

	public void setAutomunito(boolean automunito) {
		this.automunito = automunito;
	}

	public boolean isMotomunito() {
		return motomunito;
	}

	public void setMotomunito(boolean motomunito) {
		this.motomunito = motomunito;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getCf() {
		return cf;
	}

	public void setCf(String cf) {
		this.cf = cf;
	}

	public DeMansioneDTO getGruppoProf() {
		return gruppoProf;
	}

	public void setGruppoProf(DeMansioneDTO gruppoProf) {
		this.gruppoProf = gruppoProf;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public int getStartResultsFrom() {
		return startResultsFrom;
	}

	public void setStartResultsFrom(int startResultsFrom) {
		this.startResultsFrom = startResultsFrom;
	}

	public String getDescrizioneTitolo() {
		return this.descrizioneTitolo;
	}

	public void setDescrizioneTitolo(String descrizioneTitolo) {
		this.descrizioneTitolo = descrizioneTitolo;
	}
}
