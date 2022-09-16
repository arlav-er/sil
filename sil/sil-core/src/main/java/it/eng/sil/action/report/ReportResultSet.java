/*
 * Created on Mar 23, 2007
 */
package it.eng.sil.action.report;

import com.inet.report.Engine;

/**
 * Interfaccia che deve implementare la classe che imposta i ResultSet nell'engine di Crystal Clear. MAI USARE LA KEY
 * "engine_2"
 * 
 * @author savino
 */
public interface ReportResultSet {
	public void setDataTo(Engine engine) throws Exception;
}
