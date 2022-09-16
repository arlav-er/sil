/*
 * Creato il 17-feb-05
 * Author: vuoto
 * 
 */
package it.eng.sil.junit;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.Constants;
import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.ResponseContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.configuration.ConfigSingleton;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dispatching.action.ActionFactory;
import com.engiweb.framework.dispatching.action.ActionIFace;
import com.engiweb.framework.dispatching.service.DefaultRequestContext;
import com.engiweb.framework.dispatching.service.RequestContextIFace;
import com.engiweb.framework.error.EMFErrorHandler;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.sil.Values;
import it.eng.sil.security.User;

/**
 * @author vuoto
 * 
 */
public class ActionTestCase {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ActionTestCase.class.getName());

	private String actionName = null;
	private SourceBean serviceRequest = null;
	private SourceBean serviceResponse = null;
	private RequestContainer requestContainer = null;
	private SessionContainer sessionContainer = null;
	private ResponseContainer responseContainer = null;

	private ActionTestCase() {
	}

	public ActionTestCase(String _actionName) {

		actionName = _actionName;
		BaseTestCase bt = BaseTestCase.getInstance();

		_logger.debug("******************************************************");

		_logger.debug("*** Test della action " + actionName);

		_logger.debug("******************************************************");

		try {
			serviceRequest = new SourceBean(Constants.SERVICE_REQUEST);
			serviceRequest.setAttribute(Constants.ACTION_NAME, _actionName);
		} catch (SourceBeanException e3) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore in ActionTestCase", e3);

		}

		requestContainer = new RequestContainer();

		RequestContainer.setRequestContainer(requestContainer);
		requestContainer.setServiceRequest(serviceRequest);
		try {
			serviceRequest.setAttribute("ACTION_NAME", actionName);
		} catch (SourceBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sessionContainer = new SessionContainer(true);
		requestContainer.setSessionContainer(sessionContainer);

		responseContainer = new ResponseContainer();
		responseContainer.setErrorHandler(new EMFErrorHandler());

	}

	public void execService() throws Exception {

		try {
			loadUser();
		} catch (Exception e3) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					"Errore in actionTestCase. Errore nel caricamento dell'utente in sessione", e3);

			throw e3;
		}

		try {
			serviceResponse = new SourceBean(Constants.SERVICE_RESPONSE);
		} catch (SourceBeanException e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore in ActionTestCase.", e);

		}
		responseContainer.setServiceResponse(serviceResponse);

		requestContainer.setChannelType(Constants.HTTP_CHANNEL);
		RequestContextIFace requestContext = new DefaultRequestContext(requestContainer, responseContainer);

		ActionIFace action = null;
		try {
			action = ActionFactory.getAction(requestContainer);
			((RequestContextIFace) action).setRequestContext(requestContext);

			_logger.debug(serviceRequest.toXML(false));

			action.service(serviceRequest, serviceResponse);
			_logger.debug(serviceResponse.toXML(false));

		} catch (Exception e2) {
			it.eng.sil.util.TraceWrapper.fatal(_logger,
					"Errore in ActionTestCase. Errore generico nell'esecuzione della action" + actionName, e2);

			throw e2;

		}

	}

	/**
	 * @return
	 */
	public SourceBean getServiceResponse() {
		return serviceResponse;
	}

	/**
	 * @param bean
	 */
	public void setServiceRequest(SourceBean bean) {
		serviceRequest = bean;
	}

	/**
	 * @param bean
	 */
	public void setServiceResponse(SourceBean bean) {
		serviceResponse = bean;
	}

	/**
	 * @return
	 */
	public SourceBean getServiceRequest() {
		return serviceRequest;
	}

	private void loadUser() throws Exception {

		DataConnection dataConnection = null;

		DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
		dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);
		List inputParameter = new ArrayList(1);
		QueryExecutorObject queryExecObj = new QueryExecutorObject();

		queryExecObj.setDataConnection(dataConnection);

		String statement = "select t.cdnut cod_utente, t.strlogin username, " + "t.strcognome cognome,"
				+ "t.strnome nome " + " from ts_utente t" + " where upper(t.strlogin) = UPPER(?)";

		queryExecObj.setStatement(statement);
		queryExecObj.setType("SELECT");
		queryExecObj.setTransactional(false);

		String login = (String) ConfigSingleton.getInstance().getAttribute("USER_JUNIT.USER.strlogin");

		inputParameter.add(dataConnection.createDataField("", Types.VARCHAR, login));

		queryExecObj.setInputParameters(inputParameter);

		Object retVal = queryExecObj.exec();
		SourceBean rowsSourceBean = null;
		if (retVal instanceof SourceBean) {
			rowsSourceBean = (SourceBean) retVal;
		} else {
			throw new Exception("Errore nel recuper dell'utente di sessione");
		}

		String username = (String) rowsSourceBean.getAttribute("ROW.USERNAME");
		BigDecimal codUtente = (BigDecimal) rowsSourceBean.getAttribute("ROW.COD_UTENTE");

		String nome = (String) rowsSourceBean.getAttribute("ROW.nome");
		String cognome = (String) rowsSourceBean.getAttribute("ROW.cognome");

		sessionContainer.setAttribute(Values.USERID, username);
		sessionContainer.setAttribute(Values.CODUTENTE, codUtente);
		sessionContainer.setAttribute("AF_LANGUAGE", "it");
		sessionContainer.setAttribute("AF_COUNTRY", "IT");

		// Nuovo utente

		User user = new User(codUtente.intValue(), username, nome, cognome);
		sessionContainer.setAttribute(User.USERID, user);

	}

	/**
	 * @return
	 */
	public RequestContainer getRequestContainer() {
		return requestContainer;
	}

	/**
	 * @return
	 */
	public ResponseContainer getResponseContainer() {
		return responseContainer;
	}

	/**
	 * @return
	 */
	public SessionContainer getSessionContainer() {
		return sessionContainer;
	}

}