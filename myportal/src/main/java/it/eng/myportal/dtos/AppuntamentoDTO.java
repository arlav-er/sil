package it.eng.myportal.dtos;

import it.eng.myportal.entity.decodifiche.DeTipoAppuntamento;

import java.util.Date;

public class AppuntamentoDTO implements IDTO {
	private static final long serialVersionUID = 9130142426928135938L;

	private String codTipoAppuntamento;
	private String descTipoAppuntamento;
	private Integer idAgAppuntamento;
	private Date data;
	private String orario;
	private String denominazioneCpi;
	private String indirizzoStampaCpi;
	private String siglaOperatore;
	private String ambienteCpi;
	private String telPatronato;
	private String codiceFiscale;
	private String nome;
	private String cognome;

	public AppuntamentoDTO(Integer idAgAppuntamento, DeTipoAppuntamento deTipoAppuntamento, Date data, String orario,
			String denominazioneCpi, String indirizzoStampaCpi, String siglaOperatore, String ambienteCpi,
			String telPatronato, String codiceFiscale, String nome, String cognome) {
		this.setCodTipoAppuntamento(deTipoAppuntamento.getCodTipoAppuntamento());
		this.setDescTipoAppuntamento(deTipoAppuntamento.getDescrizione());
		this.idAgAppuntamento = idAgAppuntamento;
		this.data = data;
		this.orario = orario;
		this.denominazioneCpi = denominazioneCpi;
		this.indirizzoStampaCpi = indirizzoStampaCpi;
		this.siglaOperatore = siglaOperatore;
		this.ambienteCpi = ambienteCpi;
		this.telPatronato = telPatronato;
		this.codiceFiscale = codiceFiscale;
		this.nome = nome;
		this.cognome = cognome;
	}

	public Integer getIdAgAppuntamento() {
		return idAgAppuntamento;
	}

	public void setIdAgAppuntamento(Integer idAgAppuntamento) {
		this.idAgAppuntamento = idAgAppuntamento;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getOrario() {
		return orario;
	}

	public void setOrario(String orario) {
		this.orario = orario;
	}

	public String getDenominazioneCpi() {
		return denominazioneCpi;
	}

	public void setDenominazioneCpi(String denominazioneCpi) {
		this.denominazioneCpi = denominazioneCpi;
	}

	public String getIndirizzoStampaCpi() {
		return indirizzoStampaCpi;
	}

	public void setIndirizzoStampaCpi(String indirizzoStampaCpi) {
		this.indirizzoStampaCpi = indirizzoStampaCpi;
	}

	public String getSiglaOperatore() {
		return siglaOperatore;
	}

	public void setSiglaOperatore(String siglaOperatore) {
		this.siglaOperatore = siglaOperatore;
	}

	public String getAmbienteCpi() {
		return ambienteCpi;
	}

	public void setAmbienteCpi(String ambienteCpi) {
		this.ambienteCpi = ambienteCpi;
	}

	public String getTelPatronato() {
		return telPatronato;
	}

	public void setTelPatronato(String telPatronato) {
		this.telPatronato = telPatronato;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
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

	public String getCodTipoAppuntamento() {
		return codTipoAppuntamento;
	}

	public void setCodTipoAppuntamento(String codTipoAppuntamento) {
		this.codTipoAppuntamento = codTipoAppuntamento;
	}

	public String getDescTipoAppuntamento() {
		return descTipoAppuntamento;
	}

	public void setDescTipoAppuntamento(String descTipoAppuntamento) {
		this.descTipoAppuntamento = descTipoAppuntamento;
	}

}
