/*
 * Created on Mar 23, 2007
 */
package it.eng.sil.action.report;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import com.inet.report.Engine;

/**
 * Imposta i ResultSet ottenuti tramite jdbc nell'engine del report DiagnosiFunzionale. MAI USARE LA KEY "engine_2"
 * 
 * @author savino
 */
public class DiagnosiFunzionaleResultSet implements ReportResultSet {
	private Map rsData;

	/**
	 * MAI USARE LA KEY "engine_2"
	 * 
	 * @param rsData
	 *            Map dei ResultSet che verranno settati nell'engine di Crystal Clear.
	 */
	public DiagnosiFunzionaleResultSet(Map rsData) {
		this.rsData = rsData;
	}

	/**
	 * @param engine
	 *            di Crystal Clear in cui verranno settati i ResultSet
	 */
	public void setDataTo(Engine engine) throws Exception {

		ResultSet rs1 = (ResultSet) rsData.get("subreport0");
		ResultSet rs2 = (ResultSet) rsData.get("subreport2");
		ResultSet rs3 = (ResultSet) rsData.get("subreport3");
		ResultSet rs4 = (ResultSet) rsData.get("subreport4");
		ResultSet rs5 = (ResultSet) rsData.get("subreport5");
		ResultSet rs6 = (ResultSet) rsData.get("subreport6");
		ResultSet rs7 = (ResultSet) rsData.get("subreport7");
		ResultSet rs8 = (ResultSet) rsData.get("subreport8");
		ResultSet rs9 = (ResultSet) rsData.get("subreport9");
		ResultSet rs10 = (ResultSet) rsData.get("subreport13");
		ResultSet rs11 = (ResultSet) rsData.get("subreport14");

		com.inet.report.Engine subreport2 = engine.getSubReport(1);
		subreport2.setData(rs2);

		com.inet.report.Engine subreport3 = engine.getSubReport(2);
		subreport3.setData(rs3);

		com.inet.report.Engine subreport4 = engine.getSubReport(3);
		subreport4.setData(rs4);

		com.inet.report.Engine subreport5 = engine.getSubReport(4);
		subreport5.setData(rs5);

		com.inet.report.Engine subreport6 = engine.getSubReport(5);
		subreport6.setData(rs6);

		com.inet.report.Engine subreport7 = engine.getSubReport(6);
		subreport7.setData(rs7);

		com.inet.report.Engine subreport8 = engine.getSubReport(7);
		subreport8.setData(rs8);

		com.inet.report.Engine subreport9 = engine.getSubReport(8);
		subreport9.setData(rs9);

		if (rs11 != null) { // andava in errore con rs=null
			com.inet.report.Engine subreport0 = engine.getSubReport(0);
			subreport0.setData(rs11);
		}

		com.inet.report.Engine subreport10 = engine.getSubReport(12);
		subreport10.setData(rs10);

		com.inet.report.Engine subreport1 = engine.getSubReport(13);
		subreport1.setData(rs1);

		// chiusura dei ResultSet
		rs1.close();
		rs2.close();
		rs3.close();
		rs4.close();
		rs5.close();
		rs6.close();
		rs7.close();
		rs8.close();
		rs9.close();
		rs10.close();
		if (rs11 != null)
			rs11.close();
		// ora chiudo gli statements
		Object pss[] = (Object[]) rsData.get("statements");
		for (int i = 0; i < pss.length; i++)
			((Statement) pss[i]).close();

	}

}
