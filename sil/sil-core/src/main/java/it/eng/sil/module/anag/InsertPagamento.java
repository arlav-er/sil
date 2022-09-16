/*
 * Creato il 15-nov-06
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.module.anag;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.RequestContainer;
import com.engiweb.framework.base.SessionContainer;
import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.util.QueryExecutorObject;

/**
 * @author melandri
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.afExt.utils.TransactionQueryExecutor;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;

public class InsertPagamento extends AbstractSimpleModule {
	public void service(SourceBean request, SourceBean response) throws Exception {
		int idSuccess = this.disableMessageIdSuccess();
		int idFail = this.disableMessageIdFail();
		RequestContainer requestContainer = getRequestContainer();
		SessionContainer sessionContainer = (SessionContainer) requestContainer.getSessionContainer();
		User user = (User) sessionContainer.getAttribute(User.USERID);
		TransactionQueryExecutor transExec = null;
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);
		RequestContainer r = this.getRequestContainer();
		try {
			transExec = new TransactionQueryExecutor(getPool());
			this.enableTransactions(transExec);

			transExec.initTransaction();

			BigDecimal prgPagamEsonero = doNextVal(request, response);

			if (prgPagamEsonero == null) {
				throw new Exception("Impossibile leggere S_CM_PAGAMENTO_ESON.NEXTVAL");
			}

			if (request.getAttribute("prgPagamEsonero") != null) {
				request.delAttribute("prgPagamEsonero");
			}
			request.setAttribute("prgPagamEsonero", prgPagamEsonero);

			DataConnection dataConnection = null;

			DataResult dr = null;
			dataConnection = transExec.getDataConnection();

			String p_prgPagamEson = request.getAttribute("prgPagamEsonero").toString();
			Object p_prgRichEson = request.getAttribute("prgRichEsonero");
			String p_datPagamento = StringUtils.getAttributeStrNotNull(request, "DATPAGAMENTO");
			Object p_decImporto = request.getAttribute("DECIMPORTO");
			String p_cdnUtente = String.valueOf(user.getCodut());
			String p_NumKloPagEson = "1";
			String p_datInizioComp = StringUtils.getAttributeStrNotNull(request, "DATINIZIOCOMP");
			String p_datFineComp = StringUtils.getAttributeStrNotNull(request, "DATFINECOMP");

			SourceBean statementSB = (SourceBean) getConfig().getAttribute("QUERY_INSERT_PAGAMENTO");
			String statement = statementSB.getAttribute("STATEMENT").toString();
			String sqlStr = SQLStatements.getStatement(statement);

			List inputParameter = new ArrayList();
			inputParameter.add(
					dataConnection.createDataField("prgPagamEsonero", Types.BIGINT, new BigInteger(p_prgPagamEson)));
			inputParameter.add(dataConnection.createDataField("prgRichEsonero", Types.BIGINT, p_prgRichEson));
			inputParameter.add(dataConnection.createDataField("DATPAGAMENTO", Types.VARCHAR, p_datPagamento));
			inputParameter.add(dataConnection.createDataField("DECIMPORTO", Types.DOUBLE, p_decImporto));
			inputParameter
					.add(dataConnection.createDataField("p_cdnUtente", Types.BIGINT, new BigInteger(p_cdnUtente)));
			inputParameter
					.add(dataConnection.createDataField("p_cdnUtente", Types.BIGINT, new BigInteger(p_cdnUtente)));
			inputParameter.add(dataConnection.createDataField("NUMKLOPAGAMENTOESONERO", Types.BIGINT, p_NumKloPagEson));
			inputParameter.add(dataConnection.createDataField("DATINIZIOCOMP", Types.VARCHAR, p_datInizioComp));
			inputParameter.add(dataConnection.createDataField("DATFINECOMP", Types.VARCHAR, p_datFineComp));

			QueryExecutorObject queryExecObj = new QueryExecutorObject();

			queryExecObj.setRequestContainer(null);
			queryExecObj.setResponseContainer(null);
			queryExecObj.setDataConnection(dataConnection);
			queryExecObj.setInputParameters(inputParameter);
			queryExecObj.setType(QueryExecutorObject.INSERT);
			queryExecObj.setStatement(sqlStr);

			queryExecObj.setTransactional(true);
			queryExecObj.setDontForgetException(true);

			Object returned = queryExecObj.exec();

			if (returned instanceof Boolean && ((Boolean) returned).booleanValue() == true) {
				r.setServiceRequest(request);
			} else {
				throw new Exception("impossibile inserire in CM_PAGAMENTO_ESON in transazione");
			}

			transExec.commitTransaction();

			reportOperation.reportSuccess(idSuccess);
		} catch (Exception e) {
			transExec.rollBackTransaction();
			reportOperation.reportFailure(MessageCodes.General.INSERT_FAIL, e, "services()", "insert in transazione");
		} finally {
		}
	}
}
