/*
 * Created on 07-Feb-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.messages.jmsmessages;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import it.eng.sil.coop.messages.AbstractMessage;
import it.eng.sil.coop.messages.TestataMessageTO;
import it.eng.sil.coop.queues.IFaceQueue;
import it.eng.sil.coop.utils.MessageBundle;

/**
 * @author rolfini
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public abstract class AbstractNotificaLavoratoreSILMessage extends AbstractMessage {

	private static final Logger log = Logger.getLogger(AbstractNotificaLavoratoreSILMessage.class.getName());

	protected final int maxRedeliveries = 100;

	protected String codiceFiscale = null;
	protected String contenutoMessaggio = null;

	public AbstractNotificaLavoratoreSILMessage() {

	}

	public void send(IFaceQueue Q) throws Exception {

		ArrayList appParam = new ArrayList();

		appParam.add(codiceFiscale);
		appParam.add(contenutoMessaggio);

		super.send(Q, appParam);
	}

	/**
	 * @param string
	 */
	public void setCodiceFiscale(String string) {
		codiceFiscale = string;
	}

	/**
	 * @param string
	 */
	public void setContenutoMessaggio(String string) {
		contenutoMessaggio = string;
	}

	/**
	 * @return
	 */
	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	/**
	 * @return
	 */
	public String getContenutoMessaggio() {
		return contenutoMessaggio;
	}

	public void setTestata(TestataMessageTO _testata) {

		super.setTestata(_testata);
		testata.setServizio("NotificaLavoratoreSIL");
		testata.setMaxRedeliveries(maxRedeliveries);

	}

	/**
	 * @param msgCode
	 *            il codice del messaggio in messages_it-IT.properties
	 * @param params
	 *            la lista dei parametri (%) del messaggio (puo' essere null)
	 */
	public void setContenutoMessaggio(int msgCode, java.util.Vector params) {
		setContenutoMessaggio(MessageBundle.getMessage(msgCode, params));
	}

}
