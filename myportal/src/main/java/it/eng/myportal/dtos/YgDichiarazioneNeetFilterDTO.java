package it.eng.myportal.dtos;

import it.eng.myportal.entity.enums.YgDichiarazioneNeetStatoEnum;

import java.util.Date;

public class YgDichiarazioneNeetFilterDTO implements IDTO {
	private static final long serialVersionUID = -5848163042050173317L;

	private Integer idDichiarazione;
	private String nome;
	private String cognome;
	private String codiceFiscale;
	private String codiceFiscaleEnte;
	private Date dataDa;
	private Date dataA;
	private YgDichiarazioneNeetStatoEnum stato;
	private Integer idPrincipalAzienda;
	private int first;
	private int pageSize;

	public YgDichiarazioneNeetFilterDTO() {
		setIdDichiarazione(null);
		nome = null;
		cognome = null;
		codiceFiscale = null;
		codiceFiscaleEnte = null;
		dataDa = null;
		dataA = null;
		stato = null;
		idPrincipalAzienda = null;
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

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public Date getDataDa() {
		return dataDa;
	}

	public void setDataDa(Date dataDa) {
		this.dataDa = dataDa;
	}

	public Date getDataA() {
		return dataA;
	}

	public void setDataA(Date dataA) {
		this.dataA = dataA;
	}

	public YgDichiarazioneNeetStatoEnum getStato() {
		return stato;
	}

	public void setStato(YgDichiarazioneNeetStatoEnum stato) {
		this.stato = stato;
	}

	public Integer getIdPrincipalAzienda() {
		return idPrincipalAzienda;
	}

	public void setIdPrincipalAzienda(Integer idPrincipalAzienda) {
		this.idPrincipalAzienda = idPrincipalAzienda;
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getCodiceFiscaleEnte() {
		return codiceFiscaleEnte;
	}

	public void setCodiceFiscaleEnte(String codiceFiscaleEnte) {
		this.codiceFiscaleEnte = codiceFiscaleEnte;
	}

	public Integer getIdDichiarazione() {
		return idDichiarazione;
	}

	public void setIdDichiarazione(Integer idDichiarazione) {
		this.idDichiarazione = idDichiarazione;
	}

}
