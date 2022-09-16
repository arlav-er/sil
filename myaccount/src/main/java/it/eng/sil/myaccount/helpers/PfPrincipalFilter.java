package it.eng.sil.myaccount.helpers;

import it.eng.sil.myaccount.model.enums.TipoOrdinamento;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpDeMacroTipo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpGruppo;
import it.eng.sil.mycas.model.entity.gestioneprofilo.GpRuolo;

public class PfPrincipalFilter {

	private Integer idPfPrincipal;
	private String username;
	private String email;
	private GpGruppo gruppo;
	private GpRuolo ruolo;
	private GpDeMacroTipo macroTipo;
	private String nome, cognome, codiceFiscale;
	private boolean likeSearch, includiNonProfilati;
	private TipoOrdinamento tipoOrdinamentoEnum;

	public void setEmail(String email)
	{
		this.email = email;
	}
	
	public String getEmail() {
		return email;
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public GpDeMacroTipo getMacroTipo() {
		return macroTipo;
	}

	public void setMacroTipo(GpDeMacroTipo macroTipo) {
		this.macroTipo = macroTipo;
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

	public boolean isLikeSearch() {
		return likeSearch;
	}

	public void setLikeSearch(boolean likeSearch) {
		this.likeSearch = likeSearch;
	}

	public TipoOrdinamento getTipoOrdinamentoEnum() {
		return tipoOrdinamentoEnum;
	}

	public void setTipoOrdinamentoEnum(TipoOrdinamento tipoOrdinamentoEnum) {
		this.tipoOrdinamentoEnum = tipoOrdinamentoEnum;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public GpGruppo getGruppo() {
		return gruppo;
	}

	public void setGruppo(GpGruppo gruppo) {
		this.gruppo = gruppo;
	}

	public boolean isIncludiNonProfilati() {
		return includiNonProfilati;
	}

	public void setIncludiNonProfilati(boolean includiNonProfilati) {
		this.includiNonProfilati = includiNonProfilati;
	}

	public GpRuolo getRuolo() {
		return ruolo;
	}

	public void setRuolo(GpRuolo ruolo) {
		this.ruolo = ruolo;
	}

	public Integer getIdPfPrincipal() {
		return idPfPrincipal;
	}

	public void setIdPfPrincipal(Integer idPfPrincipal) {
		this.idPfPrincipal = idPfPrincipal;
	}

}
