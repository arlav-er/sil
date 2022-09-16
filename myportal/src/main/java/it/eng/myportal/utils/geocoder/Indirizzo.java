package it.eng.myportal.utils.geocoder;

import it.eng.myportal.dtos.DeComuneDTO;

/**
 * Classe che rappresenta un indirizzo. Il campo "indirizzoFormattato" contiene
 * l'indirizzo formattato per essere inviato al geoserver p-er la
 * georeferenziazione.
 * 
 * @author Enrico D'Angelo
 * 
 */
public class Indirizzo {
	private String indirizzo;
	private DeComuneDTO deComuneDTO;
	private String cap;
	private String targa;
	private String indirizzoFormattato;
	private String indirizzoShort;

	public Indirizzo(String indirizzo, DeComuneDTO deComuneDTO, String cap, String targa) {
		this.indirizzo = indirizzo;
		this.deComuneDTO = deComuneDTO;
		this.cap = cap;
		this.targa = targa;

		/*
		 * genera la stringa da georeferenziare
		 * 
		 * Se l'indirizzo e' in Italia
		 * Esempio indirizzoFormattato: VIA EMILIA LEVANTE 14 BOLOGNA, 40138 (BO), ITALY
		 * Esempio indirizzoShort: BOLOGNA, 40138 (BO), ITALY
		 * 
		 * Se l'indirizzo e' fuori Italia
		 * Esempio indirizzoFormattato: SPAGNA
		 * Esempio indirizzoShort: SPAGNA
		 */
		if (deComuneDTO.isStato()) {
			this.indirizzoFormattato = deComuneDTO.getDescrizione();
			this.indirizzoShort = deComuneDTO.getDescrizione();
		} else {
			this.indirizzoFormattato = indirizzo + " " + deComuneDTO.getDescrizione() + ", " + cap + " (" + targa + "), Italy";
			this.indirizzoShort = deComuneDTO.getDescrizione() + ", " + cap + " (" + targa + "), Italy";
		}
	}

	public Indirizzo(DeComuneDTO deComuneDTO, String cap, String targa) {
		this("", deComuneDTO, cap, targa);
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public DeComuneDTO getDeComuneDTO() {
		return deComuneDTO;
	}

	public void setDeComuneDTO(DeComuneDTO deComuneDTO) {
		this.deComuneDTO = deComuneDTO;
	}

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public String getTarga() {
		return targa;
	}

	public void setTarga(String targa) {
		this.targa = targa;
	}

	public String getIndirizzoFormattato() {
		return indirizzoFormattato;
	}

	public void setIndirizzoFormattato(String indirizzoFormattato) {
		this.indirizzoFormattato = indirizzoFormattato;
	}

	public String toString() {
		return indirizzoFormattato;
	}

	public String getIndirizzoShort() {
		return indirizzoShort;
	}

	public void setIndirizzoShort(String indirizzoShort) {
		this.indirizzoShort = indirizzoShort;
	}
}