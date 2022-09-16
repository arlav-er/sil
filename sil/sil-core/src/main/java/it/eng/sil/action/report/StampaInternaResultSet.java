/*
 * Creato il 5-apr-07
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.action.report;

/**
 * @author melandri
 * 
 * Per modificare il modello associato al commento di questo tipo generato,
 * aprire Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e
 * commenti
 */

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import com.inet.report.Engine;

public class StampaInternaResultSet implements ReportResultSet {
	private Map rsData;

	/**
	 * MAI USARE LA KEY "engine_2"
	 * 
	 * @param rsData
	 *            Map dei ResultSet che verranno settati nell'engine di Crystal Clear.
	 */
	public StampaInternaResultSet(Map rsData) {
		this.rsData = rsData;
	}

	/**
	 * @param engine
	 *            di Crystal Clear in cui verranno settati i ResultSet
	 */
	public void setDataTo(Engine engine) throws Exception {

		ResultSet rs = (ResultSet) rsData.get("subreport5");

		// com.inet.report.Engine subreport = engine.getSubReport(5);
		// subreport.setData(rs);

		// chiusura dei ResultSet
		rs.close();
		// ora chiudo gli statements
		Object st = (Object) rsData.get("statements");
		((Statement) st).close();
	}

}
