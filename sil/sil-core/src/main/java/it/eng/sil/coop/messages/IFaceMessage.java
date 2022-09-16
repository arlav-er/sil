/*
 * Created on 06-Apr-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.messages;

import java.util.Map;

import javax.jms.ObjectMessage;

import it.eng.sil.coop.queues.IFaceQueue;

/**
 * @author rolfini
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public interface IFaceMessage {

	public void callWebservice() throws Exception;

	/**
	 * setObjectMessage Costruisce il messaggio a partire dall'ObjectMessage passato come parametro
	 * 
	 * @param msg
	 * @throws Exception
	 */
	public void setObjectMessage(ObjectMessage msg) throws Exception;

	/**
	 * setDataSourceJndi Imposta il JNDI del datasource da passare ai componenti incaricati dell'eventuale reperimento
	 * degli endpoint e delle code
	 * 
	 * @param _dataSourceJndi
	 */
	public void setDataSourceJndi(String _dataSourceJndi);

	/**
	 * setServiceParameters Costruisce il messaggio a partire dai parametri di chiamata del servizio
	 * 
	 * @param param
	 *            ServiceParameters contenente i parametri come coppie key, value
	 */
	public void setServiceParameters(Map param);

	/**
	 * setTestata
	 * 
	 * imposta la testata nel messaggio
	 * 
	 * @param _testata
	 */
	public void setTestata(TestataMessageTO _testata);

	/**
	 * send
	 * 
	 * spedisce il messaggio in coda
	 * 
	 * @param Q
	 * @throws Exception
	 */
	public void send(IFaceQueue Q) throws Exception;

}
