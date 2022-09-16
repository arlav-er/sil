package it.eng.sil.module.ido;

import java.util.Iterator;
import java.util.Vector;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.SQLCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;

import it.eng.afExt.utils.LogUtils;
import it.eng.afExt.utils.MessageCodes;
import it.eng.afExt.utils.ReportOperationResult;
import it.eng.afExt.utils.StringUtils;
import it.eng.sil.module.AbstractSimpleModule;

public class AssociaPubb extends AbstractSimpleModule {

	private int messageIdSuccess = MessageCodes.General.OPERATION_SUCCESS;
	private int messageIdFail = MessageCodes.General.OPERATION_FAIL;
	private int messageIdElementDuplicate = MessageCodes.General.ELEMENT_DUPLICATED;

	public boolean doDynamicInsert(SourceBean request, SourceBean response, String statement) {
		DataConnection dc = null;
		DataConnectionManager dcm = null;
		SQLCommand cmdSelect = null;
		DataResult dr = null;

		String pool = (String) getConfig().getAttribute("POOL");
		SourceBean query = (SourceBean) getConfig().getAttribute("QUERY_UPDATE");

		// ListIFace list = new GenericList();
		ReportOperationResult reportOperation = new ReportOperationResult(this, response);

		try {
			// String statement = "";
			// statement="INSERT INTO DO_APP_UTENTE
			// VALUES(S_DO_APP_UTENTE.nextval,'" + sessionId + "'," + utente +
			// ")";
			dcm = DataConnectionManager.getInstance();

			if (dcm == null) {
				LogUtils.logError("doDynamicSelect", "dcm null", this);
			}

			dc = dcm.getConnection(pool);

			if (dc == null) {
				LogUtils.logError("doDynamicSelect", "dc null", this);
			}

			cmdSelect = dc.createInsertCommand(statement);

			// eseguiamo la query
			dr = cmdSelect.execute();

			reportOperation.reportSuccess(this.messageIdSuccess);

			return true;
		} catch (Exception e) {
			LogUtils.logError("doDynamicSelect", "Error", e, this);
			reportOperation.reportFailure(this.messageIdFail, e, "doDynamicSelect", "method failed");
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(dc, null, dr);
		}

		// ReportOperationResult reportOperation = new
		// ReportOperationResult(this, response);

		try {
			LogUtils.logDebug("doDynamicInsert", "", this);
		} catch (Exception ex) {
			// LogUtils.logError("doDynamicSelect", "Error", e, this);
			reportOperation.reportFailure(this.messageIdFail, ex, "doDynamicSelect", "method failed");
		}
		return false;
	}

	public void service(SourceBean request, SourceBean response) {
		String prgRichiestaAz = "";
		String indice = "";
		String strQuery = "";

		String strNumChk = (String) request.getAttribute("NUMCHK");
		String cdnUt = (String) request.getAttribute("CDNUT");
		String prgElencoGiornale = (String) request.getAttribute("PRGELENCOGIORNALE");
		String codGiornale = (String) request.getAttribute("CODGIORNALE");
		String datInizioSett = (String) request.getAttribute("DATINIZIOSETT");

		Integer numChk = new Integer(strNumChk);
		for (int i = 1; i < numChk.intValue() + 1; i++) {
			prgRichiestaAz = StringUtils.getAttributeStrNotNull(request, "CHK_PUBB" + String.valueOf(i));
			if (prgRichiestaAz != "") {
				strQuery = "INSERT INTO DO_DETTAGLIOPUB_GIORNALI ( "
						+ "PRGELENCOGIORNALE, PRGRICHIESTAAZ, NUMPRIORITA) " + "VALUES (" + prgElencoGiornale + ","
						+ prgRichiestaAz + ",1) ";
				doDynamicInsert(request, response, strQuery);
			}
		}
		Vector vectListaPubb = request.getAttributeAsVector("ROW");
		for (Iterator iter = vectListaPubb.iterator(); iter.hasNext();) {
			SourceBean beanUtente = (SourceBean) iter.next();
			// utente= beanUtente.getAttribute("CDNUT").toString();
			// doDynamicInsert (request,response,sessionId,utente);
		}
	}
}
