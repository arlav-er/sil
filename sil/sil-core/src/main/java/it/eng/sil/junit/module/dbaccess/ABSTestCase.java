/*
 * Created on Aug 25, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package it.eng.sil.junit.module.dbaccess;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.junit.ModuleTestCase;
import junit.framework.TestCase;

/**
 * @author savino
 * 
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class ABSTestCase extends TestCase {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(ABSTestCase.class.getName());

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ABSTestCase.class);
	}

	public void testBase() {
		ModuleTestCase m = new ModuleTestCase("M_TestAbstractSimpleModule");
		SourceBean serviceRequest = m.getServiceRequest();
		setRequestParameter(serviceRequest, "testBase");
		try {
			m.execService();
		} catch (Exception e1) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nell'esecuzione del modulo.", e1);

			fail("Errore nell'esecuzione del modulo.");
			return;
		}
		// mi attendo che non ci siano record
		assertTrue(((SourceBean) m.getServiceResponse().getAttribute("TEST_RESULT")).getAttribute("ROWS.ROW") == null);
	}

	public void test1() {
		ModuleTestCase m = new ModuleTestCase("M_TestAbstractSimpleModule");
		SourceBean serviceRequest = m.getServiceRequest();
		setRequestParameter(serviceRequest, "test1");
		try {
			m.execService();
		} catch (Exception e1) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nell'esecuzione del modulo.", e1);

			fail("Errore nell'esecuzione del modulo.");
			return;
		}
		// mi attendo che non ci siano record
		assertTrue(((SourceBean) m.getServiceResponse().getAttribute("TEST_RESULT")).getAttribute("ROWS.ROW") == null);
	}

	public void test2() {
		ModuleTestCase m = new ModuleTestCase("M_TestAbstractSimpleModule");
		SourceBean serviceRequest = m.getServiceRequest();
		setRequestParameter(serviceRequest, "test2");
		try {
			m.execService();
		} catch (Exception e1) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nell'esecuzione del modulo.", e1);

			fail("Errore nell'esecuzione del modulo.");
			return;
		}
		// mi attendo che non ci siano record
		assertTrue(((SourceBean) m.getServiceResponse().getAttribute("TEST_RESULT")).getAttribute("ROWS.ROW") == null);
	}

	public void test3() {
		ModuleTestCase m = new ModuleTestCase("M_TestAbstractSimpleModule");
		SourceBean serviceRequest = m.getServiceRequest();
		setRequestParameter(serviceRequest, "test3");
		try {
			m.execService();
		} catch (Exception e1) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nell'esecuzione del modulo.", e1);

			fail("Errore nell'esecuzione del modulo.");
			return;
		}
		// mi attendo il record inserito
		SourceBean row = (SourceBean) ((SourceBean) m.getServiceResponse().getAttribute("TEST_RESULT"))
				.getAttribute("ROWS");
		String strSpecifica = (String) row.getAttribute("row.strSpecifica");
		assertTrue("SPECIFICA TEST".equals(strSpecifica));
	}

	private void setRequestParameter(SourceBean serviceRequest, String testName) {
		try {
			serviceRequest.setAttribute("TEST_NAME", testName);
			serviceRequest.setAttribute("CDNLAVORATORE", "1");
			serviceRequest.setAttribute("CDNUTINS", "1");
			serviceRequest.setAttribute("CDNUTMOD", "1");
			serviceRequest.setAttribute("STRSPECIFICA", "SPECIFICA TEST");
		} catch (SourceBeanException e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nell'assegnazione dei parametri di ingresso.", e);

			fail("Errore nell'assegnazione dei parametri di ingresso.");
			throw new RuntimeException("Errore nell'assegnazione dei parametri di ingresso");

		}
	}
}