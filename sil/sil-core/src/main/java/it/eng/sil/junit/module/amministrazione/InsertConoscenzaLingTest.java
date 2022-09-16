/*
 * Creato il 17-feb-05
 * Author: vuoto
 * 
 */
package it.eng.sil.junit.module.amministrazione;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.junit.ModuleTestCase;
import junit.framework.TestCase;

/**
 * @author vuoto
 * 
 */
public class InsertConoscenzaLingTest extends TestCase {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(InsertConoscenzaLingTest.class.getName());

	public InsertConoscenzaLingTest() {

	}

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(InsertConoscenzaLingTest.class);
	}

	public void testService() {

		// ************************************************
		// *** PRIMO MODULO
		// ************************************************

		ModuleTestCase m = new ModuleTestCase("M_insertConoscenzaLing");
		SourceBean serviceRequest = m.getServiceRequest();

		try {

			serviceRequest.setAttribute("CDNGRADOLETTO", "1");
			serviceRequest.setAttribute("CDNGRADOPARLATO", "1");
			serviceRequest.setAttribute("CDNGRADOSCRITTO", "1");
			serviceRequest.setAttribute("CDNLAVORATORE", "384066");
			serviceRequest.setAttribute("CDNUTINS", "1");
			serviceRequest.setAttribute("CDNUTMOD", "1");

			serviceRequest.setAttribute("CODLINGUA", "1");
			serviceRequest.setAttribute("CODMODLINGUA", "");
			serviceRequest.setAttribute("FLGCERTIFICATO", "");

			serviceRequest.setAttribute("PRGLINGUA", "");
			serviceRequest.setAttribute("STRMODLINGUA", "");

		} catch (SourceBeanException e) {

			it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nell'assegnazione dei parametri di ingresso.", e);

			fail("Errore nell'assegnazione dei parametri di ingresso.");
			return;

		}

		try {
			m.execService();
		} catch (Exception e1) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nell'esecuzione del modulo.", e1);

			fail("Errore nell'esecuzione del modulo.");
			return;

		}

		System.out.println(m.getServiceResponse().toXML(false));

		assertTrue(m.getServiceResponse().containsAttribute("INSERT_OK"));

		// ************************************************
		// *** SECONDO MODULO
		// ************************************************

		m = new ModuleTestCase("M_listConoscenzeLing");
		serviceRequest = m.getServiceRequest();

		try {

			serviceRequest.setAttribute("CDNLAVORATORE", "384066");

		} catch (SourceBeanException e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nell'assegnazione dei parametri di ingresso.", e);

			assertTrue(false);
			fail("Errore nell'assegnazione dei parametri di ingresso.");
			return;
		}

		try {
			m.execService();
		} catch (Exception e1) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nell'esecuzione del modulo.", e1);

			fail("Errore nell'esecuzione del modulo.");
			return;

		}

		System.out.println(m.getServiceResponse().toXML(false));

		assertTrue(m.getServiceResponse().containsAttribute("ROWS"));

	}

}