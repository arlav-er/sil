/*
 * Creato il Feb 24, 2005
 *
 * Per modificare il modello associato a questo file generato, aprire
 * Finestra&gt;Preferenze&gt;Java&gt;Generazione codice&gt;Codice e commenti
 */
package it.eng.sil.junit.action.report.disponibilita;

import java.io.IOException;
import java.util.Iterator;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.base.SourceBeanException;
import com.engiweb.framework.error.EMFAbstractError;
import com.engiweb.framework.error.EMFUserError;

import it.eng.afExt.utils.MessageCodes;
import it.eng.sil.bean.Documento;
import it.eng.sil.junit.ActionTestCase;
import junit.framework.TestCase;

/**
 * @author vuoto
 * 
 */
public class CurriculumDisponibilitaParamsTest extends TestCase {

	static org.apache.log4j.Logger _logger = org.apache.log4j.Logger
			.getLogger(CurriculumDisponibilitaParamsTest.class.getName());

	/**
	 * Constructor for service.
	 * 
	 * @param arg0
	 */
	public CurriculumDisponibilitaParamsTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(CurriculumDisponibilitaParamsTest.class);
	}

	/**
	 * Testa il semplice caricamento del report (senza protocollazione) passando tutti i parametri richiesti.
	 */
	public void testCaricamento() {
		SourceBean serviceRequest = action.getServiceRequest();
		try {
			action.execService();
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nell'esecuzione della action.", e);

			fail("Errore nell'esecuzione della action.");
		}
		_logger.debug(action.getServiceResponse().toString());

		assertTrue(action.getServiceResponse().containsAttribute("operationResult"));
		assertTrue(action.getResponseContainer().getErrorHandler().getErrors().size() == 0);
		viewReport();
	}

	/**
	 * si richiede di protocollare ma mancano i parametri di protocollazione
	 */
	public void testProtocolloSenzaParametri() {
		SourceBean serviceRequest = action.getServiceRequest();
		try {
			serviceRequest.updAttribute("salvaDB", "true");
			action.execService();
		} catch (Exception e) {
			it.eng.sil.util.TraceWrapper.debug(_logger, "Errore nell'esecuzione della action.", e);

			fail("Errore nell'esecuzione della action.");
		}
		_logger.debug(action.getServiceResponse().toString());

		assertTrue(action.getServiceResponse().getAttribute("operationResult").equals("ERROR"));
		Iterator errors = action.getResponseContainer().getErrorHandler().getErrors().iterator();
		assertTrue(action.getResponseContainer().getErrorHandler().getErrors().size() == 1);
		EMFAbstractError error = (EMFAbstractError) errors.next();
		assertTrue(error instanceof EMFUserError);
		assertTrue(((EMFUserError) error).getCode() == MessageCodes.Protocollazione.ERR_PARAMETRO_ASSENTE);
	}

	/**
	 * senza il parametro mostraPerLavoratore il report genera una intestazione sbagliata. Quindi il parametro
	 * mostraPerLavoratore E' OBBLIGATORIO.
	 */
	public void testNoProtocolloSenzaParametro1() {
		SourceBean serviceRequest = action.getServiceRequest();
		try {
			serviceRequest.delAttribute("mostraPerLavoratore");
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

	/**
	 * parametro showNoteCPI assente: NON CI SONO MALFUNZIONAMENTI DEL REPORT
	 * 
	 */
	public void testNoProtocolloSenzaParametro2() {
		SourceBean serviceRequest = action.getServiceRequest();
		try {
			serviceRequest.delAttribute("showNoteCPI");
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

	/**
	 * se il parametro mostraPerLavoratore = true allora il report mostra la disponibilita' del lavoratore, altrimenti
	 * il curriculum.
	 */
	public void testNoProtocolloPerLavoratoreSchedaDisponibilita() {
		SourceBean serviceRequest = action.getServiceRequest();
		try {
			serviceRequest.updAttribute("mostraPerLavoratore", "true");
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

	public void testNoProtocolloNoteCpi() {
		SourceBean serviceRequest = action.getServiceRequest();
		try {
			serviceRequest.updAttribute("showNoteCPI", "true");
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

	void setRequest(SourceBean serviceRequest) throws SourceBeanException {
		serviceRequest.setAttribute("CDNLAVORATORE", "12");
		serviceRequest.setAttribute("CDNFUNZIONE", "15");
		serviceRequest.setAttribute("apri", "true");
		serviceRequest.setAttribute("asAttachment", "false");
		serviceRequest.setAttribute("docInOut", "O");
		serviceRequest.setAttribute("mostraPerLavoratore", "false");
		serviceRequest.setAttribute("showNoteCPI", "false");
		serviceRequest.setAttribute("salvaDB", "false");
		serviceRequest.setAttribute("tipoDoc", "CUA");
		serviceRequest.setAttribute("tipoFile", "PDF");
	}

	public void setUp() {
		action = new ActionTestCase("RPT_CURR_DISP");
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

		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

	private ActionTestCase action;
}