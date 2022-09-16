package it.eng.sil.test.coop.clicLavoro.ricercaPersonale;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;
import com.itextpdf.text.DocumentException;

import it.eng.sil.Values;
import it.eng.sil.coop.webservices.clicLavoro.ricercaPersonale.CLRicercaPersonaleData;
import it.eng.sil.junit.BaseTestCase;
import it.eng.sil.test.coop.clicLavoro.XP;
import it.eng.sil.util.xml.FieldFormatException;
import it.eng.sil.util.xml.MandatoryFieldException;
import it.eng.sil.util.xml.XMLValidator;
import junit.framework.TestCase;

/**
 * Il test, dopo aver costruito l'xml per una richiesta di personale, verifica la presenza o meno di alcuni dati, esegue
 * delle modifiche e si assicura che siano state recepite nell'xml generato.
 * 
 * @author rodi
 *
 */
public class RicercaDiPersonaleTestOrario extends TestCase {

	static Logger _logger = Logger.getLogger(RicercaDiPersonaleTestOrario.class.getName());

	private BigDecimal prgRichiestaAz;
	private BigDecimal prgAlternativa = new BigDecimal(1);
	private String codTipoComunicazioneCl = "01";

	private String codCPI;

	public RicercaDiPersonaleTestOrario() {

		this.codCPI = "085003400";
	}

	@Override
	protected void runTest() {
		System.setProperty("CONTEXT_NAME", "sil");
		BaseTestCase.getInstance();

		SourceBean res = (SourceBean) QueryExecutor.executeQuery("RP_SELECT_RANDOM_ORARIO_TEST_CASE", null, "SELECT",
				Values.DB_SIL_DATI);

		prgRichiestaAz = (BigDecimal) res.getAttribute("ROW.PRGRICHIESTAAZ");
		prgAlternativa = new BigDecimal(1);

		if (prgRichiestaAz == null)
			fail("Nessun test-case disponibile.");

		_logger.info("Test case) prgrichiesta: " + prgRichiestaAz + ", prgalternativa: " + prgAlternativa);

		try {

			testOrario();

		} catch (EMFUserError e) {
			fail("prgrichiesta: " + prgRichiestaAz + ", prgalternativa: " + prgAlternativa
					+ ". Errore nella generazione xml: " + e.getDescription());
			e.printStackTrace();
		} catch (MandatoryFieldException e) {
			fail("prgrichiesta: " + prgRichiestaAz + ", prgalternativa: " + prgAlternativa
					+ ". Errore nella generazione xml: " + e.getExceptionMessage());
			e.printStackTrace();
		} catch (FieldFormatException e) {
			fail("prgrichiesta: " + prgRichiestaAz + ", prgalternativa: " + prgAlternativa
					+ ". Errore nella generazione xml: " + e.getExceptionMessage());
			e.printStackTrace();
		} catch (EMFInternalError e) {
			e.printStackTrace();
		} catch (SAXException | IOException | XPathExpressionException | ParserConfigurationException e) {
			fail("prgrichiesta: " + prgRichiestaAz + ", prgalternativa: " + prgAlternativa
					+ ". Errore nella generazione xml: " + e.getMessage());
		}

	}

	/**
	 * Costruisce l'xml a partire da una domanda e ne conta le lingue. Dopodichè aggiunge un nuovo record nella
	 * tabella DO_LINGUA e ricostruisce l'xml. A quel punto esegue due controlli: 1) Il numero di lingue è aumentato
	 * di uno 2) La lingua precedentemente inserita è contenuta nel nuovo xml con i codici corretti
	 * 
	 * @throws EMFUserError
	 * @throws MandatoryFieldException
	 * @throws FieldFormatException
	 * @throws DocumentException
	 * @throws EMFInternalError
	 * @throws IOException
	 * @throws SAXException
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 */
	public void testOrario() throws EMFUserError, MandatoryFieldException, FieldFormatException, EMFInternalError,
			IOException, SAXException, XPathExpressionException, ParserConfigurationException {

		{
			// costruisci il primo xml
			CLRicercaPersonaleData risposta = new CLRicercaPersonaleData(prgRichiestaAz, prgAlternativa, codCPI,
					codTipoComunicazioneCl);
			risposta.costruisci(false);
			String primoxml = risposta.generaXML();

			Document document = XMLValidator.parseXmlFile(primoxml);
			XP primo = new XP(document.getDocumentElement());

			// verifica che idmodalitalavoro sia valorizzato a PT
			String idmodalitalavoro = primo.getText(XP.modalitalavoroNodeXPathExp);
			_logger.info("Prima idmodalitalavoro = " + idmodalitalavoro);

			assertEquals("PT", idmodalitalavoro);

		}

		// inserisci una nuova lingua legata alla richiesta
		inserisciModalitaLavoro(true);

		{
			// costruisci nuovamente l'xml
			CLRicercaPersonaleData risposta = new CLRicercaPersonaleData(prgRichiestaAz, prgAlternativa, codCPI,
					codTipoComunicazioneCl);
			risposta.costruisci(false);
			String secondoxml = risposta.generaXML();

			Document document = XMLValidator.parseXmlFile(secondoxml);
			XP secondo = new XP(document.getDocumentElement());

			// conta nuovamente le righe
			String idmodalitalavoro = secondo.getText(XP.modalitalavoroNodeXPathExp);
			_logger.info("Dopo idmodalitalavoro = " + idmodalitalavoro);

			assertEquals("FT", idmodalitalavoro);
		}

		cancellaModalitaLavoro();

		{
			// costruisci nuovamente l'xml
			CLRicercaPersonaleData risposta = new CLRicercaPersonaleData(prgRichiestaAz, prgAlternativa, codCPI,
					codTipoComunicazioneCl);
			risposta.costruisci(false);
			String terzoxml = risposta.generaXML();

			Document document = XMLValidator.parseXmlFile(terzoxml);
			XP terzo = new XP(document.getDocumentElement());

			// conta nuovamente le righe
			Node idmodalitalavoro = terzo.getNode(XP.modalitalavoroNodeXPathExp);
			_logger.info("Dopo idmodalitalavoro = " + idmodalitalavoro);

			assertEquals(null, idmodalitalavoro);
		}

	}

	private Boolean cancellaModalitaLavoro() {
		List<Object> inputParameters = new ArrayList<Object>();
		inputParameters.add(prgRichiestaAz);

		Boolean funsia = (Boolean) QueryExecutor.executeQuery("RP_DELETE_ORARIO", inputParameters.toArray(), "DELETE",
				Values.DB_SIL_DATI);

		return funsia;

	}

	private boolean inserisciModalitaLavoro(boolean fullTime) {
		List<Object> inputParameters = new ArrayList<Object>();
		inputParameters.add(prgRichiestaAz);

		Boolean funsia = (Boolean) QueryExecutor.executeQuery("RP_INSERT_ORARIO", inputParameters.toArray(), "INSERT",
				Values.DB_SIL_DATI);

		return funsia;
	}

}
