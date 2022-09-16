package it.eng.sil.test.coop.clicLavoro.ricercaPersonale;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.engiweb.framework.base.SourceBean;
import com.engiweb.framework.error.EMFInternalError;
import com.engiweb.framework.error.EMFUserError;
import com.engiweb.framework.util.QueryExecutor;
import com.itextpdf.text.DocumentException;

import it.eng.sil.Values;
import it.eng.sil.coop.webservices.clicLavoro.ricercaPersonale.CLRicercaPersonaleData;
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
public class RicercaDiPersonaleTestLingua extends TestCase {

	static Logger _logger = Logger.getLogger(RicercaDiPersonaleTestLingua.class.getName());

	private final int cdnUt = 100;

	private BigDecimal prgRichiestaAz;
	private BigDecimal prgAlternativa;

	private String codCPI;
	private String codiceOfferta;
	private String codTipoComunicazioneCl = "01";

	public RicercaDiPersonaleTestLingua(BigDecimal bdRichiesta, BigDecimal bdAlternativa, String codCPI,
			String codiceOfferta) {
		this.prgRichiestaAz = bdRichiesta;
		this.prgAlternativa = bdAlternativa;
		this.codCPI = codCPI;
		this.codiceOfferta = codiceOfferta;
	}

	@Override
	protected void runTest() {

		_logger.info("Test case) prgrichiesta: " + prgRichiestaAz + ", prgalternativa: " + prgAlternativa);

		try {

			verificaLingue();

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException | IOException | SAXException | ParserConfigurationException e) {
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
	 * @throws ParserConfigurationException
	 */
	private void verificaLingue() throws EMFUserError, MandatoryFieldException, FieldFormatException, EMFInternalError,
			IOException, SAXException, XPathExpressionException, ParserConfigurationException {
		boolean contenutoSI = false, contenutoNO = false;
		int linguePrima, lingueDopo, lingueDopoDopo;

		// memoprizza in un set le lingue contenute nell'xml
		HashSet<Lingua> lingue = new HashSet<Lingua>();

		{
			// costruisci il primo xml
			CLRicercaPersonaleData risposta = new CLRicercaPersonaleData(prgRichiestaAz, prgAlternativa, codCPI,
					codTipoComunicazioneCl);
			risposta.costruisci(false);
			String primoxml = risposta.generaXML();

			Document document = XMLValidator.parseXmlFile(primoxml);
			XP primo = new XP(document.getDocumentElement());

			// conta le righe nel primo xml
			NodeList linguexp = primo.getList(XP.lingueXPathExp);

			if (linguexp != null) {
				int i = 0;
				while (i < linguexp.getLength()) {
					Lingua inside = new Lingua(linguexp.item(i));
					lingue.add(inside);

					++i;
				}
			}
			linguePrima = lingue.size();
			_logger.info("Prima c'erano " + linguePrima + " lingue.");
		}

		// inserisci una nuova lingua legata alla richiesta
		Lingua inserita = inserisciLingua(true);

		{
			// costruisci nuovamente l'xml
			CLRicercaPersonaleData risposta = new CLRicercaPersonaleData(prgRichiestaAz, prgAlternativa, codCPI,
					codTipoComunicazioneCl);
			risposta.costruisci(false);
			String secondoxml = risposta.generaXML();

			Document document = XMLValidator.parseXmlFile(secondoxml);
			XP secondo = new XP(document.getDocumentElement());

			// conta nuovamente le righe
			NodeList linguexp = secondo.getList(XP.lingueXPathExp);

			if (linguexp != null) {
				int i = 0;
				while (i < linguexp.getLength()) {
					Lingua inside = new Lingua(linguexp.item(i));
					lingue.add(inside);
					if (inside.equals(inserita))
						contenutoSI = true;

					++i;
				}
			}
			lingueDopo = lingue.size();
			_logger.info("Dopo c'erano " + lingueDopo + " lingue.");
		}

		// deve esserci una lingua in più
		assertEquals(lingueDopo - linguePrima, 1);
		// e deve essere quella inserita
		assertEquals(true, contenutoSI);

		// inserisci una nuova lingua legata alla richiesta con un codice non valido
		inserita = inserisciLingua(false);

		{
			// costruisci nuovamente l'xml
			CLRicercaPersonaleData risposta = new CLRicercaPersonaleData(prgRichiestaAz, prgAlternativa, codCPI,
					codTipoComunicazioneCl);
			risposta.costruisci(false);
			String secondoxml = risposta.generaXML();

			Document document = XMLValidator.parseXmlFile(secondoxml);
			XP secondo = new XP(document.getDocumentElement());

			// conta nuovamente le righe
			NodeList linguexp = secondo.getList(XP.lingueXPathExp);

			if (linguexp != null) {
				int i = 0;

				while (i < linguexp.getLength()) {
					Lingua inside = new Lingua(linguexp.item(i));
					lingue.add(inside);
					if (inside.equals(inserita))
						contenutoNO = true;

					++i;
				}
			}

			lingueDopoDopo = lingue.size();
			_logger.info("DopoDopo c'erano " + lingueDopoDopo + " lingue.");
		}

		// non deve esserci una lingua in più
		assertEquals(lingueDopo, lingueDopoDopo);
		// e quella inserita non eve esserci
		assertEquals(false, contenutoNO);

	}

	/**
	 * Inserisce una nuova lingua per la richiesta in oggetto. La lingua può avere un codice valido o meno
	 * 
	 * @throws EMFInternalError
	 */
	private Lingua inserisciLingua(boolean valid) throws EMFInternalError {
		List<Object> inputParameters = new ArrayList<Object>();

		SourceBean gradoLingua = generaRandomGradoLingua();
		BigDecimal cdnGradoLetto = (BigDecimal) gradoLingua.getAttribute("ROW.CDNGRADO");
		String codGradoLetto = (String) gradoLingua.getAttribute("ROW.CODICE");

		gradoLingua = generaRandomGradoLingua();
		BigDecimal cdnGradoParlato = (BigDecimal) gradoLingua.getAttribute("ROW.CDNGRADO");
		String codGradoParlato = (String) gradoLingua.getAttribute("ROW.CODICE");

		gradoLingua = generaRandomGradoLingua();
		BigDecimal cdnGradoScritto = (BigDecimal) gradoLingua.getAttribute("ROW.CDNGRADO");
		String codGradoScritto = (String) gradoLingua.getAttribute("ROW.CODICE");

		String codLingua = generaRandomCodLingua(valid);
		inputParameters.add(cdnGradoLetto);
		inputParameters.add(cdnGradoParlato);
		inputParameters.add(cdnGradoScritto);
		inputParameters.add(cdnUt);
		inputParameters.add(cdnUt);
		inputParameters.add(codLingua);
		inputParameters.add(prgAlternativa);
		inputParameters.add(prgRichiestaAz);

		_logger.info("Aggiungo la lingua '" + codLingua + "' con gradi l,p,s: " + cdnGradoLetto + ", " + cdnGradoParlato
				+ ", " + cdnGradoScritto + ".");

		QueryExecutor.executeQuery("RP_INSERT_LINGUA", inputParameters.toArray(), "INSERT", Values.DB_SIL_DATI);

		Lingua l = new Lingua(codLingua, codGradoLetto, codGradoScritto, codGradoParlato);
		return l;
	}

	class Lingua {
		String codLingua;
		String cdnGradoLetto;
		String cdnGradoScritto;
		String cdnGradoParlato;

		public Lingua(String codLingua, String codGradoLetto, String codGradoScritto, String codGradoParlato) {
			super();
			this.codLingua = codLingua;
			this.cdnGradoLetto = codGradoLetto;
			this.cdnGradoScritto = codGradoScritto;
			this.cdnGradoParlato = codGradoParlato;
		}

		public Lingua(Node node) throws XPathExpressionException {
			XPath xPath = XPathFactory.newInstance().newXPath();

			Node singleNode = (Node) xPath.evaluate("cliclavoro:idlingua", node, XPathConstants.NODE);
			String codLingua2 = singleNode.getTextContent();

			singleNode = (Node) xPath.evaluate("cliclavoro:idlivelloletto", node, XPathConstants.NODE);
			String cdnGradoLetto2 = singleNode.getTextContent();

			singleNode = (Node) xPath.evaluate("cliclavoro:idlivelloscritto", node, XPathConstants.NODE);
			String cdnGradoScritto2 = singleNode.getTextContent();

			singleNode = (Node) xPath.evaluate("cliclavoro:idlivelloparlato", node, XPathConstants.NODE);
			String cdnGradoParlato2 = singleNode.getTextContent();

			this.codLingua = codLingua2;
			this.cdnGradoLetto = cdnGradoLetto2;
			this.cdnGradoScritto = cdnGradoScritto2;
			this.cdnGradoParlato = cdnGradoParlato2;

			_logger.info("");
		}

		public String getCodLingua() {
			return codLingua;
		}

		public void setCodLingua(String codLingua) {
			this.codLingua = codLingua;
		}

		@Override
		public boolean equals(Object other) {
			if (other == null)
				return false;
			if (!(other instanceof Lingua))
				return false;
			Lingua lingua = (Lingua) other;

			return this.codLingua.equals(lingua.codLingua) && this.cdnGradoLetto.equals(lingua.cdnGradoLetto)
					&& this.cdnGradoScritto.equals(lingua.cdnGradoScritto)
					&& this.cdnGradoParlato.equals(lingua.cdnGradoParlato);

		}

		public int hashCode() {
			return this.codLingua.length();
		}

	}

	/**
	 * Genera un codice lingua casuale che non sia già contenuto nella richiesta.
	 * 
	 * @return
	 * @throws EMFInternalError
	 */
	private String generaRandomCodLingua(boolean valid) throws EMFInternalError {
		// SourceBean test = (SourceBean) tex.executeQuery("RP_SELECT_RANDOM_COD_LINGUA", null, "SELECT");
		SourceBean test;
		String codlingua;
		if (valid) {
			test = (SourceBean) QueryExecutor.executeQuery("RP_SELECT_RANDOM_COD_LINGUA",
					new Object[] { prgRichiestaAz, prgAlternativa }, "SELECT", Values.DB_SIL_DATI);
			codlingua = (String) test.getAttribute("ROW.CODLINGUA");
		} else {
			test = (SourceBean) QueryExecutor.executeQuery("RP_SELECT_RANDOM_COD_LINGUA_NOT_VALID",
					new Object[] { prgRichiestaAz, prgAlternativa }, "SELECT", Values.DB_SIL_DATI);
			codlingua = (String) test.getAttribute("ROW.CODLINGUA");
		}
		return codlingua;
	}

	/**
	 * Genera un grado casuale.
	 * 
	 * @return
	 * @throws EMFInternalError
	 */
	private SourceBean generaRandomGradoLingua() throws EMFInternalError {

		return (SourceBean) QueryExecutor.executeQuery("RP_SELECT_RANDOM_GRADO_LINGUA", null, "SELECT",
				Values.DB_SIL_DATI);

	}

}
