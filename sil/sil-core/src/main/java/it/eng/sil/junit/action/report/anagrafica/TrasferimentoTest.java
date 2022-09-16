/*
 * Creato il Mar 4, 2005
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.junit.action.report.anagrafica;

import java.io.IOException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;

import it.eng.sil.bean.Documento;
import it.eng.sil.junit.ActionTestCase;
import junit.framework.TestCase;

/**
 * @author Savino
 * 
 */
public class TrasferimentoTest extends TestCase {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger.getLogger(TrasferimentoTest.class.getName());

	/**
	 * semplice generazione del report in modalita' sola visione. Questo test e' comodo per testare modifiche al report,
	 * dato che per arrivare a stamparlo da applicativo bisogna seguire un percorso lungo (il trasferimento di un
	 * lavoratore)
	 */
	public void testVisione() {
		SourceBean serviceRequest = action.getServiceRequest();
		try {
			action.execService();
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nell'esecuzione della action.", e);

			fail("Errore nell'esecuzione della action.");
		}
		_logger.debug(action.getServiceResponse().toString());

		assertTrue(action.getServiceResponse().getAttribute("operationResult").equals("SUCCESS"));
		assertTrue(action.getResponseContainer().getErrorHandler().getErrors().size() == 0);
		viewReport();
	}

	public void setUp() {
		action = new ActionTestCase("RPT_STAMPA_RICHIESTA_DOC");
		SourceBean serviceRequest = action.getServiceRequest();
		try {
			setRequest(serviceRequest);
		} catch (SourceBeanException e) {
			it.eng.sil.util.TraceWrapper.fatal(_logger, "Errore nell'assegnazione dei parametri di ingresso.", e);

			fail("Errore nell'assegnazione dei parametri di ingresso.");
		}
	}

	public void tearDown() {
		action = null;
	}

	private void viewReport() {
		Documento theDoc = (Documento) action.getServiceResponse().getAttribute("theDocument");
		Runtime rt = Runtime.getRuntime();
		try {
			rt.exec("C:\\Programmi\\Adobe\\Acrobat 5.0\\Reader\\AcroRd32.exe "
					+ theDoc.getTempFile().getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void setRequest(SourceBean serviceRequest) throws SourceBeanException {
		serviceRequest.setAttribute("CDNLAVORATORE", "59991");
		serviceRequest.setAttribute("CDNFUNZIONE", "15");
		serviceRequest.setAttribute("isPresaAtto", "true");
		serviceRequest.setAttribute("asAttachment", "false");
		serviceRequest.setAttribute("salvaDB", "false");
		serviceRequest.setAttribute("tipoDoc", "TRCPI");
		serviceRequest.setAttribute("apri", "true");
	}

	void setProtocollazione(SourceBean serviceRequest) throws SourceBeanException {
		serviceRequest.setAttribute("numProt", "11");
		serviceRequest.setAttribute("docInOut", "O");
		serviceRequest.setAttribute("salvaDB", "false");
		serviceRequest.setAttribute("tipoDoc", "TRCPI");
		serviceRequest.setAttribute("tipoFile", "PDF");

	}

	private ActionTestCase action;

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(TrasferimentoTest.class);
	}
}