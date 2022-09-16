/*
 * Creato il 3-ago-04
 * Author: vuoto
 * 
 */

package it.eng.sil.module.profil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dispatching.module.AbstractHttpModule;
import com.engiweb.framework.error.EMFErrorSeverity;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.MessageAppender;
import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.Values;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

/**
 * @author vuoto
 * 
 */
public class ProfilXMLSalva extends AbstractHttpModule {

	private static final long serialVersionUID = -5009631002624484439L;
	public static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProfilXMLSalva.class.getName());

	public void service(SourceBean request, SourceBean response) {

		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		OracleConnection oracleConn = null;
		HttpServletRequest httpRequest = this.getHttpRequest();

		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		int codUtente = user.getCodut();

		String cdnProfiloStr = httpRequest.getParameter("cdnprofilo");

		if (cdnProfiloStr != null) {
			int cdnProfilo = Integer.valueOf(cdnProfiloStr);

			try {
				DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
				dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);
				java.sql.Connection connection = dataConnection.getInternalConnection();
				oracleConn = Utils.getOracleConnection(connection);
				oracleConn.setAutoCommit(false);

				Set<String> setComp = new HashSet<String>();
				List<String> listAttrComp = new ArrayList<String>();
				List<String> listAttr = new ArrayList<String>();
				List<String> listAttrPos = new ArrayList<String>();

				List<String> listCompBreak = new ArrayList<String>();
				List<String> listAttrBreak = new ArrayList<String>();

				Enumeration<String> _enum = httpRequest.getParameterNames();
				while (_enum.hasMoreElements()) {
					String nomePar = _enum.nextElement();
					// String valPar=httpRequest.getParameter(nomePar);
					if (nomePar.startsWith("CK_")) {
						String codici = nomePar.substring(3);
						StringTokenizer st = new StringTokenizer(codici, "_");
						String comp = st.nextToken();
						setComp.add(comp);

						if (st.hasMoreTokens()) {
							// Vuol dire che si e' selezionato un attributo
							// e quindi serve la coppia comp-attr
							listAttrComp.add(comp);
							listAttr.add(st.nextToken());
							// recuperiamo la posizione
							String posizione = httpRequest.getParameter("POS_CK_" + codici);
							if (posizione == null) {
								posizione = "0";
							}

							try {
								posizione = String.valueOf(Integer.parseInt(posizione));
							} catch (NumberFormatException ex) {
								posizione = "0";
							}

							listAttrPos.add(posizione);

						}

					}

					if (nomePar.startsWith("RET_CK_")) {
						String codici = nomePar.substring(7);
						StringTokenizer st = new StringTokenizer(codici, "_");
						String comp = st.nextToken();
						listCompBreak.add(comp);
						listAttrBreak.add(st.nextToken());

					}

				}

				Object strComp[] = setComp.toArray();
				Object strAttrComp[] = listAttrComp.toArray();
				Object strAttr[] = listAttr.toArray();
				Object strAttrPos[] = listAttrPos.toArray();

				Object strCompBreak[] = listCompBreak.toArray();
				Object strAttrBreak[] = listAttrBreak.toArray();

				ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor("INT_ARRAY", oracleConn);
				ARRAY arrayComp = new ARRAY(descriptor, oracleConn, strComp);
				ARRAY arrayAttrComp = new ARRAY(descriptor, oracleConn, strAttrComp);
				ARRAY arrayAttr = new ARRAY(descriptor, oracleConn, strAttr);
				ARRAY arrayAttrPos = new ARRAY(descriptor, oracleConn, strAttrPos);
				ARRAY arrayCompBreak = new ARRAY(descriptor, oracleConn, strCompBreak);
				ARRAY arrayAttrBreak = new ARRAY(descriptor, oracleConn, strAttrBreak);

				OraclePreparedStatement ps = (OraclePreparedStatement) oracleConn
						.prepareStatement("begin pg_profil.set_comp_attr(?, ?, ?, ?,?, ?, ?, ?); end;");

				ps.setInt(1, cdnProfilo);
				ps.setARRAY(2, arrayComp);
				ps.setARRAY(3, arrayAttrComp);
				ps.setARRAY(4, arrayAttr);
				ps.setARRAY(5, arrayAttrPos);

				ps.setARRAY(6, arrayCompBreak);
				ps.setARRAY(7, arrayAttrBreak);

				ps.setInt(8, codUtente);

				ps.execute();
				MessageAppender.appendMessage(response, MessageCodes.General.OPERATION_SUCCESS);
				oracleConn.commit();

			} catch (Exception ex) {

				try {
					if (oracleConn != null)
						oracleConn.rollback();
				} catch (SQLException e) {
					LogUtils.logError(this.getClass().getName(), e.getMessage(), e, this);
				}

				this.getErrorHandler()
						.addError(new EMFUserError(EMFErrorSeverity.ERROR, MessageCodes.General.OPERATION_FAIL));

				LogUtils.logError(this.getClass().getName(), ex.getMessage(), ex, this);

				it.eng.sil.util.TraceWrapper.debug(_logger, "service", (Exception) ex);

			} finally {
				com.engiweb.framework.dbaccess.Utils.releaseResources(dataConnection, sqlCommand, dataResult);
			}

		}

	}

}