/*
 * Creato il 16-giu-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.sms.gateways;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.DataResultInterface;
import com.engiweb.framework.dbaccess.sql.result.ScrollableDataResult;

import it.eng.sil.Values;

/**
 * @author vuoto
 * 
 *         Per modificare il modello associato al commento di questo tipo generato, aprire
 *         Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
public class GatewayFactory {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(GatewayFactory.class.getName());
	private Map parameters = new HashMap();
	private String provider = null;

	public GatewayFactory() {
		loadPars();

	}

	public GatewayFactory(DataConnection dataConnection) throws Exception {
		loadPars(dataConnection);
	}

	public IGateway getGateway() {

		IGateway gateway = null;
		if ("SILER".equalsIgnoreCase(provider)) {
			gateway = new SilerGtw();
			gateway.setParameters(parameters);

		} else if ("SMSITALIA".equalsIgnoreCase(provider)) {
			gateway = new SmsItaliaGw();
			gateway.setParameters(parameters);

		} else if ("NETFUN".equalsIgnoreCase(provider)) {
			gateway = new NetFunGw();
			gateway.setParameters(parameters);
		} else if ("INFOTIM".equalsIgnoreCase(provider)) {
			gateway = new InfoTIMGw();
			gateway.setParameters(parameters);
		} else if ("SMASHAE4".equalsIgnoreCase(provider)) {
			gateway = new SmashAe4Gw();
			gateway.setParameters(parameters);
		} else if ("AMXSMS".equalsIgnoreCase(provider)) {
			gateway = new AmxSmsGw();
			gateway.setParameters(parameters);
		} else if ("TIM-SSC2".equalsIgnoreCase(provider)) {
			gateway = new TimSsc2Gw();
			gateway.setParameters(parameters);
		} else if ("TIM-CONSIP-2019".equalsIgnoreCase(provider)) {
			gateway = new TimConsip2019Gw();
			gateway.setParameters(parameters);
		}
		return gateway;
	}

	private void loadPars() {
		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;

		try {
			DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
			dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

			String statement = SQLStatements.getStatement("CARICA_PROVIDER_SMS");
			sqlCommand = dataConnection.createSelectCommand(statement);

			List inputParameter = new ArrayList();

			dataResult = sqlCommand.execute(inputParameter);

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();

			List righe = sb.getAttributeAsVector("ROW");
			int size = righe.size();

			for (int i = 0; i < size; i++) {
				SourceBean riga = (SourceBean) righe.get(i);

				if (i == 0) {
					provider = (String) riga.getAttribute("provider");
				}

				String parametro = (String) riga.getAttribute("parametro");
				String valore = (String) riga.getAttribute("valore");
				parameters.put(parametro, valore);

			}
		} catch (com.engiweb.framework.error.EMFInternalError ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "QueryExecutor::executeQuery:", (Exception) ex);

		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);
		}

	}

	private void loadPars(DataConnection dataConnection) throws Exception {
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;

		try {
			String statement = SQLStatements.getStatement("CARICA_PROVIDER_SMS");
			sqlCommand = dataConnection.createSelectCommand(statement);

			List inputParameter = new ArrayList();

			dataResult = sqlCommand.execute(inputParameter);

			ScrollableDataResult scrollableDataResult = (ScrollableDataResult) dataResult.getDataObject();
			SourceBean sb = scrollableDataResult.getSourceBean();

			List righe = sb.getAttributeAsVector("ROW");
			int size = righe.size();

			for (int i = 0; i < size; i++) {
				SourceBean riga = (SourceBean) righe.get(i);

				if (i == 0) {
					provider = (String) riga.getAttribute("provider");
				}

				String parametro = (String) riga.getAttribute("parametro");
				String valore = (String) riga.getAttribute("valore");
				parameters.put(parametro, valore);

			}
		} catch (com.engiweb.framework.error.EMFInternalError ex) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "QueryExecutor::executeQuery:", (Exception) ex);

		} finally {
			if (dataResult != null) {
				DataResultInterface resultInterface = dataResult.getDataObject();
				if ((resultInterface != null) && (resultInterface instanceof ScrollableDataResult)) {
					((ScrollableDataResult) resultInterface).close();
				}
			}
			if (sqlCommand != null) {
				sqlCommand.close();
			}
		}
	}

	/**
	 * @return
	 */
	public Map getParameters() {
		return parameters;
	}

	/**
	 * @return
	 */
	public String getProvider() {
		return provider;
	}

}