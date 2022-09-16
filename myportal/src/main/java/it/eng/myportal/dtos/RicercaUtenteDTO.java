package it.eng.myportal.dtos;

import it.eng.myportal.enums.TipoAccount;
import it.eng.myportal.enums.TipoRegistrazione;
import it.eng.myportal.enums.TipoRicercaUtente;

public class RicercaUtenteDTO implements IDTO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7167532462171592387L;

	private String nome;
	private String cognome;
	private String codiceFiscale;
	private String username;
	private String email;
	private String emailPEC;
	private int maxResults;
	private int startResultsFrom;

	private TipoRicercaUtente tipo = TipoRicercaUtente.INIZIA_PER;
	private TipoRegistrazione tipoRegistrazione = TipoRegistrazione.TUTTI;
	private TipoAccount tipoAccount = TipoAccount.TUTTI;
	private DeProvenienzaDTO provenienzaCurriculum;

	public RicercaUtenteDTO() {
		super();
		provenienzaCurriculum = new DeProvenienzaDTO();
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailPEC() {
		return emailPEC;
	}

	public void setEmailPEC(String emailPEC) {
		this.emailPEC = emailPEC;
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

	public TipoRicercaUtente getTipo() {
		return tipo;
	}

	public void setTipo(TipoRicercaUtente tipo) {
		this.tipo = tipo;
	}

	public TipoRegistrazione getTipoRegistrazione() {
		return tipoRegistrazione;
	}

	public void setTipoRegistrazione(TipoRegistrazione tipoRegistrazione) {
		this.tipoRegistrazione = tipoRegistrazione;
	}

	public TipoAccount getTipoAccount() {
		return tipoAccount;
	}

	public void setTipoAccount(TipoAccount tipoAccount) {
		this.tipoAccount = tipoAccount;
	}

	public DeProvenienzaDTO getProvenienzaCurriculum() {
		return provenienzaCurriculum;
	}

	public void setProvenienzaCurriculum(DeProvenienzaDTO provenienzaCurriculum) {
		this.provenienzaCurriculum = provenienzaCurriculum;
	}

	@Override
	public RicercaUtenteDTO clone() {
		RicercaUtenteDTO res = new RicercaUtenteDTO();

		res.setNome(this.getNome());
		res.setCognome(this.getCognome());
		res.setCodiceFiscale(this.getCodiceFiscale());
		res.setUsername(this.getUsername());
		res.setEmail(this.getEmail());
		res.setEmailPEC(this.getEmailPEC());
		res.setMaxResults(this.getMaxResults());
		res.setStartResultsFrom(this.getStartResultsFrom());
		res.setTipo(this.getTipo());
		res.setTipoRegistrazione(this.getTipoRegistrazione());
		res.setTipoAccount(this.getTipoAccount());
		res.setProvenienzaCurriculum(this.getProvenienzaCurriculum());

		return res;
	}
}
