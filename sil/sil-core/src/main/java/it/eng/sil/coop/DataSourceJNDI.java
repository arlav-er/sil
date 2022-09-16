/*
 * Created on 14-Apr-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.coop;

import com.engiweb.framework.dbaccess.Configurator;
import com.engiweb.framework.dbaccess.ConnectionPoolDescriptor;
import com.engiweb.framework.dbaccess.ConnectionPoolParameter;

import it.eng.sil.Values;

/**
 * 
 * Questa classe legge dal config singleton all'interno del Data_access.xml per prelevare il JNDI della connessione del
 * SIL
 * 
 * @author rolfini
 * 
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class DataSourceJNDI {

	/**
	 * 
	 */
	public DataSourceJNDI() {
	}

	public String getJndi() {

		Configurator configurator = Configurator.getInstance();

		ConnectionPoolDescriptor pool = configurator.getConnectionPoolDescriptor(Values.DB_SIL_DATI);

		ConnectionPoolParameter dataSourceParameter = pool.getConnectionPoolParameter("jndiName");
		String dataSourceName = dataSourceParameter.getValue();

		return dataSourceName;

	}

}
