/*
 * Creato il 29-ott-04
 * Author: vuoto
 * 
 */
package it.eng.sil.bean.menu;

/**
 * @author vuoto
 * 
 */
public class Funzione {

	private int cdnFunzione;

	private java.lang.String descrizione;

	/**
	 * @return
	 */
	public int getCdnFunzione() {
		return cdnFunzione;
	}

	/**
	 * @param i
	 */
	public void setCdnFunzione(int i) {
		cdnFunzione = i;
	}

	/**
	 * @return
	 */
	public java.lang.String getDescrizione() {
		return descrizione;
	}

	/**
	 * @param string
	 */
	public void setDescrizione(java.lang.String string) {
		descrizione = string;
	}

	public StringBuffer toXML() {

		StringBuffer buf = new StringBuffer();
		buf.append("<funzione " + " idmenu=\"" + this.hashCode() + "\"" + " cdnfunzione=\"" + cdnFunzione + "\""
				+ " descrizionefunzione=\"" + descrizione + "\"" + " />");
		return buf;
	}

}
