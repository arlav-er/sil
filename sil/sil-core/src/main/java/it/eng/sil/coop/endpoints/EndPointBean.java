/*
 * Created on 05-Apr-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.endpoints;

/**
 * @author rolfini
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class EndPointBean {

	private String name = null;
	private String url = null;
	// Savino 20/11/2006: aggiunte due proprieta' ed i relativi metodi getter e setter: codProvincia e flgPoloAttivo
	private String codProvincia;
	private String flgPoloAttivo;

	/**
	 * 
	 */
	protected EndPointBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected EndPointBean(String _name, String _url) {
		name = _name;
		url = _url;

	}

	public boolean equals(String _name) {

		return (name.equals(_name));

	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * nome endpoint (nome servizio + codprovincia/sistema)
	 * 
	 * @param s
	 */
	public void setName(String s) {
		name = s;
	}

	/**
	 * url associato al nome endPoint
	 * 
	 * @param s
	 */
	public void setUrl(String s) {
		url = s;
	}

	/**
	 * @return
	 */
	public String getCodProvincia() {
		return codProvincia;
	}

	/**
	 * @return
	 */
	public String getFlgPoloAttivo() {
		return flgPoloAttivo;
	}

	/**
	 * codice della Provincia/sistema al quale è riferito l'endpoint
	 * 
	 * @param s
	 */
	public void setCodProvincia(String s) {
		codProvincia = s;
	}

	/**
	 * campi flag con la possibilità di inserire S, N o Null. Indica se il polo di destinazione è attivo rispetto alla
	 * cooperazione
	 * 
	 * @param s
	 */
	public void setFlgPoloAttivo(String s) {
		flgPoloAttivo = s;
	}

}
