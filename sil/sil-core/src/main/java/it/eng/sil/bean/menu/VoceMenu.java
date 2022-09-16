/*
 * Creato il 29-ott-04
 * Author: vuoto
 * 
 */
package it.eng.sil.bean.menu;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author vuoto
 * 
 */
public class VoceMenu {

	private int livello = 0;

	private java.lang.String descrizione = null;

	private it.eng.sil.bean.menu.Funzione funzione;

	// private int prog;
	private int cdnVoceMenu;

	private java.util.List figli;

	private VoceMenu padre;

	/**
	 * @return
	 */
	public int getLivello() {
		return livello;
	}

	/**
	 * @param i
	 */
	public void setLivello(int i) {
		livello = i;
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

	/**
	 * @return
	 */
	public int getCdnVoceMenu() {
		return cdnVoceMenu;
	}

	/**
	 * @param i
	 */
	public void setCdnVoceMenu(int i) {
		cdnVoceMenu = i;
	}

	/**
	 * @return
	 */
	public it.eng.sil.bean.menu.Funzione getFunzione() {
		return funzione;
	}

	/**
	 * @param funzione
	 */
	public void setFunzione(it.eng.sil.bean.menu.Funzione funzione) {
		this.funzione = funzione;
	}

	// /**
	// * @return
	// */
	// public int getProg() {
	// return prog;
	// }
	//
	// /**
	// * @param i
	// */
	// public void setProg(int i) {
	// prog = i;
	// }

	/**
	 * @return
	 */
	public java.util.List getFigli() {
		return figli;
	}

	/**
	 * @param list
	 */
	public void setFigli(java.util.List list) {
		figli = list;
	}

	/**
	 * @return
	 */
	public VoceMenu getPadre() {
		return padre;
	}

	/**
	 * @param menu
	 */
	public void setPadre(VoceMenu menu) {
		padre = menu;
	}

	public VoceMenu() {
		figli = new ArrayList();
	}

	public StringBuffer toXML(StringBuffer buf) {

		// StringBuffer buf = new StringBuffer();

		buf.append("<vocemenu " + " idvocemenu=\"" + this.hashCode() + "\"" + " cdnvocemenu=\"" + hashCode() + "\""
				+ " descrizionevocemenu=\"" + descrizione + "\"" + " livello=\"" + livello + "\"");
		buf.append(">");

		if (funzione != null)
			buf.append(funzione.toXML());

		Iterator iter = this.getFigli().iterator();
		while (iter.hasNext()) {
			VoceMenu voce = (VoceMenu) iter.next();
			voce.toXML(buf);
		}

		buf.append("</vocemenu>");
		return buf;
	}

	public StringBuffer toXML() {

		StringBuffer buf = new StringBuffer();
		return toXML(buf);

	}

	void save(Connection conn, int cdnMenu, int pos) throws SQLException {

		doSave(conn, cdnMenu, pos);

		for (int i = 0; i < figli.size(); i++) {

			VoceMenu figlio = (VoceMenu) figli.get(i);
			figlio.save(conn, cdnMenu, i);
		}
	}

	private void doSave(Connection conn, int cdnMenu, int pos) throws SQLException {

		CallableStatement cs = (CallableStatement) conn
				.prepareCall("{ CALL pg_profil.crea_voce_menu(?, ?, ?, ?, ?, ?)}");

		cs.registerOutParameter(1, Types.INTEGER);

		cs.setInt(2, cdnMenu);

		// Aggiunta in data 30-11-2004
		// perche' nella tabella TS_VOCE_MENU
		// la dimensione del campo e' di 30 char
		if (this.descrizione != null) {
			if (this.descrizione.length() > 30) {
				this.descrizione = this.descrizione.substring(0, 30);
			}
		}

		cs.setString(3, this.descrizione);
		cs.setInt(4, pos);

		if (getPadre() != null)
			cs.setInt(5, getPadre().getCdnVoceMenu());
		else {
			cs.setNull(5, Types.INTEGER);
		}

		if (funzione != null) {
			cs.setInt(6, funzione.getCdnFunzione());
		} else {
			cs.setNull(6, Types.INTEGER);
		}

		cs.execute();

		cdnVoceMenu = cs.getInt(1);

	}

}
