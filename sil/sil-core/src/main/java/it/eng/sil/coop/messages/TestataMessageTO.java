/*
 * Created on 06-Apr-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.messages;

/**
 * @author rolfini
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class TestataMessageTO {

	// *** Struttura del messaggio
	// parametri di instradamento
	protected String destinazione;
	protected String servizio;
	protected String poloMittente;

	// parametri di autenticazione/autorizzazione
	protected String cdnUtente;
	protected String cdnGruppo;
	protected String cdnProfilo;
	protected String strMittente;

	// parametri di configurazione
	protected int maxRedeliveries = 100;
	protected int redeliveries = 0;
	// *** /Struttura del messaggio

	/**
	 * 
	 */
	public TestataMessageTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return
	 */
	public String getCdnGruppo() {
		return cdnGruppo;
	}

	/**
	 * @return
	 */
	public String getCdnProfilo() {
		return cdnProfilo;
	}

	/**
	 * @return
	 */
	public String getCdnUtente() {
		return cdnUtente;
	}

	/**
	 * @return
	 */
	public String getDestinazione() {
		return destinazione;
	}

	/**
	 * @return
	 */
	public int getMaxRedeliveries() {
		return maxRedeliveries;
	}

	/**
	 * @return
	 */
	public String getPoloMittente() {
		return poloMittente;
	}

	/**
	 * @return
	 */
	public int getRedeliveries() {
		return redeliveries;
	}

	/**
	 * @return
	 */
	public String getServizio() {
		return servizio;
	}

	/**
	 * @return
	 */
	public String getStrMittente() {
		return strMittente;
	}

	/**
	 * @param string
	 */
	public void setCdnGruppo(String string) {
		cdnGruppo = string;
	}

	/**
	 * @param string
	 */
	public void setCdnProfilo(String string) {
		cdnProfilo = string;
	}

	/**
	 * @param string
	 */
	public void setCdnUtente(String string) {
		cdnUtente = string;
	}

	/**
	 * @param string
	 */
	public void setDestinazione(String string) {
		destinazione = string;
	}

	/**
	 * @param i
	 */
	public void setMaxRedeliveries(int i) {
		maxRedeliveries = i;
	}

	/**
	 * @param string
	 */
	public void setPoloMittente(String string) {
		poloMittente = string;
	}

	/**
	 * @param i
	 */
	public void setRedeliveries(int i) {
		redeliveries = i;
	}

	/**
	 * @param string
	 */
	public void setServizio(String string) {
		servizio = string;
	}

	/**
	 * @param string
	 */
	public void setStrMittente(String string) {
		strMittente = string;
	}

}
