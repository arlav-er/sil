package it.eng.myportal.dtos;

import java.io.Serializable;

public class UtenteSAREDTO implements IDTO, Serializable {

	private static final String NESSUNA_MODIFICA = "-1";
	/**
	 * 
	 */
	private static final long serialVersionUID = -7480258624088365140L;

	private Integer idAziendaInfo;
	private Integer idPfPrincipal;
	private String codiceFiscale;
	private String ragioneSociale;
	private String username;
	private String emailRegistrazione;

	private String nomeReferente;
	private String telefonoReferente;
	private String emailReferente;

	private String codAutorizzazioneSare;
	private String autorizzazioneSAREDescrizione;
	private String modificaAutorizzazioneSARE = NESSUNA_MODIFICA;

	private String codTipoUtenteSare;
	private String descTipoUtenteSare;
	private String modificaCodTipoUtenteSare = NESSUNA_MODIFICA;

	public UtenteSAREDTO() {
	}

	// TODO mettere qua i parametri per i 2 nuovi campi! Cambiare la query della ricerca!!!!
	public UtenteSAREDTO(Integer idAziendaInfo, Integer idPfPrincipal, String codiceFiscale, String ragioneSociale,
			String username, String emailRegistrazione, String nomeReferente, String telefonoReferente,
			String emailReferente, String codAutorizzazioneSare, String autorizzazioneSAREDescrizione,
			String codTipoUtenteSare, String descTipoUtenteSare) {
		super();
		this.idAziendaInfo = idAziendaInfo;
		this.idPfPrincipal = idPfPrincipal;
		this.codiceFiscale = codiceFiscale;
		this.ragioneSociale = ragioneSociale;
		this.username = username;
		this.emailRegistrazione = emailRegistrazione;

		this.nomeReferente = nomeReferente;
		this.telefonoReferente = telefonoReferente;
		this.emailReferente = emailReferente;

		this.codAutorizzazioneSare = codAutorizzazioneSare;
		this.autorizzazioneSAREDescrizione = autorizzazioneSAREDescrizione;
		modificaAutorizzazioneSARE = codAutorizzazioneSare;
		this.codTipoUtenteSare = codTipoUtenteSare;
		this.descTipoUtenteSare = descTipoUtenteSare;
		modificaCodTipoUtenteSare = codTipoUtenteSare;
	}

	@Deprecated
	public Integer getIdAziendaInfo() {
		return idAziendaInfo;
	}

	@Deprecated
	public void setIdAzienda(Integer idAzienda) {
		this.idAziendaInfo = idAzienda;
	}

	public Integer getIdPfPrincipal() {
		return idPfPrincipal;
	}

	public void setIdPfPrincipal(Integer idPfPrincipal) {
		this.idPfPrincipal = idPfPrincipal;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCodAutorizzazioneSare() {
		return codAutorizzazioneSare;
	}

	public void setCodAutorizzazioneSare(String autorizzazioneSARE) {
		this.codAutorizzazioneSare = autorizzazioneSARE;
	}

	public String getAutorizzazioneSAREDescrizione() {
		return autorizzazioneSAREDescrizione;
	}

	public void setAutorizzazioneSAREDescrizione(String autorizzazioneSAREDescrizione) {
		this.autorizzazioneSAREDescrizione = autorizzazioneSAREDescrizione;
	}

	public String getModificaAutorizzazioneSARE() {
		return modificaAutorizzazioneSARE;
	}

	public void setModificaAutorizzazioneSARE(String modificaAutorizzazioneSARE) {
		this.modificaAutorizzazioneSARE = modificaAutorizzazioneSARE;
	}

	public String getCodTipoUtenteSare() {
		return codTipoUtenteSare;
	}

	public void setCodTipoUtenteSare(String codTipoUtenteSare) {
		this.codTipoUtenteSare = codTipoUtenteSare;
	}

	public String getDescTipoUtenteSare() {
		return descTipoUtenteSare;
	}

	public void setDescTipoUtenteSare(String descTipoUtenteSare) {
		this.descTipoUtenteSare = descTipoUtenteSare;
	}

	public String getModificaCodTipoUtenteSare() {
		return modificaCodTipoUtenteSare;
	}

	public void setModificaCodTipoUtenteSare(String modificaCodTipoUtenteSare) {
		this.modificaCodTipoUtenteSare = modificaCodTipoUtenteSare;
	}

	public String getTelefonoReferente() {
		return telefonoReferente;
	}

	public String getEmailReferente() {
		return emailReferente;
	}

	public void setTelefonoReferente(String telefonoReferente) {
		this.telefonoReferente = telefonoReferente;
	}

	public void setEmailReferente(String emailReferente) {
		this.emailReferente = emailReferente;
	}

	public String getNomeReferente() {
		return nomeReferente;
	}

	public void setNomeReferente(String nomeReferente) {
		this.nomeReferente = nomeReferente;
	}

	public String getEmailRegistrazione() {
		return emailRegistrazione;
	}

	public void setEmailRegistrazione(String emailRegistrazione) {
		this.emailRegistrazione = emailRegistrazione;
	}
}
