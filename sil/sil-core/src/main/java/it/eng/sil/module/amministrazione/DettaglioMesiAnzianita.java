package it.eng.sil.module.amministrazione;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.dbaccess.DataConnectionManager;
import com.engiweb.framework.dbaccess.Utils;
import com.engiweb.framework.dbaccess.sql.DataConnection;
import com.engiweb.framework.dbaccess.sql.DataField;
import com.engiweb.framework.dbaccess.sql.command.std.StoredProcedureCommand;
import com.engiweb.framework.dbaccess.sql.result.DataResult;
import com.engiweb.framework.dbaccess.sql.result.PunctualDataResult;

import it.eng.sil.module.AbstractSimpleModule;

public class DettaglioMesiAnzianita extends AbstractSimpleModule {

	public static String mesi[] = { "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto",
			"Settembre", "Ottobre", "Novembre", "Dicembre" };

	public void service(SourceBean request, SourceBean response) throws Exception {
		DataConnection conn = null;
		DataResult dr = null;
		StoredProcedureCommand command = null;
		BigDecimal cdnLavoratore = null;

		try {
			cdnLavoratore = new BigDecimal(request.getAttribute("CDNLAVORATORE").toString());
			DataConnectionManager dcm = DataConnectionManager.getInstance();
			conn = dcm.getConnection("SIL_DATI");

			String risultato = "";
			int paramIndex = 0;
			ArrayList parameters = null;

			command = (StoredProcedureCommand) conn
					.createStoredProcedureCommand("{ call ? := PG_MOVIMENTI.DettaglioAnzianitaFornero2014(?) }");

			parameters = new ArrayList(2);
			// 1. Parametro di Ritorno
			parameters.add(conn.createDataField("risultato", java.sql.Types.VARCHAR, null));
			command.setAsOutputParameters(paramIndex++);
			// 2. cdnLav
			parameters.add(conn.createDataField("cdnLav", java.sql.Types.BIGINT, cdnLavoratore));
			command.setAsInputParameters(paramIndex++);

			// Chiamata alla Stored Procedure
			dr = command.execute(parameters);

			// Reperisco i valori di output della stored
			PunctualDataResult pdr = (PunctualDataResult) dr.getDataObject();
			DataField df = pdr.getPunctualDatafield();
			risultato = df.getStringValue();

			// anno-mese-1-giornisosp#anno-mese-0-giornisosp
			SourceBean row = new SourceBean("ROW");
			row.setAttribute("Esito", "OK");
			row.setAttribute("Risultato", risultato);
			response.setAttribute((SourceBean) row);
		} catch (Exception e) {
			SourceBean row = new SourceBean("ROW");
			row.setAttribute("Esito", "KO");
			row.setAttribute("Risultato", "");
			response.setAttribute((SourceBean) row);
		} finally {
			Utils.releaseResources(conn, command, dr);
		}
	}
}