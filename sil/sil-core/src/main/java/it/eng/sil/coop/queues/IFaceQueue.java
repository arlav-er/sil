/*
 * Created on 13-Apr-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop.queues;

/**
 * @author rolfini
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public interface IFaceQueue {

	public String getFactory();

	public String getQueue();

	public void init(String _dataSourceJndi) throws Exception;

}
