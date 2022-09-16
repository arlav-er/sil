/*
 * Created on Sep 12, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.services;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.log4j.Logger;

import it.eng.sil.coop.CoopApplicationException_Lavoratore;

/**
 * @author savino
 * 
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class InitContestoCode implements IFaceService {

	private static final Logger log = Logger.getLogger(InitContestoCode.class.getName());

	public void send(Message msg) throws CoopApplicationException_Lavoratore, JMSException {

		log.info("InitContestoCode:: Messaggio di inizializzazione correttamente gestito");

	}
}