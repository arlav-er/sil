/*
 * Creato il 3-ago-04
 * Author: vuoto
 * 
 */

package it.eng.sil.module.profil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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
 * @author giuliani extend vuoto
 * 
 */
public class ProfVisibilitaXMLSalva extends AbstractHttpModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ProfVisibilitaXMLSalva.class.getName());

	public void service(SourceBean request, SourceBean response) {

		DataConnection dataConnection = null;
		SQLCommand sqlCommand = null;
		DataResult dataResult = null;
		OracleConnection oracleConn = null;
		HttpServletRequest httpRequest = this.getHttpRequest();

		SessionContainer sessionContainer = getRequestContainer().getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		int codUtente = user.getCodut();

		String cdnGruppoStr = httpRequest.getParameter("CDNGRUPPO");

		if (cdnGruppoStr != null) {
			int cdnGruppo = Integer.valueOf(cdnGruppoStr);
			try {
				DataConnectionManager dataConnectionManager = DataConnectionManager.getInstance();
				dataConnection = dataConnectionManager.getConnection(Values.DB_SIL_DATI);

				java.sql.Connection connection = dataConnection.getInternalConnection();
				oracleConn = Utils.getOracleConnection(connection);

				oracleConn.setAutoCommit(false);

				List<String> listComp = new ArrayList<String>();
				List<String> listFunComp = new ArrayList<String>();
				List<String> listValComp = new ArrayList<String>();

				Enumeration<String> _enum = httpRequest.getParameterNames();
				while (_enum.hasMoreElements()) {
					String nomePar = _enum.nextElement();
					String valore = (String) httpRequest.getParameter(nomePar);

					if ((valore != null) && (!valore.equals(""))) {
						// Il nomePar è sempre nella forma
						// SEL_codFunzione_codComponente es.: SEL_52_246
						// Modifica Paolo Roccetti del 15/09/2004: visto che per
						// alcuni componenti è necessario
						// impostare i filtri sia per il lavoratore che per
						// l'azienda il nuovo formato
						// dei nomi è il seguente:
						// SEL_codFunzione_codComponente_codTipoFiltro dove
						// codTipoFiltro può essere 'L' o 'U'
						// a seconda che si tratti di un filtro sul lavoratore o
						// sull'azienda.
						// Per quanto riguarda l'elaborazione è tutto come
						// prima.
						if (nomePar.startsWith("SEL_")) {
							String codici = nomePar.substring(4);
							StringTokenizer st = new StringTokenizer(codici, "_");
							listFunComp.add(st.nextToken()); // codFunzione
							listComp.add(st.nextToken()); // codComponente
							listValComp.add(valore); // filtro

						}
					}

				}

				Object strComp[] = listComp.toArray();
				Object strFunComp[] = listFunComp.toArray();
				Object strValComp[] = listValComp.toArray();

				ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor("INT_ARRAY", oracleConn);
				ARRAY arrayComp = new ARRAY(descriptor, oracleConn, strComp);
				ARRAY arrayValComp = new ARRAY(descriptor, oracleConn, strValComp);

				OraclePreparedStatement ps = (OraclePreparedStatement) oracleConn
						.prepareStatement("begin pg_profil.set_visibilita_grupo(?, ?, ?, ?); end;");
				// Al momento il codFunzione non viene passato alla SP xchè
				// manca il campo nel DB e non
				// sappiamo se deve essere aggiunto o meno. Aspettiamo il
				// ritorno di Angela....
				ps.setInt(1, cdnGruppo);
				ps.setARRAY(2, arrayComp);
				ps.setARRAY(3, arrayValComp);
				ps.setInt(4, codUtente);

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
// class ProfVisibilitaXMLSalva
