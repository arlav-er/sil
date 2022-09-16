/*
 * Created on 26-May-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.wsClient.messageReceiver;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rolfini
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class ServiceParameters extends HashMap {

	/**
	 * 
	 */
	public ServiceParameters() {
		super();
	}

	/**
	 * @param arg0
	 */
	public ServiceParameters(Map arg0) {
		super(arg0);
	}

	// Setter *speciali*, per la testata del messaggio

	public void setDestinazione(String destinazione) {
		super.put("Destinazione", destinazione);
	}

	public void setPoloMittente(String poloMittente) {
		super.put("Polomittente", poloMittente);
	}

	public void setCdnUtente(String cdnUtente) {
		super.put("cdnUtente", cdnUtente);
	}

	public void setCdnGruppo(String cdnGruppo) {
		super.put("cdnGruppo", cdnGruppo);
	}

	public void setCdnProfilo(String cdnProfilo) {
		super.put("cdnProfilo", cdnProfilo);
	}

	public void setStrMittente(String strMittente) {
		super.put("strMittente", strMittente);
	}

	public void setServizio(String servizio) {
		super.put("servizio", servizio);
	}

	public String getServizio() {
		return (String) super.get("servizio");
	}
	// Getter *speciali*, per la testata del messaggio

	public String getDestinazione() {
		return (String) super.get("Destinazione");
	}

	public String getPoloMittente() {
		return (String) super.get("Polomittente");
	}

	public String getCdnUtente() {
		return (String) super.get("cdnUtente");
	}

	public String getCdnGruppo() {
		return (String) super.get("cdnGruppo");
	}

	public String getCdnProfilo() {
		return (String) super.get("cdnProfilo");
	}

	public String getStrMittente() {
		return (String) super.get("strMittente");
	}

}
