package it.eng.sil.module.pattoonline;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.util.QueryExecutorObject;

import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.security.User;
import it.eng.sil.util.Utils;
import it.eng.sil.utils.gg.GG_Utils;

public class MakePattoOnLineModule extends AbstractSimpleModule{

	/**
	 * 
	 */
	private static final long serialVersionUID = 399679446759381483L;

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(MakePattoOnLineModule.class.getName());
 
	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse )
			throws Exception {
		ReportOperationResult reportOperation = new ReportOperationResult(this, serviceResponse);
		disableMessageIdFail();
		disableMessageIdSuccess();	
		
		User user = (User) getRequestContainer().getSessionContainer().getAttribute(User.USERID);

		BigDecimal p_cdnUtente = new BigDecimal(user.getCodut());

		String prgPatto = Utils.notNull(serviceRequest.getAttribute("PRGPATTOLAVORATORE"));
		String numKLo = Utils.notNull(serviceRequest.getAttribute("NUMKLOPATTOLAVORATORE"));
		
		QueryExecutorObject qExec = null;
		DataConnection dc = null;
		try
		{
			qExec = GG_Utils.getQueryExecutorObject();
			dc = qExec.getDataConnection();
			qExec.setStatement(SQLStatements.getStatement("MAKE_PATTOACCORDO_ONLINE"));
			qExec.setType(QueryExecutorObject.UPDATE);
			List<DataField> params = new ArrayList<DataField>();
			params.add(dc.createDataField("NUMKLOPATTOLAVORATORE", Types.NUMERIC, new BigDecimal(numKLo)));
			params.add(dc.createDataField("CDNUTMOD", Types.NUMERIC, p_cdnUtente));
			params.add(dc.createDataField("PRGPATTOLAVORATORE", Types.NUMERIC, new BigDecimal(prgPatto)));
			qExec.setInputParameters(params);
			Object result = qExec.exec();
			boolean updatePattoOnLine;
			if (result == null || !(result instanceof Boolean && ((Boolean) result).booleanValue() == true)) {
				_logger.error("Impossibile eseguire update dei dati in AM_PATTO_LAVORATORE");
				updatePattoOnLine = Boolean.FALSE;
				reportOperation.reportFailure(MessageCodes.General.UPDATE_FAIL);
			}else{
				updatePattoOnLine = Boolean.TRUE;
				reportOperation.reportSuccess(MessageCodes.General.UPDATE_SUCCESS);
			}
			
		}catch(Throwable e) {
			_logger.error("Errore: " + e);
		} finally {
			_logger.info("Connessione rilasciata");
			com.engiweb.framework.dbaccess.Utils.releaseResources(dc, null, null);
		}

	}

}
