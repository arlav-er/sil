package it.eng.myportal.dtos;

/**
 * Parametri di ricerca aziende, utilizzati nella pagina di abilitazione utenti sare.
 * 
 * @author Rodi A.
 * 
 */
public class RicercaAziendaDTO implements IDTO {

	private static final long serialVersionUID = 7167532462171592387L;

	private String utente;
	private String statoRichiesta;
	private Boolean conRettifica;
	private String idProvincia;

	public RicercaAziendaDTO() {
		super();
		utente = "";
		statoRichiesta = "";
		conRettifica = false;
	}

	public Boolean getConRettifica() {
		return conRettifica;
	}

	public void setConRettifica(Boolean conRettifica) {
		this.conRettifica = conRettifica;
	}

	public String getUtente() {
		return utente;
	}

	public void setUtente(String utente) {
		this.utente = utente;
	}

	public String getStatoRichiesta() {
		return statoRichiesta;
	}

	public void setStatoRichiesta(String statoRichiesta) {
		this.statoRichiesta = statoRichiesta;
	}

	public String getIdProvincia() {
		return idProvincia;
	}

	public void setIdProvincia(String idProvincia) {
		this.idProvincia = idProvincia;
	}

	@Override
	public RicercaAziendaDTO clone() {
		RicercaAziendaDTO res = new RicercaAziendaDTO();

		res.setUtente(this.getUtente());
		res.setStatoRichiesta(this.getStatoRichiesta());
		res.setConRettifica(this.getConRettifica());
		res.setIdProvincia(this.getIdProvincia());

		return res;
	}
}
