/*
 * Created on 06-Apr-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.services;

import javax.jms.JMSException;

import it.eng.sil.coop.CoopApplicationException_Lavoratore;

/**
 * @author rolfini
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public interface IFaceService {

	public void send(javax.jms.Message msg) throws CoopApplicationException_Lavoratore, JMSException;

}
