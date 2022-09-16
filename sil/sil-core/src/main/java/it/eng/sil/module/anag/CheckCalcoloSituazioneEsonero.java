package it.eng.sil.module.anag;

import java.util.ArrayList;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.SQLStatements;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;
import com.engiweb.framework.error.EMFInternalError;

import it.eng.afExt.utils.DateUtils;
import it.eng.afExt.utils.LogUtils;
import it.eng.sil.Values;
import it.eng.sil.module.AbstractSimpleModule;
import it.eng.sil.util.DBAccess;

public class CheckCalcoloSituazioneEsonero extends AbstractSimpleModule {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CheckCalcoloSituazioneEsonero.class.getName());

	public void service(SourceBean request, SourceBean response) {
		setSectionQuerySelect("SEL_QUERY_INI_DEC");
		SourceBean sbDatInizioPeriodiDecValore = doSelect(request, response);
		setSectionQuerySelect("SEL_QUERY_INI_VAR");
		SourceBean sbDatInizioPeriodiVariazioni = doSelect(request, response);
		setSectionQuerySelect("SEL_QUERY_FIN_DEC");
		SourceBean sbDatFinePeriodiDecValore = doSelect(request, response);
		try {
			String datInizioPrimoDecValore = (String) sbDatInizioPeriodiDecValore.getAttribute("row.datinizioval");
			String datInizioVariazione = (String) sbDatInizioPeriodiVariazioni.getAttribute("row.datiniziovalidita");
			String datUltimaCalc = (String) request.getAttribute("DATULTIMACALC");
			String datFineUltimoDecValore = (String) sbDatFinePeriodiDecValore.getAttribute("row.datfineval");
			if (checkTsConfigLocPerCalcoloSituazioneEsonero() != "Ok") {
				response.setAttribute("calcoloValido", "false");
			} else if (datInizioVariazione != null
					&& DateUtils.compare(datInizioVariazione, datInizioPrimoDecValore) < 0) {
				response.setAttribute("calcoloValido", "false");
			} else if (DateUtils.compare(datUltimaCalc, datFineUltimoDecValore) > 0) {
				response.setAttribute("calcoloValido", "false");
			} else {
				response.setAttribute("calcoloValido", "true");
			}
		} catch (Exception e) {
			e.printStackTrace();
			it.eng.sil.util.TraceWrapper.debug(_logger,
					"Impossibile sapere se è possibile calcolare la lista calcolo situazione esonero.", e);
			try {
				response.setAttribute("calcoloValido", "false");
			} catch (Exception ex) {
				it.eng.sil.util.TraceWrapper.debug(_logger,
						"SourceBeanException :: Check calcolo lista calcolo situazione esonero.", ex);
			}
		}
	}

	public String checkTsConfigLocPerCalcoloSituazioneEsonero() {

		_logger.debug(CheckCalcoloSituazioneEsonero.class.getName()
				+ "::checkTsConfigLocPerCalcoloSituazioneEsonero() CALLED...");

		String returnValue = null;
		DataConnection conn = null;
		try {

			String codiceRit = "-1";

			DBAccess dbaccess = new DBAccess();
			conn = dbaccess.getConnection(Values.DB_SIL_DATI);

			StoredProcedureCommand command = null;
			DataResult dr = null;

			String sqlFunction = SQLStatements.getStatement("CHECK_TS_CONFIG_LOC_SITUAZIONE_RICH_ESONERO");
			command = (StoredProcedureCommand) conn.createStoredProcedureCommand(sqlFunction);
			ArrayList parameters = new ArrayList(1);
			int paramIndex = 0;

			// Parametro di Ritorno
			parameters.add(conn.createDataField("risultato", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);

			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			// Reperisco il valore di output della stored
			DataField df = pdr.getPunctualDatafield();
			codiceRit = df.getStringValue();

			if (("0").equalsIgnoreCase(codiceRit)) {
				returnValue = "Ok";
			} else {
				returnValue = "notOk";
			}

		} catch (EMFInternalError xe) {
			LogUtils.logError(
					CheckCalcoloSituazioneEsonero.class.getName()
							+ "::checkTsConfigLocPerCalcoloSituazioneEsonero() CALLED...",
					"errore durante il check sulle date della ts_config_loc per il calcolo della situazione richiesta esonero",
					xe);
		} finally {
			com.engiweb.framework.dbaccess.Utils.releaseResources(conn, null, null);
		}

		return returnValue;

	}

}