package it.eng.myportal.dtos;

import it.eng.myportal.entity.decodifiche.DeProvenienza;
import it.eng.myportal.enums.TipoRicercaOfferte;

public class RicercaOfferteDTO implements IDTO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7167532462171592387L;

	private String numeroRichiesta;
	private String anno;
	private String codProvenienza;
	private DeProvenienza deProvenienza;
	private String attivita;
	private String stato;
	private String cfOrRagSocAzienda;
	private String nomeOperatore;
	private String cognomeOperatore;
	
	
	private TipoRicercaOfferte tipo = TipoRicercaOfferte.SI;

	public String getNumeroRichiesta() {
		return numeroRichiesta;
	}

	public void setNumeroRichiesta(String numeroRichiesta) {
		this.numeroRichiesta = numeroRichiesta;
	}
	
	public String getAnno() {
		return anno;
	}

	public void setAnno(String anno) {
		this.anno = anno;
	}
	
	public String getCodProvenienza() {
		return codProvenienza;
	}

	public DeProvenienza getDeProvenienza() {
		return deProvenienza;
	}

	public void setDeProvenienza(DeProvenienza deProvenienza) {
		this.deProvenienza = deProvenienza;
	}

	public void setCodProvenienza(String codProvenienza) {
		this.codProvenienza = codProvenienza;
	}

	public String getAttivita() {
		return attivita;
	}

	public void setAttivita(String attivita) {
		this.attivita = attivita;
	}
	
	public TipoRicercaOfferte getTipo() {
		return tipo;
	}

	public void setTipo(TipoRicercaOfferte tipo) {
		this.tipo = tipo;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getCfOrRagSocAzienda() {
		return cfOrRagSocAzienda;
	}

	public void setCfOrRagSocAzienda(String cfOrRagSocAzienda) {
		this.cfOrRagSocAzienda = cfOrRagSocAzienda;
	}

	public String getNomeOperatore() {
		return nomeOperatore;
	}

	public void setNomeOperatore(String nomeOperatore) {
		this.nomeOperatore = nomeOperatore;
	}

	public String getCognomeOperatore() {
		return cognomeOperatore;
	}

	public void setCognomeOperatore(String cognomeOperatore) {
		this.cognomeOperatore = cognomeOperatore;
	}
	
}
