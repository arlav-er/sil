/*
 * Created on 07-Feb-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.messages.dbmessages;

import java.sql.Clob;
import java.util.ArrayList;

import it.eng.sil.coop.messages.AbstractMessage;
import it.eng.sil.coop.queues.IFaceQueue;

/**
 * @author rolfini
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class DbMessage extends AbstractMessage {

	private Clob contenutoMessaggio = null;
	private int prgMsg = 0;

	public DbMessage() {
	}

	public void send(IFaceQueue Q) throws Exception {
		ArrayList appParam = buildMessageContent(contenutoMessaggio);
		super.send(Q, appParam);
	}

	private ArrayList buildMessageContent(Clob contenuto) {

		ArrayList param = new ArrayList();

		long inizio = 1;
		long fine = 1;

		try {
			long lunghezza = contenuto.length();

			for (int i = 0; (inizio < lunghezza); i++) {
				fine = contenuto.position(",", inizio);

				param.add(i, contenuto.getSubString(inizio, (int) (fine - inizio)));

				inizio = fine;
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return param;

	}

	// GETTER E SETTER

	public int getPrgMsg() {
		return this.prgMsg;
	}

	public void setPrgMsg(int _prgMsg) {
		this.prgMsg = _prgMsg;
	}

	/**
	 * @return
	 */
	public Clob getContenutoMessaggio() {
		return contenutoMessaggio;
	}

	/**
	 * @param clob
	 */
	public void setContenutoMessaggio(Clob clob) {
		contenutoMessaggio = clob;
	}

}
